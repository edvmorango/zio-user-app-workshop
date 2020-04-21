package scaladores.environment

import scaladores.environment.config._
import scaladores.environment.repository.DBTransactor
import scaladores.environment.repository.account.AccountRepository
import scaladores.environment.uuid.UUID
import zio.ZLayer
import zio.blocking.Blocking
import zio.clock.Clock
import zio.logging.{LogAnnotation, Logging}
import zio.logging.slf4j.Slf4jLogger

object Environments {

  type GlobalEnvironment = Blocking with Clock with UUID with Logging

  type AccountEnvironment = GlobalEnvironment with AccountRepository

  type AppEnvironment =
    Blocking with Clock with UUID with AccountRepository

  val logger: ZLayer[Any, Nothing, Logging] = Slf4jLogger.make((context, message) =>
    s"[correlationId = ${LogAnnotation.CorrelationId.render(context.get(LogAnnotation.CorrelationId))}] $message")

  val global: ZLayer[Any, Nothing, Clock with Blocking with UUID with Logging] =
    Clock.live ++ Blocking.live ++ scaladores.environment.uuid.UUID.live ++ logger

  val dbTransactor: ZLayer[Any, Nothing, DBTransactor] = Layers.databaseConfig >>> DBTransactor.live

  val accountRepository
    : ZLayer[Any, Nothing, AccountRepository] = (Clock.live ++ logger ++ dbTransactor) >>> AccountRepository.live

  val accountEnvironment: ZLayer[Any, Nothing, AccountEnvironment] = global ++ accountRepository

  val appEnvironment = global ++ accountEnvironment

}
