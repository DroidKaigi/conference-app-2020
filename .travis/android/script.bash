#!/usr/bin/env bash

set -euo pipefail

readonly working_directory="$(git rev-parse --show-toplevel)"
readonly decrypted_files_directory="$working_directory/to_be_encrypted"

die() {
  echo "$*" 1>&2
  exit 1
}

./gradlew android-base:bundleRelease -x android-base:lintVitalRelease

readonly aab_path="$(find android-base -name '*.aab' | head -1)"

dpg app upload --android \
  --app-owner droidkaigi \
  --app "$aab_path" \
  --token "$DEPLOYGATE_API_TOKEN" \
  --message "Release build of $(git rev-parse --short HEAD) at $(date)" \
  --distribution-name "Production Build" > .dpg_response

export APP_VERSION_CODE=$(cat .dpg_response | jq -r ".results.version_code")
export APP_VERSION_NAME=$(cat .dpg_response | jq -r ".results.version_name")

if [[ -z "${TRAVIS_TAG:-}" ]] && [[ "${TRAVIS_BRANCH:-}" != "release" ]]; then
  echo "Do not upload to github releases"
  exit 0
fi

if [[ -n "${TRAVIS_TAG:-}" ]]; then
  if [[ "$TRAVIS_TAG" != "v$APP_VERSION_NAME" ]]; then
    die "tag and version name are different! ($TRAVIS_TAG and $APP_VERSION_NAME)"
  fi

  export RELEASE_TAG_NAME="$TRAVIS_TAG"
else
  # TODO upload to internal tracks
  exit 0
fi

./gradlew android-base:bundleRelease -x android-base:lintVitalRelease

.travis/android/update_github_release.bash
transart -f .travis/android/to_github.transart.yml transfer
