/*
rule = CaseClassAddApplyMethod
 */
package fix

final case class CaseClassAddApplyMethod1(name: String)
object CaseClassAddApplyMethod1 {}

final case class CaseClassAddApplyMethod2(name: String, age: Int)
object CaseClassAddApplyMethod2 {
  def someOtherMethod() = 1
}

final case class CaseClassAddApplyMethod3[A]()
object CaseClassAddApplyMethod3
