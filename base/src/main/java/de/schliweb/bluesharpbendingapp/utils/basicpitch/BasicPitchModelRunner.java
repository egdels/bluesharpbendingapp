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

/**
 * Platform-specific inference of the Basic Pitch model. The runner only performs the model forward
 * pass (resampling to 22050 Hz mono and feeding the network); it returns the raw frame tensors
 * ({@link BasicPitchOutput}). The conversion of those tensors into {@link NoteEvent}s and the
 * harmonica-specific interpretation live in the platform-independent {@code base} code ({@link
 * BasicPitchProvider}, {@link HarpPitchMapper}).
 *
 * <p>Concrete implementations are platform specific (see the integration concept):
 *
 * <ul>
 *   <li>Desktop/JVM and Android: ONNX Runtime (Java / Mobile) with {@code nmp.onnx},
 *   <li>Web: TensorFlow.js / ONNX-Web (WASM).
 * </ul>
 *
 * For the Phase-0 Java A/B baseline a desktop/JVM runner is sufficient.
 */
public interface BasicPitchModelRunner {

  /**
   * Runs the model on the given audio buffer and returns the raw frame tensors.
   *
   * @param audioData the mono audio samples (normalised to {@code [-1, 1]})
   * @param sampleRate the sample rate of {@code audioData} in Hz (resampled internally to 22050 Hz)
   * @return the raw model output
   */
  BasicPitchOutput run(double[] audioData, double sampleRate);
}
