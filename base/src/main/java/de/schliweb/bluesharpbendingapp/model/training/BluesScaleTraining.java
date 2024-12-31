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
 * The BluesScaleTraining class is a specific implementation of the AbstractTraining class,
 * designed to provide training exercises for the blues scale on a harmonica. This class
 * defines the half-tone intervals corresponding to the blues scale and provides the
 * training name for identification.
 */
public class BluesScaleTraining extends AbstractTraining {

    /**
     * Represents an array of half-tone intervals used to define the blues scale.
     * These intervals specify the pitch differences between consecutive notes
     * in the scale as integer values. The array is utilized in training exercises
     * for the blues scale on a harmonica.
     */
    private static final int[] HALF_TONES = {0, 1, 2, 5, 7, 10, 12, 13, 14, 17, 19, 20, 21, 24, 26, 29, 31};

    /**
     * Constructs an instance of BluesScaleTraining using the specified key.
     * The key determines the root note for the blues scale training exercises
     * on the harmonica.
     *
     * @param key The key of the harmonica used for the training. This specifies
     *            the root note of the blues scale exercises.
     */
    protected BluesScaleTraining(AbstractHarmonica.KEY key) {
        super(key);
    }

    @Override
    int[] getHalfTones() {
        return HALF_TONES;
    }

    @Override
    public String getTrainingName() {
        return TRAINING.BLUES_SCALE.name();
    }
}
