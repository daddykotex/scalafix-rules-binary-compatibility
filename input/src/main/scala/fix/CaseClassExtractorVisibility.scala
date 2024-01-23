/*
rule = CaseClassExtractorVisibility
 */
package fix

final case class CaseClassExtractorVisibility(name: String)
object CaseClassExtractorVisibility {}

final case class CaseClassExtractorVisibilityParamsCheck(age: Int, `type`: String)
object CaseClassExtractorVisibilityParamsCheck {}

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
final case class SomeTraitImplA[A]() extends SomeTrait[A]
object SomeTraitImplA {}
final case class SomeTraitImplBPlus[+B]() extends SomeTrait[Any]
object SomeTraitImplBPlus {}
final case class SomeTraitImplBMinus[-B]() extends SomeTrait[Any]
object SomeTraitImplBMinus {}
