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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.schliweb.bluesharpbendingapp.utils.basicpitch.HarpTone.Direction;
import de.schliweb.bluesharpbendingapp.utils.basicpitch.MultiPitchPostprocessor.FilterResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * Deterministic tests for the postprocessing stage ({@link MultiPitchPostprocessor}), ported from
 * the prototype's {@code test_postprocess.py}. They reproduce the artefacts described in the issue
 * (very low bass events such as F1/A1/E2/C1/E1, short and weak events) and verify they are
 * discarded, while the real harp tones are kept. They also test the temporal smoothing.
 */
class MultiPitchPostprocessorTest {

  private static NoteEvent ev(double start, double end, double midi, double amp) {
    return new NoteEvent(start, end, midi, amp);
  }

  private static Set<Double> keptMidis(FilterResult result) {
    return result.kept().stream().map(NoteEvent::midi).collect(Collectors.toSet());
  }

  // ------------------------------------------------------------------------- //
  // filterNoteEvents
  // ------------------------------------------------------------------------- //

  @Test
  void lowMidiBassArtifactsRemoved() {
    List<NoteEvent> events =
        List.of(
            ev(0.0, 0.3, 29, 0.43), // F1
            ev(0.0, 0.3, 33, 0.35), // A1
            ev(0.0, 0.3, 40, 0.66), // E2
            ev(0.0, 0.3, 24, 0.32), // C1
            ev(0.0, 0.3, 28, 0.30), // E1
            ev(0.0, 0.6, 61, 0.65)); // C#4 -> real tone
    FilterResult result = MultiPitchPostprocessor.filterNoteEvents(events, null);
    assertEquals(Set.of(61.0), keptMidis(result));
    assertEquals(5, result.rejected().size());
  }

  @Test
  void highMidiArtifactsRemoved() {
    List<NoteEvent> events = List.of(ev(0.0, 0.5, 100, 0.9), ev(0.0, 0.5, 64, 0.9));
    FilterResult result = MultiPitchPostprocessor.filterNoteEvents(events, null);
    assertEquals(Set.of(64.0), keptMidis(result));
  }

  @Test
  void shortEventsRemoved() {
    List<NoteEvent> events = List.of(ev(0.0, 0.05, 64, 0.9), ev(0.0, 0.5, 64, 0.9));
    FilterResult result = MultiPitchPostprocessor.filterNoteEvents(events, null);
    assertEquals(1, result.kept().size());
    assertTrue(result.kept().get(0).duration() >= 0.15);
  }

  @Test
  void weakEventsRemoved() {
    List<NoteEvent> events = List.of(ev(0.0, 0.5, 64, 0.2), ev(0.0, 0.5, 64, 0.5));
    FilterResult result = MultiPitchPostprocessor.filterNoteEvents(events, null);
    assertEquals(1, result.kept().size());
    assertTrue(result.kept().get(0).amplitude() >= 0.35);
  }

  @Test
  void realChordSurvivesFilter() {
    List<NoteEvent> events =
        List.of(
            ev(2.32, 5.03, 61, 0.65), // C#4
            ev(2.38, 4.89, 64, 0.60), // E4
            ev(6.11, 10.84, 68, 0.65)); // G#4
    FilterResult result = MultiPitchPostprocessor.filterNoteEvents(events, null);
    assertEquals(3, result.kept().size());
    assertTrue(result.rejected().isEmpty());
  }

  @Test
  void configIsTunable() {
    List<NoteEvent> events = List.of(ev(0.0, 0.3, 40, 0.66)); // E2
    assertTrue(
        MultiPitchPostprocessor.filterNoteEvents(
                events, new MultiPitchPostprocessConfig(48, 96, 0.15, 0.35))
            .kept()
            .isEmpty());
    assertEquals(
        1,
        MultiPitchPostprocessor.filterNoteEvents(
                events, new MultiPitchPostprocessConfig(36, 96, 0.15, 0.35))
            .kept()
            .size());
  }

  // ------------------------------------------------------------------------- //
  // harmonic overtone suppression
  // ------------------------------------------------------------------------- //

  @Test
  void weakOctaveOvertoneRemoved() {
    // Issue: playing 5 draw (F5, MIDI 77) also lit 9 draw (F6, MIDI 89) — Basic Pitch reports the
    // octave harmonic of the played note as a second, weaker event. It must be discarded.
    List<NoteEvent> events =
        List.of(
            ev(0.0, 0.5, 77, 0.9), // F5 = 5D (played)
            ev(0.0, 0.5, 89, 0.5)); // F6 = 9D (overtone artefact)
    FilterResult result = MultiPitchPostprocessor.filterNoteEvents(events, null);
    assertEquals(Set.of(77.0), keptMidis(result));
    assertEquals(1, result.rejected().size());
    assertTrue(result.rejected().get(0).reason().contains("harmonic overtone"));
  }

  @Test
  void weakOctavePlusFifthOvertoneRemoved() {
    // 3rd harmonic: an octave plus a fifth (19 semitones) above the fundamental.
    List<NoteEvent> events = List.of(ev(0.0, 0.5, 60, 0.9), ev(0.0, 0.5, 79, 0.4));
    FilterResult result = MultiPitchPostprocessor.filterNoteEvents(events, null);
    assertEquals(Set.of(60.0), keptMidis(result));
  }

  @Test
  void genuineOctaveSplitWithComparableAmplitudeKept() {
    // A real octave split (e.g. 1B + 4B) is played with roughly equal strength on both holes and
    // must not be swallowed by the overtone suppression.
    List<NoteEvent> events = List.of(ev(0.0, 0.5, 60, 0.8), ev(0.0, 0.5, 72, 0.75));
    FilterResult result = MultiPitchPostprocessor.filterNoteEvents(events, null);
    assertEquals(Set.of(60.0, 72.0), keptMidis(result));
    assertTrue(result.rejected().isEmpty());
  }

  @Test
  void harmonicSuppressionCanBeDisabled() {
    List<NoteEvent> events = List.of(ev(0.0, 0.5, 77, 0.9), ev(0.0, 0.5, 89, 0.5));
    FilterResult result =
        MultiPitchPostprocessor.filterNoteEvents(
            events, new MultiPitchPostprocessConfig(48, 96, 0.15, 0.35, 0.0));
    assertEquals(Set.of(77.0, 89.0), keptMidis(result));
  }

  // ------------------------------------------------------------------------- //
  // smoothMappingResults
  // ------------------------------------------------------------------------- //

  /** Helper: builds a mapping result whose active channels carry the given labels. */
  private static MappingResult mr(double timeSeconds, String... labels) {
    List<ActiveChannel> active = new ArrayList<>();
    for (String lbl : labels) {
      int ch = Integer.parseInt(lbl.substring(0, lbl.length() - 1));
      // Build a tone that reproduces the label (blow, no bend, channel ch).
      active.add(new ActiveChannel(new HarpTone(ch, Direction.BLOW, 0, 60), 1.0, 0.0));
    }
    return new MappingResult(timeSeconds, active);
  }

  private static List<List<String>> labelSeq(List<MappingResult> results) {
    return results.stream().map(MappingResult::labels).collect(Collectors.toList());
  }

  @Test
  void smoothingRemovesSingleFrameOutlier() {
    List<MappingResult> seq =
        List.of(
            mr(0.0, "1B"),
            mr(0.25, "1B"),
            mr(0.5, "4B"), // outlier (1 frame)
            mr(0.75, "1B"),
            mr(1.0, "1B"));
    List<MappingResult> smoothed = MultiPitchPostprocessor.smoothMappingResults(seq, 2);
    assertEquals(List.of(List.of("1B")), labelSeq(smoothed));
  }

  @Test
  void smoothingKeepsStableChange() {
    List<MappingResult> seq = List.of(mr(0.0, "1B"), mr(0.25, "1B"), mr(0.5, "4B"), mr(0.75, "4B"));
    List<MappingResult> smoothed = MultiPitchPostprocessor.smoothMappingResults(seq, 2);
    assertEquals(List.of(List.of("1B"), List.of("4B")), labelSeq(smoothed));
  }

  @Test
  void smoothingEmptyInput() {
    assertTrue(MultiPitchPostprocessor.smoothMappingResults(List.of(), 2).isEmpty());
  }

  @Test
  void smoothingMinRunOneJustCondenses() {
    List<MappingResult> seq = List.of(mr(0.0, "1B"), mr(0.25, "1B"), mr(0.5, "4B"));
    List<MappingResult> smoothed = MultiPitchPostprocessor.smoothMappingResults(seq, 1);
    assertEquals(List.of(List.of("1B"), List.of("4B")), labelSeq(smoothed));
  }
}
