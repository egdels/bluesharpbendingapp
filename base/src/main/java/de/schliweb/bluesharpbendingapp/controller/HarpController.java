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
import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;
import de.schliweb.bluesharpbendingapp.utils.LoggingContext;
import de.schliweb.bluesharpbendingapp.utils.LoggingUtils;
import de.schliweb.bluesharpbendingapp.view.HarpSettingsView;
import de.schliweb.bluesharpbendingapp.view.HarpView;
import de.schliweb.bluesharpbendingapp.view.MainWindow;
import de.schliweb.bluesharpbendingapp.view.NoteSettingsView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

/**
 * The HarpController class handles all harmonica-related functionality.
 * It manages harmonica settings, view updates, and note processing.
 */
public class HarpController implements HarpSettingsViewHandler, HarpViewHandler, NoteSettingsViewHandler, HarpFrequencyHandler {

    /**
     * The maximum number of channels that can be utilized in the system.
     */
    private static final int CHANNEL_MAX = 10;

    /**
     * Represents the minimum allowable channel number in the application.
     */
    private static final int CHANNEL_MIN = 1;

    private final MainModel model;
    private final ModelStorageService modelStorageService;
    private final MainWindow window;
    private final ExecutorService executorService;
    private Harmonica harmonica;
    private NoteContainer[] noteContainers;

    /**
     * Constructs a new HarpController with the specified dependencies.
     *
     * @param model               The MainModel containing application state
     * @param modelStorageService The service for storing model data
     * @param window              The main application window
     * @param executorService     The executor service for asynchronous operations
     */
    public HarpController(MainModel model, ModelStorageService modelStorageService, MainWindow window, ExecutorService executorService) {
        LoggingContext.setComponent("HarpController");
        LoggingUtils.logInitializing("HarpController");

        this.model = model;
        this.modelStorageService = modelStorageService;
        this.window = window;
        this.executorService = executorService;

        LoggingUtils.logDebug("Creating and setting harmonica using stored key and tune indices");
        this.harmonica = AbstractHarmonica.create(model.getStoredKeyIndex(), model.getStoredTuneIndex());

        LoggingUtils.logInitialized("HarpController");
    }

    /**
     * Updates the harp view based on the provided frequency.
     *
     * @param frequency the frequency value to update the harp view with
     */
    public void updateHarpView(double frequency) {
        LoggingContext.setComponent("HarpController");
        LoggingUtils.logAudioProcessing("Updating harp view", "frequency=" + frequency);

        if (!executorService.isShutdown() && isHarpViewActiveAndInitialized()) {
            LoggingUtils.logDebug("Executor service is active, and harp view is initialized. Proceeding to update note containers");

            for (NoteContainer noteContainer : noteContainers) {
                LoggingUtils.logDebug("Setting frequency " + frequency + " to handle for note container: " + noteContainer);
                noteContainer.setFrequencyToHandle(frequency);

                LoggingUtils.logDebug("Submitting note container to executor service: " + noteContainer);
                executorService.submit(noteContainer);
            }

            LoggingUtils.logDebug("Harp view update completed successfully");
        } else {
            LoggingUtils.logDebug("Harp view update skipped. Either executor service is shut down or harp view is not active/initialized");
        }
    }

    @Override
    public void handleKeySelection(int keyIndex) {
        LoggingContext.setComponent("HarpController");
        LoggingUtils.logUserAction("Key selection", "keyIndex=" + keyIndex);

        LoggingUtils.logDebug("Updating stored key index in the model");
        this.model.setStoredKeyIndex(keyIndex);
        this.model.setSelectedKeyIndex(keyIndex);

        LoggingUtils.logDebug("Creating and setting a new harmonica based on the selected key and the stored tune");
        harmonica = AbstractHarmonica.create(keyIndex, model.getStoredTuneIndex());

        long startTime = System.currentTimeMillis();
        modelStorageService.storeModel(model);
        long duration = System.currentTimeMillis() - startTime;
        LoggingUtils.logPerformance("Model storage", duration);

        LoggingUtils.logOperationCompleted("Key selection");
    }

    @Override
    public void handleTuneSelection(int tuneIndex) {
        LoggingContext.setComponent("HarpController");
        LoggingUtils.logUserAction("Tune selection", "tuneIndex=" + tuneIndex);

        LoggingUtils.logDebug("Updating stored tune index in the model");
        this.model.setStoredTuneIndex(tuneIndex);
        this.model.setSelectedTuneIndex(tuneIndex);

        LoggingUtils.logDebug("Creating and setting a new harmonica based on the stored key and the selected tune");
        harmonica = AbstractHarmonica.create(model.getStoredKeyIndex(), tuneIndex);

        long startTime = System.currentTimeMillis();
        modelStorageService.storeModel(model);
        long duration = System.currentTimeMillis() - startTime;
        LoggingUtils.logPerformance("Model storage", duration);

        LoggingUtils.logOperationCompleted("Tune selection");
    }

    @Override
    public void initKeyList() {
        LoggingContext.setComponent("HarpController");
        LoggingUtils.logOperationStarted("Key list initialization");

        if (window.isHarpSettingsViewActive()) {
            LoggingUtils.logDebug("Harp settings view is active. Proceeding to initialize the key list");

            HarpSettingsView harpSettingsView = window.getHarpSettingsView();

            LoggingUtils.logDebug("Setting keys in the harp settings view");
            harpSettingsView.setKeys(AbstractHarmonica.getSupporterKeys());

            LoggingUtils.logDebug("Setting the selected key in the harp settings view");
            harpSettingsView.setSelectedKey(model.getSelectedKeyIndex());

            LoggingUtils.logOperationCompleted("Key list initialization");
        } else {
            LoggingUtils.logWarning("Key list initialization skipped", "Harp settings view is not active");
        }
    }

    @Override
    public void initTuneList() {
        LoggingContext.setComponent("HarpController");
        LoggingUtils.logOperationStarted("Tune list initialization");

        if (window.isHarpSettingsViewActive()) {
            LoggingUtils.logDebug("Harp settings view is active. Proceeding to initialize the tune list");

            HarpSettingsView harpSettingsView = window.getHarpSettingsView();

            LoggingUtils.logDebug("Setting tunes in the harp settings view");
            harpSettingsView.setTunes(AbstractHarmonica.getSupportedTunes());

            LoggingUtils.logDebug("Setting the selected tune in the harp settings view");
            harpSettingsView.setSelectedTune(model.getSelectedTuneIndex());

            LoggingUtils.logOperationCompleted("Tune list initialization");
        } else {
            LoggingUtils.logWarning("Tune list initialization skipped", "Harp settings view is not active");
        }
    }

    @Override
    public void initNotes() {
        LoggingContext.setComponent("HarpController");
        LoggingUtils.logOperationStarted("Notes initialization");

        if (!window.isHarpViewActive()) {
            LoggingUtils.logWarning("Notes initialization skipped", "Harp view is not active");
            return;
        }

        LoggingUtils.logDebug("Harp view is active. Proceeding to initialize notes");

        ArrayList<NoteContainer> notesList = new ArrayList<>();
        HarpView harpView = window.getHarpView();

        for (int channel = CHANNEL_MIN; channel <= CHANNEL_MAX; channel++) {
            processChannelNotes(channel, harmonica, harpView, notesList);
        }

        LoggingUtils.logDebug("Finished processing all channels. Total notes generated: " + notesList.size());
        this.noteContainers = Arrays.copyOf(notesList.toArray(), notesList.size(), NoteContainer[].class);

        LoggingUtils.logDebug("Initializing notes in the harp view");
        harpView.initNotes(noteContainers);
        LoggingUtils.logOperationCompleted("Notes initialization");
    }

    /**
     * Processes the musical notes associated with a specific harmonica channel.
     */
    private void processChannelNotes(int channel, Harmonica harmonica, HarpView harpView, ArrayList<NoteContainer> notesList) {
        LoggingContext.setComponent("HarpController");
        LoggingUtils.logDebug("Processing channel " + channel);

        addBlowingNotes(channel, harmonica, harpView, notesList);
        addDrawingNotes(channel, harmonica, harpView, notesList);
        addBendingNotes(channel, harmonica, harpView, notesList);
        addOverblowNotes(channel, harmonica, harpView, notesList);
        addOverdrawNotes(channel, harmonica, harpView, notesList);
    }

    /**
     * Adds a blowing note for the specified channel of the harmonica to the notes list.
     */
    private void addBlowingNotes(int channel, Harmonica harmonica, HarpView harpView, ArrayList<NoteContainer> notesList) {
        LoggingContext.setComponent("HarpController");

        double frequency = harmonica.getNoteFrequency(channel, 0);
        boolean hasInverseCentsHandling = harmonica.hasInverseCentsHandling(channel);
        String note = NoteLookup.getNoteName(frequency);

        if (note != null) {
            LoggingUtils.logDebug("Adding blowing note for channel " + channel + " with frequency: " + frequency + " and note: " + note);
            notesList.add(new NoteContainer(channel, 0, note, harmonica, harpView, hasInverseCentsHandling));
        } else {
            LoggingUtils.logWarning("Blowing note lookup returned null", "Frequency " + frequency + " for channel " + channel);
        }
    }

    /**
     * Adds drawing notes to the specified notes list based on the harmonica channel and its frequency.
     */
    private void addDrawingNotes(int channel, Harmonica harmonica, HarpView harpView, ArrayList<NoteContainer> notesList) {
        LoggingContext.setComponent("HarpController");

        double frequency = harmonica.getNoteFrequency(channel, 1);
        String note = NoteLookup.getNoteName(frequency);

        if (note != null) {
            LoggingUtils.logDebug("Adding drawing note for channel " + channel + " with frequency: " + frequency + " and note: " + note);
            notesList.add(new NoteContainer(channel, 1, note, harmonica, harpView));
        } else {
            LoggingUtils.logWarning("Drawing note lookup returned null", "Frequency " + frequency + " for channel " + channel);
        }
    }

    /**
     * Adds bending notes for a specific channel to the list of note containers.
     */
    private void addBendingNotes(int channel, Harmonica harmonica, HarpView harpView, ArrayList<NoteContainer> notesList) {
        LoggingContext.setComponent("HarpController");

        int blowBendingCount = harmonica.getBlowBendingTonesCount(channel);
        int drawBendingCount = harmonica.getDrawBendingTonesCount(channel);
        LoggingUtils.logDebug("Processing bending notes for channel " + channel +
                " with blow bending count: " + blowBendingCount +
                ", draw bending count: " + drawBendingCount);

        for (int note = 2; note < 2 + drawBendingCount; note++) {
            addNoteToList(channel, note, harmonica, harpView, notesList, "drawing bending");
        }
        for (int note = -blowBendingCount; note < 0; note++) {
            addNoteToList(channel, note, harmonica, harpView, notesList, "blow bending");
        }
    }

    /**
     * Adds overblow notes to the provided list of notes if the harmonica does not handle inverse cents for the specified channel.
     */
    private void addOverblowNotes(int channel, Harmonica harmonica, HarpView harpView, ArrayList<NoteContainer> notesList) {
        LoggingContext.setComponent("HarpController");

        if (!harmonica.hasInverseCentsHandling(channel)) {
            addNoteToList(channel, -1, harmonica, harpView, notesList, "overblow");
        }
    }

    /**
     * Adds overdraw notes to the provided notes list for the specified channel.
     */
    private void addOverdrawNotes(int channel, Harmonica harmonica, HarpView harpView, ArrayList<NoteContainer> notesList) {
        LoggingContext.setComponent("HarpController");

        if (harmonica.hasInverseCentsHandling(channel)) {
            addNoteToList(channel, 2, harmonica, harpView, notesList, "overdraw");
        }
    }

    /**
     * Adds a note to the provided list of notes.
     */
    private void addNoteToList(int channel, int noteIndex, Harmonica harmonica, HarpView harpView, ArrayList<NoteContainer> notesList, String logMessage) {
        LoggingContext.setComponent("HarpController");

        double frequency = harmonica.getNoteFrequency(channel, noteIndex);
        String note = NoteLookup.getNoteName(frequency);

        if (note != null) {
            LoggingUtils.logDebug("Adding " + logMessage + " note for channel " + channel +
                    " with frequency: " + frequency + " and note: " + note);
            notesList.add(new NoteContainer(channel, noteIndex, note, harmonica, harpView));
        } else {
            LoggingUtils.logWarning(logMessage + " note lookup failed",
                    "Channel " + channel + ", frequency " + frequency);
        }
    }

    @Override
    public void handleConcertPitchSelection(int pitchIndex) {
        LoggingContext.setComponent("HarpController");
        LoggingUtils.logUserAction("Concert pitch selection", "pitchIndex=" + pitchIndex);

        LoggingUtils.logDebug("Updating stored concert pitch index in the model to: " + pitchIndex);
        this.model.setStoredConcertPitchIndex(pitchIndex);
        this.model.setSelectedConcertPitchIndex(pitchIndex);

        LoggingUtils.logDebug("Setting concert pitch in NoteLookup using pitch index: " + pitchIndex);
        NoteLookup.setConcertPitchByIndex(pitchIndex);

        LoggingUtils.logDebug("Updating harmonica in the model using stored key index and tune index");
        int storedKeyIndex = model.getStoredKeyIndex();
        int storedTuneIndex = model.getStoredTuneIndex();
        LoggingUtils.logDebug("Stored key index: " + storedKeyIndex + ", Stored tune index: " + storedTuneIndex);

        harmonica = AbstractHarmonica.create(storedKeyIndex, storedTuneIndex);

        long startTime = System.currentTimeMillis();
        modelStorageService.storeModel(model);
        long duration = System.currentTimeMillis() - startTime;
        LoggingUtils.logPerformance("Model storage", duration);

        LoggingUtils.logOperationCompleted("Concert pitch selection");
    }

    @Override
    public void initConcertPitchList() {
        LoggingContext.setComponent("HarpController");
        LoggingUtils.logOperationStarted("Concert pitch list initialization");

        if (window.isNoteSettingsViewActive()) {
            LoggingUtils.logDebug("Note settings view is active. Proceeding with initialization");

            NoteSettingsView noteSettingsView = window.getNoteSettingsView();

            LoggingUtils.logDebug("Setting concert pitches in NoteSettingsView");
            noteSettingsView.setConcertPitches(NoteLookup.getSupportedConcertPitches());

            LoggingUtils.logDebug("Setting selected concert pitch in NoteSettingsView. Selected pitch index: " + model.getSelectedConcertPitchIndex());
            noteSettingsView.setSelectedConcertPitch(model.getSelectedConcertPitchIndex());

            LoggingUtils.logOperationCompleted("Concert pitch list initialization");
        } else {
            LoggingUtils.logWarning("Concert pitch list initialization skipped", "Note settings view is not active");
        }
    }

    /**
     * Checks if the harp view is active and initialized.
     */
    private boolean isHarpViewActiveAndInitialized() {
        LoggingContext.setComponent("HarpController");

        boolean isActive = window.isHarpViewActive();
        boolean isInitialized = (this.noteContainers != null);

        LoggingUtils.logDebug("Checking if harp view is active and initialized");
        LoggingUtils.logDebug("Harp view active: " + isActive);
        LoggingUtils.logDebug("Note containers initialized: " + isInitialized);

        if (isActive && isInitialized) {
            LoggingUtils.logDebug("Harp view is active and initialized");
            return true;
        } else {
            LoggingUtils.logDebug("Harp view is not active or not initialized. Active: " + isActive + ", Initialized: " + isInitialized);
            return false;
        }
    }
}
