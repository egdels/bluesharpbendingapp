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

import ChordDetectionResult from './ChordDetectionResult.js';

/**
 * Implementation of a spectral-based algorithm for chord detection.
 *
 * This class provides a modern approach to detect multiple pitches (chords)
 * in an audio signal using spectral analysis.
 *
 * The algorithm works by:
 * 1. Applying a window function to the audio data
 * 2. Computing the FFT to get the frequency spectrum
 * 3. Finding peaks in the spectrum that correspond to pitches
 * 4. Filtering and refining the detected pitches
 */
class ChordDetector {
    // Constants
    static NO_DETECTED_PITCH = -1; // Indicates no pitch detected
    static DEFAULT_MIN_FREQUENCY = 80.0; // Default minimum frequency in Hz
    static DEFAULT_MAX_FREQUENCY = 4835.0; // Default maximum frequency in Hz
    
    /**
     * The minimum amplitude threshold for peak detection in the spectrum.
     * Peaks with amplitudes below this threshold will not be considered.
     */
    static PEAK_THRESHOLD = 0.05;

    /**
     * The minimum distance between peaks in Hz.
     * Peaks that are closer than this distance will be merged.
     */
    static MIN_PEAK_DISTANCE_HZ = 25.0;

    /**
     * The maximum number of pitches to detect.
     */
    static MAX_PITCHES = 4;

    /**
     * The threshold for spectral flatness to distinguish between tonal sounds and noise.
     * Values above this threshold indicate noise.
     */
    static SPECTRAL_FLATNESS_THRESHOLD = 0.4;

    /**
     * Represents the tolerance level used to identify and filter out harmonic frequencies
     * in the pitch detection process within audio signals.
     *
     * This constant defines the threshold ratio within which frequencies are considered
     * close enough to be treated as harmonics of a fundamental frequency.
     */
    static HARMONIC_TOLERANCE = 0.05;

    // Configurable properties
    static minFrequency = ChordDetector.DEFAULT_MIN_FREQUENCY;
    static maxFrequency = ChordDetector.DEFAULT_MAX_FREQUENCY;

    /**
     * Sets the minimum frequency that can be detected (in Hz).
     * @param {number} frequency - The minimum frequency in Hz
     */
    static setMinFrequency(frequency) {
        ChordDetector.minFrequency = frequency;
    }

    /**
     * Gets the minimum frequency that can be detected (in Hz).
     * @returns {number} The minimum frequency in Hz
     */
    static getMinFrequency() {
        return ChordDetector.minFrequency;
    }

    /**
     * Sets the maximum frequency that can be detected (in Hz).
     * @param {number} frequency - The maximum frequency in Hz
     */
    static setMaxFrequency(frequency) {
        ChordDetector.maxFrequency = frequency;
    }

    /**
     * Gets the maximum frequency that can be detected (in Hz).
     * @returns {number} The maximum frequency in Hz
     */
    static getMaxFrequency() {
        return ChordDetector.maxFrequency;
    }

    /**
     * Detects multiple pitches (chord) in an audio signal.
     *
     * @param {Array<number>} audioData - An array of audio signal data.
     * @param {number} sampleRate - The sample rate of the audio signal in Hz.
     * @returns {Object} An object containing the detected pitches and confidence value.
     */
    static detectChord(audioData, sampleRate) {
        // Prepare for FFT (needs power of 2 size)
        const fftSize = Math.max(1024, ChordDetector.nextPowerOfTwo(audioData.length));
        const fftInput = new Array(fftSize * 2).fill(0); // Complex numbers (real, imag)

        // Apply window function and prepare FFT input
        for (let i = 0; i < audioData.length; i++) {
            fftInput[i * 2] = audioData[i] * ChordDetector.hannWindow(i, audioData.length);
            fftInput[i * 2 + 1] = 0; // Imaginary part is zero
        }

        // Perform FFT
        ChordDetector.fft(fftInput, fftSize);

        // Calculate magnitude spectrum
        const magnitudeSpectrum = new Array(Math.floor(fftSize / 2));
        for (let i = 0; i < Math.floor(fftSize / 2); i++) {
            const real = fftInput[i * 2];
            const imag = fftInput[i * 2 + 1];
            magnitudeSpectrum[i] = Math.sqrt(real * real + imag * imag);
        }

        // Calculate spectral flatness to distinguish between tonal sounds and noise
        const spectralFlatness = ChordDetector.calculateSpectralFlatness(magnitudeSpectrum, sampleRate);

        // If the spectral flatness is high, it's likely noise
        if (spectralFlatness > ChordDetector.SPECTRAL_FLATNESS_THRESHOLD) {
            return new ChordDetectionResult([], 0.0);
        }

        // Normalize the spectrum
        const maxMagnitude = Math.max(...magnitudeSpectrum);
        for (let i = 0; i < magnitudeSpectrum.length; i++) {
            magnitudeSpectrum[i] /= maxMagnitude;
        }

        // Find peaks in the spectrum
        let peaks = ChordDetector.findPeaks(magnitudeSpectrum, sampleRate, fftSize);

        // Filter peaks based on frequency range and threshold
        peaks = ChordDetector.filterPeaks(peaks);

        // Filter harmonics to avoid overtones and prioritize fundamental frequencies
        peaks = ChordDetector.filterHarmonics(peaks);

        // Prioritize lower frequencies over higher harmonics
        peaks = ChordDetector.prioritizeLowerFrequencies(peaks);

        // Merge peaks that are too close
        peaks = ChordDetector.mergePeaks(peaks);

        // Limit the number of peaks
        if (peaks.length > ChordDetector.MAX_PITCHES) {
            peaks = peaks.slice(0, ChordDetector.MAX_PITCHES);
        }

        // Extract pitches from peaks
        const pitches = peaks.map(peak => peak.frequency);

        // Calculate confidence based on the strength of the peaks
        const confidence = peaks.length === 0 ? 0.0 :
            peaks.reduce((sum, peak) => sum + peak.magnitude, 0) / peaks.length;

        return new ChordDetectionResult(pitches, confidence);
    }

    /**
     * Finds peaks in the magnitude spectrum that correspond to pitches.
     *
     * @param {Array<number>} magnitudeSpectrum - The magnitude spectrum from FFT
     * @param {number} sampleRate - The sample rate of the audio signal in Hz
     * @param {number} fftSize - The size of the FFT
     * @returns {Array<Object>} An array of peaks found in the spectrum
     */
    static findPeaks(magnitudeSpectrum, sampleRate, fftSize) {
        const peaks = [];

        // Skip the first few bins (DC and very low frequencies)
        const startBin = Math.max(1, Math.floor(ChordDetector.minFrequency * fftSize / sampleRate));
        const endBin = Math.min(magnitudeSpectrum.length - 1, Math.floor(ChordDetector.maxFrequency * fftSize / sampleRate));

        for (let i = startBin + 1; i < endBin - 1; i++) {
            // Check if this is a local maximum
            if (magnitudeSpectrum[i] > magnitudeSpectrum[i - 1] &&
                magnitudeSpectrum[i] > magnitudeSpectrum[i + 1] &&
                magnitudeSpectrum[i] > ChordDetector.PEAK_THRESHOLD) {

                // Refine the peak position using parabolic interpolation
                const refinedBin = ChordDetector.parabolicInterpolation(magnitudeSpectrum, i);
                const frequency = refinedBin * sampleRate / fftSize;

                // Add the peak to the list
                peaks.push({ frequency, magnitude: magnitudeSpectrum[i] });
            }
        }

        // Sort peaks by magnitude (descending)
        peaks.sort((p1, p2) => p2.magnitude - p1.magnitude);

        return peaks;
    }

    /**
     * Filters peaks based on frequency range and threshold.
     *
     * @param {Array<Object>} peaks - The list of peaks to filter
     * @returns {Array<Object>} The filtered list of peaks
     */
    static filterPeaks(peaks) {
        return peaks.filter(peak => 
            peak.frequency >= ChordDetector.minFrequency && 
            peak.frequency <= ChordDetector.maxFrequency
        );
    }

    /**
     * Merges peaks that are too close to each other.
     *
     * @param {Array<Object>} peaks - The list of peaks to merge
     * @returns {Array<Object>} The merged list of peaks
     */
    static mergePeaks(peaks) {
        if (peaks.length === 0) {
            return peaks;
        }

        const mergedPeaks = [];
        let currentPeak = peaks[0];

        for (let i = 1; i < peaks.length; i++) {
            const nextPeak = peaks[i];

            // Check if the peaks are too close
            if (Math.abs(nextPeak.frequency - currentPeak.frequency) < ChordDetector.MIN_PEAK_DISTANCE_HZ) {
                // Merge the peaks (weighted average based on magnitude)
                const totalMagnitude = currentPeak.magnitude + nextPeak.magnitude;
                const mergedFrequency = (currentPeak.frequency * currentPeak.magnitude +
                                        nextPeak.frequency * nextPeak.magnitude) / totalMagnitude;
                currentPeak = { frequency: mergedFrequency, magnitude: totalMagnitude };
            } else {
                // Add the current peak and move to the next one
                mergedPeaks.push(currentPeak);
                currentPeak = nextPeak;
            }
        }

        // Add the last peak
        mergedPeaks.push(currentPeak);

        return mergedPeaks;
    }

    /**
     * Finds the next power of two greater than or equal to the given number.
     *
     * @param {number} n - The number to find the next power of two for
     * @returns {number} The next power of two
     */
    static nextPowerOfTwo(n) {
        let power = 1;
        while (power < n) {
            power *= 2;
        }
        return power;
    }

    /**
     * Calculates the spectral flatness of a magnitude spectrum.
     * Spectral flatness is the ratio of the geometric mean to the arithmetic mean
     * of the spectrum, and is a good measure of how "noisy" a sound is.
     * Values close to 0 indicate tonal sounds, while values close to 1 indicate noise.
     *
     * @param {Array<number>} magnitudeSpectrum - The magnitude spectrum to calculate flatness for
     * @param {number} sampleRate - The sample rate of the audio signal in Hz
     * @returns {number} The spectral flatness value between 0 and 1
     */
    static calculateSpectralFlatness(magnitudeSpectrum, sampleRate) {
        // Skip the first few bins (DC and very low frequencies)
        const startBin = Math.max(1, Math.floor(ChordDetector.minFrequency * magnitudeSpectrum.length / (sampleRate / 2)));
        const endBin = Math.min(magnitudeSpectrum.length - 1, Math.floor(ChordDetector.maxFrequency * magnitudeSpectrum.length / (sampleRate / 2)));

        let sum = 0.0;
        let logSum = 0.0;
        let count = 0;

        // Calculate arithmetic mean and geometric mean
        for (let i = startBin; i <= endBin; i++) {
            // Add a small value to avoid log(0)
            const value = magnitudeSpectrum[i] + 1e-10;
            sum += value;
            logSum += Math.log(value);
            count++;
        }

        if (count === 0 || sum === 0) {
            return 1.0; // Maximum flatness (noise)
        }

        const arithmeticMean = sum / count;
        const geometricMean = Math.exp(logSum / count);

        // Calculate spectral flatness
        return geometricMean / arithmeticMean;
    }

    /**
     * Filters out harmonic frequencies by checking each peak against deeper fundamental frequencies.
     * Harmonics are identified by their frequency ratios (integer multiples).
     *
     * @param {Array<Object>} peaks - The list of detected peaks to filter
     * @returns {Array<Object>} The filtered list of peaks (without harmonics)
     */
    static filterHarmonics(peaks) {
        const filteredPeaks = [];

        for (let i = 0; i < peaks.length; i++) {
            let isHarmonic = false;

            for (let j = 0; j < i; j++) {
                const ratio = peaks[i].frequency / peaks[j].frequency;

                // If the peak frequency is within a harmonic series, filter it out
                // Special case for octaves (ratio ~2.0): don't filter them out
                if (Math.abs(ratio - 2.0) < 0.1) {
                    // Allow octaves to pass through
                    continue;
                }
                if (Math.abs(ratio - Math.round(ratio)) < ChordDetector.HARMONIC_TOLERANCE) {
                    if (ratio > 5.0) { // Unrealistic harmonic
                        continue; // Accept it as a separate tone
                    }
                    if (peaks[i].magnitude < peaks[j].magnitude * 0.3) { // Adjusted amplitude ratio to suppress harmonics
                        isHarmonic = true;
                        break;
                    }
                }
            }

            if (!isHarmonic) {
                filteredPeaks.push(peaks[i]);
            }
        }

        return filteredPeaks;
    }

    /**
     * Prioritizes lower frequencies by comparing relative amplitudes of peaks.
     * This ensures that higher harmonics are deprioritized if similar strength exists at lower frequencies.
     *
     * @param {Array<Object>} peaks - The list of detected peaks to prioritize
     * @returns {Array<Object>} The ordered and prioritized list of peaks
     */
    static prioritizeLowerFrequencies(peaks) {
        // Sort by frequency (ascending)
        peaks.sort((p1, p2) => p1.frequency - p2.frequency);

        const prioritizedPeaks = [];
        for (const peak of peaks) {
            let isOverridden = false;

            for (const lowerPeak of prioritizedPeaks) {
                if (peak.frequency > lowerPeak.frequency &&
                    peak.magnitude < lowerPeak.magnitude * 0.6) { // Adjusted amplitude ratio (factor 0.6)
                    isOverridden = true;
                    break;
                }
            }

            if (!isOverridden) {
                prioritizedPeaks.push(peak);
            }
        }

        return prioritizedPeaks;
    }

    /**
     * Applies parabolic interpolation to refine a peak index.
     * This method is used to improve the accuracy of peak detection.
     *
     * @param {Array<number>} values - An array of values representing the function to interpolate
     * @param {number} peakIndex - The index of the detected peak in the values array
     * @returns {number} The refined peak index as a number, adjusted using parabolic interpolation
     */
    static parabolicInterpolation(values, peakIndex) {
        if (peakIndex <= 0 || peakIndex >= values.length - 1) {
            return peakIndex;
        }

        const x0 = values[peakIndex - 1];
        const x1 = values[peakIndex];
        const x2 = values[peakIndex + 1];

        // Calculate the adjustment using parabolic interpolation
        const denominator = x0 - 2 * x1 + x2;

        // Avoid division by zero or very small values
        if (Math.abs(denominator) < 1e-10) {
            return peakIndex;
        }

        let adjustment = 0.5 * (x0 - x2) / denominator;

        // Limit the adjustment to a reasonable range to avoid extreme values
        if (Math.abs(adjustment) > 1) {
            adjustment = 0;
        }

        return peakIndex + adjustment;
    }

    /**
     * Applies a Hann window function to the sample at the given index.
     *
     * @param {number} index - The index of the sample
     * @param {number} size - The total number of samples
     * @returns {number} The window coefficient
     */
    static hannWindow(index, size) {
        return 0.5 * (1 - Math.cos(2 * Math.PI * index / (size - 1)));
    }

    /**
     * Performs an in-place Fast Fourier Transform (FFT) on the input data.
     * This is a radix-2 decimation-in-time FFT algorithm.
     *
     * @param {Array<number>} data - The input/output data array (complex numbers as pairs of real, imaginary)
     * @param {number} n - The size of the FFT (number of complex numbers)
     */
    static fft(data, n) {
        // Bit-reversal permutation
        let shift = 1;
        while (shift < n) {
            shift <<= 1;
        }
        shift >>= 1;

        // Bit reversal
        for (let i = 0; i < n; i++) {
            const j = ChordDetector.bitReverse(i, shift);
            if (j > i) {
                // Swap real parts
                let temp = data[i * 2];
                data[i * 2] = data[j * 2];
                data[j * 2] = temp;

                // Swap imaginary parts
                temp = data[i * 2 + 1];
                data[i * 2 + 1] = data[j * 2 + 1];
                data[j * 2 + 1] = temp;
            }
        }

        // Cooley-Tukey FFT
        for (let len = 2; len <= n; len <<= 1) {
            const angle = -2 * Math.PI / len;
            const wReal = Math.cos(angle);
            const wImag = Math.sin(angle);

            for (let i = 0; i < n; i += len) {
                let uReal = 1.0;
                let uImag = 0.0;

                for (let j = 0; j < len / 2; j++) {
                    const p = i + j;
                    const q = i + j + len / 2;

                    const pReal = data[p * 2];
                    const pImag = data[p * 2 + 1];
                    const qReal = data[q * 2];
                    const qImag = data[q * 2 + 1];

                    // Temporary values for the multiplication
                    const tempReal = uReal * qReal - uImag * qImag;
                    const tempImag = uReal * qImag + uImag * qReal;

                    // Update data
                    data[q * 2] = pReal - tempReal;
                    data[q * 2 + 1] = pImag - tempImag;
                    data[p * 2] = pReal + tempReal;
                    data[p * 2 + 1] = pImag + tempImag;

                    // Update u
                    const nextUReal = uReal * wReal - uImag * wImag;
                    const nextUImag = uReal * wImag + uImag * wReal;
                    uReal = nextUReal;
                    uImag = nextUImag;
                }
            }
        }
    }

    /**
     * Reverses the bits of an integer value up to the given shift.
     *
     * @param {number} value - The value to reverse
     * @param {number} shift - The bit position to reverse up to
     * @returns {number} The bit-reversed value
     */
    static bitReverse(value, shift) {
        let result = 0;
        while (shift > 0) {
            result = (result << 1) | (value & 1);
            value >>= 1;
            shift >>= 1;
        }
        return result;
    }
}

export default ChordDetector;