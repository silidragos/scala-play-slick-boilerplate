package security

import play.api.mvc._
import play.api.libs.json._
import java.util.Base64
import scala.util.control.Exception._
import scala.concurrent.Future

import models.UserRepository

object BasicAuthentication extends Controller{
  def apply[A](userExists: (String, String) => Boolean)(action: Action[A]): Action[A] =
    Action.async(action.parser) { request =>
      request.headers.get("Authorization").flatMap{ authorization =>
        authorization.split(" ").drop(1).headOption.filter{ encoded => 
          val authInfo = new String(Base64.getDecoder().decode(encoded.getBytes)).split(":").toList
          
          allCatch.opt {
            val (username, password) = (authInfo.head, authInfo(1))
            userExists(username, password)
          } getOrElse false
        }
      }.map(_ => action(request)).getOrElse{
        Future.successful(Unauthorized("Unauthorized access!"))
      }
  }
}