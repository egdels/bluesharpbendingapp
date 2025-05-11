package de.schliweb.bluesharpbendingapp.model.harmonica;

import de.schliweb.bluesharpbendingapp.utils.ChordDetectionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link ChordAndDetectionResultComparator}.
 * Tests the comparison functionality between ChordHarmonica and ChordDetectionResult objects.
 */
class ChordAndDetectionResultComparatorTest {

    private ChordAndDetectionResultComparator comparator;

    @BeforeEach
    void setUp() {
        comparator = new ChordAndDetectionResultComparator();
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
    void testCompareWithDifferentNoteCount() {
        // Create chords with different number of notes
        ChordHarmonica chordHarmonica1 = createChordHarmonicaWithTones(Arrays.asList(440.0, 523.25)); // A4, C5 - 2 notes
        ChordHarmonica chordHarmonica2 = createChordHarmonicaWithTones(Arrays.asList(440.0, 523.25, 659.25)); // A4, C5, E5 - 3 notes

        // The chord with fewer notes should be "less than" the chord with more notes
        int result = comparator.compare(chordHarmonica1, chordHarmonica2);
        assertTrue(result < 0, "ChordHarmonica with 2 notes should be less than chord with 3 notes");

        // Test the reverse comparison
        result = comparator.compare(chordHarmonica2, chordHarmonica1);
        assertTrue(result > 0, "ChordHarmonica with 3 notes should be greater than chord with 2 notes");
    }

    @Test
    void testCompareWithSameNoteCountAndEqualFrequencies() {
        // Create chords with the same number of notes and equal frequencies
        ChordHarmonica chordHarmonica1 = createChordHarmonicaWithTones(Arrays.asList(440.0, 523.25)); // A4, C5
        ChordHarmonica chordHarmonica2 = createChordHarmonicaWithTones(Arrays.asList(440.0, 523.25)); // A4, C5

        // The chords should be equal
        int result = comparator.compare(chordHarmonica1, chordHarmonica2);
        assertEquals(0, result, "Chords with same frequencies should be equal");
    }

    @Test
    void testCompareWithSameNoteCountAndFrequenciesWithinTolerance() {
        // Create chords with the same number of notes and frequencies within tolerance (±50 cents)
        // 440.0 Hz * 1.029302 ≈ 452.89 Hz (50 cents higher than A4)
        ChordHarmonica chordHarmonica1 = createChordHarmonicaWithTones(Arrays.asList(440.0, 523.25)); // A4, C5
        ChordHarmonica chordHarmonica2 = createChordHarmonicaWithTones(Arrays.asList(452.89, 538.55)); // A4+50cents, C5+50cents

        // The chords should be equal within the tolerance
        int result = comparator.compare(chordHarmonica1, chordHarmonica2);
        assertEquals(0, result, "Chords with frequencies within tolerance should be equal");
    }

    @Test
    void testCompareWithSameNoteCountAndDifferentFrequencies() {
        // Create chords with the same number of notes but different frequencies
        ChordHarmonica chordHarmonica1 = createChordHarmonicaWithTones(Arrays.asList(440.0, 523.25)); // A4, C5
        ChordHarmonica chordHarmonica2 = createChordHarmonicaWithTones(Arrays.asList(466.16, 587.33)); // A#4/Bb4, D5

        // The chord with lower frequency sum should be "less than" the chord with higher frequency sum
        int result = comparator.compare(chordHarmonica1, chordHarmonica2);
        assertTrue(result < 0, "ChordHarmonica with lower frequency sum should be less than chord with higher frequency sum");

        // Test the reverse comparison
        result = comparator.compare(chordHarmonica2, chordHarmonica1);
        assertTrue(result > 0, "ChordHarmonica with higher frequency sum should be greater than chord with lower frequency sum");
    }

    @Test
    void testCompareWithChordAndChordDetectionResult() {
        // Create a ChordHarmonica and a ChordDetectionResult with the same frequencies
        ChordHarmonica chordHarmonica = createChordHarmonicaWithTones(Arrays.asList(440.0, 523.25)); // A4, C5
        ChordDetectionResult detectionResult = new ChordDetectionResult(Arrays.asList(440.0, 523.25), 0.95);

        // They should be equal
        int result = comparator.compare(chordHarmonica, detectionResult);
        assertEquals(0, result, "ChordHarmonica and ChordDetectionResult with same frequencies should be equal");

        // Test the reverse comparison
        result = comparator.compare(detectionResult, chordHarmonica);
        assertEquals(0, result, "ChordDetectionResult and ChordHarmonica with same frequencies should be equal");
    }

    @Test
    void testCompareWithInvalidObjectTypes() {
        // Create a ChordHarmonica and an object of invalid type
        ChordHarmonica chordHarmonica = createChordHarmonicaWithTones(Arrays.asList(440.0, 523.25));
        String invalidObject = "Not a ChordHarmonica or ChordDetectionResult";

        // Should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> comparator.compare(chordHarmonica, invalidObject),
                "Should throw IllegalArgumentException for invalid object type");
        assertThrows(IllegalArgumentException.class, () -> comparator.compare(invalidObject, chordHarmonica),
                "Should throw IllegalArgumentException for invalid object type");
    }

    @Test
    void testCompareWithFirstArgumentChordAndSecondArgumentNotChord() {
        // Create a ChordHarmonica as the first argument
        ChordHarmonica chordHarmonica = createChordHarmonicaWithTones(Arrays.asList(440.0, 523.25)); // A4, C5

        // Test with different types of invalid objects as the second argument
        String stringObject = "Not a ChordHarmonica";
        Integer integerObject = 42;
        Double doubleObject = 440.0;
        List<Double> listObject = Arrays.asList(440.0, 523.25);

        // All should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> comparator.compare(chordHarmonica, stringObject),
                "Should throw IllegalArgumentException when second argument is a String");
        assertThrows(IllegalArgumentException.class, () -> comparator.compare(chordHarmonica, integerObject),
                "Should throw IllegalArgumentException when second argument is an Integer");
        assertThrows(IllegalArgumentException.class, () -> comparator.compare(chordHarmonica, doubleObject),
                "Should throw IllegalArgumentException when second argument is a Double");
        assertThrows(IllegalArgumentException.class, () -> comparator.compare(chordHarmonica, listObject),
                "Should throw IllegalArgumentException when second argument is a List");
        assertThrows(IllegalArgumentException.class, () -> comparator.compare(chordHarmonica, null),
                "Should throw IllegalArgumentException when second argument is null");
    }

    @ParameterizedTest
    @CsvSource({
            "440.0, 440.0, true",      // Exact same frequency
            "440.0, 452.89, true",     // Within tolerance (50 cents higher)
            "440.0, 427.48, true",     // Within tolerance (50 cents lower)
            "440.0, 466.16, false",    // Outside tolerance (A#4/Bb4)
            "440.0, 415.30, false"     // Outside tolerance (G#4/Ab4)
    })
    void testIsWithinCents(double f1, double f2, boolean expected) {
        // Create chords with the test frequencies
        ChordHarmonica chordHarmonica1 = createChordHarmonicaWithTones(Arrays.asList(f1, 523.25));
        ChordHarmonica chordHarmonica2 = createChordHarmonicaWithTones(Arrays.asList(f2, 523.25));

        // Test if frequencies are within tolerance
        int result = comparator.compare(chordHarmonica1, chordHarmonica2);
        if (expected) {
            assertEquals(0, result, "Frequencies " + f1 + " and " + f2 + " should be within tolerance");
        } else {
            assertNotEquals(0, result, "Frequencies " + f1 + " and " + f2 + " should not be within tolerance");
        }
    }

    @Test
    void testGetNoteCount() {
        // Create objects with different note counts
        ChordHarmonica chordHarmonica2 = createChordHarmonicaWithTones(Arrays.asList(440.0, 523.25)); // 2 notes
        ChordHarmonica chordHarmonica3 = createChordHarmonicaWithTones(Arrays.asList(440.0, 523.25, 659.25)); // 3 notes
        ChordHarmonica chordHarmonica4 = createChordHarmonicaWithTones(Arrays.asList(440.0, 523.25, 659.25, 783.99)); // 4 notes

        ChordDetectionResult result2 = new ChordDetectionResult(Arrays.asList(440.0, 523.25), 0.95); // 2 notes
        ChordDetectionResult result3 = new ChordDetectionResult(Arrays.asList(440.0, 523.25, 659.25), 0.95); // 3 notes
        ChordDetectionResult result4 = new ChordDetectionResult(Arrays.asList(440.0, 523.25, 659.25, 783.99), 0.95); // 4 notes

        // Compare objects with different note counts
        assertTrue(comparator.compare(chordHarmonica2, chordHarmonica3) < 0, "2-note chord should be less than 3-note chord");
        assertTrue(comparator.compare(chordHarmonica3, chordHarmonica4) < 0, "3-note chord should be less than 4-note chord");
        assertTrue(comparator.compare(result2, result3) < 0, "2-note result should be less than 3-note result");
        assertTrue(comparator.compare(result3, result4) < 0, "3-note result should be less than 4-note result");

        // Compare ChordHarmonica with ChordDetectionResult
        assertEquals(0, comparator.compare(chordHarmonica2, result2), "2-note chord should equal 2-note result with same frequencies");
        assertEquals(0, comparator.compare(chordHarmonica3, result3), "3-note chord should equal 3-note result with same frequencies");
        assertEquals(0, comparator.compare(chordHarmonica4, result4), "4-note chord should equal 4-note result with same frequencies");
    }

    @Test
    void testGetFrequencies() {
        // Create a ChordHarmonica and a ChordDetectionResult with the same frequencies
        List<Double> frequencies = Arrays.asList(440.0, 523.25, 659.25);
        ChordHarmonica chordHarmonica = createChordHarmonicaWithTones(frequencies);
        ChordDetectionResult detectionResult = new ChordDetectionResult(frequencies, 0.95);

        // Compare them - they should be equal
        int result = comparator.compare(chordHarmonica, detectionResult);
        assertEquals(0, result, "ChordHarmonica and ChordDetectionResult with same frequencies should be equal");
    }

    @Test
    void testAreFrequenciesEqual() {
        // Test with frequencies that are exactly the same
        List<Double> frequencies1 = Arrays.asList(440.0, 523.25, 659.25);
        List<Double> frequencies2 = Arrays.asList(440.0, 523.25, 659.25);
        ChordHarmonica chordHarmonica1 = createChordHarmonicaWithTones(frequencies1);
        ChordHarmonica chordHarmonica2 = createChordHarmonicaWithTones(frequencies2);

        assertEquals(0, comparator.compare(chordHarmonica1, chordHarmonica2), "Chords with identical frequencies should be equal");

        // Test with frequencies that are within tolerance
        List<Double> frequencies3 = Arrays.asList(452.89, 538.55, 678.45); // Each about 50 cents higher
        ChordHarmonica chordHarmonica3 = createChordHarmonicaWithTones(frequencies3);

        assertEquals(0, comparator.compare(chordHarmonica1, chordHarmonica3), "Chords with frequencies within tolerance should be equal");

        // Test with frequencies that are outside tolerance
        List<Double> frequencies4 = Arrays.asList(466.16, 554.37, 698.46); // Each about 100 cents (semitone) higher
        ChordHarmonica chordHarmonica4 = createChordHarmonicaWithTones(frequencies4);

        assertNotEquals(0, comparator.compare(chordHarmonica1, chordHarmonica4), "Chords with frequencies outside tolerance should not be equal");
    }
}
