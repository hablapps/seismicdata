create database ign;
CREATE ROLE ign;
alter user ign with password '1234';
ALTER ROLE "ign" WITH LOGIN;
GRANT CONNECT ON DATABASE ign TO ign;
GRANT USAGE ON SCHEMA public TO ign;

CREATE TABLE IF NOT EXISTS stationStat (
    minute timestamp not null,
    statName varchar NOT NULL,
    loc varchar,
    channel varchar not null,
    networkCode varchar not null,
    frequency real not null,
    records integer not null,
    avgLength real,
    lastDelay real,
    avgLatency real,
    gaps integer not null,
    nodata integer not null,
    dataavg float,
    datamax integer,
    datamin integer,
    PRIMARY KEY (minute, statName, loc, channel, networkcode)
);

CREATE OR REPLACE VIEW public.statistic
    AS
     SELECT stationstat.minute,
    concat(stationstat.networkcode, '.', btrim((stationstat.statname)::text), '.', btrim((stationstat.loc)::text), '.', stationstat.channel) AS station,
    stationstat.frequency,
    stationstat.records,
    stationstat.avgLength,
    stationstat.lastdelay,
    stationstat.avgLatency,
    stationstat.gaps,
    stationstat.nodata,
    stationstat.dataavg,
    stationstat.datamax,
    stationstat.datamin
   FROM stationstat
   order by minute desc;

GRANT ALL PRIVILEGES ON DATABASE ign TO ign;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO ign;

revoke usage on schema public from ign2;
revoke all privileges on all tables in schema public from ign2;
drop table stationstat cascade;
drop database ign2;
drop user ign2;


