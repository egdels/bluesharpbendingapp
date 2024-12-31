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
 * The CircularHarmonica class represents a circular tuned harmonica.
 * It extends the AbstractHarmonica class, providing specific half-tone
 * mappings for inwards and outwards notes, representing the circular tuning system.
 */
public class CircularHarmonica extends AbstractHarmonica {

    /**
     * A static array representing the half-tone steps for inward notes
     * in the circular tuning system of the harmonica. Each value corresponds
     * to the number of half-tone steps from the base pitch in a specific channel.
     */
    private static final int[] HALF_TONES_IN = {0, 2, 5, 9, 12, 16, 19, 22, 26, 29, 33};

    /**
     * A static array representing the half-tone steps for outward notes
     * in the circular tuning system of the harmonica. Each value corresponds
     * to the number of half-tone steps from the base pitch in a specific channel.
     */
    private static final int[] HALF_TONES_OUT = {0, 0, 4, 7, 10, 14, 17, 21, 24, 28, 31};

    /**
     * Constructs a CircularHarmonica instance with the specified key frequency.
     *
     * @param keyFrequency the base frequency of the musical key to which
     *                     the circular harmonica is tuned
     */
    public CircularHarmonica(double keyFrequency) {
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
        return TUNE.CIRCULAR.name();
    }
}
