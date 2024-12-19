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
import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;
import de.schliweb.bluesharpbendingapp.utils.NoteUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The type Abstract training.
 */
public abstract class AbstractTraining implements Training {

    /**
     * The constant precision.
     */
    private static int precision;
    /**
     * The Key frequency.
     */
    private final Double keyFrequency;

    /**
     * The Note index.
     */
    private final AtomicInteger noteIndex = new AtomicInteger(0);

    /**
     * The Is running.
     */
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * Instantiates a new Abstract training.
     *
     * @param key the key
     */
    protected AbstractTraining(AbstractHarmonica.KEY key) {
        this.keyFrequency = key.getFrequency();
    }

    /**
     * Create training.
     *
     * @param keyIndex      the key index
     * @param trainingIndex the training index
     * @return the training
     */
    public static Training create(int keyIndex, int trainingIndex) {
        return create(AbstractHarmonica.KEY.values()[keyIndex], AbstractTraining.TRAINING.values()[trainingIndex]);
    }

    /**
     * Create training.
     *
     * @param k the k
     * @param t the t
     * @return the training
     */
    public static Training create(AbstractHarmonica.KEY k, TRAINING t) {
        Training training = new MajorPentatonicScaleTraining(k);
        if (TRAINING.BLUES_SCALE.equals(t)) {
            training = new BluesScaleTraining(k);
        }
        if (TRAINING.OCTAVE_LEAPS.equals(t)) {
            training = new OctaveLeapsTraining(k);
        }
        if (TRAINING.MAJOR_CHORD_ARPEGGIO.equals(t)) {
            training = new MajorChordArpeggioTraining(k);
        }
        if (TRAINING.MAJOR_SCALE.equals(t)) {
            training = new MajorScaleTraining(k);
        }
        return training;
    }

    /**
     * Get supported trainings string [ ].
     *
     * @return the string [ ]
     */
    public static String[] getSupportedTrainings() {
        AbstractTraining.TRAINING[] values = AbstractTraining.TRAINING.values();
        String[] keys = new String[AbstractTraining.TRAINING.values().length];
        int index = 0;
        for (AbstractTraining.TRAINING value : values) {
            keys[index] = value.name();
            index++;
        }
        return keys;
    }

    /**
     * Get supported precisions string [ ].
     *
     * @return the string [ ]
     */
    public static String[] getSupportedPrecisions() {
        return new String[]{"5", "10", "15"};
    }

    /**
     * Gets precision.
     *
     * @return the precision
     */
    public static int getPrecision() {
        return precision;
    }

    /**
     * Sets precision.
     *
     * @param precision the precision
     */
    public static void setPrecision(int precision) {
        AbstractTraining.precision = precision;
    }

    /**
     * Get half tones int [ ].
     *
     * @return the int [ ]
     */
    abstract int[] getHalfTones();

    @Override
    public String[] getNotes() {
        ArrayList<String> notes = new ArrayList<>();
        for (int halfTone : getHalfTones()) {
            notes.add(NoteLookup.getNoteName(NoteUtils.addCentsToFrequency(halfTone * 100, keyFrequency)));
        }
        return Arrays.copyOf(notes.toArray(), notes.toArray().length, String[].class);
    }

    @Override
    public String getActualNote() {
        return getNotes()[noteIndex.get()];
    }

    @Override
    public String getNextNote() {
        if (noteIndex.get() == getNotes().length - 1) return null;
        return getNotes()[noteIndex.get() + 1];
    }

    @Override
    public int getProgress() {
        return (100 * noteIndex.get()) / (getNotes().length - 1);
    }

    @Override
    public String getPreviousNote() {
        if (noteIndex.get() == 0) return null;
        return getNotes()[noteIndex.get() - 1];
    }

    @Override
    public void start() {
        noteIndex.set(0);
        isRunning.set(true);
    }

    @Override
    public void stop() {
        isRunning.set(false);
    }

    @Override
    public boolean isRunning() {
        return isRunning.get();
    }

    @Override
    public String nextNote() {
        if (noteIndex.get() < getNotes().length - 1) return getNotes()[noteIndex.incrementAndGet()];
        return null;
    }

    @Override
    public boolean isNoteActive(double frequency) {
        double cents = NoteUtils.getCents(NoteLookup.getNoteFrequency(getNotes()[noteIndex.get()]), frequency);
        return cents <= 50 && cents >= -50;
    }

    /**
     * The enum Training.
     */
    public enum TRAINING {
        /**
         * Blues scale training.
         */
        BLUES_SCALE,
        /**
         * Major chord arpeggio training.
         */
        MAJOR_CHORD_ARPEGGIO,
        /**
         * Major pentatonic scale training.
         */
        MAJOR_PENTATONIC_SCALE,
        /**
         * Major scale training.
         */
        MAJOR_SCALE,
        /**
         * Octave leaps training.
         */
        OCTAVE_LEAPS
    }
}
