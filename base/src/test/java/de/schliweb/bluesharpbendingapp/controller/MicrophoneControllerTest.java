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

import de.schliweb.bluesharpbendingapp.model.MainModel;
import de.schliweb.bluesharpbendingapp.service.ModelStorageService;
import de.schliweb.bluesharpbendingapp.model.microphone.AbstractMicrophone;
import de.schliweb.bluesharpbendingapp.model.microphone.Microphone;
import de.schliweb.bluesharpbendingapp.utils.ChordDetectionResult;
import de.schliweb.bluesharpbendingapp.view.MainWindow;
import de.schliweb.bluesharpbendingapp.view.MicrophoneSettingsView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;

class MicrophoneControllerTest {

    private MicrophoneController microphoneController;
    private MainModel model;
    private ModelStorageService modelStorageService;
    private MainWindow window;
    private Microphone microphone;
    private HarpFrequencyHandler harpFrequencyHandler;
    private TrainingFrequencyHandler trainingFrequencyHandler;
    private MicrophoneSettingsView microphoneSettingsView;
    private MockedStatic<AbstractMicrophone> abstractMicrophoneMock;
    private String[] supportedAlgorithms = {"Algorithm1", "Algorithm2"};
    private String[] supportedConfidences = {"Confidence1", "Confidence2"};
    private String[] supportedMicrophones = {"Microphone1", "Microphone2"};
    private ChordDetectionResult chordResult;

    @BeforeEach
    void setup() {
        // Mock dependencies
        model = mock(MainModel.class);
        modelStorageService = mock(ModelStorageService.class);
        window = mock(MainWindow.class);
        microphone = mock(Microphone.class);
        harpFrequencyHandler = mock(HarpFrequencyHandler.class);
        trainingFrequencyHandler = mock(TrainingFrequencyHandler.class);
        microphoneSettingsView = mock(MicrophoneSettingsView.class);

        // Setup common mock behavior
        when(modelStorageService.readModel()).thenReturn(model);
        when(window.isMicrophoneSettingsViewActive()).thenReturn(true);
        when(window.getMicrophoneSettingsView()).thenReturn(microphoneSettingsView);
        when(microphone.getSupportedMicrophones()).thenReturn(supportedMicrophones);

        // Mock static methods in AbstractMicrophone
        abstractMicrophoneMock = mockStatic(AbstractMicrophone.class);
        when(AbstractMicrophone.getSupportedAlgorithms()).thenReturn(supportedAlgorithms);
        when(AbstractMicrophone.getSupportedConfidences()).thenReturn(supportedConfidences);

        // Initialize ChordDetectionResult with test values
        chordResult = new ChordDetectionResult(java.util.List.of(440.0), 0.8);

        // Create controller
        microphoneController = new MicrophoneController(
                model,
                modelStorageService,
                window,
                microphone,
                harpFrequencyHandler,
                trainingFrequencyHandler
        );
    }

    @AfterEach
    void tearDown() {
        // Close the static mock to prevent memory leaks
        if (abstractMicrophoneMock != null) {
            abstractMicrophoneMock.close();
        }
    }

    @Test
    void testOpenOpensMicrophone() {
        // Act
        microphoneController.open();

        // Assert
        verify(microphone).open();
    }

    @Test
    void testCloseClosesMicrophone() {
        // Act
        microphoneController.close();

        // Assert
        verify(microphone).close();
    }

    @Test
    void testHandleAlgorithmSelectionUpdatesModel() {
        // Arrange
        int algorithmIndex = 1;

        // Act
        microphoneController.handleAlgorithmSelection(algorithmIndex);

        // Assert
        verify(model).setStoredAlgorithmIndex(algorithmIndex);
        verify(model).setSelectedAlgorithmIndex(algorithmIndex);
    }

    @Test
    void testHandleAlgorithmSelectionUpdatesMicrophone() {
        // Arrange
        int algorithmIndex = 1;

        // Act
        microphoneController.handleAlgorithmSelection(algorithmIndex);

        // Assert
        verify(microphone).close();
        verify(microphone).setAlgorithm(algorithmIndex);
        verify(microphone).open();
    }

    @Test
    void testHandleAlgorithmSelectionStoresModel() {
        // Arrange
        int algorithmIndex = 1;

        // Act
        microphoneController.handleAlgorithmSelection(algorithmIndex);

        // Assert
        verify(modelStorageService).storeModel(model);
    }

    @Test
    void testHandleMicrophoneSelectionUpdatesModel() {
        // Arrange
        int microphoneIndex = 1;

        // Act
        microphoneController.handleMicrophoneSelection(microphoneIndex);

        // Assert
        verify(model).setStoredMicrophoneIndex(microphoneIndex);
        verify(model).setSelectedMicrophoneIndex(microphoneIndex);
    }

    @Test
    void testHandleMicrophoneSelectionUpdatesMicrophone() {
        // Arrange
        int microphoneIndex = 1;

        // Act
        microphoneController.handleMicrophoneSelection(microphoneIndex);

        // Assert
        verify(microphone).close();
        verify(microphone).setName(microphoneIndex);
        verify(microphone).open();
    }

    @Test
    void testHandleMicrophoneSelectionStoresModel() {
        // Arrange
        int microphoneIndex = 1;

        // Act
        microphoneController.handleMicrophoneSelection(microphoneIndex);

        // Assert
        verify(modelStorageService).storeModel(model);
    }

    @Test
    void testHandleUpdatesFrequencyHandlers() {
        // Arrange
        double frequency = 440.0;
        double volume = 0.8;

        // Act
        microphoneController.handle(frequency, volume, chordResult);

        // Assert
        verify(harpFrequencyHandler).updateHarpView(frequency, chordResult);
        verify(trainingFrequencyHandler).updateTrainingView(frequency);
    }

    @Test
    void testHandleUpdatesViewWhenActive() {
        // Arrange
        double frequency = 440.0;
        double volume = 0.8;
        when(window.isMicrophoneSettingsViewActive()).thenReturn(true);

        // Act
        microphoneController.handle(frequency, volume, chordResult);

        // Assert - verify that the view was updated with frequency and volume
        verify(microphoneSettingsView).setFrequency(frequency);
        verify(microphoneSettingsView).setVolume(volume);
    }

    @Test
    void testHandleDoesNotUpdateViewWhenInactive() {
        // Arrange
        double frequency = 440.0;
        double volume = 0.8;
        when(window.isMicrophoneSettingsViewActive()).thenReturn(false);

        // Act
        microphoneController.handle(frequency, volume, chordResult);

        // Assert - verify that the view was not updated
        verify(microphoneSettingsView, never()).setFrequency(anyDouble());
        verify(microphoneSettingsView, never()).setVolume(anyDouble());
    }

    @Test
    void testInitAlgorithmListSetsAlgorithmsInView() {
        // Act
        microphoneController.initAlgorithmList();

        // Assert
        verify(microphoneSettingsView).setAlgorithms(supportedAlgorithms);
    }

    @Test
    void testInitAlgorithmListSetsSelectedAlgorithm() {
        // Arrange
        int algorithmIndex = 1;
        when(model.getSelectedAlgorithmIndex()).thenReturn(algorithmIndex);

        // Act
        microphoneController.initAlgorithmList();

        // Assert
        verify(microphoneSettingsView).setSelectedAlgorithm(algorithmIndex);
    }

    @Test
    void testInitMicrophoneListSetsMicrophonesInView() {
        // Act
        microphoneController.initMicrophoneList();

        // Assert
        verify(microphoneSettingsView).setMicrophones(supportedMicrophones);
    }

    @Test
    void testInitMicrophoneListSetsSelectedMicrophone() {
        // Arrange
        int microphoneIndex = 1;
        when(model.getSelectedMicrophoneIndex()).thenReturn(microphoneIndex);

        // Act
        microphoneController.initMicrophoneList();

        // Assert
        verify(microphoneSettingsView).setSelectedMicrophone(microphoneIndex);
    }

    @Test
    void testInitConfidenceListSetsConfidencesInView() {
        // Act
        microphoneController.initConfidenceList();

        // Assert
        verify(microphoneSettingsView).setConfidences(supportedConfidences);
    }

    @Test
    void testInitConfidenceListSetsSelectedConfidence() {
        // Arrange
        int confidenceIndex = 1;
        when(model.getSelectedConfidenceIndex()).thenReturn(confidenceIndex);

        // Act
        microphoneController.initConfidenceList();

        // Assert
        verify(microphoneSettingsView).setSelectedConfidence(confidenceIndex);
    }

    @Test
    void testHandleConfidenceSelectionUpdatesModel() {
        // Arrange
        int confidenceIndex = 1;

        // Act
        microphoneController.handleConfidenceSelection(confidenceIndex);

        // Assert
        verify(model).setStoredConfidenceIndex(confidenceIndex);
        verify(model).setSelectedConfidenceIndex(confidenceIndex);
    }

    @Test
    void testHandleConfidenceSelectionUpdatesMicrophone() {
        // Arrange
        int confidenceIndex = 1;

        // Act
        microphoneController.handleConfidenceSelection(confidenceIndex);

        // Assert
        verify(microphone).setConfidence(confidenceIndex);
    }

    @Test
    void testHandleConfidenceSelectionStoresModel() {
        // Arrange
        int confidenceIndex = 1;

        // Act
        microphoneController.handleConfidenceSelection(confidenceIndex);

        // Assert
        verify(modelStorageService).storeModel(model);
    }
}
