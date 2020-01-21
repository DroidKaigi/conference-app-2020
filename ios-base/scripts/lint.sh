#!/bin/sh

git diff --name-only | grep -e '\(.*\).swift$' | while read filename; do
  mint run swiftlint --path "$filename"
done
