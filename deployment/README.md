# dataFascia Platform Deployment

These scripts deploy the dataFascia Platform software.


## Linux

Deploy to the local Linux machine.


### Requirements

* [Ansible](http://docs.ansible.com/intro_installation.html)


### Installation

* Run the command `./local-deploy.sh` from the directory where this README.md file is
  located. This should finish after many minutes.


## Mac OS X

Deploy to a virtual machine hosted by Mac OS X.


### Requirements

* [VirtualBox](https://www.virtualbox.org/wiki/Downloads)
* [Vagrant](http://www.vagrantup.com/downloads.html)
* [Ansible](http://docs.ansible.com/intro_installation.html)
* The host machine probably needs at least 8 GB of RAM.


### Installation

* Run the command `vagrant up` from the directory where this README.md file is
  located. This should finish after many minutes.

* On the host machine, add this line to the `/etc/hosts` file:

    <192.168.222.222 or your own IP address> devbox

  This enables applications running on the host machine to connect to Accumulo
  by using that name.


## Installed Software


### Accumulo

`/opt/dF/accumulo/bin`
:   executables

`/var/dF/accumulo/logs`
:   log files


### Kafka

`/opt/dF/kafka/bin`
:   executables

`/var/dF/kafka/logs`
:   log files


### Storm

`/opt/dF/storm/bin`
:   executables

`/var/dF/storm/logs`
:   log files


### dataFascia Platform shell

`/opt/dF/df-platform/bin`
:   executables
