package de.schliweb.bluesharpbendingapp.utils;
/*
 * Copyright (c) 2023 Christian Kierdorf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */

import static de.schliweb.bluesharpbendingapp.utils.AudioTestUtils.*;
import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for the YIN pitch detection algorithm.
 * <p>
 * This class tests the functionality of the YIN algorithm implementation in the PitchDetector class.
 * The YIN algorithm is a fundamental frequency estimator based on the autocorrelation method,
 * with several improvements to increase accuracy and efficiency.
 * <p>
 * The tests verify that:
 * 1. The algorithm correctly detects pitches across a range of frequencies
 * 2. It handles mixed frequencies and subharmonics appropriately
 * 3. It works correctly with different harmonica frequency ranges
 * 4. It is robust to noise and silence
 * <p>
 * This test class uses utility methods from {@link AudioTestUtils} to generate test signals.
 */
class YINPitchDetectorTest {

    private static final int SAMPLE_RATE = 44100;
    private static final double TOLERANCE = 0.5;

    @BeforeEach
    void setUp() {
        PitchDetector.setMaxFrequency(PitchDetector.getDefaultMaxFrequency());
        PitchDetector.setMinFrequency(PitchDetector.getDefaultMinFrequency());
    }

    /**
     * Tests the YIN algorithm's ability to detect the dominant frequency in a mixed signal.
     * <p>
     * This test verifies that when presented with a signal containing both a main frequency
     * and a subharmonic frequency at different amplitudes, the YIN algorithm correctly
     * identifies the main frequency as the dominant one.
     * <p>
     * The test uses different combinations of amplitudes to test the algorithm's robustness:
     * - Weak subharmonic: Main frequency should be easily detected with high confidence
     * - Moderate subharmonic: Main frequency should still be detected but with lower confidence
     * - Strong subharmonic: Main frequency should still be detected but with even lower confidence
     *
     * @param mainFrequency         the primary frequency component in Hz
     * @param mainAmplitude         the amplitude of the primary frequency
     * @param subharmonicFrequency  the secondary frequency component in Hz
     * @param subharmonicAmplitude  the amplitude of the secondary frequency
     * @param tolerance             the acceptable error margin in Hz
     * @param minConfidence         the minimum expected confidence value
     */
    @ParameterizedTest
    @CsvSource({
            // mainFrequency, mainAmplitude, subharmonicFrequency, subharmonicAmplitude, tolerance, minConfidence
            "934.6, 1.0, 460.0, 0.3, 5.0, 0.8",  // Weak subharmonic, high confidence required
            "934.6, 1.0, 460.0, 0.5, 10.0, 0.3", // Moderate subharmonic
            "934.6, 1.9, 460.0, 1.0, 10.0, 0.1"  // Dominant main frequency, lower confidence acceptable
    })
    void testDetectPitchWithMixedFrequencies(double mainFrequency, double mainAmplitude, 
                                           double subharmonicFrequency, double subharmonicAmplitude, 
                                           double tolerance, double minConfidence) {
        int duration = 1; // 1 second

        // Generate a mixed signal with both main and subharmonic frequencies
        double[] mixedWave = generateMixedSineWaveWithAmplitudes(
                mainFrequency, mainAmplitude, 
                subharmonicFrequency, subharmonicAmplitude, 
                SAMPLE_RATE, duration);

        // Invoke the pitch detection algorithm to find the dominant frequency
        PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchYIN(mixedWave, SAMPLE_RATE);

        // Assert that the detected frequency is within the tolerance of the main frequency
        assertEquals(mainFrequency, result.pitch(), tolerance,
                "The detected frequency should be within the tolerance range of the main frequency.");

        // Assert that the confidence level is above the minimum threshold
        assertTrue(result.confidence() > minConfidence,
                "The confidence should be at least " + minConfidence);
    }

    /**
     * Tests the YIN algorithm's ability to detect pitches at the extreme ends of harmonica frequency ranges.
     * <p>
     * This test verifies that the YIN algorithm can accurately detect both the minimum and maximum
     * frequencies of various harmonica types. This is important because harmonicas span a wide
     * range of frequencies, and the algorithm needs to be accurate across this entire range.
     * <p>
     * The test uses different harmonica types (keys and tunings) to test different frequency ranges.
     * For each harmonica, it tests both the minimum and maximum frequencies with appropriate tolerances.
     *
     * @param key                   the key of the harmonica
     * @param tune                  the tuning of the harmonica
     * @param maxFrequencyTolerance the acceptable error margin for the maximum frequency in Hz
     * @param minFrequencyTolerance the acceptable error margin for the minimum frequency in Hz
     */
    @ParameterizedTest
    @MethodSource("provideHarmonicaParameters")
    void testYIN_Edges_Harmonica(AbstractHarmonica.KEY key, AbstractHarmonica.TUNE tune, 
                               double maxFrequencyTolerance, double minFrequencyTolerance) {
        Harmonica harmonica = AbstractHarmonica.create(key, tune);
        PitchDetector.setMaxFrequency(harmonica.getHarmonicaMaxFrequency());
        PitchDetector.setMinFrequency(harmonica.getHarmonicaMinFrequency());

        // Test for max frequency
        double[] sineWave = generateSineWave(harmonica.getHarmonicaMaxFrequency(), SAMPLE_RATE, 1.0);
        PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchYIN(sineWave, SAMPLE_RATE);
        assertTrue(result.confidence() >= 0.95, 
                "Confidence should be high (>= 0.95) for max frequency of " + key + " " + tune);
        assertEquals(harmonica.getHarmonicaMaxFrequency(), result.pitch(), maxFrequencyTolerance,
                "Detected pitch should match the sine wave max frequency");

        // Test for min frequency
        sineWave = generateSineWave(harmonica.getHarmonicaMinFrequency(), SAMPLE_RATE, 1.0);
        result = PitchDetector.detectPitchYIN(sineWave, SAMPLE_RATE);
        assertTrue(result.confidence() >= 0.95,
                "Confidence should be high (>= 0.95) for min frequency of " + key + " " + tune);
        assertEquals(harmonica.getHarmonicaMinFrequency(), result.pitch(), minFrequencyTolerance,
                "Detected pitch should match the sine wave min frequency");
    }

    /**
     * Provides test parameters for different harmonica types.
     * <p>
     * This method generates a stream of arguments for parameterized tests that need to test
     * different harmonica types. Each argument set includes:
     * - The key of the harmonica (e.g., C, A, B)
     * - The tuning of the harmonica (e.g., RICHTER, AUGMENTED, COUNTRY)
     * - The tolerance for maximum frequency detection in Hz
     * - The tolerance for minimum frequency detection in Hz
     * <p>
     * The tolerances vary based on the harmonica type, with higher tolerances for harmonicas
     * with extreme frequency ranges.
     *
     * @return a stream of arguments for parameterized tests
     */
    private static Stream<Arguments> provideHarmonicaParameters() {
        return Stream.of(
                // Standard diatonic harmonicas
                Arguments.of(AbstractHarmonica.KEY.C, AbstractHarmonica.TUNE.RICHTER, 3.0, 0.01),
                Arguments.of(AbstractHarmonica.KEY.A, AbstractHarmonica.TUNE.RICHTER, 3.0, 0.01),
                Arguments.of(AbstractHarmonica.KEY.B, AbstractHarmonica.TUNE.RICHTER, 3.0, 0.01),

                // Augmented tuning harmonicas
                Arguments.of(AbstractHarmonica.KEY.A_FLAT, AbstractHarmonica.TUNE.AUGMENTED, 3.0, 0.01),
                Arguments.of(AbstractHarmonica.KEY.D, AbstractHarmonica.TUNE.AUGMENTED, 5.0, 0.01),
                Arguments.of(AbstractHarmonica.KEY.D_FLAT, AbstractHarmonica.TUNE.AUGMENTED, 20.0, 0.01),

                // Country tuning harmonicas
                Arguments.of(AbstractHarmonica.KEY.E, AbstractHarmonica.TUNE.COUNTRY, 50.0, 0.01),
                Arguments.of(AbstractHarmonica.KEY.E_FLAT, AbstractHarmonica.TUNE.COUNTRY, 5.0, 0.01),
                Arguments.of(AbstractHarmonica.KEY.LA, AbstractHarmonica.TUNE.COUNTRY, 3.0, 0.01),

                // Diminished tuning harmonicas
                Arguments.of(AbstractHarmonica.KEY.LF_HASH, AbstractHarmonica.TUNE.DIMINISHED, 3.0, 0.01),
                Arguments.of(AbstractHarmonica.KEY.LG, AbstractHarmonica.TUNE.DIMINISHED, 3.0, 0.01),
                Arguments.of(AbstractHarmonica.KEY.LD_FLAT, AbstractHarmonica.TUNE.DIMINISHED, 3.0, 0.01),

                // Paddy Richter tuning harmonicas
                Arguments.of(AbstractHarmonica.KEY.B_FLAT, AbstractHarmonica.TUNE.PADDYRICHTER, 3.0, 0.01),
                Arguments.of(AbstractHarmonica.KEY.LLF, AbstractHarmonica.TUNE.PADDYRICHTER, 3.0, 0.01),

                // High-pitched harmonica with larger tolerance
                Arguments.of(AbstractHarmonica.KEY.HA, AbstractHarmonica.TUNE.RICHTER, 13.0, 0.02)
        );
    }

    /**
     * Tests the YIN algorithm's robustness to noise.
     * <p>
     * This test verifies that the YIN algorithm can still accurately detect the fundamental
     * frequency of a sine wave when it is combined with a small amount of white noise.
     * This is important for real-world applications where audio signals often contain noise.
     * <p>
     * The test uses a sine wave of 934.6 Hz with 10% white noise added, and verifies that
     * the detected pitch is still close to the original frequency with reasonable confidence.
     */
    @Test
    void testFrequencyWithWhiteNoise() {
        double frequency = 934.6;
        int durationMs = 1000; // 1 second
        double noiseLevel = 0.1; // White noise as 10% of the signal amplitude
        double[] noisyWave = generateSineWaveWithNoise(frequency, SAMPLE_RATE, durationMs, noiseLevel);

        PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchYIN(noisyWave, SAMPLE_RATE);

        assertEquals(frequency, result.pitch(), TOLERANCE,
                "The detected frequency should still be 934.6 Hz, even with white noise.");
        assertTrue(result.confidence() > 0.6, "The confidence should be high enough (> 0.6).");
    }

    /**
     * Tests the YIN algorithm with a pure sine wave at the standard concert pitch A4 (440 Hz).
     * <p>
     * This test verifies that the YIN algorithm can accurately detect the pitch of a pure
     * sine wave with no noise or harmonics. This is a baseline test that any pitch detection
     * algorithm should pass with high accuracy.
     * <p>
     * The test uses a sine wave at 440 Hz (A4), which is the standard concert pitch used
     * for tuning musical instruments.
     */
    @Test
    void testPureSineWave() {
        double frequency = 440.0; // A4
        double duration = 1.0;
        double[] audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

        PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchYIN(audioData, SAMPLE_RATE);

        assertEquals(frequency, result.pitch(), 0.02, 
                "Detected pitch should match the input sine wave frequency");
        assertTrue(result.confidence() > 0.95, 
                "Confidence should be very high (> 0.95) for a pure sine wave");
    }

    /**
     * Tests the YIN algorithm's behavior with silence (all zeros).
     * <p>
     * This test verifies that the YIN algorithm correctly identifies silence as having
     * no detectable pitch. This is important for real-world applications where there
     * may be periods of silence in the audio signal.
     * <p>
     * The test uses different sample sizes to ensure the behavior is consistent regardless
     * of the buffer size.
     *
     * @param sampleSize the size of the audio buffer to test
     */
    @ParameterizedTest
    @ValueSource(ints = {44100, 48000})
    void testSilence(int sampleSize) {
        double[] audioData = new double[sampleSize]; // All zeros (silence)

        PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchYIN(audioData, SAMPLE_RATE);

        assertEquals(PitchDetector.NO_DETECTED_PITCH, result.pitch(), 
                "Detected pitch should be NO_DETECTED_PITCH for silence");
        assertEquals(0.0, result.confidence(), 
                "Confidence should be 0.0 for silence");
    }

    /**
     * Tests the YIN algorithm's behavior with white noise.
     * <p>
     * This test verifies that the YIN algorithm correctly identifies random noise as having
     * no detectable pitch. This is important for real-world applications where there
     * may be periods of noise in the audio signal.
     * <p>
     * The test uses random values between -1 and 1 to simulate white noise.
     */
    @Test
    void testNoise() {
        double[] audioData = generateWhiteNoise(SAMPLE_RATE, 0.045); // ~2000 samples

        PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchYIN(audioData, SAMPLE_RATE);

        assertEquals(PitchDetector.NO_DETECTED_PITCH, result.pitch(), 
                "Detected pitch should be NO_DETECTED_PITCH for noise");
        assertEquals(0.0, result.confidence(), 
                "Confidence should be 0.0 for noise");
    }


}
