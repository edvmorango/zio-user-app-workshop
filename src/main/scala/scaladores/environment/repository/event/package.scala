package scaladores.environment.repository

import java.time.OffsetDateTime
import java.util.UUID

import io.circe.Json
import scaladores.domain.{Event, EventContent}
import scaladores.failure.repository.EventRepositoryFailure
import zio.{Has, ZIO, ZLayer}
import zio.logging.{Logger, Logging}
import io.scalaland.chimney.dsl._
import io.circe.syntax._
import scaladores.failure.repository.EventRepositoryFailure.{EventRepositoryInsertFailure, EventRepositoryNotFound}
import zio.clock.Clock
import zio.interop.catz._
import io.circe.{Encoder, Decoder}

package object event {

  type EventRepository = Has[EventRepository.Service]

  object EventRepository {

    type LayerEnv = Clock with DBTransactor with Logging

    trait Service {

      def saveEvent[A <: EventContent: Encoder](event: Event[A]): ZIO[Any, EventRepositoryFailure, Unit]

      def findByUuid[A <: EventContent: Decoder](uuid: UUID): ZIO[Any, EventRepositoryFailure, Event[A]]

      def findLatestByCorrelationUuid[A <: EventContent: Decoder](
          uuid: UUID
      ): ZIO[Any, EventRepositoryFailure, Event[A]]

    }

    val live: ZLayer[LayerEnv, Nothing, EventRepository] =
      ZLayer.fromFunction[LayerEnv, EventRepository.Service] { env =>
        import doobie.implicits._
        import doobie.implicits.javatime._
        import doobie.postgres.implicits._

        new Service {

          private case class EventRow(
              uuid: UUID,
              serial: Int,
              correlationUuid: UUID,
              accountUuid: UUID,
              body: Json,
              createdAt: OffsetDateTime
          )

          override def saveEvent[A <: EventContent: Encoder](
              event: Event[A]
          ): ZIO[Any, EventRepositoryFailure, Unit] = {

            def createEvent(row: EventRow) =
              sql"""| INSERT INTO event (
                    | uuid,
                    | correlation_uuid,
                    | serial,
                    | account_uuid,
                    | body,
                    | created_at
                    | ) VALUES (
                    | ${row.uuid}::uuid,
                    | ${row.correlationUuid}::uuid,
                    | ${row.serial},
                    | ${row.accountUuid}::uuid,
                    | ${row.body},
                    | ${row.createdAt}
                    | ) """.stripMargin.update

            val pipeline = for {
              xa <- transactor
              _ <- createEvent(
                    event
                      .into[EventRow]
                      .withFieldComputed(_.body, _.body.asJson)
                      .transform
                  ).run
                    .transact(xa)
            } yield ()

            pipeline
              .mapError(t => EventRepositoryInsertFailure(t.getMessage))
              .tapError(f => env.get[Logger[String]].debug(f.msg))
              .provide(env)

          }

          override def findByUuid[A <: EventContent: Decoder](
              uuid: UUID
          ): ZIO[Any, EventRepositoryFailure, Event[A]] = {
            sql"""| SELECT
                  | uuid,
                  | serial,
                  | correlation_uuid,
                  | account_uuid,
                  | body,
                  | created_at
                  | FROM event
                  | WHERE uuid = ${uuid}::uuid
                 """.stripMargin
              .query[EventRow]
              .to[List]
              .transact(env.get[DBTransactor.Resource].xa)
              .orDie
              .flatMap {
                _.headOption match {
                  case None => ZIO.fail(EventRepositoryNotFound)
                  case Some(w) =>
                    ZIO.succeed(w.into[Event[A]].withFieldComputed(_.body, _.body.as[A].toOption.get).transform)
                }
              }

          }

          override def findLatestByCorrelationUuid[A <: EventContent: Decoder](
              uuid: UUID
          ): ZIO[Any, EventRepositoryFailure, Event[A]] = {
            sql"""| SELECT
                  | uuid,
                  | serial,
                  | correlation_uuid,
                  | account_uuid,
                  | body,
                  | created_at
                  | FROM event
                  | WHERE correlation_uuid = ${uuid}::uuid
                  | ORDER BY serial DESC
                 """.stripMargin
              .query[EventRow]
              .to[List]
              .transact(env.get[DBTransactor.Resource].xa)
              .orDie
              .flatMap {
                _.headOption match {
                  case None => ZIO.fail(EventRepositoryNotFound)
                  case Some(w) =>
                    ZIO.succeed(
                      w.into[Event[A]].withFieldComputed(_.body, _.body.as[A].toOption.get).transform
                    )
                }
              }

          }
        }

      }

  }

}
