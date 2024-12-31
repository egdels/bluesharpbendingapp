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

import de.schliweb.bluesharpbendingapp.controller.TrainingContainer;

/**
 * Represents the view interface for the training component in the application.
 * This interface defines methods for managing the display, interaction, and
 * updates between the training sessions and the user interface.
 */
public interface TrainingView {

    /**
     * Updates the list of available training sessions to be displayed or managed by the view.
     *
     * @param trainings an array of training session names or identifiers
     */
    void setTrainings(String[] trainings);

    /**
     * Updates the list of available precisions to be displayed or managed by the view.
     *
     * @param precisions an array of precision names or identifiers
     */
    void setPrecisions(String[] precisions);

    /**
     * Updates the selected training session by specifying its index in the list
     * of available training sessions.
     *
     * @param selectedTrainingIndex the index of the selected training session
     */
    void setSelectedTraining(int selectedTrainingIndex);

    /**
     * Updates the selected precision by specifying its index in the list
     * of available precisions.
     *
     * @param selectedPrecisionIndex the index of the selected precision
     */
    void setSelectedPrecision(int selectedPrecisionIndex);

    /**
     * Retrieves the current harp view note element that is active or being interacted with.
     *
     * @return the active HarpViewNoteElement instance currently in use
     */
    HarpViewNoteElement getActualHarpViewElement();

    /**
     * Initializes the training container in the view. This method is responsible
     * for setting up or resetting the training-related components and ensuring
     * synchronization between the view and the training container.
     *
     * @param trainingContainer the TrainingContainer instance to be initialized within the view
     */
    void initTrainingContainer(TrainingContainer trainingContainer);

    /**
     * Toggles the state of a button in the view. This method is responsible for
     * switching the button between its active and inactive states, enabling or
     * disabling user interaction accordingly.
     */
    void toggleButton();
}
