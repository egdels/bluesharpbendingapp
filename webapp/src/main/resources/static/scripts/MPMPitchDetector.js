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

/**
 * The MPMPitchDetector class is a specialized pitch detection implementation
 * using the McLeod Pitch Method (MPM). It analyzes audio signals to detect
 * the fundamental frequency (pitch) and calculate its confidence.
 *
 * This implementation leverages the Normalized Square Difference Function (NSDF)
 * for determining pitch-related peaks and uses techniques such as parabolic
 * interpolation for refined peak accuracy.
 *
 * Features:
 * - Configurable frequency detection range (minimum and maximum frequencies).
 * - Peaks are detected with a threshold to exclude insignificant candidates.
 * - Refined pitch estimation using parabolic interpolation.
 */
class MPMPitchDetector {
    // Constants
    static NO_DETECTED_PITCH = -1; // Indicates no pitch detected
    static DEFAULT_MIN_FREQUENCY = 80.0; // Default minimum frequency in Hz
    static DEFAULT_MAX_FREQUENCY = 4835.0; // Default maximum frequency in Hz
    static PEAK_THRESHOLD = 0.5; // Threshold for peak detection in the NSDF

    // Configurable properties
    static minFrequency = MPMPitchDetector.DEFAULT_MIN_FREQUENCY;
    static maxFrequency = MPMPitchDetector.DEFAULT_MAX_FREQUENCY;

    /**
     * Sets the minimum frequency that can be detected (in Hz).
     * @param {number} frequency - The minimum frequency in Hz
     */
    static setMinFrequency(frequency) {
        MPMPitchDetector.minFrequency = frequency;
    }

    /**
     * Gets the minimum frequency that can be detected (in Hz).
     * @returns {number} The minimum frequency in Hz
     */
    static getMinFrequency() {
        return MPMPitchDetector.minFrequency;
    }

    /**
     * Sets the maximum frequency that can be detected (in Hz).
     * @param {number} frequency - The maximum frequency in Hz
     */
    static setMaxFrequency(frequency) {
        MPMPitchDetector.maxFrequency = frequency;
    }

    /**
     * Gets the maximum frequency that can be detected (in Hz).
     * @returns {number} The maximum frequency in Hz
     */
    static getMaxFrequency() {
        return MPMPitchDetector.maxFrequency;
    }

    /**
     * Detects the pitch of an audio signal using the McLeod Pitch Method (MPM).
     * This method calculates the fundamental frequency of the audio data by
     * analyzing the normalized square difference function (NSDF).
     *
     * @param {Array<number>} audioData - An array of audio signal data.
     * @param {number} sampleRate - The sample rate of the audio signal in Hz.
     * @returns {Object} An object containing the detected pitch in Hz and confidence value (0 to 1).
     */
    static detectPitch(audioData, sampleRate) {
        const n = audioData.length;

        // Calculate lag limits based on the frequency range
        // We extend the range by 10% on both ends to ensure we can detect frequencies at the edges
        const minLag = Math.max(1, Math.floor(sampleRate / (MPMPitchDetector.maxFrequency * 1.1))); // Extend by 10% higher
        const maxLag = Math.min(Math.floor(n / 2), Math.floor(sampleRate / (MPMPitchDetector.minFrequency * 0.9))); // Extend by 10% lower

        // Calculate the NSDF
        const nsdf = MPMPitchDetector.calculateNSDF(audioData, n, minLag, maxLag);

        // Find peaks in the NSDF
        const candidatePeaks = MPMPitchDetector.findPeaks(nsdf, minLag, maxLag);

        // Select the most significant peak
        const peakIndex = MPMPitchDetector.selectPeak(candidatePeaks);

        // If no peak is found, return no pitch detected
        if (peakIndex <= 0) {
            return { pitch: MPMPitchDetector.NO_DETECTED_PITCH, confidence: 0.0 };
        }

        // Calculate confidence based on the NSDF value at the peak
        const confidence = nsdf[peakIndex - minLag];

        // Apply parabolic interpolation to refine the peak index
        const refinedPeakIndex = MPMPitchDetector.applyParabolicInterpolation(nsdf, peakIndex - minLag, minLag);

        // Calculate the pitch from the refined peak index
        const pitch = sampleRate / refinedPeakIndex;

        return { pitch, confidence };
    }

    /**
     * Calculates the Normalized Square Difference Function (NSDF) for a given audio signal
     * within a specific lag range defined by minLag and maxLag.
     * This allows focusing the calculation on a specific frequency range.
     *
     * @param {Array<number>} audioData - An array of values representing the audio signal to be analyzed
     * @param {number} n - The number of samples from the audio signal to process
     * @param {number} minLag - The minimum lag value to consider (corresponding to the maximum frequency)
     * @param {number} maxLag - The maximum lag value to consider (corresponding to the minimum frequency)
     * @returns {Array<number>} An array of values containing the computed NSDF values
     */
    static calculateNSDF(audioData, n, minLag, maxLag) {
        const maxLagForCalculation = Math.min(Math.floor(n / 2), maxLag);
        const nsdf = new Array(maxLagForCalculation - minLag).fill(0);

        // Calculate NSDF only for lags between minLag and maxLagForCalculation
        for (let lag = minLag; lag < maxLagForCalculation; lag++) {
            let numerator = 0;
            let denominator = 0;
            for (let i = 0; i < n - lag; i++) {
                numerator += audioData[i] * audioData[i + lag];
                denominator += audioData[i] * audioData[i] + audioData[i + lag] * audioData[i + lag];
            }

            if (denominator === 0) {
                nsdf[lag - minLag] = 0;
            } else {
                nsdf[lag - minLag] = 2 * numerator / denominator;
            }
        }

        return nsdf;
    }

    /**
     * Identifies the local peaks in the given Normalized Square Difference Function (NSDF) array
     * within a specific lag range defined by minLag and maxLag.
     * This allows focusing the peak detection on a specific frequency range.
     *
     * @param {Array<number>} nsdf - An array of values representing the Normalized Square Difference Function (NSDF)
     * @param {number} minLag - The minimum lag value to consider (corresponding to the maximum frequency)
     * @param {number} maxLag - The maximum lag value to consider (corresponding to the minimum frequency)
     * @returns {Array<number>} An array where each value represents the index of a detected peak
     */
    static findPeaks(nsdf, minLag, maxLag) {
        const candidatePeaks = [];

        // Ensure we don't go out of bounds
        if (nsdf.length < 2) {
            return candidatePeaks;
        }

        // Find all peaks in the NSDF that exceed the threshold
        for (let i = 1; i < nsdf.length - 1; i++) {
            if (nsdf[i] > nsdf[i - 1] && nsdf[i] > nsdf[i + 1] && nsdf[i] > MPMPitchDetector.PEAK_THRESHOLD) {
                // Add the actual lag value (not the array index)
                candidatePeaks.push(i + minLag);
            }
        }

        return candidatePeaks;
    }

    /**
     * Selects the most relevant peak index from a list of candidate peak indices.
     * If the list of candidate peaks is empty, the method returns -1.
     *
     * @param {Array<number>} candidatePeaks - An array of values representing the indices of candidate peaks
     * @returns {number} The index of the selected peak from the candidate peaks, or -1 if the array is empty
     */
    static selectPeak(candidatePeaks) {
        if (candidatePeaks.length === 0) {
            return -1;
        }
        return candidatePeaks[0];
    }

    /**
     * Refines the peak index using parabolic interpolation to improve accuracy in
     * analyzing peaks in the Normalized Square Difference Function (NSDF).
     *
     * @param {Array<number>} nsdf - An array of values representing the Normalized Square Difference Function (NSDF).
     * @param {number} peakIndex - The index of the detected peak in the NSDF array.
     * @param {number} minLag - The minimum lag value used in the NSDF calculation.
     * @returns {number} The refined peak index as a number, adjusted using parabolic interpolation for enhanced accuracy.
     */
    static applyParabolicInterpolation(nsdf, peakIndex, minLag) {
        if (peakIndex <= 0 || peakIndex >= nsdf.length - 1) {
            return peakIndex + minLag;
        }

        const x0 = nsdf[peakIndex - 1];
        const x1 = nsdf[peakIndex];
        const x2 = nsdf[peakIndex + 1];

        // Calculate the adjustment using parabolic interpolation
        const denominator = x0 - 2 * x1 + x2;

        // Avoid division by zero or very small values
        if (Math.abs(denominator) < 1e-10) {
            return peakIndex + minLag;
        }

        let adjustment = 0.5 * (x0 - x2) / denominator;

        // Limit the adjustment to a reasonable range to avoid extreme values
        if (Math.abs(adjustment) > 1) {
            adjustment = 0;
        }

        return (peakIndex + adjustment) + minLag;
    }
}

export default MPMPitchDetector;
