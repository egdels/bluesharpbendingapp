package de.schliweb.bluesharpbendingapp.model.harmonica;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoteLookupTest {

    /**
     * Tests the getNoteFrequency method of the NoteLookup class.
     * <p>
     * The method calculates the frequency of a musical note given its name.
     * It accounts for different octaves and follows the standard concert pitch
     * frequency (440Hz by default).
     */

    @BeforeAll
    static void init() {
        NoteLookup.setConcertPitch(440);
    }

    @Test
    void testGetNoteFrequencyForStandardNote() {
        // Arrange
        String noteName = "A4"; // Standard pitch note
        double expectedFrequency = 440.0;

        // Act
        double frequency = NoteLookup.getNoteFrequency(noteName);

        // Assert
        assertEquals(expectedFrequency, frequency, 0.01);
    }

    @Test
    void testGetNoteFrequencyForHigherOctave() {
        // Arrange
        String noteName = "C8"; // Highest standard piano note
        double expectedFrequency = 4186.01;

        // Act
        double frequency = NoteLookup.getNoteFrequency(noteName);

        // Assert
        assertEquals(expectedFrequency, frequency, 0.01);
    }

    @Test
    void testGetNoteFrequencyForLowerOctave() {
        // Arrange
        String noteName = "C0"; // Lowest standard piano note
        double expectedFrequency = 16.35;

        // Act
        double frequency = NoteLookup.getNoteFrequency(noteName);

        // Assert
        assertEquals(expectedFrequency, frequency, 0.01);
    }

    @Test
    void testGetNoteFrequencyForDifferentConcertPitch() {
        // Arrange
        String noteName = "A4";
        NoteLookup.setConcertPitch(442); // Adjust concert pitch to 442Hz
        double expectedFrequency = 442.0;

        // Act
        double frequency = NoteLookup.getNoteFrequency(noteName);

        // Assert
        assertEquals(expectedFrequency, frequency, 0.01);
    }

    @Test
    void testGetNoteFrequencyWithInvalidNoteName() {
        // Arrange
        String invalidNoteName = "InvalidNote";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> NoteLookup.getNoteFrequency(invalidNoteName)
        );
        assertTrue(exception.getMessage().contains("Ungültiger Notenname"));
    }

    @Test
    void testGetNoteFrequencyWithNullName() {
        // Arrange

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> NoteLookup.getNoteFrequency(null)
        );
        assertTrue(exception.getMessage().contains("Ungültiger Notenname"));
    }

    @Test
    void testGetNoteFrequencyWithEmptyString() {
        // Arrange
        String emptyNoteName = "";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> NoteLookup.getNoteFrequency(emptyNoteName)
        );
        assertTrue(exception.getMessage().contains("Ungültiger Notenname"));
    }
}