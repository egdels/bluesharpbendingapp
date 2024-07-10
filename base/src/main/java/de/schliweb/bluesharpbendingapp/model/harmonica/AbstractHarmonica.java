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

import de.schliweb.bluesharpbendingapp.utils.Logger;
import de.schliweb.bluesharpbendingapp.utils.NoteUtils;

import java.util.Objects;

/**
 * The type Abstract harmonica.
 *
 * <pre>
 * Channel/Notes:
 * -------
 * | 10,-2|
 * -------------------
 * | 8,-1| 9,-1| 10,-1|
 * --------------------------------------------------------------
 * | 1,0 | 2,0 | 3,0 | 4,0 | 5,0 | 6,0 | 7,0 | 8,0 | 9,0 | 10,0 |
 * --------------------------------------------------------------
 * |  1  |  2  |  3  |  4  |  5  |  6  |  7  |  8  |  9  |  10  |
 * --------------------------------------------------------------
 * | 1,1 | 2,1 | 3,1 | 4,1 | 5,1 | 6,1 | 7,1 | 8,1 | 9,1 | 10,1 |
 * --------------------------------------------------------------
 * | 1,2 | 2,2 | 3,2 | 4,2 |     | 6,2 |
 * -------------------------     -------
 * | 2,3 | 3,3 |
 * -------------
 * | 3,4 |
 * -------
 *
 * C-Dur-Richter-Harp:
 * -------
 * |   B♭ |
 * -------------------
 * |  E♭ |  F# |   B  |
 * --------------------------------------------------------------
 * |  C  |  E  |  G  |  C  |  E  |  G  |  C  |  E  |  G  |   C  |
 * --------------------------------------------------------------
 * |  1  |  2  |  3  |  4  |  5  |  6  |  7  |  8  |  9  |  10  |
 * --------------------------------------------------------------
 * |  D  |  G  |  B  |  D  |  F  |  A  |  B  |  D  |  F  |   A  |
 * --------------------------------------------------------------
 * | D♭  |  F#  | B♭  |  D♭ |    | A♭  |
 * -------------------------     -------
 * |  F  |  A  |
 * -------------
 * |  A♭  |
 * -------
 *
 * Frequencies:
 * <a href="https://de.wikipedia.org/wiki/Frequenzen_der_gleichstufigen_Stimmung">Wikipedia</a>
 * </pre>
 */
public abstract class AbstractHarmonica implements Harmonica {

    /**
     * The constant CHANNEL_MAX.
     */
    private static final int CHANNEL_MAX = 10;
    /**
     * The constant CHANNEL_MIN.
     */
    private static final int CHANNEL_MIN = 1;
    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = new Logger(AbstractHarmonica.class);
    /**
     * The constant NOTE_MAX.
     */
    private static final int NOTE_MAX = 4;
    /**
     * The constant NOTE_MIN.
     */
    private static final int NOTE_MIN = -3;
    /**
     * The Key frequency.
     */
    private final double keyFrequency;

    /**
     * Instantiates a new Abstract harmonica.
     *
     * @param keyFrequency the key frequency
     */
    protected AbstractHarmonica(double keyFrequency) {
        this.keyFrequency = keyFrequency;
    }

    /**
     * Create harmonica.
     *
     * @param keyIndex  the key index
     * @param tuneIndex the tune index
     * @return the harmonica
     */
    public static Harmonica create(int keyIndex, int tuneIndex) {
        return create(KEY.values()[keyIndex], TUNE.values()[tuneIndex]);
    }

    /**
     * Create harmonica.
     *
     * @param key  the key
     * @param tune the tune
     * @return the harmonica
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
        return harmonica;
    }

    /**
     * Gets cents.
     *
     * @param f1 the f 1
     * @param f2 the f 2
     * @return the cents
     */
    protected static double getCents(double f1, double f2) {
        return NoteUtils.getCents(f1, f2);
    }

    /**
     * Get supported tunes string [ ].
     *
     * @return the string [ ]
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
     * Get supporter keys string [ ].
     *
     * @return the string [ ]
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
     * Round double.
     *
     * @param value the value
     * @return the double
     */
    protected static double round(double value) {
        return NoteUtils.round(value);
    }

    @Override
    public int getBlowBendingTonesCount(int channel) {
        int count = getHalfTonesOut()[channel] - getHalfTonesIn()[channel] - 1;
        if (count < 0) {
            count = 0;
        }
        return count;
    }

    @Override
    public double getCentsNote(int channel, int note, double frequency) {
        return getCents(frequency, getNoteFrequency(channel, note));
    }

    /**
     * Gets channel in frequency.
     *
     * @param channel the channel
     * @return the channel in frequency
     */
    public double getChannelInFrequency(int channel) {
        return Math.pow(2.0, getHalfTonesIn()[channel] * 100.0 / 1200.0) * getKeyFrequency();
    }

    /**
     * Gets channel out frequency.
     *
     * @param channel the channel
     * @return the channel out frequency
     */
    public double getChannelOutFrequency(int channel) {
        return Math.pow(2.0, getHalfTonesOut()[channel] * 100.0 / 1200.0) * getKeyFrequency();
    }

    @Override
    public int getDrawBendingTonesCount(int channel) {
        int count = getHalfTonesIn()[channel] - getHalfTonesOut()[channel] - 1;
        if (count < 0.0) {
            count = 0;
        }
        return count;
    }

    /**
     * Get halftones in int [ ].
     *
     * @return the int [ ]
     */
    abstract int[] getHalfTonesIn();

    /**
     * Get halftones out int [ ].
     *
     * @return the int [ ]
     */
    abstract int[] getHalfTonesOut();

    /**
     * Gets key frequency.
     *
     * @return the key frequency
     */
    public double getKeyFrequency() {
        return keyFrequency;
    }

    @Override
    public String getKeyName() {
        KEY[] values = KEY.values();
        String name = null;
        for (KEY value : values) {
            if (value.getFrequency() == keyFrequency) {
                name = value.name();
                break;
            }
        }
        return name;
    }

    @Override
    public double getNoteFrequency(int channel, int note) {
        double frequency = 0.0;
        if (isOverblow(channel, note) || isOverdraw(channel, note)) {
            frequency = getOverblowOverdrawFrequency(channel);
        } else {
            if (channel >= CHANNEL_MIN && channel <= CHANNEL_MAX) {
                // obere Kanäle
                if (note == 0) {
                    frequency = getChannelOutFrequency(channel);
                }
                // untere Kanäle
                if (note == 1) {
                    frequency = getChannelInFrequency(channel);
                }
                // gezogene Bendings
                if (note > 1 && note <= NOTE_MAX) {
                    frequency = getNoteFrequency(channel, note - 1); // Rekursion
                    frequency = Math.pow(2.0, -100.0 / 1200.0) * frequency;
                }
                // geblasene Bendings
                if (note < 0 && note >= NOTE_MIN) {
                    frequency = getNoteFrequency(channel, note + 1); // Rekursion
                    frequency = Math.pow(2.0, -100.0 / 1200.0) * frequency;
                }
            }
        }
        LOGGER.debug("frequency before round " + frequency);
        frequency = round(frequency);
        LOGGER.debug("frequency after round " + frequency);
        return frequency;
    }

    @Override
    public double getNoteFrequencyMaximum(int channel, int note) {
        return (Math.pow(2.0, 50.0 / 1200.0) * getNoteFrequency(channel, note));
    }

    @Override
    public double getNoteFrequencyMinimum(int channel, int note) {
        return (Math.pow(2.0, -50.0 / 1200.0) * getNoteFrequency(channel, note));
    }

    @Override
    public abstract String getTuneName();

    @Override
    public boolean hasInverseCentsHandling(int channel) {
        return getNoteFrequency(channel, 0) > getNoteFrequency(channel, 1);
    }

    @Override
    public boolean isNoteActive(int channel, int note, double frequency) {
        double harpFrequency = getNoteFrequency(channel, note);
        LOGGER.debug(channel + " " + note + " " + harpFrequency);
        double lowerBound = (Math.pow(2.0, -50.0 / 1200.0) * harpFrequency);
        double upperBound = (Math.pow(2.0, 50.0 / 1200.0) * harpFrequency);
        LOGGER.debug(channel + " " + note + " " + frequency + " " + lowerBound + " " + upperBound);
        return frequency <= upperBound && frequency >= lowerBound;
    }

    /**
     * Gets overblow overdraw frequency.
     *
     * @param channel the channel
     * @return the overblow overdraw frequency
     */
    private double getOverblowOverdrawFrequency(int channel) {
        double frequency = 0.0;
        if (!hasInverseCentsHandling(channel)) {
            frequency = Math.pow(2.0, 100.0 / 1200.0) * getChannelInFrequency(channel);
        }
        if (hasInverseCentsHandling(channel)) {
            frequency = Math.pow(2.0, 100.0 / 1200.0) * getChannelOutFrequency(channel);
        }
        return frequency;
    }

    @Override
    public boolean isOverblow(int channel, int note) {
        return note == -1 && !hasInverseCentsHandling(channel);
    }

    @Override
    public boolean isOverdraw(int channel, int note) {
        return note == 2 && hasInverseCentsHandling(channel);
    }

    /**
     * The enum Key.
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
         * The Name.
         */
        private final String name;

        /**
         * Instantiates a new Key.
         *
         * @param name the name
         */
        KEY(String name) {
            this.name = name;
        }

        /**
         * Gets frequency.
         *
         * @return the frequency
         */
        public double getFrequency() {
            return Objects.requireNonNull(NoteLookup.getNote(name)).getValue();
        }
    }

    /**
     * The enum Tune.
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
        RICHTER
    }
}
