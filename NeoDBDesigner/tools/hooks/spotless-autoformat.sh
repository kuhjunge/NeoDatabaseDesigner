#!/bin/sh

echo '[git hook] executing gradle spotlessCheck before commit'
set -e

REPO_ROOT_DIR="$(git rev-parse --show-toplevel)"

files=$((git diff --cached --name-only --diff-filter=ACMR | grep -Ei "\.java$") || true)
if [ ! -z "${files}" ]; then
    comma_files=$(echo "$files" | paste -s -d "," -)
    "${REPO_ROOT_DIR}/gradlew" spotlessApply -Pfiles="$comma_files" &>/dev/null
    git add $(echo "$files" | paste -s -d " " -)
fi