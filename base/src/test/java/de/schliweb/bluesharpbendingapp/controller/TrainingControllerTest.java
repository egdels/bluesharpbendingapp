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
import de.schliweb.bluesharpbendingapp.model.ModelStorageService;
import de.schliweb.bluesharpbendingapp.model.training.AbstractTraining;
import de.schliweb.bluesharpbendingapp.model.training.Training;
import de.schliweb.bluesharpbendingapp.view.MainWindow;
import de.schliweb.bluesharpbendingapp.view.TrainingView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the TrainingController class to validate its behavior
 * under various conditions and scenarios.
 */
class TrainingControllerTest {

    private TrainingController trainingController;
    private MainModel model;
    private ModelStorageService modelStorageService;
    private MainWindow window;
    private ExecutorService executorService;
    private TrainingView trainingView;
    private Training training;
    private MockedStatic<AbstractTraining> abstractTrainingMock;

    @BeforeEach
    void setup() {
        // Mock dependencies
        model = mock(MainModel.class);
        modelStorageService = mock(ModelStorageService.class);
        window = mock(MainWindow.class);
        executorService = mock(ExecutorService.class);
        trainingView = mock(TrainingView.class);
        training = mock(Training.class);

        // Setup common mock behavior
        when(modelStorageService.readModel()).thenReturn(model);
        when(window.isTrainingViewActive()).thenReturn(true);
        when(window.getTrainingView()).thenReturn(trainingView);

        // Mock static method in AbstractTraining
        abstractTrainingMock = mockStatic(AbstractTraining.class);
        when(AbstractTraining.create(anyInt(), anyInt())).thenReturn(training);
        when(AbstractTraining.getSupportedTrainings()).thenReturn(new String[]{"Training1", "Training2"});
        when(AbstractTraining.getSupportedPrecisions()).thenReturn(new String[]{"10", "20", "30"});

        // Create controller
        trainingController = new TrainingController(model, modelStorageService, window, executorService);
    }

    @Test
    void testHandleTrainingSelectionUpdatesModel() {
        // Arrange
        int trainingIndex = 1;

        // Act
        trainingController.handleTrainingSelection(trainingIndex);

        // Assert
        verify(model).setStoredTrainingIndex(trainingIndex);
        verify(model).setSelectedTrainingIndex(trainingIndex);
        verify(modelStorageService).storeModel(model);
    }

    @Test
    void testHandleTrainingSelectionCreatesTraining() {
        // Arrange
        int trainingIndex = 1;
        int keyIndex = 2;
        when(model.getStoredKeyIndex()).thenReturn(keyIndex);

        // Act
        trainingController.handleTrainingSelection(trainingIndex);

        // Assert
        verify(AbstractTraining.class);
        AbstractTraining.create(keyIndex, trainingIndex);
    }

    @Test
    void testHandleTrainingStartStartsTraining() {
        // Act
        trainingController.handleTrainingStart();

        // Assert
        verify(training).start();
    }

    @Test
    void testHandleTrainingStartReinitializesContainer() {
        // Act
        trainingController.handleTrainingStart();

        // Assert
        verify(trainingView).initTrainingContainer(any(TrainingContainer.class));
    }

    @Test
    void testHandleTrainingStopStopsTraining() {
        // Act
        trainingController.handleTrainingStop();

        // Assert
        verify(training).stop();
    }

    @Test
    void testHandlePrecisionSelectionUpdatesModel() {
        // Arrange
        int precisionIndex = 1;

        // Act
        trainingController.handlePrecisionSelection(precisionIndex);

        // Assert
        verify(model).setStoredPrecisionIndex(precisionIndex);
        verify(model).setSelectedPrecisionIndex(precisionIndex);
        verify(modelStorageService).storeModel(model);
    }

    @Test
    void testHandlePrecisionSelectionSetsPrecision() {
        // Arrange
        int precisionIndex = 1;
        String[] precisions = {"10", "20", "30"};
        when(AbstractTraining.getSupportedPrecisions()).thenReturn(precisions);

        // Act
        trainingController.handlePrecisionSelection(precisionIndex);

        // Assert
        verify(AbstractTraining.class);
        AbstractTraining.setPrecision(20); // 20 is the value at index 1
    }

    @Test
    void testUpdateTrainingViewSubmitsToExecutor() {
        // Arrange
        double frequency = 440.0;
        when(executorService.isShutdown()).thenReturn(false);
        when(window.isTrainingViewActive()).thenReturn(true);

        // Initialize the training container first
        trainingController.initTrainingContainer();

        // Act
        trainingController.updateTrainingView(frequency);

        // Assert
        ArgumentCaptor<TrainingContainer> containerCaptor = ArgumentCaptor.forClass(TrainingContainer.class);
        verify(executorService).submit(containerCaptor.capture());

        TrainingContainer capturedContainer = containerCaptor.getValue();
        assertNotNull(capturedContainer);
    }

    @Test
    void testUpdateTrainingViewDoesNothingWhenViewInactive() {
        // Arrange
        double frequency = 440.0;
        when(window.isTrainingViewActive()).thenReturn(false);

        // Act
        trainingController.updateTrainingView(frequency);

        // Assert
        verify(executorService, never()).submit(any(TrainingContainer.class));
    }

    @Test
    void testUpdateTrainingViewDoesNothingWhenExecutorShutdown() {
        // Arrange
        double frequency = 440.0;
        when(executorService.isShutdown()).thenReturn(true);

        // Act
        trainingController.updateTrainingView(frequency);

        // Assert
        verify(executorService, never()).submit(any(TrainingContainer.class));
    }

    @Test
    void testInitTrainingListSetsTrainingsInView() {
        // Arrange
        String[] trainings = {"Training1", "Training2"};
        when(AbstractTraining.getSupportedTrainings()).thenReturn(trainings);

        // Act
        trainingController.initTrainingList();

        // Assert
        verify(trainingView).setTrainings(trainings);
    }

    @Test
    void testInitTrainingListSetsSelectedTraining() {
        // Arrange
        int selectedIndex = 1;
        when(model.getSelectedTrainingIndex()).thenReturn(selectedIndex);

        // Act
        trainingController.initTrainingList();

        // Assert
        verify(trainingView).setSelectedTraining(selectedIndex);
    }

    @Test
    void testInitPrecisionListSetsPrecisionsInView() {
        // Arrange
        String[] precisions = {"10", "20", "30"};
        when(AbstractTraining.getSupportedPrecisions()).thenReturn(precisions);

        // Act
        trainingController.initPrecisionList();

        // Assert
        verify(trainingView).setPrecisions(precisions);
    }

    @Test
    void testInitPrecisionListSetsSelectedPrecision() {
        // Arrange
        int selectedIndex = 2;
        when(model.getSelectedPrecisionIndex()).thenReturn(selectedIndex);

        // Act
        trainingController.initPrecisionList();

        // Assert
        verify(trainingView).setSelectedPrecision(selectedIndex);
    }

    @AfterEach
    void tearDown() {
        // Close the static mock to prevent leakage between tests
        if (abstractTrainingMock != null) {
            abstractTrainingMock.close();
        }
    }
}
