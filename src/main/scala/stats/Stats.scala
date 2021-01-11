package dev.habla.seismicdata
package stats

import java.time.LocalDateTime
import java.time.temporal.{ChronoUnit, TemporalUnit}

import protocol.{SeismicRecord, Station}

import scala.collection.mutable

case class Stats(
  stations: mutable.HashMap[Station.Id, Station],
  perStation: mutable.HashMap[Station.Id, StationStat],
  perSeconds: mutable.HashMap[LocalDateTime, (Int, Int)]
){

  def updateWith[K, V](m: mutable.HashMap[K, V])(k: K)(f: Option[V] => V): Unit =
    m.get(k).fold(
      m.update(k, f(None))
    )(
      v => m.update(k, f(Some(v)))
    )

  def add(time: LocalDateTime, record: SeismicRecord): Stats = {
    if (!stations.contains(record.station.id))
      stations.update(record.station.id, record.station)
    updateWith(perStation)(record.station.id){
      case None => StationStat.add(time, record)(StationStat(record.station))
      case Some(stat) => StationStat.add(time, record)(stat)
    }
    updateWith(perSeconds)(time.truncatedTo(ChronoUnit.SECONDS)){
      case None => (1,record.data.size)
      case Some((num, kbs)) => (num+1, kbs+record.data.size)
    }
    this
  }

  def numStations: Int =
    perStation.size

  def avgRecordsPerSec: Option[Int] =
    if (perSeconds.size == 0) None
    else Some(perSeconds.map(_._2._1).sum/perSeconds.size)

  def lastRecordsAndKBSPerSec: Option[(LocalDateTime, (Int, Int))] = {
    val l = perSeconds.toList.sortWith((t1,t2) => t1._1.compareTo(t2._1) > 0)
    if (l.isEmpty) None
    else l.tail.headOption
  }

  def maxRecordsPerStation: Option[Int] =
    if (perStation.isEmpty) None
    else Some(perStation.map(_._2.numRecords).max)

  def minRecordsPerStation: Option[Int] =
    if (perStation.isEmpty) None
    else Some(perStation.map(_._2.numRecords).min)

  def avgRecordsPerStation: Option[Int] =
    if (perStation.isEmpty) None
    else Some(perStation.map(_._2.numRecords).sum/perStation.size)

  def avgLength: Option[Int] = {
    val l = perStation.map(_._2.avgLength).filter(_ < 50000)
    if (l.isEmpty) None
    else Some(l.sum.toInt/l.size)
  }

  def avgLatency: Option[Int] = {
    val l = perStation.map(_._2.avgLatency).filter(_ < 50000)
    if (l.isEmpty) None
    else Some(l.sum.toInt/l.size)
  }

  def avgDelay: Option[Int] =
    if (perStation.isEmpty) None
    else Some(perStation.map(_._2.avgDelay).sum.toInt/perStation.size-3600000)
/*
  def avgDataAvg: Option[Int] =
    if (perStation.isEmpty) None
    else Some(perStation.map(_._2.dataAvg).sum.toInt/perStation.size)

  def avgDataMax: Option[Int] =
    if (perStation.isEmpty) None
    else Some(perStation.map(_._2.avgDataMax).sum.toInt/perStation.size)

  def avgDataMin: Option[Int] =
    if (perStation.isEmpty) None
    else Some(perStation.map(_._2.avgDataMin).sum.toInt/perStation.size)
*/
  def avgGaps: Option[Float] =
    if (perStation.isEmpty) None
    else None // Some(perStation.map(_._2.gaps.size).sum.toFloat/perStation.size)

  def avgOverlaps: Option[Float] =
    if (perStation.isEmpty) None
    else None // Some(perStation.map(_._2.over.size).sum.toFloat/perStation.size)

  def frequencies: Map[Float, Int] =
    perStation.groupBy(entry => stations(entry._2.station.id).frequency).map(t => (t._1, t._2.size))

  def csv: String = {
    val lastInfo = lastRecordsAndKBSPerSec
    List(
      "num stations" -> Some(numStations),
      "frequencies" -> Some(frequencies.mkString(", ")),
      "avg records/sec." -> avgRecordsPerSec,
      "lst records/sec." -> lastInfo.map(_._2._1),
      "lst kbs/sec." -> lastInfo.map(_._2._2/1024),
      "max records per station" -> maxRecordsPerStation,
      "min records per station" -> minRecordsPerStation,
      "avg records per station" -> avgRecordsPerStation,
      "avg gaps" -> avgGaps,
      "avg overlaps" -> avgOverlaps,
      "avg length" -> avgLength,
      "avg latency" -> avgLatency,
      "avg delay" -> avgDelay/*,
      "avg data avg" -> avgDataAvg,
      "avg data max" -> avgDataMax,
      "avg data min" -> avgDataMin*/)
    .map{ case (l,r) =>
      l + ": " + r.fold("-")(_.toString)
    }.mkString("\n")
  }

  def reset: Stats = Stats(stations)
}

object Stats{
  def apply(): Stats = Stats(mutable.HashMap(), mutable.HashMap(), mutable.HashMap())
  def apply(stations: mutable.HashMap[Station.Id, Station]): Stats =
    Stats(stations, mutable.HashMap(), mutable.HashMap())
}
