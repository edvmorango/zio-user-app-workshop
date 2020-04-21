package scaladores.endpoint.r

import io.circe.Decoder
import io.circe.generic.semiauto._

case class CreateAccountCommandRequest(document: String)

object CreateAccountCommandRequest {

  implicit val CreateAccountCommandRequestDecoder: Decoder[CreateAccountCommandRequest] =
    deriveDecoder[CreateAccountCommandRequest]

}
