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

/*
 * MIT License
 *
 * Copyright (c) 2025 Christian Kierdorf
 */

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Regression tests for bounds handling in PitchDetector.findFirstMinimum to avoid
 * ArrayIndexOutOfBoundsException when callers pass out-of-range indices.
 */
public class PitchDetectorBoundsTest {

  @Test
  public void testFindFirstMinimumClampsWhenMinIndexAtArrayLength() {
    double[] arr = new double[512];
    // Create a simple valley at index 100
    for (int i = 0; i < arr.length; i++) arr[i] = 1.0;
    arr[99] = 0.8;
    arr[100] = 0.1; // local minimum
    arr[101] = 0.8;
    // Pass minIndex equal to array length to simulate buggy caller input
    int idx = invokeFindFirstMinimum(arr, 0.5, 512, 1024);
    // With out-of-range minIndex, the function clamps to a safe range and should not crash; no
    // minimum in that range
    assertEquals(-1, idx);
  }

  @Test
  public void testFindFirstMinimumReturnsMinusOneWhenNoMinimumInRange() {
    double[] arr = new double[16];
    for (int i = 0; i < arr.length; i++) arr[i] = 1.0;
    int idx = invokeFindFirstMinimum(arr, 0.5, 0, 16);
    assertEquals(-1, idx);
  }

  @Test
  public void testFindFirstMinimumHandlesSmallArrays() {
    assertEquals(-1, invokeFindFirstMinimum(new double[] {}, 0.5, 0, 10));
    assertEquals(-1, invokeFindFirstMinimum(new double[] {1}, 0.5, 0, 1));
    assertEquals(-1, invokeFindFirstMinimum(new double[] {1, 0}, 0.5, 0, 2));
  }

  // Helper to access protected static method in same package
  private static int invokeFindFirstMinimum(
      double[] array, double threshold, int minIndex, int maxIndex) {
    return PitchDetector.findFirstMinimum(array, threshold, minIndex, maxIndex);
  }
}
