package models

import javax.inject.Inject
import java.util.UUID

import org.mindrot.jbcrypt.BCrypt
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try


case class User(id:Long, uniqueId: String, username:String, password:String)

object User{
  
  def tupled = (this.apply _).tupled
}

class UserRepository  @Inject()(protected val dbConfigProvider: DatabaseConfigProvider){
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  
  import dbConfig.profile.api._
  private[models] val Users = TableQuery[UsersTable]
  
  def registerUser(username:String, password: String): Future[String] = {
    val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

    db.run(
      ((Users returning Users.map(_.username)) +=
      User(0, UUID.randomUUID().toString(), username, passwordHash))
    )
  }

  def findUserByCredentials(username: String, password: String): Future[Option[User]] = {
    db.run(Users.filter(_.username === username).result.headOption)
    .map(u => u match { 
        case Some(u) => {
          if(BCrypt.checkpw(password, u.password)) Some(u) else None
        }
        case None => None
      })
  }
  
  def findByUniqueUserId(userId: String): Future[Option[User]] = {
    db.run(Users.filter(_.uniqueId === userId).result.headOption)
  }
  
   private[models] class UsersTable(tag: Tag) extends Table[User](tag, "users"){
    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
    def uniqueId= column[String]("unique_id")
    def username = column[String]("username")
    def password= column[String]("password")
    
    def * = (id, uniqueId, username, password) <> (User.tupled, User.unapply)
    
  }
}