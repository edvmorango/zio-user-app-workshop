package scaladores

import cats.Applicative
import io.circe._
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe._
import cats.effect.Sync

package object endpoint {

  implicit def apJsonEntityEncoder[F[_]: Applicative, A](implicit encoder: Encoder[A]): EntityEncoder[F, A] =
    jsonEncoderOf[F, A]

  implicit def circeJsonDecoderCRIO[F[_]: Sync, A](implicit decoder: Decoder[A]): EntityDecoder[F, A] =
    jsonOf[F, A]

}
