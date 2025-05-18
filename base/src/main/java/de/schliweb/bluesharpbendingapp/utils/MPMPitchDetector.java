package de.schliweb.bluesharpbendingapp.utils;


import java.util.List;
import java.util.stream.IntStream;

/**
 * The MPMPitchDetector class is a specialized pitch detection implementation
 * using the McLeod Pitch Method (MPM). It analyzes audio signals to detect
 * the fundamental frequency (pitch) and calculate its confidence.
 * <p>
 * This implementation leverages the Normalized Square Difference Function (NSDF)
 * for determining pitch-related peaks and uses techniques such as parabolic
 * interpolation for refined peak accuracy.
 * <p>
 * Features:
 * - Configurable frequency detection range (minimum and maximum frequencies).
 * - Peaks are detected with a threshold to exclude insignificant candidates.
 * - Refined pitch estimation using parabolic interpolation.
 */
public class MPMPitchDetector extends PitchDetector {

    /**
     * The threshold used for peak detection in the NSDF.
     * Peaks with values below this threshold will not be considered.
     */
    private static final double PEAK_THRESHOLD = 0.5;

    /**
     * Initializes a new instance of the MPMPitchDetector class.
     * This constructor calls the parent class's default constructor
     * to initialize shared attributes and configurations for pitch detection.
     * <p>
     * MPMPitchDetector implements the McLeod Pitch Method (MPM),
     * which is designed for detecting the fundamental frequency of an audio signal.
     * The detector primarily analyzes the Normalized Square Difference Function (NSDF)
     * for detecting pitch and evaluating its confidence.
     */
    protected MPMPitchDetector() {
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
    @Override
    PitchDetectionResult detectPitch(double[] audioData, int sampleRate) {
        int n = audioData.length;

        // Calculate lag limits based on the frequency range
        // We extend the range by 10% on both ends to ensure we can detect frequencies at the edges
        int minLag = Math.max(1, (int) (sampleRate / (maxFrequency * 1.1))); // Extend by 10% higher
        int maxLag = Math.min(n / 2, (int) (sampleRate / (minFrequency * 0.9))); // Extend by 10% lower

        // Calculate the NSDF
        double[] nsdf = calculateNSDF(audioData, n, minLag, maxLag);

        // Find peaks in the NSDF
        List<Integer> candidatePeaks = findPeaks(nsdf, minLag);

        // Select the most significant peak
        int peakIndex = selectPeak(candidatePeaks);

        // If no peak is found, return no pitch detected
        if (peakIndex <= 0) {
            return new PitchDetectionResult(NO_DETECTED_PITCH, 0.0);
        }

        // Calculate confidence based on the NSDF value at the peak
        double confidence = nsdf[peakIndex - minLag];

        // Apply parabolic interpolation to refine the peak index
        double refinedPeakIndex = PitchDetector.parabolicInterpolation(nsdf, peakIndex - minLag) + minLag;

        // Calculate the pitch from the refined peak index
        double pitch = sampleRate / refinedPeakIndex;

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
    private double[] calculateNSDF(double[] audioData, int n, int minLag, int maxLag) {
        int maxLagForCalculation = Math.min(n / 2, maxLag);
        double[] nsdf = new double[maxLagForCalculation - minLag];

        // Calculate NSDF only for lags between minLag and maxLagForCalculation
        // Use parallel streams to parallelize the outer loop
        IntStream.range(minLag, maxLagForCalculation).parallel().forEach(lag -> {
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
        });

        return nsdf;
    }

    /**
     * Identifies the local peaks in the given Normalized Square Difference Function (NSDF) array
     * within a specific lag range defined by minLag.
     * This allows focusing the peak detection on a specific frequency range.
     *
     * @param nsdf   an array of double values representing the Normalized Square Difference Function (NSDF)
     * @param minLag the minimum lag value to consider (corresponding to the maximum frequency)
     * @return a list of integers where each integer represents the index of a detected peak
     */
    private List<Integer> findPeaks(double[] nsdf, int minLag) {
        return findPeaks(nsdf, PEAK_THRESHOLD, minLag);
    }

    /**
     * Selects the most relevant peak index from a list of candidate peak indices.
     * If the list of candidate peaks is empty, the method returns -1.
     *
     * @param candidatePeaks a list of integers representing the indices of candidate peaks
     * @return the index of the selected peak from the candidate peaks, or -1 if the list is empty
     */
    private int selectPeak(List<Integer> candidatePeaks) {
        if (candidatePeaks.isEmpty()) {
            return -1;
        }
        return candidatePeaks.get(0);
    }
}
