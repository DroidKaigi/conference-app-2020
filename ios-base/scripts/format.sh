#!/bin/sh

git status --porcelain | grep -e '^[ ?][^D] ".*\.swift"$' | awk -F'"' '{print $2}' | while read filename; do
  # ref: https://github.com/yonaskolb/Mint/issues/112
  mint run swiftformat swiftformat "$SRCROOT/../$filename"
done
