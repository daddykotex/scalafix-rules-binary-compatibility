package fix

final case class CaseClassCtorVisibilitySignificantIndentation private (name: String):
  val a = 1

final case class CaseClassCtorPrivateVisibilitySignificantIndentation private (name: String):
  val a = 1
