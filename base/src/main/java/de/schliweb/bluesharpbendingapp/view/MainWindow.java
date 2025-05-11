package de.schliweb.bluesharpbendingapp.view;
/*
 * Copyright (c) 2023 Christian Kierdorf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
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
 * MainWindow serves as an interface for managing and interacting with different
 * views and components within the application. It provides methods to retrieve
 * specific views, check their active states, and perform actions such as opening
 * a component or view.
 */
public interface MainWindow {

    /**
     * Retrieves the harp settings view associated with the application.
     *
     * @return an instance of HarpSettingsView, which provides methods to manage
     * and interact with settings related to the harp.
     */
    HarpSettingsView getHarpSettingsView();


    /**
     * Retrieves the harp view associated with the application.
     *
     * @return an instance of HarpView, which provides methods to interact with
     * and manage the visual elements of the harp view.
     */
    HarpView getHarpView();

    /**
     * Retrieves the microphone settings view associated with the application.
     *
     * @return an instance of MicrophoneSettingsView, which provides methods to manage
     * and interact with settings related to the microphone.
     */
    MicrophoneSettingsView getMicrophoneSettingsView();

    /**
     * Determines if the harp settings view is currently active.
     *
     * @return true if the harp settings view is active; false otherwise
     */
    boolean isHarpSettingsViewActive();

    /**
     * Determines if the harp view is currently active.
     *
     * @return true if the harp view is active; false otherwise
     */
    boolean isHarpViewActive();

    /**
     * Determines if the microphone settings view is currently active.
     *
     * @return true if the microphone settings view is active; false otherwise
     */
    boolean isMicrophoneSettingsViewActive();

    /**
     * Determines if the training view is currently active.
     *
     * @return true if the training view is active; false otherwise
     */
    boolean isTrainingViewActive();

    /**
     * Opens the view or component associated with this method.
     * This action typically makes the view or component visible and interactive
     * within the application's user interface.
     */
    void open();

    /**
     * Determines if the note settings view is currently active.
     *
     * @return true if the note settings view is active; false otherwise
     */
    boolean isNoteSettingsViewActive();

    /**
     * Retrieves the note settings view associated with the application.
     *
     * @return an instance of NoteSettingsView, which provides methods to manage
     * and interact with settings related to musical notes.
     */
    NoteSettingsView getNoteSettingsView();

    /**
     * Retrieves the training view associated with the application.
     *
     * @return an instance of TrainingView, which provides methods to manage
     * and interact with the training-related components of the application.
     */
    TrainingView getTrainingView();

    /**
     * Retrieves the Android settings view associated with the application.
     *
     * @return an instance of AndroidSettingsView, which provides methods to manage
     * and interact with settings specific to the Android platform.
     */
    AndroidSettingsView getAndroidSettingsView();

    /**
     * Determines if the Android settings view is currently active.
     *
     * @return true if the Android settings view is active; false otherwise
     */
    boolean isAndroidSettingsViewActive();

}
