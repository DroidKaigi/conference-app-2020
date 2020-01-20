#!/bin/sh

echo "include ':ios-combined'" > $SRCROOT/../ios-settings.gradle
cd $SRCROOT/../
./gradlew :ios-combined:linkIosX64 -PXCODE_CONFIGURATION=${CONFIGURATION}
