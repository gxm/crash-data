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

USR_DIR=/home/crash
ROOT_DIR=${USR_DIR}/crash-data
PID_FILE=${ROOT_DIR}/crash-data.pid
STD_OUT_LOG=${USR_DIR}/logs/crash-data.$(/bin/date '+%Y-%m-%d-%H-%M-%S')

startService ()
{
	echo  "Starting crash-data service ... "

	if [ -e "${PID_FILE}" ]
	then
		echo "pid file exists ${PID_FILE}, exiting"
		exit 1
	fi

	LD_LIBRARY_PATH=${USR_DIR}/FileGDB_API/lib
	export LD_LIBRARY_PATH
	echo "LD_LIB_PATH $LD_LIBRARY_PATH"

	# jersey-multipart-1.19.jar only exists here to get around and issue with using SNAPSHOT-jar-with-dependencies
	# http://stackoverflow.com/questions/25470505/missing-dependency-for-formdataparam-with-jersey-multipart-1-18-1-solved
	OPTIONS="-Xms1g -Xmx2g -cp ${ROOT_DIR}/jersey-multipart-1.19.jar:${ROOT_DIR}/crash-data-0.1-SNAPSHOT-jar-with-dependencies.jar:${ROOT_DIR}/config
        -Dconfig.properties=${ROOT_DIR}/config/crash-data.properties
        -Dcom.sun.management.jmxremote
        -Dcom.sun.management.jmxremote.port=9010
        -Dcom.sun.management.jmxremote.local.only=false
        -Dcom.sun.management.jmxremote.authenticate=false
        -Dcom.sun.management.jmxremote.ssl=false
        -Djava.library.path=${USR_DIR}/giscore/filegdb/linux/filegdb/dist/Release/GNU-Linux-x86/
	"

    # only use this version for debugging
	#java ${OPTIONS} com.moulliet.metro.CrashServiceMain
	sudo nohup java ${OPTIONS} com.moulliet.metro.CrashServiceMain  1>> ${STD_OUT_LOG} 2>&1 &

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


