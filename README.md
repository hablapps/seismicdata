
1. DESCRIPTION

This application retrieves Mini-SEED records from a number of stations
available through a seedlink server, and stores periodically (each minute)
several data-quality statistics on a postgresql database.

2. REQUIREMENTS

The application has the following requirements:

* Java, version 1.8 or higher
* Postgresql, version 12.5 or higher
* Tcp access to a seedlink server

3. CONTENT

This package contains the following files:

* lib/seismicdata-<VERSION>.jar - self-contained application jar
* conf/application.conf - seedlink and postgresql server properties
* conf/logback.conf - logging configuration
* initdb.sh - script to set up the database
* dropdb.sh - script to drop the database down
* start.sh - script to start the data quality pipeline
* portfw.sh - port forwarding utility

4. DATABASE SETUP/DROP

The script `initdb.sh` creates a new postgresql user and the application
database where statistics will be stored. The database is configured with
one table, `stationStat`, where the main statistics are stored, and the
view `statistic`, which is, basically, an time-ordered view of that table.

The script reads the following configuration data from `application.conf`:

* seismic-data.database-conf.url - postgresql server to connect to
* seismic-data.database-conf.user - new database user to be created
* seismic-data.database-conf.password - password for the new user
* seismic-data.database-conf.dbname - name of the database to be created

In order to run the script, a postgresql user with priviledge to create
new users and databases must be provided, together with its password:

> initdb.sh -u <username> -p <pwd>

In addition, the script `dropdb.sh` performs a complete clean up of the
database configuration, dropping the user specified in `application.conf`
as well as the database (along with all the stored data!). Its execution
requires the same command line arguments as in the `initdb.sh` script:

> dropdb.sh -u <username> -p <pwd>

5. SEEDLINK CONFIGURATION

The following properties in `application.conf` must be set up in order
to access the seedlink server:

* seismic-data.seedlink-conf.host - host name where the seedlink
server runs (or is accessible from)
* seismic-data.seedlink-conf.port - port of the seedlink server
* seismic-data.seedlink-conf.stations - network code (CODE) and names
(NAME) of the desired stations

For each specified station [CODE, NAME], the following seedlink commands
will be issued in the handshaking phase:

	station NAME CODE
	data 1 <current-time>

i.e. the application subscribes to real-time records from the streams
of all locations and channels of the specified stations.

Note: the script portfw.sh can be used to make available through port 
`localhost:18000` a seedlink server running elsewhere. This is useful 
when the seedlink server is not directly accessible from the local host, 
but through a third party machine where we can log in. 

6. START

Once the database is setup and the seedlink properties configured,
the validation pipeline can be started with the following script:

> start.sh

The application will then attempt to connect to the database
and seedlink servers using the credentials specified previously in
`conf/application.conf`. If all goes well, it should run forever, updating
the database table with timely statistics for the stations specified,
and collecting logging information in the file specified through the
corresponding property in `conf/logback.xml`.


