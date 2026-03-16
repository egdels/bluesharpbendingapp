package de.schliweb.bluesharpbendingapp.controller;

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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link FrequencySmoother} class, verifying EMA smoothing behavior, hold-frame
 * bridging of detection gaps, and reset functionality.
 */
class FrequencySmootherTest {

  private FrequencySmoother smoother;

  @BeforeEach
  void setUp() {
    smoother = new FrequencySmoother();
  }

  @Test
  void firstValidFrequencyIsAcceptedDirectly() {
    double result = smoother.smooth(440.0);
    assertEquals(440.0, result, 0.001);
  }

  @Test
  void subsequentFrequenciesAreSmoothed() {
    smoother.smooth(440.0);
    double result = smoother.smooth(450.0);
    // EMA: 0.3 * 450 + 0.7 * 440 = 135 + 308 = 443
    assertEquals(443.0, result, 0.001);
  }

  @Test
  void smoothingConvergesToStableFrequency() {
    smoother.smooth(440.0);
    double result = 440.0;
    for (int i = 0; i < 50; i++) {
      result = smoother.smooth(440.0);
    }
    assertEquals(440.0, result, 0.01);
  }

  @Test
  void negativeFrequencyReturnsHeldValueForShortGaps() {
    smoother.smooth(440.0);
    // Short gap: should hold the last known frequency
    double result = smoother.smooth(-1);
    assertEquals(440.0, result, 0.001);
  }

  @Test
  void negativeFrequencyResetsAfterMaxHoldFrames() {
    smoother.smooth(440.0);
    double result = -1;
    // Exceed MAX_HOLD_FRAMES (8)
    for (int i = 0; i < 10; i++) {
      result = smoother.smooth(-1);
    }
    assertEquals(-1, result, 0.001);
  }

  @Test
  void holdFramesBridgeShortDetectionGaps() {
    smoother.smooth(440.0);
    // 3 frames without signal
    smoother.smooth(-1);
    smoother.smooth(-1);
    smoother.smooth(-1);
    // Signal returns - should still have a valid smoothed value
    double result = smoother.smooth(442.0);
    assertTrue(result > 0);
  }

  @Test
  void resetClearsState() {
    smoother.smooth(440.0);
    smoother.reset();
    double result = smoother.smooth(-1);
    assertEquals(-1, result, 0.001);
  }

  @Test
  void resetAllowsFreshStart() {
    smoother.smooth(440.0);
    smoother.smooth(450.0);
    smoother.reset();
    // After reset, first value should be accepted directly
    double result = smoother.smooth(500.0);
    assertEquals(500.0, result, 0.001);
  }

  @Test
  void noValidFrequencyReturnsNegativeOne() {
    double result = smoother.smooth(-1);
    assertEquals(-1, result, 0.001);
  }

  @Test
  void zeroFrequencyIsTreatedAsInvalid() {
    double result = smoother.smooth(0);
    assertEquals(-1, result, 0.001);
  }

  @Test
  void smoothingReducesMicroFluctuations() {
    smoother.smooth(440.0);
    // Simulate micro-fluctuations around 440 Hz
    double[] fluctuations = {441.0, 439.0, 440.5, 439.5, 441.5, 438.5, 440.0};
    double maxDeviation = 0;
    for (double freq : fluctuations) {
      double smoothed = smoother.smooth(freq);
      maxDeviation = Math.max(maxDeviation, Math.abs(smoothed - 440.0));
    }
    // Smoothed output should have less deviation than raw input (max raw deviation is 1.5)
    assertTrue(maxDeviation < 1.5, "Smoothed deviation should be less than raw deviation");
  }
}
