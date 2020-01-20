#!/usr/bin/env bash

set -euo pipefail

export DEPLOYGATE_DISTRIBUTION_URL=$(cat $HOME/deploygate-response.json | jq -r ".results.distribution.url")

bundle exec --gemfile=.ci/Gemfile danger --dangerfile=".ci/danger/post_deploygate.Dangerfile" --danger_id='post_deploygate' --remove-previous-comments
