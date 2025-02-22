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
        const yinDiff = [];
        const cmndf = [];
        const yinLength = Math.floor(audioData.length / 2);
        let pitch = this.NO_DETECTED_PITCH;

        // Step 1: Difference function
        for (let tau = 0; tau < yinLength; tau++) {
            yinDiff[tau] = 0;
            for (let j = 0; j < yinLength; j++) {
                const delta = audioData[j] - audioData[j + tau];
                yinDiff[tau] += delta * delta;
            }
        }

        // Step 2: Cumulative mean normalized difference function (CMNDF)
        cmndf[0] = 1.0;
        let runningSum = 0;
        for (let tau = 1; tau < yinLength; tau++) {
            runningSum += yinDiff[tau];
            cmndf[tau] = yinDiff[tau] / (runningSum / tau);
        }

        // Step 3: Find the first minimum below the defined threshold
        let tau = -1;
        for (let t = 2; t < yinLength; t++) {
            if (cmndf[t] < this.YIN_MINIMUM_THRESHOLD && this.isLocalMinimum(cmndf, t)) {
                tau = t;
                break;
            }
        }

        let confidence = 0;

        if (tau !== -1) {
            confidence = Math.max(0, 1 - (cmndf[tau] / this.YIN_MINIMUM_THRESHOLD));
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
}

export default PitchDetectionUtil;