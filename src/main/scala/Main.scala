package dev.habla.seismicdata

import caseapp._
import pureconfig._
import pureconfig.generic.auto._

object Main extends CommandApp[Command] {

	def run(command: Command, rargs: RemainingArgs): Unit =
		ConfigSource.default.load[Config].fold(
			println,
			config => command match {
				case args: InitDB => 
					println(psql.InitDB(args)(config.databaseConf))

				case args: DropDB => 
					println(psql.DropDB(args)(config.databaseConf))

				case Start() => 
					Pipeline.start(config)
			})
}
