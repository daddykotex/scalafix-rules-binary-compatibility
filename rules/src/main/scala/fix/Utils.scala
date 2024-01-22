package fix

import scalafix.v1._
import scala.meta._
import java.nio.file.Path
import scala.collection.immutable

object Utils {

  object pkg {
    def find(tree: Tree): Option[String] = {
      def go(ps: List[Pkg], acc: List[String]): List[String] = {
        ps match {
          case p :: rest =>
            val others = p.stats.collect { case pp: Pkg => pp }
            go(rest ++ others, acc :+ p.ref.toString())
          case Nil =>
            acc
        }
      }

      tree.collect { case p: Pkg =>
        go(List(p), List.empty).mkString(".")
      }.headOption
    }
  }
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

    def createWithUnapply(c: Defn.Class): Patch = Patch.empty

    def findUnapplyMethod(obj: Defn.Object): Option[Defn.Def] =
      obj.templ.stats.collectFirst {
        case m: Defn.Def if m.name.value == "unapply" =>
          m
      }
  }

  def sanitizeName(name: Name): String = {
    if (Keywords(name.value)) s"`${name.value}`" else name.value
  }

  val Keywords = Set(
    "abstract",
    "case",
    "catch",
    "class",
    "def",
    "do",
    "else",
    "extends",
    "false",
    "final",
    "finally",
    "for",
    "forSome",
    "if",
    "implicit",
    "import",
    "lazy",
    "match",
    "new",
    "object",
    "override",
    "package",
    "private",
    "protected",
    "return",
    "sealed",
    "super",
    "this",
    "throw",
    "trait",
    "true",
    "type",
    "val",
    "while",
    "with",
    "yield"
  )
}
