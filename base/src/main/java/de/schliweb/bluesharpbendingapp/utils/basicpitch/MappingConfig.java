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
 * Configuration for the {@link HarpPitchMapper}. The thresholds are centralised here (analogous to
 * {@code ChordDetectorConfig}) so they can be tuned without code changes.
 *
 * @param toleranceCents the maximum allowed deviation in cents between a detected pitch and a known
 *     harp tone (a detection further off than this is rejected)
 * @param includeBends whether the harp tone table includes the bend tones between the two reeds of
 *     each channel
 */
public record MappingConfig(double toleranceCents, boolean includeBends) {

  /** Default tolerance in cents (100 cents = one semitone). */
  public static final double DEFAULT_TOLERANCE_CENTS = 35.0;

  /**
   * Returns a default configuration ({@link #DEFAULT_TOLERANCE_CENTS} cents tolerance, bends
   * included).
   *
   * @return the default mapping configuration
   */
  public static MappingConfig defaultConfig() {
    return new MappingConfig(DEFAULT_TOLERANCE_CENTS, true);
  }
}
