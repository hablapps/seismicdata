package dev.habla.seismicdata
package psql

import java.time.LocalDateTime

import scala.concurrent.Future

import akka.stream.scaladsl._
import akka.stream.Attributes
import akka.event.Logging

import cats._, cats.syntax.all._, cats.data._, cats.instances.all._
import cats.effect.IO, cats.effect.Blocker
import _root_.doobie._, _root_.doobie.implicits._, javasql._, javatime._


import protocol._, stats._


object Persist{

  def apply(conf: Config.Database): Sink[(LocalDateTime, List[StationStat]), Future[akka.Done]] = {

    implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
    implicit val xa = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",
      conf.url,
      conf.user,
      conf.password,
      Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
    )

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
    .toMat(Sink.ignore)(Keep.right)
  }

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

  type StationStatInfo = (LocalDateTime,
    String, String, String, String, Float, Int, Float, Long, Float, Int, Long,
    Option[Float], Option[Long], Option[Long])

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