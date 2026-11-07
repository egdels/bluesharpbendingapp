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

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.schliweb.bluesharpbendingapp.utils.PitchDetector;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Roadmap Phase-0 deliverable: a <em>larger</em> A/B baseline measurement of the existing {@code
 * ChordDetector} ({@link ChordDetectorMultiPitchAdapter}) versus the Basic Pitch frontend ({@link
 * BasicPitchProvider} backed by the real {@link OnnxBasicPitchModelRunner}) on a ground-truth set
 * consisting of synthetic cases plus the real recordings {@code unbenannt2/3/4.wav}.
 *
 * <p>The test is primarily a <b>report generator</b>: it prints a Markdown table of
 * Precision/Recall/F1, inference time and approximate heap usage per case/engine (look for the
 * {@code [DEBUG_LOG]} lines). It only hard-asserts the well-defined synthetic C-major case so it
 * can never become flaky on the (optional, not-committed) real recordings.
 *
 * <p>Both engines are evaluated through an identical windowed harness (2&nbsp;s windows, 1&nbsp;s
 * hop) and an identical fair pre-filter (keep MIDI&nbsp;48..96, i.e. C3..C7) so neither engine is
 * advantaged. The real recordings and the Basic Pitch ONNX model are loaded only when locally
 * available; otherwise those rows are skipped.
 */
class BaselineReportTest {

  // --- fair, identical pre-filter for both engines (bass/treble artefacts) -----------------
  private static final int MIN_MIDI = 48; // C3
  private static final int MAX_MIDI = 96; // C7

  // --- windowing identical for both engines ------------------------------------------------
  private static final double WINDOW_SECONDS = 2.0;
  private static final double HOP_SECONDS = 1.0;

  // synthetic frequencies (equal-temperament, A4=440)
  private static final double C4 = 261.63;
  private static final double E4 = 329.63;
  private static final double G4 = 392.00;
  private static final double C5 = 523.25;
  private static final double D4 = 293.66;
  private static final double FS4 = 369.99;
  private static final double A4 = 440.00;
  private static final double B4 = 493.88;
  private static final double D5 = 587.33;
  private static final double F5 = 698.46;

  /** Default location of the Basic Pitch ONNX model inside the project's Python venv. */
  private static final Path VENV_MODEL =
      Paths.get("venv/lib/python3.11/site-packages/basic_pitch/saved_models/icassp_2022/nmp.onnx");

  @BeforeEach
  void setUp() {
    PitchDetector.setMaxFrequency(PitchDetector.getDefaultMaxFrequency());
    PitchDetector.setMinFrequency(PitchDetector.getDefaultMinFrequency());
  }

  /** A single ground-truth test case (a class, not a record, to avoid array record components). */
  private static final class Case {
    private final String name;
    private final double[] audio;
    private final int sampleRate;
    private final Set<String> truth;

    Case(String name, double[] audio, int sampleRate, Set<String> truth) {
      this.name = name;
      this.audio = audio;
      this.sampleRate = sampleRate;
      this.truth = truth;
    }

    String name() {
      return name;
    }

    double[] audio() {
      return audio;
    }

    int sampleRate() {
      return sampleRate;
    }

    Set<String> truth() {
      return truth;
    }
  }

  /** The measured outcome for one engine on one case. */
  private record Row(
      String caseName,
      String engine,
      double precision,
      double recall,
      double f1,
      double totalMs,
      double msPerWindow,
      double ramMb,
      Set<String> detected) {}

  @Test
  void abBaselineReportOnLargerGroundTruthSet() throws Exception {
    List<Case> cases = new ArrayList<>();

    // ---- synthetic cases (always available, deterministic) -------------------------------
    cases.add(new Case("synth single C4", chord(44100, 2.0, C4), 44100, Set.of("C")));
    cases.add(new Case("synth octave C4+C5", chord(44100, 2.0, C4, C5), 44100, Set.of("C")));
    cases.add(
        new Case("synth C major", chord(44100, 2.0, C4, E4, G4), 44100, Set.of("C", "E", "G")));
    cases.add(
        new Case("synth D major", chord(44100, 2.0, D4, FS4, A4), 44100, Set.of("D", "F#", "A")));
    cases.add(
        new Case("synth G7", chord(44100, 2.0, G4, B4, D5, F5), 44100, Set.of("G", "B", "D", "F")));
    // Reproduces what was actually played in unbenannt4.wav: a C octave (C4+C5, blow) alternating
    // with a D octave (D4+D5, draw). Four 1 s segments C-oct/D-oct/C-oct/D-oct, truth = {C, D}.
    cases.add(
        new Case(
            "synth C/D octave alternation",
            sequence(
                44100,
                1.0,
                new double[] {C4, C5},
                new double[] {D4, D5},
                new double[] {C4, C5},
                new double[] {D4, D5}),
            44100,
            Set.of("C", "D")));

    // ---- real recordings (optional, not committed) ---------------------------------------
    addRealCase(cases, "unbenannt2.wav", Set.of("C#", "E", "G#", "B"));
    addRealCase(cases, "unbenannt3.wav", Set.of("A", "B")); // A-Dur harp: A3/A4 blow, B3/B4 draw
    addRealCase(cases, "unbenannt4.wav", Set.of("C", "D")); // C-Dur harp: C-oct blow, D-oct draw

    // ---- the two engines -----------------------------------------------------------------
    MultiPitchDetector chordEngine = new ChordDetectorMultiPitchAdapter();
    OnnxBasicPitchModelRunner runner = tryCreateOnnxRunner();
    MultiPitchDetector basicPitchEngine = runner != null ? new BasicPitchProvider(runner) : null;

    List<Row> rows = new ArrayList<>();
    try {
      for (Case c : cases) {
        rows.add(measure(c, chordEngine));
        if (basicPitchEngine != null) {
          rows.add(measure(c, basicPitchEngine));
        }
      }
    } finally {
      if (runner != null) {
        runner.close();
      }
    }

    printMarkdown(cases, rows, basicPitchEngine != null);

    // Sanity: the synthetic C-major case must work for the ChordDetector (deterministic).
    Row cMajorChord =
        rows.stream()
            .filter(r -> r.caseName().equals("synth C major") && r.engine().equals("ChordDetector"))
            .findFirst()
            .orElseThrow();
    assertTrue(
        cMajorChord.f1() >= 0.9,
        "ChordDetector must recover synthetic C major tone classes: F1=" + cMajorChord.f1());
    assertTrue(!rows.isEmpty(), "the baseline report must contain at least one measured row");
  }

  // ---------------------------------------------------------------------------------------
  // measurement
  // ---------------------------------------------------------------------------------------

  /**
   * Runs one engine over one case with identical windowing + filter and measures P/R/F1/time/RAM.
   */
  private Row measure(Case c, MultiPitchDetector engine) {
    int windowSamples = (int) Math.round(WINDOW_SECONDS * c.sampleRate());
    int hopSamples = (int) Math.round(HOP_SECONDS * c.sampleRate());

    Runtime rt = Runtime.getRuntime();
    System.gc();
    long usedBefore = rt.totalMemory() - rt.freeMemory();
    long peakUsed = usedBefore;

    Set<String> detected = new LinkedHashSet<>();
    long start = System.nanoTime();
    int windows = 0;
    for (int from = 0; from < c.audio().length; from += hopSamples) {
      int len = Math.min(windowSamples, c.audio().length - from);
      if (len < c.sampleRate()) { // ignore trailing <1s remainder
        if (windows > 0) {
          break;
        }
      }
      double[] window = new double[len];
      System.arraycopy(c.audio(), from, window, 0, len);
      List<NoteEvent> events = engine.detect(window, c.sampleRate());
      for (String tone : toneClassesInRange(events)) {
        detected.add(tone);
      }
      long used = rt.totalMemory() - rt.freeMemory();
      peakUsed = Math.max(peakUsed, used);
      windows++;
      if (len < windowSamples) {
        break;
      }
    }
    long elapsed = System.nanoTime() - start;

    BaselineComparison.Metrics m =
        new BaselineComparison.Metrics(engine.name(), "tone-classes", c.truth(), detected, elapsed);
    double totalMs = elapsed / 1_000_000.0;
    double msPerWindow = windows > 0 ? totalMs / windows : totalMs;
    double ramMb = Math.max(0, peakUsed - usedBefore) / (1024.0 * 1024.0);
    return new Row(
        c.name(),
        engine.name(),
        m.precision(),
        m.recall(),
        m.f1(),
        totalMs,
        msPerWindow,
        ramMb,
        detected);
  }

  /**
   * Extracts tone classes (octave-independent) from events, applying the fair MIDI range filter.
   */
  private static Set<String> toneClassesInRange(List<NoteEvent> events) {
    List<NoteEvent> kept = new ArrayList<>();
    for (NoteEvent e : events) {
      int midi = (int) Math.round(e.midi());
      if (midi >= MIN_MIDI && midi <= MAX_MIDI) {
        kept.add(e);
      }
    }
    return BaselineComparison.toneClasses(kept);
  }

  // ---------------------------------------------------------------------------------------
  // reporting
  // ---------------------------------------------------------------------------------------

  private static void printMarkdown(List<Case> cases, List<Row> rows, boolean hasBasicPitch) {
    System.out.println("[DEBUG_LOG] ");
    System.out.println(
        "[DEBUG_LOG] === Phase-0 A/B-Baseline (tone classes, MIDI 48..96, 2s/1s windows) ===");
    System.out.println("[DEBUG_LOG] Basic Pitch (real ONNX) available: " + hasBasicPitch);
    System.out.println(
        "[DEBUG_LOG] | Case | Engine | P | R | F1 | t total (ms) | t/window (ms) | ~RAM (MB) | detected |");
    System.out.println("[DEBUG_LOG] |---|---|---:|---:|---:|---:|---:|---:|---|");
    for (Row r : rows) {
      System.out.println(
          String.format(
              Locale.ROOT,
              "[DEBUG_LOG] | %s | %s | %.2f | %.2f | %.2f | %.1f | %.1f | %.1f | %s |",
              r.caseName(),
              r.engine(),
              r.precision(),
              r.recall(),
              r.f1(),
              r.totalMs(),
              r.msPerWindow(),
              r.ramMb(),
              new TreeSet<>(r.detected())));
    }
    // Aggregate mean F1 per engine.
    summariseEngine(rows, "ChordDetector");
    if (hasBasicPitch) {
      summariseEngine(rows, "BasicPitch");
    }
    System.out.println("[DEBUG_LOG] Ground truth per case:");
    for (Case c : cases) {
      System.out.println("[DEBUG_LOG]   " + c.name() + " -> " + new TreeSet<>(c.truth()));
    }
  }

  private static void summariseEngine(List<Row> rows, String engine) {
    List<Row> engineRows = rows.stream().filter(r -> r.engine().equals(engine)).toList();
    if (engineRows.isEmpty()) {
      return;
    }
    double meanF1 = engineRows.stream().mapToDouble(Row::f1).average().orElse(0);
    double meanMsPerWindow = engineRows.stream().mapToDouble(Row::msPerWindow).average().orElse(0);
    System.out.println(
        String.format(
            Locale.ROOT,
            "[DEBUG_LOG] >> %-13s mean F1 = %.2f over %d cases, mean t/window = %.1f ms",
            engine,
            meanF1,
            engineRows.size(),
            meanMsPerWindow));
  }

  // ---------------------------------------------------------------------------------------
  // helpers: synthetic audio, real WAV loading, ONNX runner
  // ---------------------------------------------------------------------------------------

  /**
   * Concatenates several chord segments of equal length into one signal. Each {@code segment} is an
   * array of simultaneously sounding frequencies; segments are played back-to-back. Models an
   * alternating blow/draw harmonica passage (e.g. the C-octave / D-octave alternation in {@code
   * unbenannt4.wav}).
   */
  private static double[] sequence(int sampleRate, double segmentSeconds, double[]... segments) {
    int segSamples = (int) (sampleRate * segmentSeconds);
    double[] audio = new double[segSamples * segments.length];
    int offset = 0;
    for (double[] freqs : segments) {
      double[] seg = chord(sampleRate, segmentSeconds, freqs);
      System.arraycopy(seg, 0, audio, offset, seg.length);
      offset += seg.length;
    }
    return audio;
  }

  /** Generates a chord as the sum of equal-amplitude sine waves. */
  private static double[] chord(int sampleRate, double seconds, double... freqs) {
    int samples = (int) (sampleRate * seconds);
    double[] audio = new double[samples];
    for (int i = 0; i < samples; i++) {
      double t = (double) i / sampleRate;
      double sum = 0.0;
      for (double f : freqs) {
        sum += Math.sin(2 * Math.PI * f * t);
      }
      audio[i] = sum;
    }
    return audio;
  }

  /** Adds a real recording case if the WAV exists under {@code ~/Documents}; otherwise skips it. */
  private void addRealCase(List<Case> cases, String fileName, Set<String> truth) {
    Path wav = Paths.get(System.getProperty("user.home"), "Documents", fileName);
    if (!Files.isRegularFile(wav)) {
      System.out.println("[DEBUG_LOG] real recording not available, skipping row: " + wav);
      return;
    }
    try {
      WavData data = readWavMono(wav);
      cases.add(new Case(fileName, data.samples(), data.sampleRate(), truth));
    } catch (Exception e) {
      System.out.println("[DEBUG_LOG] failed to read " + wav + ": " + e + " -> skipping row");
    }
  }

  private OnnxBasicPitchModelRunner tryCreateOnnxRunner() {
    try {
      if (OnnxBasicPitchModelRunner.isBundledModelAvailable()) {
        return OnnxBasicPitchModelRunner.fromBundledModel();
      }
      Path model = VENV_MODEL;
      if (!Files.isRegularFile(model)) {
        model = Paths.get("..").resolve(VENV_MODEL).normalize();
      }
      if (Files.isRegularFile(model)) {
        return new OnnxBasicPitchModelRunner(model);
      }
    } catch (UnsatisfiedLinkError | NoClassDefFoundError e) {
      System.out.println("[DEBUG_LOG] ONNX native unavailable, Basic Pitch rows skipped: " + e);
    } catch (Exception e) {
      System.out.println(
          "[DEBUG_LOG] could not create ONNX runner, Basic Pitch rows skipped: " + e);
    }
    return null;
  }

  /** Minimal mono WAV payload (a class, not a record, to avoid array record components). */
  private static final class WavData {
    private final double[] samples;
    private final int sampleRate;

    WavData(double[] samples, int sampleRate) {
      this.samples = samples;
      this.sampleRate = sampleRate;
    }

    double[] samples() {
      return samples;
    }

    int sampleRate() {
      return sampleRate;
    }
  }

  /** Reads a PCM WAV file, downmixes to mono and normalizes to roughly [-1, 1]. */
  private static WavData readWavMono(Path wav) throws Exception {
    try (AudioInputStream in = AudioSystem.getAudioInputStream(wav.toFile())) {
      AudioFormat fmt = in.getFormat();
      AudioFormat pcm =
          new AudioFormat(
              AudioFormat.Encoding.PCM_SIGNED,
              fmt.getSampleRate(),
              16,
              fmt.getChannels(),
              fmt.getChannels() * 2,
              fmt.getSampleRate(),
              false);
      try (AudioInputStream pcmIn = AudioSystem.getAudioInputStream(pcm, in)) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[8192];
        int n;
        while ((n = pcmIn.read(chunk)) > 0) {
          buffer.write(chunk, 0, n);
        }
        byte[] bytes = buffer.toByteArray();
        int channels = pcm.getChannels();
        int frames = bytes.length / (2 * channels);
        double[] samples = new double[frames];
        for (int f = 0; f < frames; f++) {
          int acc = 0;
          for (int ch = 0; ch < channels; ch++) {
            int idx = (f * channels + ch) * 2;
            int lo = bytes[idx] & 0xFF;
            int hi = bytes[idx + 1];
            acc += (short) ((hi << 8) | lo);
          }
          samples[f] = (acc / (double) channels) / 32768.0;
        }
        return new WavData(samples, (int) pcm.getSampleRate());
      }
    }
  }
}
