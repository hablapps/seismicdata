#!/bin/sh

sbt assembly
version=`grep 'version :=' build.sbt | cut -d "\"" -f 2`

DEPLOY_DIR=/tmp/seismicdata

rm -rf $DEPLOY_DIR
mkdir $DEPLOY_DIR
mkdir $DEPLOY_DIR/conf
mkdir $DEPLOY_DIR/lib

cp src/main/resources/application.conf $DEPLOY_DIR/conf
cp src/main/resources/logback.xml $DEPLOY_DIR/conf
cp target/scala-2.13/seismicstats-0.8.jar $DEPLOY_DIR/lib
cp utils/initdb.sh $DEPLOY_DIR/
cp utils/dropdb.sh $DEPLOY_DIR/
cp utils/start.sh $DEPLOY_DIR/
cp utils/portfw.sh $DEPLOY_DIR
cp INSTALL $DEPLOY_DIR

cd $DEPLOY_DIR/..
tar -czf seismicdata-$version.tgz seismicdata