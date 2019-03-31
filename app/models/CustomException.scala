package models

sealed trait CustomException
case object InvalidEmailFormat extends CustomException{
  override def toString: String = "Email format is invalid"
}
case object InvalidGender extends CustomException{
  override def toString: String = "Gender value is invalid. Allowed values are male,female and others"
}
case object InvalidNameFormat extends CustomException{
  override def toString: String = "First name and sure name cannot be empty"
}
