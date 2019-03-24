package service

import config.BaseTest
import config.TestUtil._
import controllers.UsersController
import fixtures.UsersFixture._
import gateway.NotificationServiceGateway
import models.{ErrorMessage, UsersRequest}
import org.mockito.Mockito._
import play.api.libs.ws.WSClient
import repository.UsersRepository
import utils.Constants.{USER_ALREADY_EXISTS, USER_CREATED_SUCCESSFULLY, USER_NOT_FOUND}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserServiceTest extends BaseTest {

  var usersRepository: UsersRepository = _
  var notificationServiceGateway: NotificationServiceGateway = _
  var userService: UserService = _
  var userController: UsersController = _
  var wsClient: WSClient = _

  override def beforeEach(): Unit = {
    usersRepository = mock(classOf[UsersRepository])
    notificationServiceGateway = mock(classOf[NotificationServiceGateway])
    wsClient = mock(classOf[WSClient])
    userService = new UserService(usersRepository, notificationServiceGateway)
  }

  test("should fetch the user when given id") {
    val id = 1
    when(usersRepository.getUserById(id))
      .thenReturn(Future.successful(Seq(user)))

    val eventualUser = userService.getUserById(id).await

    eventualUser.right.get shouldBe user
  }

  test("should return error message when given user not found for given id") {
    val id = 1
    when(usersRepository.getUserById(id)).thenReturn(Future.successful(Seq()))

    val eventualUser = userService.getUserById(id).await

    val expectedResult = ErrorMessage(USER_NOT_FOUND)

    eventualUser.left.get shouldBe expectedResult
  }

  test("should fetch all the users") {
    when(usersRepository.getAllUsers).thenReturn(Future.successful(users))

    val actualUsers = userService.getAllUsers.await

    actualUsers shouldBe users
  }

  test(
    "should not create user if a user already exists with the given email id") {
    val userRequest =
      UsersRequest("Turner", "Tom", "male", "tom.turner@provider.de", subscribedNewsletter = true)
    when(usersRepository.checkIfUserExists(userRequest))
      .thenReturn(Future.successful(users))

    val result = userService.createUser(userRequest, wsClient).await

    result.left.get shouldBe USER_ALREADY_EXISTS

  }

  test("should create user if user does not exist") {
    val userRequest =
      UsersRequest("Turner", "Tom", "male", "tom.turner@provider.de", subscribedNewsletter = true)
    when(usersRepository.checkIfUserExists(userRequest))
      .thenReturn(Future.successful(Seq()))
    when(
      usersRepository.createUser(userRequest.sureName,
                                 userRequest.firstName,
                                 userRequest.gender,
                                 userRequest.email,
                                 userRequest.subscribedNewsletter))
      .thenReturn(Future.successful(user))

    val result = userService.createUser(userRequest, wsClient).await

    result.right.get shouldBe USER_CREATED_SUCCESSFULLY + 1

  }

}
