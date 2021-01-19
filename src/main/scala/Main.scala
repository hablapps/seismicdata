package dev.habla.seismicdata

import caseapp._
import pureconfig._
import pureconfig.generic.auto._

object Main extends CommandApp[Command] {

	def run(command: Command, rargs: RemainingArgs): Unit =
		ConfigSource.default.at("seismic-data").load[Config].fold(
			println,
			config => command match {
				case args: InitDB => 
					psql.InitDB(args)(config.databaseConf).fold(System.err.println, println)

				case args: DropDB => 
					psql.DropDB(args)(config.databaseConf).fold(System.err.println, println)

				case Start() => 
					Pipeline.start(config)
			})
}
