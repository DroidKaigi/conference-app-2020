#!/usr/bin/env bash

set -euo pipefail

prepare_custom_bins() {
    local -r bin_dir="$HOME/.bin"
    mkdir -p $bin_dir

    curl -sL "https://raw.githubusercontent.com/jmatsu/dpg/master/install.bash" | bash
    curl -sL "https://raw.githubusercontent.com/jmatsu/transart/master/install.bash" | bash

    cp ./dpg $bin_dir/
    cp ./transart $bin_dir/
}

prepare_custom_bins
./gradlew dependencies