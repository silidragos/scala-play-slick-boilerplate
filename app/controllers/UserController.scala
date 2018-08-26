package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import middlewares.LoggedAction 

import models.UserRepository
import services.JWTService
import security.BasicAuth

@Singleton
class UserController @Inject()(userRepository: UserRepository, jwtService: JWTService,  cc: ControllerComponents) 
  extends AbstractController(cc) {
  
  def userLogin() = Action.async(parse.json){ implicit request : Request[JsValue] =>
    println("Entered login()")
    val username = (request.body \ "username").as[String]
    val password = (request.body \ "password").as[String]
    
    //val authInfo = new String(Base64.getDecoder().decode(encoded.getBytes)).split(":").toList
    // .map(resultTuple => block(AuthenticatedRequest(User(1,"uuid", resultTuple._1, resultTuple._2), request)))
    
    userRepository.findUserByCredentials(username, password).map(userOption => {
          userOption match {
            case Some(u) => Ok(s"Welcome ${jwtService.getTokenForUser(u.uniqueId)}")
            case None => Unauthorized("User doesn't exist!")
          }
    })
  }
}