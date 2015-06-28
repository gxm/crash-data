#!/bin/bash

# These are the commands to configure a metro server to host this application
#

sudo useradd crash -m -s /bin/bash
sudo passwd crash
sudo visudo
#add crash user to sudoers list
#crash   ALL=(ALL)     ALL
su - crash
mkdir crash-data
mkdir data
mkdir logs
exit

#http://docs.mongodb.org/manual/tutorial/install-mongodb-on-red-hat/
sudo vi /etc/yum.repos.d/mongodb-org-3.0.repo
#copy
[mongodb-org-3.0]
name=MongoDB Repository
baseurl=https://repo.mongodb.org/yum/redhat/$releasever/mongodb-org/3.0/x86_64/
gpgcheck=0
enabled=1

sudo yum install -y mongodb-org

# for using custom giscore
http://downloads2.esri.com/Software/FileGDB_API_1_3-64.tar.gz
scp FileGDB_API_1_3-64.tar.gz crashmap:.
tar -xzf FileGDB_API_1_3-64.tar.gz

export LD_LIBRARY_PATH=/home/crash/FileGDB_API/lib

git clone https://github.com/gxm/giscore.git

# run make from correct dir
cd /home/crash/giscore/filegdb/linux/filegdb
make all
# library is now in
# /home/crash/giscore/filegdb/linux/filegdb/dist/Release/GNU-Linux-x86/libfilegdb.so
# Use with
# -Djava.library.path=/home/crash/giscore/filegdb/linux/filegdb/dist/Release/GNU-Linux-x86/

cd ~/crash-data
wget http://central.maven.org/maven2/com/sun/jersey/contribs/jersey-multipart/1.19/jersey-multipart-1.19.jar

#install haproxy
#http://tecadmin.net/install-and-configure-haproxy-on-centos/

# http://www.serverlab.ca/tutorials/linux/network-services/deploying-an-haproxy-load-balancer-on-centos-6/

# how is the adming login created?