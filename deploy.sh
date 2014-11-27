#!/bin/sh
if [ $# != 1 ]
then
	echo "Usage: bash $0 {deploy|restart|build|load}"
	exit
fi

SCRIPTS="transport/scripts/"

build()
{
    mvn package assembly:single
    STATUS=$?
    if [ ${STATUS} -eq 0 ]; then
        echo "Build Successful"
    else
        echo "Build Failed, exiting"
        exit
    fi
    rsync -avr target/transport-0.1-SNAPSHOT-jar-with-dependencies.jar transports:transport
}

deploy()
{
    deployService
    deployWeb
}

deployService()
{
    echo "deploying files"
    rsync -avr raw/* transports:transport/raw
    rsync -avr config/prod/ transports:transport/config/
    rsync -avr scripts/*.sh transports:${SCRIPTS}
    ssh transports chmod u+x ${SCRIPTS}*.sh
}

deployWeb()
{
    echo "deploying web files"
    rsync -avr web/ transport:transport.moulliet.com/
}

load()
{
    echo "loading data"
    ssh transports bash ${SCRIPTS}load.sh
}

restart()
{
    echo "restarting service"
    ssh transports bash ${SCRIPTS}transport.sh restart
    #todo this requires NOPWD set for user
    #ssh transports sudo service apache2 restart
}


case $1 in
	deploy)
		deploy
		;;
	restart)
		deploy
		restart
		;;
	build)
		build
		deploy
		restart
		;;
	load)
		build
		deployService
		load
		deployWeb
		restart
		;;
	*)
		echo "Usage: bash $0 {deploy|restart|build|load}" >&2
esac