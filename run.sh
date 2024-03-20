#!/usr/bin/env bash

./gradlew -q spotlessApply microBundle -x test

java -jar build/libs/regulus-microbundle.jar \
  --nohazelcast \
  --autobindhttp \
  --contextroot '/' \
  --nocluster
