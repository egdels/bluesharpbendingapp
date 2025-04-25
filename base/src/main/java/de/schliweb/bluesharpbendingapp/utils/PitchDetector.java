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
public class PitchDetector {

    /**
     * A constant representing the absence of a detected pitch.
     * When a pitch detection method fails to identify a fundamental frequency within
     * the audio signal, this value is returned to indicate no pitch was detected.
     */
    public static final double NO_DETECTED_PITCH = -1;

    /**
     * The default minimum frequency that can be detected (in Hz).
     */
    protected static final double DEFAULT_MIN_FREQUENCY = 80.0;

    /**
     * The default maximum frequency that can be detected (in Hz).
     */
    protected static final double DEFAULT_MAX_FREQUENCY = 4835.0;

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


    protected PitchDetector () {}

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
     * Sets the minimum frequency that can be detected by the pitch detectors.
     * This method updates the minimum frequency for all available pitch detection algorithms.
     *
     * @param harmonicaMinFrequency the minimum frequency in Hz to be set for pitch detection.
     */
    public static void setMinFrequency(double harmonicaMinFrequency) {
        MPMPitchDetector.setMinFrequency(harmonicaMinFrequency);
        YINPitchDetector.setMinFrequency(harmonicaMinFrequency);
    }

    /**
     * Sets the maximum frequency that can be detected by the pitch detectors.
     * This method updates the maximum frequency for all available pitch detection algorithms.
     *
     * @param harmonicaMaxFrequency the maximum frequency in Hz to be set for pitch detection
     */
    public static void setMaxFrequency(double harmonicaMaxFrequency) {
        MPMPitchDetector.setMaxFrequency(harmonicaMaxFrequency);
        YINPitchDetector.setMaxFrequency(harmonicaMaxFrequency);
    }

    /**
     * Represents the result of a pitch detection operation.
     * This class stores the detected pitch frequency in Hz
     * and a confidence score indicating the reliability of the detection.
     */
    public record PitchDetectionResult(double pitch, double confidence) {
    }
}
