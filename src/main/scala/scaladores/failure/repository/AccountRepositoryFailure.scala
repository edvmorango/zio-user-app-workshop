package scaladores.failure.repository

import io.circe.Encoder
import io.circe.generic.semiauto._
import io.circe.syntax._

sealed trait AccountRepositoryFailure extends Throwable

object AccountRepositoryFailure {

  case class AccountRepositoryInsertFailure(msg: String) extends AccountRepositoryFailure

  case object AccountRepositoryNotFound extends AccountRepositoryFailure

  implicit val AccountRepositoryInsertFailureEncoder: Encoder[AccountRepositoryInsertFailure] =
    deriveEncoder[AccountRepositoryInsertFailure]

  implicit val AccountRepositoryFailureEncoder: Encoder[AccountRepositoryFailure] =
    Encoder.instance[AccountRepositoryFailure] {
      case f: AccountRepositoryInsertFailure => f.asJson
      case AccountRepositoryNotFound         => AccountRepositoryNotFound.productPrefix.asJson
    }

}
