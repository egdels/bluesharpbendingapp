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
 * A single multi-pitch note event as produced by a {@link MultiPitchDetector} (e.g. the Basic Pitch
 * model frontend). It is the Java pendant to the Python prototype's {@code note_events}.
 *
 * <p>A note event carries a (fractional) MIDI pitch, the time interval over which it sounds and an
 * amplitude/activation in the range {@code [0, 1]}. The amplitude doubles as the per-note
 * confidence for the deterministic Let's-Bend interpretation (mapping/postprocessing).
 *
 * @param start the start time of the event in seconds
 * @param end the end time of the event in seconds
 * @param midi the (possibly fractional) MIDI note number (A4 = 69)
 * @param amplitude the activation/confidence of the event in {@code [0, 1]}
 */
public record NoteEvent(double start, double end, double midi, double amplitude) {

  /**
   * Convenience factory for a note event without explicit timing (start/end = 0), useful for
   * deterministic mapping tests where only the pitch and confidence matter.
   *
   * @param midi the (possibly fractional) MIDI note number
   * @param confidence the per-note confidence in {@code [0, 1]}
   * @return a new {@code NoteEvent}
   */
  public static NoteEvent of(double midi, double confidence) {
    return new NoteEvent(0.0, 0.0, midi, confidence);
  }

  /**
   * Returns the duration of the event in seconds.
   *
   * @return {@code end - start}
   */
  public double duration() {
    return end - start;
  }
}
