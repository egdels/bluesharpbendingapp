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

import de.schliweb.bluesharpbendingapp.utils.LoggingContext;
import de.schliweb.bluesharpbendingapp.utils.LoggingUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that turns the raw frame tensors of a {@link BasicPitchModelRunner} into a list of {@link
 * NoteEvent}s, exposing Basic Pitch as a {@link MultiPitchDetector}. Java pendant to the note-event
 * extraction of the Python prototype ({@code evaluate_basic_pitch.predict()}).
 *
 * <p>The extraction is intentionally simple and deterministic (no model-internal logic): for each
 * of the 88 note bins it scans the per-frame {@code note} activations and groups consecutive frames
 * above {@code noteThreshold} into a single event. The event amplitude is the peak activation
 * across the run. This is sufficient for the Phase-0 A/B baseline; richer onset/contour handling
 * can be added later without changing the interface.
 */
public final class BasicPitchProvider implements MultiPitchDetector {

  /** Default activation threshold above which a note bin is considered "on". */
  public static final double DEFAULT_NOTE_THRESHOLD = 0.3;

  private final BasicPitchModelRunner runner;
  private final double noteThreshold;

  /**
   * Creates a provider with the default note threshold.
   *
   * @param runner the platform-specific model runner
   */
  public BasicPitchProvider(BasicPitchModelRunner runner) {
    this(runner, DEFAULT_NOTE_THRESHOLD);
  }

  /**
   * Creates a provider with the given note threshold.
   *
   * @param runner the platform-specific model runner
   * @param noteThreshold the activation threshold above which a note bin is considered "on"
   */
  public BasicPitchProvider(BasicPitchModelRunner runner, double noteThreshold) {
    this.runner = runner;
    this.noteThreshold = noteThreshold;
  }

  @Override
  public List<NoteEvent> detect(double[] audioData, double sampleRate) {
    BasicPitchOutput output = runner.run(audioData, sampleRate);
    return extractNoteEvents(output, noteThreshold);
  }

  @Override
  public String name() {
    return "BasicPitch";
  }

  /**
   * Extracts note events from a {@link BasicPitchOutput} by thresholding the {@code note} matrix
   * and grouping consecutive active frames per note bin.
   *
   * @param output the raw model output
   * @param noteThreshold the activation threshold
   * @return the extracted note events
   */
  public static List<NoteEvent> extractNoteEvents(BasicPitchOutput output, double noteThreshold) {
    List<NoteEvent> events = new ArrayList<>();
    float[][] note = output.note();
    if (note == null || note.length == 0) {
      return events;
    }
    int frames = note.length;
    int numNotes = note[0].length;
    double fps = output.framesPerSecond();
    for (int col = 0; col < numNotes; col++) {
      int runStart = -1;
      double peak = 0.0;
      for (int frame = 0; frame < frames; frame++) {
        double activation = note[frame][col];
        boolean on = activation >= noteThreshold;
        if (on) {
          if (runStart < 0) {
            runStart = frame;
            peak = activation;
          } else {
            peak = Math.max(peak, activation);
          }
        } else if (runStart >= 0) {
          events.add(makeEvent(runStart, frame, col, peak, fps));
          runStart = -1;
          peak = 0.0;
        }
      }
      if (runStart >= 0) {
        events.add(makeEvent(runStart, frames, col, peak, fps));
      }
    }
    return events;
  }

  /** Builds a note event from a run of active frames in a note bin. */
  private static NoteEvent makeEvent(
      int startFrame, int endFrame, int col, double peak, double fps) {
    double start = startFrame / fps;
    double end = endFrame / fps;
    int midi = BasicPitchOutput.MIN_MIDI + col;
    return new NoteEvent(start, end, midi, peak);
  }

  /** Closes the model runner if it holds native resources (the ONNX Runtime session). */
  @Override
  public void close() {
    if (runner instanceof AutoCloseable closeable) {
      try {
        closeable.close();
      } catch (Exception e) {
        LoggingContext.setComponent("BasicPitchProvider");
        LoggingUtils.logWarning("Failed to close Basic Pitch model runner: " + e.getMessage());
      }
    }
  }
}
