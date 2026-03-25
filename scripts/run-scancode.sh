#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
OUT_DIR="${ROOT_DIR}/ort-out/scancode"

echo "==> Cleaning previous ScanCode output"
rm -rf "${OUT_DIR}"
mkdir -p "${OUT_DIR}"

# Scan only the shipped app source code, not the entire repository.
# This project has multiple source modules: base, desktop, android, webapp.
# We scan the main source directories of each module.
SCAN_DIRS=(
  "${ROOT_DIR}/base/src/main"
  "${ROOT_DIR}/desktop/src/main"
  "${ROOT_DIR}/android/app/src/main"
  "${ROOT_DIR}/webapp/src/main"
)

for SCAN_DIR in "${SCAN_DIRS[@]}"; do
  if [ -d "${SCAN_DIR}" ]; then
    MODULE_NAME=$(echo "${SCAN_DIR}" | sed "s|${ROOT_DIR}/||" | tr '/' '-')
    echo "==> Running ScanCode on ${SCAN_DIR}"
    scancode \
      --copyright \
      --license \
      --info \
      --license-references \
      --strip-root \
      --processes 4 \
      --timeout 120 \
      --ignore "*.so" \
      --ignore "*.ort" \
      --ignore "*.traineddata" \
      --ignore "*.ttf" \
      --ignore "*.otf" \
      --ignore "*.gz" \
      --ignore "*.png" \
      --ignore "*.jpg" \
      --ignore "*.jpeg" \
      --ignore "*.webp" \
      --ignore "*.pdf" \
      --json-pp "${OUT_DIR}/scancode-${MODULE_NAME}.json" \
      "${SCAN_DIR}"
  fi
done

echo "==> ScanCode completed"
echo "Results: ${OUT_DIR}/"
