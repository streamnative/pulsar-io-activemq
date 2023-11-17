#!/usr/bin/env bash

set -ex

echo "Releasing connector AWS EventBridge"

version=${1#v}
if [[ "x$version" == "x" ]]; then
  echo "You need give a version number of the connector AWS EventBridge"
  exit 1
fi

# Create a direcotry to save assets
ASSETS_DIR=release
mkdir $ASSETS_DIR

mvn clean install -DskipTests
mv target/pulsar-io-aws-eventbridge-*.nar  ./$ASSETS_DIR
cp README.md ./$ASSETS_DIR/pulsar-io-aws-eventbridge.md
