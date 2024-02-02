package fix

final case class CaseClassAddWithMethods1(name: String) {
def withName(value: String): CaseClassAddWithMethods1 = {
  copy(name = value)
}

}

final case class CaseClassAddWithMethods2(name: String, age: Int) {
def withName(value: String): CaseClassAddWithMethods2 = {
  copy(name = value)
}

def withAge(value: Int): CaseClassAddWithMethods2 = {
  copy(age = value)
}

}

final case class CaseClassAddWithMethods3(name: String, age: Int) extends Serializable {
def withName(value: String): CaseClassAddWithMethods3 = {
  copy(name = value)
}

def withAge(value: Int): CaseClassAddWithMethods3 = {
  copy(age = value)
}

}