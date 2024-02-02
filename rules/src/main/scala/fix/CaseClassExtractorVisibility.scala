package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scalafix.v1._

import scala.meta._

case class CaseClassExtractorVisibilityConfig(
    excludedPackages: List[String] = List.empty
) {
  private val pkgsRegex = excludedPackages.map(_.r)

  def shouldExclude(pkg: String): Boolean = {
    val debug = pkgsRegex.map(r => pkg -> r.findFirstMatchIn(pkg)).mkString
    if (pkgsRegex.isEmpty) false
    else pkgsRegex.exists(_.findFirstMatchIn(pkg).isDefined)
  }
}
object CaseClassExtractorVisibilityConfig {
  val default = CaseClassExtractorVisibilityConfig()
  implicit val surface: Surface[CaseClassExtractorVisibilityConfig] =
    metaconfig.generic.deriveSurface[CaseClassExtractorVisibilityConfig]
  implicit val decoder: ConfDecoder[fix.CaseClassExtractorVisibilityConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class CaseClassExtractorVisibility(config: CaseClassExtractorVisibilityConfig)
    extends SemanticRule("CaseClassExtractorVisibility") {
  def this() = this(CaseClassExtractorVisibilityConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] =
    config.conf
      .getOrElse("CaseClassExtractorVisibility")(this.config)
      .map { newConfig => new CaseClassExtractorVisibility(newConfig) }

  override def fix(implicit doc: SemanticDocument): Patch = {
    val exclude = Utils.pkg.find(doc.tree).exists(config.shouldExclude)
    if (exclude) Patch.empty
    else {
      val caseClasses = doc.tree.collect { case Utils.caseClass(cc) if !cc.mods.exists(_.is[Mod.Private]) => cc }
      val caseClassesWithObjects = caseClasses.map(c => c -> Utils.companion.findCompanionObject(doc.tree)(c))
      val patches = caseClassesWithObjects.map {
        case (cc, None) => Patch.empty
        case (cc, Some(obj)) => {
          // object exists
          val unapplyM = Utils.companion.findUnapplyMethod(obj)
          unapplyM match {
            // no unapply
            case None =>
              addPrivateUnapply(obj, cc)
            // an unapply is already defined, we don't change it
            case Some(_) =>
              Patch.empty
          }

        }
      }
      Patch.fromIterable(patches)
    }
  }

  private def addPrivateUnapply(d: Defn.Object, cc: Defn.Class): Patch = {
    val generatedUnapply = generateUnapply(cc)
    d.templ.stats.headOption match {
      // body is empty `object Thing` or `object Thing {}`
      case None if d.templ.pos.isEmpty =>
        Patch.addRight(
          d.name,
          s"""| {
              |$generatedUnapply
              |}""".stripMargin
        )
      case None =>
        Patch.replaceTree(
          d.templ,
          s"""|{
              |$generatedUnapply
              |}""".stripMargin
        )
      case Some(value) =>
        Patch.addLeft(value, generatedUnapply)
    }
  }

  private def generateUnapply(cc: Defn.Class): String = {
    val nameTypes = cc.ctor.paramClauses.flatMap(c => c.values).map(p => p.name -> p.decltpe.get)

    val rtypeList = nameTypes.map(_._2.toString())
    val rType = if (rtypeList.isEmpty) "Unit" else rtypeList.mkString("(", ", ", ")")
    val returnType = s"Option[$rType]"

    val rValueList = nameTypes.map(nt => s"c.${Utils.sanitizeName(nt._1)}")
    val rValue =
      if (rValueList.isEmpty) "Some(())"
      else {
        val values = rValueList.mkString(", ")
        if (rValueList.size > 1) s"Some(($values))"
        else s"Some($values)"
      }
    val typeParams = cc.tparamClause.copy(values =
      cc.tparamClause.values.map(p =>
        p.copy(mods = p.mods.filterNot(_.is[Mod.Covariant]).filterNot(_.is[Mod.Contravariant]))
      )
    )
    s"""|  private def unapply$typeParams(c: ${cc.name.value}${typeParams}): $returnType = {
        |    $rValue
        |  }
        |""".stripMargin

  }
}
