package de.schliweb.bluesharpbendingapp.model.microphone.desktop;
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


import de.schliweb.bluesharpbendingapp.model.microphone.Microphone;
import de.schliweb.bluesharpbendingapp.model.microphone.MicrophoneHandler;
import de.schliweb.bluesharpbendingapp.utils.Logger;
import de.schliweb.bluesharpbendingapp.utils.PitchDetectionUtil;

import javax.sound.sampled.*;
import javax.sound.sampled.Mixer.Info;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * The {@code MicrophoneDesktop} class implements the {@code Microphone} interface and represents a
 * desktop-specific microphone system for audio data recording and processing. It provides functionalities
 * to handle audio input, manage audio format configurations, perform algorithm-based pitch detection,
 * and delegate processed data to a handler.
 * <p>
 * This class interacts with audio hardware and uses multi-threading for efficient data processing. It also
 * allows selecting different microphones and algorithms, ensuring flexibility in audio applications.
 */
public class MicrophoneDesktop implements Microphone {

    /**
     * Defines the size of the buffer used for audio data processing in the application.
     * This constant specifies the number of bytes that can be temporarily stored in
     * the buffer while reading or processing audio streams. A larger buffer size may
     * result in smoother audio streaming but can increase latency, whereas a smaller
     * size may decrease latency but increase the risk of audio disruptions.
     */
    protected static final int BUFFER_SIZE = 8192;
    /**
     * Represents the audio sample rate used by the application.
     * The sample rate determines the number of audio samples captured
     * or processed per second. A higher sample rate results in better audio fidelity
     * but requires more processing power and storage. In this case, the sample rate
     * is set to 48,000 samples per second, which is a common rate for high-quality audio.
     */
    private static final int SAMPLE_RATE = 44100;
    /**
     * Represents the number of bits used per audio sample in the audio processing system.
     * This value defines the resolution of the audio samples, impacting the audio quality
     * and dynamic range. A higher value generally represents better audio quality.
     */
    private static final int BITS_PER_SAMPLE = 16;
    /**
     * Represents the number of bytes used to store a single audio sample.
     * This value is calculated by dividing the number of bits per sample (BITS_PER_SAMPLE) by 8.
     * It is commonly used in audio processing to determine the size of buffers
     * and manage sample-related computations.
     */
    private static final int BYTES_PER_SAMPLE = BITS_PER_SAMPLE / 8;
    /**
     * The LOGGER is a static final instance of the Logger class, associated with the
     * MicrophoneDesktop class for contextual logging of messages. It is used to log
     * debug, informational, and error messages with details about the current class
     * and method, aiding in application diagnostics and development debugging.
     */
    private static final Logger LOGGER = new Logger(MicrophoneDesktop.class);
    /**
     * A reusable buffer for storing normalized audio sample data. The array is
     * sized proportionally to the audio buffer size and the number of bytes per sample,
     * ensuring efficient reuse during audio processing tasks.
     * This reduces the overhead of creating new buffers for each audio operation.
     */
    private final double[] reusableAudioData = new double[BUFFER_SIZE / BYTES_PER_SAMPLE];
    /**
     * Represents a thread-safe queue for storing audio data captured from the microphone.
     * This queue is used to manage audio data buffers in a producer-consumer pattern, where
     * audio data is produced (captured) by the microphone and consumed (processed) by a separate thread.
     * <p>
     * The implementation uses a {@code BlockingQueue} to ensure thread safety and to provide
     * blocking operations for adding or retrieving audio data. This helps in synchronizing
     * the data flow between audio capturing and processing components of the application.
     */
    protected BlockingQueue<byte[]> audioDataQueue;
    /**
     * The {@code processingExecutor} is an {@link ExecutorService} instance
     * responsible for managing and executing asynchronous tasks related to
     * audio data processing within the {@code MicrophoneDesktop} class.
     * <p>
     * This executor handles audio processing workloads off the main thread,
     * ensuring that computationally heavy operations, such as audio format
     * conversions and pitch detection, do not block the application's primary flow.
     * Tasks submitted to this executor may include processing raw audio buffers,
     * converting audio data, and performing algorithmic analysis like pitch detection.
     * <p>
     * Proper lifecycle management of this executor is critical. It should
     * be shut down during the cleanup process to prevent resource leaks
     * or lingering background tasks.
     */
    protected ExecutorService processingExecutor;
    /**
     * The {@code ExecutorService} instance used to manage and execute asynchronous tasks
     * in a multi-threaded environment. This service provides a flexible mechanism for
     * handling concurrent operations, allowing the scheduling, execution, and monitoring
     * of runnable and callable tasks. It facilitates efficient thread pool management and
     * improves responsiveness in the application's audio processing and related activities.
     */
    protected ExecutorService executorService;
    /**
     * Represents the current assigned handler for the microphone operations.
     * The microphone handler processes audio data, including receiving pitch (frequency)
     * and volume information. This variable is marked as `volatile` to ensure thread-safe
     * access and updates, as it may be modified or accessed by multiple threads during
     * audio processing.
     * <p>
     * Used for delegating audio processing results (e.g., frequency and volume calculations)
     * to the handler implementation for further handling or display in the application.
     */
    private volatile MicrophoneHandler microphoneHandler;
    /**
     * The name of the microphone instance. This variable represents the
     * user-friendly identifier for the currently selected microphone device.
     * It is used for display purposes and to differentiate between multiple
     * microphone configurations or options.
     */
    private String name;
    /**
     * The pitch detection algorithm currently in use by the `MicrophoneDesktop` class.
     * This variable serves as the default algorithm for audio processing and pitch detection.
     * By default, the algorithm is set to "YIN". The chosen algorithm can be changed via
     * the associated setter method.
     * <p>
     * This is an integral part of the microphone class's audio processing logic.
     */
    private String algorithm = "YIN"; // Default algorithm

    /**
     * Retrieves the audio format configuration used by the application.
     * The audio format is specified with a sample rate, bit depth, channel count,
     * and endianness, which are required for capturing or processing audio data.
     *
     * @return an {@code AudioFormat} instance representing the format configuration
     * with predefined SAMPLE_RATE, BITS_PER_SAMPLE, 1 channel, signed audio,
     * and big-endian byte order.
     */
    public static AudioFormat getAudioFormat() {
        return new AudioFormat(SAMPLE_RATE, BITS_PER_SAMPLE, 1, true, true);
    }

    /**
     * Closes the resources utilized by the microphone implementation.
     * This method ensures proper cleanup by shutting down any executors
     * used for audio processing or related tasks. It is recommended to
     * invoke this method when the microphone is no longer needed to
     * prevent resource leaks.
     */
    @Override
    public void close() {
        if (executorService != null) {
            executorService.shutdown();
        }
        if (processingExecutor != null) {
            processingExecutor.shutdown();
        }
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
        return name;
    }

    @Override
    public void setName(int microphoneIndex) {
        try {
            name = getSupportedMicrophones()[microphoneIndex];
        } catch (ArrayIndexOutOfBoundsException exception) {
            LOGGER.error(exception.getMessage());
        }
    }

    @Override
    public String[] getSupportedAlgorithms() {
        return new String[]{"YIN", "MPM"};
    }

    @Override
    public String[] getSupportedMicrophones() {
        Info[] mixerInfos = AudioSystem.getMixerInfo();
        ArrayList<String> microphones = new ArrayList<>();
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, getAudioFormat());
        for (Info mixerInfo : mixerInfos) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            if (mixer.isLineSupported(dataLineInfo)) {
                LOGGER.debug("Mixer.getLineInfo(): " + mixer.getLineInfo().toString());
                try {
                    TargetDataLine targetDataLine = (TargetDataLine) mixer.getLine(dataLineInfo);
                    targetDataLine.open();
                    targetDataLine.close();
                } catch (LineUnavailableException e) {
                    LOGGER.error("No supported microphone: " + e.getMessage());
                    continue;
                }
                microphones.add(mixer.getMixerInfo().getName());
            }
        }
        return microphones.toArray(String[]::new);
    }

    /**
     * Retrieves a TargetDataLine instance corresponding to the specified mixer name.
     * This method iterates through available mixers, checking if they support the
     * required audio format configuration. If a mixer with the specified name
     * is found and is compatible, its TargetDataLine is obtained.
     *
     * @param name the name of the mixer for which the TargetDataLine is to be retrieved
     * @return a TargetDataLine instance if a matching mixer is found, or null if no such mixer is available
     */
    private TargetDataLine getTargetDataLine(String name) {
        TargetDataLine targetDataLine = null;
        Info[] mixerInfos = AudioSystem.getMixerInfo();
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, getAudioFormat());

        for (Info mixerInfo : mixerInfos) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            if (mixer.isLineSupported(dataLineInfo) && mixer.getMixerInfo().getName().equals(name)) {
                try {
                    targetDataLine = (TargetDataLine) mixer.getLine(dataLineInfo);
                } catch (LineUnavailableException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
        if (targetDataLine == null) {
            LOGGER.error("No matching mixer found for name: " + name + ".");
        }
        return targetDataLine;
    }

    /**
     * Opens and initializes the microphone for capturing and processing audio data.
     * This method sets up necessary resources, including thread pools and queues,
     * to enable efficient audio data acquisition and processing.
     * <p>
     * Specifically, this method performs the following:
     * - Initializes a {@link BlockingQueue} for audio data buffering.
     * - Configures and starts a separate thread for capturing audio data from a
     * {@link TargetDataLine}.
     * - Sets up a configurable thread pool for processing audio data.
     * - Ensures each thread operates independently and does not block JVM shutdown.
     * <p>
     * Key functionalities:
     * 1. Captures audio data in chunks from the system microphone using the specified
     * audio format configuration.
     * 2. Buffers audio data into a thread-safe {@code BlockingQueue} for concurrent access.
     * 3. Uses parallel threads to process audio frames by analyzing and extracting
     * properties like pitch and RMS (Root Mean Square).
     * <p>
     * Note:
     * - This method ensures that resources are properly handled in multi-threaded
     * environments by utilizing daemon threads and interrupt handling mechanisms.
     * - It is recommended to invoke {@link #close()} to release resources when
     * the microphone is no longer required.
     * <p>
     * Exceptions:
     * - Handles {@link LineUnavailableException} if the target
     * microphone is not available or cannot be accessed.
     * - Properly logs any errors or interruptions of the threads for debugging purposes.
     * <p>
     * Logging:
     * - Logs errors related to opening the microphone or interruptions during processing.
     * - Logs informational messages about thread interruptions when processing threads stop.
     */
    @Override
    public void open() {

        // Initialize BlockingQueue and Thread Pool
        audioDataQueue = new LinkedBlockingQueue<>();
        processingExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        executorService = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "AudioProcessingThread");
            thread.setDaemon(true); // Prevents the thread from blocking JVM shutdown
            thread.setPriority(Thread.MAX_PRIORITY); // Highest priority for audio processing
            return thread;
        });

        executorService.execute(() -> {
            try {
                TargetDataLine line = getTargetDataLine(name);
                if (line != null) {
                    line.open(getAudioFormat());
                    line.start();

                    byte[] buffer = new byte[BUFFER_SIZE];
                    while (!Thread.currentThread().isInterrupted()) {
                        int bytesRead = line.read(buffer, 0, BUFFER_SIZE);
                        if (bytesRead > 0) {
                            // Insert audio data into the queue
                            byte[] dataCopy = new byte[bytesRead];
                            System.arraycopy(buffer, 0, dataCopy, 0, bytesRead);

                            // Offer the data to the BlockingQueue
                            audioDataQueue.offer(dataCopy);
                        }
                    }
                    line.close();
                }
            } catch (LineUnavailableException e) {
                LOGGER.error("Error opening microphone: " + e.getMessage());
            }
        });

        // Parallel thread processing
        processingExecutor.execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // Retrieve data from the queue
                    byte[] audioFrame = audioDataQueue.take();

                    // Process the audio data
                    processAudioData(audioFrame, audioFrame.length);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.info("Processing thread interrupted.");
                }
            }
        });


    }


    /**
     * Converts raw audio data from bytes into normalized double values in the range [-1.0, 1.0].
     * The method reads the specified number of bytes and processes them into audio samples,
     * adjusting for the appropriate bit depth and endianness, and stores the resulting
     * samples in a reusable audio data array.
     *
     * @param buffer    the byte array containing raw audio data
     * @param bytesRead the number of bytes read from the buffer that need to be converted
     * @return an array of doubles containing the normalized audio data
     */
    private double[] convertToDouble(byte[] buffer, int bytesRead) {
        int samples = bytesRead / BYTES_PER_SAMPLE;
        for (int i = 0; i < samples; i++) {
            reusableAudioData[i] = (buffer[2 * i] << 8 | (buffer[2 * i + 1] & 0xFF)) / 32768.0; // Normalize to range [-1.0, 1.0]
        }
        return reusableAudioData;
    }

    /**
     * Processes the given audio data buffer to detect the pitch and calculate the RMS (Root Mean Square).
     * It uses the selected pitch detection algorithm (YIN or MPM) and sends the frequency and
     * amplitude data to the microphone handler.
     *
     * @param buffer    the audio buffer containing raw audio data
     * @param bytesRead the number of bytes read from the buffer
     */
    protected void processAudioData(byte[] buffer, int bytesRead) {
        double[] audioData = convertToDouble(buffer, bytesRead);
        double pitch = -1;

        // Use the utility class for pitch detection, passing the SAMPLE_RATE as a parameter
        if ("YIN".equals(getAlgorithm())) {
            pitch = PitchDetectionUtil.detectPitchWithYIN(audioData, SAMPLE_RATE);
        } else if ("MPM".equals(getAlgorithm())) {
            pitch = PitchDetectionUtil.detectPitchWithMPM(audioData, SAMPLE_RATE);
        }
        if (microphoneHandler != null) {
            microphoneHandler.handle(pitch, PitchDetectionUtil.calcRMS(audioData)); // frequency, RMS
        }
    }
}
