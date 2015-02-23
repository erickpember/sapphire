# dataFascia Platform in Virtual Machine

This Vagrant script installs the dataFascia Platform software in a virtual
machine.


## Requirements

* [VirtualBox](https://www.virtualbox.org/wiki/Downloads)
* [Vagrant](http://www.vagrantup.com/downloads.html)
* [Ansible](http://docs.ansible.com/intro_installation.html)
* The host machine probably needs at least 8 GB of RAM.


## Installation

* Run the command `vagrant up` from the directory where this README.md file is
  located. This should finish after many minutes.

* On the host machine, add this line to the `/etc/hosts` file:

    192.168.222.222 devbox

  This enables applications running on the host machine to connect to Accumulo
  by using that name.


## Installed Software


### Accumulo

`/datafascia/accumulo/bin`
:   executables

`/datafascia/accumulo/logs`
:   log files

When you start or resume the virtual machine, you must start Accumulo with the
command:

    sudo service accumulo start


### Kafka

`/datafascia/kafka/bin`
:   executables

`/datafascia/kafka/logs`
:   log files


### Storm

`/datafascia/storm/bin`
:   executables

`/datafascia/storm/logs`
:   log files


### dataFascia Platform shell

`/datafascia/df-platform/bin`
:   executables
