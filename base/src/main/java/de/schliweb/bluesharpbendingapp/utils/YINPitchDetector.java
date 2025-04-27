package de.schliweb.bluesharpbendingapp.utils;

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
class YINPitchDetector extends PitchDetector {

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

    /**
     * Default constructor for the YINPitchDetector class.
     * Initializes a new instance of the pitch detection algorithm using the YIN method.
     * This constructor provides a protected access level, allowing subclasses or classes
     * within the same package to instantiate this detector.
     */
    protected YINPitchDetector() {
        super();
    }


    /**
     * Detects the pitch of an audio signal and returns the pitch frequency along with a confidence score.
     * This method uses the YIN algorithm with enhancements such as RMS-based dynamic thresholding
     * and parabolic interpolation for accurate pitch estimation.
     *
     * @param audioData  an array of double values representing the audio signal to be analyzed. Each value
     *                   corresponds to the amplitude of the signal at a specific point in time.
     * @param sampleRate the sample rate of the audio signal in Hz. This value is used to derive
     *                   frequency calculations from time lags.
     * @return a {@code PitchDetectionResult} object containing the detected pitch frequency (in Hz) and the
     *         confidence score (ranging from 0.0 to 1.0). If no pitch is detected, the pitch is set
     *         to {@code NO_DETECTED_PITCH} and the confidence is 0.0.
     */
    @Override
    PitchDetectionResult detectPitch(double[] audioData, int sampleRate) {
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
            double refinedTau = PitchDetector.parabolicInterpolation(cmndf, tauEstimate);

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
