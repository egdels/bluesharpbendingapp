package de.schliweb.bluesharpbendingapp.model.training;
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
import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The type Training test.
 */
class TrainingTest {

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
        String[] expectedNotes = {
                "C4", "C5", "C6", "C7", "C6", "C5",
                "D4", "D5", "D6", "D5", "E4", "E5", "E6", "E5",
                "F4", "F5", "F6", "F5", "G4", "G5", "G6", "G5",
                "A4", "A5", "A6", "A5", "B4", "B5", "B6", "B5"
        };

        assertArrayEquals(expectedNotes, training.getNotes());
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

        assertNull(training.getPreviousNote());
        assertEquals("D4", training.getActualNote());
        assertEquals("F#4", training.getNextNote());
        training.nextNote();
        assertEquals("D4", training.getPreviousNote());
        assertEquals("F#4", training.getActualNote());
        assertEquals("A4", training.getNextNote());
        while (!training.isCompleted()) {
            training.nextNote();
            training.success();
        }
        assertNull(training.getNextNote());
        assertEquals("D5", training.getActualNote());
        assertEquals(100, training.getProgress());
        training.stop();
        training.start();
        assertNull(training.getPreviousNote());
        assertEquals("D4", training.getActualNote());
        assertEquals("F#4", training.getNextNote());
        training.stop();
        training.start();
        String[] notes = training.getNotes();
        for (String note : notes) {
            assertEquals(note, training.getActualNote());
            training.nextNote();
        }
        training.start();
        for (int i = 0; i < notes.length - 1; i++) {
            training.nextNote();
            training.success();
        }
        training.nextNote();
        assertFalse(training.isCompleted());
        assertTrue(training.getProgress() < 100 && training.getProgress() > 0);
        training.success();
        assertEquals(100, training.getProgress());
    }
}
