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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the BluesScaleTraining class.
 */
class BluesScaleTrainingTest {

    private BluesScaleTraining training;
    private final AbstractHarmonica.KEY testKey = AbstractHarmonica.KEY.C;

    @BeforeEach
    void setUp() {
        training = (BluesScaleTraining) AbstractTraining.create(testKey, AbstractTraining.TRAINING.BLUES_SCALE);
    }

    @Test
    void testCreate() {
        Training bluesTraining = AbstractTraining.create(testKey, AbstractTraining.TRAINING.BLUES_SCALE);
        assertNotNull(bluesTraining);
        assertInstanceOf(BluesScaleTraining.class, bluesTraining);
        assertEquals(AbstractTraining.TRAINING.BLUES_SCALE.name(), bluesTraining.getTrainingName());
    }

    @Test
    void testGetTrainingName() {
        assertEquals(AbstractTraining.TRAINING.BLUES_SCALE.name(), training.getTrainingName());
    }

    @Test
    void testGetNotes() {
        String[] notes = training.getNotes();
        assertNotNull(notes);

        // The blues scale should have 17 notes based on the HALF_TONES array
        assertEquals(17, notes.length);

        // Verify the first note is the key note (C for C key)
        assertEquals("C4", notes[0]);

        // Verify a few other notes in the scale
        // For C key, the blues scale should include C, Db, D, F, G, Bb, C, etc.
        assertEquals("C#4", notes[1]); // C# is 1 half-tone up from C
        assertEquals("D4", notes[2]);  // D is 2 half-tones up from C
        assertEquals("F4", notes[3]);  // F is 5 half-tones up from C
        assertEquals("G4", notes[4]);  // G is 7 half-tones up from C
        assertEquals("A#4", notes[5]); // A# is 10 half-tones up from C
        assertEquals("C5", notes[6]);  // C is 12 half-tones up from C (octave)
    }

    @Test
    void testTrainingFlow() {
        // Test the training flow: start, get notes, progress, completion

        // Initially not running
        assertFalse(training.isRunning());

        // Start the training
        training.start();
        assertTrue(training.isRunning());

        // Initial progress should be 0
        assertEquals(0, training.getProgress());

        // Current note should be the first note
        assertEquals(training.getNotes()[0], training.getActualNote());

        // Next note should be the second note
        assertEquals(training.getNotes()[1], training.getNextNote());

        // Previous note should be null (we're at the beginning)
        assertNull(training.getPreviousNote());

        // Test note detection
        double keyFreq = testKey.getFrequency();

        // Test with the correct frequency (should be active)
        assertTrue(training.isNoteActive(keyFreq));

        // Test with a frequency that's too high (should not be active)
        assertFalse(training.isNoteActive(keyFreq * 1.1));

        // Test with a frequency that's too low (should not be active)
        assertFalse(training.isNoteActive(keyFreq * 0.9));

        // Mark as success and check progress
        training.success();
        int expectedProgress = (int) (100.0 / training.getNotes().length);
        assertEquals(expectedProgress, training.getProgress());

        // Move to next note
        String nextNote = training.nextNote();
        assertEquals(training.getNotes()[1], nextNote);
        assertEquals(training.getNotes()[1], training.getActualNote());
        assertEquals(training.getNotes()[2], training.getNextNote());
        assertEquals(training.getNotes()[0], training.getPreviousNote());

        // Training should not be completed yet
        assertFalse(training.isCompleted());

        // Stop the training
        training.stop();
        assertFalse(training.isRunning());
    }

    @Test
    void testCompletion() {
        training.start();

        // Mark all notes as successful
        for (int i = 0; i < training.getNotes().length; i++) {
            training.success();
        }

        // Training should be completed
        assertTrue(training.isCompleted());

        // Progress should be 100%
        assertEquals(100, training.getProgress());
    }
}