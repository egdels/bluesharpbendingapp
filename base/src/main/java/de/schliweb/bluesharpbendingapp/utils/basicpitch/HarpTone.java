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
 * A single playable tone of a harmonica, identified by channel (hole), playing direction and bend
 * level. This is the Java pendant to the Python prototype's {@code HarpTone}.
 *
 * @param channel the harmonica channel/hole (1..10 for a Richter harp)
 * @param direction the playing direction (blow or draw)
 * @param bend the bend level (0 = unbent, 1.. = number of semitones bent down)
 * @param midi the MIDI note number this tone produces
 */
public record HarpTone(int channel, Direction direction, int bend, int midi) {

  /** Playing direction of a harmonica tone. */
  public enum Direction {
    /** Exhaling into the harmonica. */
    BLOW,
    /** Inhaling on the harmonica. */
    DRAW
  }

  /** Pitch class names indexed by {@code midi % 12} (B = 11). */
  private static final String[] NOTE_NAMES = {
    "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
  };

  /**
   * Converts a MIDI note number to a readable note name (e.g. 60 -&gt; {@code C4}).
   *
   * @param midi the MIDI note number
   * @return the note name
   */
  public static String midiToName(int midi) {
    return NOTE_NAMES[Math.floorMod(midi, 12)] + (Math.floorDiv(midi, 12) - 1);
  }

  /**
   * Converts a MIDI note number to a frequency in Hz (A4 = 440 Hz).
   *
   * @param midi the (possibly fractional) MIDI note number
   * @return the frequency in Hz
   */
  public static double midiToFreq(double midi) {
    return 440.0 * Math.pow(2.0, (midi - 69) / 12.0);
  }

  /**
   * Converts a frequency in Hz to a (fractional) MIDI note number (A4 = 440 Hz).
   *
   * @param freq the frequency in Hz
   * @return the fractional MIDI note number
   */
  public static double freqToMidi(double freq) {
    return 69.0 + 12.0 * (Math.log(freq / 440.0) / Math.log(2.0));
  }

  /**
   * Returns the short label of this tone, e.g. {@code 4B}, {@code 2D}, {@code 3D'} (one bend).
   *
   * @return the short label
   */
  public String label() {
    String d = direction == Direction.BLOW ? "B" : "D";
    return channel + d + "'".repeat(Math.max(0, bend));
  }

  /**
   * Returns the readable note name of this tone (e.g. {@code C4}).
   *
   * @return the note name
   */
  public String name() {
    return midiToName(midi);
  }

  /**
   * Returns the frequency of this tone in Hz.
   *
   * @return the frequency in Hz
   */
  public double freq() {
    return midiToFreq(midi);
  }
}
