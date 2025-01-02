package de.schliweb.bluesharpbendingapp.model.harmonica;

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