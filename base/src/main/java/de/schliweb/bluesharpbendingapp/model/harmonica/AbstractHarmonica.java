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

import de.schliweb.bluesharpbendingapp.utils.NoteUtils;
import lombok.Getter;


/**
 * Abstract base class representing the common features and behaviors of a harmonica.
 * This class implements the {@link Harmonica} interface and provides a foundation
 * for different types of harmonicas, including methods for note and frequency calculations.
 *
 * <pre>
 * Channel/Notes:
 *                                                       --------
 *                                                       | 10,-2|
 *                                           --------------------
 *                                           | 8,-1| 9,-1| 10,-1|
 * --------------------------------------------------------------
 * | 1,0 | 2,0 | 3,0 | 4,0 | 5,0 | 6,0 | 7,0 | 8,0 | 9,0 | 10,0 |
 * --------------------------------------------------------------
 * |  1  |  2  |  3  |  4  |  5  |  6  |  7  |  8  |  9  |  10  |
 * --------------------------------------------------------------
 * | 1,1 | 2,1 | 3,1 | 4,1 | 5,1 | 6,1 | 7,1 | 8,1 | 9,1 | 10,1 |
 * --------------------------------------------------------------
 * | 1,2 | 2,2 | 3,2 | 4,2 |     | 6,2 |
 * -------------------------     -------
 *       | 2,3 | 3,3 |
 *       -------------
 *             | 3,4 |
 *             -------
 *
 * C-Dur-Richter-Harp:
 *                                                        -------
 *                                                        |  A# |
 *                                            -------------------
 *                                           |  D# |  F# |   B  |
 * --------------------------------------------------------------
 * |  C  |  E  |  G  |  C  |  E  |  G  |  C  |  E  |  G  |   C  |
 * --------------------------------------------------------------
 * |  1  |  2  |  3  |  4  |  5  |  6  |  7  |  8  |  9  |  10  |
 * --------------------------------------------------------------
 * |  D  |  G  |  B  |  D  |  F  |  A  |  B  |  D  |  F  |   A  |
 * --------------------------------------------------------------
 * | C#  |  F# | A#  | C#  |     | G#  |
 * -------------------------     -------
 *       |  F  |  A  |
 *       -------------
 *             |  G# |
 *             -------
 *
 * Frequencies:
 * <a href="https://de.wikipedia.org/wiki/Frequenzen_der_gleichstufigen_Stimmung">Wikipedia</a>
 * </pre>
 */
@Getter
public abstract class AbstractHarmonica implements Harmonica {

    /**
     * Represents the maximum number of channels supported by the harmonica model.
     * This constant defines the upper limit on the number of channels that can be used
     * or processed within a single harmonica instance.
     */
    private static final int CHANNEL_MAX = 10;
    /**
     * The minimum valid channel number for the harmonica. This value defines the lowest
     * channel index that can be used when interacting with a harmonica instance.
     */
    private static final int CHANNEL_MIN = 1;
    /**
     * Represents the maximum note value allowed for a harmonica channel.
     * This constant is used to define the upper bound of valid note ranges
     * for the harmonica.
     */
    private static final int NOTE_MAX = 4;
    /**
     * Defines the lowest possible musical note value that the system can represent.
     * This value is used as a lower bound for note calculations and validations
     * within the harmonica model.
     */
    private static final int NOTE_MIN = -3;

    /**
     * Represents the base frequency of the musical key to which the harmonica is tuned.
     * This value forms the foundation for calculating all other note and channel frequencies
     * on the harmonica and plays a crucial role in determining its pitch.
     */
    private final double keyFrequency;

    /**
     * Constructs an instance of AbstractHarmonica with the specified key frequency.
     *
     * @param keyFrequency the base frequency of the musical key that the harmonica is tuned to
     */
    protected AbstractHarmonica(double keyFrequency) {
        this.keyFrequency = keyFrequency;
    }

    /**
     * Creates a harmonica instance using indexes for key and tune.
     *
     * @param keyIndex  the index of the musical key in the enumeration of keys
     * @param tuneIndex the index of the tuning system in the enumeration of tunes
     * @return a new instance of a {@link Harmonica} configured for the specified key and tuning system
     */
    public static Harmonica create(int keyIndex, int tuneIndex) {
        return create(KEY.values()[keyIndex], TUNE.values()[tuneIndex]);
    }

    /**
     * Creates a harmonica instance based on the provided key and tune.
     *
     * @param key  the musical key for the harmonica (e.g., C, D)
     * @param tune the tuning system that determines the layout of notes
     * @return a new instance of a {@link Harmonica} configured for the given key and tuning system
     */
    public static Harmonica create(KEY key, TUNE tune) {
        Harmonica harmonica = new RichterHarmonica(key.getFrequency());
        if (TUNE.COUNTRY.equals(tune)) {
            harmonica = new CountryHarmonica(key.getFrequency());
        }
        if (TUNE.DIMINISHED.equals(tune)) {
            harmonica = new DiminishedHarmonica(key.getFrequency());
        }
        if (TUNE.HARMONICMOLL.equals(tune)) {
            harmonica = new HarmonicMollHarmonica(key.getFrequency());
        }
        if (TUNE.PADDYRICHTER.equals(tune)) {
            harmonica = new PaddyRichterHarmonica(key.getFrequency());
        }
        if (TUNE.MELODYMAKER.equals(tune)) {
            harmonica = new MelodyMakerHarmonica(key.getFrequency());
        }
        if (TUNE.NATURALMOLL.equals(tune)) {
            harmonica = new NaturalMollHarmonica(key.getFrequency());
        }
        if (TUNE.CIRCULAR.equals(tune)) {
            harmonica = new CircularHarmonica(key.getFrequency());
        }
        if (TUNE.AUGMENTED.equals(tune)) {
            harmonica = new AugmentedHarmonica(key.getFrequency());
        }
        return harmonica;
    }

    /**
     * Calculates the distance between two frequencies in musical cents.
     *
     * @param f1 the reference frequency
     * @param f2 the target frequency
     * @return the difference in cents between the two frequencies
     */
    protected static double getCents(double f1, double f2) {
        return NoteUtils.getCents(f1, f2);
    }

    /**
     * Returns the names of all supported tuning systems for the harmonica.
     *
     * @return an array of tuning system names as strings
     */
    public static String[] getSupportedTunes() {
        TUNE[] values = TUNE.values();
        String[] keys = new String[TUNE.values().length];
        int index = 0;
        for (TUNE value : values) {
            keys[index] = value.name();
            index++;
        }
        return keys;
    }

    /**
     * Returns the list of musical keys supported by this application.
     *
     * @return an array of key names as strings
     */
    public static String[] getSupporterKeys() {
        KEY[] values = KEY.values();
        String[] keys = new String[KEY.values().length];
        int index = 0;
        for (KEY value : values) {
            keys[index] = value.name();
            index++;
        }
        return keys;
    }

    /**
     * Rounds the provided value to a fixed precision using the NoteUtils utility method.
     *
     * @param value the value to be rounded
     * @return the rounded value with a fixed precision
     */
    protected static double round(double value) {
        return NoteUtils.round(value);
    }

    /**
     * Creates a harmonica instance based on the provided key and tune names.
     *
     * @param selectedKey  the name of the musical key for the harmonica (e.g., "C", "D")
     * @param selectedTune the name of the tuning system that determines the layout of notes
     * @return a new instance of a {@link Harmonica} configured for the given key and tuning system
     */
    public static Harmonica create(String selectedKey, String selectedTune) {
          KEY key = KEY.valueOf(selectedKey);
          TUNE tune = TUNE.valueOf(selectedTune);
          return create(key, tune);
    }

    /**
     * Calculates the number of blow bending tones available for a specific channel.
     * Blow bending is a technique where the player lowers the pitch of a blown note.
     *
     * @param channel the channel number to check for blow bending capabilities
     * @return the number of blow bending tones available for the specified channel
     */
    @Override
    public int getBlowBendingTonesCount(int channel) {
        int count = getHalfTonesOut()[channel] - getHalfTonesIn()[channel] - 1;
        if (count < 0) {
            count = 0;
        }
        return count;
    }

    /**
     * Calculates the difference in cents between a given frequency and the standard frequency
     * of a specific note on a specific channel of the harmonica.
     *
     * @param channel   the channel number of the harmonica
     * @param note      the note index within the channel
     * @param frequency the frequency to compare against the standard note frequency
     * @return the difference in cents between the given frequency and the standard note frequency
     */
    @Override
    public double getCentsNote(int channel, int note, double frequency) {
        return getCents(frequency, getNoteFrequency(channel, note));
    }

    /**
     * Calculates the frequency of a specific channel on the harmonica based on its relative pitch (in cents)
     * and the harmonic key's base frequency.
     *
     * @param channel the channel number for which the frequency should be calculated
     * @return the frequency of the specified channel in Hertz
     */
    public double getChannelInFrequency(int channel) {
        return NoteUtils.addCentsToFrequency(getHalfTonesIn()[channel] * 100.0, getKeyFrequency());
    }

    /**
     * Calculates the frequency of a specific channel on the harmonica for an output sound,
     * based on its relative pitch (in cents) and the harmonic key's base frequency.
     *
     * @param channel the channel number for which the output frequency should be calculated
     * @return the frequency of the specified channel in Hertz
     */
    public double getChannelOutFrequency(int channel) {
        return NoteUtils.addCentsToFrequency(getHalfTonesOut()[channel] * 100.0, getKeyFrequency());
    }

    /**
     * Calculates the number of draw bending tones available for a specific channel.
     * Draw bending is a technique where the player lowers the pitch of a drawn note.
     *
     * @param channel the channel number to check for draw bending capabilities
     * @return the number of draw bending tones available for the specified channel
     */
    @Override
    public int getDrawBendingTonesCount(int channel) {
        int count = getHalfTonesIn()[channel] - getHalfTonesOut()[channel] - 1;
        if (count < 0.0) {
            count = 0;
        }
        return count;
    }

    /**
     * Retrieves an array of halftone intervals available for the input notes of the harmonica.
     *
     * @return an array of integers representing the halftone intervals associated with the input notes.
     */
    abstract int[] getHalfTonesIn();

    /**
     * Retrieves an array of halftone intervals available for the output notes of the harmonica.
     *
     * @return an array of integers representing the halftone intervals associated with the output notes.
     */
    abstract int[] getHalfTonesOut();

    /**
     * Retrieves the name of the key corresponding to the current note frequency.
     *
     * @return the name of the key if found in the enumeration of keys; otherwise, returns the note name derived from the note frequency.
     */
    @Override
    public String getKeyName() {
        String noteName = NoteLookup.getNoteName(keyFrequency);
        for (KEY key : KEY.values()) {
            if(key.getName().equals(noteName) ) {
                return key.name();
            }
        }
        return noteName;
    }

    /**
     * Retrieves the name of a specific note on a specific channel of the harmonica.
     * This method validates if the note is playable on the given channel before returning its name.
     * Valid notes include standard blow and draw notes, as well as bend notes, overblows, and overdraws.
     *
     * @param channel the channel number of the harmonica (1-10)
     * @param note    the note index within the channel (positive for draw notes, negative for blow notes)
     * @return the name of the note (e.g., "C", "D#") if it exists, or null if the note is not valid
     */
    @Override
    public String getNoteName(int channel, int note) {
        if ((note >= 0 && note <= 1) ||
                // DrawBends
                (note > 1 && note <= getDrawBendingTonesCount(channel)+1) ||
                // BlowBends
                (note < 0 && Math.abs(note) <= getBlowBendingTonesCount(channel))||
                // OverBlows
                (note==2 && hasInverseCentsHandling(channel)) ||
                // OverDraws
                (note==-1 && !hasInverseCentsHandling(channel))
        ) {
            return NoteLookup.getNoteName(getNoteFrequency(channel, note));
        }
        return null;
    }

    /**
     * Calculates the frequency of a specific note on a specific channel of the harmonica.
     * This method handles various types of notes including standard blow and draw notes,
     * as well as special techniques like bending, overblow, and overdraw.
     *
     * @param channel the channel number of the harmonica (1-10)
     * @param note    the note index within the channel (positive for draw notes, negative for blow notes)
     * @return the frequency of the specified note in Hertz, or 0.0 if the note is not valid
     */
    @Override
    public double getNoteFrequency(int channel, int note) {
        if (isOverblow(channel, note) || isOverdraw(channel, note)) {
            return round(getOverblowOverdrawFrequency(channel));
        }

        if (!isValidChannel(channel)) {
            return 0.0;
        }

        if (note == 0) {
            return round(getChannelOutFrequency(channel));
        }

        if (note == 1) {
            return round(getChannelInFrequency(channel));
        }

        if (isDrawBending(note)) {
            return round(applyHalftoneToFrequency(channel, note - 1));
        }

        if (isBlowBending(note)) {
            return round(applyHalftoneToFrequency(channel, note + 1));
        }

        return 0.0;
    }

    /**
     * Validates whether the given channel number is within the permissible range.
     *
     * @param channel the channel number to validate
     * @return true if the channel is within the valid range defined by CHANNEL_MIN and CHANNEL_MAX, false otherwise
     */
    private boolean isValidChannel(int channel) {
        return channel >= CHANNEL_MIN && channel <= CHANNEL_MAX;
    }

    /**
     * Determines if the given note corresponds to a draw bending note on the harmonica.
     *
     * @param note the note number to check
     * @return true if the note is between 2 and NOTE_MAX (inclusive), indicating a valid draw bending note; false otherwise
     */
    private boolean isDrawBending(int note) {
        return note > 1 && note <= NOTE_MAX;
    }

    /**
     * Determines if the given note corresponds to a blow bending note on the harmonica.
     *
     * @param note the note number to check
     * @return true if the note is less than 0 and greater than or equal to NOTE_MIN, indicating a valid blow bending note; false otherwise
     */
    private boolean isBlowBending(int note) {
        return note < 0 && note >= NOTE_MIN;
    }

    /**
     * Applies a halftone adjustment to the frequency of a specified note for a given channel.
     * The adjustment is performed by subtracting 100 cents from the base frequency
     * of the specified channel and note combination.
     *
     * @param channel the channel number for which the halftone adjustment is to be applied
     * @param nextNote the next note in the sequence whose frequency is to be adjusted
     * @return the frequency of the specified note after applying the halftone adjustment, in Hertz
     */
    private double applyHalftoneToFrequency(int channel, int nextNote) {
        double baseFrequency = getNoteFrequency(channel, nextNote); // recursion
        return NoteUtils.addCentsToFrequency(-100.0, baseFrequency);
    }

    /**
     * Calculates the maximum acceptable frequency for a specific note on a specific channel.
     * This method adds 50 cents to the standard frequency of the note, defining the upper
     * boundary of the frequency range that is considered to be the same note.
     *
     * @param channel the channel number of the harmonica (1-10)
     * @param note    the note index within the channel
     * @return the maximum acceptable frequency for the specified note in Hertz
     */
    @Override
    public double getNoteFrequencyMaximum(int channel, int note) {
        return NoteUtils.addCentsToFrequency(50.0, getNoteFrequency(channel, note));
    }

    /**
     * Calculates the minimum acceptable frequency for a specific note on a specific channel.
     * This method subtracts 50 cents from the standard frequency of the note, defining the lower
     * boundary of the frequency range that is considered to be the same note.
     *
     * @param channel the channel number of the harmonica (1-10)
     * @param note    the note index within the channel
     * @return the minimum acceptable frequency for the specified note in Hertz
     */
    @Override
    public double getNoteFrequencyMinimum(int channel, int note) {
        return NoteUtils.addCentsToFrequency(-50.0, getNoteFrequency(channel, note));
    }

    /**
     * Determines whether a specific channel on the harmonica has inverse cents handling.
     * Inverse cents handling means that the blow note (note 0) has a higher frequency
     * than the draw note (note 1), which affects how overblow and overdraw techniques
     * are applied to that channel.
     *
     * @param channel the channel number of the harmonica (1-10)
     * @return true if the channel has inverse cents handling (blow note frequency > draw note frequency),
     *         false otherwise
     */
    @Override
    public boolean hasInverseCentsHandling(int channel) {
        return getNoteFrequency(channel, 0) > getNoteFrequency(channel, 1);
    }

    /**
     * Calculates the overblow or overdraw frequency for a specified channel on the harmonica.
     * <p>
     * The method determines the frequency based on whether the channel uses inverse cents handling.
     * It adjusts the frequency of the channel by adding 100 cents using input or output frequencies.
     *
     * @param channel the channel number for which the overblow or overdraw frequency should be calculated
     * @return the calculated overblow or overdraw frequency in Hertz
     */
    private double getOverblowOverdrawFrequency(int channel) {
        double frequency = 0.0;
        if (!hasInverseCentsHandling(channel)) {
            frequency = NoteUtils.addCentsToFrequency(100, getChannelInFrequency(channel));
        }
        if (hasInverseCentsHandling(channel)) {
            frequency = NoteUtils.addCentsToFrequency(100, getChannelOutFrequency(channel));
        }
        return frequency;
    }

    /**
     * Determines whether a specific note on a specific channel is an overblow note.
     * An overblow is a technique where the player forces air through a draw reed
     * to produce a higher pitch than the standard blow or draw notes.
     *
     * @param channel the channel number of the harmonica (1-10)
     * @param note    the note index within the channel
     * @return true if the specified note is an overblow, false otherwise
     */
    @Override
    public boolean isOverblow(int channel, int note) {
        return note == -1 && !hasInverseCentsHandling(channel);
    }

    /**
     * Determines whether a specific note on a specific channel is an overdraw note.
     * An overdraw is a technique where the player draws air through a blow reed
     * to produce a higher pitch than the standard blow or draw notes.
     * This is essentially the opposite of an overblow.
     *
     * @param channel the channel number of the harmonica (1-10)
     * @param note    the note index within the channel
     * @return true if the specified note is an overdraw, false otherwise
     */
    @Override
    public boolean isOverdraw(int channel, int note) {
        return note == 2 && hasInverseCentsHandling(channel);
    }

    /**
     * An enumeration of musical keys, each associated with a specific pitch frequency.
     */
    public enum KEY {
        /**
         * A key.
         */
        A("A3"),
        /**
         * A flat key.
         */
        A_FLAT("G#3"),
        /**
         * B key.
         */
        B("B3"),
        /**
         * B flat key.
         */
        B_FLAT("A#3"),
        /**
         * C key.
         */
        C("C4"),
        /**
         * D key.
         */
        D("D4"),
        /**
         * D flat key.
         */
        D_FLAT("C#4"),
        /**
         * E key.
         */
        E("E4"),
        /**
         * E flat key.
         */
        E_FLAT("D#4"),
        /**
         * F key.
         */
        F("F4"),
        /**
         * F hash key.
         */
        F_HASH("F#4"),
        /**
         * G key.
         */
        G("G3"),
        /**
         * Ha key.
         */
        HA("A4"),
        /**
         * Ha flat key.
         */
        HA_FLAT("G#4"),
        /**
         * Hb flat key.
         */
        HB_FLAT("A#4"),
        /**
         * Hg key.
         */
        HG("G4"),
        /**
         * La key.
         */
        LA("A2"),
        /**
         * La flat key.
         */
        LA_FLAT("G#2"),
        /**
         * Lb key.
         */
        LB("B2"),
        /**
         * Lb flat key.
         */
        LB_FLAT("A#2"),
        /**
         * Lc key.
         */
        LC("C3"),
        /**
         * Ld key.
         */
        LD("D3"),
        /**
         * Ld flat key.
         */
        LD_FLAT("C#3"),
        /**
         * Le key.
         */
        LE("E3"),
        /**
         * Le flat key.
         */
        LE_FLAT("D#3"),
        /**
         * Lf key.
         */
        LF("F3"),
        /**
         * Lf hash key.
         */
        LF_HASH("F#3"),
        /**
         * Lg key.
         */
        LG("G2"),
        /**
         * Lle key.
         */
        LLE("E2"),
        /**
         * Llf key.
         */
        LLF("F2"),
        /**
         * Llf hash key.
         */
        LLF_HASH("F#2");

        /**
         * Represents the name of the musical key, corresponding to a specific pitch.
         * This value is used to identify the frequency associated with the key.
         */
        private final String name;

        /**
         * Constructs a KEY instance with the specified musical key name.
         *
         * @param name the name of the musical key, representing a specific pitch frequency
         */
        KEY(String name) {
            this.name = name;
        }

        /**
         * Retrieves the frequency of the musical key associated with this instance.
         * The frequency is determined by looking up the musical key's name.
         *
         * @return the frequency in Hertz (Hz) corresponding to the musical key
         * associated with this instance
         * @throws NullPointerException if the frequency cannot be determined
         */
        public double getFrequency() {
            return NoteLookup.getNoteFrequency(name);
        }

        /**
         * Retrieves the name associated with this instance of the musical key.
         *
         * @return the name of the musical key as a string
         */
        public String getName() { return name;}
    }

    /**
     * Retrieves the minimum frequency that the harmonica can produce.
     * This is calculated by reducing the frequency of the first channel's output by 50 cents.
     *
     * @return the minimum frequency of the harmonica in Hertz
     */
    @Override
    public double getHarmonicaMinFrequency() {
        return NoteUtils.addCentsToFrequency(-50.0, getChannelOutFrequency(1));
    }

    /**
     * Retrieves the maximum frequency produced by the harmonica.
     * This is calculated by increasing the base frequency of the 10th channel's output by 50 cents.
     *
     * @return the maximum frequency of the harmonica in Hertz
     */
    @Override
    public double getHarmonicaMaxFrequency() {
        return NoteUtils.addCentsToFrequency(50.0, getChannelOutFrequency(10));
    }

    /**
     * An enumeration of supported harmonica tuning systems.
     */
    public enum TUNE {
        /**
         * Country tune.
         */
        COUNTRY,
        /**
         * Diminished tune.
         */
        DIMINISHED,
        /**
         * Harmonicmoll tune.
         */
        HARMONICMOLL,
        /**
         * Melodymaker tune.
         */
        MELODYMAKER,
        /**
         * Naturalmoll tune.
         */
        NATURALMOLL,
        /**
         * Paddyrichter tune.
         */
        PADDYRICHTER,
        /**
         * Richter tune.
         */
        RICHTER,
        /**
         * Circular tune.
         */
        CIRCULAR,
        /**
         * Augmented tune.
         */
        AUGMENTED
    }
}
