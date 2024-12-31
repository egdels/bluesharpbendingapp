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
 * Represents the functionality of a Harmonica, including methods
 * for retrieving note frequencies, detecting bending, overblowing,
 * and overdraw characteristics, as well as information about tuning
 * and key configurations.
 */
public interface Harmonica {
    /**
     * Retrieves the number of blow bending tones available on a specified channel of the harmonica.
     *
     * @param channel the channel of the harmonica for which to determine the blow bending tones count
     * @return the number of blow bending tones on the specified channel
     */
    int getBlowBendingTonesCount(int channel);


    /**
     * Calculates the deviation in cents between the specified note's frequency and a reference frequency.
     *
     * @param channel   the channel of the harmonica where the note resides
     * @param note      the specific note on the given channel
     * @param frequency the actual frequency of the note
     * @return the deviation in cents from the reference frequency for the given note
     */
    double getCentsNote(int channel, int note, double frequency);


    /**
     * Retrieves the number of draw bending tones available on a specified channel of the harmonica.
     *
     * @param channel the channel of the harmonica for which to determine the draw bending tones count
     * @return the number of draw bending tones on the specified channel
     */
    int getDrawBendingTonesCount(int channel);

    /**
     * Retrieves the name of the key associated with the harmonica.
     *
     * @return the name of the key as a string
     */
    String getKeyName();

    /**
     * Retrieves the frequency of a specific note on a given channel of the harmonica.
     *
     * @param channel the channel of the harmonica where the note is located
     * @param note the specific note whose frequency is to be retrieved
     * @return the frequency of the specified note in hertz
     */
    double getNoteFrequency(int channel, int note);

    /**
     * Retrieves the maximum frequency of a specified note on a given channel of the harmonica.
     *
     * @param channel the channel of the harmonica where the note is located
     * @param note    the specific note whose maximum frequency is to be retrieved
     * @return the maximum frequency of the specified note in hertz
     */
    double getNoteFrequencyMaximum(int channel, int note);

    /**
     * Retrieves the minimum frequency of a specified note on a given channel of the harmonica.
     *
     * @param channel the channel of the harmonica where the note is located
     * @param note the specific note whose minimum frequency is to be retrieved
     * @return the minimum frequency of the specified note in hertz
     */
    double getNoteFrequencyMinimum(int channel, int note);

    /**
     * Retrieves the name of the tune associated with the harmonica.
     *
     * @return the name of the tune as a string
     */
    String getTuneName();

    /**
     * Determines whether the specified channel of the harmonica has inverse cents handling enabled.
     *
     * @param channel the channel of the harmonica to check for inverse cents handling
     * @return true if the specified channel has inverse cents handling enabled; false otherwise
     */
    boolean hasInverseCentsHandling(int channel);

    /**
     * Determines whether the specified note on a given channel of the harmonica
     * with the provided frequency is currently active.
     *
     * @param channel   the channel of the harmonica where the note is located
     * @param note      the specific note to check for activity
     * @param frequency the frequency of the note to check
     * @return true if the specified note is active, false otherwise
     */
    boolean isNoteActive(int channel, int note, double frequency);

    /**
     * Determines whether the specified note on a given channel of the harmonica is an overblow.
     *
     * @param channel the channel of the harmonica where the note is located
     * @param note    the specific note to check
     * @return true if the specified note is an overblow, false otherwise
     */
    boolean isOverblow(int channel, int note);

    /**
     * Determines whether the specified note on a given channel of the harmonica is an overdraw.
     *
     * @param channel the channel of the harmonica where the note is located
     * @param note    the specific note to check
     * @return true if the specified note is an overdraw, false otherwise
     */
    boolean isOverdraw(int channel, int note);
}
