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
 * The type Major chord arpeggio training.
 */
public class MajorChordArpeggioTraining extends AbstractTraining {

    /**
     * The constant HALF_TONES.
     */
    private static final int[] HALF_TONES = {2, 6, 9, 2, 6, 9, 11, 2, 6, 9, 12, 2, 6, 9, 11, 12, 14};

    /**
     * Instantiates a new Major chord arpeggio training.
     *
     * @param key the key
     */
    protected MajorChordArpeggioTraining(AbstractHarmonica.KEY key) {
        super(key);
    }

    @Override
    int[] getHalfTones() {
        return HALF_TONES;
    }

    @Override
    public String getTrainingName() {
        return TRAINING.MAJOR_CHORD_ARPEGGIO.name();
    }
}