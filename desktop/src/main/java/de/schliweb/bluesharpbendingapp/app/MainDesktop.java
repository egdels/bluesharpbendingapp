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
import de.schliweb.bluesharpbendingapp.model.MainModel;
import de.schliweb.bluesharpbendingapp.model.microphone.Microphone;
import de.schliweb.bluesharpbendingapp.model.microphone.desktop.MicrophoneDesktop;
import de.schliweb.bluesharpbendingapp.view.desktop.MainWindowDesktopFXController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.Objects;
import java.util.Scanner;


/**
 * The MainDesktop class represents the entry point for a JavaFX desktop application.
 * It serves as the main application class responsible for initializing the application,
 * managing its lifecycle, and handling essential tasks such as loading data, interacting
 * with the GUI controller, and shutting down.
 */
@Slf4j
public class MainDesktop extends Application {

    /**
     * The constant TEMP_DIR represents the directory path where temporary files are stored
     * for the application. It is constructed dynamically using the user's home directory
     * and a predefined subdirectory specific to the application.
     * <p>
     * Key Characteristics:
     * - Utilizes the user's home directory as the base path.
     * - Appends a specific temporary folder named "BluesHarpBendingApp.tmp".
     * - Ensures platform-independent file path construction by leveraging the default
     *   file system separator.
     * <p>
     * Usage:
     * TEMP_DIR is used internally to define a consistent temporary storage location
     * for the application's runtime operations.
     */
    private static final String TEMP_DIR = System.getProperty("user.home") + FileSystems.getDefault().getSeparator() + "BluesHarpBendingApp.tmp" + FileSystems.getDefault().getSeparator();

    /**
     * Represents the name of the temporary file used by the application.
     * This file is utilized for storing intermediate or temporary data,
     * particularly related to the main model of the application.
     * <p>
     * TEMP_FILE is a constant holding the file name "Model.tmp", which may
     * be used in operations such as saving, reading, or managing model data.
     * <p>
     * It is defined as a static final variable, meaning its value cannot
     * be modified during application runtime.
     */
    private static final String TEMP_FILE = "Model.tmp";

    /**
     * A static variable that stores the version information of the application retrieved from the host.
     * This variable is initially set to null and is updated as needed during runtime to reflect the
     * version details fetched from an external source.
     */
    private static String versionFromHost = null;

    /**
     * A static instance of the MainController used to manage the primary
     * interactions and operations of the MainDesktop application.
     * The controller facilitates communication and control between
     * different components of the application.
     */
    private static MainController controller;

    /**
     * The main model instance used across the application to store and manage
     * the core data or state encapsulated by the {@code MainModel} class.
     * This static field acts as a shared resource within the {@code MainDesktop}
     * class and its operations.
     * <p>
     * This field is initialized and manipulated by specific methods within the
     * {@code MainDesktop} class, ensuring centralized control over the model's
     * lifecycle and integrity. Modifications to this model should adhere to
     * the imposed access restrictions and lifecycle management of the enclosing class.
     */
    private static MainModel mainModel;

    /**
     * The main entry point for the application. This method initializes the logging settings,
     * configures the microphone, retrieves the main model, and starts the application.
     *
     * @param args the command-line arguments passed to the application. Supported arguments:
     *             - "debug": Enables debug-level logging and informational logging.
     *             - "info": Enables informational logging.
     */
    public static void main(String[] args) {


        checkVersionFromHost();

        Microphone microphone = new MicrophoneDesktop();
        microphone.setName(0);

        mainModel = readModel();
        mainModel.setMicrophone(microphone);
        microphone.setAlgorithm(mainModel.getStoredAlgorithmIndex());
        microphone.setName(mainModel.getStoredMicrophoneIndex());
        launch(args);
    }

    /**
     * Stores the provided model object into a file within a temporary directory.
     * Ensures the directory exists and handles the creation of the file if it does not already exist.
     * Writes the string representation of the model to the file.
     * Logs any errors encountered during the process.
     *
     * @param model the {@link MainModel} object to be stored in the file
     */
    private static void storeModel(MainModel model) {

        File directory = new File(TEMP_DIR);
        if (!directory.exists()) {
            boolean isCreated = directory.mkdirs();
            if (isCreated) log.debug("Directory created");
        }
        File file = new File(TEMP_DIR + FileSystems.getDefault().getSeparator() + TEMP_FILE);
        try (FileOutputStream fos = new FileOutputStream(file); BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos))) {
            boolean isCreated = file.createNewFile();
            if (isCreated) log.debug("Filed created");

            bw.write(model.getString());

        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    /**
     * Reads the model object from a temporary directory file if it exists.
     * If the directory or file is not present, it initializes with a default MainModel object.
     * The method enforces directory creation if missing, reads the file,
     * and constructs the model from a string representation in the file content.
     * Logs debug and error messages appropriately during execution.
     *
     * @return the {@link MainModel} object read from the file or a default instance if the file does not exist.
     */
    private static MainModel readModel() {

        MainModel model = new MainModel();
        File directory = new File(TEMP_DIR);
        if (!directory.exists()) {
            boolean isCreated = directory.mkdirs();
            if (isCreated) log.debug("Directory created");
        }
        File file = new File(TEMP_DIR + FileSystems.getDefault().getSeparator() + TEMP_FILE);
        if (file.exists()) {
            try (FileInputStream fos = new FileInputStream(file); BufferedReader bw = new BufferedReader(new InputStreamReader(fos))) {

                String line = bw.readLine();
                if (line != null) model = MainModel.createFromString(line);

            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return model;
    }

    /**
     * Checks the latest version information available on a remote host and logs related details.
     * Connects to a predefined URL to fetch the version information, reads the response content,
     * and updates the application's version information if available.
     * <p>
     * The method performs the following steps:
     * - Creates a connection to the specified URL.
     * - Sends a GET request to the server and checks the HTTP response code.
     * - If the response code is HTTP 200 (OK), fetches the content of the response.
     * - Reads the version string from the response and trims it to remove extra spaces if needed.
     * - Logs the retrieved version string or error details in case of failures.
     * <p>
     * Any exceptions such as URI syntax errors or IO-related issues during the connection or
     * data retrieval process are caught and handled by logging the error message.
     */
    private static void checkVersionFromHost() {
        URL url;
        HttpURLConnection huc;
        try {
            url = new URI("https://letsbend.de/download/version.txt").toURL();
            huc = (HttpURLConnection) url.openConnection();
            int responseCode = huc.getResponseCode();

            if (HttpURLConnection.HTTP_OK == responseCode) {
                log.info("ok");
                Scanner scanner = new Scanner((InputStream) huc.getContent());
                versionFromHost = scanner.nextLine();
                scanner.close();
                if (versionFromHost != null) {
                    versionFromHost = versionFromHost.trim();
                }
                log.info(versionFromHost);
            }
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Retrieves the latest application version from a remote host.
     * If the version information has already been fetched and cached, it returns the cached value.
     * Otherwise, it fetches the version from the host by invoking the {@code checkVersionFromHost()} method.
     *
     * @return the latest version as a {@code String}, or {@code null} if the version could not be fetched.
     */
    public static String getVersionFromHost() {
        if (versionFromHost != null) return versionFromHost;
        checkVersionFromHost();
        return versionFromHost;
    }

    /**
     * Gracefully shuts down the application by performing the following steps:
     * <p>
     * 1. Logs a shutdown information message using the application's logger.
     * 2. Stops the application controller, ensuring all essential resources and
     *    processes are properly terminated.
     * 3. Stores the state of the main application model to a file for possible recovery
     *    or future use.
     * 4. Exits the application with a system-level termination call.
     * <p>
     * This method ensures a proper and clean shutdown sequence, avoiding potential
     * resource leaks or incomplete data handling.
     */
    public static void close() {
        log.info("Shutting down");
        controller.stop();
        storeModel(mainModel);
        System.exit(0);
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
            controller = new MainController(mainWindowDesktopFXController, mainModel);
            controller.start();

            // Set the scene and show the stage
            primaryStage.setScene(scene);
            primaryStage.setTitle("Let's Bend - Desktop");
            primaryStage.show();
        } catch (Exception e) {
            // Log an error if something goes wrong during application startup
            log.error("Error while starting the application: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        close();
    }

}
