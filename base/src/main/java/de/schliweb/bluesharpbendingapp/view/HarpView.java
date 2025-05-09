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

import de.schliweb.bluesharpbendingapp.controller.NoteContainer;

/**
 * The HarpView interface provides methods to interact with and manage
 * the visual elements of a harp view.
 */
public interface HarpView {

    /**
     * Retrieves the harp view note element associated with the specified channel and note.
     *
     * @param channel the channel number corresponding to the harp element
     * @param note    the note number corresponding to the harp element
     * @return the harp view note element associated with the given channel and note
     */
    HarpViewNoteElement getHarpViewElement(int channel, int note);

    /**
     * Initializes the notes within the harp view using the provided array of NoteContainer objects.
     *
     * @param noteContainers an array of NoteContainer instances representing the notes to be initialized
     */
    void initNotes(NoteContainer[] noteContainers);

    /**
     * Updates the display of tuning and key information in the harp view.
     *
     * @param keyName   the name of the currently selected key
     * @param tuningName the name of the currently selected tuning
     */
    void updateTuningKeyInfo(String keyName, String tuningName);
}
