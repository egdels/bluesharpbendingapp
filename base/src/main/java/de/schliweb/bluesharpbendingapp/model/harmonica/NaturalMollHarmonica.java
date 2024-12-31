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
 * The NaturalMollHarmonica class represents a harmonica with a natural minor tuning.
 * This class defines specific half-tone arrangements for the in and out breath patterns,
 * and extends the functionality of the AbstractHarmonica class.
 */
public class NaturalMollHarmonica extends AbstractHarmonica {
    /**
     * Defines the half-tone intervals for the in-breath pattern of a natural
     * minor tuned harmonica. Each element represents the half-tone offset
     * from the base note for a corresponding channel of the harmonica in
     * ascending order.
     */
    private static final int[] HALF_TONES_IN = {0, 2, 7, 10, 14, 17, 21, 22, 26, 29, 33};

    /**
     * Defines the half-tone intervals for the out-breath pattern of a natural
     * minor tuned harmonica. Each element represents the half-tone offset
     * from the base note for a corresponding channel of the harmonica in
     * ascending order.
     */
    private static final int[] HALF_TONES_OUT = {0, 0, 3, 7, 12, 15, 19, 24, 27, 31, 36};

    /**
     * Constructs a NaturalMollHarmonica instance with the specified key frequency.
     *
     * @param keyFrequency the base frequency of the musical key that the harmonica is tuned to,
     *                     defining the pitch that serves as the foundation for its notes
     */
    public NaturalMollHarmonica(double keyFrequency) {
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
        return TUNE.NATURALMOLL.name();
    }
}
