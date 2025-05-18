package de.schliweb.bluesharpbendingapp.model.microphone;
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

import de.schliweb.bluesharpbendingapp.utils.LoggingContext;
import de.schliweb.bluesharpbendingapp.utils.LoggingUtils;

/**
 * AbstractMicrophone provides a partial implementation of the Microphone interface,
 * with functionality for managing algorithm and confidence configurations. It serves
 * as a base class for concrete microphone implementations.
 * <p>
 * This class defines default behavior for handling supported algorithms and confidence levels,
 * while leaving remaining microphone-specific behavior to be implemented by subclasses.
 */
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
     * Represents the confidence level used by the microphone specifically for chord detection.
     * <p>
     * This variable holds a numeric value between 0.0 and 1.0, where higher values indicate
     * greater confidence in the results of the chord detection algorithm. The default value
     * is set to 0.8, which is slightly lower than the single note confidence to account for
     * the increased complexity of chord detection.
     * <p>
     * The chord confidence level can be adjusted dynamically based on the index of available
     * confidence levels defined in the system or application. It influences the internal processing
     * and validation mechanisms of the microphone's chord detection operations.
     */
    protected double chordConfidence = 0.8;

    /**
     * Indicates whether chord detection is enabled for the microphone.
     * <p>
     * When set to true, the microphone will perform chord detection during audio processing.
     * When set to false, chord detection will be skipped to improve performance.
     * <p>
     * The default value is false, meaning chord detection is disabled by default.
     */
    protected boolean chordDetectionEnabled = false;
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

    /**
     * Retrieves a list of chord confidence levels supported by the microphone.
     * <p>
     * Chord confidence levels are used specifically for chord detection and may have
     * different default values than regular confidence levels. The default chord confidence
     * is 0.8, which is slightly lower than the default regular confidence (0.95) to account
     * for the increased complexity of chord detection.
     *
     * @return an array of strings, where each string represents a supported chord confidence level
     */
    public static String[] getSupportedChordConfidences() {
        return new String[]{"0.8", "0.75", "0.7", "0.65", "0.6", "0.55", "0.5", "0.45", "0.4", "0.35", "0.3", "0.25", "0.2", "0.15", "0.1"};
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
        LoggingContext.setComponent("AbstractMicrophone");

        try {
            confidence = Double.parseDouble(getSupportedConfidences()[confidenceIndex]);
            LoggingUtils.logDebug("Set confidence to: " + confidence);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException exception) {
            LoggingUtils.logError("Failed to set confidence", exception);
        }
    }

    @Override
    public String getChordConfidence() {
        return Double.toString(chordConfidence);
    }

    @Override
    public void setChordConfidence(int confidenceIndex) {
        LoggingContext.setComponent("AbstractMicrophone");

        try {
            chordConfidence = Double.parseDouble(getSupportedChordConfidences()[confidenceIndex]);
            LoggingUtils.logDebug("Set chord confidence to: " + chordConfidence);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException exception) {
            LoggingUtils.logError("Failed to set chord confidence", exception);
        }
    }

    @Override
    public boolean isChordDetectionEnabled() {
        return chordDetectionEnabled;
    }

    @Override
    public void setChordDetectionEnabled(boolean enabled) {
        LoggingContext.setComponent("AbstractMicrophone");
        this.chordDetectionEnabled = enabled;
        LoggingUtils.logDebug("Chord detection " + (enabled ? "enabled" : "disabled"));
    }

}
