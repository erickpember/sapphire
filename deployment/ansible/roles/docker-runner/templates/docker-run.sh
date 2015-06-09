#!/bin/bash
if [ $# -lt 2 ]; then
  echo "usage: $0 host_name run_arguments"
  exit 1
fi
hostName=$1
shift 1
set -ex

containerId=$(docker run --name=$hostName {{ docker_run_options }} $*)

ipAddress=$(docker inspect --format {{ "'{{ .NetworkSettings.IPAddress }}'" }} $containerId)

echo "$ipAddress $hostName" >{{ dnsmasq_hosts_dir }}/$hostName
service dnsmasq restart

# Delay because systemd allows at most 5 start requests in 10 seconds.
sleep 2

echo $containerId
