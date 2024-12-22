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
 * The interface Training view.
 */
public interface TrainingView {

    /**
     * Sets trainings.
     *
     * @param trainings the trainings
     */
    void setTrainings(String[] trainings);

    /**
     * Sets precisions.
     *
     * @param precisions the precisions
     */
    void setPrecisions(String[] precisions);

    /**
     * Sets selected training.
     *
     * @param selectedTrainingIndex the selected training index
     */
    void setSelectedTraining(int selectedTrainingIndex);

    /**
     * Sets selected precision.
     *
     * @param selectedPrecisionIndex the selected precision index
     */
    void setSelectedPrecision(int selectedPrecisionIndex);

    /**
     * Gets actual harp view element.
     *
     * @return the actual harp view element
     */
    HarpViewNoteElement getActualHarpViewElement();

    /**
     * Init training container.
     *
     * @param trainingContainer the training container
     */
    void initTrainingContainer(TrainingContainer trainingContainer);

    /**
     * Toggle button.
     */
    void toggleButton();
}
