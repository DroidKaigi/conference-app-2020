#!/usr/bin/env bash

set -eu

source $HOME/toolkit.sh

readonly directory="$1"

readonly aab_path="$(find $directory -name '*.aab' | head -1)"

if [[ -f "$aab_path" ]]; then
    github::set_output "aab_path" "$aab_path"
    github::set_output "aab_hit" "true"
else
    github::set_output "aab_hit" "false"
fi

readonly apk_path="$(find $directory -name '*.apk' | head -1)"

if [[ -f "$apk_path" ]]; then
    github::set_output "apk_path" "$apk_path"
    github::set_output "apk_hit" "true"
else
    github::set_output "apk_hit" "false"
fi
