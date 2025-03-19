package de.schliweb.bluesharpbendingapp.controller;

import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.microphone.Microphone;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import de.schliweb.bluesharpbendingapp.view.MainWindow;
import de.schliweb.bluesharpbendingapp.model.MainModel;

import static org.mockito.Mockito.*;


 class MainControllerTest {

    private MainController mainController;
    private ThreadPoolExecutor mockExecutor;
    private MainModel mockMainModel;
    private MainWindow mockMainWindow;

     @BeforeEach
    void setUp() {
        // Create a mock ThreadPoolExecutor
        mockExecutor = mock(ThreadPoolExecutor.class);

        mockMainWindow = mock(MainWindow.class);
        mockMainModel = mock(MainModel.class);

        // Create a mock for Microphone
        Microphone mockMicrophone = mock(Microphone.class);
        when(mockMainModel.getMicrophone()).thenReturn(mockMicrophone);

        // Configure additional Mockito stubs or mocks
        when(mockMainModel.getStoredAlgorithmIndex()).thenReturn(1);
        when(mockMainModel.getStoredMicrophoneIndex()).thenReturn(1);
        when(mockMainModel.getStoredConfidenceIndex()).thenReturn(1);
        when(mockMainModel.getHarmonica()).thenReturn(AbstractHarmonica.create(0, 0));
        when(mockMainWindow.isHarpViewActive()).thenReturn(true);
        when(mockMainWindow.getHarpView()).thenReturn(mock(de.schliweb.bluesharpbendingapp.view.HarpView.class));

        // Create MainController
        mainController = new MainController(mockMainWindow, mockMainModel);
        mainController.setExecutorService(mockExecutor);
        mainController.initNotes();
    }

    @Test
    void testUpdateHarpView_TaskSubmission() {
        // Simulate that the executor is not shut down
        when(mockExecutor.isShutdown()).thenReturn(false);

        // Call the method
        mainController.handle(440.0, 0.8);

        // Verify that a task is submitted to the executor
        verify(mockExecutor, atLeastOnce()).submit(any(Runnable.class));
    }

    @Test
    void testUpdateHarpView_RejectsTaskWhenShutdown() {
        // Simulate that the executor has entered the "shutting down" state
        when(mockExecutor.isShutdown()).thenReturn(true);

        // Ensure that submit() throws a RejectedExecutionException
        doThrow(new RejectedExecutionException()).when(mockExecutor).submit(any(Runnable.class));

        // Check that no exception is thrown by the handle method
        Assertions.assertDoesNotThrow(() -> mainController.handle(440.0, 0.8));


    }

     @Test
     void testHandle_InactiveHarpView() {
         // Simulate that HarpView is inactive
         when(mockMainWindow.isHarpViewActive()).thenReturn(false);

         // Call the handle method
         mainController.handle(440.0, 0.8);

         // Verify that no tasks are submitted to the executor
         verify(mockExecutor, never()).submit(any(Runnable.class));
     }

}

