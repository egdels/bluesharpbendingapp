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
import java.util.Arrays;

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
    private PitchEstimationAlgorithm algo = PitchEstimationAlgorithm.MPM;
    /**
     * The Dispatcher.
     */
    private AudioDispatcher dispatcher;

    /**
     * The Microphone handler.
     */
    private MicrophoneHandler microphoneHandler;

    /**
     * The Name.
     */
    private String name;

    /**
     * The Target data line.
     */
    private TargetDataLine targetDataLine;

    /**
     * Instantiates a new Microphone desktop.
     */
    public MicrophoneDesktop() {
        LOGGER.info("Created");
    }

    /**
     * Gets audio format.
     *
     * @return the audio format
     */
    public static AudioFormat getAudioFormat() {
        LOGGER.info("Enter");
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, true);
        LOGGER.info("Return " + format);
        return format;
    }

    @Override
    public void close() {
        LOGGER.info("Enter");
        getTargetDataLine().stop();
        getTargetDataLine().close();
        setTargetDataLine(null);
        LOGGER.info("Leave");
    }

    @Override
    public String getAlgorithm() {
        LOGGER.info("Enter");
        LOGGER.info("Return" + algo.name());
        return algo.name();
    }

    @Override
    public void setAlgorithm(int index) {
        LOGGER.info("Enter with parameter " + index);
        algo = PitchEstimationAlgorithm.values()[index];
        LOGGER.debug("has algorithm " + algo);
        LOGGER.info("Leave");
    }

    /**
     * Gets microphone handler.
     *
     * @return the microphone handler
     */
    public MicrophoneHandler getMicrophoneHandler() {
        LOGGER.info("Enter");
        LOGGER.info("Return" + microphoneHandler.toString());
        return microphoneHandler;
    }

    @Override
    public void setMicrophoneHandler(MicrophoneHandler microphoneHandler) {
        LOGGER.info("Enter with parameter " + microphoneHandler.toString());
        this.microphoneHandler = microphoneHandler;
        LOGGER.info("Leave");
    }

    @Override
    public String getName() {
        LOGGER.info("Enter");
        LOGGER.info("Return" + name);
        return name;
    }

    @Override
    public void setName(int microphoneIndex) {
        LOGGER.info("Enter with parameter " + microphoneIndex);
        try {
            name = getSupportedMicrophones()[microphoneIndex];
        } catch (ArrayIndexOutOfBoundsException exception) {
            LOGGER.error(exception.getMessage());
        }
        LOGGER.info("Leave");
    }

    @Override
    public String[] getSupportedAlgorithms() {
        LOGGER.info("Enter");
        PitchEstimationAlgorithm[] values = PitchEstimationAlgorithm.values();
        String[] algorithms = new String[values.length];
        int index = 0;
        for (PitchEstimationAlgorithm value : values) {
            algorithms[index] = value.name();
            index++;
        }
        LOGGER.info("Return " + Arrays.toString(algorithms));
        return algorithms;
    }

    @Override
    public String[] getSupportedMicrophones() {
        LOGGER.info("Enter");
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
        LOGGER.info("Return " + microphones);
        return microphones.toArray(String[]::new);
    }

    /**
     * Gets target data line.
     *
     * @return the target data line
     */
    public TargetDataLine getTargetDataLine() {
        LOGGER.info("Enter");
        if (targetDataLine == null) {
            LOGGER.debug("has name " + name);
            initTargetDataLine(name);
        }
        LOGGER.info("Return " + targetDataLine.toString());
        return targetDataLine;
    }

    /**
     * Sets target data line.
     *
     * @param targetDataLine the target data line
     */
    public void setTargetDataLine(TargetDataLine targetDataLine) {
        if (null != targetDataLine)
            LOGGER.info("Enter with parameter " + targetDataLine);
        this.targetDataLine = targetDataLine;
        LOGGER.info("Leave");
    }

    @Override
    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
        LOGGER.debug("Enter");
        if (pitchDetectionResult.getPitch() != -1) {
            double timeStamp = audioEvent.getTimeStamp();
            float pitch = pitchDetectionResult.getPitch();
            float probability = pitchDetectionResult.getProbability();
            double rms = audioEvent.getRMS() * 100;
            String message = String.format("Pitch detected at %.2fs: %.2fHz ( %.2f probability, RMS: %.5f )\n",
                    timeStamp, pitch, probability, rms);
            LOGGER.debug(message);
            MicrophoneHandler microphoneHandler = getMicrophoneHandler();
            if (microphoneHandler != null) {
                microphoneHandler.handle(pitch, rms, probability);
            }
        } else {
            MicrophoneHandler microphoneHandler = getMicrophoneHandler();
            if (microphoneHandler != null) {
                microphoneHandler.handle(0.0, 0.0, 0.0);
            }
        }
        LOGGER.debug("Leave");
    }

    /**
     * Init target data line.
     *
     * @param name the name
     */
    private void initTargetDataLine(String name) {
        LOGGER.info("Enter with parameter: " + name);
        Info[] mixerInfos = AudioSystem.getMixerInfo();
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, getAudioFormat());
        for (Info mixerInfo : mixerInfos) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            if (mixer.isLineSupported(dataLineInfo) && mixer.getMixerInfo().getName().equals(name)) {
                try {
                    mixer.getLine(dataLineInfo);
                    setTargetDataLine((TargetDataLine) mixer.getLine(dataLineInfo));
                } catch (LineUnavailableException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
        LOGGER.info("Leave");
    }

    @Override
    public void open() {
        LOGGER.info("Enter");
        if (dispatcher != null) {
            dispatcher.stop();
        }
        try {
            getTargetDataLine().open(getAudioFormat(), BUFFER_SIZE);
            getTargetDataLine().start();
            final AudioInputStream stream = new AudioInputStream(getTargetDataLine());
            JVMAudioInputStream audioStream = new JVMAudioInputStream(stream);
            // create a new dispatcher
            dispatcher = new AudioDispatcher(audioStream, BUFFER_SIZE, OVERLAP);
            dispatcher.addAudioProcessor(new PitchProcessor(algo, SAMPLE_RATE, BUFFER_SIZE, this));

            new Thread(dispatcher, "Audio dispatching").start();
        } catch (LineUnavailableException e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("Leave");
    }

}
