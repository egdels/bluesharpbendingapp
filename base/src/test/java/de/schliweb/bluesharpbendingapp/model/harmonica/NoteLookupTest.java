package de.schliweb.bluesharpbendingapp.model.harmonica;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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


    @ParameterizedTest
    @CsvSource({
            "A4, 440.0",      // Standard pitch note
            "C8, 4186.01",    // Highest note
            "C0, 16.35"       // Lowest note
    })
    void testGetNoteFrequency(String noteName, double expectedFrequency)
    {
        // Arrange

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
        assertTrue(exception.getMessage().contains("Invalid note name"));
    }

    @Test
    void testGetNoteFrequencyWithNullName() {
        // Arrange

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> NoteLookup.getNoteFrequency(null)
        );
        assertTrue(exception.getMessage().contains("Invalid note name"));
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
        assertTrue(exception.getMessage().contains("Invalid note name"));
    }
}