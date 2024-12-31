package de.schliweb.bluesharpbendingapp.model.training;
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
 * The Training interface represents a blueprint for training functionality
 * which includes managing notes, controlling the state, and tracking the progress.
 * This can be implemented to define specific types of trainings.
 */
public interface Training {

    /**
     * Retrieves the notes associated with the training.
     *
     * @return an array of strings representing the notes for the training
     */
    String[] getNotes();

    /**
     * Retrieves the name of the training.
     *
     * @return a string representing the name of the training
     */
    String getTrainingName();

    /**
     * Retrieves the actual note currently being processed or focused on during the training.
     *
     * @return a string representing the actual note
     */
    String getActualNote();

    /**
     * Retrieves the previous note in the training sequence.
     *
     * @return a string representing the previous note, or null if no previous note exists
     */
    String getPreviousNote();

    /**
     * Retrieves the next note in the training sequence.
     *
     * @return a string representing the next note in the sequence, or null if no further
     *         notes are available.
     */
    String getNextNote();

    /**
     * Advances the training to the next note in the sequence and returns it.
     * This method typically updates the state of the training to point to the next note.
     *
     * @return a string representing the next note in the sequence, or null if no further
     *         notes are available.
     */
    String nextNote();

    /**
     * Determines whether a note with the given frequency is currently active.
     *
     * @param frequency the frequency of the note to check
     * @return true if the note with the specified frequency is active, false otherwise
     */
    boolean isNoteActive(double frequency);

    /**
     * Indicates whether the training process is currently running.
     *
     * @return true if the training is running, false otherwise
     */
    boolean isRunning();

    /**
     * Starts the training process. This method transitions the training
     * into a running state and begins processing the sequence of training events.
     * Implementations may use this method to set up initial conditions,
     * reset relevant states, or trigger the first event in the sequence.
     */
    void start();

    /**
     * Stops the training process. This method transitions the training
     * out of a running state and halts any ongoing processing of training
     * events or sequence of notes. Implementations may use this method
     * to reset relevant states, release resources, or terminate active components.
     */
    void stop();

    /**
     * Retrieves the progress of the training process as an integer value.
     * The progress may represent a percentage or any other unit indicating
     * how much of the training has been completed.
     *
     * @return an integer representing the progress of the training
     */
    int getProgress();


    /**
     * Indicates whether the training has been completed.
     *
     * @return true if the training is completed, false otherwise
     */
    boolean isCompleted();

    /**
     * Marks the completion of a successful operation or task within the training process.
     *
     * This method can be used to signify that a specific milestone, step, or goal
     * within the training sequence has been successfully achieved. It may trigger
     * actions such as updating internal states, logging success events, or
     * notifying listeners, if applicable.
     */
    void success();
}
