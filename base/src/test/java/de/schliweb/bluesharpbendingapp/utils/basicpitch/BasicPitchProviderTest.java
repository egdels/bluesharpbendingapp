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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link BasicPitchProvider} note-event extraction. The model inference itself is
 * mocked by a {@link BasicPitchModelRunner} that returns synthetic frame tensors, so the test is
 * deterministic and requires no ONNX model.
 */
class BasicPitchProviderTest {

  private static final int FRAMES = 100;
  private static final double FPS = BasicPitchOutput.DEFAULT_FRAMES_PER_SECOND;

  /** Builds an empty note/onset/contour output of the given frame count. */
  private static float[][] emptyMatrix(int frames, int cols) {
    return new float[frames][cols];
  }

  /** Column index in the note matrix for a given MIDI note. */
  private static int col(int midi) {
    return midi - BasicPitchOutput.MIN_MIDI;
  }

  /** A runner returning a fixed, prepared output regardless of input. */
  private static BasicPitchModelRunner fixedRunner(BasicPitchOutput output) {
    return (audioData, sampleRate) -> output;
  }

  @Test
  void extractsSingleNoteRun() {
    float[][] note = emptyMatrix(FRAMES, BasicPitchOutput.NUM_NOTES);
    // C4 (MIDI 60) active in frames 10..49 with activation 0.8.
    for (int f = 10; f < 50; f++) {
      note[f][col(60)] = 0.8f;
    }
    BasicPitchOutput output =
        new BasicPitchOutput(note, emptyMatrix(FRAMES, BasicPitchOutput.NUM_NOTES), null, FPS);

    List<NoteEvent> events = BasicPitchProvider.extractNoteEvents(output, 0.3);

    assertEquals(1, events.size());
    NoteEvent ev = events.get(0);
    assertEquals(60, ev.midi(), 1e-9);
    assertEquals(0.8, ev.amplitude(), 1e-6);
    assertEquals(10 / FPS, ev.start(), 1e-9);
    assertEquals(50 / FPS, ev.end(), 1e-9);
  }

  @Test
  void extractsSimultaneousNotes() {
    float[][] note = emptyMatrix(FRAMES, BasicPitchOutput.NUM_NOTES);
    // C major blow chord: C4 (60), E4 (64), G4 (67) sounding together.
    for (int f = 5; f < 60; f++) {
      note[f][col(60)] = 0.7f;
      note[f][col(64)] = 0.6f;
      note[f][col(67)] = 0.5f;
    }
    BasicPitchOutput output =
        new BasicPitchOutput(note, emptyMatrix(FRAMES, BasicPitchOutput.NUM_NOTES), null, FPS);

    List<NoteEvent> events = BasicPitchProvider.extractNoteEvents(output, 0.3);

    Set<Double> midis = events.stream().map(NoteEvent::midi).collect(Collectors.toSet());
    assertEquals(Set.of(60.0, 64.0, 67.0), midis);
  }

  @Test
  void weakActivationBelowThresholdIgnored() {
    float[][] note = emptyMatrix(FRAMES, BasicPitchOutput.NUM_NOTES);
    for (int f = 0; f < FRAMES; f++) {
      note[f][col(64)] = 0.2f; // below threshold 0.3
    }
    BasicPitchOutput output =
        new BasicPitchOutput(note, emptyMatrix(FRAMES, BasicPitchOutput.NUM_NOTES), null, FPS);

    assertTrue(BasicPitchProvider.extractNoteEvents(output, 0.3).isEmpty());
  }

  @Test
  void providerExposesBasicPitchAsMultiPitchDetector() {
    float[][] note = emptyMatrix(FRAMES, BasicPitchOutput.NUM_NOTES);
    for (int f = 10; f < 40; f++) {
      note[f][col(72)] = 0.9f; // C5
    }
    BasicPitchOutput output =
        new BasicPitchOutput(note, emptyMatrix(FRAMES, BasicPitchOutput.NUM_NOTES), null, FPS);
    MultiPitchDetector detector = new BasicPitchProvider(fixedRunner(output));

    List<NoteEvent> events = detector.detect(new double[1024], 22050);

    assertEquals("BasicPitch", detector.name());
    assertEquals(1, events.size());
    assertEquals(72, events.get(0).midi(), 1e-9);
  }

  @Test
  void emptyOutputYieldsNoEvents() {
    BasicPitchOutput output = new BasicPitchOutput(new float[0][0], new float[0][0], null, FPS);
    assertTrue(BasicPitchProvider.extractNoteEvents(output, 0.3).isEmpty());
  }
}
