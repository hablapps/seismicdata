package dev.habla.seismicdata

import protocol._, psql._, stats._

import akka.actor.ActorSystem
import akka.stream.scaladsl.RunnableGraph
import akka.stream.scaladsl.{Flow, Sink, Source, Tcp, Keep}
import utils.PressureGauge.State
import scala.concurrent.duration._


object Pipeline{
	
	def apply(config: Config)(implicit as: ActorSystem): RunnableGraph[(State, State)] = 
	    SeedlinkProtocol(remoteStationNames/*.take(10)*/, config.seedlinkConf)
	      .map(Some.apply)//.take(20)
	      .merge(Source.tick(10.second, 1.minute, None).log("tick")/*.take(20)*/)
	      .via(UpdateStats.apply)
	      .toMat(psql.Persist(config.databaseConf))(Keep.left)

}