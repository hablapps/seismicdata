package dev.habla.seismicdata

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

object Stats extends App {
  implicit val system: ActorSystem = ActorSystem("TCP_Server_Actor_System")
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec = system.dispatcher
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    // "jdbc:postgresql://pct-empresas-122.uc3m.es:5432/ign",
    "jdbc:postgresql://localhost:5432/ign",
    "ign",
    "1234",
    Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
  )

  val seedlinkHost = "127.0.0.1"
  val seedlinkPort = 18000

  val ((before, after), f) =
    SeedlinkProtocol(remoteStationNames/*.take(10)*/, seedlinkHost, seedlinkPort)
      .map(Some.apply)//.take(20)
      .merge(Source.tick(10.second, 1.minute, None).log("tick")/*.take(20)*/)
      .via(updateStats)
      .toMat(persist)(Keep.both)
      .run

  type Samples = AtomicReference[List[Boolean]]

  def newSamples: Samples = new AtomicReference(List.empty[Boolean])

  def addSample(v: Boolean, s: Samples): Unit = s.set(v +: s.get().take(500)) // keep 40 samples.

  def average(s: Samples): Double = {
    val list = s.get()
    (100.0 * list.count(identity)) / list.size
  }
  val beforeSamples = newSamples
  val afterSamples = newSamples

  system.scheduler.schedule(10.millis, 10.millis) {
      addSample(before.underPressure, beforeSamples)
    addSample(after.underPressure, afterSamples)
  }
  system.scheduler.schedule(2.second, 2.second) {
    //println(f"Backpressure before ${average(beforeSamples)}%3.0f %%, after: ${average(afterSamples)}%3.0f %%")
  }

  /*f.onComplete{ r =>
    println(r)
    system.terminate().onComplete(end => println(s"Closing the Actor System: $end"))
  }*/

  val UTCTimeZoneId = java.util.TimeZone.getTimeZone("UTC").toZoneId

  def updateStats: Flow[Option[SeismicRecord], (LocalDateTime, List[StationStat]), akka.NotUsed] =
    Flow[Option[SeismicRecord]].statefulMapConcat{ () =>
      var stats = dev.habla.seismicdata.stats.Stats()

      maybeRecord => maybeRecord match {
        case None =>
          val stationStats = stats.perStation.values.toList
          stats = stats.reset
          List((now(UTCTimeZoneId).truncatedTo(ChronoUnit.MINUTES), stationStats))
        case Some(r) =>
          stats.add(now(UTCTimeZoneId), r)
          List()
      }
    }


  type StationStatInfo = (LocalDateTime,
    String, String, String, String, Float, Int, Float, Long, Float, Int, Long,
    Option[Float], Option[Long], Option[Long])

  def persist: Sink[(LocalDateTime, List[StationStat]), akka.NotUsed] =
    Flow[(LocalDateTime, List[StationStat])].mapAsync(1){
      case (minute, stats) =>
        //println(stats.find(_.station.id == Station.Id("CDIE","HN3","0K","ES"))+"\n\n")
        //println(stats.find(_.station.id.name.startsWith("CDIE"))+"\n\n")
        Update[StationStatInfo](insertStatsSQL)
          .updateMany(stats.map(stationInfo(minute)))
          .transact(xa).attempt.unsafeToFuture
    }.divertTo(Flow[Either[Throwable,Int]]
        .log("persist error").withAttributes(Attributes.logLevels(
          onElement = Logging.ErrorLevel, onFinish = Logging.InfoLevel, onFailure = Logging.ErrorLevel))
        .to(Sink.ignore), _.isLeft)
    .to(Sink.ignore)

  val insertStatsSQL =
    """insert into stationStat
             values (?,
                     ?,
                     ?,
                     ?,
                     ?,
                     ?,
                     ?,
                     ?,
                     ?,
                     ?,
                     ?,
                     ?,
                     ?,
                     ?,
                     ?)
        """

  def stationInfo(minute: LocalDateTime)(stat: StationStat): StationStatInfo = {
    val  (gaps, noData) = stat.gapInfo
    (minute,
     stat.station.id.name,
     stat.station.id.loc,
     stat.station.id.channel,
     stat.station.id.network,
     stat.station.frequency,
     stat.numRecords,
     stat.avgLength,
     stat.lastDelay,
     stat.avgLatency,
     gaps,
     noData,
     stat.dataAvg,
     stat.dataMax,
     stat.dataMin)
  }

}
