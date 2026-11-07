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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * Engine-agnostic harness for the Roadmap Phase-0 A/B baseline comparison between the existing
 * {@code ChordDetector} ({@link ChordDetectorMultiPitchAdapter}) and the ML-based Basic Pitch
 * frontend ({@link BasicPitchProvider}). Both engines are driven through the shared {@link
 * MultiPitchDetector} interface on identical audio, so the comparison is fair.
 *
 * <p>The harness evaluates a detector on two levels (as required by §8.3 of the integration
 * concept):
 *
 * <ul>
 *   <li><b>tone classes</b> — the detected pitch classes (C, C#, …, B) regardless of octave, which
 *       is the natural granularity of a multi-pitch estimator, and
 *   <li><b>harp channels</b> — the deterministic {@link HarpPitchMapper} interpretation (e.g.
 *       {@code 1B, 2B, 3B}).
 * </ul>
 *
 * For each level it computes True/False Positives, False Negatives, Precision, Recall and F1
 * against a manually provided ground truth, plus the wall-clock inference time. The harness
 * performs no model inference itself; it is purely the measurement/scoring layer.
 */
public final class BaselineComparison {

  private final HarpPitchMapper mapper;

  /** Creates a harness with a default Richter-C mapper. */
  public BaselineComparison() {
    this(new HarpPitchMapper());
  }

  /**
   * Creates a harness with the given mapper.
   *
   * @param mapper the harp pitch mapper used for the harp-channel evaluation
   */
  public BaselineComparison(HarpPitchMapper mapper) {
    this.mapper = mapper;
  }

  /**
   * Scoring metrics for a single engine on a single test case. Java pendant to the Precision/Recall
   * evaluation of the Python prototype tests.
   *
   * @param engine the engine name (e.g. {@code ChordDetector} / {@code BasicPitch})
   * @param level the evaluation level ({@code tone-classes} or {@code harp-channels})
   * @param expected the ground-truth labels
   * @param detected the labels actually detected by the engine
   * @param inferenceNanos the wall-clock inference time in nanoseconds
   */
  public record Metrics(
      String engine,
      String level,
      Set<String> expected,
      Set<String> detected,
      long inferenceNanos) {

    /**
     * Returns the number of true positives (detected labels that are also expected).
     *
     * @return the true-positive count
     */
    public int truePositives() {
      return (int) detected.stream().filter(expected::contains).count();
    }

    /**
     * Returns the number of false positives (detected labels that are not expected).
     *
     * @return the false-positive count
     */
    public int falsePositives() {
      return (int) detected.stream().filter(l -> !expected.contains(l)).count();
    }

    /**
     * Returns the number of false negatives (expected labels that were not detected).
     *
     * @return the false-negative count
     */
    public int falseNegatives() {
      return (int) expected.stream().filter(l -> !detected.contains(l)).count();
    }

    /**
     * Returns the precision {@code tp / (tp + fp)} (1.0 if nothing was detected).
     *
     * @return the precision in {@code [0, 1]}
     */
    public double precision() {
      int tp = truePositives();
      int fp = falsePositives();
      return (tp + fp) == 0 ? 1.0 : (double) tp / (tp + fp);
    }

    /**
     * Returns the recall {@code tp / (tp + fn)} (1.0 if nothing was expected).
     *
     * @return the recall in {@code [0, 1]}
     */
    public double recall() {
      int tp = truePositives();
      int fn = falseNegatives();
      return (tp + fn) == 0 ? 1.0 : (double) tp / (tp + fn);
    }

    /**
     * Returns the F1 score, the harmonic mean of {@link #precision()} and {@link #recall()}.
     *
     * @return the F1 score in {@code [0, 1]}
     */
    public double f1() {
      double p = precision();
      double r = recall();
      return (p + r) == 0 ? 0.0 : 2 * p * r / (p + r);
    }

    /**
     * Returns a single-line, human-readable report of these metrics.
     *
     * @return the formatted report line
     */
    public String format() {
      return String.format(
          Locale.ROOT,
          "%-13s %-13s P=%.2f R=%.2f F1=%.2f  TP=%d FP=%d FN=%d  t=%.1fms  detected=%s",
          engine,
          level,
          precision(),
          recall(),
          f1(),
          truePositives(),
          falsePositives(),
          falseNegatives(),
          inferenceNanos / 1_000_000.0,
          new TreeSet<>(detected));
    }
  }

  /**
   * Runs the given detector on the audio, measures inference time and scores the detected tone
   * classes against the expected ones.
   *
   * @param detector the engine under test
   * @param audioData the mono audio samples
   * @param sampleRate the sample rate in Hz
   * @param expectedToneClasses the ground-truth tone classes (e.g. {@code {"C", "E", "G"}})
   * @return the tone-class metrics
   */
  public Metrics evaluateToneClasses(
      MultiPitchDetector detector,
      double[] audioData,
      double sampleRate,
      Set<String> expectedToneClasses) {
    long start = System.nanoTime();
    List<NoteEvent> events = detector.detect(audioData, sampleRate);
    long elapsed = System.nanoTime() - start;
    return new Metrics(
        detector.name(), "tone-classes", expectedToneClasses, toneClasses(events), elapsed);
  }

  /**
   * Runs the given detector on the audio, measures inference time, applies the artefact filter and
   * the deterministic harp mapping, and scores the resulting harp channels against the expected
   * ones.
   *
   * @param detector the engine under test
   * @param audioData the mono audio samples
   * @param sampleRate the sample rate in Hz
   * @param expectedChannels the ground-truth harp channels (e.g. {@code {"1B", "2B", "3B"}})
   * @return the harp-channel metrics
   */
  public Metrics evaluateHarpChannels(
      MultiPitchDetector detector,
      double[] audioData,
      double sampleRate,
      Set<String> expectedChannels) {
    long start = System.nanoTime();
    List<NoteEvent> events = detector.detect(audioData, sampleRate);
    long elapsed = System.nanoTime() - start;
    MappingResult mapping = mapper.mapNotes(events, 0.0);
    return new Metrics(
        detector.name(),
        "harp-channels",
        expectedChannels,
        new LinkedHashSet<>(mapping.labels()),
        elapsed);
  }

  /**
   * Extracts the set of detected pitch classes (C, C#, …, B) from a list of note events, ignoring
   * the octave. This is the granularity at which a multi-pitch estimator is fairly evaluated.
   *
   * @param events the detected note events
   * @return the set of pitch-class names
   */
  public static Set<String> toneClasses(List<NoteEvent> events) {
    Set<String> classes = new LinkedHashSet<>();
    for (NoteEvent event : events) {
      int midi = (int) Math.round(event.midi());
      String name = HarpTone.midiToName(midi);
      // strip trailing octave number to obtain the pitch class only
      classes.add(name.replaceAll("-?\\d+$", ""));
    }
    return classes;
  }
}
