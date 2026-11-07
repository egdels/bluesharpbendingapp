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
 * The raw frame-level output of the Basic Pitch model, as produced by a {@link
 * BasicPitchModelRunner}. Mirrors the three tensors of the prototype ({@code note}/{@code
 * onset}/{@code contour}).
 *
 * <p>{@code note} and {@code onset} have the shape {@code [frames][88]} (MIDI notes 21..108),
 * {@code contour} has the shape {@code [frames][264]}. Each value is an activation in {@code [0,
 * 1]}.
 *
 * <p>This is a thin data holder over the model tensors. It is intentionally a regular class (not a
 * record) because its components are arrays.
 */
public final class BasicPitchOutput {

  /** Lowest MIDI note covered by the model's note/onset matrices (column 0). */
  public static final int MIN_MIDI = 21;

  /** Number of note/onset columns (MIDI 21..108). */
  public static final int NUM_NOTES = 88;

  /** Default frame rate of the ICASSP-2022 model. */
  public static final double DEFAULT_FRAMES_PER_SECOND = 86.1328125;

  private final float[][] note;
  private final float[][] onset;
  private final float[][] contour;
  private final double framesPerSecond;

  /**
   * Creates a new model output holder.
   *
   * @param note the per-frame note activations {@code [frames][88]}
   * @param onset the per-frame onset activations {@code [frames][88]}
   * @param contour the per-frame pitch contour activations {@code [frames][264]}
   * @param framesPerSecond the frame rate (Basic Pitch ICASSP-2022 model: ~86 fps)
   */
  public BasicPitchOutput(
      float[][] note, float[][] onset, float[][] contour, double framesPerSecond) {
    this.note = note;
    this.onset = onset;
    this.contour = contour;
    this.framesPerSecond = framesPerSecond;
  }

  /**
   * Returns the per-frame note activations {@code [frames][88]}.
   *
   * @return the note matrix
   */
  public float[][] note() {
    return note;
  }

  /**
   * Returns the per-frame onset activations {@code [frames][88]}.
   *
   * @return the onset matrix
   */
  public float[][] onset() {
    return onset;
  }

  /**
   * Returns the per-frame pitch contour activations {@code [frames][264]}.
   *
   * @return the contour matrix
   */
  public float[][] contour() {
    return contour;
  }

  /**
   * Returns the frame rate in frames per second.
   *
   * @return the frame rate
   */
  public double framesPerSecond() {
    return framesPerSecond;
  }
}
