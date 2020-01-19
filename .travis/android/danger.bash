#!/usr/bin/env bash

set -euo pipefail

bundle check --path=vendor/bundle --gemfile=.ci/Gemfile || bundle install --path=vendor/bundle --jobs=4 --clean --gemfile=.ci/Gemfile
bundle exec --gemfile=.ci/Gemfile danger --dangerfile=".ci/danger/static_analysis.Dangerfile" --danger_id='static_analysis' --remove-previous-comments