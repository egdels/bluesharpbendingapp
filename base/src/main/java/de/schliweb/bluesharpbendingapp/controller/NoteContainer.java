package de.schliweb.bluesharpbendingapp.controller;
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

import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;
import de.schliweb.bluesharpbendingapp.view.HarpView;
import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class represents a container for musical notes, designed to handle various
 * functionalities such as caching note data, managing note frequency changes, and
 * interacting with visual or processing elements like harmonicas and harp views.
 * The class also supports handling single notes and chords while providing
 * thread-safe mechanisms for state management.
 * Implements the {@code Runnable} interface for task scheduling and execution.
 */
public class NoteContainer implements Runnable {

    /**
     * A cache storing the last updated cent values, indexed by a combination of channel and note.
     * Used for optimizing frequency calculations and preventing redundant computations.
     * The key is represented as a string combining channel and note, while the value is the cent offset as a double.
     */
    // Cache for the most recently updated cents values, indexed by channel and note
    private static final Map<String, Double> centsCache = new HashMap<>();

    @Getter
    private final int channel;

    @Getter
    private final int note;

    @Getter
    private final String noteName;
    /**
     * A scheduled thread pool executor responsible for running periodic or delayed tasks within the
     * context of the NoteContainer class.
     * <p>
     * Initialized with a single-threaded scheduler to ensure tasks are executed in sequence and
     * to avoid concurrency issues related to shared resources within the NoteContainer.
     * <p>
     * This executor is suitable for tasks that require controlled scheduling, such as
     * managing note-related functionalities or handling time-sensitive operations.
     */
    private final ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
    /**
     * Indicates whether the NoteContainer instance needs to be cleared or reset.
     * The value is managed in a thread-safe manner using an AtomicBoolean.
     */
    private final AtomicBoolean toBeCleared = new AtomicBoolean(false);
    private final AtomicBoolean toBeClearedChord = new AtomicBoolean(false);
    /**
     * Represents a unique identifier used for caching operations in the NoteContainer.
     * The cacheKey is utilized to distinguish specific instances of notes and their
     * associated data, ensuring efficient retrieval and storage in caching mechanisms.
     * It is immutable and uniquely represents the data within a specific NoteContainer.
     */
    private final String cacheKey;

    @Setter
    protected volatile double frequencyToHandle;

    /**
     * Flag indicating whether this note is part of a chord.
     * When set to true, the note will be highlighted as part of a chord
     * rather than showing a line indicator.
     */
    @Setter
    private boolean isPartOfChord = false;

    /**
     * Represents a harmonica instance used within a NoteContainer.
     * This field enables operations involving various harmonica-related
     * functionalities such as retrieving note frequencies, bending tones,
     * active notes evaluation, and handling of harmonica-specific configurations.
     */
    private Harmonica harmonica;
    /**
     * Represents a visual or interactive element in the harp view corresponding to a musical note.
     * This element provides methods to update its state or clear its representation,
     * supporting dynamic updates such as displaying pitch variations.
     */
    private HarpViewNoteElement harpViewElement;
    /**
     * Indicates whether inverse cents handling is enabled for this NoteContainer instance.
     * Inverse cents handling refers to a specific behavior or adjustment related
     * to musical tuning or note calculation. When set to true, this behavior is activated.
     * Defaults to false.
     */
    private boolean hasInverseCentsHandling = false;
    /**
     * Represents the maximum frequency that can be handled within the NoteContainer.
     * This value is used to enforce an upper limit on frequency-related operations.
     */
    @Getter
    private double maxFrequency;
    /**
     * Represents the minimum frequency that a NoteContainer can handle.
     * This value is used to define the lower boundary for frequency processing
     * or identification.
     */
    @Getter
    private double minFrequency;


    /**
     * Instantiates a new NoteContainer with specified channel, note, and note name.
     *
     * @param channel  the channel associated with the note
     * @param note     the numerical value of the note
     * @param noteName the name of the note
     */
    public NoteContainer(int channel, int note, String noteName) {
        this.channel = channel;
        this.note = note;
        this.noteName = noteName;
        this.cacheKey = channel + "-" + note;
    }

    /**
     * Instantiates a new NoteContainer with specified channel, note, note name, harmonica, and harp view.
     *
     * @param channel   the channel associated with the note
     * @param note      the numerical value of the note
     * @param noteName  the name of the note
     * @param harmonica the harmonica used for note frequency calculations
     * @param harpView  the harp view used to obtain the harp view element
     */
    public NoteContainer(int channel, int note, String noteName, Harmonica harmonica, HarpView harpView) {
        this(channel, note, noteName);
        this.harpViewElement = harpView.getHarpViewElement(channel, note);
        this.minFrequency = harmonica.getNoteFrequencyMinimum(channel, note);
        this.maxFrequency = harmonica.getNoteFrequencyMaximum(channel, note);
        this.harmonica = harmonica;
    }

    /**
     * Instantiates a new NoteContainer with specified channel, note, note name, harmonica, harp view,
     * and inverse cents handling option.
     *
     * @param channel                 the channel associated with the note
     * @param note                    the numerical value of the note
     * @param noteName                the name of the note
     * @param harmonica               the harmonica used for note frequency calculations
     * @param harpView                the harp view used to obtain the harp view element
     * @param hasInverseCentsHandling the flag indicating if inverse cents handling is enabled
     */
    public NoteContainer(int channel, int note, String noteName, Harmonica harmonica, HarpView harpView, boolean hasInverseCentsHandling) {
        this(channel, note, noteName, harmonica, harpView);
        this.hasInverseCentsHandling = hasInverseCentsHandling;
    }

    /**
     * Executes actions related to frequency handling and state management.
     * <p>
     * When invoked, this method checks and processes frequency changes or chord state updates.
     * If the current frequency is within the permissible range and the note is not part of a
     * chord, it invokes {@code handleFrequencyChange()} to manage the frequency update. For
     * notes that are part of a chord, {@code handleChordFrequencyChange()} is called to handle
     * chord-specific frequency changes.
     * <p>
     * Additionally, if the {@code toBeCleared} flag is set, it resets the associated state by
     * clearing any cached frequency deviation and schedules a UI element clear operation with
     * a delay of 100 milliseconds. Similarly, if the {@code toBeClearedChord} flag is set, this
     * method processes the chord state, resets the {@code isPartOfChord} flag to false, and
     * schedules a UI element clear operation with a delay of 200 milliseconds.
     * <p>
     * This method ensures that updates to the note's state and its visual representation are
     * performed consistently based on the current frequency and chord participation status.
     */
    @Override
    public void run() {
        if (!isPartOfChord && frequencyToHandle <= maxFrequency && minFrequency <= frequencyToHandle) {
            handleFrequencyChange();
        }
        if (isPartOfChord) {
            handleChordFrequencyChange();
        }
        // execute once
        if (toBeCleared.compareAndSet(true, false)) {
            exec.schedule(() -> {
                harpViewElement.clear();
                centsCache.remove(cacheKey);
            }, 100, TimeUnit.MILLISECONDS);
        }
        if (toBeClearedChord.compareAndSet(true, false)) {
            exec.schedule(() ->{
                isPartOfChord = false;
                harpViewElement.clear();
            }, 200, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Handles frequency change events for a specific note and updates the internal and visual state accordingly.
     * <p>
     * This method calculates the deviation in cents for the given note using a reference frequency and
     * checks if the deviation has changed significantly (by at least 2 cents) compared to the previously
     * cached value. If a significant change is detected, the cached value is updated, and the harp view
     * element is updated to visually reflect the new frequency's deviation. The method also sets an internal
     * flag to indicate that further processing or cleanup may be required.
     * <p>
     * The deviation can be inverted based on the configuration of the {@code hasInverseCentsHandling} flag.
     */
    private void handleFrequencyChange() {
        double cents = harmonica.getCentsNote(channel, note, frequencyToHandle);
        double lastCents = centsCache.getOrDefault(cacheKey, Double.MAX_VALUE);
        if (Math.abs(cents - lastCents) >= 2) {
            centsCache.put(cacheKey, cents);
            harpViewElement.update(hasInverseCentsHandling ? -cents : cents);
            toBeCleared.set(true);
        }
    }

    /**
     * Handles the state update when a chord frequency change is detected.
     * <p>
     * This method checks if the {@code toBeClearedChord} flag is not set. If the flag is unset,
     * it updates the visual state of the associated UI element by highlighting it as a chord
     * using {@code harpViewElement.highlightAsChord()}. It then sets the {@code toBeClearedChord}
     * flag to {@code true} to indicate that the chord has been processed and further cleanup
     * or updates may be necessary.
     * <p>
     * This method is primarily used to manage the UI and internal state when a chord frequency
     * change event occurs.
     */
    private void handleChordFrequencyChange() {
        if (!toBeClearedChord.get()) {
            harpViewElement.highlightAsChord();
            toBeClearedChord.set(true);
        }
    }

    /**
     * Determines if the note in the current channel is overblow.
     *
     * @return true if the note is an overblow note, false otherwise
     */
    public boolean isOverblow() {
        return harmonica.isOverblow(channel, note);
    }

    /**
     * Determines if the note in the specified channel is an overdraw.
     *
     * @return true if the note is an overdraw, false otherwise
     */
    public boolean isOverdraw() {
        return harmonica.isOverdraw(channel, note);
    }
}
