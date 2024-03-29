package fix

final case class CaseClassExtractorVisibility(name: String)
object CaseClassExtractorVisibility {
  private def unapply(c: CaseClassExtractorVisibility): Option[(String)] = {
    Some(c.name)
  }

}

final case class CaseClassExtractorVisibilityParamsCheck(age: Int, `type`: String)
object CaseClassExtractorVisibilityParamsCheck {
  private def unapply(c: CaseClassExtractorVisibilityParamsCheck): Option[(Int, String)] = {
    Some((c.age, c.`type`))
  }

}

final case class CaseClassExtractorVisibilityWithEmptyObject(name: String)
object CaseClassExtractorVisibilityWithEmptyObject {
  private def unapply(c: CaseClassExtractorVisibilityWithEmptyObject): Option[(String)] = {
    Some(c.name)
  }

}

final case class CaseClassExtractorVisibilityWithOtherContent(name: String)
object CaseClassExtractorVisibilityWithOtherContent {

  /** some comment
    */
    private def unapply(c: CaseClassExtractorVisibilityWithOtherContent): Option[(String)] = {
    Some(c.name)
  }
def something(): Unit = ()
}

final case class CaseClassExtractorVisibilityWithComment(name: String)

object CaseClassExtractorVisibilityWithComment {
  private def unapply(c: CaseClassExtractorVisibilityWithComment): Option[(String)] = {
    Some(c.name)
  }

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
  private def unapply[A](c: SomeTraitImplA[A]): Option[Unit] = {
    Some(())
  }

}
final case class SomeTraitImplBPlus[+B]() extends SomeTrait[Any]
object SomeTraitImplBPlus {
  private def unapply[B](c: SomeTraitImplBPlus[B]): Option[Unit] = {
    Some(())
  }

}
final case class SomeTraitImplBMinus[-B]() extends SomeTrait[Any]
object SomeTraitImplBMinus {
  private def unapply[B](c: SomeTraitImplBMinus[B]): Option[Unit] = {
    Some(())
  }

}