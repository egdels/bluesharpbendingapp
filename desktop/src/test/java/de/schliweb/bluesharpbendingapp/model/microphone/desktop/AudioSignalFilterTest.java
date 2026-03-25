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
package de.schliweb.bluesharpbendingapp.model.microphone.desktop;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AudioSignalFilterTest {

  private AudioSignalFilter filter;

  @BeforeEach
  void setUp() {
    filter = new AudioSignalFilter();
  }

  @Test
  void testNoiseGateBelowThreshold() {
    assertTrue(filter.isBelowNoiseGate(0.0001));
    assertTrue(filter.isBelowNoiseGate(0.0));
    assertTrue(filter.isBelowNoiseGate(0.0009));
  }

  @Test
  void testNoiseGateAboveThreshold() {
    assertFalse(filter.isBelowNoiseGate(0.001));
    assertFalse(filter.isBelowNoiseGate(0.01));
    assertFalse(filter.isBelowNoiseGate(1.0));
  }

  @Test
  void testHighPassFilterRemovesDC() {
    double[] dcSignal = new double[1000];
    java.util.Arrays.fill(dcSignal, 0.5);
    filter.applyHighPassFilter(dcSignal);

    // After high-pass filtering, the DC component should be significantly reduced
    // Check the last samples (after filter has settled)
    double sum = 0;
    for (int i = 500; i < 1000; i++) {
      sum += Math.abs(dcSignal[i]);
    }
    double avgAmplitude = sum / 500;
    assertTrue(avgAmplitude < 0.01, "DC component should be removed, avg=" + avgAmplitude);
  }

  @Test
  void testHighPassFilterPreservesHighFrequency() {
    // Generate a 440 Hz sine wave at 44100 Hz sample rate
    double[] sineWave = new double[4410]; // 0.1 seconds
    for (int i = 0; i < sineWave.length; i++) {
      sineWave[i] = Math.sin(2 * Math.PI * 440 * i / 44100.0);
    }
    double originalRms = calcRms(sineWave);

    filter.applyHighPassFilter(sineWave);
    double filteredRms = calcRms(sineWave);

    // 440 Hz should pass through with minimal attenuation (>80%)
    assertTrue(
        filteredRms > originalRms * 0.8,
        "440 Hz should pass through, ratio=" + filteredRms / originalRms);
  }

  @Test
  void testNoiseProfileCalibration() {
    assertFalse(filter.isNoiseProfileReady());

    double[] frame = new double[100];
    java.util.Arrays.fill(frame, 0.001);

    // Feed 10 frames to calibrate
    for (int i = 0; i < 10; i++) {
      filter.applyNoiseReduction(frame);
    }

    assertTrue(filter.isNoiseProfileReady());
  }

  @Test
  void testNoiseReductionReducesNoise() {
    double[] noiseFrame = new double[100];
    java.util.Arrays.fill(noiseFrame, 0.001);

    // Calibrate with noise
    for (int i = 0; i < 10; i++) {
      filter.applyNoiseReduction(noiseFrame.clone());
    }

    // Now apply to a similar noise frame
    double[] testFrame = new double[100];
    java.util.Arrays.fill(testFrame, 0.001);
    double beforeRms = calcRms(testFrame);

    filter.applyNoiseReduction(testFrame);
    double afterRms = calcRms(testFrame);

    // Noise should be significantly reduced
    assertTrue(
        afterRms < beforeRms * 0.5, "Noise should be reduced, ratio=" + afterRms / beforeRms);
  }

  @Test
  void testNoiseReductionPreservesSignal() {
    double[] noiseFrame = new double[100];
    java.util.Arrays.fill(noiseFrame, 0.001);

    // Calibrate with quiet noise
    for (int i = 0; i < 10; i++) {
      filter.applyNoiseReduction(noiseFrame.clone());
    }

    // Apply to a loud signal
    double[] signalFrame = new double[100];
    java.util.Arrays.fill(signalFrame, 0.5);
    double beforeRms = calcRms(signalFrame);

    filter.applyNoiseReduction(signalFrame);
    double afterRms = calcRms(signalFrame);

    // Loud signal should be mostly preserved (>95%)
    assertTrue(
        afterRms > beforeRms * 0.95, "Signal should be preserved, ratio=" + afterRms / beforeRms);
  }

  @Test
  void testReset() {
    double[] frame = new double[100];
    java.util.Arrays.fill(frame, 0.001);

    for (int i = 0; i < 10; i++) {
      filter.applyNoiseReduction(frame.clone());
    }
    assertTrue(filter.isNoiseProfileReady());

    filter.reset();
    assertFalse(filter.isNoiseProfileReady());
  }

  @Test
  void testHighPassFilterStateReset() {
    double[] data = {0.5, 0.5, 0.5};
    filter.applyHighPassFilter(data);

    filter.reset();

    // After reset, filter should behave as if freshly created
    double[] data2 = {0.5, 0.5, 0.5};
    AudioSignalFilter fresh = new AudioSignalFilter();
    double[] data3 = {0.5, 0.5, 0.5};
    fresh.applyHighPassFilter(data3);

    filter.applyHighPassFilter(data2);
    assertArrayEquals(data3, data2, 1e-10);
  }

  private double calcRms(double[] data) {
    double sum = 0;
    for (double v : data) {
      sum += v * v;
    }
    return Math.sqrt(sum / data.length);
  }
}
