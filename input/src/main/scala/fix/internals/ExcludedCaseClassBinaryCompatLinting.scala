/*
rule = CaseClassBinaryCompatLinting
CaseClassBinaryCompatLinting.excludedPackages = [
  "fix.internals"
]
 */
package fix.internals

final case class RegularCaseClass(name: String)
