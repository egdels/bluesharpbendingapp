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
 * The NoteLookup class provides utility methods to perform operations related
 * to musical notes and their frequencies. It allows for retrieving note
 * names, frequencies, and managing concert pitches.
 */
public class NoteLookup {

    /**
     * An array of strings representing the names of musical notes in one chromatic octave.
     * This array includes both natural notes (e.g., "C", "D", "E") and sharps (e.g., "C#", "D#"),
     * covering a total of 12 semitones in western music notation.
     * It is primarily used for musical note calculations and lookup operations related to note names.
     */
    private static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    /**
     * Represents the default frequency of the concert pitch (A4) in hertz.
     * This value is widely accepted as the standard pitch reference in
     * music tuning and is commonly set to 440.0 Hz.
     */
    private static final double DEFAULT_CONCERT_PITCH_FREQUENCY = 440.0;

    @Getter
    private static int concertPitch = (int) DEFAULT_CONCERT_PITCH_FREQUENCY;

    private NoteLookup() {
    }

    /**
     * Computes the musical note name for a given frequency in Hertz.
     * <p>
     * This method uses the provided frequency to calculate the corresponding
     * MIDI note number, from which the note name and octave are derived.
     * The calculation takes into consideration the current concert pitch setting.
     * If the frequency is invalid (non-positive or out of MIDI range), the method
     * will return null.
     *
     * @param frequency the frequency in Hertz for which to determine the note name
     * @return the name of the note corresponding to the provided frequency, including the octave,
     *         or null if the frequency is invalid or out of range
     */
    public static String getNoteName(double frequency) {
        if (frequency <= 0) return null;

        int midiNumber = (int) Math.round(69 + 12 * Math.log(frequency / concertPitch) / Math.log(2));
        if (midiNumber < 0 || midiNumber > 127) return null; // MIDI-Begrenzung

        int octave = (midiNumber / 12) - 1;
        String noteName = NOTE_NAMES[midiNumber % 12];

        return noteName + octave;
    }


    /**
     * Calculates the frequency in Hertz of a given musical note name.
     * The provided note name should include both the note (e.g., "C", "D#", "F")
     * and the octave number (e.g., "4"), using standard scientific pitch notation.
     * The method uses the current concert pitch as a reference for frequency calculation.
     *
     * @param noteName the name of the musical note for which to determine the frequency,
     *                 in the format "NoteOctave" (e.g., "A4", "C#3").
     * @return the frequency in Hertz corresponding to the given note name.
     * @throws IllegalArgumentException if the noteName is null, improperly formatted,
     *                                  or includes an unsupported note or octave.
     */
    public static double getNoteFrequency(String noteName) {
        if (noteName == null || noteName.length() < 2 || noteName.length() > 3) {
            throw new IllegalArgumentException("Invalid note name: " + noteName);
        }

        // Extract the note name and the octave number
        String note = noteName.substring(0, noteName.length() - 1); // The part before the octave (e.g., "C")
        int midiNumber = getMidiNumber(noteName, note);

        // Calculate the frequency based on the MIDI number
        return NoteUtils.round(concertPitch * Math.pow(2, (midiNumber - 69) / 12.0));
    }

    /**
     * Calculates the MIDI number for a given musical note and its corresponding note name.
     * The MIDI number is determined based on the note's position in the note name list
     * and the specified octave extracted from the provided note name.
     *
     * @param noteName the full name of the note, which includes the letter and the octave
     *                 (e.g., "C4", "D#5"). The last character(s) of the string represent the octave.
     * @param note     the specific note (e.g., "C", "D#", "F") without the octave information.
     * @return the MIDI number corresponding to the given note and octave.
     * @throws IllegalArgumentException if the given note name or note is invalid or not recognized.
     */
    private static int getMidiNumber(String noteName, String note) {
        int octave = Integer.parseInt(noteName.substring(noteName.length() - 1)); // The octave value (e.g., "4")

        // Determine the index of the note in the note names list
        int noteIndex = -1;
        for (int i = 0; i < NOTE_NAMES.length; i++) {
            if (NOTE_NAMES[i].equals(note)) {
                noteIndex = i;
                break;
            }
        }

        if (noteIndex == -1) {
            throw new IllegalArgumentException("Invalid note name: " + noteName);
        }

        // Calculate the MIDI number of the note
        return noteIndex + (octave + 1) * 12;
    }


    /**
     * Retrieves the list of supported concert pitch frequencies expressed in Hertz.
     *
     * @return an array of strings representing supported concert pitch frequencies
     */
    public static String[] getSupportedConcertPitches() {
        return new String[]{"431", "432", "433", "434", "435", "436", "437", "438",
                "439", "440", "441", "442", "443", "444", "445", "446"};
    }

    /**
     * Sets the concert pitch used for tuning musical notes.
     * Updates internal lookup data based on the newly specified concert pitch.
     *
     * @param concertPitch the frequency in Hertz to set as the concert pitch
     */
    public static void setConcertPitch(int concertPitch) {
        NoteLookup.concertPitch = concertPitch;
    }


    /**
     * Retrieves the name of the current concert pitch.
     * The concert pitch represents the reference tuning frequency
     * (commonly A4) used for musical note calculations.
     * <p>
     * This method uses the current concert pitch frequency to
     * derive its corresponding musical note name.
     *
     * @return the name of the musical note corresponding to the current concert pitch
     */
    public static String getConcertPitchName() {
        return String.valueOf(getConcertPitch());
    }

    /**
     * Sets the concert pitch by selecting it from a predefined list of supported
     * frequencies using an index value. The selected concert pitch is then applied
     * as the tuning reference for musical notes.
     *
     * @param pitchIndex the index of the desired concert pitch from the supported
     *                   list of concert pitch frequencies
     */
    public static void setConcertPitchByIndex(int pitchIndex) {
        String pitchName = getSupportedConcertPitches()[pitchIndex];
        setConcertPitch(Integer.parseInt(pitchName));
    }

}
