#!/bin/bash

# These are the commands to configure a linode server to host this application
#

ssh root@104.237.130.146
sudo echo "crash01" > /etc/hostname
hostname -F /etc/hostname
#add '104.237.130.146 crash01' to /etc/hosts

sudo useradd crash -m -s /bin/bash
sudo passwd crash
sudo adduser crash sudo
su - crash
mkdir crash-data
exit

sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
apt-get upgrade --show-upgraded
sudo apt-get install oracle-java8-installer
sudo apt-get install oracle-java8-set-default
java -version

#http://docs.mongodb.org/manual/tutorial/install-mongodb-on-ubuntu/
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 7F0CEB10
echo 'deb http://downloads-distro.mongodb.org/repo/ubuntu-upstart dist 10gen' | sudo tee /etc/apt/sources.list.d/mongodb.list
sudo apt-get update
sudo apt-get install -y mongodb-org
sudo service mongod restart

sudo dpkg-reconfigure tzdata

# what is this intended to do?
sudo vi /etc/ssh/sshd_config
sudo service ssh restart

sudo apt-get install fail2ban

sudo apt-get install ganglia-monitor gmetad
sudo apt-get install ganglia-webfrontend

cd /etc/apache2/sites-enabled
sudo ln -s /etc/ganglia-webfrontend/apache.conf ganglia.conf
sudo service apache2 restart

# for using custom giscore
sudo apt-get install gcc

export LD_LIBRARY_PATH=/home/crash/FileGDB_API/lib

sudo apt-get install git

git clone git://github.com/gxm/giscore.git --branch coord-xform

sudo apt-get install make build-essential g++

# run make from correct dir
cd /home/crash/giscore/filegdb/linux/filegdb
make all
# library is now in
# /home/crash/giscore/filegdb/linux/filegdb/dist/Release/GNU-Linux-x86/libfilegdb.so
# Use with
# -Djava.library.path=/home/crash/giscore/filegdb/linux/filegdb/dist/Release/GNU-Linux-x86/

