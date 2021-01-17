package dev.habla
package seismicdata
package psql


import doobie._
import doobie.implicits._
import cats._, cats.syntax.all._


object DropDB{
	
	def apply(cmd: seismicdata.DropDB)(conf: Config.Database): Either[Throwable, Int] = 
		transactor(cmd.url, cmd.user, cmd.pwd).rawTrans.apply(
			(fr0"DROP DATABASE " ++ Fragment.const(conf.dbname)).update.run *>
		 	(fr0"DROP ROLE " ++ Fragment.const(conf.user)).update.run
		).attempt.unsafeRunSync
    
}