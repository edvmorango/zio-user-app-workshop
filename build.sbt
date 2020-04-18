name := "zio-user-app-workshop"

version := "0.1"

scalaVersion := "2.13.1"

resolvers += Resolver.mavenCentral
resolvers += Resolver.jcenterRepo

scalacOptions ++= Seq(
  "-language:higherKinds",
  "-deprecation",
  "-encoding",
  "utf-8",
  "-explaintypes",
  "-feature",
  "-language:existentials",
  "-language:implicitConversions",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint:infer-any",
  "-Xlint:type-parameter-shadow",
  "-Ywarn-dead-code",
  "-Ywarn-extra-implicit",
  "-Ywarn-unused:implicits",
  "-Ywarn-unused:imports",
  "-Ywarn-unused:locals",
  "-Ywarn-unused:params",
  "-Ywarn-unused:patvars",
  "-Ywarn-unused:privates",
  "-Ywarn-value-discard"
)

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)

val zioVersion        = "1.0.0-RC18-2"
val zioInteropCats    = "2.0.0.0-RC12"
val zioLoggingVersion = "0.2.7"
val zioConfigVersion  = "1.0.0-RC16-1"
val zioKafkaVersion   = "0.8.0"

val chimneyVersion = "0.5.0"
val fuuidVersion = "0.3.0"
val circeVersion = "0.13.0"
val http4sVersion = "0.21.3"
val doobieVersion = "0.9.0"
val postgresVersion = "42.2.10"
val logbackVersion = "1.2.3"


libraryDependencies ++= Seq(
  "dev.zio"                      %% "zio"                           % zioVersion,
  "dev.zio"                      %% "zio-streams"                   % zioVersion,
  "dev.zio"                      %% "zio-logging"                   % zioLoggingVersion,
  "dev.zio"                      %% "zio-interop-cats"              % zioInteropCats,
  "dev.zio"                      %% "zio-kafka"                     % zioKafkaVersion,
  "dev.zio"                      %% "zio-config"                    % zioConfigVersion,
  "dev.zio"                      %% "zio-config-magnolia"           % zioConfigVersion,
  "io.scalaland"                 %% "chimney"                       % chimneyVersion,

  "io.chrisdavenport"            %% "fuuid"                         % fuuidVersion,

  "io.circe"                     %% "circe-core"                    % circeVersion,
  "io.circe"                     %% "circe-generic"                 % circeVersion,
  "io.circe"                     %% "circe-parser"                  % circeVersion,

  "org.http4s"                   %% "http4s-dsl"                    % http4sVersion,
  "org.http4s"                   %% "http4s-circe"                  % http4sVersion,
  "org.http4s"                   %% "http4s-blaze-server"           % http4sVersion,

  "org.tpolecat"                 %% "doobie-core"                   % doobieVersion,
  "org.tpolecat"                 %% "doobie-postgres"               % doobieVersion,
  "org.postgresql"               %  "postgresql"                    % postgresVersion,

  "ch.qos.logback"               %  "logback-classic"               % logbackVersion,

  "dev.zio"                      %% "zio-test"                      % zioVersion              % "test",
  "dev.zio"                      %% "zio-test-sbt"                  % zioVersion              % "test"
)


testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
