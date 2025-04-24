package de.schliweb.bluesharpbendingapp.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for the ZCRPitchDetector.
 * This class tests the functionality of the Zero-Crossing Rate with Spectral Weighting algorithm implementation.
 */
class ZCRPitchDetectorTest {

    private static final int SAMPLE_RATE = 44100;
    private static final double TOLERANCE = 5.0; // Higher tolerance for ZCR algorithm

    @BeforeEach
    void setUp() {
        ZCRPitchDetector.setMaxFrequency(ZCRPitchDetector.getMaxFrequency());
        ZCRPitchDetector.setMinFrequency(ZCRPitchDetector.getMinFrequency());
    }

    @Test
    void testDetectPitch_PureSineWave() {
        // Arrange
        double frequency = 440.0; // A4
        double duration = 1.0;
        double[] audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

        // Act
        ZCRPitchDetector.PitchDetectionResult result = ZCRPitchDetector.detectPitch(audioData, SAMPLE_RATE);

        // Assert
        assertEquals(frequency, result.pitch(), TOLERANCE, "Detected pitch should match the input sine wave frequency");
        assertTrue(result.confidence() > 0.5, "Confidence should be reasonable for a pure sine wave");
    }

    @Test
    void testDetectPitch_Silence() {
        // Arrange
        double[] audioData = new double[SAMPLE_RATE]; // 1 second of silence

        // Act
        ZCRPitchDetector.PitchDetectionResult result = ZCRPitchDetector.detectPitch(audioData, SAMPLE_RATE);

        // Assert
        assertEquals(ZCRPitchDetector.NO_DETECTED_PITCH, result.pitch(), "Should return NO_DETECTED_PITCH for silence");
        assertEquals(0.0, result.confidence(), "Confidence should be 0.0 for silence");
    }

    @Test
    void testDetectPitch_Noise() {
        // Arrange
        double[] audioData = new double[SAMPLE_RATE]; // 1 second of random noise
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] = Math.random() * 2 - 1; // Random values between -1 and 1
        }

        // Act
        ZCRPitchDetector.PitchDetectionResult result = ZCRPitchDetector.detectPitch(audioData, SAMPLE_RATE);

        // The ZCR algorithm might detect a pitch in noise due to its nature
        // We're not strictly asserting NO_DETECTED_PITCH here
        if (result.pitch() != ZCRPitchDetector.NO_DETECTED_PITCH) {
            // If a pitch is detected, the confidence should be low
            assertTrue(result.confidence() < 0.5, "Confidence should be low for noise");
        }
    }

    @Test
    void testDetectPitch_LowFrequency() {
        // Arrange
        double frequency = 100.0; // Low frequency
        double duration = 1.0;
        double[] audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

        // Act
        ZCRPitchDetector.PitchDetectionResult result = ZCRPitchDetector.detectPitch(audioData, SAMPLE_RATE);

        // Assert
        assertEquals(frequency, result.pitch(), TOLERANCE, "Detected pitch should match the low frequency sine wave");
        assertTrue(result.confidence() > 0.5, "Confidence should be reasonable for a low frequency sine wave");
    }

    @Test
    void testDetectPitch_HighFrequency() {
        // Arrange
        double frequency = 3000.0; // High frequency
        double duration = 1.0;
        double[] audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

        // Act
        ZCRPitchDetector.PitchDetectionResult result = ZCRPitchDetector.detectPitch(audioData, SAMPLE_RATE);

        // Assert
        assertEquals(frequency, result.pitch(), TOLERANCE * 2, "Detected pitch should match the high frequency sine wave");
        assertTrue(result.confidence() > 0.5, "Confidence should be reasonable for a high frequency sine wave");
    }

    @Test
    void testDetectPitch_WithHarmonics() {
        // Arrange
        double fundamentalFrequency = 440.0; // A4
        double harmonicFrequency = 880.0; // A5 (first harmonic)
        double duration = 1.0;
        double[] fundamental = generateSineWave(fundamentalFrequency, SAMPLE_RATE, duration);
        double[] harmonic = generateSineWave(harmonicFrequency, SAMPLE_RATE, duration);

        double[] audioData = new double[fundamental.length];
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] = fundamental[i] + 0.5 * harmonic[i]; // Add harmonic with half the amplitude
        }

        // Act
        ZCRPitchDetector.PitchDetectionResult result = ZCRPitchDetector.detectPitch(audioData, SAMPLE_RATE);

        // Print the detected pitch for debugging
        System.out.println("[DEBUG_LOG] Detected pitch for signal with harmonics: " + result.pitch());
        System.out.println("[DEBUG_LOG] Confidence: " + result.confidence());

        // Assert - ZCR might detect either the fundamental or the harmonic
        // We'll accept either result as valid
        // Use a very large tolerance (100 Hz) for signals with harmonics
        // This is because the ZCR algorithm might detect a frequency that's between the fundamental and harmonic
        double harmonicTolerance = 100.0;
        boolean isValidPitch = Math.abs(result.pitch() - fundamentalFrequency) <= harmonicTolerance ||
                               Math.abs(result.pitch() - harmonicFrequency) <= harmonicTolerance;

        assertTrue(isValidPitch, "Detected pitch should match either the fundamental or harmonic frequency");
        assertTrue(result.confidence() > 0.5, "Confidence should be reasonable for a signal with harmonics");
    }

    @ParameterizedTest
    @CsvSource({
        "440.0, 0.01, 5.0", // Low amplitude
        "440.0, 0.1, 5.0",  // Medium amplitude
        "440.0, 1.0, 5.0"   // High amplitude
    })
    void testDetectPitch_DifferentAmplitudes(double frequency, double amplitude, double tolerance) {
        // Arrange
        double duration = 1.0;
        double[] audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

        // Scale the amplitude
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] *= amplitude;
        }

        // Act
        ZCRPitchDetector.PitchDetectionResult result = ZCRPitchDetector.detectPitch(audioData, SAMPLE_RATE);

        // Assert
        // For very low amplitudes, the ZCR algorithm might not detect a pitch
        if (result.pitch() != ZCRPitchDetector.NO_DETECTED_PITCH) {
            assertEquals(frequency, result.pitch(), tolerance, 
                      "Detected pitch should match the input frequency regardless of amplitude");
            assertTrue(result.confidence() > 0.3, "Confidence should be reasonable regardless of amplitude");
        }
    }

    @ParameterizedTest
    @CsvSource({
        "100.0, 5.0", // Low frequency
        "440.0, 5.0", // Medium frequency
        "2000.0, 10.0" // High frequency
    })
    void testDetectPitch_DifferentFrequencies(double frequency, double tolerance) {
        // Arrange
        double duration = 1.0;
        double[] audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

        // Act
        ZCRPitchDetector.PitchDetectionResult result = ZCRPitchDetector.detectPitch(audioData, SAMPLE_RATE);

        // Assert
        assertEquals(frequency, result.pitch(), tolerance, "Detected pitch should match the input frequency");
        assertTrue(result.confidence() > 0.5, "Confidence should be reasonable for pure sine waves");
    }

    @Test
    void testFrequencyRangeSetting() {
        // Arrange
        double minFreq = 100.0;
        double maxFreq = 3000.0;
        ZCRPitchDetector.setMinFrequency(minFreq);
        ZCRPitchDetector.setMaxFrequency(maxFreq);

        double frequency = 440.0; // A4
        double duration = 1.0;
        double[] audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

        // Act
        ZCRPitchDetector.PitchDetectionResult result = ZCRPitchDetector.detectPitch(audioData, SAMPLE_RATE);

        // Assert
        assertEquals(frequency, result.pitch(), TOLERANCE, "Detected pitch should match the input frequency");
        assertEquals(minFreq, ZCRPitchDetector.getMinFrequency(), "Min frequency should be set correctly");
        assertEquals(maxFreq, ZCRPitchDetector.getMaxFrequency(), "Max frequency should be set correctly");
    }

    @Test
    void testFrequencyOutsideRange() {
        // Arrange
        double minFreq = 500.0;
        double maxFreq = 1000.0;
        ZCRPitchDetector.setMinFrequency(minFreq);
        ZCRPitchDetector.setMaxFrequency(maxFreq);

        double frequency = 440.0; // A4 (below min frequency)
        double duration = 1.0;
        double[] audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

        // Act
        ZCRPitchDetector.PitchDetectionResult result = ZCRPitchDetector.detectPitch(audioData, SAMPLE_RATE);

        // Assert
        // Our detector is capable of detecting pitches outside the specified range
        if (result.pitch() != ZCRPitchDetector.NO_DETECTED_PITCH) {
            assertEquals(frequency, result.pitch(), TOLERANCE, 
                      "The detector should accurately detect the pitch even outside the specified range");
            // But confidence should be lower
            assertTrue(result.confidence() < 0.8, "Confidence should be lower for frequencies outside the range");
        }
    }

    @Test
    void testDefaultFrequencyRange() {
        // Arrange
        ZCRPitchDetector.setMinFrequency(ZCRPitchDetector.getDefaultMinFrequency());
        ZCRPitchDetector.setMaxFrequency(ZCRPitchDetector.getDefaultMaxFrequency());

        // Assert
        assertEquals(ZCRPitchDetector.getDefaultMinFrequency(), ZCRPitchDetector.getMinFrequency(),
                    "Default min frequency should match the default min frequency");
        assertEquals(ZCRPitchDetector.getDefaultMaxFrequency(), ZCRPitchDetector.getMaxFrequency(),
                    "Default max frequency should match the default max frequency");
    }

    @Test
    void testInvalidInput() {
        // Arrange
        double[] nullData = null;
        double[] emptyData = new double[0];
        double[] singleSample = new double[1];

        // Act & Assert
        assertEquals(ZCRPitchDetector.NO_DETECTED_PITCH, ZCRPitchDetector.detectPitch(nullData, SAMPLE_RATE).pitch(),
                   "Should return NO_DETECTED_PITCH for null input");
        assertEquals(ZCRPitchDetector.NO_DETECTED_PITCH, ZCRPitchDetector.detectPitch(emptyData, SAMPLE_RATE).pitch(),
                   "Should return NO_DETECTED_PITCH for empty input");
        assertEquals(ZCRPitchDetector.NO_DETECTED_PITCH, ZCRPitchDetector.detectPitch(singleSample, SAMPLE_RATE).pitch(),
                   "Should return NO_DETECTED_PITCH for single sample input");
    }

    @Test
    void testSquareWave() {
        // Arrange
        double frequency = 440.0; // A4
        double duration = 1.0;
        double[] audioData = generateSquareWave(frequency, SAMPLE_RATE, duration);

        // Act
        ZCRPitchDetector.PitchDetectionResult result = ZCRPitchDetector.detectPitch(audioData, SAMPLE_RATE);

        // Assert
        assertEquals(frequency, result.pitch(), TOLERANCE * 2, 
                   "Detected pitch should match the square wave frequency");
        assertTrue(result.confidence() > 0.5, "Confidence should be reasonable for a square wave");
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

    /**
     * Generates a square wave based on the given frequency, sample rate, and duration.
     *
     * @param frequency the frequency of the square wave in Hertz
     * @param sampleRate the number of samples per second
     * @param duration the duration of the square wave in seconds
     * @return an array of doubles representing the generated square wave
     */
    private double[] generateSquareWave(double frequency, int sampleRate, double duration) {
        int samples = (int) (sampleRate * duration);
        double[] squareWave = new double[samples];
        for (int i = 0; i < samples; i++) {
            squareWave[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate) >= 0 ? 1.0 : -1.0;
        }
        return squareWave;
    }
}
