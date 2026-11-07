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

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OnnxValue;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Desktop/JVM implementation of {@link BasicPitchModelRunner} backed by the ONNX Runtime for Java
 * ({@code com.microsoft.onnxruntime}). It runs the Basic Pitch ICASSP-2022 model ({@code nmp.onnx})
 * and reproduces the windowing/unwrapping of the reference Python implementation ({@code
 * basic_pitch.inference.run_inference}) so that the resulting {@link BasicPitchOutput} frame
 * tensors match the prototype.
 *
 * <p>Processing steps:
 *
 * <ol>
 *   <li>resample the input to {@value AudioResampler#TARGET_SAMPLE_RATE} Hz mono ({@link
 *       AudioResampler}),
 *   <li>pad {@value #OVERLAP_LEN}/2 zeros to the front,
 *   <li>cut into overlapping windows of {@value #AUDIO_N_SAMPLES} samples (hop {@value #HOP_SIZE}),
 *   <li>run the model per window (input {@code serving_default_input_2:0}, outputs {@code
 *       StatefulPartitionedCall:1/2/0} = note/onset/contour),
 *   <li>drop {@value #N_OVERLAPPING_FRAMES}/2 overlap frames at both ends of each window,
 *       concatenate and trim to the original audio length.
 * </ol>
 *
 * <p>The instance owns an {@link OrtSession} and must be {@link #close() closed}. The ONNX Runtime
 * dependency is provided by the desktop module at runtime; Android will use its own runner later.
 */
public final class OnnxBasicPitchModelRunner implements BasicPitchModelRunner, AutoCloseable {

  /** Number of input samples per window (Basic Pitch: 22050 * 2 - 256). */
  public static final int AUDIO_N_SAMPLES = 43844;

  /** FFT hop of the model (samples per output frame). */
  public static final int FFT_HOP = 256;

  /** Output frame rate (integer, as used by the reference implementation for trimming). */
  public static final int ANNOTATIONS_FPS = AudioResampler.TARGET_SAMPLE_RATE / FFT_HOP;

  /** Number of overlapping frames between consecutive windows. */
  public static final int N_OVERLAPPING_FRAMES = 30;

  /** Overlap length in samples ({@value #N_OVERLAPPING_FRAMES} * {@value #FFT_HOP}). */
  public static final int OVERLAP_LEN = N_OVERLAPPING_FRAMES * FFT_HOP;

  /** Hop between window start positions in samples. */
  public static final int HOP_SIZE = AUDIO_N_SAMPLES - OVERLAP_LEN;

  /**
   * Classpath location of the bundled Basic Pitch ONNX model. The model (~225 KB) ships as a
   * resource in the {@code base} module so the desktop application can run inference without an
   * external model file (see {@link #fromBundledModel()}).
   */
  public static final String BUNDLED_MODEL_RESOURCE = "/basic_pitch/nmp.onnx";

  private static final String INPUT_NAME = "serving_default_input_2:0";
  private static final String OUTPUT_NOTE = "StatefulPartitionedCall:1";
  private static final String OUTPUT_ONSET = "StatefulPartitionedCall:2";
  private static final String OUTPUT_CONTOUR = "StatefulPartitionedCall:0";

  private final OrtEnvironment environment;
  private final OrtSession session;

  /** Whether {@link #close()} has already released the session. */
  private final java.util.concurrent.atomic.AtomicBoolean closed =
      new java.util.concurrent.atomic.AtomicBoolean();

  /**
   * Creates a runner for the ONNX model at the given path.
   *
   * @param modelPath the path to {@code nmp.onnx}
   * @throws OrtException if the model cannot be loaded
   * @throws IllegalArgumentException if {@code modelPath} is {@code null}
   */
  public OnnxBasicPitchModelRunner(Path modelPath) throws OrtException {
    this(modelPath, new OrtSession.SessionOptions());
  }

  /**
   * Creates a runner for the ONNX model at the given path using the supplied session options. This
   * overload exists mainly to benchmark different {@link OrtSession.SessionOptions} (graph
   * optimization level, thread counts, execution mode, ...).
   *
   * @param modelPath the path to {@code nmp.onnx}
   * @param options the session options to use
   * @throws OrtException if the model cannot be loaded
   * @throws IllegalArgumentException if {@code modelPath} or {@code options} is {@code null}
   */
  public OnnxBasicPitchModelRunner(Path modelPath, OrtSession.SessionOptions options)
      throws OrtException {
    if (modelPath == null) {
      throw new IllegalArgumentException("modelPath must not be null");
    }
    if (options == null) {
      throw new IllegalArgumentException("options must not be null");
    }
    this.environment = OrtEnvironment.getEnvironment();
    this.session = environment.createSession(modelPath.toString(), options);
  }

  /**
   * Creates a runner from the raw bytes of the ONNX model (e.g. a bundled classpath resource).
   *
   * @param modelBytes the serialized ONNX model
   * @throws OrtException if the model cannot be loaded
   * @throws IllegalArgumentException if {@code modelBytes} is {@code null} or empty
   */
  public OnnxBasicPitchModelRunner(byte[] modelBytes) throws OrtException {
    this(modelBytes, new OrtSession.SessionOptions());
  }

  /**
   * Creates a runner from the raw bytes of the ONNX model using the supplied session options.
   *
   * @param modelBytes the serialized ONNX model
   * @param options the session options to use
   * @throws OrtException if the model cannot be loaded
   * @throws IllegalArgumentException if {@code modelBytes} is {@code null}/empty or {@code options}
   *     is {@code null}
   */
  public OnnxBasicPitchModelRunner(byte[] modelBytes, OrtSession.SessionOptions options)
      throws OrtException {
    if (modelBytes == null || modelBytes.length == 0) {
      throw new IllegalArgumentException("modelBytes must not be null or empty");
    }
    if (options == null) {
      throw new IllegalArgumentException("options must not be null");
    }
    this.environment = OrtEnvironment.getEnvironment();
    this.session = environment.createSession(modelBytes, options);
  }

  /**
   * Loads the model from the given classpath resource.
   *
   * @param resourcePath the absolute classpath resource path (e.g. {@code /basic_pitch/nmp.onnx})
   * @return a runner backed by the resource
   * @throws OrtException if the model cannot be loaded
   * @throws IOException if the resource cannot be read
   * @throws IllegalArgumentException if the resource does not exist
   */
  public static OnnxBasicPitchModelRunner fromResource(String resourcePath)
      throws OrtException, IOException {
    return fromResource(resourcePath, new OrtSession.SessionOptions());
  }

  /**
   * Loads the model from the given classpath resource using the supplied session options.
   *
   * @param resourcePath the absolute classpath resource path (e.g. {@code /basic_pitch/nmp.onnx})
   * @param options the session options to use
   * @return a runner backed by the resource
   * @throws OrtException if the model cannot be loaded
   * @throws IOException if the resource cannot be read
   * @throws IllegalArgumentException if the resource does not exist
   */
  public static OnnxBasicPitchModelRunner fromResource(
      String resourcePath, OrtSession.SessionOptions options) throws OrtException, IOException {
    try (InputStream in = OnnxBasicPitchModelRunner.class.getResourceAsStream(resourcePath)) {
      if (in == null) {
        throw new IllegalArgumentException("ONNX model resource not found: " + resourcePath);
      }
      return new OnnxBasicPitchModelRunner(in.readAllBytes(), options);
    }
  }

  /**
   * Loads the Basic Pitch model bundled as a classpath resource at {@value
   * #BUNDLED_MODEL_RESOURCE}.
   *
   * @return a runner backed by the bundled model
   * @throws OrtException if the model cannot be loaded
   * @throws IOException if the bundled resource cannot be read
   * @throws IllegalArgumentException if the bundled resource is missing
   */
  public static OnnxBasicPitchModelRunner fromBundledModel() throws OrtException, IOException {
    return fromResource(BUNDLED_MODEL_RESOURCE);
  }

  /**
   * Loads the bundled Basic Pitch model using the supplied session options.
   *
   * @param options the session options to use
   * @return a runner backed by the bundled model
   * @throws OrtException if the model cannot be loaded
   * @throws IOException if the bundled resource cannot be read
   * @throws IllegalArgumentException if the bundled resource is missing
   */
  public static OnnxBasicPitchModelRunner fromBundledModel(OrtSession.SessionOptions options)
      throws OrtException, IOException {
    return fromResource(BUNDLED_MODEL_RESOURCE, options);
  }

  /**
   * Indicates whether the bundled model resource is present on the classpath.
   *
   * @return {@code true} if {@value #BUNDLED_MODEL_RESOURCE} can be resolved
   */
  public static boolean isBundledModelAvailable() {
    return isModelResourceAvailable(BUNDLED_MODEL_RESOURCE);
  }

  @Override
  public BasicPitchOutput run(double[] audioData, double sampleRate) {
    if (audioData == null) {
      throw new IllegalArgumentException("audioData must not be null");
    }
    double[] resampled = AudioResampler.resampleTo22050(audioData, sampleRate);
    int originalLength = resampled.length;

    // Pad OVERLAP_LEN/2 zeros to the front (reference get_audio_input).
    int frontPad = OVERLAP_LEN / 2;
    double[] padded = new double[frontPad + originalLength];
    System.arraycopy(resampled, 0, padded, frontPad, originalLength);

    List<float[][]> noteWindows = new ArrayList<>();
    List<float[][]> onsetWindows = new ArrayList<>();
    List<float[][]> contourWindows = new ArrayList<>();

    try {
      Set<String> requested = new LinkedHashSet<>();
      requested.add(OUTPUT_NOTE);
      requested.add(OUTPUT_ONSET);
      requested.add(OUTPUT_CONTOUR);

      for (int i = 0; i < padded.length; i += HOP_SIZE) {
        float[][][] input = new float[1][AUDIO_N_SAMPLES][1];
        for (int s = 0; s < AUDIO_N_SAMPLES; s++) {
          int idx = i + s;
          input[0][s][0] = idx < padded.length ? (float) padded[idx] : 0f;
        }
        try (OnnxTensor tensor = OnnxTensor.createTensor(environment, input);
            OrtSession.Result result =
                session.run(Collections.singletonMap(INPUT_NAME, tensor), requested)) {
          noteWindows.add(extractFrames(result, OUTPUT_NOTE));
          onsetWindows.add(extractFrames(result, OUTPUT_ONSET));
          contourWindows.add(extractFrames(result, OUTPUT_CONTOUR));
        }
      }
    } catch (OrtException e) {
      throw new IllegalStateException("Basic Pitch ONNX inference failed", e);
    }

    int outputFrames =
        (int)
            Math.floor(
                (double) originalLength * ANNOTATIONS_FPS / AudioResampler.TARGET_SAMPLE_RATE);
    float[][] note = unwrap(noteWindows, outputFrames);
    float[][] onset = unwrap(onsetWindows, outputFrames);
    float[][] contour = unwrap(contourWindows, outputFrames);

    return new BasicPitchOutput(
        note, onset, contour, (double) AudioResampler.TARGET_SAMPLE_RATE / FFT_HOP);
  }

  /** Extracts the {@code [frames][bins]} matrix of one window output, dropping the batch dim. */
  private static float[][] extractFrames(OrtSession.Result result, String name)
      throws OrtException {
    Optional<OnnxValue> value = result.get(name);
    if (value.isEmpty()) {
      throw new IllegalStateException("ONNX model did not return output: " + name);
    }
    float[][][] batched = (float[][][]) value.get().getValue();
    return batched[0];
  }

  /**
   * Reproduces {@code basic_pitch.inference.unwrap_output}: drops {@code N_OVERLAPPING_FRAMES/2}
   * frames at both ends of every window, concatenates the remainder and trims to {@code
   * outputFrames}.
   */
  private static float[][] unwrap(List<float[][]> windows, int outputFrames) {
    int nOlap = N_OVERLAPPING_FRAMES / 2;
    List<float[]> frames = new ArrayList<>();
    for (float[][] window : windows) {
      int end = window.length - nOlap;
      for (int f = nOlap; f < end; f++) {
        frames.add(window[f]);
      }
    }
    int total = Math.min(outputFrames, frames.size());
    if (total < 0) {
      total = 0;
    }
    float[][] out = new float[total][];
    for (int f = 0; f < total; f++) {
      out[f] = frames.get(f);
    }
    return out;
  }

  /** Returns whether the bundled ONNX model resource is available on the classpath. */
  public static boolean isModelResourceAvailable(String resourcePath) {
    return OnnxBasicPitchModelRunner.class.getResource(resourcePath) != null;
  }

  /** Convenience check: whether the given file exists and looks like an ONNX model. */
  public static boolean isModelFileAvailable(Path modelPath) {
    return modelPath != null && Files.isRegularFile(modelPath);
  }

  /**
   * Closes the ONNX Runtime session. Idempotent: further calls do nothing. Must not be called while
   * {@link #run} is executing on another thread; callers close the runner on the same thread that
   * runs inference.
   */
  @Override
  public void close() throws OrtException {
    if (closed.compareAndSet(false, true)) {
      session.close();
    }
  }
}
