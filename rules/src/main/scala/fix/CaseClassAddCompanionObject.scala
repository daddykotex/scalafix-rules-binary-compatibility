package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scalafix.v1._

import scala.meta._

case class CaseClassAddCompanionObjectConfig(
    excludedPackages: List[String] = List.empty
) {
  private val pkgsRegex = excludedPackages.map(_.r)

  def shouldExclude(pkg: String): Boolean = {
    val debug = pkgsRegex.map(r => pkg -> r.findFirstMatchIn(pkg)).mkString
    if (pkgsRegex.isEmpty) false
    else pkgsRegex.exists(_.findFirstMatchIn(pkg).isDefined)
  }
}
object CaseClassAddCompanionObjectConfig {
  val default = CaseClassAddCompanionObjectConfig()
  implicit val surface: Surface[CaseClassAddCompanionObjectConfig] =
    metaconfig.generic.deriveSurface[CaseClassAddCompanionObjectConfig]
  implicit val decoder: ConfDecoder[fix.CaseClassAddCompanionObjectConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class CaseClassAddCompanionObject(config: CaseClassAddCompanionObjectConfig)
    extends SemanticRule("CaseClassAddCompanionObject") {
  def this() = this(CaseClassAddCompanionObjectConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] =
    config.conf
      .getOrElse("CaseClassAddCompanionObject")(this.config)
      .map { newConfig => new CaseClassAddCompanionObject(newConfig) }

  override def fix(implicit doc: SemanticDocument): Patch = {
    val caseClasses = doc.tree.collect { case Utils.caseClass(cc) => cc }
    val caseClassesWithObjects = caseClasses.map(c => c -> Utils.companion.findCompanionObject(doc.tree)(c))
    val patches = caseClassesWithObjects.map {
      case (cc, None) =>
        // no object
        Patch.addRight(cc, "\n\n" + generateEmptyObject(cc))
      case (_, _) => Patch.empty
    }
    Patch.fromIterable(patches)
  }

  private def generateEmptyObject(cc: Defn.Class): String = {
    s"""|object ${cc.name.value} {
        |
        |}""".stripMargin
  }
}
