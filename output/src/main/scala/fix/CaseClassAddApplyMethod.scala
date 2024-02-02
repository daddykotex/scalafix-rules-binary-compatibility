package fix

final case class CaseClassAddApplyMethod1(name: String)
object CaseClassAddApplyMethod1 {
  def apply(name: String): CaseClassAddApplyMethod1 = {
    new CaseClassAddApplyMethod1(name)
  }

}

final case class CaseClassAddApplyMethod2(name: String, age: Int)
object CaseClassAddApplyMethod2 {
    def apply(name: String,age: Int): CaseClassAddApplyMethod2 = {
    new CaseClassAddApplyMethod2(name, age)
  }
def someOtherMethod() = 1
}

final case class CaseClassAddApplyMethod3[A]()
object CaseClassAddApplyMethod3 {
  def apply[A](): CaseClassAddApplyMethod3[A] = {
    new CaseClassAddApplyMethod3()
  }

}