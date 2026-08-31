#!/usr/bin/env bash
# Idempotent project bootstrap for the ShoppingApp Android build.
# Runs after the repository is checked out. Safe to run repeatedly.
set -euo pipefail

ANDROID_HOME="${ANDROID_HOME:-/opt/android-sdk}"

# Gradle locates the SDK via local.properties (git-ignored) or ANDROID_HOME.
# Write it explicitly so the build works regardless of the shell environment.
printf 'sdk.dir=%s\n' "${ANDROID_HOME}" > local.properties

# Warm the Gradle/dependency caches and verify the debug build compiles.
./gradlew --no-daemon :app:assembleDebug
