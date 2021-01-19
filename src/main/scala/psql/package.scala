package dev.habla.seismicdata

package object psql{

	import doobie._
	import doobie.implicits._

	import cats._, cats.syntax.all._
	import cats.effect.IO, cats.effect.Blocker

	
	def transactor(url: String, user: String, pwd: String): Either[Throwable, Transactor.Aux[IO, Unit]] = {
		implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
	    val transactor = Transactor.fromDriverManager[IO](
	      "org.postgresql.Driver",
	      url,
	      user,
	      pwd,
	      Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
	    )
	    ping(transactor).map(_ => transactor)
	}

	def transactor(config: Config.Database): Either[Throwable, Transactor.Aux[IO, Unit]] = 
		transactor(config.url+config.dbname, config.user, config.password)

	def ping(xa: Transactor.Aux[IO, Unit]): Either[Throwable, Int] = 
		xa.rawTrans.apply(sql"/* ping */ SELECT 1".query[Int].unique).attempt.unsafeRunSync

}