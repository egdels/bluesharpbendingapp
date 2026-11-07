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
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import de.schliweb.bluesharpbendingapp.utils.PitchDetector;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Roadmap Phase-0 A/B baseline tests: the existing {@code ChordDetector} (via {@link
 * ChordDetectorMultiPitchAdapter}) and a Basic Pitch frontend ({@link BasicPitchProvider}) are
 * driven through the very same {@link BaselineComparison} harness on identical audio, and their
 * Precision/Recall/F1 are scored on tone classes and harp channels.
 *
 * <p>The Basic Pitch path is exercised here with a deterministic in-memory model runner (no ONNX
 * dependency) so the comparison scaffolding is fully testable today; once a real {@link
 * BasicPitchModelRunner} (ONNX Runtime) is available it plugs into the identical harness.
 */
class BaselineComparisonTest {

  private static final int SAMPLE_RATE = 44100;

  // C major chord (C4, E4, G4) – the canonical case reused from ChordDetectorTest.
  private static final double C4 = 261.63;
  private static final double E4 = 329.63;
  private static final double G4 = 392.0;

  private BaselineComparison harness;

  @BeforeEach
  void setUp() {
    PitchDetector.setMaxFrequency(PitchDetector.getDefaultMaxFrequency());
    PitchDetector.setMinFrequency(PitchDetector.getDefaultMinFrequency());
    harness = new BaselineComparison();
  }

  @Test
  void chordDetectorReachesPerfectToneClassF1OnCMajor() {
    double[] audio = generateChord(new double[] {C4, E4, G4}, SAMPLE_RATE, 1.0);
    MultiPitchDetector engine = new ChordDetectorMultiPitchAdapter();

    BaselineComparison.Metrics m =
        harness.evaluateToneClasses(engine, audio, SAMPLE_RATE, Set.of("C", "E", "G"));

    System.out.println("[DEBUG_LOG] " + m.format());
    assertEquals(
        1.0, m.f1(), 1e-9, "ChordDetector should perfectly recover the C major tone classes");
  }

  @Test
  void chordDetectorMapsCMajorToBlowTriad() {
    double[] audio = generateChord(new double[] {C4, E4, G4}, SAMPLE_RATE, 1.0);
    MultiPitchDetector engine = new ChordDetectorMultiPitchAdapter();

    BaselineComparison.Metrics m =
        harness.evaluateHarpChannels(engine, audio, SAMPLE_RATE, Set.of("1B", "2B", "3B"));

    System.out.println("[DEBUG_LOG] " + m.format());
    // G4 is ambiguous (3B blow == 2D draw). The mapper now resolves the assignment globally, so it
    // maps the full C major blow triad to 1B,2B,3B instead of letting 2D block 2B (Phase-0 finding
    // resolved by the global, count-maximising conflict resolution in HarpPitchMapper).
    assertEquals(
        1.0, m.f1(), 1e-9, "ChordDetector C major should map cleanly to 1B/2B/3B: " + m.format());
  }

  @Test
  void basicPitchPathRunsThroughTheSameHarness() {
    double[] audio = generateChord(new double[] {C4, E4, G4}, SAMPLE_RATE, 1.0);
    // C4 = MIDI 60, E4 = 64, G4 = 67.
    MultiPitchDetector engine = new BasicPitchProvider(fakeRunner(60, 64, 67));

    BaselineComparison.Metrics tones =
        harness.evaluateToneClasses(engine, audio, SAMPLE_RATE, Set.of("C", "E", "G"));
    BaselineComparison.Metrics channels =
        harness.evaluateHarpChannels(engine, audio, SAMPLE_RATE, Set.of("1B", "2B", "3B"));

    System.out.println("[DEBUG_LOG] " + tones.format());
    System.out.println("[DEBUG_LOG] " + channels.format());
    assertEquals(1.0, tones.f1(), 1e-9, "Basic Pitch frontend should recover the tone classes");
    assertEquals(1.0, channels.f1(), 1e-9, "Basic Pitch frontend should map to 1B/2B/3B");
  }

  @Test
  void abComparisonReportsBothEngines() {
    double[] audio = generateChord(new double[] {C4, E4, G4}, SAMPLE_RATE, 1.0);
    Set<String> groundTruth = Set.of("C", "E", "G");

    BaselineComparison.Metrics chordDetector =
        harness.evaluateToneClasses(
            new ChordDetectorMultiPitchAdapter(), audio, SAMPLE_RATE, groundTruth);
    BaselineComparison.Metrics basicPitch =
        harness.evaluateToneClasses(
            new BasicPitchProvider(fakeRunner(60, 64, 67)), audio, SAMPLE_RATE, groundTruth);

    System.out.println("[DEBUG_LOG] === Phase-0 A/B baseline (tone classes) ===");
    System.out.println("[DEBUG_LOG] " + chordDetector.format());
    System.out.println("[DEBUG_LOG] " + basicPitch.format());

    // Both engines must solve this clean synthetic case; the harness only scores, it does not
    // pick a winner – that decision is the outcome of the full Phase-0 ground-truth set.
    assertTrue(chordDetector.f1() >= 0.9 && basicPitch.f1() >= 0.9);
  }

  /**
   * Returns the bundled {@code nmp.ort} model as a file (copied from the classpath to a temporary
   * file), or {@code null} if it is not on the classpath.
   */
  private static Path locateModel() {
    try (java.io.InputStream in =
        OnnxBasicPitchModelRunner.class.getResourceAsStream(
            OnnxBasicPitchModelRunner.BUNDLED_MODEL_RESOURCE)) {
      if (in == null) {
        return null;
      }
      Path file = Files.createTempFile("nmp", ".ort");
      file.toFile().deleteOnExit();
      Files.copy(in, file, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
      return file;
    } catch (java.io.IOException e) {
      return null;
    }
  }

  /**
   * The real Phase-0 A/B baseline: the existing {@code ChordDetector} and the Basic Pitch frontend
   * backed by the <em>real</em> ONNX model ({@link OnnxBasicPitchModelRunner}) are driven through
   * the same harness on the identical synthetic C major chord. Skips when the model is not locally
   * available (it is not committed) or no ONNX native is present for this platform.
   */
  @Test
  void abComparisonWithRealOnnxModel() throws Exception {
    Path model = locateModel();
    assumeTrue(model != null, "Bundled Basic Pitch ONNX model not on the classpath; skipping");

    // Basic Pitch expects 22050 Hz; the runner resamples internally, so we synthesize at 22050.
    int sampleRate = 22050;
    double[] audio = generateChord(new double[] {C4, E4, G4}, sampleRate, 2.0);
    Set<String> groundTruth = Set.of("C", "E", "G");

    BaselineComparison.Metrics chordDetector =
        harness.evaluateToneClasses(
            new ChordDetectorMultiPitchAdapter(), audio, sampleRate, groundTruth);

    OnnxBasicPitchModelRunner runner;
    try {
      runner = new OnnxBasicPitchModelRunner(model);
    } catch (UnsatisfiedLinkError | NoClassDefFoundError e) {
      assumeTrue(false, "ONNX Runtime native library unavailable on this platform; skipping: " + e);
      return;
    }
    BaselineComparison.Metrics basicPitch;
    try (runner) {
      basicPitch =
          harness.evaluateToneClasses(
              new BasicPitchProvider(runner), audio, sampleRate, groundTruth);
    }

    System.out.println(
        "[DEBUG_LOG] === Phase-0 A/B baseline with REAL ONNX model (tone classes) ===");
    System.out.println("[DEBUG_LOG] " + chordDetector.format());
    System.out.println("[DEBUG_LOG] " + basicPitch.format());

    // The real model must recover the C major tone classes on this clean synthetic chord.
    assertTrue(
        basicPitch.f1() >= 0.9,
        "Real Basic Pitch model should recover C/E/G tone classes: " + basicPitch.format());
  }

  @Test
  void metricsMathIsCorrect() {
    // expected = {C, E, G}; detected = {C, E, B} -> tp=2, fp=1 (B), fn=1 (G).
    BaselineComparison.Metrics m =
        new BaselineComparison.Metrics(
            "test", "tone-classes", Set.of("C", "E", "G"), Set.of("C", "E", "B"), 0L);
    assertEquals(2, m.truePositives());
    assertEquals(1, m.falsePositives());
    assertEquals(1, m.falseNegatives());
    assertEquals(2.0 / 3.0, m.precision(), 1e-9);
    assertEquals(2.0 / 3.0, m.recall(), 1e-9);
    assertEquals(2.0 / 3.0, m.f1(), 1e-9);
  }

  /** Generates a chord as the sum of equal-amplitude sine waves. */
  private static double[] generateChord(double[] freqs, int sampleRate, double durationSeconds) {
    int samples = (int) (sampleRate * durationSeconds);
    double[] audio = new double[samples];
    for (int i = 0; i < samples; i++) {
      double t = (double) i / sampleRate;
      double sum = 0.0;
      for (double f : freqs) {
        sum += Math.sin(2 * Math.PI * f * t);
      }
      // Sum (not average) of equal-amplitude sines, matching the proven ChordDetectorTest setup.
      audio[i] = sum;
    }
    return audio;
  }

  /**
   * A deterministic in-memory {@link BasicPitchModelRunner} that activates the note bins of the
   * given MIDI notes across all frames. Stands in for the real ONNX model so the A/B harness is
   * testable without the model dependency.
   */
  private static BasicPitchModelRunner fakeRunner(int... midis) {
    return (audioData, sampleRate) -> {
      int frames = 50;
      float[][] note = new float[frames][BasicPitchOutput.NUM_NOTES];
      for (int midi : midis) {
        int col = midi - BasicPitchOutput.MIN_MIDI;
        for (int f = 0; f < frames; f++) {
          note[f][col] = 0.9f;
        }
      }
      float[][] onset = new float[frames][BasicPitchOutput.NUM_NOTES];
      float[][] contour = new float[frames][264];
      return new BasicPitchOutput(note, onset, contour, BasicPitchOutput.DEFAULT_FRAMES_PER_SECOND);
    };
  }

  @Test
  void toneClassesStripOctave() {
    Set<String> classes =
        BaselineComparison.toneClasses(
            List.of(NoteEvent.of(60, 1.0), NoteEvent.of(72, 1.0), NoteEvent.of(64, 1.0)));
    // MIDI 60 and 72 are both "C" (different octaves) -> collapsed.
    assertEquals(Set.of("C", "E"), classes);
  }
}
