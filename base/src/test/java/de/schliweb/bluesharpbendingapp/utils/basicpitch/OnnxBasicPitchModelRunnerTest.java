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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link OnnxBasicPitchModelRunner}. The constant/argument tests run everywhere; the
 * actual inference test only runs when the bundled {@code nmp.onnx} model is locally available
 * (e.g. the project's {@code venv}); otherwise it is skipped (the model is not committed to the
 * repository and ONNX Runtime needs the native model file).
 */
class OnnxBasicPitchModelRunnerTest {

  /**
   * Returns the bundled {@code nmp.onnx} model as a file (copied from the classpath to a temporary
   * file), or {@code null} if it is not on the classpath.
   */
  private static Path locateModel() {
    try (java.io.InputStream in =
        OnnxBasicPitchModelRunner.class.getResourceAsStream(
            OnnxBasicPitchModelRunner.BUNDLED_MODEL_RESOURCE)) {
      if (in == null) {
        return null;
      }
      Path file = Files.createTempFile("nmp", ".onnx");
      file.toFile().deleteOnExit();
      Files.copy(in, file, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
      return file;
    } catch (java.io.IOException e) {
      return null;
    }
  }

  @Test
  void windowingConstantsMatchReferenceImplementation() {
    assertEquals(43844, OnnxBasicPitchModelRunner.AUDIO_N_SAMPLES);
    assertEquals(256, OnnxBasicPitchModelRunner.FFT_HOP);
    assertEquals(86, OnnxBasicPitchModelRunner.ANNOTATIONS_FPS);
    assertEquals(30, OnnxBasicPitchModelRunner.N_OVERLAPPING_FRAMES);
    assertEquals(7680, OnnxBasicPitchModelRunner.OVERLAP_LEN);
    assertEquals(36164, OnnxBasicPitchModelRunner.HOP_SIZE);
  }

  @Test
  void rejectsMissingResource() {
    assertThrows(
        IllegalArgumentException.class,
        () -> OnnxBasicPitchModelRunner.fromResource("/does/not/exist.onnx"));
    assertTrue(!OnnxBasicPitchModelRunner.isModelResourceAvailable("/does/not/exist.onnx"));
  }

  @Test
  void bundledModelResourceIsAvailable() {
    // The model now ships as a classpath resource in the base module, so it must be resolvable
    // both for tests and for the desktop runtime.
    assertTrue(
        OnnxBasicPitchModelRunner.isBundledModelAvailable(),
        "bundled model resource must be present: "
            + OnnxBasicPitchModelRunner.BUNDLED_MODEL_RESOURCE);
  }

  @Test
  void detectsCMajorChordFromBundledModel() throws Exception {
    assumeTrue(
        OnnxBasicPitchModelRunner.isBundledModelAvailable(),
        "bundled Basic Pitch ONNX model not available; skipping");

    double sampleRate = 22050;
    int n = (int) (sampleRate * 2);
    double[] audio = new double[n];
    double[] freqs = {261.63, 329.63, 392.00};
    for (int i = 0; i < n; i++) {
      double t = i / sampleRate;
      double v = 0;
      for (double f : freqs) {
        v += Math.sin(2 * Math.PI * f * t);
      }
      audio[i] = 0.3 * v / freqs.length;
    }

    OnnxBasicPitchModelRunner runner;
    try {
      runner = OnnxBasicPitchModelRunner.fromBundledModel();
    } catch (UnsatisfiedLinkError | NoClassDefFoundError e) {
      assumeTrue(false, "ONNX Runtime native library unavailable on this platform; skipping: " + e);
      return;
    }
    try (runner) {
      BasicPitchOutput out = runner.run(audio, sampleRate);
      List<NoteEvent> events =
          BasicPitchProvider.extractNoteEvents(out, BasicPitchProvider.DEFAULT_NOTE_THRESHOLD);
      boolean hasC = events.stream().anyMatch(e -> Math.round(e.midi()) % 12 == 0);
      boolean hasE = events.stream().anyMatch(e -> Math.round(e.midi()) % 12 == 4);
      boolean hasG = events.stream().anyMatch(e -> Math.round(e.midi()) % 12 == 7);
      assertTrue(hasC && hasE && hasG, "C/E/G must be detected from bundled model, got: " + events);
    }
  }

  @Test
  void detectsCMajorChordFromRealModel() throws Exception {
    Path model = locateModel();
    assumeTrue(model != null, "Bundled Basic Pitch ONNX model not on the classpath; skipping");

    // Synthesize ~2 s of a C major chord (C4/E4/G4) at 22050 Hz.
    double sampleRate = 22050;
    int n = (int) (sampleRate * 2);
    double[] audio = new double[n];
    double[] freqs = {261.63, 329.63, 392.00};
    for (int i = 0; i < n; i++) {
      double t = i / sampleRate;
      double v = 0;
      for (double f : freqs) {
        v += Math.sin(2 * Math.PI * f * t);
      }
      audio[i] = 0.3 * v / freqs.length;
    }

    OnnxBasicPitchModelRunner runner;
    try {
      runner = new OnnxBasicPitchModelRunner(model);
    } catch (UnsatisfiedLinkError | NoClassDefFoundError e) {
      // The onnxruntime:1.22.0 artifact bundles native libraries for Linux, Windows and macOS
      // (osx-aarch64/osx-x64). This remains as a defensive fallback for any platform/arch that is
      // still not covered by the bundled natives, in which case inference cannot run.
      assumeTrue(false, "ONNX Runtime native library unavailable on this platform; skipping: " + e);
      return;
    }
    try (runner) {
      BasicPitchOutput out = runner.run(audio, sampleRate);
      assertTrue(out.note().length > 0, "model must produce note frames");
      assertEquals(88, out.note()[0].length, "note matrix must have 88 columns");
      assertEquals(264, out.contour()[0].length, "contour matrix must have 264 columns");

      // Use the existing extraction to confirm the three pitch classes are present.
      List<NoteEvent> events =
          BasicPitchProvider.extractNoteEvents(out, BasicPitchProvider.DEFAULT_NOTE_THRESHOLD);
      boolean hasC = events.stream().anyMatch(e -> Math.round(e.midi()) % 12 == 0);
      boolean hasE = events.stream().anyMatch(e -> Math.round(e.midi()) % 12 == 4);
      boolean hasG = events.stream().anyMatch(e -> Math.round(e.midi()) % 12 == 7);
      assertTrue(hasC && hasE && hasG, "C/E/G must be detected, got: " + events);
    }
  }
}
