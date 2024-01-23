/*
rule = CaseClassExtractorVisibility
 */
package fix

final case class CaseClassExtractorVisibility(name: String)

final case class CaseClassExtractorVisibilityParamsCheck(age: Int, `type`: String)

final case class CaseClassExtractorVisibilityWithObject(name: String)

object CaseClassExtractorVisibilityWithObject {}

final case class CaseClassExtractorVisibilityWithEmptyObject(name: String)

object CaseClassExtractorVisibilityWithEmptyObject

final case class CaseClassExtractorVisibilityWithOtherContent(name: String)

object CaseClassExtractorVisibilityWithOtherContent {

  /** some comment
    */
  def something(): Unit = ()
}

final case class CaseClassExtractorVisibilityWithComment(name: String)

object CaseClassExtractorVisibilityWithComment {

  /** some comment
    */
}

final case class CaseClassExtractorVisibilityWithObjectAndPublicUnapply(name: String)

object CaseClassExtractorVisibilityWithObjectAndPublicUnapply {
  def unapply(c: CaseClassExtractorVisibilityWithObjectAndPublicUnapply): Option[(String)] = {
    Some(c.name)
  }
}

final case class CaseClassExtractorVisibilityWithObjectAndPrivateUnapply(name: String)

object CaseClassExtractorVisibilityWithObjectAndPrivateUnapply {
  private def unapply(c: CaseClassExtractorVisibilityWithObjectAndPrivateUnapply): Option[(String)] = {
    Some(c.name)
  }
}

trait SomeTrait[A] {}
final case class NoMatch[A]() extends SomeTrait[A]
