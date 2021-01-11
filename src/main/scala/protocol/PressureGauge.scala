package dev.habla.seismicdata.protocol 

import java.util.concurrent.atomic.AtomicBoolean

import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.stream.Attributes.Name
import akka.stream.stage.{GraphStageLogic, GraphStageWithMaterializedValue, InHandler, OutHandler}

object PressureGauge {

  trait State {
    def name: String
    def underPressure: Boolean
  }

  def apply[A](): PressureGauge[A] = new PressureGauge[A]()

}

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