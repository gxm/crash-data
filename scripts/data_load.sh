#!/bin/sh
ROOT_DIR=/home/crash/crash-data

OPTIONS="-Xms512m -Xmx512m -cp ${ROOT_DIR}/crash-data-0.1-SNAPSHOT-jar-with-dependencies.jar:${ROOT_DIR}/config
        -Dconfig.properties=${ROOT_DIR}/config/crash-data.properties"

java ${OPTIONS} com.moulliet.metro.load.LoadShapefile
