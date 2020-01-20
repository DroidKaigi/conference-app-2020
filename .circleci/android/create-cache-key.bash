#!/usr/bin/env bash

set -o pipefail
set -euo pipefail

md5() {
  if type md5sum >/dev/null 2>&1; then
    md5sum "$1"
  else
    command md5 "$1"
  fi
}

md5_files() {
  for_buildSrc() {
    local path=
    while read path; do
      md5 $path
    done < <(find buildSrc -type f | sort)
  }

  for_gradle_files() {
    local path=
    while read path; do
      md5 $path
    done < <(find . -name "*.gradle" | sort)
  }

  for_gradle_kts_files() {
    local path=
    while read path; do
      md5 $path
    done < <(find . -name "build.gradle.kts" | sort)
  }

  for_buildSrc
  for_gradle_files
  for_gradle_kts_files
}

md5_files