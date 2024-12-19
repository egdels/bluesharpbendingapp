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

/**
 * The type Main controller.
 */
public class MainController implements MicrophoneHandler, MicrophoneSettingsViewHandler, HarpSettingsViewHandler, HarpViewHandler, NoteSettingsViewHandler, TrainingViewHandler {

    /**
     * The constant CHANNEL_MAX.
     */
    private static final int CHANNEL_MAX = 10;

    /**
     * The constant CHANNEL_MIN.
     */
    private static final int CHANNEL_MIN = 1;

    /**
     * The Model.
     */
    private final MainModel model;

    /**
     * The Window.
     */
    private final MainWindow window;

    /**
     * The Notes.
     */
    private NoteContainer[] noteContainers;
    private TrainingContainer trainingContainer;

    /**
     * Instantiates a new Main controller.
     *
     * @param window the window
     * @param model  the model
     */
    public MainController(MainWindow window, MainModel model) {
        this.window = window;
        this.model = model;
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
        this.model.getMicrophone().setMicrophoneHandler(this);

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
    public void handle(double frequency, double volume, double probability) {
        updateMicrophoneSettingsViewVolume(volume);
        updateMicrophoneSettingsViewFrequency(frequency);
        updateMicrophoneSettingsViewProbability(probability);
        updateHarpView(frequency);
        updateTrainingView(frequency);
    }

    private void updateTrainingView(double frequency) {
        if (window.isTrainingViewActive() && this.trainingContainer != null) {
            trainingContainer.setFrequencyToHandle(frequency);
            (new Thread(trainingContainer)).start();
        }
    }

    /**
     * Update microphone settings view probability.
     *
     * @param probability the probability
     */
    private void updateMicrophoneSettingsViewProbability(double probability) {
        if (window.isMicrophoneSettingsViewActive()) {
            MicrophoneSettingsView microphoneSettingsView = window.getMicrophoneSettingsView();
            microphoneSettingsView.setProbability(probability);
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
     * Start.
     */
    public void start() {
        this.model.getMicrophone().open();
        this.window.open();
    }

    /**
     * Stop.
     */
    public void stop() {
        this.model.getMicrophone().close();
    }

    /**
     * Update harp view.
     *
     * @param frequency the frequency
     */
    private void updateHarpView(double frequency) {
        if (window.isHarpViewActive() && this.noteContainers != null) {
            for (NoteContainer noteContainer : noteContainers) {
                noteContainer.setFrequencyToHandle(frequency);
                (new Thread(noteContainer)).start();
            }
        }
    }

    /**
     * Update microphone settings view frequency.
     *
     * @param frequency the frequency
     */
    private void updateMicrophoneSettingsViewFrequency(double frequency) {
        if (window.isMicrophoneSettingsViewActive()) {
            MicrophoneSettingsView microphoneSettingsView = window.getMicrophoneSettingsView();
            microphoneSettingsView.setFrequency(frequency);
        }
    }

    /**
     * Update microphone settings view volume.
     *
     * @param volume the volume
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
