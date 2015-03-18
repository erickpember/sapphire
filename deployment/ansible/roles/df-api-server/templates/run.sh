#!/bin/bash
set -ex

environment_name=local

cd {{ company_ansible_dir }}
ansible-playbook \
    --extra-vars="environment_name=$environment_name" \
    --inventory-file=environment/$environment_name/hosts \
    --tags=run \
    df-api-server.yml

cd {{ company_install_prefix }}/df-api-server
exec java -jar df-api-server-{{ df_platform_version }}.jar server configuration.yml
