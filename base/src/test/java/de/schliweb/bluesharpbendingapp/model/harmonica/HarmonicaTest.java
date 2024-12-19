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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The type Harmonica test.
 */
class HarmonicaTest {

    /**
     * The constant A4.
     */
    private static final double A4 = 440.000;

    /**
     * The constant A5.
     */
    private static final double A5 = 880.001;

    /**
     * The constant A6.
     */
    private static final double A6 = 1760.002;

    /**
     * The constant Ab4.
     */
    private static final double Ab4 = 415.304;

    /**
     * The constant Ab5.
     */
    private static final double Ab5 = 830.61;

    /**
     * The constant B4.
     */
    private static final double B4 = 493.884;

    /**
     * The constant B5.
     */
    private static final double B5 = 987.768;

    /**
     * The constant B6.
     */
    private static final double B6 = 1975.536;

    /**
     * The constant Bb4.
     */
    private static final double Bb4 = 466.164;

    /**
     * The constant Bb6.
     */
    private static final double Bb6 = 1864.657;

    /**
     * The constant C4.
     */
    private static final double C4 = 261.626;

    /**
     * The constant C5.
     */
    private static final double C5 = 523.252;

    /**
     * The constant C6.
     */
    private static final double C6 = 1046.504;

    /**
     * The constant C7.
     */
    private static final double C7 = 2093.008;

    /**
     * The constant D4.
     */
    private static final double D4 = 293.665;

    /**
     * The constant D5.
     */
    private static final double D5 = 587.330;

    /**
     * The constant D6.
     */
    private static final double D6 = 1174.661;

    /**
     * The constant Db4.
     */
    private static final double Db4 = 277.182;

    /**
     * The constant Db5.
     */
    private static final double Db5 = 554.365;

    /**
     * The constant E4.
     */
    private static final double E4 = 329.628;

    /**
     * The constant E5.
     */
    private static final double E5 = 659.256;

    /**
     * The constant E6.
     */
    private static final double E6 = 1318.512;

    /**
     * The constant Eb6.
     */
    private static final double Eb6 = 1244.509;

    /**
     * The constant F4.
     */
    private static final double F4 = 349.227;

    /**
     * The constant F5.
     */
    private static final double F5 = 698.457;

    /**
     * The constant F6.
     */
    private static final double F6 = 1396.915;

    /**
     * The constant Fis4.
     */
    private static final double Fis4 = 369.994;

    /**
     * The constant Fis6.
     */
    private static final double Fis6 = 1479.979;

    /**
     * The constant G4.
     */
    private static final double G4 = 391.996;

    /**
     * The constant G5.
     */
    private static final double G5 = 783.992;

    /**
     * The constant G6.
     */
    private static final double G6 = 1567.984;

    /**
     * The constant Dis4.
     */
    private static final double Dis4 = 311.127;

    /**
     * The constant Gis4.
     */
    private static final double Gis4 = 415.305;

    /**
     * The constant Fis5.
     */
    private static final double Fis5 = 739.989;

    /**
     * The constant Dis5.
     */
    private static final double Dis5 = 622.254;

    /**
     * The constant Ais5.
     */
    private static final double Ais5 = 932.328;

    /**
     * The constant Cis6.
     */
    private static final double Cis6 = 1108.73;

    /**
     * The constant Gis6.
     */
    private static final double Gis6 = 1661.22;

    /**
     * The constant Cis7.
     */
    private static final double Cis7 = 2217.46;

    /**
     * The constant harmonica.
     */
    private static Harmonica harmonica;

    /**
     * Test get blow bending tones count.
     */
    @Test
    void testGetBlowBendingTonesCount() {
        harmonica = new RichterHarmonica(AbstractHarmonica.KEY.C.getFrequency());

        assertEquals(0, harmonica.getBlowBendingTonesCount(1));
        assertEquals(0, harmonica.getBlowBendingTonesCount(2));
        assertEquals(0, harmonica.getBlowBendingTonesCount(3));
        assertEquals(0, harmonica.getBlowBendingTonesCount(4));
        assertEquals(0, harmonica.getBlowBendingTonesCount(5));
        assertEquals(0, harmonica.getBlowBendingTonesCount(6));
        assertEquals(0, harmonica.getBlowBendingTonesCount(7));
        assertEquals(1, harmonica.getBlowBendingTonesCount(8));
        assertEquals(1, harmonica.getBlowBendingTonesCount(9));
        assertEquals(2, harmonica.getBlowBendingTonesCount(10));

        harmonica = new HarmonicMollHarmonica(AbstractHarmonica.KEY.C.getFrequency());

        assertEquals(0, harmonica.getBlowBendingTonesCount(1));
        assertEquals(0, harmonica.getBlowBendingTonesCount(2));
        assertEquals(0, harmonica.getBlowBendingTonesCount(3));
        assertEquals(0, harmonica.getBlowBendingTonesCount(4));
        assertEquals(0, harmonica.getBlowBendingTonesCount(5));
        assertEquals(0, harmonica.getBlowBendingTonesCount(6));
        assertEquals(0, harmonica.getBlowBendingTonesCount(7));
        assertEquals(0, harmonica.getBlowBendingTonesCount(8));
        assertEquals(1, harmonica.getBlowBendingTonesCount(9));
        assertEquals(3, harmonica.getBlowBendingTonesCount(10));
    }

    /**
     * Test get draw bending note count.
     */
    @Test
    void testGetDrawBendingNoteCount() {

        harmonica = new RichterHarmonica(AbstractHarmonica.KEY.C.getFrequency());

        assertEquals(1, harmonica.getDrawBendingTonesCount(1));
        assertEquals(2, harmonica.getDrawBendingTonesCount(2));
        assertEquals(3, harmonica.getDrawBendingTonesCount(3));
        assertEquals(1, harmonica.getDrawBendingTonesCount(4));
        assertEquals(0, harmonica.getDrawBendingTonesCount(5));
        assertEquals(1, harmonica.getDrawBendingTonesCount(6));
        assertEquals(0, harmonica.getDrawBendingTonesCount(7));
        assertEquals(0, harmonica.getDrawBendingTonesCount(8));
        assertEquals(0, harmonica.getDrawBendingTonesCount(9));
        assertEquals(0, harmonica.getDrawBendingTonesCount(10));

        harmonica = new HarmonicMollHarmonica(AbstractHarmonica.KEY.C.getFrequency());

        assertEquals(1, harmonica.getDrawBendingTonesCount(1));
        assertEquals(3, harmonica.getDrawBendingTonesCount(2));
        assertEquals(3, harmonica.getDrawBendingTonesCount(3));
        assertEquals(1, harmonica.getDrawBendingTonesCount(4));
        assertEquals(1, harmonica.getDrawBendingTonesCount(5));
        assertEquals(0, harmonica.getDrawBendingTonesCount(6));
        assertEquals(0, harmonica.getDrawBendingTonesCount(7));
        assertEquals(0, harmonica.getDrawBendingTonesCount(8));
        assertEquals(0, harmonica.getDrawBendingTonesCount(9));
        assertEquals(0, harmonica.getDrawBendingTonesCount(10));
    }

    /**
     * Test get note frequency.
     */
    @Test
    void testGetNoteFrequency() {
        harmonica = new RichterHarmonica(AbstractHarmonica.KEY.C.getFrequency());

        assertEquals(C4, harmonica.getNoteFrequency(1, 0));
        assertEquals(E4, harmonica.getNoteFrequency(2, 0));
        assertEquals(G4, harmonica.getNoteFrequency(3, 0));
        assertEquals(C5, harmonica.getNoteFrequency(4, 0));
        assertEquals(E5, harmonica.getNoteFrequency(5, 0));
        assertEquals(G5, harmonica.getNoteFrequency(6, 0));
        assertEquals(C6, harmonica.getNoteFrequency(7, 0));
        assertEquals(E6, harmonica.getNoteFrequency(8, 0));
        assertEquals(G6, harmonica.getNoteFrequency(9, 0));
        assertEquals(C7, harmonica.getNoteFrequency(10, 0));

        assertEquals(D4, harmonica.getNoteFrequency(1, 1));
        assertEquals(G4, harmonica.getNoteFrequency(2, 1));
        assertEquals(B4, harmonica.getNoteFrequency(3, 1));
        assertEquals(D5, harmonica.getNoteFrequency(4, 1));
        assertEquals(F5, harmonica.getNoteFrequency(5, 1));
        assertEquals(A5, harmonica.getNoteFrequency(6, 1));
        assertEquals(B5, harmonica.getNoteFrequency(7, 1));
        assertEquals(D6, harmonica.getNoteFrequency(8, 1));
        assertEquals(F6, harmonica.getNoteFrequency(9, 1));
        assertEquals(A6, harmonica.getNoteFrequency(10, 1));

        // bendings
        assertEquals(Db4, harmonica.getNoteFrequency(1, 2));
        assertEquals(Fis4, harmonica.getNoteFrequency(2, 2));
        assertEquals(F4, harmonica.getNoteFrequency(2, 3));
        assertEquals(Bb4, harmonica.getNoteFrequency(3, 2));
        assertEquals(A4, harmonica.getNoteFrequency(3, 3));
        assertEquals(Ab4, harmonica.getNoteFrequency(3, 4));
        assertEquals(Db5, harmonica.getNoteFrequency(4, 2));
        assertEquals(Ab5, harmonica.getNoteFrequency(6, 2));

        assertEquals(Eb6, harmonica.getNoteFrequency(8, -1));
        assertEquals(Fis6, harmonica.getNoteFrequency(9, -1));
        assertEquals(B6, harmonica.getNoteFrequency(10, -1));
        assertEquals(Bb6, harmonica.getNoteFrequency(10, -2));
    }

    /**
     * Test is note active.
     */
    @Test
    void testIsNoteActive() {
        harmonica = new RichterHarmonica(AbstractHarmonica.KEY.C.getFrequency());

        assertTrue(harmonica.isNoteActive(1, 0, C4));
        assertTrue(harmonica.isNoteActive(2, 0, E4));
        assertTrue(harmonica.isNoteActive(3, 0, G4));
        assertTrue(harmonica.isNoteActive(4, 0, C5));
        assertTrue(harmonica.isNoteActive(5, 0, E5));
        assertTrue(harmonica.isNoteActive(6, 0, G5));
        assertTrue(harmonica.isNoteActive(7, 0, C6));
        assertTrue(harmonica.isNoteActive(8, 0, E6));
        assertTrue(harmonica.isNoteActive(9, 0, G6));
        assertTrue(harmonica.isNoteActive(10, 0, C7));

        assertTrue(harmonica.isNoteActive(1, 1, D4));
        assertTrue(harmonica.isNoteActive(2, 1, G4));
        assertTrue(harmonica.isNoteActive(3, 1, B4));
        assertTrue(harmonica.isNoteActive(4, 1, D5));
        assertTrue(harmonica.isNoteActive(5, 1, F5));
        assertTrue(harmonica.isNoteActive(6, 1, A5));
        assertTrue(harmonica.isNoteActive(7, 1, B5));
        assertTrue(harmonica.isNoteActive(8, 1, D6));
        assertTrue(harmonica.isNoteActive(9, 1, F6));
        assertTrue(harmonica.isNoteActive(10, 1, A6));

        assertTrue(harmonica.isNoteActive(3, 3, A4));
        assertTrue(harmonica.isNoteActive(2, 3, F4));
    }

    /**
     * Test overblow overdraw.
     */
    @Test
    void testOverblowOverdraw() {
        harmonica = new RichterHarmonica(AbstractHarmonica.KEY.C.getFrequency());
        String note = NoteLookup.getNoteName(harmonica.getNoteFrequency(1, -1));
        assert note != null;
        assertEquals("D#4", note);

        note = NoteLookup.getNoteName(harmonica.getNoteFrequency(2, -1));
        assert note != null;
        assertEquals("G#4", note);

        note = NoteLookup.getNoteName(harmonica.getNoteFrequency(3, -1));
        assert note != null;
        assertEquals("C5", note);

        note = NoteLookup.getNoteName(harmonica.getNoteFrequency(4, -1));
        assert note != null;
        assertEquals("D#5", note);

        note = NoteLookup.getNoteName(harmonica.getNoteFrequency(5, -1));
        assert note != null;
        assertEquals("F#5", note);

        note = NoteLookup.getNoteName(harmonica.getNoteFrequency(6, -1));
        assert note != null;
        assertEquals("A#5", note);

        note = NoteLookup.getNoteName(harmonica.getNoteFrequency(7, 2));
        assert note != null;
        assertEquals("C#6", note);

        note = NoteLookup.getNoteName(harmonica.getNoteFrequency(8, 2));
        assert note != null;
        assertEquals("F6", note);

        note = NoteLookup.getNoteName(harmonica.getNoteFrequency(9, 2));
        assert note != null;
        assertEquals("G#6", note);

        note = NoteLookup.getNoteName(harmonica.getNoteFrequency(10, 2));
        assert note != null;
        assertEquals("C#7", note);

        assertTrue(harmonica.isNoteActive(1, -1, Dis4));
        assertTrue(harmonica.isNoteActive(2, -1, Gis4));
        assertTrue(harmonica.isNoteActive(3, -1, C5));
        assertTrue(harmonica.isNoteActive(4, -1, Dis5));
        assertTrue(harmonica.isNoteActive(5, -1, Fis5));
        assertTrue(harmonica.isNoteActive(6, -1, Ais5));

        assertTrue(harmonica.isNoteActive(7, 2, Cis6));
        assertTrue(harmonica.isNoteActive(8, 2, F6));
        assertTrue(harmonica.isNoteActive(9, 2, Gis6));
        assertTrue(harmonica.isNoteActive(10, 2, Cis7));
    }


}
