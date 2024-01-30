/*
rule = CaseClassBinaryCompatLintingRule
 */
package fix

// format: off

//scalafix:off CaseClassBinaryCompatLintingRule.CaseClassCompanionObject
final case class RegularCaseClass(name: String) /* assert: CaseClassBinaryCompatLintingRule.CaseClassPrivateConstructor
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
To evolve in a backward compatible way, case classes primary constructor should be private. */
//scalafix:on CaseClassBinaryCompatLintingRule.CaseClassCompanionObject

final case class CaseClassWithPrivateCtor private (name: String) /* assert: CaseClassBinaryCompatLintingRule.CaseClassCompanionObject
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
To evolve in a backward compatible way, you must define the companion object explicitly. */

final case class CompanionObjectWithoutUnapply private (name: String)

object CompanionObjectWithoutUnapply {/* assert: CaseClassBinaryCompatLintingRule.CaseClassMissingUnapply
       ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
To evolve in a backward compatible way, you must implement the `unapply` method explicitly and it must be private. */
    def test = 1
}

final case class CompanionObjectPublicUnapply private (name: String)

object CompanionObjectPublicUnapply {
    def unapply(c: CompanionObjectPublicUnapply): Option[CompanionObjectPublicUnapply] = {/* assert: CaseClassBinaryCompatLintingRule.CaseClassPrivateUnapply
        ^^^^^^^
To evolve in a backward compatible way, the `unapply` method must be private. */
        Some(c)
    }
}