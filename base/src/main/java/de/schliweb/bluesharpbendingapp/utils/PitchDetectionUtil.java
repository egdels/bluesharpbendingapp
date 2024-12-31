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

/**
 * Utility class for pitch detection and signal analysis.
 * Provides static methods for detecting pitch using different algorithms
 * and for calculating signal properties.
 */
public class PitchDetectionUtil {

    /**
     * Detects pitch using the YIN algorithm.
     *
     * @param audioData  the normalized audio data as an array of doubles
     * @param sampleRate the sampling rate of the audio data (Hz)
     * @return detected pitch frequency in Hertz (Hz), or -1 if no pitch is detected
     */
    public static double detectPitchWithYIN(double[] audioData, int sampleRate) {
        int bufferSize = audioData.length;

        // Step 1: Difference function
        double[] difference = new double[bufferSize / 2];
        for (int tau = 0; tau < difference.length; tau++) {
            double sum = 0;
            for (int i = 0; i < bufferSize / 2; i++) {
                double delta = audioData[i] - audioData[i + tau];
                sum += delta * delta;
            }
            difference[tau] = sum;
        }

        // Step 2: Cumulative mean normalized difference function
        double[] cmndf = new double[difference.length];
        cmndf[0] = 1; // First value always 1
        double runningSum = 0;
        for (int tau = 1; tau < difference.length; tau++) {
            runningSum += difference[tau];
            cmndf[tau] = difference[tau] / ((runningSum / tau) + 1e-10); // Avoid division by zero
        }

        // Step 3: Find the first minimum below a threshold
        int tauEstimate = -1;
        double threshold = 0.1; // Default YIN threshold (adjustable)
        for (int tau = 2; tau < cmndf.length; tau++) { // Start at 2 to skip smallest lags
            if (cmndf[tau] < threshold && isLocalMinimum(cmndf, tau)) {
                tauEstimate = tau;
                break;
            }
        }


        // Step 4: Parabolic interpolation for more precise estimation
        double tauEstimateAfterInterpolation; // Ändere Datentyp zu double
        if (tauEstimate != -1) {
            tauEstimateAfterInterpolation = parabolicInterpolation(cmndf, tauEstimate);
            return (double) sampleRate / tauEstimateAfterInterpolation;
        } else {
            return -1; // No pitch detected
        }
    }

    /**
     * Helper function to check if the given index is a local minimum.
     *
     * @param array the array of values
     * @param index the index to check
     * @return true if the index is a local minimum
     */
    private static boolean isLocalMinimum(double[] array, int index) {
        if (index <= 0 || index >= array.length - 1) {
            return false;
        }
        return array[index] < array[index - 1] && array[index] < array[index + 1];
    }

    /**
     * Parabolic interpolation to refine tau estimate for higher pitch accuracy.
     *
     * @param cmndf the values of the cumulative mean normalized difference function
     * @param tau   the initial tau estimate
     * @return refined tau estimate
     */
    private static double parabolicInterpolation(double[] cmndf, int tau) {
        if (tau <= 0 || tau >= cmndf.length - 1) {
            return tau;
        }
        double x0 = cmndf[tau - 1];
        double x1 = cmndf[tau];
        double x2 = cmndf[tau + 1];
        return tau + (x0 - x2) / (2 * (2 * x1 - x0 - x2)); // Parabolic refinement
    }

    /**
     * Detects pitch using the McLeod Pitch Method (MPM).
     *
     * @param audioData   the normalized audio data as an array of doubles
     * @param sampleRate  the sampling rate of the audio data (Hz)
     * @return detected pitch frequency in Hertz (Hz)
     */
    public static double detectPitchWithMPM(double[] audioData, int sampleRate) {
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
        double maxValue = 0;
        for (int lag = 1; lag < maxLag - 1; lag++) {
            if (nsdf[lag] > nsdf[lag - 1] && nsdf[lag] > nsdf[lag + 1] && nsdf[lag] > maxValue) {
                maxValue = nsdf[lag];
                peakIndex = lag;
            }
        }

        // Step 3: Parabolic interpolation for more accurate peak detection
        if (peakIndex > 0 && peakIndex < maxLag - 1) {
            double x0 = nsdf[peakIndex - 1];
            double x1 = nsdf[peakIndex];
            double x2 = nsdf[peakIndex + 1];
            peakIndex = peakIndex + (int) (0.5 * (x0 - x2) / (x0 - 2 * x1 + x2));
        }

        // Step 4: Convert lag to frequency
        if (peakIndex > 0) {
            return (double) sampleRate / peakIndex;
        }

        return -1; // No pitch detected
    }

    /**
     * Calculates the RMS (Root Mean Square) to measure signal amplitude.
     *
     * @param audioData the normalized audio data
     * @return RMS value
     */
    public static double calcRMS(double[] audioData) {
        double sum = 0;
        for (double sample : audioData) {
            sum += sample * sample;
        }
        return Math.sqrt(sum / audioData.length) * 100;
    }


}