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
import de.schliweb.bluesharpbendingapp.utils.Logger;
import de.schliweb.bluesharpbendingapp.view.MainWindow;
import de.schliweb.bluesharpbendingapp.view.desktop.MainWindowDesktop;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.Scanner;

/**
 * The type Main desktop.
 */
public class MainDesktop {

    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = new Logger(MainDesktop.class);

    /**
     * The constant TEMP_DIR.
     */
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir")
            + FileSystems.getDefault().getSeparator() + "BluesHarpBendingApp.tmp"
            + FileSystems.getDefault().getSeparator();

    /**
     * The constant TEMP_FILE.
     */
    private static final String TEMP_FILE = "Model.tmp";

    /**
     * The constant versionFromHost.
     */
    private static String versionFromHost = null;

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        Logger.setInfo(true);
        LOGGER.info("Enter with parameter " + Arrays.toString(args));
        Logger.setDebug(false);

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
        Microphone microphone = new MicrophoneDesktop(); // TODO
        microphone.setName(0);

        MainWindow mainWindow = new MainWindowDesktop(isDonationWare);
        MainModel mainModel = readModel();
        mainModel.setMicrophone(microphone);
        microphone.setAlgorithm(mainModel.getStoredAlgorithmIndex());
        microphone.setName(mainModel.getStoredMicrophoneIndex());
        MainController controller = new MainController(mainWindow, mainModel);
        controller.start();
        storeModel(mainModel);
        LOGGER.info("Shutting down");
        System.exit(0);
    }

    /**
     * Store model.
     *
     * @param model the model
     */
    private static void storeModel(MainModel model) {
        LOGGER.info("Enter with parameter " + model.toString());
        File directory = new File(TEMP_DIR);
        if (!directory.exists()) {
            boolean isCreated = directory.mkdirs();
            if (isCreated) LOGGER.debug("Directory created");
        }
        File file = new File(TEMP_DIR + FileSystems.getDefault().getSeparator() + TEMP_FILE);
        try {
            boolean isCreated = file.createNewFile();
            if (isCreated) LOGGER.debug("Filed created");
            FileOutputStream fos = new FileOutputStream(file);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(model.toString());

            bw.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("Leave");
    }

    /**
     * Read model main model.
     *
     * @return the main model
     */
    private static MainModel readModel() {
        LOGGER.info("Enter");
        MainModel model = new MainModel();
        File directory = new File(TEMP_DIR);
        if (!directory.exists()) {
            boolean isCreated = directory.mkdirs();
            if (isCreated) LOGGER.debug("Directory created");
        }
        File file = new File(TEMP_DIR + FileSystems.getDefault().getSeparator() + TEMP_FILE);
        if (file.exists()) {
            try {
                FileInputStream fos = new FileInputStream(file);
                BufferedReader bw = new BufferedReader(new InputStreamReader(fos));
                String line = bw.readLine();
                model = MainModel.createFromString(line);
                bw.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
        return model;
    }

    /**
     * Check version from host.
     */
    private static void checkVersionFromHost() {
        URL url;
        HttpURLConnection huc;
        try {
            url = new URL("https://www.letsbend.de/download/version.txt");
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
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Gets version from host.
     *
     * @return the version from host
     */
    public static String getVersionFromHost() {
        if (versionFromHost != null) return versionFromHost;
        checkVersionFromHost();
        return versionFromHost;
    }
}
