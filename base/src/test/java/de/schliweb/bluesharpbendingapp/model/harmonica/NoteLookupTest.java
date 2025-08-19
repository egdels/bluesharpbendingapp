package de.schliweb.bluesharpbendingapp.model.harmonica;
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
    @CsvSource({"A4, 440.0",      // Standard pitch note
            "C8, 4186.01",    // Highest note
            "C0, 16.35"       // Lowest note
    })
    void testGetNoteFrequency(String noteName, double expectedFrequency) {
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
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> NoteLookup.getNoteFrequency(invalidNoteName));
        assertTrue(exception.getMessage().contains("Invalid note name"));
    }

    @Test
    void testGetNoteFrequencyWithNullName() {
        // Arrange

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> NoteLookup.getNoteFrequency(null));
        assertTrue(exception.getMessage().contains("Invalid note name"));
    }

    @Test
    void testGetNoteFrequencyWithEmptyString() {
        // Arrange
        String emptyNoteName = "";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> NoteLookup.getNoteFrequency(emptyNoteName));
        assertTrue(exception.getMessage().contains("Invalid note name"));
    }
    @ParameterizedTest
    @CsvSource({
            "C4, 261.63",   // Middle C
            "D4, 293.66",   // D above middle C
            "E4, 329.63",   // E above middle C
            "F4, 349.23",   // F above middle C
            "G4, 392.00",   // G above middle C
            "A4, 440.00",   // A above middle C
            "B4, 493.88"    // B above middle C
    })
    void testGetNoteFrequencyInMiddleOctave(String noteName, double expectedFrequency) {
        double frequency = NoteLookup.getNoteFrequency(noteName);
        assertEquals(expectedFrequency, frequency, 0.01);
    }
    @ParameterizedTest
    @CsvSource({
            "C#4, 277.18",
            "Db4, 277.18",
            "F#5, 739.99",
            "Gb5, 739.99"
    })
    void testGetNoteFrequencyWithAccidentals(String noteName, double expectedFrequency) {
        double frequency = NoteLookup.getNoteFrequency(noteName);
        assertEquals(expectedFrequency, frequency, 0.01);
    }

    @Test
    void testGetNoteFrequencyWithWhitespaceAndLowercase() {
        assertEquals(440.0, NoteLookup.getNoteFrequency(" a4 "), 0.01);
        assertEquals(261.63, NoteLookup.getNoteFrequency("c4"), 0.01);
    }

    @Test
    void testGetNoteFrequencyWithExtremeOctaves() {
        assertThrows(IllegalArgumentException.class, () -> NoteLookup.getNoteFrequency("C-1"));
        assertThrows(IllegalArgumentException.class, () -> NoteLookup.getNoteFrequency("C10"));
    }

    @Test
    void testSetConcertPitchAffectsOtherNotes() {
        NoteLookup.setConcertPitch(432);
        double freq = NoteLookup.getNoteFrequency("C4");
        assertEquals(256.87, freq, 0.01);
        NoteLookup.setConcertPitch(440); // Reset
    }
}