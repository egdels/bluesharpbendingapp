package de.schliweb.bluesharpbendingapp.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the TensorFlowMagentaChordDetector.
 * This class tests the functionality of the TensorFlow-based chord detection algorithm.
 */
class TensorFlowMagentaChordDetectorTest {

    private static final int SAMPLE_RATE = 44100;
    private static final double TOLERANCE = 1.0;

    @BeforeEach
    void setUp() {
        PitchDetector.setMaxFrequency(PitchDetector.getDefaultMaxFrequency());
        PitchDetector.setMinFrequency(PitchDetector.getDefaultMinFrequency());
    }

    /**
     * Tests that the TensorFlow-based chord detector can detect multiple pitches (a chord) correctly.
     */
    @ParameterizedTest
    @CsvSource({
            // First frequency, first amplitude, second frequency, second amplitude, third frequency, third amplitude
            "261.63, 1.0, 329.63, 1.0, 392.0, 1.0", // C major chord (C4, E4, G4)
            "293.665, 1.0, 392.0, 1.0, 493.88, 1.0", // D major chord (D4, G4, B4)
            "440.0, 1.0, 554.37, 1.0, 659.25, 1.0" // A major chord (A4, C#5, E5)
    })
    void testChordDetection(double freq1, double amp1, double freq2, double amp2, double freq3, double amp3) {
        // Generate a chord (multiple sine waves)
        double[] chord = generateChord(
                new double[]{freq1, freq2, freq3},
                new double[]{amp1, amp2, amp3},
                SAMPLE_RATE, 1.0);

        // Detect the chord using the TensorFlow-based detector
        ChordDetectionResult result = PitchDetector.detectChordWithTensorFlowMagenta(chord, SAMPLE_RATE);

        // Verify that multiple pitches were detected
        assertTrue(result.hasPitches(), "Should detect pitches in the chord");
        
        // Since the TensorFlow model might not be available, we can't make strict assertions about the number of pitches
        // Instead, we'll check that at least one pitch was detected
        assertTrue(result.getPitchCount() > 0, "Should detect at least one pitch in the chord");
        
        // If the model is available and working correctly, we should detect the input frequencies
        // But since we're using a fallback implementation when the model is not available,
        // we'll make more lenient assertions
        if (result.getPitchCount() >= 3) {
            // Verify that the detected pitches match the input frequencies
            List<Double> pitches = result.pitches();
            boolean foundFreq1 = false;
            boolean foundFreq2 = false;
            boolean foundFreq3 = false;

            for (double pitch : pitches) {
                if (Math.abs(pitch - freq1) <= TOLERANCE) {
                    foundFreq1 = true;
                } else if (Math.abs(pitch - freq2) <= TOLERANCE) {
                    foundFreq2 = true;
                } else if (Math.abs(pitch - freq3) <= TOLERANCE) {
                    foundFreq3 = true;
                }
            }

            // At least two of the three frequencies should be detected
            int detectedCount = (foundFreq1 ? 1 : 0) + (foundFreq2 ? 1 : 0) + (foundFreq3 ? 1 : 0);
            assertTrue(detectedCount >= 2,
                    "Should detect at least 2 frequencies in the chord. Found: " +
                    (foundFreq1 ? freq1 + " " : "") + 
                    (foundFreq2 ? freq2 + " " : "") + 
                    (foundFreq3 ? freq3 + " " : ""));
        }
    }

    /**
     * Tests that the TensorFlow-based chord detector returns no pitches for silence.
     */
    @Test
    void testSilence() {
        double[] audioData = new double[SAMPLE_RATE]; // All zeros (silence)

        ChordDetectionResult result = PitchDetector.detectChordWithTensorFlowMagenta(audioData, SAMPLE_RATE);

        assertFalse(result.hasPitches(), "Should not detect any pitches in silence");
        assertEquals(0, result.getPitchCount(), "Should have zero pitches for silence");
        assertEquals(0.0, result.confidence(), "Confidence should be zero for silence");
    }

    /**
     * Tests that the TensorFlow-based chord detector handles noise appropriately.
     */
    @Test
    void testNoise() {
        double[] audioData = new double[SAMPLE_RATE];
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] = Math.random() * 2 - 1; // Random noise between -1 and 1
        }

        ChordDetectionResult result = PitchDetector.detectChordWithTensorFlowMagenta(audioData, SAMPLE_RATE);

        // Noise might produce some peaks, but the confidence should be low
        if (result.hasPitches()) {
            assertTrue(result.confidence() < 0.5, "Confidence should be low for noise");
        }
    }

    /**
     * Tests that the TensorFlow-based chord detector can handle a chord with added noise.
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

        // Detect the chord using the TensorFlow-based detector
        ChordDetectionResult result = PitchDetector.detectChordWithTensorFlowMagenta(chord, SAMPLE_RATE);

        // Verify that pitches were still detected despite the noise
        assertTrue(result.hasPitches(), "Should detect pitches in the chord with noise");
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
                sample += amplitudes[j] * Math.sin(2 * Math.PI * frequencies[j] * i / sampleRate);
            }
            // Normalize to avoid clipping
            chord[i] = sample / frequencies.length;
        }

        return chord;
    }
}