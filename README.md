crash-data
==========

Tools for visualizing and analyzing crash data 

The Java process serves all static files and dynamic data.

## Local Development

install:
* [java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [bower](http://bower.io/) - Manages javascript libraries for web pages
    * run ```bower install```
* [mongoDB](http://www.mongodb.org/) - Database for crashes
* Run MongoDB locally with data directory
    * ```mongod ```
* Setup dependencies for [giscore](https://github.com/OpenSextant/giscore/wiki/FileGDB-Dependencies)
* Run all tests
* Run com.moulliet.metro.CrashServiceMain

## Deployment

Follow scripts/linode_setup.sh or metro_setup.sh to setup a new server.

To deploy from your local machine:
* Download the project code from github
* Setup ssh keys for target server(s)
* run ./deploy.sh to build code and deploy to a server.
    * sh deploy.sh build serverName
* crash-data/scripts 

* The service can be restarted using:
```
sh crash-data/scripts/crash-data.sh restart
```
This should be run as the crash user
