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
 * MajorPentatonicScaleTraining is a concrete implementation of {@link AbstractTraining}
 * for training exercises based on the major pentatonic scale.
 * <p>
 * This class provides a specific set of half-tone intervals corresponding to
 * the notes in the major pentatonic scale and defines the behavior for retrieving
 * the training name and note sequence.
 */
public class MajorPentatonicScaleTraining extends AbstractTraining {

    /**
     * Represents the sequence of half-tone intervals corresponding to the
     * major pentatonic scale for training exercises.
     * <p>
     * This array defines the notes in the scale as a series of half-step
     * intervals, where each value indicates the interval in semitones from
     * the root note.
     * <p>
     * The array is used to generate the correct note sequence for training
     * exercises in the {@link MajorPentatonicScaleTraining} implementation.
     * <p>
     * These intervals adhere to the structure of the major pentatonic scale
     * and are utilized in methods such as {@code getHalfTones} to provide
     * the functional behavior for the training.
     */
    private static final int[] HALF_TONES = {2, 4, 7, 9, 11, 14, 16, 19, 21, 23, 26, 28, 31, 33, 35};


    /**
     * Constructs a MajorPentatonicScaleTraining instance for practicing the major
     * pentatonic scale based on the specified key.
     *
     * @param key the key of the harmonica for which the training exercises
     *            are based. This determines the root note and scale structure
     *            for the training session.
     */
    protected MajorPentatonicScaleTraining(AbstractHarmonica.KEY key) {
        super(key);
    }

    @Override
    int[] getHalfTones() {
        return HALF_TONES;
    }

    @Override
    public String getTrainingName() {
        return TRAINING.MAJOR_PENTATONIC_SCALE.name();
    }
}
