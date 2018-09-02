package services

import javax.inject._
import scala.util.Try
import play.api.libs.json.Json
import io.igl.jwt._
import com.github.nscala_time.time.Imports._

@Singleton
class JWTService {
  val secret = "my-secret"
  
  def getTokenForUser(userId: String) : String = {
    val payload = Json.obj("userId" -> userId)
    val jwt = new DecodedJwt(Seq(Alg(Algorithm.HS256), Typ("JWT")), Seq(Exp(DateTime.now().plusHours(1).getMillis()), Sub(userId)))
    return jwt.encodedAndSigned(secret)  
  }
  
  def validateToken(jwt : String) : Try[Jwt] =
    DecodedJwt.validateEncodedJwt(
      jwt,
      secret,
      Algorithm.HS256,
      Set(Typ),
      Set(Exp, Sub)
    )
}