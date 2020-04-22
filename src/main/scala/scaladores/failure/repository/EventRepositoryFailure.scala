package scaladores.failure.repository

import io.circe.Encoder
import io.circe.generic.semiauto._
import io.circe.syntax._

sealed trait EventRepositoryFailure extends Throwable

object EventRepositoryFailure {

  case class EventRepositoryInsertFailure(msg: String) extends EventRepositoryFailure

  case object EventRepositoryNotFound extends EventRepositoryFailure

  implicit val EventRepositoryInsertFailureEncoder: Encoder[EventRepositoryInsertFailure] =
    deriveEncoder[EventRepositoryInsertFailure]

  implicit val EventRepositoryFailureEncoder: Encoder[EventRepositoryFailure] =
    Encoder.instance[EventRepositoryFailure] {
      case f: EventRepositoryInsertFailure => f.asJson
      case EventRepositoryNotFound         => EventRepositoryNotFound.productPrefix.asJson
    }

}
