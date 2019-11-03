#!/usr/bin/env bash

set -eu
set -o pipefail

source /toolkit.sh

if [[ -z "${INPUT_FILENAME:-}" ]]; then
    github::error "$INPUT_FILENAME is required"
    github::failure
fi

readonly filename="${INPUT_FILENAME%.gpg}"

decrypt_file.bash "$filename"

if (($(cat "$filename" | xargs | wc -l) == 1)); then
    cat "$filename" | github::add_mask
else
    github::warning "Be careful! The secret cannot be masked so never print it!"
fi