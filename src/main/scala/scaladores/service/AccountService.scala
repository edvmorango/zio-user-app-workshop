package scaladores.service

import scaladores.domain.{Account, CreateAccountCommand}
import scaladores.environment.Environments.AccountEnvironment
import scaladores.environment.repository.account.AccountRepository
import zio.ZIO
import scaladores.environment.uuid._
import io.scalaland.chimney.dsl._
import scaladores.failure.AccountFailure

object AccountService {

  def createAccount(command: CreateAccountCommand): ZIO[AccountEnvironment, AccountFailure, Account] = ZIO.accessM {
    env =>
      val pipeline: ZIO[AccountEnvironment, Nothing, Account] = for {
        account <- genUuid.map(uuid => command.into[Account].withFieldConst(_.uuid, uuid).transform)
        _       <- env.get[AccountRepository.Service].create(account).orDie
      } yield account

      pipeline

  }

}
