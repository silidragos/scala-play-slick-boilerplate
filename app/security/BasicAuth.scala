package security

import javax.inject._
import play.Logger
import play.api.mvc._
import play.api.mvc.Results.Unauthorized
import scala.concurrent.Future
import scala.util.control.Exception._
import scala.util.{Try, Success, Failure}
import scala.concurrent.ExecutionContext
import play.api.libs.concurrent.Execution.Implicits._
import io.igl.jwt.Sub

import models.User
import models.UserRepository
import services.JWTService

case class AuthenticatedRequest[A](val user:User, val request: Request[A]) 
  extends WrappedRequest[A](request)


//TODO: Need to check if JWT expires
class BasicAuth @Inject() (val parser: BodyParsers.Default, val jwtService:JWTService, val userRepository:UserRepository, val ec: ExecutionContext) 
  extends ActionBuilder[AuthenticatedRequest, AnyContent]{
  def invokeBlock[A](
    request: Request[A],
     block: AuthenticatedRequest[A] => Future[Result]) : Future[Result] = {
    
    request.headers.get("Authorization").flatMap(token =>{
     token.split(" ").drop(1).headOption      
    })
    .map(encoded => {
      jwtService.validateToken(encoded)
      })
    .map ( decoded => decoded match {
      case Success(jwt) => userRepository.findByUniqueUserId(jwt.getClaim[Sub].get.value)
      case Failure(e) => Future.successful(None)
    })
    .getOrElse(Future.successful(None))
    .flatMap(u => u match {
        case Some(u) => block(AuthenticatedRequest(u, request))
        case None => Future.successful(Unauthorized("Bad JWT. Sry :( "))
    })
    
//    .map(decoded => decoded match {
//      case Success(jwt) => block(AuthenticatedRequest(userRepository.findByUniqueUserId(jwt.getClaim[Sub].get.value), request))
//      case Failure(e) => Future.successful(Unauthorized("Bad JWT. Sry :( "))
//    })
//    .getOrElse(Future.successful(Unauthorized("No account. Sry :( ")))
  }
  
  override def executionContext = ec
}