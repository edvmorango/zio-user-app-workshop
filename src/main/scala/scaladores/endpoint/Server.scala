package scaladores.endpoint

import cats.data.Kleisli
import cats.effect.ExitCode
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.{Request, Response}
import scaladores.environment.Environments.AppEnvironment
import scaladores.environment.config.HttpServerConfig
import zio.interop.catz._
import cats.implicits._
import zio.{Has, RIO, ZIO}

object Server {

  type ServerRIO[A] = RIO[AppEnvironment, A]

  def createRoutes(basePath: String): Kleisli[ServerRIO, Request[ServerRIO], Response[ServerRIO]] = {

    val healthEndpoint = new HealthEndpoint[AppEnvironment]("health").endpoints

    val accountEndpoint = new AccountEndpoint[AppEnvironment]("account").endpoints

    val openEndpoints = healthEndpoint <+> accountEndpoint

    Router[ServerRIO](basePath -> openEndpoints).orNotFound

  }

  def runServer: ZIO[Has[HttpServerConfig] with AppEnvironment, Nothing, Unit] =
    ZIO
      .runtime[Has[scaladores.environment.config.HttpServerConfig] with AppEnvironment]
      .flatMap { implicit rts =>
        val cfg = rts.environment.get[HttpServerConfig]

        BlazeServerBuilder[ServerRIO]
          .bindHttp(cfg.port, cfg.host)
          .withHttpApp(createRoutes(cfg.path))
          .serve
          .compile[ServerRIO, ServerRIO, ExitCode]
          .drain

      }
      .orDie

}
