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
 * A utility class for looking up musical note names and their corresponding frequencies.
 * Provides methods to calculate note names for given frequencies and to calculate frequencies for given note names.
 */
class NoteLookup {
    /**
     * An immutable array containing the names of musical notes in the chromatic scale.
     * The array represents the 12 pitch classes for one octave, starting from "C" to "B".
     *
     * This array is frozen to prevent any modifications to it, ensuring consistency
     * across its usage in the application.
     *
     * @constant {string[]} NOTE_NAMES
     */
    static NOTE_NAMES = Object.freeze(["C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"]);

    /**
     * Represents the default frequency (in Hertz) of the concert pitch,
     * commonly referred to as A440. This is the standard pitch tuning
     * for musical instruments and serves as the reference point where
     * the A above middle C (A4) is set to 440 Hz.
     */
    static DEFAULT_CONCERT_PITCH_FREQUENCY = 440.0;

    /**
     * Represents the reference frequency for tuning musical instruments, commonly known as concert pitch.
     * This variable is typically set to a standard frequency, such as 440 Hz, which corresponds to the pitch of A4.
     * It is used as the baseline frequency against which other notes and instruments are tuned.
     *
     * The value is based on the default concert pitch frequency defined in NoteLookup.
     */
    static concertPitch = NoteLookup.DEFAULT_CONCERT_PITCH_FREQUENCY;

    /**
     * Computes the note name based on a given frequency.
     *
     * @param {number} frequency - The frequency of the note in hertz. Must be a positive value.
     * @return {string|null} The name of the note (e.g., "A4") if the frequency is valid and corresponds to a MIDI value
     * within the acceptable range (0-127), or null if the frequency is invalid.
     */
    static getNoteName(frequency) {
        if (frequency <= 0) {
            return null; // Ignore invalid frequencies
        }

        // Calculate the MIDI number of the tone
        const midiNumber = Math.round(69 + (12 * Math.log2(frequency / NoteLookup.concertPitch)));

        // Check if the MIDI value lies within the valid range (MIDI values typically range from 0 to 127)
        if (midiNumber < 0 || midiNumber > 127) {
            return null;
        }

        // Calculate the note index and octave
        const noteIndex = midiNumber % 12;
        const octave = Math.floor(midiNumber / 12) - 1;

        return `${NoteLookup.NOTE_NAMES[noteIndex]}${octave}`;
    }

    /**
     * Calculates the frequency of a given musical note based on its name and octave.
     *
     * @param {string} noteName - The name of the musical note (e.g., "A4", "C#3", "Bb2").
     *                            The format should include the note, optional sharp (#) or flat (b), and octave.
     * @return {number|null} The frequency of the note in hertz, or null if the input is invalid.
     */
    static getFrequency(noteName) {
        const match = noteName.match(/^([A-G]#?|[A-G]b?)(-?\d+)$/);
        if (!match) {
            return null; // Ignore invalid input
        }

        const note = match[1];
        const octave = parseInt(match[2], 10);

        // Find the note index in the chromatic array
        const noteIndex = NoteLookup.NOTE_NAMES.indexOf(note);
        if (noteIndex === -1) {
            return null;
        }

        // Calculate the MIDI number based on the note and the octave
        const midiNumber = noteIndex + (octave + 1) * 12;

        // Calculate the frequency based on the MIDI number
        return NoteLookup.concertPitch * Math.pow(2, (midiNumber - 69) / 12);
    }
}

export default NoteLookup;
