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
package de.schliweb.bluesharpbendingapp.benchmark;

import static de.schliweb.bluesharpbendingapp.benchmark.SyntheticHarmonicaSignals.SAMPLE_RATE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.schliweb.bluesharpbendingapp.benchmark.SyntheticHarmonicaSignals.ToneModel;
import de.schliweb.bluesharpbendingapp.benchmark.SyntheticHarmonicaSignals.Trajectory;
import org.junit.jupiter.api.Test;

/** Checks that the benchmark signals really have the ground truth they claim. */
class SyntheticHarmonicaSignalsTest {

  @Test
  void sineHasRequestedFrequency() {
    double[] s =
        SyntheticHarmonicaSignals.tone(
            SyntheticHarmonicaSignals.steady(440), ToneModel.SINE, 1.0, 1);
    int crossings = 0;
    for (int i = 1; i < s.length; i++) {
      if (s[i - 1] < 0 && s[i] >= 0) {
        crossings++;
      }
    }
    assertEquals(440, crossings, 1);
  }

  @Test
  void bendTrajectoryReachesDepth() {
    Trajectory bend = SyntheticHarmonicaSignals.bend(400, 100, 0.3, 0.6);
    assertEquals(400, bend.frequencyAt(0.1), 1e-9);
    assertEquals(-50, SyntheticHarmonicaSignals.cents(bend.frequencyAt(0.6), 400), 1e-6);
    assertEquals(-100, SyntheticHarmonicaSignals.cents(bend.frequencyAt(1.0), 400), 1e-6);
  }

  @Test
  void breathNoiseHasRequestedSnr() {
    double[] clean =
        SyntheticHarmonicaSignals.tone(
            SyntheticHarmonicaSignals.steady(300), ToneModel.HARMONICA, 1.0, 2);
    double[] noisy = SyntheticHarmonicaSignals.withBreathNoise(clean, 10, 3);
    double[] noise = new double[clean.length];
    for (int i = 0; i < noise.length; i++) {
      noise[i] = noisy[i] - clean[i];
    }
    double snr =
        20
            * Math.log10(
                SyntheticHarmonicaSignals.rms(clean) / SyntheticHarmonicaSignals.rms(noise));
    assertEquals(10, snr, 0.01);
  }

  @Test
  void signalsAreDeterministic() {
    double[] a =
        SyntheticHarmonicaSignals.tone(
            SyntheticHarmonicaSignals.steady(200), ToneModel.HARMONICA, 0.2, 7);
    double[] b =
        SyntheticHarmonicaSignals.tone(
            SyntheticHarmonicaSignals.steady(200), ToneModel.HARMONICA, 0.2, 7);
    assertArrayEquals(a, b);
    assertEquals(SAMPLE_RATE / 5, a.length);
  }
}
