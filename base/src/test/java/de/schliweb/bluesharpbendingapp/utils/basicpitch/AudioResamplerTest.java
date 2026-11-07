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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests for {@link AudioResampler}, the 22050 Hz mono pre-processing for Basic Pitch. */
class AudioResamplerTest {

  /** Generates a sine wave of the given frequency, sample rate and duration. */
  private static double[] sine(double freq, double sampleRate, double seconds) {
    int n = (int) Math.round(sampleRate * seconds);
    double[] s = new double[n];
    for (int i = 0; i < n; i++) {
      s[i] = Math.sin(2.0 * Math.PI * freq * i / sampleRate);
    }
    return s;
  }

  /** Counts upward zero crossings, a cheap proxy for the dominant frequency. */
  private static int upwardZeroCrossings(double[] s) {
    int count = 0;
    for (int i = 1; i < s.length; i++) {
      if (s[i - 1] < 0 && s[i] >= 0) {
        count++;
      }
    }
    return count;
  }

  @Test
  void downsamplingHalvesTheLength() {
    double[] in = sine(440, 44100, 1.0);
    double[] out = AudioResampler.resample(in, 44100, 22050);
    assertEquals(22050, out.length, "1 s at 22050 Hz must yield ~22050 samples");
  }

  @Test
  void targetRateConstantIs22050() {
    assertEquals(22050, AudioResampler.TARGET_SAMPLE_RATE);
    double[] out = AudioResampler.resampleTo22050(sine(440, 48000, 0.5), 48000);
    assertEquals(11025, out.length, "0.5 s at 22050 Hz must yield ~11025 samples");
  }

  @Test
  void equalRatesReturnEquivalentCopy() {
    double[] in = sine(440, 22050, 0.2);
    double[] out = AudioResampler.resampleTo22050(in, 22050);
    assertEquals(in.length, out.length);
    org.junit.jupiter.api.Assertions.assertArrayEquals(in, out, 0.0);
    assertTrue(in != out, "must return a defensive copy, not the same instance");
  }

  @Test
  void preservesFrequencyAfterResampling() {
    double freq = 440;
    double[] in = sine(freq, 44100, 1.0);
    double[] out = AudioResampler.resampleTo22050(in, 44100);
    // A 440 Hz tone has ~440 upward zero crossings per second regardless of the sample rate.
    int crossings = upwardZeroCrossings(out);
    assertEquals(freq, crossings, 3.0, "resampling must preserve the tone's frequency");
  }

  @Test
  void upsamplingDoublesTheLength() {
    double[] in = sine(440, 22050, 0.5);
    double[] out = AudioResampler.resample(in, 22050, 44100);
    assertEquals(in.length * 2, out.length, 1);
  }

  @Test
  void emptyInputYieldsEmptyOutput() {
    assertEquals(0, AudioResampler.resampleTo22050(new double[0], 44100).length);
  }

  @Test
  void toMonoAveragesStereoChannels() {
    double[] stereo = {1.0, 0.0, 0.5, 0.5, -1.0, 1.0};
    double[] mono = AudioResampler.toMono(stereo, 2);
    org.junit.jupiter.api.Assertions.assertArrayEquals(new double[] {0.5, 0.5, 0.0}, mono, 1e-12);
  }

  @Test
  void toMonoIsIdentityForSingleChannel() {
    double[] in = {0.1, -0.2, 0.3};
    double[] mono = AudioResampler.toMono(in, 1);
    org.junit.jupiter.api.Assertions.assertArrayEquals(in, mono, 0.0);
    assertTrue(in != mono, "must return a defensive copy");
  }

  @Test
  void rejectsInvalidArguments() {
    assertThrows(IllegalArgumentException.class, () -> AudioResampler.resample(null, 44100, 22050));
    assertThrows(
        IllegalArgumentException.class,
        () -> AudioResampler.resample(new double[] {0.0}, 0, 22050));
    assertThrows(
        IllegalArgumentException.class, () -> AudioResampler.toMono(new double[] {0.0}, 0));
  }
}
