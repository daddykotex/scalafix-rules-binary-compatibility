package fix

import scalafix.v1._
import scala.meta._

class CaseClassExtractorVisibility extends SemanticRule("CaseClassExtractorVisibility") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    val caseClasses = doc.tree.collect { case Utils.caseClass(cc) => cc }
    val caseClassesWithObjects = caseClasses.map(c => c -> Utils.companion.findCompanionObject(doc.tree)(c))
    val patches = caseClassesWithObjects.map {
      case (cc, None) =>
        // no object
        Patch.addRight(cc, "\n\n" + generateObjectAndUnapply(cc))
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

  private def addPrivateUnapply(d: Defn.Object, cc: Defn.Class): Patch = {
    val generatedUnapply = generateUnapply(cc, indent = 2)
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

  private def generateObjectAndUnapply(cc: Defn.Class): String = {
    val generatedUnapply = generateUnapply(cc, indent = 2)
    s"""|object ${cc.name.value} {
        |$generatedUnapply
        |}""".stripMargin
  }

  private def generateUnapply(cc: Defn.Class, indent: Int): String = {
    val nameTypes = cc.ctor.paramClauses.flatMap(c => c.values).map(p => p.name -> p.decltpe.get)

    val types = nameTypes.map(_._2.toString()).mkString(", ")
    val returnType = s"Option[($types)]"

    val values = nameTypes.map(nt => s"c.${Utils.sanitizeName(nt._1)}").mkString(", ")
    val returnValue = s"Some($values)"
    s"""|  private def unapply(c: ${cc.name.value}): $returnType = {
        |    $returnValue
        |  }""".stripMargin

  }
}
