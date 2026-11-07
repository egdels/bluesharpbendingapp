# ONNX Runtime for Android (Basic Pitch chord engine)

The optional Basic Pitch chord engine ("Chord Engine (experimental)" in the settings) needs
ONNX Runtime. On Android it is **built from source**, because F-Droid does not accept the
prebuilt `onnxruntime-android` AAR from Maven. Without the runtime the app still builds and
runs; the chord detection then falls back to the established ChordDetector.

## What is built

- Source: git submodule `external/onnxruntime`, tag `v1.31.1` (shallow), plus its nested
  submodule `cmake/external/onnx`. Pinned Gradle files for the ORT Java build live in
  `external/onnxruntime_pinned/` (taken from makeacopy, same ORT version).
- Script: `scripts/build_onnxruntime_android.sh` (adapted from makeacopy)
  - minimal build (`--minimal_build extended`) with only the 21 operators of the model:
    `scripts/onnxruntime/nmp.required_operators.config`
  - CPU execution provider only, `--no_telemetry`, stripped, 16 KB page alignment
  - output (not committed, see `.gitignore`):
    - `android/app/src/main/jniLibs/<abi>/libonnxruntime.so`, `libonnxruntime4j_jni.so`
    - `android/app/libs/onnxruntime-1.31.1.jar` (Java API)
- Model: `base/src/main/resources/basic_pitch/nmp.ort` (ORT format, required by the minimal
  build). Regenerate model and operator config with
  `scripts/onnxruntime/convert_basic_pitch_model.py`.

## Building locally

Tool versions match CI and F-Droid: NDK 28.0.13004108, CMake 3.31.6, Python 3.11 with
`flatbuffers`, JDK 21 (or 17).

```bash
git submodule update --init --depth 1 external/onnxruntime
git -C external/onnxruntime submodule update --init --depth 1 cmake/external/onnx

python3.11 -m venv /tmp/ortvenv
/tmp/ortvenv/bin/pip install "cmake==3.31.6" flatbuffers
export PATH="/tmp/ortvenv/bin:$PATH"

# all four ABIs, or e.g. ABIS="arm64-v8a" for a quick device build
./scripts/build_onnxruntime_android.sh
./gradlew :android:assembleDebug
```

The build copies the sources to `/tmp/onnxruntime-src` and builds in `/tmp/onnxruntime-build`
(several GB per ABI; each ABI's build tree is deleted afterwards unless `KEEP_BUILD=1`).

## CI

`.github/actions/build-onnxruntime-android` runs the script before the Android release builds
(`build-on-push.yml`, `android-release.yml`) and caches the result, keyed on the submodule
commit, the operator config, the script and the pinned files. Only a change to one of those
triggers a rebuild.

## F-Droid recipe (fdroiddata)

The recipe in `fdroiddata/metadata/de.schliweb.bluesharpbendingapp.yml` has to change for
the first version that contains the runtime. Proposal for a universal APK (all four ABIs in
one build), modelled on makeacopy's recipe:

```yaml
  - versionName: 3.9.0
    versionCode: 30900
    commit: v3.9.0
    subdir: android/app
    submodules: true
    timeout: 14400
    sudo:
      - apt-get update
      - apt-get install -y make ninja-build cmake python3-flatbuffers
    gradle:
      - yes
    rm:
      - npm
      - desktop
      - webapp
      - package.json
      - external/onnxruntime/onnxruntime/test
      - external/onnxruntime/samples
    prebuild: sed -i -e '/^\t\tif (publishToAdo) {/,/^\t\t}/d' ../../external/onnxruntime/java/build.gradle
    scanignore:
      - external/onnxruntime/java/build-android.gradle
      - external/onnxruntime/java/build.gradle
      - external/onnxruntime/js/react_native/e2e/android/build.gradle
      - external/onnxruntime/js/react_native/android/build.gradle
      - external/onnxruntime_pinned/build-android.gradle
    scandelete:
      - external/onnxruntime
    build:
      - cd ../..
      - ./scripts/build_onnxruntime_android.sh
    ndk: 28.0.13004108
```

Notes:

- Debian's `cmake` package must be >= 3.28 on the F-Droid build server (makeacopy builds there
  with this recipe, so it is). The script picks the first CMake >= 3.28 (`ORT_CMAKE`, `PATH`,
  Android SDK).
- A universal APK compiles ONNX Runtime four times in one build. makeacopy instead builds one
  APK per ABI (`enableAbiSplits`, one recipe entry per ABI, `VercodeOperation`), which keeps
  each F-Droid build shorter and each APK smaller, but changes the versionCode scheme.
- `AutoUpdateMode` keeps working as long as the recipe of the new version is copied unchanged.
