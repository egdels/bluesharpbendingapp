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

import de.schliweb.bluesharpbendingapp.controller.*;
import de.schliweb.bluesharpbendingapp.view.*;

import javafx.fxml.FXML;

import javafx.scene.layout.VBox;
import lombok.Setter;

/**
 * The MainWindowDesktopFXController class serves as the controller for the main
 * application window in a JavaFX desktop application. It manages the initialization
 * and interaction with various views, such as harp view, settings view, about view,
 * and training view. Additionally, it provides functionality to set view handlers
 * and determine the active view within the application.
 * <p>
 * This class implements the MainWindow interface, ensuring compatibility with
 * the defined main window contract.
 */
public class MainWindowDesktopFXController implements MainWindow {


    /**
     * The container for the harp view in the application's user interface.
     * <p>
     * This VBox is used to display the harp view, which allows users to interact
     * with harp-related features, such as viewing and playing notes. The view is
     * dynamically initialized and inserted into this container during the application
     * setup process.
     * <p>
     * The controller manages the visibility and content of this container through
     * various methods, enabling seamless transitions between different sections
     * of the application.
     * <p>
     * This field is linked to the associated FXML file via the {@code @FXML} annotation,
     * allowing the JavaFX framework to inject the corresponding UI element.
     */
    @FXML
    private VBox harpViewContainer;

    /**
     * Represents the container for the settings view in the application's user interface.
     * <p>
     * This field is bound to the corresponding UI element specified in the FXML file
     * and serves as the parent layout node for the settings view. It is used to display
     * and manage various settings configurations, including but not limited to:
     * - Microphone options
     * - Harp settings
     * - Note settings
     * <p>
     * The container is controlled and manipulated by the methods in the associated
     * controller class, primarily for showing or hiding the settings view or initializing
     * its content with relevant data.
     */
    @FXML
    private VBox settingsViewContainer;

    /**
     * Represents the container for the "About" view in the application's user interface.
     * <p>
     * This field is managed by the JavaFX framework and corresponds to a VBox layout
     * defined in the FXML file. It serves as the visual container for displaying the
     * "About" section of the application.
     * <p>
     * The container is dynamically made visible or hidden depending on the current
     * user interaction or application state. It is primarily accessed and managed
     * through methods within the controller class, such as {@code showAboutView()}.
     * <p>
     * This field is initialized automatically when the associated FXML is loaded,
     * and it is configured to host the root node of the "About" view.
     */
    @FXML
    private VBox aboutViewContainer;

    /**
     * Represents the VBox container used to host the training view in the application's
     * user interface. This container serves as the layout component for displaying
     * and managing the training-related elements within the JavaFX application.
     * <p>
     * The {@code trainingViewContainer} is managed by the {@code MainWindowDesktopFXController} and is
     * part of the application's overall view setup. It plays a crucial role in transitioning to
     * the training view when user actions require displaying training-specific functionalities.
     * <p>
     * Specifically, this field:
     * - Is populated and initialized during the controller's view and container setup process.
     * - Becomes visible when the {@code showTrainingView()} method is invoked, allowing the user
     *   to interact with the training view.
     * - Is hidden as part of the {@code hideAllViews()} method to ensure no overlapping visibility
     *   with other view containers.
     */
    @FXML
    private VBox trainingViewContainer;

    /**
     * A private field in the MainWindowDesktopFXController class that holds an instance
     * of the HarpSettingsViewHandler interface. This handler is responsible for managing
     * and controlling the harp settings view within the application.
     * <p>
     * The HarpSettingsViewHandler interface provides methods for initializing key and
     * tuning options and handling user interactions such as selecting keys and tunings
     * in the harp settings view. This field enables the controller to delegate specific
     * harp settings-related operations to the corresponding handler implementation.
     */
    @Setter
    private HarpSettingsViewHandler harpSettingsViewHandler;

    /**
     * The handler associated with managing microphone settings in the application.
     * This variable is responsible for coordinating interactions and initializing
     * data related to microphone selection, algorithm configuration, and confidence levels.
     * <p>
     * The `MicrophoneSettingsViewHandler` plays a crucial role in enabling the user
     * to configure microphone-related options via the settings view.
     * <p>
     * Responsibilities include:
     * - Handling algorithm and microphone selection.
     * - Initializing lists for available microphone devices, algorithms, and confidence levels.
     * - Providing interaction logic for user-selected configurations.
     * <p>
     * It is typically invoked during initialization and when the settings
     * view is displayed to ensure the user has access to the relevant configuration options.
     */
    @Setter
    private MicrophoneSettingsViewHandler microphoneSettingsViewHandler;

    /**
     * A setter method for the NoteSettingsViewHandler instance.
     * <p>
     * The `noteSettingsViewHandler` is used to manage functionalities related to note settings,
     * specifically for initializing and handling adjustments of concert pitch within the application.
     * It plays a critical role in ensuring that the note settings view operates correctly by
     * delegating tasks such as updating the UI or managing the application's state related to
     * note settings.
     * <p>
     * This field is typically utilized during the application's setup or configuration phase
     * to associate the appropriate implementation of the NoteSettingsViewHandler interface
     * with the main application controller.
     */
    @Setter
    private NoteSettingsViewHandler noteSettingsViewHandler;

    /**
     * Manages the training view of the application by handling its initialization
     * and associated tasks, such as setting up the training list and configuring
     * the training container.
     * <p>
     * This field serves as the primary handler for the training view's functionality,
     * enabling interaction and coordination between the user interface and the
     * application's logic specific to training operations.
     * <p>
     * The {@code TrainingViewHandler} is used in methods like {@code showTrainingView()}
     * to initialize and prepare the training view when it is displayed.
     * <p>
     * It may handle tasks such as:
     * - Initializing the training list.
     * - Setting up the precision settings.
     * - Managing the layout container for the training view.
     */
    @Setter
    private TrainingViewHandler trainingViewHandler;

    /**
     * Represents the handler responsible for managing and interacting with
     * the visual and functional components of the harp view in the application.
     * <p>
     * This variable holds an instance of {@code HarpViewHandler}, providing
     * methods to initialize, configure, or update the harp view. It is utilized
     * in the application to handle harp-specific operations, such as initializing
     * the notes displayed within the harp view or responding to user interactions.
     * <p>
     * The {@code harpViewHandler} is critical for ensuring that the harp view
     * operates correctly and interacts seamlessly with other parts of the
     * application, such as rendering note data or supporting user interface transitions.
     */
    @Setter
    private HarpViewHandler harpViewHandler;

    /**
     * Represents the primary graphical user interface (GUI) component for displaying
     * the harp view in the application.
     * <p>
     * This variable is an instance of {@code HarpViewDesktopFX} and is used to manage
     * and control the harp-specific UI elements, such as displaying notes and handling
     * interactions related to the virtual harp. It is initialized during the
     * {@code initializeViews()} method and added to the appropriate layout container
     * in {@code setupContainers()}.
     * <p>
     * The harp view is shown to the user through the {@code showHarpView()} method, where
     * its associated container is made visible, and necessary data such as notes are
     * initialized. It interacts with the view handler {@code harpViewHandler} for handling
     * specific actions and updates.
     */
    private HarpViewDesktopFX harpView;

    /**
     * Represents the settings view instance for the desktop application.
     * <p>
     * This variable serves as a reference to the {@code SettingsViewDesktopFX} object,
     * which encapsulates the user interface and controller logic for the settings section
     * of the application. It is initialized during the application startup and used to
     * manage and display settings-related functionality.
     * <p>
     * The settings view is associated with an FXML layout file and a corresponding controller
     * that handles user interactions and populates the necessary settings data.
     */
    private SettingsViewDesktopFX settingsView;

    /**
     * Represents the "About" view of the application within the main controller.
     * <p>
     * This field is an instance of the {@code AboutViewDesktopFX} class, which is responsible
     * for managing the user interface and behavior of the "About" section. It provides
     * information about the application, such as its purpose, features, and possibly author or version details.
     * <p>
     * The {@code aboutView} field is initialized within the {@code initializeViews()} method,
     * where it is created and prepared for display. The view is shown when the {@code showAboutView()}
     * method is invoked, typically in response to a user action within the application's interface.
     * <p>
     * The associated container for this view is the {@code aboutViewContainer}, which is managed
     * by the controller to control the visibility of the "About" section and ensure a smooth
     * transition between different views in the application.
     */
    private AboutViewDesktopFX aboutView;

    /**
     * Represents the training view in the application's user interface.
     * <p>
     * This variable holds an instance of the {@code TrainingViewDesktopFX} class,
     * responsible for managing and displaying the training view's graphical components
     * and logic. It integrates the user interface defined in the associated FXML file
     * and provides access to the view's root node and controller.
     * <p>
     * The training view is one of several views managed by the controller, and its lifecycle
     * and visibility are controlled via methods such as {@code showTrainingView()}
     * and {@code hideAllViews()} in the {@code MainWindowDesktopFXController}.
     */
    private TrainingViewDesktopFX trainingView;


    /**
     * Initializes the main controller by preparing the views and their respective containers.
     * <p>
     * This method is automatically invoked by the JavaFX framework when the associated FXML is loaded.
     * It performs the following tasks:
     * 1. Calls {@code initializeViews()} to create and initialize all the views used in the application.
     * 2. Calls {@code setupContainers()} to configure the layout containers to host the views.
     */
    @FXML
    public void initialize() {
        initializeViews();
        setupContainers();
    }

    /**
     * Displays the harp view by making its container visible and initializing its note data.
     * <p>
     * This method is part of the JavaFX controller and is triggered to show the harp view
     * when a specific user action occurs. It performs the following steps:
     * 1. Calls {@code hideAllViews()} to ensure that all other view containers are hidden.
     * 2. Sets the visibility of the {@code harpViewContainer} to {@code true}, making the harp view visible to the user.
     * 3. Invokes {@code harpViewHandler.initNotes()} to initialize the notes displayed within the harp view.
     */
    @FXML
    private void showHarpView() {
        hideAllViews();
        harpViewContainer.setVisible(true);
        harpViewHandler.initNotes();
    }

    /**
     * Displays the "About" view by making its container visible.
     * <p>
     * This method is part of the JavaFX controller and is used to transition
     * the user interface to the "About" section of the application. It performs the following steps:
     * 1. Invokes {@code hideAllViews()} to ensure all other view containers are hidden.
     * 2. Sets the visibility of {@code aboutViewContainer} to {@code true},
     *    making the "About" view visible to the user.
     */
    @FXML
    public void showAboutView() {
        hideAllViews();
        aboutViewContainer.setVisible(true);
    }

    /**
     * Displays the settings view by making its container visible and initializing
     * various settings lists.
     * <p> <p>
     * This method is part of the JavaFX controller and is invoked to transition the
     * user interface to the settings section of the application. It performs the
     * following operations:
     * 1. Invokes {@code hideAllViews()} to ensure all other view containers are hidden.
     * 2. Sets the visibility of the {@code settingsViewContainer} to {@code true}.
     * 3. Calls initialization methods from the respective handlers to populate
     *    the required settings data, including:
     *    - Microphone options (microphone list, algorithm list, confidence levels)
     *    - Harp settings (key list, tuning options)
     *    - Note settings (concert pitch options)
     */
    @FXML
    public void showSettingsView() {
        hideAllViews();
        settingsViewContainer.setVisible(true);
        microphoneSettingsViewHandler.initMicrophoneList();
        microphoneSettingsViewHandler.initAlgorithmList();
        microphoneSettingsViewHandler.initConfidenceList();
        harpSettingsViewHandler.initKeyList();
        harpSettingsViewHandler.initTuneList();
        noteSettingsViewHandler.initConcertPitchList();
    }

    /**
     * Displays the training view by making its container visible and initializing
     * the associated elements.
     * <p>
     * This method is responsible for transitioning the user interface to the
     * training view of the application. It performs the following actions:
     * 1. Calls {@code hideAllViews()} to ensure that all other view containers are hidden.
     * 2. Sets the visibility of the {@code trainingViewContainer} to {@code true}.
     * 3. Invokes initialization methods from the {@code trainingViewHandler} to prepare
     *    the necessary data and configurations for the training view, including:
     *    - Initializing the training list via {@code trainingViewHandler.initTrainingList()}.
     *    - Setting up the training container via {@code trainingViewHandler.initTrainingContainer()}.
     *    - Initializing the precision list via {@code trainingViewHandler.initPrecisionList()}.
     */
    @FXML
    private void showTrainingView() {
        hideAllViews();
        trainingViewContainer.setVisible(true);
        trainingViewHandler.initTrainingList();
        trainingViewHandler.initTrainingContainer();
        trainingViewHandler.initPrecisionList();
    }

    /**
     * Hides all view containers in the application by setting their visibility to {@code false}.
     * <p>
     * This method ensures that no view container is visible by:
     * - Hiding the harp view container.
     * - Hiding the training view container.
     * - Hiding the settings view container.
     * - Hiding the "About" view container.
     * <p>
     * It is typically called before displaying a specific view to ensure that only
     * the desired view is visible to the user.
     */
    private void hideAllViews() {
        harpViewContainer.setVisible(false);
        trainingViewContainer.setVisible(false);
        settingsViewContainer.setVisible(false);
        aboutViewContainer.setVisible(false);
    }

    /**
     * Initializes the view components for the main application controller.
     * <p>
     * This method is responsible for creating instances of the various view objects required
     * by the application. Each view corresponds to a distinct part of the application's
     * user interface and is initialized here to make them available for further configuration
     * or interaction during runtime.
     * <p>
     * Specifically, this method performs the following actions:
     * 1. Creates an instance of the harp view using {@code HarpViewDesktopFX}.
     * 2. Creates an instance of the settings view using {@code SettingsViewDesktopFX}.
     * 3. Creates an instance of the "About" view using {@code AboutViewDesktopFX}.
     * 4. Creates an instance of the training view using {@code TrainingViewDesktopFX}.
     * <p>
     * This method is invoked as part of the application's initialization process
     * to ensure that all views are constructed and ready for use.
     */
    private void initializeViews() {
        harpView = new HarpViewDesktopFX();
        settingsView = new SettingsViewDesktopFX();
        aboutView = new AboutViewDesktopFX();
        trainingView = new TrainingViewDesktopFX();
    }

    /**
     * Configures the layout containers for the application by adding the root Node
     * of each corresponding view to its respective container.
     * <p>
     * This method is responsible for setting up the various view containers with
     * their associated UI components, ensuring that each view is correctly embedded
     * within the graphical user interface of the application. Specifically, it performs
     * the following steps:
     * <p>
     * 1. Adds the root Node of the harp view to the harp view container.
     * 2. Adds the root Node of the settings view to the settings view container.
     * 3. Adds the root Node of the "About" view to the "About" view container.
     * 4. Adds the root Node of the training view to the training view container.
     */
    private void setupContainers() {
        harpViewContainer.getChildren().add(harpView.getRoot());
        settingsViewContainer.getChildren().add(settingsView.getRoot());
        aboutViewContainer.getChildren().add(aboutView.getRoot());
        trainingViewContainer.getChildren().add(trainingView.getRoot());
    }

    @Override
    public HarpSettingsView getHarpSettingsView() {
        return settingsView.getController();
    }


    @Override
    public HarpView getHarpView() {
        return harpView.getController();
    }

    @Override
    public MicrophoneSettingsView getMicrophoneSettingsView() {
        return settingsView.getController();
    }

    @Override
    public boolean isHarpSettingsViewActive() {
        return settingsViewContainer.isVisible();
    }

    @Override
    public boolean isHarpViewActive() {
        return harpViewContainer.isVisible();
    }

    @Override
    public boolean isMicrophoneSettingsViewActive() {
        return settingsViewContainer.isVisible();
    }

    @Override
    public boolean isTrainingViewActive() {
        return trainingViewContainer.isVisible();
    }

    @Override
    public void open() {
        settingsView.getController().setHarpSettingsViewHandler(harpSettingsViewHandler);
        settingsView.getController().setMicrophoneSettingsViewHandler(microphoneSettingsViewHandler);
        settingsView.getController().setNoteSettingsViewHandler(noteSettingsViewHandler);
        trainingView.getController().setTrainingViewHandler(trainingViewHandler);
        showHarpView();
    }


    @Override
    public boolean isNoteSettingsViewActive() {
        return settingsViewContainer.isVisible();
    }

    @Override
    public NoteSettingsView getNoteSettingsView() {
        return settingsView.getController();
    }

    @Override
    public TrainingView getTrainingView() {
        return trainingView.getController();
    }

}

