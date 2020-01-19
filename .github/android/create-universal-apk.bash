#!/usr/bin/env bash

set -euo pipefail

readonly working_directory="$(git rev-parse --show-toplevel)"

cd "$working_directory"

source $HOME/toolkit.sh

readonly aab_file="$1"

if [[ ! -f "$aab_file" ]]; then
    github::error "$aab_file was not found."
    github::failure
fi

java -jar bundletool-all.jar build-apks \
  --mode=universal\
  --ks=android-base/release.keystore\
  --ks-pass=pass:$RELEASE_KEYSTORE_STORE_PASSWORD\
  --ks-key-alias=$RELEASE_KEYSTORE_KEY_ALIAS\
  --key-pass=pass:$RELEASE_KEYSTORE_KEY_PASSWORD\
  --bundle=$aab_file\
  --output=android-base/build/universal.apks

mkdir -p android-base/build/outputs/universal-apk

unzip android-base/build/universal.apks -d android-base/build/outputs/universal-apk

readonly path="$(find android-base/build/outputs/universal-apk -name '*.apk' | head -1)"

if [[ -f "$path" ]]; then
    github::set_output "path" "$path"
    github::set_output "hit" "true"
else
    github::set_output "hit" "false"
fi
