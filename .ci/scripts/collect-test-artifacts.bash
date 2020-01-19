#!/usr/bin/env bash

set -euo pipefail

mkdir -p $JUNIT_TEST_RESULT_DIR
find . -type f -regex ".*/build/test-results/.*xml" -exec cp {} $JUNIT_TEST_RESULT_DIR/ \;

mkdir -p $JUNIT_TEST_REPORT_DIR

while read dirpath; do
    cp -r $dirpath $JUNIT_TEST_REPORT_DIR/$(echo $dirpath | sed -e 's/^\.\///g' -e 's/\//-/g')
done < <(find . -type d -regex ".*/build/reports")