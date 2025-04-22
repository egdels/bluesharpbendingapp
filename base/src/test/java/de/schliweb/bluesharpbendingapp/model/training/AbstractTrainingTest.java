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

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AbstractTrainingTest {

    /**
     * Test class to validate the functionality of the 'create' method in the
     * AbstractTraining class. The 'create' method is responsible for instantiating
     * various Training implementations based on specified key and training type.
     */

    @Test
    void shouldCreateMajorPentatonicScaleTrainingWhenTrainingIsMajorPentatonicScale() {
        AbstractHarmonica.KEY key = AbstractHarmonica.KEY.C;
        AbstractTraining.TRAINING trainingType = AbstractTraining.TRAINING.MAJOR_PENTATONIC_SCALE;

        Training result = AbstractTraining.create(key, trainingType);

        assertNotNull(result);
        assertInstanceOf(MajorPentatonicScaleTraining.class, result);
    }

    @Test
    void shouldCreateBluesScaleTrainingWhenTrainingIsBluesScale() {
        AbstractHarmonica.KEY key = AbstractHarmonica.KEY.C;
        AbstractTraining.TRAINING trainingType = AbstractTraining.TRAINING.BLUES_SCALE;

        Training result = AbstractTraining.create(key, trainingType);

        assertNotNull(result);
        assertInstanceOf(BluesScaleTraining.class, result);
    }

    @Test
    void shouldCreateOctaveLeapsTrainingWhenTrainingIsOctaveLeaps() {
        AbstractHarmonica.KEY key = AbstractHarmonica.KEY.C;
        AbstractTraining.TRAINING trainingType = AbstractTraining.TRAINING.OCTAVE_LEAPS;

        Training result = AbstractTraining.create(key, trainingType);

        assertNotNull(result);
        assertInstanceOf(OctaveLeapsTraining.class, result);
    }

    @Test
    void shouldCreateMajorChordArpeggioTrainingWhenTrainingIsMajorChordArpeggio() {
        AbstractHarmonica.KEY key = AbstractHarmonica.KEY.C;
        AbstractTraining.TRAINING trainingType = AbstractTraining.TRAINING.MAJOR_CHORD_ARPEGGIO;

        Training result = AbstractTraining.create(key, trainingType);

        assertNotNull(result);
        assertInstanceOf(MajorChordArpeggioTraining.class, result);
    }

    @Test
    void shouldCreateMajorScaleTrainingWhenTrainingIsMajorScale() {
        AbstractHarmonica.KEY key = AbstractHarmonica.KEY.C;
        AbstractTraining.TRAINING trainingType = AbstractTraining.TRAINING.MAJOR_SCALE;

        Training result = AbstractTraining.create(key, trainingType);

        assertNotNull(result);
        assertInstanceOf(MajorScaleTraining.class, result);
    }

    @Test
    void shouldCreateDummyTrainingWhenTrainingIsDummy() {
        AbstractHarmonica.KEY key = AbstractHarmonica.KEY.C;
        AbstractTraining.TRAINING trainingType = AbstractTraining.TRAINING.DUMMY;

        Training result = AbstractTraining.create(key, trainingType);

        assertNotNull(result);
        assertInstanceOf(DummyTraining.class, result);
    }

    @Test
    void shouldCreateTrainingUsingKeyIndexAndTrainingIndex() {
        int keyIndex = AbstractHarmonica.KEY.C.ordinal();
        int trainingIndex = AbstractTraining.TRAINING.BLUES_SCALE.ordinal();

        Training result = AbstractTraining.create(keyIndex, trainingIndex);

        assertNotNull(result);
        assertInstanceOf(BluesScaleTraining.class, result);
    }
}