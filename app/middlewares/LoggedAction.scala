package middlewares

import play.Logger
import play.api.mvc._
import scala.concurrent.Future

case class LoggedAction[A] (action: Action[A]) extends Action[A]{
  def apply(request: Request[A]) : Future[Result] = {
   Logger.info(s"Calling action ${request.path}")
   action(request)
  }
  
  override def parser = action.parser
  override def executionContext = action.executionContext
}