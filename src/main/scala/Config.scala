package dev.habla.seismicdata

case class Config(
	databaseConf: Config.Database, 
	seedlinkConf: Config.Seedlink)

object Config{
	
	case class Database(
		url: String, 
		user: String, 
		password: String,
		dbname: String)

	case class Seedlink(
		host: String, 
		port: Int, 
		stations: List[(String, String)])
}