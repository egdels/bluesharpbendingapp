package de.schliweb.bluesharpbendingapp.model.microphone;

import lombok.extern.slf4j.Slf4j;

/**
 * AbstractMicrophone provides a partial implementation of the Microphone interface,
 * with functionality for managing algorithm and confidence configurations. It serves
 * as a base class for concrete microphone implementations.
 * <p>
 * This class defines default behavior for handling supported algorithms and confidence levels,
 * while leaving remaining microphone-specific behavior to be implemented by subclasses.
 */
@Slf4j
public abstract class AbstractMicrophone implements Microphone {

    /**
     * Represents the confidence level used by the microphone for audio processing and analysis.
     * <p>
     * This variable holds a numeric value between 0.0 and 1.0, where higher values indicate
     * greater confidence in the results of the audio processing algorithm. The default value
     * is set to 0.95, providing a balance between accuracy and computational efficiency.
     * <p>
     * The confidence level can be adjusted dynamically based on the index of available
     * confidence levels defined in the system or application. It influences the internal processing
     * and validation mechanisms of the microphone's operations.
     */
    protected double confidence = 0.95;
    /**
     * Represents the algorithm used by the microphone for audio processing.
     * <p>
     * The default value is "YIN", which indicates the default algorithm for pitch detection.
     * The algorithm can be dynamically updated based on the indices of supported algorithms
     * retrieved from the system or application context.
     * <p>
     * This variable is private to ensure encapsulation, and modifications should be made
     * using the setter methods provided by the AbstractMicrophone or its subclasses.
     */
    private String algorithm = "YIN"; // Default algorithm

    /**
     * Retrieves a list of supported algorithms for audio processing.
     * <p>
     * This method returns an array of strings, with each string representing
     * the name of an algorithm that can be used for audio analysis, such as
     * "YIN" or "MPM". These algorithms are predefined and represent the
     * available options for processing audio input in applications leveraging
     * this microphone implementation.
     *
     * @return an array of strings, where each string is the name of a supported algorithm
     */
    public static String[] getSupportedAlgorithms() {
        return new String[]{"YIN", "MPM"};
    }

    /**
     * Retrieves a list of confidence levels supported by the microphone.
     *
     * @return an array of strings, where each string represents a supported confidence level
     */
    public static String[] getSupportedConfidences() {
        return new String[]{"0.95", "0.9", "0.85", "0.8", "0.75", "0.7", "0.65", "0.6", "0.55", "0.5", "0.45", "0.4", "0.35", "0.3", "0.25", "0.2", "0.15", "0.1", "0.05"};
    }

    @Override
    public String getAlgorithm() {
        return algorithm;
    }

    @Override
    public void setAlgorithm(int index) {
        String[] algorithms = getSupportedAlgorithms();
        if (index >= 0 && index < algorithms.length) {
            algorithm = algorithms[index];
        }
    }

    @Override
    public String getConfidence() {
        return Double.toString(confidence);
    }

    @Override
    public void setConfidence(int confidenceIndex) {
        try {
            confidence = Double.parseDouble(getSupportedConfidences()[confidenceIndex]);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException exception) {
            log.error(exception.getMessage());
        }
    }

}
