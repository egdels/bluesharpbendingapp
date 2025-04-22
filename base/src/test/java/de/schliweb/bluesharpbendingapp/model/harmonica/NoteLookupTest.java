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
}