package de.schliweb.bluesharpbendingapp.service;
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
import de.schliweb.bluesharpbendingapp.utils.LoggingContext;
import de.schliweb.bluesharpbendingapp.utils.LoggingUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.FileSystems;

/**
 * The ModelStorageService class offers functionalities for persisting and retrieving
 * instances of the MainModel class to and from a temporary file storage.
 * This service simplifies the process of saving application state or configurations
 * while ensuring proper handling of file system operations.
 * <p>
 * The service performs serialization of MainModel to a temporary file and
 * deserialization back into an object. It also creates necessary directories
 * if they do not exist, ensuring seamless file storage.
 * <p>
 * The temporary file is stored in the user's home directory with a predefined sub-path.
 */
public class ModelStorageService {


    /**
     * Represents the path to a temporary directory used for storing
     * temporary files or data during the application's runtime.
     * The value is intended to remain constant and should not be modified after initialization.
     */
    private final String tempDirectory;

    /**
     * Represents the name or path of a temporary file.
     * This variable is immutable and cannot be modified after being initialized.
     * It is used to store or reference the file location for temporary operations.
     */
    private final String tempFile;


    /**
     * Constructs a new ModelStorageService with the specified temporary directory and file.
     *
     * @param tempDirectory the directory path where temporary files will be stored
     * @param tempFile      the name of the temporary file
     */
    public ModelStorageService(String tempDirectory, String tempFile) {
        LoggingContext.setComponent("ModelStorageService");
        LoggingUtils.logInitializing("ModelStorageService");

        this.tempDirectory = tempDirectory;
        this.tempFile = tempFile;

        LoggingUtils.logDebug("Initialized with directory", tempDirectory);
        LoggingUtils.logDebug("Initialized with file", tempFile);
        LoggingUtils.logInitialized("ModelStorageService");
    }

    /**
     * Persists the given MainModel instance to a temporary file for storage.
     * The method ensures that the required directory and file are created if they do not exist.
     * The model's data is serialized into a string format and written to the file.
     *
     * @param model The MainModel object containing the data to be stored.
     */
    public void storeModel(MainModel model) {
        LoggingContext.setComponent("ModelStorageService");
        LoggingContext.setOperation("storeModel");
        LoggingUtils.logOperationStarted("Model storage");

        long startTime = System.currentTimeMillis();

        File directory = new File(tempDirectory);
        if (!directory.exists()) {
            boolean isCreated = directory.mkdirs();
            if (isCreated) {
                LoggingUtils.logDebug("Directory created", tempDirectory);
            }
        }

        File file = new File(tempDirectory + FileSystems.getDefault().getSeparator() + tempFile);
        try (FileOutputStream fos = new FileOutputStream(file); BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos))) {
            boolean isCreated = file.createNewFile();
            if (isCreated) {
                LoggingUtils.logDebug("File created", file.getAbsolutePath());
            }

            bw.write(model.getString());
            LoggingUtils.logDebug("Model data written to file");

        } catch (IOException e) {
            LoggingUtils.logError("Failed to store model", e);
        }

        long duration = System.currentTimeMillis() - startTime;
        LoggingUtils.logPerformance("Model storage", duration);
        LoggingUtils.logOperationCompleted("Model storage");
    }

    /**
     * Reads and returns a MainModel instance from a temporary file storage location.
     * The method checks for the existence of a predefined directory and file for storing the model.
     * If the file exists, it deserializes the model's configuration from the file's content.
     * If the file or directory does not exist, a new instance of MainModel is returned.
     *
     * @return A MainModel object populated with data read from the file,
     * or a new MainModel instance if the file is not available or cannot be read.
     */
    public MainModel readModel() {
        LoggingContext.setComponent("ModelStorageService");
        LoggingContext.setOperation("readModel");
        LoggingUtils.logOperationStarted("Model loading");

        long startTime = System.currentTimeMillis();

        MainModel model = new MainModel();
        File directory = new File(tempDirectory);
        if (!directory.exists()) {
            boolean isCreated = directory.mkdirs();
            if (isCreated) {
                LoggingUtils.logDebug("Directory created", tempDirectory);
            }
        }

        File file = new File(tempDirectory + FileSystems.getDefault().getSeparator() + tempFile);
        if (file.exists()) {
            LoggingUtils.logDebug("Model file found", file.getAbsolutePath());
            try (FileInputStream fos = new FileInputStream(file); BufferedReader bw = new BufferedReader(new InputStreamReader(fos))) {
                String line = bw.readLine();
                if (line != null) {
                    LoggingUtils.logDebug("Model data read from file");
                    model = createFromString(line);
                } else {
                    LoggingUtils.logWarning("Empty model file", "Using default model");
                }
            } catch (IOException e) {
                LoggingUtils.logError("Failed to read model", e);
            }
        } else {
            LoggingUtils.logDebug("Model file not found", "Using default model");
        }

        long duration = System.currentTimeMillis() - startTime;
        LoggingUtils.logPerformance("Model loading", duration);
        LoggingUtils.logOperationCompleted("Model loading");
        return model;
    }


    /**
     * Creates a MainModel instance from a given string representation.
     * The input string is expected to be in a specific format, where
     * key-value pairs are defined as "getMethodName:value" and separated
     * by a delimiter. The method dynamically invokes corresponding setters
     * in the MainModel class to populate its fields based on the provided input.
     *
     * @param string the string representation of the MainModel object,
     *               containing key-value pairs to initialize the fields.
     * @return a newly created MainModel instance populated with values
     * based on the input string.
     */
    private MainModel createFromString(String string) {
        LoggingContext.setComponent("ModelStorageService");
        LoggingContext.setOperation("createFromString");
        LoggingUtils.logDebug("Creating model from string representation");

        long startTime = System.currentTimeMillis();

        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        MainModel model = new MainModel();
        Method[] methods = model.getClass().getMethods();
        int propertiesSet = 0;

        for (String entry : strings) {
            entry = entry.replaceFirst("get", "set");
            if (entry.contains(":")) {
                String m = entry.split(":")[0];
                String p = entry.split(":")[1];
                for (Method method : methods) {
                    if (method.getName().equals(m)) {
                        try {
                            method.invoke(model, Integer.parseInt(p));
                            propertiesSet++;
                            LoggingUtils.logDebug("Set model property", m + "=" + p);
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            LoggingUtils.logError("Failed to set model property " + m, e);
                        }
                    }
                }
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        LoggingUtils.logPerformance("Model creation from string", duration);
        LoggingUtils.logDebug("Model created with " + propertiesSet + " properties set");
        return model;
    }

}
