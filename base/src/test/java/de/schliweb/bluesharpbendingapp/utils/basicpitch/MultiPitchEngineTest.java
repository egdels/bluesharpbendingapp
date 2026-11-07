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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica.KEY;
import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica.TUNE;
import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Tests for the key/tuning-aware multi-pitch integration: the {@link HarpPitchMapper#forHarmonica}
 * mapping for different harp keys and tunings, the dynamically derived MIDI bounds and the {@link
 * MultiPitchEngine} orchestration including the transparent fallback to the {@code ChordDetector}.
 */
class MultiPitchEngineTest {

  private static List<String> mapBlow(Harmonica h, int... channels) {
    HarpPitchMapper mapper = HarpPitchMapper.forHarmonica(h, MappingConfig.defaultConfig());
    double[] freqs = new double[channels.length];
    for (int i = 0; i < channels.length; i++) {
      freqs[i] = h.getNoteFrequency(channels[i], 0);
    }
    return mapper.mapFrequencies(0.0, freqs).labels();
  }

  private static List<String> mapDraw(Harmonica h, int... channels) {
    HarpPitchMapper mapper = HarpPitchMapper.forHarmonica(h, MappingConfig.defaultConfig());
    double[] freqs = new double[channels.length];
    for (int i = 0; i < channels.length; i++) {
      freqs[i] = h.getNoteFrequency(channels[i], 1);
    }
    return mapper.mapFrequencies(0.0, freqs).labels();
  }

  /**
   * Expected labels for a single blow note on the given channel: the blow tone itself plus every
   * enharmonic duplicate of the same MIDI note in the harp table (e.g. Richter G4 = 2D ≡ 3B), in
   * channel order – the mapper shows all channels that produce the same sounding note.
   */
  private static List<String> expectedBlowLabels(Harmonica h, int channel) {
    List<HarpTone> table = HarpPitchMapper.buildTableForHarmonica(h, true);
    int midi = (int) Math.round(HarpTone.freqToMidi(h.getNoteFrequency(channel, 0)));
    return table.stream()
        .filter(t -> t.midi() == midi)
        .sorted(java.util.Comparator.comparingInt(HarpTone::channel))
        .map(HarpTone::label)
        .toList();
  }

  // ------------------------------------------------------------------------- //
  // 1. Mapping is key aware (C, A and a third key) and tuning aware.
  // ------------------------------------------------------------------------- //

  @ParameterizedTest
  @EnumSource(
      value = KEY.class,
      names = {"C", "A", "D", "G", "F"})
  void singleBlowNotesMapAcrossKeys(KEY key) {
    Harmonica h = AbstractHarmonica.create(key, TUNE.RICHTER);
    for (int ch = 1; ch <= 10; ch++) {
      assertEquals(expectedBlowLabels(h, ch), mapBlow(h, ch), "key=" + key + " channel=" + ch);
    }
  }

  @ParameterizedTest
  @EnumSource(value = TUNE.class)
  void singleBlowNotesMapAcrossTunings(TUNE tune) {
    Harmonica h = AbstractHarmonica.create(KEY.C, tune);
    for (int ch = 1; ch <= 10; ch++) {
      assertEquals(expectedBlowLabels(h, ch), mapBlow(h, ch), "tune=" + tune + " channel=" + ch);
    }
  }

  // ------------------------------------------------------------------------- //
  // 2. Real octave splits must be preserved (1B+4B and 1D+4D), independent of key.
  // ------------------------------------------------------------------------- //

  @ParameterizedTest
  @EnumSource(
      value = KEY.class,
      names = {"C", "A", "D"})
  void octaveSplitsArePreserved(KEY key) {
    Harmonica h = AbstractHarmonica.create(key, TUNE.RICHTER);
    assertEquals(List.of("1B", "4B"), mapBlow(h, 1, 4), "blow octave split, key=" + key);
    assertEquals(List.of("1D", "4D"), mapDraw(h, 1, 4), "draw octave split, key=" + key);
  }

  // ------------------------------------------------------------------------- //
  // 3. Dynamic MIDI bounds derived from the current harp key/tuning.
  // ------------------------------------------------------------------------- //

  @Test
  void dynamicMidiBoundsFollowHarpTable() {
    Harmonica c = AbstractHarmonica.create(KEY.C, TUNE.RICHTER);
    HarpPitchMapper mapper = HarpPitchMapper.forHarmonica(c, MappingConfig.defaultConfig());
    MultiPitchPostprocessConfig cfg =
        MultiPitchPostprocessConfig.forHarp(
            mapper, MultiPitchPostprocessConfig.DEFAULT_MIDI_MARGIN);
    assertEquals(
        mapper.minTableMidi() - MultiPitchPostprocessConfig.DEFAULT_MIDI_MARGIN, cfg.minMidi());
    assertEquals(
        mapper.maxTableMidi() + MultiPitchPostprocessConfig.DEFAULT_MIDI_MARGIN, cfg.maxMidi());
  }

  @Test
  void lowerKeyHasLowerDynamicBounds() {
    HarpPitchMapper cMapper =
        HarpPitchMapper.forHarmonica(AbstractHarmonica.create(KEY.C, TUNE.RICHTER), null);
    HarpPitchMapper aMapper =
        HarpPitchMapper.forHarmonica(AbstractHarmonica.create(KEY.A, TUNE.RICHTER), null);
    // A harp (A3 base) sounds lower than C harp (C4 base), so its dynamic range starts lower.
    assertTrue(aMapper.minTableMidi() < cMapper.minTableMidi());
  }

  // ------------------------------------------------------------------------- //
  // 4. MultiPitchEngine: default engine, octave splits end-to-end, debug info.
  // ------------------------------------------------------------------------- //

  @Test
  void defaultEngineIsChordDetector() {
    MultiPitchEngine engine = MultiPitchEngine.createDefault();
    assertEquals(MultiPitchEngine.Engine.CHORD_DETECTOR, engine.selectedEngine());
  }

  @Test
  void engineWithFakeDetectorMapsOctaveSplit() {
    Harmonica c = AbstractHarmonica.create(KEY.C, TUNE.RICHTER);
    double c4 = c.getNoteFrequency(1, 0);
    double c5 = c.getNoteFrequency(4, 0);
    MultiPitchDetector fake =
        new MultiPitchDetector() {
          @Override
          public List<NoteEvent> detect(double[] audioData, double sampleRate) {
            return List.of(
                new NoteEvent(0.0, 0.3, HarpTone.freqToMidi(c4), 0.9),
                new NoteEvent(0.0, 0.3, HarpTone.freqToMidi(c5), 0.9));
          }

          @Override
          public String name() {
            return "Fake";
          }
        };
    MultiPitchEngine engine =
        new MultiPitchEngine(MultiPitchEngine.Engine.BASIC_PITCH, fake, c, true);
    MultiPitchEngine.EngineResult result = engine.process(new double[256], 22050, 0.0);
    assertFalse(result.fellBack());
    assertEquals(MultiPitchEngine.Engine.BASIC_PITCH, result.engineUsed());
    assertEquals(List.of("1B", "4B"), result.mapping().labels());
  }

  @Test
  void engineFallsBackWhenDetectorThrows() {
    Harmonica c = AbstractHarmonica.create(KEY.C, TUNE.RICHTER);
    MultiPitchDetector broken =
        new MultiPitchDetector() {
          @Override
          public List<NoteEvent> detect(double[] audioData, double sampleRate) {
            throw new IllegalStateException("model boom");
          }

          @Override
          public String name() {
            return "Broken";
          }
        };
    MultiPitchEngine engine =
        new MultiPitchEngine(MultiPitchEngine.Engine.BASIC_PITCH, broken, c, false);
    // A real-ish (silent) buffer; the fallback ChordDetector must not throw.
    MultiPitchEngine.EngineResult result = engine.process(new double[1024], 22050, 0.0);
    assertTrue(result.fellBack());
    assertEquals(MultiPitchEngine.Engine.CHORD_DETECTOR, result.engineUsed());
    assertNotNull(result.mapping());
  }

  @Test
  void basicPitchProviderInitialisationNeverCrashes() {
    // Either Basic Pitch loads (bundled model + ONNX native present) or it falls back gracefully.
    MultiPitchEngine engine =
        MultiPitchEngine.createBasicPitch(AbstractHarmonica.create(KEY.C, TUNE.RICHTER));
    assertNotNull(engine);
    assertNotNull(engine.mapper());
    assertNotNull(engine.postprocessConfig());
  }

  /**
   * Regression test: the ChordDetector engine (the fallback when Basic Pitch is unavailable) must
   * map a chord to harp channels. Its events used to have zero duration and were all dropped by the
   * post-processor's minimum-duration filter, so nothing was detected.
   */
  @Test
  void chordDetectorEngineMapsCMajorBlowChordToChannels1To3() {
    int sampleRate = 44100;
    double[] audio = new double[sampleRate];
    double[] freqs = {261.63, 329.63, 392.00};
    for (int i = 0; i < audio.length; i++) {
      double t = (double) i / sampleRate;
      for (double f : freqs) {
        for (int k = 1; k <= 6; k++) {
          audio[i] += Math.sin(2 * Math.PI * f * k * t) / k / 6;
        }
      }
    }
    MultiPitchEngine engine = MultiPitchEngine.createDefault();
    engine.setHarmonica(AbstractHarmonica.create(KEY.C, TUNE.RICHTER));

    MultiPitchEngine.EngineResult result = engine.process(audio, sampleRate, 0);

    List<String> labels = result.mapping().labels();
    assertTrue(
        labels.containsAll(List.of("1B", "2B", "3B")),
        "expected channels 1-3 blow, got " + labels + ", filtered " + result.filteredEvents());
  }
}
