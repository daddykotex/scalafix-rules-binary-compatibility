package fix

import scalafix.v1._
import scala.meta._

class CaseClassCtorVisibility extends SemanticRule("CaseClassCtorVisibility") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    val patches = doc.tree.collect {
      case caseClass(cc) if !cc.ctor.mods.exists(isPrivateOrProtected) =>
        addPrivateModToConstructor(cc)
      case _ => Patch.empty
    }
    Patch.fromIterable(patches)
  }

  private def isPrivateOrProtected(mod: Mod): Boolean =
    mod.is[Mod.Private] || mod.is[Mod.Protected]

  private def addPrivateModToConstructor(c: Defn.Class): Patch =
    Patch.addRight(c.name, " private ")

  object caseClass {
    def unapply(defn: Tree): Option[Defn.Class] =
      defn match {
        case c: Defn.Class if c.mods.exists(_.is[Mod.Case]) => Some(c)
        case _                                              => None
      }
  }

}
