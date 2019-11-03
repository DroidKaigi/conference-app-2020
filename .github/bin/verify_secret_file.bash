#!/usr/bin/env bash

## pre-commit hook sounds good

set -eu
set -o pipefail

cd "$(git rev-parse --show-toplevel)"

while read encrypted_file; do
  readonly original_file="${encrypted_file%.gpg}"

  if git diff --name-only --cached "$original_file" >/dev/null 2>&1; then
    echo "$original_file was indexed so removed from indices. Please be careful." 1>&2
    git reset -- "$original_file"
    echo "${original_file#./}" >> .gitignore
  fi
done < <(find . -name "*.gpg" -type f)

git add .gitignore || true
