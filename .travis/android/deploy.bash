#!/usr/bin/env bash

set -euo pipefail

die() {
  echo "$*" 1>&2
  exit 1
}

readonly apk_path="$(find android-base -name '*.apk' | head -1)"

curl -X POST \
  -sSfL \
  -o response.json \
  -H "Authorization: token $DEPLOYGATE_API_TOKEN" \
  -F "message=Debug build : $(git rev-parse --short HEAD) at $(date)" \
  -F "distribution_name=$TRAVIS_BRANCH" \
  -F "file=@$apk_path" \
  "https://deploygate.com/api/users/droidkaigi/apps"

export DEPLOYGATE_DISTRIBUTION_URL=$(cat response.json | jq -r ".results.distribution.url")

bundle check --path=vendor/bundle --gemfile=.ci/Gemfile || bundle install --path=vendor/bundle --jobs=4 --clean --gemfile=.ci/Gemfile
bundle exec --gemfile=.ci/Gemfile danger --dangerfile=".ci/danger/post_deploygate.Dangerfile" --danger_id='post_deploygate' --remove-previous-comments

curl -sfSL -o /dev/null "$DEPLOYGATE_DISTRIBUTION_URL"
