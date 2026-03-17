#!/bin/bash
#
# Script to create an AppImage from the jpackage app-image output.
# This script is intended to be run on Linux (e.g., in a GitHub Actions CI environment).
#
# Prerequisites:
#   - The jpackageAppImage Gradle task must have been run first.
#   - appimagetool must be available (downloaded by this script if not present).
#
# Usage: ./build-appimage.sh [version]
#   version: optional, defaults to "1.0.0"
#
# The AppImage is built with embedded update information for GitHub Releases,
# enabling delta updates via AppImageUpdate and compatible tools.
# A .zsync file is generated alongside the AppImage for this purpose.
#

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "${SCRIPT_DIR}/../../.." && pwd)"
DESKTOP_DIR="${PROJECT_DIR}/desktop"
BUILD_DIR="${DESKTOP_DIR}/build"
APP_IMAGE_SOURCE="${BUILD_DIR}/appimage/BluesHarpBendingApp"
APPDIR="${BUILD_DIR}/BluesHarpBendingApp.AppDir"
OUTPUT_DIR="${BUILD_DIR}/appimage-output"
VERSION="${1:-1.0.0}"
GITHUB_OWNER="egdels"
GITHUB_REPO="bluesharpbendingapp"

echo "=== Building AppImage for BluesHarpBendingApp v${VERSION} ==="

# Verify app-image exists
if [ ! -d "${APP_IMAGE_SOURCE}" ]; then
    echo "ERROR: App-image not found at ${APP_IMAGE_SOURCE}"
    echo "Please run './gradlew :desktop:jpackageAppImage' first."
    exit 1
fi

# Clean previous AppDir
rm -rf "${APPDIR}" "${OUTPUT_DIR}"
mkdir -p "${APPDIR}" "${OUTPUT_DIR}"

# Copy app-image contents into AppDir
cp -r "${APP_IMAGE_SOURCE}"/* "${APPDIR}/"

# Create AppDir structure
mkdir -p "${APPDIR}/usr/share/icons/hicolor/256x256/apps"

# Copy icon
cp "${DESKTOP_DIR}/src/main/resources/ic_launcher_round.png" \
   "${APPDIR}/BluesHarpBendingApp.png"
cp "${DESKTOP_DIR}/src/main/resources/ic_launcher_round.png" \
   "${APPDIR}/usr/share/icons/hicolor/256x256/apps/BluesHarpBendingApp.png"

# Copy desktop file
cp "${SCRIPT_DIR}/BluesHarpBendingApp.desktop" "${APPDIR}/BluesHarpBendingApp.desktop"

# Create AppRun script
cat > "${APPDIR}/AppRun" << 'APPRUN_EOF'
#!/bin/bash
SELF="$(readlink -f "$0")"
HERE="${SELF%/*}"
export PATH="${HERE}/bin:${PATH}"
exec "${HERE}/bin/BluesHarpBendingApp" "$@"
APPRUN_EOF
chmod +x "${APPDIR}/AppRun"

# Download appimagetool if not available
APPIMAGETOOL="${BUILD_DIR}/appimagetool"
if [ ! -f "${APPIMAGETOOL}" ]; then
    echo "Downloading appimagetool..."
    ARCH="$(uname -m)"
    curl -fsSL -o "${APPIMAGETOOL}" \
        "https://github.com/AppImage/appimagetool/releases/download/continuous/appimagetool-${ARCH}.AppImage"
    chmod +x "${APPIMAGETOOL}"
fi

# Build AppImage
export ARCH="$(uname -m)"
export VERSION="${VERSION}"

# Build update information string for GitHub Releases (zsync transport)
UPDATE_INFO="gh-releases-zsync|${GITHUB_OWNER}|${GITHUB_REPO}|latest|BluesHarpBendingApp-*-${ARCH}.AppImage.zsync"

echo "Creating AppImage with update information..."
echo "Update info: ${UPDATE_INFO}"
"${APPIMAGETOOL}" --no-appstream \
    --updateinformation "${UPDATE_INFO}" \
    "${APPDIR}" \
    "${OUTPUT_DIR}/BluesHarpBendingApp-${VERSION}-${ARCH}.AppImage"

echo "=== AppImage created successfully ==="
echo "Output: ${OUTPUT_DIR}/BluesHarpBendingApp-${VERSION}-${ARCH}.AppImage"
echo "Zsync: ${OUTPUT_DIR}/BluesHarpBendingApp-${VERSION}-${ARCH}.AppImage.zsync"
