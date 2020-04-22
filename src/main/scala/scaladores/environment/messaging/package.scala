package scaladores.environment

import java.util.UUID

import scaladores.environment.config.MessagingConfig
import zio.{Has, ZLayer, ZManaged}
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde.Serde
import io.circe.Json
import io.circe.parser.parse
import zio.blocking.Blocking
import zio.clock.Clock
import zio.kafka.consumer.Consumer.{AutoOffsetStrategy, OffsetRetrieval}
import zio.kafka.consumer.{Consumer, ConsumerSettings}
package object messaging {

  val jsonSerde = Serde.string.inmap(str => parse(str).toOption.get)(_.noSpaces)

  val producer: ZLayer[Has[MessagingConfig], Nothing, Producer[Any, UUID, Json]] = ZLayer
    .fromManaged {
      for {
        cfg <- ZManaged
                .access[Has[MessagingConfig]](_.get[MessagingConfig].host.split(',').toList)
        res <- Producer.make[Any, UUID, Json](ProducerSettings(cfg), Serde.uuid, jsonSerde).build
      } yield res
    }
    .map(_.get[Producer[Any, UUID, Json]])
    .orDie

  val consumer: ZLayer[Clock with Blocking with Has[MessagingConfig], Nothing, Consumer] = ZLayer
    .fromManaged {
      for {
        cfg <- ZManaged
                .access[Has[MessagingConfig]](_.get[MessagingConfig].host.split(',').toList)
        res <- Consumer
                .make(
                  ConsumerSettings(cfg)
                    .withGroupId("groupId")
                    .withOffsetRetrieval(OffsetRetrieval.Auto(AutoOffsetStrategy.Earliest)))
                .build
      } yield res
    }
    .map(_.get[Consumer])
    .orDie

}
