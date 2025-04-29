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

/**
 * This interface represents a Microphone that can be used for audio input
 * in applications. It allows configuration of various properties such as
 * the microphone name, audio processing algorithm, and confidence level,
 * while also providing methods for retrieving supported options and
 * managing audio resources.
 */
public interface Microphone {

    /**
     * Closes the microphone resource to release any active audio input streams.
     * This method is used to ensure proper cleanup of the microphone when it is
     * no longer needed or is about to be reconfigured.
     */
    void close();

    /**
     * Retrieves the currently selected algorithm of the microphone.
     *
     * @return the name of the active algorithm as a String
     */
    String getAlgorithm();

    /**
     * Sets the algorithm to be used by the microphone based on the given index.
     * The index corresponds to a pre-defined list of algorithms supported by the microphone.
     *
     * @param storedAlgorithmIndex the index of the algorithm to be set
     */
    void setAlgorithm(int storedAlgorithmIndex);

    /**
     * Retrieves the name of the microphone.
     *
     * @return the name of the microphone as a String
     */
    String getName();

    /**
     * Sets the name of the microphone based on the given index.
     * The index corresponds to a pre-defined list of supported microphones.
     *
     * @param storedMicrophoneIndex the index of the microphone to be set
     */
    void setName(int storedMicrophoneIndex);

    /**
     * Retrieves a list of microphones supported by the system or application.
     *
     * @return an array of strings, where each string represents the name of a supported microphone
     */
    String[] getSupportedMicrophones();

    /**
     * Sets the confidence level for the microphone based on the given index.
     * The index corresponds to a pre-defined list of confidence levels
     * supported by the microphone.
     *
     * @param confidenceIndex the index of the confidence level to be set
     */
    void setConfidence(int confidenceIndex);

    /**
     * Opens the microphone for audio input.
     * This method is used to initialize the microphone and allocate necessary
     * resources for capturing audio data. It should be called after configuring
     * any specific microphone or algorithm settings, to activate the microphone
     * for use in the application.
     */
    void open();

    /**
     * Sets the MicrophoneHandler for the microphone. The handler is responsible
     * for processing audio data, including handling frequency and volume changes.
     *
     * @param handler the MicrophoneHandler instance that will process audio data
     */
    void setMicrophoneHandler(MicrophoneHandler handler);

    /**
     * Retrieves the current confidence level set for the microphone.
     *
     * @return the currently selected confidence level as a String
     */
    String getConfidence();
}
