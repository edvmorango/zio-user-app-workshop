package scaladores.failure

import io.circe.Encoder
import io.circe.generic.semiauto._
import io.circe.syntax._

sealed trait AccountFailure

object AccountFailure {

  case class AccountRepositoryFailure(f: AccountRepositoryFailure) extends AccountFailure

  implicit val EncoderAccountRepositoryFailure: Encoder[AccountRepositoryFailure] =
    deriveEncoder[AccountRepositoryFailure]

  implicit val EncoderAccountFailure: Encoder[AccountFailure] = Encoder.instance[AccountFailure] {
    case f: AccountRepositoryFailure => f.asJson
  }

}
