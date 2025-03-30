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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit test class for validating the functionality of the NoteContainer class.
 * This class ensures proper behavior of methods like setting frequency, updating,
 * clearing the associated HarpViewNoteElement, and handling valid/invalid frequency ranges.
 */
class NoteContainerTest {
    private static final int CHANNEL = 1;
    private static final int NOTE = 5;
    private static final String NOTE_NAME = "C";
    private static final double MIN_FREQUENCY = 100.0;
    private static final double MAX_FREQUENCY = 200.0;
    private HarpViewNoteElement mockHarpViewElement;
    private NoteContainer noteContainer;
    private Harmonica mockHarmonica;

    /**
     * Sets up the testing environment before each test method is executed.
     * This method initializes and configures mock objects and the necessary
     * dependencies required for the tests within the NoteContainerTest class.
     * <p>
     * Tasks performed in this method:
     * - Creates mock objects for Harmonica, HarpView, and HarpViewNoteElement.
     * - Configures behavior for the mock objects using predefined data.
     * - Initializes an instance of the NoteContainer class with the required inputs.
     * <p>
     * Mocks and their behaviors:
     * - The mock Harmonica returns predefined minimum and maximum frequencies
     *   for a given channel and note when appropriate methods are called.
     * - The mock HarpView returns an associated HarpViewNoteElement for the
     *   specified channel and note.
     * - The mock HarpViewNoteElement is used to represent the visual element
     *   in the harp view for the corresponding note.
     */
    @BeforeEach
    void setUp() {
        // Create mocks
        mockHarmonica = mock(Harmonica.class);
        HarpView mockHarpView = mock(HarpView.class);
        mockHarpViewElement = mock(HarpViewNoteElement.class);

        // Define mock behaviors
        when(mockHarmonica.getNoteFrequencyMinimum(CHANNEL, NOTE)).thenReturn(MIN_FREQUENCY);
        when(mockHarmonica.getNoteFrequencyMaximum(CHANNEL, NOTE)).thenReturn(MAX_FREQUENCY);
        when(mockHarpView.getHarpViewElement(CHANNEL, NOTE)).thenReturn(mockHarpViewElement);

        // Create NoteContainer
        noteContainer = new NoteContainer(CHANNEL, NOTE, NOTE_NAME, mockHarmonica, mockHarpView);
    }

    /**
     * Tests that the `update` method of `HarpViewNoteElement` is called once when the provided
     * frequency is within a valid range, and ensures that the `clear` method is never called
     * under this condition.
     * <p>
     * Preconditions:
     * - The provided frequency to handle is within the defined minimum and maximum valid range.
     * <p>
     * Test Steps:
     * 1. A valid frequency value is assigned using the `setFrequencyToHandle` method of the
     *    `NoteContainer` class.
     * 2. The `run` method of the `NoteContainer` class is executed.
     * 3. It is verified that the `update` method of the mocked `HarpViewNoteElement` is called exactly once.
     * 4. It is verified that the `clear` method of the mocked `HarpViewNoteElement` is never called.
     * <p>
     * Expected Behavior:
     * - The `update` method is triggered for frequency changes within the valid range.
     * - The `clear` method is not triggered when the frequency remains within the valid range.
     */
    @Test
    void testUpdateCalled_WhenFrequencyInRange() {
        // Arrange
        double validFrequency = 150.0; // Within range
        noteContainer.setFrequencyToHandle(validFrequency);

        // Act
        noteContainer.run();

        // Assert
        verify(mockHarpViewElement, times(1)).update(anyDouble()); // Update called once
        verify(mockHarpViewElement, never()).clear(); // Clear not called
    }

    /**
     * Verifies that the `clear` method of the `HarpViewNoteElement` is called exactly once
     * when the frequency goes out of a valid range and ensures that the `update` method is
     * not excessively called during this scenario.
     * <p>
     * Preconditions:
     * - A frequency value is initially set within the valid range of `MIN_FREQUENCY` and
     *   `MAX_FREQUENCY`.
     * - The frequency value is then updated to a value outside the valid range.
     * <p>
     * Test Steps:
     * 1. Set a frequency within the valid range using `setFrequencyToHandle`.
     * 2. Execute the `run` method to simulate normal operation with a valid frequency.
     * 3. Wait for the scheduler to complete the task execution.
     * 4. Update the frequency to a value beyond the defined valid range using `setFrequencyToHandle`.
     * 5. Execute the `run` method again to trigger out-of-range handling logic.
     * 6. Wait for the scheduler to complete the task execution.
     * <p>
     * Assertions:
     * - The `update` method of the mocked `HarpViewNoteElement` is called once when the frequency
     *   was within the valid range.
     * - The `clear` method of the mocked `HarpViewNoteElement` is called once when the frequency
     *   transitions to an out-of-range value.
     */
    @Test
    void testClearCleanNotCalled_WhenFrequencyOutOfRange() {
        // Arrange
        double outOfRangeFrequency = 250.0; // Out of range
        double inRangeFrequency = 150.0;   // Within range

        // First, set frequency within range
        noteContainer.setFrequencyToHandle(inRangeFrequency);
        noteContainer.run();

        // Wait for the "update" to be called
        await().atMost(1000, MILLISECONDS).untilAsserted(() -> verify(mockHarpViewElement, atLeastOnce()).update(anyDouble()));

        // Then set frequency out of range
        noteContainer.setFrequencyToHandle(outOfRangeFrequency);

        // Act
        noteContainer.run();

        // Wait for the final conditions to be met
        await().atMost(1000, MILLISECONDS).untilAsserted(() -> {
            verify(mockHarpViewElement, times(1)).update(anyDouble()); // Update called first
            verify(mockHarpViewElement, times(1)).clear();             // Clear called once
        });
    }


    /**
     * Verifies that the `clear` method of the `HarpViewNoteElement` is not invoked when the provided
     * frequency remains out of the valid range during the test execution.
     * <p>
     * Preconditions:
     * - A frequency value is set that exceeds the defined valid range (`MIN_FREQUENCY` and `MAX_FREQUENCY`).
     * <p>
     * Test Steps:
     * 1. Set a frequency outside the valid range using the `setFrequencyToHandle` method of the `NoteContainer` class.
     * 2. Execute the `run` method of the `NoteContainer` class to simulate the handling of the out-of-range frequency.
     * 3. Allow some time to pass to simulate scheduler execution and possible state changes.
     * <p>
     * Expected Behavior:
     * - The `clear` method is never called because the frequency remains out of the valid range throughout the test.
     * - There is no repeated clearing of the element for continuous out-of-range frequencies.
     *
     */
    @Test
    void testClearNotCalled_WhenFrequencyRemainsOutOfRange() {
        // Arrange
        double outOfRangeFrequency = 250.0; // Frequency out of range

        // Set frequency out of range
        noteContainer.setFrequencyToHandle(outOfRangeFrequency);

        // Act
        noteContainer.run(); // First run

        // Assert
        await().atMost(1000, MILLISECONDS).untilAsserted(() -> {
            verify(mockHarpViewElement, never()).clear(); // Verify "clear" was not called
        });
    }


    /**
     * Tests that the `update` method of the `HarpViewNoteElement` is called again after the frequency
     * transitions from out-of-range back to within the valid range, and ensures that the `clear`
     * method is not called during this transition.
     * <p>
     * Preconditions:
     * - A frequency value is initially set outside the valid range (`MIN_FREQUENCY` to `MAX_FREQUENCY`).
     * - The frequency is then updated to a value within the valid range.
     * <p>
     * Test Steps:
     * 1. Set the frequency to a value outside the valid range using `setFrequencyToHandle`.
     * 2. Execute the `run` method to simulate handling for the out-of-range frequency.
     * 3. Update the frequency to a value within the valid range.
     * 4. Execute the `run` method again to simulate handling for the new in-range frequency.
     * <p>
     * Assertions:
     * - The `update` method of the mocked `HarpViewNoteElement` is called exactly once after the
     *   frequency returns to the valid range.
     * - The `clear` method of the mocked `HarpViewNoteElement` is never called during this test case.
     */
    @Test
    void testUpdateCalledAgain_WhenFrequencyBackInRange() {
        // Arrange
        double outOfRangeFrequency = 250.0; // Out of range
        double inRangeFrequency = 150.0;   // Within range

        // Set frequency out of range
        noteContainer.setFrequencyToHandle(outOfRangeFrequency);
        noteContainer.run();

        // Set frequency back in range
        noteContainer.setFrequencyToHandle(inRangeFrequency);

        // Act
        noteContainer.run();

        // Assert
        verify(mockHarpViewElement, times(0)).clear(); // Clear not called
        verify(mockHarpViewElement, times(1)).update(anyDouble()); // Update called again
    }

    /**
     * Verifies that when inverse handling is set to true, the `update` method of the `HarpViewNoteElement`
     * is called with a negative value of the calculated cents.
     * <p>
     * Preconditions:
     * - The `NoteContainer` instance is initialized with inverse handling set to true.
     * - The tested frequency is within the valid range specified by `MIN_FREQUENCY` and `MAX_FREQUENCY`.
     * - Mocked `Harmonica` and `HarpViewNoteElement` are configured with the necessary behaviors.
     * <p>
     * Test Steps:
     * 1. Set a valid frequency within the defined range using the `setFrequencyToHandle` method.
     * 2. Execute the `run` method of the `NoteContainer` instance.
     * 3. Verify that the `update` method of the `HarpViewNoteElement` is invoked exactly once with the negative
     *    value of the computed cents.
     * 4. Verify that the `clear` method of the `HarpViewNoteElement` is never called during the execution.
     * <p>
     * Expected Behavior:
     * - When inverse handling is true, the calculated cents are negated, and the `update` method is called
     *   with this negative value.
     * - The `clear` method is not invoked since the frequency remains within the valid range.
     */
    @Test
    void testNegativeCentsWhenInverseHandlingTrue() {
        // Arrange
        double validFrequency = 150.0; // Within range
        Harmonica mockHarmonicaWithInverse = mock(Harmonica.class);
        HarpView mockHarpViewWithInverse = mock(HarpView.class);
        HarpViewNoteElement mockHarpViewElementWithInverse = mock(HarpViewNoteElement.class);

        when(mockHarmonicaWithInverse.getNoteFrequencyMinimum(CHANNEL, NOTE)).thenReturn(MIN_FREQUENCY);
        when(mockHarmonicaWithInverse.getNoteFrequencyMaximum(CHANNEL, NOTE)).thenReturn(MAX_FREQUENCY);
        when(mockHarpViewWithInverse.getHarpViewElement(CHANNEL, NOTE)).thenReturn(mockHarpViewElementWithInverse);
        when(mockHarmonicaWithInverse.getCentsNote(CHANNEL, NOTE, validFrequency)).thenReturn(50.0);

        NoteContainer inverseNoteContainer = new NoteContainer(
                CHANNEL, NOTE, NOTE_NAME, mockHarmonicaWithInverse, mockHarpViewWithInverse, true
        );

        inverseNoteContainer.setFrequencyToHandle(validFrequency);

        // Act
        inverseNoteContainer.run();

        // Assert
        verify(mockHarpViewElementWithInverse, times(1)).update(-50.0);
        verify(mockHarpViewElementWithInverse, never()).clear();
    }

    /**
     * Tests the behavior of the `setFrequencyToHandle` method in the `NoteContainer` class and ensures
     * that the frequency value is properly updated and handled by the `run` method.
     * <p>
     * Preconditions:
     * - The `NoteContainer` instance is initialized and ready for testing.
     * - A valid frequency value is provided.
     * <p>
     * Test Steps:
     * 1. Set a valid frequency value using the `setFrequencyToHandle` method.
     * 2. Execute the `run` method of the `NoteContainer` instance.
     * 3. Verify that the frequency value is correctly updated within the `NoteContainer` instance.
     * <p>
     * Assertions:
     * - The frequency value of the `NoteContainer` matches the value provided via `setFrequencyToHandle`.
     */
    @Test
    void testSetFrequencyToHandleUpdatesFrequency() {
        // Arrange
        double expectedFrequency = 123.4;

        // Act
        noteContainer.setFrequencyToHandle(expectedFrequency);

        // Assert
        noteContainer.run();
        assertEquals(expectedFrequency, noteContainer.frequencyToHandle);
    }


    /**
     * Verifies that neither the `update` method nor the `clear` method of the mocked `HarpViewNoteElement`
     * is called when an invalid frequency is provided.
     * <p>
     * Preconditions:
     * - The provided frequency is outside the valid range defined by `MIN_FREQUENCY` and `MAX_FREQUENCY`.
     * <p>
     * Test Steps:
     * 1. An invalid frequency value is set using the `setFrequencyToHandle` method of the `NoteContainer` class.
     * 2. The `run` method of the `NoteContainer` class is executed to handle the invalid frequency.
     * 3. The test verifies that neither the `update` method nor the `clear` method of the mocked `HarpViewNoteElement` is invoked.
     * <p>
     * Expected Behavior:
     * - The `update` method is not called as the frequency is invalid.
     * - The `clear` method is not called as no valid state transition occurs for the invalid frequency.
     */
    @Test
    void testNoUpdateOrClearCalledForInvalidFrequency() {
        // Arrange
        double invalidFrequency = 300.0; // Out of range

        noteContainer.setFrequencyToHandle(invalidFrequency);

        // Act
        noteContainer.run();

        // Assert
        verify(mockHarpViewElement, never()).update(anyDouble());
        verify(mockHarpViewElement, never()).clear();
    }
    /**
     * Tests that the `run` method of `noteContainer` correctly calls the `update` method on the
     * `HarpViewElement` mock when provided with a valid frequency.
     * <p>
     * The test verifies the following behavior:
     * - For a frequency within the valid range, the `update` method of the `HarpViewElement` mock
     *   is called exactly once with the correct `cents` value obtained from the `Harmonica` mock.
     * - The `clear` method of the `HarpViewElement` mock is never called during this process.
     * <p>
     * This ensures that the `noteContainer` processes valid frequency inputs as expected,
     * updating the view with the calculated pitch adjustment in cents.
     */
    @Test
    void testRunCallsUpdateForValidFrequency() {
        // Arrange
        double validFrequency = 150.0; // Frequency in range
        double cents = 10.0;

        when(mockHarmonica.getCentsNote(CHANNEL, NOTE, validFrequency)).thenReturn(cents);

        noteContainer.setFrequencyToHandle(validFrequency);

        // Act
        noteContainer.run();

        // Assert
        verify(mockHarpViewElement, times(1)).update(cents);
        verify(mockHarpViewElement, never()).clear();
    }

    @Test
    void testRunCallsNoClearForInvalidFrequency() {
        // Arrange
        double invalidFrequency = 300.0; // Frequency out of range
        noteContainer.setFrequencyToHandle(invalidFrequency);

        // Act
        noteContainer.run();

        // Assert
        await().atMost(200, MILLISECONDS).untilAsserted(() -> {
            verify(mockHarpViewElement, never()).clear();
            verify(mockHarpViewElement, never()).update(anyDouble());
        });
    }


    /**
     * Test that the {@code run} method in the {@code noteContainer} does not update or clear
     * the {@code mockHarpViewElement} when the frequency change does not exceed the defined threshold.
     * <p>
     * This test verifies the behavior of the system in handling minimal frequency change scenarios.
     * Initially, a valid frequency and a corresponding unchanged cents value are set and cached
     * via the {@code setFrequencyToHandle} and {@code run} calls. Subsequently, a minimal change
     * in the frequency is simulated that remains under the threshold to trigger an update or clear
     * operation. The assertions confirm that no additional updates or clear operations are performed
     * beyond the initial call.
     * <p>
     * Mocks used:
     * - {@code mockHarmonica}: Simulates the behavior of determining the cents offset for a given frequency.
     * - {@code mockHarpViewElement}: Tracks the interactions for the update and clear operations.
     * <p>
     * Assertions:
     * - {@code update(anyDouble())} is called exactly once (during the initial call only).
     * - {@code clear()} is never called as the frequency change remains under threshold.
     */
    @Test
    void testRunDoesNotUpdateOrClearForUnchangedFrequency() {
        // Arrange
        double validFrequency = 150.0; // Frequency within range
        double unchangedCents = 10.0;

        when(mockHarmonica.getCentsNote(CHANNEL, NOTE, validFrequency)).thenReturn(unchangedCents);

        // First call to set the cache
        noteContainer.setFrequencyToHandle(validFrequency);
        noteContainer.run();

        // Simulate minimal change that does not exceed threshold
        when(mockHarmonica.getCentsNote(CHANNEL, NOTE, validFrequency)).thenReturn(unchangedCents + 1);

        // Act
        noteContainer.run();

        // Assert
        verify(mockHarpViewElement, times(1)).update(anyDouble()); // Only first call
        verify(mockHarpViewElement, never()).clear();
    }

    /**
     * Tests that the {@code getChannel} method of the {@code noteContainer} object
     * correctly returns the expected channel value.
     * <p>
     * This test verifies that the method {@code getChannel} returns the correct
     * channel value, as defined by the constant {@code CHANNEL}. It ensures
     * consistency and correctness of the channel retrieval functionality.
     * <p>
     * The test performs the following steps:
     * 1. Invokes the {@code getChannel} method from the {@code noteContainer}.
     * 2. Asserts that the returned channel value matches the expected {@code CHANNEL} value.
     */
    @Test
    void testGetChannelReturnsCorrectValue() {
        // Act
        int returnedChannel = noteContainer.getChannel();

        // Assert
        assertEquals(CHANNEL, returnedChannel, "The getChannel method should return the correct channel.");
    }

    /**
     * Tests the getChannel method of the NoteContainer class when only the primary constructor is used.
     * <p>
     * The method verifies that the channel value passed to the constructor is correctly retrieved
     * using the getChannel method. It ensures that the channel value is accurately maintained
     * within the NoteContainer instance.
     * <p>
     * This test is useful to confirm that the primary constructor correctly initializes
     * and preserves the channel information.
     */
    @Test
    void testGetChannelWithConstructorOnly() {
        // Arrange
        int expectedChannel = 2;
        NoteContainer simpleNoteContainer = new NoteContainer(expectedChannel, NOTE, NOTE_NAME);

        // Act
        int returnedChannel = simpleNoteContainer.getChannel();

        // Assert
        assertEquals(expectedChannel, returnedChannel, "The getChannel method should work correctly with the primary constructor.");
    }
}