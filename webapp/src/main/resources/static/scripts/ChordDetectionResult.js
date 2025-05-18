/**
 * Represents the result of a chord detection operation.
 * This class stores multiple detected pitch frequencies in Hz
 * and a confidence score indicating the reliability of the detection.
 *
 * This is an extension of the PitchDetectionResult concept to handle
 * polyphonic sounds (chords) where multiple pitches are present simultaneously.
 */
class ChordDetectionResult {
    /**
     * Creates a new ChordDetectionResult with the given pitches and confidence.
     *
     * @param {Array<number>} pitches - An array of detected pitch frequencies in Hz
     * @param {number} confidence - A value between 0.0 and 1.0 indicating the reliability of the detection
     */
    constructor(pitches, confidence) {
        // Defensive copy to ensure immutability
        this.pitches = [...pitches];
        this.confidence = confidence;
    }

    /**
     * Creates a new ChordDetectionResult with the given pitches and confidence.
     *
     * @param {Array<number>} pitches - An array of detected pitch frequencies in Hz
     * @param {number} confidence - A value between 0.0 and 1.0 indicating the reliability of the detection
     * @returns {ChordDetectionResult} A new ChordDetectionResult instance
     */
    static of(pitches, confidence) {
        return new ChordDetectionResult(pitches, confidence);
    }

    /**
     * Creates a ChordDetectionResult from a single pitch detection result.
     *
     * @param {Object} result - The pitch detection result to convert, with pitch and confidence properties
     * @returns {ChordDetectionResult} A new ChordDetectionResult containing the single pitch
     */
    static fromPitchDetectionResult(result) {
        if (result.pitch === -1) {
            return new ChordDetectionResult([], 0.0);
        }
        return new ChordDetectionResult([result.pitch], result.confidence);
    }

    /**
     * Checks if any pitches were detected.
     *
     * @returns {boolean} True if at least one pitch was detected, false otherwise
     */
    hasPitches() {
        return this.pitches.length > 0;
    }

    /**
     * Gets the number of detected pitches.
     *
     * @returns {number} The number of detected pitches
     */
    getPitchCount() {
        return this.pitches.length;
    }

    /**
     * Gets the pitch at the specified index.
     *
     * @param {number} index - The index of the pitch to get
     * @returns {number} The pitch at the specified index
     * @throws {Error} If the index is out of range
     */
    getPitch(index) {
        if (index < 0 || index >= this.pitches.length) {
            throw new Error("Index out of range");
        }
        return this.pitches[index];
    }

    /**
     * Returns a string representation of this ChordDetectionResult instance.
     *
     * @returns {string} A string representation of the ChordDetectionResult instance
     */
    toString() {
        return `ChordDetectionResult{pitches=[${this.pitches.join(", ")}], confidence=${this.confidence}}`;
    }
}

export default ChordDetectionResult;