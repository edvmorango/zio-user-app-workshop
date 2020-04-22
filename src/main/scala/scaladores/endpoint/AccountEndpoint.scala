package scaladores.endpoint

import io.scalaland.chimney.dsl._
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Response}
import scaladores.domain.{Account, CreateAccountCommand}
import scaladores.endpoint.r.{AccountResponse, CreateAccountCommandRequest}
import scaladores.endpoint.support.JSONSupport
import scaladores.environment.Environments.AccountEnvironment
import scaladores.environment.uuid.UUID
import scaladores.failure.AccountFailure
import scaladores.failure.AccountFailure._
import scaladores.service.AccountService._
import zio.interop.catz._
import zio.{RIO, ZIO}
import cats.implicits._
import zio.logging._
class AccountEndpoint[R <: AccountEnvironment](root: String) extends JSONSupport[R] {

  type AccountTask[A] = RIO[R, A]

  val dsl: Http4sDsl[AccountTask] = Http4sDsl[AccountTask]

  import dsl._

  private def failureHandler(failure: AccountFailure): AccountTask[Response[AccountTask]] = failure match {
    case AccountNotFoundFailure                 => NotFound()
    case f: AccountValidationFailure            => BadRequest(f)
    case f: AccountDocumentAlreadyExistsFailure => Conflict(f)
    case f: AccountParsingFailure               => BadRequest(f)
  }

  object DocumentQueryParameter extends QueryParamDecoderMatcher[String]("document")

  def endpoints: HttpRoutes[AccountTask] = HttpRoutes.of[AccountTask] {

    case GET -> Root / `root` / UUIDVar(uuid) =>
      val pipeline: ZIO[R, AccountFailure, Account] = findByUuid(uuid)

      pipeline
        .foldM(failureHandler, v => Ok(v.into[AccountResponse].transform))

    case GET -> Root / `root` :? DocumentQueryParameter(document) =>
      val pipeline: ZIO[R, AccountFailure, Account] = findByDocument(document)

      pipeline
        .foldM(failureHandler, v => Ok(v.into[AccountResponse].transform))

    case req @ POST -> Root / `root` =>
      val pipeline = for {

        request <- req.as[CreateAccountCommandRequest].mapError(t => AccountParsingFailure(t.getMessage))

        correlationUuid <- ZIO.accessM[UUID](_.get[UUID.Service].genUuid)

        account <- log.locally(LogAnnotation.CorrelationId(correlationUuid.some)) {
                    createAccount(
                      request.into[CreateAccountCommand].withFieldConst(_.correlationUuid, correlationUuid).transform)
                  }
      } yield account

      pipeline
        .foldM(failureHandler, v => Created(v.into[AccountResponse].transform))

  }

}
