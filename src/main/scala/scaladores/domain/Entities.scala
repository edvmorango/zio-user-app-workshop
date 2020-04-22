package scaladores.domain

import java.util.UUID
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

case class Account(uuid: UUID, document: String)

object Account {

  implicit val AccountEncoder: Encoder[Account] = deriveEncoder[Account]
  implicit val AccountDecoder: Decoder[Account] = deriveDecoder[Account]

}
