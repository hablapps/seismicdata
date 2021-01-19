package dev.habla.seismicdata

sealed trait Command

case class InitDB(user: String, pwd: String) extends Command

case class DropDB(user: String, pwd: String) extends Command

case class Start() extends Command

