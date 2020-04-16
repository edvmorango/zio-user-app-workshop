package spec

import zio.test.{DefaultRunnableSpec, ZSpec}
import zio.test._
import zio.test.Assertion._

object ExampleSpec extends DefaultRunnableSpec {
  override def spec: ZSpec[_root_.zio.test.environment.TestEnvironment, Any] = suite("Example spec")(
    test("1 == 1")(assert(1)(equalTo(1)))
  )
}
