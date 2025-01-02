package de.schliweb.bluesharpbendingapp.model.mircophone.android;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Process;
import de.schliweb.bluesharpbendingapp.model.microphone.Microphone;
import de.schliweb.bluesharpbendingapp.model.microphone.MicrophoneHandler;
import de.schliweb.bluesharpbendingapp.utils.Logger;
import de.schliweb.bluesharpbendingapp.utils.PitchDetectionUtil;

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
public class MicrophoneAndroid implements Microphone {

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
    private static final int BUFFER_SIZE = Math.max(4096, AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_FLOAT
    ));


    /**
     * A static logger instance used for logging messages related to the {@link MicrophoneAndroid} class.
     * <p>
     * This logger is initialized specifically for the {@link MicrophoneAndroid} class and assists with
     * capturing and recording debug, informational, and error messages during execution. It provides
     * a centralized mechanism to track the behavior and state of methods and operations within the class.
     * <p>
     * Typical use cases include:
     * - Logging errors that occur during microphone initialization or audio processing.
     * - Debugging issues related to the {@code open()}, {@code close()}, and associated methods.
     * - Capturing operational details for performance analysis or troubleshooting.
     * <p>
     * Being a static final instance, this logger is shared across all instances of {@link MicrophoneAndroid}
     * and cannot be modified after initialization.
     */
    private static final Logger logger = new Logger(MicrophoneAndroid.class);

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
     * Represents the currently selected pitch detection algorithm for processing audio data.
     * <p>
     * The algorithm variable determines which pitch detection method will be used while
     * processing audio data. Supported algorithms include:
     * - "YIN": Implements the YIN pitch detection algorithm, which is suitable for monophonic
     * signals and provides high accuracy for pitch detection.
     * - "MPM": Implements the McLeod Pitch Method (MPM) for fast and efficient pitch detection.
     * <p>
     * This variable is used internally by the microphone processing system, specifically in the
     * {@code processAudioData} method, to apply the appropriate pitch detection algorithm.
     * It can be retrieved or updated using the respective methods in the MicrophoneAndroid class.
     * <p>
     * Default value: "YIN"
     */
    private String algorithm = "YIN";
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
     * A thread-safe blocking queue used to store audio data buffers for processing.
     * <p>
     * This queue holds arrays of floating-point numbers, where each array represents
     * a segment of raw audio data captured from the microphone. The captured audio
     * data is enqueued by the recording thread and subsequently dequeued by a processing
     * thread for further analysis, such as pitch detection and RMS calculation.
     * <p>
     * The queue ensures proper synchronization between the producer (recording thread)
     * and the consumer (processing thread), preventing data loss or concurrent access
     * issues. Its size and behavior are configured to balance between memory
     * usage and processing latency.
     */
    private BlockingQueue<float[]> audioDataQueue;
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
            AudioRecord audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_FLOAT,
                    BUFFER_SIZE
            );

            if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                logger.error("Failed to initialize AudioRecord.");
                return;
            }

            audioDataQueue = new LinkedBlockingQueue<>();
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
                            audioDataQueue.offer(dataCopy);
                        } else if (bytesRead < 0) {
                            logger.error("AudioRecord read error: " + bytesRead);
                            break;
                        }
                    }
                } finally {
                    audioRecord.stop();
                    audioRecord.release();
                    logger.info("Audio recording thread stopped.");
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
                        logger.info("Processing thread interrupted.");
                    }
                }
            });

        } catch (Exception e) {
            logger.error("Failed to start recording: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        if (executor != null) {
            executor.shutdown();
        }
        if (processingExecutor != null) {
            processingExecutor.shutdown();
        }
    }

    @Override
    public String[] getSupportedAlgorithms() {
        return new String[]{"YIN", "MPM"};
    }

    @Override
    public String[] getSupportedMicrophones() {
        return new String[0];
    }

    @Override
    public String getAlgorithm() {
        return algorithm;
    }

    @Override
    public void setAlgorithm(int index) {
        String[] algorithms = getSupportedAlgorithms();
        if (index >= 0 && index < algorithms.length) {
            algorithm = algorithms[index];
        }
    }

    @Override
    public void setMicrophoneHandler(MicrophoneHandler microphoneHandler) {
        this.microphoneHandler = microphoneHandler;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void setName(int microphoneIndex) {
        // Nicht benötigt auf Android
    }


    /**
     * Processes audio data by analyzing the input buffer to detect pitch and compute RMS values.
     * The method converts the input float buffer to a double array, applies
     * pitch detection based on the configured algorithm, and invokes a handler
     * with the detected pitch and RMS values if available.
     *
     * @param buffer    The audio data buffer containing raw sampled data as float values.
     * @param bytesRead The number of valid bytes read from the audio stream and contained in the buffer.
     */
    private void processAudioData(float[] buffer, int bytesRead) {
        double[] audioData = new double[bytesRead];
        for (int i = 0; i < bytesRead; i++) {
            audioData[i] = buffer[i];
        }
        double pitch = -1;

        // Use the utility class for pitch detection, passing the SAMPLE_RATE as a parameter
        if ("YIN".equals(getAlgorithm())) {
            pitch = PitchDetectionUtil.detectPitchWithYIN(audioData, SAMPLE_RATE);
        } else if ("MPM".equals(getAlgorithm())) {
            pitch = PitchDetectionUtil.detectPitchWithMPM(audioData, SAMPLE_RATE);
        }
        if (microphoneHandler != null && pitch >= 0) {
            microphoneHandler.handle(pitch, PitchDetectionUtil.calcRMS(audioData)); // frequency, RMS
        }
    }

}