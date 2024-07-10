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

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * The type Note lookup.
 */
public class NoteLookup {

    /**
     * The constant CENTS_MAX.
     */
    private static final double CENTS_MAX = 50.0;
    /**
     * The constant CENTS_MIN.
     */
    private static final double CENTS_MIN = -50.0;

    /**
     * The constant DEFAULT_CONCERT_PITCH_FREQUENCY.
     */
    private static final double DEFAULT_CONCERT_PITCH_FREQUENCY = 440.0;

    /**
     * The constant notes.
     */
    private static final HashMap<String, Double> notes = new HashMap<>();
    /**
     * The constant concertPitch.
     */
    private static int concertPitch = (int) DEFAULT_CONCERT_PITCH_FREQUENCY;

    static {
        initLookup();
    }

    private NoteLookup() {
    }

    /**
     * Gets note.
     *
     * @param frequency the frequency
     * @return the note
     */
    public static Entry<String, Double> getNote(double frequency) {
        for (Entry<String, Double> note : notes.entrySet()) {
            Double noteFrequency = note.getValue();
            double cents = NoteUtils.getCents(noteFrequency, frequency);
            if (cents >= CENTS_MIN && cents <= CENTS_MAX) {
                return note;
            }
        }
        return null;
    }

    /**
     * Gets note.
     *
     * @param name the name
     * @return the note
     */
    public static Entry<String, Double> getNote(String name) {
        for (Entry<String, Double> note : notes.entrySet()) {
            String noteName = note.getKey();
            if (noteName.equals(name)) {
                return note;
            }
        }
        return null;
    }

    /**
     * Init lookup.
     */
    private static void initLookup() {
        notes.put("C8", 4186.01);
        notes.put("B7", 3951.07);
        notes.put("A#7", 3729.31);
        notes.put("A7", 3520.0);
        notes.put("G#7", 3322.44);
        notes.put("G7", 3135.96);
        notes.put("F#7", 2959.96);
        notes.put("F7", 2793.83);
        notes.put("E7", 2637.02);
        notes.put("D#7", 2489.02);
        notes.put("D7", 2349.32);
        notes.put("C#7", 2217.46);
        notes.put("C7", 2093.00);
        notes.put("B6", 1975.53);
        notes.put("A#6", 1864.66);
        notes.put("A6", 1760.0);
        notes.put("G#6", 1661.22);
        notes.put("G6", 1567.98);
        notes.put("F#6", 1479.98);
        notes.put("F6", 1396.91);
        notes.put("E6", 1318.51);
        notes.put("D#6", 1244.51);
        notes.put("D6", 1174.66);
        notes.put("C#6", 1108.73);
        notes.put("C6", 1046.50);
        notes.put("B5", 987.767);
        notes.put("A#5", 932.328);
        notes.put("A5", 880.0);
        notes.put("G#5", 830.609);
        notes.put("G5", 783.991);
        notes.put("F#5", 739.989);
        notes.put("F5", 698.456);
        notes.put("E5", 659.255);
        notes.put("D#5", 622.254);
        notes.put("D5", 587.330);
        notes.put("C#5", 554.365);
        notes.put("C5", 523.251);
        notes.put("B4", 493.883);
        notes.put("A#4", 466.164);
        notes.put("A4", 440.0);
        notes.put("G#4", 415.305);
        notes.put("G4", 391.995);
        notes.put("F#4", 369.994);
        notes.put("F4", 349.228);
        notes.put("E4", 329.628);
        notes.put("D#4", 311.127);
        notes.put("D4", 293.665);
        notes.put("C#4", 277.183);
        notes.put("C4", 261.626);
        notes.put("B3", 246.942);
        notes.put("A#3", 233.082);
        notes.put("A3", 220.0);
        notes.put("G#3", 207.652);
        notes.put("G3", 195.998);
        notes.put("F#3", 184.997);
        notes.put("F3", 174.614);
        notes.put("E3", 164.814);
        notes.put("D#3", 155.563);
        notes.put("D3", 146.832);
        notes.put("C#3", 138.591);
        notes.put("C3", 130.813);
        notes.put("B2", 123.471);
        notes.put("A#2", 116.541);
        notes.put("A2", 110.0);
        notes.put("G#2", 103.826);
        notes.put("G2", 97.9989);
        notes.put("F#2", 92.4986);
        notes.put("F2", 87.3071);
        notes.put("E2", 82.4069);
        notes.put("D#2", 77.7817);
        notes.put("D2", 73.4162);
        notes.put("C#2", 69.2957);
        notes.put("C2", 65.4064);
        notes.put("B1", 61.7354);
        notes.put("A#1", 58.2705);
        notes.put("A1", 55.0);
        notes.put("G#1", 51.9131);
        notes.put("G1", 48.9994);
        notes.put("F#1", 46.2493);
        notes.put("F1", 43.6535);
        notes.put("E1", 41.2034);
        notes.put("D#1", 38.8909);
        notes.put("D1", 36.7081);
        notes.put("C#1", 34.6478);
        notes.put("C1", 32.7032);
        notes.put("B0", 30.8677);
        notes.put("A#0", 29.1352);
        notes.put("A0", 27.5);
        notes.put("G#0", 25.9565);
        notes.put("G0", 24.4997);
        notes.put("F#0", 23.1247);
        notes.put("F0", 21.8268);
        notes.put("E0", 20.6017);
        notes.put("D#0", 19.4454);
        notes.put("D0", 18.3540);
        notes.put("C#0", 17.3239);
        notes.put("C0", 16.3516);
    }

    /**
     * Get supported concert pitches string [ ].
     *
     * @return the string [ ]
     */
    public static String[] getSupportedConcertPitches() {
        return new String[]{"431", "432", "433", "434", "435", "436", "437", "438",
                "439", "440", "441", "442", "443", "444", "445", "446"};
    }

    /**
     * Gets concert pitch.
     *
     * @return the concert pitch
     */
    public static int getConcertPitch() {
        return concertPitch;
    }

    /**
     * Sets concert pitch.
     *
     * @param concertPitch the concert pitch
     */
    public static void setConcertPitch(int concertPitch) {
        NoteLookup.concertPitch = concertPitch;
        initLookup();
        updateLookup();
    }

    /**
     * Update lookup.
     */
    private static void updateLookup() {
        double cents = NoteUtils.getCents(concertPitch, DEFAULT_CONCERT_PITCH_FREQUENCY);
        for (Entry<String, Double> note : notes.entrySet()) {
            double noteFrequency = note.getValue();
            double newNoteFrequency = NoteUtils.round(Math.pow(2.0, cents / 1200.0) * noteFrequency);
            note.setValue(newNoteFrequency);
        }
    }

    /**
     * Gets concert pitch name.
     *
     * @return the concert pitch name
     */
    public static String getConcertPitchName() {
        return String.valueOf(getConcertPitch());
    }

    /**
     * Sets concert pitch by index.
     *
     * @param pitchIndex the pitch index
     */
    public static void setConcertPitchByIndex(int pitchIndex) {
        String pitchName = getSupportedConcertPitches()[pitchIndex];
        setConcertPitch(Integer.parseInt(pitchName));
    }
}
