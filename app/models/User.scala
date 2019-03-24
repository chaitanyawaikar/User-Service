package models

import play.api.libs.json.{Json, OFormat}

case class User(id: Int, sureName: String, firstName: String, gender: String, email: String, subscribedNewsletter: Boolean)


object User {
  implicit val usersFormat: OFormat[User] = Json.format[User]
}
