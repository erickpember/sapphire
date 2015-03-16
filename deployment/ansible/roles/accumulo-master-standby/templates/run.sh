#!/bin/bash
set -ex

environment_name=local

cd {{ company_ansible_dir }}
ansible-playbook \
    --extra-vars="environment_name=$environment_name" \
    --inventory-file=environment/$environment_name/hosts \
    --tags=run \
    accumulo-master-standby.yml

exec tail -f /var/log/hadoop-hdfs/hadoop-hdfs-secondarynamenode-*.log
