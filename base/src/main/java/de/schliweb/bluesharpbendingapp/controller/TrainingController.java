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
import de.schliweb.bluesharpbendingapp.model.training.AbstractTraining;
import de.schliweb.bluesharpbendingapp.model.training.Training;
import de.schliweb.bluesharpbendingapp.utils.LoggingContext;
import de.schliweb.bluesharpbendingapp.utils.LoggingUtils;
import de.schliweb.bluesharpbendingapp.view.MainWindow;
import de.schliweb.bluesharpbendingapp.view.TrainingView;

import java.util.concurrent.ExecutorService;

/**
 * The TrainingController class handles all training-related functionality.
 * It manages training settings, view updates, and training processes.
 */
public class TrainingController implements TrainingViewHandler, TrainingFrequencyHandler {

    private final MainModel model;
    private final ModelStorageService modelStorageService;
    private final MainWindow window;
    private final ExecutorService executorService;
    private Training training;
    private TrainingContainer trainingContainer;

    /**
     * Constructs a new TrainingController with the specified dependencies.
     *
     * @param model               The MainModel containing application state
     * @param modelStorageService The service for storing model data
     * @param window              The main application window
     * @param executorService     The executor service for asynchronous operations
     */
    public TrainingController(MainModel model, ModelStorageService modelStorageService, MainWindow window, ExecutorService executorService) {
        LoggingContext.setComponent("TrainingController");
        LoggingUtils.logInitializing("TrainingController");

        this.model = model;
        this.modelStorageService = modelStorageService;
        this.window = window;
        this.executorService = executorService;

        LoggingUtils.logDebug("Creating and setting training object using stored key and training indices");
        this.training = AbstractTraining.create(model.getStoredKeyIndex(), model.getStoredTrainingIndex());

        LoggingUtils.logInitialized("TrainingController");
    }

    /**
     * Updates the training view based on the provided frequency.
     *
     * @param frequency the frequency value to update the training view with
     */
    public void updateTrainingView(double frequency) {
        LoggingContext.setComponent("TrainingController");
        LoggingUtils.logAudioProcessing("Updating training view", "frequency=" + frequency);

        if (!executorService.isShutdown() && isTrainingViewActiveAndInitialized()) {
            LoggingUtils.logDebug("Training view is active and initialized. Updating frequency and submitting to executor.");

            trainingContainer.setFrequencyToHandle(frequency);

            LoggingUtils.logDebug("Submitting trainingContainer to executorService");
            executorService.submit(trainingContainer);
        } else {
            LoggingUtils.logDebug("Training view update skipped. Either executorService is shut down or the training view is not active/initialized.");
        }

        LoggingUtils.logDebug("Training view update process completed");
    }

    /**
     * Checks if the training view is active and the training container is initialized.
     *
     * @return true if the training view is active and the training container is not null; false otherwise
     */
    private boolean isTrainingViewActiveAndInitialized() {
        LoggingContext.setComponent("TrainingController");
        LoggingUtils.logDebug("Checking if training view is active and initialized");

        boolean isActive = window.isTrainingViewActive();
        boolean isInitialized = this.trainingContainer != null;

        LoggingUtils.logDebug("Training view active: " + isActive);
        LoggingUtils.logDebug("Training container initialized: " + isInitialized);

        boolean result = isActive && isInitialized;

        if (result) {
            LoggingUtils.logDebug("Training view is active and initialized");
        } else {
            LoggingUtils.logDebug("Training view is not active or not initialized. Active: " + isActive + ", Initialized: " + isInitialized);
        }

        return result;
    }

    @Override
    public void initTrainingList() {
        LoggingContext.setComponent("TrainingController");
        LoggingUtils.logOperationStarted("Training list initialization");

        if (window.isTrainingViewActive()) {
            LoggingUtils.logDebug("Training view is active. Proceeding with initialization");

            TrainingView trainingView = window.getTrainingView();

            LoggingUtils.logDebug("Setting training list in TrainingView");
            trainingView.setTrainings(AbstractTraining.getSupportedTrainings());

            LoggingUtils.logDebug("Setting selected training in TrainingView. Selected training index: " + model.getSelectedTrainingIndex());
            trainingView.setSelectedTraining(model.getSelectedTrainingIndex());

            LoggingUtils.logOperationCompleted("Training list initialization");
        } else {
            LoggingUtils.logWarning("Training list initialization skipped", "Training view is not active");
        }
    }

    @Override
    public void initPrecisionList() {
        LoggingContext.setComponent("TrainingController");
        LoggingUtils.logOperationStarted("Precision list initialization");

        if (window.isTrainingViewActive()) {
            LoggingUtils.logDebug("Training view is active. Proceeding with initialization");

            TrainingView trainingView = window.getTrainingView();

            LoggingUtils.logDebug("Setting precision list in TrainingView");
            trainingView.setPrecisions(AbstractTraining.getSupportedPrecisions());

            LoggingUtils.logDebug("Setting selected precision in TrainingView. Selected precision index: " + model.getSelectedPrecisionIndex());
            trainingView.setSelectedPrecision(model.getSelectedPrecisionIndex());

            LoggingUtils.logOperationCompleted("Precision list initialization");
        } else {
            LoggingUtils.logWarning("Precision list initialization skipped", "Training view is not active");
        }
    }

    @Override
    public void handleTrainingSelection(int trainingIndex) {
        LoggingContext.setComponent("TrainingController");
        LoggingUtils.logUserAction("Training selection", "trainingIndex=" + trainingIndex);

        LoggingUtils.logDebug("Updating stored training index in the model to: " + trainingIndex);
        this.model.setStoredTrainingIndex(trainingIndex);
        this.model.setSelectedTrainingIndex(trainingIndex);

        LoggingUtils.logDebug("Creating and setting the training in the model. Using stored key index: " + model.getStoredKeyIndex() + " and training index: " + trainingIndex);
        training = AbstractTraining.create(model.getStoredKeyIndex(), trainingIndex);

        LoggingUtils.logDebug("Initializing training container after selection");
        initTrainingContainer();

        long startTime = System.currentTimeMillis();
        modelStorageService.storeModel(model);
        long duration = System.currentTimeMillis() - startTime;
        LoggingUtils.logPerformance("Model storage", duration);

        LoggingUtils.logOperationCompleted("Training selection handling");
    }

    @Override
    public void initTrainingContainer() {
        LoggingContext.setComponent("TrainingController");
        LoggingUtils.logOperationStarted("Training container initialization");

        if (window.isTrainingViewActive()) {
            LoggingUtils.logDebug("Training view is active. Proceeding with training container initialization");

            this.trainingContainer = new TrainingContainer(training, window.getTrainingView());
            LoggingUtils.logDebug("Created new TrainingContainer with current training model and training view");

            TrainingView trainingView = window.getTrainingView();
            LoggingUtils.logDebug("Passing TrainingContainer to TrainingView for initialization");
            trainingView.initTrainingContainer(trainingContainer);

            LoggingUtils.logOperationCompleted("Training container initialization");
        } else {
            LoggingUtils.logWarning("Training container initialization skipped", "Training view is not active");
        }
    }

    @Override
    public void handleTrainingStart() {
        LoggingContext.setComponent("TrainingController");
        LoggingUtils.logUserAction("Training start", "Starting training session");

        if (training != null) {
            LoggingUtils.logDebug("Retrieved training from model. Training details: " + training);

            training.start();
            LoggingUtils.logOperationCompleted("Training start");

            LoggingUtils.logDebug("Reinitializing training container after training start");
            initTrainingContainer();

            LoggingUtils.logDebug("Training container reinitialized successfully after training start");
        } else {
            LoggingUtils.logWarning("Training start skipped", "No training found in model");
        }
    }

    @Override
    public void handleTrainingStop() {
        LoggingContext.setComponent("TrainingController");
        LoggingUtils.logUserAction("Training stop", "Stopping training session");

        if (training != null) {
            LoggingUtils.logDebug("Retrieved training from model. Training details: " + training);

            training.stop();
            LoggingUtils.logOperationCompleted("Training stop");
        } else {
            LoggingUtils.logWarning("Training stop skipped", "No training found in model");
        }
    }

    @Override
    public void handlePrecisionSelection(int selectedIndex) {
        LoggingContext.setComponent("TrainingController");
        LoggingUtils.logUserAction("Precision selection", "selectedIndex=" + selectedIndex);

        LoggingUtils.logDebug("Updating stored precision index to: " + selectedIndex);
        this.model.setStoredPrecisionIndex(selectedIndex);
        this.model.setSelectedPrecisionIndex(selectedIndex);

        try {
            String[] supportedPrecisions = AbstractTraining.getSupportedPrecisions();
            LoggingUtils.logDebug("Supported precisions: " + String.join(", ", supportedPrecisions));

            int precision = Integer.parseInt(supportedPrecisions[selectedIndex]);
            LoggingUtils.logDebug("Setting precision to: " + precision);

            AbstractTraining.setPrecision(precision);
            LoggingUtils.logOperationCompleted("Precision selection. Precision set to: " + precision);

        } catch (ArrayIndexOutOfBoundsException e) {
            LoggingUtils.logError("Selected index is out of bounds. Supported precisions length: " + AbstractTraining.getSupportedPrecisions().length + ", selected index: " + selectedIndex, e);
        } catch (NumberFormatException e) {
            LoggingUtils.logError("Failed to parse precision value at index: " + selectedIndex, e);
        }

        long startTime = System.currentTimeMillis();
        modelStorageService.storeModel(model);
        long duration = System.currentTimeMillis() - startTime;
        LoggingUtils.logPerformance("Model storage", duration);
    }
}
