package scaladores.failure

import io.circe.Encoder
import io.circe.generic.semiauto._
import io.circe.syntax._

sealed trait AccountFailure

object AccountFailure {

  case class AccountDocumentAlreadyExistsFailure(document: String) extends AccountFailure

  case object AccountNotFoundFailure extends AccountFailure

  case class AccountValidationFailure(attribute: String, msg: String) extends AccountFailure

  implicit val EncoderAccountDocumentAlreadyExistsFailure: Encoder[AccountDocumentAlreadyExistsFailure] =
    deriveEncoder[AccountDocumentAlreadyExistsFailure]

  implicit val EncoderAccountValidationFailure: Encoder[AccountValidationFailure] =
    deriveEncoder[AccountValidationFailure]

  implicit val EncoderAccountFailure: Encoder[AccountFailure] = Encoder.instance[AccountFailure] {
    case f: AccountDocumentAlreadyExistsFailure => f.asJson
    case f: AccountValidationFailure            => f.asJson
    case AccountNotFoundFailure                 => AccountNotFoundFailure.productPrefix.asJson

  }

}
