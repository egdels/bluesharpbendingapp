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
class NoteUtils {
    static DECIMAL_PRECISION = 3;

    constructor() {
        throw new Error("This class cannot be instantiated.");
    }

    static getCents(f1, f2) {
        return 1200 * (Math.log(f1 / f2) / Math.log(2));
    }

    static round(value) {
        return Math.floor(value * Math.pow(10, NoteUtils.DECIMAL_PRECISION)) / Math.pow(10, NoteUtils.DECIMAL_PRECISION);
    }

    static addCentsToFrequency(cents, frequency) {
        return Math.pow(2.0, cents / 1200.0) * frequency;
    }
}

export class NoteLookup {
    static CENTS_MAX = 50.0;
    static CENTS_MIN = -50.0;
    static DEFAULT_CONCERT_PITCH_FREQUENCY = 440.0;
    static notes = new Map();
    static concertPitch = Math.floor(NoteLookup.DEFAULT_CONCERT_PITCH_FREQUENCY);

    static {
        NoteLookup.initLookup();
    }

    constructor() {
        throw new Error("This class cannot be instantiated.");
    }

    static getNote(frequency) {
        for (let [noteName, noteFrequency] of NoteLookup.notes) {
            let cents = NoteUtils.getCents(noteFrequency, frequency);
            if (cents >= NoteLookup.CENTS_MIN && cents <= NoteLookup.CENTS_MAX) {
                console.log(noteName);
                console.log(noteFrequency);
                return { key: noteName, value: noteFrequency };
            }
        }
        return null;
    }

    static getNoteByName(name) {
        for (let [noteName, noteFrequency] of NoteLookup.notes) {
            if (noteName === name) {
                return { key: noteName, value: noteFrequency };
            }
        }
        return null;
    }

    static initLookup() {
        NoteLookup.notes.set("C8", 4186.01);
        NoteLookup.notes.set("B7", 3951.07);
        NoteLookup.notes.set("A#7", 3729.31);
        NoteLookup.notes.set("A7", 3520.0);
        NoteLookup.notes.set("G#7", 3322.44);
        NoteLookup.notes.set("G7", 3135.96);
        NoteLookup.notes.set("F#7", 2959.96);
        NoteLookup.notes.set("F7", 2793.83);
        NoteLookup.notes.set("E7", 2637.02);
        NoteLookup.notes.set("D#7", 2489.02);
        NoteLookup.notes.set("D7", 2349.32);
        NoteLookup.notes.set("C#7", 2217.46);
        NoteLookup.notes.set("C7", 2093.00);
        NoteLookup.notes.set("B6", 1975.53);
        NoteLookup.notes.set("A#6", 1864.66);
        NoteLookup.notes.set("A6", 1760.0);
        NoteLookup.notes.set("G#6", 1661.22);
        NoteLookup.notes.set("G6", 1567.98);
        NoteLookup.notes.set("F#6", 1479.98);
        NoteLookup.notes.set("F6", 1396.91);
        NoteLookup.notes.set("E6", 1318.51);
        NoteLookup.notes.set("D#6", 1244.51);
        NoteLookup.notes.set("D6", 1174.66);
        NoteLookup.notes.set("C#6", 1108.73);
        NoteLookup.notes.set("C6", 1046.50);
        NoteLookup.notes.set("B5", 987.767);
        NoteLookup.notes.set("A#5", 932.328);
        NoteLookup.notes.set("A5", 880.0);
        NoteLookup.notes.set("G#5", 830.609);
        NoteLookup.notes.set("G5", 783.991);
        NoteLookup.notes.set("F#5", 739.989);
        NoteLookup.notes.set("F5", 698.456);
        NoteLookup.notes.set("E5", 659.255);
        NoteLookup.notes.set("D#5", 622.254);
        NoteLookup.notes.set("D5", 587.330);
        NoteLookup.notes.set("C#5", 554.365);
        NoteLookup.notes.set("C5", 523.251);
        NoteLookup.notes.set("B4", 493.883);
        NoteLookup.notes.set("A#4", 466.164);
        NoteLookup.notes.set("A4", 440.0);
        NoteLookup.notes.set("G#4", 415.305);
        NoteLookup.notes.set("G4", 391.995);
        NoteLookup.notes.set("F#4", 369.994);
        NoteLookup.notes.set("F4", 349.228);
        NoteLookup.notes.set("E4", 329.628);
        NoteLookup.notes.set("D#4", 311.127);
        NoteLookup.notes.set("D4", 293.665);
        NoteLookup.notes.set("C#4", 277.183);
        NoteLookup.notes.set("C4", 261.626);
        NoteLookup.notes.set("B3", 246.942);
        NoteLookup.notes.set("A#3", 233.082);
        NoteLookup.notes.set("A3", 220.0);
        NoteLookup.notes.set("G#3", 207.652);
        NoteLookup.notes.set("G3", 195.998);
        NoteLookup.notes.set("F#3", 184.997);
        NoteLookup.notes.set("F3", 174.614);
        NoteLookup.notes.set("E3", 164.814);
        NoteLookup.notes.set("D#3", 155.563);
        NoteLookup.notes.set("D3", 146.832);
        NoteLookup.notes.set("C#3", 138.591);
        NoteLookup.notes.set("C3", 130.813);
        NoteLookup.notes.set("B2", 123.471);
        NoteLookup.notes.set("A#2", 116.541);
        NoteLookup.notes.set("A2", 110.0);
        NoteLookup.notes.set("G#2", 103.826);
        NoteLookup.notes.set("G2", 97.9989);
        NoteLookup.notes.set("F#2", 92.4986);
        NoteLookup.notes.set("F2", 87.3071);
        NoteLookup.notes.set("E2", 82.4069);
        NoteLookup.notes.set("D#2", 77.7817);
        NoteLookup.notes.set("D2", 73.4162);
        NoteLookup.notes.set("C#2", 69.2957);
        NoteLookup.notes.set("C2", 65.4064);
        NoteLookup.notes.set("B1", 61.7354);
        NoteLookup.notes.set("A#1", 58.2705);
        NoteLookup.notes.set("A1", 55.0);
        NoteLookup.notes.set("G#1", 51.9131);
        NoteLookup.notes.set("G1", 48.9994);
        NoteLookup.notes.set("F#1", 46.2493);
        NoteLookup.notes.set("F1", 43.6535);
        NoteLookup.notes.set("E1", 41.2034);
        NoteLookup.notes.set("D#1", 38.8909);
        NoteLookup.notes.set("D1", 36.7081);
        NoteLookup.notes.set("C#1", 34.6478);
        NoteLookup.notes.set("C1", 32.7032);
        NoteLookup.notes.set("B0", 30.8677);
        NoteLookup.notes.set("A#0", 29.1352);
        NoteLookup.notes.set("A0", 27.5);
        NoteLookup.notes.set("G#0", 25.9565);
        NoteLookup.notes.set("G0", 24.4997);
        NoteLookup.notes.set("F#0", 23.1247);
        NoteLookup.notes.set("F0", 21.8268);
        NoteLookup.notes.set("E0", 20.6017);
        NoteLookup.notes.set("D#0", 19.4454);
        NoteLookup.notes.set("D0", 18.3540);
        NoteLookup.notes.set("C#0", 17.3239);
        NoteLookup.notes.set("C0", 16.3516);
    }

    static getSupportedConcertPitches() {
        return ["431", "432", "433", "434", "435", "436", "437", "438",
                "439", "440", "441", "442", "443", "444", "445", "446"];
    }

    static getConcertPitch() {
        return NoteLookup.concertPitch;
    }

    static setConcertPitch(concertPitch) {
        NoteLookup.concertPitch = concertPitch;
        NoteLookup.initLookup();
        NoteLookup.updateLookup();
    }

    static updateLookup() {
        let cents = NoteUtils.getCents(NoteLookup.concertPitch, NoteLookup.DEFAULT_CONCERT_PITCH_FREQUENCY);
        for (let [noteName, noteFrequency] of NoteLookup.notes) {
            NoteLookup.notes.set(noteName, NoteUtils.round(NoteUtils.addCentsToFrequency(cents, noteFrequency)));
        }
    }

    static getConcertPitchName() {
        return String(NoteLookup.getConcertPitch());
    }

    static setConcertPitchByIndex(pitchIndex) {
        let pitchName = NoteLookup.getSupportedConcertPitches()[pitchIndex];
        NoteLookup.setConcertPitch(parseInt(pitchName));
    }
}

// Harmonica interface
class Harmonica {
    /**
     * Gets blow bending tones count.
     * @param {number} channel - The channel
     * @returns {number} The blow bending tones count
     */
    getBlowBendingTonesCount(channel) {
        throw new Error("Method not implemented.");
    }

    /**
     * Gets cents note.
     * @param {number} channel - The channel
     * @param {number} note - The note
     * @param {number} frequency - The frequency
     * @returns {number} The cents note
     */
    getCentsNote(channel, note, frequency) {
        throw new Error("Method not implemented.");
    }

    /**
     * Gets draw bending tones count.
     * @param {number} channel - The channel
     * @returns {number} The draw bending tones count
     */
    getDrawBendingTonesCount(channel) {
        throw new Error("Method not implemented.");
    }

    /**
     * Gets key name.
     * @returns {string} The key name
     */
    getKeyName() {
        throw new Error("Method not implemented.");
    }

    /**
     * Gets note frequency.
     * @param {number} channel - The channel
     * @param {number} note - The note
     * @returns {number} The note frequency
     */
    getNoteFrequency(channel, note) {
        throw new Error("Method not implemented.");
    }

    /**
     * Gets note frequency maximum.
     * @param {number} channel - The channel
     * @param {number} note - The note
     * @returns {number} The note frequency maximum
     */
    getNoteFrequencyMaximum(channel, note) {
        throw new Error("Method not implemented.");
    }

    /**
     * Gets note frequency minimum.
     * @param {number} channel - The channel
     * @param {number} note - The note
     * @returns {number} The note frequency minimum
     */
    getNoteFrequencyMinimum(channel, note) {
        throw new Error("Method not implemented.");
    }

    /**
     * Gets tune name.
     * @returns {string} The tune name
     */
    getTuneName() {
        throw new Error("Method not implemented.");
    }

    /**
     * Has inverse cents handling boolean.
     * @param {number} channel - The channel
     * @returns {boolean} The boolean
     */
    hasInverseCentsHandling(channel) {
        throw new Error("Method not implemented.");
    }

    /**
     * Is note active boolean.
     * @param {number} channel - The channel
     * @param {number} note - The note
     * @param {number} frequency - The frequency
     * @returns {boolean} The boolean
     */
    isNoteActive(channel, note, frequency) {
        throw new Error("Method not implemented.");
    }

    /**
     * Is overblow boolean.
     * @param {number} channel - The channel
     * @param {number} note - The note
     * @returns {boolean} The boolean
     */
    isOverblow(channel, note) {
        throw new Error("Method not implemented.");
    }

    /**
     * Is overdraw boolean.
     * @param {number} channel - The channel
     * @param {number} note - The note
     * @returns {boolean} The boolean
     */
    isOverdraw(channel, note) {
        throw new Error("Method not implemented.");
    }
}

export class AbstractHarmonica {
    static CHANNEL_MAX = 10;
    static CHANNEL_MIN = 1;
    static NOTE_MAX = 4;
    static NOTE_MIN = -3;

    constructor(keyFrequency) {
        this.keyFrequency = keyFrequency;
    }

    static createByIndex (keyIndex, tuneIndex) {
        return AbstractHarmonica.create(Object.values(KEY)[keyIndex],Object.values(TUNE)[tuneIndex]);
    }

    static create(key, tune) {
        let harmonica = new RichterHarmonica(key.frequency);
        if (tune === TUNE.COUNTRY) {
            harmonica = new CountryHarmonica(key.frequency);
        }
        if (tune === TUNE.DIMINISHED) {
            harmonica = new DiminishedHarmonica(key.frequency);
        }
        if (tune === TUNE.HARMONICMOLL) {
            harmonica = new HarmonicMollHarmonica(key.frequency);
        }
        if (tune === TUNE.PADDYRICHTER) {
            harmonica = new PaddyRichterHarmonica(key.frequency);
        }
        if (tune === TUNE.MELODYMAKER) {
            harmonica = new MelodyMakerHarmonica(key.frequency);
        }
        if (tune === TUNE.NATURALMOLL) {
            harmonica = new NaturalMollHarmonica(key.frequency);
        }
        if (tune === TUNE.CIRCULAR) {
            harmonica = new CircularHarmonica(key.frequency);
        }
        if (tune === TUNE.AUGMENTED) {
            harmonica = new AugmentedHarmonica(key.frequency);
        }
        return harmonica;
    }

    static getCents(f1, f2) {
        return NoteUtils.getCents(f1, f2);
    }

    static getSupportedTunes() {
        return Object.values(TUNE);
    }

    static getSupporterKeys() {
        return Object.values(KEY).map(key => key.name);
    }

    static round(value) {
        return NoteUtils.round(value);
    }

    getBlowBendingTonesCount(channel) {
        let count = this.getHalfTonesOut()[channel] - this.getHalfTonesIn()[channel] - 1;
        return count < 0 ? 0 : count;
    }

    getCentsNote(channel, note, frequency) {
        return AbstractHarmonica.getCents(frequency, this.getNoteFrequency(channel, note));
    }

    getChannelInFrequency(channel) {
        return NoteUtils.addCentsToFrequency(this.getHalfTonesIn()[channel] * 100.0, this.getKeyFrequency());
    }

    getChannelOutFrequency(channel) {
        return NoteUtils.addCentsToFrequency(this.getHalfTonesOut()[channel] * 100.0, this.getKeyFrequency());
    }

    getDrawBendingTonesCount(channel) {
        let count = this.getHalfTonesIn()[channel] - this.getHalfTonesOut()[channel] - 1;
        return count < 0 ? 0 : count;
    }

    getKeyFrequency() {
        return this.keyFrequency;
    }

    getKeyName() {
        for (let value of Object.values(KEY)) {
            if (value.frequency === this.keyFrequency) {
                return value.name;
            }
        }
        return null;
    }

    getNoteFrequency(channel, note) {
        let frequency = 0.0;
        if (this.isOverblow(channel, note) || this.isOverdraw(channel, note)) {
            frequency = this.getOverblowOverdrawFrequency(channel);
        } else {
            if (channel >= AbstractHarmonica.CHANNEL_MIN && channel <= AbstractHarmonica.CHANNEL_MAX) {
                if (note === 0) {
                    frequency = this.getChannelOutFrequency(channel);
                }
                if (note === 1) {
                    frequency = this.getChannelInFrequency(channel);
                }
                if (note > 1 && note <= AbstractHarmonica.NOTE_MAX) {
                    frequency = this.getNoteFrequency(channel, note - 1);
                    frequency = NoteUtils.addCentsToFrequency(-100.0, frequency);
                }
                if (note < 0 && note >= AbstractHarmonica.NOTE_MIN) {
                    frequency = this.getNoteFrequency(channel, note + 1);
                    frequency = NoteUtils.addCentsToFrequency(-100.0, frequency);
                }
            }
        }
        return AbstractHarmonica.round(frequency);
    }

    getNoteFrequencyMaximum(channel, note) {
        return NoteUtils.addCentsToFrequency(50.0, this.getNoteFrequency(channel, note));
    }

    getNoteFrequencyMinimum(channel, note) {
        return NoteUtils.addCentsToFrequency(-50.0, this.getNoteFrequency(channel, note));
    }

    hasInverseCentsHandling(channel) {
        return this.getNoteFrequency(channel, 0) > this.getNoteFrequency(channel, 1);
    }

    isNoteActive(channel, note, frequency) {
        const harpFrequency = this.getNoteFrequency(channel, note);
        return frequency <= NoteUtils.addCentsToFrequency(50.0, harpFrequency) && frequency >= NoteUtils.addCentsToFrequency(-50.0, harpFrequency);
    }

    getOverblowOverdrawFrequency(channel) {
        let frequency = 0.0;
        if (!this.hasInverseCentsHandling(channel)) {
            frequency = NoteUtils.addCentsToFrequency(100, this.getChannelInFrequency(channel));
        }
        if (this.hasInverseCentsHandling(channel)) {
            frequency = NoteUtils.addCentsToFrequency(100, this.getChannelOutFrequency(channel));
        }
        return frequency;
    }

    isOverblow(channel, note) {
        return note === -1 && !this.hasInverseCentsHandling(channel);
    }

    isOverdraw(channel, note) {
        return note === 2 && this.hasInverseCentsHandling(channel);
    }

    getHalfTonesIn() {
        throw new Error("Method not implemented.");
    }

    getHalfTonesOut() {
        throw new Error("Method not implemented.");
    }
}

export const KEY = {
    A: { name: "A", frequency: () => NoteLookup.getNote("A3").getValue() },
    A_FLAT: { name: "A_FLAT", frequency: () => NoteLookup.getNote("G#3").getValue() },
    B: { name: "B", frequency: () => NoteLookup.getNote("B3").getValue() },
    B_FLAT: { name: "B_FLAT", frequency: () => NoteLookup.getNote("A#3").getValue() },
    C: { name: "C", frequency: () => NoteLookup.getNote("C4").getValue() },
    D: { name: "D", frequency: () => NoteLookup.getNote("D4").getValue() },
    D_FLAT: { name: "D_FLAT", frequency: () => NoteLookup.getNote("C#4").getValue() },
    E: { name: "E", frequency: () => NoteLookup.getNote("E4").getValue() },
    E_FLAT: { name: "E_FLAT", frequency: () => NoteLookup.getNote("D#4").getValue() },
    F: { name: "F", frequency: () => NoteLookup.getNote("F4").getValue() },
    F_HASH: { name: "F_HASH", frequency: () => NoteLookup.getNote("F#4").getValue() },
    G: { name: "G", frequency: () => NoteLookup.getNote("G3").getValue() },
    HA_FLAT: { name: "HA_FLAT", frequency: () => NoteLookup.getNote("G#4").getValue() },
    HB_FLAT: { name: "HB_FLAT", frequency: () => NoteLookup.getNote("A#4").getValue() },
    HG: { name: "HG", frequency: () => NoteLookup.getNote("G4").getValue() },
    LA: { name: "LA", frequency: () => NoteLookup.getNote("A2").getValue() },
    LA_FLAT: { name: "LA_FLAT", frequency: () => NoteLookup.getNote("G#2").getValue() },
    LB: { name: "LB", frequency: () => NoteLookup.getNote("B2").getValue() },
    LB_FLAT: { name: "LB_FLAT", frequency: () => NoteLookup.getNote("A#2").getValue() },
    LC: { name: "LC", frequency: () => NoteLookup.getNote("C3").getValue() },
    LD: { name: "LD", frequency: () => NoteLookup.getNote("D3").getValue() },
    LD_FLAT: { name: "LD_FLAT", frequency: () => NoteLookup.getNote("C#3").getValue() },
    LE: { name: "LE", frequency: () => NoteLookup.getNote("E3").getValue() },
    LE_FLAT: { name: "LE_FLAT", frequency: () => NoteLookup.getNote("D#3").getValue() },
    LF: { name: "LF", frequency: () => NoteLookup.getNote("F3").getValue() },
    LF_HASH: { name: "LF_HASH", frequency: () => NoteLookup.getNote("F#3").getValue() },
    LG: { name: "LG", frequency: () => NoteLookup.getNote("G2").getValue() },
    LLE: { name: "LLE", frequency: () => NoteLookup.getNote("E2").getValue() },
    LLF: { name: "LLF", frequency: () => NoteLookup.getNote("F2").getValue() },
    LLF_HASH: { name: "LLF_HASH", frequency: () => NoteLookup.getNote("F#2").getValue() }
};

export const TUNE = {
    COUNTRY: "COUNTRY",
    DIMINISHED: "DIMINISHED",
    HARMONICMOLL: "HARMONICMOLL",
    MELODYMAKER: "MELODYMAKER",
    NATURALMOLL: "NATURALMOLL",
    PADDYRICHTER: "PADDYRICHTER",
    RICHTER: "RICHTER",
    CIRCULAR: "CIRCULAR",
    AUGMENTED: "AUGMENTED"
};

/**
 * The CountryHarmonica class extends AbstractHarmonica
 */
export class AugmentedHarmonica extends AbstractHarmonica {
    /**
     * The constant HALF_TONES_IN.
     * <p>D G B D F# A B D F A</p>
     */
    static HALF_TONES_IN = [0, 3, 7, 11, 15, 19, 23, 27, 31, 35, 39];

    /**
     * The constant HALF_TONES_OUT.
     * <p>C E G C E G C E G C</p>
     */
    static HALF_TONES_OUT = [0, 0, 4, 8, 12, 16, 20, 24, 28, 32, 36];

    /**
     * Instantiates a new Country harmonica.
     *
     * @param {number} keyFrequency - the key frequency
     */
    constructor(keyFrequency) {
        super(keyFrequency);
    }

    /**
     * @returns {number[]} The half tones in
     */
    getHalfTonesIn() {
        return AugmentedHarmonica.HALF_TONES_IN;
    }

    /**
     * @returns {number[]} The half tones out
     */
    getHalfTonesOut() {
        return AugmentedHarmonica.HALF_TONES_OUT;
    }

    /**
     * @returns {string} The tune name
     */
    getTuneName() {
        return 'AUGMENTED';
    }
}

/**
 * The CountryHarmonica class extends AbstractHarmonica
 */
export class CircularHarmonica extends AbstractHarmonica {
    /**
     * The constant HALF_TONES_IN.
     * <p>D G B D F# A B D F A</p>
     */
    static HALF_TONES_IN = [0, 2, 5, 9, 12, 16, 19, 22, 26, 29, 33];

    /**
     * The constant HALF_TONES_OUT.
     * <p>C E G C E G C E G C</p>
     */
    static HALF_TONES_OUT = [0, 0, 4, 7, 10, 14, 17, 21, 24, 28, 31];

    /**
     * Instantiates a new Country harmonica.
     *
     * @param {number} keyFrequency - the key frequency
     */
    constructor(keyFrequency) {
        super(keyFrequency);
    }

    /**
     * @returns {number[]} The half tones in
     */
    getHalfTonesIn() {
        return CircularHarmonica.HALF_TONES_IN;
    }

    /**
     * @returns {number[]} The half tones out
     */
    getHalfTonesOut() {
        return CircularHarmonica.HALF_TONES_OUT;
    }

    /**
     * @returns {string} The tune name
     */
    getTuneName() {
        return 'CIRCULAR';
    }
}



// Importing necessary modules (if any)
/**
 * The CountryHarmonica class extends AbstractHarmonica
 */
export class CountryHarmonica extends AbstractHarmonica {
    /**
     * The constant HALF_TONES_IN.
     * <p>D G B D F# A B D F A</p>
     */
    static HALF_TONES_IN = [0, 2, 7, 11, 14, 18, 21, 23, 26, 29, 33];

    /**
     * The constant HALF_TONES_OUT.
     * <p>C E G C E G C E G C</p>
     */
    static HALF_TONES_OUT = [0, 0, 4, 7, 12, 16, 19, 24, 28, 31, 36];

    /**
     * Instantiates a new Country harmonica.
     *
     * @param {number} keyFrequency - the key frequency
     */
    constructor(keyFrequency) {
        super(keyFrequency);
    }

    /**
     * @returns {number[]} The half tones in
     */
    getHalfTonesIn() {
        return CountryHarmonica.HALF_TONES_IN;
    }

    /**
     * @returns {number[]} The half tones out
     */
    getHalfTonesOut() {
        return CountryHarmonica.HALF_TONES_OUT;
    }

    /**
     * @returns {string} The tune name
     */
    getTuneName() {
        return 'COUNTRY';
    }
}

/**
 * The CountryHarmonica class extends AbstractHarmonica
 */
export class DiminishedHarmonica extends AbstractHarmonica {
    /**
     * The constant HALF_TONES_IN.
     * <p>D G B D F# A B D F A</p>
     */
    static HALF_TONES_IN = [0, 2, 7, 11, 14, 17, 21, 23, 26, 29, 33];

    /**
     * The constant HALF_TONES_OUT.
     * <p>C E G C E G C E G C</p>
     */
    static HALF_TONES_OUT = [0, 0, 4, 7, 12, 16, 19, 24, 28, 31, 36];

    /**
     * Instantiates a new Country harmonica.
     *
     * @param {number} keyFrequency - the key frequency
     */
    constructor(keyFrequency) {
        super(keyFrequency);
    }

    /**
     * @returns {number[]} The half tones in
     */
    getHalfTonesIn() {
        return DiminishedHarmonica.HALF_TONES_IN;
    }

    /**
     * @returns {number[]} The half tones out
     */
    getHalfTonesOut() {
        return DiminishedHarmonica.HALF_TONES_OUT;
    }

    /**
     * @returns {string} The tune name
     */
    getTuneName() {
        return 'DIMINISHED';
    }
}

/**
 * The CountryHarmonica class extends AbstractHarmonica
 */
export class HarmonicMollHarmonica extends AbstractHarmonica {
    /**
     * The constant HALF_TONES_IN.
     * <p>D G B D F# A B D F A</p>
     */
    static HALF_TONES_IN = [0, 2, 7, 11, 14, 17, 20, 23, 26, 29, 32];

    /**
     * The constant HALF_TONES_OUT.
     * <p>C E G C E G C E G C</p>
     */
    static HALF_TONES_OUT = [0, 0, 3, 7, 12, 15, 19, 24, 27, 31, 36];

    /**
     * Instantiates a new Country harmonica.
     *
     * @param {number} keyFrequency - the key frequency
     */
    constructor(keyFrequency) {
        super(keyFrequency);
    }

    /**
     * @returns {number[]} The half tones in
     */
    getHalfTonesIn() {
        return HarmonicMollHarmonica.HALF_TONES_IN;
    }

    /**
     * @returns {number[]} The half tones out
     */
    getHalfTonesOut() {
        return HarmonicMollHarmonica.HALF_TONES_OUT;
    }

    /**
     * @returns {string} The tune name
     */
    getTuneName() {
        return 'HARMONICMOLL';
    }
}

/**
 * The CountryHarmonica class extends AbstractHarmonica
 */
export class MelodyMakerHarmonica extends AbstractHarmonica {
    /**
     * The constant HALF_TONES_IN.
     * <p>D G B D F# A B D F A</p>
     */
    static HALF_TONES_IN = [0, 2, 7, 11, 14, 18, 21, 23, 26, 29, 33];

    /**
     * The constant HALF_TONES_OUT.
     * <p>C E G C E G C E G C</p>
     */
    static HALF_TONES_OUT = [0, 0, 4, 7, 12, 16, 19, 24, 28, 31, 36];

    /**
     * Instantiates a new Country harmonica.
     *
     * @param {number} keyFrequency - the key frequency
     */
    constructor(keyFrequency) {
        super(keyFrequency);
    }

    /**
     * @returns {number[]} The half tones in
     */
    getHalfTonesIn() {
        return CountryHarmonica.HALF_TONES_IN;
    }

    /**
     * @returns {number[]} The half tones out
     */
    getHalfTonesOut() {
        return CountryHarmonica.HALF_TONES_OUT;
    }

    /**
     * @returns {string} The tune name
     */
    getTuneName() {
        return 'COUNTRY';
    }
}

/**
 * The CountryHarmonica class extends AbstractHarmonica
 */
export class NaturalMollHarmonica extends AbstractHarmonica {
    /**
     * The constant HALF_TONES_IN.
     * <p>D G B D F# A B D F A</p>
     */
    static HALF_TONES_IN = [0, 2, 7, 11, 14, 18, 21, 23, 26, 29, 33];

    /**
     * The constant HALF_TONES_OUT.
     * <p>C E G C E G C E G C</p>
     */
    static HALF_TONES_OUT = [0, 0, 4, 7, 12, 16, 19, 24, 28, 31, 36];

    /**
     * Instantiates a new Country harmonica.
     *
     * @param {number} keyFrequency - the key frequency
     */
    constructor(keyFrequency) {
        super(keyFrequency);
    }

    /**
     * @returns {number[]} The half tones in
     */
    getHalfTonesIn() {
        return CountryHarmonica.HALF_TONES_IN;
    }

    /**
     * @returns {number[]} The half tones out
     */
    getHalfTonesOut() {
        return CountryHarmonica.HALF_TONES_OUT;
    }

    /**
     * @returns {string} The tune name
     */
    getTuneName() {
        return 'COUNTRY';
    }
}

/**
 * The CountryHarmonica class extends AbstractHarmonica
 */
export class PaddyRichterHarmonica extends AbstractHarmonica {
    /**
     * The constant HALF_TONES_IN.
     * <p>D G B D F# A B D F A</p>
     */
    static HALF_TONES_IN = [0, 2, 7, 11, 14, 18, 21, 23, 26, 29, 33];

    /**
     * The constant HALF_TONES_OUT.
     * <p>C E G C E G C E G C</p>
     */
    static HALF_TONES_OUT = [0, 0, 4, 7, 12, 16, 19, 24, 28, 31, 36];

    /**
     * Instantiates a new Country harmonica.
     *
     * @param {number} keyFrequency - the key frequency
     */
    constructor(keyFrequency) {
        super(keyFrequency);
    }

    /**
     * @returns {number[]} The half tones in
     */
    getHalfTonesIn() {
        return CountryHarmonica.HALF_TONES_IN;
    }

    /**
     * @returns {number[]} The half tones out
     */
    getHalfTonesOut() {
        return CountryHarmonica.HALF_TONES_OUT;
    }

    /**
     * @returns {string} The tune name
     */
    getTuneName() {
        return 'COUNTRY';
    }
}

/**
 * The CountryHarmonica class extends AbstractHarmonica
 */
export class RichterHarmonica extends AbstractHarmonica {
    /**
     * The constant HALF_TONES_IN.
     * <p>D G B D F# A B D F A</p>
     */
    static HALF_TONES_IN = [0, 2, 7, 11, 14, 17, 21, 23, 26, 29, 33];

    /**
     * The constant HALF_TONES_OUT.
     * <p>C E G C E G C E G C</p>
     */
    static HALF_TONES_OUT = [0, 0, 4, 7, 12, 16, 19, 24, 28, 31, 36];

    /**
     * Instantiates a new Country harmonica.
     *
     * @param {number} keyFrequency - the key frequency
     */
    constructor(keyFrequency) {
        super(keyFrequency);
    }

    /**
     * @returns {number[]} The half tones in
     */
    getHalfTonesIn() {
        return RichterHarmonica.HALF_TONES_IN;
    }

    /**
     * @returns {number[]} The half tones out
     */
    getHalfTonesOut() {
        return RichterHarmonica.HALF_TONES_OUT;
    }

    /**
     * @returns {string} The tune name
     */
    getTuneName() {
        return 'RICHTER';
    }
}

