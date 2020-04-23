package scaladores.environment

import java.io.File

import scaladores.app.Main
import zio.{Has, ZLayer}
import zio.config.magnolia.DeriveConfigDescriptor._
import zio.config.typesafe.TypesafeConfig

package object config {

  case class DatabaseConfig(driver: String, url: String, user: String, password: String)
  case class HttpServerConfig(host: String, port: Int, path: String)
  case class MessagingConfig(host: String)

  case class Config(database: DatabaseConfig, httpServer: HttpServerConfig, messaging: MessagingConfig)

  object Layers {

    private val appConfig: ZLayer[Any, Nothing, zio.config.Config[Config]] = TypesafeConfig
      .fromHoconFile(new File(Main.getClass.getResource("/application.conf").toURI), descriptor[Config])
      .orDie

    val databaseConfig: ZLayer[Any, Nothing, Has[DatabaseConfig]]   = appConfig.map(e => Has(e.get[Config].database))
    val httpServer: ZLayer[Any, Nothing, Has[HttpServerConfig]]     = appConfig.map(e => Has(e.get[Config].httpServer))
    val messagingConfig: ZLayer[Any, Nothing, Has[MessagingConfig]] = appConfig.map(e => Has(e.get[Config].messaging))
  }
}`
