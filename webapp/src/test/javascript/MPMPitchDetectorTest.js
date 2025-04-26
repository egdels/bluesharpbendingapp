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

import MPMPitchDetector from '../../main/resources/static/scripts/MPMPitchDetector.js';

/**
 * Test suite for the MPMPitchDetector.
 * This class tests the functionality of the MPM algorithm implementation in JavaScript.
 */
describe('MPMPitchDetector', () => {
    const SAMPLE_RATE = 44100;
    const TOLERANCE = 1.0;

    beforeEach(() => {
        // Reset to default frequency range before each test
        MPMPitchDetector.setMinFrequency(MPMPitchDetector.DEFAULT_MIN_FREQUENCY);
        MPMPitchDetector.setMaxFrequency(MPMPitchDetector.DEFAULT_MAX_FREQUENCY);
    });

    /**
     * Tests the detection of pitch with a pure sine wave.
     */
    test('should detect pitch of a pure sine wave', () => {
        // Arrange
        const frequency = 440.0; // A4
        const duration = 1.0;
        const audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

        // Act
        const result = MPMPitchDetector.detectPitch(audioData, SAMPLE_RATE);

        // Assert
        expect(Math.abs(result.pitch - frequency)).toBeLessThanOrEqual(TOLERANCE);
        expect(result.confidence).toBeGreaterThan(0.9);
    });

    /**
     * Tests the detection of pitch with silence.
     */
    test('should return NO_DETECTED_PITCH for silence', () => {
        // Arrange
        const audioData = new Array(SAMPLE_RATE).fill(0); // 1 second of silence

        // Act
        const result = MPMPitchDetector.detectPitch(audioData, SAMPLE_RATE);

        // Assert
        expect(result.pitch).toBe(MPMPitchDetector.NO_DETECTED_PITCH);
        expect(result.confidence).toBe(0.0);
    });

    /**
     * Tests the detection of pitch with noise.
     */
    test('should return NO_DETECTED_PITCH for noise', () => {
        // Arrange
        const audioData = new Array(SAMPLE_RATE); // 1 second of random noise
        for (let i = 0; i < audioData.length; i++) {
            audioData[i] = Math.random() * 2 - 1; // Random values between -1 and 1
        }

        // Act
        const result = MPMPitchDetector.detectPitch(audioData, SAMPLE_RATE);

        // Assert
        expect(result.pitch).toBe(MPMPitchDetector.NO_DETECTED_PITCH);
        expect(result.confidence).toBe(0.0);
    });

    /**
     * Tests the detection of pitch with a low frequency.
     */
    test('should detect pitch of a low frequency sine wave', () => {
        // Arrange
        const frequency = 80.0; // Low frequency
        const duration = 1.0;
        const audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

        // Act
        const result = MPMPitchDetector.detectPitch(audioData, SAMPLE_RATE);

        // Assert
        expect(Math.abs(result.pitch - frequency)).toBeLessThanOrEqual(TOLERANCE);
        expect(result.confidence).toBeGreaterThan(0.9);
    });

    /**
     * Tests the detection of pitch with a high frequency.
     */
    test('should detect pitch of a high frequency sine wave', () => {
        // Arrange
        const frequency = 4000.0; // High frequency
        const duration = 1.0;
        const audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

        // Act
        const result = MPMPitchDetector.detectPitch(audioData, SAMPLE_RATE);

        // Assert
        expect(Math.abs(result.pitch - frequency)).toBeLessThanOrEqual(TOLERANCE);
        expect(result.confidence).toBeGreaterThan(0.9);
    });

    /**
     * Tests the detection of pitch with harmonics.
     */
    test('should detect fundamental frequency with harmonics', () => {
        // Arrange
        const fundamentalFrequency = 440.0; // A4
        const harmonicFrequency = 880.0; // A5 (first harmonic)
        const duration = 1.0;
        const fundamental = generateSineWave(fundamentalFrequency, SAMPLE_RATE, duration);
        const harmonic = generateSineWave(harmonicFrequency, SAMPLE_RATE, duration);

        const audioData = new Array(fundamental.length);
        for (let i = 0; i < audioData.length; i++) {
            audioData[i] = fundamental[i] + 0.5 * harmonic[i]; // Add harmonic with half the amplitude
        }

        // Act
        const result = MPMPitchDetector.detectPitch(audioData, SAMPLE_RATE);

        // Assert
        expect(Math.abs(result.pitch - fundamentalFrequency)).toBeLessThanOrEqual(TOLERANCE);
        expect(result.confidence).toBeGreaterThan(0.9);
    });

    /**
     * Tests the detection of pitch with different amplitudes.
     */
    test.each([
        [440.0, 0.01, 1.0], // Low amplitude
        [440.0, 0.1, 1.0],  // Medium amplitude
        [440.0, 1.0, 1.0]   // High amplitude
    ])('should detect pitch with different amplitudes', (frequency, amplitude, tolerance) => {
        // Arrange
        const duration = 1.0;
        const audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

        // Scale the amplitude
        for (let i = 0; i < audioData.length; i++) {
            audioData[i] *= amplitude;
        }

        // Act
        const result = MPMPitchDetector.detectPitch(audioData, SAMPLE_RATE);

        // Assert
        expect(Math.abs(result.pitch - frequency)).toBeLessThanOrEqual(tolerance);
        expect(result.confidence).toBeGreaterThan(0.9);
    });

    /**
     * Tests the detection of pitch with different frequencies.
     */
    test.each([
        [100.0, 1.0], // Low frequency
        [440.0, 1.0], // Medium frequency
        [2000.0, 5.0] // High frequency
    ])('should detect pitch with different frequencies', (frequency, tolerance) => {
        // Arrange
        const duration = 1.0;
        const audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

        // Act
        const result = MPMPitchDetector.detectPitch(audioData, SAMPLE_RATE);

        // Assert
        expect(Math.abs(result.pitch - frequency)).toBeLessThanOrEqual(tolerance);
        expect(result.confidence).toBeGreaterThan(0.9);
    });

    /**
     * Tests the frequency range setting.
     */
    test('should respect frequency range settings', () => {
        // Arrange
        const minFreq = 100.0;
        const maxFreq = 3000.0;
        MPMPitchDetector.setMinFrequency(minFreq);
        MPMPitchDetector.setMaxFrequency(maxFreq);

        const frequency = 440.0; // A4
        const duration = 1.0;
        const audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

        // Act
        const result = MPMPitchDetector.detectPitch(audioData, SAMPLE_RATE);

        // Assert
        expect(Math.abs(result.pitch - frequency)).toBeLessThanOrEqual(TOLERANCE);
        expect(MPMPitchDetector.getMinFrequency()).toBe(minFreq);
        expect(MPMPitchDetector.getMaxFrequency()).toBe(maxFreq);
    });

    /**
     * Tests the detection of pitch with a frequency outside the range.
     */
    test('should not detect pitch outside frequency range', () => {
        // Arrange
        const minFreq = 500.0;
        const maxFreq = 1000.0;
        MPMPitchDetector.setMinFrequency(minFreq);
        MPMPitchDetector.setMaxFrequency(maxFreq);

        const frequency = 440.0; // A4 (below min frequency)
        const duration = 1.0;
        const audioData = generateSineWave(frequency, SAMPLE_RATE, duration);

        // Act
        const result = MPMPitchDetector.detectPitch(audioData, SAMPLE_RATE);

        // Assert
        expect(result.pitch).toBe(MPMPitchDetector.NO_DETECTED_PITCH);
    });

    /**
     * Tests the default frequency range.
     */
    test('should have correct default frequency range', () => {
        // Arrange
        MPMPitchDetector.setMinFrequency(MPMPitchDetector.DEFAULT_MIN_FREQUENCY);
        MPMPitchDetector.setMaxFrequency(MPMPitchDetector.DEFAULT_MAX_FREQUENCY);
        
        // Assert
        expect(MPMPitchDetector.getMinFrequency()).toBe(MPMPitchDetector.DEFAULT_MIN_FREQUENCY);
        expect(MPMPitchDetector.getMaxFrequency()).toBe(MPMPitchDetector.DEFAULT_MAX_FREQUENCY);
    });

    /**
     * Tests the detection of pitch with mixed frequencies.
     */
    test.each([
        // mainFrequency, mainAmplitude, subharmonicFrequency, subharmonicAmplitude, tolerance, minConfidence
        [934.6, 1.0, 460.0, 0.3, 5.0, 0.8],  // Weak subharmonic, high confidence required
        [934.6, 1.0, 460.0, 0.5, 10.0, 0.3], // Moderate subharmonic
        [934.6, 1.9, 460.0, 1.0, 10.0, 0.1]  // Dominant main frequency, lower confidence acceptable
    ])('should detect pitch with mixed frequencies', (mainFrequency, mainAmplitude, subharmonicFrequency, subharmonicAmplitude, tolerance, minConfidence) => {
        // Arrange
        const duration = 1; // 1 second

        // Generate a mixed signal with both main and subharmonic frequencies
        const mixedWave = generateMixedSineWaveWithAmplitudes(mainFrequency, mainAmplitude, subharmonicFrequency, subharmonicAmplitude, SAMPLE_RATE, duration);
        
        // Act
        const result = MPMPitchDetector.detectPitch(mixedWave, SAMPLE_RATE);
        
        // Assert
        expect(Math.abs(result.pitch - mainFrequency)).toBeLessThanOrEqual(tolerance);
        expect(result.confidence).toBeGreaterThan(minConfidence);
    });

    /**
     * Generates a sine wave based on the given frequency, sample rate, and duration.
     */
    function generateSineWave(frequency, sampleRate, duration) {
        const samples = Math.floor(sampleRate * duration);
        const sineWave = new Array(samples);
        
        for (let i = 0; i < samples; i++) {
            sineWave[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }
        
        return sineWave;
    }

    /**
     * Helper method: Generates a sine wave with two combined frequencies and different amplitudes
     */
    function generateMixedSineWaveWithAmplitudes(primaryFreq, primaryAmp, secondaryFreq, secondaryAmp, sampleRate, duration) {
        const sampleCount = sampleRate * duration;
        const mixedWave = new Array(sampleCount);
        
        for (let i = 0; i < sampleCount; i++) {
            mixedWave[i] = primaryAmp * Math.sin(2.0 * Math.PI * primaryFreq * i / sampleRate)
                    + secondaryAmp * Math.sin(2.0 * Math.PI * secondaryFreq * i / sampleRate);
        }
        
        return mixedWave;
    }

    /**
     * Helper method: Generates a sine wave with added white noise
     */
    function generateSineWaveWithNoise(frequency, sampleRate, durationMs, noiseLevel) {
        const sampleCount = Math.floor(sampleRate * durationMs / 1000);
        const noisyWave = new Array(sampleCount);
        
        for (let i = 0; i < sampleCount; i++) {
            noisyWave[i] = Math.sin(2.0 * Math.PI * frequency * i / sampleRate) + noiseLevel * (Math.random() - 0.5);
        }
        
        return noisyWave;
    }

    /**
     * Generates a sine wave signal that combines two overlapping sine waves with different frequencies and amplitudes.
     */
    function generateOverlappingSineWave(frequency1, amplitude1, frequency2, amplitude2, sampleRate, duration) {
        const samples = Math.floor(sampleRate * duration);
        const sineWave = new Array(samples);
        
        for (let i = 0; i < samples; i++) {
            sineWave[i] = amplitude1 * Math.sin(2 * Math.PI * frequency1 * i / sampleRate)
                    + amplitude2 * Math.sin(2 * Math.PI * frequency2 * i / sampleRate);
        }
        
        return sineWave;
    }
});