/*
rule = CaseClassAddWithMethods
 */
package fix

final case class CaseClassAddWithMethods1(name: String)

final case class CaseClassAddWithMethods2(name: String, age: Int)

final case class CaseClassAddWithMethods3(name: String, age: Int) extends Serializable
