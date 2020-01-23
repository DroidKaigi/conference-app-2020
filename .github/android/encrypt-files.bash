#!/usr/bin/env bash

set -euo pipefail

readonly working_directory="$(git rev-parse --show-toplevel)"
readonly encrypted_files_directory="$working_directory/.encrypted"

cd $working_directory

zip -r to_be_encrypted.zip to_be_encrypted/

gpg --symmetric --cipher-algo AES256 -o $encrypted_files_directory/to_be_encrypted.zip.gpg to_be_encrypted.zip