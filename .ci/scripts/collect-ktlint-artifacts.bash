#!/usr/bin/env bash

set -euo pipefail

mkdir -p $KTLINT_REPORT_DIR

while read dirpath; do
    cp -r $dirpath $KTLINT_REPORT_DIR/$(echo $dirpath | sed -e 's/^\.\///g' -e 's/\//-/g')
done < <(find . -type d -regex ".*/build/ktlint")
