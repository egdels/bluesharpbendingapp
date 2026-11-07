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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Deterministic tests for the Phase-2 mapping ({@link HarpPitchMapper}), ported from the
 * prototype's {@code test_harp_mapping.py}. They require neither Basic Pitch nor an audio model;
 * they verify the frequency/MIDI table, the channel mapping, the plausibility rules and the output
 * format.
 */
class HarpPitchMapperTest {

  private final HarpPitchMapper mapper = new HarpPitchMapper();

  private static List<String> labels(MappingResult result) {
    return result.labels();
  }

  // ------------------------------------------------------------------------- //
  // 1. Frequency/MIDI table
  // ------------------------------------------------------------------------- //

  @Test
  void tableBaseNotesMatchJavaModel() {
    List<HarpTone> table = HarpPitchMapper.buildRichterCTable(false);
    Map<Integer, Integer> blow =
        table.stream()
            .filter(t -> t.direction() == Direction.BLOW && t.bend() == 0)
            .collect(Collectors.toMap(HarpTone::channel, HarpTone::midi));
    Map<Integer, Integer> draw =
        table.stream()
            .filter(t -> t.direction() == Direction.DRAW && t.bend() == 0)
            .collect(Collectors.toMap(HarpTone::channel, HarpTone::midi));
    for (int ch = 1; ch <= 10; ch++) {
      assertEquals(HarpPitchMapper.KEY_C_BASE_MIDI + HarpPitchMapper.halfToneOut(ch), blow.get(ch));
      assertEquals(HarpPitchMapper.KEY_C_BASE_MIDI + HarpPitchMapper.halfToneIn(ch), draw.get(ch));
    }
  }

  @Test
  void tableKnownReferenceNotes() {
    Map<String, HarpTone> byLabel =
        HarpPitchMapper.buildRichterCTable(true).stream()
            .collect(Collectors.toMap(HarpTone::label, t -> t, (a, b) -> a));
    assertEquals("C4", byLabel.get("1B").name());
    assertEquals("D4", byLabel.get("1D").name());
    assertEquals("C5", byLabel.get("4B").name());
    assertEquals("G4", byLabel.get("2D").name());
    assertEquals("C7", byLabel.get("10B").name());
  }

  @Test
  void tableHasExpectedBends() {
    Map<String, HarpTone> byLabel =
        HarpPitchMapper.buildRichterCTable(true).stream()
            .collect(Collectors.toMap(HarpTone::label, t -> t, (a, b) -> a));
    assertEquals("C#4", byLabel.get("1D'").name());
    assertEquals("F4", byLabel.get("2D''").name());
    assertEquals("G#4", byLabel.get("3D'''").name());
  }

  @Test
  void tableWithoutBendsHas20Tones() {
    assertEquals(20, HarpPitchMapper.buildRichterCTable(false).size());
  }

  // ------------------------------------------------------------------------- //
  // 2. Mapping of single notes
  // ------------------------------------------------------------------------- //

  @ParameterizedTest
  @CsvSource({"60,1B", "62,1D", "64,2B", "72,4B", "96,10B"})
  void singleNoteMapping(int midi, String expected) {
    assertEquals(List.of(expected), labels(mapper.mapMidiNotes(0.0, midi)));
  }

  @Test
  void singleNoteViaFrequency() {
    assertEquals(List.of("4B"), labels(mapper.mapFrequencies(0.0, 523.25)));
  }

  @Test
  void ambiguousSingleNoteActivatesBothChannels() {
    // G4 (MIDI 67) exists twice on a Richter C harp: 2 draw and 3 blow. It is impossible to tell
    // from the audio which channel the player uses, so both must be shown as active.
    assertEquals(List.of("2D", "3B"), labels(mapper.mapMidiNotes(0.0, 67)));
  }

  // ------------------------------------------------------------------------- //
  // 3. Chords (matches RichterHarmonicaChordTest cases)
  // ------------------------------------------------------------------------- //

  @Test
  void blowChordHoles123() {
    assertEquals(List.of("1B", "2B", "3B"), labels(mapper.mapMidiNotes(0.42, 60, 64, 67)));
  }

  @Test
  void blowChordHoles456() {
    assertEquals(List.of("4B", "5B", "6B"), labels(mapper.mapMidiNotes(0.0, 72, 76, 79)));
  }

  @Test
  void drawChordHoles123() {
    assertEquals(List.of("1D", "2D", "3D"), labels(mapper.mapMidiNotes(0.0, 62, 67, 71)));
  }

  @Test
  void twoNoteBlowSplit() {
    assertEquals(List.of("4B", "6B"), labels(mapper.mapMidiNotes(0.0, 72, 79)));
  }

  // ------------------------------------------------------------------------- //
  // 4. Plausibility rules
  // ------------------------------------------------------------------------- //

  @Test
  void onlyKnownHarpTonesAllowed() {
    MappingResult result = mapper.mapMidiNotes(0.0, 97);
    assertTrue(labels(result).isEmpty());
    assertEquals(1, result.rejected().size());
  }

  @Test
  void unknownNoteFilteredKeepsValid() {
    MappingResult result = mapper.mapMidiNotes(0.0, 60, 97);
    assertEquals(List.of("1B"), labels(result));
    assertEquals(1, result.rejected().size());
  }

  @Test
  void noSimultaneousBlowAndDrawSameChannel() {
    MappingResult result = mapper.mapMidiNotes(0.0, 64, 67);
    List<String> lbls = labels(result);
    assertTrue(lbls.contains("2B"));
    assertTrue(lbls.contains("3B"));
    List<Integer> channels =
        result.active().stream().map(a -> a.tone().channel()).collect(Collectors.toList());
    Set<Integer> unique = new HashSet<>(channels);
    assertEquals(channels.size(), unique.size());
  }

  @Test
  void toleranceCentsAcceptsSlightlyDetuned() {
    double detuned = HarpTone.midiToFreq(60 + 0.20); // 20 cents too high
    assertEquals(List.of("1B"), labels(mapper.mapFrequencies(0.0, detuned)));
  }

  @Test
  void toleranceCentsRejectsWhenTooStrict() {
    HarpPitchMapper strict =
        new HarpPitchMapper(HarpPitchMapper.KEY_C_BASE_MIDI, new MappingConfig(5.0, true));
    double detuned = HarpTone.midiToFreq(60 + 0.20);
    assertTrue(labels(strict.mapFrequencies(0.0, detuned)).isEmpty());
  }

  @Test
  void confidencePropagatedAndAveraged() {
    MappingResult result =
        mapper.mapNotes(List.of(NoteEvent.of(60, 0.8), NoteEvent.of(64, 0.6)), 1.0);
    assertEquals(List.of("1B", "2B"), labels(result));
    assertEquals(0.7, result.confidence(), 1e-9);
  }

  // ------------------------------------------------------------------------- //
  // 5. Output format
  // ------------------------------------------------------------------------- //

  @Test
  void outputFormatMatchesSpec() {
    MappingResult result = mapper.mapMidiNotes(0.42, 72, 76, 79);
    assertEquals("time=0.42s  active=[4B,5B,6B]  confidence=1.00", result.format());
  }

  @Test
  void outputFormatEmpty() {
    MappingResult result = mapper.mapMidiNotes(1.5, 97);
    assertEquals("time=1.50s  active=[]  confidence=0.00", result.format());
  }
}
