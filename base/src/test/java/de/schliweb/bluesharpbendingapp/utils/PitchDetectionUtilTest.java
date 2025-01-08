package de.schliweb.bluesharpbendingapp.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PitchDetectionUtilTest {

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

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate);
        assertEquals(frequency, detectedPitch, 2, "Detected pitch should match the input sine wave frequency");
    }

    /**
     * Ensures detectPitchWithYIN returns -1 for silence (no pitch detected).
     */
    @Test
    void testDetectPitchWithYIN_Silence() {
        int sampleRate = 44100;
        double[] audioData = new double[1000];

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate);
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

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate);
        assertEquals(-1, detectedPitch, "Detected pitch should be -1 for noise");
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

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate);
        assertEquals(frequency, detectedPitch, 0.1, "Detected pitch should match the low-frequency sine wave");
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
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate) +
                    (0.5 * Math.sin(2 * Math.PI * harmonic * i / sampleRate));
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate);
        assertEquals(frequency, detectedPitch, 2, "Detected pitch should match the fundamental frequency");
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

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate);
        assertEquals(frequency, detectedPitch, 2, "Detected pitch should match the sine wave frequency even at low amplitude");
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

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate);
        assertEquals(frequency, detectedPitch, 2, "Detected pitch should match the sine wave frequency with large buffer size");
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

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate);
        assertEquals(frequency, detectedPitch, 5, "Detected pitch should match the high-frequency sine wave");
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

        double detectedPitch = PitchDetectionUtil.detectPitchWithYIN(audioData, sampleRate);
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

        double detectedPitch = PitchDetectionUtil.detectPitchWithMPM(audioData, sampleRate);
        assertEquals(frequency, detectedPitch, 1, "Detected pitch should match the input sine wave frequency");
    }

    /**
     * Ensures detectPitchWithMPM returns -1 for silence (no pitch detected).
     */
    @Test
    void testDetectPitchWithMPM_Silence() {
        int sampleRate = 44100;
        double[] audioData = new double[1000];

        double detectedPitch = PitchDetectionUtil.detectPitchWithMPM(audioData, sampleRate);
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

        double detectedPitch = PitchDetectionUtil.detectPitchWithMPM(audioData, sampleRate);
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
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }

        double detectedPitch = PitchDetectionUtil.detectPitchWithMPM(audioData, sampleRate);
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

        double detectedPitch = PitchDetectionUtil.detectPitchWithMPM(audioData, sampleRate);
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

        double detectedPitch = PitchDetectionUtil.detectPitchWithMPM(audioData, sampleRate);
        assertTrue(detectedPitch > 0, "Detected pitch should be a positive number for simple input");
    }

}