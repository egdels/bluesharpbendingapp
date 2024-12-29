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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
     * The constant LOGGER.
     */
    private static final Logger LOGGER = new Logger(MicrophoneAndroid.class);
    /**
     * The constant BUFFER_SIZE.
     */
    private static final int BUFFER_SIZE = 4096;
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
    private volatile PitchEstimationAlgorithm algo = PitchEstimationAlgorithm.MPM;
    /**
     * The Dispatcher.
     */
    private AudioDispatcher dispatcher;
    /**
     * The Microphone handler.
     */
    private volatile MicrophoneHandler microphoneHandler;
    /**
     * The Executor service.
     */
    private ExecutorService executorService;

    /**
     * Get supported algorithms string [ ].
     *
     * @return the string [ ]
     */
    @Override
    public String[] getSupportedAlgorithms() {
        PitchProcessor.PitchEstimationAlgorithm[] values = PitchProcessor.PitchEstimationAlgorithm.values();
        ArrayList<String> algorithms = new ArrayList<>();
        for (PitchProcessor.PitchEstimationAlgorithm value : values) {
            algorithms.add(value.name());
        }
        return Arrays.copyOf(algorithms.toArray(), algorithms.toArray().length, String[].class);
    }

    /**
     * Get supported microphones string [ ].
     *
     * @return the string [ ]
     */
    @Override
    public String[] getSupportedMicrophones() {
        // no need on android
        return new String[0];
    }

    /**
     * Close.
     */
    public void close() {
        if (dispatcher != null) {
            dispatcher.stop();
            dispatcher = null;
        }
        executorService.shutdown();
        executorService = null;
    }

    /**
     * Gets algorithm.
     *
     * @return the algorithm
     */
    @Override
    public String getAlgorithm() {
        return algo.name();
    }

    /**
     * Sets algorithm.
     *
     * @param index the index
     */
    @Override
    public void setAlgorithm(int index) {
        algo = PitchEstimationAlgorithm.values()[index];
    }

    /**
     * Gets microphone handler.
     *
     * @return the microphone handler
     */
    private MicrophoneHandler getMicrophoneHandler() {
        return microphoneHandler;
    }

    /**
     * Sets microphone handler.
     *
     * @param microphoneHandler the microphone handler
     */
    @Override
    public void setMicrophoneHandler(MicrophoneHandler microphoneHandler) {
        this.microphoneHandler = microphoneHandler;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    @Override
    public String getName() {
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
        // no need on android
    }

    /**
     * Open.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void open() {
        executorService = Executors.newSingleThreadExecutor();

        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE, BUFFER_SIZE, OVERLAP);
        dispatcher.addAudioProcessor(new PitchProcessor(algo, SAMPLE_RATE, BUFFER_SIZE, this));
        executorService.submit(() -> {
            Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
            try {
                dispatcher.run();
            } catch (AssertionError e) {
                LOGGER.info("AssertionError:" + " Reopen Microphone to process this issue.");
                close();
                open();
            }
        });
    }


    /**
     * Handle pitch.
     *
     * @param pitchDetectionResult the pitch detection result
     * @param audioEvent           the audio event
     */
    @Override
    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
        float pitch = 0;
        float probability = 0;
        double rms = 0;
        if (pitchDetectionResult.getPitch() != -1) {
            pitch = pitchDetectionResult.getPitch();
            probability = pitchDetectionResult.getProbability();
            rms = audioEvent.getRMS() * 100;
        }
        MicrophoneHandler handler = getMicrophoneHandler();
        if (handler != null) {
            handler.handle(pitch, rms, probability);
        }
    }
}


