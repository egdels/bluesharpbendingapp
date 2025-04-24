package de.schliweb.bluesharpbendingapp.utils;

import lombok.Getter;
import lombok.Setter;

/**
 * Implementation of the YIN algorithm for pitch detection.
 * <p>
 * This class provides a clean, efficient implementation of the YIN algorithm
 * for detecting the fundamental frequency (pitch) of an audio signal.
 * The implementation allows configuring the frequency range and provides
 * both the detected pitch and a confidence value.
 * <p>
 * The YIN algorithm works by:
 * 1. Computing the difference function to measure signal similarity at various lags
 * 2. Normalizing the difference function using the CMNDF algorithm
 * 3. Finding the first minimum in the CMNDF below a threshold
 * 4. Refining the estimate using parabolic interpolation
 * 5. Calculating the pitch and confidence from the refined estimate
 */
public class YINPitchDetector extends PitchDetector {

    /**
     * The minimum frequency that can be detected (in Hz).
     * This can be configured using the setter method.
     */
    @Setter
    @Getter
    protected static double minFrequency = DEFAULT_MIN_FREQUENCY;

    /**
     * The maximum frequency that can be detected (in Hz).
     * This can be configured using the setter method.
     */
    @Setter
    @Getter
    protected static double maxFrequency = DEFAULT_MAX_FREQUENCY;

    /**
     * The minimum threshold value used in the YIN pitch detection algorithm.
     * This threshold is applied to the cumulative mean normalized difference function (CMNDF)
     * to identify potential pitch candidates. Values in the CMNDF below this threshold
     * are considered for determining the fundamental frequency of an audio signal.
     */
    private static final double YIN_MINIMUM_THRESHOLD = 0.4;

    /**
     * A scaling factor used in the Root Mean Square (RMS) calculation to normalize
     * and adjust the amplitude threshold in pitch detection processes.
     */
    private static final double RMS_SCALING_FACTOR = 0.3;

    private YINPitchDetector() {
        super();
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
    public static PitchDetectionResult detectPitch(double[] audioData, int sampleRate) {
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
                return new PitchDetector.PitchDetectionResult(pitch, confidence);
            }
        }

        // Step 7: If no pitch is detected, return no pitch with confidence set to 0.0
        return new PitchDetector.PitchDetectionResult(NO_DETECTED_PITCH, 0.0);
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
    private int findFirstMinimum(double[] cmndf, double threshold) {
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
    private double[] computeCMNDF(double[] difference) {
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
     * @param audioData  an array of double values representing the original audio signal
     *                   to be analyzed. Each value corresponds to the amplitude of the
     *                   signal at a specific point in time.
     * @param bufferSize the size of the buffer to process in the audio data. This determines
     *                   the range of time lags to evaluate in the difference function.
     * @return an array of double values representing the computed difference function.
     * Each value corresponds to the dissimilarity measure for a specific time lag.
     */
    private static double[] computeDifferenceFunction(double[] audioData, int bufferSize) {
        double[] audioSquared = new double[audioData.length];
        for (int i = 0; i < audioData.length; i++) {
            audioSquared[i] = audioData[i] * audioData[i];
        }

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

}
