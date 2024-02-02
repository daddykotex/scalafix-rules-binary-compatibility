package fix

final case class CaseClassExtractorVisibility(name: String)
object CaseClassExtractorVisibility {
  @scala.annotation.nowarn("msg=private method unapply in object CaseClassExtractorVisibility is never used")
  private def unapply(c: CaseClassExtractorVisibility): Option[CaseClassExtractorVisibility] = Some(c)
  
}

final case class CaseClassExtractorVisibilityParamsCheck(age: Int, `type`: String)
object CaseClassExtractorVisibilityParamsCheck {
  @scala.annotation.nowarn("msg=private method unapply in object CaseClassExtractorVisibilityParamsCheck is never used")
  private def unapply(c: CaseClassExtractorVisibilityParamsCheck): Option[CaseClassExtractorVisibilityParamsCheck] = Some(c)
  
}

final case class CaseClassExtractorVisibilityWithEmptyObject(name: String)
object CaseClassExtractorVisibilityWithEmptyObject {
  @scala.annotation.nowarn("msg=private method unapply in object CaseClassExtractorVisibilityWithEmptyObject is never used")
  private def unapply(c: CaseClassExtractorVisibilityWithEmptyObject): Option[CaseClassExtractorVisibilityWithEmptyObject] = Some(c)
  
}

final case class CaseClassExtractorVisibilityWithOtherContent(name: String)
object CaseClassExtractorVisibilityWithOtherContent {

  /** some comment
    */
    @scala.annotation.nowarn("msg=private method unapply in object CaseClassExtractorVisibilityWithOtherContent is never used")
  private def unapply(c: CaseClassExtractorVisibilityWithOtherContent): Option[CaseClassExtractorVisibilityWithOtherContent] = Some(c)
  def something(): Unit = ()
}

final case class CaseClassExtractorVisibilityWithComment(name: String)

object CaseClassExtractorVisibilityWithComment {
  @scala.annotation.nowarn("msg=private method unapply in object CaseClassExtractorVisibilityWithComment is never used")
  private def unapply(c: CaseClassExtractorVisibilityWithComment): Option[CaseClassExtractorVisibilityWithComment] = Some(c)
  
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
object SomeTraitImplA {
  @scala.annotation.nowarn("msg=private method unapply in object SomeTraitImplA is never used")
  private def unapply[A](c: SomeTraitImplA[A]): Option[SomeTraitImplA[A]] = Some(c)
  
}
final case class SomeTraitImplBPlus[+B]() extends SomeTrait[Any]
object SomeTraitImplBPlus {
  @scala.annotation.nowarn("msg=private method unapply in object SomeTraitImplBPlus is never used")
  private def unapply[B](c: SomeTraitImplBPlus[B]): Option[SomeTraitImplBPlus[B]] = Some(c)
  
}
final case class SomeTraitImplBMinus[-B]() extends SomeTrait[Any]
object SomeTraitImplBMinus {
  @scala.annotation.nowarn("msg=private method unapply in object SomeTraitImplBMinus is never used")
  private def unapply[B](c: SomeTraitImplBMinus[B]): Option[SomeTraitImplBMinus[B]] = Some(c)
  
}