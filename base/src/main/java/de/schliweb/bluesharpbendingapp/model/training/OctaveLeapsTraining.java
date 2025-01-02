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
 * The OctaveLeapsTraining class is a specific implementation of the {@link AbstractTraining} class,
 * designed to train users on octave leaps exercises using a harmonica key. This class focuses
 * on octave intervals and provides relevant functionalities tailored for this type of training.
 */
public class OctaveLeapsTraining extends AbstractTraining {

    /**
     * A constant array representing the sequence of half-tone steps used in the
     * octave leaps training exercise. Each element denotes a half-tone offset
     * relative to a base note in the harmonica's scale.
     * <p>
     * This array defines the structure of the musical intervals utilized for
     * training purposes in the {@link OctaveLeapsTraining} class. The intervals
     * represented include various patterns designed to simulate octave leaps
     * on a harmonica.
     */
    private static final int[] HALF_TONES = {0, 12, 24, 36, 24, 12, 2, 14, 26, 14, 4, 16, 28, 16, 5, 17, 29, 17, 7, 19, 31, 19, 9, 21, 33, 21, 11, 23, 35, 23};

    /**
     * Constructs an instance of OctaveLeapsTraining with the specified harmonica key.
     * This initializer sets up the training to target octave leaps utilities
     * and prepares internal configurations based on the given key.
     *
     * @param key The harmonica key used for initializing the training session. This key
     *            determines the pitch and tonal structure for the octave leaps exercises.
     */
    protected OctaveLeapsTraining(AbstractHarmonica.KEY key) {
        super(key);
    }

    @Override
    int[] getHalfTones() {
        return HALF_TONES;
    }

    @Override
    public String getTrainingName() {
        return TRAINING.OCTAVE_LEAPS.name();
    }
}
