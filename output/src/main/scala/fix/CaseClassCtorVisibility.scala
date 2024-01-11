package fix

final case class CaseClassCtorVisibility private (name: String) {
  val a = 1
}

final case class CaseClassCtorPrivateVisibility private (name: String) {
  val a = 1
}
