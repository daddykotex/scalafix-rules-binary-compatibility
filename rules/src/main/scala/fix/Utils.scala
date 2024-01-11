package fix

import scala.meta._

object Utils {
  object caseClass {
    def unapply(defn: Tree): Option[Defn.Class] =
      defn match {
        case c: Defn.Class if c.mods.exists(_.is[Mod.Case]) => Some(c)
        case _                                              => None
      }
  }

  object mod {
    def isPrivateOrProtected(mod: Mod): Boolean =
      mod.is[Mod.Private] || mod.is[Mod.Protected]
  }

  object companion {
    def findCompanionObject(tree: Tree)(c: Defn.Class): Option[Defn.Object] = {
      val objects = tree.collect {
        case o: Defn.Object if o.name.value == c.name.value =>
          o
      }

      if (objects.size > 1) sys.error(s"Found multiple objects with the name ${c.name.value}. Expected 0 or 1.")
      else objects.headOption
    }
  }
}
