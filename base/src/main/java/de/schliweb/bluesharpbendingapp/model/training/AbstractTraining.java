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

/**
 * The AbstractTraining class provides a base implementation of the {@link Training} interface.
 * It contains methods and properties common to different types of training exercises.
 *
 * This class also provides static methods to create specific types of training exercises
 * and retrieve supported training options and precision levels.
 */
public abstract class AbstractTraining implements Training {

    /**
     * Represents the level of precision used in training-related calculations or operations.
     * This variable is a shared static field across training instances
     * and determines the granularity or accuracy of training evaluations.
     */
    private static int precision;
    /**
     * Represents the central frequency value associated with a key in the
     * context of training. This frequency is used to identify the pitch or note
     * being targeted during a training session.
     *
     * It is a constant and defined as a Double to allow for precision in
     * representing pitch frequencies.
     */
    private final Double keyFrequency;

    /**
     * Represents the index of the current note in the training session.
     * This variable is used for tracking the progress within a sequence of notes.
     * It is initialized to 0 and updated as the training progresses.
     */
    private int noteIndex = 0;

    /**
     * A flag indicating whether the training is currently active or running.
     * This variable is used to track the state of the training and is typically
     * modified by the {@code start()} and {@code stop()} methods.
     */
    private boolean isRunning = false;

    /**
     * Represents the index for tracking the number of consecutive successes
     * during a training session. This variable is used to monitor progress
     * and can help determine when a specific training task or activity has
     * been successfully completed multiple times in succession.
     */
    private int successIndex=0;

    /**
     * Constructs an instance of AbstractTraining with the specified key.
     *
     * @param key the harmonica key used to determine the frequency
     */
    protected AbstractTraining(AbstractHarmonica.KEY key) {
        this.keyFrequency = key.getFrequency();
    }

    /**
     * Creates a new Training instance based on the specified key and training indices.
     *
     * @param keyIndex the index of the harmonica key, used to determine which key to use
     * @param trainingIndex the index of the training type, used to determine which training to create
     * @return a Training instance corresponding to the specified key and training type
     */
    public static Training create(int keyIndex, int trainingIndex) {
        return create(AbstractHarmonica.KEY.values()[keyIndex], AbstractTraining.TRAINING.values()[trainingIndex]);
    }

    /**
     * Creates a new Training instance based on the specified key and training type.
     *
     * @param k the harmonica key used to create the training instance
     * @param t the type of training to be created
     * @return a Training instance corresponding to the specified key and training type
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
        if (TRAINING.DUMMY.equals(t)) {
            training = new DummyTraining(k);
        }
        return training;
    }

    /**
     * Retrieves an array of all supported training types as their respective names.
     *
     * @return an array of strings representing the names of all available training types
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
     * Retrieves an array of supported precision levels for the training.
     *
     * @return an array of strings, where each string represents a precision level
     *         available for the training, such as "5", "10", "15", etc.
     */
    public static String[] getSupportedPrecisions() {
        return new String[]{"5", "10", "15", "20", "25", "30", "35", "40", "45"};
    }

    /**
     * Retrieves the current precision level for the training.
     *
     * @return the precision level as an integer
     */
    public static int getPrecision() {
        return precision;
    }

    /**
     * Sets the precision level for the training.
     *
     * @param precision the precision value to be set. It determines the level
     *                  of accuracy or granularity for training operations.
     */
    public static void setPrecision(int precision) {
        AbstractTraining.precision = precision;
    }

    /**
     * Retrieves an array of integers representing the half-tone intervals
     * relevant to the specific implementation of a training type.
     *
     * @return an array of integers, where each integer corresponds to a half-tone step
     *         required for the associated training.
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
        return getNotes()[noteIndex];
    }

    @Override
    public String getNextNote() {
        if (noteIndex >= getNotes().length - 1) return null;
        return getNotes()[noteIndex + 1];
    }

    @Override
    public int getProgress() {
        return (100 * (successIndex)) / getNotes().length;
    }

    @Override
    public String getPreviousNote() {
        if (noteIndex <= 0) return null;
        return getNotes()[noteIndex - 1];
    }

    @Override
    public void start() {
        noteIndex=0;
        successIndex=0;
        isRunning=true;
    }

    @Override
    public void stop() {
        isRunning=false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public String nextNote() {
        if (noteIndex < getNotes().length - 1) return getNotes()[++noteIndex];
        return null;
    }

    @Override
    public boolean isNoteActive(double frequency) {
        double cents = NoteUtils.getCents(NoteLookup.getNoteFrequency(getNotes()[noteIndex]), frequency);
        return cents <= 50 && cents >= -50;
    }

    @Override
    public boolean isCompleted() {
        return successIndex >= getNotes().length;
    }

    @Override
    public void success () {
        successIndex++;
    }

    /**
     * An enumeration representing different types of training modes available
     * for a harmonica training application. Each enum constant corresponds
     * to a specific training type that targets a particular skill or aspect
     * of playing the harmonica.
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

        DUMMY,
        /**
         * Octave leaps training.
         */
        OCTAVE_LEAPS
    }
}
