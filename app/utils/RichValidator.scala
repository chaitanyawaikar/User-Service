package utils

import models._

object RichValidator {

  implicit class UserRequestValidation(userRequest : UsersRequest){

    def validateParams: Seq[Either[CustomException, String]] = {
        Seq(validateName, validateEmail, validateGender).filter(_.isLeft)
    }

    private def validateName: Either[InvalidNameFormat.type, String] = {
      if(userRequest.firstName.nonEmpty && userRequest.sureName.nonEmpty)
        Right("")
      else
        Left(InvalidNameFormat)
    }

    private def validateGender: Either[InvalidGender.type, String] = {
      if(Seq("MALE","FEMALE").contains(userRequest.gender.toUpperCase))
        Right("")
      else
        Left(InvalidGender)
    }

    private def validateEmail: Either[InvalidEmailFormat.type, String] = {
      if(userRequest.email.matches("^[A-Za-z0-9+_.-]+@(.+)$"))
        Right("")
      else
        Left(InvalidEmailFormat)
    }
  }

}
