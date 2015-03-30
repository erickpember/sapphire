#!/bin/bash

environment_name=local

ansible-playbook \
    --connection=local \
    --extra-vars="environment_name=$environment_name" \
    --inventory-file=ansible/environment/$environment_name/hosts \
    --skip-tags=image,run \
    ansible/local.yml
