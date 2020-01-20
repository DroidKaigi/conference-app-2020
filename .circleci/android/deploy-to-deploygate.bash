#!/usr/bin/env bash

set -euo pipefail

die() {
  echo "$*" 1>&2
  exit 1
}

if [[ $CIRCLE_BRANCH =~ ^(master|release)$ ]]; then
  echo "Skip deployment"
  exit 0
fi

readonly apk_path="$(find android-base/build/outputs -name '*.apk' | head -1)"

curl -X POST \
  -sSfL \
  -o $HOME/deploygate-response.json \
  -H "Authorization: token $DEPLOYGATE_API_TOKEN" \
  -F "message=Debug build : $(git rev-parse --short HEAD) at $(date)" \
  -F "distribution_name=$CIRCLE_BRANCH" \
  -F "file=@$apk_path" \
  "https://deploygate.com/api/users/droidkaigi/apps"

curl -sfSL -o /dev/null "$(cat $HOME/deploygate-response.json | jq -r ".results.file")"
