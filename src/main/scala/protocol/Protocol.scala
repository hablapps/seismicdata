package dev.habla.seismicdata
package protocol

import utils._

import java.util.TimeZone
import java.time.LocalDateTime
import java.time.temporal.{ChronoField, TemporalField, ChronoUnit, TemporalUnit}

import scala.util.{Success, Try}
import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{BidiFlow, Flow, Keep, Sink, Source, Tcp}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import akka.stream.{Attributes, BidiShape, Inlet, Outlet}
import akka.util.ByteString
import akka.event.Logging
import edu.sc.seis.seisFile.mseed.{DataRecord, SeedRecord}
import scala.concurrent.duration._


case class SeedlinkProtocol() extends GraphStage[BidiShape[String, ByteString, ByteString, ByteString]] {

  override def toString: String = "SeedlinkProtocol"

  val inC: Inlet[String] = Inlet("inC")
  val outC: Outlet[ByteString] = Outlet("outC")
  val inS: Inlet[ByteString] = Inlet("inS")
  val outS: Outlet[ByteString] = Outlet("outS")

  override val shape = BidiShape(inC, outC, inS, outS)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {
      var lastCommand: Option[String] = None
      var transmission: Boolean = false

      setHandler(inC, new InHandler {
        override def onPush(): Unit = {
          val elem = grab(inC)
          // TODO: use proper logging
          // println(s"inC: onPush $elem")
          if (elem.trim == "end") transmission = true
          lastCommand = Some(elem.trim)
          push(outC, ByteString(elem))
        }

        override def onUpstreamFinish(): Unit = ()
      })

      setHandler(outC, new OutHandler {
        override def onPull(): Unit = {
          // println(s"ouC: onPull")
          if (lastCommand.isEmpty) pull(inC)
        }
      })

      setHandler(inS, new InHandler {
        override def onPush(): Unit = {
          val elem = grab(inS)
          // println(s"inS: onPush ${elem.utf8String}")
          if (transmission)
            push(outS, elem)
          else if (elem.utf8String.trim == "OK") {
            pull(inC)
            pull(inS)
          } else
            fail(outS, new Exception("fail command " + lastCommand.get))
        }
      })

      setHandler(outS, new OutHandler {
        override def onPull(): Unit = {
          // println("ouS: onPull")
          pull(inS)
        }
      })
    }
}

object SeedlinkProtocol{

  def apply(config: Config.Seedlink)(
    implicit as: ActorSystem): Source[SeismicRecord, (PressureGauge.State, PressureGauge.State)] =
    Source(config.stations)
      .viaMat(
        BidiFlow.fromFlowsMat(
          commands.log("command").withAttributes(Attributes.logLevels(
            onElement = Logging.InfoLevel, onFinish = Logging.InfoLevel, onFailure = Logging.ErrorLevel)),
          Flow.fromGraph(PressureGauge[ByteString]())
            /*.conflate((bs1, bs2) => { 
              val bs = bs1 ++ bs2
              print(".")
              bs
            })*/.via(accumulateRecords))(Keep.right)
        .atop(BidiFlow.fromGraph(SeedlinkProtocol()))
        .join(Tcp().outgoingConnection(config.host, config.port)))(Keep.right)
      .map(translateRecord)
      //.throttle(1, per=10.second)
      .viaMat(PressureGauge())(Keep.both)
      .divertTo(Flow[Try[SeismicRecord]]
        .log("errorenous record").withAttributes(Attributes.logLevels(
          onElement = Logging.ErrorLevel, onFinish = Logging.InfoLevel, onFailure = Logging.ErrorLevel))
        .to(Sink.ignore), _.isFailure)
      .map{ case Success(r) => r }
      .log("new record").withAttributes(Attributes.logLevels(
        onElement = Logging.DebugLevel, onFinish = Logging.InfoLevel, onFailure = Logging.ErrorLevel))

  def commands = {
    val now = LocalDateTime.now(TimeZone.getTimeZone("UTC").toZoneId)
    val nowp = 
      now.get(ChronoField.YEAR) + "," +
      now.get(ChronoField.MONTH_OF_YEAR) + "," +
      now.get(ChronoField.DAY_OF_MONTH) + "," +
      now.get(ChronoField.HOUR_OF_DAY) + "," +
      now.get(ChronoField.MINUTE_OF_HOUR) + "," +
      now.get(ChronoField.SECOND_OF_MINUTE)
    Flow[(String, String)]
      .flatMapConcat{ case (net, station) =>
        Source(List(
          s"station $station $net\r\n",
          s"data 1 $nowp\r\n"))
      }.concat(Source.single("end\r\n"))
  }

  def accumulateRecords = Flow[ByteString].statefulMapConcat{ () =>
    var records: ByteString = ByteString()
    //var kbs: Int = 0
    //var time: LocalDateTime = LocalDateTime.now.truncatedTo(ChronoUnit.SECONDS)
    bs => {
      /*val now = LocalDateTime.now.truncatedTo(ChronoUnit.SECONDS) 
      if (now == time) kbs += bs.size
      else { 
        println(s"\n----------- $time = ${kbs.toFloat/1024} Kbs -------------")
        time = now
        kbs = bs.size
      }*/
      val (result, tail) = splitAt(520)(records ++ bs)
      records = tail
      result
    }
  }

  def splitAt(n: Int)(bs: ByteString): (List[ByteString], ByteString) = {
    def aux(bs: ByteString, out: List[ByteString]): (List[ByteString], ByteString) =
      if (bs.size < 520) (out, bs)
      else {
        val (head, tail) = bs.splitAt(n)
        aux(tail, head :: out)
      }
    aux(bs, List())
  }

  def translateRecord(bs: ByteString): Try[SeismicRecord] =
    SeismicRecord(
      SeedRecord.read(bs.drop(8).toArray)
        .asInstanceOf[DataRecord]
    )
}
