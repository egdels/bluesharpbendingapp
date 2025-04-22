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

import static org.junit.jupiter.api.Assertions.*;

class AbstractHarmonicaTest {

    @Test
    void testCreateRichterHarmonica() {
        AbstractHarmonica.KEY key = AbstractHarmonica.KEY.C;
        AbstractHarmonica.TUNE tune = AbstractHarmonica.TUNE.RICHTER;

        Harmonica harmonica = AbstractHarmonica.create(key, tune);

        assertNotNull(harmonica);
        assertInstanceOf(RichterHarmonica.class, harmonica);
        assertEquals(key.getFrequency(), harmonica.getNoteFrequency(1, 0));
    }

    @Test
    void testCreateCountryHarmonica() {
        AbstractHarmonica.KEY key = AbstractHarmonica.KEY.D;
        AbstractHarmonica.TUNE tune = AbstractHarmonica.TUNE.COUNTRY;

        Harmonica harmonica = AbstractHarmonica.create(key, tune);

        assertNotNull(harmonica);
        assertInstanceOf(CountryHarmonica.class, harmonica);
        assertEquals(key.getFrequency(), harmonica.getNoteFrequency(1, 0));
    }

    @Test
    void testCreateDiminishedHarmonica() {
        AbstractHarmonica.KEY key = AbstractHarmonica.KEY.E;
        AbstractHarmonica.TUNE tune = AbstractHarmonica.TUNE.DIMINISHED;

        Harmonica harmonica = AbstractHarmonica.create(key, tune);

        assertNotNull(harmonica);
        assertInstanceOf(DiminishedHarmonica.class, harmonica);
        assertEquals(key.getFrequency(), harmonica.getNoteFrequency(1, 0));
    }

    @Test
    void testCreateHarmonicMollHarmonica() {
        AbstractHarmonica.KEY key = AbstractHarmonica.KEY.B;
        AbstractHarmonica.TUNE tune = AbstractHarmonica.TUNE.HARMONICMOLL;

        Harmonica harmonica = AbstractHarmonica.create(key, tune);

        assertNotNull(harmonica);
        assertInstanceOf(HarmonicMollHarmonica.class, harmonica);
        assertEquals(key.getFrequency(), harmonica.getNoteFrequency(1, 0));
    }

    @Test
    void testCreatePaddyRichterHarmonica() {
        AbstractHarmonica.KEY key = AbstractHarmonica.KEY.G;
        AbstractHarmonica.TUNE tune = AbstractHarmonica.TUNE.PADDYRICHTER;

        Harmonica harmonica = AbstractHarmonica.create(key, tune);

        assertNotNull(harmonica);
        assertInstanceOf(PaddyRichterHarmonica.class, harmonica);
        assertEquals(key.getFrequency(), harmonica.getNoteFrequency(1, 0));
    }

    @Test
    void testCreateMelodyMakerHarmonica() {
        AbstractHarmonica.KEY key = AbstractHarmonica.KEY.F;
        AbstractHarmonica.TUNE tune = AbstractHarmonica.TUNE.MELODYMAKER;

        Harmonica harmonica = AbstractHarmonica.create(key, tune);

        assertNotNull(harmonica);
        assertInstanceOf(MelodyMakerHarmonica.class, harmonica);
        assertEquals(key.getFrequency(), harmonica.getNoteFrequency(1, 0));
    }

    @Test
    void testCreateNaturalMollHarmonica() {
        AbstractHarmonica.KEY key = AbstractHarmonica.KEY.A_FLAT;
        AbstractHarmonica.TUNE tune = AbstractHarmonica.TUNE.NATURALMOLL;

        Harmonica harmonica = AbstractHarmonica.create(key, tune);

        assertNotNull(harmonica);
        assertInstanceOf(NaturalMollHarmonica.class, harmonica);
        assertEquals(key.getFrequency(), harmonica.getNoteFrequency(1, 0));
    }

    @Test
    void testCreateCircularHarmonica() {
        AbstractHarmonica.KEY key = AbstractHarmonica.KEY.B_FLAT;
        AbstractHarmonica.TUNE tune = AbstractHarmonica.TUNE.CIRCULAR;

        Harmonica harmonica = AbstractHarmonica.create(key, tune);

        assertNotNull(harmonica);
        assertInstanceOf(CircularHarmonica.class, harmonica);
        assertEquals(key.getFrequency(), harmonica.getNoteFrequency(1, 0));
    }

    @Test
    void testCreateAugmentedHarmonica() {
        AbstractHarmonica.KEY key = AbstractHarmonica.KEY.F_HASH;
        AbstractHarmonica.TUNE tune = AbstractHarmonica.TUNE.AUGMENTED;

        Harmonica harmonica = AbstractHarmonica.create(key, tune);

        assertNotNull(harmonica);
        assertInstanceOf(AugmentedHarmonica.class, harmonica);
        assertEquals(key.getFrequency(), harmonica.getNoteFrequency(1, 0));
    }
}