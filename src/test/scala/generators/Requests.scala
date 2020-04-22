package generators

import scaladores.endpoint.r.CreateAccountCommandRequest
import zio.random.Random
import zio.test.Gen

object Requests {

  val anyCreateAccountCommandRequest: Gen[Random, CreateAccountCommandRequest] = for {
    document <- Gen.long(11111111111L, 99999999999L).map(_.toString)
  } yield CreateAccountCommandRequest(document)

}
