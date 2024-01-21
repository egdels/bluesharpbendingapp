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

import de.schliweb.bluesharpbendingapp.controller.HarpSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.controller.HarpViewHandler;
import de.schliweb.bluesharpbendingapp.controller.MicrophoneSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.controller.NoteSettingsViewHandler;

/**
 * The interface Main window.
 */
public interface MainWindow {

    /**
     * Gets harp settings view.
     *
     * @return the harp settings view
     */
    HarpSettingsView getHarpSettingsView();


    /**
     * Gets harp view.
     *
     * @return the harp view
     */
    HarpView getHarpView();

    /**
     * Gets microphone settings view.
     *
     * @return the microphone settings view
     */
    MicrophoneSettingsView getMicrophoneSettingsView();

    /**
     * Is harp settings view active boolean.
     *
     * @return the boolean
     */
    boolean isHarpSettingsViewActive();

    /**
     * Is harp view active boolean.
     *
     * @return the boolean
     */
    boolean isHarpViewActive();

    /**
     * Is microphone settings view active boolean.
     *
     * @return the boolean
     */
    boolean isMicrophoneSettingsViewActive();

    /**
     * Open.
     */
    void open();

    /**
     * Sets harp settings view handler.
     *
     * @param harpSettingsViewHandler the harp settings view handler
     */
    void setHarpSettingsViewHandler(HarpSettingsViewHandler harpSettingsViewHandler);

    /**
     * Sets harp view handler.
     *
     * @param harpViewHandler the harp view handler
     */
    void setHarpViewHandler(HarpViewHandler harpViewHandler);

    /**
     * Sets microphone settings view handler.
     *
     * @param microphoneSettingsViewHandler the microphone settings view handler
     */
    void setMicrophoneSettingsViewHandler(MicrophoneSettingsViewHandler microphoneSettingsViewHandler);

    /**
     * Is note settings view active boolean.
     *
     * @return the boolean
     */
    boolean isNoteSettingsViewActive();

    /**
     * Gets note settings view.
     *
     * @return the note settings view
     */
    NoteSettingsView getNoteSettingsView();

    /**
     * Sets note settings view handler.
     *
     * @param noteSettingsViewHandler the note settings view handler
     */
    void setNoteSettingsViewHandler(NoteSettingsViewHandler noteSettingsViewHandler);
}
