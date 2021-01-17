name := "seismicdata"
scalaVersion := "2.13.4"
version := "0.1"

libraryDependencies ++= Seq(
  "com.github.alexarchambault" %% "case-app" % "2.0.1",
  "com.github.scopt" %% "scopt" % "4.0.0",
  "com.typesafe.akka" %% "akka-stream" % "2.6.8",
  "com.typesafe.akka" %% "akka-http" % "10.2.0",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scalatest" %% "scalatest" % "3.2.2" % "test",
  "org.postgresql" % "postgresql" % "42.2.10",
  "org.tpolecat" %% "doobie-postgres" % "0.8.8",
  "org.tpolecat" %% "doobie-hikari" % "0.8.8",
  "edu.sc.seis" % "seisFile" % "1.8.4" exclude("org.slf4j", "slf4j-log4j12"),
  "com.github.pureconfig" %% "pureconfig" % "0.14.0")

mainClass in (Compile, run) := Some("dev.habla.seismicdata.Main")
mainClass in assembly := Some("dev.habla.seismicdata.Main")
assemblyJarName in assembly := "seismicstats.jar"


