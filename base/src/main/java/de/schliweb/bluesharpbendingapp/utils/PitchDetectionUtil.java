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

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for pitch detection and audio analysis.
 * Provides methods to detect pitch using different algorithms such as YIN and MPM
 * and to analyze key audio features like RMS (Root Mean Square).
 */
public class PitchDetectionUtil {

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
     * Detects the pitch of an audio signal using the YIN algorithm.
     *
     * @param audioData  an array of double values representing the audio signal.
     * @param sampleRate the sample rate of the audio signal in Hz.
     * @return a PitchDetectionResult containing the detected pitch in Hz and a confidence value, or -1 and 0 if no pitch is detected.
     */
    public static PitchDetectionResult detectPitchWithYIN(double[] audioData, int sampleRate) {
        int bufferSize = audioData.length;

        // Step 1: Compute the difference function
        double[] difference = computeDifferenceFunction(audioData, bufferSize);

        // Step 2: Compute the cumulative mean normalized difference function
        double[] cmndf = computeCMNDF(difference);

        // Step 3: Find the first minimum below a threshold
        int tauEstimate = findFirstMinimum(cmndf, YIN_MINIMUM_THRESHOLD);
        double confidence = 0.0;

        // Step 4: Use parabolic interpolation for more precise tau estimation
        if (tauEstimate != -1) {
            confidence = Math.max(0, 1 - (cmndf[tauEstimate] / YIN_MINIMUM_THRESHOLD));
            double refinedTau = parabolicInterpolation(cmndf, tauEstimate);
            if (refinedTau > 0.0) {
                double pitch = (double) sampleRate / refinedTau;
                return new PitchDetectionResult(pitch, confidence);
            }
        }
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
     * Finds the first index in the given cumulative mean normalized difference function (CMNDF)
     * array where the value is below a specified threshold and is a local minimum.
     *
     * @param cmndf     an array of double values representing the cumulative mean normalized difference function (CMNDF)
     * @param threshold a double value representing the threshold to evaluate against
     * @return the index of the first local minimum in the CMNDF array that is below the threshold,
     * or -1 if no such local minimum is found
     */
    private static int findFirstMinimum(double[] cmndf, double threshold) {
        for (int tau = 2; tau < cmndf.length - 1; tau++) {
            if (cmndf[tau] < threshold && isLocalMinimum(cmndf, tau)) {
                return tau;
            }
        }
        return -1;
    }

    /**
     * Computes the Cumulative Mean Normalized Difference Function (CMNDF) for a given difference function.
     * The CMNDF is used in pitch detection algorithms to evaluate the periodicity of signals.
     *
     * @param difference an array of double values representing the difference function
     *                   of a signal, typically derived from a pitch detection process.
     * @return an array of double values representing the CMNDF, where each value indicates
     * the normalized difference for a potential periodicity.
     */
    private static double[] computeCMNDF(double[] difference) {
        double[] cmndf = new double[difference.length];
        cmndf[0] = 1; // First value is always 1
        double cumulativeSum = 0;
        for (int tau = 1; tau < difference.length; tau++) {
            cumulativeSum += difference[tau];
            cmndf[tau] = difference[tau] / ((cumulativeSum / tau) + 1e-10); // Avoid division by zero
        }
        return cmndf;
    }

    /**
     * Computes the difference function of the given audio data for use in pitch detection algorithms.
     * The difference function calculates the squared difference between signal values at various time lags.
     *
     * @param audioData  an array of double values representing the audio signal
     * @param bufferSize the size of the buffer to be used for the computation, representing the length of the audio segment
     * @return an array of double values representing the computed difference function
     */
    private static double[] computeDifferenceFunction(double[] audioData, int bufferSize) {
        double[] difference = new double[bufferSize / 2];
        for (int tau = 0; tau < difference.length; tau++) {
            double sum = 0;
            for (int i = 0; i < bufferSize / 2; i++) {
                double delta = audioData[i] - audioData[i + tau];
                sum += delta * delta;
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
        double[] nsdf = new double[n];
        int maxLag = n / 2;

        // Step 1: Calculate the NSDF (Normalized Square Difference Function)
        for (int lag = 0; lag < maxLag; lag++) {
            double numerator = 0;
            double denominator = 0;
            for (int i = 0; i < n - lag; i++) {
                numerator += audioData[i] * audioData[i + lag];
                denominator += audioData[i] * audioData[i] + audioData[i + lag] * audioData[i + lag];
            }
            nsdf[lag] = 2 * numerator / denominator; // Normalized Autocorrelation
        }

        // Step 2: Find peaks in the NSDF
        int peakIndex = -1;
        // Step 2: Find peaks in the NSDF
        List<Integer> candidatePeaks = new ArrayList<>();
        for (int lag = 1; lag < maxLag - 1; lag++) {
            if (nsdf[lag] > nsdf[lag - 1] && nsdf[lag] > nsdf[lag + 1] && nsdf[lag] > 0.7) {
                candidatePeaks.add(lag);
            }
        }

        // Filter peaks for harmonics
        if (!candidatePeaks.isEmpty()) {
            for (int i = 1; i < candidatePeaks.size(); i++) {
                if ((double) candidatePeaks.get(i) / candidatePeaks.get(0) >= 2.0) {
                    peakIndex = candidatePeaks.get(0); // Fundamental Frequency
                    break;
                }
            }
        }
        double confidence = 0;

        // Step 3: Parabolic interpolation for more accurate peak detection
        if (peakIndex > 0 && peakIndex < maxLag - 1) {
            double x0 = nsdf[peakIndex - 1];
            double x1 = nsdf[peakIndex];
            double x2 = nsdf[peakIndex + 1];
            confidence = x1; // Confidence as the highest NSDF value at peakIndex
            peakIndex = peakIndex + (int) (0.5 * (x0 - x2) / (x0 - 2 * x1 + x2));
        }

        // Step 4: Convert lag to frequency
        if (peakIndex > 0) {
            double pitch = (double) sampleRate / peakIndex;
            return new PitchDetectionResult(pitch, confidence);
        }

        return new PitchDetectionResult(NO_DETECTED_PITCH, 0.0); // No pitch detected
    }

    /**
     * Calculates the Root Mean Square (RMS) value of an audio signal.
     * RMS is a measure of the magnitude of a varying quantity and is
     * commonly used in audio processing to determine signal energy.
     *
     * @param audioData an array of double values representing the audio signal
     *                  for which the RMS is to be calculated.
     * @return the RMS value of the audio signal as a double, scaled by a factor of 100.
     */
    public static double calcRMS(double[] audioData) {
        double sum = 0;
        for (double sample : audioData) {
            sum += sample * sample;
        }
        return Math.sqrt(sum / audioData.length) * 100;
    }

    /**
     * Represents the result of a pitch detection operation.
     * This class stores the detected pitch frequency in Hz
     * and a confidence score indicating the reliability of the detection.
     */
    public static class PitchDetectionResult {
        /**
         * Represents the detected pitch frequency in Hertz (Hz).
         * This value indicates the frequency of the detected sound,
         * measured as cycles per second.
         * It is a final variable, meaning its value is immutable
         * after being set during instantiation of the associated object.
         */
        public final double pitch;
        /**
         * Represents the confidence score of a pitch detection result.
         * This value indicates the reliability of the detected pitch and is typically
         * represented as a decimal number between 0 and 1, where a higher value signifies
         * greater confidence in the accuracy of the detected pitch.
         */
        public final double confidence;


        /**
         * Constructs a new instance of {@code PitchDetectionResult} with the specified pitch and confidence values.
         *
         * @param pitch      the detected pitch frequency in hertz
         * @param confidence the confidence score of the pitch detection, typically between 0.0 and 1.0
         */
        public PitchDetectionResult(double pitch, double confidence) {
            this.pitch = pitch;
            this.confidence = confidence;
        }

        /**
         * Retrieves the detected pitch value.
         *
         * @return the pitch frequency in Hz as a double
         */
        public double getPitch() {
            return pitch;
        }

        /**
         * Retrieves the confidence score indicating the reliability of the pitch detection.
         *
         * @return the confidence score, a double value typically between 0 and 1,
         * where higher values represent greater confidence in the detected pitch.
         */
        public double getConfidence() {
            return confidence;
        }
    }

}