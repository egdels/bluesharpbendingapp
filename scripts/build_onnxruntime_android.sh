#!/usr/bin/env bash
#
# Builds a reduced ONNX Runtime for Android from source (git submodule external/onnxruntime,
# v1.31.1) for the optional Basic Pitch chord detection.
#
# Why from source: F-Droid builds everything from source and rejects the prebuilt
# onnxruntime-android AAR. The build is also much smaller:
#   * minimal build ("extended") with only the 21 operators of the Basic Pitch model
#     (scripts/onnxruntime/nmp.required_operators.config, see convert_basic_pitch_model.py)
#   * CPU execution provider only, no ML/contrib extras, no exceptions/RTTI
#   * --no_telemetry (ORT enables telemetry by default for Android builds since 1.31)
#
# Output:
#   android/app/src/main/jniLibs/<abi>/libonnxruntime.so, libonnxruntime4j_jni.so
#   android/app/libs/onnxruntime-1.31.1.jar (Java API)
# Both are build outputs and not committed.
#
# Requirements: Android SDK + NDK, CMake >= 3.28 (3.31.x recommended), Python 3 with the
# "flatbuffers" package, Java 17/21, ninja (optional, faster).
#
# Environment overrides:
#   ABIS="arm64-v8a armeabi-v7a x86 x86_64"   ABIs to build (default: all four)
#   ORT_CMAKE=/path/to/cmake                  CMake to use
#   ORT_OPS_CONFIG=<file>|none                operator config (none = full build)
#   JOBS=<n>                                  parallel jobs
#
# Adapted from makeacopy's scripts/build_onnxruntime_android.sh (same ORT version and flags).
set -euo pipefail

if [ "${DEBUG:-0}" = "1" ]; then
  set -x
fi
info() { >&2 echo "$@"; }

# Reproducible build timestamp and locale
export SOURCE_DATE_EPOCH=1700000000
export TZ=UTC

REPO_DIR="$(cd "$(dirname "$0")/.." && pwd)"
ORT_VERSION="1.31.1"
ORT_DIR_ORIG="$REPO_DIR/external/onnxruntime"
ORT_DIR="${ORT_SRC_COPY:-/tmp/onnxruntime-src}"
BUILD_ROOT="${ORT_BUILD_ROOT:-/tmp/onnxruntime-build}"
PINNED_DIR="$REPO_DIR/external/onnxruntime_pinned"
JNI_LIBS_BASE="$REPO_DIR/android/app/src/main/jniLibs"
APP_LIBS="$REPO_DIR/android/app/libs"
ORT_JAR="$APP_LIBS/onnxruntime-$ORT_VERSION.jar"

[ -f "$ORT_DIR_ORIG/tools/ci_build/build.py" ] || {
  echo "ERROR: ONNX Runtime submodule missing. Run: git submodule update --init --recursive external/onnxruntime" >&2
  exit 1
}

# ---- Operator config (minimal build) ----
ORT_OPS_CONFIG="${ORT_OPS_CONFIG:-$REPO_DIR/scripts/onnxruntime/nmp.required_operators.config}"
if [ "$ORT_OPS_CONFIG" = "none" ]; then
  ORT_OPS_CONFIG=""
  info "Minimal build: disabled"
else
  [ -f "$ORT_OPS_CONFIG" ] || { echo "ERROR: ops config not found: $ORT_OPS_CONFIG" >&2; exit 1; }
  # Copy to a fixed path so the build does not depend on where the repo is checked out.
  cp -f "$ORT_OPS_CONFIG" /tmp/ort_required_operators.config
  ORT_OPS_CONFIG=/tmp/ort_required_operators.config
  info "Minimal build: enabled ($(grep -vc '^#' "$ORT_OPS_CONFIG") operator lines)"
fi

ABIS="${ABIS:-arm64-v8a armeabi-v7a x86 x86_64}"
JOBS="${JOBS:-$(getconf _NPROCESSORS_ONLN 2>/dev/null || sysctl -n hw.ncpu 2>/dev/null || echo 2)}"
export CMAKE_BUILD_PARALLEL_LEVEL="$JOBS"
info "ABIs: $ABIS, jobs: $JOBS"

CMAKE_GENERATOR=""
command -v ninja >/dev/null 2>&1 && CMAKE_GENERATOR="Ninja"

# ---- Android SDK / NDK ----
if [ -z "${ANDROID_SDK_ROOT:-}" ]; then
  for d in "${ANDROID_HOME:-}" "$HOME/Library/Android/sdk" "$HOME/Android/Sdk"; do
    if [ -n "$d" ] && [ -d "$d" ]; then export ANDROID_SDK_ROOT="$d"; break; fi
  done
fi
[ -n "${ANDROID_SDK_ROOT:-}" ] || { echo "ERROR: Android SDK not found (set ANDROID_SDK_ROOT)" >&2; exit 1; }
if [ -z "${ANDROID_NDK_HOME:-}" ]; then
  LATEST_NDK="$(ls -d "$ANDROID_SDK_ROOT"/ndk/* 2>/dev/null | sort -V | tail -n1 || true)"
  [ -n "$LATEST_NDK" ] && export ANDROID_NDK_HOME="$LATEST_NDK"
fi
[ -n "${ANDROID_NDK_HOME:-}" ] || { echo "ERROR: Android NDK not found (set ANDROID_NDK_HOME)" >&2; exit 1; }
info "SDK: $ANDROID_SDK_ROOT"
info "NDK: $ANDROID_NDK_HOME"

# ---- CMake >= 3.28 ----
ver_ge() { [ "$(printf '%s\n' "$1" "$2" | sort -V | head -n1)" = "$2" ]; }
cmake_version() { "$1" --version | awk '/version/{print $3; exit}'; }
CMAKE_PATH=""
for cand in "${ORT_CMAKE:-}" "$(command -v cmake || true)" \
  $(ls -d "$ANDROID_SDK_ROOT"/cmake/*/bin/cmake 2>/dev/null | sort -V -r); do
  if [ -n "$cand" ] && [ -x "$cand" ] && ver_ge "$(cmake_version "$cand")" "3.28.0"; then
    CMAKE_PATH="$cand"
    break
  fi
done
[ -n "$CMAKE_PATH" ] || { echo "ERROR: no CMake >= 3.28 found (set ORT_CMAKE)" >&2; exit 1; }
info "CMake: $CMAKE_PATH ($(cmake_version "$CMAKE_PATH"))"

# ---- Java 21/17 for the ORT Java build (macOS helper) ----
if [ "$(uname)" = "Darwin" ] && [ -z "${JAVA_HOME:-}" ]; then
  JAVA_HOME="$(/usr/libexec/java_home -v 21 2>/dev/null || /usr/libexec/java_home -v 17 2>/dev/null || true)"
  [ -n "$JAVA_HOME" ] && export JAVA_HOME && export PATH="$JAVA_HOME/bin:$PATH"
fi
info "JAVA_HOME: ${JAVA_HOME:-unset}"

# ---- llvm-strip ----
HOST_TAG="$(uname | tr '[:upper:]' '[:lower:]')-x86_64"
STRIP_BIN="$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/$HOST_TAG/bin/llvm-strip"
[ -x "$STRIP_BIN" ] || STRIP_BIN="$(command -v llvm-strip || true)"

# ---- Fresh source copy with pinned Gradle files ----
info "Copying ONNX Runtime sources to $ORT_DIR ..."
rm -rf "$ORT_DIR"
mkdir -p "$ORT_DIR"
cp -a "$ORT_DIR_ORIG/." "$ORT_DIR"
for f in build-android.gradle settings-android.gradle; do
  cp -f "$PINNED_DIR/$f" "$ORT_DIR/java/$f"
done
cp -f "$PINNED_DIR/gradle-wrapper.properties" "$ORT_DIR/java/gradle/wrapper/gradle-wrapper.properties"

rm -rf "$BUILD_ROOT"
mkdir -p "$BUILD_ROOT" "$APP_LIBS"

for ABI in $ABIS; do
  info "Building ONNX Runtime $ORT_VERSION for $ABI ..."
  ABI_BUILD_DIR="$BUILD_ROOT/$ABI"
  mkdir -p "$ABI_BUILD_DIR"
  C_FLAGS="-g0 -fdebug-prefix-map=$ORT_DIR=. -fmacro-prefix-map=$ORT_DIR=. -ffile-prefix-map=$ORT_DIR=."
  LDFLAGS="-Wl,--build-id=none -Wl,-z,max-page-size=16384"
  ARGS=(
    --build_dir "$ABI_BUILD_DIR"
    --config Release
    --build_shared_lib
    --skip_tests
    --skip_onnx_tests
    --parallel "$JOBS"
    --use_full_protobuf
    --android
    --android_sdk_path "$ANDROID_SDK_ROOT"
    --android_ndk_path "$ANDROID_NDK_HOME"
    --android_api 28
    --android_abi "$ABI"
    --build_java
    --skip_submodule_sync
    --compile_no_warning_as_error
    --no_telemetry
  )
  if [ -n "$ORT_OPS_CONFIG" ]; then
    ARGS+=( --minimal_build extended --include_ops_by_config "$ORT_OPS_CONFIG" )
  fi
  if [ -n "$CMAKE_GENERATOR" ]; then
    ARGS+=( --cmake_generator "$CMAKE_GENERATOR" )
  fi
  DEFINES=(
    onnxruntime_BUILD_SHARED_LIB=ON
    onnxruntime_BUILD_JAVA=ON
    onnxruntime_ENABLE_PYTHON=OFF
    onnxruntime_BUILD_CSHARP=OFF
    onnxruntime_BUILD_NODEJS=OFF
    onnxruntime_BUILD_OBJC=OFF
    onnxruntime_BUILD_BENCHMARKS=OFF
    onnxruntime_BUILD_MS_EXPERIMENTAL_OPS=OFF
    onnxruntime_USE_FULL_PROTOBUF=ON
    onnxruntime_USE_MIMALLOC=OFF
    onnxruntime_USE_XNNPACK=OFF
    onnxruntime_USE_KLEIDIAI=OFF
    onnxruntime_USE_SVE=OFF
    onnxruntime_USE_CUDA=OFF
    onnxruntime_USE_TENSORRT=OFF
    onnxruntime_USE_ROCM=OFF
    onnxruntime_USE_DNNL=OFF
    onnxruntime_USE_OPENVINO=OFF
    onnxruntime_USE_QNN=OFF
    onnxruntime_USE_ARMNN=OFF
    onnxruntime_USE_ACL=OFF
    onnxruntime_USE_WEBNN=OFF
    onnxruntime_USE_WEBGPU=OFF
    onnxruntime_BUILD_WEBASSEMBLY_STATIC_LIB=OFF
    onnxruntime_ENABLE_LTO=OFF
    onnxruntime_ENABLE_MEMORY_PROFILE=OFF
    onnxruntime_ENABLE_LAZY_TENSOR=OFF
    onnxruntime_DISABLE_CONTRIB_OPS=OFF
    onnxruntime_DISABLE_ML_OPS=ON
    onnxruntime_DISABLE_RTTI=ON
    onnxruntime_DISABLE_EXCEPTIONS=ON
    onnxruntime_DISABLE_FLOAT8_TYPES=ON
    onnxruntime_BUILD_SHARED_LIB_TESTS=OFF
    onnxruntime_BUILD_UNIT_TESTS=OFF
    CMAKE_C_FLAGS="$C_FLAGS"
    CMAKE_CXX_FLAGS="$C_FLAGS"
    CMAKE_SHARED_LINKER_FLAGS="$LDFLAGS"
    CMAKE_EXE_LINKER_FLAGS="$LDFLAGS"
  )
  LOG="$ABI_BUILD_DIR/build_$ABI.log"
  ( cd "$ORT_DIR" && python3 tools/ci_build/build.py "${ARGS[@]}" \
      --cmake_path "$CMAKE_PATH" --cmake_extra_defines "${DEFINES[@]}" ) >> "$LOG" 2>&1 || {
    echo "ERROR: ONNX Runtime build failed for $ABI, see $LOG" >&2
    tail -n 40 "$LOG" >&2 || true
    exit 1
  }

  LIB_CORE="$(find "$ABI_BUILD_DIR" -type f -name 'libonnxruntime.so' -print -quit)"
  LIB_JNI="$(find "$ABI_BUILD_DIR" -type f -name 'libonnxruntime4j_jni.so' -print -quit)"
  [ -n "$LIB_CORE" ] && [ -n "$LIB_JNI" ] || { echo "ERROR: built libraries not found for $ABI" >&2; exit 1; }
  mkdir -p "$JNI_LIBS_BASE/$ABI"
  cp -f "$LIB_CORE" "$JNI_LIBS_BASE/$ABI/libonnxruntime.so"
  cp -f "$LIB_JNI" "$JNI_LIBS_BASE/$ABI/libonnxruntime4j_jni.so"
  if [ -x "$STRIP_BIN" ]; then
    for lib in "$JNI_LIBS_BASE/$ABI"/libonnxruntime*.so; do
      "$STRIP_BIN" --strip-all --remove-section=.comment --remove-section=.note \
        --remove-section=.note.gnu.build-id --remove-section=.note.gnu.property "$lib" || true
    done
  fi

  if [ ! -f "$ORT_JAR" ]; then
    JAR_PATH="$(find "$ORT_DIR/java/build/libs" "$ABI_BUILD_DIR" -type f -name 'onnxruntime*.jar' \
      ! -name '*sources*' ! -name '*javadoc*' -print -quit 2>/dev/null || true)"
    # Note: no "unzip | grep -q" here: grep -q exits early, unzip gets SIGPIPE and pipefail
    # turns that into a failure.
    if [ -z "$JAR_PATH" ] || ! unzip -Z1 "$JAR_PATH" > /tmp/ort_jar_list.txt \
      || ! grep -q 'ai/onnxruntime/OrtEnvironment.class' /tmp/ort_jar_list.txt; then
      echo "ERROR: ONNX Runtime Java jar not found" >&2
      exit 1
    fi
    cp -f "$JAR_PATH" "$ORT_JAR"
    # The jar also carries the native libraries of the ABI it was built with. On Android ORT loads
    # them from jniLibs via System.loadLibrary, so drop them: they would only bloat the APK and tie
    # the jar to one ABI.
    zip -q -d "$ORT_JAR" 'ai/onnxruntime/native/*' || true
  fi
  # Free disk space: the per-ABI build tree is several GB.
  [ "${KEEP_BUILD:-0}" = "1" ] || rm -rf "$ABI_BUILD_DIR"
done

info "Done."
info "JAR: $ORT_JAR"
for ABI in $ABIS; do
  info "  $ABI: $(du -h "$JNI_LIBS_BASE/$ABI/libonnxruntime.so" | cut -f1) libonnxruntime.so"
done
