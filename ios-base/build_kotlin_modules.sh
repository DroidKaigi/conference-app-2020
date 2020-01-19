#!/bin/sh

cd $SRCROOT/../
./gradlew :data:api:linkIosX64 -PXCODE_CONFIGURATION=${CONFIGURATION}
