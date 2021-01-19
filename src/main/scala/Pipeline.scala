package dev.habla.seismicdata

import akka.actor.ActorSystem
import akka.stream.scaladsl.{RunnableGraph, Source, Keep}

import scala.concurrent.duration._
import scala.concurrent.Future
import doobie.Transactor, doobie._, doobie.implicits._
import cats.effect.IO

import utils.PressureGauge.State
import protocol._, psql._, stats._


import java.util.TimeZone
import java.time.LocalDateTime
import java.time.temporal.{ChronoField, TemporalField, ChronoUnit, TemporalUnit}

object Pipeline{

	def start(config: Config): Unit = 
		psql.transactor(config.databaseConf).fold(
			println, 
			implicit transactor => {
				implicit val system: ActorSystem = ActorSystem("TCP_Server_Actor_System")

				val ((before, after), done) = Pipeline(config).run
				//  utils.PressureGauge.scheduleSamples(before, after)

				done.onComplete{ r => 
					println(r)
					system.terminate
				}(system.dispatcher)
			}
		)
					
	
	def apply(config: Config)(implicit as: ActorSystem, xa: Transactor.Aux[IO, Unit]): RunnableGraph[((State, State), Future[akka.Done])] = 
	    SeedlinkProtocol(config.seedlinkConf)
			.map(Some.apply)
			.merge(Source.tick(10.second, 1.minute, None).log("tick"))
			.via(UpdateStats.apply)
			.toMat(psql.Persist.apply)(Keep.both)		
}