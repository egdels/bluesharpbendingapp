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
 * The MajorScaleTraining class provides specific functionalities for training
 * with the major scale in the context of harmonica practice. This class
 * extends the {@link AbstractTraining} class to leverage common training
 * behaviors and implements methods tailored to the major scale.
 * <p>
 * The major scale is represented by predefined half-tone intervals stored
 * in the {@code HALF_TONES} constant array. This ensures consistent and
 * accurate generation of notes for training purposes.
 */
public class MajorScaleTraining extends AbstractTraining {

    /**
     * Represents an array of half-tone intervals for the major scale used in the
     * {@code MajorScaleTraining} class. These intervals define the semitone
     * differences between consecutive notes in the scale, ensuring accurate
     * note generation for training purposes.
     * <p>
     * This array includes repeated octaves and additional intervals to support
     * transposition and advanced usage in harmonica practice. The values are
     * structured such that it spans multiple octaves:
     * <p>
     * - 0: Root note (no offset)
     * - 2: Whole step
     * - 4: Two whole steps
     * - 5: Minor third
     * - 7: Perfect fourth
     * - 9: Perfect fifth
     * - 11: Major sixth, etc.
     * <p>
     * The intervals continue to repeat for a total to cover multiple octaves
     * for extended scales.
     */
    private static final int[] HALF_TONES = {0, 2, 4, 5, 7, 9, 11, 12, 14, 16, 17, 19, 21, 23, 24, 26, 28, 29, 31, 33, 35, 36};

    /**
     * Constructs a new instance of {@code MajorScaleTraining} using the specified key.
     * This class is specifically tailored to provide training functionality for
     * the major scale in harmonica practice.
     *
     * @param key the musical key for which the major scale training will be performed.
     *            This determines the root note and tonal structure for the scale.
     */
    protected MajorScaleTraining(AbstractHarmonica.KEY key) {
        super(key);
    }


    @Override
    int[] getHalfTones() {
        return HALF_TONES;
    }

    @Override
    public String getTrainingName() {
        return TRAINING.MAJOR_SCALE.name();
    }
}
