package dev.habla.seismicdata

package utils 

import java.util.concurrent.atomic.AtomicBoolean

import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.stream.Attributes.Name
import akka.stream.stage.{GraphStageLogic, GraphStageWithMaterializedValue, InHandler, OutHandler}

class PressureGauge[A] extends GraphStageWithMaterializedValue[FlowShape[A, A], PressureGauge.State] {

  val in  = Inlet[A]("PressureGauge.in")
  val out = Outlet[A]("PressureGauge.out")

  override val shape = FlowShape.of(in, out)

  override def createLogicAndMaterializedValue(attr: Attributes): (GraphStageLogic, PressureGauge.State) = {
    val backpressure = new AtomicBoolean(true)

    val logic = new GraphStageLogic(shape) {
      setHandler(
        in,
        new InHandler {
          override def onPush(): Unit = {
            backpressure.set(true)
            push(out, grab(in))
          }
        }
      )
      setHandler(
        out,
        new OutHandler {
          override def onPull(): Unit = {
            backpressure.set(false)
            pull(in)
          }
        }
      )
    }

    val state = new PressureGauge.State {
      override def underPressure = backpressure.get()
      override val name          = attr.get[Name](Name("pressureGauge")).n
    }
    
    (logic, state)
  }
}

object PressureGauge {

  trait State {
    def name: String
    def underPressure: Boolean
  }

  def apply[A](): PressureGauge[A] = new PressureGauge[A]()

  import java.util.concurrent.atomic.AtomicReference

  type Samples = AtomicReference[List[Boolean]]

  def newSamples: Samples = new AtomicReference(List.empty[Boolean])

  def addSample(v: Boolean, s: Samples): Unit = s.set(v +: s.get().take(500)) // keep 40 samples.

  def average(s: Samples): Double = {
    val list = s.get()
    (100.0 * list.count(identity)) / list.size
  }

  import akka.actor.ActorSystem
  import scala.concurrent.duration._


  def scheduleSamples(before: State, after: State)(implicit system: ActorSystem) = {
    implicit val ec = system.dispatcher
    val beforeSamples = newSamples
    val afterSamples = newSamples
    
    system.scheduler.schedule(10.millis, 10.millis) {
      addSample(before.underPressure, beforeSamples)
      addSample(after.underPressure, afterSamples)
    }
    system.scheduler.schedule(2.second, 2.second) {
      println(f"Backpressure before ${average(beforeSamples)}%3.0f %%, after: ${average(afterSamples)}%3.0f %%")
    }
  }


}

