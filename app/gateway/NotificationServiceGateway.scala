package gateway

import com.google.inject.Inject
import config.AppConfig
import models.User
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

class NotificationServiceGateway @Inject()(implicit context: ExecutionContext) {

  def sendWelcomeEmail(ws: WSClient, user: User): Future[Boolean] = {
    ws.url(AppConfig.notificationServiceUrl).post(Json.toJson(user)).map {
      response =>
        response.status == 200
    }
  }
}
