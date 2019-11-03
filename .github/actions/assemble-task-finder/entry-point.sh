#!/usr/bin/env bash

set -eu
set -o pipefail

if [[ -z "${INPUT_BUILD_FLAVOR:-}" ]]; then
  github::error "inputs.build_flavor is required"
  github::failure
fi

readonly build_flavor="$INPUT_BUILD_FLAVOR"
readonly vitalTaskName="android-base:lintVital${build_flavor^}"

if ./gradlew tasks | grep "$vitalTaskName" >/dev/null 2>&1; then
  github::set_output "args" ":android-base:assemble${build_flavor^}" -x "$vitalTaskName"
else
  github::set_output "args" ":android-base:assemble${build_flavor^}"
fi