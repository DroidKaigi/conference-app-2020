 #!/bin/sh

 cd $SRCROOT/../
 BUILD_IOS=true ./gradlew :ioscombined:$KN_LIBRARY_BUILD_TASK -PXCODE_CONFIGURATION=${CONFIGURATION}
 mkdir -p $SRCROOT/build
 cp -r $KN_LIBRARY_BUILD_PATH $SRCROOT/build
