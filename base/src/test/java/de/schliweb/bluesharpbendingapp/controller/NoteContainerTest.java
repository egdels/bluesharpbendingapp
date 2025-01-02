package de.schliweb.bluesharpbendingapp.controller;

import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;
import de.schliweb.bluesharpbendingapp.view.HarpView;
import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        // Mocks erstellen
        Harmonica mockHarmonica = mock(Harmonica.class);
        HarpView mockHarpView = mock(HarpView.class);
        mockHarpViewElement = mock(HarpViewNoteElement.class);

        // Mock-Verhalten definieren
        when(mockHarmonica.getNoteFrequencyMinimum(CHANNEL, NOTE)).thenReturn(MIN_FREQUENCY);
        when(mockHarmonica.getNoteFrequencyMaximum(CHANNEL, NOTE)).thenReturn(MAX_FREQUENCY);
        when(mockHarpView.getHarpViewElement(CHANNEL, NOTE)).thenReturn(mockHarpViewElement);

        // NoteContainer erstellen
        noteContainer = new NoteContainer(CHANNEL, NOTE, NOTE_NAME, mockHarmonica, mockHarpView);
    }

    @Test
    void testUpdateCalled_WhenFrequencyInRange() {
        // Arrange
        double validFrequency = 150.0; // Innerhalb des Bereichs
        noteContainer.setFrequencyToHandle(validFrequency);

        // Act
        noteContainer.run();

        // Assert
        verify(mockHarpViewElement, times(1)).update(anyDouble()); // Update einmal
        verify(mockHarpViewElement, never()).clear(); // Clear nicht aufgerufen
    }

    @Test
    void testClearCalled_WhenFrequencyOutOfRange() throws InterruptedException {
        // Arrange
        double outOfRangeFrequency = 250.0; // Außerhalb des Bereichs
        double inRangeFrequency = 150.0;  // Innerhalb des Bereichs

        // Frequenz zuerst im Bereich
        noteContainer.setFrequencyToHandle(inRangeFrequency);
        noteContainer.run();

        // Frequenz verlässt den Bereich
        noteContainer.setFrequencyToHandle(outOfRangeFrequency);

        // Act
        noteContainer.run(); // Run aufrufen, um in den Else-Zweig zu gehen
        Thread.sleep(200); // Auf Scheduler warten

        // Assert
        verify(mockHarpViewElement, times(1)).update(anyDouble()); // Update wurde zuerst aufgerufen
        verify(mockHarpViewElement, times(1)).clear(); // Clear einmalig aufgerufen
    }

    @Test
    void testClearNotCalled_WhenFrequencyRemainsOutOfRange() throws InterruptedException {
        // Arrange
        double outOfRangeFrequency = 250.0; // Frequenz außerhalb

        // Frequenz außerhalb setzen
        noteContainer.setFrequencyToHandle(outOfRangeFrequency);

        // Act
        noteContainer.run(); // Erster Run
        noteContainer.run(); // Wiederholter Run
        Thread.sleep(200); // Auf Scheduler warten

        // Assert
        verify(mockHarpViewElement, times(0)).clear(); // Clear nur einmalig
    }

    @Test
    void testUpdateCalledAgain_WhenFrequencyBackInRange() {
        // Arrange
        double outOfRangeFrequency = 250.0; // Außerhalb des Bereichs
        double inRangeFrequency = 150.0;   // Innerhalb des Bereichs

        // Frequenz außerhalb setzen
        noteContainer.setFrequencyToHandle(outOfRangeFrequency);
        noteContainer.run();

        // Frequenz zurück in den Bereich setzen
        noteContainer.setFrequencyToHandle(inRangeFrequency);

        // Act
        noteContainer.run();

        // Assert
        verify(mockHarpViewElement, times(0)).clear(); // Clear nicht aufgerufen
        verify(mockHarpViewElement, times(1)).update(anyDouble()); // Update wieder aufgerufen
    }
}