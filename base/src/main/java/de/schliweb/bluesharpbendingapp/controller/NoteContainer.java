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
 * The NoteContainer class represents a musical note within a specific channel
 * and provides functionality to handle frequency updates, harmonic adjustments,
 * and interaction with a visual representation of a harp.
 * <p>
 * This class implements the {@link Runnable} interface to periodically update
 * its associated harp view element based on the frequency handling.
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
    private double maxFrequency;
    /**
     * Represents the minimum frequency that a NoteContainer can handle.
     * This value is used to define the lower boundary for frequency processing
     * or identification.
     */
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
    public NoteContainer(int channel, int note, String noteName, Harmonica harmonica, HarpView harpView,
                         boolean hasInverseCentsHandling) {
        this(channel, note, noteName, harmonica, harpView);
        this.hasInverseCentsHandling = hasInverseCentsHandling;
    }

    @Override
    public void run() {
        if (frequencyToHandle <= maxFrequency && minFrequency <= frequencyToHandle) {
            handleFrequencyChange();
        } else {
            // execute once
            if (toBeCleared.compareAndSet(true, false)) {
                centsCache.remove(cacheKey);
                exec.schedule(harpViewElement::clear, 100, TimeUnit.MILLISECONDS);
            }

        }
    }

    /**
     * Handles the change in frequency for a specific note by calculating the deviation in cents,
     * checking for significant changes, and updating the associated harp view element if needed.
     * <p>
     * This method performs the following steps:
     * 1. Calculates the cents deviation for the note using the current frequency and retrieves
     *    the last cached cents value for comparison.
     * 2. Determines whether the change in cents exceeds a predefined threshold of significance.
     * 3. Updates the cached cents value and the harp view element accordingly, while applying
     *    inversion logic if specified.
     * 4. Flags the state to indicate that updates are required for subsequent operations.
     */
    private void handleFrequencyChange() {
        // Calculation of the current cents
        double cents = harmonica.getCentsNote(channel, note, frequencyToHandle);
        // Retrieve the last cached value (use default value - Double.MAX_VALUE if not present)
        double lastCents = centsCache.getOrDefault(cacheKey, Double.MAX_VALUE);
        // Check if there's a significant change (≥ 5 cents deviation)
        if (Math.abs(cents - lastCents) >= 2) {
            // Update the cache
            centsCache.put(cacheKey, cents);
            // Pass the value to HarpViewElement and invert it if necessary
            harpViewElement.update(hasInverseCentsHandling ? -cents : cents);
            toBeCleared.set(true);
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
