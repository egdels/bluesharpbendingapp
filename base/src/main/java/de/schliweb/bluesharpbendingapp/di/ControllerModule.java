package de.schliweb.bluesharpbendingapp.di;
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

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import de.schliweb.bluesharpbendingapp.controller.*;
import de.schliweb.bluesharpbendingapp.model.MainModel;
import de.schliweb.bluesharpbendingapp.model.ModelStorageService;
import de.schliweb.bluesharpbendingapp.model.microphone.Microphone;
import de.schliweb.bluesharpbendingapp.view.MainWindow;

import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;

/**
 * Dagger module that provides controller dependencies for the application.
 * This module is responsible for configuring how controller instances
 * are created and managed throughout the application.
 */
@Module
public abstract class ControllerModule {

    /**
     * Provides a singleton instance of HarpController.
     *
     * @param model               the MainModel containing application state
     * @param modelStorageService the service for storing model data
     * @param window              the main application window
     * @param executorService     the executor service for asynchronous operations
     * @return a singleton HarpController instance
     */
    @Provides
    @Singleton
    static HarpController provideHarpController(MainModel model, ModelStorageService modelStorageService, MainWindow window, ExecutorService executorService) {
        return new HarpController(model, modelStorageService, window, executorService);
    }

    /**
     * Provides a singleton instance of TrainingController.
     *
     * @param model               the MainModel containing application state
     * @param modelStorageService the service for storing model data
     * @param window              the main application window
     * @param executorService     the executor service for asynchronous operations
     * @return a singleton TrainingController instance
     */
    @Provides
    @Singleton
    static TrainingController provideTrainingController(MainModel model, ModelStorageService modelStorageService, MainWindow window, ExecutorService executorService) {
        return new TrainingController(model, modelStorageService, window, executorService);
    }

    /**
     * Provides a singleton instance of MicrophoneController.
     *
     * @param model               the MainModel containing application state
     * @param modelStorageService the service for storing model data
     * @param window              the main application window
     * @param microphone          the microphone instance to control
     * @param harpController      the controller for harp-related functionality
     * @param trainingController  the controller for training-related functionality
     * @return a singleton MicrophoneController instance
     */
    @Provides
    @Singleton
    static MicrophoneController provideMicrophoneController(MainModel model, ModelStorageService modelStorageService, MainWindow window, Microphone microphone, HarpController harpController, TrainingController trainingController) {
        return new MicrophoneController(model, modelStorageService, window, microphone, harpController, trainingController);
    }

    /**
     * Provides a singleton instance of MainController.
     *
     * @param model               the MainModel containing the state of the application
     * @param window              the main application window
     * @param modelStorageService the service responsible for managing the persistence of model data
     * @param microphoneController the controller for managing microphone-related functionality
     * @param executorService     the executor service for handling asynchronous operations
     * @return a singleton MainController instance
     */
    @Provides
    @Singleton
    static MainController provideMainController(MainModel model, MainWindow window, ModelStorageService modelStorageService, MicrophoneController microphoneController, ExecutorService executorService) {
        return new MainController(model, window, modelStorageService, microphoneController, executorService);
    }

    // Bind interfaces to implementations using @Binds
    @Binds
    abstract TrainingViewHandler bindTrainingViewHandler(TrainingController impl);

    @Binds
    abstract NoteSettingsViewHandler bindNoteSettingsViewHandler(HarpController impl);

    @Binds
    abstract MicrophoneSettingsViewHandler bindMicrophoneSettingsViewHandler(MicrophoneController impl);

    @Binds
    abstract HarpViewHandler bindHarpViewHandler(HarpController impl);

    @Binds
    abstract HarpSettingsViewHandler bindHarpSettingsViewHandler(HarpController impl);

    @Binds
    abstract AndroidSettingsHandler bindAndroidSettingsHandler(MainController impl);
}

