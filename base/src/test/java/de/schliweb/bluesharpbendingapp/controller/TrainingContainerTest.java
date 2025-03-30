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
import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;
import de.schliweb.bluesharpbendingapp.model.training.Training;
import de.schliweb.bluesharpbendingapp.utils.NoteUtils;
import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;
import de.schliweb.bluesharpbendingapp.view.TrainingView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;
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

        trainingContainer.setLockAllThreads(true);

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
    void testRunToBeClearedTrue() {
        // Arrange
        Training trainingMock = mock(Training.class);
        TrainingView viewMock = mock(TrainingView.class);

        HarpViewNoteElement harpElementMock = mock(HarpViewNoteElement.class);
        when(viewMock.getActualHarpViewElement()).thenReturn(harpElementMock);

        TrainingContainer trainingContainer = new TrainingContainer(trainingMock, viewMock);
        trainingContainer.toBeCleared.set(true); // Zustand festlegen

        // Act
        trainingContainer.run();

        // Assert: Awaitility stellt sicher, dass die Mocks korrekt aufgerufen werden
        await()
                .atMost(1000, MILLISECONDS) // max. 1000ms warten
                .untilAsserted(() -> {
                    verify(harpElementMock, never()).update(anyDouble()); // update(...) nie aufgerufen
                    verify(harpElementMock, times(1)).clear();            // clear() einmal aufgerufen
                    verify(trainingMock, never()).success();              // success() nie aufgerufen
                    verify(trainingMock, never()).nextNote();             // nextNote() nie aufgerufen
                });
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