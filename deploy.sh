#!/usr/bin/env bash

WAR_FILE=regulus*.war
WAR_FILE_DIR=build/libs/$WAR_FILE
PAYARA_SERVER_DIR=/home/maelstrom/Downloads/payara-web-6.2024.2
PAYARA_AUTODEPLOY_DIR=$PAYARA_SERVER_DIR/payara6/glassfish/domains/domain1/autodeploy

start_time=$(date +%s)

./gradlew -q spotlessApply build -x test

rm -f $PAYARA_AUTODEPLOY_DIR/$WAR_FILE

cp $WAR_FILE_DIR $PAYARA_AUTODEPLOY_DIR

end_time=$(date +%s)
duration=$((end_time - start_time))
echo "Deployed in $duration seconds."
