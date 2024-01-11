package fix

import scalafix.v1._
import scala.meta._

class CaseClassCtorVisibility extends SemanticRule("CaseClassCtorVisibility") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    val patches = doc.tree.collect {
      case Utils.caseClass(cc) if !cc.ctor.mods.exists(Utils.mod.isPrivateOrProtected) =>
        addPrivateModToConstructor(cc)
      case _ => Patch.empty
    }
    Patch.fromIterable(patches)
  }

  private def addPrivateModToConstructor(c: Defn.Class): Patch =
    Patch.addRight(c.name, " private ")

}
