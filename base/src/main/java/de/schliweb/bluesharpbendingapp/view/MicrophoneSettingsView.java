package de.schliweb.bluesharpbendingapp.view;
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
 * The MicrophoneSettingsView interface provides methods for managing and configuring
 * settings related to microphones. This includes setting algorithms, microphones,
 * frequencies, volumes, and the indices of selected microphones and algorithms.
 */
public interface MicrophoneSettingsView {

    /**
     * Sets the available algorithms to be used for processing in the application.
     *
     * @param algorithms an array of strings representing the names of the algorithms to be set
     */
    void setAlgorithms(String[] algorithms);

    /**
     * Sets the frequency value to be used in the microphone settings.
     *
     * @param frequency the frequency value to be set, represented as a double
     */
    void setFrequency(double frequency);

    /**
     * Sets the index of the selected confidence level from a list of available confidence levels.
     *
     * @param confidenceIndex the index of the confidence level to be selected, represented as an integer
     */
    void setSelectedConfidence(int confidenceIndex);

    /**
     * Sets the available confidence levels to be used in the microphone settings.
     *
     * @param confidences an array of strings representing the confidence levels to be set
     */
    void setConfidences(String[] confidences);

    /**
     * Sets the list of available microphones to be used in the application.
     *
     * @param microphones an array of strings representing the names or identifiers of the microphones
     */
    void setMicrophones(String[] microphones);

    /**
     * Sets the index of the selected algorithm from a list of available algorithms.
     *
     * @param selectedAlgorithmIndex the index of the algorithm to be selected,
     *                               represented as an integer
     */
    void setSelectedAlgorithm(int selectedAlgorithmIndex);

    /**
     * Sets the index of the selected microphone from a list of available microphones.
     *
     * @param selectedMicrophoneIndex the index of the microphone to be selected,
     *                                represented as an integer
     */
    void setSelectedMicrophone(int selectedMicrophoneIndex);

    /**
     * Sets the volume level for the microphone settings.
     *
     * @param volume the volume level to be set, represented as a double
     */
    void setVolume(double volume);

    /**
     * Sets the index of the selected chord confidence level from a list of available confidence levels.
     * This confidence level is used specifically for chord detection.
     *
     * @param chordConfidenceIndex the index of the chord confidence level to be selected, represented as an integer
     */
    void setSelectedChordConfidence(int chordConfidenceIndex);

    /**
     * Sets the available chord confidence levels to be used in the microphone settings.
     * These confidence levels are used specifically for chord detection.
     *
     * @param chordConfidences an array of strings representing the chord confidence levels to be set
     */
    void setChordConfidences(String[] chordConfidences);

}
