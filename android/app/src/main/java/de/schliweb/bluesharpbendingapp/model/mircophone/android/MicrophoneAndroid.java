package de.schliweb.bluesharpbendingapp.model.mircophone.android;
/*
 * Copyright (c) 2023 Christian Kierdorf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Process;
import de.schliweb.bluesharpbendingapp.model.microphone.AbstractMicrophone;
import de.schliweb.bluesharpbendingapp.model.microphone.Microphone;
import de.schliweb.bluesharpbendingapp.model.microphone.MicrophoneHandler;
import de.schliweb.bluesharpbendingapp.utils.*;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Implementation of the {@link Microphone} interface specific to the Android platform.
 * This class provides functionality for capturing and processing audio data from the device's microphone.
 * It allows real-time audio analysis, including pitch detection and amplitude measurement,
 * using configurable algorithms and threading mechanisms.
 * <p>
 * Main features include:
 * - Audio recording using Android's {@link AudioRecord} API.
 * - Customizable pitch detection algorithm (e.g., YIN, MPM).
 * - Multi-threaded processing with specialized executors for audio data capture and analysis.
 */
public class MicrophoneAndroid extends AbstractMicrophone {

    /**
     * The sample rate used for audio recording and processing.
     * <p>
     * This constant defines the number of audio samples captured per second
     * and is set to 44,100 Hz (CD-quality audio). It is a standard sample
     * rate suitable for most audio recording applications, providing
     * high fidelity audio data.
     * <p>
     * The sample rate is used in various audio processing operations, including
     * pitch detection and RMS (Root Mean Square) calculations, to ensure
     * consistent and accurate results.
     */
    private static final int SAMPLE_RATE = 44100;

    /**
     * The default audio buffer size used for capturing and processing audio data in the microphone system.
     * <p>
     * This value is determined by taking the maximum of a predefined constant size (4096 bytes)
     * and the minimum buffer size required by the {@link AudioRecord#getMinBufferSize(int, int, int)} API,
     * based on the configured sample rate, channel configuration, and encoding format.
     * <p>
     * A sufficiently large buffer size ensures that audio data is captured and processed
     * without underruns and provides reliable real-time performance for audio recording tasks.
     */
    private static final int BUFFER_SIZE = Math.max(4096, AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT));


    /**
     * A volatile instance of {@link MicrophoneHandler} used for handling
     * frequency and amplitude data from the microphone.
     * <p>
     * This variable allows communication between the processed audio data and
     * an external handler or observer. It is used to pass detected pitch
     * (frequency) and RMS (Root Mean Square) values of audio data to be managed
     * or displayed as needed. When a valid {@link MicrophoneHandler} is set,
     * it ensures that real-time audio processing results are effectively
     * utilized by the associated handler.
     * <p>
     * Since this field is defined as volatile, it maintains thread safety
     * and ensures visibility of updates across the different threads involved
     * in audio recording and processing.
     */
    private volatile MicrophoneHandler microphoneHandler;

    /**
     * The {@code executor} is a thread pool used to handle asynchronous or parallel tasks
     * related to the microphone's operation and audio data processing.
     * <p>
     * This {@link ExecutorService} enables the decoupling of audio data recording and
     * processing activities by distributing these tasks across multiple threads. It helps
     * enhance performance and maintain responsiveness, especially during real-time
     * audio analysis. Tasks in this thread pool may include operations like audio
     * data capture, queuing, and processing with selected pitch detection algorithms.
     * <p>
     * The thread pool's lifecycle must be carefully managed to ensure proper resource
     * cleanup when tasks are no longer needed, such as shutting down the pool when
     * the microphone is closed.
     * <p>
     * Note: The configuration of this {@code ExecutorService} (e.g., thread count)
     * can significantly impact the overall performance of audio-related operations.
     */
    private ExecutorService executor;

    /**
     * An {@link ExecutorService} used for managing the execution of audio processing tasks.
     * <p>
     * This thread pool is dedicated to handling audio data processing tasks that require
     * concurrency or background execution. It ensures that audio data is processed
     * asynchronously without blocking the main thread or audio recording tasks.
     * <p>
     * Specific tasks executed by this {@code ExecutorService} include:
     * - Processing raw audio data to detect pitch and calculate RMS.
     * - Applying the selected pitch detection algorithm (e.g., YIN, MPM).
     * - Sending processed audio information (frequency and RMS) to the {@code MicrophoneHandler}.
     * <p>
     * The configuration (e.g., thread pool size) and lifecycle management of this
     * {@code ExecutorService} are managed within the MicrophoneAndroid class.
     * <p>
     * Care must be taken to properly shut down the executor service to release system
     * resources when the microphone is closed or the application is terminated.
     */
    private ExecutorService processingExecutor;

    /**
     * Initializes and starts recording audio using the device's microphone.
     * The method sets up an AudioRecord instance and associated worker threads
     * to read, queue, and process audio data in a multi-threaded environment.
     * <p>
     * During operation:
     * - One thread is established for audio capture and reads raw audio data
     * into a buffer, which is subsequently added to a shared queue.
     * - Another thread fetches audio data from the queue and processes it
     * using the configured algorithm for further analysis.
     * <p>
     * The method handles any errors during initialization or audio capture,
     * ensuring proper resource cleanup such as stopping and releasing the AudioRecord
     * instance and interrupting threads when stopping the recording process.
     * <p>
     * Logs critical errors such as initialization failures or audio read errors
     * to assist with debugging and tracking application behavior.
     * <p>
     * If exceptions occur during the recording setup or execution, they are caught,
     * logged, and the method exits gracefully.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void open() {
        try {
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT, BUFFER_SIZE);

            if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                LoggingUtils.logError("Failed to initialize AudioRecord");
                return;
            }

            BlockingQueue<float[]> audioDataQueue = new LinkedBlockingQueue<>();
            processingExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            // Reusable buffer
            final float[] buffer = new float[BUFFER_SIZE / 4];

            executor = Executors.newFixedThreadPool(1);

            executor.execute(() -> {
                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                audioRecord.startRecording();

                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        int bytesRead = audioRecord.read(buffer, 0, buffer.length, AudioRecord.READ_BLOCKING);
                        if (bytesRead > 0) {
                            float[] dataCopy = new float[bytesRead];
                            System.arraycopy(buffer, 0, dataCopy, 0, bytesRead);
                            // add to queue
                            boolean offer = audioDataQueue.offer(dataCopy);
                            LoggingUtils.logDebug("Audio data added to queue", String.valueOf(offer));
                        } else if (bytesRead < 0) {
                            LoggingUtils.logError("AudioRecord read error", String.valueOf(bytesRead));
                            break;
                        }
                    }
                } finally {
                    audioRecord.stop();
                    audioRecord.release();
                    LoggingUtils.logOperationCompleted("Audio recording thread");
                }
            });

            // process in parallel
            processingExecutor.execute(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        float[] audioFrame = audioDataQueue.take();
                        // process
                        processAudioData(audioFrame, audioFrame.length);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        LoggingUtils.logOperationCompleted("Processing thread interrupted");
                    }
                }
            });

        } catch (Exception e) {
            LoggingUtils.logError("Failed to start recording", e);
        }
    }

    /**
     * Closes the microphone and releases associated resources.
     * <p>
     * This method shuts down the executor services used for audio recording and processing,
     * effectively stopping all audio capture and analysis operations. It ensures proper
     * cleanup of system resources when the microphone is no longer needed.
     * <p>
     * This implementation gracefully terminates the thread pools without forcibly
     * interrupting running tasks, allowing them to complete their current work.
     */
    @Override
    public void close() {
        if (executor != null) {
            executor.shutdown();
        }
        if (processingExecutor != null) {
            processingExecutor.shutdown();
        }
    }

    /**
     * Returns an array of supported microphone device names.
     * <p>
     * In the Android implementation, this method returns an empty array as
     * microphone selection is typically handled by the Android system rather
     * than the application. The default microphone is used for audio capture.
     * <p>
     * This method is primarily implemented to satisfy the interface contract
     * defined in the {@link Microphone} interface.
     *
     * @return an empty array of strings, as microphone selection is not
     *         supported in the Android implementation
     */
    @Override
    public String[] getSupportedMicrophones() {
        return new String[0];
    }


    /**
     * Sets the handler for microphone data processing.
     * <p>
     * This method assigns a {@link MicrophoneHandler} instance that will receive
     * and process the audio data captured by the microphone. The handler is responsible
     * for managing the detected pitch (frequency) and amplitude (RMS) values.
     * <p>
     * The handler is stored as a volatile field to ensure thread safety and visibility
     * across different threads involved in audio processing.
     *
     * @param microphoneHandler the handler instance that will receive microphone data,
     *                         or null to remove the current handler
     */
    @Override
    public void setMicrophoneHandler(MicrophoneHandler microphoneHandler) {
        this.microphoneHandler = microphoneHandler;
    }

    /**
     * Returns the name of the currently selected microphone device.
     * <p>
     * In the Android implementation, this method returns an empty string as
     * microphone selection is typically handled by the Android system rather
     * than the application. The default microphone is used for audio capture.
     * <p>
     * This method is primarily implemented to satisfy the interface contract
     * defined in the {@link Microphone} interface.
     *
     * @return an empty string, as microphone naming is not supported in the
     *         Android implementation
     */
    @Override
    public String getName() {
        return "";
    }

    /**
     * Sets the microphone device by index.
     * <p>
     * In the Android implementation, this method has no effect as microphone
     * selection is typically handled by the Android system rather than the
     * application. The default microphone is used for audio capture regardless
     * of the provided index.
     * <p>
     * This method is primarily implemented to satisfy the interface contract
     * defined in the {@link Microphone} interface.
     *
     * @param microphoneIndex the index of the microphone to select (ignored in Android implementation)
     */
    @Override
    public void setName(int microphoneIndex) {
        // Not needed on Android
    }


    /**
     * Processes the raw audio data from the microphone buffer to detect pitch and calculate RMS.
     *
     * <p>This method takes a buffer of raw audio data and the number of bytes read from the buffer,
     * converts it to a double array, and then uses a pitch detection algorithm to estimate the
     * fundamental frequency (pitch) and confidence level of the detected pitch. It also calculates
     * the Root Mean Square (RMS) of the audio data.</p>
     *
     * <p>The specific pitch detection algorithm used (YIN or MPM) is determined by the result of
     * {@link #getAlgorithm()}.</p>
     *
     * <p>If the confidence level is below a threshold specified by {@link #confidence}, the
     * detected pitch is discarded (set to -1).</p>
     *
     * <p>Finally, if a {@link #microphoneHandler} is set, it will be called with the detected
     * pitch and the calculated RMS.</p>
     *
     * @param buffer    The raw audio data buffer.
     * @param bytesRead The number of bytes read from the buffer.
     */
    private void processAudioData(float[] buffer, int bytesRead) {
        double[] audioData = new double[bytesRead];
        for (int i = 0; i < bytesRead; i++) {
            audioData[i] = buffer[i];
        }
        double conf = 0;
        double pitch = -1;
        PitchDetector.PitchDetectionResult result;
        ChordDetectionResult chordResult = PitchDetector.detectChord(audioData, SAMPLE_RATE);

        // Use the utility class for pitch detection, passing the SAMPLE_RATE as a parameter
        if ("YIN".equals(getAlgorithm())) {
            result = PitchDetector.detectPitchYIN(audioData, SAMPLE_RATE);
            pitch = result.pitch();
            conf = result.confidence();
        } else if ("MPM".equals(getAlgorithm())) {
            result = PitchDetector.detectPitchMPM(audioData, SAMPLE_RATE);
            pitch = result.pitch();
            conf = result.confidence();
        }

        if (conf < confidence) pitch = -1;
        if (microphoneHandler != null) {
            microphoneHandler.handle(pitch, PitchDetector.calcRMS(audioData), chordResult); // frequency, RMS
        }
    }

}
