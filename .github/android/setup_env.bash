#!/usr/bin/env bash

set -euo pipefail

github::debug() {
    echo "::debug::$@"
}

github::error() {
    echo "::error::$@"
}

github::failure() {
    exit 1
}

github::set_output() {
    echo "::set-output name=$1::$2"
}

# a directory for custom binaries
readonly bin_dir="$HOME/.bin"
mkdir -p $bin_dir

curl -sL "https://raw.githubusercontent.com/jmatsu/transart/master/install.bash" | bash

cp ./transart $bin_dir/

# print .versions

github::set_output "ruby-version" "$(cat .ruby-version)"