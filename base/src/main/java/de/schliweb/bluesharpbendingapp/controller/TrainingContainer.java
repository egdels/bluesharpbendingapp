/*
 * Copyright (c) 2023 Christian Kierdorf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package de.schliweb.bluesharpbendingapp.controller;

import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;
import de.schliweb.bluesharpbendingapp.model.training.Training;
import de.schliweb.bluesharpbendingapp.utils.LoggingUtils;
import de.schliweb.bluesharpbendingapp.utils.NoteUtils;
import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;
import de.schliweb.bluesharpbendingapp.view.TrainingView;
import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Setter;

/**
 * The TrainingContainer class is responsible for managing the training process, coordinating
 * updates between the active training session, associated view, and note display logic. It
 * implements the Runnable interface to execute periodic training logic on a separate thread.
 */
public class TrainingContainer implements Runnable {

  /**
   * Indicates whether all threads associated with the training process should be locked. This
   * variable is marked as volatile to ensure visibility across different threads, helping to manage
   * the synchronization of thread execution within the system.
   */
  private volatile boolean lockAllThreads = false;

  /**
   * A ScheduledThreadPoolExecutor instance used to schedule and execute tasks with a
   * single-threaded execution policy. This executor ensures that tasks are executed sequentially in
   * a dedicated thread, providing thread safety for operations within the class.
   */
  private final ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);

  /**
   * Represents a specific training process or activity that is managed by the TrainingContainer.
   * This variable provides access to the methods defined by the Training interface, allowing the
   * container to control and retrieve details about the training session.
   *
   * <p>The Training instance supports functionalities like starting, stopping, tracking progress,
   * and managing training notes.
   */
  private final Training training;

  /**
   * Represents the view associated with the training functionality. This is an instance of the
   * {@link TrainingView} interface responsible for interacting with the UI layer of the training
   * component.
   *
   * <p>This variable is immutable and initialized during the construction of the {@link
   * TrainingContainer} class.
   */
  private final TrainingView view;

  /**
   * Represents a note element in the harp view associated with the training container. This element
   * is responsible for visual updates and clearing operations based on the state of the training
   * session. It adheres to the HarpViewNoteElement interface.
   */
  private final HarpViewNoteElement element;

  /**
   * Represents a flag to indicate whether to proceed to the next note in a training sequence. Uses
   * an atomic boolean for thread-safe operations to ensure consistent state across multiple threads
   * within the TrainingContainer.
   */
  protected final AtomicBoolean toNextNote = new AtomicBoolean(false);

  /**
   * Represents a flag indicating whether a certain operation or state within the TrainingContainer
   * should be cleared or reset. This is a thread-safe indicator managed through an AtomicBoolean to
   * allow concurrent access and modification.
   */
  protected final AtomicBoolean toBeCleared = new AtomicBoolean(false);

  /**
   * Represents the minimum duration in milliseconds that a note must be held within the precision
   * threshold before it is considered successfully played. This prevents accidental brief hits from
   * being counted as successful note matches.
   */
  private static final long NOTE_HOLD_DURATION_MS = 300;

  /**
   * Tracks the timestamp (in milliseconds) when the current note first entered the precision
   * threshold. Used together with {@link #NOTE_HOLD_DURATION_MS} to ensure the player holds the
   * note for a minimum duration before advancing to the next note.
   */
  private long noteHitStartTime = 0;

  /**
   * Represents the frequency value that the system or training module needs to handle or process.
   * This value is volatile to ensure visibility and thread safety when accessed or modified by
   * multiple threads, as it may be updated dynamically during runtime.
   */
  @Setter private volatile double frequencyToHandle;

  /**
   * A cache that stores precomputed frequency-to-cents mappings for efficient lookup. The keys
   * represent specific frequency-related identifiers, and the values represent the corresponding
   * cent values as doubles. This cache is used to optimize operations where frequent calculations
   * of frequency-related conversions are required.
   */
  private final HashMap<String, Double> centsCache = new HashMap<>();

  /**
   * Constructs a new TrainingContainer instance.
   *
   * @param training the training instance to manage, responsible for handling the training logic
   * @param trainingView the view instance associated with the training, providing visual elements
   *     and interactions
   */
  public TrainingContainer(Training training, TrainingView trainingView) {
    this.training = training;
    this.view = trainingView;
    this.element = view.getActualHarpViewElement();
  }

  /**
   * Checks whether all threads are currently locked.
   *
   * @return true if all threads are locked, false otherwise
   */
  public synchronized boolean isLockAllThreads() {
    return lockAllThreads;
  }

  /**
   * Sets the lock state for all threads managed by the TrainingContainer.
   *
   * @param lockAllThreads a boolean value indicating whether all threads should be locked (true) or
   *     unlocked (false)
   */
  public synchronized void setLockAllThreads(boolean lockAllThreads) {
    this.lockAllThreads = lockAllThreads;
  }

  @Override
  public void run() {
    if (isLockAllThreads()) return; // Thread-safe access

    if (training.isRunning() && training.isNoteActive(frequencyToHandle)) {
      LoggingUtils.logDebug(
          "TrainingContainer: note active for frequency="
              + frequencyToHandle
              + ", actualNote="
              + training.getActualNote());
      handleFrequencyChange();
    } else {
      // if next note is set execute once!
      if (toNextNote.compareAndSet(true, false)) {
        advanceToNextNote();
      } else {
        // execute once
        if (toBeCleared.compareAndSet(true, false)) {
          centsCache.clear();
          exec.schedule(element::clear, 100, TimeUnit.MILLISECONDS);
        }
      }
    }
  }

  /**
   * Advances the training to the next note. This method handles locking, updating the training
   * state, and scheduling the view update. It is called either from run() when toNextNote is set,
   * or directly from handleFrequencyChange() when the hold duration is met, ensuring the note
   * advances even if no further frequency measurements arrive (silence after hitting the note).
   */
  private void advanceToNextNote() {
    setLockAllThreads(true);

    LoggingUtils.logDebug(
        "TrainingContainer: advancing to next note. Current note="
            + training.getActualNote()
            + ", progress="
            + training.getProgress()
            + "%");

    // mark actual note as success
    training.success();
    // to next Note
    training.nextNote();
    // no need to be cleared
    toBeCleared.set(false);
    // reset hold timer for next note
    noteHitStartTime = 0;

    LoggingUtils.logDebug(
        "TrainingContainer: next note="
            + training.getActualNote()
            + ", completed="
            + training.isCompleted());

    // wait 500 ms and execute
    exec.schedule(
        () -> {
          if (training.isCompleted()) {
            LoggingUtils.logOperationCompleted("Training completed successfully");
            training.stop();
            view.toggleButton();
          }
          view.initTrainingContainer(this);
          setLockAllThreads(false);
        },
        500,
        TimeUnit.MILLISECONDS);
  }

  private synchronized void handleFrequencyChange() {
    // Calculate the current cents
    String currentNote = training.getActualNote();
    double actualNoteFrequency = NoteLookup.getNoteFrequency(currentNote);
    double cents = NoteUtils.getCents(actualNoteFrequency, frequencyToHandle);
    // Retrieve the last cached value (use default value - Double.MAX_VALUE if not present)
    double lastCents = centsCache.getOrDefault(currentNote, Double.MAX_VALUE);

    LoggingUtils.logDebug(
        "TrainingContainer: handleFrequencyChange note="
            + currentNote
            + ", noteFreq="
            + actualNoteFrequency
            + ", playedFreq="
            + frequencyToHandle
            + ", cents="
            + String.format("%.1f", cents)
            + ", precision="
            + training.getPrecision());

    // Check if there is a significant change (≥ 2 cents deviation)
    if (Math.abs(cents - lastCents) >= 2) {
      // Update the cache
      centsCache.put(currentNote, cents);
      // Pass the value to HarpViewElement and optionally invert it
      element.update(cents);
      toBeCleared.set(true);
    }
    if (Math.abs(cents) < training.getPrecision()) {
      long now = System.currentTimeMillis();
      if (noteHitStartTime == 0) {
        LoggingUtils.logDebug(
            "TrainingContainer: note hit started for "
                + currentNote
                + ", cents="
                + String.format("%.1f", cents));
        noteHitStartTime = now;
      } else if (now - noteHitStartTime >= NOTE_HOLD_DURATION_MS) {
        LoggingUtils.logDebug(
            "TrainingContainer: note held long enough, advancing. note="
                + currentNote
                + ", holdTime="
                + (now - noteHitStartTime)
                + "ms");
        noteHitStartTime = 0;
        // Directly advance instead of setting toNextNote flag.
        // This ensures the note advances immediately, even if silence follows
        // and no further frequency measurements trigger run().
        advanceToNextNote();
      }
    } else {
      if (noteHitStartTime != 0) {
        LoggingUtils.logDebug(
            "TrainingContainer: hold timer reset, left precision range. note="
                + currentNote
                + ", cents="
                + String.format("%.1f", cents));
      }
      // Reset hold timer when player leaves precision range
      noteHitStartTime = 0;
    }
  }

  /**
   * Retrieves the actual note name currently being processed or managed in the training instance.
   *
   * @return the name of the actual note as a String
   */
  public String getActualNoteName() {
    return training.getActualNote();
  }

  /**
   * Retrieves the name of the next note in the training sequence.
   *
   * @return the name of the next note as a String, or null if there is no next note
   */
  public String getNextNoteName() {
    return training.getNextNote();
  }

  /**
   * Retrieves the name of the previous note in the training sequence.
   *
   * @return the name of the previous note as a String, or null if there is no previous note
   */
  public String getPreviousNoteName() {
    return training.getPreviousNote();
  }

  /**
   * Retrieves the current progress of the training.
   *
   * @return the progress value as an integer, typically representing the completion percentage or
   *     status of the training process
   */
  public int getProgress() {
    return training.getProgress();
  }
}
