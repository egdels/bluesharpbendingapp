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
 * The type Paddy richter harmonica.
 */
public class PaddyRichterHarmonica extends AbstractHarmonica {
    /**
     * The constant HALF_TONES_IN.
     * <p>D G H D F# A H D F# A</p>
     */
    private static final int[] HALF_TONES_IN = {0, 2, 7, 11, 14, 17, 21, 23, 26, 29, 33};

    /**
     * The constant HALF_TONES_OUT.
     * <p>C E A C E G C E G C</p>
     */
    private static final int[] HALF_TONES_OUT = {0, 0, 4, 9, 12, 16, 19, 24, 28, 31, 36};

    /**
     * Instantiates a new Paddy richter harmonica.
     *
     * @param keyFrequency the key frequency
     */
    public PaddyRichterHarmonica(double keyFrequency) {
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
        return TUNE.PADDYRICHTER.name();
    }
}
