package de.schliweb.bluesharpbendingapp.utils;
/*
 * Copyright (c) 2023 Christian Kierdorf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for the NoteUtils utility class.
 * This class contains tests for all public methods in NoteUtils.
 */
class NoteUtilsTest {

    /**
     * Tests the getCents method with various frequency pairs.
     * The method should calculate the difference in cents between two frequencies.
     *
     * @param f1            the first frequency
     * @param f2            the second frequency
     * @param expectedCents the expected difference in cents
     */
    @ParameterizedTest
    @CsvSource({"440.0, 440.0, 0.0",           // Same frequency, 0 cents
            "466.16, 440.0, 100.0",        // A4 to A4#, 100 cents (1 semitone)
            "880.0, 440.0, 1200.0",        // A4 to A5, 1200 cents (1 octave)
            "220.0, 440.0, -1200.0",       // A4 to A3, -1200 cents (1 octave down)
            "493.88, 440.0, 200.0",        // A4 to B4, 200 cents (2 semitones)
            "415.30, 440.0, -100.0",       // A4 to G#4, -100 cents (1 semitone down)
            "554.37, 440.0, 400.0"         // A4 to C#5, 400 cents (4 semitones)
    })
    void testGetCents(double f1, double f2, double expectedCents) {
        double actualCents = NoteUtils.getCents(f1, f2);
        // Allow for a small margin of error due to floating-point calculations
        assertEquals(expectedCents, actualCents, 0.1);
    }

    /**
     * Tests the round method with various values.
     * The method should round the given value to 3 decimal places using floor rounding mode.
     */
    @Test
    void testRound() {
        // Test with exact values
        assertEquals(123.456, NoteUtils.round(123.456), "Exact value should remain the same");

        // Test with values that should be rounded down
        assertEquals(123.456, NoteUtils.round(123.4567), "Value should be rounded down to 3 decimal places");
        assertEquals(123.456, NoteUtils.round(123.4569), "Value should be rounded down to 3 decimal places");

        // Test with negative values
        assertEquals(-123.456, NoteUtils.round(-123.456), "Exact negative value should remain the same");
        assertEquals(-123.457, NoteUtils.round(-123.4567), "Negative value should be rounded down to 3 decimal places");

        // Test with zero
        assertEquals(0.0, NoteUtils.round(0.0), "Zero should remain zero");

        // Test with very small values
        assertEquals(0.001, NoteUtils.round(0.001), "Small value should be preserved");
        assertEquals(0.001, NoteUtils.round(0.0014), "Small value should be rounded down");
        assertEquals(0.0, NoteUtils.round(0.0004), "Very small value should be rounded to zero");
    }

    /**
     * Tests the addCentsToFrequency method with various inputs.
     * The method should adjust a frequency by a specified number of cents.
     *
     * @param cents             the number of cents to add
     * @param frequency         the base frequency
     * @param expectedFrequency the expected adjusted frequency
     */
    @ParameterizedTest
    @CsvSource({"0.0, 440.0, 440.0",           // No change
            "100.0, 440.0, 466.16",        // A4 to A4#, up 1 semitone
            "1200.0, 440.0, 880.0",        // A4 to A5, up 1 octave
            "-1200.0, 440.0, 220.0",       // A4 to A3, down 1 octave
            "200.0, 440.0, 493.88",        // A4 to B4, up 2 semitones
            "-100.0, 440.0, 415.30",       // A4 to G#4, down 1 semitone
            "400.0, 440.0, 554.37"         // A4 to C#5, up 4 semitones
    })
    void testAddCentsToFrequency(double cents, double frequency, double expectedFrequency) {
        double actualFrequency = NoteUtils.addCentsToFrequency(cents, frequency);
        // Allow for a small margin of error due to floating-point calculations
        assertEquals(expectedFrequency, actualFrequency, 0.01);
    }

    /**
     * Tests the addCentsToFrequency method with edge cases.
     */
    @Test
    void testAddCentsToFrequencyEdgeCases() {
        // Test with zero frequency
        assertEquals(0.0, NoteUtils.addCentsToFrequency(100.0, 0.0), "Adding cents to zero frequency should return zero");

        // Test with very large cents values
        double veryHighFrequency = NoteUtils.addCentsToFrequency(12000.0, 440.0); // 10 octaves up
        assertEquals(440.0 * Math.pow(2, 10), veryHighFrequency, 0.01);

        // Test with very small frequency
        double veryLowFrequency = 0.001;
        double adjustedFrequency = NoteUtils.addCentsToFrequency(1200.0, veryLowFrequency); // 1 octave up
        assertEquals(veryLowFrequency * 2, adjustedFrequency, 0.00001);
    }
}