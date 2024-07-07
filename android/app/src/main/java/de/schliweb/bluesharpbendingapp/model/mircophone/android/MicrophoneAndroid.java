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
import android.os.Process;

import java.util.ArrayList;
import java.util.Arrays;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import de.schliweb.bluesharpbendingapp.model.microphone.Microphone;
import de.schliweb.bluesharpbendingapp.model.microphone.MicrophoneHandler;
import de.schliweb.bluesharpbendingapp.utils.Logger;

/**
 * The type Microphone android.
 */
public class MicrophoneAndroid implements PitchDetectionHandler, Microphone {
    /**
     * The constant BUFFER_SIZE.
     */
    private static final int BUFFER_SIZE = 4096;
    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = new Logger(MicrophoneAndroid.class);
    /**
     * The constant OVERLAP.
     */
    private static final int OVERLAP = 0;
    /**
     * The constant SAMPLE_RATE.
     */
    private static final int SAMPLE_RATE = 44100;
    /**
     * The Algo.
     */
    private final PitchEstimationAlgorithm algo = PitchEstimationAlgorithm.MPM;
    /**
     * The Dispatcher.
     */
    private AudioDispatcher dispatcher;
    /**
     * The Microphone handler.
     */
    private MicrophoneHandler microphoneHandler;

    /**
     * Instantiates a new Microphone android.
     */
    public MicrophoneAndroid() {
        LOGGER.info("Created");
    }

    /**
     * Get supported algorithms string [ ].
     *
     * @return the string [ ]
     */
    @Override
    public String[] getSupportedAlgorithms() {
        LOGGER.info("Enter");
        PitchProcessor.PitchEstimationAlgorithm[] values = PitchProcessor.PitchEstimationAlgorithm.values();
        ArrayList<String> algorithms = new ArrayList<>();
        for (PitchProcessor.PitchEstimationAlgorithm value : values) {
            algorithms.add(value.name());
        }
        LOGGER.info("Return " + algorithms);
        LOGGER.info("Return " + Arrays.toString(algorithms.toArray()));
        return Arrays.copyOf(algorithms.toArray(),
                algorithms.toArray().length,
                String[].class);
    }

    /**
     * Get supported microphones string [ ].
     *
     * @return the string [ ]
     */
    @Override
    public String[] getSupportedMicrophones() {
        LOGGER.info("Enter");
        // no need on android
        return new String[0];
    }

    /**
     * Close.
     */
    public void close() {
        LOGGER.info("Enter");
        if (dispatcher != null) {
            dispatcher.stop();
            dispatcher = null;
        }
        LOGGER.info("Leave");
    }

    /**
     * Gets algorithm.
     *
     * @return the algorithm
     */
    @Override
    public String getAlgorithm() {
        LOGGER.info("Enter");
        LOGGER.info("Return" + algo.name());
        return algo.name();
    }

    /**
     * Sets algorithm.
     *
     * @param index the index
     */
    @Override
    public void setAlgorithm(int index) {
        LOGGER.info("Enter with parameter " + index);
        // algo = PitchEstimationAlgorithm.values()[index]; not used on android to keep it simple
        LOGGER.debug("has algorithm " + algo);
        LOGGER.info("Leave");
    }

    /**
     * Gets microphone handler.
     *
     * @return the microphone handler
     */
    private MicrophoneHandler getMicrophoneHandler() {
        LOGGER.info("Enter");
        return microphoneHandler;
    }

    /**
     * Sets microphone handler.
     *
     * @param microphoneHandler the microphone handler
     */
    @Override
    public void setMicrophoneHandler(MicrophoneHandler microphoneHandler) {
        LOGGER.info("Enter ");
        this.microphoneHandler = microphoneHandler;
        LOGGER.info("Leave");
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    @Override
    public String getName() {
        LOGGER.info("Enter");
        // no need on android
        return "";
    }

    /**
     * Sets name.
     *
     * @param microphoneIndex the microphone index
     */
    @Override
    public void setName(int microphoneIndex) {
        LOGGER.info("Enter with parameter " + microphoneIndex);
        // no need on android
        LOGGER.info("Leave");
    }

    /**
     * Open.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void open() {
        LOGGER.info("Enter");

        if (dispatcher != null) {
            dispatcher.stop();
        }
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE, BUFFER_SIZE, OVERLAP);
        dispatcher.addAudioProcessor(new PitchProcessor(algo, SAMPLE_RATE, BUFFER_SIZE, this));
        class DispatcherRunnable implements Runnable {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
                try {
                    dispatcher.run();
                } catch (AssertionError e) {
                    LOGGER.error("AssertionError " + e.getMessage());
                    open();
                }
            }
        }
        new Thread(new DispatcherRunnable(), "Audio dispatching").start();
    }


    /**
     * Handle pitch.
     *
     * @param pitchDetectionResult the pitch detection result
     * @param audioEvent           the audio event
     */
    @Override
    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
        LOGGER.info("handlePitch");
        float pitch = 0;
        float probability = 0;
        double rms = 0;
        if (pitchDetectionResult.getPitch() != -1) {
            double timeStamp = audioEvent.getTimeStamp();
            pitch = pitchDetectionResult.getPitch();
            probability = pitchDetectionResult.getProbability();
            rms = audioEvent.getRMS() * 100;
            @SuppressLint("DefaultLocale") String message = String.format("Pitch detected at %.2fs: %.2fHz ( %.2f probability, RMS: %.5f )\n",
                    timeStamp, pitch, probability, rms);
            LOGGER.debug(message);
        }
        MicrophoneHandler microphoneHandler = getMicrophoneHandler();
        if (microphoneHandler != null) {
            microphoneHandler.handle(pitch, rms, probability);
        }
        LOGGER.info("Pitch handled");
    }
}


