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

The F-Droid recipe (`fdroiddata/metadata/de.schliweb.bluesharpbendingapp.yml`) is a
**reproducible build**: it lists `Binaries:` (the APK of the GitHub release) and
`AllowedAPKSigningKeys`. F-Droid builds the app itself, compares its APK byte for byte with
`android-release.apk` from the GitHub release and only then publishes the developer-signed APK.
With ONNX Runtime built from source, the native libraries and the ORT jar must therefore come out
identical on the F-Droid build server and in CI. Falling back to F-Droid's own signing is not an
option: existing F-Droid installations could not update.

Build entry for the first version with ONNX Runtime (based on makeacopy's working recipe, same ORT
version and NDK); keep the rest of the recipe as it is:

```yaml
  - versionName: 3.9.0
    versionCode: 30900
    commit: <commit of tag v3.9.0>
    timeout: 14400
    subdir: android/app
    submodules: true
    sudo:
      - apt-get update
      - apt-get install -y make ninja-build cmake python3-flatbuffers zip unzip
    gradle:
      - yes
    rm:
      - npm
      - desktop
      - webapp
      - package.json
      - external/onnxruntime/onnxruntime/test
      - external/onnxruntime/samples
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

What changed compared to 3.8.0 and why:

- `submodules: true` fetches `external/onnxruntime`.
- `sudo` installs the build tools. `zip`/`unzip` are required by the script (it strips the
  native libraries from the ORT jar); it fails if they are missing instead of producing a
  different jar.
- `build` + `ndk` build ONNX Runtime for all four ABIs before Gradle; this takes much longer than
  a plain build, hence `timeout: 14400`.
- `rm`, `scanignore`, `scandelete` as in makeacopy, so the F-Droid scanner does not stop at test
  binaries or Maven repositories inside the ONNX Runtime sources. (makeacopy's `prebuild` that
  removes a `publishToAdo` block from `java/build.gradle` is not needed: ORT 1.31.1 has no such
  block.)

Keeping CI and F-Droid identical (reproducibility):

| | CI (`.github/actions/build-onnxruntime-android`) | F-Droid recipe |
|---|---|---|
| NDK | 28.0.13004108 | `ndk: 28.0.13004108` |
| CMake | 3.31.6 (pip) | Debian `cmake` (makeacopy pins 3.31.6 to match it) |
| Generator | Ninja (apt `ninja-build`) | Ninja (`ninja-build`) |
| zip/unzip | apt | apt |
| Paths, timestamps | `/tmp/onnxruntime-src`, `SOURCE_DATE_EPOCH` set by the script | same script |

Checked locally: two builds of the same ABI on the same machine give a byte-identical
`libonnxruntime.so`. The ORT jar differs only in its zip entry timestamps (build time); that does
not reach the APK, which contains the classes as dex code (the jar's only other entry,
`META-INF/MANIFEST.MF`, is not packaged).

Not verified so far: whether the F-Droid server's CMake and JDK (used for the ORT Java jar) produce
byte-identical output. Before the release, check with a test build: a merge request against
fdroiddata (F-Droid's CI builds it) or `fdroid build` with the fdroidserver Docker image, then
compare the APK with the CI release APK (e.g. `diffoscope`).

- `AutoUpdateMode` keeps working as long as the recipe of the new version is copied unchanged.
