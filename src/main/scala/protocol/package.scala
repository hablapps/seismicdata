package dev.habla.seismicdata

import scala.concurrent._
import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.stream.scaladsl._, Tcp._
import akka.util.ByteString

package object protocol{
	
	def tcpFlow(host: String, port: Int)(implicit system: ActorSystem): Future[Flow[ByteString, ByteString, Future[OutgoingConnection]]] = {
		val connection: Flow[ByteString, ByteString, Future[OutgoingConnection]] = 
			Tcp().outgoingConnection(host, port)
		Source(List("cat\n").map(ByteString.apply)).via(connection).runForeach{ bs => println(bs.utf8String) }.map(_ => connection)(system.dispatcher)
	}
}