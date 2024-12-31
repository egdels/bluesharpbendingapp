package de.schliweb.bluesharpbendingapp.controller;
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
 * Interface responsible for handling interactions and settings related to the microphone
 * and algorithm configuration in the application. This interface provides methods
 * to initialize lists of available algorithms and microphones, as well as handlers
 * for selecting specific algorithms or microphones.
 */
public interface MicrophoneSettingsViewHandler {
    /**
     * Handles the selection of a specific algorithm based on the provided index.
     *
     * @param algorithmIndex the index of the algorithm to be selected
     */
    void handleAlgorithmSelection(int algorithmIndex);

    /**
     * Handles the selection of a specific microphone based on the provided index.
     *
     * @param microphoneIndex the index of the microphone to be selected
     */
    void handleMicrophoneSelection(int microphoneIndex);

    /**
     * Initializes the list of available algorithms. This method is responsible for
     * setting up and preparing the list of algorithms that can be used or selected
     * in the application.
     */
    void initAlgorithmList();

    /**
     * Initializes the list of available microphones. This method is responsible for
     * gathering, setting up, and preparing the microphone options that can be used or
     * selected in the application.
     */
    void initMicrophoneList();
}
