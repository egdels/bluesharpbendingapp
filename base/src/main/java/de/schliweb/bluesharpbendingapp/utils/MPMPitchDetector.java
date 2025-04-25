package de.schliweb.bluesharpbendingapp.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
public class MPMPitchDetector extends PitchDetector {

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
     * The threshold used for peak detection in the NSDF.
     * Peaks with values below this threshold will not be considered.
     */
    private static final double PEAK_THRESHOLD = 0.5;

    private MPMPitchDetector() {
        super();
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
    public static PitchDetectionResult detectPitch(double[] audioData, int sampleRate) {
        int n = audioData.length;

        // Calculate lag limits based on the frequency range
        // We extend the range by 10% on both ends to ensure we can detect frequencies at the edges
        int minLag = Math.max(1, (int)(sampleRate / (maxFrequency * 1.1))); // Extend by 10% higher
        int maxLag = Math.min(n / 2, (int)(sampleRate / (minFrequency * 0.9))); // Extend by 10% lower

        // Calculate the NSDF
        double[] nsdf = calculateNSDF(audioData, n, minLag, maxLag);

        // Find peaks in the NSDF
        List<Integer> candidatePeaks = findPeaks(nsdf, minLag, maxLag);

        // Select the most significant peak
        int peakIndex = selectPeak(candidatePeaks);

        // If no peak is found, return no pitch detected
        if (peakIndex <= 0) {
            return new PitchDetectionResult(NO_DETECTED_PITCH, 0.0);
        }

        // Calculate confidence based on the NSDF value at the peak
        double confidence = nsdf[peakIndex - minLag];

        // Apply parabolic interpolation to refine the peak index
        double refinedPeakIndex = applyParabolicInterpolation(nsdf, peakIndex - minLag, minLag);

        // Calculate the pitch from the refined peak index
        double pitch = (double) sampleRate / refinedPeakIndex;

        return new PitchDetectionResult(pitch, confidence);
    }

    /**
     * Calculates the Normalized Square Difference Function (NSDF) for a given audio signal
     * within a specific lag range defined by minLag and maxLag.
     * This allows focusing the calculation on a specific frequency range.
     *
     * @param audioData an array of double values representing the audio signal to be analyzed
     * @param n         the number of samples from the audio signal to process
     * @param minLag    the minimum lag value to consider (corresponding to the maximum frequency)
     * @param maxLag    the maximum lag value to consider (corresponding to the minimum frequency)
     * @return an array of double values containing the computed NSDF values
     */
    private static double[] calculateNSDF(double[] audioData, int n, int minLag, int maxLag) {
        int maxLagForCalculation = Math.min(n / 2, maxLag);
        double[] nsdf = new double[maxLagForCalculation - minLag];

        // Calculate NSDF only for lags between minLag and maxLagForCalculation
        for (int lag = minLag; lag < maxLagForCalculation; lag++) {
            double numerator = 0;
            double denominator = 0;
            for (int i = 0; i < n - lag; i++) {
                numerator += audioData[i] * audioData[i + lag];
                denominator += audioData[i] * audioData[i] + audioData[i + lag] * audioData[i + lag];
            }

            if (denominator == 0) {
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
     * @param nsdf   an array of double values representing the Normalized Square Difference Function (NSDF)
     * @param minLag the minimum lag value to consider (corresponding to the maximum frequency)
     * @param maxLag the maximum lag value to consider (corresponding to the minimum frequency)
     * @return a list of integers where each integer represents the index of a detected peak
     */
    private static List<Integer> findPeaks(double[] nsdf, int minLag, int maxLag) {
        List<Integer> candidatePeaks = new ArrayList<>();

        // Ensure we don't go out of bounds
        if (nsdf.length < 2) {
            return candidatePeaks;
        }

        // Find all peaks in the NSDF that exceed the threshold
        for (int i = 1; i < nsdf.length - 1; i++) {
            if (nsdf[i] > nsdf[i - 1] && nsdf[i] > nsdf[i + 1] && nsdf[i] > PEAK_THRESHOLD) {
                // Add the actual lag value (not the array index)
                candidatePeaks.add(i + minLag);
            }
        }

        return candidatePeaks;
    }

    /**
     * Selects the most relevant peak index from a list of candidate peak indices.
     * If the list of candidate peaks is empty, the method returns -1.
     *
     * @param candidatePeaks a list of integers representing the indices of candidate peaks
     * @return the index of the selected peak from the candidate peaks, or -1 if the list is empty
     */
    private static int selectPeak(List<Integer> candidatePeaks) {
        if (candidatePeaks.isEmpty()) {
            return -1;
        }
        return candidatePeaks.get(0);
    }

    /**
     * Refines the peak index using parabolic interpolation to improve accuracy in
     * analyzing peaks in the Normalized Square Difference Function (NSDF).
     *
     * @param nsdf      an array of double values representing the Normalized Square Difference Function (NSDF).
     * @param peakIndex the index of the detected peak in the NSDF array.
     * @param minLag    the minimum lag value used in the NSDF calculation.
     * @return the refined peak index as a double, adjusted using parabolic interpolation for enhanced accuracy.
     */
    private static double applyParabolicInterpolation(double[] nsdf, int peakIndex, int minLag) {
        if (peakIndex <= 0 || peakIndex >= nsdf.length - 1) {
            return peakIndex + minLag;
        }

        double x0 = nsdf[peakIndex - 1];
        double x1 = nsdf[peakIndex];
        double x2 = nsdf[peakIndex + 1];

        // Calculate the adjustment using parabolic interpolation
        double denominator = x0 - 2 * x1 + x2;

        // Avoid division by zero or very small values
        if (Math.abs(denominator) < 1e-10) {
            return peakIndex + minLag;
        }

        double adjustment = 0.5 * (x0 - x2) / denominator;

        // Limit the adjustment to a reasonable range to avoid extreme values
        if (Math.abs(adjustment) > 1) {
            adjustment = 0;
        }

        return (peakIndex + adjustment) + minLag;
    }

}
