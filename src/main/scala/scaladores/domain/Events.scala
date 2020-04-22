package scaladores.domain

import java.time.OffsetDateTime
import java.util.UUID

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._
import io.circe.syntax._
import cats.syntax.functor._

case class Event(uuid: UUID,
                 serial: Int,
                 correlationUuid: UUID,
                 accountUuid: UUID,
                 createdAt: OffsetDateTime,
                 body: EventContent) { self =>

  final def json = self.asJson

}

object Event {

  implicit val EventEncoder: Encoder[Event] = deriveEncoder[Event]
  implicit val EventDecoder: Decoder[Event] = deriveDecoder[Event]

}

sealed trait EventContent

object EventContent {

  case class AccountCreatedEvent(account: Account) extends EventContent

  implicit val AccountCreatedEventEncoder: Encoder[AccountCreatedEvent] = deriveEncoder[AccountCreatedEvent]
  implicit val AccountCreatedEventDecoder: Decoder[AccountCreatedEvent] = deriveDecoder[AccountCreatedEvent]

  implicit val EventContentEncoder: Encoder[EventContent] = Encoder.instance[EventContent] {
    case e: AccountCreatedEvent => e.asJson
  }

  implicit val EventContentDecoder: Decoder[EventContent] =
    List[Decoder[EventContent]](AccountCreatedEventDecoder.widen).reduceLeft(_ or _)

}
