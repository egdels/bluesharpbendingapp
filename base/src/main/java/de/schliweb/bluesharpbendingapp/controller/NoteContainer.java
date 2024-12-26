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

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The type NoteContainer.
 */
public class NoteContainer implements Runnable {

    /**
     * The Channel.
     */
    private final int channel;
    /**
     * The NoteContainer.
     */
    private final int note;
    /**
     * The NoteContainer name.
     */
    private final String noteName;
    /**
     * The Thread Executer.
     */
    private final ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
    /**
     * The Frequency to handle.
     */
    private volatile double frequencyToHandle;
    /**
     * The Harmonica.
     */
    private Harmonica harmonica;
    /**
     * The Harp view element.
     */
    private HarpViewNoteElement harpViewElement;
    /**
     * The Has inverse cents handling.
     */
    private boolean hasInverseCentsHandling = false;
    /**
     * The Max frequency.
     */
    private double maxFrequency;
    /**
     * The Min frequency.
     */
    private double minFrequency;
    /**
     * The To Be Cleared.
     */
    private final AtomicBoolean toBeCleared= new AtomicBoolean(false);;


    /**
     * Instantiates a new NoteContainer.
     *
     * @param channel  the channel
     * @param note     the note
     * @param noteName the note name
     */
    public NoteContainer(int channel, int note, String noteName) {
        this.channel = channel;
        this.note = note;
        this.noteName = noteName;
    }

    /**
     * Instantiates a new NoteContainer.
     *
     * @param channel   the channel
     * @param note      the note
     * @param noteName  the note name
     * @param harmonica the harmonica
     * @param harpView  the harp view
     */
    public NoteContainer(int channel, int note, String noteName, Harmonica harmonica, HarpView harpView) {
        this(channel, note, noteName);
        this.harpViewElement = harpView.getHarpViewElement(channel, note);
        this.minFrequency = harmonica.getNoteFrequencyMinimum(channel, note);
        this.maxFrequency = harmonica.getNoteFrequencyMaximum(channel, note);
        this.harmonica = harmonica;
    }

    /**
     * Instantiates a new NoteContainer.
     *
     * @param channel                 the channel
     * @param note                    the note
     * @param noteName                the note name
     * @param harmonica               the harmonica
     * @param harpView                the harp view
     * @param hasInverseCentsHandling the has inverse cents handling
     */
    public NoteContainer(int channel, int note, String noteName, Harmonica harmonica, HarpView harpView,
                         boolean hasInverseCentsHandling) {
        this(channel, note, noteName, harmonica, harpView);
        this.hasInverseCentsHandling = hasInverseCentsHandling;
    }

    /**
     * Gets channel.
     *
     * @return the channel
     */
    public int getChannel() {
        return channel;
    }

    /**
     * Gets note.
     *
     * @return the note
     */
    public int getNote() {
        return note;
    }

    /**
     * Gets note name.
     *
     * @return the note name
     */
    public String getNoteName() {
        return noteName;
    }

    @Override
    public void run() {
        if (frequencyToHandle <= maxFrequency && minFrequency <= frequencyToHandle) {
            double cents = harmonica.getCentsNote(channel, note, frequencyToHandle);
            if (!hasInverseCentsHandling)
                harpViewElement.update(cents);
            else {
                harpViewElement.update(-cents);
            }
            toBeCleared.set(true);
        } else {
            // execute once
            if (toBeCleared.compareAndSet(true, false)) {
                exec.schedule(() -> {
                    harpViewElement.clear();
                }, 100, TimeUnit.MILLISECONDS);
            }
        }
    }

    /**
     * Sets frequency to handle.
     *
     * @param frequencyToHandle the frequency to handle
     */
    public void setFrequencyToHandle(double frequencyToHandle) {
        this.frequencyToHandle = frequencyToHandle;
    }

    /**
     * Is overblow boolean.
     *
     * @return the boolean
     */
    public boolean isOverblow() {
        return harmonica.isOverblow(channel, note);
    }

    /**
     * Is overdraw boolean.
     *
     * @return the boolean
     */
    public boolean isOverdraw() {
        return harmonica.isOverdraw(channel, note);
    }
}
