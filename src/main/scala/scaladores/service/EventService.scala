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

object EventService {

  def emitEvent(accountUuid: java.util.UUID,
                correlationUuid: java.util.UUID,
                eventContent: EventContent): ZIO[EventEnvironment, EventFailure, Unit] =
    ZIO.accessM { env =>
      val pipeline = for {
        serial <- env.get[EventRepository.Service].findLatestByCorrelationUuid(correlationUuid).fold(_ => 1, _.serial)
        now    <- env.get[Clock.Service].currentDateTime.orDie
        newEvent <- env
                     .get[UUID.Service]
                     .genUuid
                     .map(uuid => Event(uuid, serial, correlationUuid, accountUuid, now, eventContent))
        _ <- env.get[EventRepository.Service].saveEvent(newEvent).orDie
        _ <- Producer
              .produce[Any, java.util.UUID, Json](new ProducerRecord("tp-account", newEvent.uuid, newEvent.json))
              .orDie

      } yield ()

      pipeline

    }

}
