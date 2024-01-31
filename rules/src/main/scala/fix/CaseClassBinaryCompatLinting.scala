package fix

import metaconfig.ConfDecoder
import metaconfig.Configured
import metaconfig.generic.Surface
import scalafix.v1._

import scala.meta._

/** Ensures case class abide by the rules described in:
  * https://docs.scala-lang.org/overviews/core/binary-compatibility-for-library-authors.html#changing-a-case-class-definition-in-a-backwards-compatible-manner
  */
class CaseClassBinaryCompatLinting(config: CaseClassBinaryCompatLintingConfig)
    extends SemanticRule("CaseClassBinaryCompatLinting") {
  def this() = this(CaseClassBinaryCompatLintingConfig.default)

  override def withConfiguration(config: Configuration): Configured[Rule] =
    config.conf
      .getOrElse("CaseClassBinaryCompatLinting")(this.config)
      .map { newConfig => new CaseClassBinaryCompatLinting(newConfig) }

  def isConstructorPrivate(cc: Defn.Class): Boolean =
    cc.ctor.mods.exists(_.is[Mod.Private])

  def isClassPrivate(cc: Defn.Class): Boolean =
    cc.mods.exists(_.is[Mod.Private])

  def isMethodPrivate(ddef: Defn.Def): Boolean =
    ddef.mods.exists(_.is[Mod.Private])

  override def fix(implicit doc: SemanticDocument): Patch = {
    val nonPrivateCaseClasses: Seq[Defn.Class] = doc.tree.collect {
      case Utils.caseClass(cc) if !isClassPrivate(cc) => cc
    }

    Patch.fromIterable(
      nonPrivateCaseClasses.flatMap { cc =>
        val p1 = lintPrivateConstructor(cc)

        val obj = Utils.companion.findCompanionObject(doc.tree)(cc)
        val p2 = lintCompanionObject(obj)(cc)
        val p3 = lintPrivateUnapply(obj)(cc)

        Seq(p1, p2, p3)
      }
    )
  }

  private def lintPrivateConstructor(cc: Defn.Class): Patch = {
    if (isConstructorPrivate(cc)) Patch.empty
    else
      Patch.lint(
        Diagnostic.apply(
          "CaseClassPrivateConstructor",
          "To evolve in a backward compatible way, case classes primary constructor should be private.",
          cc.pos
        )
      )
  }

  private def lintCompanionObject(companionObject: Option[Defn.Object])(cc: Defn.Class): Patch = {
    companionObject match {
      case None =>
        Patch.lint(
          Diagnostic.apply(
            "CaseClassCompanionObject",
            "To evolve in a backward compatible way, you must define the companion object explicitly.",
            cc.pos
          )
        )
      case Some(_) => Patch.empty
    }
  }

  private def lintPrivateUnapply(companionObject: Option[Defn.Object])(cc: Defn.Class): Patch = {
    companionObject match {
      case None => Patch.empty
      case Some(obj) =>
        val unapplyM = Utils.companion.findUnapplyMethod(obj)
        unapplyM match {
          case None =>
            Patch.lint(
              Diagnostic.apply(
                "CaseClassMissingUnapply",
                "To evolve in a backward compatible way, you must implement the `unapply` method explicitly and it must be private.",
                obj.name.pos
              )
            )
          case Some(ddef) =>
            if (isMethodPrivate(ddef)) Patch.empty
            else
              Patch.lint(
                Diagnostic.apply(
                  "CaseClassPrivateUnapply",
                  "To evolve in a backward compatible way, the `unapply` method must be private.",
                  ddef.name.pos
                )
              )
        }
    }
  }

}

case class CaseClassBinaryCompatLintingConfig(
    excludedPackages: List[String] = List.empty
) {
  private val pkgsRegex = excludedPackages.map(_.r)

  def shouldExclude(pkg: String): Boolean = {
    val debug = pkgsRegex.map(r => pkg -> r.findFirstMatchIn(pkg)).mkString
    if (pkgsRegex.isEmpty) false
    else pkgsRegex.exists(_.findFirstMatchIn(pkg).isDefined)
  }
}

object CaseClassBinaryCompatLintingConfig {
  val default = CaseClassBinaryCompatLintingConfig()
  implicit val surface: Surface[CaseClassBinaryCompatLintingConfig] =
    metaconfig.generic.deriveSurface[CaseClassBinaryCompatLintingConfig]
  implicit val decoder: ConfDecoder[fix.CaseClassBinaryCompatLintingConfig] =
    metaconfig.generic.deriveDecoder(default)
}
