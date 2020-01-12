#!/usr/bin/env bash

set -euo pipefail

readonly working_directory="$(git rev-parse --show-toplevel)"
readonly decrypted_files_directory="$working_directory/to_be_encrypted"

die() {
  echo "$*" 1>&2
  exit 1
}

move() {
  mkdir -p "$(dirname "$2")"
  mv "$1" "$2"
}

setup_google_services_json() {
  move "$decrypted_files_directory/google-services.json" android-base/src/release/
}

setup_release_keystore() {
  move "$decrypted_files_directory/release.keystore" android-base/
}

setup_google_services_json
setup_release_keystore

 ./gradlew assembleRelease

 readonly apk_path="$(find android-base -name '*.apk' | head -1)"

dpg app upload --android \
  --app-owner droidkaigi \
  --app "$apk_path" \
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

./gradlew bundleRelease

.travis/android/update_github_release.bash
transart -f .travis/android/to_github.transart.yml transfer