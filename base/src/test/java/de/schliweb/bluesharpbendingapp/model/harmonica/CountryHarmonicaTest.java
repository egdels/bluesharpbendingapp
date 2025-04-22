package de.schliweb.bluesharpbendingapp.model.harmonica;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the CountryHarmonica class.
 */
class CountryHarmonicaTest {

    private CountryHarmonica harmonica;

    @BeforeEach
    void setUp() {
        // Create a C harmonica using the KEY enum
        harmonica = (CountryHarmonica) AbstractHarmonica.create(AbstractHarmonica.KEY.C, AbstractHarmonica.TUNE.COUNTRY);
    }

    @Test
    void testGetTuneName() {
        assertEquals(AbstractHarmonica.TUNE.COUNTRY.name(), harmonica.getTuneName());
    }

    @Test
    void testHalfTonesIn() {
        // Test that the half-tones for inhaling are correctly defined
        // The actual frequencies will depend on the key frequency and the half-tone arrays

        // Channel 1 should have the frequency for 0 half-tones from the key
        double channel1Frequency = harmonica.getChannelInFrequency(1);

        // Channel 2 should have the frequency for 2 half-tones from the key
        double channel2Frequency = harmonica.getChannelInFrequency(2);
        assertTrue(channel2Frequency > channel1Frequency);

        // Channel 6 should have the frequency for 18 half-tones from the key (different from Richter)
        double channel6Frequency = harmonica.getChannelInFrequency(6);
        assertTrue(channel6Frequency > channel2Frequency);
    }

    @Test
    void testHalfTonesOut() {
        // Test that the half-tones for exhaling are correctly defined
        // The actual frequencies will depend on the key frequency and the half-tone arrays

        // Channel 1 should have the frequency for 0 half-tones from the key
        double channel1Frequency = harmonica.getChannelOutFrequency(1);

        // Channel 3 should have the frequency for 4 half-tones from the key
        double channel3Frequency = harmonica.getChannelOutFrequency(3);
        assertTrue(channel3Frequency > channel1Frequency);

        // Channel 5 should have the frequency for 12 half-tones from the key
        double channel5Frequency = harmonica.getChannelOutFrequency(5);
        assertTrue(channel5Frequency > channel3Frequency);
    }

    @Test
    void testGetNoteFrequency() {
        // Test note frequencies for different channels and notes

        // Channel 2, Note 0 (blow) should be a different frequency
        double channel2Note0Frequency = harmonica.getNoteFrequency(2, 0);

        // Channel 2, Note 1 (draw) should be higher than the blow frequency
        double channel2Note1Frequency = harmonica.getNoteFrequency(2, 1);
        assertTrue(channel2Note1Frequency > channel2Note0Frequency);
    }

    @Test
    void testDrawBendingTonesCount() {
        // Test that draw bending tones are calculated correctly
        // We don't test specific values, but verify that the calculation is consistent

        // Get the half-tone arrays
        int[] halfTonesIn = harmonica.getHalfTonesIn();
        int[] halfTonesOut = harmonica.getHalfTonesOut();

        // Test a few channels
        for (int channel = 1; channel <= 6; channel++) {
            int expected = Math.max(0, halfTonesIn[channel] - halfTonesOut[channel] - 1);
            assertEquals(expected, harmonica.getDrawBendingTonesCount(channel), "Draw bending tones for channel " + channel);
        }
    }

    @Test
    void testBlowBendingTonesCount() {
        // Test that blow bending tones are calculated correctly
        // We don't test specific values, but verify that the calculation is consistent

        // Get the half-tone arrays
        int[] halfTonesIn = harmonica.getHalfTonesIn();
        int[] halfTonesOut = harmonica.getHalfTonesOut();

        // Test a few channels
        for (int channel = 8; channel <= 10; channel++) {
            int expected = Math.max(0, halfTonesOut[channel] - halfTonesIn[channel] - 1);
            assertEquals(expected, harmonica.getBlowBendingTonesCount(channel), "Blow bending tones for channel " + channel);
        }
    }

    @Test
    void testGetNoteName() {
        // Test note names for different channels and notes

        // Channel 1, Note 0 (blow) should be C
        assertEquals("C4", harmonica.getNoteName(1, 0));

        // Channel 4, Note 1 (draw) should be D
        assertEquals("D5", harmonica.getNoteName(4, 1));

        // Channel 6, Note 0 (blow) should be G
        assertEquals("G5", harmonica.getNoteName(6, 0));
    }
}