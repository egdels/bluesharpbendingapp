package de.schliweb.bluesharpbendingapp.model.harmonica;
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
 * The AugmentedHarmonica class represents a type of harmonica tuned to the augmented scale.
 * It extends the functionality of the AbstractHarmonica class and provides specific
 * implementations for the notes' half-tone intervals for both in and out directions.
 * <p>
 * This class uses two static arrays to define half-tone intervals for the draw (HALF_TONES_IN)
 * and blow (HALF_TONES_OUT) operations on the harmonica.
 * <p>
 * The augmented scale is defined by its unique pattern of intervals, and this harmonica
 * is tuned specifically to conform to that scale.
 */
public class AugmentedHarmonica extends AbstractHarmonica {

    /**
     * Defines the half-tone intervals for the draw (inhale) notes of the augmented harmonica.
     * These values represent the pitch differences, in semitones, relative to the base frequency
     * of the harmonica's key for each draw channel.
     * <p>
     * The pattern provided aligns with the augmented scale, which is characterized by equal intervals
     * of three semitones between consecutive notes.
     */
    private static final int[] HALF_TONES_IN = {0, 3, 7, 11, 15, 19, 23, 27, 31, 35, 39};

    /**
     * Defines the half-tone intervals for the blow (exhale) notes of the augmented harmonica.
     * These values represent the pitch differences, in semitones, relative to the base frequency
     * of the harmonica's key for each blow channel.
     * <p>
     * The pattern provided aligns with the augmented scale and starts with a doubling of the base
     * frequency at the initial two positions, followed by intervals increasing by four semitones
     * from the third position onward.
     */
    private static final int[] HALF_TONES_OUT = {0, 0, 4, 8, 12, 16, 20, 24, 28, 32, 36};


    /**
     * Constructs an instance of AugmentedHarmonica with the specified key frequency.
     *
     * @param keyFrequency the base frequency of the musical key that the harmonica is tuned to
     */
    public AugmentedHarmonica(double keyFrequency) {
        super(keyFrequency);
    }

    @Override
    int[] getHalfTonesIn() {
        return HALF_TONES_IN;
    }

    @Override
    int[] getHalfTonesOut() {
        return HALF_TONES_OUT;
    }

    @Override
    public String getTuneName() {
        return TUNE.AUGMENTED.name();
    }
}
