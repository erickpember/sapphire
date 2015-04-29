#!/bin/bash
# Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
# For license information, please contact http://datafascia.com/contact

basedir=/tmp/datafascia-test

mkdir -p $basedir

cd $basedir

if [ ! -f $basedir/client.jks ]; then
  echo "Generating client keystore."
  keytool -genkey -noprompt \
   -alias testuser \
   -validity 7300 \
   -dname "CN=testuser" \
   -keystore client.jks \
   -storepass secret \
   -keypass secret
fi

if [ ! -f $basedir/server.jks ]; then
  echo "Generating server keystore."
  keytool -genkey -noprompt \
   -alias server \
   -validity 7300 \
   -dname "CN=localhost" \
   -keystore server.jks \
   -storepass secret \
   -keypass secret
fi
