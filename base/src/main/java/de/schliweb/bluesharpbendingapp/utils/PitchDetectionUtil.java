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

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for pitch detection and audio analysis.
 * Provides methods to detect pitch using different algorithms such as YIN and MPM
 * and to analyze key audio features like RMS (Root Mean Square).
 */
public class PitchDetectionUtil {

    @Getter
    private static final double defaultMinFrequency=80.0;

    @Getter
    private static final double defaultMaxFrequency=4835.0;

    @Setter
    private static double minFrequency=defaultMinFrequency;

    @Setter
    private static double maxFrequency=defaultMaxFrequency;


    /**
     * A constant representing the absence of a detected pitch in pitch detection algorithms.
     * When a pitch detection method fails to identify a fundamental frequency within
     * the audio signal, this value is returned to indicate no pitch was detected.
     */
    public static final double NO_DETECTED_PITCH = -1;
    /**
     * Represents the minimum threshold value used in the YIN pitch detection algorithm.
     * This threshold is applied to the cumulative mean normalized difference function (CMNDF)
     * to identify potential pitch candidates. Values in the CMNDF below this threshold
     * are considered for determining the fundamental frequency of an audio signal.
     * <p>
     * The threshold ensures that only significant periodicities are evaluated, helping
     * to discriminate noise or irrelevant fluctuations from valid pitch candidates.
     */
    private static final double YIN_MINIMUM_THRESHOLD = 0.4;

    /**
     * A scaling factor used in the Root Mean Square (RMS) calculation to normalize
     * and adjust the amplitude threshold in pitch detection processes.
     * <p>
     * This constant is utilized to fine-tune the sensitivity of audio signal analysis,
     * ensuring that the detected pitch is accurate and reliable, particularly under
     * varying signal conditions and noise levels.
     */
    private static final double RMS_SCALING_FACTOR = 0.3;


    private PitchDetectionUtil() {
    }

    /**
     * Detects the pitch of an audio signal using the YIN algorithm.
     * <p>
     * This implementation calculates the pitch (fundamental frequency) of an audio signal
     * by analyzing periodic patterns in the signal's waveform. The algorithm evaluates the
     * signal's periodicity with the help of the cumulative mean normalized difference function (CMNDF),
     * and dynamically determines thresholds for improved accuracy.
     * If a pitch is successfully detected, the method refines the results and provides a
     * confidence value to indicate the reliability of the estimate.
     *
     * @param audioData  an array of double values representing the audio signal to analyze.
     *                   Each value corresponds to the amplitude of the signal at a specific point in time.
     * @param sampleRate the sample rate of the audio signal in Hz, which determines
     *                   the number of samples per second and is needed to calculate the
     *                   pitch in terms of frequency.
     * @return a PitchDetectionResult object containing:
     * - detected pitch (in Hz): If detected, this is the fundamental frequency of the signal.
     * - confidence (range: 0.0 to 1.0): A measure of reliability in the pitch detection.
     * If no pitch is detected, the pitch is set to NO_DETECTED_PITCH, and the confidence is 0.0.
     */
    public static PitchDetectionResult detectPitchWithYIN(double[] audioData, int sampleRate) {
        // Determine the size of the input buffer (length of the audio sample data)
        int bufferSize = audioData.length;

        // Calculate tau limits based on the frequency range
        int maxTau = (int) (sampleRate / NoteUtils.addCentsToFrequency(-25, minFrequency));
        int minTau = (int) (sampleRate / NoteUtils.addCentsToFrequency(25, maxFrequency));

        // Step 1: Compute the difference function to measure signal similarity at various lags
        double[] difference = computeDifferenceFunction(audioData, bufferSize);

        // Step 2: Normalize the difference function using the CMNDF algorithm
        double[] cmndf = computeCMNDFInRange(difference, minTau, maxTau);

        // Step 3: Compute the Root Mean Square (RMS) to assess the signal's energy level
        double rms = calcRMS(audioData);

        // Step 4: Adapt the YIN threshold based on RMS to improve pitch detection reliability
        double dynamicThreshold = Math.min(0.5, YIN_MINIMUM_THRESHOLD * (1 + RMS_SCALING_FACTOR / (rms + 0.01)));

        // Step 5: Find the first minimum in the CMNDF below the threshold; this corresponds to the lag (tau)
        int tauEstimate = findFirstMinimum(cmndf, dynamicThreshold, minTau, maxTau);

        // Step 6: If a valid lag is found, refine it using parabolic interpolation for accuracy
        if (tauEstimate != -1) {
            double refinedTau = parabolicInterpolation(cmndf, tauEstimate);

            // Ensure the refined lag is valid before proceeding
            if (refinedTau > 0) {
                // Calculate the confidence by evaluating how close the CMNDF value is to the threshold
                double confidence = 1 - Math.pow((cmndf[tauEstimate] / dynamicThreshold), 2);

                // Derive the pitch (fundamental frequency) from the sample rate and tau
                double pitch = sampleRate / refinedTau;

                // Return the detected pitch and confidence in a result object
                return new PitchDetectionResult(pitch, confidence);
            }
        }

        // Step 7: If no pitch is detected, return no pitch with confidence set to 0.0
        return new PitchDetectionResult(NO_DETECTED_PITCH, 0.0);
    }

    /**
     * Refines the estimate of the lag value `tau` using parabolic interpolation
     * for improved accuracy in analyzing periodic signals.
     *
     * @param cmndf an array of double values representing the cumulative mean normalized difference function (CMNDF)
     * @param tau   an integer representing the initial lag value
     * @return the refined lag value as a double, obtained through parabolic interpolation
     */
    private static double parabolicInterpolation(double[] cmndf, int tau) {
        if (tau <= 0 || tau >= cmndf.length - 1) {
            return tau;
        }
        double x0 = cmndf[tau - 1];
        double x1 = cmndf[tau];
        double x2 = cmndf[tau + 1];
        return tau + (x0 - x2) / (2 * (x0 - 2 * x1 + x2)); // Parabolic refinement
    }

    /**
     * Determines whether a specific element in an array is a local minimum.
     * A local minimum is defined as an element that is smaller than its
     * immediate neighbors.
     *
     * @param array the array of double values to evaluate
     * @param index the index of the element to check for being a local minimum
     * @return true if the element at the specified index is a local minimum;
     * false otherwise
     */
    private static boolean isLocalMinimum(double[] array, int index) {
        if (index <= 0 || index >= array.length - 1) {
            return false;
        }
        return array[index] < array[index - 1] && array[index] < array[index + 1];
    }

    /**
     * Finds the first index in the Cumulative Mean Normalized Difference Function (CMNDF)
     * array where the value is below a specified threshold and is a local minimum.
     * This simplified version searches the entire array.
     *
     * @param cmndf     an array of double values representing the cumulative mean normalized
     *                  difference function (CMNDF). Each element corresponds to the
     *                  periodicity measure for a specific lag value.
     * @param threshold a double value representing the threshold for identifying valid
     *                  CMNDF values. Only elements below this value will be considered.
     * @return the index of the first local minimum that satisfies the threshold condition;
     *         returns -1 if no valid index is found.
     */
    private static int findFirstMinimum(double[] cmndf, double threshold) {
        return findFirstMinimum(cmndf, threshold, 0, cmndf.length);
    }

    /**
     * Finds the first index in the Cumulative Mean Normalized Difference Function (CMNDF)
     * array where the value is below a specified threshold and is a local minimum.
     *
     * @param cmndf     an array of double values representing the cumulative mean normalized
     *                  difference function (CMNDF). Each element corresponds to the
     *                  periodicity measure for a specific lag value.
     * @param threshold a double value representing the threshold for identifying valid
     *                  CMNDF values. Only elements below this value will be considered.
     * @param minTau    an integer specifying the minimum lag value to start the search from.
     * @param maxTau    an integer specifying the maximum lag value up to which the search
     *                  should be conducted.
     * @return the index of the first local minimum that satisfies the threshold condition;
     *         returns -1 if no valid index is found within the given range.
     */
    private static int findFirstMinimum(double[] cmndf, double threshold, int minTau, int maxTau) {
        for (int tau = minTau; tau < maxTau; tau++) {
            if (cmndf[tau] < threshold && isLocalMinimum(cmndf, tau)) {
                return tau;
            }
        }
        return -1;
    }

    /**
     * Computes the Cumulative Mean Normalized Difference Function (CMNDF) for a difference array.
     * This is a simplified version that computes CMNDF for the entire array without range restrictions.
     * 
     * @param difference an array of difference values to compute the CMNDF from
     * @return an array representing the CMNDF values
     */
    private static double[] computeCMNDF(double[] difference) {
        return computeCMNDFInRange(difference, 0, difference.length - 1);
    }

    /**
     * Computes the Cumulative Mean Normalized Difference Function (CMNDF) for the given range of τ values.
     * This method calculates a normalized measure within the relevant τ range, specified by minTau and maxTau,
     * while ignoring values outside that range.
     *
     * @param difference an array of difference values to compute the CMNDF from
     * @param minTau the minimum index in the τ range to be considered for calculation
     * @param maxTau the maximum index in the τ range to be considered for calculation
     * @return an array representing the CMNDF values, where values outside the specified range are set to 1
     */
    private static double[] computeCMNDFInRange(double[] difference, int minTau, int maxTau) {
        double[] cmndf = new double[difference.length];
        cmndf[0] = 1;
        double cumulativeSum = 0;

        for (int tau = 1; tau < difference.length; tau++) {
            cumulativeSum += difference[tau];

            // Nur im relevanten Bereich berechnen
            if (tau >= minTau && tau <= maxTau) {
                cmndf[tau] = difference[tau] / ((cumulativeSum / tau) + 1e-10);
            } else {
                cmndf[tau] = 1; // Werte außerhalb des Bereichs ignorieren
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
     * @param audioData  an array of double values representing the original audio signal
     *                   to be analyzed. Each value corresponds to the amplitude of the
     *                   signal at a specific point in time.
     * @param bufferSize the size of the buffer to process in the audio data. This determines
     *                   the range of time lags to evaluate in the difference function.
     * @return an array of double values representing the computed difference function.
     * Each value corresponds to the dissimilarity measure for a specific time lag.
     */
    private static double[] computeDifferenceFunction(double[] audioData, int bufferSize) {
        double[] audioSquared = Arrays.stream(audioData).map(x -> x * x).toArray();
        double[] difference = new double[bufferSize / 2];

        for (int tau = 0; tau < difference.length; tau++) {
            double sum = 0;
            for (int i = 0; i < bufferSize / 2; i++) {
                sum += audioSquared[i] + audioSquared[i + tau] - 2 * audioData[i] * audioData[i + tau];
            }
            difference[tau] = sum;
        }
        return difference;
    }

    /**
     * Detects the pitch of an audio signal using the McLeod Pitch Method (MPM).
     * This method calculates the fundamental frequency of the audio data by
     * analyzing the normalized square difference function (NSDF).
     *
     * @param audioData  an array of double values representing the audio signal.
     * @param sampleRate the sample rate of the audio signal in Hz.
     * @return a PitchDetectionResult containing the detected pitch in Hz and confidence value (0 to 1).
     */
    public static PitchDetectionResult detectPitchWithMPM(double[] audioData, int sampleRate) {
        int n = audioData.length;
        double[] nsdf = calculateNSDF(audioData, n);
        List<Integer> candidatePeaks = findPeaks(nsdf, n / 2);

        int peakIndex = selectPeak(candidatePeaks);
        double confidence = (peakIndex > 0) ? nsdf[peakIndex] : 0;

        if (peakIndex > 0) {
            peakIndex = applyParabolicInterpolation(nsdf, peakIndex);
        }

        if (peakIndex > 0) {
            double pitch = (double) sampleRate / peakIndex;
            return new PitchDetectionResult(pitch, confidence);
        }

        return new PitchDetectionResult(NO_DETECTED_PITCH, 0.0); // No pitch detected
    }

    /**
     * Calculates the Normalized Square Difference Function (NSDF) for a given audio signal.
     * The NSDF is commonly used in pitch detection algorithms to analyze the periodicity
     * of the audio signal by comparing overlapping sections of the data.
     *
     * @param audioData an array of double values representing the audio signal to be analyzed
     * @param n         the number of samples from the audio signal to process
     * @return an array of double values containing the computed NSDF values, where each index
     * represents the normalized squared difference for a specific time lag
     */
    private static double[] calculateNSDF(double[] audioData, int n) {
        double[] nsdf = new double[n];
        int maxLag = n / 2;

        for (int lag = 0; lag < maxLag; lag++) {
            double numerator = 0;
            double denominator = 0;
            for (int i = 0; i < n - lag; i++) {
                numerator += audioData[i] * audioData[i + lag];
                denominator += audioData[i] * audioData[i] + audioData[i + lag] * audioData[i + lag];
            }

            if (denominator == 0) {
                nsdf[lag] = 0; // Fallback value in case the denominator is 0
            } else {
                nsdf[lag] = 2 * numerator / denominator;  // Normalized Autocorrelation
            }
        }

        return nsdf;
    }

    /**
     * Identifies the local peaks in the given Normalized Square Difference Function (NSDF) array
     * that exceed a specified threshold and are within the provided lag range.
     * <p>
     * A peak is defined as a value that is greater than its immediate neighbors and satisfies the
     * threshold condition.
     *
     * @param nsdf   an array of double values representing the Normalized Square Difference Function (NSDF).
     *               Each element corresponds to a specific lag in the pitch detection process.
     * @param maxLag an integer representing the maximum lag limit within which peaks should be identified.
     * @return a list of integers where each integer represents the index of a detected peak
     * in the NSDF array that meets the specified criteria.
     */
    private static List<Integer> findPeaks(double[] nsdf, int maxLag) {
        List<Integer> candidatePeaks = new ArrayList<>();

        for (int lag = 1; lag < maxLag - 1; lag++) {
            if (nsdf[lag] > nsdf[lag - 1] && nsdf[lag] > nsdf[lag + 1] && nsdf[lag] > 0.7) {
                candidatePeaks.add(lag);
            }
        }

        return candidatePeaks;
    }

    /**
     * Selects the primary peak from a list of candidate peaks based on certain criteria.
     * This is used to determine the most significant peak in a pitch detection process.
     *
     * @param candidatePeaks a list of integers representing the indices of candidate peaks in the NSDF array.
     *                       Each value corresponds to a potential periodicity of the audio signal.
     * @return the index of the selected peak from the list of candidate peaks if a valid peak is found,
     * or -1 if no valid peak is identified.
     */
    private static int selectPeak(List<Integer> candidatePeaks) {
        if (candidatePeaks.isEmpty()) {
            return -1;
        }

        for (int i = 1; i < candidatePeaks.size(); i++) {
            if ((double) candidatePeaks.get(i) / candidatePeaks.get(0) >= 2.0) {
                return candidatePeaks.get(0);
            }
        }

        return -1;
    }

    /**
     * Refines the peak index using parabolic interpolation to improve accuracy in
     * analyzing peaks in the Normalized Square Difference Function (NSDF).
     *
     * @param nsdf      an array of double values representing the Normalized Square Difference Function (NSDF).
     *                  Each element corresponds to a specific lag in the pitch detection process.
     * @param peakIndex the initial index of the detected peak in the NSDF array.
     * @return the refined peak index as an integer, adjusted using parabolic interpolation for enhanced accuracy.
     */
    private static int applyParabolicInterpolation(double[] nsdf, int peakIndex) {
        if (peakIndex > 0 && peakIndex < nsdf.length - 1) {
            double x0 = nsdf[peakIndex - 1];
            double x1 = nsdf[peakIndex];
            double x2 = nsdf[peakIndex + 1];

            return peakIndex + (int) (0.5 * (x0 - x2) / (x0 - 2 * x1 + x2));
        }
        return peakIndex;
    }

    /**
     * Calculates the Root Mean Square (RMS) value of an audio signal.
     * The RMS value is a measure of the audio signal's energy and is
     * commonly used in audio processing to represent the signal's amplitude.
     *
     * @param audioData an array of double values representing the audio signal.
     *                  Each value corresponds to the amplitude of the signal at
     *                  a specific point in time.
     * @return the RMS value of the audio signal as a double.
     */
    public static double calcRMS(double[] audioData) {
        double sum = 0;
        for (double sample : audioData) {
            sum += sample * sample;
        }
        return Math.sqrt(sum / audioData.length);
    }

    /**
     * Detects the pitch of a harmonica audio signal using an optimized version of the McLeod Pitch Method (MPM).
     * This method is specifically tuned for harmonica sounds, with optimizations for the typical frequency
     * range and harmonic characteristics of harmonicas.
     *
     * @param audioData  an array of double values representing the harmonica audio signal.
     * @param sampleRate the sample rate of the audio signal in Hz.
     * @return a PitchDetectionResult containing the detected pitch in Hz and confidence value (0 to 1).
     */
    private static PitchDetectionResult detectPitchWithMPMForHarmonica(double[] audioData, int sampleRate) {
        // Harmonica-specific frequency range optimization
        // Most harmonicas play in the range of ~196Hz (G3) to ~988Hz (B5)
        int n = audioData.length;

        // Optimize for harmonica frequency range
        // For a typical harmonica range (196Hz-988Hz) at 44100Hz sample rate,
        // we're looking at periods of ~45-225 samples
        int minLag = Math.max(1, sampleRate / 1200); // Higher frequency limit (~1200Hz)
        int maxLag = Math.min(n / 2, sampleRate / 150); // Lower frequency limit (~150Hz)

        // Calculate NSDF only for the relevant lag range to save computation
        double[] nsdf = new double[n];

        // Optimized NSDF calculation for harmonica frequency range
        for (int lag = 0; lag < minLag; lag++) {
            nsdf[lag] = 0; // Skip calculation for lags outside harmonica range
        }

        for (int lag = minLag; lag < maxLag; lag++) {
            double numerator = 0;
            double denominator = 0;

            // Use a smaller window for calculation to improve performance
            int windowSize = Math.min(n - lag, 1024); // Limit window size

            for (int i = 0; i < windowSize; i++) {
                numerator += audioData[i] * audioData[i + lag];
                denominator += audioData[i] * audioData[i] + audioData[i + lag] * audioData[i + lag];
            }

            if (denominator == 0) {
                nsdf[lag] = 0;
            } else {
                nsdf[lag] = 2 * numerator / denominator;
            }
        }

        for (int lag = maxLag; lag < n; lag++) {
            nsdf[lag] = 0; // Skip calculation for lags outside harmonica range
        }

        // Fast peak finding for harmonicas
        List<Integer> candidatePeaks = new ArrayList<>();
        for (int lag = minLag + 1; lag < maxLag - 1; lag++) {
            // Only check every other sample to improve performance
            if (lag % 2 == 0 && nsdf[lag] > nsdf[lag - 2] && nsdf[lag] > nsdf[lag + 2] && nsdf[lag] > 0.7) {
                // Verify it's a true peak by checking immediate neighbors
                if (nsdf[lag] > nsdf[lag - 1] && nsdf[lag] > nsdf[lag + 1]) {
                    candidatePeaks.add(lag);
                }
            }
        }

        // Early exit if no peaks found
        if (candidatePeaks.isEmpty()) {
            return new PitchDetectionResult(NO_DETECTED_PITCH, 0.0);
        }

        // Simplified peak selection for harmonicas
        int peakIndex = candidatePeaks.get(0); // Take the first peak
        double confidence = nsdf[peakIndex];

        // Simple parabolic interpolation
        if (peakIndex > 0 && peakIndex < nsdf.length - 1) {
            double x0 = nsdf[peakIndex - 1];
            double x1 = nsdf[peakIndex];
            double x2 = nsdf[peakIndex + 1];
            double adjustment = 0.5 * (x0 - x2) / (x0 - 2 * x1 + x2);

            // Only apply if the adjustment is reasonable
            if (Math.abs(adjustment) < 1) {
                peakIndex += adjustment;
            }
        }

        double pitch = (double) sampleRate / peakIndex;

        // Additional confidence boost for pitches in the harmonica range
        if (pitch >= 196 && pitch <= 988) {
            confidence = Math.min(1.0, confidence * 1.1);
        }

        return new PitchDetectionResult(pitch, confidence);
    }

    /**
     * Detects the pitch of a harmonica audio signal using an optimized version of the YIN algorithm.
     * This method is specifically tuned for harmonica sounds, with optimizations for the typical frequency
     * range and harmonic characteristics of harmonicas.
     *
     * @param audioData  an array of double values representing the harmonica audio signal.
     * @param sampleRate the sample rate of the audio signal in Hz.
     * @return a PitchDetectionResult containing the detected pitch in Hz and confidence value (0 to 1).
     */
    private static PitchDetectionResult detectPitchWithYINForHarmonica(double[] audioData, int sampleRate) {
        // Harmonica-specific frequency range optimization
        // Most harmonicas play in the range of ~196Hz (G3) to ~988Hz (B5)
        int bufferSize = audioData.length;

        // Optimize for harmonica frequency range
        // For a typical harmonica range (196Hz-988Hz) at 44100Hz sample rate,
        // we're looking at periods of ~45-225 samples
        int maxTau = Math.min(bufferSize / 2, sampleRate / 150); // Lower frequency limit (~150Hz)
        int minTau = Math.max(2, sampleRate / 1200); // Higher frequency limit (~1200Hz)

        // Step 1: Compute the difference function more efficiently for harmonica range
        double[] difference = new double[bufferSize / 2];

        // Pre-compute squared audio data for efficiency
        double[] audioSquared = new double[bufferSize];
        for (int i = 0; i < bufferSize; i++) {
            audioSquared[i] = audioData[i] * audioData[i];
        }

        // Only compute difference function for relevant tau range
        for (int tau = 0; tau < difference.length; tau++) {
            if (tau < minTau || tau > maxTau) {
                difference[tau] = 1.0; // Set to high value outside harmonica range
                continue;
            }

            // Use a smaller window for calculation to improve performance
            int windowSize = Math.min(bufferSize / 2, 1024); // Limit window size
            double sum = 0;

            for (int i = 0; i < windowSize; i++) {
                sum += audioSquared[i] + audioSquared[i + tau] - 2 * audioData[i] * audioData[i + tau];
            }
            difference[tau] = sum;
        }

        // Step 2: Compute CMNDF more efficiently
        double[] cmndf = new double[difference.length];
        cmndf[0] = 1.0;

        // Only compute CMNDF for relevant tau range
        double cumulativeSum = 0;
        for (int tau = 1; tau < difference.length; tau++) {
            if (tau < minTau || tau > maxTau) {
                cmndf[tau] = 1.0; // Set to high value outside harmonica range
                continue;
            }

            cumulativeSum += difference[tau];
            cmndf[tau] = difference[tau] / ((cumulativeSum / tau) + 1e-10);
        }

        // Step 3: Compute RMS for threshold adjustment
        double rms = calcRMS(audioData);

        // Step 4: Use a more aggressive threshold for harmonica sounds
        // Harmonicas have strong fundamentals, so we can use a lower threshold
        double harmonicaThreshold = Math.min(0.4, YIN_MINIMUM_THRESHOLD * (1 + RMS_SCALING_FACTOR / (rms + 0.01)));

        // Step 5: Find the first minimum more efficiently
        int tauEstimate = -1;
        for (int tau = minTau; tau <= maxTau; tau++) {
            if (cmndf[tau] < harmonicaThreshold && isLocalMinimum(cmndf, tau)) {
                tauEstimate = tau;
                break;
            }
        }

        // Step 6: If a valid lag is found, refine it using parabolic interpolation
        if (tauEstimate != -1) {
            double refinedTau = parabolicInterpolation(cmndf, tauEstimate);

            // Ensure the refined lag is valid
            if (refinedTau > 0) {
                // Calculate confidence
                double confidence = 1 - Math.pow((cmndf[tauEstimate] / harmonicaThreshold), 2);

                // Derive the pitch
                double pitch = sampleRate / refinedTau;

                // Boost confidence for pitches in the harmonica range
                if (pitch >= 196 && pitch <= 988) {
                    confidence = Math.min(1.0, confidence * 1.1);
                }

                return new PitchDetectionResult(pitch, confidence);
            }
        }

        // Step 7: If no pitch is detected, return no pitch with confidence set to 0.0
        return new PitchDetectionResult(NO_DETECTED_PITCH, 0.0);
    }

    /**
     * Represents the result of a pitch detection operation.
     * This class stores the detected pitch frequency in Hz
     * and a confidence score indicating the reliability of the detection.
     */
    public record PitchDetectionResult(double pitch, double confidence) {
    }
}
