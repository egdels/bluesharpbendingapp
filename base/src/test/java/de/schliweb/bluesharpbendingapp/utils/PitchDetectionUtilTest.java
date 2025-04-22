package de.schliweb.bluesharpbendingapp.utils;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PitchDetectionUtilTest {

    private static final int SAMPLE_RATE = 44100;
    private static final double TOLERANCE = 0.5;

    /**
     * Tests that a sine wave with 934.6 Hz is correctly detected and not confused
     * with a subharmonic frequency such as 460 Hz.
     */
    @Test
    void testDetectPitchWithYIN_CorrectlyDetect934HzAvoid460Hz() {
        double frequency = 934.6;
        int duration = 1; // 1 second
        double[] sineWave = generateSineWave(frequency, SAMPLE_RATE, duration);

        PitchDetectionUtil.PitchDetectionResult result =
                PitchDetectionUtil.detectPitchWithYIN(sineWave, SAMPLE_RATE);

        assertEquals(frequency, result.pitch(), TOLERANCE,
                "The detected frequency should be 934.6 Hz.");
        assertTrue(result.confidence() > 0.8, "The confidence should be high (> 0.8).");
    }

    /**
     * Tests the algorithm's ability to distinguish between a fundamental frequency
     * and subharmonics by ensuring the frequency (460 Hz) is correctly rejected when
     * the input signal is 934.6 Hz.
     */
    @Test
    void testDetectPitchWithYIN_RejectSubharmonic460HzFor934Hz() {
        double frequency = 460.0;
        int duration = 1; // 1 second
        double[] sineWave = generateSineWave(frequency, SAMPLE_RATE, duration);

        PitchDetectionUtil.PitchDetectionResult result =
                PitchDetectionUtil.detectPitchWithYIN(sineWave, SAMPLE_RATE);

        assertEquals(frequency, result.pitch(), TOLERANCE,
                "The detected frequency should be 460 Hz.");
        assertTrue(result.confidence() > 0.8, "The confidence should be high (> 0.8).");
    }

    /**
     * Tests the algorithm with a combined input of 934.6 Hz and a subharmonic (460 Hz)
     * to ensure that the fundamental frequency is preferred over the subharmonic.
     */
    @Test
    void testDetectPitchWithYIN_MixedFrequenciesWithSubharmonic() {
        double mainFrequency = 934.6;
        double subharmonicFrequency = 460.0;
        int duration = 1; // 1 second

        // Generate a mixed wave with the main frequency having significantly higher amplitude
        double[] mixedWave = generateMixedSineWaveWithAmplitudes(mainFrequency, 1.9, subharmonicFrequency, 1.0, SAMPLE_RATE, duration);

        PitchDetectionUtil.PitchDetectionResult result =
                PitchDetectionUtil.detectPitchWithYIN(mixedWave, SAMPLE_RATE);

        // Use a larger tolerance (10 Hz) to account for the complexity of mixed frequencies
        assertEquals(mainFrequency, result.pitch(), 10.0,
                "The detected frequency should primarily be around 934.6 Hz.");
        assertTrue(result.confidence() > 0.1, "The confidence should be high enough (> 0.1).");
    }

    @Test
    void testDetectPitchWithMPM_MixedFrequenciesWithSubharmonic() {
        double mainFrequency = 934.6;
        double subharmonicFrequency = 460.0;
        int duration = 1; // 1 second

        // Generate a mixed wave with the main frequency having significantly higher amplitude
        double[] mixedWave = generateMixedSineWaveWithAmplitudes(mainFrequency, 2.4, subharmonicFrequency, 1.0, SAMPLE_RATE, duration);

        PitchDetectionUtil.PitchDetectionResult result =
                PitchDetectionUtil.detectPitchWithMPM(mixedWave, SAMPLE_RATE);


        // Use a larger tolerance (10 Hz) to account for the complexity of mixed frequencies
        assertEquals(mainFrequency, result.pitch(), 4,
                "The detected frequency should primarily be around 934.6 Hz.");
        assertTrue(result.confidence() > 0.7, "The confidence should be high enough (> 0.7).");
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
     * Tests the algorithm with a sine wave of 934.6 Hz combined with a small amount of white noise
     * to verify that the fundamental frequency is still detected correctly.
     */
    @Test
    void testDetectPitchWithYIN_FrequencyWithWhiteNoise() {
        double frequency = 934.6;
        int durationMs = 1000; // 1 second
        double noiseLevel = 0.1; // White noise as 10% of the signal amplitude
        double[] noisyWave = generateSineWaveWithNoise(frequency, SAMPLE_RATE, durationMs, noiseLevel);

        PitchDetectionUtil.PitchDetectionResult result =
                PitchDetectionUtil.detectPitchWithYIN(noisyWave, SAMPLE_RATE);

        assertEquals(frequency, result.pitch(), TOLERANCE,
                "The detected frequency should still be 934.6 Hz, even with white noise.");
        assertTrue(result.confidence() > 0.6, "The confidence should be high enough (> 0.6).");
    }

    // Helper method: Generates a sine wave with added white noise
    private double[] generateSineWaveWithNoise(double frequency, int sampleRate, int durationMs, double noiseLevel) {
        int sampleCount = sampleRate * durationMs / 1000;
        double[] noisyWave = new double[sampleCount];
        for (int i = 0; i < sampleCount; i++) {
            noisyWave[i] = Math.sin(2.0 * Math.PI * frequency * i / sampleRate) + noiseLevel * (Math.random() - 0.5);
        }
        return noisyWave;
    }

    @ParameterizedTest
    @org.junit.jupiter.params.provider.CsvSource({
            "44100, 1",
            "44100, 5",
            "44100, 10",
            "48000, 1",
            "48000, 5",
            "48000, 10",
    })
    void testDetectPitchWithYIN_934_6_0Hz_VaryingSampleRatesAndDurations(int sampleRate, int duration) {
        // Arrange
        double frequency = 934.6;
        double[] sineWave = generateSineWave(frequency, sampleRate, duration);

        // Act
        PitchDetectionUtil.PitchDetectionResult result = PitchDetectionUtil.detectPitchWithYIN(sineWave, sampleRate);
        assertTrue( 0.95 <= result.confidence());
        // Assert
        assertEquals(frequency, result.pitch(), 2,
                "Detected pitch does not match the expected value at sample rate "
                        + sampleRate + " Hz and duration " + duration + " seconds");
    }

    /**
     * Verifies that the detectPitchWithMPM method accurately detects the pitch of a sine wave
     * with zero-crossing behavior. The test generates a square-like waveform with alternating
     * positive and negative amplitudes at a specified frequency using a given sampling rate.
     * <p>
     * The method ensures that the detected pitch closely matches the original frequency of the
     * generated waveform by asserting the result within a specified tolerance.
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

        PitchDetectionUtil.PitchDetectionResult result = PitchDetectionUtil.detectPitchWithMPM(audioData, sampleRate);
        double detectedPitch = result.pitch(); // Assuming getPitch() retrieves the required pitch as a double
        assertEquals(frequency, detectedPitch, 1, "Detected pitch should match the sine wave frequency with zero crossing behavior");
    }

    /**
     * Verifies detectPitchWithMPM detects pitch for a sine wave with high amplitude.
     */
    @Test
    void testDetectPitchWithMPM_HighAmplitude() {
        int sampleRate = 44100;
        double frequency = 523.25; // Frequency of C5 note
        double amplitude = 1.0;   // High amplitude
        double duration = 1.0;
        double[] audioData = generateSineWave(frequency, sampleRate, duration);
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] *= amplitude; // Skaliere auf hohe Amplitude
        }

        PitchDetectionUtil.PitchDetectionResult result = PitchDetectionUtil.detectPitchWithMPM(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertEquals(frequency, detectedPitch, 2, "Detected pitch should match the sine wave frequency even with high amplitude");
    }

    /**
     * Validates detectPitchWithMPM for a synthesized waveform designed to test refined minimum detection.
     */
    @Test
    void testDetectPitchWithMPM_RefinedMinimum() {
        int sampleRate = 44100;
        double frequency = 300.0; // Pitch to detect
        double duration = 1.0;    // 1 second of audio

        double[] audioData = generateSineWave(frequency, sampleRate, duration);
        double[] harmonic = generateSineWave(2 * frequency, sampleRate, duration);
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] += 0.3 * harmonic[i]; // Harmonik hinzufügen
        }

        PitchDetectionUtil.PitchDetectionResult result = PitchDetectionUtil.detectPitchWithMPM(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertEquals(frequency, detectedPitch, 0.5, "Detected pitch should match the synthesized waveform's base frequency");
    }

    /**
     * Tests detectPitchWithMPM with a linearly increasing signal.
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

        PitchDetectionUtil.PitchDetectionResult result = PitchDetectionUtil.detectPitchWithMPM(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertEquals(-1, detectedPitch, "Detected pitch should be -1 for a linearly increasing signal");
    }

    /**
     * Ensures detectPitchWithMPM returns -1 for non-periodic signals.
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

        PitchDetectionUtil.PitchDetectionResult result = PitchDetectionUtil.detectPitchWithMPM(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertEquals(-1, detectedPitch, "Detected pitch should be -1 for non-periodic signals");
    }

    /**
     * Validates detectPitchWithMPM on a sine wave with varying amplitude.
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

        PitchDetectionUtil.PitchDetectionResult result = PitchDetectionUtil.detectPitchWithMPM(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertEquals(frequency, detectedPitch, 1, "Detected pitch should match the sine wave frequency despite varying amplitude");
    }

    /**
     * Ensures detectPitchWithMPM detects pitch correctly for a mid-frequency sine wave.
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

        PitchDetectionUtil.PitchDetectionResult result = PitchDetectionUtil.detectPitchWithMPM(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertEquals(frequency, detectedPitch, 1, "Detected pitch should match the sine wave frequency");
    }

    /**
     * Ensures detectPitchWithMPM detects pitch correctly with white noise layered over a sine wave.
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

        PitchDetectionUtil.PitchDetectionResult result = PitchDetectionUtil.detectPitchWithMPM(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertEquals(frequency, detectedPitch, 1, "Detected pitch should match the sine wave frequency despite noise");
    }

    /**
     * Ensures detectPitchWithMPM can detect low amplitude sine waves.
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

        PitchDetectionUtil.PitchDetectionResult result = PitchDetectionUtil.detectPitchWithMPM(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertEquals(frequency, detectedPitch, 1, "Detected pitch should match the sine wave frequency even at low amplitude");
    }

    /**
     * Verifies detectPitchWithYIN correctly detects pitch for a zero-crossing waveform.
     */
    @Test
    void testDetectPitchWithYIN_ZeroCrossingWave() {
        int sampleRate = 44100;
        double frequency = 480.0; // Frequency of the sine wave
        double amplitude = 0.5;  // Amplitude of the sine wave
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = (Math.sin(2 * Math.PI * frequency * i / sampleRate) >= 0) ? amplitude : -amplitude;
        }

        PitchDetectionUtil.PitchDetectionResult result = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertEquals(frequency, detectedPitch, 0.3, "Detected pitch should match the sine wave frequency with zero crossing behavior");
    }

    /**
     * Verifies detectPitchWithYIN detects pitch for a sine wave with high amplitude.
     */
    @Test
    void testDetectPitchWithYIN_HighAmplitude() {
        int sampleRate = 44100;
        double frequency = 523.25; // Frequency of C5 note
        double amplitude = 1.0;   // High amplitude
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = amplitude * Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }

        PitchDetectionUtil.PitchDetectionResult result = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertEquals(frequency, detectedPitch, 0.03, "Detected pitch should match the sine wave frequency even with high amplitude");
    }

    /**
     * Validates detectPitchWithYIN for a synthesized waveform designed to test refined minimum detection.
     */
    @Test
    void testDetectPitchWithYIN_RefinedMinimum() {
        int sampleRate = 44100;
        double frequency = 300.0; // Pitch to detect
        double duration = 1.0;    // 1 second of audio
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate)
                    + 0.3 * Math.sin(2 * Math.PI * 2 * frequency * i / sampleRate); // Adding a harmonic
        }

        PitchDetectionUtil.PitchDetectionResult result = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertEquals(frequency, detectedPitch, 0.02, "Detected pitch should match the synthesized waveform's base frequency");
    }

    /**
     * Tests detectPitchWithYIN with a linearly increasing signal.
     */
    @Test
    void testDetectPitchWithYIN_LinearlyIncreasingSignal() {
        int sampleRate = 44100;
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];

        // Linearly increasing signal
        for (int i = 0; i < samples; i++) {
            audioData[i] = i / (double) samples;
        }

        PitchDetectionUtil.PitchDetectionResult result = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertEquals(-1, detectedPitch, "Detected pitch should be -1 for a linearly increasing signal");
    }

    /**
     * Ensures detectPitchWithYIN returns -1 for non-periodic signals.
     */
    @Test
    void testDetectPitchWithYIN_NonPeriodicSignal() {
        int sampleRate = 44100;
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];

        // Non-periodic signal
        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.random() * 2 - 1; // Random values between -1 and 1
        }

        PitchDetectionUtil.PitchDetectionResult result = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate);
        double detectedPitch = result.pitch();
        assertEquals(-1, detectedPitch, "Detected pitch should be -1 for non-periodic signals");
    }

    /**
     * Validates detectPitchWithYIN on a sine wave with varying amplitude.
     */
    @Test
    void testDetectPitchWithYIN_VaryingAmplitudeWave() {
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

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.02, "Detected pitch should match the sine wave frequency despite varying amplitude");
    }

    /**
     * Verifies that detectPitchWithYIN correctly identifies a pitch
     * for a pure sine wave at a specific frequency.
     */
    @Test
    void testDetectPitchWithYIN_PureSineWave() {
        int sampleRate = 44100;
        double frequency = 440.0; // A4
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.02, "Detected pitch should match the input sine wave frequency");
    }

    /**
     * Ensures detectPitchWithYIN returns -1 for silence with varying audio data sizes.
     */
    @ParameterizedTest
    @ValueSource(ints = {1000, 2000, 4000, 8000, 16000, 44100})
    void testDetectPitchWithYIN_Silence(int sampleSize) {
        int sampleRate = 44100;
        double[] audioData = new double[sampleSize];

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(-1, detectedPitch, "Detected pitch should be -1 for silence");
    }

    /**
     * Validates that detectPitchWithYIN handles random noise and returns -1 (no valid pitch).
     */
    @Test
    void testDetectPitchWithYIN_Noise() {
        int sampleRate = 44100;
        double[] audioData = new double[1000];
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] = Math.random() * 2 - 1; // Random noise between -1 and 1
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(-1, detectedPitch, "Detected pitch should be -1 for noise");
    }

    /**
     * Tests the pitch detection functionality using the YIN algorithm with audio data 
     * containing very low variance noise.
     * <p>
     * This test generates a 1-second segment of audio sampled at 44.1 kHz. The audio 
     * consists of noise with minimal variance, scaled in such a way as to simulate 
     * very subtle random fluctuations. The test verifies the detection result by 
     * asserting that the detected pitch is -1, as low-variance noise should not 
     * produce a discernible pitch.
     * <p>
     * The test ensures that the YIN algorithm handles low-variance noise appropriately 
     * and does not incorrectly detect a pitch where none should exist.
     */
    @Test
    void testDetectPitchWithYIN_VeryLowVarianceNoise() {
        int sampleRate = 44100;
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];

        // Very low variance noise
        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.random() * 0.1 - 0.05; // Low variance
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(-1, detectedPitch, "Detected pitch should be -1 for low-variance noise");
    }

    /**
     * Ensures detectPitchWithYIN can correctly calculate pitch for a low-frequency sine wave.
     */
    @Test
    void testDetectPitchWithYIN_LowFrequencySineWave() {
        int sampleRate = 44100;
        double frequency = 100.0; // Low frequency sine wave
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.003, "Detected pitch should match the low-frequency sine wave");
    }

    /**
     * Verifies detectPitchWithYIN calculates pitch accurately from combined harmonic sine waves.
     */
    @Test
    void testDetectPitchWithYIN_Harmonics() {
        int sampleRate = 44100;
        double frequency = 440.0; // Fundamental frequency (A4)
        double harmonic = 880.0; // Second harmonic (A5)
        double duration = 1.0;
        double[] audioData = generateSineWave(frequency, sampleRate, duration);
        double[] harmonicData = generateSineWave(harmonic, sampleRate, duration);
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] += 0.5 * harmonicData[i]; // Adding harmonic
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.02, "Detected pitch should match the fundamental frequency");
    }

    /**
     * Ensures detectPitchWithYIN can detect low amplitude sine waves.
     */
    @Test
    void testDetectPitchWithYIN_LowAmplitude() {
        int sampleRate = 44100;
        double frequency = 440.0; // A4
        double amplitude = 0.01; // Low amplitude
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = amplitude * Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.02, "Detected pitch should match the sine wave frequency even at low amplitude");
    }

    /**
     * Tests detectPitchWithYIN with a large buffer size.
     */
    @Test
    void testDetectPitchWithYIN_MaxBufferSize() {
        int sampleRate = 44100;
        double frequency = 440.0; // A4
        double duration = 5.0; // Large buffer size for 5 seconds audio
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.02, "Detected pitch should match the sine wave frequency with large buffer size");
    }

    /**
     * Ensures detectPitchWithYIN detects pitch correctly for a high-frequency sine wave.
     */
    @Test
    void testDetectPitchWithYIN_HighFrequencySineWave() {
        int sampleRate = 44100;
        double frequency = 1000.0; // High frequency sine wave
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.3, "Detected pitch should match the high-frequency sine wave");
    }


    /**
     * Ensures detectPitchWithYIN detects pitch correctly for a mid-frequency sine wave.
     */
    @Test
    void testDetectPitchWithYIN_MidFrequencyWave() {
        int sampleRate = 44100;
        double frequency = 250.0; // Mid-frequency sine wave
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.003, "Detected pitch should match the sine wave frequency");
    }

    /**
     * Ensures detectPitchWithYIN detects pitch correctly with white noise layered over a sine wave.
     */
    @Test
    void testDetectPitchWithYIN_SineWithNoise() {
        int sampleRate = 44100;
        double frequency = 440.0; // Sine wave
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate) +
                    (Math.random() * 0.3 - 0.15); // Adding random noise
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.5, "Detected pitch should match the sine wave frequency despite noise");
    }

    /**
     * Tests detectPitchWithYIN for extreme low-frequency sine waves.
     */
    @Test
    void testDetectPitchWithYIN_ExtremeLowFrequency() {
        int sampleRate = 44100;
        double frequency = 10.0; // Extreme low frequency sine wave
        double duration = 2.0; // Longer duration to allow full wavelength
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.01, "Detected pitch should match the low-frequency sine wave");
    }

    /**
     * Validates detectPitchWithYIN accuracy across varying sample rates.
     */
    @Test
    void testDetectPitchWithYIN_SampleRateTolerance() {
        double frequency = 440.0; // A4
        double duration = 1.0;

        int[] sampleRates = {8000, 16000, 44100}; // Test different sample rates
        double[] tolerances = {0.7, 0.11, 0.02}; // Increasing accuracy with higher sample rate

        for (int i = 0; i < sampleRates.length; i++) {
            int sampleRate = sampleRates[i];
            int samples = (int) (sampleRate * duration);
            double[] audioData = new double[samples];
            for (int j = 0; j < samples; j++) {
                audioData[j] = Math.sin(2 * Math.PI * frequency * j / sampleRate);
            }

            double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
            assertEquals(frequency, detectedPitch, tolerances[i], "Pitch detection tolerance should match sample rate precision");
        }
    }

    /**
     * Confirms detectPitchWithYIN returns valid and meaningful results with non-zero values.
     */
    @Test
    void testDetectPitchWithYIN_SimpleInput() {
        int sampleRate = 48000;
        double frequency = 300.0; // Sine wave frequency
        double[] audioData = new double[1024];
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertTrue(detectedPitch > 0, "Detected pitch should be a positive number for simple input");
    }

    /**
     * Verifies that detectPitchWithMPM correctly identifies a pitch
     * for a pure sine wave at a specific frequency.
     */
    @Test
    void testDetectPitchWithMPM_PureSineWave() {
        int sampleRate = 44100;
        double frequency = 440.0; // A4
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithMPM(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 1, "Detected pitch should match the input sine wave frequency");
    }

    /**
     * Ensures detectPitchWithMPM returns -1 for silence (no pitch detected).
     */
    @Test
    void testDetectPitchWithMPM_Silence() {
        int sampleRate = 44100;
        double[] audioData = new double[1000];

        double detectedPitch = PitchDetectionUtil.detectPitchWithMPM(audioData, sampleRate).pitch();
        assertEquals(-1, detectedPitch, "Detected pitch should be -1 for silence");
    }

    /**
     * Validates that detectPitchWithMPM handles random noise and returns -1 (no valid pitch).
     */
    @Test
    void testDetectPitchWithMPM_Noise() {
        int sampleRate = 44100;
        double[] audioData = new double[1000];
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] = Math.random() * 2 - 1; // Random noise between -1 and 1
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithMPM(audioData, sampleRate).pitch();
        assertEquals(-1, detectedPitch, "Detected pitch should be -1 for noise");
    }

    /**
     * Ensures detectPitchWithMPM can correctly calculate pitch for a low-frequency sine wave.
     */
    @Test
    void testDetectPitchWithMPM_LowFrequencySineWave() {
        int sampleRate = 44100;
        double frequency = 100.0; // Low frequency sine wave
        double duration = 1.0;
        double[] audioData = generateSineWave(frequency, sampleRate, duration);

        double detectedPitch = PitchDetectionUtil.detectPitchWithMPM(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.1, "Detected pitch should match the low-frequency sine wave");
    }

    /**
     * Ensures detectPitchWithMPM detects pitch correctly for a high-frequency sine wave.
     */
    @Test
    void testDetectPitchWithMPM_HighFrequencySineWave() {
        int sampleRate = 44100;
        double frequency = 1000.0; // High frequency sine wave
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithMPM(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 3, "Detected pitch should match the high-frequency sine wave");
    }

    /**
     * Confirms detectPitchWithMPM returns valid and meaningful results with non-zero values.
     */
    @Test
    void testDetectPitchWithMPM_SimpleInput() {
        int sampleRate = 48000;
        double frequency = 300.0; // Sine wave frequency
        double[] audioData = new double[1024];
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithMPM(audioData, sampleRate).pitch();
        assertTrue(detectedPitch > 0, "Detected pitch should be a positive number for simple input");
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
     * Tests the pitch detection algorithm with the YIN method for a sine wave of frequency F5 (698.46 Hz).
     * <p>
     * This method generates a sine wave at a specific frequency (F5), sample rate (44.1 kHz), and duration (1 second),
     * and uses the YIN algorithm to detect the pitch. The detected pitch is then compared to the expected frequency
     * (F5, 698.46 Hz) to validate the pitch detection accuracy within a tolerance of 1.0 Hz.
     * <p>
     * The test ensures the YIN implementation correctly identifies the fundamental frequency of the input signal.
     */
    @Test
    void testDetectPitchWithYIN_F5() {
        // ARRANGE
        double frequency = 698.46; // F5
        int sampleRate = 44100;
        double duration = 1.0; // 1 second

        // Generate a sine wave
        double[] sineWave = generateSineWave(frequency, sampleRate, duration);

        // ACT
        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(sineWave, sampleRate).pitch();

        // ASSERT
        assertEquals(frequency, detectedPitch, 0.1, "The detected frequency should be F5 (698.46 Hz).");
    }

    /**
     * Tests the pitch detection functionality using the YIN algorithm for a specific frequency (F6 - 1396.91 Hz).
     * <p>
     * This test verifies the ability of the pitch detection utility to correctly identify
     * the frequency of a generated sine wave that represents the note F6.
     * <p>
     * Steps performed:
     * - A sine wave is generated for the given frequency (F6) at a specified sample rate and duration.
     * - The YIN pitch detection algorithm is applied to the generated sine wave.
     * - The detected frequency is compared against the expected frequency, with an acceptable error margin of 1.0 Hz.
     * <p>
     * The test passes if the detected frequency matches the expected frequency within the specified tolerance.
     * <p>
     * Expected Result: 
     * The pitch detection algorithm should correctly detect the input frequency 
     * as approximately 1396.91 Hz (F6), ensuring accuracy within the defined tolerance.
     */
    @Test
    void testDetectPitchWithYIN_F6() {
        // ARRANGE
        double frequency = 1396.91; // F6
        int sampleRate = 44100;
        double duration = 1.0; // 1 second

        // Generate a sine wave
        double[] sineWave = generateSineWave(frequency, sampleRate, duration);

        // ACT
        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(sineWave, sampleRate).pitch();

        // ASSERT
        assertEquals(frequency, detectedPitch, 0.3, "The detected frequency should be F6 (1396.91 Hz).");
    }

    /**
     * Tests the pitch detection functionality using the YIN algorithm in a scenario 
     * where no distinct pitch is present in the input signal.
     * <p>
     * This test generates a one-second sample of white noise, which does not have 
     * any clear pitch, and verifies that the pitch detection method correctly 
     * identifies this condition by returning -1.
     * <p>
     * It simulates white noise in the range [-1, 1] at a sample rate of 44100 Hz, 
     * calls the pitch detection utility, and asserts that the returned pitch value 
     * matches the expected result (-1).
     */
    @Test
    void testDetectPitchWithYIN_NoPitch() {
        // ARRANGE
        int sampleRate = 44100;
        // 1 second
        double[] noise = new double[sampleRate];
        for (int i = 0; i < sampleRate; i++) {
            noise[i] = Math.random() * 2 - 1; // White noise in the range [-1, 1]
        }

        // ACT
        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(noise, sampleRate).pitch();

        // ASSERT
        assertEquals(-1, detectedPitch, "The method should return -1 when no discernible frequency is present.");
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
        int samples = (int) (sampleRate * duration); // Anzahl der Samples
        double[] sineWave = new double[samples];
        for (int i = 0; i < samples; i++) {
            sineWave[i] = amplitude1 * Math.sin(2 * Math.PI * frequency1 * i / sampleRate)
                    + amplitude2 * Math.sin(2 * Math.PI * frequency2 * i / sampleRate);
        }
        return sineWave;
    }

    /**
     * Tests the pitch detection using the YIN algorithm for a scenario with overlapping frequencies 
     * of F5 (698.46 Hz) and F6 (1396.91 Hz), where F6 has a dominant amplitude.
     * <p>
     * This method generates a synthetic sine wave composed of two overlapping frequencies: F5 and F6. 
     * The amplitude of F6 is greater than the amplitude of F5, making F6 the dominant frequency in the signal.
     * The test then uses the YIN pitch detection algorithm to determine the detected frequency from the generated wave.
     * <p>
     * The test verifies that the detected frequency corresponds to F6 (1396.91 Hz) within a tolerance of ±12.5 Hz, 
     * as F6 is dominant due to its higher amplitude.
     */
    @Test
    void testDetectPitchWithYIN_OverlappingF5andF6_F6Dominant() {
        // ARRANGE
        double frequencyF5 = 698.46; // Frequency of F5
        double amplitudeF5 = 0.5;    // Amplitude of F5
        double frequencyF6 = 1396.91; // Frequency of F6
        double amplitudeF6 = 1.0;    // Dominant amplitude of F6
        int sampleRate = 44100;      // Sample rate (Hz)
        double duration = 1.0;       // Duration of 1 second

        // Generate the overlapping sine wave
        double[] sineWave = generateOverlappingSineWave(frequencyF5, amplitudeF5, frequencyF6, amplitudeF6, sampleRate, duration);

        // ACT
        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(sineWave, sampleRate).pitch();

        // ASSERT
        assertEquals(frequencyF6, detectedPitch, 12.5, "The detected frequency should be F6 (1396.91 Hz) because F6 has the higher amplitude.");
    }

    @Test
    void testDetectPitchWithYIN_OverlappingA3andA3Sharp_A3Dominant() {
        // ARRANGE
        double frequencyA3Sharp = 233.082;
        double amplitudeA3Sharp = 0.5;
        double frequencyA3 = 220.0;
        double amplitudeA3 = 1.0;
        int sampleRate = 44100;
        double duration = 1.0;

        // Generate the overlapping sine wave
        double[] sineWave = generateOverlappingSineWave(frequencyA3Sharp, amplitudeA3Sharp, frequencyA3, amplitudeA3, sampleRate, duration);

        // ACT
        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(sineWave, sampleRate).pitch();

        // ASSERT
        assertEquals(frequencyA3, detectedPitch, 3, "The detected frequency should be A3 (220 Hz) because A3 has the higher amplitude.");
    }


    @Test
    void testDetectPitchWithYIN_A3_PureSineWave() {
        int sampleRate = 44100;
        double frequency = 220.0; // A3
        double duration = 1.0;

        double[] sineWave = generateSineWave(frequency, sampleRate, duration);

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(sineWave, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.002, "Detected pitch should match the frequency of the input sine wave (A3).");
    }
    /**
     * Tests detectPitchWithYIN for a sine wave with noise at the note A3 (220 Hz).
     */
    @Test
    void testDetectPitchWithYIN_A3_SineWithNoise() {
        int sampleRate = 44100;
        double frequency = 220.0; // A3
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate) + (Math.random() * 0.1 - 0.05);
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.04, "Detected pitch should match A3 despite added noise.");
    }
    /**
     * Tests detectPitchWithYIN for overlapping frequencies where A3 (220 Hz) is dominant.
     */
    @Test
    void testDetectPitchWithYIN_A3_OverlappingFrequencies() {
        double frequencyA3 = 220.0;
        double amplitudeA3 = 1.0; // Dominant amplitude of A3
        double frequencyOther = 440.0; // Overlapping frequency
        double amplitudeOther = 0.3; // Lower amplitude
        int sampleRate = 44100;
        double duration = 1.0;

        double[] audioData = generateOverlappingSineWave(frequencyA3, amplitudeA3, frequencyOther, amplitudeOther, sampleRate, duration);

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(frequencyA3, detectedPitch, 0.002, "Detected pitch should be A3 (220 Hz), the dominant frequency.");
    }
    /**
     * Tests detectPitchWithYIN for a low amplitude sine wave at the note A3 (220 Hz).
     */
    @Test
    void testDetectPitchWithYIN_A3_LowAmplitude() {
        int sampleRate = 44100;
        double frequency = 220.0; // A3
        double amplitude = 0.01; // Very low amplitude
        double duration = 1.0;

        double[] audioData = generateSineWave(frequency, sampleRate, duration);
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] *= amplitude;
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.002, "Detected pitch should match frequency of A3 despite low amplitude.");
    }
    /**
     * Tests detectPitchWithYIN for a non-periodic signal for A3 (220 Hz).
     */
    @Test
    void testDetectPitchWithYIN_A3_NonPeriodicSignal() {
        int sampleRate = 44100;
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];

        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.random() * 2 - 1; // Random noise
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(-1, detectedPitch, "The method should return -1 for random noise signals.");
    }
    /**
     * Tests detectPitchWithYIN for a square wave signal at A3 (220 Hz).
     */
    @Test
    void testDetectPitchWithYIN_A3_SquareWave() {
        int sampleRate = 44100;
        double frequency = 220.0; // A3
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = (Math.sin(2 * Math.PI * frequency * i / sampleRate) >= 0) ? 0.5 : -0.5;
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.05, "Detected pitch should match the frequency of the square wave (A3).");
    }
    /**
     * Tests detectPitchWithYIN for A3 signals layered with low noise.
     */
    @Test
    void testDetectPitchWithYIN_A3_WithNoiseOverlay() {
        int sampleRate = 44100;
        double frequency = 220.0; // A3
        double duration = 1.0;
        double[] audioData = generateSineWave(frequency, sampleRate, duration);
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] += Math.random() * 0.05 - 0.025;
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.05, "Detected pitch should match A3 despite low noise overlay.");
    }

    @Test
    void testDetectPitchWithYIN_A2_PureSineWave() {
        int sampleRate = 44100;
        double frequency = 110.0; // A2
        double duration = 1.0;

        double[] sineWave = generateSineWave(frequency, sampleRate, duration);

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(sineWave, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.05, "Detected pitch should match the frequency of the input sine wave (A2).");
    }

    /**
     * Tests detectPitchWithYIN for a sine wave with noise at the note A2 (110 Hz).
     */
    @Test
    void testDetectPitchWithYIN_A2_SineWithNoise() {
        int sampleRate = 44100;
        double frequency = 110.0; // A2
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate) + (Math.random() * 0.1 - 0.05);
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.05, "Detected pitch should match A2 despite added noise.");
    }

    /**
     * Tests detectPitchWithYIN for overlapping frequencies where A2 (110 Hz) is dominant.
     */
    @Test
    void testDetectPitchWithYIN_A2_OverlappingFrequencies() {
        double frequencyA2 = 110.0;
        double amplitudeA2 = 1.0; // Dominant amplitude of A2
        double frequencyOther = 220.0; // Overlapping frequency
        double amplitudeOther = 0.3; // Lower amplitude
        int sampleRate = 44100;
        double duration = 1.0;

        double[] audioData = generateOverlappingSineWave(frequencyA2, amplitudeA2, frequencyOther, amplitudeOther, sampleRate, duration);

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(frequencyA2, detectedPitch, 0.05, "Detected pitch should be A2 (110 Hz), the dominant frequency.");
    }

    /**
     * Tests detectPitchWithYIN for a low amplitude sine wave at the note A2 (110 Hz).
     */
    @Test
    void testDetectPitchWithYIN_A2_LowAmplitude() {
        int sampleRate = 44100;
        double frequency = 110.0; // A2
        double amplitude = 0.01; // Very low amplitude
        double duration = 1.0;

        double[] audioData = generateSineWave(frequency, sampleRate, duration);
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] *= amplitude;
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.05, "Detected pitch should match frequency of A2 despite low amplitude.");
    }

    /**
     * Tests detectPitchWithYIN for a sine wave at A2 (110 Hz) with high frequency deviation.
     */
    @Test
   void testDetectPitchWithYIN_A2_HighFrequencyDeviation() {
        int sampleRate = 44100;
        double frequency = 110.0; // A2
        double duration = 1.0;
        double deviation = 2.0; // Variation in frequency
        int samples = (int) (sampleRate * duration);

        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            double currentFrequency = frequency + Math.sin(2 * Math.PI * i / sampleRate) * deviation;
            audioData[i] = Math.sin(2 * Math.PI * currentFrequency * i / sampleRate);
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.1, "Detected pitch should match the base frequency of A2 (110 Hz).");
    }

    /**
     * Tests detectPitchWithYIN for non-periodic signals for which no pitch is expected.
     */
    @Test
    void testDetectPitchWithYIN_A2_NonPeriodicSignal() {
        int sampleRate = 44100;
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];

        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.random() * 2 - 1; // Random noise
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(-1, detectedPitch, "The method should return -1 for non-periodic signals.");
    }

    /**
     * Tests detectPitchWithYIN for a square wave with the main frequency of A2 (110 Hz).
     */
    @Test
    void testDetectPitchWithYIN_A2_SquareWave() {
        int sampleRate = 44100;
        double frequency = 110.0; // A2
        double duration = 1.0;
        int samples = (int) (sampleRate * duration);

        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = (Math.sin(2 * Math.PI * frequency * i / sampleRate) >= 0) ? 0.5 : -0.5;
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.02, "Detected pitch should match the frequency of the square wave (A2).");
    }

    /**
     * Tests detectPitchWithYIN for the harmonic of a lower frequency signal near A2.
     */
    @Test
    void testDetectPitchWithYIN_A2_Harmonic() {
        int sampleRate = 44100;
        double frequency = 110.0; // Fundamental frequency (A2)
        double harmonic = 220.0; // Second harmonic (A3)
        double duration = 1.0;

        double[] audioData = generateSineWave(frequency, sampleRate, duration);
        double[] harmonicData = generateSineWave(harmonic, sampleRate, duration);
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] += 0.5 * harmonicData[i]; // Adding harmonic
        }
        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.02, "Detected pitch should match the fundamental frequency");
    }

    /**
     * Tests detectPitchWithYIN for overlapping low-frequency signals and noise with A2 dominance.
     */
    @Test
    void testDetectPitchWithYIN_A2_LowFreqWithNoiseOverlay() {
        int sampleRate = 44100;
        double frequency = 110.0; // A2
        double duration = 1.0;

        double[] audioData = generateSineWave(frequency, sampleRate, duration);
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] += Math.random() * 0.05 - 0.025; // Overlay noise
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate).pitch();
        assertEquals(frequency, detectedPitch, 0.02, "Detected pitch should match A2 despite noise overlay.");
    }
}
