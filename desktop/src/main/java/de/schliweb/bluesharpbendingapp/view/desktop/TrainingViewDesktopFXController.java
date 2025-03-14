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

import de.schliweb.bluesharpbendingapp.controller.TrainingContainer;
import de.schliweb.bluesharpbendingapp.controller.TrainingViewHandler;
import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;
import de.schliweb.bluesharpbendingapp.view.TrainingView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * The TrainingViewDesktopFXController class is a JavaFX controller that implements
 * the TrainingView interface to interact with the user interface components of the
 * training view for a desktop application. It manages the bindings and updates
 * of UI elements based on training interaction and handles user actions relating
 * to training sessions.
 */
@Slf4j
public class TrainingViewDesktopFXController implements TrainingView {

    /**
     * Represents a visual pane component in the TrainingViewDesktopFXController class.
     * This pane is designed to display notes or content related to the training view.
     * It serves as part of the user interface for managing and presenting training-related data.
     * This component is linked through JavaFX's FXML framework.
     */
    @FXML
    public Pane trainingNote;

    /**
     * A ComboBox UI element that displays available training options
     * in the desktop version of the training view.
     * <p>
     * This ComboBox is dynamically populated with a list of training titles
     * and allows the user to select a specific training. It is integrated
     * with the training view controller and reacts to user interactions
     * or programmatic updates.
     * <p>
     * The ComboBox is managed and initialized by the TrainingViewDesktopFXController,
     * which handles its setup, selection changes, and interaction events.
     */
    @FXML
    private ComboBox<String> comboTrainings;

    /**
     * Represents a combo box UI element for selecting the precision level in the training view.
     * Used in the TrainingViewDesktopFXController to allow users to choose from a predefined
     * list of precision options.
     * <p>
     * This component is initialized and populated with available precision values during
     * the setup of the training view. The selected value in this combo box can influence
     * the behavior of the training process based on user preferences.
     */
    @FXML
    private ComboBox<String> comboPrecision;

    /**
     * The {@code progressBar} represents a visual JavaFX component used to indicate
     * the progress of an ongoing task or operation within the TrainingViewDesktopFXController class.
     * <p>
     * This ProgressBar is associated with the FXML layout and is managed within the JavaFX application.
     * It provides real-time feedback to the user regarding the progress status,
     * such as training execution or process completion.
     * <p>
     * The component's state can be dynamically updated during runtime to reflect
     * the completion percentage of the current task.
     */
    @Getter
    @FXML
    private ProgressBar progressBar;

    /**
     * Represents the "Start" button in the application's UI.
     * The button is responsible for initiating a specific action
     * or process when triggered by the user.
     * <p>
     * This button is annotated with @FXML, indicating it is linked
     * to a corresponding element in an FXML file. The associated
     * event handler for this button should handle the start process
     * logic.
     */
    @FXML
    private Button startButton;

    /**
     * The `stopButton` is an FXML-injected `Button` UI element.
     * <p>
     * This button is used to stop the ongoing training process managed
     * by the `TrainingViewDesktopFXController`. The specific functionality
     * of this button is implemented within the corresponding handler method.
     */
    @FXML
    private Button stopButton;

    /**
     * Represents a handler for managing the training view and associated actions within the
     * TrainingViewDesktopFXController.
     * <p>
     * This field is responsible for delegating tasks such as initializing data, handling
     * user interactions, and managing the state of the training view. The associated
     * TrainingViewHandler provides the logic for these operations, ensuring that the view
     * remains synchronized with the application's state and behavior.
     * <p>
     * The TrainingViewHandler instance is expected to implement the logic for interacting
     * with the training and precision lists, starting and stopping training sessions, and
     * initializing related components or containers.
     */
    @Setter
    private TrainingViewHandler trainingViewHandler;

    /**
     * Initializes the FXML controller and sets up the initial state of the training view.
     * <p>
     * This method is automatically called after the FXML file has been loaded.
     * It configures the necessary listeners for the ComboBox elements used in the view.
     * The listeners monitor changes in the values of the ComboBox controls and handle
     * corresponding selection modifications.
     * <p>
     * Specifically:
     * - Adds a change listener to `comboTrainings` to handle training selection changes.
     * - Adds a change listener to `comboPrecision` to handle precision selection changes.
     * <p>
     * These listeners are utilized to trigger relevant functionality through
     * the `TrainingViewHandler`.
     */
    @FXML
    public void initialize() {
        addChangeListenerToComboBox(comboTrainings);
        addChangeListenerToComboBox(comboPrecision);
    }

    /**
     * Adds a change listener to the specified ComboBox to handle value selection changes.
     * <p>
     * This method ensures that when a value in the ComboBox changes, the appropriate actions
     * are performed based on the selected indexes in other ComboBox controls
     * (e.g., `comboTrainings` and `comboPrecision`). The method triggers associated handlers
     * for training and precision selection if a valid index is selected.
     *
     * @param combo the ComboBox to which the change listener is added
     */
    private void addChangeListenerToComboBox(ComboBox<String> combo) {
        combo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (comboTrainings.getSelectionModel().getSelectedIndex() >= 0) {
                    trainingViewHandler.handleTrainingSelection(comboTrainings.getSelectionModel().getSelectedIndex());
                }
                if (comboPrecision.getSelectionModel().getSelectedIndex() >= 0) {
                    trainingViewHandler.handlePrecisionSelection(comboPrecision.getSelectionModel().getSelectedIndex());
                }
            }
        });
    }

    /**
     * Handles the click event for the start button in the training view.
     * <p>
     * When triggered, this method initiates the training process via the `trainingViewHandler`.
     * It also updates the UI state by enabling the stop button and disabling the start button.
     * This ensures that the start button cannot be clicked again until the current training
     * session is complete or stopped.
     * <p>
     * Preconditions:
     * - The `trainingViewHandler` must not be null. If it is null, the method does nothing.
     * <p>
     * Postconditions:
     * - The start of the training session is handled by the `trainingViewHandler`.
     * - The stop button is enabled, allowing users to stop the training.
     * - The start button is disabled to prevent redundant actions.
     */
    @FXML
    private void handleStartButton() {
        if (trainingViewHandler != null) {
            trainingViewHandler.handleTrainingStart();
            stopButton.setDisable(false);
            startButton.setDisable(true);
        }
    }

    /**
     * Handles the click event for the stop button in the training view.
     * <p>
     * This method stops the ongoing training process by invoking the `handleTrainingStop` method
     * from the `trainingViewHandler` instance. It also updates the UI components,
     * disabling the stop button and enabling the start button to reflect the state
     * where training is no longer active.
     * <p>
     * Preconditions:
     * - `trainingViewHandler` must not be null. If it is null, the method will not
     *   execute any functionality.
     * <p>
     * Postconditions:
     * - The training process is stopped.
     * - The stop button is disabled.
     * - The start button is enabled.
     */
    @FXML
    private void handleStopButton() {
        if (trainingViewHandler != null) {
            trainingViewHandler.handleTrainingStop();
            stopButton.setDisable(true);
            startButton.setDisable(false);
        }
    }

    @Override
    public void setTrainings(String[] trainings) {
        initComboBox(comboTrainings, trainings);
    }

    @Override
    public void setPrecisions(String[] precisions) {
        initComboBox(comboPrecision, precisions);
    }

    @Override
    public void setSelectedTraining(int selectedTrainingIndex) {
        setSelected(selectedTrainingIndex, comboTrainings);
    }

    @Override
    public void setSelectedPrecision(int selectedPrecisionIndex) {
        setSelected(selectedPrecisionIndex, comboPrecision);
    }

    @Override
    public HarpViewNoteElement getActualHarpViewElement() {
        return HarpViewNoteElementDesktopFX.getInstance(trainingNote);
    }

    @Override
    public void initTrainingContainer(TrainingContainer trainingContainer) {
        Platform.runLater(() -> {
            HarpViewNoteElementDesktopFX actualNoteElementDesktop = HarpViewNoteElementDesktopFX.getInstance(trainingNote);
            actualNoteElementDesktop.setNoteName(trainingContainer.getActualNoteName() != null ?
                    trainingContainer.getActualNoteName() : "");
            int progress = trainingContainer.getProgress();
            double normalizedProgress = progress / 100.0;
            progressBar.setProgress(normalizedProgress);
            actualNoteElementDesktop.clear();
        });
    }

    @Override
    public void toggleButton() {
        startButton.setDisable(false);
        stopButton.setDisable(true);
    }

    /**
     * Initializes the specified ComboBox with the provided list of items.
     * <p>
     * This method clears any existing items in the ComboBox and populates it
     * with the items from the provided array. If the ComboBox is null, an error
     * is logged, and the method exits without performing any operations.
     *
     * @param combo the ComboBox to initialize; must not be null
     * @param items the array of items to populate in the ComboBox
     */
    private void initComboBox(ComboBox<String> combo, String[] items) {
        // Check if the ComboBox is null
        if (combo == null) {
            log.error("ComboBox is not initialized in initializeComboBox()!");
            return; // Exit the method if the ComboBox is null
        }

        // Clear any existing items from the ComboBox
        combo.getItems().clear();

        // Add all specified items from the provided array to the ComboBox
        combo.getItems().addAll(items);
    }

    /**
     * Selects an item in the specified ComboBox based on the provided index.
     * <p>
     * This method validates the ComboBox and the given index before making any selection.
     * If the ComboBox is null, an error message is logged and the method exits.
     * Similarly, if the specified index is out of bounds, an error message is logged,
     * and no selection is made.
     *
     * @param selectedIndex the index of the item to select in the ComboBox
     * @param combo the ComboBox in which the selection should be made
     */
    private void setSelected(int selectedIndex, ComboBox<String> combo) {
        // Check if the ComboBox is null
        if (combo == null) {
            log.error("ComboBox is not initialized in setSelected()!");
            return; // Exit the method if the ComboBox is null
        }

        // Validate whether the selected index is within the valid bounds of the ComboBox items
        if (selectedIndex < 0 || selectedIndex >= combo.getItems().size()) {
            log.error("Invalid index: {}", selectedIndex);
            return; // Exit the method if the index is out of range
        }

        // Select the item in the ComboBox corresponding to the provided index
        combo.getSelectionModel().select(selectedIndex);
    }

}



