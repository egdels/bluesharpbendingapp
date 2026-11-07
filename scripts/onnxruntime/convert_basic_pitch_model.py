#!/usr/bin/env python3
"""Convert the Basic Pitch model to ORT format and regenerate the required-operators config.

The app ships base/src/main/resources/basic_pitch/nmp.ort, converted from
basic_pitch/saved_models/icassp_2022/nmp.onnx of Spotify basic-pitch 0.4.0
(sha256 2c3c1d144bfa61ad236e92e169c13535c880469a12a047d4e73451f2c059a0ec).

ORT format is required by the reduced ("minimal") ONNX Runtime built for Android; the full
desktop runtime loads it as well. BASIC graph optimizations only, so the model stays portable
across CPU architectures and ONNX Runtime versions (no platform-specific fused operators).
Converted with onnxruntime 1.22.0, the version of the desktop runtime: newer runtimes read
ORT format files written by older ones, not the other way round.

The output is not byte-identical between runs (internal ordering differs), but numerically
identical: the ORT model produces exactly the same outputs as the ONNX original.

Usage (Python 3.11):
  python3 -m venv venv && venv/bin/pip install onnxruntime==1.22.0
  venv/bin/python scripts/onnxruntime/convert_basic_pitch_model.py path/to/nmp.onnx
"""

import hashlib
import pathlib
import sys

import onnxruntime as ort
from onnxruntime.tools.ort_format_model import create_config_from_models

EXPECTED_SHA256 = "2c3c1d144bfa61ad236e92e169c13535c880469a12a047d4e73451f2c059a0ec"
REPO = pathlib.Path(__file__).resolve().parents[2]
ORT_MODEL = REPO / "base/src/main/resources/basic_pitch/nmp.ort"
OPS_CONFIG = REPO / "scripts/onnxruntime/nmp.required_operators.config"


def main() -> None:
    if len(sys.argv) != 2:
        sys.exit(__doc__)
    source = pathlib.Path(sys.argv[1])
    digest = hashlib.sha256(source.read_bytes()).hexdigest()
    if digest != EXPECTED_SHA256:
        sys.exit(f"unexpected model {source}: sha256 {digest}, expected {EXPECTED_SHA256}")
    if ort.__version__ != "1.22.0":
        print(f"warning: onnxruntime {ort.__version__} instead of 1.22.0", file=sys.stderr)

    options = ort.SessionOptions()
    options.graph_optimization_level = ort.GraphOptimizationLevel.ORT_ENABLE_BASIC
    options.optimized_model_filepath = str(ORT_MODEL)
    options.add_session_config_entry("session.save_model_format", "ORT")
    ort.InferenceSession(str(source), options, providers=["CPUExecutionProvider"])

    create_config_from_models([ORT_MODEL], OPS_CONFIG, enable_type_reduction=False)
    text = OPS_CONFIG.read_text().replace(str(ORT_MODEL), "base/src/main/resources/basic_pitch/nmp.ort")
    OPS_CONFIG.write_text(text)
    print(f"wrote {ORT_MODEL} and {OPS_CONFIG}")


if __name__ == "__main__":
    main()
