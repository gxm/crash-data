#!/bin/sh

if [ $# != 2 ]
then
	echo "Usage: sh $0 {restart|deploy|build} {server}"
	echo "deploy: pushes out static files to the server"
	echo "build: performs a clean build and runs test, then deploys with a restart"
	exit
fi

SCRIPTS="crash-data/scripts"
SERVER=$2

build() {
    # this requires JAVA_HOME & DYLD_LIBRARY_PATH to be set for maven
    export DYLD_LIBRARY_PATH="$DYLD_LIBRARY_PATH:../FileGDB_API/lib"
    mvn package assembly:single
    STATUS=$?
    if [ ${STATUS} -eq 0 ]; then
        echo "Build Successful"
    else
        echo "Build Failed, exiting"
        exit
    fi
    rsync -avr target/crash-data-0.1-SNAPSHOT-jar-with-dependencies.jar ${SERVER}:crash-data/
}

deploy() {
    echo "deploying files"
    rsync -avr config/prod/ ${SERVER}:crash-data/config/
    rsync -avr scripts/*.sh ${SERVER}:${SCRIPTS}/
    rsync -avr data/* ${SERVER}:data/
    ssh ${SERVER} chmod +x ${SCRIPTS}/*.sh
    rsync -avr public/ ${SERVER}:crash-data/public/
}

restart() {
    echo "restarting service"
    ssh ${SERVER} sh ${SCRIPTS}/crash-data.sh restart
}

case $1 in
    restart)
		restart
		;;
	deploy)
		deploy
		restart
		;;
	build)
		build
		deploy
		restart
		;;
	*)
		echo "Usage: bash $0 {restart|deploy|build} {server}" >&2
esac