package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import middlewares.LoggedAction 

import models.UserRepository
import security.BasicAuth

@Singleton
class UserController @Inject()(userRepository: UserRepository, cc: ControllerComponents) extends AbstractController(cc) {
  
  def userLogin() = Action.async(parse.json){ implicit request : Request[JsValue] =>
    println("Entered login()")
    val username = (request.body \ "username").as[String]
    val password = (request.body \ "password").as[String]
    
    userRepository.findUserByCredentials(username, password).map(userOption => {
          userOption match {
            case Some(u) => Ok(s"Welcome ${u}")
            case None => Unauthorized("User doesn't exist!")
          }
    })
  }
}