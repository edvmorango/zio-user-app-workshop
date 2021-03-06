package scaladores.app

import scaladores.consumer.AccountConsumer
import scaladores.endpoint.Server
import scaladores.environment.Environments._
import scaladores.environment.config.Layers._
import zio.{App, ZIO}

object Main extends App {

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {

    val pipeline: ZIO[Any, Nothing, Int] = for {
      _ <- AccountConsumer.pipeline.provideLayer(appEnvironment).fork
      _ <- Server.runServer.provideLayer(httpServer ++ appEnvironment)
    } yield 1

    pipeline

  }

}
