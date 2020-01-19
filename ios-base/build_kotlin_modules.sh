#!/bin/sh

cd ../
./gradlew :ios-combined:linkIosX64 -PXCODE_CONFIGURATION=${CONFIGURATION}
