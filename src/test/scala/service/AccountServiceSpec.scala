package service

import envs.Environments._
import generators.Commands._
import scaladores.service.AccountService._
import zio.test.Assertion._
import zio.test.TestAspect._
import zio.test._

object AccountServiceSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[_root_.zio.test.environment.TestEnvironment, Any] =
    suite("AccountServiceSpec")(
      testM("should create an account for a valid CreateAccountCommand") {
        checkAllM(anyCreateAccountCommand) { (command) =>
          val pipeline = createAccount(command)
          assertM(pipeline.map(_.document))(equalTo(command.document)).provideLayer(fakeEnv)
        }
      } @@ before(cleanAndMigrate)
    ) @@ after(cleanAndMigrate)
}
