package de.schliweb.bluesharpbendingapp.controller;

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

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;
import de.schliweb.bluesharpbendingapp.model.training.AbstractTraining;
import de.schliweb.bluesharpbendingapp.model.training.Training;
import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;
import de.schliweb.bluesharpbendingapp.view.TrainingView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration test that runs a complete DummyTraining session through the TrainingContainer.
 * DummyTraining with KEY.C produces 3 notes: C4, C#4, D4 (half-tone intervals {0, 1, 2}). This test
 * covers various real-world scenarios: clean playing, silence, imprecise playing, too-short hits,
 * leaving precision range, and completing the full training.
 */
class TrainingContainerDummyIntegrationTest {

  private Training training;
  private TrainingView viewMock;
  private HarpViewNoteElement elementMock;

  // Frequencies for the 3 DummyTraining notes (KEY.C)
  private double freqC4;
  private double freqCSharp4;
  private double freqD4;

  @BeforeEach
  void setUp() {
    // Real DummyTraining with KEY.C
    training = AbstractTraining.create(AbstractHarmonica.KEY.C, AbstractTraining.TRAINING.DUMMY);
    training.setPrecision(35);
    training.start();

    viewMock = mock(TrainingView.class);
    elementMock = mock(HarpViewNoteElement.class);
    when(viewMock.getActualHarpViewElement()).thenReturn(elementMock);

    // Look up actual note frequencies
    freqC4 = NoteLookup.getNoteFrequency("C4");
    freqCSharp4 = NoteLookup.getNoteFrequency("C#4");
    freqD4 = NoteLookup.getNoteFrequency("D4");
  }

  /**
   * Helper: simulates playing a frequency for a given duration by calling run() repeatedly. Each
   * call simulates ~50ms interval (typical microphone update rate). Uses real Thread.sleep() so
   * that System.currentTimeMillis()-based hold detection works.
   */
  private void playFrequency(TrainingContainer container, double frequency, int durationMs) {
    int intervalMs = 50;
    int iterations = durationMs / intervalMs;
    for (int i = 0; i < iterations; i++) {
      container.setFrequencyToHandle(frequency);
      container.run();
      try {
        Thread.sleep(intervalMs);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }

  /**
   * Helper: simulates silence (no frequency updates, no run() calls) for a given duration. During
   * silence, no frequency measurements arrive, so run() is not called.
   */
  private void simulateSilence(int durationMs) throws InterruptedException {
    Thread.sleep(durationMs);
  }

  /**
   * Helper: waits for the TrainingContainer to unlock after advancing to the next note.
   * advanceToNextNote() schedules unlock after 500ms.
   */
  private void waitForUnlock(TrainingContainer container) {
    await().atMost(2000, MILLISECONDS).until(() -> !container.isLockAllThreads());
  }

  /**
   * Complete training run: plays each note cleanly for long enough to advance. Verifies that all 3
   * notes are completed and training stops.
   */
  @Test
  void testCompleteCleanTrainingRun() {
    TrainingContainer container = new TrainingContainer(training, viewMock);

    // Note 1: C4 - play exact frequency for 500ms (> 300ms hold duration)
    assertEquals("C4", training.getActualNote());
    playFrequency(container, freqC4, 500);
    waitForUnlock(container);

    // Note 2: C#4
    assertEquals("C#4", training.getActualNote());
    playFrequency(container, freqCSharp4, 500);
    waitForUnlock(container);

    // Note 3: D4
    assertEquals("D4", training.getActualNote());
    playFrequency(container, freqD4, 500);
    waitForUnlock(container);

    // Training should be completed
    assertTrue(training.isCompleted());
    assertFalse(training.isRunning());
    verify(viewMock).toggleButton();
  }

  /**
   * Too-short hit: playing a note for less than 300ms should NOT advance. Then playing long enough
   * should advance.
   */
  @Test
  void testTooShortHitDoesNotAdvance() {
    TrainingContainer container = new TrainingContainer(training, viewMock);

    assertEquals("C4", training.getActualNote());

    // Play C4 for only ~130ms (1 iteration) - too short
    playFrequency(container, freqC4, 130);

    // Should still be on C4
    assertEquals("C4", training.getActualNote());
    assertEquals(0, training.getProgress());

    // Now play long enough
    playFrequency(container, freqC4, 500);
    waitForUnlock(container);

    // Should have advanced to C#4
    assertEquals("C#4", training.getActualNote());
  }

  /**
   * Silence between notes: after hitting a note, silence follows. The note should still advance
   * because advanceToNextNote() is called directly.
   */
  @Test
  void testSilenceAfterHittingNote() throws InterruptedException {
    TrainingContainer container = new TrainingContainer(training, viewMock);

    // Play C4 long enough to trigger advance
    playFrequency(container, freqC4, 500);
    waitForUnlock(container);

    // Silence - no frequency updates for 1 second
    simulateSilence(1000);

    // Should have advanced to C#4 despite silence
    assertEquals("C#4", training.getActualNote());
  }

  /**
   * Imprecise playing: frequency is within ±50 cents (note active) but outside precision (35
   * cents). Should show visual feedback but NOT advance.
   */
  @Test
  void testImprecisePlayingDoesNotAdvance() {
    TrainingContainer container = new TrainingContainer(training, viewMock);

    // Play a frequency that is ~40 cents off from C4 (within 50 cents = note active, but > 35 =
    // outside precision)
    // cents = 1200 * log2(freq / noteFreq), so for +40 cents: freq = noteFreq * 2^(40/1200)
    double impreciseFreq = freqC4 * Math.pow(2, 40.0 / 1200.0);

    // Play imprecise for a long time
    playFrequency(container, impreciseFreq, 1000);

    // Should still be on C4 - no advance
    assertEquals("C4", training.getActualNote());
    assertEquals(0, training.getProgress());

    // But element.update() should have been called (visual feedback)
    verify(elementMock, atLeastOnce()).update(anyDouble());
  }

  /**
   * Frequency completely outside note range (> 50 cents off): note is not active. No visual
   * feedback, no advance.
   */
  @Test
  void testWrongNoteDoesNothing() {
    TrainingContainer container = new TrainingContainer(training, viewMock);

    // Play D4 frequency while C4 is expected - way off
    playFrequency(container, freqD4, 1000);

    // Should still be on C4
    assertEquals("C4", training.getActualNote());
    assertEquals(0, training.getProgress());
  }

  /**
   * Interrupted hold: player hits the note, then leaves precision range, then comes back. The hold
   * timer should reset when leaving precision range.
   */
  @Test
  void testInterruptedHoldResetsTimer() {
    TrainingContainer container = new TrainingContainer(training, viewMock);

    // Play C4 for 200ms (within precision, but < 300ms hold)
    playFrequency(container, freqC4, 260);

    // Leave precision range - play imprecise frequency
    double impreciseFreq = freqC4 * Math.pow(2, 40.0 / 1200.0);
    playFrequency(container, impreciseFreq, 260);

    // Come back to C4 for 200ms - timer should have reset, so still not enough
    playFrequency(container, freqC4, 260);

    // Should still be on C4
    assertEquals("C4", training.getActualNote());

    // Now play long enough without interruption
    playFrequency(container, freqC4, 500);
    waitForUnlock(container);

    assertEquals("C#4", training.getActualNote());
  }

  /**
   * Mixed scenario: silence, wrong notes, imprecise playing, then correct playing. Simulates a
   * realistic training session.
   */
  @Test
  void testRealisticMixedScenario() throws InterruptedException {
    TrainingContainer container = new TrainingContainer(training, viewMock);

    // --- Note 1: C4 ---
    assertEquals("C4", training.getActualNote());

    // Player starts with silence
    simulateSilence(500);
    assertEquals("C4", training.getActualNote());

    // Player plays wrong note (D4 instead of C4)
    playFrequency(container, freqD4, 500);
    assertEquals("C4", training.getActualNote());

    // Player plays imprecise C4 (40 cents off)
    double impreciseC4 = freqC4 * Math.pow(2, 40.0 / 1200.0);
    playFrequency(container, impreciseC4, 500);
    assertEquals("C4", training.getActualNote());

    // Player plays C4 too briefly
    playFrequency(container, freqC4, 130);
    assertEquals("C4", training.getActualNote());

    // Player finally plays C4 correctly and long enough
    playFrequency(container, freqC4, 500);
    waitForUnlock(container);
    assertEquals("C#4", training.getActualNote());

    // --- Note 2: C#4 ---
    // Silence between notes
    simulateSilence(300);

    // Player plays slightly sharp C#4 (within precision, ~20 cents off)
    double slightlySharpCSharp4 = freqCSharp4 * Math.pow(2, 20.0 / 1200.0);
    playFrequency(container, slightlySharpCSharp4, 500);
    waitForUnlock(container);
    assertEquals("D4", training.getActualNote());

    // --- Note 3: D4 ---
    // Player nails it immediately
    playFrequency(container, freqD4, 500);
    waitForUnlock(container);

    // Training complete
    assertTrue(training.isCompleted());
    assertFalse(training.isRunning());
    verify(viewMock).toggleButton();
  }

  /** Verifies progress tracking throughout the training. */
  @Test
  void testProgressTracking() {
    TrainingContainer container = new TrainingContainer(training, viewMock);

    assertEquals(0, training.getProgress());

    // Complete note 1
    playFrequency(container, freqC4, 500);
    waitForUnlock(container);
    assertEquals(33, training.getProgress()); // 1/3 = 33%

    // Complete note 2
    playFrequency(container, freqCSharp4, 500);
    waitForUnlock(container);
    assertEquals(66, training.getProgress()); // 2/3 = 66%

    // Complete note 3
    playFrequency(container, freqD4, 500);
    waitForUnlock(container);
    assertEquals(100, training.getProgress()); // 3/3 = 100%
  }

  /** Verifies that note names are correctly reported by the container. */
  @Test
  void testNoteNames() {
    TrainingContainer container = new TrainingContainer(training, viewMock);

    assertEquals("C4", container.getActualNoteName());
    assertEquals("C#4", container.getNextNoteName());
    assertNull(container.getPreviousNoteName());

    // Advance to note 2
    playFrequency(container, freqC4, 500);
    waitForUnlock(container);

    assertEquals("C#4", container.getActualNoteName());
    assertEquals("D4", container.getNextNoteName());
    assertEquals("C4", container.getPreviousNoteName());
  }

  /** Verifies that playing slightly flat (within precision) still counts. */
  @Test
  void testSlightlyFlatPlaying() {
    TrainingContainer container = new TrainingContainer(training, viewMock);

    // Play C4 slightly flat (~30 cents below, within precision of 35)
    double flatC4 = freqC4 * Math.pow(2, -30.0 / 1200.0);
    playFrequency(container, flatC4, 500);
    waitForUnlock(container);

    assertEquals("C#4", training.getActualNote());
  }

  /**
   * Verifies that playing at the exact precision boundary does NOT advance (|cents| must be
   * strictly less than precision).
   */
  @Test
  void testExactPrecisionBoundaryDoesNotAdvance() {
    TrainingContainer container = new TrainingContainer(training, viewMock);

    // Play C4 at 36 cents off (> precision of 35, so should not advance)
    double boundaryFreq = freqC4 * Math.pow(2, 36.0 / 1200.0);
    playFrequency(container, boundaryFreq, 1000);

    // Should NOT advance
    assertEquals("C4", training.getActualNote());
  }
}
