package dev.habla
package seismicdata
package psql


import doobie._
import doobie.implicits._
import cats._, cats.syntax.all._


object DropDB{
	
	def apply(cmd: seismicdata.DropDB)(conf: Config.Database): Either[Throwable, Unit] = for {
		xa <- transactor(conf.url, cmd.user, cmd.pwd)
		_ <- xa.rawTrans.apply(
			(fr0"DROP DATABASE " ++ Fragment.const(conf.dbname)).update.run *>
		 	(fr0"DROP ROLE " ++ Fragment.const(conf.user)).update.run
		).attempt.unsafeRunSync
	} yield ()
    
}