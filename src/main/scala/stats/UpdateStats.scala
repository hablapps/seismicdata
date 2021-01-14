package dev.habla.seismicdata
package stats

import protocol._

import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.temporal.ChronoUnit

import akka.stream.scaladsl.{Flow, Sink, Source, Tcp, Keep}

object UpdateStats{

  val UTCTimeZoneId = java.util.TimeZone.getTimeZone("UTC").toZoneId

  def apply: Flow[Option[SeismicRecord], (LocalDateTime, List[StationStat]), akka.NotUsed] = 
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
  

}