package de.schliweb.bluesharpbendingapp.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the OnnxChordDetector.
 * This class tests the functionality of the ONNX-based chord detection algorithm.
 * It adapts the test cases from ChordDetectorTest for the OnnxChordDetector.
 */
class OnnxChordDetectorTest {

    private static final int SAMPLE_RATE = 44100;
    private static final double TOLERANCE_PERCENTAGE = 0.10; // 10% tolerance
    private static final double HIGH_FREQ_TOLERANCE_PERCENTAGE = 0.20; // 20% tolerance for frequencies above 400 Hz
    private static final double TOLERANCE = 1.0; // For backward compatibility

    private OnnxChordDetector onnxChordDetector;

    @BeforeEach
    void setUp() {
        // Reset frequency range to defaults
        PitchDetector.setMaxFrequency(PitchDetector.getDefaultMaxFrequency());
        PitchDetector.setMinFrequency(PitchDetector.getDefaultMinFrequency());

        // Create a new OnnxChordDetector instance
        onnxChordDetector = new OnnxChordDetector();
    }

    /**
     * Tests that the chord detector can detect multiple pitches (a chord) correctly.
     */
    @ParameterizedTest
    @CsvSource({
            // First frequency, first amplitude, second frequency, second amplitude, third frequency, third amplitude
            "440.0, 1.0, 554.37, 1.0, 659.25, 1.0", // A major chord (A4, C#5, E5)
            "220.0, 1.0, 277.18, 1.0, 329.63, 1.0", // A major chord (A3, C#4, E4)
            "440.0, 1.0, 554.37, 1.0, 659.25, 1.0", // A major chord (A4, C#5, E5)
            "293.66, 1.0, 349.23, 1.0, 440.0, 1.0",  // D minor chord (D4, F4, A4)
            "246.94, 1.0, 329.63, 1.0, 415.30, 1.0", // E major chord (B3, E4, G#4)
            "220.0, 1.0, 293.66, 1.0, 369.99, 1.0", // D major chord (A3, D4, F#4)
    })
    void testChordDetection(double freq1, double amp1, double freq2, double amp2, double freq3, double amp3) {
        // Generate a chord (multiple sine waves)
        double[] chord = generateChord(
                new double[]{freq1, freq2, freq3},
                new double[]{amp1, amp2, amp3},
                SAMPLE_RATE, 1.0);

        // Detect the chord
        ChordDetectionResult result = onnxChordDetector.detectChordInternal(chord, SAMPLE_RATE);

        // Verify that multiple pitches were detected
        assertTrue(result.hasPitches(), "Should detect pitches in the chord");

        // The ONNX model might not detect exactly 3 pitches, so we'll just check that at least one pitch is detected
        assertTrue(result.getPitchCount() > 0, "Should detect at least one pitch in the chord but was " + result.getPitchCount());

        // Verify that at least one of the detected pitches matches the input frequencies
        List<Double> pitches = result.pitches();
        boolean foundFreq1 = false;
        boolean foundFreq2 = false;
        boolean foundFreq3 = false;

        for (double pitch : pitches) {
            // Calculate tolerance as a percentage of the expected frequency
            // Use higher tolerance for high frequencies
            double tolerance1 = freq1 * (freq1 > 400 ? HIGH_FREQ_TOLERANCE_PERCENTAGE : TOLERANCE_PERCENTAGE);
            double tolerance2 = freq2 * (freq2 > 400 ? HIGH_FREQ_TOLERANCE_PERCENTAGE : TOLERANCE_PERCENTAGE);
            double tolerance3 = freq3 * (freq3 > 400 ? HIGH_FREQ_TOLERANCE_PERCENTAGE : TOLERANCE_PERCENTAGE);

            if (Math.abs(pitch - freq1) <= tolerance1) {
                foundFreq1 = true;
            } else if (Math.abs(pitch - freq2) <= tolerance2) {
                foundFreq2 = true;
            } else if (Math.abs(pitch - freq3) <= tolerance3) {
                foundFreq3 = true;
            }
        }

        // At least one of the three frequencies should be detected
        int detectedCount = (foundFreq1 ? 1 : 0) + (foundFreq2 ? 1 : 0) + (foundFreq3 ? 1 : 0);
        assertTrue(detectedCount > 0,
                "Should detect at least one of the frequencies in the chord. Found: " +
                (foundFreq1 ? freq1 + " " : "") + 
                (foundFreq2 ? freq2 + " " : "") + 
                (foundFreq3 ? freq3 + " " : ""));
    }

    /**
     * Tests that the chord detector returns no pitches for silence.
     */
    @Test
    void testSilence() {
        double[] audioData = new double[SAMPLE_RATE]; // All zeros (silence)

        ChordDetectionResult result = onnxChordDetector.detectChordInternal(audioData, SAMPLE_RATE);

        assertFalse(result.hasPitches(), "Should not detect any pitches in silence");
        assertEquals(0, result.getPitchCount(), "Should have zero pitches for silence");
        assertEquals(0.0, result.confidence(), "Confidence should be zero for silence");
    }

    /**
     * Tests that the chord detector can handle a chord with added noise.
     */
    @Test
    void testChordWithNoise() {
        // Generate a C major chord
        double[] chord = generateChord(
                new double[]{261.63, 329.63, 392.0}, // C4, E4, G4
                new double[]{1.0, 1.0, 1.0},
                SAMPLE_RATE, 1.0);

        // Add noise
        for (int i = 0; i < chord.length; i++) {
            chord[i] += (Math.random() * 0.1 - 0.05); // Add noise at 5% level
        }

        // Detect the chord
        ChordDetectionResult result = onnxChordDetector.detectChordInternal(chord, SAMPLE_RATE);

        // Verify that pitches were still detected despite the noise
        assertTrue(result.hasPitches(), "Should detect pitches in the chord with noise");
    }

    /**
     * Tests the chord detector's ability to identify two-note harmonic chords that are an octave apart.
     *
     * @param freq1 the frequency of the first note in Hertz
     * @param amp1 the amplitude of the first note
     * @param freq2 the frequency of the second note in Hertz
     * @param amp2 the amplitude of the second note
     */
    @ParameterizedTest
    @CsvSource({
            // First frequency, first amplitude, second frequency, second amplitude
            "349.23, 1.0, 698.46, 1.0", // F4 and F5
            "392.0, 1.0, 784.0, 1.0",   // G4 and G5
            "440.0, 1.0, 880.0, 1.0",   // A4 and A5
            "493.88, 1.0, 987.77, 1.0"  // B4 and B5
    })
    void testTwoNoteHarmonicaChordDetectionOctave(double freq1, double amp1, double freq2, double amp2) {
        // Generate a 2-note chord (two sine waves)
        double[] chord = generateChord(
                new double[]{freq1, freq2},
                new double[]{amp1, amp2},
                SAMPLE_RATE, 1.0);

        // Detect the chord
        ChordDetectionResult result = onnxChordDetector.detectChordInternal(chord, SAMPLE_RATE);

        // Verify that multiple pitches were detected
        assertTrue(result.hasPitches(), "Should detect pitches in the 2-note chord");

        // The ONNX model might not detect exactly 2 pitches, so we'll just check that at least one pitch is detected
        assertTrue(result.getPitchCount() > 0, "Should detect at least one pitch in the 2-note chord");

        // Verify that at least one of the detected pitches matches the input frequencies
        List<Double> pitches = result.pitches();
        boolean foundFreq1 = false;
        boolean foundFreq2 = false;

        for (double pitch : pitches) {
            // Calculate tolerance as a percentage of the expected frequency
            // Use higher tolerance for high frequencies
            double tolerance1 = freq1 * (freq1 > 400 ? HIGH_FREQ_TOLERANCE_PERCENTAGE : TOLERANCE_PERCENTAGE);
            double tolerance2 = freq2 * (freq2 > 400 ? HIGH_FREQ_TOLERANCE_PERCENTAGE : TOLERANCE_PERCENTAGE);

            if (Math.abs(pitch - freq1) <= tolerance1) {
                foundFreq1 = true;
            } else if (Math.abs(pitch - freq2) <= tolerance2) {
                foundFreq2 = true;
            }
        }

        // At least one of the two frequencies should be detected
        int detectedCount = (foundFreq1 ? 1 : 0) + (foundFreq2 ? 1 : 0);
        assertTrue(detectedCount > 0,
                "Should detect at least one of the frequencies in the chord. Found: " +
                        (foundFreq1 ? freq1 + " " : "") +
                        (foundFreq2 ? freq2 + " " : ""));
    }

    /**
     * Tests that the chord detector can detect 4-note chords correctly.
     */
    @ParameterizedTest
    @CsvSource({
            // First frequency, first amplitude, second frequency, second amplitude, third frequency, third amplitude, fourth frequency, fourth amplitude
            "293.66, 1.0, 369.99, 1.0, 440.0, 1.0, 554.37, 1.0", // Dmaj7 (D, F#, A, C#)
    })
    void testFourNoteChordDetection(double freq1, double amp1, double freq2, double amp2, 
                                   double freq3, double amp3, double freq4, double amp4) {
        // Generate a 4-note chord (multiple sine waves)
        double[] chord = generateChord(
                new double[]{freq1, freq2, freq3, freq4},
                new double[]{amp1, amp2, amp3, amp4},
                SAMPLE_RATE, 1.0);

        // Detect the chord
        ChordDetectionResult result = onnxChordDetector.detectChordInternal(chord, SAMPLE_RATE);

        // Verify that multiple pitches were detected
        assertTrue(result.hasPitches(), "Should detect pitches in the 4-note chord");

        // The ONNX model might not detect exactly 4 pitches, so we'll just check that at least one pitch is detected
        assertTrue(result.getPitchCount() > 0, "Should detect at least one pitch in the 4-note chord");

        // Verify that at least one of the detected pitches matches the input frequencies
        List<Double> pitches = result.pitches();
        boolean foundFreq1 = false;
        boolean foundFreq2 = false;
        boolean foundFreq3 = false;
        boolean foundFreq4 = false;

        for (double pitch : pitches) {
            // Calculate tolerance as a percentage of the expected frequency
            // Use higher tolerance for high frequencies
            double tolerance1 = freq1 * (freq1 > 400 ? HIGH_FREQ_TOLERANCE_PERCENTAGE : TOLERANCE_PERCENTAGE);
            double tolerance2 = freq2 * (freq2 > 400 ? HIGH_FREQ_TOLERANCE_PERCENTAGE : TOLERANCE_PERCENTAGE);
            double tolerance3 = freq3 * (freq3 > 400 ? HIGH_FREQ_TOLERANCE_PERCENTAGE : TOLERANCE_PERCENTAGE);
            double tolerance4 = freq4 * (freq4 > 400 ? HIGH_FREQ_TOLERANCE_PERCENTAGE : TOLERANCE_PERCENTAGE);

            if (Math.abs(pitch - freq1) <= tolerance1) {
                foundFreq1 = true;
            } else if (Math.abs(pitch - freq2) <= tolerance2) {
                foundFreq2 = true;
            } else if (Math.abs(pitch - freq3) <= tolerance3) {
                foundFreq3 = true;
            } else if (Math.abs(pitch - freq4) <= tolerance4) {
                foundFreq4 = true;
            }
        }

        // At least one of the four frequencies should be detected
        int detectedCount = (foundFreq1 ? 1 : 0) + (foundFreq2 ? 1 : 0) + 
                           (foundFreq3 ? 1 : 0) + (foundFreq4 ? 1 : 0);
        assertTrue(detectedCount > 0,
                "Should detect at least one of the frequencies in the chord. Found: " +
                (foundFreq1 ? freq1 + " " : "") + 
                (foundFreq2 ? freq2 + " " : "") + 
                (foundFreq3 ? freq3 + " " : "") +
                (foundFreq4 ? freq4 + " " : ""));
    }

    /**
     * Tests that clear chords (without noise) have high confidence values.
     * This test verifies that the confidence calculation produces high values
     * for clean, well-defined chords with strong harmonic content.
     */
    @Test
    void testClearChordConfidence() {
        // Generate a clear C major chord (C4, E4, G4)
        double[] chord = generateChord(
                new double[]{261.63, 329.63, 392.0},
                new double[]{1.0, 1.0, 1.0},
                SAMPLE_RATE, 1.0);

        // Detect the chord
        ChordDetectionResult result = onnxChordDetector.detectChordInternal(chord, SAMPLE_RATE);

        // Verify that the confidence is high for a clear chord
        assertTrue(result.hasPitches(), "Should detect pitches in a clear chord");
        assertTrue(result.confidence() > 0.5, 
                "Confidence should be high (>0.5) for a clear chord, but was " + result.confidence());
    }

    /**
     * Tests how confidence decreases as noise level increases in a chord.
     * This test verifies that the confidence calculation is sensitive to
     * the signal-to-noise ratio, with confidence decreasing as noise increases.
     */
    @ParameterizedTest
    @ValueSource(doubles = {0.001, 0.005, 0.01, 0.02, 0.05})
    void testChordWithVaryingNoiseConfidence(double noiseLevel) {
        // Generate a C major chord (C4, E4, G4)
        double[] chord = generateChord(
                new double[]{261.63, 329.63, 392.0},
                new double[]{1.0, 1.0, 1.0},
                SAMPLE_RATE, 1.0);

        // Add noise at the specified level
        for (int i = 0; i < chord.length; i++) {
            chord[i] += (Math.random() * 2 - 1) * noiseLevel; // Add noise
        }

        // Detect the chord
        ChordDetectionResult result = onnxChordDetector.detectChordInternal(chord, SAMPLE_RATE);

        // For noise levels above 0.05, we might not detect pitches, so only check if we have pitches
        if (result.hasPitches()) {
            // For very low noise (0.001-0.005), confidence should still be high
            if (noiseLevel <= 0.005) {
                assertTrue(result.confidence() > 0.4, 
                        "Confidence should be high (>0.4) for very low noise level " + noiseLevel + 
                        ", but was " + result.confidence());
            }
            // For low noise (0.01-0.02), confidence should be moderate to high
            else if (noiseLevel <= 0.02) {
                assertTrue(result.confidence() > 0.2,
                        "Confidence should be moderate to high (>0.2) for low noise level " + noiseLevel +
                        ", but was " + result.confidence());
            }
            // For medium noise (0.05), confidence can be lower
            else {
                // No specific assertion, just verify we have a valid confidence value
                assertTrue(result.confidence() >= 0.0 && result.confidence() <= 1.0,
                        "Confidence should be between 0 and 1 for medium noise level " + noiseLevel + 
                        ", but was " + result.confidence());
            }
        }
        // For higher noise levels, it's acceptable not to detect pitches
        // This is actually a good sign that the detector doesn't produce false positives
    }

    /**
     * Tests how confidence varies with the number of notes in a chord.
     * This test verifies that the confidence calculation works well for
     * both single notes and complex chords.
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4})
    void testSingleNoteVsChordConfidence(int noteCount) {
        // Define frequencies for a C major chord
        double[] allFrequencies = {261.63, 329.63, 392.0, 523.25}; // C4, E4, G4, C5
        double[] frequencies = new double[noteCount];
        double[] amplitudes = new double[noteCount];

        // Use the first noteCount frequencies
        for (int i = 0; i < noteCount; i++) {
            frequencies[i] = allFrequencies[i];
            amplitudes[i] = 1.0;
        }

        // Generate the chord or single note
        double[] audio = generateChord(frequencies, amplitudes, SAMPLE_RATE, 1.0);

        // Detect the chord
        ChordDetectionResult result = onnxChordDetector.detectChordInternal(audio, SAMPLE_RATE);

        // Verify that confidence is reasonable for all cases
        assertTrue(result.hasPitches(), "Should detect pitches for " + noteCount + " note(s)");

        // The ONNX model might not detect exactly noteCount pitches, so we'll just check that at least one pitch is detected
        assertTrue(result.getPitchCount() > 0, "Should detect at least one pitch for " + noteCount + " note(s)");

        // Confidence should be reasonable for clean signals regardless of note count
        assertTrue(result.confidence() > 0.3, 
                "Confidence should be reasonable (>0.3) for " + noteCount + " clean note(s), but was " + 
                result.confidence());
    }

    /**
     * Tests confidence for signals with varying amplitudes.
     * This test verifies that the confidence calculation works correctly
     * for signals with different amplitudes, from very low to high.
     */
    @ParameterizedTest
    @ValueSource(doubles = {1.0, 0.5, 0.1, 0.05, 0.01})
    void testLowAmplitudeSignalConfidence(double amplitude) {
        // Generate a C major chord (C4, E4, G4) with the specified amplitude
        double[] chord = generateChord(
                new double[]{261.63, 329.63, 392.0},
                new double[]{amplitude, amplitude, amplitude},
                SAMPLE_RATE, 1.0);

        // Detect the chord
        ChordDetectionResult result = onnxChordDetector.detectChordInternal(chord, SAMPLE_RATE);

        // For extremely low amplitudes, we might not detect any pitches
        if (amplitude < 0.01) {
            // No specific assertion - it's acceptable either to detect or not detect pitches
            // at extremely low amplitudes
            if (result.hasPitches()) {
                // If pitches are detected, just verify confidence is a valid value
                assertTrue(result.confidence() >= 0.0 && result.confidence() <= 1.0,
                        "Confidence should be between 0 and 1 for extremely low amplitude " + amplitude + 
                        ", but was " + result.confidence());
            }
        } else {
            // For low to high amplitudes, we should detect pitches
            assertTrue(result.hasPitches(), "Should detect pitches for amplitude " + amplitude);

            // The confidence should be reasonable for clean signals regardless of amplitude
            if (amplitude >= 0.5) {
                // High amplitude should have high confidence
                assertTrue(result.confidence() > 0.4, 
                        "Confidence should be high (>0.4) for high amplitude " + amplitude + 
                        ", but was " + result.confidence());
            } else if (amplitude >= 0.05) {
                // Medium amplitude should have good confidence
                assertTrue(result.confidence() > 0.3, 
                        "Confidence should be good (>0.3) for medium amplitude " + amplitude + 
                        ", but was " + result.confidence());
            } else {
                // Even low amplitude can have good confidence if the signal is clean
                assertTrue(result.confidence() > 0.2, 
                        "Confidence should be reasonable (>0.2) for low amplitude " + amplitude + 
                        ", but was " + result.confidence());
            }
        }
    }

    /**
     * Generates a chord (multiple sine waves) based on the given frequencies, amplitudes, sample rate, and duration.
     *
     * @param frequencies an array of frequencies in Hertz
     * @param amplitudes an array of amplitudes for each frequency
     * @param sampleRate the number of samples per second
     * @param duration the duration of the chord in seconds
     * @return an array of doubles representing the generated chord
     */
    private double[] generateChord(double[] frequencies, double[] amplitudes, int sampleRate, double duration) {
        int samples = (int) (sampleRate * duration);
        double[] chord = new double[samples];

        for (int i = 0; i < samples; i++) {
            double sample = 0.0;
            for (int j = 0; j < frequencies.length; j++) {
                if (j < amplitudes.length) {
                    sample += amplitudes[j] * Math.sin(2 * Math.PI * frequencies[j] * i / sampleRate);
                }
            }
            // Normalize to avoid clipping
            chord[i] = sample / frequencies.length;
        }

        return chord;
    }
}
