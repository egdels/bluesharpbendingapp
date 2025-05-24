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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for the FFT pitch detection algorithm with focus on frequencies at and above 1000 Hz.
 * <p>
 * This class tests the functionality of the FFT algorithm implementation in the FFTDetector class,
 * specifically for higher frequencies (≥ 1000 Hz) that are relevant for harmonica playing.
 * <p>
 * The tests verify that:
 * 1. The algorithm correctly detects pitches at and above 1000 Hz
 * 2. It handles mixed frequencies and harmonics appropriately at higher frequencies
 * 3. It is robust to noise at higher frequencies
 * 4. It can detect rapid frequency changes in the higher frequency range
 * <p>
 * This test class uses utility methods from {@link AudioTestUtils} to generate test signals.
 */
class FFTDetectorTest {

    private static final int SAMPLE_RATE = 44100;
    private static final double TOLERANCE_HIGH_FREQ = 10.0; // Higher tolerance for high frequencies
    private static final double TOLERANCE_LOW_FREQ = 20.0; // Even higher tolerance for low frequencies
    private static final double TOLERANCE_MID_FREQ = 15.0; // Medium tolerance for mid-range frequencies (300-1000 Hz)

    private FFTDetector fftDetector;

    @BeforeEach
    void setUp() {
        fftDetector = new FFTDetector();
        // Set frequency range to include high frequencies
        PitchDetector.setMinFrequency(1000.0); // Focus on frequencies from 1000 Hz
        PitchDetector.setMaxFrequency(4000.0); // Up to 4000 Hz
    }

    /**
     * Tests the FFT algorithm's ability to detect pure sine waves at various high frequencies.
     * <p>
     * This test verifies that the FFT algorithm can accurately detect the pitch of pure
     * sine waves at frequencies at and above 1000 Hz. This is important for harmonica
     * playing where many notes fall in this higher frequency range.
     *
     * @param frequency the frequency of the sine wave to test in Hz
     */
    @ParameterizedTest
    @ValueSource(doubles = {1200.0, 1500.0, 1800.0, 2000.0, 2500.0, 3000.0, 3500.0})
    void testPureSineWaveHighFrequencies(double frequency) {
        double duration = 1.0; // 1 second for better FFT analysis
        double[] audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

        PitchDetector.PitchDetectionResult result = fftDetector.detectPitch(audioData, SAMPLE_RATE);

        assertEquals(frequency, result.pitch(), TOLERANCE_HIGH_FREQ, 
                "Detected pitch should match the input sine wave frequency: " + frequency + " Hz");
        assertTrue(result.confidence() > 0.7, 
                "Confidence should be high (> 0.7) for a pure sine wave at " + frequency + " Hz");
    }

    /**
     * Tests the FFT algorithm's ability to detect the dominant frequency in a mixed signal
     * at high frequencies.
     * <p>
     * This test verifies that when presented with a signal containing both a main high frequency
     * and a harmonic or subharmonic, the FFT algorithm correctly identifies the main frequency
     * as the dominant one.
     *
     * @param mainFrequency         the primary frequency component in Hz
     * @param mainAmplitude         the amplitude of the primary frequency
     * @param secondaryFrequency    the secondary frequency component in Hz
     * @param secondaryAmplitude    the amplitude of the secondary frequency
     * @param tolerance             the acceptable error margin in Hz
     * @param minConfidence         the minimum expected confidence value
     */
    @ParameterizedTest
    @CsvSource({
            // mainFrequency, mainAmplitude, secondaryFrequency, secondaryAmplitude, tolerance, minConfidence
            "1500.0, 1.0, 750.0, 0.2, 15.0, 0.5",    // Main frequency with subharmonic (weaker)
            "2000.0, 1.0, 4000.0, 0.3, 15.0, 0.5",   // Main frequency with harmonic (weaker)
            "2500.0, 1.0, 1250.0, 0.25, 20.0, 0.4"   // Main frequency with subharmonic (weaker)
    })
    void testDetectPitchWithMixedHighFrequencies(double mainFrequency, double mainAmplitude, 
                                               double secondaryFrequency, double secondaryAmplitude, 
                                               double tolerance, double minConfidence) {
        double duration = 2.0; // 2 seconds for better FFT analysis with mixed signals

        // Generate a mixed signal with both main and secondary frequencies
        double[] mixedWave = generateMixedSineWaveWithAmplitudes(
                mainFrequency, mainAmplitude, 
                secondaryFrequency, secondaryAmplitude, 
                SAMPLE_RATE, (int)duration);

        // Invoke the pitch detection algorithm to find the dominant frequency
        PitchDetector.PitchDetectionResult result = fftDetector.detectPitch(mixedWave, SAMPLE_RATE);

        // Assert that the detected frequency is within the tolerance of the main frequency
        assertEquals(mainFrequency, result.pitch(), tolerance,
                "The detected frequency should be within the tolerance range of the main frequency.");

        // Assert that the confidence level is above the minimum threshold
        assertTrue(result.confidence() > minConfidence,
                "The confidence should be at least " + minConfidence);
    }

    /**
     * Tests the FFT algorithm's robustness to noise at high frequencies.
     * <p>
     * This test verifies that the FFT algorithm can still accurately detect the fundamental
     * frequency of a sine wave at high frequencies when it is combined with white noise.
     * This is important for real-world applications where audio signals often contain noise.
     *
     * @param frequency the frequency of the sine wave to test in Hz
     * @param noiseLevel the level of noise as a fraction of the signal amplitude
     */
    @ParameterizedTest
    @CsvSource({
            // frequency, noiseLevel
            "1500.0, 0.1",  // 10% noise
            "2000.0, 0.15", // 15% noise
            "2500.0, 0.1"   // 10% noise
    })
    void testHighFrequencyWithWhiteNoise(double frequency, double noiseLevel) {
        int durationMs = 1000; // 1 second for better FFT analysis with noise
        double[] noisyWave = generateSineWaveWithNoise(frequency, SAMPLE_RATE, durationMs, noiseLevel);

        PitchDetector.PitchDetectionResult result = fftDetector.detectPitch(noisyWave, SAMPLE_RATE);

        assertEquals(frequency, result.pitch(), TOLERANCE_HIGH_FREQ,
                "The detected frequency should still be " + frequency + " Hz, even with " + (noiseLevel * 100) + "% white noise.");
        assertTrue(result.confidence() > 0.4, 
                "The confidence should be high enough (> 0.4) even with noise.");
    }

    /**
     * Tests the FFT algorithm's ability to detect high frequencies with varying amplitudes.
     * <p>
     * This test verifies that the FFT algorithm can accurately detect the pitch of sine waves
     * at high frequencies even when the amplitude varies over time. This is important for
     * real-world applications where the amplitude of a signal may not be constant.
     *
     * @param frequency the frequency of the sine wave to test in Hz
     */
    @ParameterizedTest
    @ValueSource(doubles = {1500.0, 2000.0, 2500.0})
    void testHighFrequencyWithVaryingAmplitude(double frequency) {
        double duration = 1.5; // 1.5 seconds for better FFT analysis with varying amplitude
        double[] varyingAmplitudeWave = generateSineWaveWithVaryingAmplitude(frequency, SAMPLE_RATE, duration);

        PitchDetector.PitchDetectionResult result = fftDetector.detectPitch(varyingAmplitudeWave, SAMPLE_RATE);

        assertEquals(frequency, result.pitch(), TOLERANCE_HIGH_FREQ,
                "The detected frequency should be " + frequency + " Hz, even with varying amplitude.");
        assertTrue(result.confidence() > 0.4, 
                "The confidence should be high enough (> 0.4) even with varying amplitude.");
    }

    /**
     * Tests the FFT algorithm's ability to detect very high frequencies near the upper limit.
     * <p>
     * This test verifies that the FFT algorithm can accurately detect frequencies near the
     * upper limit of the detectable range. This is important for ensuring the algorithm
     * works correctly across the entire specified frequency range.
     */
    @Test
    void testVeryHighFrequencies() {
        // Test frequencies near the upper limit, but not at the extreme edge
        double[] frequencies = {3500.0, 3700.0, 3900.0};
        double duration = 1.5; // 1.5 seconds for better FFT analysis at high frequencies

        for (double frequency : frequencies) {
            double[] audioData = generateSineWave(frequency, SAMPLE_RATE, duration);
            PitchDetector.PitchDetectionResult result = fftDetector.detectPitch(audioData, SAMPLE_RATE);

            assertEquals(frequency, result.pitch(), TOLERANCE_HIGH_FREQ,
                    "The detected frequency should be " + frequency + " Hz at the upper limit.");
            assertTrue(result.confidence() > 0.6, 
                    "The confidence should be high enough (> 0.6) even at very high frequencies.");
        }
    }

    /**
     * Tests the FFT algorithm's behavior with silence (all zeros).
     * <p>
     * This test verifies that the FFT algorithm correctly identifies silence as having
     * no detectable pitch, even when configured for high frequencies.
     */
    @Test
    void testSilence() {
        double[] audioData = new double[SAMPLE_RATE / 2]; // 0.5 seconds of silence

        PitchDetector.PitchDetectionResult result = fftDetector.detectPitch(audioData, SAMPLE_RATE);

        assertEquals(PitchDetector.NO_DETECTED_PITCH, result.pitch(), 
                "Detected pitch should be NO_DETECTED_PITCH for silence");
        assertEquals(0.0, result.confidence(), 
                "Confidence should be 0.0 for silence");
    }

    /**
     * Tests the FFT algorithm's behavior with white noise.
     * <p>
     * This test verifies that the FFT algorithm correctly identifies random noise as having
     * no detectable pitch, even when configured for high frequencies.
     */
    @Test
    void testNoise() {
        double[] audioData = generateWhiteNoise(SAMPLE_RATE, 0.5); // 0.5 seconds of noise

        PitchDetector.PitchDetectionResult result = fftDetector.detectPitch(audioData, SAMPLE_RATE);

        // The FFT algorithm might detect some frequency in noise, but the confidence should be relatively low
        assertTrue(result.confidence() < 0.4, 
                "Confidence should be relatively low (< 0.4) for noise, but was " + result.confidence());
    }

    /**
     * Tests the FFT algorithm's ability to detect rapid frequency transitions in high frequency ranges.
     * <p>
     * This test verifies that the FFT algorithm can accurately detect the frequency before and after
     * a rapid transition between two high frequencies. This is important for real-time applications
     * where the frequency may change quickly, such as when playing fast passages on a harmonica.
     *
     * @param freq1 the first frequency in Hz
     * @param freq2 the second frequency in Hz
     */
    @ParameterizedTest
    @CsvSource({
            // freq1, freq2
            "1500.0, 2000.0",  // Upward transition
            "2500.0, 1800.0",  // Downward transition
            "3000.0, 3500.0"   // High frequency transition
    })
    void testHighFrequencyTransition(double freq1, double freq2) {
        double duration = 2.0; // 2 seconds total
        double transitionPoint = 0.5; // Transition at 1 second

        // Generate signal with abrupt frequency transition
        double[] audioData = generateFrequencyTransition(freq1, freq2, SAMPLE_RATE, duration, transitionPoint);

        // Analyze first half (before transition)
        double[] firstHalf = new double[SAMPLE_RATE / 2]; // 0.5 seconds of data
        System.arraycopy(audioData, 0, firstHalf, 0, firstHalf.length);
        PitchDetector.PitchDetectionResult result1 = fftDetector.detectPitch(firstHalf, SAMPLE_RATE);

        // Analyze second half (after transition)
        double[] secondHalf = new double[SAMPLE_RATE / 2]; // 0.5 seconds of data
        System.arraycopy(audioData, SAMPLE_RATE, secondHalf, 0, secondHalf.length);
        PitchDetector.PitchDetectionResult result2 = fftDetector.detectPitch(secondHalf, SAMPLE_RATE);

        // Verify that the detected frequencies match the expected frequencies
        assertEquals(freq1, result1.pitch(), TOLERANCE_HIGH_FREQ,
                "The detected frequency before transition should be " + freq1 + " Hz");
        assertEquals(freq2, result2.pitch(), TOLERANCE_HIGH_FREQ,
                "The detected frequency after transition should be " + freq2 + " Hz");

        // Verify that the confidence is high enough
        assertTrue(result1.confidence() > 0.6, 
                "The confidence for the first frequency should be high enough (> 0.6)");
        assertTrue(result2.confidence() > 0.6, 
                "The confidence for the second frequency should be high enough (> 0.6)");
    }

    /**
     * Tests the FFT algorithm's ability to detect the dominant frequency in a signal
     * with multiple high frequency components.
     * <p>
     * This test verifies that when presented with a signal containing multiple high frequency
     * components with different amplitudes, the FFT algorithm correctly identifies the
     * strongest frequency as the dominant one.
     */
    @Test
    void testMultipleHighFrequencies() {
        // Test with 3 high frequencies, with the middle one being dominant
        double[] frequencies = {1500.0, 2000.0, 2500.0};
        double[] amplitudes = {0.3, 1.0, 0.4}; // Middle frequency is dominant
        double duration = 2.0; // 2 seconds for better FFT analysis

        double[] audioData = generateMultipleFrequencySignal(frequencies, amplitudes, SAMPLE_RATE, duration);
        PitchDetector.PitchDetectionResult result = fftDetector.detectPitch(audioData, SAMPLE_RATE);

        // Verify that the detected frequency is the dominant one (2000 Hz)
        assertEquals(frequencies[1], result.pitch(), TOLERANCE_HIGH_FREQ,
                "The detected frequency should be the dominant one: " + frequencies[1] + " Hz");
        assertTrue(result.confidence() > 0.5, 
                "The confidence should be high enough (> 0.5) for the dominant frequency");
    }

    /**
     * Tests the FFT algorithm's ability to track frequency sweeps in high frequency ranges.
     * <p>
     * This test verifies that the FFT algorithm can accurately detect frequencies at various
     * points during a frequency sweep. This is important for analyzing sounds with changing
     * frequencies, such as bending notes on a harmonica.
     *
     * @param startFreq the starting frequency of the sweep in Hz
     * @param endFreq   the ending frequency of the sweep in Hz
     */
    @ParameterizedTest
    @CsvSource({
            // startFreq, endFreq
            "1200.0, 1800.0",  // Upward sweep in mid-high range
            "2500.0, 2000.0",  // Downward sweep in high range
            "3000.0, 3500.0"   // Upward sweep in very high range
    })
    void testHighFrequencySweep(double startFreq, double endFreq) {
        double duration = 3.0; // 3 seconds for better analysis of the sweep

        // Generate frequency sweep signal
        double[] audioData = generateFrequencySweep(startFreq, endFreq, SAMPLE_RATE, duration);

        // Analyze beginning of sweep (should be close to startFreq)
        double[] beginning = new double[SAMPLE_RATE / 2]; // 0.5 seconds
        System.arraycopy(audioData, 0, beginning, 0, beginning.length);
        PitchDetector.PitchDetectionResult resultBegin = fftDetector.detectPitch(beginning, SAMPLE_RATE);

        // Analyze middle of sweep (should be close to average of startFreq and endFreq)
        double[] middle = new double[SAMPLE_RATE / 2]; // 0.5 seconds
        System.arraycopy(audioData, SAMPLE_RATE, middle, 0, middle.length);
        PitchDetector.PitchDetectionResult resultMiddle = fftDetector.detectPitch(middle, SAMPLE_RATE);

        // Analyze end of sweep (should be close to endFreq)
        double[] end = new double[SAMPLE_RATE / 2]; // 0.5 seconds
        System.arraycopy(audioData, 2 * SAMPLE_RATE, end, 0, end.length);
        PitchDetector.PitchDetectionResult resultEnd = fftDetector.detectPitch(end, SAMPLE_RATE);

        // Expected middle frequency
        double expectedMiddleFreq = (startFreq + endFreq) / 2.0;

        // Verify that the detected frequencies are close to the expected frequencies
        // Use a higher tolerance for frequency sweeps (10% of the frequency value)
        // This accounts for the inherent limitations of FFT when analyzing signals with changing frequencies
        // End frequencies need even higher tolerance (10%) as they are harder to detect accurately
        double startTolerance = Math.max(TOLERANCE_HIGH_FREQ * 2, startFreq * 0.05);
        double middleTolerance = Math.max(TOLERANCE_HIGH_FREQ * 2, expectedMiddleFreq * 0.05);
        double endTolerance = Math.max(TOLERANCE_HIGH_FREQ * 2, endFreq * 0.10);

        assertEquals(startFreq, resultBegin.pitch(), startTolerance,
                "The detected frequency at the beginning should be close to " + startFreq + " Hz");
        assertEquals(expectedMiddleFreq, resultMiddle.pitch(), middleTolerance,
                "The detected frequency in the middle should be close to " + expectedMiddleFreq + " Hz");
        assertEquals(endFreq, resultEnd.pitch(), endTolerance,
                "The detected frequency at the end should be close to " + endFreq + " Hz");
    }

    /**
     * Tests the FFT algorithm's ability to detect harmonica-specific high frequency patterns.
     * <p>
     * This test simulates typical harmonica high frequency patterns, including a fundamental
     * frequency with harmonics at specific amplitude ratios. This is important for ensuring
     * the algorithm works well with real harmonica sounds.
     */
    @Test
    void testHarmonicaHighFrequencyPatterns() {
        // Simulate a high harmonica note with fundamental and harmonics
        // Typical harmonica note has strong fundamental and 2nd harmonic, weaker 3rd and 4th harmonics
        double fundamental = 1500.0; // High C note
        double[] frequencies = {
                fundamental,           // Fundamental
                fundamental * 2.0,     // 2nd harmonic (octave)
                fundamental * 3.0,     // 3rd harmonic
                fundamental * 4.0      // 4th harmonic
        };
        double[] amplitudes = {1.0, 0.7, 0.4, 0.2}; // Typical harmonica harmonic amplitudes
        double duration = 2.0; // 2 seconds for better FFT analysis

        double[] audioData = generateMultipleFrequencySignal(frequencies, amplitudes, SAMPLE_RATE, duration);
        PitchDetector.PitchDetectionResult result = fftDetector.detectPitch(audioData, SAMPLE_RATE);

        // Verify that the detected frequency is the fundamental
        assertEquals(fundamental, result.pitch(), TOLERANCE_HIGH_FREQ,
                "The detected frequency should be the fundamental: " + fundamental + " Hz");
        assertTrue(result.confidence() > 0.6, 
                "The confidence should be high enough (> 0.6) for a harmonica-like sound");
    }

    /**
     * Tests the FFT algorithm's ability to detect frequencies at the extreme edges of the
     * high frequency range.
     * <p>
     * This test verifies that the FFT algorithm can accurately detect frequencies that are
     * very close to the minimum and maximum frequency boundaries. This is important for
     * ensuring the algorithm works correctly across the entire specified frequency range.
     */
    @Test
    void testHighFrequencyBoundaries() {
        // Test frequencies very close to the boundaries
        double[] boundaryFrequencies = {
                1005.0,  // Just above min frequency (1000 Hz)
                1010.0,  // Slightly above min frequency
                3990.0,  // Just below max frequency (4000 Hz)
                3995.0   // Very close to max frequency
        };
        double duration = 2.0; // 2 seconds for better FFT analysis

        for (double frequency : boundaryFrequencies) {
            double[] audioData = generateSineWave(frequency, SAMPLE_RATE, duration);
            PitchDetector.PitchDetectionResult result = fftDetector.detectPitch(audioData, SAMPLE_RATE);

            assertEquals(frequency, result.pitch(), TOLERANCE_HIGH_FREQ,
                    "The detected frequency should be " + frequency + " Hz at the boundary");
            assertTrue(result.confidence() > 0.6, 
                    "The confidence should be high enough (> 0.6) even at boundary frequencies");
        }
    }

    /**
     * Tests the FFT algorithm's ability to detect pure sine waves at various mid-range frequencies.
     * <p>
     * This test verifies that the FFT algorithm can roughly detect the pitch of pure
     * sine waves at frequencies between 300 Hz and 1000 Hz. According to the issue description,
     * the confidence level is not important, and the accuracy is of secondary importance.
     * <p>
     * The test uses a medium tolerance (15.0 Hz) for mid-range frequencies, which is between
     * the tolerance for low frequencies (20.0 Hz) and high frequencies (10.0 Hz).
     *
     * @param frequency the frequency of the sine wave to test in Hz
     */
    @ParameterizedTest
    @ValueSource(doubles = {300.0, 400.0, 500.0, 600.0, 700.0, 800.0, 900.0, 950.0})
    void testPureSineWaveMidRangeFrequencies(double frequency) {
        // Save original frequency range
        double originalMinFreq = PitchDetector.getMinFrequency();
        double originalMaxFreq = PitchDetector.getMaxFrequency();

        try {
            // Set frequency range to include mid-range frequencies
            PitchDetector.setMinFrequency(300.0);
            PitchDetector.setMaxFrequency(1000.0);

            double duration = 1.5; // 1.5 seconds for better FFT analysis of mid-range frequencies
            double[] audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

            PitchDetector.PitchDetectionResult result = fftDetector.detectPitch(audioData, SAMPLE_RATE);

            // Check if the detected pitch is roughly correct (within tolerance)
            assertEquals(frequency, result.pitch(), TOLERANCE_MID_FREQ, 
                    "Detected pitch should roughly match the input sine wave frequency: " + frequency + " Hz");

            // We don't care about confidence for mid-range frequencies as per the issue description
        } finally {
            // Restore original frequency range
            PitchDetector.setMinFrequency(originalMinFreq);
            PitchDetector.setMaxFrequency(originalMaxFreq);
        }
    }

    /**
     * Tests the FFT algorithm's ability to detect pure sine waves at various low frequencies.
     * <p>
     * This test verifies that the FFT algorithm can roughly detect the pitch of pure
     * sine waves at frequencies below 300 Hz. According to the issue description,
     * the confidence level is not important, and the accuracy is of secondary importance.
     * <p>
     * The test uses a higher tolerance (20.0 Hz) for low frequencies compared to high frequencies.
     *
     * @param frequency the frequency of the sine wave to test in Hz
     */
    @ParameterizedTest
    @ValueSource(doubles = {50.0, 100.0, 150.0, 200.0, 250.0, 290.0})
    void testPureSineWaveLowFrequencies(double frequency) {
        // Save original frequency range
        double originalMinFreq = PitchDetector.getMinFrequency();
        double originalMaxFreq = PitchDetector.getMaxFrequency();

        try {
            // Set frequency range to include low frequencies
            PitchDetector.setMinFrequency(40.0);  // Low enough to capture all test frequencies
            PitchDetector.setMaxFrequency(300.0); // Up to the HIGH_FREQ_THRESHOLD

            double duration = 2.0; // 2 seconds for better FFT analysis of low frequencies
            double[] audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

            PitchDetector.PitchDetectionResult result = fftDetector.detectPitch(audioData, SAMPLE_RATE);

            // Check if the detected pitch is roughly correct (within tolerance)
            assertEquals(frequency, result.pitch(), TOLERANCE_LOW_FREQ, 
                    "Detected pitch should roughly match the input sine wave frequency: " + frequency + " Hz");

            // We don't care about confidence for low frequencies as per the issue description
        } finally {
            // Restore original frequency range
            PitchDetector.setMinFrequency(originalMinFreq);
            PitchDetector.setMaxFrequency(originalMaxFreq);
        }
    }

    /**
     * Tests the FFT algorithm's ability to detect the dominant frequency in a mixed signal
     * at mid-range frequencies (300-1000 Hz).
     * <p>
     * This test verifies that when presented with a signal containing both a main mid-range frequency
     * and a secondary frequency, the FFT algorithm correctly identifies the main frequency
     * as the dominant one.
     *
     * @param mainFrequency         the primary frequency component in Hz
     * @param mainAmplitude         the amplitude of the primary frequency
     * @param secondaryFrequency    the secondary frequency component in Hz
     * @param secondaryAmplitude    the amplitude of the secondary frequency
     */
    @ParameterizedTest
    @CsvSource({
            // mainFrequency, mainAmplitude, secondaryFrequency, secondaryAmplitude
            "500.0, 1.0, 750.0, 0.3",    // Mid-range frequency with higher harmonic (weaker)
            "600.0, 1.0, 300.0, 0.4",    // Mid-range frequency with subharmonic (weaker)
            "800.0, 1.0, 400.0, 0.5"     // Mid-range frequency with subharmonic (stronger)
    })
    void testDetectPitchWithMixedMidRangeFrequencies(double mainFrequency, double mainAmplitude, 
                                                  double secondaryFrequency, double secondaryAmplitude) {
        // Save original frequency range
        double originalMinFreq = PitchDetector.getMinFrequency();
        double originalMaxFreq = PitchDetector.getMaxFrequency();

        try {
            // Set frequency range to include mid-range frequencies and their harmonics/subharmonics
            PitchDetector.setMinFrequency(250.0);
            PitchDetector.setMaxFrequency(1000.0);

            double duration = 2.0; // 2 seconds for better FFT analysis with mixed signals

            // Generate a mixed signal with both main and secondary frequencies
            double[] mixedWave = generateMixedSineWaveWithAmplitudes(
                    mainFrequency, mainAmplitude, 
                    secondaryFrequency, secondaryAmplitude, 
                    SAMPLE_RATE, (int)duration);

            // Invoke the pitch detection algorithm to find the dominant frequency
            PitchDetector.PitchDetectionResult result = fftDetector.detectPitch(mixedWave, SAMPLE_RATE);

            // Assert that the detected frequency is within the tolerance of the main frequency
            assertEquals(mainFrequency, result.pitch(), TOLERANCE_MID_FREQ,
                    "The detected frequency should be within the tolerance range of the main frequency.");

            // We don't care about confidence for mid-range frequencies as per the issue description
        } finally {
            // Restore original frequency range
            PitchDetector.setMinFrequency(originalMinFreq);
            PitchDetector.setMaxFrequency(originalMaxFreq);
        }
    }

    /**
     * Tests the FFT algorithm's ability to detect the dominant frequency in a mixed signal
     * at low frequencies.
     * <p>
     * This test verifies that when presented with a signal containing both a main low frequency
     * and a secondary frequency, the FFT algorithm correctly identifies the main frequency
     * as the dominant one.
     *
     * @param mainFrequency         the primary frequency component in Hz
     * @param mainAmplitude         the amplitude of the primary frequency
     * @param secondaryFrequency    the secondary frequency component in Hz
     * @param secondaryAmplitude    the amplitude of the secondary frequency
     */
    @ParameterizedTest
    @CsvSource({
            // mainFrequency, mainAmplitude, secondaryFrequency, secondaryAmplitude
            "100.0, 1.0, 200.0, 0.3",    // Low frequency with higher harmonic (weaker)
            "200.0, 1.0, 100.0, 0.4",    // Low frequency with subharmonic (weaker)
            "250.0, 1.0, 500.0, 0.5"     // Low frequency with harmonic (stronger)
    })
    void testDetectPitchWithMixedLowFrequencies(double mainFrequency, double mainAmplitude, 
                                              double secondaryFrequency, double secondaryAmplitude) {
        // Save original frequency range
        double originalMinFreq = PitchDetector.getMinFrequency();
        double originalMaxFreq = PitchDetector.getMaxFrequency();

        try {
            // Set frequency range to include low frequencies and their harmonics
            PitchDetector.setMinFrequency(40.0);
            PitchDetector.setMaxFrequency(600.0);

            double duration = 3.0; // 3 seconds for better FFT analysis with mixed signals

            // Generate a mixed signal with both main and secondary frequencies
            double[] mixedWave = generateMixedSineWaveWithAmplitudes(
                    mainFrequency, mainAmplitude, 
                    secondaryFrequency, secondaryAmplitude, 
                    SAMPLE_RATE, (int)duration);

            // Invoke the pitch detection algorithm to find the dominant frequency
            PitchDetector.PitchDetectionResult result = fftDetector.detectPitch(mixedWave, SAMPLE_RATE);

            // Assert that the detected frequency is within the tolerance of the main frequency
            assertEquals(mainFrequency, result.pitch(), TOLERANCE_LOW_FREQ,
                    "The detected frequency should be within the tolerance range of the main frequency.");

            // We don't care about confidence for low frequencies as per the issue description
        } finally {
            // Restore original frequency range
            PitchDetector.setMinFrequency(originalMinFreq);
            PitchDetector.setMaxFrequency(originalMaxFreq);
        }
    }

    /**
     * Tests the FFT algorithm's robustness to noise at mid-range frequencies (300-1000 Hz).
     * <p>
     * This test verifies that the FFT algorithm can still roughly detect the fundamental
     * frequency of a sine wave at mid-range frequencies when it is combined with white noise.
     * This is important for real-world applications where audio signals often contain noise.
     *
     * @param frequency the frequency of the sine wave to test in Hz
     * @param noiseLevel the level of noise as a fraction of the signal amplitude
     */
    @ParameterizedTest
    @CsvSource({
            // frequency, noiseLevel
            "350.0, 0.1",   // 10% noise
            "600.0, 0.15",  // 15% noise
            "850.0, 0.2"    // 20% noise
    })
    void testMidRangeFrequencyWithWhiteNoise(double frequency, double noiseLevel) {
        // Save original frequency range
        double originalMinFreq = PitchDetector.getMinFrequency();
        double originalMaxFreq = PitchDetector.getMaxFrequency();

        try {
            // Set frequency range to include mid-range frequencies
            PitchDetector.setMinFrequency(300.0);
            PitchDetector.setMaxFrequency(1000.0);

            int durationMs = 1500; // 1.5 seconds for better FFT analysis with noise
            double[] noisyWave = generateSineWaveWithNoise(frequency, SAMPLE_RATE, durationMs, noiseLevel);

            PitchDetector.PitchDetectionResult result = fftDetector.detectPitch(noisyWave, SAMPLE_RATE);

            assertEquals(frequency, result.pitch(), TOLERANCE_MID_FREQ,
                    "The detected frequency should still be roughly " + frequency + " Hz, even with " + 
                    (noiseLevel * 100) + "% white noise.");

            // We don't care about confidence for mid-range frequencies as per the issue description
        } finally {
            // Restore original frequency range
            PitchDetector.setMinFrequency(originalMinFreq);
            PitchDetector.setMaxFrequency(originalMaxFreq);
        }
    }

    /**
     * Tests the FFT algorithm's robustness to noise at low frequencies.
     * <p>
     * This test verifies that the FFT algorithm can still roughly detect the fundamental
     * frequency of a sine wave at low frequencies when it is combined with white noise.
     * This is important for real-world applications where audio signals often contain noise.
     *
     * @param frequency the frequency of the sine wave to test in Hz
     * @param noiseLevel the level of noise as a fraction of the signal amplitude
     */
    @ParameterizedTest
    @CsvSource({
            // frequency, noiseLevel
            "80.0, 0.1",   // 10% noise
            "150.0, 0.15", // 15% noise
            "250.0, 0.2"   // 20% noise
    })
    void testLowFrequencyWithWhiteNoise(double frequency, double noiseLevel) {
        // Save original frequency range
        double originalMinFreq = PitchDetector.getMinFrequency();
        double originalMaxFreq = PitchDetector.getMaxFrequency();

        try {
            // Set frequency range to include low frequencies
            PitchDetector.setMinFrequency(40.0);
            PitchDetector.setMaxFrequency(300.0);

            int durationMs = 2000; // 2 seconds for better FFT analysis with noise
            double[] noisyWave = generateSineWaveWithNoise(frequency, SAMPLE_RATE, durationMs, noiseLevel);

            PitchDetector.PitchDetectionResult result = fftDetector.detectPitch(noisyWave, SAMPLE_RATE);

            assertEquals(frequency, result.pitch(), TOLERANCE_LOW_FREQ,
                    "The detected frequency should still be roughly " + frequency + " Hz, even with " + 
                    (noiseLevel * 100) + "% white noise.");

            // We don't care about confidence for low frequencies as per the issue description
        } finally {
            // Restore original frequency range
            PitchDetector.setMinFrequency(originalMinFreq);
            PitchDetector.setMaxFrequency(originalMaxFreq);
        }
    }

    /**
     * Tests the FFT algorithm's ability to detect mid-range frequencies (300-1000 Hz) with varying amplitudes.
     * <p>
     * This test verifies that the FFT algorithm can roughly detect the pitch of sine waves
     * at mid-range frequencies even when the amplitude varies over time. This is important for
     * real-world applications where the amplitude of a signal may not be constant.
     *
     * @param frequency the frequency of the sine wave to test in Hz
     */
    @ParameterizedTest
    @ValueSource(doubles = {350.0, 500.0, 750.0, 950.0})
    void testMidRangeFrequencyWithVaryingAmplitude(double frequency) {
        // Save original frequency range
        double originalMinFreq = PitchDetector.getMinFrequency();
        double originalMaxFreq = PitchDetector.getMaxFrequency();

        try {
            // Set frequency range to include mid-range frequencies
            PitchDetector.setMinFrequency(300.0);
            PitchDetector.setMaxFrequency(1000.0);

            double duration = 2.0; // 2 seconds for better FFT analysis with varying amplitude
            double[] varyingAmplitudeWave = generateSineWaveWithVaryingAmplitude(frequency, SAMPLE_RATE, duration);

            PitchDetector.PitchDetectionResult result = fftDetector.detectPitch(varyingAmplitudeWave, SAMPLE_RATE);

            assertEquals(frequency, result.pitch(), TOLERANCE_MID_FREQ,
                    "The detected frequency should be roughly " + frequency + " Hz, even with varying amplitude.");

            // We don't care about confidence for mid-range frequencies as per the issue description
        } finally {
            // Restore original frequency range
            PitchDetector.setMinFrequency(originalMinFreq);
            PitchDetector.setMaxFrequency(originalMaxFreq);
        }
    }

    /**
     * Tests the FFT algorithm's ability to detect low frequencies with varying amplitudes.
     * <p>
     * This test verifies that the FFT algorithm can roughly detect the pitch of sine waves
     * at low frequencies even when the amplitude varies over time. This is important for
     * real-world applications where the amplitude of a signal may not be constant.
     *
     * @param frequency the frequency of the sine wave to test in Hz
     */
    @ParameterizedTest
    @ValueSource(doubles = {70.0, 150.0, 250.0})
    void testLowFrequencyWithVaryingAmplitude(double frequency) {
        // Save original frequency range
        double originalMinFreq = PitchDetector.getMinFrequency();
        double originalMaxFreq = PitchDetector.getMaxFrequency();

        try {
            // Set frequency range to include low frequencies
            PitchDetector.setMinFrequency(40.0);
            PitchDetector.setMaxFrequency(300.0);

            double duration = 3.0; // 3 seconds for better FFT analysis with varying amplitude
            double[] varyingAmplitudeWave = generateSineWaveWithVaryingAmplitude(frequency, SAMPLE_RATE, duration);

            PitchDetector.PitchDetectionResult result = fftDetector.detectPitch(varyingAmplitudeWave, SAMPLE_RATE);

            assertEquals(frequency, result.pitch(), TOLERANCE_LOW_FREQ,
                    "The detected frequency should be roughly " + frequency + " Hz, even with varying amplitude.");

            // We don't care about confidence for low frequencies as per the issue description
        } finally {
            // Restore original frequency range
            PitchDetector.setMinFrequency(originalMinFreq);
            PitchDetector.setMaxFrequency(originalMaxFreq);
        }
    }

    /**
     * Tests the FFT algorithm's ability to track frequency sweeps in mid-range frequency ranges (300-1000 Hz).
     * <p>
     * This test verifies that the FFT algorithm can roughly detect frequencies at various
     * points during a frequency sweep in the mid-range frequency range. This is important for
     * analyzing sounds with changing frequencies, such as bending notes on a harmonica.
     *
     * @param startFreq the starting frequency of the sweep in Hz
     * @param endFreq   the ending frequency of the sweep in Hz
     */
    @ParameterizedTest
    @CsvSource({
            // startFreq, endFreq
            "350.0, 650.0",    // Upward sweep in mid-range
            "800.0, 500.0",    // Downward sweep in mid-range
            "400.0, 900.0"     // Wider upward sweep in mid-range
    })
    void testMidRangeFrequencySweep(double startFreq, double endFreq) {
        // Save original frequency range
        double originalMinFreq = PitchDetector.getMinFrequency();
        double originalMaxFreq = PitchDetector.getMaxFrequency();

        try {
            // Set frequency range to include mid-range frequencies
            PitchDetector.setMinFrequency(300.0);
            PitchDetector.setMaxFrequency(1000.0);

            double duration = 3.0; // 3 seconds for better analysis of the sweep

            // Generate frequency sweep signal
            double[] audioData = generateFrequencySweep(startFreq, endFreq, SAMPLE_RATE, duration);

            // Analyze beginning of sweep (should be close to startFreq)
            double[] beginning = new double[SAMPLE_RATE]; // 1 second
            System.arraycopy(audioData, 0, beginning, 0, beginning.length);
            PitchDetector.PitchDetectionResult resultBegin = fftDetector.detectPitch(beginning, SAMPLE_RATE);

            // Analyze middle of sweep (should be close to average of startFreq and endFreq)
            double[] middle = new double[SAMPLE_RATE]; // 1 second
            System.arraycopy(audioData, SAMPLE_RATE, middle, 0, middle.length);
            PitchDetector.PitchDetectionResult resultMiddle = fftDetector.detectPitch(middle, SAMPLE_RATE);

            // Analyze end of sweep (should be close to endFreq)
            double[] end = new double[SAMPLE_RATE]; // 1 second
            System.arraycopy(audioData, 2 * SAMPLE_RATE, end, 0, end.length);
            PitchDetector.PitchDetectionResult resultEnd = fftDetector.detectPitch(end, SAMPLE_RATE);

            // Expected middle frequency
            double expectedMiddleFreq = (startFreq + endFreq) / 2.0;

            // Verify that the detected frequencies are close to the expected frequencies
            // Use a much higher tolerance for frequency sweeps as they are harder to detect accurately
            // For mid-range frequencies, the issue description states that accuracy is of secondary importance
            // Use a percentage-based tolerance (25% of the expected frequency) to account for FFT limitations
            double startTolerance = Math.max(TOLERANCE_MID_FREQ * 2, startFreq * 0.25);
            double middleTolerance = Math.max(TOLERANCE_MID_FREQ * 2, expectedMiddleFreq * 0.15);
            double endTolerance = Math.max(TOLERANCE_MID_FREQ * 2, endFreq * 0.20);

            assertEquals(startFreq, resultBegin.pitch(), startTolerance,
                    "The detected frequency at the beginning should be roughly " + startFreq + " Hz");
            assertEquals(expectedMiddleFreq, resultMiddle.pitch(), middleTolerance,
                    "The detected frequency in the middle should be roughly " + expectedMiddleFreq + " Hz");
            assertEquals(endFreq, resultEnd.pitch(), endTolerance,
                    "The detected frequency at the end should be roughly " + endFreq + " Hz");

            // We don't care about confidence for mid-range frequencies as per the issue description
        } finally {
            // Restore original frequency range
            PitchDetector.setMinFrequency(originalMinFreq);
            PitchDetector.setMaxFrequency(originalMaxFreq);
        }
    }

    /**
     * Tests the FFT algorithm's ability to track frequency sweeps in low frequency ranges.
     * <p>
     * This test verifies that the FFT algorithm can roughly detect frequencies at various
     * points during a frequency sweep in the low frequency range. This is important for
     * analyzing sounds with changing frequencies, such as bending notes on a harmonica.
     *
     * @param startFreq the starting frequency of the sweep in Hz
     * @param endFreq   the ending frequency of the sweep in Hz
     */
    @ParameterizedTest
    @CsvSource({
            // startFreq, endFreq
            "50.0, 150.0",    // Upward sweep in low range
            "200.0, 100.0",   // Downward sweep in low range
            "150.0, 250.0"    // Upward sweep in mid-low range
    })
    void testLowFrequencySweep(double startFreq, double endFreq) {
        // Save original frequency range
        double originalMinFreq = PitchDetector.getMinFrequency();
        double originalMaxFreq = PitchDetector.getMaxFrequency();

        try {
            // Set frequency range to include low frequencies
            PitchDetector.setMinFrequency(40.0);
            PitchDetector.setMaxFrequency(300.0);

            double duration = 4.0; // 4 seconds for better analysis of the sweep

            // Generate frequency sweep signal
            double[] audioData = generateFrequencySweep(startFreq, endFreq, SAMPLE_RATE, duration);

            // Analyze beginning of sweep (should be close to startFreq)
            double[] beginning = new double[SAMPLE_RATE]; // 1 second
            System.arraycopy(audioData, 0, beginning, 0, beginning.length);
            PitchDetector.PitchDetectionResult resultBegin = fftDetector.detectPitch(beginning, SAMPLE_RATE);

            // Analyze middle of sweep (should be close to average of startFreq and endFreq)
            double[] middle = new double[SAMPLE_RATE]; // 1 second
            System.arraycopy(audioData, SAMPLE_RATE, middle, 0, middle.length);
            PitchDetector.PitchDetectionResult resultMiddle = fftDetector.detectPitch(middle, SAMPLE_RATE);

            // Analyze end of sweep (should be close to endFreq)
            double[] end = new double[SAMPLE_RATE]; // 1 second
            System.arraycopy(audioData, 3 * SAMPLE_RATE, end, 0, end.length);
            PitchDetector.PitchDetectionResult resultEnd = fftDetector.detectPitch(end, SAMPLE_RATE);

            // Expected middle frequency
            double expectedMiddleFreq = (startFreq + endFreq) / 2.0;

            // Verify that the detected frequencies are close to the expected frequencies
            // Use a higher tolerance for frequency sweeps (30.0 Hz) as they are harder to detect accurately
            assertEquals(startFreq, resultBegin.pitch(), TOLERANCE_LOW_FREQ * 1.5,
                    "The detected frequency at the beginning should be roughly " + startFreq + " Hz");
            assertEquals(expectedMiddleFreq, resultMiddle.pitch(), TOLERANCE_LOW_FREQ * 1.5,
                    "The detected frequency in the middle should be roughly " + expectedMiddleFreq + " Hz");
            assertEquals(endFreq, resultEnd.pitch(), TOLERANCE_LOW_FREQ * 1.5,
                    "The detected frequency at the end should be roughly " + endFreq + " Hz");

            // We don't care about confidence for low frequencies as per the issue description
        } finally {
            // Restore original frequency range
            PitchDetector.setMinFrequency(originalMinFreq);
            PitchDetector.setMaxFrequency(originalMaxFreq);
        }
    }

    /**
     * Tests the FFT algorithm's ability to handle subharmonics in various scenarios.
     * <p>
     * This test verifies that the FFT algorithm correctly handles subharmonics, which are
     * frequency components at fractions of the fundamental frequency (e.g., f/2, f/3, f/4).
     * Subharmonics are important in harmonica playing, especially when analyzing bending
     * techniques and complex tones.
     * <p>
     * The test covers:
     * 1. Different subharmonic ratios (1/2, 1/3, 1/4)
     * 2. Different amplitude ratios between fundamental and subharmonic
     * 3. Cases where the subharmonic is stronger than the fundamental
     * 4. Complex signals with multiple subharmonics
     */
    @Test
    void testSubharmonics() {
        // Temporarily adjust frequency range to include lower frequencies for subharmonic testing
        double originalMinFreq = PitchDetector.getMinFrequency();
        double originalMaxFreq = PitchDetector.getMaxFrequency();

        try {
            // Set wider frequency range to detect both fundamentals and subharmonics
            PitchDetector.setMinFrequency(500.0);
            PitchDetector.setMaxFrequency(4000.0);

            // Test case 1: Fundamental with half-frequency subharmonic (f/2)
            // The fundamental should be detected when it's stronger
            double fundamental = 2000.0;
            double subharmonic = fundamental / 2.0; // 1000 Hz
            double duration = 2.0; // 2 seconds for better FFT analysis

            // 1.1: Fundamental stronger than subharmonic (should detect fundamental)
            double[] audioData1 = generateMixedSineWaveWithAmplitudes(
                    fundamental, 1.0,  // Fundamental at full amplitude
                    subharmonic, 0.5,  // Subharmonic at half amplitude
                    SAMPLE_RATE, (int)duration);

            PitchDetector.PitchDetectionResult result1 = fftDetector.detectPitch(audioData1, SAMPLE_RATE);
            assertEquals(fundamental, result1.pitch(), TOLERANCE_HIGH_FREQ,
                    "When fundamental is stronger, it should be detected as the pitch");
            assertTrue(result1.confidence() > 0.5,
                    "Confidence should be high enough (> 0.5) when fundamental is stronger");

            // 1.2: Subharmonic stronger than fundamental (should detect subharmonic)
            double[] audioData2 = generateMixedSineWaveWithAmplitudes(
                    fundamental, 0.4,  // Fundamental at lower amplitude
                    subharmonic, 1.0,  // Subharmonic at full amplitude
                    SAMPLE_RATE, (int)duration);

            PitchDetector.PitchDetectionResult result2 = fftDetector.detectPitch(audioData2, SAMPLE_RATE);
            assertEquals(subharmonic, result2.pitch(), TOLERANCE_HIGH_FREQ,
                    "When subharmonic is stronger, it should be detected as the pitch");
            assertTrue(result2.confidence() > 0.5,
                    "Confidence should be high enough (> 0.5) when subharmonic is stronger");

            // Test case 2: Fundamental with third-frequency subharmonic (f/3)
            double thirdSubharmonic = fundamental / 3.0; // ~666.7 Hz

            // 2.1: Fundamental stronger than third subharmonic
            double[] audioData3 = generateMixedSineWaveWithAmplitudes(
                    fundamental, 1.0,       // Fundamental at full amplitude
                    thirdSubharmonic, 0.4,  // Third subharmonic at lower amplitude
                    SAMPLE_RATE, (int)duration);

            PitchDetector.PitchDetectionResult result3 = fftDetector.detectPitch(audioData3, SAMPLE_RATE);
            assertEquals(fundamental, result3.pitch(), TOLERANCE_HIGH_FREQ,
                    "When fundamental is stronger than third subharmonic, it should be detected");

            // 2.2: Third subharmonic stronger than fundamental
            double[] audioData4 = generateMixedSineWaveWithAmplitudes(
                    fundamental, 0.3,       // Fundamental at lower amplitude
                    thirdSubharmonic, 0.9,  // Third subharmonic at higher amplitude
                    SAMPLE_RATE, (int)duration);

            PitchDetector.PitchDetectionResult result4 = fftDetector.detectPitch(audioData4, SAMPLE_RATE);
            assertEquals(thirdSubharmonic, result4.pitch(), TOLERANCE_HIGH_FREQ,
                    "When third subharmonic is stronger, it should be detected as the pitch");

            // Test case 3: Complex signal with fundamental, harmonic, and subharmonic
            // This simulates a more realistic harmonica tone with complex frequency components
            double[] frequencies = {
                    fundamental / 2.0,  // Subharmonic (f/2)
                    fundamental,        // Fundamental (f)
                    fundamental * 2.0,  // First harmonic (2f)
                    fundamental * 3.0   // Second harmonic (3f)
            };

            // Test 3.1: Fundamental is dominant
            double[] amplitudes1 = {0.3, 1.0, 0.6, 0.4};  // Fundamental dominant
            double[] complexSignal1 = generateMultipleFrequencySignal(frequencies, amplitudes1, SAMPLE_RATE, duration);

            PitchDetector.PitchDetectionResult complexResult1 = fftDetector.detectPitch(complexSignal1, SAMPLE_RATE);
            assertEquals(fundamental, complexResult1.pitch(), TOLERANCE_HIGH_FREQ,
                    "In complex signal with dominant fundamental, fundamental should be detected");

            // Test 3.2: Subharmonic is dominant
            double[] amplitudes2 = {1.0, 0.4, 0.3, 0.2};  // Subharmonic dominant
            double[] complexSignal2 = generateMultipleFrequencySignal(frequencies, amplitudes2, SAMPLE_RATE, duration);

            PitchDetector.PitchDetectionResult complexResult2 = fftDetector.detectPitch(complexSignal2, SAMPLE_RATE);
            assertEquals(fundamental / 2.0, complexResult2.pitch(), TOLERANCE_HIGH_FREQ,
                    "In complex signal with dominant subharmonic, subharmonic should be detected");

            // Test case 4: Multiple subharmonics (f/2 and f/3)
            // This tests how the algorithm handles multiple subharmonic components
            double[] multiSubFrequencies = {
                    fundamental / 3.0,  // f/3 subharmonic
                    fundamental / 2.0,  // f/2 subharmonic
                    fundamental         // Fundamental
            };

            // Test 4.1: f/2 subharmonic is dominant
            double[] multiSubAmplitudes1 = {0.3, 1.0, 0.5};  // f/2 dominant
            double[] multiSubSignal1 = generateMultipleFrequencySignal(multiSubFrequencies, multiSubAmplitudes1, SAMPLE_RATE, duration);

            PitchDetector.PitchDetectionResult multiSubResult1 = fftDetector.detectPitch(multiSubSignal1, SAMPLE_RATE);
            assertEquals(fundamental / 2.0, multiSubResult1.pitch(), TOLERANCE_HIGH_FREQ,
                    "When f/2 subharmonic is dominant among multiple subharmonics, it should be detected");

            // Test 4.2: f/3 subharmonic is dominant
            double[] multiSubAmplitudes2 = {1.0, 0.4, 0.3};  // f/3 dominant
            double[] multiSubSignal2 = generateMultipleFrequencySignal(multiSubFrequencies, multiSubAmplitudes2, SAMPLE_RATE, duration);

            PitchDetector.PitchDetectionResult multiSubResult2 = fftDetector.detectPitch(multiSubSignal2, SAMPLE_RATE);
            assertEquals(fundamental / 3.0, multiSubResult2.pitch(), TOLERANCE_HIGH_FREQ,
                    "When f/3 subharmonic is dominant among multiple subharmonics, it should be detected");
        } finally {
            // Restore original frequency range
            PitchDetector.setMinFrequency(originalMinFreq);
            PitchDetector.setMaxFrequency(originalMaxFreq);
        }
    }
}
