#!/bin/bash
set -ex

environment_name=local

cd {{ company_ansible_dir }}
ansible-playbook \
    --extra-vars="environment_name=$environment_name" \
    --inventory-file=environment/$environment_name/hosts \
    --tags=run \
    kafka.yml

export LOG_DIR={{ kafka_log_dir }}
exec {{ kafka_home }}/bin/kafka-server-start.sh {{ kafka_home }}/config/server.properties
