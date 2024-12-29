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

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import de.schliweb.bluesharpbendingapp.model.microphone.Microphone;
import de.schliweb.bluesharpbendingapp.model.microphone.MicrophoneHandler;
import de.schliweb.bluesharpbendingapp.utils.Logger;

import javax.sound.sampled.*;
import javax.sound.sampled.Mixer.Info;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * The type Microphone desktop.
 */
public class MicrophoneDesktop implements PitchDetectionHandler, Microphone {

    /**
     * The constant BUFFER_SIZE.
     */
    private static final int BUFFER_SIZE = 4096;
    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = new Logger(MicrophoneDesktop.class);

    /**
     * The constant OVERLAP.
     */
    private static final int OVERLAP = 0;

    /**
     * The constant SAMPLE_RATE.
     */
    private static final float SAMPLE_RATE = 44100;

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
     * The Name.
     */
    private String name;

    /**
     * The Executor service.
     */
    private ExecutorService executorService;

    /**
     * Gets audio format.
     *
     * @return the audio format
     */
    public static AudioFormat getAudioFormat() {
        return new AudioFormat(SAMPLE_RATE, 16, 1, true, true);
    }

    @Override
    public void close() {
        if (dispatcher != null) {
            dispatcher.stop();
        }
        executorService.shutdown();
    }

    @Override
    public String getAlgorithm() {
        return algo.name();
    }

    @Override
    public void setAlgorithm(int index) {
        algo = PitchEstimationAlgorithm.values()[index];
    }

    /**
     * Gets microphone handler.
     *
     * @return the microphone handler
     */
    public MicrophoneHandler getMicrophoneHandler() {
        return microphoneHandler;
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
        PitchEstimationAlgorithm[] values = PitchEstimationAlgorithm.values();
        String[] algorithms = new String[values.length];
        int index = 0;
        for (PitchEstimationAlgorithm value : values) {
            algorithms[index] = value.name();
            index++;
        }
        return algorithms;
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


    /**
     * Gets target data line.
     *
     * @param name the name
     * @return the target data line
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
        executorService = Executors.newSingleThreadExecutor();
        try {
            AudioFormat format = getAudioFormat();
            TargetDataLine line = getTargetDataLine(name);
            if (line != null) {
                line.open(format);
                line.start();
                JVMAudioInputStream audioStream = new JVMAudioInputStream(new AudioInputStream(line));
                dispatcher = new AudioDispatcher(audioStream, BUFFER_SIZE, OVERLAP);
                dispatcher.addAudioProcessor(new PitchProcessor(algo, SAMPLE_RATE, BUFFER_SIZE, this));
                executorService.submit(dispatcher);
            }
        } catch (LineUnavailableException e) {
            LOGGER.error(e.getMessage());
        }
    }

}
