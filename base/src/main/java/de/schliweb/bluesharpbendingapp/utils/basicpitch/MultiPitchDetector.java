/*
 * Copyright (c) 2023 Christian Kierdorf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package de.schliweb.bluesharpbendingapp.utils.basicpitch;

import java.util.List;

/**
 * Common abstraction for the multi-pitch ("which tones are sounding") stage of the recognition
 * pipeline. Both the existing FFT-based {@code ChordDetector} and the new ML-based {@link
 * BasicPitchProvider} can be exposed behind this interface, which enables a fair A/B baseline
 * comparison (Roadmap Phase 0) without a duplicated road map.
 *
 * <p>Implementations only perform the signal-level multi-pitch estimation. The harmonica-specific
 * interpretation (channels, bends, octaves, splits) is the responsibility of the deterministic
 * {@link HarpPitchMapper} downstream.
 */
public interface MultiPitchDetector extends AutoCloseable {

  /**
   * Estimates the simultaneously sounding pitches in the given audio buffer.
   *
   * @param audioData the mono audio samples (normalised to {@code [-1, 1]})
   * @param sampleRate the sample rate of {@code audioData} in Hz
   * @return the detected note events (may be empty if nothing is detected)
   */
  List<NoteEvent> detect(double[] audioData, double sampleRate);

  /**
   * Returns a short, human-readable name of this detector implementation (e.g. for benchmark
   * reports in the A/B baseline comparison).
   *
   * @return the detector name
   */
  String name();

  /**
   * Releases native resources held by the detector (e.g. an ONNX Runtime session). The default
   * implementation does nothing; detectors without native resources need not override it. Errors
   * are not propagated.
   */
  @Override
  default void close() {
    // nothing to release by default
  }
}
