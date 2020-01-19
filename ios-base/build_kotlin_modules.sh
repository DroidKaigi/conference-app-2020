#!/bin/sh

cd ../
./gradlew :data:api:linkIosX64 -PXCODE_CONFIGURATION=${CONFIGURATION}
