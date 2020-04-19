package service

import zio.test.Assertion._
import zio.test._
import generators.Commands._
import scaladores.service.AccountService._
import envs.Environements.fakeEnv

object AccountServiceSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[_root_.zio.test.environment.TestEnvironment, Any] = suite("AccountServiceSpec")(
    testM("should create an account") {
      checkAllM(anyCreateAccountCommand) { (command) =>
        val pipeline = createAccount(command)

        assertM(pipeline.map(_.document))(equalTo(command.document)).provideLayer(fakeEnv)

      }
    }
  )

}
