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
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;
import de.schliweb.bluesharpbendingapp.utils.ChordDetectionResult;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class MultiPitchChordTrackerTest {

  private static final int SR = 44100;
  private static final int BUFFER = 4410;
  private static final Harmonica C_RICHTER =
      AbstractHarmonica.create(AbstractHarmonica.KEY.C, AbstractHarmonica.TUNE.RICHTER);

  /** An executor whose tasks only run when the test says so. */
  private static final class ManualExecutor extends AbstractExecutorService {
    final Queue<Runnable> tasks = new ArrayDeque<>();
    boolean shutdown;

    void runAll() {
      Runnable r;
      while ((r = tasks.poll()) != null) {
        r.run();
      }
    }

    @Override
    public void execute(Runnable command) {
      if (shutdown) {
        throw new RejectedExecutionException("shut down");
      }
      tasks.add(command);
    }

    @Override
    public void shutdown() {
      shutdown = true;
    }

    @Override
    public List<Runnable> shutdownNow() {
      shutdown = true;
      return List.copyOf(tasks);
    }

    @Override
    public boolean isShutdown() {
      return shutdown;
    }

    @Override
    public boolean isTerminated() {
      return shutdown && tasks.isEmpty();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) {
      return isTerminated();
    }
  }

  /** A runner that counts closes; used to observe when the engine releases its session. */
  private static final class CountingRunner implements BasicPitchModelRunner, AutoCloseable {
    final AtomicInteger closed = new AtomicInteger();

    @Override
    public BasicPitchOutput run(double[] audioData, double sampleRate) {
      return new BasicPitchOutput(new float[0][88], new float[0][88], new float[0][264], 86.0);
    }

    @Override
    public void close() {
      closed.incrementAndGet();
    }
  }

  private static double[] cMajorChord(double seconds) {
    double[] audio = new double[(int) (seconds * SR)];
    double[] freqs = {261.63, 329.63, 392.00};
    for (int i = 0; i < audio.length; i++) {
      double t = (double) i / SR;
      for (double f : freqs) {
        for (int k = 1; k <= 6; k++) {
          audio[i] += Math.sin(2 * Math.PI * f * k * t) / k / 6;
        }
      }
    }
    return audio;
  }

  private static void feed(MultiPitchChordTracker tracker, double[] audio) {
    for (int start = 0; start + BUFFER <= audio.length; start += BUFFER) {
      double[] buffer = java.util.Arrays.copyOfRange(audio, start, start + BUFFER);
      tracker.accept(buffer, BUFFER);
    }
  }

  private static MultiPitchChordTracker chordDetectorTracker(ManualExecutor executor) {
    MultiPitchEngine engine = MultiPitchEngine.createDefault();
    engine.setHarmonica(C_RICHTER);
    return new MultiPitchChordTracker(engine, SR, 1.0, 0.4, 0.6, executor);
  }

  @Test
  void noAnalysisBeforeTheWindowIsFull() {
    ManualExecutor executor = new ManualExecutor();
    MultiPitchChordTracker tracker = chordDetectorTracker(executor);
    feed(tracker, cMajorChord(0.9));
    assertTrue(executor.tasks.isEmpty());
    assertFalse(tracker.latest().hasPitches());
  }

  @Test
  void detectsChordAsHarpToneFrequencies() {
    ManualExecutor executor = new ManualExecutor();
    MultiPitchChordTracker tracker = chordDetectorTracker(executor);
    feed(tracker, cMajorChord(1.0));
    executor.runAll();

    ChordDetectionResult result = tracker.latest();

    assertEquals(3, result.getPitchCount(), "got " + result);
    for (double expected : new double[] {261.63, 329.63, 392.00}) {
      assertTrue(
          result.pitches().stream().anyMatch(p -> Math.abs(p - expected) < 1.0),
          expected + " missing in " + result);
    }
    assertTrue(result.confidence() > 0);
  }

  @Test
  void dropsHopsWhileAnAnalysisIsPending() {
    ManualExecutor executor = new ManualExecutor();
    MultiPitchChordTracker tracker = chordDetectorTracker(executor);
    feed(tracker, cMajorChord(2.5)); // several hops, but nothing has run yet
    assertEquals(1, executor.tasks.size());
  }

  @Test
  void resultExpiresWhenNoNewerAnalysisArrives() {
    ManualExecutor executor = new ManualExecutor();
    MultiPitchChordTracker tracker = chordDetectorTracker(executor);
    feed(tracker, cMajorChord(1.0));
    executor.runAll();
    assertTrue(tracker.latest().hasPitches());

    // Keep feeding without running the worker: the result ages beyond 0.6 s.
    executor.shutdown(); // further submissions are rejected and ignored
    feed(tracker, new double[(int) (0.8 * SR)]);

    assertFalse(tracker.latest().hasPitches());
  }

  @Test
  void harmonicaChangeDiscardsTheOldResult() {
    ManualExecutor executor = new ManualExecutor();
    MultiPitchChordTracker tracker = chordDetectorTracker(executor);
    feed(tracker, cMajorChord(1.0));
    executor.runAll();
    assertTrue(tracker.latest().hasPitches());

    tracker.setHarmonica(
        AbstractHarmonica.create(AbstractHarmonica.KEY.A, AbstractHarmonica.TUNE.RICHTER));
    executor.runAll();

    assertFalse(tracker.latest().hasPitches());
  }

  @Test
  void closeReleasesTheSessionOnTheWorkerAndIsIdempotent() {
    ManualExecutor executor = new ManualExecutor();
    CountingRunner runner = new CountingRunner();
    MultiPitchEngine engine =
        new MultiPitchEngine(
            MultiPitchEngine.Engine.BASIC_PITCH, new BasicPitchProvider(runner), C_RICHTER, false);
    MultiPitchChordTracker tracker =
        new MultiPitchChordTracker(engine, SR, 1.0, 0.4, 0.6, executor);

    tracker.close();
    assertEquals(0, runner.closed.get(), "must not close outside the worker thread");
    executor.runAll();
    assertEquals(1, runner.closed.get());

    tracker.close();
    feed(tracker, cMajorChord(1.5));
    executor.runAll();
    assertEquals(1, runner.closed.get());
    assertTrue(executor.isShutdown());
  }
}
