package envs

import scaladores.environment.config._
import scaladores.environment.repository.DBTransactor
import scaladores.environment.repository.account._
import scaladores.environment.uuid.UUID
import zio.{Has, ZLayer}
import zio.blocking._
import zio.clock._

object Environements {

  val config = Layers.databaseConfig ++ Layers.httpServer

  val global: ZLayer[Any, Nothing, Blocking with Clock with UUID] = Blocking.live ++ Clock.live ++ UUID.live

  val repository = Layers.databaseConfig >>> (global ++ DBTransactor.live) >>> AccountRepository.live

  val fakeEnv = global ++ repository ++ (config >>> DBTransactor.live)

}
