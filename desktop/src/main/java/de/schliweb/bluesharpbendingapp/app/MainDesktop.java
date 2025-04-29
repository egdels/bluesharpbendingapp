package de.schliweb.bluesharpbendingapp.app;
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

import de.schliweb.bluesharpbendingapp.controller.MainController;
import de.schliweb.bluesharpbendingapp.model.VersionService;
import de.schliweb.bluesharpbendingapp.utils.LoggingContext;
import de.schliweb.bluesharpbendingapp.utils.LoggingUtils;
import de.schliweb.bluesharpbendingapp.view.desktop.MainWindowDesktopFXController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
public class MainDesktop extends Application {

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
        LoggingContext.setComponent("MainDesktop");
        LoggingUtils.logAppStartup("Desktop Application");
        VersionService.getVersionFromHost();
        launch(args);
    }

    /**
     * Initializes and starts the application's user interface.
     * This method is called automatically by the JavaFX runtime after the application
     * has been launched. It performs the following operations:
     * - Loads the main window FXML layout
     * - Sets up the scene with appropriate stylesheets
     * - Initializes dependency injection
     * - Configures the main controller
     * - Displays the primary stage
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file for the main window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main-window.fxml"));
            Parent rootNode = loader.load();
            MainWindowDesktopFXController mainWindowController = loader.getController();

            // Create a scene with the loaded FXML
            Scene scene = new Scene(rootNode);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/main-window.css")).toExternalForm());

            // Initialize the application with dependency injection
            BlueSharpBendingDesktopApplication app = new BlueSharpBendingDesktopApplication();
            app.initializeDependencyInjection(TEMP_DIR, mainWindowController);

            // Get the main controller from the Dagger component
            controller = app.getAppComponent().getMainController();

            // Inject dependencies into the main window controller
            app.getAppComponent().inject(mainWindowController);

            // Start the controller
            controller.start();

            primaryStage.iconifiedProperty().addListener((obs, wasIconified, isIconified) -> {
                if (isIconified) {
                    LoggingUtils.logDebug("Application minimized");
                    controller.stop();
                } else {
                    LoggingUtils.logDebug("Application restored");
                    controller.start();
                }
            });

            // Set the scene and show the stage
            primaryStage.setScene(scene);
            primaryStage.setTitle("Let's Bend - Desktop");
            primaryStage.show();

            // Log successful application startup
            LoggingContext.setComponent("MainDesktop");
            LoggingUtils.logInitialized("Desktop Application UI");
        } catch (Exception e) {
            // Log an error if something goes wrong during application startup
            LoggingContext.setComponent("MainDesktop");
            LoggingUtils.logError("Error while starting the application", e);
        }
    }

    /**
     * Performs cleanup operations when the application is about to stop.
     * This method is called automatically by the JavaFX runtime when the application
     * is shutting down. It handles the following tasks:
     * - Logs the application shutdown event
     * - Calls the controller's stop method to perform any necessary cleanup
     * - Terminates the application process
     */
    @Override
    public void stop() {
        LoggingContext.setComponent("MainDesktop");
        LoggingUtils.logAppShutdown("Desktop Application");
        controller.stop();
        System.exit(0);
    }

}
