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
 * An interface for handling note settings-related functionalities within the application.
 * This interface facilitates the initialization and management of concert pitch settings
 * for enhancing interaction in the note settings view.
 */
public interface NoteSettingsViewHandler {

    /**
     * Handles the selection of a concert pitch based on the provided index.
     * This method is typically invoked to update the application state or UI
     * when a user selects a specific concert pitch setting.
     *
     * @param pitchIndex the index of the selected concert pitch in the list of available pitches
     */
    void handleConcertPitchSelection(int pitchIndex);

    /**
     * Initializes the list of available concert pitch settings.
     * This method is responsible for setting up the options that users can select
     * from in relation to concert pitch adjustments. It is typically invoked during
     * the preparation or setup phase of the note settings view.
     */
    void initConcertPitchList();

}
