package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scalafix.v1._

import scala.meta._

case class CaseClassCtorVisibilityConfig(
    excludedPackages: List[String] = List.empty
) {
  private val pkgsRegex = excludedPackages.map(_.r)

  def shouldExclude(pkg: String): Boolean = {
    if (pkgsRegex.isEmpty) false
    else pkgsRegex.exists(_.findFirstMatchIn(pkg).isDefined)
  }
}
object CaseClassCtorVisibilityConfig {
  val default = CaseClassCtorVisibilityConfig()
  implicit val surface: Surface[CaseClassCtorVisibilityConfig] =
    metaconfig.generic.deriveSurface[CaseClassCtorVisibilityConfig]
  implicit val decoder: ConfDecoder[fix.CaseClassCtorVisibilityConfig] =
    metaconfig.generic.deriveDecoder(default)
}

class CaseClassCtorVisibility(config: CaseClassCtorVisibilityConfig) extends SemanticRule("CaseClassCtorVisibility") {
  def this() = this(CaseClassCtorVisibilityConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] =
    config.conf
      .getOrElse("CaseClassCtorVisibility")(this.config)
      .map { newConfig => new CaseClassCtorVisibility(newConfig) }

  override def fix(implicit doc: SemanticDocument): Patch = {
    // the construsctor or the class itself already has a
    // visiblity modifier
    def hasVisibilityMods(cc: Defn.Class): Boolean =
      cc.ctor.mods.exists(Utils.mod.isPrivateOrProtected) ||
        cc.mods.exists(Utils.mod.isPrivateOrProtected)

    val currentPackage = Utils.pkg.find(doc.tree)

    val shouldRun = currentPackage.isEmpty || !currentPackage.exists(config.shouldExclude)

    if (shouldRun) {
      val patches = doc.tree.collect {
        case Utils.caseClass(cc) if !hasVisibilityMods(cc) =>
          addPrivateModToConstructor(cc)
        case _ =>
          Patch.empty
      }
      Patch.fromIterable(patches)
    } else {
      Patch.empty
    }
  }

  private def addPrivateModToConstructor(c: Defn.Class): Patch =
    if (c.tparamClause.isEmpty) Patch.addRight(c.name, " private ")
    else Patch.addRight(c.tparamClause, " private ")
}
