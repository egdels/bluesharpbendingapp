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
import de.schliweb.bluesharpbendingapp.model.harmonica.*;
import de.schliweb.bluesharpbendingapp.service.ModelStorageService;
import de.schliweb.bluesharpbendingapp.utils.ChordDetectionResult;
import de.schliweb.bluesharpbendingapp.utils.LoggingContext;
import de.schliweb.bluesharpbendingapp.utils.LoggingUtils;
import de.schliweb.bluesharpbendingapp.utils.PitchDetector;
import de.schliweb.bluesharpbendingapp.view.HarpSettingsView;
import de.schliweb.bluesharpbendingapp.view.HarpView;
import de.schliweb.bluesharpbendingapp.view.MainWindow;
import de.schliweb.bluesharpbendingapp.view.NoteSettingsView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * The HarpController class is responsible for managing and coordinating
 * the logic of the harp application. It integrates multiple functionalities,
 * including updating the harp view, handling note-related settings, and
 * managing frequency and chord detection. This class interacts with
 * the application state, model storage services, and user interface modules
 * to ensure the seamless operation of the harp application.
 * <p>
 * It extends functionality from the following handler interfaces:
 * - HarpSettingsViewHandler: for managing key and tuning settings.
 * - HarpViewHandler: for managing notes in the harp view.
 * - NoteSettingsViewHandler: for handling concert pitch-related settings.
 * - HarpFrequencyHandler: for updating frequency and chord-related display.
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
    private final ChordAndDetectionResultComparator chordComparator = new ChordAndDetectionResultComparator();
    private MicrophoneController microphoneController;
    private List<ChordHarmonica> possibleChords;
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
        this.possibleChords = harmonica.getPossibleChords();
        PitchDetector.setMinFrequency(harmonica.getHarmonicaMinFrequency());
        PitchDetector.setMaxFrequency(harmonica.getHarmonicaMaxFrequency());
        LoggingUtils.logInitialized("HarpController");
    }

    /**
     * Sets the MicrophoneController instance to be used by this HarpController.
     * This method is used to resolve the circular dependency between HarpController and MicrophoneController.
     *
     * @param microphoneController The MicrophoneController instance to use
     */
    public void setMicrophoneController(MicrophoneController microphoneController) {
        LoggingContext.setComponent("HarpController");
        LoggingUtils.logDebug("Setting MicrophoneController instance");
        this.microphoneController = microphoneController;
    }

    /**
     * Updates the harp view with the provided frequency and chord detection results.
     * This method resets note container states and processes either chord highlighting or
     * single frequency updates based on the chord detection result, but not both simultaneously.
     * Chord detection can be enabled or disabled via the showChord setting.
     *
     * @param frequency   the frequency to update in the harp view in Hz
     * @param chordResult the result of chord detection containing detected pitches
     */
    public void updateHarpView(double frequency, ChordDetectionResult chordResult) {
        LoggingContext.setComponent("HarpController");
        LoggingUtils.logAudioProcessing("Updating harp view", "frequency=" + frequency);

        if (!executorService.isShutdown() && isHarpViewActiveAndInitialized()) {
            LoggingUtils.logDebug("Executor service is active, and harp view is initialized. Proceeding to update note containers");

            boolean chordDetected = false;

            // Check if we're in chord mode (showChordIndex = 1) or individual note mode (showChordIndex = 0)
            if (model.getSelectedShowChordIndex() == 1) {
                // Chord mode: Only process chords, no individual notes
                LoggingUtils.logDebug("Chord display mode is enabled. Processing chord detection.");
                chordDetected = processChords(frequency, chordResult);
            }

            if (!chordDetected) {
                // Individual note mode: Only process individual notes, no chords
                updateNoteContainersForNote(frequency);
            }

            for (NoteContainer noteContainer : noteContainers) {
                executorService.submit(noteContainer);
                LoggingUtils.logDebug("Submitting note container to executor service: " + noteContainer);
            }

            LoggingUtils.logDebug("Harp view update completed successfully");
        } else {
            LoggingUtils.logDebug("Harp view update skipped. Either executor service is shut down or harp view is not active/initialized");
        }
    }

    /**
     * Processes chord detection results for the provided frequency. This method
     * checks if there are detected pitches in the chord detection result. If so,
     * it evaluates possible chords and updates related note containers for matched chords.
     *
     * @param frequency   the frequency in Hz to process for chord detection
     * @param chordResult the result of chord detection containing detected pitches
     * @return true if a matching chord is detected and processed, false otherwise
     */
    private boolean processChords(double frequency, ChordDetectionResult chordResult) {
        if (!chordResult.hasPitches()) {
            return false;
        }

        boolean chordDetected = false;
        if (possibleChords == null) {
            possibleChords = harmonica.getPossibleChords();
        }
        List<ChordHarmonica> chords = possibleChords;

        for (ChordHarmonica chord : chords) {
            if (chordComparator.compare(chord, chordResult) == 0) {
                chordDetected = true;
                updateNoteContainersForChord(chord, frequency);
            }
        }

        return chordDetected;
    }

    /**
     * Updates the note containers with the specified chord and frequency.
     * If a note container is part of the provided chord, its state is updated
     * to mark it as part of the chord, and its frequency is set to the provided value.
     * A debug log is generated for each updated note container.
     *
     * @param chord     the chord used to determine which note containers are part of it
     * @param frequency the frequency to assign to the note containers that are part of the chord, in Hz
     */
    private void updateNoteContainersForChord(ChordHarmonica chord, double frequency) {
        for (NoteContainer noteContainer : noteContainers) {
            if (isPartOfChord(noteContainer, chord)) {
                noteContainer.setPartOfChord(true);
                noteContainer.setFrequencyToHandle(frequency);
                LoggingUtils.logDebug("Setting frequency " + frequency + " for chord note container: " + noteContainer);
            }
        }
    }

    /**
     * Updates all note containers with the specified frequency.
     * Each note container's frequency is set to the provided value,
     * and a debug log is generated for each updated note container.
     *
     * @param frequency the frequency value to assign to each note container, in Hz
     */
    private void updateNoteContainersForNote(double frequency) {
        for (NoteContainer noteContainer : noteContainers) {
            noteContainer.setFrequencyToHandle(frequency);
            LoggingUtils.logDebug("Setting frequency " + frequency + " for note container: " + noteContainer);
        }
    }

    /**
     * Determines whether the given NoteContainer is part of the specified ChordHarmonica.
     *
     * @param noteContainer the NoteContainer to check
     * @param chord         the ChordHarmonica to compare against
     * @return true if the NoteContainer's note and channel match the chord's properties, false otherwise
     */
    private boolean isPartOfChord(NoteContainer noteContainer, ChordHarmonica chord) {
        return noteContainer.getNote() == chord.getNoteIndex() && chord.getChannels().contains(noteContainer.getChannel());
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
        possibleChords = harmonica.getPossibleChords();
        PitchDetector.setMinFrequency(harmonica.getHarmonicaMinFrequency());
        PitchDetector.setMaxFrequency(harmonica.getHarmonicaMaxFrequency());
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
        possibleChords = harmonica.getPossibleChords();
        PitchDetector.setMinFrequency(harmonica.getHarmonicaMinFrequency());
        PitchDetector.setMaxFrequency(harmonica.getHarmonicaMaxFrequency());
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
    public void handleShowChordSelection(int showChordIndex) {
        LoggingContext.setComponent("HarpController");
        LoggingUtils.logUserAction("Show chord selection", "showChordIndex=" + showChordIndex);

        this.model.setSelectedShowChordIndex(showChordIndex);
        this.model.setStoredShowChordIndex(showChordIndex);

        // Update the microphone's chord detection setting immediately
        if (microphoneController != null) {
            LoggingUtils.logDebug("Updating microphone chord detection setting");
            microphoneController.updateChordDetectionSetting();
        } else {
            LoggingUtils.logWarning("Cannot update microphone chord detection setting", "MicrophoneController is null");
        }

        modelStorageService.storeModel(model);
        LoggingUtils.logOperationCompleted("Show chord selection handling");
    }

    @Override
    public void initShowChordSetting() {
        LoggingContext.setComponent("HarpController");
        LoggingUtils.logOperationStarted("Show chord setting initialization");

        if (window.isHarpSettingsViewActive()) {
            LoggingUtils.logDebug("Harp settings view is active. Proceeding to initialize the show chord setting");

            HarpSettingsView harpSettingsView = window.getHarpSettingsView();

            LoggingUtils.logDebug("Setting the show chord state in the harp settings view");
            harpSettingsView.setSelectedShowChord(model.getSelectedShowChordIndex());

            LoggingUtils.logOperationCompleted("Show chord setting initialization");
        } else {
            LoggingUtils.logWarning("Show chord setting initialization skipped", "Harp settings view is not active");
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

        // Update the tuning and key information display
        String keyName = this.harmonica.getKeyName();
        String tuningName = this.harmonica.getTuneName();
        LoggingUtils.logDebug("Updating tuning and key information: Key=" + keyName + ", Tuning=" + tuningName);
        harpView.updateTuningKeyInfo(keyName, tuningName);

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
        LoggingUtils.logDebug("Processing bending notes for channel " + channel + " with blow bending count: " + blowBendingCount + ", draw bending count: " + drawBendingCount);

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
            LoggingUtils.logDebug("Adding " + logMessage + " note for channel " + channel + " with frequency: " + frequency + " and note: " + note);
            notesList.add(new NoteContainer(channel, noteIndex, note, harmonica, harpView));
        } else {
            LoggingUtils.logWarning(logMessage + " note lookup failed", "Channel " + channel + ", frequency " + frequency);
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
        possibleChords = harmonica.getPossibleChords();
        PitchDetector.setMinFrequency(harmonica.getHarmonicaMinFrequency());
        PitchDetector.setMaxFrequency(harmonica.getHarmonicaMaxFrequency());
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
