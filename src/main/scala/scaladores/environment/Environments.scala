package scaladores.environment

import scaladores.environment.repository.DBTransactor
import scaladores.environment.repository.account.AccountRepository
import scaladores.environment.uuid.UUID
import zio.blocking.Blocking
import zio.clock.Clock

object Environments {

  type GlobalEnvironment = Blocking with Clock with UUID

  type AccountEnvironment = GlobalEnvironment with DBTransactor with AccountRepository

}
