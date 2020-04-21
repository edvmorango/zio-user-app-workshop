package scaladores.service

import io.scalaland.chimney.dsl._
import scaladores.domain.{Account, CreateAccountCommand}
import scaladores.environment.Environments.AccountEnvironment
import scaladores.environment.repository.account.AccountRepository
import scaladores.environment.uuid._
import scaladores.failure.AccountFailure
import scaladores.failure.AccountFailure._
import zio.ZIO
import zio.logging._
object AccountService {

  def createAccount(command: CreateAccountCommand): ZIO[AccountEnvironment, AccountFailure, Account] = ZIO.accessM {
    env =>
      val pipeline: ZIO[AccountEnvironment, AccountFailure, Account] = for {
        account <- genUuid.map(uuid => command.into[Account].withFieldConst(_.uuid, uuid).transform)
        _ <- env
              .get[AccountRepository.Service]
              .findByDocument(command.document)
              .flip
              .mapError(a => AccountDocumentAlreadyExistsFailure(a.document))
              .tapError(_ => log.debug("USER ALREADY EXISTS"))
        _ <- env
              .get[AccountRepository.Service]
              .create(account)
              .orDie
      } yield account

      pipeline
  }

  def findByDocument(document: String): ZIO[AccountEnvironment, AccountFailure, Account] = ZIO.accessM { env =>
    env.get[AccountRepository.Service].findByDocument(document).mapError(_ => AccountNotFoundFailure)
  }

}
