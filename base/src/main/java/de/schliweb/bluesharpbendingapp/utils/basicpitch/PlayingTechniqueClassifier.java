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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Deterministically classifies the playing technique behind a set of simultaneously active
 * harmonica channels (as produced by {@link HarpPitchMapper}/{@link MultiPitchEngine}). This
 * implements the technique classification sketched in §6.3 of the integration concept (chord /
 * octave / split / tongue blocking) without any fixed Richter-C assumption: the decision is derived
 * purely from the channels, playing directions and MIDI distances of the active tones, so it works
 * for every harp key and tuning.
 *
 * <p>The classification is a pure function of the active channels and therefore fully
 * unit-testable. It never removes octaves or splits; it only labels them.
 */
public final class PlayingTechniqueClassifier {

  /** The recognised playing technique. */
  public enum Technique {
    /** No active channel. */
    SILENCE,
    /** Exactly one active channel. */
    SINGLE_NOTE,
    /** Two adjacent channels in the same direction (a double stop). */
    INTERVAL,
    /** Two channels exactly one octave (12 semitones) apart. */
    OCTAVE,
    /**
     * Two (or more) non-adjacent channels in the same direction with at least one skipped hole in
     * between, i.e. a tongue-blocked split that is not a plain octave.
     */
    SPLIT,
    /** Three or more contiguous channels in the same direction sounding together. */
    CHORD,
    /** A combination that does not match any of the simpler categories. */
    COMPLEX
  }

  /**
   * The outcome of a classification.
   *
   * @param technique the recognised technique
   * @param tongueBlocked whether the technique requires tongue blocking (split/octave with a gap)
   * @param labels the labels of the active channels involved (e.g. {@code [1B, 4B]})
   */
  public record Classification(Technique technique, boolean tongueBlocked, List<String> labels) {}

  private PlayingTechniqueClassifier() {
    // Utility class.
  }

  /**
   * Classifies the playing technique for the active channels of a mapping result.
   *
   * @param result the mapping result; {@code null} is treated as silence
   * @return the classification
   */
  public static Classification classify(MappingResult result) {
    if (result == null) {
      return new Classification(Technique.SILENCE, false, List.of());
    }
    return classify(result.active());
  }

  /**
   * Classifies the playing technique for a list of active channels.
   *
   * @param active the active channels; {@code null} or empty is treated as silence
   * @return the classification
   */
  public static Classification classify(List<ActiveChannel> active) {
    if (active == null || active.isEmpty()) {
      return new Classification(Technique.SILENCE, false, List.of());
    }

    List<ActiveChannel> sorted = new ArrayList<>(active);
    sorted.sort(
        Comparator.comparingInt((ActiveChannel a) -> a.tone().channel())
            .thenComparingInt(a -> a.tone().midi()));

    List<String> labels = new ArrayList<>();
    for (ActiveChannel a : sorted) {
      labels.add(a.label());
    }

    // Enharmonic alternatives (e.g. G4 = 2D ≡ 3B) are reported as separate active channels by the
    // mapper so both channels light up, but they represent the same sounding note. For the
    // technique decision only one representative per MIDI note is considered; the labels still
    // list every active channel.
    sorted = dedupeByMidi(sorted);

    if (sorted.size() == 1) {
      return new Classification(Technique.SINGLE_NOTE, false, labels);
    }

    boolean sameDirection = allSameDirection(sorted);

    if (sorted.size() == 2) {
      HarpTone a = sorted.get(0).tone();
      HarpTone b = sorted.get(1).tone();
      int channelGap = Math.abs(a.channel() - b.channel());
      int midiGap = Math.abs(a.midi() - b.midi());

      if (sameDirection && midiGap == 12) {
        // True octave; on a harmonica this requires tongue blocking (holes in between are blocked).
        return new Classification(Technique.OCTAVE, channelGap > 1, labels);
      }
      if (sameDirection && channelGap == 1) {
        return new Classification(Technique.INTERVAL, false, labels);
      }
      if (sameDirection && channelGap > 1) {
        return new Classification(Technique.SPLIT, true, labels);
      }
      return new Classification(Technique.COMPLEX, false, labels);
    }

    // Three or more active channels.
    if (sameDirection && isContiguous(sorted)) {
      return new Classification(Technique.CHORD, false, labels);
    }
    if (sameDirection && hasOctavePair(sorted)) {
      // e.g. an octave played together with an inner note -> still a tongue-blocked split family.
      return new Classification(Technique.SPLIT, true, labels);
    }
    if (sameDirection) {
      return new Classification(Technique.SPLIT, true, labels);
    }
    return new Classification(Technique.COMPLEX, false, labels);
  }

  private static List<ActiveChannel> dedupeByMidi(List<ActiveChannel> sorted) {
    List<ActiveChannel> deduped = new ArrayList<>();
    Set<Integer> seenMidis = new HashSet<>();
    for (ActiveChannel a : sorted) {
      if (seenMidis.add(a.tone().midi())) {
        deduped.add(a);
      }
    }
    return deduped;
  }

  private static boolean allSameDirection(List<ActiveChannel> sorted) {
    HarpTone.Direction first = sorted.get(0).tone().direction();
    for (ActiveChannel a : sorted) {
      if (a.tone().direction() != first) {
        return false;
      }
    }
    return true;
  }

  private static boolean isContiguous(List<ActiveChannel> sorted) {
    for (int i = 1; i < sorted.size(); i++) {
      if (sorted.get(i).tone().channel() - sorted.get(i - 1).tone().channel() != 1) {
        return false;
      }
    }
    return true;
  }

  private static boolean hasOctavePair(List<ActiveChannel> sorted) {
    for (int i = 0; i < sorted.size(); i++) {
      for (int j = i + 1; j < sorted.size(); j++) {
        if (Math.abs(sorted.get(i).tone().midi() - sorted.get(j).tone().midi()) == 12) {
          return true;
        }
      }
    }
    return false;
  }
}
