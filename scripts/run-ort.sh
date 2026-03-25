#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
OUT_DIR="${ROOT_DIR}/ort-out"

echo "==> Cleaning previous ORT output"
rm -rf "${OUT_DIR}"
mkdir -p "${OUT_DIR}"

echo "==> Running ORT analyze"
ort --config "${ROOT_DIR}/ort/config/config.yml" \
  analyze \
  -i "${ROOT_DIR}" \
  -o "${OUT_DIR}/analyzer"

echo "==> Running ORT report"
ort --config "${ROOT_DIR}/ort/config/config.yml" \
  report \
  -i "${OUT_DIR}/analyzer/analyzer-result.yml" \
  -o "${OUT_DIR}/reports" \
  --report-formats WebApp,StaticHtml,CycloneDx,SpdxDocument

echo "==> ORT completed"
echo "Analyzer: ${OUT_DIR}/analyzer"
echo "Reports:  ${OUT_DIR}/reports"
