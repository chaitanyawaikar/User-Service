package models

import play.api.libs.json.{Json, Writes}

case class ErrorMessage(errorMessage: String)

case class ErrorResponse(statusCode : Int, errorMessage: String)
case class SuccessResponse(statusCode : Int, successMessage: String)

object ErrorResponse{

  implicit val errorResponseWrites: Writes[ErrorResponse] = (errorResponse: ErrorResponse) => Json.obj(
    "statusCode" -> errorResponse.statusCode,
    "errorMessage" -> errorResponse.errorMessage
  )
}

object SuccessResponse{

  implicit val successResponseWrites: Writes[SuccessResponse] = (successResponse: SuccessResponse) => Json.obj(
    "statusCode" -> successResponse.statusCode,
    "successMessage" -> successResponse.successMessage
  )
}
