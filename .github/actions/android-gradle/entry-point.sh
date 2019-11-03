#!/usr/bin/env bash

set -eu
set -o pipefail

source /toolkit.sh

if [[ -n "${INPUT_SOURCE:-}" ]]; then
  if [[ -f "$INPUT_SOURCE" ]]; then
    source "$INPUT_SOURCE"
  else
    github::warning "$INPUT_SOURCE does not exist but specified"
  fi
fi

cd "$GITHUB_WORKSPACE"

if [[ -n "${INPUT_WORK_DIR:-}" ]]; then
  cd "$INPUT_WORK_DIR"
fi

# do not use double quotes
./gradlew $INPUT_RUN