package fixtures

import models.{User, UsersRequest}

object UsersFixture {

  def user =
    User(1,
         "Turner",
         "Tom",
         "male",
         "tom.turner@provider.de",
         subscribedNewsletter = true)

  def userRequest =
    UsersRequest("Turner",
                 "Tom",
                 "male",
                 "tom.turner@provider.de",
                 subscribedNewsletter = true)

  def users = Seq(
    User(2,
         "Turner",
         "Tom",
         "male",
         "tom.turner@provider.de",
         subscribedNewsletter = true),
    User(4,
         "Doe",
         "John",
         "male",
         "jon.doe@test-mailing.com",
         subscribedNewsletter = true)
  )

  val userModelJson =
    "{\"id\":1,\"sureName\":\"Turner\",\"firstName\":\"Tom\",\"gender\":\"male\",\"email\":\"tom.turner@provider.de\",\"subscribedNewsletter\":true}"

}
