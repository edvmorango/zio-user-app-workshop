package endpoint

import cats.data.Kleisli
import envs.Environments.{cleanAndMigrate, fakeEnv}
import generators.Requests.anyCreateAccountCommandRequest
import io.circe.Json
import org.http4s._
import scaladores.endpoint.Server
import scaladores.endpoint.Server.ServerRIO
import scaladores.endpoint.r.AccountResponse
import scaladores.endpoint.support.JSONSupport
import scaladores.environment.Environments.{AccountEnvironment, AppEnvironment}
import zio.RIO
import zio.test.Assertion._
import zio.test.TestAspect._
import zio.test._
import zio.interop.catz._

object AccountEndpointSpec extends DefaultRunnableSpec with JSONSupport[AppEnvironment] {

  private val endpoint: Kleisli[ServerRIO, Request[ServerRIO], Response[ServerRIO]] = Server.createRoutes("/")

  type AccountTask[A] = RIO[AccountEnvironment, A]

  override def spec: ZSpec[_root_.zio.test.environment.TestEnvironment, Any] =
    suite("AccountEndpoint")(
      testM("a valid POST to /account must return 201")(
        checkAllM(anyCreateAccountCommandRequest) { (request) =>
          for {
            res <- endpoint
                    .run(Request[ServerRIO](Method.POST, Uri.unsafeFromString("/account")).withEntity(request))
                    .provideLayer(fakeEnv)
          } yield assert(res.status)(equalTo(Status.Created))
        }
      ),
      testM("a GET to /account/:accountUuid who exists  must return 200")(
        checkAllM(anyCreateAccountCommandRequest) { (request) =>
          val pipeline = for {
            postRes <- endpoint
                        .run(Request[ServerRIO](Method.POST, Uri.unsafeFromString("/account")).withEntity(request))
            body <- postRes.as[AccountResponse]
            res  <- endpoint.run(Request[ServerRIO](Method.GET, Uri.unsafeFromString(s"/account/${body.uuid}")))
          } yield assert(res.status)(equalTo(Status.Ok))

          pipeline.provideLayer(fakeEnv)

        }
      ),
      testM("a GET to /account?document=:document who exists  ust return 200")(
        checkAllM(anyCreateAccountCommandRequest) { request =>
          val pipeline = for {
            postRes <- endpoint
                        .run(Request[ServerRIO](Method.POST, Uri.unsafeFromString("/account")).withEntity(request))
            body <- postRes.as[AccountResponse]
            res <- endpoint.run(
                    Request[ServerRIO](Method.GET, Uri.unsafeFromString(s"/account?document=${body.document}")))
          } yield assert(res.status)(equalTo(Status.Ok))

          pipeline.provideLayer(fakeEnv)

        }
      ),
      testM("a valid but duplicated POST to /account must return 409")(
        checkAllM(anyCreateAccountCommandRequest) { request =>
          val pipeline = for {
            _ <- endpoint
                  .run(Request[ServerRIO](Method.POST, Uri.unsafeFromString("/account")).withEntity(request))
            res <- endpoint
                    .run(Request[ServerRIO](Method.POST, Uri.unsafeFromString("/account")).withEntity(request))
          } yield assert(res.status)(equalTo(Status.Conflict))

          pipeline.provideLayer(fakeEnv)
        }
      ),
      testM("an invalid POST to /account must return 400")(
        checkAllM(anyCreateAccountCommandRequest) { _ =>
          val pipeline = for {
            res <- endpoint
                    .run(Request[ServerRIO](Method.POST, Uri.unsafeFromString("/account"))
                      .withEntity(Json.obj("arbitrary" -> Json.fromString("arbitrary"))))
          } yield assert(res.status)(equalTo(Status.BadRequest))

          pipeline.provideLayer(fakeEnv)
        }
      ),
    ) @@ sequential @@ before(cleanAndMigrate)

}
