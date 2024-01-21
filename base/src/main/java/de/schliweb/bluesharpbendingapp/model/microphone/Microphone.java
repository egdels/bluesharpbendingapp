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
 * The interface Microphone.
 */
public interface Microphone {

    /**
     * Close.
     */
    void close();

    /**
     * Gets algorithm.
     *
     * @return the algorithm
     */
    String getAlgorithm();

    /**
     * Sets algorithm.
     *
     * @param storedAlgorithmIndex the stored algorithm index
     */
    void setAlgorithm(int storedAlgorithmIndex);

    /**
     * Gets name.
     *
     * @return the name
     */
    String getName();

    /**
     * Sets name.
     *
     * @param storedMicrophoneIndex the stored microphone index
     */
    void setName(int storedMicrophoneIndex);

    /**
     * Get supported algorithms string [ ].
     *
     * @return the string [ ]
     */
    String[] getSupportedAlgorithms();

    /**
     * Get supported microphones string [ ].
     *
     * @return the string [ ]
     */
    String[] getSupportedMicrophones();

    /**
     * Open.
     */
    void open();

    /**
     * Sets microphone handler.
     *
     * @param handler the handler
     */
    void setMicrophoneHandler(MicrophoneHandler handler);

}
