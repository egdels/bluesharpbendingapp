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
 * The DiminishedHarmonica class is a subclass of AbstractHarmonica, representing
 * a harmonica tuned using a diminished tuning structure. This tuning features specific
 * semitone offsets for both blow and draw notes, defined in the HALF_TONES_IN
 * and HALF_TONES_OUT arrays.
 * <p>
 * The diminished tuning creates a unique musical structure that is often associated
 * with specialized styles and techniques in harmonica playing. Instances of this class
 * are initialized with a specific key frequency that defines the base pitch of the harmonica.
 */
public class DiminishedHarmonica extends AbstractHarmonica {
    /**
     * An array representing the semitone offsets for the draw notes of the
     * diminished harmonica tuning. These offsets specify the pitch differences
     * between the note produced by drawing air through each channel and the root
     * note for the harmonica.
     * <p>
     * The values in this array define specific semitone intervals for each
     * draw note, forming part of the diminished tuning structure.
     */
    private static final int[] HALF_TONES_IN = {0, 2, 5, 8, 11, 14, 17, 20, 23, 26, 29};
    /**
     * An array representing the semitone offsets for the blow notes of the
     * diminished harmonica tuning. These offsets specify the pitch differences
     * between the note produced by blowing air through each channel and the root
     * note for the harmonica.
     * <p>
     * The values in this array define specific semitone intervals for each
     * blow note, forming part of the diminished tuning structure.
     */
    private static final int[] HALF_TONES_OUT = {0, 0, 3, 6, 9, 12, 15, 18, 21, 24, 27};

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
