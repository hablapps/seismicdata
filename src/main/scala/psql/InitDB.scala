package dev.habla
package seismicdata
package psql

import doobie._
import doobie.implicits._

import cats._, cats.syntax.all._ // , cats.implicits._
import cats.effect.IO, cats.effect.Blocker

object InitDB{
	
	def apply(cmd: seismicdata.InitDB)(conf: Config.Database): Either[Throwable, Unit] = for {
		xa <- transactor(conf.url, cmd.user, cmd.pwd)
		_ <- xa.rawTrans.apply(
			createDb(conf.dbname) *>
			createRole(conf.dbname, conf.user, conf.password)).attempt.unsafeRunSync
		xa2 <- transactor(conf.url+conf.dbname, cmd.user, cmd.pwd)
		_ <- xa2.rawTrans.apply(
			createTableAndView *> 
			givePermissions(conf.user)).attempt.unsafeRunSync
	} yield ()

	
	def createDb(name: String): ConnectionIO[Int] = 
		(fr0"CREATE DATABASE " ++ Fragment.const(name)).update.run

	def createRole(dbName: String, name: String, pwd: String): ConnectionIO[Int] = 
		(fr0"CREATE ROLE " ++ Fragment.const(name)).update.run *>
    	(fr0"GRANT ALL PRIVILEGES ON DATABASE " ++ Fragment.const(dbName) ++ fr0" TO " ++ Fragment.const(name)).update.run *>
		(fr0"GRANT CONNECT ON DATABASE " ++ Fragment.const(dbName) ++ fr0" TO " ++ Fragment.const(name) ++ fr0"; ").update.run *>
		(fr0"ALTER USER " ++ Fragment.const(name) ++ fr0" with password '" ++ Fragment.const0(pwd) ++ fr0"'").update.run *>
		(fr0"ALTER ROLE " ++ Fragment.const(name) ++ fr0" WITH LOGIN").update.run
			
	val createTableAndView = 
		sql"""|CREATE TABLE IF NOT EXISTS stationStat (
	    	  |    minute timestamp not null,
	    	  |    statName varchar NOT NULL,
	    	  |    loc varchar,
	    	  |    channel varchar not null,
	    	  |    networkCode varchar not null,
	    	  |    frequency real not null,
	    	  |    records integer not null,
	    	  |    avgLength real,
	    	  |    lastDelay real,
	    	  |    avgLatency real,
	    	  |    gaps integer not null,
	    	  |    nodata integer not null,
	    	  |    dataavg float,
	    	  |    datamax integer,
	    	  |    datamin integer,
	    	  |    PRIMARY KEY (minute, statName, loc, channel, networkcode)
	    	  |);
              |
              |CREATE OR REPLACE VIEW public.statistic AS
              |SELECT stationstat.minute,
              |    concat(stationstat.networkcode, '.', btrim((stationstat.statname)::text), '.', btrim((stationstat.loc)::text), '.', stationstat.channel) AS station,
              |    stationstat.frequency,
              |    stationstat.records,
              |    stationstat.avgLength,
              |    stationstat.lastdelay,
              |    stationstat.avgLatency,
              |    stationstat.gaps,
              |    stationstat.nodata,
              |    stationstat.dataavg,
              |    stationstat.datamax,
              |    stationstat.datamin
              |   FROM stationstat
              |   order by minute desc;
              |""".stripMargin.update.run


    def givePermissions(userName: String): ConnectionIO[Int] = 
		 (fr"GRANT USAGE ON SCHEMA public TO " ++ Fragment.const(userName) ++ fr"; " ++
    	 fr"GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO " ++ Fragment.const(userName) ++ fr";").update.run

}