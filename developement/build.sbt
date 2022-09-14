name := "developement"

version := "0.1"

scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  dependencies.akkaActors,
  dependencies.akkaStreams,
  dependencies.logback,
  dependencies.scalaLogging,
  dependencies.scalaTest
)


lazy val dependencies = new {
  val akkaVersion = "2.6.0"
  val logbackVersion = "1.2.2"
  val scalaLoggingVersion = "3.9.2"

  val akkaActors = "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion
  val akkaStreams = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  val logback = "ch.qos.logback" % "logback-classic" % logbackVersion
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
  val scalaTest = "org.scalatest" %% "scalatest" % "3.2.13" % "test"
}