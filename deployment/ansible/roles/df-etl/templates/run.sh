#!/bin/bash
set -ex

environment_name=local

cd {{ company_ansible_dir }}
ansible-playbook \
    --extra-vars="environment_name=$environment_name" \
    --inventory-file=environment/$environment_name/hosts \
    --tags=run \
    df-etl.yml

{{ nifi_home }}/bin/nifi.sh start
exec tail -F {{ nifi_log_dir }}/nifi-app.log
