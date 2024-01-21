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
 * The interface Harmonica.
 */
public interface Harmonica {
    /**
     * Gets blow bending tones count.
     *
     * @param channel the channel
     * @return the blow bending tones count
     */
    int getBlowBendingTonesCount(int channel);


    /**
     * Gets cents note.
     *
     * @param channel   the channel
     * @param note      the note
     * @param frequency the frequency
     * @return the cents note
     */
    double getCentsNote(int channel, int note, double frequency);


    /**
     * Gets draw bending tones count.
     *
     * @param channel the channel
     * @return the draw bending tones count
     */
    int getDrawBendingTonesCount(int channel);

    /**
     * Gets key name.
     *
     * @return the key name
     */
    String getKeyName();

    /**
     * Gets note frequency.
     *
     * @param channel the channel
     * @param note    the note
     * @return the note frequency
     */
    double getNoteFrequency(int channel, int note);

    /**
     * Gets note frequency maximum.
     *
     * @param channel the channel
     * @param note    the note
     * @return the note frequency maximum
     */
    double getNoteFrequencyMaximum(int channel, int note);

    /**
     * Gets note frequency minimum.
     *
     * @param channel the channel
     * @param note    the note
     * @return the note frequency minimum
     */
    double getNoteFrequencyMinimum(int channel, int note);

    /**
     * Gets tune name.
     *
     * @return the tune name
     */
    String getTuneName();

    /**
     * Has inverse cents handling boolean.
     *
     * @param channel the channel
     * @return the boolean
     */
    boolean hasInverseCentsHandling(int channel);

    /**
     * Is note active boolean.
     *
     * @param channel   the channel
     * @param note      the note
     * @param frequency the frequency
     * @return the boolean
     */
    boolean isNoteActive(int channel, int note, double frequency);

    /**
     * Is overblow boolean.
     *
     * @param channel the channel
     * @param note    the note
     * @return the boolean
     */
    boolean isOverblow(int channel, int note);

    /**
     * Is overdraw boolean.
     *
     * @param channel the channel
     * @param note    the note
     * @return the boolean
     */
    boolean isOverdraw(int channel, int note);
}
