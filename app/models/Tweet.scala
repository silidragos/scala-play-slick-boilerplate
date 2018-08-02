package models

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import play.api.libs.functional.syntax._
import slick.dbio
import slick.dbio.Effect.Read
import slick.jdbc.JdbcProfile
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
  
case class Tweet(id: Long, body:String)

object Tweet{
//  implicit val format = Json.WithDefaultValues.format[Tweet]
  
  implicit val tweetReads: Reads[Tweet] = (
      (JsPath \ "id").read[Long] orElse Reads.pure(0L) and
      (JsPath \ "body").read[String]
      )(Tweet.apply _)
      
  implicit val tweetWrites: Writes[Tweet] = Json.writes[Tweet]
  
  def tupled = (this.apply _).tupled
}


class TweetRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider){
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  
  import dbConfig.profile.api._
  private[models] val Tweets = TableQuery[TweetsTable]
  
  private def _findById(id: Long): DBIO[Option[Tweet]] =
    Tweets.filter(_.id === id).result.headOption
    
  def findById(id: Long): Future[Option[Tweet]] =
    db.run(_findById(id))
    
  def all: Future[Seq[Tweet]] = 
    db.run(Tweets.to[List].result)
    
    def add(tweet: Tweet): Future[Long] = 
      db.run((Tweets returning Tweets.map(_.id)) += tweet)
      
    def delete(id: Long): Future[Int] = 
       db.run(Tweets.filter(_.id === id).delete)
    
   private[models] class TweetsTable(tag: Tag) extends Table[Tweet](tag, "tweets"){
    def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
    def body = column[String]("body")
    
    def * = (id, body) <> (Tweet.tupled, Tweet.unapply)
    
  }
}