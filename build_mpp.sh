#!/bin/zsh
# Builds patches.mpp from the compiled patch classes, following the same
# packaging conventions as the Morphe Gradle plugin (PatchesPlugin):
#   - patch classes (+ bundled runtime deps like kotlin-stdlib)
#   - D8-dexed classes merged back into the jar as classes.dex / classes2.dex
#   - manifest "About" attributes + Patcher-Version
#
# Prerequisites:
#   - JDK 17 (with javac), Gradle, Android SDK with build-tools and platforms
# Optional environment overrides:
#   ANDROID_SDK_DIR   (default: ~/Library/Android/sdk)
#   STDLIB_VERSION    (default: 2.4.10)
set -euo pipefail

cd "$(dirname "$0")"
if [[ -z "${JAVA_HOME:-}" ]]; then
  echo "Error: JAVA_HOME must point to a JDK 17 installation (javac + jar are used)." >&2
  exit 1
fi
export PATH="$JAVA_HOME/bin:$PATH"

ROOT=$(pwd)
BUILD=$ROOT/patches/build
WORK=$BUILD/mpp-work
OUT=$ROOT/patches.mpp
SDK_DIR=${ANDROID_SDK_DIR:-$HOME/Library/Android/sdk}
STDLIB_VERSION=${STDLIB_VERSION:-2.4.10}
VERSION=${VERSION:-1.0.0}
STDLIB=$(find ~/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/kotlin-stdlib/$STDLIB_VERSION -maxdepth 2 -name "kotlin-stdlib-$STDLIB_VERSION.jar" | head -1)
if [[ -z "$STDLIB" ]]; then
  echo "Error: kotlin-stdlib-$STDLIB_VERSION.jar not found in the Gradle cache." >&2
  echo "Run 'gradle :patches:compileKotlin' once so Gradle can download it." >&2
  exit 1
fi
ANDROID_JAR=$(find "$SDK_DIR/platforms" -name "android.jar" | sort -V | tail -1)
D8=$(find "$SDK_DIR/build-tools" -name d8 | sort -V | tail -1)
if [[ -z "$ANDROID_JAR" || -z "$D8" ]]; then
  echo "Error: android.jar / d8 not found under $SDK_DIR." >&2
  exit 1
fi
JAR_TOOL=$JAVA_HOME/bin/jar
build_log=$WORK/build.log

rm -rf "$WORK" "$OUT"
mkdir -p "$WORK/classes" "$WORK/dexout"

# 1. Collect the patch classes + kotlin-stdlib classes into one directory,
#    excluding files that the Gradle plugin also excludes.
(
  cd "$WORK/classes"
  unzip -qo "$BUILD/libs/patches.jar" -x "META-INF/*" "module-info.class" "*/module-info.class"
  unzip -qo "$STDLIB" -x "META-INF/*" "module-info.class" "*/module-info.class" "META-INF/versions/*" "META-INF/LICENSE*" "META-INF/NOTICE*" "META-INF/*.kotlin_*" "*/*.kotlin_metadata"
  find . -name "*.proto" -delete
  find . -name "module-info.class" -delete
  find . -name "*.kotlin_metadata" -delete
  find . -name "*.kotlin_builtins" -not -name "kotlin*.kotlin_builtins" -delete
)

# 2. Write the manifest.
cat > "$WORK/MANIFEST.MF" <<EOF
Manifest-Version: 1.0
Created-By: SyncPatch
Name: SyncPatch
Description: Patches for Sync for Reddit. Makes hardcoded fonts follow the app's title font settings.
Version: $VERSION
Timestamp: $(date +%s)
Source: https://github.com/burakdede0/SyncPatch
Author: burakdede0
Contact: https://github.com/burakdede0/SyncPatch/issues
Website: https://github.com/burakdede0/SyncPatch
License: GPLv3
Patcher-Version: 1.15.1
EOF

# 3. Initial jar with classes, resources and the manifest.
(
  cd "$WORK/classes"
  $JAR_TOOL cfm "$WORK/patches-raw.jar" "$WORK/MANIFEST.MF" .
)

# 4. D8-dex the jar contents into classes.dex (DexIndexed; may split).
$D8 \
  --release \
  --min-api 26 \
  --output "$WORK/dexout" \
  --lib "$ANDROID_JAR" \
  "$WORK/patches-raw.jar"

ls -la "$WORK/dexout"

# 5. Merge the dex files back into the jar as classes.dex / classes2.dex / ...
cp "$WORK/patches-raw.jar" "$OUT"
cd "$WORK/dexout"
for dex in classes*.dex; do
  $JAR_TOOL -uf "$OUT" "$dex"
done
cd "$ROOT"

echo "Built $OUT"
unzip -l "$OUT" > "$build_log" 2>&1
grep -E "mpe|classes.dex|MANIFEST" "$build_log" || true
