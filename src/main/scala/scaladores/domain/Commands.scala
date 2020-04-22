package scaladores.domain

import java.util.UUID
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

case class CreateAccountCommand(correlationUuid: UUID, document: String)

object CreateAccountCommand {

  implicit val CreateAccountCommandEncoder: Encoder[CreateAccountCommand] = deriveEncoder[CreateAccountCommand]
  implicit val CreateAccountCommandDecoder: Decoder[CreateAccountCommand] = deriveDecoder[CreateAccountCommand]

}
