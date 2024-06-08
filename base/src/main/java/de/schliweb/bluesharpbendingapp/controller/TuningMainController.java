package de.schliweb.bluesharpbendingapp.controller;

import de.schliweb.bluesharpbendingapp.model.MainModel;
import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;
import de.schliweb.bluesharpbendingapp.model.microphone.Microphone;
import de.schliweb.bluesharpbendingapp.model.microphone.MicrophoneHandler;
import de.schliweb.bluesharpbendingapp.utils.Logger;
import de.schliweb.bluesharpbendingapp.utils.NoteUtils;
import de.schliweb.bluesharpbendingapp.view.MicrophoneSettingsView;
import de.schliweb.bluesharpbendingapp.view.NoteSettingsView;
import de.schliweb.bluesharpbendingapp.view.TuningMainWindow;
import de.schliweb.bluesharpbendingapp.view.TuneView;

import java.util.Map;

/**
 * The type Main controller.
 */
public class TuningMainController implements MicrophoneHandler, MicrophoneSettingsViewHandler, NoteSettingsViewHandler, TuneViewHandler {

    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = new Logger(de.schliweb.bluesharpbendingapp.controller.MainController.class);
    /**
     * The Window.
     */
    private TuningMainWindow window;
    /**
     * The Model.
     */
    private MainModel model;

    /**
     * Instantiates a new Main controller.
     *
     * @param tuningMainWindow the main window
     * @param mainModel  the main model
     */
    public TuningMainController(TuningMainWindow tuningMainWindow, MainModel mainModel) {
        this.window = tuningMainWindow;
        this.model = mainModel;
        NoteLookup.setConcertPitchByIndex(model.getStoredConcertPitchIndex());
        Microphone microphone = model.getMicrophone();
        microphone.setAlgorithm(model.getStoredAlgorithmIndex());
        microphone.setName(model.getStoredMicrophoneIndex());
        this.model.setMicrophone(microphone);
        this.window.setMicrophoneSettingsViewHandler(this);
        this.window.setTuneViewHandler(this);
        this.window.setNoteSettingsViewHandler(this);
        this.model.getMicrophone().setMicrophoneHandler(this);
        LOGGER.info("Created");
    }

    /**
     * Start.
     */
    public void start() {
        LOGGER.info("Enter");
        this.model.getMicrophone().open();
        this.window.open();
        this.model.getMicrophone().close();
        LOGGER.info("Leave");
    }


    @Override
    public void handleAlgorithmSelection(int algorithmIndex) {
        LOGGER.info("Enter parameter " + algorithmIndex);
        this.model.setStoredAlgorithmIndex(algorithmIndex);
        Microphone microphone = model.getMicrophone();
        microphone.close();
        microphone.setAlgorithm(algorithmIndex);
        microphone.open();
        LOGGER.info("Leave");
    }

    @Override
    public void handleMicrophoneSelection(int microphoneIndex) {
        LOGGER.info("Enter with parameter " + microphoneIndex);
        this.model.setStoredMicrophoneIndex(microphoneIndex);
        Microphone microphone = model.getMicrophone();
        microphone.close();
        microphone.setName(microphoneIndex);
        microphone.open();
        LOGGER.info("Leave");
    }

    @Override
    public void initAlgorithmList() {
        LOGGER.info("Enter");
        if (window.isMicrophoneSettingsViewActive()) {
            MicrophoneSettingsView microphoneSettingsView = window.getMicrophoneSettingsView();
            microphoneSettingsView.setAlgorithms(model.getAlgorithms());
            microphoneSettingsView.setSelectedAlgorithm(model.getSelectedAlgorithmIndex());
        }
        LOGGER.info("Leave");
    }

    @Override
    public void initMicrophoneList() {
        LOGGER.info("Enter");
        if (window.isMicrophoneSettingsViewActive()) {
            MicrophoneSettingsView microphoneSettingsView = window.getMicrophoneSettingsView();
            microphoneSettingsView.setMicrophones(model.getMicrophones());
            microphoneSettingsView.setSelectedMicrophone(model.getSelectedMicrophoneIndex());
        }
        LOGGER.info("Leave");
    }

    @Override
    public void handleConcertPitchSelection(int pitchIndex) {
        LOGGER.info("Enter parameter " + pitchIndex);
        this.model.setStoredConcertPitchIndex(pitchIndex);
        NoteLookup.setConcertPitchByIndex(pitchIndex);
        model.setHarmonica(AbstractHarmonica.create(model.getStoredKeyIndex(), model.getStoredTuneIndex()));
        LOGGER.info("Leave");
    }

    @Override
    public void initConcertPitchList() {
        LOGGER.info("Enter");
        if (window.isNoteSettingsViewActive()) {
            NoteSettingsView noteSettingsView = window.getNoteSettingsView();
            noteSettingsView.setConcertPitches(model.getConcertPitches());
            noteSettingsView.setSelectedConcertPitch(model.getSelectedConcertPitchIndex());
        }
        LOGGER.info("Leave");
    }

    @Override
    public void handle(double frequency, double volume, double probability) {
        updateMicrophoneSettingsViewVolume(volume);
        updateMicrophoneSettingsViewFrequency(frequency);
        updateMicrophoneSettingsViewProbability(probability);
        updateTuneView(frequency, volume, probability);
    }

    /**
     * Update tune view.
     *
     * @param frequency   the frequency
     * @param volume      the volume
     * @param probability the probability
     */
    private void updateTuneView(double frequency, double volume, double probability) {
        LOGGER.info("Enter with parameter " + frequency + " " + volume + " " + probability);
        if (window.isTuneViewActive()) {


            TuneView tuneView = window.getTuneView();
            Map.Entry<String, Double> note = NoteLookup.getNote(frequency);

            if (note != null && probability > 0.95)
                tuneView.update(note.getKey(), NoteUtils.getCents(frequency, note.getValue()));
            else
                tuneView.clear();
        }
    }

    /**
     * Update microphone settings view volume.
     *
     * @param volume the volume
     */
    private void updateMicrophoneSettingsViewVolume(double volume) {
        LOGGER.info("Enter with parameter " + volume);
        if (window.isMicrophoneSettingsViewActive()) {
            MicrophoneSettingsView microphoneSettingsView = window.getMicrophoneSettingsView();
            microphoneSettingsView.setVolume(volume);
        }
        LOGGER.info("Leave");
    }

    /**
     * Update microphone settings view probability.
     *
     * @param probability the probability
     */
    private void updateMicrophoneSettingsViewProbability(double probability) {
        LOGGER.info("Enter with parameter " + probability);
        if (window.isMicrophoneSettingsViewActive()) {
            MicrophoneSettingsView microphoneSettingsView = window.getMicrophoneSettingsView();
            microphoneSettingsView.setProbability(probability);
        }
        LOGGER.info("Leave");
    }

    /**
     * Update microphone settings view frequency.
     *
     * @param frequency the frequency
     */
    private void updateMicrophoneSettingsViewFrequency(double frequency) {
        LOGGER.info("Enter with parameter " + frequency);
        if (window.isMicrophoneSettingsViewActive()) {
            MicrophoneSettingsView microphoneSettingsView = window.getMicrophoneSettingsView();
            microphoneSettingsView.setFrequency(frequency);
        }
        LOGGER.info("Leave");
    }

    @Override
    public void initTuneView() {
        if (window.isTuneViewActive()) {
            TuneView tuneView = window.getTuneView();
            tuneView.init();
        }
    }
}
