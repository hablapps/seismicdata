package dev.habla.seismicdata
package stats

import java.time.LocalDateTime
import java.time.temporal.{ChronoUnit, TemporalUnit}

import protocol.{SeismicRecord, Station}

import scala.collection.mutable

case class StationStat(station: Station,
                       data: List[(Long, LocalDateTime, LocalDateTime)] = List(),
                       last: Option[LocalDateTime] = None,
                       sumLength: Long = 0,
                       sumLatency: Long = 0,
                       sumDelays: Long = 0,
                       lastDelay: Long = 0,
                       sumdata: Option[Long] = None,
                       dataMax: Option[Long] = None,
                       dataMin: Option[Long] = None,
                       numSamples: Int = 0,
                       numRecords: Int = 0){

  def avgLength: Float =
    sumLength/numRecords

  def avgDelay: Float =
    sumDelays/numRecords

  def avgLatency: Float =
    sumLatency/numRecords

  def dataAvg: Option[Float] =
    sumdata.map(_.toFloat/numSamples)

  def gapInfo: (Int, Long) = {
    val result: (Int, Long) = data.sortBy(_._1) match {
    case Nil => (0,0L)
    case h :: tail => tail.foldLeft(((0,0L),h)){
      case (((n,t), (_, _, e1)), p2@(_, b2, e2)) if e1.plus(station.step, ChronoUnit.MILLIS) == b2 => 
        ((n,t), p2)
      case (((n,t), (_, _, e1)), p2@(_, b2, e2)) => 
        ((n+1, t + ChronoUnit.MILLIS.between(e1.plus(station.step, ChronoUnit.MILLIS),b2)), p2)
    }._1}
    /*if (station.id.name.startsWith("CDIE") && station.id.channel == "HHE" && station.id.loc == "1K"){
      println(data)
      println(data.sortBy(_._1))
      println(result)
    }*/
    result
  }


  /*def csv: String =
    List(id, frequency, numRecords, avgLength, avgDelay-3600000, avgLatency, data.size, gaps.size, over.size)
      .mkString(";")*/
}

object StationStat{

  def combine[A](a1: Option[A], a2: Option[A])(f: (A, A) => A): Option[A] =
    (a1, a2) match {
      case (Some(a1), Some(a2)) => Some(f(a1,a2))
      case (Some(a1), None) => Some(a1)
      case (None, Some(a2)) => Some(a2)
      case _ => None
    }

  def add(time: LocalDateTime, record: SeismicRecord)(stats: StationStat): StationStat = {
/*    
    def comp(e: LocalDateTime) =
      e.plus(record.station.step, ChronoUnit.MILLIS).compareTo(record.begin)


    val (data, gaps, overlaps) = stats.data match {
      case Nil =>
        (List(record.begin -> record.end), List(), List())
      case (b, e) :: tail if comp(e) == 0 =>
        ((b, record.end) :: tail, stats.gaps, stats.over)
      case (b, e) :: tail if comp(e) < 0 =>
        (stats.data, stats.gaps, (record.begin -> record.end) :: stats.over)
      case (b, e) :: tail if comp(e) > 0 =>
        ((record.begin, record.end) :: stats.data, (e.plus(record.station.step, ChronoUnit.MILLIS) -> record.begin) :: stats.gaps, stats.over)
    }
*/
    val data = (record.sequence, record.begin, record.end) :: stats.data

    val delay = ChronoUnit.MILLIS.between(record.end, time)

    val length = ChronoUnit.MILLIS.between(record.begin, record.end)

    val latency = stats.last.fold(length)( last => ChronoUnit.MILLIS.between(last, time))

    StationStat(
      station = stats.station,
      data = data,
      last = Some(time),
      sumLength = stats.sumLength + length,
      sumLatency = stats.sumLatency + latency,
      sumDelays = stats.sumDelays + delay,
      lastDelay = delay,
      sumdata = combine[Long](stats.sumdata, if (record.data.size>0) Some(record.data.sum) else None)(_+_),
      dataMax = combine[Long](stats.dataMax, if (record.data.size>0) Some(record.data.max) else None)(_.max(_)),
      dataMin = combine[Long](stats.dataMin, if (record.data.size>0) Some(record.data.min) else None)(_.min(_)),
      numSamples = stats.numSamples + record.data.size,
      numRecords = stats.numRecords + 1)
  }
}

