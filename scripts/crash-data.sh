#!/bin/sh
if [ $# != 1 ]
then
	echo "Usage: bash $0 {start|stop|restart}"
	exit
fi
# Make sure root can't run our script
if [[ $EUID -eq 0 ]]; then
   echo "error - this script should not run as root" 1>&2
   exit 1
fi

ROOT_DIR=/home/crash/crash-data/
PID_FILE=${ROOT_DIR}crash-data.pid
STD_OUT_LOG=${ROOT_DIR}logs/crash-data.$(/bin/date '+%Y-%m-%d-%H-%M-%S')

startService ()
{
	echo  "Starting crash-data service ... "

	if [ -e "${PID_FILE}" ]
	then
		echo "pid file exists ${PID_FILE}, exiting"
		exit 1
	fi

	OPTIONS="-Xms384m -Xmx384m -cp ${ROOT_DIR}crash-data-0.1-SNAPSHOT-jar-with-dependencies.jar:${ROOT_DIR}config
        -Dconfig.properties=config/crash-data.properties
        -Dbase.dir=${ROOT_DIR}
        -Dcom.sun.management.jmxremote
        -Dcom.sun.management.jmxremote.port=9010
        -Dcom.sun.management.jmxremote.local.only=false
        -Dcom.sun.management.jmxremote.authenticate=false
        -Dcom.sun.management.jmxremote.ssl=false
	"

    # only use this version for debugging
	#java ${OPTIONS} com.moulliet.metro.CrashServiceMain
	nohup java ${OPTIONS} com.moulliet.metro.CrashServiceMain  1>> ${STD_OUT_LOG} 2>&1 &

	echo -n $! > "${PID_FILE}"
	echo STARTED
}
stopService ()
{
	echo "Stopping crash-data service ... "
	if [ ! -e "${PID_FILE}" ]
	then
		echo "error: could not find file ${PID_FILE}"
	else
		PID=$(cat "${PID_FILE}")
		kill ${PID}
		echo "WAITING for PID ${PID}"
		while ps -p ${PID} > /dev/null; do sleep 1; echo slept; done
		rm "${PID_FILE}"
		echo STOPPED
	fi
}

case $1 in
	start)
		startService
		;;
	stop)
		stopService
		;;
	restart)
		stopService
		startService
		;;
	*)
		echo "Usage: bash $0 {start|stop|restart}" >&2
esac


