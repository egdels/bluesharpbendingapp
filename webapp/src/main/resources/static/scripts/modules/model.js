
import { AbstractHarmonica } from './harmonica.js';
import { NoteLookup } from './harmonica.js';

export class MainModel {

    constructor() {
        this.harmonica = null;
        this.microphone = null;
        this.storedAlgorithmIndex = 1;
        this.storedKeyIndex = 4;
        this.storedMicrophoneIndex = 0;
        this.storedTuneIndex = 6;
        this.storedConcertPitchIndex = 11;
    }

    static createFromString(string) {
        const strings = string.replace("[", "").replace("]", "").split(",");
        const model = new MainModel();
        const methods = Object.getOwnPropertyNames(Object.getPrototypeOf(model)).filter(prop => typeof model[prop] === 'function');
        strings.forEach(entry => {
            entry = entry.replace(/^get/, 'set');
            if (entry.includes(":")) {
                const [m, p] = entry.split(":");
                methods.forEach(method => {
                    if (method === m) {
                        try {
                            model[method](parseInt(p));
                        } catch (e) {
                            console.log(e.message);
                        }
                    }
                });
            }
        });
        return model;
    }

    getAlgorithms() {
        return this.microphone.getSupportedAlgorithms();
    }

    getHarmonica() {
        return this.harmonica;
    }

    setHarmonica(harmonica) {
        this.harmonica = harmonica;
    }

    getKeys() {
        return AbstractHarmonica.getSupporterKeys();
    }

    getMicrophone() {
        return this.microphone;
    }

    setMicrophone(microphone) {
        this.microphone = microphone;
    }

    getMicrophones() {
        return this.microphone.getSupportedMicrophones();
    }

    getConcertPitches() {
        return NoteLookup.getSupportedConcertPitches();
    }

    getSelectedAlgorithmIndex() {
        const algorithms = this.getAlgorithms();
        let index = 0;
        algorithms.forEach((algorithm, i) => {
            if (algorithm === this.microphone.getAlgorithm()) {
                index = i;
            }
        });
        return index;
    }

    getSelectedKeyIndex() {
        const keys = this.getKeys();
        let index = 0;
        keys.forEach((key, i) => {
            if (key === this.harmonica.getKeyName()) {
                index = i;
            }
        });
        return index;
    }

    getSelectedMicrophoneIndex() {
        const microphones = this.getMicrophones();
        let index = 0;
        microphones.forEach((mic, i) => {
            if (mic === this.microphone.getName()) {
                index = i;
            }
        });
        return index;
    }

    getSelectedTuneIndex() {
        const tunes = this.getTunes();
        let index = 0;
        tunes.forEach((tune, i) => {
            if (tune === this.harmonica.getTuneName()) {
                index = i;
            }
        });
        return index;
    }

    getSelectedConcertPitchIndex() {
        const pitches = this.getConcertPitches();
        let index = 0;
        pitches.forEach((pitch, i) => {
            if (pitch === NoteLookup.getConcertPitchName()) {
                index = i;
            }
        });
        return index;
    }

    getStoredAlgorithmIndex() {
        return this.storedAlgorithmIndex;
    }

    setStoredAlgorithmIndex(algorithmIndex) {
        this.storedAlgorithmIndex = algorithmIndex;
    }

    getStoredKeyIndex() {
        return this.storedKeyIndex;
    }

    setStoredKeyIndex(keyIndex) {
        this.storedKeyIndex = keyIndex;
    }

    getStoredMicrophoneIndex() {
        return this.storedMicrophoneIndex;
    }

    setStoredMicrophoneIndex(microphoneIndex) {
        this.storedMicrophoneIndex = microphoneIndex;
    }

    getStoredTuneIndex() {
        return this.storedTuneIndex;
    }

    setStoredTuneIndex(storedTuneIndex) {
        this.storedTuneIndex = storedTuneIndex;
    }

    getStoredConcertPitchIndex() {
        return this.storedConcertPitchIndex;
    }

    setStoredConcertPitchIndex(storedConcertPitchIndex) {
        this.storedConcertPitchIndex = storedConcertPitchIndex;
    }

    getTunes() {
        return AbstractHarmonica.getSupportedTunes();
    }

    getString() {
        const methods = Object.getOwnPropertyNames(Object.getPrototypeOf(this)).filter(prop => typeof this[prop] === 'function');
        const stringList = [];
        methods.forEach(m => {
            if (m.startsWith("getStored") && typeof this[m]() === 'number') {
                try {
                    stringList.push(`${m}:${this[m]()}`);
                } catch (e) {
                    console.log(e.message);
                }
            }
        });
        return stringList.toString();
    }
}

