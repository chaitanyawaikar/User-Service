package controllers

import java.util.concurrent.TimeUnit

import akka.util.Timeout
import fixtures.UsersFixture._
import models.ErrorMessage
import org.mockito.Matchers.any
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import service.UserService
import utils.Constants.{USER_ALREADY_EXISTS, USER_CREATED_SUCCESSFULLY, USER_NOT_FOUND}

import scala.concurrent.{ExecutionContext, Future}

//noinspection ScalaDeprecation
class UsersControllerTest
    extends PlaySpec
    with GuiceOneAppPerSuite
    with MockitoSugar {

  "UsersController" should {

    implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
    val mockService = mock[UserService]

    val application = new GuiceApplicationBuilder()
      .overrides(bind[UserService].toInstance(mockService))
      .build

    "return list of users with status 200 when /users endpoint hit" in {
      when(mockService.getAllUsers).thenReturn(Future.successful(users))

      val fakeRequest = FakeRequest(GET, "/user-service/api/users")
      val futureResult: Future[Result] = route(application, fakeRequest).get
      val resultJson: JsValue =
        contentAsJson(futureResult)(Timeout(2, TimeUnit.SECONDS))
      resultJson mustBe Json.toJson(users)
      status(futureResult) mustBe 200
    }

    "return internal server error with status 500 when /users encounters exception" in {
      when(mockService.getAllUsers).thenReturn(Future.failed(new RuntimeException))

      val fakeRequest = FakeRequest(GET, "/user-service/api/users")
      val futureResult: Future[Result] = route(application, fakeRequest).get
      status(futureResult) mustBe 500
    }

    "return user by id with status 200 when /users/id endpoint hit" in {
      val id1 = 1
      val userModel = user.copy(id = id1)
      when(mockService.getUserById(id1)).thenReturn(Future.successful(Right(userModel)))

      val fakeRequest = FakeRequest(GET, "/user-service/api/users/1")
      val futureResult: Future[Result] = route(application, fakeRequest).get
      val resultJson: JsValue =
        contentAsJson(futureResult)(Timeout(2, TimeUnit.SECONDS))
      resultJson mustBe Json.toJson(userModel)
      status(futureResult) mustBe 200
    }

    "return user not found when /users/id endpoint hit" in {
      val id1 = 1
      when(mockService.getUserById(id1)).thenReturn(Future.successful(Left(ErrorMessage(USER_NOT_FOUND))))

      val fakeRequest = FakeRequest(GET, "/user-service/api/users/1")
      val futureResult: Future[Result] = route(application, fakeRequest).get
      status(futureResult) mustBe 404
    }

    "return internal server error with status 500 when /users/id encounters exception" in {
      when(mockService.getUserById(1)).thenReturn(Future.failed(new RuntimeException))

      val fakeRequest = FakeRequest(GET, "/user-service/api/users/1")
      val futureResult: Future[Result] = route(application, fakeRequest).get
      status(futureResult) mustBe 500
    }

    "create user when POST endpoint /users hit" in {

      val fakeRequest = FakeRequest(POST, "/user-service/api/users").withBody(Json.toJson(userRequest))

      when(mockService.createUser(any(),any()))
        .thenReturn(Future.successful(Right(USER_CREATED_SUCCESSFULLY)))

      val futureResult: Future[Result] = route(application, fakeRequest).get
      status(futureResult) mustBe 200
    }

    "return conflict code when user already exists" in {

      val fakeRequest = FakeRequest(POST, "/user-service/api/users").withBody(Json.toJson(userRequest))

      when(mockService.createUser(any(),any()))
        .thenReturn(Future.successful(Left(Seq(USER_ALREADY_EXISTS))))

      val futureResult: Future[Result] = route(application, fakeRequest).get
      status(futureResult) mustBe 409
    }

    "return internal server error when runtime exception encountered" in {

      val fakeRequest = FakeRequest(POST, "/user-service/api/users").withBody(Json.toJson(userRequest))

      when(mockService.createUser(any(),any()))
        .thenReturn(Future.failed(new RuntimeException))

      val futureResult: Future[Result] = route(application, fakeRequest).get
      status(futureResult) mustBe 500
    }

  }
}
