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
 * The DiminishedHarmonica class represents a type of harmonica with a specific
 * tuning known as the diminished tuning. It extends the AbstractHarmonica class,
 * providing the necessary implementation details for this specific tuning type.
 */
public class DiminishedHarmonica extends AbstractHarmonica {
    /**
     * An array representing the semitone offsets for the draw notes of the
     * diminished harmonica tuning. These offsets specify the pitch differences
     * between the note produced by drawing air through each channel and the root
     * note for the harmonica.
     * <p>
     * The values in this array correspond to specific semitone offsets for each
     * draw note, providing the foundation for the diminished tuning structure in
     * this context.
     */
    private static final int[] HALF_TONES_IN = {0, 2, 7, 11, 14, 17, 21, 23, 26, 29, 33};

    /**
     * An array representing the semitone offsets for the blow notes of the
     * diminished harmonica tuning. These offsets define the pitch differences
     * between the note produced by blowing air through each channel and the root
     * note of the harmonica.
     * <p>
     * The values in this array correspond to specific semitone offsets for each
     * blow note, contributing to the tuning structure of this harmonica type.
     */
    private static final int[] HALF_TONES_OUT = {0, 0, 4, 7, 12, 16, 19, 24, 28, 31, 36};

    /**
     * Constructs a DiminishedHarmonica object with the specified key frequency.
     * This constructor initializes the harmonica with the diminished tuning,
     * setting the base frequency of the musical key that the harmonica is tuned to.
     *
     * @param keyFrequency the base frequency of the key that the diminished harmonica is tuned to
     */
    public DiminishedHarmonica(double keyFrequency) {
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
        return TUNE.DIMINISHED.name();
    }
}
