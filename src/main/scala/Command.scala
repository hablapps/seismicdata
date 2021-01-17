package dev.habla.seismicdata

sealed trait Command

case class InitDB(url: String, user: String, pwd: String) extends Command

case class DropDB(url: String, user: String, pwd: String) extends Command

case class Start() extends Command

