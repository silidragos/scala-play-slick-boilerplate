package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models.{TweetRepo, Tweet}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class HomeController @Inject()(tweetRepo: TweetRepo, cc: ControllerComponents) extends AbstractController(cc) {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok("Welcome Home!")
  }
  
  def findAllTweets = Action.async{ implicit request =>
    tweetRepo.all.map( tweets =>
      Ok(Json.toJson(tweets))  
    )  
  }
  
  def findTweet(tweetId: Int) = Action.async{ implicit request =>
    tweetRepo.findById(tweetId).map( tweet =>
      Ok(Json.toJson(tweet.get))  
    )  
  }
  
  
  def createTweet() = Action.async(parse.json){ implicit request: Request[JsValue] =>
    val tweet = request.body.as[Tweet]
    tweetRepo.add(tweet).map(id =>
      Ok(s"Got new tweet with id $id and body " + (request.body \ "body").as[String])
    )
  }
  
  def deleteTweet(tweetId: Int) = Action.async{ implicit request =>
    tweetRepo.delete(tweetId).map(count => Ok(s"$count tweets deleted!"))
  }
}
