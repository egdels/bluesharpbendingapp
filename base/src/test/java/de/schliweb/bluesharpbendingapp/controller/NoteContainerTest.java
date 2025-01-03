package de.schliweb.bluesharpbendingapp.controller;

import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;
import de.schliweb.bluesharpbendingapp.view.HarpView;
import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class NoteContainerTest {
    private static final int CHANNEL = 1;
    private static final int NOTE = 5;
    private static final String NOTE_NAME = "C";
    private static final double MIN_FREQUENCY = 100.0;
    private static final double MAX_FREQUENCY = 200.0;
    private HarpViewNoteElement mockHarpViewElement;
    private NoteContainer noteContainer;

    @BeforeEach
    void setUp() {
        // Create mocks
        Harmonica mockHarmonica = mock(Harmonica.class);
        HarpView mockHarpView = mock(HarpView.class);
        mockHarpViewElement = mock(HarpViewNoteElement.class);

        // Define mock behaviors
        when(mockHarmonica.getNoteFrequencyMinimum(CHANNEL, NOTE)).thenReturn(MIN_FREQUENCY);
        when(mockHarmonica.getNoteFrequencyMaximum(CHANNEL, NOTE)).thenReturn(MAX_FREQUENCY);
        when(mockHarpView.getHarpViewElement(CHANNEL, NOTE)).thenReturn(mockHarpViewElement);

        // Create NoteContainer
        noteContainer = new NoteContainer(CHANNEL, NOTE, NOTE_NAME, mockHarmonica, mockHarpView);
    }

    @Test
    void testUpdateCalled_WhenFrequencyInRange() {
        // Arrange
        double validFrequency = 150.0; // Within range
        noteContainer.setFrequencyToHandle(validFrequency);

        // Act
        noteContainer.run();

        // Assert
        verify(mockHarpViewElement, times(1)).update(anyDouble()); // Update called once
        verify(mockHarpViewElement, never()).clear(); // Clear not called
    }

    @Test
    void testClearCleanNotCalled_WhenFrequencyOutOfRange() throws InterruptedException {
        // Arrange
        double outOfRangeFrequency = 250.0; // Out of range
        double inRangeFrequency = 150.0;  // Within range

        // First, set frequency within range
        noteContainer.setFrequencyToHandle(inRangeFrequency);
        noteContainer.run();
        Thread.sleep(1000); // Wait for scheduler

        // Then set frequency out of range
        noteContainer.setFrequencyToHandle(outOfRangeFrequency);

        // Act
        noteContainer.run(); // Trigger run to enter "else" condition
        Thread.sleep(1000); // Wait for scheduler

        // Assert
        verify(mockHarpViewElement, times(1)).update(anyDouble()); // Update called first
        verify(mockHarpViewElement, times(1)).clear(); // Clear called once
    }

    @Test
    void testClearNotCalled_WhenFrequencyRemainsOutOfRange() throws InterruptedException {
        // Arrange
        double outOfRangeFrequency = 250.0; // Frequency out of range

        // Set frequency out of range
        noteContainer.setFrequencyToHandle(outOfRangeFrequency);

        // Act
        noteContainer.run(); // First run
        Thread.sleep(1000); // Wait for scheduler

        // Assert
        verify(mockHarpViewElement, times(0)).clear(); // Clear not called again
    }

    @Test
    void testUpdateCalledAgain_WhenFrequencyBackInRange() {
        // Arrange
        double outOfRangeFrequency = 250.0; // Out of range
        double inRangeFrequency = 150.0;   // Within range

        // Set frequency out of range
        noteContainer.setFrequencyToHandle(outOfRangeFrequency);
        noteContainer.run();

        // Set frequency back in range
        noteContainer.setFrequencyToHandle(inRangeFrequency);

        // Act
        noteContainer.run();

        // Assert
        verify(mockHarpViewElement, times(0)).clear(); // Clear not called
        verify(mockHarpViewElement, times(1)).update(anyDouble()); // Update called again
    }

    @Test
    void testNegativeCentsWhenInverseHandlingTrue() {
        // Arrange
        double validFrequency = 150.0; // Within range
        Harmonica mockHarmonicaWithInverse = mock(Harmonica.class);
        HarpView mockHarpViewWithInverse = mock(HarpView.class);
        HarpViewNoteElement mockHarpViewElementWithInverse = mock(HarpViewNoteElement.class);

        when(mockHarmonicaWithInverse.getNoteFrequencyMinimum(CHANNEL, NOTE)).thenReturn(MIN_FREQUENCY);
        when(mockHarmonicaWithInverse.getNoteFrequencyMaximum(CHANNEL, NOTE)).thenReturn(MAX_FREQUENCY);
        when(mockHarpViewWithInverse.getHarpViewElement(CHANNEL, NOTE)).thenReturn(mockHarpViewElementWithInverse);
        when(mockHarmonicaWithInverse.getCentsNote(CHANNEL, NOTE, validFrequency)).thenReturn(50.0);

        NoteContainer inverseNoteContainer = new NoteContainer(
                CHANNEL, NOTE, NOTE_NAME, mockHarmonicaWithInverse, mockHarpViewWithInverse, true
        );

        inverseNoteContainer.setFrequencyToHandle(validFrequency);

        // Act
        inverseNoteContainer.run();

        // Assert
        verify(mockHarpViewElementWithInverse, times(1)).update(-50.0);
        verify(mockHarpViewElementWithInverse, never()).clear();
    }

    @Test
    void testSetFrequencyToHandleUpdatesFrequency() {
        // Arrange
        double expectedFrequency = 123.4;

        // Act
        noteContainer.setFrequencyToHandle(expectedFrequency);

        // Assert
        noteContainer.run();
        assertEquals(expectedFrequency, noteContainer.frequencyToHandle);
    }


    @Test
    void testNoUpdateOrClearCalledForInvalidFrequency() {
        // Arrange
        double invalidFrequency = 300.0; // Out of range

        noteContainer.setFrequencyToHandle(invalidFrequency);

        // Act
        noteContainer.run();

        // Assert
        verify(mockHarpViewElement, never()).update(anyDouble());
        verify(mockHarpViewElement, never()).clear();
    }
}