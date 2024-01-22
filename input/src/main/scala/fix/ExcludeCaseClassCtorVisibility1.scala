/*
rule = CaseClassCtorVisibility
CaseClassCtorVisibility.excludedPackages = [
  "fix.internal"
]
 */
package fix
package internal

final case class ExcludeCaseClassCtorVisibility1(name: String)
