#!/bin/sh

git diff --name-only --diff-filter=d | grep -e '\(.*\).swift$' | while read filename; do
  # ref: https://github.com/yonaskolb/Mint/issues/112
  mint run swiftformat swiftformat "$SRCROOT/../$filename"
done
