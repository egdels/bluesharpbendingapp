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

import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;

/**
 * The DummyTraining class is a specific implementation of the AbstractTraining class.
 * It represents a placeholder or basic training mode that provides a predefined set
 * of half-tone intervals for training purposes. This class can be used for testing
 * or as a default training mode.
 */
public class DummyTraining extends AbstractTraining {

    /**
     * A static array defining predefined half-tone intervals.
     * This array is utilized to represent a sequence of half-tone steps,
     * commonly used in musical training or simulation scenarios.
     * In the context of the DummyTraining class specifically, it highlights
     * the intervals as {0, 1, 2}.
     */
    private static final int[] HALF_TONES = {0, 1, 2};

    /**
     * Constructs an instance of DummyTraining using the specified key.
     *
     * @param key the musical key to be used for this training. It defines the
     *            specific tonal context in which the training operates.
     */
    protected DummyTraining(AbstractHarmonica.KEY key) {
        super(key);
    }

    @Override
    int[] getHalfTones() {
        return HALF_TONES;
    }

    @Override
    public String getTrainingName() {
        return TRAINING.DUMMY.name();
    }
}
