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
 * Represents a Paddy Richter tuned harmonica, which is a modification of the Richter tuning.
 * It features a specific arrangement of pitches for blow (out) and draw (in) notes, tailored
 * for particular playing styles such as Irish music.
 * <p>
 * This class extends the {@code AbstractHarmonica} class and provides the custom
 * half-tone mappings for the Paddy Richter tuning.
 */
public class PaddyRichterHarmonica extends AbstractHarmonica {
    /**
     * Defines the half-tone intervals for the draw (in) notes of the Paddy Richter tuned harmonica.
     * These intervals specify the number of half-tone steps above a reference note for each channel
     * of the harmonica, tailored to the Paddy Richter tuning. The values correspond to the following
     * tone arrangement:
     * <p>
     * Channel 1: 0 half-tones,
     * Channel 2: 2 half-tones,
     * Channel 3: 7 half-tones,
     * Channel 4: 11 half-tones,
     * Channel 5: 14 half-tones,
     * Channel 6: 17 half-tones,
     * Channel 7: 21 half-tones,
     * Channel 8: 23 half-tones,
     * Channel 9: 26 half-tones,
     * Channel 10: 29 half-tones,
     * Channel 11: 33 half-tones.
     */
    private static final int[] HALF_TONES_IN = {0, 2, 7, 11, 14, 17, 21, 23, 26, 29, 33};

    /**
     * Defines the half-tone intervals for the blow (out) notes of the Paddy Richter tuned harmonica.
     * These intervals represent the number of half-tone steps above a reference note for each channel
     * of the harmonica, tailored to the Paddy Richter tuning. The values correspond to the following
     * tone arrangement:
     * <p>
     * Channel 1: 0 half-tones,
     * Channel 2: 0 half-tones,
     * Channel 3: 4 half-tones,
     * Channel 4: 9 half-tones,
     * Channel 5: 12 half-tones,
     * Channel 6: 16 half-tones,
     * Channel 7: 19 half-tones,
     * Channel 8: 24 half-tones,
     * Channel 9: 28 half-tones,
     * Channel 10: 31 half-tones,
     * Channel 11: 36 half-tones.
     */
    private static final int[] HALF_TONES_OUT = {0, 0, 4, 9, 12, 16, 19, 24, 28, 31, 36};

    /**
     * Constructs a Paddy Richter tuned harmonica with the specified key frequency.
     * The key frequency determines the base frequency of the musical key that the
     * harmonica is tuned to.
     *
     * @param keyFrequency the base frequency of the musical key that the harmonica is tuned to
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
