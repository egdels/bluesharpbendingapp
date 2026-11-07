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
import de.schliweb.bluesharpbendingapp.utils.LoggingContext;
import de.schliweb.bluesharpbendingapp.utils.LoggingUtils;
import java.util.List;

/**
 * Orchestrates the multi-pitch recognition pipeline behind a single, platform-independent entry
 * point and ties together the deterministic stages described in the integration concept ({@code
 * docs/Integrationskonzept_BasicPitch_LetsBend.md} §4–§6):
 *
 * <pre>
 *   audio → {@link MultiPitchDetector} → NoteEvents
 *         → {@link MultiPitchPostprocessor} (artefact filter)
 *         → {@link HarpPitchMapper} (key/tuning-aware mapping + plausibility)
 *         → {@link MappingResult} (active harp channels)
 * </pre>
 *
 * <p>The engine is selectable ({@link Engine#CHORD_DETECTOR}, default, vs. {@link
 * Engine#BASIC_PITCH}, experimental). Basic Pitch produces only {@link NoteEvent}s — never chords
 * directly; the harmonica-specific interpretation stays in the deterministic mapper. Errors in the
 * selected detector never propagate: the engine transparently falls back to the established {@code
 * ChordDetector}. The class is detector-agnostic and therefore unit-testable without an audio model
 * (inject a fake {@link MultiPitchDetector}); the heavy ONNX model is created once via {@link
 * #createBasicPitch(Harmonica)} and reused across frames, so the model is never initialised per
 * audio frame. {@link #process(double[], double, double)} is intended to run on a worker thread so
 * UI rendering is not blocked by inference.
 */
public final class MultiPitchEngine implements AutoCloseable {

  /** Selectable multi-pitch engine. */
  public enum Engine {
    /** The existing FFT/HPS/NMF {@code ChordDetector} (default). */
    CHORD_DETECTOR,
    /** The experimental ML-based Basic Pitch (ONNX) engine. */
    BASIC_PITCH
  }

  /** Default safety margin (semitones) for the dynamically derived MIDI range. */
  public static final int DEFAULT_MIDI_MARGIN = MultiPitchPostprocessConfig.DEFAULT_MIDI_MARGIN;

  /**
   * The outcome of one {@link #process(double[], double, double)} call, including the debug
   * information requested by the issue (chosen engine, inference time, raw/filtered events, mapped
   * channels).
   *
   * @param mapping the mapped active harp channels for this frame
   * @param engineUsed the engine that actually produced the result (may differ from the selected
   *     engine when a fallback happened)
   * @param fellBack whether a fallback to the {@code ChordDetector} happened for this frame
   * @param inferenceMillis the wall-clock time of the detector inference in milliseconds
   * @param rawEvents the raw note events returned by the detector
   * @param keptEvents the note events that survived the artefact filter
   * @param filteredEvents the note events removed by the artefact filter (with reasons)
   * @param technique the playing technique classified from the mapped active channels
   */
  public record EngineResult(
      MappingResult mapping,
      Engine engineUsed,
      boolean fellBack,
      double inferenceMillis,
      List<NoteEvent> rawEvents,
      List<NoteEvent> keptEvents,
      List<MultiPitchPostprocessor.RejectedEvent> filteredEvents,
      PlayingTechniqueClassifier.Classification technique) {}

  private final MultiPitchDetector primaryDetector;
  private final MultiPitchDetector fallbackDetector;
  private final Engine selectedEngine;
  private final boolean debug;

  private volatile HarpPitchMapper mapper;
  private volatile MultiPitchPostprocessConfig postprocessConfig;

  /**
   * Creates an engine with an explicit primary detector and the given harmonica.
   *
   * @param selectedEngine the selected engine identifier (used for logging/UI)
   * @param primaryDetector the detector to use; {@code null} forces the fallback
   * @param harmonica the currently selected harmonica (key + tuning); {@code null} uses Richter C
   * @param debug whether to emit detailed debug logging per frame
   */
  public MultiPitchEngine(
      Engine selectedEngine,
      MultiPitchDetector primaryDetector,
      Harmonica harmonica,
      boolean debug) {
    this.selectedEngine = selectedEngine != null ? selectedEngine : Engine.CHORD_DETECTOR;
    this.fallbackDetector = new ChordDetectorMultiPitchAdapter();
    this.primaryDetector =
        this.selectedEngine == Engine.CHORD_DETECTOR || primaryDetector == null
            ? this.fallbackDetector
            : primaryDetector;
    this.debug = debug;
    setHarmonica(harmonica);
  }

  /**
   * Creates the default engine ({@code ChordDetector}, Richter C, no debug).
   *
   * @return the default engine
   */
  public static MultiPitchEngine createDefault() {
    return new MultiPitchEngine(Engine.CHORD_DETECTOR, null, null, false);
  }

  /**
   * Creates a Basic-Pitch engine using the bundled {@code nmp.onnx} model, falling back to the
   * {@code ChordDetector} engine if the model (or the ONNX runtime native) is unavailable. The
   * model is loaded once and reused for the lifetime of the returned engine, so inference never
   * re-initialises the model per frame. Any error while loading Basic Pitch is logged and never
   * crashes the caller.
   *
   * @param harmonica the currently selected harmonica (key + tuning)
   * @return a Basic-Pitch engine, or a {@code ChordDetector} engine if Basic Pitch is unavailable
   */
  public static MultiPitchEngine createBasicPitch(Harmonica harmonica) {
    LoggingContext.setComponent("MultiPitchEngine");
    try {
      OnnxBasicPitchModelRunner runner = OnnxBasicPitchModelRunner.fromBundledModel();
      MultiPitchDetector provider = new BasicPitchProvider(runner);
      LoggingUtils.logDebug("Basic Pitch engine initialised (bundled nmp.onnx)");
      return new MultiPitchEngine(Engine.BASIC_PITCH, provider, harmonica, false);
    } catch (Throwable t) {
      // Includes OrtException, IOException and (Native)LinkageError if the ONNX runtime is missing.
      LoggingUtils.logWarning(
          "Basic Pitch unavailable, falling back to ChordDetector: " + t.getMessage());
      return new MultiPitchEngine(Engine.CHORD_DETECTOR, null, harmonica, false);
    }
  }

  /**
   * Rebuilds the key/tuning-aware mapper and the dynamic MIDI range for the given harmonica. Safe
   * to call whenever the user changes the harp key or tuning.
   *
   * @param harmonica the currently selected harmonica; {@code null} uses a Richter C mapper
   */
  public void setHarmonica(Harmonica harmonica) {
    HarpPitchMapper m =
        harmonica != null
            ? HarpPitchMapper.forHarmonica(harmonica, MappingConfig.defaultConfig())
            : new HarpPitchMapper();
    this.mapper = m;
    this.postprocessConfig = MultiPitchPostprocessConfig.forHarp(m, DEFAULT_MIDI_MARGIN);
  }

  /**
   * Returns the engine identifier selected by the user (independent of any per-frame fallback).
   *
   * @return the selected engine
   */
  public Engine selectedEngine() {
    return selectedEngine;
  }

  /**
   * Returns the harp tone mapper currently in use (key/tuning aware).
   *
   * @return the mapper
   */
  public HarpPitchMapper mapper() {
    return mapper;
  }

  /**
   * Returns the dynamic postprocessing configuration currently in use.
   *
   * @return the postprocessing configuration
   */
  public MultiPitchPostprocessConfig postprocessConfig() {
    return postprocessConfig;
  }

  /**
   * Runs the full pipeline for one audio buffer. Detector errors are caught and trigger a
   * transparent fallback to the {@code ChordDetector}, so this method never throws because of the
   * detector. Intended to be invoked from a worker thread.
   *
   * @param audioData the mono audio samples (normalised to {@code [-1, 1]})
   * @param sampleRate the sample rate of {@code audioData} in Hz
   * @param timeSeconds the time stamp for the produced {@link MappingResult}
   * @return the engine result including debug information
   */
  public EngineResult process(double[] audioData, double sampleRate, double timeSeconds) {
    LoggingContext.setComponent("MultiPitchEngine");
    Engine used = selectedEngine;
    boolean fellBack = false;
    long t0 = System.nanoTime();
    List<NoteEvent> rawEvents;
    try {
      rawEvents = primaryDetector.detect(audioData, sampleRate);
    } catch (Throwable t) {
      LoggingUtils.logWarning(
          "Detector '"
              + primaryDetector.name()
              + "' failed, falling back to ChordDetector: "
              + t.getMessage());
      rawEvents = fallbackDetector.detect(audioData, sampleRate);
      used = Engine.CHORD_DETECTOR;
      fellBack = true;
    }
    double inferenceMillis = (System.nanoTime() - t0) / 1_000_000.0;

    MultiPitchPostprocessor.FilterResult filtered =
        MultiPitchPostprocessor.filterNoteEvents(rawEvents, postprocessConfig);
    MappingResult mapping = mapper.mapNotes(filtered.kept(), timeSeconds);
    PlayingTechniqueClassifier.Classification technique =
        PlayingTechniqueClassifier.classify(mapping);

    if (debug) {
      LoggingUtils.logDebug(
          String.format(
              "engine=%s backend=%s inference=%.1fms rawEvents=%d kept=%d filtered=%d channels=%s technique=%s",
              selectedEngine,
              used == Engine.BASIC_PITCH ? "ONNX" : "FFT",
              inferenceMillis,
              rawEvents.size(),
              filtered.kept().size(),
              filtered.rejected().size(),
              mapping.labels(),
              technique.technique()));
    }

    return new EngineResult(
        mapping,
        used,
        fellBack,
        inferenceMillis,
        rawEvents,
        filtered.kept(),
        filtered.rejected(),
        technique);
  }

  /**
   * Releases the native resources of the primary detector (the ONNX Runtime session of Basic
   * Pitch). Must be called when the engine is replaced or no longer used; otherwise every
   * re-creation leaks a native session. Safe to call more than once.
   */
  @Override
  public void close() {
    if (primaryDetector != fallbackDetector) {
      primaryDetector.close();
    }
  }
}
