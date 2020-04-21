package scaladores.app

import zio.{App, ZIO}
object Main extends App {

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {

    for {
      _ <- ZIO.unit
    } yield 1

  }

}
