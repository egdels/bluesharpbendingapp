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
/**
 * Utility class for pitch detection and audio analysis.
 */
class PitchDetectionUtil {
    // Constants
    static NO_DETECTED_PITCH = -1; // Indicates no pitch detected
    static YIN_MINIMUM_THRESHOLD = 0.4; // YIN’s minimum threshold for periodicity
    static RMS_SCALING_FACTOR = 0.3; // Scaling factor for RMS calculation

    /**
     * Detects the pitch of an audio signal using the YIN algorithm.
     *
     * The YIN algorithm is used to analyze audio signals and identify the fundamental frequency (pitch).
     *
     * @param {Array<number>} audioData - The array of sample amplitudes of the audio signal.
     * @param {number} sampleRate - The sample rate of the audio signal (Hz).
     * @returns {{pitch: number, confidence: number}} - An object containing the detected pitch in Hz and confidence (0–1).
     */
    static detectPitchWithYIN(audioData, sampleRate) {
        const yinLength = Math.floor(audioData.length / 2);
        let pitch = this.NO_DETECTED_PITCH;

        // Step 1: Difference function
        const yinDiff = this.computeDifferenceFunction(audioData, yinLength);


        // Step 2: Cumulative mean normalized difference function (CMNDF)
        const cmndf = this.computeCMNDF(yinDiff, yinLength);

        const rms = this.calculateRMS(audioData);
        const dynamicThreshold = this.YIN_MINIMUM_THRESHOLD * (1+ this.RMS_SCALING_FACTOR*(1-rms));

        // Step 3: Find the first minimum below the defined threshold
        let tau = this.findFirstMinimum(cmndf, dynamicThreshold);

        let confidence = 0;

        if (tau !== -1) {
            confidence = Math.max(0, 1 - (cmndf[tau] / dynamicThreshold));
            const refinedTau = this.parabolicInterpolation(cmndf, tau);
            if (refinedTau > 0) {
                pitch = sampleRate / refinedTau;
            }
        }
        if (pitch === this.NO_DETECTED_PITCH) {
            return {pitch: this.NO_DETECTED_PITCH, confidence: 0};
        }
        return {pitch, confidence};
    }

    /**
     * Computes the Cumulative Mean Normalized Difference Function (CMNDF) from the given difference function array.
     *
     * @param {number[]} yinDiff - The array containing the difference function values.
     * @param {number} yinLength - The length of the `yinDiff` array.
     * @return {number[]} An array containing the computed CMNDF values.
     */
    static computeCMNDF(yinDiff, yinLength) {
        const cmndf = new Array(yinLength).fill(0);
        cmndf[0] = 1.0;
        let runningSum = 0;
        for (let tau = 1; tau < yinLength; tau++) {
            runningSum += yinDiff[tau];
            cmndf[tau] = yinDiff[tau] / (runningSum / tau); // Normalized difference
        }
        return cmndf;
    }


    /**
     * Computes the difference function for the given audio data.
     *
     * @param {Array<number>} audioData - The array of audio data samples.
     * @param {number} yinLength - The length of the YIN algorithm processing window.
     * @return {Array<number>} An array containing the computed difference function results.
     */
    static computeDifferenceFunction(audioData, yinLength) {
        const yinDiff = new Array(yinLength).fill(0);
        for (let tau = 0; tau < yinLength; tau++) {
            for (let j = 0; j < yinLength; j++) {
                const delta = audioData[j] - audioData[j + tau];
                yinDiff[tau] += delta * delta;
            }
        }
        return yinDiff;
    }


    /**
     * Finds the first index in the given array where the value is below a specified threshold
     * and is a local minimum.
     *
     * @param {number[]} cmndf - The array to search through.
     * @param {number} threshold - The value below which the function will look for a local minimum.
     * @return {number} The index of the first local minimum below the threshold, or -1 if not found.
     */
    static findFirstMinimum(cmndf, threshold) {
        for (let tau = 2; tau < cmndf.length - 1; tau++) {
            if (cmndf[tau] < threshold && this.isLocalMinimum(cmndf, tau)) {
                return tau;
            }
        }
        return -1;
    }


    /**
     * Refines the estimate of the lag value `tau` using parabolic interpolation to improve accuracy.
     *
     * @param {Array<number>} cmndf - The CMNDF array.
     * @param {number} tau - The initial lag value.
     * @returns {number} - The refined lag value.
     */
    static parabolicInterpolation(cmndf, tau) {
        if (tau < 1 || tau >= cmndf.length - 1) {
            return tau;
        }
        const y1 = cmndf[tau - 1];
        const y2 = cmndf[tau];
        const y3 = cmndf[tau + 1];

        // Parabolic interpolation formula
        return tau + (y3 - y1) / (2 * (2 * y2 - y1 - y3));
    }

    /**
     * Determines whether a specific element in an array is a local minimum.
     *
     * A local minimum is defined as an element that is smaller than its immediate neighbors.
     *
     * @param {Array<number>} array - The array to evaluate.
     * @param {number} index - The index of the element to check.
     * @returns {boolean} - True if the element is a local minimum, otherwise false.
     */
    static isLocalMinimum(array, index) {
        if (index <= 0 || index >= array.length - 1) {
            return false; // Boundary elements cannot be local minima
        }
        return array[index] < array[index - 1] && array[index] < array[index + 1];
    }

    /**
     * Detects the pitch of an audio signal using the McLeod Pitch Method (MPM).
     * This method calculates the fundamental frequency of the audio data by
     * analyzing the normalized square difference function (NSDF).
     *
     * @param {Float32Array} audioData - An array of audio signal data.
     * @param {number} sampleRate - The sample rate of the audio signal in Hz.
     * @returns {Object} An object containing the detected pitch in Hz and confidence value (0 to 1).
     */
    static detectPitchWithMPM(audioData, sampleRate) {
        const n = audioData.length;
        const nsdf = new Array(n).fill(0);
        const maxLag = Math.floor(n / 2);
        let peakIndex = -1;
        let confidence = 0;

        // Step 1: Calculate the NSDF (Normalized Square Difference Function)
        for (let lag = 0; lag < maxLag; lag++) {
            let numerator = 0;
            let denominator = 0;
            for (let i = 0; i < n - lag; i++) {
                numerator += audioData[i] * audioData[i + lag];
                denominator += audioData[i] * audioData[i] + audioData[i + lag] * audioData[i + lag];
            }
            if (denominator !== 0) {
                nsdf[lag] = (2 * numerator) / denominator; // Normalized Autocorrelation
            } else {
                nsdf[lag] = 0;
            }
        }

        // Step 2: Find peaks in the NSDF
        const candidatePeaks = [];
        for (let lag = 1; lag < maxLag - 1; lag++) {
            if (
                nsdf[lag] > nsdf[lag - 1] &&
                nsdf[lag] > nsdf[lag + 1] &&
                nsdf[lag] > 0.7 // Threshold for confidence
            ) {
                candidatePeaks.push(lag);
            }
        }

        // Step 3: Filter peaks for harmonics
        if (candidatePeaks.length > 0) {
            for (let i = 1; i < candidatePeaks.length; i++) {
                if (candidatePeaks[i] / candidatePeaks[0] >= 2.0) {
                    peakIndex = candidatePeaks[0]; // Fundamental frequency
                    break;
                }
            }
        }

        // Step 4: Parabolic interpolation for more accurate peak detection
        if (peakIndex > 0 && peakIndex < maxLag - 1) {
            const x0 = nsdf[peakIndex - 1];
            const x1 = nsdf[peakIndex];
            const x2 = nsdf[peakIndex + 1];

            confidence = x1; // Confidence as the highest NSDF value at peakIndex
            peakIndex = Math.round(
                peakIndex + 0.5 * (x0 - x2) / (x0 - 2 * x1 + x2)
            );
        }

        // Step 5: Convert lag to frequency
        if (peakIndex > 0) {
            const pitch = sampleRate / peakIndex;
            return { pitch, confidence };
        }

        return { pitch: this.NO_DETECTED_PITCH, confidence: 0.0 }; // No pitch detected
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
    static calculateRMS(audioData) {
        if (!Array.isArray(audioData) || audioData.length === 0) {
            return 0;
        }

        const sumOfSquares = audioData.reduce((sum, sample) => sum + sample ** 2, 0);
        return Math.sqrt(sumOfSquares / audioData.length);
    }

}

export default PitchDetectionUtil;