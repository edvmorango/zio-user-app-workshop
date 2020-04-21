package scaladores.endpoint

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import scaladores.endpoint.r.HealthResponse
import scaladores.endpoint.support.JSONSupport
import zio.RIO
import zio.clock.Clock
import zio.interop.catz._

class HealthEndpoint[R <: Clock](root: String) extends JSONSupport[R] {

  type HealthTask[A] = RIO[R, A]

  val dsl: Http4sDsl[HealthTask] = Http4sDsl[HealthTask]

  import dsl._

  def endpoints: HttpRoutes[HealthTask] = HttpRoutes.of[HealthTask] {

    case GET -> Root / `root` => Ok(HealthResponse("OK"))

  }

}
