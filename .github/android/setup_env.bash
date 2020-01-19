#!/usr/bin/env bash

set -euo pipefail

curl -o $HOME/toolkit.sh -sL "https://raw.githubusercontent.com/jmatsu/github-actions-toolkit/f23fcb2f07c2cc309e207e7ccc2a9731e01b4b81/toolkit.sh"

source $HOME/toolkit.sh

# a directory for custom binaries
readonly bin_dir="$HOME/.bin"
mkdir -p $bin_dir

curl -sL "https://raw.githubusercontent.com/jmatsu/transart/master/install.bash" | bash

cp ./transart $bin_dir/

# print .versions

github::set_output "ruby-version" "$(cat .ruby-version)"