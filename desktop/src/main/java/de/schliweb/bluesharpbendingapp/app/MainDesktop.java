package de.schliweb.bluesharpbendingapp.app;
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

import de.schliweb.bluesharpbendingapp.controller.MainController;
import de.schliweb.bluesharpbendingapp.model.ModelStorageService;
import de.schliweb.bluesharpbendingapp.model.VersionService;
import de.schliweb.bluesharpbendingapp.model.microphone.desktop.MicrophoneDesktop;
import de.schliweb.bluesharpbendingapp.view.desktop.MainWindowDesktopFXController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.FileSystems;
import java.util.Objects;


/**
 * The MainDesktop class serves as the entry point for the desktop application.
 * It extends the {@code Application} class from JavaFX to manage the lifecycle
 * of the application and initialize the main user interface, controllers, and services.
 * <p>
 * Key responsibilities:
 * - Launching the application and initializing required resources.
 * - Setting up the primary stage with the main window layout and styles.
 * - Managing the application lifecycle, including start and stop processes.
 * <p>
 * This class initializes:
 * - JavaFX user interface from an FXML file.
 * - The main controller responsible for application workflow and interaction with models.
 * - Temporary directory and file to store application data during runtime.
 * <p>
 * Methods:
 * - {@code main(String[] args)}: Entry point to launch the application.
 * - {@code start(Stage primaryStage)}: Called during application startup to set up the GUI and initialize required components.
 * - {@code stop()}: Called during application termination to perform cleanup operations and properly shut down resources.
 */
@Slf4j
public class MainDesktop extends Application {

    /**
     * Represents the name of the temporary file used by the application.
     * This file is utilized to store intermediate or transient data that
     * may be required during the runtime of the application.
     * <p>
     * The file is located in the application's working directory or a
     * predefined directory, as needed, and is named "Model.tmp".
     */
    private static final String TEMP_FILE = "Model.tmp";

    /**
     * Specifies the path for the temporary directory used by the application.
     * The directory is located in the user's home directory and named "BluesHarpBendingApp.tmp".
     * The path includes the system-specific file separator.
     * <p>
     * This temporary directory is intended to store application-specific temporary files
     * and is created if it does not already exist.
     * <p>
     * The value is dynamically constructed using the "user.home" system property
     * and the default file system separator.
     * <p>
     * Example usage includes storing logs or temporary files for the application in
     * a consistent and centralized location.
     */
    private static final String TEMP_DIR = System.getProperty("user.home") + FileSystems.getDefault().getSeparator() + "BluesHarpBendingApp.tmp" + FileSystems.getDefault().getSeparator();

    /**
     * A private instance of `MainController` used as the primary
     * controller within the application. This variable serves as
     * the main interface for coordinating and managing application-level
     * logic or interactions between various components. Typically,
     * it may handle tasks such as navigating between views, processing
     * user actions, and updating the user interface based on state changes.
     */
    private MainController controller;

    /**
     * Main entry point for the application. This method invokes the version retrieval logic
     * and launches the application.
     *
     * @param args command-line arguments passed to the application, typically used for
     *             configuration or initialization purposes.
     */
    public static void main(String[] args) {
        VersionService.getVersionFromHost();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file for the main window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main-window.fxml"));

            // Create the scene and apply the stylesheet
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/main-window.css")).toExternalForm());

            // Access the controller of the FXML file
            MainWindowDesktopFXController mainWindowDesktopFXController = loader.getController();

            // Initialize the main application controller with the view-controller and application model
            controller = new MainController(mainWindowDesktopFXController, new MicrophoneDesktop(), new ModelStorageService(TEMP_DIR, TEMP_FILE));
            controller.start();

            // Set the scene and show the stage
            primaryStage.setScene(scene);
            primaryStage.setTitle("Let's Bend - Desktop");
            primaryStage.show();
        } catch (Exception e) {
            // Log an error if something goes wrong during application startup
            log.error("Error while starting the application: {}", e.getMessage());
        }
    }

    @Override
    public void stop() {
        log.info("Shutting down");
        controller.stop();
        System.exit(0);
    }

}
