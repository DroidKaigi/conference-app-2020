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

if [[ -z "${GPG_ENCRYPTION_PASSWORD:-}" ]]; then
    github::error "GPG_ENCRYPTION_PASSWORD is required"
    github::failure
fi

readonly working_directory="$(git rev-parse --show-toplevel)"
readonly decrypted_files_directory="$working_directory/to_be_encrypted"

cd $working_directory

readonly file="to_be_encrypted.zip"

gpg --quiet --batch --yes \
    --decrypt --passphrase="$GPG_ENCRYPTION_PASSWORD" \
    --output "$working_directory/$file" "$working_directory/.encrypted/$file.gpg"

unzip "$working_directory/$file"

move() {
  mkdir -p "$2"
  mv "$1" "$2/"
}

move "$decrypted_files_directory/google-services.json" android-base/src/release
move "$decrypted_files_directory/release.keystore" android-base