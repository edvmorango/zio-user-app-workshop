package scaladores.endpoint.r

import java.util._

import io.circe.Encoder
import io.circe.generic.semiauto._

case class HealthResponse(status: String)

object HealthResponse {
  implicit val HealthResponseEncoder: Encoder[HealthResponse] = deriveEncoder[HealthResponse]
}

case class AccountResponse(uuid: UUID, document: String)

object AccountResponse {
  implicit val AccountResponseEncoder: Encoder[AccountResponse] = deriveEncoder[AccountResponse]
}
