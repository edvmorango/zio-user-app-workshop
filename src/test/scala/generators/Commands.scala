package generators

import scaladores.domain.CreateAccountCommand
import zio.random.Random
import zio.test.Gen

object Commands {

  val anyCreateAccountCommand: Gen[Random, CreateAccountCommand] = for {
    document <- Gen.long(11111111111L, 99999999999L).map(_.toString)
  } yield CreateAccountCommand(document)

}
