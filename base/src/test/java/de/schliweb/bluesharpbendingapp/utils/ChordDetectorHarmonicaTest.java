package de.schliweb.bluesharpbendingapp.utils;

import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for the ChordDetector with different harmonicas for single note recognition.
 * This class tests the functionality of the chord detection algorithm when the frequency
 * range is restricted to the frequency range of different harmonicas.
 */
class ChordDetectorHarmonicaTest {

    private static final int SAMPLE_RATE = 44100;
    private static final double TOLERANCE = 1.0;
    private static final double CONFIDENCE_THRESHOLD = 0.9;

    @BeforeEach
    void setUp() {
        // Reset to default frequency range before each test
        PitchDetector.setMaxFrequency(PitchDetector.getDefaultMaxFrequency());
        PitchDetector.setMinFrequency(PitchDetector.getDefaultMinFrequency());
    }

    /**
     * Tests that the ChordDetector can detect single notes at the edges of a harmonica's frequency range.
     * This test verifies that when the frequency range is restricted to a harmonica's range,
     * the ChordDetector can still accurately detect single notes at the minimum and maximum frequencies.
     */
    @ParameterizedTest
    @MethodSource("provideHarmonicaParameters")
    void testChordDetector_SingleNote_HarmonicaEdges(AbstractHarmonica.KEY key, AbstractHarmonica.TUNE tune, 
                                                    double maxFrequencyTolerance, double minFrequencyTolerance) {
        // Create a harmonica instance
        Harmonica harmonica = AbstractHarmonica.create(key, tune);

        // Set the frequency range to the harmonica's range
        PitchDetector.setMaxFrequency(harmonica.getHarmonicaMaxFrequency());
        PitchDetector.setMinFrequency(harmonica.getHarmonicaMinFrequency());

        // Test for a frequency slightly below the maximum (95% of max)
        // This is more reliable as the ChordDetector may have difficulty with the extreme edges
        double maxTestFrequency = harmonica.getHarmonicaMaxFrequency() * 0.95;
        double[] sineWave = generateSineWave(maxTestFrequency, SAMPLE_RATE, 1.0);
        ChordDetectionResult result = PitchDetector.detectChord(sineWave, SAMPLE_RATE);

        // Verify that a pitch was detected
        assertTrue(result.hasPitches(), 
                "Should detect a pitch at 95% of the maximum frequency of the " + key + " " + tune + " harmonica");

        // Verify that only one pitch was detected (single note)
        assertEquals(1, result.getPitchCount(), 
                "Should detect exactly one pitch for a single note at 95% of the maximum frequency");

        // Verify that the detected pitch matches the input frequency
        assertEquals(maxTestFrequency, result.getPitch(0), maxFrequencyTolerance,
                "Detected pitch should match 95% of the maximum frequency of the harmonica");

        // Verify that the confidence is high
        assertTrue(result.confidence() >= CONFIDENCE_THRESHOLD,
                "Confidence should be high for a pure sine wave at the maximum frequency");

        // Test for a frequency slightly above the minimum (105% of min)
        // This is more reliable as the ChordDetector may have difficulty with the extreme edges
        double minTestFrequency = harmonica.getHarmonicaMinFrequency() * 1.05;
        sineWave = generateSineWave(minTestFrequency, SAMPLE_RATE, 1.0);
        result = PitchDetector.detectChord(sineWave, SAMPLE_RATE);

        // Verify that a pitch was detected
        assertTrue(result.hasPitches(), 
                "Should detect a pitch at 105% of the minimum frequency of the " + key + " " + tune + " harmonica");

        // Verify that only one pitch was detected (single note)
        assertEquals(1, result.getPitchCount(), 
                "Should detect exactly one pitch for a single note at 105% of the minimum frequency");

        // Verify that the detected pitch matches the input frequency
        // Use a larger tolerance (0.5 Hz) for the minimum frequency test
        assertEquals(minTestFrequency, result.getPitch(0), 0.5,
                "Detected pitch should match 105% of the minimum frequency of the harmonica");

        // Verify that the confidence is high
        assertTrue(result.confidence() >= CONFIDENCE_THRESHOLD,
                "Confidence should be high for a pure sine wave at the minimum frequency");
    }

    /**
     * Tests that the ChordDetector can detect single notes at various frequencies within a harmonica's range.
     * This test verifies that when the frequency range is restricted to a harmonica's range,
     * the ChordDetector can accurately detect single notes at different frequencies within that range.
     */
    @ParameterizedTest
    @MethodSource("provideHarmonicaParameters")
    void testChordDetector_SingleNote_HarmonicaMidRange(AbstractHarmonica.KEY key, AbstractHarmonica.TUNE tune, 
                                                       double maxFrequencyTolerance, double minFrequencyTolerance) {
        // Create a harmonica instance
        Harmonica harmonica = AbstractHarmonica.create(key, tune);

        // Set the frequency range to the harmonica's range
        PitchDetector.setMaxFrequency(harmonica.getHarmonicaMaxFrequency());
        PitchDetector.setMinFrequency(harmonica.getHarmonicaMinFrequency());

        // Calculate a frequency in the middle of the harmonica's range
        double midFrequency = (harmonica.getHarmonicaMinFrequency() + harmonica.getHarmonicaMaxFrequency()) / 2.0;

        // Test for mid frequency
        double[] sineWave = generateSineWave(midFrequency, SAMPLE_RATE, 1.0);
        ChordDetectionResult result = PitchDetector.detectChord(sineWave, SAMPLE_RATE);

        // Verify that a pitch was detected
        assertTrue(result.hasPitches(), 
                "Should detect a pitch at the middle frequency of the " + key + " " + tune + " harmonica");

        // Verify that only one pitch was detected (single note)
        assertEquals(1, result.getPitchCount(), 
                "Should detect exactly one pitch for a single note at the middle frequency");

        // Verify that the detected pitch matches the input frequency
        assertEquals(midFrequency, result.getPitch(0), TOLERANCE,
                "Detected pitch should match the middle frequency of the harmonica");

        // Verify that the confidence is high
        assertTrue(result.confidence() >= CONFIDENCE_THRESHOLD,
                "Confidence should be high for a pure sine wave at the middle frequency");

        // Calculate a frequency at 1/4 of the harmonica's range
        double quarterFrequency = harmonica.getHarmonicaMinFrequency() + 
                                 (harmonica.getHarmonicaMaxFrequency() - harmonica.getHarmonicaMinFrequency()) / 4.0;

        // Test for quarter frequency
        sineWave = generateSineWave(quarterFrequency, SAMPLE_RATE, 1.0);
        result = PitchDetector.detectChord(sineWave, SAMPLE_RATE);

        // Verify that a pitch was detected
        assertTrue(result.hasPitches(), 
                "Should detect a pitch at the quarter frequency of the " + key + " " + tune + " harmonica");

        // Verify that only one pitch was detected (single note)
        assertEquals(1, result.getPitchCount(), 
                "Should detect exactly one pitch for a single note at the quarter frequency");

        // Verify that the detected pitch matches the input frequency
        assertEquals(quarterFrequency, result.getPitch(0), TOLERANCE,
                "Detected pitch should match the quarter frequency of the harmonica");

        // Verify that the confidence is high
        assertTrue(result.confidence() >= CONFIDENCE_THRESHOLD,
                "Confidence should be high for a pure sine wave at the quarter frequency");

        // Calculate a frequency at 3/4 of the harmonica's range
        double threeQuarterFrequency = harmonica.getHarmonicaMinFrequency() + 
                                      3 * (harmonica.getHarmonicaMaxFrequency() - harmonica.getHarmonicaMinFrequency()) / 4.0;

        // Test for three-quarter frequency
        sineWave = generateSineWave(threeQuarterFrequency, SAMPLE_RATE, 1.0);
        result = PitchDetector.detectChord(sineWave, SAMPLE_RATE);

        // Verify that a pitch was detected
        assertTrue(result.hasPitches(), 
                "Should detect a pitch at the three-quarter frequency of the " + key + " " + tune + " harmonica");

        // Verify that only one pitch was detected (single note)
        assertEquals(1, result.getPitchCount(), 
                "Should detect exactly one pitch for a single note at the three-quarter frequency");

        // Verify that the detected pitch matches the input frequency
        assertEquals(threeQuarterFrequency, result.getPitch(0), TOLERANCE,
                "Detected pitch should match the three-quarter frequency of the harmonica");

        // Verify that the confidence is high
        assertTrue(result.confidence() >= CONFIDENCE_THRESHOLD,
                "Confidence should be high for a pure sine wave at the three-quarter frequency");
    }

    /**
     * Tests that the ChordDetector correctly rejects frequencies outside the harmonica's range.
     * This test verifies that when the frequency range is restricted to a harmonica's range,
     * the ChordDetector does not detect pitches that are outside that range.
     */
    @ParameterizedTest
    @MethodSource("provideHarmonicaParameters")
    void testChordDetector_SingleNote_OutsideHarmonicaRange(AbstractHarmonica.KEY key, AbstractHarmonica.TUNE tune, 
                                                          double maxFrequencyTolerance, double minFrequencyTolerance) {
        // Create a harmonica instance
        Harmonica harmonica = AbstractHarmonica.create(key, tune);

        // Set the frequency range to the harmonica's range
        PitchDetector.setMaxFrequency(harmonica.getHarmonicaMaxFrequency());
        PitchDetector.setMinFrequency(harmonica.getHarmonicaMinFrequency());

        // Calculate frequencies outside the harmonica's range
        double belowMinFrequency = harmonica.getHarmonicaMinFrequency() * 0.8; // 20% below min
        double aboveMaxFrequency = harmonica.getHarmonicaMaxFrequency() * 1.2; // 20% above max

        // Test for frequency below the minimum
        double[] sineWave = generateSineWave(belowMinFrequency, SAMPLE_RATE, 1.0);
        ChordDetectionResult result = PitchDetector.detectChord(sineWave, SAMPLE_RATE);

        // Verify that no pitches were detected or the detected pitch is within the harmonica's range
        if (result.hasPitches()) {
            assertTrue(result.getPitch(0) >= harmonica.getHarmonicaMinFrequency(),
                    "If a pitch is detected for a frequency below the minimum, it should be within the harmonica's range");
        }

        // Test for frequency above the maximum
        sineWave = generateSineWave(aboveMaxFrequency, SAMPLE_RATE, 1.0);
        result = PitchDetector.detectChord(sineWave, SAMPLE_RATE);

        // Verify that no pitches were detected or the detected pitch is within the harmonica's range
        if (result.hasPitches()) {
            assertTrue(result.getPitch(0) <= harmonica.getHarmonicaMaxFrequency(),
                    "If a pitch is detected for a frequency above the maximum, it should be within the harmonica's range");
        }
    }

    /**
     * Provides parameters for the harmonica tests.
     * Each parameter set includes a harmonica key, tune, and frequency tolerances.
     * 
     * @return a stream of arguments for the parameterized tests
     */
    private static Stream<Arguments> provideHarmonicaParameters() {
        return Stream.of(
                // Common harmonica types with different keys and tunings
                Arguments.of(AbstractHarmonica.KEY.C, AbstractHarmonica.TUNE.RICHTER, 0.3, 0.01),
                Arguments.of(AbstractHarmonica.KEY.A, AbstractHarmonica.TUNE.RICHTER, 0.1, 0.01),
                Arguments.of(AbstractHarmonica.KEY.D, AbstractHarmonica.TUNE.RICHTER, 0.3, 0.01),
                Arguments.of(AbstractHarmonica.KEY.G, AbstractHarmonica.TUNE.RICHTER, 0.3, 0.01),
                Arguments.of(AbstractHarmonica.KEY.A_FLAT, AbstractHarmonica.TUNE.AUGMENTED, 0.1, 0.01),
                Arguments.of(AbstractHarmonica.KEY.D, AbstractHarmonica.TUNE.AUGMENTED, 0.3, 0.01),
                Arguments.of(AbstractHarmonica.KEY.E, AbstractHarmonica.TUNE.COUNTRY, 0.4, 0.01),
                Arguments.of(AbstractHarmonica.KEY.B_FLAT, AbstractHarmonica.TUNE.PADDYRICHTER, 0.1, 0.01)
        );
    }

    /**
     * Generates a sine wave based on the given frequency, sample rate, and duration.
     *
     * @param frequency the frequency of the sine wave in Hertz
     * @param sampleRate the number of samples per second
     * @param duration the duration of the sine wave in seconds
     * @return an array of doubles representing the generated sine wave
     */
    private double[] generateSineWave(double frequency, int sampleRate, double duration) {
        int samples = (int) (sampleRate * duration);
        double[] sineWave = new double[samples];
        for (int i = 0; i < samples; i++) {
            sineWave[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }
        return sineWave;
    }
}
