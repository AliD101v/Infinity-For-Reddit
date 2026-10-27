#!/usr/bin/env bash
# Builds an APK using the personal settings in .env (run scripts/setup-build-env.sh once first).
# Usage: scripts/build-apk.sh [debug|release|minifiedRelease] (default: release)
set -euo pipefail
cd "$(dirname "$0")/.."

if [ -z "${JAVA_HOME:-}" ]; then
    JAVA_HOME="$(ls -d "$HOME"/.local/jdk/jdk-21* 2>/dev/null | sort -V | tail -1)"
    export JAVA_HOME
fi
export ANDROID_HOME="${ANDROID_HOME:-$HOME/Android/Sdk}"

variant="${1:-release}"
case "$variant" in
    debug | release | minifiedRelease) ;;
    *) echo "Usage: $0 [debug|release|minifiedRelease]" >&2; exit 1 ;;
esac

if [ ! -f .env ]; then
    echo "Missing .env; run scripts/setup-build-env.sh or copy .env.example to .env." >&2
    exit 1
fi

./gradlew "assemble${variant^}"

apk_dir="app/build/outputs/apk/$variant"
echo
echo "APK(s):"
ls -1 "$apk_dir"/*.apk
