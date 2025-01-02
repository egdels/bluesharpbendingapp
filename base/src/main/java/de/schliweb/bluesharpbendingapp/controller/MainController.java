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
        this.window = window;
        this.model = model;
        this.executorService = Executors.newCachedThreadPool();
        // Gespeicherten Kammerton, vor Erstellung der Harmonika setzen
        NoteLookup.setConcertPitchByIndex(model.getStoredConcertPitchIndex());
        this.model.setHarmonica(AbstractHarmonica.create(model.getStoredKeyIndex(), model.getStoredTuneIndex()));
        this.model.setTraining(AbstractTraining.create(model.getStoredKeyIndex(), model.getStoredTrainingIndex()));
        Microphone microphone = model.getMicrophone();
        microphone.setAlgorithm(model.getStoredAlgorithmIndex());
        microphone.setName(model.getStoredMicrophoneIndex());
        this.model.setMicrophone(microphone);
        this.window.setMicrophoneSettingsViewHandler(this);
        this.window.setHarpSettingsViewHandler(this);
        this.window.setHarpViewHandler(this);
        this.window.setNoteSettingsViewHandler(this);
        this.window.setTrainingViewHandler(this);
        microphone.setMicrophoneHandler(this);
    }

    @Override
    public void handleAlgorithmSelection(int algorithmIndex) {
        this.model.setStoredAlgorithmIndex(algorithmIndex);
        Microphone microphone = model.getMicrophone();
        microphone.close();
        microphone.setAlgorithm(algorithmIndex);
        microphone.open();

    }

    @Override
    public void handleKeySelection(int keyIndex) {
        this.model.setStoredKeyIndex(keyIndex);
        model.setHarmonica(AbstractHarmonica.create(keyIndex, model.getStoredTuneIndex()));
    }

    @Override
    public void handleMicrophoneSelection(int microphoneIndex) {
        this.model.setStoredMicrophoneIndex(microphoneIndex);
        Microphone microphone = model.getMicrophone();
        microphone.close();
        microphone.setName(microphoneIndex);
        microphone.open();
    }

    @Override
    public void handleTuneSelection(int tuneIndex) {
        this.model.setStoredTuneIndex(tuneIndex);
        model.setHarmonica(AbstractHarmonica.create(model.getStoredKeyIndex(), tuneIndex));
    }


    @Override
    public void handle(double frequency, double volume) {
        updateMicrophoneSettingsViewVolume(volume);
        updateMicrophoneSettingsViewFrequency(frequency);
        updateHarpView(frequency);
        updateTrainingView(frequency);
    }

    /**
     * Updates the training view based on the provided frequency. If the training view
     * is active and the trainingContainer is initialized, it sets the frequency to be
     * handled by the trainingContainer and submits it to the executor service for processing.
     *
     * @param frequency the frequency value to update the training view with
     */
    private void updateTrainingView(double frequency) {
        if (!executorService.isShutdown())
            if (window.isTrainingViewActive() && (this.trainingContainer != null)) {
                trainingContainer.setFrequencyToHandle(frequency);
                executorService.submit(trainingContainer);
            }
    }

    @Override
    public void initAlgorithmList() {
        if (window.isMicrophoneSettingsViewActive()) {
            MicrophoneSettingsView microphoneSettingsView = window.getMicrophoneSettingsView();
            microphoneSettingsView.setAlgorithms(model.getAlgorithms());
            microphoneSettingsView.setSelectedAlgorithm(model.getSelectedAlgorithmIndex());
        }
    }

    @Override
    public void initKeyList() {
        if (window.isHarpSettingsViewActive()) {
            HarpSettingsView harpSettingsView = window.getHarpSettingsView();
            harpSettingsView.setKeys(model.getKeys());
            harpSettingsView.setSelectedKey(model.getSelectedKeyIndex());
        }
    }

    @Override
    public void initMicrophoneList() {
        if (window.isMicrophoneSettingsViewActive()) {
            MicrophoneSettingsView microphoneSettingsView = window.getMicrophoneSettingsView();
            microphoneSettingsView.setMicrophones(model.getMicrophones());
            microphoneSettingsView.setSelectedMicrophone(model.getSelectedMicrophoneIndex());
        }
    }

    @Override
    public void initNotes() {
        if (window.isHarpViewActive()) {
            ArrayList<NoteContainer> notesList = new ArrayList<>();
            Harmonica harmonica = model.getHarmonica();
            HarpView harpView = window.getHarpView();

            for (int channel = CHANNEL_MIN; channel <= CHANNEL_MAX; channel++) {
                // Blastöne
                double frequency0 = harmonica.getNoteFrequency(channel, 0);
                boolean hasInverseCentsHandling = harmonica.hasInverseCentsHandling(channel);
                String note0 = NoteLookup.getNoteName(frequency0);
                assert note0 != null;
                notesList.add(new NoteContainer(channel, 0, note0, harmonica, harpView, hasInverseCentsHandling));

                // Ziehtöne
                double frequency1 = harmonica.getNoteFrequency(channel, 1);
                String note1 = NoteLookup.getNoteName(frequency1);
                assert note1 != null;
                notesList.add(new NoteContainer(channel, 1, note1, harmonica, harpView, hasInverseCentsHandling));

                // Bendingtöne
                int blowBendingCount = harmonica.getBlowBendingTonesCount(channel);
                int drawBendingCount = harmonica.getDrawBendingTonesCount(channel);
                for (int note = 2; note < 2 + drawBendingCount; note++) {
                    String noteEntry = NoteLookup.getNoteName(harmonica.getNoteFrequency(channel, note));
                    assert noteEntry != null;
                    notesList.add(new NoteContainer(channel, note, noteEntry, harmonica, harpView));
                }
                for (int note = -blowBendingCount; note < 0; note++) {
                    String noteEntry = NoteLookup.getNoteName(harmonica.getNoteFrequency(channel, note));
                    assert noteEntry != null;
                    notesList.add(new NoteContainer(channel, note, noteEntry, harmonica, harpView, true));
                }

                // Overblows
                if (!hasInverseCentsHandling) {
                    String noteEntry = NoteLookup.getNoteName(harmonica.getNoteFrequency(channel, -1));
                    assert noteEntry != null;
                    notesList.add(new NoteContainer(channel, -1, noteEntry, harmonica, harpView, false));
                }

                // Overdraws
                if (hasInverseCentsHandling) {
                    String noteEntry = NoteLookup.getNoteName(harmonica.getNoteFrequency(channel, 2));
                    assert noteEntry != null;
                    notesList.add(new NoteContainer(channel, 2, noteEntry, harmonica, harpView, true));
                }
            }
            this.noteContainers = Arrays.copyOf(notesList.toArray(), notesList.toArray().length, NoteContainer[].class);
            harpView.initNotes(Arrays.copyOf(notesList.toArray(), notesList.toArray().length, NoteContainer[].class));
        }
    }

    @Override
    public void initTuneList() {
        if (window.isHarpSettingsViewActive()) {
            HarpSettingsView harpSettingsView = window.getHarpSettingsView();
            harpSettingsView.setTunes(model.getTunes());
            harpSettingsView.setSelectedTune(model.getSelectedTuneIndex());
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
        this.model.getMicrophone().open();
        this.window.open();
    }

    /**
     * Stops the application by shutting down essential components.
     * <p>
     * This method closes the microphone resource to ensure proper release
     * of audio input devices and shuts down the executor service
     * to terminate any ongoing asynchronous operations.
     */
    public void stop() {
        this.model.getMicrophone().close();
        executorService.shutdown();
    }

    /**
     * Updates the harp view based on the provided frequency. If the harp view is active
     * and the note containers are initialized, it sets the frequency to be handled by
     * each NoteContainer and submits them to the executor service for asynchronous processing.
     *
     * @param frequency the frequency value to update the harp view with
     */
    private void updateHarpView(double frequency) {
        if (!executorService.isShutdown()) {
            if (window.isHarpViewActive() && this.noteContainers != null) {
                for (NoteContainer noteContainer : noteContainers) {
                    noteContainer.setFrequencyToHandle(frequency);
                    executorService.submit(noteContainer);
                }
            }
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
        if (window.isMicrophoneSettingsViewActive()) {
            MicrophoneSettingsView microphoneSettingsView = window.getMicrophoneSettingsView();
            microphoneSettingsView.setFrequency(frequency);
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
        if (window.isMicrophoneSettingsViewActive()) {
            MicrophoneSettingsView microphoneSettingsView = window.getMicrophoneSettingsView();
            microphoneSettingsView.setVolume(volume);
        }
    }

    @Override
    public void handleConcertPitchSelection(int pitchIndex) {
        this.model.setStoredConcertPitchIndex(pitchIndex);
        NoteLookup.setConcertPitchByIndex(pitchIndex);
        model.setHarmonica(AbstractHarmonica.create(model.getStoredKeyIndex(), model.getStoredTuneIndex()));
    }

    @Override
    public void initConcertPitchList() {
        if (window.isNoteSettingsViewActive()) {
            NoteSettingsView noteSettingsView = window.getNoteSettingsView();
            noteSettingsView.setConcertPitches(model.getConcertPitches());
            noteSettingsView.setSelectedConcertPitch(model.getSelectedConcertPitchIndex());
        }
    }

    @Override
    public void initTrainingList() {
        if (window.isTrainingViewActive()) {
            TrainingView trainingView = window.getTrainingView();
            trainingView.setTrainings(model.getTrainings());
            trainingView.setSelectedTraining(model.getSelectedTrainingIndex());
        }
    }

    @Override
    public void initPrecisionList() {
        if (window.isTrainingViewActive()) {
            TrainingView trainingView = window.getTrainingView();
            trainingView.setPrecisions(model.getPrecisions());
            trainingView.setSelectedPrecision(model.getSelectedPrecisionIndex());
        }
    }

    @Override
    public void handleTrainingSelection(int trainingIndex) {
        this.model.setStoredTrainingIndex(trainingIndex);
        model.setTraining(AbstractTraining.create(model.getStoredKeyIndex(), trainingIndex));
        initTrainingContainer();
    }

    @Override
    public void initTrainingContainer() {
        if (window.isTrainingViewActive()) {
            this.trainingContainer = new TrainingContainer(this.model.getTraining(), window.getTrainingView());
            TrainingView trainingView = window.getTrainingView();
            trainingView.initTrainingContainer(trainingContainer);
        }
    }

    @Override
    public void handleTrainingStart() {
        Training training = this.model.getTraining();
        training.start();
        initTrainingContainer();
    }

    @Override
    public void handleTrainingStop() {
        Training training = this.model.getTraining();
        training.stop();
    }

    @Override
    public void handlePrecisionSelection(int selectedIndex) {
        this.model.setStoredPrecisionIndex(selectedIndex);
        AbstractTraining.setPrecision(Integer.parseInt(AbstractTraining.getSupportedPrecisions()[selectedIndex]));
    }
}
