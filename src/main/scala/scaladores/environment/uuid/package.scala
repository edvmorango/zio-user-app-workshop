package scaladores.environment

import java.util

import io.chrisdavenport.fuuid.FUUID
import zio.{Has, Layer, Task, ZIO, ZLayer}
import zio.interop.catz._

package object uuid {

  type UUID = Has[UUID.Service]

  object UUID {

    trait Service {

      def genUuid: ZIO[Any, Nothing, util.UUID]

    }

    val live: Layer[Nothing, UUID] = ZLayer.succeed(new Service {
      override def genUuid: ZIO[Any, Nothing, util.UUID] = FUUID.randomFUUID[Task].map(FUUID.Unsafe.toUUID).orDie
    })

  }

  def genUuid: ZIO[UUID, Nothing, util.UUID] = ZIO.accessM(_.get[UUID.Service].genUuid)

}
