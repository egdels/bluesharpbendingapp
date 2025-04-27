package de.schliweb.bluesharpbendingapp.controller;
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
import de.schliweb.bluesharpbendingapp.model.ModelStorageService;
import de.schliweb.bluesharpbendingapp.model.microphone.Microphone;
import de.schliweb.bluesharpbendingapp.view.AndroidSettingsView;
import de.schliweb.bluesharpbendingapp.view.MainWindow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.*;



class MainControllerTest {

    private MainController mainController;
    private MainModel model;
    private Microphone microphone;
    private ModelStorageService modelStorageService;
    private MainWindow mainWindow;
    private MicrophoneController microphoneController;
    private HarpController harpController;
    private TrainingController trainingController;
    private ExecutorService executorService;
    private AndroidSettingsView androidSettingsView;

    @BeforeEach
    void setup() {
        model = mock(MainModel.class);
        microphone = mock(Microphone.class);
        modelStorageService = mock(ModelStorageService.class);
        mainWindow = mock(MainWindow.class);
        harpController = mock(HarpController.class);
        trainingController = mock(TrainingController.class);
        microphoneController = mock(MicrophoneController.class);
        executorService = mock(ExecutorService.class);
        androidSettingsView = mock(AndroidSettingsView.class);

        when(modelStorageService.readModel()).thenReturn(model);
        when(mainWindow.isAndroidSettingsViewActive()).thenReturn(true);
        when(mainWindow.getAndroidSettingsView()).thenReturn(androidSettingsView);

        mainController = new MainController(
                model,
                mainWindow,
                modelStorageService,
                microphoneController
        );
    }

    @AfterEach
    void tearDown() {
        // Clean up resources if needed
    }

    @Test
    void testStartOpensMicrophone() {
        // Act
        mainController.start();

        // Assert
        verify(microphoneController).open();
    }

    @Test
    void testStartOpensMainWindow() {
        // Act
        mainController.start();

        // Assert
        verify(mainWindow).open();
    }

    @Test
    void testStopClosesMicrophone() {
        // Act
        mainController.stop();

        // Assert
        verify(microphoneController).close();
    }

    @Test
    void testStopStoresModel() {
        // Act
        mainController.stop();

        // Assert
        verify(modelStorageService).storeModel(model);
    }

    @Test
    void testHandleLockScreenSelectionUpdatesModel() {
        // Arrange
        int lockScreenIndex = 2;

        // Act
        mainController.handleLockScreenSelection(lockScreenIndex);

        // Assert
        verify(model).setSelectedLockScreenIndex(lockScreenIndex);
        verify(model).setStoredLockScreenIndex(lockScreenIndex);
    }

    @Test
    void testHandleLockScreenSelectionStoresModel() {
        // Arrange
        int lockScreenIndex = 2;

        // Act
        mainController.handleLockScreenSelection(lockScreenIndex);

        // Assert
        verify(modelStorageService).storeModel(model);
    }

    @Test
    void testInitLockScreenSetsSelectedLockScreen() {
        // Arrange
        int lockScreenIndex = 3;
        when(model.getSelectedLockScreenIndex()).thenReturn(lockScreenIndex);

        // Act
        mainController.initLockScreen();

        // Assert
        verify(androidSettingsView).setSelectedLockScreen(lockScreenIndex);
    }

    @Test
    void testInitLockScreenDoesNothingWhenViewInactive() {
        // Arrange
        when(mainWindow.isAndroidSettingsViewActive()).thenReturn(false);

        // Act
        mainController.initLockScreen();

        // Assert - verify that the view was not accessed
        verify(mainWindow, never()).getAndroidSettingsView();
    }

}
