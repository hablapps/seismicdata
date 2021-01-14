package dev.habla.seismicdata

import pureconfig._
import pureconfig.generic.auto._

import java.time.LocalDateTime.now
import java.time.temporal.ChronoUnit
import java.time.LocalDateTime

import scala.collection.mutable
import scala.concurrent.duration._
import akka.stream.ActorMaterializer
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source, Tcp, Keep}
import akka.stream.Attributes
import akka.event.Logging

import protocol._, stats._
import java.util.concurrent.atomic.AtomicReference
import scala.util.{Failure, Success}

import scala.concurrent.Future
import cats._, cats.syntax.all._, cats.data._, cats.instances.all._
import cats.effect.IO, cats.effect.Blocker
import fs2.Stream
import _root_.doobie._, _root_.doobie.implicits._, javasql._, javatime._

object Main extends App {

  val Right(config) = ConfigSource.default.load[Config]

  implicit val system: ActorSystem = ActorSystem("TCP_Server_Actor_System")

  val (before, after) = Pipeline(config).run
//  utils.PressureGauge.scheduleSamples(before, after)
}
