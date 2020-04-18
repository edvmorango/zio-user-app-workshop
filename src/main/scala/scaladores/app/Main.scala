package scaladores.app

import scaladores.environment.config._
import zio.{App, Has, ZIO}

object Main extends App {

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {

    for {
      _ <- ZIO
            .accessM[Has[DatabaseConfig]] { e =>
              ZIO.effectTotal(println(e.get[DatabaseConfig]))
            }
            .provideLayer(Layers.databaseConfig)
    } yield 1

  }

}
