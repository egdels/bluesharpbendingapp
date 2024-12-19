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

import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;
import de.schliweb.bluesharpbendingapp.model.training.AbstractTraining;
import de.schliweb.bluesharpbendingapp.model.training.Training;
import de.schliweb.bluesharpbendingapp.utils.NoteUtils;
import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;
import de.schliweb.bluesharpbendingapp.view.TrainingView;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The type Training container.
 */
public class TrainingContainer implements Runnable {

    /**
     * The Training.
     */
    private final Training training;

    /**
     * The View.
     */
    private final TrainingView view;
    /**
     * The Element.
     */
    private final HarpViewNoteElement element;
    /**
     * The Exec.
     */
    private final ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
    /**
     * The To be cleared.
     */
    private final AtomicBoolean toBeCleared = new AtomicBoolean(false);
    /**
     * The To next note.
     */
    private final AtomicBoolean toNextNote = new AtomicBoolean(false);
    /**
     * The Frequency to handle.
     */
    private double frequencyToHandle;

    /**
     * Instantiates a new Training container.
     *
     * @param training     the training
     * @param trainingView the training view
     */
    public TrainingContainer(Training training, TrainingView trainingView) {
        this.training = training;
        this.view = trainingView;
        this.element = view.getActualHarpViewElement();
    }

    @Override
    public void run() {
        if (training.isRunning() && training.isNoteActive(frequencyToHandle)) {
            double cents = NoteUtils.getCents(NoteLookup.getNoteFrequency(training.getActualNote()), frequencyToHandle);
            element.update(cents);
            toBeCleared.set(true);
            if (Math.abs(cents) < AbstractTraining.getPrecision())
                toNextNote.set(true);
        } else {
            if (toBeCleared.get()) {
                exec.schedule(() -> {
                    element.clear();
                    toBeCleared.set(false);
                }, 100, TimeUnit.MILLISECONDS);
            }
            if (toNextNote.get()) {
                exec.schedule(() -> {
                    training.nextNote();
                    toNextNote.set(false);
                    view.initTrainingContainer(this);
                }, 100, TimeUnit.MILLISECONDS);
            }
        }
    }

    /**
     * Sets frequency to handle.
     *
     * @param frequencyToHandle the frequency to handle
     */
    public void setFrequencyToHandle(double frequencyToHandle) {
        this.frequencyToHandle = frequencyToHandle;
    }

    /**
     * Gets actual note name.
     *
     * @return the actual note name
     */
    public String getActualNoteName() {
        return training.getActualNote();
    }

    /**
     * Gets progress.
     *
     * @return the progress
     */
    public int getProgress() {
        return training.getProgress();
    }
}
