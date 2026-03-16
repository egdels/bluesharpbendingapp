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

/**
 * Provides frequency smoothing using an Exponential Moving Average (EMA) algorithm to reduce
 * flickering in the pitch display. Raw pitch detection values from microphone input naturally
 * fluctuate slightly between frames, even for a stable tone. This smoother dampens those
 * micro-variations to produce a more continuous and visually stable frequency output.
 *
 * <p>The smoothing factor (alpha) controls the trade-off between responsiveness and stability:
 * lower values produce smoother output but with more latency, while higher values are more
 * responsive but less smooth.
 *
 * <p>When no pitch is detected (frequency &lt;= 0), the smoother maintains the last known good
 * frequency for a configurable number of frames before resetting. This prevents brief detection
 * gaps from causing the display to flicker off and on.
 */
public class FrequencySmoother {

  /**
   * The smoothing factor for the Exponential Moving Average. A value of 0.3 provides a good balance
   * between responsiveness to actual pitch changes and stability against micro-fluctuations.
   */
  private static final double ALPHA = 0.3;

  /**
   * The maximum number of consecutive frames with no detected pitch before the smoother resets.
   * This allows the display to persist briefly through short detection gaps without holding stale
   * values indefinitely.
   */
  private static final int MAX_HOLD_FRAMES = 8;

  /** The current smoothed frequency value. A value of -1 indicates no valid frequency. */
  private double smoothedFrequency = -1;

  /** Counter tracking the number of consecutive frames with no detected pitch. */
  private int noSignalCount = 0;

  /**
   * Applies exponential moving average smoothing to the given frequency value.
   *
   * <p>Behavior:
   *
   * <ul>
   *   <li>If the frequency is valid (&gt; 0) and no previous smoothed value exists, the frequency
   *       is accepted directly as the initial value.
   *   <li>If a valid frequency is provided and a smoothed value exists, the new smoothed value is
   *       calculated as: {@code smoothed = alpha * frequency + (1 - alpha) * smoothed}.
   *   <li>If the frequency is invalid (&lt;= 0) and the hold frame limit has not been reached, the
   *       last smoothed value is returned to bridge short detection gaps.
   *   <li>If the frequency is invalid and the hold frame limit is exceeded, the smoother resets and
   *       returns -1.
   * </ul>
   *
   * @param frequency the raw detected frequency in Hz, or a value &lt;= 0 if no pitch was detected
   * @return the smoothed frequency in Hz, or -1 if no valid frequency is available
   */
  public double smooth(double frequency) {
    if (frequency > 0) {
      noSignalCount = 0;
      if (smoothedFrequency <= 0) {
        smoothedFrequency = frequency;
      } else {
        smoothedFrequency = ALPHA * frequency + (1 - ALPHA) * smoothedFrequency;
      }
      return smoothedFrequency;
    }

    // No pitch detected
    noSignalCount++;
    if (noSignalCount <= MAX_HOLD_FRAMES && smoothedFrequency > 0) {
      // Hold the last known frequency for a few frames to bridge short gaps
      return smoothedFrequency;
    }

    // Too many frames without signal, reset
    smoothedFrequency = -1;
    return -1;
  }

  /** Resets the smoother to its initial state, clearing any stored frequency and signal count. */
  public void reset() {
    smoothedFrequency = -1;
    noSignalCount = 0;
  }
}
