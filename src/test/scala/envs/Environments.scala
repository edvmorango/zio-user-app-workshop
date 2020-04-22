package envs

import java.util

import io.circe.Json
import org.flywaydb.core.Flyway
import scaladores.environment
import scaladores.environment.config._
import scaladores.environment.messaging.producer
import scaladores.environment.repository.DBTransactor
import scaladores.environment.repository.account._
import scaladores.environment.repository.event.EventRepository
import scaladores.environment.uuid.UUID
import zio.blocking._
import zio.clock._
import zio.kafka.consumer.Consumer
import zio.kafka.producer.Producer
import zio.logging.Logging
import zio.{URIO, ZIO, ZLayer}
import scaladores.environment.Environments.global
object Environments {

  val config = Layers.databaseConfig ++ Layers.httpServer ++ Layers.messagingConfig

  val accountRepository
    : ZLayer[Any, Nothing, AccountRepository] = Layers.databaseConfig >>> (global ++ DBTransactor.live) >>> AccountRepository.live

  val eventRepository
    : ZLayer[Any, Nothing, EventRepository] = Layers.databaseConfig >>> (global ++ DBTransactor.live) >>> EventRepository.live

  val messaging
    : ZLayer[Any, Nothing, Consumer with Producer[Any, util.UUID, Json]] = (Clock.live ++ Blocking.live ++ Layers.messagingConfig) >>> (environment.messaging.consumer ++ producer)

  val fakeEnv: ZLayer[
    Any,
    Nothing,
    Clock with Blocking with UUID with Logging with Consumer with Producer[Any, util.UUID, Json] with AccountRepository with EventRepository] = global ++ messaging ++ accountRepository ++ eventRepository

  val cleanAndMigrate: URIO[Any, Flyway] = (for {
    flyway <- config.build
               .map(_.get[DatabaseConfig])
               .use { db =>
                 ZIO.effect {
                   val cfg = Flyway.configure()
                   cfg.dataSource(db.url, db.user, db.password)
                   cfg.locations("classpath:/database/migrations")
                   cfg.load()
                 }
               }
    _ <- ZIO.effect(flyway.clean())
    _ <- ZIO.effect(flyway.migrate())
  } yield flyway).orDie

}
