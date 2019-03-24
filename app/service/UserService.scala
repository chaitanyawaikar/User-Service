package service

import com.google.inject.{Inject, Singleton}
import gateway.NotificationServiceGateway
import models.{ErrorMessage, User, UsersRequest}
import play.api.libs.ws.WSClient
import repository.UsersRepository
import utils.Constants._
import utils.SetupData.setupData

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserService @Inject()(
    usersRepository: UsersRepository,
    notificationServiceGateway: NotificationServiceGateway)(
    implicit ec: ExecutionContext) {

  def createUser(user: UsersRequest,
                 ws: WSClient): Future[Either[String, String]] = {

    usersRepository.checkIfUserExists(user).flatMap { users =>
      if (users.nonEmpty) {
        Future.successful(Left(USER_ALREADY_EXISTS))
      } else {
        usersRepository
          .createUser(user.sureName,
                      user.firstName,
                      user.gender,
                      user.email,
                      user.subscribedNewsletter)
          .map { u: User =>
            notificationServiceGateway.sendWelcomeEmail(ws, u)
            Right(USER_CREATED_SUCCESSFULLY + u.id)
          }
      }
    }
  }

  def getAllUsers: Future[Seq[User]] = usersRepository.getAllUsers()

  def getUserById(id: Int): Future[Either[ErrorMessage, User]] = {
    usersRepository.getUserById(id).map { users =>
      users.toList match {
        case h :: Nil => Right(h)
        case _        => Left(ErrorMessage(USER_NOT_FOUND))
      }
    }
  }

  def setup(): Future[Seq[User]] = {
    usersRepository.setup().flatMap { _ =>
      Future.sequence(
        setupData.map(
          u =>
            usersRepository.createUser(u.sureName,
                                       u.firstName,
                                       u.gender,
                                       u.email,
                                       u.subscribedNewsletter)))
    }
  }
}
