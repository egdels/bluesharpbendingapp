package de.schliweb.bluesharpbendingapp.model.training;

import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * The type Training test.
 */
public class TrainingTest {

    /**
     * Test major scale.
     */
    @Test
    void testMajorScale() {
        Training training = AbstractTraining.create(AbstractHarmonica.KEY.C, AbstractTraining.TRAINING.MAJOR_SCALE);
        String[] notes = training.getNotes();
        assertEquals("C4", notes[0]);
        assertEquals("D4", notes[1]);
        assertEquals("E4", notes[2]);
        assertEquals("F4", notes[3]);
        assertEquals("G4", notes[4]);
        assertEquals("A4", notes[5]);
        assertEquals("B4", notes[6]);
        assertEquals("C5", notes[7]);
        assertEquals("D5", notes[8]);
        assertEquals("E5", notes[9]);
        assertEquals("F5", notes[10]);
        assertEquals("G5", notes[11]);
        assertEquals("A5", notes[12]);
        assertEquals("B5", notes[13]);
        assertEquals("C6", notes[14]);
        assertEquals("D6", notes[15]);
        assertEquals("E6", notes[16]);
        assertEquals("F6", notes[17]);
        assertEquals("G6", notes[18]);
        assertEquals("A6", notes[19]);
        assertEquals("B6", notes[20]); // Blow Bending
        assertEquals("C7", notes[21]);
    }

    /**
     * Test octave leaps.
     */
    @Test
    void testOctaveLeaps() {
        Training training = AbstractTraining.create(AbstractHarmonica.KEY.C, AbstractTraining.TRAINING.OCTAVE_LEAPS);
        String[] notes = training.getNotes();
        assertEquals("C4", notes[0]);
        assertEquals("C5", notes[1]);
        assertEquals("C6", notes[2]);
        assertEquals("C7", notes[3]);
        assertEquals("C6", notes[4]);
        assertEquals("C5", notes[5]);
        assertEquals("D4", notes[6]);
        assertEquals("D5", notes[7]);
        assertEquals("D6", notes[8]);
        assertEquals("D5", notes[9]);
        assertEquals("E4", notes[10]);
        assertEquals("E5", notes[11]);
        assertEquals("E6", notes[12]);
        assertEquals("E5", notes[13]);
        assertEquals("F4", notes[14]);
        assertEquals("F5", notes[15]);
        assertEquals("F6", notes[16]);
        assertEquals("F5", notes[17]);
        assertEquals("G4", notes[18]);
        assertEquals("G5", notes[19]);
        assertEquals("G6", notes[20]);
        assertEquals("G5", notes[21]);
        assertEquals("A4", notes[22]);
        assertEquals("A5", notes[23]);
        assertEquals("A6", notes[24]);
        assertEquals("A5", notes[25]);
        assertEquals("B4", notes[26]);
        assertEquals("B5", notes[27]);
        assertEquals("B6", notes[28]); // Blow Bending
        assertEquals("B5", notes[29]);
    }

    /**
     * Test major pentatonic.
     */
    @Test
    void testMajorPentatonic() {
        Training training = AbstractTraining.create(AbstractHarmonica.KEY.C, AbstractTraining.TRAINING.MAJOR_PENTATONIC_SCALE);
        String[] notes = training.getNotes();
        assertEquals("D4", notes[0]);
        assertEquals("E4", notes[1]);
        assertEquals("G4", notes[2]);
        assertEquals("A4", notes[3]);
        assertEquals("B4", notes[4]);
        assertEquals("D5", notes[5]);
        assertEquals("E5", notes[6]);
        assertEquals("G5", notes[7]);
        assertEquals("A5", notes[8]);
        assertEquals("B5", notes[9]);
        assertEquals("D6", notes[10]);
        assertEquals("E6", notes[11]);
        assertEquals("G6", notes[12]);
        assertEquals("A6", notes[13]);
        assertEquals("B6", notes[14]); // Blow Bending
    }

    /**
     * Test blues scale.
     */
    @Test
    void testBluesScale() {
        Training training = AbstractTraining.create(AbstractHarmonica.KEY.C, AbstractTraining.TRAINING.BLUES_SCALE);
        String[] notes = training.getNotes();
        assertEquals("C4", notes[0]);
        assertEquals("C#4", notes[1]);
        assertEquals("D4", notes[2]);
        assertEquals("F4", notes[3]);
        assertEquals("G4", notes[4]);
        assertEquals("A#4", notes[5]);
        assertEquals("C5", notes[6]);
        assertEquals("C#5", notes[7]);
        assertEquals("D5", notes[8]);
        assertEquals("F5", notes[9]);
        assertEquals("G5", notes[10]);
        assertEquals("G#5", notes[11]);
        assertEquals("A5", notes[12]);
        assertEquals("C6", notes[13]);
        assertEquals("D6", notes[14]);
        assertEquals("F6", notes[15]);
        assertEquals("G6", notes[16]);
    }

    /**
     * Test chord arpeggio.
     */
    @Test
    void testChordArpeggio() {
        Training training = AbstractTraining.create(AbstractHarmonica.KEY.C, AbstractTraining.TRAINING.MAJOR_CHORD_ARPEGGIO);
        String[] notes = training.getNotes();
        assertEquals("D4", notes[0]);
        assertEquals("F#4", notes[1]);
        assertEquals("A4", notes[2]);
        assertEquals("D4", notes[3]);
        assertEquals("F#4", notes[4]);
        assertEquals("A4", notes[5]);
        assertEquals("B4", notes[6]);
        assertEquals("D4", notes[7]);
        assertEquals("F#4", notes[8]);
        assertEquals("A4", notes[9]);
        assertEquals("C5", notes[10]);
        assertEquals("D4", notes[11]);
        assertEquals("F#4", notes[12]);
        assertEquals("A4", notes[13]);
        assertEquals("B4", notes[14]);
        assertEquals("C5", notes[15]);
        assertEquals("D5", notes[16]);
    }

    /**
     * Test training.
     */
    @Test
    void testTraining() {
        Training training = AbstractTraining.create(AbstractHarmonica.KEY.C, AbstractTraining.TRAINING.MAJOR_CHORD_ARPEGGIO);
        training.start();
        System.out.println(training.getProgress());
        assertNull(training.getPreviousNote());
        assertEquals("D4", training.getActualNote());
        assertEquals("F#4", training.getNextNote());
        training.nextNote();
        System.out.println(training.getProgress());
        assertEquals("D4", training.getPreviousNote());
        assertEquals("F#4", training.getActualNote());
        assertEquals("A4", training.getNextNote());
        training.nextNote();
        System.out.println(training.getProgress());
        training.nextNote();
        training.nextNote();
        training.nextNote();
        System.out.println(training.getProgress());
        training.nextNote();
        training.nextNote();
        training.nextNote();
        training.nextNote();
        training.nextNote();
        training.nextNote();
        training.nextNote();
        training.nextNote();
        training.nextNote();
        training.nextNote();
        training.nextNote();
        training.nextNote();
        training.nextNote();
        training.nextNote();
        training.nextNote();
        training.nextNote();
        System.out.println(training.getProgress());
        training.nextNote();
        training.nextNote();
        training.nextNote();
        training.nextNote();
        training.nextNote();
        training.nextNote();
        training.nextNote();
        training.nextNote();
        training.nextNote();
        training.nextNote();
        System.out.println(training.getProgress());

        assertNull(training.getNextNote());
        assertEquals("D5", training.getActualNote());

        training.stop();
        training.start();
        assertNull(training.getPreviousNote());
        assertEquals("D4", training.getActualNote());
        assertEquals("F#4", training.getNextNote());

    }
}
