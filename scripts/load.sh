#!/bin/sh
ROOT_DIR=/home/moulliet/transport/

java -Xms512m -Xmx512m -cp ${ROOT_DIR}transport-0.1-SNAPSHOT-jar-with-dependencies.jar:${ROOT_DIR}config \
    -Dbase.dir=${ROOT_DIR} \
    com.moulliet.transport.crash.odot.OdotDataLoader