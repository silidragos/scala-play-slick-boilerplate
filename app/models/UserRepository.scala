package models

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


case class User(id:Long, uniqueId: String, username:String, password:String)

object User{
  
  def tupled = (this.apply _).tupled
}

class UserRepository  @Inject()(protected val dbConfigProvider: DatabaseConfigProvider){
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  
  import dbConfig.profile.api._
  private[models] val Users = TableQuery[UsersTable]
  
  
  def findUserByCredentials(username: String, password: String): Future[Option[User]] = {
    db.run(Users.filter(_.username === username).result.headOption)
    .map(u => u match { 
        case Some(u) => {
          if(u.password.equals(password)) Some(u) else None 
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