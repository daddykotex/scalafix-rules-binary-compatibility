package fix

final case class CaseClassCtorVisibility private (name: String) {
  val a = 1
}

final case class CaseClassCtorPrivateVisibility private (name: String) {
  val a = 1
}

private final case class PrivateCaseClassCtorVisibility(name: String) {
  val a = 1
}

final case class GenericCaseClassCtorVisibility[A] private (value: A)
