package models

import config.BaseTest
import fixtures.UsersFixture._
import play.api.libs.json.Json


class UserTest extends BaseTest {

  test("should serialize to users model"){
    val actualSerializedModel = Json.toJson(user)
    actualSerializedModel.toString() shouldBe userModelJson
  }

}
