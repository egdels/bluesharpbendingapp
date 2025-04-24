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
 * Test class for the YINPitchDetector.
 * This class tests the functionality of the YIN algorithm implementation.
 */
class YINPitchDetectorTest {

    private static final int SAMPLE_RATE = 44100;
    private static final double TOLERANCE = 0.5;

    @BeforeEach
    void setUp() {
        YINPitchDetector.setMinFrequency(YINPitchDetector.getDefaultMinFrequency());
        YINPitchDetector.setMaxFrequency(YINPitchDetector.getDefaultMaxFrequency());
    }

    @ParameterizedTest
    @CsvSource({
            // mainFrequency, mainAmplitude, subharmonicFrequency, subharmonicAmplitude, tolerance, minConfidence
            "934.6, 1.0, 460.0, 0.3, 5.0, 0.8",  // Weak subharmonic, high confidence required
            "934.6, 1.0, 460.0, 0.5, 10.0, 0.3", // Moderate subharmonic
            "934.6, 1.9, 460.0, 1.0, 10.0, 0.1"  // Dominant main frequency, lower confidence acceptable
    })
    void testDetectPitchWithMixedFrequencies(double mainFrequency, double mainAmplitude, double subharmonicFrequency, double subharmonicAmplitude, double tolerance, double minConfidence) {
        int duration = 1; // 1 second

        // Generate a mixed signal with both main and subharmonic frequencies
        double[] mixedWave = generateMixedSineWaveWithAmplitudes(mainFrequency, mainAmplitude, subharmonicFrequency, subharmonicAmplitude, SAMPLE_RATE, duration);

        // Invoke the pitch detection algorithm to find the dominant frequency
        YINPitchDetector.PitchDetectionResult result = YINPitchDetector.detectPitch(mixedWave, SAMPLE_RATE);

        // Assert that the detected frequency is within the tolerance of the main frequency
        assertEquals(mainFrequency, result.pitch(), tolerance,
                "The detected frequency should be within the tolerance range of the main frequency.");
        // Assert that the confidence level is above the minimum threshold
        assertTrue(result.confidence() > minConfidence,
                "The confidence should be at least " + minConfidence);
    }

    @ParameterizedTest
    @MethodSource("provideHarmonicaParameters")
    void testYIN_Edges_Harmonica(AbstractHarmonica.KEY key, AbstractHarmonica.TUNE tune, double maxFrequencyTolerance, double minFrequencyTolerance) {
        Harmonica harmonica = AbstractHarmonica.create(key, tune);
        YINPitchDetector.setMaxFrequency(harmonica.getHarmonicaMaxFrequency());
        YINPitchDetector.setMinFrequency(harmonica.getHarmonicaMinFrequency());

        // Test for max frequency
        double[] sineWave = generateSineWave(harmonica.getHarmonicaMaxFrequency(), SAMPLE_RATE, 1.0);
        YINPitchDetector.PitchDetectionResult result = YINPitchDetector.detectPitch(sineWave, SAMPLE_RATE);
        assertTrue(result.confidence() >= 0.95);
        assertEquals(harmonica.getHarmonicaMaxFrequency(), result.pitch(), maxFrequencyTolerance,
                "Detected pitch should match the sine wave max frequency");

        // Test for min frequency
        sineWave = generateSineWave(harmonica.getHarmonicaMinFrequency(), SAMPLE_RATE, 1.0);
        result = YINPitchDetector.detectPitch(sineWave, SAMPLE_RATE);
        assertTrue(result.confidence() >= 0.95);
        assertEquals(harmonica.getHarmonicaMinFrequency(), result.pitch(), minFrequencyTolerance,
                "Detected pitch should match the sine wave min frequency");
    }

    private static Stream<Arguments> provideHarmonicaParameters() {
        return Stream.of(
                Arguments.of(AbstractHarmonica.KEY.C, AbstractHarmonica.TUNE.RICHTER, 3.0, 0.01),
                Arguments.of(AbstractHarmonica.KEY.A, AbstractHarmonica.TUNE.RICHTER, 3.0, 0.01),
                Arguments.of(AbstractHarmonica.KEY.B, AbstractHarmonica.TUNE.RICHTER, 3.0, 0.01),
                Arguments.of(AbstractHarmonica.KEY.A_FLAT, AbstractHarmonica.TUNE.AUGMENTED, 3.0, 0.01),
                Arguments.of(AbstractHarmonica.KEY.D, AbstractHarmonica.TUNE.AUGMENTED, 5.0, 0.01),
                Arguments.of(AbstractHarmonica.KEY.D_FLAT, AbstractHarmonica.TUNE.AUGMENTED, 20.0, 0.01),
                Arguments.of(AbstractHarmonica.KEY.E, AbstractHarmonica.TUNE.COUNTRY, 50.0, 0.01),
                Arguments.of(AbstractHarmonica.KEY.E_FLAT, AbstractHarmonica.TUNE.COUNTRY, 5.0, 0.01),
                Arguments.of(AbstractHarmonica.KEY.LA, AbstractHarmonica.TUNE.COUNTRY, 3.0, 0.01),
                Arguments.of(AbstractHarmonica.KEY.LF_HASH, AbstractHarmonica.TUNE.DIMINISHED, 3.0, 0.01),
                Arguments.of(AbstractHarmonica.KEY.LG, AbstractHarmonica.TUNE.DIMINISHED, 3.0, 0.01),
                Arguments.of(AbstractHarmonica.KEY.LD_FLAT, AbstractHarmonica.TUNE.DIMINISHED, 3.0, 0.01),
                Arguments.of(AbstractHarmonica.KEY.B_FLAT, AbstractHarmonica.TUNE.PADDYRICHTER, 3.0, 0.01),
                Arguments.of(AbstractHarmonica.KEY.LLF, AbstractHarmonica.TUNE.PADDYRICHTER, 3.0, 0.01)
        );
    }

    /**
     * Tests the algorithm with a sine wave of 934.6 Hz combined with a small amount of white noise
     * to verify that the fundamental frequency is still detected correctly.
     */
    @Test
    void testFrequencyWithWhiteNoise() {
        double frequency = 934.6;
        int durationMs = 1000; // 1 second
        double noiseLevel = 0.1; // White noise as 10% of the signal amplitude
        double[] noisyWave = generateSineWaveWithNoise(frequency, SAMPLE_RATE, durationMs, noiseLevel);

        YINPitchDetector.PitchDetectionResult result = YINPitchDetector.detectPitch(noisyWave, SAMPLE_RATE);

        assertEquals(frequency, result.pitch(), TOLERANCE,
                "The detected frequency should still be 934.6 Hz, even with white noise.");
        assertTrue(result.confidence() > 0.6, "The confidence should be high enough (> 0.6).");
    }

    @Test
    void testPureSineWave() {
        int sampleRate = 44100;
        double frequency = 440.0; // A4
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }

        double detectedPitch = YINPitchDetector.detectPitch(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.02, "Detected pitch should match the input sine wave frequency");
    }

    @ParameterizedTest
    @ValueSource(ints = {44100, 48000})
    void testSilence(int sampleSize) {
        int sampleRate = 44100;
        double[] audioData = new double[sampleSize];

        double detectedPitch = YINPitchDetector.detectPitch(audioData, sampleRate).pitch();
        assertEquals(YINPitchDetector.NO_DETECTED_PITCH, detectedPitch, "Detected pitch should be -1 for silence");
    }

    @Test
    void testNoise() {
        int sampleRate = 44100;
        double[] audioData = new double[2000];
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] = Math.random() * 2 - 1; // Random noise between -1 and 1
        }

        double detectedPitch = YINPitchDetector.detectPitch(audioData, sampleRate).pitch();
        assertEquals(YINPitchDetector.NO_DETECTED_PITCH, detectedPitch, "Detected pitch should be -1 for noise");
    }

    /**
     * Helper method: Generates a sine wave with two combined frequencies and different amplitudes
     */
    private double[] generateMixedSineWaveWithAmplitudes(double primaryFreq, double primaryAmp, 
                                                        double secondaryFreq, double secondaryAmp, 
                                                        int sampleRate, int duration) {
        int sampleCount = sampleRate * duration;
        double[] mixedWave = new double[sampleCount];
        for (int i = 0; i < sampleCount; i++) {
            mixedWave[i] = primaryAmp * Math.sin(2.0 * Math.PI * primaryFreq * i / sampleRate)
                    + secondaryAmp * Math.sin(2.0 * Math.PI * secondaryFreq * i / sampleRate);
        }
        return mixedWave;
    }

    /**
     * Helper method: Generates a sine wave with added white noise
     */
    private double[] generateSineWaveWithNoise(double frequency, int sampleRate, int durationMs, double noiseLevel) {
        int sampleCount = sampleRate * durationMs / 1000;
        double[] noisyWave = new double[sampleCount];
        for (int i = 0; i < sampleCount; i++) {
            noisyWave[i] = Math.sin(2.0 * Math.PI * frequency * i / sampleRate) + noiseLevel * (Math.random() - 0.5);
        }
        return noisyWave;
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
     * Generates a sine wave signal that combines two overlapping sine waves with different frequencies and amplitudes.
     *
     * @param frequency1 The frequency of the first sine wave in Hertz.
     * @param amplitude1 The amplitude of the first sine wave.
     * @param frequency2 The frequency of the second sine wave in Hertz.
     * @param amplitude2 The amplitude of the second sine wave.
     * @param sampleRate The number of samples per second (sample rate) in Hertz.
     * @param duration The duration of the generated wave in seconds.
     * @return An array of doubles representing the overlapping sine wave signal.
     */
    private double[] generateOverlappingSineWave(double frequency1, double amplitude1, double frequency2, double amplitude2, int sampleRate, double duration) {
        int samples = (int) (sampleRate * duration);
        double[] sineWave = new double[samples];
        for (int i = 0; i < samples; i++) {
            sineWave[i] = amplitude1 * Math.sin(2 * Math.PI * frequency1 * i / sampleRate)
                    + amplitude2 * Math.sin(2 * Math.PI * frequency2 * i / sampleRate);
        }
        return sineWave;
    }
}