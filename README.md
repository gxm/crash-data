crash-data
==========

Tools for visualizing and analyzing crash data 

### Use

### Local Development

install:
* [bower](http://bower.io/) - Manages javascript libraries for web pages
* [mongoDB](http://www.mongodb.org/) - Database for crashes
* Run MongoDB locally with data directory
    * Load Data into MongoDB with com.moulliet.metro.load.LoadShapeFile class
    * This presumes data file has already pre-parsed to single crash JSON records per line
* Run all tests
* Run com.moulliet.metro.CrashServiceMain

### Deployment

Deployed at 104.237.130.146

Java process serves data requests and static files

follow scripts/linode_setup.sh to setup a server

run deploy.sh to build code and deploy to a server.
