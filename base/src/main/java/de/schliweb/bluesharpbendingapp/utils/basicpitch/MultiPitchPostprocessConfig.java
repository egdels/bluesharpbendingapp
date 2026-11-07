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
 * Thresholds for the artefact filtering of multi-pitch note events. Centralised in a config object
 * (analogous to {@code ChordDetectorConfig}) so the values can be tuned without code changes. Java
 * pendant to the prototype's {@code PostprocessConfig}.
 *
 * <p>The defaults follow the recommendations derived from the real recordings:
 *
 * <ul>
 *   <li>{@code minMidi = 48} (C3) / {@code maxMidi = 96} (C7) — harmonica range; discards
 *       bass/overtone artefacts such as F1/A1/E2,
 *   <li>{@code minDurationSeconds = 0.15} — removes very short flicker events,
 *   <li>{@code minAmplitude = 0.35} — removes weak, uncertain events.
 * </ul>
 *
 * @param minMidi the lowest accepted MIDI note (inclusive)
 * @param maxMidi the highest accepted MIDI note (inclusive)
 * @param minDurationSeconds the minimum event duration in seconds
 * @param minAmplitude the minimum event amplitude in {@code [0, 1]}
 * @param harmonicAmplitudeRatio the amplitude ratio below which a simultaneous event lying exactly
 *     a harmonic interval (octave, octave+fifth, two octaves) above a stronger event is discarded
 *     as an overtone artefact; {@code 0} disables the harmonic suppression
 */
public record MultiPitchPostprocessConfig(
    int minMidi,
    int maxMidi,
    double minDurationSeconds,
    double minAmplitude,
    double harmonicAmplitudeRatio) {

  /**
   * Convenience constructor keeping the previous four-argument signature; uses {@link
   * #DEFAULT_HARMONIC_AMPLITUDE_RATIO} for the harmonic suppression.
   *
   * @param minMidi the lowest accepted MIDI note (inclusive)
   * @param maxMidi the highest accepted MIDI note (inclusive)
   * @param minDurationSeconds the minimum event duration in seconds
   * @param minAmplitude the minimum event amplitude in {@code [0, 1]}
   */
  public MultiPitchPostprocessConfig(
      int minMidi, int maxMidi, double minDurationSeconds, double minAmplitude) {
    this(minMidi, maxMidi, minDurationSeconds, minAmplitude, DEFAULT_HARMONIC_AMPLITUDE_RATIO);
  }

  /** Default lowest accepted MIDI note (C3). */
  public static final int DEFAULT_MIN_MIDI = 48;

  /** Default highest accepted MIDI note (C7). */
  public static final int DEFAULT_MAX_MIDI = 96;

  /** Default minimum event duration in seconds. */
  public static final double DEFAULT_MIN_DURATION_SECONDS = 0.15;

  /** Default minimum event amplitude. */
  public static final double DEFAULT_MIN_AMPLITUDE = 0.35;

  /**
   * Default amplitude ratio for the harmonic (overtone) suppression: a note exactly an octave (or
   * octave+fifth / two octaves) above a simultaneously detected, stronger note is treated as an
   * overtone artefact of that note if its amplitude is below this fraction of the lower note's
   * amplitude. Genuine octave splits are played with roughly equal strength on both holes and thus
   * stay above this ratio.
   */
  public static final double DEFAULT_HARMONIC_AMPLITUDE_RATIO = 0.8;

  /**
   * Default safety margin (in semitones) added below the lowest and above the highest harp tone
   * when deriving the MIDI range dynamically from a harp table.
   */
  public static final int DEFAULT_MIDI_MARGIN = 3;

  /**
   * Returns the default configuration.
   *
   * @return the default postprocessing configuration
   */
  public static MultiPitchPostprocessConfig defaultConfig() {
    return new MultiPitchPostprocessConfig(
        DEFAULT_MIN_MIDI, DEFAULT_MAX_MIDI, DEFAULT_MIN_DURATION_SECONDS, DEFAULT_MIN_AMPLITUDE);
  }

  /**
   * Returns a configuration whose MIDI range is derived dynamically from the current harp key and
   * tuning (instead of the fixed C3..C7 defaults): {@code minMidi = lowestHarpMidi - margin} and
   * {@code maxMidi = highestHarpMidi + margin}. Duration and amplitude keep the proven defaults.
   *
   * @param mapper the key/tuning-aware mapper supplying the harp tone range
   * @param margin the safety margin in semitones added on both ends (use {@link
   *     #DEFAULT_MIDI_MARGIN})
   * @return a postprocessing configuration with a harp-derived MIDI range
   */
  public static MultiPitchPostprocessConfig forHarp(HarpPitchMapper mapper, int margin) {
    int min = mapper.minTableMidi() - margin;
    int max = mapper.maxTableMidi() + margin;
    return new MultiPitchPostprocessConfig(
        min, max, DEFAULT_MIN_DURATION_SECONDS, DEFAULT_MIN_AMPLITUDE);
  }
}
