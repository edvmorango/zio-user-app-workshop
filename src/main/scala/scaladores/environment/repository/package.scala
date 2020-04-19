package scaladores.environment

import doobie.util.transactor.Transactor
import zio.{Has, Task, ZIO, ZLayer}
import zio.interop.catz._
import config._
import scaladores.environment.repository.DBTransactor.Resource

package object repository {

  type DBTransactor = Has[DBTransactor.Resource]

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
