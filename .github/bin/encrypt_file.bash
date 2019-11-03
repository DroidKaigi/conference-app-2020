#!/usr/bin/env bash

set -eu
set -o pipefail

readonly file="$1"

if [[ ! -f "$file" ]]; then
    echo "$file does not exist" 1>&2
    exit 1
fi

gpg --symmetric --cipher-algo AES256 "$1"