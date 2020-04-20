package scaladores.environment

import scaladores.environment.config._
import scaladores.environment.repository.DBTransactor
import scaladores.environment.repository.account.AccountRepository
import scaladores.environment.uuid.UUID
import zio.ZLayer
import zio.blocking.Blocking
import zio.clock.Clock

object Environments {

  type GlobalEnvironment = Blocking with Clock with UUID

  type AccountEnvironment = GlobalEnvironment with AccountRepository

  type AppEnvironment =
    Blocking with Clock with UUID with AccountRepository

  val global
    : ZLayer[Any, Nothing, Clock with Blocking with UUID] = Clock.live ++ Blocking.live ++ scaladores.environment.uuid.UUID.live

  val dbTransactor: ZLayer[Any, Nothing, DBTransactor] = Layers.databaseConfig >>> DBTransactor.live

  val accountRepository
    : ZLayer[Any, Nothing, AccountRepository] = (Clock.live ++ dbTransactor) >>> AccountRepository.live

  val accountEnvironment: ZLayer[Any, Nothing, AccountEnvironment] = global ++ accountRepository

  val appEnvironment = global ++ accountEnvironment

}
