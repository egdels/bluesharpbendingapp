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
 * Deterministic audio pre-processing for the Basic Pitch model: resamples an arbitrary mono signal
 * to the model's required sample rate of {@value #TARGET_SAMPLE_RATE} Hz.
 *
 * <p>Every platform-specific {@link BasicPitchModelRunner} (Desktop/ONNX, Android/ONNX, Web) has to
 * feed the network with 22050 Hz mono samples (Basic Pitch ICASSP-2022 specification). The
 * microphone front-ends of Let's Bend, however, capture at higher, platform-dependent rates (e.g.
 * 44100/48000 Hz). This helper isolates that conversion so it can be reused and unit-tested
 * independently of the actual ML inference (which needs an external runtime and the bundled model).
 *
 * <p>The implementation uses linear interpolation, which is sufficient for a multi-pitch frontend:
 * Basic Pitch is robust against the minor high-frequency roll-off, and the alternative
 * (poly-phase/FIR resampling) would add complexity without a measurable benefit for note detection.
 */
public final class AudioResampler {

  /** Sample rate the Basic Pitch model expects (mono), in Hz. */
  public static final int TARGET_SAMPLE_RATE = 22050;

  private AudioResampler() {}

  /**
   * Resamples the given mono signal to {@value #TARGET_SAMPLE_RATE} Hz.
   *
   * @param audioData the mono samples (typically normalised to {@code [-1, 1]})
   * @param sourceSampleRate the sample rate of {@code audioData} in Hz
   * @return the resampled signal at {@value #TARGET_SAMPLE_RATE} Hz
   * @throws IllegalArgumentException if {@code audioData} is {@code null} or {@code
   *     sourceSampleRate} is not positive
   */
  public static double[] resampleTo22050(double[] audioData, double sourceSampleRate) {
    return resample(audioData, sourceSampleRate, TARGET_SAMPLE_RATE);
  }

  /**
   * Resamples the given mono signal from {@code sourceSampleRate} to {@code targetSampleRate} using
   * linear interpolation.
   *
   * @param audioData the mono input samples
   * @param sourceSampleRate the input sample rate in Hz
   * @param targetSampleRate the desired output sample rate in Hz
   * @return the resampled signal; the same array (defensively copied) is returned when source and
   *     target rates are equal
   * @throws IllegalArgumentException if {@code audioData} is {@code null} or a sample rate is not
   *     positive
   */
  public static double[] resample(
      double[] audioData, double sourceSampleRate, double targetSampleRate) {
    if (audioData == null) {
      throw new IllegalArgumentException("audioData must not be null");
    }
    if (sourceSampleRate <= 0 || targetSampleRate <= 0) {
      throw new IllegalArgumentException("sample rates must be positive");
    }
    if (sourceSampleRate == targetSampleRate) {
      return audioData.clone();
    }
    if (audioData.length == 0) {
      return new double[0];
    }
    if (audioData.length == 1) {
      // A single sample carries no time information to interpolate; replicate it.
      double targetLengthSingle = Math.max(1, Math.round(targetSampleRate / sourceSampleRate));
      double[] out = new double[(int) targetLengthSingle];
      java.util.Arrays.fill(out, audioData[0]);
      return out;
    }

    double ratio = targetSampleRate / sourceSampleRate;
    int outLength = (int) Math.round(audioData.length * ratio);
    if (outLength <= 0) {
      return new double[0];
    }
    double[] out = new double[outLength];
    // Map output index -> fractional input index (source step per output sample).
    double step = sourceSampleRate / targetSampleRate;
    for (int i = 0; i < outLength; i++) {
      double srcPos = i * step;
      int idx = (int) Math.floor(srcPos);
      double frac = srcPos - idx;
      if (idx >= audioData.length - 1) {
        out[i] = audioData[audioData.length - 1];
      } else {
        out[i] = audioData[idx] * (1.0 - frac) + audioData[idx + 1] * frac;
      }
    }
    return out;
  }

  /**
   * Downmixes interleaved multi-channel audio to mono by averaging the channels.
   *
   * @param interleaved the interleaved samples ({@code [c0_0, c1_0, ..., c0_1, c1_1, ...]})
   * @param channels the number of channels ({@code >= 1})
   * @return the mono signal; the input (defensively copied) when {@code channels == 1}
   * @throws IllegalArgumentException if {@code interleaved} is {@code null} or {@code channels < 1}
   */
  public static double[] toMono(double[] interleaved, int channels) {
    if (interleaved == null) {
      throw new IllegalArgumentException("interleaved must not be null");
    }
    if (channels < 1) {
      throw new IllegalArgumentException("channels must be >= 1");
    }
    if (channels == 1) {
      return interleaved.clone();
    }
    int frames = interleaved.length / channels;
    double[] mono = new double[frames];
    for (int f = 0; f < frames; f++) {
      double sum = 0.0;
      int base = f * channels;
      for (int c = 0; c < channels; c++) {
        sum += interleaved[base + c];
      }
      mono[f] = sum / channels;
    }
    return mono;
  }
}
