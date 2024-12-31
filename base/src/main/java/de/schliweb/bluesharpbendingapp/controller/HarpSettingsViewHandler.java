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
 * This interface defines methods for handling and managing the harp settings view.
 * It provides functionality for selecting keys and tunings, as well as initializing
 * the key and tuning options available in the settings. Implementations of this interface
 * are responsible for managing user interactions with the harp settings and updating
 * the associated application state accordingly.
 */
public interface HarpSettingsViewHandler {
    /**
     * Handles the selection of a key in the harp settings.
     * This method is invoked to process the user's chosen key from a list of available keys.
     *
     * @param keyIndex the index of the selected key, used to identify and apply the chosen key
     */
    void handleKeySelection(int keyIndex);

    /**
     * Handles the selection of a tuning in the harp settings.
     * This method updates the application state based on the tuning option selected by the user.
     *
     * @param tuneIndex the index of the selected tuning option
     */
    void handleTuneSelection(int tuneIndex);

    /**
     * Initializes and prepares the list of available keys in the harp settings.
     * This method ensures that the key options are populated and ready for user interaction.
     */
    void initKeyList();

    /**
     * Initializes and populates the list of tuning options available in the harp settings.
     * Provides users with a list of tunings they can select and apply.
     */
    void initTuneList();
}
