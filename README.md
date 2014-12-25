crash-data
==========

Tools for visualizing and analyzing crash data 

### Use

### Local Development

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

### Deployment

Deployed at 104.237.130.146

Java process serves data requests and static files

follow scripts/linode_setup.sh to setup a server

run deploy.sh to build code and deploy to a server.
