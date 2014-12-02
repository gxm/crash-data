#!/bin/sh

if [ $# != 1 ]
then
	echo "Usage: bash $0 {deploy|restart|build|loadData}"
	echo "deploy: pushes out static files to the server"
	echo "build: performs a clean build and runs test, then deploys with a restart"
	exit
fi

SCRIPTS="crash-data/scripts/"

build()
{
    # this requires JAVA_HOME to be set for maven
#    mvn package assembly:single
#    STATUS=$?
#    if [ ${STATUS} -eq 0 ]; then
#        echo "Build Successful"
#    else
#        echo "Build Failed, exiting"
#        exit
#    fi
    #rsync -avr target/crash-data-0.1-SNAPSHOT-jar-with-dependencies.jar crash01:crash-data/
    echo "uncomment"
}

deploy()
{
    echo "deploying files"
    rsync -avr config/prod/ crash01:crash-data/config/
    rsync -avr scripts/*.sh crash01:${SCRIPTS}
    ssh crash01 chmod u+x ${SCRIPTS}*.sh
    rsync -avr public/ crash01:crash-data/public/
}

loadData()
{
    echo "loadDataing data"
    ssh crash01 bash ${SCRIPTS}loadData.sh
}

restart()
{
    echo "restarting service"
    ssh crash01 bash ${SCRIPTS}crash-data.sh restart
}

case $1 in
	deploy)
		deploy
		;;
	build)
		build
		deploy
		restart
		;;
	loadData)
		build
		deploy
		loadData
		restart
		;;
	*)
		echo "Usage: bash $0 {deploy|build|loadData}" >&2
esac