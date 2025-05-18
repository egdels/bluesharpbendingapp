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

import NoteUtils from './NoteUtils.js';

/**
 * Implementation of the YIN algorithm for pitch detection.
 * 
 * This class provides a clean, efficient implementation of the YIN algorithm
 * for detecting the fundamental frequency (pitch) of an audio signal.
 * The implementation allows configuring the frequency range and provides
 * both the detected pitch and a confidence value.
 * 
 * The YIN algorithm works by:
 * 1. Computing the difference function to measure signal similarity at various lags
 * 2. Normalizing the difference function using the CMNDF algorithm
 * 3. Finding the first minimum in the CMNDF below a threshold
 * 4. Refining the estimate using parabolic interpolation
 * 5. Calculating the pitch and confidence from the refined estimate
 */
class YINPitchDetector {
    // Constants
    static NO_DETECTED_PITCH = -1; // Indicates no pitch detected
    static DEFAULT_MIN_FREQUENCY = 80.0; // Default minimum frequency in Hz
    static DEFAULT_MAX_FREQUENCY = 4835.0; // Default maximum frequency in Hz
    static YIN_MINIMUM_THRESHOLD = 0.4; // YIN's minimum threshold for periodicity
    static RMS_SCALING_FACTOR = 0.3; // Scaling factor for RMS calculation

    // Configurable properties
    static minFrequency = YINPitchDetector.DEFAULT_MIN_FREQUENCY;
    static maxFrequency = YINPitchDetector.DEFAULT_MAX_FREQUENCY;

    /**
     * Sets the minimum frequency that can be detected (in Hz).
     * @param {number} frequency - The minimum frequency in Hz
     */
    static setMinFrequency(frequency) {
        YINPitchDetector.minFrequency = frequency;
    }

    /**
     * Gets the minimum frequency that can be detected (in Hz).
     * @returns {number} The minimum frequency in Hz
     */
    static getMinFrequency() {
        return YINPitchDetector.minFrequency;
    }

    /**
     * Sets the maximum frequency that can be detected (in Hz).
     * @param {number} frequency - The maximum frequency in Hz
     */
    static setMaxFrequency(frequency) {
        YINPitchDetector.maxFrequency = frequency;
    }

    /**
     * Gets the maximum frequency that can be detected (in Hz).
     * @returns {number} The maximum frequency in Hz
     */
    static getMaxFrequency() {
        return YINPitchDetector.maxFrequency;
    }

    /**
     * Detects the pitch of an audio signal using the YIN algorithm.
     * 
     * This implementation calculates the pitch (fundamental frequency) of an audio signal
     * by analyzing periodic patterns in the signal's waveform. The algorithm evaluates the
     * signal's periodicity with the help of the cumulative mean normalized difference function (CMNDF),
     * and dynamically determines thresholds for improved accuracy.
     * If a pitch is successfully detected, the method refines the results and provides a
     * confidence value to indicate the reliability of the estimate.
     *
     * @param {Array<number>} audioData - An array of values representing the audio signal to analyze.
     * @param {number} sampleRate - The sample rate of the audio signal in Hz.
     * @returns {Object} An object containing:
     *   - pitch (number): The detected pitch in Hz, or NO_DETECTED_PITCH if none detected.
     *   - confidence (number): A value between 0.0 and 1.0 indicating detection reliability.
     */
    static detectPitch(audioData, sampleRate) {
        // Determine the size of the input buffer (length of the audio sample data)
        const bufferSize = audioData.length;

        // Calculate tau limits based on the frequency range
        const maxTau = Math.floor(sampleRate / NoteUtils.addCentsToFrequency(-25, YINPitchDetector.minFrequency));
        const minTau = Math.floor(sampleRate / NoteUtils.addCentsToFrequency(25, YINPitchDetector.maxFrequency));

        // Step 1: Compute the difference function to measure signal similarity at various lags
        const difference = YINPitchDetector.computeDifferenceFunction(audioData, bufferSize);

        // Step 2: Normalize the difference function using the CMNDF algorithm
        const cmndf = YINPitchDetector.computeCMNDFInRange(difference, minTau, maxTau);

        // Step 3: Compute the Root Mean Square (RMS) to assess the signal's energy level
        const rms = YINPitchDetector.calcRMS(audioData);

        // Step 4: Adapt the YIN threshold based on RMS to improve pitch detection reliability
        const dynamicThreshold = Math.min(0.5, YINPitchDetector.YIN_MINIMUM_THRESHOLD * (1 + YINPitchDetector.RMS_SCALING_FACTOR / (rms + 0.01)));

        // Step 5: Find the first minimum in the CMNDF below the threshold; this corresponds to the lag (tau)
        const tauEstimate = YINPitchDetector.findFirstMinimum(cmndf, dynamicThreshold, minTau, maxTau);

        // Step 6: If a valid lag is found, refine it using parabolic interpolation for accuracy
        if (tauEstimate !== -1) {
            const refinedTau = YINPitchDetector.parabolicInterpolation(cmndf, tauEstimate);

            // Ensure the refined lag is valid before proceeding
            if (refinedTau > 0) {
                // Calculate the confidence by evaluating how close the CMNDF value is to the threshold
                const confidence = 1 - Math.pow((cmndf[tauEstimate] / dynamicThreshold), 2);

                // Derive the pitch (fundamental frequency) from the sample rate and tau
                const pitch = sampleRate / refinedTau;

                // Return the detected pitch and confidence in a result object
                return { pitch, confidence };
            }
        }

        // Step 7: If no pitch is detected, return no pitch with confidence set to 0.0
        return { pitch: YINPitchDetector.NO_DETECTED_PITCH, confidence: 0.0 };
    }

    /**
     * Refines the estimate of the lag value `tau` using parabolic interpolation
     * for improved accuracy in analyzing periodic signals.
     *
     * @param {Array<number>} cmndf - An array representing the cumulative mean normalized difference function (CMNDF)
     * @param {number} tau - An integer representing the initial lag value
     * @returns {number} The refined lag value as a double, obtained through parabolic interpolation
     */
    static parabolicInterpolation(cmndf, tau) {
        if (tau <= 0 || tau >= cmndf.length - 1) {
            return tau;
        }
        const x0 = cmndf[tau - 1];
        const x1 = cmndf[tau];
        const x2 = cmndf[tau + 1];
        return tau + (x0 - x2) / (2 * (x0 - 2 * x1 + x2)); // Parabolic refinement
    }

    /**
     * Determines whether a specific element in an array is a local minimum.
     * A local minimum is defined as an element that is smaller than its
     * immediate neighbors.
     *
     * @param {Array<number>} array - The array of values to evaluate
     * @param {number} index - The index of the element to check for being a local minimum
     * @returns {boolean} True if the element at the specified index is a local minimum;
     *                   false otherwise
     */
    static isLocalMinimum(array, index) {
        if (index <= 0 || index >= array.length - 1) {
            return false;
        }
        return array[index] < array[index - 1] && array[index] < array[index + 1];
    }

    /**
     * Finds the first index in the Cumulative Mean Normalized Difference Function (CMNDF)
     * array where the value is below a specified threshold and is a local minimum.
     *
     * @param {Array<number>} cmndf - An array representing the cumulative mean normalized
     *                  difference function (CMNDF). Each element corresponds to the
     *                  periodicity measure for a specific lag value.
     * @param {number} threshold - A value representing the threshold for identifying valid
     *                  CMNDF values. Only elements below this value will be considered.
     * @param {number} minTau - An integer specifying the minimum lag value to start the search from.
     * @param {number} maxTau - An integer specifying the maximum lag value up to which the search
     *                  should be conducted.
     * @returns {number} The index of the first local minimum that satisfies the threshold condition;
     *         returns -1 if no valid index is found within the given range.
     */
    static findFirstMinimum(cmndf, threshold, minTau, maxTau) {
        for (let tau = minTau; tau < maxTau; tau++) {
            if (cmndf[tau] < threshold && YINPitchDetector.isLocalMinimum(cmndf, tau)) {
                return tau;
            }
        }
        return -1;
    }

    /**
     * Computes the Cumulative Mean Normalized Difference Function (CMNDF) for the given range of τ values.
     * This method calculates a normalized measure within the relevant τ range, specified by minTau and maxTau,
     * while ignoring values outside that range.
     *
     * @param {Array<number>} difference - An array of difference values to compute the CMNDF from
     * @param {number} minTau - The minimum index in the τ range to be considered for calculation
     * @param {number} maxTau - The maximum index in the τ range to be considered for calculation
     * @returns {Array<number>} An array representing the CMNDF values, where values outside the specified range are set to 1
     */
    static computeCMNDFInRange(difference, minTau, maxTau) {
        const cmndf = new Array(difference.length).fill(0);
        cmndf[0] = 1;
        let cumulativeSum = 0;

        for (let tau = 1; tau < difference.length; tau++) {
            cumulativeSum += difference[tau];

            // Only calculate in the relevant range
            if (tau >= minTau && tau <= maxTau) {
                cmndf[tau] = difference[tau] / ((cumulativeSum / tau) + 1e-10);
            } else {
                cmndf[tau] = 1; // Ignore values outside the range
            }
        }

        return cmndf;
    }

    /**
     * Computes the difference function for an audio signal, used as an intermediate
     * step in signal processing algorithms like pitch detection. The difference function
     * evaluates the dissimilarity between overlapping segments of the audio data at
     * various time lags to assess periodicity.
     *
     * @param {Array<number>} audioData - An array of values representing the original audio signal
     *                   to be analyzed. Each value corresponds to the amplitude of the
     *                   signal at a specific point in time.
     * @param {number} bufferSize - The size of the buffer to process in the audio data. This determines
     *                   the range of time lags to evaluate in the difference function.
     * @returns {Array<number>} An array of values representing the computed difference function.
     * Each value corresponds to the dissimilarity measure for a specific time lag.
     */
    static computeDifferenceFunction(audioData, bufferSize) {
        const audioSquared = audioData.map(sample => sample * sample);
        const difference = new Array(Math.floor(bufferSize / 2)).fill(0);

        for (let tau = 0; tau < difference.length; tau++) {
            let sum = 0;
            for (let i = 0; i < Math.floor(bufferSize / 2); i++) {
                sum += audioSquared[i] + audioSquared[i + tau] - 2 * audioData[i] * audioData[i + tau];
            }
            difference[tau] = sum;
        }
        return difference;
    }

    /**
     * Calculates the Root Mean Square (RMS) of an audio signal.
     *
     * RMS is a measure of the signal's power and helps to determine
     * whether it's strong enough for pitch detection.
     *
     * @param {Array<number>} audioData - The array of sample amplitudes of the audio signal.
     * @returns {number} - The RMS value of the audio data.
     */
    static calcRMS(audioData) {
        if (!Array.isArray(audioData) || audioData.length === 0) {
            return 0;
        }

        const sumOfSquares = audioData.reduce((sum, sample) => sum + sample * sample, 0);
        return Math.sqrt(sumOfSquares / audioData.length);
    }
}

export default YINPitchDetector;
