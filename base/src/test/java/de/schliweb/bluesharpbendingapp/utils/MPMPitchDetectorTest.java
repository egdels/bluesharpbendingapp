package de.schliweb.bluesharpbendingapp.utils;

import static de.schliweb.bluesharpbendingapp.utils.AudioTestUtils.*;

import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for the MPM (McLeod Pitch Method) pitch detection algorithm.
 * <p>
 * This class tests the functionality of the MPM algorithm implementation in the PitchDetector class.
 * The MPM algorithm is a time-domain pitch detection method that uses the Normalized Square Difference
 * Function (NSDF) to identify the fundamental frequency of an audio signal.
 * <p>
 * The tests verify that:
 * 1. The algorithm correctly detects pitches across a range of frequencies
 * 2. It handles different amplitudes and signal types appropriately
 * 3. It works correctly with different harmonica frequency ranges
 * 4. It is robust to noise, silence, and non-periodic signals
 * 5. It correctly handles edge cases like zero-crossing waves and varying amplitudes
 * <p>
 * This test class uses utility methods from {@link AudioTestUtils} to generate test signals.
 */
class MPMPitchDetectorTest {

    private static final int SAMPLE_RATE = 44100;
    private static final double TOLERANCE = 1.0;

    @BeforeEach
    void setUp() {
        PitchDetector.setMaxFrequency(PitchDetector.getDefaultMaxFrequency());
        PitchDetector.setMinFrequency(PitchDetector.getDefaultMinFrequency());
    }

    @Test
    void testDetectPitch_PureSineWave() {
        // Arrange
        double frequency = 440.0; // A4
        double duration = 1.0;
        double[] audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

        // Act
        PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, SAMPLE_RATE);

        // Assert
        assertEquals(frequency, result.pitch(), TOLERANCE, "Detected pitch should match the input sine wave frequency");
        assertTrue(result.confidence() > 0.9, "Confidence should be high for a pure sine wave");
    }

    @Test
    void testDetectPitch_Silence() {
        // Arrange
        double[] audioData = new double[SAMPLE_RATE]; // 1 second of silence

        // Act
        PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, SAMPLE_RATE);

        // Assert
        assertEquals(PitchDetector.NO_DETECTED_PITCH, result.pitch(), "Should return NO_DETECTED_PITCH for silence");
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
          PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, SAMPLE_RATE);

        // Assert
        assertEquals(PitchDetector.NO_DETECTED_PITCH, result.pitch(), "Should return NO_DETECTED_PITCH for noise");
        assertEquals(0.0, result.confidence(), "Confidence should be 0.0 for noise");
    }

    @Test
    void testDetectPitch_LowFrequency() {
        // Arrange
        double frequency = 80.0; // Low frequency
        double duration = 1.0;
        double[] audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

        // Act
        PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, SAMPLE_RATE);

        // Assert
        assertEquals(frequency, result.pitch(), TOLERANCE, "Detected pitch should match the low frequency sine wave");
        assertTrue(result.confidence() > 0.9, "Confidence should be high for a low frequency sine wave");
    }

    @Test
    void testDetectPitch_HighFrequency() {
        // Arrange
        double frequency = 4000.0; // High frequency
        double duration = 1.0;
        double[] audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

        // Act
          PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, SAMPLE_RATE);

        // Assert
        assertEquals(frequency, result.pitch(), TOLERANCE, "Detected pitch should match the high frequency sine wave");
        assertTrue(result.confidence() > 0.9, "Confidence should be high for a high frequency sine wave");
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
          PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, SAMPLE_RATE);

        // Assert
        assertEquals(fundamentalFrequency, result.pitch(), TOLERANCE, "Detected pitch should match the fundamental frequency");
        assertTrue(result.confidence() > 0.9, "Confidence should be high for a signal with harmonics");
    }

    @ParameterizedTest
    @CsvSource({
        "440.0, 0.01, 1.0", // Low amplitude
        "440.0, 0.1, 1.0",  // Medium amplitude
        "440.0, 1.0, 1.0"   // High amplitude
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
          PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, SAMPLE_RATE);

        // Assert
        assertEquals(frequency, result.pitch(), tolerance, "Detected pitch should match the input frequency regardless of amplitude");
        assertTrue(result.confidence() > 0.9, "Confidence should be high regardless of amplitude");
    }

    @ParameterizedTest
    @CsvSource({
        "100.0, 1.0", // Low frequency
        "440.0, 1.0", // Medium frequency
        "2000.0, 5.0" // High frequency
    })
    void testDetectPitch_DifferentFrequencies(double frequency, double tolerance) {
        // Arrange
        double duration = 1.0;
        double[] audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

        // Act
          PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, SAMPLE_RATE);

        // Assert
        assertEquals(frequency, result.pitch(), tolerance, "Detected pitch should match the input frequency");
        assertTrue(result.confidence() > 0.9, "Confidence should be high for pure sine waves");
    }

    @Test
    void testFrequencyRangeSetting() {
        // Arrange
        double minFreq = 100.0;
        double maxFreq = 3000.0;
        PitchDetector.setMinFrequency(minFreq);
        PitchDetector.setMaxFrequency(maxFreq);

        double frequency = 440.0; // A4
        double duration = 1.0;
        double[] audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

        // Act
        PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, SAMPLE_RATE);

        // Assert
        assertEquals(frequency, result.pitch(), TOLERANCE, "Detected pitch should match the input frequency");
        assertEquals(minFreq, PitchDetector.getMinFrequency(), "Min frequency should be set correctly");
        assertEquals(maxFreq, PitchDetector.getMaxFrequency(), "Max frequency should be set correctly");
    }

    @Test
    void testFrequencyOutsideRange() {
        // Arrange
        double minFreq = 500.0;
        double maxFreq = 1000.0;
        PitchDetector.setMinFrequency(minFreq);
        PitchDetector.setMaxFrequency(maxFreq);

        double frequency = 440.0; // A4 (below min frequency)
        double duration = 1.0;
        double[] audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

        // Act
          PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, SAMPLE_RATE);

        assertEquals(PitchDetector.NO_DETECTED_PITCH, result.pitch(), "The detector should not detect a pitch outside the specified range");
    }

    @Test
    void testDefaultFrequencyRange() {
        // Arrange
        PitchDetector.setMinFrequency(PitchDetector.getDefaultMinFrequency());
        PitchDetector.setMaxFrequency(PitchDetector.getDefaultMaxFrequency());
        // Assert
        assertEquals(PitchDetector.getDefaultMinFrequency(), PitchDetector.getMinFrequency(),
                    "Default min frequency should match the default min frequency");
        assertEquals(PitchDetector.getDefaultMaxFrequency(), PitchDetector.getMaxFrequency(),
                    "Default max frequency should match the default max frequency");
    }

    @ParameterizedTest
    @MethodSource("provideHarmonicaParameters")
    void testMPM_Edges_Harmonica(AbstractHarmonica.KEY key, AbstractHarmonica.TUNE tune, double maxFrequencyTolerance, double minFrequencyTolerance) {
        Harmonica harmonica = AbstractHarmonica.create(key, tune);
        PitchDetector.setMaxFrequency(harmonica.getHarmonicaMaxFrequency());
        PitchDetector.setMinFrequency(harmonica.getHarmonicaMinFrequency());

        // Test for max frequency
        double[] sineWave = generateSineWave(harmonica.getHarmonicaMaxFrequency(), SAMPLE_RATE, 1.0);
          PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(sineWave, SAMPLE_RATE);
        assertTrue(result.confidence() >= 0.95);
        assertEquals(harmonica.getHarmonicaMaxFrequency(), result.pitch(), maxFrequencyTolerance,
                "Detected pitch should match the sine wave max frequency");
        // Test for min frequency
        sineWave = generateSineWave(harmonica.getHarmonicaMinFrequency(), SAMPLE_RATE, 1.0);
        result = PitchDetector.detectPitchMPM(sineWave, SAMPLE_RATE);
        assertTrue(result.confidence() >= 0.95);
        assertEquals(harmonica.getHarmonicaMinFrequency(), result.pitch(), minFrequencyTolerance,
                "Detected pitch should match the sine wave min frequency");
    }

    private static Stream<Arguments> provideHarmonicaParameters() {
        return Stream.of(
                Arguments.of(AbstractHarmonica.KEY.C, AbstractHarmonica.TUNE.RICHTER, 0.3, 0.01),
                Arguments.of(AbstractHarmonica.KEY.HA_FLAT, AbstractHarmonica.TUNE.RICHTER, 2, 0.01),
                Arguments.of(AbstractHarmonica.KEY.A, AbstractHarmonica.TUNE.RICHTER, 0.1, 0.01),
                Arguments.of(AbstractHarmonica.KEY.B, AbstractHarmonica.TUNE.RICHTER, 0.2, 0.01),
                Arguments.of(AbstractHarmonica.KEY.A_FLAT, AbstractHarmonica.TUNE.AUGMENTED, 0.1, 0.01),
                Arguments.of(AbstractHarmonica.KEY.D, AbstractHarmonica.TUNE.AUGMENTED, 0.3, 0.01),
                Arguments.of(AbstractHarmonica.KEY.D_FLAT, AbstractHarmonica.TUNE.AUGMENTED, 0.2, 0.01),
                Arguments.of(AbstractHarmonica.KEY.E, AbstractHarmonica.TUNE.COUNTRY, 0.4, 0.01),
                Arguments.of(AbstractHarmonica.KEY.E_FLAT, AbstractHarmonica.TUNE.COUNTRY, 0.3, 0.01),
                Arguments.of(AbstractHarmonica.KEY.LA, AbstractHarmonica.TUNE.COUNTRY, 0.1, 0.01),
                Arguments.of(AbstractHarmonica.KEY.LF_HASH, AbstractHarmonica.TUNE.DIMINISHED, 0.1, 0.01),
                Arguments.of(AbstractHarmonica.KEY.LG, AbstractHarmonica.TUNE.DIMINISHED, 0.1, 0.01),
                Arguments.of(AbstractHarmonica.KEY.LD_FLAT, AbstractHarmonica.TUNE.DIMINISHED, 0.1, 0.01),
                Arguments.of(AbstractHarmonica.KEY.B_FLAT, AbstractHarmonica.TUNE.PADDYRICHTER, 0.1, 0.01),
                Arguments.of(AbstractHarmonica.KEY.LLF, AbstractHarmonica.TUNE.PADDYRICHTER, 0.1, 0.01),
                Arguments.of(AbstractHarmonica.KEY.LLF, AbstractHarmonica.TUNE.PADDYRICHTER, 0.1, 0.01),
                Arguments.of(AbstractHarmonica.KEY.HA, AbstractHarmonica.TUNE.RICHTER, 1.2, 0.01)
        );
    }

    @ParameterizedTest
    @CsvSource({
            // mainFrequency, mainAmplitude, subharmonicFrequency, subharmonicAmplitude, tolerance, minConfidence
            "934.6, 1.0, 460.0, 0.3, 5.0, 0.8",  // Weak subharmonic, high confidence required
            "934.6, 1.0, 460.0, 0.5, 10.0, 0.3", // Moderate subharmonic
            "934.6, 1.9, 460.0, 1.0, 10.0, 0.1"  // Dominant main frequency, lower confidence acceptable
    })
    void testDetectPitchWithMPM_MixedFrequencies(double mainFrequency, double mainAmplitude, double subharmonicFrequency, double subharmonicAmplitude, double tolerance, double minConfidence) {
        int duration = 1; // 1 second

        // Generate a mixed signal with both main and subharmonic frequencies
        double[] mixedWave = generateMixedSineWaveWithAmplitudes(mainFrequency, mainAmplitude, subharmonicFrequency, subharmonicAmplitude, SAMPLE_RATE, duration);
        // Invoke the pitch detection algorithm to find the dominant frequency
          PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(mixedWave, SAMPLE_RATE);
        // Assert that the detected frequency is within the tolerance of the main frequency
        assertEquals(mainFrequency, result.pitch(), tolerance,
                "The detected frequency should be within the tolerance range of the main frequency.");
        // Assert that the confidence level is above the minimum threshold
        assertTrue(result.confidence() > minConfidence,
                "The confidence should be at least " + minConfidence);
    }


    /**
     * Verifies that the detectPitch method accurately detects the pitch of a sine wave
     * with zero-crossing behavior. The test generates a square-like waveform with alternating
     * positive and negative amplitudes at a specified frequency using a given sampling rate.
     */
    @Test
    void testDetectPitchWithMPM_ZeroCrossingWave() {
        int sampleRate = 44100;
        double frequency = 480.0; // Frequency of the sine wave
        double amplitude = 0.5;  // Amplitude of the sine wave
        double duration = 1.0;
        double[] audioData = generateSineWave(frequency, sampleRate, duration);
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] = (audioData[i] >= 0) ? amplitude : -amplitude; // Zero-crossing behavior
        }

          PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, sampleRate);
        double detectedPitch = result.pitch(); 
        assertEquals(frequency, detectedPitch, TOLERANCE, "Detected pitch should match the sine wave frequency with zero crossing behavior");
    }

    /**
     * Verifies detectPitch detects pitch for a sine wave with high amplitude.
     */
    @Test
    void testDetectPitchWithMPM_HighAmplitude() {
        int sampleRate = 44100;
        double frequency = 523.25; // Frequency of C5 note
        double amplitude = 1.0;   // High amplitude
        double duration = 1.0;
        double[] audioData = generateSineWave(frequency, sampleRate, duration);
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] *= amplitude; // Scale to high amplitude
        }

          PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertEquals(frequency, detectedPitch, TOLERANCE, "Detected pitch should match the sine wave frequency even with high amplitude");
    }

    /**
     * Validates detectPitch for a synthesized waveform designed to test refined minimum detection.
     */
    @Test
    void testDetectPitchWithMPM_RefinedMinimum() {
        int sampleRate = 44100;
        double frequency = 300.0; // Pitch to detect
        double duration = 1.0;    // 1 second of audio

        double[] audioData = generateSineWave(frequency, sampleRate, duration);
        double[] harmonic = generateSineWave(2 * frequency, sampleRate, duration);
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] += 0.3 * harmonic[i]; // Add harmonic
        }

          PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertEquals(frequency, detectedPitch, TOLERANCE, "Detected pitch should match the synthesized waveform's base frequency");
    }

    /**
     * Tests detectPitch with a linearly increasing signal.
     */
    @Test
    void testDetectPitchWithMPM_LinearlyIncreasingSignal() {
        int sampleRate = 44100;
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];

        // Linearly increasing signal
        for (int i = 0; i < samples; i++) {
            audioData[i] = i / (double) samples;
        }

          PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertEquals(PitchDetector.NO_DETECTED_PITCH, detectedPitch, "Detected pitch should be NO_DETECTED_PITCH for a linearly increasing signal");
    }

    /**
     * Ensures detectPitch returns NO_DETECTED_PITCH for non-periodic signals.
     */
    @Test
    void testDetectPitchWithMPM_NonPeriodicSignal() {
        int sampleRate = 44100;
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];

        // Non-periodic signal
        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.random() * 2 - 1; // Random values between -1 and 1
        }

          PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertEquals(PitchDetector.NO_DETECTED_PITCH, detectedPitch, "Detected pitch should be NO_DETECTED_PITCH for non-periodic signals");
    }

    /**
     * Validates detectPitch on a sine wave with varying amplitude.
     */
    @Test
    void testDetectPitchWithMPM_VaryingAmplitudeWave() {
        int sampleRate = 44100;
        double frequency = 440.0; // A4 frequency
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];

        // Sine wave with varying amplitude (linearly increasing)
        for (int i = 0; i < samples; i++) {
            double amplitude = (double) i / samples; // Scale amplitude from 0 to 1
            audioData[i] = amplitude * Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }

          PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertEquals(frequency, detectedPitch, TOLERANCE, "Detected pitch should match the sine wave frequency despite varying amplitude");
    }

    /**
     * Ensures detectPitch detects pitch correctly for a mid-frequency sine wave.
     */
    @Test
    void testDetectPitchWithMPM_MidFrequencyWave() {
        int sampleRate = 44100;
        double frequency = 250.0; // Mid-frequency sine wave
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }

          PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertEquals(frequency, detectedPitch, TOLERANCE, "Detected pitch should match the sine wave frequency");
    }

    /**
     * Ensures detectPitch detects pitch correctly with white noise layered over a sine wave.
     */
    @Test
    void testDetectPitchWithMPM_SineWithNoise() {
        int sampleRate = 44100;
        double frequency = 440.0; // Sine wave
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate) +
                    (Math.random() * 0.3 - 0.15); // Adding random noise
        }

          PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertEquals(frequency, detectedPitch, TOLERANCE, "Detected pitch should match the sine wave frequency despite noise");
    }

    /**
     * Ensures detectPitch can detect low amplitude sine waves.
     */
    @Test
    void testDetectPitchWithMPM_LowAmplitude() {
        int sampleRate = 44100;
        double frequency = 440.0; // A4
        double amplitude = 0.01; // Low amplitude
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = amplitude * Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }

          PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertEquals(frequency, detectedPitch, TOLERANCE, "Detected pitch should match the sine wave frequency even at low amplitude");
    }

    /**
     * Ensures detectPitch can correctly calculate pitch for a low-frequency sine wave.
     */
    @Test
    void testDetectPitchWithMPM_LowFrequencySineWave() {
        int sampleRate = 44100;
        PitchDetector.setMinFrequency(10);
        double frequency = 80.0; // Low frequency sine wave
        double duration = 1.0;
        double[] audioData = generateSineWave(frequency, sampleRate, duration);

          PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertEquals(frequency, detectedPitch, TOLERANCE, "Detected pitch should match the low-frequency sine wave");
    }

    /**
     * Ensures detectPitch detects pitch correctly for a high-frequency sine wave.
     */
    @Test
    void testDetectPitchWithMPM_HighFrequencySineWave() {
        int sampleRate = 44100;
        double frequency = 4000.0; // High frequency sine wave
        double duration = 1.0;

        double[] audioData = generateSineWave(frequency, sampleRate, duration);

          PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertEquals(frequency, detectedPitch, TOLERANCE, "Detected pitch should match the high-frequency sine wave");
    }

    /**
     * Confirms detectPitch returns valid and meaningful results with non-zero values.
     */
    @Test
    void testDetectPitchWithMPM_SimpleInput() {
        int sampleRate = 48000;
        double frequency = 300.0; // Sine wave frequency
        double[] audioData = new double[1024];
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }
          PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertTrue(detectedPitch > 0, "Detected pitch should be a positive number for simple input");
    }
}
