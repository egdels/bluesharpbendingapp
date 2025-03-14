package de.schliweb.bluesharpbendingapp.controller;
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

import de.schliweb.bluesharpbendingapp.model.MainModel;
import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;
import de.schliweb.bluesharpbendingapp.model.microphone.Microphone;
import de.schliweb.bluesharpbendingapp.model.microphone.MicrophoneHandler;
import de.schliweb.bluesharpbendingapp.model.training.AbstractTraining;
import de.schliweb.bluesharpbendingapp.model.training.Training;
import de.schliweb.bluesharpbendingapp.view.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The MainController class orchestrates communication and logic between the view and model components
 * of the application. It handles user interactions, updates views, manages background tasks, and
 * processes microphone input for the harmonica and training features.
 * <p>
 * Implements multiple handler interfaces to manage various sections of the application.
 */
@Slf4j
public class MainController implements MicrophoneHandler, MicrophoneSettingsViewHandler, HarpSettingsViewHandler, HarpViewHandler, NoteSettingsViewHandler, TrainingViewHandler {

    /**
     * The maximum number of channels that can be utilized in the system.
     * This value is static and final, indicating it is a constant and cannot be changed.
     */
    private static final int CHANNEL_MAX = 10;

    /**
     * Represents the minimum allowable channel number in the application.
     * The value of this constant is used as the lower bound when dealing
     * with channel-related functionality and ensures proper configuration.
     */
    private static final int CHANNEL_MIN = 1;

    /**
     * Represents the primary data model used by the MainController to manage application state and logic.
     * This model contains the core data structures and methods necessary for handling business logic
     * and interactions within the application.
     * <p>
     * The MainController relies on this model to process and update relevant data during runtime, and
     * to coordinate the behavior of the user interface and underlying components.
     */
    private final MainModel model;

    /**
     * Represents the primary window of the application's user interface.
     * This field holds a reference to the {@link MainWindow} implementation
     * used by the {@code MainController} to manage and interact with the UI components.
     * <p>
     * The {@link MainWindow} interface provides access to various views such as
     * harp settings, microphone settings, note settings, harp view, and training view,
     * and allows for the assignment of corresponding handlers to manage specific
     * functionality for these views.
     * <p>
     * This field is immutable and is initialized in the constructor of {@code MainController}.
     */
    private final MainWindow window;
    /**
     * Manages a fixed thread pool for executing asynchronous tasks in the MainController.
     * Provides concurrency control and efficient resource management for background operations.
     * This ExecutorService handles various tasks related to the functionality of the MainController,
     * such as note updates, processing input data, and managing training operations.
     * Ensures safe execution of tasks in a multithreaded environment.
     */
    private final ExecutorService executorService;
    /**
     * An array of NoteContainer objects used to manage and represent musical notes within the
     * context of the application. Each NoteContainer corresponds to a specific note and its associated
     * properties, behaviors, and interactions with other components such as the harmonica and views.
     */
    private NoteContainer[] noteContainers;
    /**
     * Handles the TrainingContainer instance within the MainController class.
     * Responsible for managing and coordinating training-related operations,
     * including interaction with the training view and training logic.
     */
    private TrainingContainer trainingContainer;

    /**
     * Constructs a new MainController instance with the specified MainWindow and MainModel.
     * Initializes the application by setting up harmonica, training, microphone, and view handlers.
     * Also configures the stored settings for concert pitch, key, tune, training, and microphone parameters.
     *
     * @param window the main application window interface that acts as a view layer
     * @param model  the main application model containing data and logic
     */
    public MainController(MainWindow window, MainModel model) {
        log.info("Initializing MainController with MainWindow and MainModel...");

        this.window = window;
        this.model = model;
        this.executorService = Executors.newCachedThreadPool();

        log.info("Setting stored reference pitch based on the model's stored concert pitch index...");
        NoteLookup.setConcertPitchByIndex(model.getStoredConcertPitchIndex());

        log.info("Creating and setting harmonica using stored key and tune indices...");
        this.model.setHarmonica(AbstractHarmonica.create(model.getStoredKeyIndex(), model.getStoredTuneIndex()));

        log.info("Creating and setting training object using stored key and training indices...");
        this.model.setTraining(AbstractTraining.create(model.getStoredKeyIndex(), model.getStoredTrainingIndex()));

        Microphone microphone = model.getMicrophone();
        log.info("Configuring microphone with stored settings...");
        microphone.setAlgorithm(model.getStoredAlgorithmIndex());
        microphone.setName(model.getStoredMicrophoneIndex());
        microphone.setConfidence(model.getStoredConfidenceIndex());
        this.model.setMicrophone(microphone);

        log.info("Assigning view handlers to MainWindow...");
        this.window.setMicrophoneSettingsViewHandler(this);
        this.window.setHarpSettingsViewHandler(this);
        this.window.setHarpViewHandler(this);
        this.window.setNoteSettingsViewHandler(this);
        this.window.setTrainingViewHandler(this);

        log.info("Registering microphone handler...");
        microphone.setMicrophoneHandler(this);

        log.info("MainController initialized successfully.");
    }


    @Override
    public void handleAlgorithmSelection(int algorithmIndex) {
        log.info("Handling algorithm selection: algorithmIndex={}", algorithmIndex);

        log.debug("Updating stored algorithm index in the model...");
        this.model.setStoredAlgorithmIndex(algorithmIndex);

        log.debug("Fetching microphone from model...");
        Microphone microphone = model.getMicrophone();

        log.info("Closing the current microphone by handleAlgorithmSelection...");
        microphone.close();

        log.info("Setting the microphone to use algorithm: index={}", algorithmIndex);
        microphone.setAlgorithm(algorithmIndex);

        log.info("Reopening the microphone with the updated algorithm...");
        microphone.open();

        log.info("Algorithm selection process completed successfully.");
    }


    @Override
    public void handleKeySelection(int keyIndex) {
        log.info("Handling key selection: keyIndex={}", keyIndex);

        log.debug("Updating stored key index in the model...");
        this.model.setStoredKeyIndex(keyIndex);

        log.info("Creating and setting a new harmonica based on the selected key and the stored tune...");
        model.setHarmonica(AbstractHarmonica.create(keyIndex, model.getStoredTuneIndex()));

        log.info("Key selection process completed successfully.");
    }


    @Override
    public void handleMicrophoneSelection(int microphoneIndex) {
        log.info("Handling microphone selection: microphoneIndex={}", microphoneIndex);

        log.debug("Updating stored microphone index in the model...");
        this.model.setStoredMicrophoneIndex(microphoneIndex);

        log.debug("Fetching microphone from the model...");
        Microphone microphone = model.getMicrophone();

        log.info("Closing the current microphone by handleMicrophoneSelection...");
        microphone.close();

        log.info("Setting the microphone to use the new configuration: index={}", microphoneIndex);
        microphone.setName(microphoneIndex);

        log.info("Reopening the microphone with the updated configuration...");
        microphone.open();

        log.info("Microphone selection process completed successfully.");
    }

    @Override
    public void handleTuneSelection(int tuneIndex) {
        log.info("Handling tune selection: tuneIndex={}", tuneIndex);

        log.debug("Updating stored tune index in the model...");
        this.model.setStoredTuneIndex(tuneIndex);

        log.info("Creating and setting a new harmonica based on the stored key and the selected tune...");
        model.setHarmonica(AbstractHarmonica.create(model.getStoredKeyIndex(), tuneIndex));

        log.info("Tune selection process completed successfully.");
    }


    @Override
    public void handle(double frequency, double volume) {
        log.debug("Handling microphone input: frequency={}, volume={}", frequency, volume);

        log.debug("Updating microphone settings view with volume={}", volume);
        updateMicrophoneSettingsViewVolume(volume);

        log.debug("Updating microphone settings view with frequency={}", frequency);
        updateMicrophoneSettingsViewFrequency(frequency);

        log.debug("Updating harp view with frequency={}", frequency);
        updateHarpView(frequency);

        log.debug("Updating training view with frequency={}", frequency);
        updateTrainingView(frequency);

        log.debug("Microphone input handling process completed successfully.");
    }

    /**
     * Updates the training view based on the provided frequency. If the training view
     * is active and the trainingContainer is initialized, it sets the frequency to be
     * handled by the trainingContainer and submits it to the executor service for processing.
     *
     * @param frequency the frequency value to update the training view with
     */
    private void updateTrainingView(double frequency) {
        log.debug("Updating training view with frequency={}", frequency);

        if (!executorService.isShutdown() && isTrainingViewActiveAndInitialized()) {
            log.debug("Training view is active and initialized. Updating frequency and submitting to executor.");

            trainingContainer.setFrequencyToHandle(frequency);

            log.debug("Submitting trainingContainer to executorService...");
            executorService.submit(trainingContainer);
        } else {
            log.debug("Training view update skipped. Either executorService is shut down or the training view is not active/initialized.");
        }

        log.debug("Training view update process completed.");
    }

    /**
     * Checks if the training view is active and the training container is initialized.
     *
     * @return true if the training view is active and the training container is not null; false otherwise
     */
    private boolean isTrainingViewActiveAndInitialized() {
        log.debug("Checking if training view is active and initialized...");

        boolean isActive = window.isTrainingViewActive();
        boolean isInitialized = this.trainingContainer != null;

        log.debug("Training view active: {}", isActive);
        log.debug("Training container initialized: {}", isInitialized);

        boolean result = isActive && isInitialized;

        if (result) {
            log.debug("Training view is active and initialized.");
        } else {
            log.debug("Training view is not active or not initialized. Active: {}, Initialized: {}", isActive, isInitialized);
        }

        return result;
    }

    @Override
    public void initAlgorithmList() {
        log.info("Initializing algorithm list...");

        if (window.isMicrophoneSettingsViewActive()) {
            log.debug("Microphone settings view is active. Proceeding to initialize the algorithm list.");

            MicrophoneSettingsView microphoneSettingsView = window.getMicrophoneSettingsView();

            log.debug("Setting algorithms in the microphone settings view...");
            microphoneSettingsView.setAlgorithms(model.getAlgorithms());

            log.debug("Setting the selected algorithm in the microphone settings view...");
            microphoneSettingsView.setSelectedAlgorithm(model.getSelectedAlgorithmIndex());

            log.info("Algorithm list initialization completed successfully.");
        } else {
            log.warn("Algorithm list initialization skipped. Microphone settings view is not active.");
        }
    }

    @Override
    public void initKeyList() {
        log.info("Initializing key list...");

        if (window.isHarpSettingsViewActive()) {
            log.debug("Harp settings view is active. Proceeding to initialize the key list.");

            HarpSettingsView harpSettingsView = window.getHarpSettingsView();

            log.debug("Setting keys in the harp settings view...");
            harpSettingsView.setKeys(model.getKeys());

            log.debug("Setting the selected key in the harp settings view...");
            harpSettingsView.setSelectedKey(model.getSelectedKeyIndex());

            log.info("Key list initialization completed successfully.");
        } else {
            log.warn("Key list initialization skipped. Harp settings view is not active.");
        }
    }

    @Override
    public void initMicrophoneList() {
        log.info("Initializing microphone list...");

        if (window.isMicrophoneSettingsViewActive()) {
            log.debug("Microphone settings view is active. Proceeding to initialize the microphone list.");

            MicrophoneSettingsView microphoneSettingsView = window.getMicrophoneSettingsView();

            log.debug("Setting microphone list in the microphone settings view...");
            microphoneSettingsView.setMicrophones(model.getMicrophones());

            log.debug("Setting the selected microphone in the microphone settings view...");
            microphoneSettingsView.setSelectedMicrophone(model.getSelectedMicrophoneIndex());

            log.info("Microphone list initialization completed successfully.");
        } else {
            log.warn("Microphone list initialization skipped. Microphone settings view is not active.");
        }
    }

    @Override
    public void initConfidenceList() {
        log.info("Initializing confidence list...");

        if (window.isMicrophoneSettingsViewActive()) {
            log.debug("Microphone settings view is active. Proceeding to initialize the confidence list.");

            MicrophoneSettingsView microphoneSettingsView = window.getMicrophoneSettingsView();

            log.debug("Setting confidence list in the microphone settings view...");
            microphoneSettingsView.setConfidences(model.getConfidences());

            log.debug("Setting the selected confidence value in the microphone settings view...");
            microphoneSettingsView.setSelectedConfidence(model.getSelectedConfidenceIndex());

            log.info("Confidence list initialization completed successfully.");
        } else {
            log.warn("Confidence list initialization skipped. Microphone settings view is not active.");
        }
    }

    @Override
    public void handleConfidenceSelection(int confidenceIndex) {
        log.info("Handling confidence selection. Selected confidence index: {}", confidenceIndex);

        log.debug("Updating stored confidence index in the model...");
        this.model.setStoredConfidenceIndex(confidenceIndex);

        Microphone microphone = model.getMicrophone();
        if (microphone != null) {
            log.debug("Setting confidence index {} on the microphone...", confidenceIndex);
            microphone.setConfidence(confidenceIndex);
            log.info("Confidence selection handled successfully. Microphone updated.");
        } else {
            log.warn("Cannot set confidence index. Microphone object is null.");
        }
    }

    @Override
    public void initNotes() {
        log.info("Initializing notes...");

        if (window.isHarpViewActive()) {
            log.debug("Harp view is active. Proceeding to initialize notes...");

            ArrayList<NoteContainer> notesList = new ArrayList<>();
            Harmonica harmonica = model.getHarmonica();
            HarpView harpView = window.getHarpView();

            for (int channel = CHANNEL_MIN; channel <= CHANNEL_MAX; channel++) {
                log.debug("Processing channel {}...", channel);

                // Blowing notes
                double frequency0 = harmonica.getNoteFrequency(channel, 0);
                boolean hasInverseCentsHandling = harmonica.hasInverseCentsHandling(channel);
                String note0 = NoteLookup.getNoteName(frequency0);
                if (note0 != null) {
                    log.debug("Adding blowing note for channel {} with frequency: {} and note: {}", channel, frequency0, note0);
                    notesList.add(new NoteContainer(channel, 0, note0, harmonica, harpView, hasInverseCentsHandling));
                } else {
                    log.warn("Blowing note lookup for frequency {} returned null for channel {}", frequency0, channel);
                }

                // Drawing notes
                double frequency1 = harmonica.getNoteFrequency(channel, 1);
                String note1 = NoteLookup.getNoteName(frequency1);
                if (note1 != null) {
                    log.debug("Adding drawing note for channel {} with frequency: {} and note: {}", channel, frequency1, note1);
                    notesList.add(new NoteContainer(channel, 1, note1, harmonica, harpView, hasInverseCentsHandling));
                } else {
                    log.warn("Drawing note lookup for frequency {} returned null for channel {}", frequency1, channel);
                }

                // Bending notes
                int blowBendingCount = harmonica.getBlowBendingTonesCount(channel);
                int drawBendingCount = harmonica.getDrawBendingTonesCount(channel);
                log.debug("Processing bending notes for channel {} with blow bending count: {}, draw bending count: {}", channel, blowBendingCount, drawBendingCount);
                for (int note = 2; note < 2 + drawBendingCount; note++) {
                    String noteEntry = NoteLookup.getNoteName(harmonica.getNoteFrequency(channel, note));
                    if (noteEntry != null) {
                        log.debug("Adding draw bending note for channel {}: {}", channel, noteEntry);
                        notesList.add(new NoteContainer(channel, note, noteEntry, harmonica, harpView));
                    } else {
                        log.warn("Bending note lookup failed for channel {}, note index {}", channel, note);
                    }
                }
                for (int note = -blowBendingCount; note < 0; note++) {
                    String noteEntry = NoteLookup.getNoteName(harmonica.getNoteFrequency(channel, note));
                    if (noteEntry != null) {
                        log.debug("Adding blow bending note for channel {}: {}", channel, noteEntry);
                        notesList.add(new NoteContainer(channel, note, noteEntry, harmonica, harpView, true));
                    } else {
                        log.warn("Bending note lookup failed for channel {}, note index {}", channel, note);
                    }
                }

                // Overblows
                if (!hasInverseCentsHandling) {
                    double overblowFrequency = harmonica.getNoteFrequency(channel, -1);
                    String noteEntry = NoteLookup.getNoteName(overblowFrequency);
                    if (noteEntry != null) {
                        log.debug("Adding overblow for channel {}: {}", channel, noteEntry);
                        notesList.add(new NoteContainer(channel, -1, noteEntry, harmonica, harpView, false));
                    } else {
                        log.warn("Overblow note lookup failed for channel {}, frequency {}", channel, overblowFrequency);
                    }
                }

                // Overdraws
                if (hasInverseCentsHandling) {
                    double overdrawFrequency = harmonica.getNoteFrequency(channel, 2);
                    String noteEntry = NoteLookup.getNoteName(overdrawFrequency);
                    if (noteEntry != null) {
                        log.debug("Adding overdraw for channel {}: {}", channel, noteEntry);
                        notesList.add(new NoteContainer(channel, 2, noteEntry, harmonica, harpView, true));
                    } else {
                        log.warn("Overdraw note lookup failed for channel {}, frequency {}", channel, overdrawFrequency);
                    }
                }
            }

            log.debug("Finished processing all channels. Total notes generated: {}", notesList.size());
            this.noteContainers = Arrays.copyOf(notesList.toArray(), notesList.size(), NoteContainer[].class);

            log.debug("Initializing notes in the harp view...");
            harpView.initNotes(noteContainers);
            log.info("Notes initialization completed successfully.");
        } else {
            log.warn("Notes initialization skipped. Harp view is not active.");
        }
    }

    @Override
    public void initTuneList() {
        log.info("Initializing tune list...");

        if (window.isHarpSettingsViewActive()) {
            log.debug("Harp settings view is active. Proceeding to initialize the tune list.");

            HarpSettingsView harpSettingsView = window.getHarpSettingsView();

            log.debug("Setting tunes in the harp settings view...");
            harpSettingsView.setTunes(model.getTunes());

            log.debug("Setting the selected tune in the harp settings view...");
            harpSettingsView.setSelectedTune(model.getSelectedTuneIndex());

            log.info("Tune list initialization completed successfully.");
        } else {
            log.warn("Tune list initialization skipped. Harp settings view is not active.");
        }
    }

    /**
     * Starts the primary components of the application.
     * <p>
     * This method initializes the application by opening the microphone for audio
     * input and launching the main application window for user interaction. It relies
     * on the underlying MainModel and MainWindow instances to perform these actions.
     */
    public void start() {
        log.info("Starting application...");

        log.debug("Opening microphone...");
        this.model.getMicrophone().open();

        log.debug("Opening main application window...");
        this.window.open();

        log.info("Application started successfully.");
    }

    /**
     * Stops the application by shutting down essential components.
     * <p>
     * This method closes the microphone resource to ensure proper release
     * of audio input devices and shuts down the executor service
     * to terminate any ongoing asynchronous operations.
     */
    public void stop() {
        log.info("Stopping application...");

        log.debug("Closing microphone...");
        this.model.getMicrophone().close();

        log.debug("Shutting down executor service...");
        executorService.shutdown();

        log.info("Application stopped successfully.");
    }

    /**
     * Updates the harp view based on the provided frequency. If the harp view is active
     * and the note containers are initialized, it sets the frequency to be handled by
     * each NoteContainer and submits them to the executor service for asynchronous processing.
     *
     * @param frequency the frequency value to update the harp view with
     */
    private void updateHarpView(double frequency) {
        log.debug("Updating harp view with frequency: {}", frequency);

        if (!executorService.isShutdown() && isHarpViewActiveAndInitialized()) {
            log.debug("Executor service is active, and harp view is initialized. Proceeding to update note containers.");

            for (NoteContainer noteContainer : noteContainers) {
                log.debug("Setting frequency {} to handle for note container: {}", frequency, noteContainer);
                noteContainer.setFrequencyToHandle(frequency);

                log.debug("Submitting note container to executor service: {}", noteContainer);
                executorService.submit(noteContainer);
            }

            log.debug("Harp view update completed successfully.");
        } else {
            log.debug("Harp view update skipped. Either executor service is shut down or harp view is not active/initialized.");
        }
    }

    /**
     * Checks if the harp view is active and initialized.
     * The method verifies whether the harp view is currently active
     * by calling the window's isHarpViewActive method and ensures
     * the noteContainers object is not null.
     *
     * @return true if the harp view is active and the noteContainers are initialized; false otherwise
     */
    private boolean isHarpViewActiveAndInitialized() {
        boolean isActive = window.isHarpViewActive();
        boolean isInitialized = (this.noteContainers != null);

        log.debug("Checking if harp view is active and initialized...");
        log.debug("Harp view active: {}", isActive);
        log.debug("Note containers initialized: {}", isInitialized);

        if (isActive && isInitialized) {
            log.debug("Harp view is active and initialized.");
            return true;
        } else {
            log.debug("Harp view is not active or not initialized. Active: {}, Initialized: {}", isActive, isInitialized);
            return false;
        }
    }
    /**
     * Updates the microphone settings view with the specified frequency.
     * <p>
     * If the microphone settings view is active, this method retrieves the
     * instance of the MicrophoneSettingsView from the window and sets the
     * given frequency.
     *
     * @param frequency the frequency value to update in the microphone settings view
     */
    private void updateMicrophoneSettingsViewFrequency(double frequency) {
        log.debug("Updating microphone settings view with frequency: {}", frequency);

        if (window.isMicrophoneSettingsViewActive()) {
            log.debug("Microphone settings view is active. Proceeding with frequency update.");

            MicrophoneSettingsView microphoneSettingsView = window.getMicrophoneSettingsView();

            log.debug("Setting frequency {} in MicrophoneSettingsView: {}", frequency, microphoneSettingsView);
            microphoneSettingsView.setFrequency(frequency);

            log.debug("Microphone settings view frequency updated successfully.");
        } else {
            log.debug("Microphone settings view is not active. Frequency update skipped.");
        }
    }

    /**
     * Updates the microphone settings view with the specified volume level.
     * <p>
     * If the microphone settings view is active, this method retrieves the
     * instance of the MicrophoneSettingsView from the window and sets the
     * given volume value.
     *
     * @param volume the volume level to update in the microphone settings view
     */
    private void updateMicrophoneSettingsViewVolume(double volume) {
        log.debug("Updating microphone settings view with volume: {}", volume);

        if (window.isMicrophoneSettingsViewActive()) {
            log.debug("Microphone settings view is active. Proceeding with volume update.");

            MicrophoneSettingsView microphoneSettingsView = window.getMicrophoneSettingsView();

            log.debug("Setting volume {} in MicrophoneSettingsView: {}", volume, microphoneSettingsView);
            microphoneSettingsView.setVolume(volume);

            log.debug("Microphone settings view volume updated successfully.");
        } else {
            log.debug("Microphone settings view is not active. Volume update skipped.");
        }
    }

    @Override
    public void handleConcertPitchSelection(int pitchIndex) {
        log.info("Handling concert pitch selection. Selected pitch index: {}", pitchIndex);

        log.debug("Updating stored concert pitch index in the model to: {}", pitchIndex);
        this.model.setStoredConcertPitchIndex(pitchIndex);

        log.debug("Setting concert pitch in NoteLookup using pitch index: {}", pitchIndex);
        NoteLookup.setConcertPitchByIndex(pitchIndex);

        log.debug("Updating harmonica in the model using stored key index and tune index.");
        int storedKeyIndex = model.getStoredKeyIndex();
        int storedTuneIndex = model.getStoredTuneIndex();
        log.debug("Stored key index: {}, Stored tune index: {}", storedKeyIndex, storedTuneIndex);

        model.setHarmonica(AbstractHarmonica.create(storedKeyIndex, storedTuneIndex));

        log.info("Concert pitch selection handled successfully. Model updated with the new harmonica.");
    }

    @Override
    public void initConcertPitchList() {
        log.info("Initializing concert pitch list...");

        if (window.isNoteSettingsViewActive()) {
            log.debug("Note settings view is active. Proceeding with initialization.");

            NoteSettingsView noteSettingsView = window.getNoteSettingsView();

            log.debug("Setting concert pitches in NoteSettingsView.");
            noteSettingsView.setConcertPitches(model.getConcertPitches());

            log.debug("Setting selected concert pitch in NoteSettingsView. Selected pitch index: {}", model.getSelectedConcertPitchIndex());
            noteSettingsView.setSelectedConcertPitch(model.getSelectedConcertPitchIndex());

            log.info("Concert pitch list initialized successfully in NoteSettingsView.");
        } else {
            log.warn("Note settings view is not active. Initialization of concert pitch list skipped.");
        }
    }

    @Override
    public void initTrainingList() {
        log.info("Initializing training list...");

        if (window.isTrainingViewActive()) {
            log.debug("Training view is active. Proceeding with initialization.");

            TrainingView trainingView = window.getTrainingView();

            log.debug("Setting training list in TrainingView.");
            trainingView.setTrainings(model.getTrainings());

            log.debug("Setting selected training in TrainingView. Selected training index: {}", model.getSelectedTrainingIndex());
            trainingView.setSelectedTraining(model.getSelectedTrainingIndex());

            log.info("Training list initialized successfully in TrainingView.");
        } else {
            log.warn("Training view is not active. Initialization of training list skipped.");
        }
    }

    @Override
    public void initPrecisionList() {
        log.info("Initializing precision list...");

        if (window.isTrainingViewActive()) {
            log.debug("Training view is active in initPrecisionList(). Proceeding with initialization.");

            TrainingView trainingView = window.getTrainingView();

            log.debug("Setting precision list in TrainingView.");
            trainingView.setPrecisions(model.getPrecisions());

            log.debug("Setting selected precision in TrainingView. Selected precision index: {}", model.getSelectedPrecisionIndex());
            trainingView.setSelectedPrecision(model.getSelectedPrecisionIndex());

            log.info("Precision list initialized successfully in TrainingView.");
        } else {
            log.warn("Training view is not active. Initialization of precision list skipped.");
        }
    }

    @Override
    public void handleTrainingSelection(int trainingIndex) {
        log.info("Handling training selection. Selected training index: {}", trainingIndex);

        log.debug("Updating stored training index in the model to: {}", trainingIndex);
        this.model.setStoredTrainingIndex(trainingIndex);

        log.debug("Creating and setting the training in the model. Using stored key index: {} and training index: {}",
                model.getStoredKeyIndex(), trainingIndex);
        model.setTraining(AbstractTraining.create(model.getStoredKeyIndex(), trainingIndex));

        log.debug("Initializing training container after selection.");
        initTrainingContainer();

        log.info("Training selection handled successfully. Training and container updated.");
    }

    @Override
    public void initTrainingContainer() {
        log.info("Initializing training container...");

        if (window.isTrainingViewActive()) {
            log.debug("Training view is active. Proceeding with training container initialization.");

            this.trainingContainer = new TrainingContainer(this.model.getTraining(), window.getTrainingView());
            log.debug("Created new TrainingContainer with current training model and training view.");

            TrainingView trainingView = window.getTrainingView();
            log.debug("Passing TrainingContainer to TrainingView for initialization.");
            trainingView.initTrainingContainer(trainingContainer);

            log.info("Training container initialized successfully.");
        } else {
            log.warn("Training view is not active. Initialization of training container skipped.");
        }
    }

    @Override
    public void handleTrainingStart() {
        log.info("Starting training...");

        Training training = this.model.getTraining();
        if (training != null) {
            log.debug("Retrieved training from model. Training details: {}", training);

            training.start();
            log.info("Training started successfully.");

            log.debug("Reinitializing training container after training start.");
            initTrainingContainer();

            log.info("Training container reinitialized successfully after training start.");
        } else {
            log.warn("No training found in model. Training start and container initialization skipped.");
        }
    }

    @Override
    public void handleTrainingStop() {
        log.info("Stopping training...");

        Training training = this.model.getTraining();
        if (training != null) {
            log.debug("Retrieved training from model in handleTrainingStop(). Training details: {}", training);

            training.stop();
            log.info("Training stopped successfully.");
        } else {
            log.warn("No training found in model. Training stop skipped.");
        }
    }

    @Override
    public void handlePrecisionSelection(int selectedIndex) {
        log.info("Handling precision selection. Selected precision index: {}", selectedIndex);

        log.debug("Updating stored precision index to: {}", selectedIndex);
        this.model.setStoredPrecisionIndex(selectedIndex);

        try {
            String[] supportedPrecisions = AbstractTraining.getSupportedPrecisions();
            log.debug("Supported precisions: {}", (Object) supportedPrecisions);

            int precision = Integer.parseInt(supportedPrecisions[selectedIndex]);
            log.debug("Setting precision to: {}", precision);

            AbstractTraining.setPrecision(precision);
            log.info("Precision selection handled successfully. Precision set to: {}", precision);

        } catch (ArrayIndexOutOfBoundsException e) {
            log.error("Selected index is out of bounds. Supported precisions length: {}, selected index: {}",
                    AbstractTraining.getSupportedPrecisions().length, selectedIndex, e);
        } catch (NumberFormatException e) {
            log.error("Failed to parse precision value at index: {}", selectedIndex, e);
        }
    }
}
