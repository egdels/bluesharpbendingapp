package de.schliweb.bluesharpbendingapp.controller;

import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;
import de.schliweb.bluesharpbendingapp.model.training.Training;
import de.schliweb.bluesharpbendingapp.utils.NoteUtils;
import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;
import de.schliweb.bluesharpbendingapp.view.TrainingView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * Unit tests for the TrainingContainer class to validate its behavior
 * under various conditions and scenarios.
 */
class TrainingContainerTest {

    /**
     * Tests that the `run` method does not proceed if `lockAllThreads` is true.
     */
    @Test
    void testRunDoesNothingWhenLockAllThreadsIsTrue() {
        Training trainingMock = mock(Training.class);
        TrainingView viewMock = mock(TrainingView.class);

        HarpViewNoteElement harpElementMock = mock(HarpViewNoteElement.class);
        when(viewMock.getActualHarpViewElement()).thenReturn(harpElementMock);

        TrainingContainer trainingContainer = new TrainingContainer(trainingMock, viewMock);

        // Set static LockAllThreads to true
        TrainingContainer.lockAllThreads = true;

        trainingContainer.run();

        // Verify that no interactions took place due to the lock
        verify(harpElementMock, never()).update(anyDouble());
        verify(harpElementMock, never()).clear();
        verify(trainingMock, never()).success();
        verify(trainingMock, never()).nextNote();
    }

    /**
     * Tests that the `run` method handles frequency change if training is running and note is active.
     */
    @Test
    void testRunHandlesFrequencyChange() {
        Training trainingMock = mock(Training.class);
        TrainingView viewMock = mock(TrainingView.class);
        when(trainingMock.isRunning()).thenReturn(true);
        when(trainingMock.isNoteActive(anyDouble())).thenReturn(true);

        HarpViewNoteElement harpElementMock = mock(HarpViewNoteElement.class);
        when(viewMock.getActualHarpViewElement()).thenReturn(harpElementMock);
        when(trainingMock.getActualNote()).thenReturn("A");

        TrainingContainer trainingContainer = new TrainingContainer(trainingMock, viewMock);
        trainingContainer.setFrequencyToHandle(440.0);

        mockStatic(NoteLookup.class);
        mockStatic(NoteUtils.class);
        when(NoteLookup.getNoteFrequency(anyString())).thenReturn(440.0);
        when(NoteUtils.getCents(anyDouble(), anyDouble())).thenReturn(5.0);

        trainingContainer.run();

        verify(harpElementMock, times(1)).update(anyDouble());
    }

    /**
     * Tests the behavior when `toNextNote` is true.
     */
    @Test
    void testRunToNextNoteTrue() {
        Training trainingMock = mock(Training.class);
        TrainingView viewMock = mock(TrainingView.class);

        TrainingContainer trainingContainer = new TrainingContainer(trainingMock, viewMock);
        trainingContainer.toNextNote.set(true);

        trainingContainer.run();

        verify(trainingMock, times(1)).success();
        verify(trainingMock, times(1)).nextNote();
    }

    /**
     * Tests the behavior when `toBeCleared` is true.
     */
    @Test
    void testRunToBeClearedTrue() throws InterruptedException {
        Training trainingMock = mock(Training.class);
        TrainingView viewMock = mock(TrainingView.class);

        HarpViewNoteElement harpElementMock = mock(HarpViewNoteElement.class);
        when(viewMock.getActualHarpViewElement()).thenReturn(harpElementMock);

        TrainingContainer trainingContainer = new TrainingContainer(trainingMock, viewMock);
        trainingContainer.toBeCleared.set(true);

        trainingContainer.run();


        Thread.sleep(1000); // Wait for scheduler


        verify(harpElementMock, never()).update(anyDouble());
        verify(harpElementMock, times(1)).clear();
        verify(trainingMock, never()).success();
        verify(trainingMock, never()).nextNote();
    }

    /**
     * Tests that `run` does not proceed when training is not running.
     */
    @Test
    void testRunDoesNothingWhenTrainingNotRunning() {
        Training trainingMock = mock(Training.class);
        TrainingView viewMock = mock(TrainingView.class);
        when(trainingMock.isRunning()).thenReturn(false);

        TrainingContainer trainingContainer = new TrainingContainer(trainingMock, viewMock);
        trainingContainer.run();

        verify(trainingMock, never()).isNoteActive(anyDouble());
    }

    @AfterEach
    void tearDown() {
        // Schließt statische Mocks und stellt den ursprünglichen Zustand wieder her
        clearAllCaches();
    }
}