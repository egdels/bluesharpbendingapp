package de.schliweb.bluesharpbendingapp.di;
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

import de.schliweb.bluesharpbendingapp.model.microphone.Microphone;
import de.schliweb.bluesharpbendingapp.utils.LoggingContext;
import de.schliweb.bluesharpbendingapp.utils.LoggingUtils;
import de.schliweb.bluesharpbendingapp.view.MainWindow;

/**
 * Utility class for initializing dependency injection across different platforms.
 * This class reduces code duplication by providing common methods for dependency injection setup.
 */
public class DependencyInjectionInitializer {

    /**
     * Represents the filename used for storing and retrieving a temporary model file.
     * This file is typically located in the application's cache directory.
     */
    public static final String TEMP_FILE = "Model.tmp";

    /**
     * Creates the base module with the provided dependencies.
     *
     * @param tempDirectory The directory path where temporary files will be stored
     * @return The configured BaseModule instance
     */
    public static BaseModule createBaseModule(String tempDirectory) {
        return new BaseModule(tempDirectory, TEMP_FILE);
    }

    /**
     * Creates the view module with the provided dependencies.
     *
     * @param mainWindow The MainWindow instance to use, or null if not available yet
     * @return The configured ViewModule instance
     */
    public static ViewModule createViewModule(MainWindow mainWindow) {
        return new ViewModule(mainWindow);
    }

    /**
     * Creates the microphone module with the provided dependencies.
     *
     * @param microphone The Microphone implementation to use
     * @return The configured MicrophoneModule instance
     */
    public static MicrophoneModule createMicrophoneModule(Microphone microphone) {
        return new MicrophoneModule(microphone);
    }

    /**
     * Logs the application startup with the provided application name.
     *
     * @param componentName The name of the component for logging context
     * @param appName The name of the application for the startup log message
     */
    public static void logAppStartup(String componentName, String appName) {
        LoggingContext.setComponent(componentName);
        LoggingUtils.logAppStartup(appName);
    }

    /**
     * Logs that dependency injection has been initialized.
     *
     * @param componentName The name of the component for logging context
     */
    public static void logDependencyInjectionInitialized(String componentName) {
        LoggingContext.setComponent(componentName);
        LoggingUtils.logInitialized("Dependency injection");
    }
}