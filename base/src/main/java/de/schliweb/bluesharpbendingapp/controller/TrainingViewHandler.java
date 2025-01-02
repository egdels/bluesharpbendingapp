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
 * Interface defining methods for handling the training view and associated actions.
 * <p>
 * This interface provides functionality such as initializing training and precision lists,
 * handling selections, and managing the start and stop of training processes. Implementers
 * of this interface are expected to define how these operations interact with the application
 * logic and update the view accordingly.
 */
public interface TrainingViewHandler {

    /**
     * Initializes the training list with the necessary data.
     * <p>
     * This method is responsible for preparing the training list that will
     * be displayed or utilized within the application. It ensures that the list
     * is populated and ready for further operations such as selection or modification.
     * Implementers are expected to define the specific logic for populating
     * and initializing the training list based on the application's requirements.
     */
    void initTrainingList();

    /**
     * Initializes the precision list with the necessary data.
     * <p>
     * This method is responsible for preparing the precision list to be displayed or utilized
     * within the application. It ensures that the list is accurately populated and ready for
     * operations such as selection or further processing. Implementers are expected to define
     * the specific logic for populating and initializing the precision list based on the
     * application's requirements.
     */
    void initPrecisionList();

    /**
     * Handles the selection event for a training item.
     * <p>
     * This method is invoked when a user interacts with the training list and selects
     * a specific training item. The selected index corresponds to the position of the
     * chosen item in the training list. Implementers should define the logic to handle
     * the selection, such as updating the view or performing an action based on the selection.
     *
     * @param selectedIndex the index of the selected training item in the list
     */
    void handleTrainingSelection(int selectedIndex);

    /**
     * Initializes the training container.
     * <p>
     * This method is responsible for setting up and configuring the training container,
     * ensuring it is ready for use within the application. The training container typically
     * serves as the primary structure for managing training-related components or data.
     * <p>
     * Implementers should define the specific logic required to properly initialize and
     * prepare the container based on the application's requirements, such as setting up
     * dependencies, views, or any necessary state.
     */
    void initTrainingContainer();

    /**
     * Handles the start of a training process.
     * <p>
     * This method is invoked to initiate the training process within the application.
     * Implementers should define the logic necessary to prepare and trigger any required
     * operations or state changes associated with the beginning of a training session.
     * This may include setting up resources, notifying relevant components, or updating
     * the application view as needed.
     */
    void handleTrainingStart();

    /**
     * Handles the stopping of a training process.
     * <p>
     * This method is invoked to terminate an ongoing training session within the application.
     * Implementers should define the logic required to gracefully stop the training process,
     * release any resources associated with the session, and update the application state
     * or view as necessary to reflect the termination of training.
     */
    void handleTrainingStop();

    /**
     * Handles the selection event for a precision item.
     * <p>
     * This method is invoked when a user interacts with the precision list and selects
     * a specific precision item. The selected index corresponds to the position of the
     * chosen item in the precision list. Implementers should define the logic to handle
     * the selection, such as updating the view or performing an action based on the selection.
     *
     * @param selectedIndex the index of the selected precision item in the list
     */
    void handlePrecisionSelection(int selectedIndex);
}
