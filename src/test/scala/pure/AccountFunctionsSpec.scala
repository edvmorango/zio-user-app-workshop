package pure

import generators.Commands._
import scaladores.service.pure.AccountFunctions._
import zio.test.Assertion.equalTo
import zio.test.TestAspect._
import zio.test.{DefaultRunnableSpec, ZSpec, assert, suite, _}

object AccountFunctionsSpec extends DefaultRunnableSpec {
  override def spec: ZSpec[_root_.zio.test.environment.TestEnvironment, Any] = suite("AccountFunctionsProperties")(
    suite(
      "Commutativity (this isn't really commutative, use Validated instead)   --- validateDocumentSize ∘ validateDocumentCharacters = validateDocumentCharacters ∘  validateDocumentSize ")(
      testM("validateDocumentSize and validateDocumentCharacters succeeds") {
        check(anyCreateAccountCommand) { c =>
          val fg = validateDocumentSize(c).flatMap(validateDocumentCharacters)
          val gf = validateDocumentCharacters(c).flatMap(validateDocumentSize)

          assert(fg)(equalTo(gf))

        }

      },
      testM("validateDocumentSize fails") {
        check(anyCreateAccountCommand, Gen.anyLong.filter(l => l > 0 && l.toString.length != 11)) { (c, newDocument) =>
          val nc = c.copy(document = newDocument.toString)

          val fg = validateDocumentSize(nc).flatMap(validateDocumentCharacters)
          val gf = validateDocumentCharacters(nc).flatMap(validateDocumentSize)

          assert(fg)(equalTo(gf))

        }

      },
      testM("validateDocumentCharacters fails") {
        check(anyCreateAccountCommand, Gen.anyString.filter(_.length == 11)) { (c, newDocument) =>
          val nc = c.copy(document = newDocument)

          val fg = validateDocumentSize(nc).flatMap(validateDocumentCharacters)
          val gf = validateDocumentCharacters(nc).flatMap(validateDocumentSize)

          assert(fg)(equalTo(gf))

        }

      },
      testM("validateDocumentCharacters and validateDocumentSize fails") {
        check(anyCreateAccountCommand, Gen.anyString.filter(s => s.length > 0 && s.length != 11)) { (c, newDocument) =>
          val nc = c.copy(document = newDocument)

          val fg = validateDocumentSize(nc).flatMap(validateDocumentCharacters)
          val gf = validateDocumentCharacters(nc).flatMap(validateDocumentSize)

          assert(fg)(equalTo(gf))

        }

      } @@ ignore
    )
  )
}
