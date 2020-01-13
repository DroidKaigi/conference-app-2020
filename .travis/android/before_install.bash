#!/usr/bin/env bash

set -euo pipefail

readonly working_directory="$(git rev-parse --show-toplevel)"
readonly decrypted_files_directory="$working_directory/to_be_encrypted"

prepare_custom_bins() {
    local -r bin_dir="$HOME/.bin"
    mkdir -p $bin_dir

    curl -sL "https://raw.githubusercontent.com/jmatsu/dpg/master/install.bash" | bash
    curl -sL "https://raw.githubusercontent.com/jmatsu/transart/master/install.bash" | bash

    cp ./dpg $bin_dir/
    cp ./transart $bin_dir/
}

move() {
  mkdir -p "$2"
  mv "$1" "$2/"
}

setup_google_services_json() {
  move "$decrypted_files_directory/google-services.json" android-base/src/release
}

setup_release_keystore() {
  move "$decrypted_files_directory/release.keystore" android-base
}

setup_google_services_json
setup_release_keystore

prepare_custom_bins
./gradlew dependencies