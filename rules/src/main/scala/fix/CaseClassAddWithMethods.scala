package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scalafix.v1._

import scala.meta._

case class CaseClassAddWithMethodsConfig(
    excludedPackages: List[String] = List.empty
) {
  private val pkgsRegex = excludedPackages.map(_.r)

  def shouldExclude(pkg: String): Boolean = {
    val debug = pkgsRegex.map(r => pkg -> r.findFirstMatchIn(pkg)).mkString
    if (pkgsRegex.isEmpty) false
    else pkgsRegex.exists(_.findFirstMatchIn(pkg).isDefined)
  }
}
object CaseClassAddWithMethodsConfig {
  val default = CaseClassAddWithMethodsConfig()
  implicit val surface: Surface[CaseClassAddWithMethodsConfig] =
    metaconfig.generic.deriveSurface[CaseClassAddWithMethodsConfig]
  implicit val decoder: ConfDecoder[fix.CaseClassAddWithMethodsConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class CaseClassAddWithMethods(config: CaseClassAddWithMethodsConfig) extends SemanticRule("CaseClassAddWithMethods") {
  def this() = this(CaseClassAddWithMethodsConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] =
    config.conf
      .getOrElse("CaseClassAddWithMethods")(this.config)
      .map { newConfig => new CaseClassAddWithMethods(newConfig) }

  override def fix(implicit doc: SemanticDocument): Patch = {
    val exclude = Utils.pkg.find(doc.tree).exists(config.shouldExclude)
    if (exclude) Patch.empty
    else {
      val caseClasses = doc.tree.collect { case Utils.caseClass(cc) if !cc.mods.exists(_.is[Mod.Private]) => cc }
      val patches = caseClasses.map { cc =>
        val withMethods = generateWithMethods(cc)
        addMethods(withMethods, cc)
      }
      Patch.fromIterable(patches)
    }
  }

  private def addMethods(methods: Seq[String], clazz: Defn.Class): Patch = {
    val code = methods.mkString("\n")
    clazz.templ.stats.headOption match {
      // body is empty `object Thing` or `object Thing {}`
      case None if clazz.templ.pos.isEmpty =>
        Patch.addRight(
          clazz.ctor,
          s"""| {
              |$code
              |}""".stripMargin
        )
      case None =>
        Patch.replaceTree(
          clazz.templ,
          s"""|{
              |$code
              |}""".stripMargin
        )
      case Some(value) =>
        Patch.addLeft(value, code)
    }
  }

  private def generateWithMethods(cc: Defn.Class): Seq[String] = {
    val nameTypes = cc.ctor.paramClauses.flatMap(c => c.values).map(p => p.name -> p.decltpe.get)

    nameTypes
      .map { case (name, tpe) =>
        val capName = capitalize(name.value)
        s"""|def with$capName(value: $tpe) = {
            |  copy($name = value)
            |}
            |""".stripMargin

      }
  }

  private def capitalize(s: String): String =
    if (s.isEmpty()) s
    else {
      val f = s.head.toUpper.toString()
      f + s.tail
    }

}
