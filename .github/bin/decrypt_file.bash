#!/usr/bin/env bash

set -eu
set -o pipefail

# a name of an original raw file
readonly file="${1%.gpg}"

gpg --quiet --batch --yes --decrypt --passphrase="$LARGE_SECRET_PASSPHRASE" \
--output ".github/decrypted-secrets/$file" "$file.gpg"