#!/usr/bin/env bash

set -euo pipefail

source $HOME/toolkit.sh

readonly apk_path="$1"

aapt dump badging "$apk_path" | grep 'versionName=' | awk -F: 'match($0,"versionName="){ print $2 }' | tr -d "'" | awk '$0=$3' | sed 's/versionName=//' | github::set_output value