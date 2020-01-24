#!/bin/sh

git diff --name-only --diff-filter=d | grep -e '\(.*\).swift$' | while read filename; do
  mint run realm/swiftlint swiftlint --path "$SRCROOT/../$filename"
done
