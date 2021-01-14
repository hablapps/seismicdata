package dev.habla.seismicdata

import protocol._, psql._, stats._

import akka.actor.ActorSystem
import akka.stream.scaladsl.{RunnableGraph, Source, Keep}
import utils.PressureGauge.State
import scala.concurrent.duration._

object Pipeline{
	
	def apply(config: Config)(implicit as: ActorSystem): RunnableGraph[(State, State)] = 
	    SeedlinkProtocol(config.seedlinkConf)
			.map(Some.apply)
			.merge(Source.tick(10.second, 1.minute, None).log("tick"))
			.via(UpdateStats.apply)
			.toMat(psql.Persist(config.databaseConf))(Keep.left)
			
}