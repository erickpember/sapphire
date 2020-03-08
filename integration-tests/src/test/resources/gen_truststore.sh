#!/bin/bash
# Copyright 2020 dataFascia Corporation
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

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
