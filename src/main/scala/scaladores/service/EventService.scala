package scaladores.service

import io.circe.Json
import org.apache.kafka.clients.producer.ProducerRecord
import scaladores.domain.{Event, EventContent}
import scaladores.environment.Environments.EventEnvironment
import scaladores.environment.repository.event.EventRepository
import scaladores.environment.uuid.UUID
import scaladores.failure.EventFailure
import zio.ZIO
import zio.clock.Clock
import zio.kafka.producer.Producer
import io.circe.{Encoder, Decoder}
import io.circe.syntax._

object EventService {

  def emitEvent[A <: EventContent: Encoder: Decoder](
      accountUuid: java.util.UUID,
      correlationUuid: java.util.UUID,
      eventContent: A
  ): ZIO[EventEnvironment, EventFailure, Unit] =
    ZIO.accessM { env =>
      val pipeline = for {
        serial <- env
                   .get[EventRepository.Service]
                   .findLatestByCorrelationUuid[EventContent](correlationUuid)
                   .fold(_ => 1, _.serial + 1)
        now <- env.get[Clock.Service].currentDateTime.orDie
        newEvent <- env
                     .get[UUID.Service]
                     .genUuid
                     .map(uuid => Event[A](uuid, serial, correlationUuid, accountUuid, now, eventContent))

        _ <- env.get[EventRepository.Service].saveEvent[A](newEvent).orDie
        _ <- Producer
              .produce[Any, java.util.UUID, Json](new ProducerRecord("tp-account", newEvent.uuid, newEvent.asJson))
              .orDie

      } yield ()

      pipeline

    }

}
