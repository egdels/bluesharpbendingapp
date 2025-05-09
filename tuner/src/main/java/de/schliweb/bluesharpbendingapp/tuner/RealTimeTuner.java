package de.schliweb.bluesharpbendingapp.tuner;
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

import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;
import de.schliweb.bluesharpbendingapp.model.microphone.AbstractMicrophone;
import de.schliweb.bluesharpbendingapp.model.microphone.MicrophoneHandler;
import de.schliweb.bluesharpbendingapp.model.microphone.desktop.MicrophoneDesktop;
import de.schliweb.bluesharpbendingapp.utils.NoteUtils;
import de.schliweb.bluesharpbendingapp.view.desktop.TuningMeterFX;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.List;

public class RealTimeTuner implements MicrophoneHandler {

    private static final String DEFAULT_ALGORITHM = "YIN";

    private static final int DEFAULT_NAME = 0;

    private static final double DEFAULT_CONFIDENCE = 0.7;

    private MicrophoneDesktop microphone;

    private boolean isTuning = false;

    @FXML
    private Label noteLabel;

    @FXML
    private Label frequencyLabel;

    @FXML
    private Label centsValueLabel;

    @FXML
    private Label tuningStatusLabel;

    @FXML
    private Button startStopButton;

    @FXML
    private TuningMeterFX tuningMeter;

    @FXML
    private void initialize() {
        initializeMicrophone();
        updateUIForStoppedState();
    }

    @FXML
    public void toggleTuning() {
        if (isTuning) {
            stopTuning();
            updateUIForStoppedState();
        } else {
            startTuning();
            updateUIForListeningState();
        }
    }

    private void updateUIForStoppedState() {
        startStopButton.setText("Start");
        tuningStatusLabel.setText("Stopped");
    }

    private void updateUIForListeningState() {
        startStopButton.setText("Stop");
        tuningStatusLabel.setText("Listening...");
    }

    public void startTuning() {
        isTuning = true;
        microphone.open();
    }

    public void stopTuning() {
        if (isTuning) {
            isTuning = false;
            closeMicrophone();
        }
    }

    @Override
    public void handle(double pitch, double volume) {
        if (isValidPitch(pitch)) {
            processPitchData(pitch);
        }
    }

    private void initializeMicrophone() {
        microphone = new MicrophoneDesktop();
        List<String> supportedAlgorithms = List.of(AbstractMicrophone.getSupportedAlgorithms());
        int algorithmIndex = supportedAlgorithms.indexOf(DEFAULT_ALGORITHM);
        microphone.setAlgorithm(algorithmIndex);
        microphone.setName(DEFAULT_NAME);
        List<String> supportedConfidences = List.of(AbstractMicrophone.getSupportedConfidences());
        int confidenceIndex = supportedConfidences.indexOf(DEFAULT_CONFIDENCE + "");
        microphone.setConfidence(confidenceIndex);
        microphone.setMicrophoneHandler(this);
    }

    private void closeMicrophone() {
        if (microphone != null) {
            microphone.close();
        }
    }

    private boolean isValidPitch(double pitch) {
        return pitch > 0;
    }

    private void processPitchData(double pitch) {
        String note = NoteLookup.getNoteName(pitch);
        double referenceFrequency = NoteLookup.getNoteFrequency(note);
        double cents = NoteUtils.getCents(pitch, referenceFrequency);

        Platform.runLater(() -> {
            noteLabel.setText("Note: " + note);
            frequencyLabel.setText("Frequency: " + Math.round(pitch) + " Hz");
            centsValueLabel.setText("Cents: " + Math.round(cents));
            updateTuningStatus(cents);
        });

        tuningMeter.setCents(cents);
    }

    private void updateTuningStatus(double cents) {
        if (Math.abs(cents) < 5) {
            tuningStatusLabel.setText("In Tune");
        } else if (Math.abs(cents) < 15) {
            tuningStatusLabel.setText(cents < 0 ? "Slightly Flat" : "Slightly Sharp");
        } else {
            tuningStatusLabel.setText(cents < 0 ? "Too Flat" : "Too Sharp");
        }
    }
}
