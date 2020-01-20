#!/usr/bin/env bash

set -euo pipefail

readonly working_directory="$(git rev-parse --show-toplevel)"

cd "$working_directory"

github::debug() {
    echo "::debug:: $@"
}

github::set_output() {
    echo "::set-output name=$1::$2"
}

./gradlew android-base:bundleRelease -x android-base:lintVitalRelease

readonly path="$(find android-base/build/outputs -name '*.aab' | head -1)"

if [[ -f "$path" ]]; then
    github::debug "found an aab file at $path"
    github::set_output "path" "$path"
    github::set_output "hit" "true"
else
    github::debug "could not find an aab file"
    github::set_output "hit" "false"
fi
