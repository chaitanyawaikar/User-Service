package controllers

import javax.inject.Inject
import models.{ErrorResponse, SuccessResponse, UsersRequest}
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, MessagesAbstractController, MessagesControllerComponents}
import service.UserService
import utils.Constants._

import scala.concurrent.{ExecutionContext, Future}

class UsersController @Inject()(
    service: UserService,
    ws: WSClient,
    cc: MessagesControllerComponents)(implicit ec: ExecutionContext)
    extends MessagesAbstractController(cc) {

  def setup: Action[AnyContent] = Action.async { implicit request =>
    service.setup().map { _ =>
      Ok(Json.toJson(SuccessResponse(OK, SETUP_COMPLETED_SUCCESSFULLY)))
    }
  }

  def getUsers: Action[AnyContent] =
    Action.async { implicit request =>
      service.getAllUsers
        .map { response =>
          Ok(Json.toJson(response))
        }
        .recover {
          case _ =>
            InternalServerError(Json.toJson(
              ErrorResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG)))
        }
    }

  def getUserById(id: Int): Action[AnyContent] =
    Action.async { implicit request =>
      service
        .getUserById(id)
        .map {
          case Right(data) =>
            Ok(Json.toJson(data))
          case Left(error) =>
            NotFound(Json.toJson(ErrorResponse(NOT_FOUND, error.errorMessage)))
        }
        .recover {
          case _ =>
            InternalServerError(Json.toJson(
              ErrorResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG)))
        }
    }

  def createUser: Action[JsValue] = Action.async(parse.json) { request =>
    request.body
      .validate[UsersRequest]
      .map { user: UsersRequest =>
        service
          .createUser(user, ws)
          .map {
            case Right(msg) =>
              Ok(Json.toJson(SuccessResponse(OK, msg)))
            case Left(errorMessages: Seq[String]) =>
              Conflict(Json.toJson(errorMessages.map(ErrorResponse(CONFLICT, _))))
          }
          .recover {
            case _ =>
              InternalServerError(
                Json.toJson(ErrorResponse(INTERNAL_SERVER_ERROR,
                                          INTERNAL_SERVER_ERROR_MSG)))
          }
      }
      .recoverTotal { _ =>
        Future.successful(
          BadRequest(
            Json.toJson(ErrorResponse(BAD_REQUEST, INVALID_JSON_INPUT))))
      }
  }
}
