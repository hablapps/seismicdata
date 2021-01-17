package dev.habla.seismicdata.protocol

case class Station(
    id: Station.Id,
    frequency: Float){
  def step: Int = (1000.0/frequency).toInt
}

object Station{
    case class Id(
        name: String,
        channel: String,
        loc: String,
        network: String,
    )
}
