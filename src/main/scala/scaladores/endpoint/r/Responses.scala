package scaladores.endpoint.r

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

case class HealthResponse(status: String)

object HealthResponse {

  implicit val HealthResponseEncoder: Encoder[HealthResponse] = deriveEncoder[HealthResponse]
  implicit val HealthResponseDecoder: Decoder[HealthResponse] = deriveDecoder[HealthResponse]

}
