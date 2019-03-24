package models

import play.api.libs.json.{Json, OFormat}

case class UsersRequest(sureName: String, firstName: String, gender: String, email: String, subscribedNewsletter: Boolean)


object UsersRequest {
  implicit val usersRequestsFormat: OFormat[UsersRequest] = Json.format[UsersRequest]
}
