#!/bin/bash
set -ex

environment_name=local

cd {{ company_ansible_dir }}
ansible-playbook \
    --extra-vars="environment_name=$environment_name" \
    --inventory-file=environment/$environment_name/hosts \
    --tags=run \
    storm-nimbus.yml

{{ storm_home }}/bin/storm nimbus &

exec {{ storm_home }}/bin/storm ui
