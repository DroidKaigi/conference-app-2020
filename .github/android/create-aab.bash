#!/usr/bin/env bash

set -euo pipefail

readonly working_directory="$(git rev-parse --show-toplevel)"
cd "$working_directory"

source $HOME/toolkit.sh

./gradlew android-base:bundleRelease -x android-base:lintVitalRelease

readonly path="$(find android-base -name '*.aab' | head -1)"

if [[ -f "$path" ]]; then
    github::set_output "path" "$path"
    github::set_output "hit" "true"
else
    github::set_output "hit" "false"
fi
