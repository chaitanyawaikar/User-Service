package gateway

import config.TestUtil._
import config.{AppConfig, BaseTest}
import fixtures.UsersFixture._
import org.mockito.Mockito._
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class NotificationServiceGatewayTest extends BaseTest {

  var wsClient: WSClient = _
  var notificationServiceGateway: NotificationServiceGateway = _
  var wSResponse: WSResponse = _
  var wrRequest: WSRequest = _

  override def beforeEach(): Unit = {
    wsClient = mock(classOf[WSClient])
    wSResponse = mock(classOf[WSResponse])
    wrRequest = mock(classOf[WSRequest])
    notificationServiceGateway = new NotificationServiceGateway
  }

  test("should call the notification service with user object") {

    when(wsClient.url(AppConfig.notificationServiceUrl)).thenReturn(wrRequest)
    when(wrRequest.post(Json.toJson(user)))
      .thenReturn(Future.successful(wSResponse))
    when(wSResponse.status).thenReturn(200)

    val future =
      notificationServiceGateway.sendWelcomeEmail(wsClient, user).await

    future shouldBe true
  }

  test("should call the notification service and return error") {

    when(wsClient.url(AppConfig.notificationServiceUrl)).thenReturn(wrRequest)
    when(wrRequest.post(Json.toJson(user)))
      .thenReturn(Future.successful(wSResponse))
    when(wSResponse.status).thenReturn(404)

    val future =
      notificationServiceGateway.sendWelcomeEmail(wsClient, user).await

    future shouldBe false
  }

}
