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

import de.schliweb.bluesharpbendingapp.utils.ChordDetectionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link ChordAndDetectionResultComparator}.
 * Tests the comparison functionality between ChordHarmonica and ChordDetectionResult objects.
 */
@DisplayName("ChordAndDetectionResultComparator Tests")
class ChordAndDetectionResultComparatorTest {

    private ChordAndDetectionResultComparator comparator;

    @BeforeEach
    void setUp() {
        // Initialize the comparator before each test
        comparator = new ChordAndDetectionResultComparator();
        // Reset the concert pitch to its default value to ensure test isolation
        NoteLookup.setConcertPitch(440);
    }

    /**
     * Helper method to create a ChordHarmonica with specific tones.
     * This method creates a mock Harmonica that returns the specified tones
     * when getNoteFrequency is called.
     *
     * @param tones the list of tones (frequencies) to include in the chord
     * @return a ChordHarmonica with the specified tones
     */
    private ChordHarmonica createChordHarmonicaWithTones(List<Double> tones) {
        Harmonica mockHarmonica = Mockito.mock(Harmonica.class);
        List<Integer> channels = new ArrayList<>();

        for (int i = 0; i < tones.size(); i++) {
            channels.add(i + 1); // Use channel numbers 1, 2, 3, etc.
            when(mockHarmonica.getNoteFrequency(i + 1, 0)).thenReturn(tones.get(i));
        }

        return new ChordHarmonica(mockHarmonica, channels, 0);
    }

    @Test
    @DisplayName("Compare chords with different note counts")
    void testCompareWithDifferentNoteCount() {
        // Arrange
        // Create chords with different number of notes
        ChordHarmonica chordHarmonica1 = createChordHarmonicaWithTones(Arrays.asList(440.0, 523.25)); // A4, C5 - 2 notes
        ChordHarmonica chordHarmonica2 = createChordHarmonicaWithTones(Arrays.asList(440.0, 523.25, 659.25)); // A4, C5, E5 - 3 notes

        // Act
        // The chord with fewer notes should be "less than" the chord with more notes
        int result = comparator.compare(chordHarmonica1, chordHarmonica2);

        // Assert
        assertTrue(result < 0, "ChordHarmonica with 2 notes should be less than chord with 3 notes");

        // Act - Test the reverse comparison
        result = comparator.compare(chordHarmonica2, chordHarmonica1);

        // Assert
        assertTrue(result > 0, "ChordHarmonica with 3 notes should be greater than chord with 2 notes");
    }

    @Test
    @DisplayName("Compare chords with same note count and equal frequencies")
    void testCompareWithSameNoteCountAndEqualFrequencies() {
        // Arrange
        // Create chords with the same number of notes and equal frequencies
        ChordHarmonica chordHarmonica1 = createChordHarmonicaWithTones(Arrays.asList(440.0, 523.25)); // A4, C5
        ChordHarmonica chordHarmonica2 = createChordHarmonicaWithTones(Arrays.asList(440.0, 523.25)); // A4, C5

        // Act
        // The chords should be equal
        int result = comparator.compare(chordHarmonica1, chordHarmonica2);

        // Assert
        assertEquals(0, result, "Chords with same frequencies should be equal");
    }

    /**
     * Tests that chords with the same number of notes and frequencies within the
     * musical tolerance (±50 cents) are considered equal by the comparator.
     * <p>
     * This test focuses specifically on the basic case of two-note chords with
     * frequencies that differ by exactly 50 cents (the boundary of tolerance).
     * For more comprehensive testing with multiple notes and different tolerance levels,
     * see {@link #testFrequencyComparisonWithMultipleNotes()}.
     */
    @Test
    @DisplayName("Compare chords with same note count and frequencies at tolerance boundary")
    void testCompareWithSameNoteCountAndFrequenciesAtToleranceBoundary() {
        // Arrange
        // Create reference chord with two notes (A4, C5)
        ChordHarmonica referenceChord = createChordHarmonicaWithTones(
                Arrays.asList(440.0, 523.25)); // A4, C5

        // Create chord with frequencies exactly 50 cents higher (at the tolerance boundary)
        // 440.0 Hz * 1.029302 ≈ 452.89 Hz (50 cents higher than A4)
        // 523.25 Hz * 1.029302 ≈ 538.55 Hz (50 cents higher than C5)
        ChordHarmonica boundaryChord = createChordHarmonicaWithTones(
                Arrays.asList(452.89, 538.55)); // A4+50cents, C5+50cents

        // Act
        int result = comparator.compare(referenceChord, boundaryChord);

        // Assert
        assertEquals(0, result, 
                "Chords with frequencies at the 50 cents tolerance boundary should be equal");
    }

    @Test
    @DisplayName("Compare chords with same note count but different frequencies")
    void testCompareWithSameNoteCountAndDifferentFrequencies() {
        // Arrange
        // Create chords with the same number of notes but different frequencies
        ChordHarmonica chordHarmonica1 = createChordHarmonicaWithTones(Arrays.asList(440.0, 523.25)); // A4, C5
        ChordHarmonica chordHarmonica2 = createChordHarmonicaWithTones(Arrays.asList(466.16, 587.33)); // A#4/Bb4, D5

        // Act
        // The chord with lower frequency sum should be "less than" the chord with higher frequency sum
        int result = comparator.compare(chordHarmonica1, chordHarmonica2);

        // Assert
        assertTrue(result < 0, "ChordHarmonica with lower frequency sum should be less than chord with higher frequency sum");

        // Act - Test the reverse comparison
        result = comparator.compare(chordHarmonica2, chordHarmonica1);

        // Assert
        assertTrue(result > 0, "ChordHarmonica with higher frequency sum should be greater than chord with lower frequency sum");
    }

    @Test
    @DisplayName("Compare ChordHarmonica with ChordDetectionResult having same frequencies")
    void testCompareWithChordAndChordDetectionResult() {
        // Arrange
        // Create a ChordHarmonica and a ChordDetectionResult with the same frequencies
        ChordHarmonica chordHarmonica = createChordHarmonicaWithTones(Arrays.asList(440.0, 523.25)); // A4, C5
        ChordDetectionResult detectionResult = new ChordDetectionResult(Arrays.asList(440.0, 523.25), 0.95);

        // Act
        int result = comparator.compare(chordHarmonica, detectionResult);

        // Assert
        assertEquals(0, result, "ChordHarmonica and ChordDetectionResult with same frequencies should be equal");

        // Act - Test the reverse comparison
        result = comparator.compare(detectionResult, chordHarmonica);

        // Assert
        assertEquals(0, result, "ChordDetectionResult and ChordHarmonica with same frequencies should be equal");
    }

    @Test
    @DisplayName("Compare with invalid object types should throw IllegalArgumentException")
    void testCompareWithInvalidObjectTypes() {
        // Arrange
        // Create a ChordHarmonica and an object of invalid type
        ChordHarmonica chordHarmonica = createChordHarmonicaWithTones(Arrays.asList(440.0, 523.25));
        String invalidObject = "Not a ChordHarmonica or ChordDetectionResult";

        // Act & Assert
        // Should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> comparator.compare(chordHarmonica, invalidObject), "Should throw IllegalArgumentException for invalid object type");

        assertThrows(IllegalArgumentException.class, () -> comparator.compare(invalidObject, chordHarmonica), "Should throw IllegalArgumentException for invalid object type");
    }

    @Test
    @DisplayName("Compare with various invalid second argument types")
    void testCompareWithFirstArgumentChordAndSecondArgumentNotChord() {
        // Arrange
        // Create a ChordHarmonica as the first argument
        ChordHarmonica chordHarmonica = createChordHarmonicaWithTones(Arrays.asList(440.0, 523.25)); // A4, C5

        // Test with different types of invalid objects as the second argument
        String stringObject = "Not a ChordHarmonica";
        Integer integerObject = 42;
        Double doubleObject = 440.0;
        List<Double> listObject = Arrays.asList(440.0, 523.25);

        // Act & Assert
        // All should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> comparator.compare(chordHarmonica, stringObject), "Should throw IllegalArgumentException when second argument is a String");

        assertThrows(IllegalArgumentException.class, () -> comparator.compare(chordHarmonica, integerObject), "Should throw IllegalArgumentException when second argument is an Integer");

        assertThrows(IllegalArgumentException.class, () -> comparator.compare(chordHarmonica, doubleObject), "Should throw IllegalArgumentException when second argument is a Double");

        assertThrows(IllegalArgumentException.class, () -> comparator.compare(chordHarmonica, listObject), "Should throw IllegalArgumentException when second argument is a List");

        assertThrows(IllegalArgumentException.class, () -> comparator.compare(chordHarmonica, null), "Should throw IllegalArgumentException when second argument is null");
    }

    @ParameterizedTest
    @DisplayName("Test frequency comparison with various tolerance levels")
    @CsvSource({"440.0, 440.0, true",      // Exact same frequency
            "440.0, 452.89, true",     // Within tolerance (50 cents higher)
            "440.0, 427.48, true",     // Within tolerance (50 cents lower)
            "440.0, 466.16, false",    // Outside tolerance (A#4/Bb4)
            "440.0, 415.30, false"     // Outside tolerance (G#4/Ab4)
    })
    void testIsWithinCents(double f1, double f2, boolean expected) {
        // Arrange
        // Create chords with the test frequencies
        ChordHarmonica chordHarmonica1 = createChordHarmonicaWithTones(Arrays.asList(f1, 523.25));
        ChordHarmonica chordHarmonica2 = createChordHarmonicaWithTones(Arrays.asList(f2, 523.25));

        // Act
        // Test if frequencies are within tolerance
        int result = comparator.compare(chordHarmonica1, chordHarmonica2);

        // Assert
        if (expected) {
            assertEquals(0, result, "Frequencies " + f1 + " and " + f2 + " should be within tolerance");
        } else {
            assertNotEquals(0, result, "Frequencies " + f1 + " and " + f2 + " should not be within tolerance");
        }
    }

    /**
     * Tests the comparison of objects with different note counts and verifies that:
     * 1. Objects with fewer notes are considered "less than" objects with more notes
     * 2. Objects of different types (ChordHarmonica and ChordDetectionResult) but with
     *    the same number of notes and frequencies are considered equal
     * <p>
     * This test is comprehensive as it tests both the primary sorting criterion (note count)
     * and the cross-type comparison functionality.
     */
    @Test
    @DisplayName("Test note count comparison with different numbers of notes")
    void testNoteCountComparison() {
        // Arrange - Create objects with different note counts (2, 3, and 4 notes)
        // Two-note objects (A4, C5)
        ChordHarmonica twoNoteChord = createChordHarmonicaWithTones(
                Arrays.asList(440.0, 523.25));
        ChordDetectionResult twoNoteResult = new ChordDetectionResult(
                Arrays.asList(440.0, 523.25), 0.95);

        // Three-note objects (A4, C5, E5 - major triad)
        ChordHarmonica threeNoteChord = createChordHarmonicaWithTones(
                Arrays.asList(440.0, 523.25, 659.25));
        ChordDetectionResult threeNoteResult = new ChordDetectionResult(
                Arrays.asList(440.0, 523.25, 659.25), 0.95);

        // Four-note objects (A4, C5, E5, G5 - major seventh chord)
        ChordHarmonica fourNoteChord = createChordHarmonicaWithTones(
                Arrays.asList(440.0, 523.25, 659.25, 783.99));
        ChordDetectionResult fourNoteResult = new ChordDetectionResult(
                Arrays.asList(440.0, 523.25, 659.25, 783.99), 0.95);

        // Act & Assert - Test 1: Compare objects with different note counts
        // Objects with fewer notes should be "less than" objects with more notes
        assertTrue(comparator.compare(twoNoteChord, threeNoteChord) < 0, 
                "2-note chord should be less than 3-note chord");
        assertTrue(comparator.compare(threeNoteChord, fourNoteChord) < 0, 
                "3-note chord should be less than 4-note chord");
        assertTrue(comparator.compare(twoNoteResult, threeNoteResult) < 0, 
                "2-note result should be less than 3-note result");
        assertTrue(comparator.compare(threeNoteResult, fourNoteResult) < 0, 
                "3-note result should be less than 4-note result");

        // Act & Assert - Test 2: Compare ChordHarmonica with ChordDetectionResult
        // Objects of different types but with same note count and frequencies should be equal
        assertEquals(0, comparator.compare(twoNoteChord, twoNoteResult), 
                "2-note chord should equal 2-note result with same frequencies");
        assertEquals(0, comparator.compare(threeNoteChord, threeNoteResult), 
                "3-note chord should equal 3-note result with same frequencies");
        assertEquals(0, comparator.compare(fourNoteChord, fourNoteResult), 
                "4-note chord should equal 4-note result with same frequencies");
    }

    // This test was removed because it was redundant with testCompareWithChordAndChordDetectionResult

    /**
     * This test specifically focuses on the frequency comparison aspect of the comparator
     * with multiple frequencies and different tolerance levels.
     * While there is some overlap with testCompareWithSameNoteCountAndFrequenciesWithinTolerance,
     * this test provides more comprehensive coverage by:
     * 1. Testing with three frequencies instead of two
     * 2. Testing both within-tolerance and outside-tolerance scenarios in a single test
     * 3. Using specific musical intervals (50 cents and 100 cents) to verify the tolerance boundaries
     */
    @Test
    @DisplayName("Test frequency comparison with multiple notes and various tolerance levels")
    void testFrequencyComparisonWithMultipleNotes() {
        // Arrange - Create reference chord with three notes (A4, C5, E5 - a major triad)
        List<Double> referenceFrequencies = Arrays.asList(440.0, 523.25, 659.25);
        ChordHarmonica referenceChord = createChordHarmonicaWithTones(referenceFrequencies);

        // Test 1: Identical frequencies
        List<Double> identicalFrequencies = Arrays.asList(440.0, 523.25, 659.25);
        ChordHarmonica identicalChord = createChordHarmonicaWithTones(identicalFrequencies);
        assertEquals(0, comparator.compare(referenceChord, identicalChord), 
                "Chords with identical frequencies should be equal");

        // Test 2: Frequencies within tolerance (each about 50 cents higher)
        List<Double> withinToleranceFrequencies = Arrays.asList(452.89, 538.55, 678.45);
        ChordHarmonica withinToleranceChord = createChordHarmonicaWithTones(withinToleranceFrequencies);
        assertEquals(0, comparator.compare(referenceChord, withinToleranceChord), 
                "Chords with frequencies within tolerance (50 cents) should be equal");

        // Test 3: Frequencies outside tolerance (each about 100 cents/semitone higher)
        List<Double> outsideToleranceFrequencies = Arrays.asList(466.16, 554.37, 698.46);
        ChordHarmonica outsideToleranceChord = createChordHarmonicaWithTones(outsideToleranceFrequencies);
        assertNotEquals(0, comparator.compare(referenceChord, outsideToleranceChord), 
                "Chords with frequencies outside tolerance (100 cents) should not be equal");
    }

    @Test
    @DisplayName("Test comparison with different order of same frequencies")
    void testCompareWithDifferentOrderOfSameFrequencies() {
        // Arrange
        // Create chords with the same frequencies but in different order
        List<Double> frequencies1 = Arrays.asList(440.0, 523.25, 659.25); // A4, C5, E5
        List<Double> frequencies2 = Arrays.asList(659.25, 440.0, 523.25); // E5, A4, C5
        ChordHarmonica chordHarmonica1 = createChordHarmonicaWithTones(frequencies1);
        ChordHarmonica chordHarmonica2 = createChordHarmonicaWithTones(frequencies2);

        // Act
        int result = comparator.compare(chordHarmonica1, chordHarmonica2);

        // Assert
        // The areFrequenciesEqual method sorts the frequencies before comparison (O(n log n))
        assertEquals(0, result, "Chords with same frequencies in different order should be equal");
    }

    @Test
    @DisplayName("Test comparison with empty frequency lists")
    void testCompareWithEmptyFrequencyLists() {
        // Arrange
        // Create chords with empty frequency lists
        ChordHarmonica emptyChord1 = createChordHarmonicaWithTones(Collections.emptyList());
        ChordHarmonica emptyChord2 = createChordHarmonicaWithTones(Collections.emptyList());
        ChordDetectionResult emptyResult = new ChordDetectionResult(Collections.emptyList(), 0.0);

        // Act & Assert
        // Empty chords should be equal to each other
        assertEquals(0, comparator.compare(emptyChord1, emptyChord2), "Empty chords should be equal to each other");

        // Empty chord should be equal to empty detection result
        assertEquals(0, comparator.compare(emptyChord1, emptyResult), "Empty chord should be equal to empty detection result");

        // Empty chord should be less than non-empty chord
        ChordHarmonica nonEmptyChord = createChordHarmonicaWithTones(Arrays.asList(440.0));
        assertTrue(comparator.compare(emptyChord1, nonEmptyChord) < 0, "Empty chord should be less than non-empty chord");
        assertTrue(comparator.compare(nonEmptyChord, emptyChord1) > 0, "Non-empty chord should be greater than empty chord");
    }
}
