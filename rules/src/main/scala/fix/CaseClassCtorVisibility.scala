package fix

import scalafix.v1._
import scala.meta._

class CaseClassCtorVisibility extends SemanticRule("CaseClassCtorVisibility") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    // the construsctor or the class itself already has a
    // visiblity modifier
    def hasVisibilityMods(cc: Defn.Class): Boolean =
      cc.ctor.mods.exists(Utils.mod.isPrivateOrProtected) ||
        cc.mods.exists(Utils.mod.isPrivateOrProtected)

    val patches = doc.tree.collect {
      case Utils.caseClass(cc) if !hasVisibilityMods(cc) =>
        addPrivateModToConstructor(cc)
      case _ => Patch.empty
    }
    Patch.fromIterable(patches)
  }

  private def addPrivateModToConstructor(c: Defn.Class): Patch =
    Patch.addRight(c.name, " private ")

}
