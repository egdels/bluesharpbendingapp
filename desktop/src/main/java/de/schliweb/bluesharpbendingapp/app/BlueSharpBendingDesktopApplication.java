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

import de.schliweb.bluesharpbendingapp.di.*;
import de.schliweb.bluesharpbendingapp.model.microphone.desktop.MicrophoneDesktop;
import de.schliweb.bluesharpbendingapp.utils.LoggingUtils;
import de.schliweb.bluesharpbendingapp.view.MainWindow;
import lombok.Getter;

/**
 * Application class for the BlueSharp Bending Desktop App.
 * Initializes the dependency injection framework (Dagger) and provides access to the component.
 */
public class BlueSharpBendingDesktopApplication {

    /**
     * Represents the filename used for storing and retrieving a temporary model file.
     * This file is typically located in the application's cache directory.
     */
    private static final String TEMP_FILE = "Model.tmp";

    /**
     * The Dagger component instance used for dependency injection throughout the application.
     */
    @Getter
    private DesktopAppComponent appComponent;

    /**
     * Initializes the dependency injection framework with the necessary modules.
     * This method is called during application startup and can also be called
     * with a specific MainWindow instance when needed.
     *
     * @param tempDirectory The directory path where temporary files will be stored
     * @param mainWindow    The MainWindow instance to use,
     *                      or null if not available yet.
     */
    public void initializeDependencyInjection(String tempDirectory, MainWindow mainWindow) {
        LoggingUtils.logAppStartup("BlueSharp Bending Desktop Application");

        // Create modules with the provided dependencies
        BaseModule baseModule = new BaseModule(tempDirectory, TEMP_FILE);
        ViewModule viewModule = new ViewModule(mainWindow);
        MicrophoneModule microphoneModule = new MicrophoneModule(new MicrophoneDesktop());

        // Create the Dagger component with all required modules
        appComponent = DaggerDesktopAppComponent.builder().baseModule(baseModule).viewModule(viewModule).microphoneModule(microphoneModule).build();

        LoggingUtils.logInitialized("Dependency injection");
    }
}
