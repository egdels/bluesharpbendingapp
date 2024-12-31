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

import com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme;
import com.formdev.flatlaf.util.SystemInfo;
import de.schliweb.bluesharpbendingapp.controller.MainController;
import de.schliweb.bluesharpbendingapp.model.MainModel;
import de.schliweb.bluesharpbendingapp.model.microphone.Microphone;
import de.schliweb.bluesharpbendingapp.model.microphone.desktop.MicrophoneDesktop;
import de.schliweb.bluesharpbendingapp.utils.Logger;
import de.schliweb.bluesharpbendingapp.view.MainWindow;
import de.schliweb.bluesharpbendingapp.view.desktop.MainWindowDesktop;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.Scanner;

/**
 * The MainDesktop class serves as the entry point and main controller for the
 * desktop application. It handles application settings initialization,
 * version management, and model persistence. This class defines static variables
 * and methods to configure the application behavior, manage resources, and
 * communicate between different system components.
 */
public class MainDesktop {

    /**
     * A Logger instance for logging messages specifically related to the MainDesktop class.
     * This logger provides contextual logging capabilities, using the MainDesktop class
     * as a reference for the source of logged messages. It supports different logging levels
     * such as debug, info, and error to aid in tracing application behavior and troubleshooting.
     */
    private static final Logger LOGGER = new Logger(MainDesktop.class);

    /**
     * A constant representing the directory path for temporary files used by the application.
     *
     * This path is automatically generated based on the user's home directory and a predefined
     * folder structure specific to the application. The directory is intended to store temporary files
     * and data that may be created or used during the application's runtime.
     *
     * The directory path includes:
     * - The user's home directory (retrieved using the "user.home" system property).
     * - A folder named "BluesHarpBendingApp.tmp" for organizational purposes.
     *
     * The use of `FileSystems.getDefault().getSeparator()` ensures that the generated path
     * conforms to the file system and platform-specific directory separator.
     *
     * This field is defined as `private` for encapsulation, `static` to allow shared usage across
     * the class, and `final` to prevent modification after initialization.
     */
    private static final String TEMP_DIR = System.getProperty("user.home") + FileSystems.getDefault().getSeparator() + "BluesHarpBendingApp.tmp" + FileSystems.getDefault().getSeparator();

    /**
     * Represents the name of the temporary file used for storing serialized data of the application's model.
     * This is a constant value that specifies the file name where the model will be saved or read during
     * operations within the MainDesktop class.
     *
     * The file is expected to be located in the application's designated temporary directory.
     */
    private static final String TEMP_FILE = "Model.tmp";

    /**
     * Represents a cached version string retrieved from the host system or server.
     * This static field is intended to store the version information temporarily,
     * allowing it to be accessed by other methods or components within the application.
     *
     * This variable is initially set to null and is expected to be populated through
     * interactions with the host, such as through network communications or file I/O.
     * It may be used for version checks or other operations requiring the application
     * to verify the version information of the host.
     */
    private static String versionFromHost = null;

    /**
     * A static reference to the main controller instance used within the application.
     * This controller serves as a central point for managing the application's primary operations.
     *
     * It is used across the application to coordinate tasks, manage state, and provide
     * a single access point for key functionalities implemented in the system.
     */
    private static MainController controller;

    /**
     * Represents the static instance of the MainModel used as the primary data model
     * within the MainDesktop application. This is shared across the application's context
     * to maintain consistency and synchronization of data.
     */
    private static MainModel mainModel;

    /**
     * The main entry point of the application. This method initializes application configurations,
     * sets up a graphical user interface, and starts the main controller.
     *
     * @param args an array of command-line arguments. Recognized arguments include:
     *             - "debug": enables debug-level logging.
     *             - "info": enables informational logging.
     *             - "donationware": enables application operation in donationware mode.
     */
    public static void main(String[] args) {
        Logger.setInfo(true);

        Logger.setDebug(false);

        if( SystemInfo.isMacOS ) {
            // enable screen menu bar
            // (moves menu bar from JFrame window to top of screen)
            System.setProperty( "apple.laf.useScreenMenuBar", "false" );

            // application name used in screen menu bar
            // (in first menu after the "apple" menu)
            System.setProperty( "apple.awt.application.name", "Let's Bend" );

            // appearance of window title bars
            // possible values:
            //   - "system": use current macOS appearance (light or dark)
            //   - "NSAppearanceNameAqua": use light appearance
            //   - "NSAppearanceNameDarkAqua": use dark appearance
            // (must be set on main thread and before AWT/Swing is initialized;
            //  setting it on AWT thread does not work)
            System.setProperty( "apple.awt.application.appearance", "system" );
        }

        System.setProperty("flatlaf.menuBarEmbedded", "true");
        System.setProperty("flatlaf.useWindowDecorations", "true");
        FlatArcDarkOrangeIJTheme.setup();

        if (SystemInfo.isLinux) {
            // enable custom window decorations
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        }

        checkVersionFromHost();
        Logger.setInfo(false);
        boolean isDonationWare = false;
        for (String arg : args) {
            if ("debug".equals(arg)) {
                Logger.setDebug(true);
                Logger.setInfo(true);
            }
            if ("info".equals(arg)) {
                Logger.setInfo(true);
            }
            if ("donationware".equals(arg)) {
                isDonationWare = true;
            }
        }
        Microphone microphone = new MicrophoneDesktop();
        microphone.setName(0);

        MainWindow mainWindow = new MainWindowDesktop(isDonationWare);


        mainModel = readModel();
        mainModel.setMicrophone(microphone);
        microphone.setAlgorithm(mainModel.getStoredAlgorithmIndex());
        microphone.setName(mainModel.getStoredMicrophoneIndex());
        controller = new MainController(mainWindow, mainModel);
        controller.start();
    }

    /**
     * Stores the provided MainModel instance to a temporary file within the application's
     * designated temporary directory. If the temporary directory or file does not exist,
     * it will attempt to create them. The content of the model is written as a string
     * to the file.
     *
     * @param model the MainModel instance to be stored. It must provide a valid string
     *              representation through the getString() method, which will be written
     *              to the file.
     */
    private static void storeModel(MainModel model) {

        File directory = new File(TEMP_DIR);
        if (!directory.exists()) {
            boolean isCreated = directory.mkdirs();
            if (isCreated) LOGGER.debug("Directory created");
        }
        File file = new File(TEMP_DIR + FileSystems.getDefault().getSeparator() + TEMP_FILE);
        try (FileOutputStream fos = new FileOutputStream(file); BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos))) {
            boolean isCreated = file.createNewFile();
            if (isCreated) LOGGER.debug("Filed created");

            bw.write(model.getString());

        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

    }

    /**
     * Reads a MainModel instance from a temporary file in the designated temporary directory.
     * If the temporary directory does not exist, it attempts to create it. If the temporary file
     * exists, its content is read and used to construct a MainModel instance. If the file is absent
     * or an error occurs during the process, a new MainModel instance is returned.
     *
     * @return a MainModel instance. If the file exists and is readable, the model is constructed
     *         from its content. Otherwise, a default instance is returned.
     */
    private static MainModel readModel() {

        MainModel model = new MainModel();
        File directory = new File(TEMP_DIR);
        if (!directory.exists()) {
            boolean isCreated = directory.mkdirs();
            if (isCreated) LOGGER.debug("Directory created");
        }
        File file = new File(TEMP_DIR + FileSystems.getDefault().getSeparator() + TEMP_FILE);
        if (file.exists()) {
            try (FileInputStream fos = new FileInputStream(file); BufferedReader bw = new BufferedReader(new InputStreamReader(fos))) {

                String line = bw.readLine();
                if (line != null) model = MainModel.createFromString(line);

            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
        return model;
    }

    /**
     * Checks the version of the application from a remote host by accessing a specified URL.
     * Retrieves the version information from the content, trims it, and logs the result.
     * If an error occurs during the process, logs the error message.
     *
     * This method connects to the remote host using HTTPS, reads the content from the response,
     * and stores the version information in the class-level variable `versionFromHost`.
     * The HTTP response is validated before processing the content.
     *
     * Exceptions caught include `IOException` for network-related issues and `URISyntaxException`
     * for invalid URI syntax.
     */
    private static void checkVersionFromHost() {
        URL url;
        HttpURLConnection huc;
        try {
            url = new URI("https://www.letsbend.de/download/version.txt").toURL();
            huc = (HttpURLConnection) url.openConnection();
            int responseCode = huc.getResponseCode();

            if (HttpURLConnection.HTTP_OK == responseCode) {
                LOGGER.info("ok");
                Scanner scanner = new Scanner((InputStream) huc.getContent());
                versionFromHost = scanner.nextLine();
                scanner.close();
                if (versionFromHost != null) {
                    versionFromHost = versionFromHost.trim();
                }
                LOGGER.info(versionFromHost);
            }
        } catch (IOException |URISyntaxException e ) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Retrieves the version information of the application from a remote host.
     * If the version has already been fetched, it returns the cached version.
     * Otherwise, it triggers a check to fetch the version from the host.
     *
     * @return the version of the application as a String. If the version could not be fetched,
     *         it may return null or an empty string.
     */
    public static String getVersionFromHost() {
        if (versionFromHost != null) return versionFromHost;
        checkVersionFromHost();
        return versionFromHost;
    }

    /**
     * Terminates the application by performing necessary cleanup operations and shutting down.
     *
     * This method executes the following tasks in order:
     * 1. Logs an informational message indicating the application is shutting down.
     * 2. Stops the main controller, ensuring that all essential components and resources
     *    are properly released, including audio input devices and any ongoing
     *    asynchronous operations.
     * 3. Saves the current state of the main application model to a designated temporary
     *    file, allowing recovery or storage of application data.
     * 4. Exits the application with a termination status of 0, signaling a successful shutdown.
     */
    public static void close() {
        LOGGER.info("Shutting down");
        controller.stop();
        storeModel(mainModel);
        System.exit(0);
    }
}
