package scaladores.failure

import io.circe.Encoder
import io.circe.generic.semiauto._
import io.circe.syntax._

sealed trait AccountFailure

object AccountFailure {

  case class AccountDocumentAlreadyExistsFailure(document: String) extends AccountFailure

  case object AccountNotFoundFailure extends AccountFailure

  implicit val EncoderAccountDocumentAlreadyExistsFailure: Encoder[AccountDocumentAlreadyExistsFailure] =
    deriveEncoder[AccountDocumentAlreadyExistsFailure]

  implicit val EncoderAccountFailure: Encoder[AccountFailure] = Encoder.instance[AccountFailure] {
    case f: AccountDocumentAlreadyExistsFailure => f.asJson
    case AccountNotFoundFailure                 => AccountNotFoundFailure.productPrefix.asJson
  }

}
