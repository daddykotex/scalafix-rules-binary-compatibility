package fix

final case class CaseClassAddWithMethods1(name: String) {
def withName(value: String) = {
  copy(name = value)
}

}

final case class CaseClassAddWithMethods2(name: String, age: Int) {
def withName(value: String) = {
  copy(name = value)
}

def withAge(value: Int) = {
  copy(age = value)
}

}

final case class CaseClassAddWithMethods3(name: String, age: Int) extends Serializable {
def withName(value: String) = {
  copy(name = value)
}

def withAge(value: Int) = {
  copy(age = value)
}

}