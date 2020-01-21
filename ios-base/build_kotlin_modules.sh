#!/bin/sh

cd $SRCROOT/../
BUILD_IOS=true ./gradlew :ios-combined:linkIosX64 -PXCODE_CONFIGURATION=${CONFIGURATION}
