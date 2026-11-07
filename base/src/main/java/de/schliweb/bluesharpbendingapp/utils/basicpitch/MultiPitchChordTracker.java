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

import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;
import de.schliweb.bluesharpbendingapp.utils.ChordDetectionResult;
import de.schliweb.bluesharpbendingapp.utils.LoggingContext;
import de.schliweb.bluesharpbendingapp.utils.LoggingUtils;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Runs a {@link MultiPitchEngine} (Basic Pitch) on a sliding window next to the per-buffer pitch
 * detection and provides its latest result as a {@link ChordDetectionResult}.
 *
 * <p>The microphone feeds every captured buffer via {@link #accept}. Once a full window is
 * available and a hop has elapsed, a snapshot of the window is analysed on a dedicated worker
 * thread; the audio thread never blocks, and hops are dropped while an analysis is still running.
 * {@link #latest()} returns the newest result as long as it is not older than the configured
 * maximum age, otherwise an empty result.
 *
 * <p>All access to the engine (inference, harmonica changes and closing) happens on the worker
 * thread, so the native ONNX Runtime session is never closed during an inference.
 */
public final class MultiPitchChordTracker implements AutoCloseable {

  private static final ChordDetectionResult EMPTY = new ChordDetectionResult(List.of(), 0.0);

  /** A result together with the number of samples written when its window ended. */
  private record Snapshot(ChordDetectionResult result, long windowEndSample) {}

  private final MultiPitchEngine engine;
  private final int sampleRate;
  private final double[] ring;
  private final int hopSamples;
  private final long maxAgeSamples;
  private final ExecutorService worker;
  private final AtomicBoolean busy = new AtomicBoolean();
  private final AtomicBoolean closed = new AtomicBoolean();
  private final AtomicReference<Snapshot> latest = new AtomicReference<>();

  private int writePos;
  // Written by the feeding thread only, read by latest() from another thread.
  private volatile long totalWritten;
  private long lastSubmitted;

  /**
   * Creates a tracker with its own daemon worker thread.
   *
   * @param engine the engine to run; owned by the tracker and closed with it
   * @param sampleRate the sample rate of the fed audio in Hz
   * @param windowSeconds the length of the analysed window
   * @param hopSeconds the time between two analyses
   * @param maxAgeSeconds how long a result stays valid after its window ended
   */
  public MultiPitchChordTracker(
      MultiPitchEngine engine,
      int sampleRate,
      double windowSeconds,
      double hopSeconds,
      double maxAgeSeconds) {
    this(
        engine,
        sampleRate,
        windowSeconds,
        hopSeconds,
        maxAgeSeconds,
        Executors.newSingleThreadExecutor(
            r -> {
              Thread t = new Thread(r, "multi-pitch-tracker");
              t.setDaemon(true);
              return t;
            }));
  }

  /**
   * Creates a tracker that runs the analyses on the given single-threaded executor (for tests).
   *
   * @param engine the engine to run; owned by the tracker and closed with it
   * @param sampleRate the sample rate of the fed audio in Hz
   * @param windowSeconds the length of the analysed window
   * @param hopSeconds the time between two analyses
   * @param maxAgeSeconds how long a result stays valid after its window ended
   * @param worker a single-threaded executor; shut down by {@link #close()}
   */
  MultiPitchChordTracker(
      MultiPitchEngine engine,
      int sampleRate,
      double windowSeconds,
      double hopSeconds,
      double maxAgeSeconds,
      ExecutorService worker) {
    this.engine = engine;
    this.sampleRate = sampleRate;
    this.ring = new double[(int) Math.round(windowSeconds * sampleRate)];
    this.hopSamples = Math.max(1, (int) Math.round(hopSeconds * sampleRate));
    this.maxAgeSamples = Math.round(maxAgeSeconds * sampleRate);
    this.worker = worker;
  }

  /**
   * Feeds captured samples. Must always be called from the same thread (the capture thread); {@link
   * #latest()} may be called from another one.
   *
   * @param samples the normalised samples
   * @param count the number of valid samples in {@code samples}
   */
  @SuppressWarnings("NonAtomicVolatileUpdate") // single writer thread, see above
  public void accept(double[] samples, int count) {
    if (closed.get()) {
      return;
    }
    for (int i = 0; i < count; i++) {
      ring[writePos] = samples[i];
      writePos = (writePos + 1) % ring.length;
    }
    totalWritten += count;
    if (totalWritten < ring.length || totalWritten - lastSubmitted < hopSamples) {
      return;
    }
    if (!busy.compareAndSet(false, true)) {
      return; // previous analysis still running: drop this hop
    }
    lastSubmitted = totalWritten;
    double[] window = new double[ring.length];
    for (int i = 0; i < ring.length; i++) {
      window[i] = ring[(writePos + i) % ring.length];
    }
    long windowEnd = totalWritten;
    try {
      worker.execute(() -> analyse(window, windowEnd));
    } catch (RejectedExecutionException e) {
      busy.set(false);
    }
  }

  private void analyse(double[] window, long windowEnd) {
    try {
      if (closed.get()) {
        return;
      }
      MultiPitchEngine.EngineResult result =
          engine.process(window, sampleRate, (double) windowEnd / sampleRate);
      List<Double> pitches =
          result.mapping().active().stream().map(a -> a.tone().freq()).distinct().toList();
      latest.set(
          new Snapshot(
              new ChordDetectionResult(
                  pitches, pitches.isEmpty() ? 0.0 : result.mapping().confidence()),
              windowEnd));
    } catch (RuntimeException e) {
      LoggingContext.setComponent("MultiPitchChordTracker");
      LoggingUtils.logError("Multi-pitch analysis failed", e);
    } finally {
      busy.set(false);
    }
  }

  /**
   * Returns the newest result, or an empty result if there is none or it is older than the maximum
   * age (measured in fed samples since the end of its window). May be called from any thread.
   *
   * @return the latest chord result
   */
  public ChordDetectionResult latest() {
    Snapshot snapshot = latest.get();
    if (snapshot == null || totalWritten - snapshot.windowEndSample() > maxAgeSamples) {
      return EMPTY;
    }
    return snapshot.result();
  }

  /**
   * Switches the harp key/tuning the results are mapped to. The pending result is discarded, since
   * it was mapped to the previous harmonica.
   *
   * @param harmonica the newly selected harmonica
   */
  public void setHarmonica(Harmonica harmonica) {
    if (closed.get()) {
      return;
    }
    try {
      worker.execute(
          () -> {
            engine.setHarmonica(harmonica);
            latest.set(null);
          });
    } catch (RejectedExecutionException e) {
      // closed concurrently: nothing to update
    }
  }

  /**
   * Returns the engine actually used (Basic Pitch, or the ChordDetector fallback).
   *
   * @return the selected engine of the underlying multi-pitch engine
   */
  public MultiPitchEngine.Engine engineType() {
    return engine.selectedEngine();
  }

  /**
   * Stops the analyses and releases the engine's native resources on the worker thread, after any
   * running analysis has finished. Idempotent.
   */
  @Override
  public void close() {
    if (!closed.compareAndSet(false, true)) {
      return;
    }
    latest.set(null);
    try {
      worker.execute(engine::close);
    } catch (RejectedExecutionException e) {
      engine.close();
    }
    worker.shutdown();
  }
}
