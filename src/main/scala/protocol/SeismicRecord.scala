package dev.habla.seismicdata.protocol

import java.time.LocalDateTime

case class SeismicRecord(
	sequence: Long,
    station: Station,
    begin: LocalDateTime,
    end: LocalDateTime,
    data: Array[Int])
