package repository

import javax.inject.{Inject, Singleton}
import models.{User, UsersRequest}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UsersRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(
    implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class UsersTable(tag: Tag) extends Table[User](tag, "Users") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def sureName = column[String]("sureName")

    def firstName = column[String]("firstName")

    def gender = column[String]("gender")

    def email = column[String]("email")

    def subscribedNewsletter = column[Boolean]("subscribedNewsletter")

    def * =
      (id, sureName, firstName, gender, email, subscribedNewsletter) <> ((User.apply _).tupled, User.unapply)
  }

  /**
    * The starting point for all queries on the people table.
    */
  private val usersTable = TableQuery[UsersTable]

  def setup(): Future[Unit] = {
    val schema = usersTable.schema
    db.run(DBIO.seq(schema.create))
  }

  def createUser(sureName: String,
                 firstName: String,
                 gender: String,
                 email: String,
                 subscribedNewsletter: Boolean): Future[User] = db.run {

    (usersTable.map(p =>
      (p.sureName, p.firstName, p.gender, p.email, p.subscribedNewsletter))
      returning usersTable.map(_.id)

      into ((user, id) => User(id, user._1, user._2, user._3, user._4, user._5))) += (sureName, firstName, gender, email, subscribedNewsletter)
  }

  def checkIfUserExists(user: UsersRequest): Future[Seq[User]] = db.run {
    usersTable.filter(u => u.email === user.email).result
  }

  def getAllUsers: Future[Seq[User]] = db.run {
    usersTable.result
  }

  def getUserById(id: Int): Future[Seq[User]] = db.run {
    usersTable.filter(user => user.id === id).result
  }

}
