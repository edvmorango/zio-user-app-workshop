package service

import envs.Environments._
import generators.Commands._
import scaladores.environment.Environments.AccountEnvironment
import scaladores.failure.AccountFailure
import scaladores.failure.AccountFailure.{AccountDocumentAlreadyExistsFailure, AccountValidationFailure}
import scaladores.service.AccountService._
import zio.ZIO
import zio.test.Assertion._
import zio.test.TestAspect.{before, _}
import zio.test._

object AccountServiceSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[_root_.zio.test.environment.TestEnvironment, Any] =
    suite("AccountServiceSpec")(
      testM("should create an account for a valid CreateAccountCommand") {
        checkAllM(anyCreateAccountCommand) { (command) =>
          val pipeline = for {
            account      <- createAccount(command)
            foundAccount <- findByDocument(command.document)
          } yield account == foundAccount

          assertM(pipeline)(isTrue).provideLayer(fakeEnv)
        }
      },
      testM("should fail to create an account when the document is invalid") {
        checkAllM(anyCreateAccountCommand, Gen.anyLong.filter(l => l > 0 && l.toString.length != 11)) {
          (command, invalidDocument) =>
            val pipeline = for {
              _ <- createAccount(command.copy(document = invalidDocument.toString))
            } yield ()

            assertM(pipeline.either)(isLeft(equalTo(AccountValidationFailure("document", "INVALID FORMAT"))))
              .provideLayer(fakeEnv)
        }
      },
      testM("should fail to create an account when the document already exists") {
        checkAllM(anyCreateAccountCommand) { (command) =>
          val pipeline: ZIO[AccountEnvironment, AccountFailure, Unit] = for {
            _ <- createAccount(command)
            _ <- createAccount(command)
          } yield ()

          assertM(pipeline.either)(isLeft(equalTo(AccountDocumentAlreadyExistsFailure(command.document))))
            .provideLayer(fakeEnv)
        }
      }
    ) @@ sequential @@ before(cleanAndMigrate)
}
