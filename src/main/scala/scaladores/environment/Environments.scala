package scaladores.environment

import java.util

import io.circe.Json
import scaladores.environment
import scaladores.environment.config._
import scaladores.environment.messaging.producer
import scaladores.environment.repository.DBTransactor
import scaladores.environment.repository.account.AccountRepository
import scaladores.environment.uuid.UUID
import zio.ZLayer
import zio.blocking.Blocking
import zio.clock.Clock
import zio.kafka.consumer.Consumer
import zio.kafka.producer.Producer
import zio.logging.slf4j.Slf4jLogger
import zio.logging.{LogAnnotation, Logging}

object Environments {

  type GlobalEnvironment = Blocking with Clock with UUID with Logging

  type AccountEnvironment = GlobalEnvironment with AccountRepository

  type AppEnvironment =
    Clock with Blocking with UUID with Logging with Consumer with Producer[Any, util.UUID, Json] with AccountRepository

  val logger: ZLayer[Any, Nothing, Logging] = Slf4jLogger.make((context, message) =>
    s"[correlationId = ${LogAnnotation.CorrelationId.render(context.get(LogAnnotation.CorrelationId))}] $message")

  val global: ZLayer[Any, Nothing, Clock with Blocking with UUID with Logging] =
    Clock.live ++ Blocking.live ++ scaladores.environment.uuid.UUID.live ++ logger

  val messaging
    : ZLayer[Any, Nothing, Consumer with Producer[Any, util.UUID, Json]] = (Clock.live ++ Blocking.live ++ Layers.messagingConfig) >>> (environment.messaging.consumer ++ producer)

  val dbTransactor: ZLayer[Any, Nothing, DBTransactor] = Layers.databaseConfig >>> DBTransactor.live

  val accountRepository
    : ZLayer[Any, Nothing, AccountRepository] = (Clock.live ++ logger ++ dbTransactor) >>> AccountRepository.live

  val accountEnvironment: ZLayer[Any, Nothing, AccountEnvironment] = global ++ accountRepository

  val appEnvironment: ZLayer[Any,
                             Nothing,
                             Blocking with Clock with UUID with Logging with Consumer with Producer[
                               Any,
                               util.UUID,
                               Json] with AccountRepository] = global ++ messaging ++ accountRepository

}
