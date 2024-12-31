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
 * The RichterHarmonica class represents a harmonica following the Richter tuning system.
 * This specific tuning is defined by its unique arrangement of half-tone steps for
 * both inhaling and exhaling notes.
 * <p>The harmonica operates using a defined key frequency, and the tone arrangements
 * are modeled by two arrays, HALF_TONES_IN and HALF_TONES_OUT.</p>
 */
public class RichterHarmonica extends AbstractHarmonica {
    /**
     * Represents the sequence of half-tone intervals for inhaling notes
     * on a Richter-tuned harmonica. The array values correspond to the
     * cumulative number of half-tones from the base key note for each
     * harmonica channel when air is drawn in.
     */
    private static final int[] HALF_TONES_IN = {0, 2, 7, 11, 14, 17, 21, 23, 26, 29, 33};

    /**
     * Represents the sequence of half-tone intervals for exhaling (blowing) notes
     * on a Richter-tuned harmonica. The array values correspond to the
     * cumulative number of half-tones from the base key note for each
     * harmonica channel when air is blown out.
     */
    private static final int[] HALF_TONES_OUT = {0, 0, 4, 7, 12, 16, 19, 24, 28, 31, 36};

    /**
     * Constructs a new RichterHarmonica instance with the specified key frequency.
     *
     * @param keyFrequency the base frequency of the key this harmonica is tuned to, in hertz
     */
    public RichterHarmonica(double keyFrequency) {
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
        return TUNE.RICHTER.name();
    }
}
