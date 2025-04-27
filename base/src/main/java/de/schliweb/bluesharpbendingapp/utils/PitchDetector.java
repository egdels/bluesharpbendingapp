package de.schliweb.bluesharpbendingapp.utils;

import lombok.Getter;
import lombok.Setter;

/**
 * Abstract base class for pitch detection algorithms.
 * <p>
 * This class provides common functionality for different pitch detection algorithms
 * such as YIN, MPM, and ZCR. It defines the basic structure and shared methods
 * that all pitch detectors use, allowing for consistent configuration and usage
 * across different implementations.
 * <p>
 * Each concrete implementation should provide its own algorithm-specific logic
 * in the detectPitch method.
 */
public abstract class PitchDetector {

    /**
     * A constant representing the absence of a detected pitch.
     * When a pitch detection method fails to identify a fundamental frequency within
     * the audio signal, this value is returned to indicate no pitch was detected.
     */
    public static final double NO_DETECTED_PITCH = -1;

    /**
     * The default minimum frequency that can be detected (in Hz).
     */
    private static final double DEFAULT_MIN_FREQUENCY = 80.0;

    /**
     * The default maximum frequency that can be detected (in Hz).
     */
    private static final double DEFAULT_MAX_FREQUENCY = 4835.0;

    /**
     * The minimum frequency that can be detected (in Hz).
     */
    @Getter
    @Setter
    protected static double minFrequency = DEFAULT_MIN_FREQUENCY;

    /**
     * The maximum frequency that can be detected (in Hz).
     */
    @Getter
    @Setter
    protected static double maxFrequency = DEFAULT_MAX_FREQUENCY;

    /**
     * Gets the default minimum frequency that can be detected (in Hz).
     * 
     * @return the default minimum frequency in Hz
     */
    public static double getDefaultMinFrequency() {
        return DEFAULT_MIN_FREQUENCY;
    }

    /**
     * Gets the default maximum frequency that can be detected (in Hz).
     * 
     * @return the default maximum frequency in Hz
     */
    public static double getDefaultMaxFrequency() {
        return DEFAULT_MAX_FREQUENCY;
    }

    /**
     * Static method for detecting pitch using the YIN algorithm.
     * This method is provided for backward compatibility with code that uses static methods.
     * It creates a temporary YINPitchDetector instance and delegates to it.
     *
     * @param audioData  an array of double values representing the audio signal.
     * @param sampleRate the sample rate of the audio signal in Hz.
     * @return a PitchDetectionResult containing the detected pitch in Hz and confidence value.
     */
    public static PitchDetectionResult detectPitchYIN(double[] audioData, int sampleRate) {
        YINPitchDetector detector = new YINPitchDetector();
        return detector.detectPitch(audioData, sampleRate);
    }

    /**
     * Static method for detecting pitch using the MPM algorithm.
     * This method is provided for backward compatibility with code that uses static methods.
     * It creates a temporary MPMPitchDetector instance and delegates to it.
     *
     * @param audioData  an array of double values representing the audio signal.
     * @param sampleRate the sample rate of the audio signal in Hz.
     * @return a PitchDetectionResult containing the detected pitch in Hz and confidence value.
     */
    public static PitchDetectionResult detectPitchMPM(double[] audioData, int sampleRate) {
        MPMPitchDetector detector = new MPMPitchDetector();
        return detector.detectPitch(audioData, sampleRate);
    }

    /**
     * Detects the pitch of an audio signal using the specific algorithm implemented by the subclass.
     *
     * @param audioData  an array of double values representing the audio signal.
     * @param sampleRate the sample rate of the audio signal in Hz.
     * @return a PitchDetectionResult containing the detected pitch in Hz and confidence value.
     */
    abstract PitchDetectionResult detectPitch(double[] audioData, int sampleRate);

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
     * Checks if the audio data is silent (contains only very small values).
     *
     * @param audioData an array of double values representing the audio signal.
     * @param threshold the threshold below which the signal is considered silent.
     * @return true if the audio data is silent, false otherwise.
     */
    protected static boolean isSilent(double[] audioData, double threshold) {
        double rms = calcRMS(audioData);
        return rms < threshold;
    }

    /**
     * Applies parabolic interpolation to refine a peak index.
     * This method is used by pitch detection algorithms to improve the accuracy
     * of peak detection in various functions (NSDF, CMNDF, etc.).
     *
     * @param values    an array of double values representing the function to interpolate
     * @param peakIndex the index of the detected peak in the values array
     * @return the refined peak index as a double, adjusted using parabolic interpolation
     */
    protected static double parabolicInterpolation(double[] values, int peakIndex) {
        if (peakIndex <= 0 || peakIndex >= values.length - 1) {
            return peakIndex;
        }

        double x0 = values[peakIndex - 1];
        double x1 = values[peakIndex];
        double x2 = values[peakIndex + 1];

        // Calculate the adjustment using parabolic interpolation
        double denominator = x0 - 2 * x1 + x2;

        // Avoid division by zero or very small values
        if (Math.abs(denominator) < 1e-10) {
            return peakIndex;
        }

        double adjustment = 0.5 * (x0 - x2) / denominator;

        // Limit the adjustment to a reasonable range to avoid extreme values
        if (Math.abs(adjustment) > 1) {
            adjustment = 0;
        }

        return peakIndex + adjustment;
    }

    /**
     * Represents the result of a pitch detection operation.
     * This class stores the detected pitch frequency in Hz
     * and a confidence score indicating the reliability of the detection.
     */
    public record PitchDetectionResult(double pitch, double confidence) {
    }
}
