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
package de.schliweb.bluesharpbendingapp.benchmark;

import static de.schliweb.bluesharpbendingapp.benchmark.SyntheticHarmonicaSignals.SAMPLE_RATE;
import static de.schliweb.bluesharpbendingapp.benchmark.SyntheticHarmonicaSignals.cents;

import de.schliweb.bluesharpbendingapp.benchmark.SyntheticHarmonicaSignals.ToneModel;
import de.schliweb.bluesharpbendingapp.benchmark.SyntheticHarmonicaSignals.Trajectory;
import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;
import de.schliweb.bluesharpbendingapp.utils.PitchDetector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Benchmark for the pitch detectors on synthetic harmonica signals with a known ground truth.
 *
 * <p>Monophonic detectors are fed frame by frame, exactly like the microphone does (frame size and
 * hop are parameters), with the detection range set from the harmonica as {@code HarpController}
 * does. Per detector and signal category it measures:
 *
 * <ul>
 *   <li>detection rate: share of frames with a detected pitch
 *   <li>median / 95th percentile of the absolute deviation in cents (detected frames only)
 *   <li>gross errors: share of detected frames off by more than 50 cents
 *   <li>octave errors: share of detected frames off by about one or two octaves
 *   <li>false detections: share of noise-only frames for which a pitch was reported
 *   <li>mean time per call
 * </ul>
 *
 * <p>Polyphonic detectors get chord windows and are scored by precision and recall of the detected
 * pitches (a pitch counts as correct within 50 cents of a chord tone).
 */
public final class PitchBenchmark {

  /** A monophonic detector: returns the pitch in Hz or {@link PitchDetector#NO_DETECTED_PITCH}. */
  public interface MonoDetector {
    /**
     * Returns the display name.
     *
     * @return the name
     */
    String name();

    /**
     * Detects the pitch of one frame.
     *
     * @param frame the samples
     * @param sampleRate the sample rate in Hz
     * @return the pitch in Hz, or {@link PitchDetector#NO_DETECTED_PITCH}
     */
    double detect(double[] frame, int sampleRate);
  }

  /** A polyphonic detector: returns all detected pitches in Hz. */
  public interface PolyDetector {
    /**
     * Returns the display name.
     *
     * @return the name
     */
    String name();

    /**
     * Detects all pitches of one window.
     *
     * @param window the samples
     * @param sampleRate the sample rate in Hz
     * @return the detected pitches in Hz (possibly empty)
     */
    List<Double> detect(double[] window, int sampleRate);
  }

  /** The default monophonic detectors of the app. */
  public static List<MonoDetector> defaultMonoDetectors() {
    return List.of(
        mono("YIN", (f, sr) -> PitchDetector.detectPitchYIN(f, sr).pitch()),
        mono("MPM", (f, sr) -> PitchDetector.detectPitchMPM(f, sr).pitch()),
        mono("Hybrid", (f, sr) -> PitchDetector.detectPitchHybrid(f, sr).pitch()));
  }

  /** The default polyphonic detectors of the app. */
  public static List<PolyDetector> defaultPolyDetectors() {
    return List.of(poly("ChordDetector", (w, sr) -> PitchDetector.detectChord(w, sr).pitches()));
  }

  /**
   * Creates a named monophonic detector.
   *
   * @param name the name
   * @param function the detection function
   * @return the detector
   */
  public static MonoDetector mono(String name, MonoFunction function) {
    return new MonoDetector() {
      @Override
      public String name() {
        return name;
      }

      @Override
      public double detect(double[] frame, int sampleRate) {
        return function.detect(frame, sampleRate);
      }
    };
  }

  /**
   * Creates a named polyphonic detector.
   *
   * @param name the name
   * @param function the detection function
   * @return the detector
   */
  public static PolyDetector poly(String name, PolyFunction function) {
    return new PolyDetector() {
      @Override
      public String name() {
        return name;
      }

      @Override
      public List<Double> detect(double[] window, int sampleRate) {
        return function.detect(window, sampleRate);
      }
    };
  }

  /** Function shape of a monophonic detector. */
  @FunctionalInterface
  public interface MonoFunction {
    /**
     * Detects the pitch of one frame.
     *
     * @param frame the samples
     * @param sampleRate the sample rate
     * @return the pitch in Hz or {@link PitchDetector#NO_DETECTED_PITCH}
     */
    double detect(double[] frame, int sampleRate);
  }

  /** Function shape of a polyphonic detector. */
  @FunctionalInterface
  public interface PolyFunction {
    /**
     * Detects all pitches of one window.
     *
     * @param window the samples
     * @param sampleRate the sample rate
     * @return the detected pitches in Hz
     */
    List<Double> detect(double[] window, int sampleRate);
  }

  /** The harmonicas the scenarios are generated for: low, medium and high keys. */
  static final List<AbstractHarmonica.KEY> KEYS =
      List.of(
          AbstractHarmonica.KEY.LF, AbstractHarmonica.KEY.G,
          AbstractHarmonica.KEY.C, AbstractHarmonica.KEY.HG);

  private static final double TONE_SECONDS = 0.6;
  private static final double BEND_SECONDS = 1.2;
  private static final double SKIP_ATTACK_SECONDS = 0.03;

  /** One test signal with its ground truth. */
  record Scenario(String category, AbstractHarmonica.KEY key, double[] signal, Trajectory truth) {}

  /** A chord window with its ground-truth tones. */
  record ChordScenario(
      String category, AbstractHarmonica.KEY key, double[] signal, double[] tones) {}

  /**
   * Builds all monophonic scenarios: every blow and draw note of every key under several
   * conditions, bends on the draw notes of channels 1-6, and breath noise without any tone.
   *
   * @return the scenarios, grouped by key
   */
  static Map<AbstractHarmonica.KEY, List<Scenario>> monoScenarios() {
    Map<AbstractHarmonica.KEY, List<Scenario>> result = new LinkedHashMap<>();
    long seed = 1;
    for (AbstractHarmonica.KEY key : KEYS) {
      Harmonica harp = AbstractHarmonica.create(key, AbstractHarmonica.TUNE.RICHTER);
      List<Scenario> list = new ArrayList<>();
      for (int channel = 1; channel <= 10; channel++) {
        for (int note = 0; note <= 1; note++) {
          double f = harp.getNoteFrequency(channel, note);
          Trajectory steady = SyntheticHarmonicaSignals.steady(f);
          list.add(
              new Scenario(
                  "sine",
                  key,
                  SyntheticHarmonicaSignals.tone(steady, ToneModel.SINE, TONE_SECONDS, seed++),
                  steady));
          double[] harmonica =
              SyntheticHarmonicaSignals.tone(steady, ToneModel.HARMONICA, TONE_SECONDS, seed++);
          list.add(new Scenario("harmonica", key, harmonica, steady));
          list.add(
              new Scenario(
                  "harmonica + noise 20 dB",
                  key,
                  SyntheticHarmonicaSignals.withBreathNoise(harmonica, 20, seed++),
                  steady));
          list.add(
              new Scenario(
                  "harmonica + noise 10 dB",
                  key,
                  SyntheticHarmonicaSignals.withBreathNoise(harmonica, 10, seed++),
                  steady));
          list.add(
              new Scenario(
                  "weak fundamental",
                  key,
                  SyntheticHarmonicaSignals.tone(
                      steady, ToneModel.WEAK_FUNDAMENTAL, TONE_SECONDS, seed++),
                  steady));
          Trajectory vibrato = SyntheticHarmonicaSignals.vibrato(f, 25, 5);
          list.add(
              new Scenario(
                  "vibrato ±25 cents",
                  key,
                  SyntheticHarmonicaSignals.tone(
                      vibrato, ToneModel.HARMONICA, TONE_SECONDS, seed++),
                  vibrato));
        }
      }
      for (int channel = 1; channel <= 6; channel++) {
        double f = harp.getNoteFrequency(channel, 1);
        Trajectory bend = SyntheticHarmonicaSignals.bend(f, 100, 0.3, 0.6);
        list.add(
            new Scenario(
                "bend -100 cents",
                key,
                SyntheticHarmonicaSignals.tone(bend, ToneModel.HARMONICA, BEND_SECONDS, seed++),
                bend));
      }
      for (int i = 0; i < 4; i++) {
        list.add(
            new Scenario(
                "breath noise only",
                key,
                SyntheticHarmonicaSignals.breathNoiseOnly(TONE_SECONDS, seed++),
                null));
      }
      result.put(key, list);
    }
    return result;
  }

  /**
   * Builds chord scenarios: blow and draw chords on adjacent channels and the octave split 1+4.
   *
   * @return the chord scenarios
   */
  static List<ChordScenario> chordScenarios() {
    List<ChordScenario> list = new ArrayList<>();
    long seed = 10_000;
    int[][] blowChords = {{1, 2, 3}, {4, 5, 6}, {1, 2}, {1, 4}};
    int[][] drawChords = {{1, 2}, {2, 3}, {4, 5}};
    for (AbstractHarmonica.KEY key : KEYS) {
      Harmonica harp = AbstractHarmonica.create(key, AbstractHarmonica.TUNE.RICHTER);
      for (int[] chord : blowChords) {
        list.add(chord(harp, key, chord, 0, seed));
        seed += 10;
      }
      for (int[] chord : drawChords) {
        list.add(chord(harp, key, chord, 1, seed));
        seed += 10;
      }
    }
    return list;
  }

  private static ChordScenario chord(
      Harmonica harp, AbstractHarmonica.KEY key, int[] channels, int note, long seed) {
    double[][] tones = new double[channels.length][];
    double[] freqs = new double[channels.length];
    for (int i = 0; i < channels.length; i++) {
      freqs[i] = harp.getNoteFrequency(channels[i], note);
      tones[i] =
          SyntheticHarmonicaSignals.tone(
              SyntheticHarmonicaSignals.steady(freqs[i]),
              ToneModel.HARMONICA,
              TONE_SECONDS,
              seed + i);
    }
    String kind = channels.length == 2 && channels[1] - channels[0] == 3 ? "octave split" : "chord";
    String category = kind + " " + channels.length + " notes " + (note == 0 ? "blow" : "draw");
    return new ChordScenario(category, key, SyntheticHarmonicaSignals.mix(tones), freqs);
  }

  /** Accumulated metrics for one detector and category. */
  static final class MonoStats {
    int frames;
    int detected;
    int gross;
    int octave;
    final List<Double> absCents = new ArrayList<>();
    long nanos;
    int calls;

    double detectionRate() {
      return frames == 0 ? 0 : (double) detected / frames;
    }

    double percentile(double p) {
      if (absCents.isEmpty()) {
        return Double.NaN;
      }
      double[] sorted = absCents.stream().mapToDouble(Double::doubleValue).sorted().toArray();
      int index = (int) Math.min(sorted.length - 1, Math.floor(p * sorted.length));
      return sorted[index];
    }
  }

  /** Accumulated metrics for one polyphonic detector and category. */
  static final class PolyStats {
    int truePositives;
    int falsePositives;
    int falseNegatives;
    int windows;
    int exact;
    long nanos;

    double precision() {
      int d = truePositives + falsePositives;
      return d == 0 ? 0 : (double) truePositives / d;
    }

    double recall() {
      int d = truePositives + falseNegatives;
      return d == 0 ? 0 : (double) truePositives / d;
    }
  }

  /**
   * Runs the monophonic benchmark.
   *
   * @param detectors the detectors to compare
   * @param frameSize the frame size in samples
   * @param hop the hop size in samples
   * @return per category the stats of each detector (insertion ordered)
   */
  static Map<String, Map<String, MonoStats>> runMono(
      List<MonoDetector> detectors, int frameSize, int hop) {
    Map<String, Map<String, MonoStats>> result = new LinkedHashMap<>();
    Map<AbstractHarmonica.KEY, List<Scenario>> scenarios = monoScenarios();
    try {
      for (Map.Entry<AbstractHarmonica.KEY, List<Scenario>> entry : scenarios.entrySet()) {
        Harmonica harp = AbstractHarmonica.create(entry.getKey(), AbstractHarmonica.TUNE.RICHTER);
        PitchDetector.setMinFrequency(harp.getHarmonicaMinFrequency());
        PitchDetector.setMaxFrequency(harp.getHarmonicaMaxFrequency());
        warmUp(detectors, entry.getValue().get(1).signal(), frameSize);
        for (Scenario scenario : entry.getValue()) {
          Map<String, MonoStats> byDetector =
              result.computeIfAbsent(scenario.category(), c -> new LinkedHashMap<>());
          for (MonoDetector detector : detectors) {
            MonoStats stats = byDetector.computeIfAbsent(detector.name(), d -> new MonoStats());
            evaluate(detector, scenario, frameSize, hop, stats);
          }
        }
      }
    } finally {
      PitchDetector.setMinFrequency(PitchDetector.getDefaultMinFrequency());
      PitchDetector.setMaxFrequency(PitchDetector.getDefaultMaxFrequency());
    }
    return result;
  }

  private static void warmUp(List<MonoDetector> detectors, double[] signal, int frameSize) {
    double[] frame = Arrays.copyOf(signal, frameSize);
    for (int i = 0; i < 20; i++) {
      for (MonoDetector detector : detectors) {
        detector.detect(frame, SAMPLE_RATE);
      }
    }
  }

  private static void evaluate(
      MonoDetector detector, Scenario scenario, int frameSize, int hop, MonoStats stats) {
    double[] signal = scenario.signal();
    int skip = (int) (SKIP_ATTACK_SECONDS * SAMPLE_RATE);
    for (int start = skip; start + frameSize <= signal.length; start += hop) {
      double[] frame = Arrays.copyOfRange(signal, start, start + frameSize);
      long t0 = System.nanoTime();
      double pitch = detector.detect(frame, SAMPLE_RATE);
      stats.nanos += System.nanoTime() - t0;
      stats.calls++;
      stats.frames++;
      if (pitch == PitchDetector.NO_DETECTED_PITCH || pitch <= 0) {
        continue;
      }
      stats.detected++;
      if (scenario.truth() == null) {
        continue; // noise only: any detection is a false detection
      }
      double centre = (start + frameSize / 2.0) / SAMPLE_RATE;
      double deviation = cents(pitch, scenario.truth().frequencyAt(centre));
      double abs = Math.abs(deviation);
      stats.absCents.add(abs);
      if (abs > 50) {
        stats.gross++;
      }
      double octaves = Math.abs(deviation / 1200.0);
      if (octaves > 0.5 && Math.abs(octaves - Math.round(octaves)) < 100.0 / 1200.0) {
        stats.octave++;
      }
    }
  }

  /**
   * Runs the polyphonic benchmark.
   *
   * @param detectors the detectors to compare
   * @param windowSize the window size in samples
   * @param hop the hop size in samples
   * @return per category the stats of each detector
   */
  static Map<String, Map<String, PolyStats>> runPoly(
      List<PolyDetector> detectors, int windowSize, int hop) {
    Map<String, Map<String, PolyStats>> result = new LinkedHashMap<>();
    try {
      for (ChordScenario scenario : chordScenarios()) {
        Harmonica harp = AbstractHarmonica.create(scenario.key(), AbstractHarmonica.TUNE.RICHTER);
        PitchDetector.setMinFrequency(harp.getHarmonicaMinFrequency());
        PitchDetector.setMaxFrequency(harp.getHarmonicaMaxFrequency());
        Map<String, PolyStats> byDetector =
            result.computeIfAbsent(scenario.category(), c -> new LinkedHashMap<>());
        for (PolyDetector detector : detectors) {
          PolyStats stats = byDetector.computeIfAbsent(detector.name(), d -> new PolyStats());
          double[] signal = scenario.signal();
          int skip = (int) (SKIP_ATTACK_SECONDS * SAMPLE_RATE);
          for (int start = skip; start + windowSize <= signal.length; start += hop) {
            double[] window = Arrays.copyOfRange(signal, start, start + windowSize);
            long t0 = System.nanoTime();
            List<Double> detected = detector.detect(window, SAMPLE_RATE);
            stats.nanos += System.nanoTime() - t0;
            stats.windows++;
            score(detected, scenario.tones(), stats);
          }
        }
      }
    } finally {
      PitchDetector.setMinFrequency(PitchDetector.getDefaultMinFrequency());
      PitchDetector.setMaxFrequency(PitchDetector.getDefaultMaxFrequency());
    }
    return result;
  }

  private static void score(List<Double> detected, double[] tones, PolyStats stats) {
    boolean[] matched = new boolean[tones.length];
    int tp = 0;
    int fp = 0;
    for (double pitch : detected) {
      int best = -1;
      double bestAbs = Double.MAX_VALUE;
      for (int i = 0; i < tones.length; i++) {
        double abs = Math.abs(cents(pitch, tones[i]));
        if (!matched[i] && abs < bestAbs) {
          bestAbs = abs;
          best = i;
        }
      }
      if (best >= 0 && bestAbs <= 50) {
        matched[best] = true;
        tp++;
      } else {
        fp++;
      }
    }
    int fn = 0;
    for (boolean m : matched) {
      if (!m) {
        fn++;
      }
    }
    stats.truePositives += tp;
    stats.falsePositives += fp;
    stats.falseNegatives += fn;
    if (fp == 0 && fn == 0) {
      stats.exact++;
    }
  }

  /**
   * Renders the monophonic results as a Markdown table.
   *
   * @param title the section title
   * @param results the results of {@link #runMono}
   * @return the Markdown text
   */
  static String monoMarkdown(String title, Map<String, Map<String, MonoStats>> results) {
    StringBuilder sb = new StringBuilder();
    sb.append("## ").append(title).append("\n\n");
    sb.append(
        "| Category | Detector | Detected | Median \\|cents\\| | P95 \\|cents\\| | >50 cents |"
            + " Octave errors | µs/call |\n");
    sb.append("|---|---|---:|---:|---:|---:|---:|---:|\n");
    for (Map.Entry<String, Map<String, MonoStats>> category : results.entrySet()) {
      boolean noise = category.getKey().startsWith("breath noise");
      for (Map.Entry<String, MonoStats> d : category.getValue().entrySet()) {
        MonoStats s = d.getValue();
        double us = s.calls == 0 ? 0 : s.nanos / 1000.0 / s.calls;
        if (noise) {
          sb.append(
              String.format(
                  Locale.ROOT,
                  "| %s | %s | %.1f %% (false) | – | – | – | – | %.0f |%n",
                  category.getKey(),
                  d.getKey(),
                  100 * s.detectionRate(),
                  us));
        } else {
          int n = Math.max(1, s.detected);
          sb.append(
              String.format(
                  Locale.ROOT,
                  "| %s | %s | %.1f %% | %.2f | %.2f | %.1f %% | %.1f %% | %.0f |%n",
                  category.getKey(),
                  d.getKey(),
                  100 * s.detectionRate(),
                  s.percentile(0.5),
                  s.percentile(0.95),
                  100.0 * s.gross / n,
                  100.0 * s.octave / n,
                  us));
        }
      }
    }
    return sb.append('\n').toString();
  }

  /**
   * Renders the polyphonic results as a Markdown table.
   *
   * @param title the section title
   * @param results the results of {@link #runPoly}
   * @return the Markdown text
   */
  static String polyMarkdown(String title, Map<String, Map<String, PolyStats>> results) {
    StringBuilder sb = new StringBuilder();
    sb.append("## ").append(title).append("\n\n");
    sb.append("| Category | Detector | Precision | Recall | Exact windows | ms/window |\n");
    sb.append("|---|---|---:|---:|---:|---:|\n");
    for (Map.Entry<String, Map<String, PolyStats>> category : results.entrySet()) {
      for (Map.Entry<String, PolyStats> d : category.getValue().entrySet()) {
        PolyStats s = d.getValue();
        sb.append(
            String.format(
                Locale.ROOT,
                "| %s | %s | %.2f | %.2f | %.1f %% | %.2f |%n",
                category.getKey(),
                d.getKey(),
                s.precision(),
                s.recall(),
                s.windows == 0 ? 0 : 100.0 * s.exact / s.windows,
                s.windows == 0 ? 0 : s.nanos / 1e6 / s.windows));
      }
    }
    return sb.append('\n').toString();
  }

  private PitchBenchmark() {}
}
