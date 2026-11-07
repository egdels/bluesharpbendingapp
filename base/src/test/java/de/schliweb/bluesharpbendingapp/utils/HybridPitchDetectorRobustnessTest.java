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
package de.schliweb.bluesharpbendingapp.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.schliweb.bluesharpbendingapp.benchmark.SyntheticHarmonicaSignals;
import de.schliweb.bluesharpbendingapp.benchmark.SyntheticHarmonicaSignals.ToneModel;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Regression tests for two Hybrid detector errors found with the pitch benchmark: the FFT branch
 * reported the 2nd harmonic of tones with a weak fundamental (octave error) and found pitches in
 * pure breath noise.
 */
class HybridPitchDetectorRobustnessTest {

  private static final int SR = SyntheticHarmonicaSignals.SAMPLE_RATE;

  @BeforeEach
  void setUp() {
    PitchDetector.setMinFrequency(80);
    PitchDetector.setMaxFrequency(3500);
  }

  @AfterEach
  void tearDown() {
    PitchDetector.setMinFrequency(PitchDetector.getDefaultMinFrequency());
    PitchDetector.setMaxFrequency(PitchDetector.getDefaultMaxFrequency());
  }

  @ParameterizedTest
  @ValueSource(doubles = {175, 220, 294, 440, 466, 880})
  void weakFundamentalIsNotReportedAnOctaveHigher(double frequency) {
    double[] tone =
        SyntheticHarmonicaSignals.tone(
            SyntheticHarmonicaSignals.steady(frequency), ToneModel.WEAK_FUNDAMENTAL, 0.6, 7);
    for (int frameSize : new int[] {1024, 4410}) {
      for (int start = 1323; start + frameSize <= tone.length; start += frameSize) {
        double[] frame = Arrays.copyOfRange(tone, start, start + frameSize);
        double pitch = PitchDetector.detectPitchHybrid(frame, SR).pitch();
        if (pitch > 0) {
          assertEquals(
              0,
              SyntheticHarmonicaSignals.cents(pitch, frequency),
              50,
              "frame "
                  + frameSize
                  + " at "
                  + start
                  + ": "
                  + pitch
                  + " Hz for "
                  + frequency
                  + " Hz");
        }
      }
    }
  }

  @ParameterizedTest
  @ValueSource(longs = {5000, 5001, 5002, 5003})
  void breathNoiseHasNoPitch(long seed) {
    double[] noise = SyntheticHarmonicaSignals.breathNoiseOnly(0.6, seed);
    for (int frameSize : new int[] {1024, 4410}) {
      for (int start = 1323; start + frameSize <= noise.length; start += frameSize) {
        double[] frame = Arrays.copyOfRange(noise, start, start + frameSize);
        assertEquals(
            PitchDetector.NO_DETECTED_PITCH,
            PitchDetector.detectPitchHybrid(frame, SR).pitch(),
            "frame " + frameSize + " at " + start);
      }
    }
  }
}
