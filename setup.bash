#!/usr/bin/env bash

set -eu
set -o pipefail

cd "$(git rev-parse --show-toplevel)"

ln -s "$PWD/.hooks/pre-commit.bash" .git/hooks/pre-commit