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
 * The HarmonicMollHarmonica class represents a harmonica tuned to the Harmonic Moll scale.
 * This class extends the AbstractHarmonica base class and provides the specific
 * half-tone mappings for the Harmonic Moll tuning.
 */
public class HarmonicMollHarmonica extends AbstractHarmonica {
    /**
     * Represents the half-tone intervals for the blow notes of the harmonica
     * in the Harmonic Moll tuning system. Each value in the array corresponds
     * to the number of semitone steps (half-tones) from a reference note,
     * typically used to determine the pitch of the blow notes for specific
     * channels.
     */
    private static final int[] HALF_TONES_IN = {0, 2, 7, 11, 14, 17, 20, 23, 26, 29, 32};

    /**
     * Represents the half-tone intervals for the draw notes of the harmonica
     * in the Harmonic Moll tuning system. Each value in the array corresponds
     * to the number of semitone steps (half-tones) from a reference note,
     * typically used to determine the pitch of the draw notes for specific
     * channels.
     */
    private static final int[] HALF_TONES_OUT = {0, 0, 3, 7, 12, 15, 19, 24, 27, 31, 36};

    /**
     * Constructs a Harmonic Moll Harmonica with the specified key frequency.
     *
     * @param keyFrequency the fundamental frequency (in hertz) of the musical key
     *                     to which the harmonica is tuned
     */
    public HarmonicMollHarmonica(double keyFrequency) {
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
        return TUNE.HARMONICMOLL.name();
    }
}
