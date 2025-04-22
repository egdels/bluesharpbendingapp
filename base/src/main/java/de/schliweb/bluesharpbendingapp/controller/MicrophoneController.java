package de.schliweb.bluesharpbendingapp.controller;
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

import de.schliweb.bluesharpbendingapp.model.MainModel;
import de.schliweb.bluesharpbendingapp.model.ModelStorageService;
import de.schliweb.bluesharpbendingapp.model.microphone.AbstractMicrophone;
import de.schliweb.bluesharpbendingapp.model.microphone.Microphone;
import de.schliweb.bluesharpbendingapp.model.microphone.MicrophoneHandler;
import de.schliweb.bluesharpbendingapp.utils.LoggingContext;
import de.schliweb.bluesharpbendingapp.utils.LoggingUtils;
import de.schliweb.bluesharpbendingapp.view.MainWindow;
import de.schliweb.bluesharpbendingapp.view.MicrophoneSettingsView;


/**
 * The MicrophoneController class handles all microphone-related functionality.
 * It manages microphone settings, input processing, and updates to the microphone settings view.
 */
public class MicrophoneController implements MicrophoneHandler, MicrophoneSettingsViewHandler {

    private final MainModel model;
    private final ModelStorageService modelStorageService;
    private final MainWindow window;
    private final Microphone microphone;
    private final HarpFrequencyHandler harpFrequencyHandler;
    private final TrainingFrequencyHandler trainingFrequencyHandler;

    /**
     * Constructs a new MicrophoneController with the specified dependencies.
     *
     * @param model                    The MainModel containing application state
     * @param modelStorageService      The service for storing model data
     * @param window                   The main application window
     * @param microphone               The microphone instance to control
     * @param harpFrequencyHandler     The handler for harp frequency updates
     * @param trainingFrequencyHandler The handler for training frequency updates
     */
    public MicrophoneController(MainModel model, ModelStorageService modelStorageService, MainWindow window, Microphone microphone, HarpFrequencyHandler harpFrequencyHandler, TrainingFrequencyHandler trainingFrequencyHandler) {
        LoggingContext.setComponent("MicrophoneController");
        LoggingUtils.logInitializing("MicrophoneController");

        this.model = model;
        this.modelStorageService = modelStorageService;
        this.window = window;
        this.microphone = microphone;
        this.harpFrequencyHandler = harpFrequencyHandler;
        this.trainingFrequencyHandler = trainingFrequencyHandler;

        LoggingUtils.logDebug("Configuring microphone with stored settings");
        microphone.setAlgorithm(model.getStoredAlgorithmIndex());
        microphone.setName(model.getStoredMicrophoneIndex());
        microphone.setConfidence(model.getStoredConfidenceIndex());

        LoggingUtils.logDebug("Registering microphone handler");
        microphone.setMicrophoneHandler(this);

        LoggingUtils.logInitialized("MicrophoneController");
    }

    /**
     * Opens the microphone for audio input.
     */
    public void open() {
        LoggingContext.setComponent("MicrophoneController");
        LoggingUtils.logOperationStarted("Microphone opening");
        microphone.open();
        LoggingUtils.logOperationCompleted("Microphone opening");
    }

    /**
     * Closes the microphone to release resources.
     */
    public void close() {
        LoggingContext.setComponent("MicrophoneController");
        LoggingUtils.logOperationStarted("Microphone closing");
        microphone.close();
        LoggingUtils.logOperationCompleted("Microphone closing");
    }

    @Override
    public void handleAlgorithmSelection(int algorithmIndex) {
        LoggingContext.setComponent("MicrophoneController");
        LoggingUtils.logUserAction("Algorithm selection", "algorithmIndex=" + algorithmIndex);

        LoggingUtils.logDebug("Updating stored algorithm index in the model");
        this.model.setStoredAlgorithmIndex(algorithmIndex);
        this.model.setSelectedAlgorithmIndex(algorithmIndex);

        LoggingUtils.logDebug("Closing the current microphone");
        microphone.close();

        LoggingUtils.logDebug("Setting new algorithm index on the microphone");
        microphone.setAlgorithm(algorithmIndex);

        LoggingUtils.logDebug("Opening the microphone with the new algorithm");
        microphone.open();

        LoggingUtils.logDebug("Storing updated model");
        long startTime = System.currentTimeMillis();
        modelStorageService.storeModel(model);
        long duration = System.currentTimeMillis() - startTime;
        LoggingUtils.logPerformance("Model storage", duration);

        LoggingUtils.logOperationCompleted("Algorithm selection handling");
    }

    @Override
    public void handleMicrophoneSelection(int microphoneIndex) {
        LoggingContext.setComponent("MicrophoneController");
        LoggingUtils.logUserAction("Microphone selection", "microphoneIndex=" + microphoneIndex);

        LoggingUtils.logDebug("Updating stored microphone index in the model");
        this.model.setStoredMicrophoneIndex(microphoneIndex);
        this.model.setSelectedMicrophoneIndex(microphoneIndex);

        LoggingUtils.logDebug("Closing the current microphone");
        microphone.close();

        LoggingUtils.logDebug("Setting new microphone index");
        microphone.setName(microphoneIndex);

        LoggingUtils.logDebug("Opening the microphone with the new configuration");
        microphone.open();

        LoggingUtils.logDebug("Storing updated model");
        long startTime = System.currentTimeMillis();
        modelStorageService.storeModel(model);
        long duration = System.currentTimeMillis() - startTime;
        LoggingUtils.logPerformance("Model storage", duration);

        LoggingUtils.logOperationCompleted("Microphone selection handling");
    }

    @Override
    public void handle(double frequency, double volume) {
        LoggingContext.setComponent("MicrophoneController");
        LoggingUtils.logAudioProcessing("Microphone input", "frequency=" + frequency + ", volume=" + volume);

        updateMicrophoneSettingsViewVolume(volume);
        updateMicrophoneSettingsViewFrequency(frequency);

        // Forward the frequency to other controllers
        harpFrequencyHandler.updateHarpView(frequency);
        trainingFrequencyHandler.updateTrainingView(frequency);
    }

    @Override
    public void initAlgorithmList() {
        LoggingContext.setComponent("MicrophoneController");
        LoggingUtils.logOperationStarted("Algorithm list initialization");

        if (window.isMicrophoneSettingsViewActive()) {
            MicrophoneSettingsView microphoneSettingsView = window.getMicrophoneSettingsView();

            LoggingUtils.logDebug("Setting algorithms in the microphone settings view");
            microphoneSettingsView.setAlgorithms(AbstractMicrophone.getSupportedAlgorithms());

            LoggingUtils.logDebug("Setting the selected algorithm in the microphone settings view");
            microphoneSettingsView.setSelectedAlgorithm(model.getSelectedAlgorithmIndex());

            LoggingUtils.logOperationCompleted("Algorithm list initialization");
        } else {
            LoggingUtils.logWarning("Algorithm list initialization skipped", "Microphone settings view is not active");
        }
    }

    @Override
    public void initMicrophoneList() {
        LoggingContext.setComponent("MicrophoneController");
        LoggingUtils.logOperationStarted("Microphone list initialization");

        if (window.isMicrophoneSettingsViewActive()) {
            MicrophoneSettingsView microphoneSettingsView = window.getMicrophoneSettingsView();

            LoggingUtils.logDebug("Setting microphone list in the microphone settings view");
            microphoneSettingsView.setMicrophones(microphone.getSupportedMicrophones());

            LoggingUtils.logDebug("Setting the selected microphone in the microphone settings view");
            microphoneSettingsView.setSelectedMicrophone(model.getSelectedMicrophoneIndex());

            LoggingUtils.logOperationCompleted("Microphone list initialization");
        } else {
            LoggingUtils.logWarning("Microphone list initialization skipped", "Microphone settings view is not active");
        }
    }

    @Override
    public void initConfidenceList() {
        LoggingContext.setComponent("MicrophoneController");
        LoggingUtils.logOperationStarted("Confidence list initialization");

        if (window.isMicrophoneSettingsViewActive()) {
            MicrophoneSettingsView microphoneSettingsView = window.getMicrophoneSettingsView();

            LoggingUtils.logDebug("Setting confidence list in the microphone settings view");
            microphoneSettingsView.setConfidences(AbstractMicrophone.getSupportedConfidences());

            LoggingUtils.logDebug("Setting the selected confidence in the microphone settings view");
            microphoneSettingsView.setSelectedConfidence(model.getSelectedConfidenceIndex());

            LoggingUtils.logOperationCompleted("Confidence list initialization");
        } else {
            LoggingUtils.logWarning("Confidence list initialization skipped", "Microphone settings view is not active");
        }
    }

    @Override
    public void handleConfidenceSelection(int confidenceIndex) {
        LoggingContext.setComponent("MicrophoneController");
        LoggingUtils.logUserAction("Confidence selection", "confidenceIndex=" + confidenceIndex);

        LoggingUtils.logDebug("Updating stored confidence index in the model");
        this.model.setStoredConfidenceIndex(confidenceIndex);
        this.model.setSelectedConfidenceIndex(confidenceIndex);

        if (microphone != null) {
            LoggingUtils.logDebug("Setting confidence index on the microphone", "confidenceIndex=" + confidenceIndex);
            microphone.setConfidence(confidenceIndex);
            LoggingUtils.logOperationCompleted("Confidence selection handling");
        } else {
            LoggingUtils.logWarning("Cannot set confidence index", "Microphone object is null");
        }

        LoggingUtils.logDebug("Storing updated model");
        long startTime = System.currentTimeMillis();
        modelStorageService.storeModel(model);
        long duration = System.currentTimeMillis() - startTime;
        LoggingUtils.logPerformance("Model storage", duration);
    }

    /**
     * Updates the microphone settings view with the specified frequency.
     *
     * @param frequency the frequency value to update in the microphone settings view
     */
    private void updateMicrophoneSettingsViewFrequency(double frequency) {
        LoggingContext.setComponent("MicrophoneController");
        LoggingUtils.logAudioProcessing("Updating view frequency", "frequency=" + frequency);

        if (window.isMicrophoneSettingsViewActive()) {
            MicrophoneSettingsView microphoneSettingsView = window.getMicrophoneSettingsView();
            microphoneSettingsView.setFrequency(frequency);
        }
    }

    /**
     * Updates the microphone settings view with the specified volume level.
     *
     * @param volume the volume level to update in the microphone settings view
     */
    private void updateMicrophoneSettingsViewVolume(double volume) {
        LoggingContext.setComponent("MicrophoneController");
        LoggingUtils.logAudioProcessing("Updating view volume", "volume=" + volume);

        if (window.isMicrophoneSettingsViewActive()) {
            MicrophoneSettingsView microphoneSettingsView = window.getMicrophoneSettingsView();
            microphoneSettingsView.setVolume(volume);
        }
    }
}
