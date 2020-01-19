#!/bin/sh

cd ../
./gradlew :data:repository:linkIosX64 -PXCODE_CONFIGURATION=${CONFIGURATION}
