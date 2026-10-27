#!/usr/bin/env bash
# One-time setup for building on Linux/WSL without root: installs a JDK and the Android SDK under
# $HOME, creates .env from .env.example and generates a release signing keystore.
set -euo pipefail
cd "$(dirname "$0")/.."

JDK_DIR="$HOME/.local/jdk"
ANDROID_HOME="${ANDROID_HOME:-$HOME/Android/Sdk}"
CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-13114758_latest.zip"
TMP_DIR="$(mktemp -d)"
trap 'rm -rf "$TMP_DIR"' EXIT

if ! ls -d "$JDK_DIR"/jdk-21* >/dev/null 2>&1; then
    echo "Installing Temurin JDK 21 into $JDK_DIR"
    mkdir -p "$JDK_DIR"
    curl -fsSL -o "$TMP_DIR/jdk.tar.gz" "https://api.adoptium.net/v3/binary/latest/21/ga/linux/x64/jdk/hotspot/normal/eclipse"
    tar -xzf "$TMP_DIR/jdk.tar.gz" -C "$JDK_DIR"
fi
export JAVA_HOME="$(ls -d "$JDK_DIR"/jdk-21* | sort -V | tail -1)"
export PATH="$JAVA_HOME/bin:$PATH"

if [ ! -x "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" ]; then
    echo "Installing Android command-line tools into $ANDROID_HOME"
    mkdir -p "$ANDROID_HOME/cmdline-tools"
    curl -fsSL -o "$TMP_DIR/cmdline-tools.zip" "$CMDLINE_TOOLS_URL"
    python3 -c "import sys, zipfile; zipfile.ZipFile(sys.argv[1]).extractall(sys.argv[2])" \
        "$TMP_DIR/cmdline-tools.zip" "$TMP_DIR"
    mv "$TMP_DIR/cmdline-tools" "$ANDROID_HOME/cmdline-tools/latest"
    chmod +x "$ANDROID_HOME/cmdline-tools/latest/bin/"*
fi
yes | "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" --licenses >/dev/null || true
"$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" "platform-tools" "platforms;android-36" "build-tools;36.0.0" >/dev/null

if [ ! -f .env ]; then
    cp .env.example .env
    echo "Created .env; fill in REDDIT_CLIENT_ID and REDDIT_USERNAME."
fi

keystore_file="$(sed -n 's/^KEYSTORE_FILE=//p' .env)"
if [ -n "$keystore_file" ] && [ ! -f "$keystore_file" ]; then
    password="$(sed -n 's/^KEYSTORE_PASSWORD=//p' .env)"
    if [ -z "$password" ]; then
        password="$(head -c 24 /dev/urandom | base64 | tr -dc 'A-Za-z0-9')"
        sed -i "s/^KEYSTORE_PASSWORD=.*/KEYSTORE_PASSWORD=$password/; s/^KEY_PASSWORD=.*/KEY_PASSWORD=$password/" .env
    fi
    alias="$(sed -n 's/^KEY_ALIAS=//p' .env)"
    mkdir -p "$(dirname "$keystore_file")"
    keytool -genkeypair -keystore "$keystore_file" -alias "${alias:-infinity}" -keyalg RSA -keysize 4096 \
        -validity 36500 -storepass "$password" -keypass "$password" -dname "CN=Infinity for Reddit (personal build)"
    echo "Generated $keystore_file. Back it up together with .env; updates must be signed with the same key."
fi

echo "Done. Build with: scripts/build-apk.sh [debug|release]"
