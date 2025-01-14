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
 * The NoteSettingsView interface defines methods for managing the note-related settings
 * in the application. It encapsulates functionality to set and update concert pitches.
 */
public interface NoteSettingsView {

    /**
     * Sets the array of concert pitches. The provided array contains
     * string representations of the available concert pitches.
     *
     * @param pitches an array of strings representing the concert pitches
     */
    void setConcertPitches(String[] pitches);

    /**
     * Sets the selected concert pitch by its index in the list of available concert pitches.
     *
     * @param selectedConcertPitchIndex the index of the concert pitch to be selected
     */
    void setSelectedConcertPitch(int selectedConcertPitchIndex);

}
