package scaladores.endpoint.support

import io.circe.Json.Null
import io.circe.syntax._
import io.circe.{Decoder, Encoder, Json, Printer}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}
import zio.RIO
import zio.interop.catz._

trait JSONSupport[R] {

  type ClockRIO[A] = RIO[R, A]

  implicit val printer: Printer = Printer.noSpaces.copy(dropNullValues = true)

  implicit def valueAsJson[A](value: A)(implicit encoder: Encoder[A]): Json =
    Encoder[A](encoder)(value)

  implicit def optionAsJson[A](op: Option[A])(implicit encoder: Encoder[A]): Json =
    op.map(valueAsJson(_)(encoder)).getOrElse(Null)

  implicit def valueListAsJson[A](value: List[A])(implicit encoder: Encoder[List[A]]): Json = value match {
    case Nil  => Json.Null
    case list => encoder(list)
  }

  implicit class AnyObjectToJsonImplicit[T](obj: T) {
    implicit def toJson(implicit e: Encoder[T]): Json = obj.asJson
  }

  implicit def circeJsonDecoderCRIO[A](implicit decoder: Decoder[A]): EntityDecoder[ClockRIO, A] =
    jsonOf[ClockRIO, A]
  implicit def circeJsonEncoderCRIO[A](implicit encoder: Encoder[A]): EntityEncoder[ClockRIO, A] =
    jsonEncoderOf[ClockRIO, A]

}
