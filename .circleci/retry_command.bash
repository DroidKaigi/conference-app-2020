#!/usr/bin/env bash

set -euo pipefail

: "${MAX_RETRY_COUNT:=3}"

retry() {
    local retry=0

    while let "$MAX_RETRY_COUNT > $retry"; do
        let "retry=$retry+1"

        "$@" && exit 0

        sleep 3
    done

    echo "Failed to process : $@" 1>&2
    exit 1
}

retry "$@"