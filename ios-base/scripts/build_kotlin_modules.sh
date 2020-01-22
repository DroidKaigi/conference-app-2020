 #!/bin/sh

 cd $SRCROOT/../
 BUILD_IOS=true ./gradlew :ios-combined:$KN_LIBRARY_BUILD_TASK -PXCODE_CONFIGURATION=${CONFIGURATION}
 mkdir $SRCROOT/build
 cp -r $KN_LIBRARY_BUILD_PATH $SRCROOT/build
