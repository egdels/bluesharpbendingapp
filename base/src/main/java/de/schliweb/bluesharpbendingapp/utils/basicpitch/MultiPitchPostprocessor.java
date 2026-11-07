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
import java.util.List;

/**
 * Postprocessing stage for multi-pitch note events. Java port of the prototype's {@code
 * postprocess.py}.
 *
 * <p>Background: Basic Pitch produces usable polyphony on real recordings, but also artefacts —
 * especially in the bass range (very low MIDI notes such as F1/A1/E2/C1/E1), very short events and
 * weak events. These are filtered out early, before the harp mapping, following the architecture
 * "Basic Pitch provides candidates, Let's Bend decides".
 *
 * <p>The stage implements the steps requested by the issue:
 *
 * <ol>
 *   <li>remove low/high MIDI artefacts ({@code minMidi} / {@code maxMidi}),
 *   <li>remove short events ({@code minDurationSeconds}),
 *   <li>remove weak events ({@code minAmplitude}),
 *   <li>remove harmonic overtone artefacts (a weak note exactly an octave / octave+fifth / two
 *       octaves above a stronger simultaneous note, {@code harmonicAmplitudeRatio}),
 *   <li>(downstream in the mapping) only allow harp tones and resolve conflicts,
 *   <li>smooth over time ({@link #smoothMappingResults(List, int)}).
 * </ol>
 */
public final class MultiPitchPostprocessor {

  private MultiPitchPostprocessor() {
    // utility class
  }

  /** A rejected note event together with the reason it was discarded. */
  public record RejectedEvent(NoteEvent event, String reason) {}

  /**
   * The result of {@link #filterNoteEvents(List, MultiPitchPostprocessConfig)}: the kept events and
   * the rejected events with reasons.
   *
   * @param kept the events that passed all filters
   * @param rejected the events that were discarded
   */
  public record FilterResult(List<NoteEvent> kept, List<RejectedEvent> rejected) {}

  /**
   * Filters raw note events by MIDI range, duration and amplitude.
   *
   * @param noteEvents the raw note events
   * @param config the thresholds (a {@code null} value uses {@link
   *     MultiPitchPostprocessConfig#defaultConfig()})
   * @return the filter result with kept and rejected events
   */
  public static FilterResult filterNoteEvents(
      List<NoteEvent> noteEvents, MultiPitchPostprocessConfig config) {
    MultiPitchPostprocessConfig cfg =
        config != null ? config : MultiPitchPostprocessConfig.defaultConfig();
    List<NoteEvent> kept = new ArrayList<>();
    List<RejectedEvent> rejected = new ArrayList<>();
    for (NoteEvent ev : noteEvents) {
      double midi = ev.midi();
      double duration = ev.duration();
      if (midi < cfg.minMidi()) {
        rejected.add(
            new RejectedEvent(ev, "MIDI " + Math.round(midi) + " < minMidi " + cfg.minMidi()));
        continue;
      }
      if (midi > cfg.maxMidi()) {
        rejected.add(
            new RejectedEvent(ev, "MIDI " + Math.round(midi) + " > maxMidi " + cfg.maxMidi()));
        continue;
      }
      if (duration < cfg.minDurationSeconds()) {
        rejected.add(
            new RejectedEvent(
                ev, "duration " + duration + "s < minDuration " + cfg.minDurationSeconds() + "s"));
        continue;
      }
      if (ev.amplitude() < cfg.minAmplitude()) {
        rejected.add(
            new RejectedEvent(
                ev, "amplitude " + ev.amplitude() + " < minAmplitude " + cfg.minAmplitude()));
        continue;
      }
      kept.add(ev);
    }
    suppressHarmonicOvertones(kept, rejected, cfg.harmonicAmplitudeRatio());
    return new FilterResult(kept, rejected);
  }

  /**
   * Harmonic intervals (in semitones) at which overtones of a fundamental appear: octave (2nd
   * harmonic), octave+fifth (3rd harmonic) and two octaves (4th harmonic).
   */
  private static final int[] HARMONIC_INTERVALS = {12, 19, 24};

  /** Tolerance in semitones when matching a note against a harmonic interval. */
  private static final double HARMONIC_TOLERANCE_SEMITONES = 0.5;

  /**
   * Removes overtone artefacts: a note lying exactly a harmonic interval (octave, octave+fifth, two
   * octaves) above a stronger, simultaneously detected note is very likely the overtone of that
   * note (e.g. Basic Pitch reporting F6 while only F5/5-draw is played) and is discarded when its
   * amplitude is below {@code ratio} times the fundamental's amplitude. Genuine octave splits are
   * played with roughly equal strength on both holes, keep a comparable amplitude and therefore
   * survive this filter.
   *
   * @param kept the events that passed the basic filters (modified in place)
   * @param rejected the rejected events (appended to)
   * @param ratio the amplitude ratio threshold; values {@code <= 0} disable the suppression
   */
  private static void suppressHarmonicOvertones(
      List<NoteEvent> kept, List<RejectedEvent> rejected, double ratio) {
    if (ratio <= 0 || kept.size() < 2) {
      return;
    }
    List<NoteEvent> overtones = new ArrayList<>();
    for (NoteEvent ev : kept) {
      NoteEvent fundamental = findFundamental(kept, ev, ratio);
      if (fundamental != null) {
        overtones.add(ev);
        rejected.add(
            new RejectedEvent(
                ev,
                "harmonic overtone of MIDI "
                    + Math.round(fundamental.midi())
                    + " (amplitude "
                    + ev.amplitude()
                    + " < "
                    + ratio
                    + " * "
                    + fundamental.amplitude()
                    + ")"));
      }
    }
    kept.removeAll(overtones);
  }

  /**
   * Returns a stronger event whose pitch lies a harmonic interval below {@code ev}, or {@code null}
   * if {@code ev} is not an overtone candidate.
   */
  private static NoteEvent findFundamental(List<NoteEvent> kept, NoteEvent ev, double ratio) {
    for (NoteEvent other : kept) {
      if (other.equals(ev)) {
        continue;
      }
      double interval = ev.midi() - other.midi();
      for (int harmonic : HARMONIC_INTERVALS) {
        if (Math.abs(interval - harmonic) <= HARMONIC_TOLERANCE_SEMITONES
            && ev.amplitude() < ratio * other.amplitude()) {
          return other;
        }
      }
    }
    return null;
  }

  /**
   * Temporal smoothing of the mapped results (against flickering).
   *
   * <p>A simple run-length criterion is applied over the uncondensed sequence of time steps: a
   * channel assignment is only kept if it is stable over at least {@code minRun} consecutive time
   * steps. Shorter "outliers" are replaced by the last stable assignment. Expects the raw (not
   * condensed) result list, i.e. one entry per time step; consecutive identical assignments are
   * condensed afterwards.
   *
   * @param results one mapping result per time step
   * @param minRun the minimum run length (number of frames); values &lt;= 1 just condense
   * @return the smoothed, condensed result list
   */
  public static List<MappingResult> smoothMappingResults(List<MappingResult> results, int minRun) {
    if (results == null || results.isEmpty()) {
      return new ArrayList<>();
    }
    if (minRun <= 1) {
      return condense(results);
    }

    List<List<String>> labelsSeq = new ArrayList<>();
    for (MappingResult r : results) {
      labelsSeq.add(r.labels());
    }

    List<MappingResult> smoothed = new ArrayList<>();
    List<String> lastStable = new ArrayList<>();

    int i = 0;
    int n = labelsSeq.size();
    while (i < n) {
      int j = i;
      while (j < n && labelsSeq.get(j).equals(labelsSeq.get(i))) {
        j++;
      }
      int runLen = j - i;
      if (runLen >= minRun || labelsSeq.get(i).isEmpty()) {
        // Stable run (or silence) -> keep.
        for (int k = i; k < j; k++) {
          smoothed.add(results.get(k));
        }
        lastStable = labelsSeq.get(i);
      } else {
        // Too short an outlier -> replace by the last stable assignment.
        for (int k = i; k < j; k++) {
          smoothed.add(resultWithLabels(results.get(k), lastStable, results));
        }
      }
      i = j;
    }
    return condense(smoothed);
  }

  /** Builds a mapping result with the time of {@code template} but the channels {@code labels}. */
  private static MappingResult resultWithLabels(
      MappingResult template, List<String> labels, List<MappingResult> allResults) {
    if (labels.isEmpty()) {
      return new MappingResult(template.timeSeconds());
    }
    // Reuse an earlier result with exactly these labels (incl. ActiveChannel/confidence).
    for (MappingResult r : allResults) {
      if (r.labels().equals(labels)) {
        return new MappingResult(template.timeSeconds(), r.active());
      }
    }
    return new MappingResult(template.timeSeconds());
  }

  /** Condenses consecutive identical channel assignments into a single event. */
  private static List<MappingResult> condense(List<MappingResult> results) {
    List<MappingResult> condensed = new ArrayList<>();
    List<String> lastLabels = null;
    for (MappingResult r : results) {
      List<String> labels = r.labels();
      if (!labels.equals(lastLabels)) {
        condensed.add(r);
        lastLabels = labels;
      }
    }
    return condensed;
  }
}
