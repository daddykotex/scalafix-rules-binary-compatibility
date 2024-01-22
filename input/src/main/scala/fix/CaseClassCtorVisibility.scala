/*
rule = CaseClassCtorVisibility
 */
package fix

final case class CaseClassCtorVisibility(name: String) {
  val a = 1
}

final case class CaseClassCtorPrivateVisibility private (name: String) {
  val a = 1
}

private final case class PrivateCaseClassCtorVisibility(name: String) {
  val a = 1
}
