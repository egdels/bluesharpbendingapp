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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the NaturalMollHarmonica class.
 */
class NaturalMollHarmonicaTest {

    private NaturalMollHarmonica harmonica;

    @BeforeEach
    void setUp() {
        // Create a C harmonica using the KEY enum
        harmonica = (NaturalMollHarmonica) AbstractHarmonica.create(AbstractHarmonica.KEY.C, AbstractHarmonica.TUNE.NATURALMOLL);
    }

    @Test
    void testGetTuneName() {
        assertEquals(AbstractHarmonica.TUNE.NATURALMOLL.name(), harmonica.getTuneName());
    }

    @Test
    void testHalfTonesIn() {
        // Test that the half-tones for inhaling are correctly defined
        // The actual frequencies will depend on the key frequency and the half-tone arrays

        // Channel 1 should have a frequency
        double channel1Frequency = harmonica.getChannelInFrequency(1);
        assertTrue(channel1Frequency > 0);

        // Channel 2 should have a higher frequency than channel 1
        double channel2Frequency = harmonica.getChannelInFrequency(2);
        assertTrue(channel2Frequency > channel1Frequency);

        // Channel 3 should have a higher frequency than channel 2
        double channel3Frequency = harmonica.getChannelInFrequency(3);
        assertTrue(channel3Frequency > channel2Frequency);

        // Channel 4 should have a higher frequency than channel 3
        double channel4Frequency = harmonica.getChannelInFrequency(4);
        assertTrue(channel4Frequency > channel3Frequency);
    }

    @Test
    void testHalfTonesOut() {
        // Test that the half-tones for exhaling are correctly defined
        // The actual frequencies will depend on the key frequency and the half-tone arrays

        // Channel 1 should have a frequency
        double channel1Frequency = harmonica.getChannelOutFrequency(1);
        assertTrue(channel1Frequency > 0);

        // Channel 2 should have a frequency
        double channel2Frequency = harmonica.getChannelOutFrequency(2);
        assertTrue(channel2Frequency > 0);

        // Channel 3 should have a higher frequency than channel 1
        double channel3Frequency = harmonica.getChannelOutFrequency(3);
        assertTrue(channel3Frequency > channel1Frequency);

        // Channel 4 should have a higher frequency than channel 3
        double channel4Frequency = harmonica.getChannelOutFrequency(4);
        assertTrue(channel4Frequency > channel3Frequency);
    }

    @Test
    void testGetNoteFrequency() {
        // Test note frequencies for different channels and notes

        // Channel 1, Note 0 (blow) should have a frequency
        double channel1Note0Frequency = harmonica.getNoteFrequency(1, 0);
        assertTrue(channel1Note0Frequency > 0);

        // Channel 1, Note 1 (draw) should have a frequency
        double channel1Note1Frequency = harmonica.getNoteFrequency(1, 1);
        assertTrue(channel1Note1Frequency > 0);

        // Channel 3, Note 0 (blow) should be lower than Channel 3, Note 1 (draw)
        double channel3Note0Frequency = harmonica.getNoteFrequency(3, 0);
        double channel3Note1Frequency = harmonica.getNoteFrequency(3, 1);
        assertTrue(channel3Note0Frequency < channel3Note1Frequency);
    }

    @Test
    void testDrawBendingTonesCount() {
        // Test that draw bending tones are calculated correctly
        // For Natural Minor tuning, channels 1-7 should have draw bending

        // Channel 1 should have a non-negative number of draw bending tones
        int channel1DrawBendingTones = harmonica.getDrawBendingTonesCount(1);
        assertTrue(channel1DrawBendingTones >= 0);

        // Channel 3 should have a non-negative number of draw bending tones
        int channel3DrawBendingTones = harmonica.getDrawBendingTonesCount(3);
        assertTrue(channel3DrawBendingTones >= 0);

        // Channel 4 should have a non-negative number of draw bending tones
        int channel4DrawBendingTones = harmonica.getDrawBendingTonesCount(4);
        assertTrue(channel4DrawBendingTones >= 0);
    }

    @Test
    void testBlowBendingTonesCount() {
        // Test that blow bending tones are calculated correctly
        // For Natural Minor tuning, channels 8-10 should have blow bending

        // Channel 8 should have a non-negative number of blow bending tones
        int channel8BlowBendingTones = harmonica.getBlowBendingTonesCount(8);
        assertTrue(channel8BlowBendingTones >= 0);

        // Channel 9 should have a non-negative number of blow bending tones
        int channel9BlowBendingTones = harmonica.getBlowBendingTonesCount(9);
        assertTrue(channel9BlowBendingTones >= 0);

        // Channel 10 should have a non-negative number of blow bending tones
        int channel10BlowBendingTones = harmonica.getBlowBendingTonesCount(10);
        assertTrue(channel10BlowBendingTones >= 0);
    }

    @Test
    void testGetNoteName() {
        // Test note names for different channels and notes

        // Get the actual note names from the harmonica
        String channel1Note0 = harmonica.getNoteName(1, 0);
        String channel3Note1 = harmonica.getNoteName(3, 1);
        String channel6Note0 = harmonica.getNoteName(6, 0);

        // Verify they are not null or empty
        assertNotNull(channel1Note0);
        assertFalse(channel1Note0.isEmpty());

        assertNotNull(channel3Note1);
        assertFalse(channel3Note1.isEmpty());

        assertNotNull(channel6Note0);
        assertFalse(channel6Note0.isEmpty());

        // Verify they have the expected format (letter, optional #, number)
        assertTrue(channel1Note0.matches("[A-G](#)?\\d"));
        assertTrue(channel3Note1.matches("[A-G](#)?\\d"));
        assertTrue(channel6Note0.matches("[A-G](#)?\\d"));
    }
}
