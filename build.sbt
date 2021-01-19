name := "seismicdata"
scalaVersion := "2.13.4"
version := "0.1"

val akkaVersion = "2.6.8"

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.1" cross CrossVersion.full)

libraryDependencies ++= Seq(
  "com.github.alexarchambault" %% "case-app" % "2.0.1",
  "com.github.pureconfig" %% "pureconfig" % "0.14.0",
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scalatest" %% "scalatest" % "3.2.2" % "test",
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "org.postgresql" % "postgresql" % "42.2.10",
  "org.tpolecat" %% "doobie-postgres" % "0.8.8",
  "org.tpolecat" %% "doobie-hikari" % "0.8.8",
  "edu.sc.seis" % "seisFile" % "1.8.4" exclude("org.slf4j", "slf4j-log4j12"))

mainClass in (Compile, run) := Some("dev.habla.seismicdata.Main")
mainClass in assembly := Some("dev.habla.seismicdata.Main")
assemblyJarName in assembly := "seismicstats.jar"


