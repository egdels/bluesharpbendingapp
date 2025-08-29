package de.schliweb.bluesharpbendingapp.controller;
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


import de.schliweb.bluesharpbendingapp.favorites.Favorite;
import de.schliweb.bluesharpbendingapp.favorites.FavoriteManager;
import de.schliweb.bluesharpbendingapp.model.MainModel;
import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;
import de.schliweb.bluesharpbendingapp.service.ModelStorageService;
import de.schliweb.bluesharpbendingapp.utils.LoggingContext;
import de.schliweb.bluesharpbendingapp.utils.LoggingUtils;
import de.schliweb.bluesharpbendingapp.view.AndroidSettingsView;
import de.schliweb.bluesharpbendingapp.view.MainWindow;

import java.util.List;

/**
 * The MainController class is responsible for managing the primary workflow and logic
 * of the application. It acts as a mediator between the user interface, model, and other
 * services to ensure proper coordination of tasks.
 * <p>
 * This class implements the AndroidSettingsHandler interface to provide functionality
 * specific to managing Android lock screen settings.
 */
public class MainController implements AndroidSettingsHandler, FavoritesHandler {

    private final MainModel model;
    private final ModelStorageService modelStorageService;

    private final MainWindow window;

    private final MicrophoneController microphoneController;

    private final FavoriteManager favoriteManager;

    /**
     * Constructs a new MainController and initializes its components using the provided
     * dependencies.
     *
     * @param window               The MainWindow instance used to set up UI view handlers.
     * @param modelStorageService  The ModelStorageService instance used to retrieve and store model data.
     * @param microphoneController The controller for microphone-related functionality.
     */
    public MainController(MainModel model, MainWindow window, ModelStorageService modelStorageService, MicrophoneController microphoneController, FavoriteManager favoriteManager) {
        LoggingContext.setComponent("MainController");
        LoggingUtils.logInitializing("MainController");
        this.model = model;
        this.microphoneController = microphoneController;
        this.modelStorageService = modelStorageService;
        this.window = window;
        this.favoriteManager = favoriteManager;

        LoggingUtils.logDebug("Setting stored reference pitch based on the model's stored concert pitch index");
        NoteLookup.setConcertPitchByIndex(model.getStoredConcertPitchIndex());

        LoggingUtils.logInitialized("MainController");
    }

    // FavoritesHandler implementation (delegates to FavoriteManager)
    @Override
    public List<Favorite> loadFavorites() {
        return favoriteManager.load();
    }

    @Override
    public Favorite toggleFavorite(String tuningId, String tuningHash, String key, Integer holes, String label) {
        return favoriteManager.toggle(tuningId, tuningHash, key, holes, label);
    }

    @Override
    public boolean isCurrentFavorite(String tuningId, String tuningHash, String key, Integer holes) {
        return favoriteManager.isCurrentFavorite(tuningId, tuningHash, key, holes);
    }

    @Override
    public void renameFavorite(String id, String newLabel) {
        favoriteManager.rename(id, newLabel);
    }

    @Override
    public void removeFavorite(String id) {
        favoriteManager.remove(id);
    }

    @Override
    public void reorderFavorites(List<Favorite> inNewOrder) {
        favoriteManager.reorder(inNewOrder);
    }

    /**
     * Starts the primary components of the application.
     * <p>
     * This method initializes the application by opening the microphone for audio
     * input and launching the main application window for user interaction.
     */
    public void start() {
        LoggingContext.setComponent("MainController");
        LoggingUtils.logOperationStarted("Application startup");

        LoggingUtils.logDebug("Opening microphone");
        microphoneController.open();

        LoggingUtils.logDebug("Opening main application window");
        this.window.open();

        LoggingUtils.logOperationCompleted("Application startup");
    }

    /**
     * Stops the application by shutting down essential components.
     * <p>
     * This method closes the microphone resource to ensure proper release
     * of audio input devices and shuts down the executor service
     * to terminate any ongoing asynchronous operations.
     */
    public void stop() {
        LoggingContext.setComponent("MainController");
        LoggingUtils.logOperationStarted("Application shutdown");

        LoggingUtils.logDebug("Closing microphone");
        microphoneController.close();

        LoggingUtils.logDebug("Storing model");
        long startTime = System.currentTimeMillis();
        modelStorageService.storeModel(model);
        long duration = System.currentTimeMillis() - startTime;
        LoggingUtils.logPerformance("Model storage", duration);

        LoggingUtils.logOperationCompleted("Application shutdown");
    }

    @Override
    public void handleLockScreenSelection(int lockScreenIndex) {
        this.model.setSelectedLockScreenIndex(lockScreenIndex);
        this.model.setStoredLockScreenIndex(lockScreenIndex);
        modelStorageService.storeModel(model);
    }

    @Override
    public void initLockScreen() {
        if (window.isAndroidSettingsViewActive()) {
            AndroidSettingsView androidSettingsView = window.getAndroidSettingsView();
            if (androidSettingsView != null) {
                androidSettingsView.setSelectedLockScreen(model.getSelectedLockScreenIndex());
            }
        }
    }

}
