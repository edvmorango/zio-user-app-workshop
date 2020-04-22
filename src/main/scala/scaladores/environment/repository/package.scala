package scaladores.environment

import doobie.util.transactor.Transactor
import zio.{Has, Task, ZIO, ZLayer}
import zio.interop.catz._
import config._
import doobie.util.meta.Meta
import io.circe.Json
import org.postgresql.util.PGobject
import scaladores.environment.repository.DBTransactor.Resource
import io.circe.parser._
package object repository {

  type DBTransactor = Has[DBTransactor.Resource]

  implicit val jsonMeta: Meta[Json] =
    Meta.Advanced.other[PGobject]("json").timap(obj => parse(obj.getValue).toOption.get) { json =>
      val o = new PGobject
      o.setType("json")
      o.setValue(json.noSpaces)
      o
    }

  object DBTransactor {

    trait Resource {

      val xa: Transactor[Task]

    }

    val live: ZLayer[Has[DatabaseConfig], Nothing, DBTransactor] =
      ZLayer.fromFunction { env =>
        val cfg = env.get[DatabaseConfig]
        new Resource {
          override val xa: Transactor[Task] = Transactor.fromDriverManager(cfg.driver, cfg.url, cfg.user, cfg.password)
        }
      }

  }

  val transactor: ZIO[DBTransactor, Nothing, Transactor[Task]] = ZIO.access[DBTransactor](_.get[Resource].xa)

}
