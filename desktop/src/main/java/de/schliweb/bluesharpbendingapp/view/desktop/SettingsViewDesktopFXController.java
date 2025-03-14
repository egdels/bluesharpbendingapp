package de.schliweb.bluesharpbendingapp.view.desktop;
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

import de.schliweb.bluesharpbendingapp.controller.HarpSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.controller.MicrophoneSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.controller.NoteSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.model.MainModel;
import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;
import de.schliweb.bluesharpbendingapp.utils.NoteUtils;
import de.schliweb.bluesharpbendingapp.view.HarpSettingsView;
import de.schliweb.bluesharpbendingapp.view.MicrophoneSettingsView;
import de.schliweb.bluesharpbendingapp.view.NoteSettingsView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.function.IntConsumer;

/**
 * The SettingsViewDesktopFXController class is responsible for managing the JavaFX UI components
 * that are part of the settings view in a desktop application. It implements the HarpSettingsView,
 * MicrophoneSettingsView, and NoteSettingsView interfaces to manage harp, microphone, and note-related settings.
 */
@Slf4j
public class SettingsViewDesktopFXController implements HarpSettingsView, MicrophoneSettingsView, NoteSettingsView {

    /**
     * A ComboBox element in the user interface for selecting concert pitches.
     * It allows users to choose between available pitch settings, typically
     * representing reference tuning frequencies (e.g., 440 Hz for A4).
     * <p>
     * This ComboBox is part of the SettingsViewDesktopFXController class
     * and is initialized and managed through its associated methods. The options
     * and selected pitch index can be customized programmatically using the
     * relevant controller methods.
     */
    @FXML
    public ComboBox<String> comboConcertPitches;

    /**
     * Represents a ComboBox UI element used to display and select confidence levels
     * in the application's settings. The options and functionality of this ComboBox
     * are managed by methods within the `SettingsViewDesktopFXController` class.
     * <p>
     * This field is annotated with `@FXML` to indicate it is injected via the FXML file
     * defining the UI layout. Its exact behavior depends on how it is initialized and
     * interacted with within the application's lifecycle.
     */
    @FXML
    public ComboBox<String> comboConfidences;

    /**
     * Represents the label displaying the current value of the musical note being processed
     * or tuned in the settings view.
     * This label is dynamically updated to reflect the detected or selected note.
     */
    @FXML
    public Label valueNote;

    /**
     * Represents a tuning meter integrated into the application's user interface.
     * This variable is an instance of {@link TuningMeterFX}, which provides a graphical
     * display for visualizing the deviation in musical pitch.
     * <p>
     * The tuning meter is used as part of the settings interface to help users
     * adjust and monitor pitch accuracy. It visually displays the deviation in
     * cents, ranging from -50 to +50, with a needle indicating the current value.
     * The meter is designed to respond in real time to changes in pitch.
     * <p>
     * This variable is annotated with {@code @FXML}, indicating that it is
     * injected and controlled by the associated FXML file for the
     * {@code SettingsViewDesktopFXController}.
     */
    @FXML
    public TuningMeterFX tuningMeter;

    /**
     * Represents a ComboBox UI element for selecting algorithm options in the
     * SettingsViewDesktopFXController. This component is initialized and managed
     * to provide and display a list of available algorithms.
     * <p>
     * The selected algorithm can be dynamically set and updated through the
     * controller logic, and this ComboBox reacts to changes in the associated
     * data. It serves as an interactive field for users to choose their desired
     * algorithm option.
     */
    @FXML
    private ComboBox<String> comboAlgorithms;

    /**
     * ComboBox that allows users to select a microphone from a list of available microphone options.
     * This field is part of the SettingsViewDesktopFXController and is designed to facilitate interaction
     * with microphone settings in the application's GUI.
     * <p>
     * The available microphone options are typically provided dynamically, and the selection made
     * by the user will influence the application's behavior with respect to audio input.
     * <p>
     * This field is annotated with @FXML, indicating it is linked to the corresponding element in the
     * associated FXML layout file and is initialized by the JavaFX framework during the loading of the view.
     */
    @FXML
    private ComboBox<String> comboMicrophones;

    /**
     * Represents a JavaFX label in the user interface that displays the current frequency value.
     * It is part of the SettingsViewDesktopFXController and is used to reflect updates to the
     * frequency based on user interactions or real-time data changes in the application.
     */
    @FXML
    private Label valueFrequency;

    /**
     * A UI component represented by a {@link Label} that displays the current volume value.
     * This is a part of the {@code SettingsViewDesktopFXController} class and is linked using
     * JavaFX's {@code @FXML} annotation for interaction with the FXML-defined UI elements.
     */
    @FXML
    private Label valueVolume;

    /**
     * A ComboBox component used to display and manage a list of keys.
     * This component allows the user to select a specific key from a predefined list.
     * It is integrated into the JavaFX UI and is linked via the @FXML annotation for
     * interaction with the associated controller.
     */
    @Getter
    @FXML
    private ComboBox<String> comboKeys;

    /**
     * Represents a ComboBox component for selecting tunes in the user interface.
     * This ComboBox allows the user to choose from a list of predefined tunes.
     * It is used as part of the settings view in the application.
     */
    @Getter
    @FXML
    private ComboBox<String> comboTunes;

    /**
     * Represents a handler for managing harp settings in the view layer.
     * This variable is tied to an instance of {@link HarpSettingsViewHandler},
     * which provides functionality to handle key and tuning selections,
     * as well as initializing key and tuning options for user interaction.
     * <p>
     * In the context of the {@code SettingsViewDesktopFXController},
     * this handler is used to delegate operations related to harp settings,
     * ensuring separation of concerns and streamlined management of
     * application state related to these settings.
     */
    @Setter
    private HarpSettingsViewHandler harpSettingsViewHandler;

    /**
     * Manages and provides the functionality for handling microphone settings within the
     * application. This variable refers to the {@link MicrophoneSettingsViewHandler} interface
     * implementation that is responsible for initializing and managing the list of microphones,
     * algorithms, and confidence levels, as well as handling the respective selection events.
     * <p>
     * It allows integration and interaction with the user interface components to ensure proper
     * configuration of microphone-related settings in the application.
     */
    @Setter
    private MicrophoneSettingsViewHandler microphoneSettingsViewHandler;

    /**
     * The {@code noteSettingsViewHandler} variable represents an instance of a handler
     * for managing interactions and operations related to the note settings view in the application.
     * It is primarily responsible for facilitating functionalities such as setting and updating
     * available concert pitch settings, reacting to user inputs, and ensuring synchronization
     * between the user interface and the underlying note settings logic.
     * <p>
     * This variable is designed to interface with the {@link NoteSettingsViewHandler} implementation
     * and ensures proper encapsulation within the application's view controller. It aids in
     * decoupling logic specific to note settings management, making the codebase modular and maintainable.
     * <p>
     * It is expected to be set during the initialization or configuration of the
     * {@code SettingsViewDesktopFXController} to enable proper view handling.
     */
    @Setter
    private NoteSettingsViewHandler noteSettingsViewHandler;

    /**
     * Initializes the settings view, setting up necessary configurations
     * for the graphical user interface elements.
     * <p>
     * This method is annotated with {@code @FXML} to indicate its use in
     * JavaFX applications and is called automatically when the corresponding
     * FXML file is loaded.
     * <p>
     * The initialization process includes:
     * - Logging the start of the initialization procedure for debugging purposes.
     * - Adding change listeners to a predefined set of {@link ComboBox} components
     *   (e.g., {@code comboKeys}, {@code comboTunes}, {@code comboAlgorithms},
     *   {@code comboMicrophones}, {@code comboConfidences}, and
     *   {@code comboConcertPitches}) to respond to value changes.
     * <p>
     * The change listeners added to the combo boxes trigger appropriate handlers
     * by delegating to the corresponding view handler, enabling real-time updates
     * of settings within the application when user selections are made.
     */
    @FXML
    public void initialize() {
        log.info("initialize()");
        for (ComboBox<String> stringComboBox : Arrays.asList(comboKeys, comboTunes, comboAlgorithms, comboMicrophones, comboConfidences, comboConcertPitches)) {
            addChangeListenerToComboBox(stringComboBox);
        }
    }

    @Override
    public void setKeys(String[] keys) {
        initComboBox(comboKeys, keys);
    }

    @Override
    public void setSelectedKey(int selectedKeyIndex) {
        setSelected(selectedKeyIndex, comboKeys);
    }

    @Override
    public void setSelectedTune(int selectedTuneIndex) {
        setSelected(selectedTuneIndex, comboTunes);
    }

    /**
     * Sets the selected index for the specified ComboBox.
     * If the provided index is valid, the corresponding item in the ComboBox
     * is selected. If the ComboBox is null or the index is out of bounds,
     * the method logs an error and performs no further action.
     *
     * @param selectedIndex the index of the item to be selected in the ComboBox
     * @param combo the ComboBox in which to select the specified item
     */
    private void setSelected(int selectedIndex, ComboBox<String> combo) {
        // Check if the ComboBox is null
        if (combo == null) {
            log.error("ComboBox is not initialized in setSelected()!");
            return; // Exit the method as there's nothing to work with
        }

        // Validate the selected index is within valid bounds of the ComboBox items
        if (selectedIndex < 0 || selectedIndex >= combo.getItems().size()) {
            log.error("Invalid index: {} for ComboBox {} in setSelected()!", selectedIndex, combo.getId());
            return; // Exit the method as the index is out of range
        }

        // Select the item in the ComboBox based on the provided index
        combo.getSelectionModel().select(selectedIndex);
    }


    /**
     * Initializes the specified ComboBox with the provided items.
     * <p>
     * This method first validates the ComboBox to ensure it is not null.
     * If the ComboBox is null, an error is logged, and the method exits without performing any operations.
     * If the ComboBox is valid, it clears any existing items and adds all elements from the provided array to it.
     *
     * @param combo the ComboBox to be initialized
     * @param items an array of strings representing the items to be added to the ComboBox
     */
    private void initComboBox(ComboBox<String> combo, String[] items) {
        // Check if the ComboBox is null
        if (combo == null) {
            log.error("ComboBox is not initialized in initComboBox()!");
            return; // Exit the method as there's nothing to work with
        }

        // Clear all existing items from the ComboBox
        combo.getItems().clear();

        // Add all provided items to the ComboBox
        combo.getItems().addAll(items);
    }

    /**
     * Adds a change listener to the specified ComboBox, enabling real-time handling of
     * value changes within the application. The listener reacts to updates in the
     * ComboBox's selected value and delegates actions to the appropriate handlers based
     * on the selected indices of various application-specific ComboBox components
     * (e.g., keys, tunes, algorithms, microphones, confidences, and concert pitches).
     * <p>
     * The method logs any value changes in the ComboBox for debugging purposes. It also
     * ensures that relevant settings are updated by invoking corresponding handlers
     * when a new value is selected.
     *
     * @param combo the ComboBox to which the change listener will be added
     */
    private void addChangeListenerToComboBox(ComboBox<String> combo) {
        combo.valueProperty().addListener((observable, oldValue, newValue) -> {
            log.info("ComboBox ({}) changed from {} to {}", combo.getId(), oldValue, newValue);

            if (newValue != null) {
                handleSelectionChange(comboKeys, harpSettingsViewHandler::handleKeySelection);
                handleSelectionChange(comboTunes, harpSettingsViewHandler::handleTuneSelection);
                handleSelectionChange(comboAlgorithms, microphoneSettingsViewHandler::handleAlgorithmSelection);
                handleSelectionChange(comboMicrophones, microphoneSettingsViewHandler::handleMicrophoneSelection);
                handleSelectionChange(comboConfidences, microphoneSettingsViewHandler::handleConfidenceSelection);
                handleSelectionChange(comboConcertPitches, noteSettingsViewHandler::handleConcertPitchSelection);
            }
        });
    }

    /**
     * Handles a selection change event for the provided ComboBox and executes
     * the given handler with the index of the selected item.
     *
     * @param comboBox the ComboBox whose selection changes are to be handled
     * @param handler  the callback to handle the selected index; it is invoked
     *                 with the index of the selected item if the selection is valid
     */
    private void handleSelectionChange(ComboBox<String> comboBox, IntConsumer handler) {
        int selectedIndex = comboBox.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            handler.accept(selectedIndex);
        }
    }

    @Override
    public void setTunes(String[] tunes) {
        initComboBox(comboTunes, tunes);
    }


    @Override
    public void setConcertPitches(String[] pitches) {
        initComboBox(comboConcertPitches, pitches);
    }


    @Override
    public void setSelectedConcertPitch(int selectedConcertPitchIndex) {
        setSelected(selectedConcertPitchIndex, comboConcertPitches);
    }


    @Override
    public void setAlgorithms(String[] algorithms) {
        initComboBox(comboAlgorithms, algorithms);
    }

    @Override
    public void setFrequency(double frequency) {
        javafx.application.Platform.runLater(() -> valueFrequency.setText(String.format("%.2f", frequency)));
        if (frequency > 0) {
            String note = NoteLookup.getNoteName(frequency);
            if (note != null) {
                javafx.application.Platform.runLater(() -> valueNote.setText(note));
                double referenceFrequency = NoteLookup.getNoteFrequency(note);
                double cents = NoteUtils.getCents(frequency, referenceFrequency);
                tuningMeter.setCents(cents);
            }
        }
    }

    @Override
    public void setSelectedConfidence(int confidenceIndex) {
        setSelected(confidenceIndex, comboConfidences);
    }

    @Override
    public void setConfidences(String[] confidences) {
        initComboBox(comboConfidences, confidences);
    }

    @Override
    public void setMicrophones(String[] microphones) {
        initComboBox(comboMicrophones, microphones);
    }

    @Override
    public void setSelectedAlgorithm(int selectedAlgorithmIndex) {
        setSelected(selectedAlgorithmIndex, comboAlgorithms);
    }

    @Override
    public void setSelectedMicrophone(int selectedMicrophoneIndex) {
        setSelected(selectedMicrophoneIndex, comboMicrophones);
    }

    @Override
    public void setVolume(double volume) {
        Platform.runLater(() -> valueVolume.setText(String.format("%.2f", volume)));
    }

    /**
     * Resets the application settings in the view to their previously stored values.
     * <p>
     * When invoked, this method:
     * - Creates a new instance of {@code MainModel} to retrieve stored settings.
     * - Updates the selected indices for various settings components (concert pitch, key,
     *   algorithm, microphone, tune, and confidence) based on the stored values in the model.
     * <p>
     * The method modifies the state of the settings view by delegating to helper
     * methods for updating UI components to reflect the stored defaults.
     */
    public void handleResetButton() {
        MainModel model = new MainModel();
        setSelectedConcertPitch(model.getStoredConcertPitchIndex());
        setSelectedKey(model.getStoredKeyIndex());
        setSelectedAlgorithm(model.getStoredAlgorithmIndex());
        setSelectedMicrophone(model.getStoredMicrophoneIndex());
        setSelectedTune(model.getStoredTuneIndex());
        setSelectedConfidence(model.getStoredConfidenceIndex());
    }
}
