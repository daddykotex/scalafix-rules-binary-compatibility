package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scalafix.v1._

import scala.meta._

case class CaseClassAddApplyMethodConfig(
    excludedPackages: List[String] = List.empty
) {
  private val pkgsRegex = excludedPackages.map(_.r)

  def shouldExclude(pkg: String): Boolean = {
    val debug = pkgsRegex.map(r => pkg -> r.findFirstMatchIn(pkg)).mkString
    if (pkgsRegex.isEmpty) false
    else pkgsRegex.exists(_.findFirstMatchIn(pkg).isDefined)
  }
}
object CaseClassAddApplyMethodConfig {
  val default = CaseClassAddApplyMethodConfig()
  implicit val surface: Surface[CaseClassAddApplyMethodConfig] =
    metaconfig.generic.deriveSurface[CaseClassAddApplyMethodConfig]
  implicit val decoder: ConfDecoder[fix.CaseClassAddApplyMethodConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class CaseClassAddApplyMethod(config: CaseClassAddApplyMethodConfig) extends SemanticRule("CaseClassAddApplyMethod") {
  def this() = this(CaseClassAddApplyMethodConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] =
    config.conf
      .getOrElse("CaseClassAddApplyMethod")(this.config)
      .map { newConfig => new CaseClassAddApplyMethod(newConfig) }

  override def fix(implicit doc: SemanticDocument): Patch = {
    val exclude = Utils.pkg.find(doc.tree).exists(config.shouldExclude)
    if (exclude) Patch.empty
    else {
      val caseClasses = doc.tree.collect { case Utils.caseClass(cc) => cc }
      val caseClassesWithObjects = caseClasses.map(c => c -> Utils.companion.findCompanionObject(doc.tree)(c))
      val patches = caseClassesWithObjects.map {
        case (cc, None) => Patch.empty
        case (cc, Some(obj)) => {
          // object exists
          val unapplyM = Utils.companion.findApplyMethod(obj)
          unapplyM match {
            // no unapply
            case None =>
              addApply(obj, cc)
            // an unapply is already defined, we don't change it
            case Some(_) =>
              Patch.empty
          }

        }
      }
      Patch.fromIterable(patches)
    }
  }

  private def addApply(d: Defn.Object, cc: Defn.Class): Patch = {
    val generatedUnapply = generateApply(cc)
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

  private def generateApply(cc: Defn.Class): String = {
    val nameTypes = cc.ctor.paramClauses.flatMap(c => c.values).map(p => p.name -> p.decltpe.get)

    val params = nameTypes.map { case (name, typ) => s"$name: $typ" }.mkString(",")
    val args = nameTypes.map(_._1).mkString(", ")

    val typeParams = cc.tparamClause.copy(values =
      cc.tparamClause.values.map(p =>
        p.copy(mods = p.mods.filterNot(_.is[Mod.Covariant]).filterNot(_.is[Mod.Contravariant]))
      )
    )
    s"""|  def apply$typeParams($params): ${cc.name}${typeParams} = {
        |    new ${cc.name}($args)
        |  }
        |""".stripMargin

  }
}
