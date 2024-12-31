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
 * The MicrophoneDesktop class provides a concrete implementation of the Microphone interface
 * for desktop applications. It handles microphone configuration, audio data processing,
 * and pitch detection using supported algorithms.
 */
public class MicrophoneDesktop implements Microphone {

    // Neue Mitgliedervariablen
    private BlockingQueue<byte[]> audioDataQueue;
    private ExecutorService processingExecutor;

    /**
     * Represents the size of the buffer used for audio data processing.
     * This constant defines the number of bytes allocated for one
     * buffer used during audio data capture or processing operations
     * in the application.
     */
    private static final int BUFFER_SIZE = 4096;
    /**
     * Defines the audio sample rate used for recording audio data.
     * The sample rate is the number of audio samples captured per second
     * and is measured in Hertz (Hz). A value of 22050 Hz is commonly used
     * for audio applications where moderate quality is required while
     * maintaining a balance between performance and size.
     */
    private static final int SAMPLE_RATE = 22050;
    /**
     * The LOGGER is a static final instance of the Logger class, associated with the
     * MicrophoneDesktop class for contextual logging of messages. It is used to log
     * debug, informational, and error messages with details about the current class
     * and method, aiding in application diagnostics and development debugging.
     */
    private static final Logger LOGGER = new Logger(MicrophoneDesktop.class);
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
     * The {@code ExecutorService} instance used to manage and execute asynchronous tasks
     * in a multi-threaded environment. This service provides a flexible mechanism for
     * handling concurrent operations, allowing the scheduling, execution, and monitoring
     * of runnable and callable tasks. It facilitates efficient thread pool management and
     * improves responsiveness in the application's audio processing and related activities.
     */
    private ExecutorService executorService;
    /**
     * The pitch detection algorithm currently in use by the `MicrophoneDesktop` class.
     * This variable serves as the default algorithm for audio processing and pitch detection.
     * By default, the algorithm is set to "YIN". The chosen algorithm can be changed via
     * the associated setter method.
     * <p>
     * This is an integral part of the microphone class's audio processing logic.
     */
    private String algorithm = "YIN"; // Default algorithm

    private final double[] reusableAudioData = new double[BUFFER_SIZE / BYTES_PER_SAMPLE];

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
     * Closes the resources associated with the instance, specifically shutting down
     * the executor service if it is not null. This ensures proper cleanup of resources
     * and prevents potential memory leaks or lingering background tasks.
     */
    @Override
    public void close() {
        if (executorService != null) {
            executorService.shutdown();
        }
        if(processingExecutor != null) {
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
        for (Info mixerInfo : mixerInfos) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            if (mixer.isLineSupported(new DataLine.Info(TargetDataLine.class, getAudioFormat()))) {
                LOGGER.debug("Mixer.getLineInfo(): " + mixer.getLineInfo().toString());
                microphones.add(mixer.getMixerInfo().getName());
            } else {
                LOGGER.debug("Kein unterstütztes Microphone: " + mixer.getLineInfo().toString());
            }
        }
        return microphones.toArray(String[]::new);
    }

    /**
     * Retrieves a {@code TargetDataLine} for the specified mixer name, which supports the audio
     * format defined in the application. The method iterates through available mixer devices
     * to find a compatible data line.
     *
     * @param name the name of the mixer to search for a target data line
     * @return the {@code TargetDataLine} instance for the specified mixer if found; otherwise, {@code null}
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
        return targetDataLine;
    }

    @Override
    public void open() {

        // Initialisiere BlockingQueue und Thread-Pool
        audioDataQueue = new LinkedBlockingQueue<>();
        processingExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        executorService = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "AudioProcessingThread");
            thread.setDaemon(true); // Verhindert, dass der Thread das Beenden der JVM blockiert
            thread.setPriority(Thread.MAX_PRIORITY); // Höchste Priorität für Audio-Verarbeitung
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
                            // Audio-Daten in die Queue einfügen
                            byte[] dataCopy = new byte[bytesRead];
                            System.arraycopy(buffer, 0, dataCopy, 0, bytesRead);

                            // Füge die Daten in die BlockingQueue ein
                            audioDataQueue.offer(dataCopy);
                        }
                    }
                    line.close();
                }
            } catch (LineUnavailableException e) {
                LOGGER.error("Error opening microphone: " + e.getMessage());
            }
        });

        // Verarbeitung in parallelen Threads
        processingExecutor.execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // Daten aus der Queue abrufen
                    byte[] audioFrame = audioDataQueue.take();

                    // Verarbeite die Audiodaten
                    processAudioData(audioFrame, audioFrame.length);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.info("Processing thread interrupted.");
                }
            }
        });


    }


    /**
     * Converts raw audio bytes into a normalized double array for audio processing.
     * The bytes are interpreted as 16-bit signed values (big-endian format) and
     * normalized to the range [-1.0, 1.0] for compatibility with audio algorithms.
     *
     * @param buffer    the raw audio buffer containing audio data
     * @param bytesRead the number of bytes read from the line
     * @return an array of normalized audio samples as double values
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
    private void processAudioData(byte[] buffer, int bytesRead) {
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
