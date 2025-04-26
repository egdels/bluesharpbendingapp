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

import YINPitchDetector from '../../main/resources/static/scripts/YINPitchDetector.js';

/**
 * Test suite for the YINPitchDetector.
 * This class tests the functionality of the YIN algorithm implementation in JavaScript.
 */
describe('YINPitchDetector', () => {
    const SAMPLE_RATE = 44100;
    const TOLERANCE = 0.5;

    beforeEach(() => {
        // Reset to default frequency range before each test
        YINPitchDetector.setMinFrequency(YINPitchDetector.DEFAULT_MIN_FREQUENCY);
        YINPitchDetector.setMaxFrequency(YINPitchDetector.DEFAULT_MAX_FREQUENCY);
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
        const duration = 1; // 1 second

        // Generate a mixed signal with both main and subharmonic frequencies
        const mixedWave = generateMixedSineWaveWithAmplitudes(mainFrequency, mainAmplitude, subharmonicFrequency, subharmonicAmplitude, SAMPLE_RATE, duration);

        // Invoke the pitch detection algorithm to find the dominant frequency
        const result = YINPitchDetector.detectPitch(mixedWave, SAMPLE_RATE);

        // Assert that the detected frequency is within the tolerance of the main frequency
        expect(Math.abs(result.pitch - mainFrequency)).toBeLessThanOrEqual(tolerance);
        
        // Assert that the confidence level is above the minimum threshold
        expect(result.confidence).toBeGreaterThan(minConfidence);
    });

    /**
     * Tests the detection of pitch with a pure sine wave.
     */
    test('should detect pitch of a pure sine wave', () => {
        const frequency = 440.0; // A4
        const duration = 1.0;
        const samples = Math.floor(SAMPLE_RATE * duration);
        const audioData = new Array(samples);
        
        for (let i = 0; i < samples; i++) {
            audioData[i] = Math.sin(2 * Math.PI * frequency * i / SAMPLE_RATE);
        }

        const result = YINPitchDetector.detectPitch(audioData, SAMPLE_RATE);
        
        expect(Math.abs(result.pitch - frequency)).toBeLessThanOrEqual(0.02);
    });

    /**
     * Tests the detection of pitch with silence.
     */
    test.each([44100, 48000])('should return NO_DETECTED_PITCH for silence with sample size %i', (sampleSize) => {
        const audioData = new Array(sampleSize).fill(0);

        const result = YINPitchDetector.detectPitch(audioData, SAMPLE_RATE);
        
        expect(result.pitch).toBe(YINPitchDetector.NO_DETECTED_PITCH);
    });

    /**
     * Tests the detection of pitch with noise.
     */
    test('should return NO_DETECTED_PITCH for noise', () => {
        const audioData = new Array(2000);
        
        for (let i = 0; i < audioData.length; i++) {
            audioData[i] = Math.random() * 2 - 1; // Random noise between -1 and 1
        }

        const result = YINPitchDetector.detectPitch(audioData, SAMPLE_RATE);
        
        expect(result.pitch).toBe(YINPitchDetector.NO_DETECTED_PITCH);
    });

    /**
     * Tests the detection of pitch with white noise.
     */
    test('should detect frequency with white noise', () => {
        const frequency = 934.6;
        const durationMs = 1000; // 1 second
        const noiseLevel = 0.1; // White noise as 10% of the signal amplitude
        
        const noisyWave = generateSineWaveWithNoise(frequency, SAMPLE_RATE, durationMs, noiseLevel);

        const result = YINPitchDetector.detectPitch(noisyWave, SAMPLE_RATE);

        expect(Math.abs(result.pitch - frequency)).toBeLessThanOrEqual(TOLERANCE);
        expect(result.confidence).toBeGreaterThan(0.6);
    });

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