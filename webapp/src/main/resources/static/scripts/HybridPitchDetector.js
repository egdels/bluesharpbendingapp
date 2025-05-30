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

import YINPitchDetector from './YINPitchDetector.js';
import MPMPitchDetector from './MPMPitchDetector.js';
import FFTDetector from './FFTDetector.js';

/**
 * The HybridPitchDetector class provides a robust, hybrid approach for pitch detection by
 * combining multiple algorithms and techniques to achieve high accuracy across a wide
 * frequency range. It incorporates noise detection, energy analysis, and pitch estimation
 * tailored to different frequency bands.
 *
 * This class leverages the YIN, MPM, and Fourier Transform algorithms to effectively handle
 * pitch detection for low, mid, and high-frequency ranges, respectively. By analyzing the
 * energy distribution and characteristics of the input audio signal, it dynamically applies
 * the most appropriate algorithm for pitch detection.
 *
 * This detector is designed for applications in music analysis, speech processing, and other
 * domains requiring precise pitch estimation.
 */
class HybridPitchDetector {
    // Constants
    static NO_DETECTED_PITCH = -1; // Indicates no pitch detected
    static DEFAULT_MIN_FREQUENCY = 80.0; // Default minimum frequency in Hz
    static DEFAULT_MAX_FREQUENCY = 4835.0; // Default maximum frequency in Hz
    
    // Thresholds and frequency ranges for algorithm selection
    static THRESHOLD_LOW_FREQUENCY_ENERGY = 750;
    static FREQUENCY_RANGE_LOW = 275;
    static FREQUENCY_RANGE_HIGH = 900;
    static THRESHOLD_HIGH_FREQUENCY_ENERGY = 400;
    
    // Configurable properties
    static minFrequency = HybridPitchDetector.DEFAULT_MIN_FREQUENCY;
    static maxFrequency = HybridPitchDetector.DEFAULT_MAX_FREQUENCY;

    /**
     * Sets the minimum frequency that can be detected (in Hz).
     * @param {number} frequency - The minimum frequency in Hz
     */
    static setMinFrequency(frequency) {
        HybridPitchDetector.minFrequency = frequency;
        YINPitchDetector.setMinFrequency(frequency);
        MPMPitchDetector.setMinFrequency(frequency);
        FFTDetector.setMinFrequency(frequency);
    }

    /**
     * Gets the minimum frequency that can be detected (in Hz).
     * @returns {number} The minimum frequency in Hz
     */
    static getMinFrequency() {
        return HybridPitchDetector.minFrequency;
    }

    /**
     * Sets the maximum frequency that can be detected (in Hz).
     * @param {number} frequency - The maximum frequency in Hz
     */
    static setMaxFrequency(frequency) {
        HybridPitchDetector.maxFrequency = frequency;
        YINPitchDetector.setMaxFrequency(frequency);
        MPMPitchDetector.setMaxFrequency(frequency);
        FFTDetector.setMaxFrequency(frequency);
    }

    /**
     * Gets the maximum frequency that can be detected (in Hz).
     * @returns {number} The maximum frequency in Hz
     */
    static getMaxFrequency() {
        return HybridPitchDetector.maxFrequency;
    }

    /**
     * Sets the frequency range threshold for low frequencies.
     * This method is primarily used for testing and optimization purposes.
     *
     * @param {number} frequency - The new frequency range threshold value in Hz
     */
    static setFrequencyRangeLow(frequency) {
        HybridPitchDetector.FREQUENCY_RANGE_LOW = frequency;
    }

    /**
     * Gets the current frequency range threshold for low frequencies.
     *
     * @returns {number} The current frequency range threshold value in Hz
     */
    static getFrequencyRangeLow() {
        return HybridPitchDetector.FREQUENCY_RANGE_LOW;
    }

    /**
     * Sets the frequency range threshold for high frequencies.
     * This method is primarily used for testing and optimization purposes.
     *
     * @param {number} frequency - The new frequency range threshold value in Hz
     */
    static setFrequencyRangeHigh(frequency) {
        HybridPitchDetector.FREQUENCY_RANGE_HIGH = frequency;
    }

    /**
     * Gets the current frequency range threshold for high frequencies.
     *
     * @returns {number} The current frequency range threshold value in Hz
     */
    static getFrequencyRangeHigh() {
        return HybridPitchDetector.FREQUENCY_RANGE_HIGH;
    }

    /**
     * Sets the threshold for low frequency energy.
     * This method is primarily used for testing and optimization purposes.
     *
     * @param {number} threshold - The new threshold value
     */
    static setThresholdLowFrequencyEnergy(threshold) {
        HybridPitchDetector.THRESHOLD_LOW_FREQUENCY_ENERGY = threshold;
    }

    /**
     * Gets the current threshold for low frequency energy.
     *
     * @returns {number} The current threshold value
     */
    static getThresholdLowFrequencyEnergy() {
        return HybridPitchDetector.THRESHOLD_LOW_FREQUENCY_ENERGY;
    }

    /**
     * Sets the threshold for high frequency energy.
     * This method is primarily used for testing and optimization purposes.
     *
     * @param {number} threshold - The new threshold value
     */
    static setThresholdHighFrequencyEnergy(threshold) {
        HybridPitchDetector.THRESHOLD_HIGH_FREQUENCY_ENERGY = threshold;
    }

    /**
     * Gets the current threshold for high frequency energy.
     *
     * @returns {number} The current threshold value
     */
    static getThresholdHighFrequencyEnergy() {
        return HybridPitchDetector.THRESHOLD_HIGH_FREQUENCY_ENERGY;
    }

    /**
     * Detects the pitch of a given audio signal using a hybrid approach that combines
     * multiple pitch detection algorithms (e.g., YIN, FFT, MPM). The detection process
     * includes noise diagnosis, energy analysis for low and high frequencies, and
     * fallback mechanisms to improve accuracy across a wide range of input signals.
     *
     * @param {Array<number>} audioData - An array of doubles representing the audio signal data in the time domain
     * @param {number} sampleRate - The sample rate of the audio data in Hz
     * @returns {Object} An object containing the detected pitch in Hz and the confidence score
     */
    static detectPitch(audioData, sampleRate) {
        // Step 1: Noise-Diagnose – if signal contains only noise
        if (HybridPitchDetector.isLikelyNoise(audioData)) {
            return { pitch: HybridPitchDetector.NO_DETECTED_PITCH, confidence: 0.0 };
        }

        // Step 2: Energy analysis for frequencies below FREQUENCY_RANGE_LOW
        const lowFrequencyEnergy = HybridPitchDetector.calculateEnergyUsingGoertzel(audioData, sampleRate, HybridPitchDetector.FREQUENCY_RANGE_LOW);
        
        // Step 3: Decision based on energy analysis
        if (lowFrequencyEnergy > HybridPitchDetector.THRESHOLD_LOW_FREQUENCY_ENERGY) {
            // Use YIN for frequencies below FREQUENCY_RANGE_LOW
            const yinResult = YINPitchDetector.detectPitch(audioData, sampleRate);
            if (yinResult.pitch !== HybridPitchDetector.NO_DETECTED_PITCH) {
                return yinResult;
            }
        } else {
            // Step 4: Energy analysis for high frequencies
            const highFrequencyEnergy = HybridPitchDetector.calculateEnergyUsingGoertzel(audioData, sampleRate, HybridPitchDetector.FREQUENCY_RANGE_HIGH);

            // If energy in high frequency range is high, use FFT
            if (highFrequencyEnergy > HybridPitchDetector.THRESHOLD_HIGH_FREQUENCY_ENERGY) {
                const fftResult = FFTDetector.detectPitch(audioData, sampleRate);
                if (fftResult.pitch !== HybridPitchDetector.NO_DETECTED_PITCH) {
                    return fftResult;
                }
            }

            // If high frequency energy is NOT high enough, use MPM
            const mpmResult = MPMPitchDetector.detectPitch(audioData, sampleRate);
            if (mpmResult.pitch !== HybridPitchDetector.NO_DETECTED_PITCH) {
                return mpmResult;
            }
        }

        // Fallback: Try again with YIN
        return YINPitchDetector.detectPitch(audioData, sampleRate);
    }

    /**
     * Calculates the energy of a specific frequency component in the given audio data
     * using the Goertzel algorithm. This method is designed for efficient frequency
     * analysis, particularly when evaluating a single frequency component.
     *
     * @param {Array<number>} audioData - An array of doubles representing the audio signal data in the time domain
     * @param {number} sampleRate - The sample rate of the audio data in Hz
     * @param {number} frequency - The target frequency in Hz to calculate the energy for
     * @returns {number} The calculated energy of the specified frequency in the audio data
     */
    static calculateEnergyUsingGoertzel(audioData, sampleRate, frequency) {
        const samples = audioData.length;
        const omega = 2.0 * Math.PI * frequency / sampleRate; // Calculate target frequency
        const cosine = Math.cos(omega);
        const coeff = 2.0 * cosine;
        let q0 = 0, q1 = 0, q2 = 0;

        for (let i = 0; i < samples; i++) {
            q0 = coeff * q1 - q2 + audioData[i];
            q2 = q1;
            q1 = q0;
        }

        // Goertzel energy measurement:
        return q1 * q1 + q2 * q2 - coeff * q1 * q2;
    }

    /**
     * Determines if the audio signal is likely to be noise based on statistical properties.
     * 
     * This method analyzes the audio data by calculating:
     * 1. The mean and standard deviation to determine the coefficient of variation (CV)
     * 2. The zero-crossing rate (ZCR) to measure how often the signal changes sign
     * 
     * White noise typically has a high coefficient of variation (CV > 5.0) and
     * a high zero-crossing rate (ZCR > 0.4). These thresholds were determined
     * empirically to provide good discrimination between musical signals and noise.
     *
     * @param {Array<number>} audioData - The audio data to analyze
     * @returns {boolean} True if the signal is likely to be noise, false otherwise
     */
    static isLikelyNoise(audioData) {
        if (audioData.length === 0) {
            return true;
        }

        // Calculate mean
        const sum = audioData.reduce((acc, val) => acc + val, 0);
        const mean = sum / audioData.length;

        // Calculate standard deviation
        const squaredDiffs = audioData.map(val => {
            const diff = val - mean;
            return diff * diff;
        });
        const avgSquaredDiff = squaredDiffs.reduce((acc, val) => acc + val, 0) / audioData.length;
        const stdDev = Math.sqrt(avgSquaredDiff);

        // Calculate zero-crossing rate
        let zeroCrossings = 0;
        for (let i = 1; i < audioData.length; i++) {
            if ((audioData[i] >= 0 && audioData[i - 1] < 0) || (audioData[i] < 0 && audioData[i - 1] >= 0)) {
                zeroCrossings++;
            }
        }
        const zeroCrossingRate = zeroCrossings / (audioData.length - 1);

        // White noise typically has:
        // 1. High standard deviation relative to mean (high coefficient of variation)
        // 2. High zero-crossing rate
        const cv = Math.abs(stdDev / (mean + 1e-10));

        // Thresholds for noise detection
        return cv > 5.0 && zeroCrossingRate > 0.4;
    }
}

export default HybridPitchDetector;