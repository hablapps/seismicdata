package dev.habla.seismicdata

package object psql{

	import doobie._
	import doobie.implicits._

	import cats._, cats.syntax.all._
	import cats.effect.IO, cats.effect.Blocker

	
	def transactor(url: String, user: String, pwd: String): Transactor.Aux[IO, Unit] = {
		implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
	    Transactor.fromDriverManager[IO](
	      "org.postgresql.Driver",
	      url,
	      user,
	      pwd,
	      Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
	    )
	}

	def transactor(config: Config.Database): Transactor.Aux[IO, Unit] = 
		transactor(config.url+config.dbname, config.user, config.password)


}