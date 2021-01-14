package dev.habla.seismicdata.protocol

import java.time.LocalDateTime

case class SeismicRecord(
	sequence: Long,
    station: Station,
    begin: LocalDateTime,
    end: LocalDateTime,
    data: Array[Int])

object SeismicRecord{

	import scala.util.Try
	import edu.sc.seis.seisFile.mseed.{Btime, ControlRecord, DataRecord, SeedRecord}

	def apply(byteArray: Array[Byte]): Option[SeismicRecord] =
	    SeedRecord.read(byteArray) match {
	      case record: DataRecord => apply(record).toOption
	      case _ => None
	    }

	def apply(record: DataRecord): Try[SeismicRecord] = Try{
		val stationId = Station.Id(
		    record.getHeader.getStationIdentifier,
		    record.getHeader.getChannelIdentifier,
		    record.getHeader.getLocationIdentifier,
		    record.getHeader.getNetworkCode)
		val frequency = record.getHeader.getSampleRate
		val begin = bTimeToLocalDate(record.getBtimeRange.getBegin)
		val end = bTimeToLocalDate(record.getBtimeRange.getEnd)
		val sequence = record.getHeader.getSequenceNum
		val data = record.decompress().getAsInt
		val rec = SeismicRecord(sequence, Station(stationId, frequency), begin, end, data)
		/*if (stationId.name.startsWith("CDIE")&& stationId.channel == "HHE" && stationId.loc == "1K")
		  println(record.getHeader.getSequenceNum + ": " + rec)*/
		rec
	}

	import java.time.ZoneId
	
	private def bTimeToLocalDate(btime: Btime): LocalDateTime = {
		val dateCalendar = btime.convertToCalendar()
		LocalDateTime.ofInstant(dateCalendar.toInstant, ZoneId.of("UTC"))
	}

}