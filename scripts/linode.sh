#!/bin/bash

# These are the commands to configure a linode server to host this application
#

ssh root@74.207.248.81
sudo echo "crash01" > /etc/hostname
hostname -F /etc/hostname

sudo useradd crash -m -s /bin/bash
sudo passwd crash
sudo adduser crash sudo

#old
sudo apt-get install openjdk-7-jdk

sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
apt-get upgrade --show-upgraded
sudo apt-get install oracle-java8-installer
sudo apt-get install oracle-java8-set-default
java -version

# todo ...

sudo apt-key adv --keyserver keyserver.ubuntu.com --recv 7F0CEB10
echo 'deb http://downloads-distro.mongodb.org/repo/ubuntu-upstart dist 10gen' | sudo tee /etc/apt/sources.list.d/10gen.list
sudo apt-get update
sudo apt-get install mongodb-10gen
sudo service mongodb restart

sudo dpkg-reconfigure tzdata

sudo vi /etc/ssh/sshd_config
sudo service ssh restart

sudo apt-get install fail2ban

sudo apt-get install ganglia-monitor
sudo apt-get install ganglia-monitor gmetad
sudo apt-get install ganglia-webfrontend

cd /etc/apache2/sites-enabled
sudo ln -s /etc/ganglia-webfrontend/apache.conf ganglia.conf
sudo service apache2 restart

sudo ln -s /etc/apache2/mods-available/proxy_http.load /etc/apache2/mods-enabled/proxy_http.load
sudo ln -s /etc/apache2/mods-available/proxy.load /etc/apache2/mods-enabled/proxy.load

sudo ln -s /home/moulliet/transport/config/transport.conf /etc/apache2/sites-enabled/transport.conf

sudo service apache2 restart

#todo pick up here
sudo iptables -L