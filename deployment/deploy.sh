#!/bin/bash
#
# Deploy software to an environment.

if [ $# -lt 1 ]; then
  echo "usage: $0 enviroment_name [options]"
  exit 1
fi
environment_name=$1
shift

export ANSIBLE_HOST_KEY_CHECKING=False

ansible-playbook \
    --extra-vars="environment_name=$environment_name" \
    --inventory-file=ansible/environment/$environment_name/hosts \
    --private-key=~/.ssh/cluster2.pem \
    --user=ec2-user \
    --skip-tags=image,run \
    ansible/local.yml \
    $*
