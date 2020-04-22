package scaladores.failure

import io.circe.Encoder
import io.circe.generic.semiauto._
import io.circe.syntax._

sealed trait AccountFailure

object AccountFailure {

  case class AccountDocumentAlreadyExistsFailure(document: String) extends AccountFailure

  case object AccountNotFoundFailure extends AccountFailure

  case class AccountParsingFailure(msg: String) extends AccountFailure

  case class AccountValidationFailure(attribute: String, msg: String) extends AccountFailure

  implicit val AccountDocumentAlreadyExistsFailureEncoder: Encoder[AccountDocumentAlreadyExistsFailure] =
    deriveEncoder[AccountDocumentAlreadyExistsFailure]

  implicit val AccountValidationFailureEncoder: Encoder[AccountValidationFailure] =
    deriveEncoder[AccountValidationFailure]

  implicit val AccountParsingFailureEncoder: Encoder[AccountParsingFailure] =
    deriveEncoder[AccountParsingFailure]

  implicit val AccountFailureEncoder: Encoder[AccountFailure] = Encoder.instance[AccountFailure] {
    case f: AccountDocumentAlreadyExistsFailure => f.asJson
    case f: AccountValidationFailure            => f.asJson
    case f: AccountParsingFailure               => f.asJson
    case AccountNotFoundFailure                 => AccountNotFoundFailure.productPrefix.asJson
  }

}
