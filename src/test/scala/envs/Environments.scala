package envs

import org.flywaydb.core.Flyway
import scaladores.environment.config._
import scaladores.environment.repository.DBTransactor
import scaladores.environment.repository.account._
import scaladores.environment.uuid.UUID
import zio.blocking._
import zio.clock._
import zio.logging.Logging
import zio.{ZIO, ZLayer}

object Environments {

  val config = Layers.databaseConfig ++ Layers.httpServer

  val global
    : ZLayer[Any, Nothing, Blocking with Clock with UUID with Logging] = Blocking.live ++ Clock.live ++ UUID.live ++ scaladores.environment.Environments.logger

  val repository
    : ZLayer[Any, Nothing, AccountRepository] = Layers.databaseConfig >>> (global ++ DBTransactor.live) >>> AccountRepository.live

  val fakeEnv: ZLayer[Any, Nothing, Blocking with Clock with UUID with Logging with AccountRepository] =
    global ++ repository

  val cleanAndMigrate = (for {
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
