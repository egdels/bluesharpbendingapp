/**
 * Utility class for performing operations related to musical notes and frequencies.
 */
class NoteUtils {
    /**
     * Represents the number of decimal places to be used for rounding
     * or formatting numbers in calculations or displays.
     *
     * @constant {number}
     * @default 3
     */
    static DECIMAL_PRECISION = 3; // Dezimalstellen zur Rundung

    /**
     * Calculates the number of cents between two given frequencies.
     *
     * @param {number} f1 - The frequency of the first tone. Must be a positive number.
     * @param {number} f2 - The frequency of the second tone. Must be a positive number.
     * @return {number} The difference in cents between the two given frequencies.
     * @throws {Error} If either of the frequencies is less than or equal to zero.
     */
    static getCents(f1, f2) {
        if (f1 <= 0 || f2 <= 0) {
            throw new Error("Frequenzen müssen positiv sein.");
        }
        return 1200 * (Math.log2(f1 / f2));
    }

    /**
     * Rounds a numerical value to a specified decimal precision.
     *
     * @param {number} value The numerical value to be rounded.
     * @return {number} The rounded numerical value.
     * @throws {Error} If the input value is not a valid number.
     */
    static round(value) {
        if (isNaN(value)) {
            throw new Error("Ein gültiger numerischer Wert wird erwartet.");
        }
        const factor = Math.pow(10, NoteUtils.DECIMAL_PRECISION);
        return Math.floor(value * factor) / factor;
    }

    /**
     * Adjusts a given frequency by a specified number of cents.
     *
     * @param {number} cents - The number of cents to offset the frequency. Positive values increase, and negative values decrease the frequency.
     * @param {number} frequency - The base frequency to which the cents offset is applied. Must be greater than zero.
     * @return {number} The new frequency after applying the cents adjustment.
     * @throws {Error} If the provided frequency is not greater than zero.
     */
    static addCentsToFrequency(cents, frequency) {
        if (frequency <= 0) {
            throw new Error("Die Frequenz muss positiv sein.");
        }
        return frequency * Math.pow(2, cents / 1200);
    }
}

export default NoteUtils;