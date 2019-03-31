package utils

import config.BaseTest
import fixtures.UsersFixture._
import models.{InvalidEmailFormat, InvalidGender, InvalidNameFormat}
import utils.RichValidator._

class RichValidatorTest extends BaseTest {

  test("should validate the email format and return Invalid Email Format error"){
    val model = userRequest.copy(email = "@gmail.com")
    model.validateParams shouldBe Seq(Left(InvalidEmailFormat))
  }

  test("should validate the email format"){
    val model = userRequest.copy(email = "abc@gmail.com")
    model.validateParams shouldBe Seq()
  }

  test("should validate the gender and return InvalidGender error"){
    val model = userRequest.copy(gender = "qw")
    model.validateParams shouldBe Seq(Left(InvalidGender))
  }

  test("should validate the gender when known value given"){
    val model = userRequest.copy(gender = "MaLe")
    model.validateParams shouldBe Seq()
  }

  test("should validate the firstName and sureName and return error if empty"){
    val model = userRequest.copy(firstName = "")
    model.validateParams shouldBe Seq(Left(InvalidNameFormat))
  }

  test("should return seq of errors for all validations that fail for a user request"){
    val model = userRequest.copy(firstName = "",email = "@gmail.com")
    model.validateParams shouldBe Seq(Left(InvalidNameFormat),Left(InvalidEmailFormat))
  }

}
