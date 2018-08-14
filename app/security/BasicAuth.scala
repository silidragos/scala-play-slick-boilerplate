package security

import javax.inject._
import play.Logger
import play.api.mvc._
import play.api.mvc.Results.Unauthorized
import scala.concurrent.Future
import scala.util.control.Exception._
import java.util.Base64

import models.User
import models.UserRepository
import scala.concurrent.ExecutionContext

case class AuthenticatedRequest[A](val user:User, val request: Request[A]) extends WrappedRequest[A](request)

class BasicAuth @Inject() (val parser: BodyParsers.Default, val ec: ExecutionContext) extends ActionBuilder[AuthenticatedRequest, AnyContent]{
  def invokeBlock[A](
    request: Request[A],
     block: AuthenticatedRequest[A] => Future[Result]) : Future[Result] = {
    
    request.headers.get("Authorization").flatMap(token =>{
     token.split(" ").drop(1).headOption      
    })
    .map(encoded => {
      val authInfo = new String(Base64.getDecoder().decode(encoded.getBytes)).split(":").toList
      (authInfo.head, authInfo(1))
    })
    .map(resultTuple => block(AuthenticatedRequest(User(1,"uuid", resultTuple._1, resultTuple._2), request)))
    .getOrElse(Future.successful(Unauthorized("No account. Sry :( ")))
  }
  
  override def executionContext = ec
}