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
 * The HarpSettingsView interface defines the contract for managing and updating harp settings in the application.
 * It provides methods to set available keys and tunes, as well as select a specific key or tune.
 */
public interface HarpSettingsView {

    /**
     * Sets the available keys for the harp settings view.
     *
     * @param keys an array of strings representing the available keys
     */
    void setKeys(String[] keys);

    /**
     * Sets the selected key in the harp settings view.
     *
     * @param selectedKeyIndex the index of the selected key in the available keys
     */
    void setSelectedKey(int selectedKeyIndex);

    /**
     * Sets the selected tune in the harp settings view based on the specified index.
     *
     * @param selectedTuneIndex the index of the selected tune in the available tunes
     */
    void setSelectedTune(int selectedTuneIndex);

    /**
     * Sets the available tunes for the harp settings view.
     *
     * @param tunes an array of strings representing the available tunes
     */
    void setTunes(String[] tunes);

}
