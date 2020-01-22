#!/usr/bin/env bash

set -euo pipefail

die() {
    echo "$*" 1>&2
    exit 1
}

create_release_draft() {
    curl -X POST \
        -H "Authorization: token $GITHUB_TOKEN" \
        -d "$(jq -n \
          --arg tag_name "$RELEASE_TAG_NAME" \
          --arg target_commitish "$(git rev-parse HEAD)" \
          --arg name "$RELEASE_TAG_NAME" \
          --arg body "$(cat .release_template)" \
          '{ "draft": true, "name": $name, "tag_name": $tag_name, "target_commitish": $target_commitish, "body": $body }')" \
        "https://api.github.com/repos/$REPOSITORY_SLUG/releases"
}

update_release_draft() {
    local -r release_id="$1"

    curl -X POST \
        -H "Authorization: token $GITHUB_TOKEN" \
        -d "$(jq -n \
          --arg target_commitish "$(git rev-parse --short HEAD)" \
          --arg body "$(cat .release_template)" \
          '{ "target_commitish": $target_commitish, "body": $body }')" \
        "https://api.github.com/repos/$REPOSITORY_SLUG/releases/$release_id"
}

draft_count() {
    # this query does not return the size of draft releases actually but it's okay for now.

    curl -X GET \
        -H "Authorization: token $GITHUB_TOKEN" \
        "https://api.github.com/repos/$REPOSITORY_SLUG/releases" | \
        jq -r '.[] // {"draft": false} | select(.draft) // [] | length'
}

get_release_id() {
    curl -X GET \
        -H "Authorization: token $GITHUB_TOKEN" \
        "https://api.github.com/repos/$REPOSITORY_SLUG/releases" | \
        jq -r --arg tag_name "$RELEASE_TAG_NAME" '.[] // {} | select(.tag_name == $tag_name and .draft) // { "id" : -1 } | .id // -1'
}

: "${REPOSITORY_SLUG:=DroidKaigi/conference-app-2020}"

ruby -rerb -e 'puts ERB.new(File.read("./.ci/scripts/github-release/release_template.erb")).result(binding)' > .release_template

readonly release_id="$(get_release_id)"

if let "${release_id:--1} > 0"; then
    update_release_draft "$release_id"
elif let "$(draft_count) == 0"; then
    create_release_draft
else
    die 'a draft already exists. This script could not update any releases.'
fi
