#!/usr/bin/env bash
#
# Fetch the Android Quickstart sample from the ThunderID samples repository into
# tests/e2e/.samples and point it at this SDK checkout instead of the published release, so the
# E2E suite and the PR builder exercise the code under test.
#
# Usage:
#   ./prepare-sample.sh               Prints the prepared sample directory on stdout
#
# Environment:
#   THUNDERID_SAMPLES_DIR   Existing local samples checkout to copy the sample from (no clone)
#   THUNDERID_SAMPLES_REPO  Samples repository to clone (default https://github.com/thunder-id/samples.git)
#   THUNDERID_SAMPLES_REF   Branch, tag or commit to clone (default main)
#
# Every run starts from a fresh copy, so re-running is safe.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../../.." && pwd)"
SAMPLES_DIR="$SCRIPT_DIR/../.samples"
SAMPLE_PATH="quickstarts/android"
SAMPLES_REPO="${THUNDERID_SAMPLES_REPO:-https://github.com/thunder-id/samples.git}"
SAMPLES_REF="${THUNDERID_SAMPLES_REF:-main}"

# The published artifact the sample depends on, substituted with this checkout's root project.
SDK_MODULE="com.github.thunder-id:android-sdks"

rm -rf "$SAMPLES_DIR"
mkdir -p "$SAMPLES_DIR"

if [ -n "${THUNDERID_SAMPLES_DIR:-}" ]; then
    echo "==> Copying $SAMPLE_PATH from $THUNDERID_SAMPLES_DIR" >&2
    [ -d "$THUNDERID_SAMPLES_DIR/$SAMPLE_PATH" ] || {
        echo "ERROR: $THUNDERID_SAMPLES_DIR/$SAMPLE_PATH does not exist." >&2
        exit 1
    }
    mkdir -p "$SAMPLES_DIR/$(dirname "$SAMPLE_PATH")"
    cp -R "$THUNDERID_SAMPLES_DIR/$SAMPLE_PATH" "$SAMPLES_DIR/$SAMPLE_PATH"
    # Drop local build state and configuration a working checkout may carry.
    rm -rf "$SAMPLES_DIR/$SAMPLE_PATH"/{build,.gradle,local.properties,config.properties}
else
    echo "==> Fetching $SAMPLE_PATH from $SAMPLES_REPO at $SAMPLES_REF" >&2
    # init + fetch rather than `clone --branch`, so a commit SHA works as the ref too.
    git -C "$SAMPLES_DIR" init -q
    git -C "$SAMPLES_DIR" remote add origin "$SAMPLES_REPO"
    git -C "$SAMPLES_DIR" fetch -q --depth 1 origin "$SAMPLES_REF"
    git -C "$SAMPLES_DIR" checkout -q FETCH_HEAD
fi

[ -d "$SAMPLES_DIR/$SAMPLE_PATH" ] || {
    echo "ERROR: $SAMPLE_PATH does not exist in $SAMPLES_REPO at $SAMPLES_REF." >&2
    exit 1
}
SAMPLE_DIR="$(cd "$SAMPLES_DIR/$SAMPLE_PATH" && pwd)"

grep -qF "\"$SDK_MODULE:" "$SAMPLE_DIR/build.gradle.kts" || {
    echo "ERROR: $SAMPLE_DIR/build.gradle.kts does not depend on $SDK_MODULE." >&2
    exit 1
}

echo "==> Pointing the sample at the local SDK in $REPO_ROOT" >&2
cat >> "$SAMPLE_DIR/settings.gradle.kts" <<KTS

// Added by tests/e2e/scripts/prepare-sample.sh: build against the local SDK checkout.
includeBuild("$REPO_ROOT") {
    name = "android"
    dependencySubstitution {
        substitute(module("$SDK_MODULE")).using(project(":"))
    }
}
KTS

echo "$SAMPLE_DIR"
