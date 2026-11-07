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
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

/**
 * Micro-benchmark for {@link OnnxBasicPitchModelRunner} inference latency under different {@link
 * OrtSession.SessionOptions}. It is meant as a measurement tool, not a hard pass/fail test: it
 * always asserts only that inference produced output, and prints the measured latencies (median /
 * min / max over several iterations) using the {@code [DEBUG_LOG]} prefix so the numbers show up in
 * the test output.
 *
 * <p>The benchmark is skipped automatically when the bundled {@code nmp.ort} model or the ONNX
 * Runtime native library is not available on the current platform, so it never breaks a normal
 * build.
 *
 * <p>Because results depend heavily on the host CPU, treat the printed numbers as relative
 * comparisons between the configured {@link OrtSession.SessionOptions}, not as absolute targets.
 */
class OnnxBasicPitchModelRunnerBenchmarkTest {

  /** Length of the synthetic benchmark signal in seconds. */
  private static final double SIGNAL_SECONDS = 2.0;

  /** Sample rate of the synthetic benchmark signal. */
  private static final double SAMPLE_RATE = 22050;

  /** Number of warm-up inferences (JIT/graph warm-up) excluded from the measurement. */
  private static final int WARMUP_ITERATIONS = 2;

  /** Number of measured inferences per configuration. */
  private static final int MEASURED_ITERATIONS = 10;

  @Test
  void benchmarkSessionOptionConfigurations() throws Exception {
    assumeTrue(
        OnnxBasicPitchModelRunner.isBundledModelAvailable(),
        "bundled Basic Pitch ONNX model not available; skipping benchmark");

    double[] audio = synthesizeChord();

    // Configurations to compare. Each entry creates a fresh SessionOptions instance so the runtime
    // builds an independent session. The default configuration mirrors the previous production code
    // (plain "new SessionOptions()"); the others enable graph optimization and constrain threading,
    // which tends to help for the small Basic Pitch model in a real-time audio context.
    List<Config> configs = new ArrayList<>();
    configs.add(new Config("default (no options set)", OrtSession.SessionOptions::new));
    configs.add(
        new Config(
            "ALL_OPT, intraOp=1, sequential",
            () -> {
              OrtSession.SessionOptions o = new OrtSession.SessionOptions();
              try {
                o.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT);
                o.setExecutionMode(OrtSession.SessionOptions.ExecutionMode.SEQUENTIAL);
                o.setIntraOpNumThreads(1);
                o.setInterOpNumThreads(1);
                o.setMemoryPatternOptimization(true);
              } catch (OrtException e) {
                throw new IllegalStateException(e);
              }
              return o;
            }));
    configs.add(
        new Config(
            "ALL_OPT, intraOp=2, sequential",
            () -> {
              OrtSession.SessionOptions o = new OrtSession.SessionOptions();
              try {
                o.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT);
                o.setExecutionMode(OrtSession.SessionOptions.ExecutionMode.SEQUENTIAL);
                o.setIntraOpNumThreads(2);
                o.setInterOpNumThreads(1);
                o.setMemoryPatternOptimization(true);
              } catch (OrtException e) {
                throw new IllegalStateException(e);
              }
              return o;
            }));

    System.out.println(
        "[DEBUG_LOG] Basic Pitch inference benchmark: signal="
            + SIGNAL_SECONDS
            + "s @ "
            + (int) SAMPLE_RATE
            + " Hz, warmup="
            + WARMUP_ITERATIONS
            + ", measured="
            + MEASURED_ITERATIONS
            + " iterations");

    boolean anyConfigRan = false;
    for (Config config : configs) {
      OnnxBasicPitchModelRunner runner;
      try {
        runner = OnnxBasicPitchModelRunner.fromBundledModel(config.options.get());
      } catch (UnsatisfiedLinkError | NoClassDefFoundError e) {
        assumeTrue(
            false, "ONNX Runtime native library unavailable on this platform; skipping: " + e);
        return;
      }

      try (runner) {
        // Warm-up (and a correctness sanity check on the first run).
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
          BasicPitchOutput out = runner.run(audio, SAMPLE_RATE);
          assertTrue(
              out.note().length > 0, "model must produce note frames for config: " + config.name);
        }

        long[] timingsNanos = new long[MEASURED_ITERATIONS];
        for (int i = 0; i < MEASURED_ITERATIONS; i++) {
          long start = System.nanoTime();
          BasicPitchOutput out = runner.run(audio, SAMPLE_RATE);
          timingsNanos[i] = System.nanoTime() - start;
          assertTrue(out.note().length > 0, "model must produce note frames");
        }

        Arrays.sort(timingsNanos);
        double medianMs = timingsNanos[timingsNanos.length / 2] / 1_000_000.0;
        double minMs = timingsNanos[0] / 1_000_000.0;
        double maxMs = timingsNanos[timingsNanos.length - 1] / 1_000_000.0;

        System.out.printf(
            "[DEBUG_LOG] %-34s median=%7.2f ms  min=%7.2f ms  max=%7.2f ms%n",
            config.name, medianMs, minMs, maxMs);
        anyConfigRan = true;
      }
    }

    assertTrue(anyConfigRan, "at least one configuration must have been benchmarked");
  }

  /**
   * Measures how the inference latency scales with the analysed buffer length. The production code
   * feeds a sliding window into {@link OnnxBasicPitchModelRunner#run}, which splits the buffer into
   * several model windows; shortening the window is therefore the cheapest way to reduce latency.
   * This benchmark quantifies the expected gain of going from a 2 s to a 1 s window (using the
   * default session options, which the configuration benchmark showed to be the fastest).
   */
  @Test
  void benchmarkSignalLengths() throws Exception {
    assumeTrue(
        OnnxBasicPitchModelRunner.isBundledModelAvailable(),
        "bundled Basic Pitch ONNX model not available; skipping benchmark");

    double[] lengthsSeconds = {2.0, 1.0, 0.5};

    System.out.println(
        "[DEBUG_LOG] Basic Pitch inference benchmark by signal length @ "
            + (int) SAMPLE_RATE
            + " Hz, default options, warmup="
            + WARMUP_ITERATIONS
            + ", measured="
            + MEASURED_ITERATIONS
            + " iterations");

    boolean anyRan = false;
    for (double seconds : lengthsSeconds) {
      double[] audio = synthesizeChord(seconds);
      OnnxBasicPitchModelRunner runner;
      try {
        runner = OnnxBasicPitchModelRunner.fromBundledModel();
      } catch (UnsatisfiedLinkError | NoClassDefFoundError e) {
        assumeTrue(
            false, "ONNX Runtime native library unavailable on this platform; skipping: " + e);
        return;
      }

      try (runner) {
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
          BasicPitchOutput out = runner.run(audio, SAMPLE_RATE);
          assertTrue(out.note().length > 0, "model must produce note frames");
        }
        long[] timingsNanos = new long[MEASURED_ITERATIONS];
        for (int i = 0; i < MEASURED_ITERATIONS; i++) {
          long start = System.nanoTime();
          BasicPitchOutput out = runner.run(audio, SAMPLE_RATE);
          timingsNanos[i] = System.nanoTime() - start;
          assertTrue(out.note().length > 0, "model must produce note frames");
        }
        Arrays.sort(timingsNanos);
        double medianMs = timingsNanos[timingsNanos.length / 2] / 1_000_000.0;
        double minMs = timingsNanos[0] / 1_000_000.0;
        double maxMs = timingsNanos[timingsNanos.length - 1] / 1_000_000.0;
        System.out.printf(
            "[DEBUG_LOG] signal=%4.1f s   median=%7.2f ms  min=%7.2f ms  max=%7.2f ms%n",
            seconds, medianMs, minMs, maxMs);
        anyRan = true;
      }
    }

    assertTrue(anyRan, "at least one signal length must have been benchmarked");
  }

  /** Synthesizes a C major chord (C4/E4/G4) used as a deterministic benchmark signal. */
  private static double[] synthesizeChord() {
    return synthesizeChord(SIGNAL_SECONDS);
  }

  /** Synthesizes a C major chord (C4/E4/G4) of the given length used as a benchmark signal. */
  private static double[] synthesizeChord(double seconds) {
    int n = (int) (SAMPLE_RATE * seconds);
    double[] audio = new double[n];
    double[] freqs = {261.63, 329.63, 392.00};
    for (int i = 0; i < n; i++) {
      double t = i / SAMPLE_RATE;
      double v = 0;
      for (double f : freqs) {
        v += Math.sin(2 * Math.PI * f * t);
      }
      audio[i] = 0.3 * v / freqs.length;
    }
    return audio;
  }

  /** A named benchmark configuration providing a fresh {@link OrtSession.SessionOptions}. */
  private record Config(String name, Supplier<OrtSession.SessionOptions> options) {}
}
