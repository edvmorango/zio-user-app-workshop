package scaladores.consumer

import cats.implicits._
import scaladores.domain.Event
import scaladores.environment.Environments._
import scaladores.environment.messaging._
import zio.ZIO
import zio.kafka.consumer._
import zio.kafka.serde.Serde
import zio.logging._
import scaladores.domain.EventContent.AccountCreatedEvent
object AccountConsumer {

  type ConsumerEnv = AccountEnvironment

  val pipeline: ZIO[ConsumerEnv, Throwable, Unit] = Consumer
    .subscribeAnd(Subscription.topics("tp-account"))
    .plainStream(Serde.uuid, jsonSerde)
    .flattenChunks
    .mapMPar(16) { record =>
      val expr = record.record.value().as[Event[AccountCreatedEvent]] match {
        case Right(event) =>
          log.locally(LogAnnotation.CorrelationId(event.correlationUuid.some))(effectPipeline(event))
        case Left(_) =>
          ZIO.unit
      }
      expr.as(record.offset)
    }
    .aggregateAsync(Consumer.offsetBatches)
    .mapM(_.commit)
    .runDrain

  def effectPipeline(event: Event[AccountCreatedEvent]): ZIO[ConsumerEnv, Nothing, Unit] = ZIO.accessM { _ =>
    for {
      _ <- log.info(s"EVENT CONSUMED SUCCESSFULLY ${event}")
    } yield ()
  }

}
