package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models.{TweetRepo, Tweet}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import middlewares.LoggedAction 

import models.UserRepository
import security.BasicAuth

@Singleton
class HomeController @Inject()(userRepository: UserRepository ,tweetRepo: TweetRepo, basicAuth: BasicAuth, cc: ControllerComponents) extends AbstractController(cc) {

  def index() = basicAuth{ implicit request =>
        Ok(s"Welcome Home! ${request.user.username} , ${request.user.password}")
  }
  
  def findLastXTweets(count: Option[Int]) = Action.async{ implicit request =>
     tweetRepo.findLast(count.getOrElse(0)).map(tweets =>
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
  
  def updateTweet() = Action.async(parse.json){ implicit request: Request[JsValue] =>
    val tweet = request.body.as[Tweet];
    tweetRepo.update(tweet).map( count =>
      Ok(s"Updated $count rouws with id ${tweet.id}")
    )
  }
  
  def deleteTweet(tweetId: Int) = Action.async{ implicit request =>
    tweetRepo.delete(tweetId).map(count => Ok(s"$count tweets deleted!"))
  }
}
