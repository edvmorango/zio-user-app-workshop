package scaladores.endpoint.r

import io.circe.Encoder
import io.circe.generic.semiauto._

case class HealthResponse(status: String)

object HealthResponse {

  implicit val HealthResponseEncoder: Encoder[HealthResponse] = deriveEncoder[HealthResponse]

}
