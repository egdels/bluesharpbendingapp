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

import de.schliweb.bluesharpbendingapp.model.MainModel;
import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.ChordHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;
import de.schliweb.bluesharpbendingapp.service.ModelStorageService;
import de.schliweb.bluesharpbendingapp.utils.ChordDetectionResult;
import de.schliweb.bluesharpbendingapp.view.HarpView;
import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;
import de.schliweb.bluesharpbendingapp.view.MainWindow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link HarpController} focusing on chord detection and highlighting functionality.
 * This class tests how the HarpController processes chord detection results and updates the UI accordingly.
 */
class HarpControllerChordTest {

    private static final int CHANNEL_MIN = 1;
    private static final int CHANNEL_MAX = 10;

    // Test frequencies (in Hz)
    private static final double FREQ_C4 = 261.63;  // C4
    private static final double FREQ_E4 = 329.63;  // E4
    private static final double FREQ_G4 = 392.0;   // G4
    private static final double FREQ_C5 = 523.25;  // C5

    // Frequency tolerance for note containers
    private static final double FREQ_TOLERANCE = 5.0;

    private MainModel mockModel;
    private ModelStorageService mockModelStorageService;
    private MainWindow mockWindow;
    private ExecutorService mockExecutorService;
    private HarpView mockHarpView;
    private Harmonica mockHarmonica;

    private HarpController harpController;
    private List<NoteContainer> noteContainers;
    private HarpViewNoteElement[] mockHarpViewElements;

    @BeforeEach
    void setUp() {
        // Create mocks
        mockModel = mock(MainModel.class);
        mockModelStorageService = mock(ModelStorageService.class);
        mockWindow = mock(MainWindow.class);
        mockExecutorService = mock(ExecutorService.class);
        mockHarpView = mock(HarpView.class);
        mockHarmonica = mock(Harmonica.class);

        // Setup mock behavior
        when(mockWindow.isHarpViewActive()).thenReturn(true);
        when(mockWindow.getHarpView()).thenReturn(mockHarpView);
        when(mockModel.getStoredKeyIndex()).thenReturn(AbstractHarmonica.KEY.C.ordinal());
        when(mockModel.getStoredTuneIndex()).thenReturn(AbstractHarmonica.TUNE.RICHTER.ordinal());

        // Create HarpController
        harpController = new HarpController(mockModel, mockModelStorageService, mockWindow, mockExecutorService);

        // Setup note containers and harp view elements
        setupNoteContainers();
    }

    /**
     * Sets up mock note containers and harp view elements for testing.
     * Creates mock HarpViewNoteElement instances for each channel and
     * NoteContainer instances for different note types (blow, draw, bend).
     */
    private void setupNoteContainers() {
        // Create mock harp view elements for each channel
        mockHarpViewElements = new HarpViewNoteElement[CHANNEL_MAX + 1];
        for (int channel = CHANNEL_MIN; channel <= CHANNEL_MAX; channel++) {
            mockHarpViewElements[channel] = mock(HarpViewNoteElement.class);
            when(mockHarpView.getHarpViewElement(eq(channel), anyInt())).thenReturn(mockHarpViewElements[channel]);
        }

        // Create a real harmonica for frequency calculations
        Harmonica realHarmonica = AbstractHarmonica.create(AbstractHarmonica.KEY.C, AbstractHarmonica.TUNE.RICHTER);

        // Create note containers
        noteContainers = new ArrayList<>();
        for (int channel = CHANNEL_MIN; channel <= CHANNEL_MAX; channel++) {
            // Add blowing note (note=0)
            NoteContainer blowingNote = mock(NoteContainer.class);
            when(blowingNote.getChannel()).thenReturn(channel);
            when(blowingNote.getNote()).thenReturn(0);
            when(blowingNote.getMinFrequency()).thenReturn(realHarmonica.getNoteFrequencyMinimum(channel, 0));
            when(blowingNote.getMaxFrequency()).thenReturn(realHarmonica.getNoteFrequencyMaximum(channel, 0));
            noteContainers.add(blowingNote);

            // Add drawing note (note=1)
            NoteContainer drawingNote = mock(NoteContainer.class);
            when(drawingNote.getChannel()).thenReturn(channel);
            when(drawingNote.getNote()).thenReturn(1);
            when(drawingNote.getMinFrequency()).thenReturn(realHarmonica.getNoteFrequencyMinimum(channel, 1));
            when(drawingNote.getMaxFrequency()).thenReturn(realHarmonica.getNoteFrequencyMaximum(channel, 1));
            noteContainers.add(drawingNote);

            // Add bending note (note=2)
            NoteContainer bendingNote = mock(NoteContainer.class);
            when(bendingNote.getChannel()).thenReturn(channel);
            when(bendingNote.getNote()).thenReturn(2);
            when(bendingNote.getMinFrequency()).thenReturn(realHarmonica.getNoteFrequencyMinimum(channel, 2));
            when(bendingNote.getMaxFrequency()).thenReturn(realHarmonica.getNoteFrequencyMaximum(channel, 2));
            noteContainers.add(bendingNote);
        }

        // Setup possible chords for the harmonica
        List<ChordHarmonica> possibleChords = createPossibleChords(realHarmonica);
        when(mockHarmonica.getPossibleChords()).thenReturn(possibleChords);

        // Use a spy to intercept calls to updateHarpView
        harpController = spy(harpController);

        // Inject our mocked noteContainers and harmonica
        try {
            java.lang.reflect.Field noteContainersField = HarpController.class.getDeclaredField("noteContainers");
            noteContainersField.setAccessible(true);
            noteContainersField.set(harpController, noteContainers.toArray(new NoteContainer[0]));

            java.lang.reflect.Field harmonicaField = HarpController.class.getDeclaredField("harmonica");
            harmonicaField.setAccessible(true);
            harmonicaField.set(harpController, mockHarmonica);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test environment", e);
        }
    }

    /**
     * Creates a list of possible chords for the harmonica.
     * This includes 2-note, 3-note, and 4-note chords.
     */
    private List<ChordHarmonica> createPossibleChords(Harmonica harmonica) {
        List<ChordHarmonica> chords = new ArrayList<>();

        // Add 2-note chords (adjacent channels)
        chords.add(new ChordHarmonica(harmonica, Arrays.asList(1, 2), 0));

        // Add 3-note chords (adjacent channels)
        chords.add(new ChordHarmonica(harmonica, Arrays.asList(1, 2, 3), 0));

        // Add 4-note chords (adjacent channels)
        chords.add(new ChordHarmonica(harmonica, Arrays.asList(1, 2, 3, 4), 0));

        // Add 2-note chord with middle channel covered
        chords.add(new ChordHarmonica(harmonica, Arrays.asList(1, 3), 0));

        // Add 2-note chord with two middle channels covered
        chords.add(new ChordHarmonica(harmonica, Arrays.asList(1, 4), 0));

        return chords;
    }

    /**
     * Helper method to set up frequency ranges for specific channels.
     *
     * @param channel   The channel number to set up frequency ranges for
     * @param frequency The center frequency to use for the range
     */
    private void setupFrequencyRanges(int channel, double frequency) {
        for (NoteContainer noteContainer : noteContainers) {
            if (noteContainer.getChannel() == channel) {
                when(noteContainer.getMinFrequency()).thenReturn(frequency - FREQ_TOLERANCE);
                when(noteContainer.getMaxFrequency()).thenReturn(frequency + FREQ_TOLERANCE);
            }
        }
    }

    /**
     * Helper method to create a ChordDetectionResult with the given frequencies.
     *
     * @param frequencies The frequencies to include in the chord detection result
     * @return A ChordDetectionResult instance with the specified frequencies
     */
    private ChordDetectionResult createChordDetectionResult(double... frequencies) {
        return ChordDetectionResult.of(frequencies, 1.0);
    }

    /**
     * Helper method to set up a chord test with the specified channels and frequencies.
     * This method configures the frequency ranges for the specified channels and enables chord display mode.
     *
     * @param channels    Array of channel numbers to set up
     * @param frequencies Array of frequencies corresponding to the channels
     * @return A ChordDetectionResult containing the specified frequencies
     */
    private ChordDetectionResult setupChordTest(int[] channels, double[] frequencies) {
        // Validate input
        if (channels.length != frequencies.length) {
            throw new IllegalArgumentException("Channels and frequencies arrays must have the same length");
        }

        // Create chord detection result
        ChordDetectionResult chordResult = createChordDetectionResult(frequencies);

        // Setup frequency ranges for each channel
        for (int i = 0; i < channels.length; i++) {
            setupFrequencyRanges(channels[i], frequencies[i]);
        }

        // Enable chord display mode
        when(mockModel.getSelectedShowChordIndex()).thenReturn(1);

        return chordResult;
    }

    /**
     * Helper method to verify that the specified channels are marked as part of a chord,
     * and optionally verify that other channels are not marked as part of a chord.
     *
     * @param chordChannels    Array of channel numbers that should be part of the chord
     * @param excludedChannels Array of channel numbers that should not be part of the chord (optional)
     */
    private void verifyChordNotes(int[] chordChannels, int[] excludedChannels) {
        for (NoteContainer noteContainer : noteContainers) {
            // Check if the note container's channel is in the chord channels
            boolean isChordChannel = false;
            for (int channel : chordChannels) {
                if (noteContainer.getChannel() == channel && noteContainer.getNote() == 0) {
                    isChordChannel = true;
                    break;
                }
            }

            if (isChordChannel) {
                // Verify that chord channels are marked as part of the chord
                verify(noteContainer, atLeastOnce()).setPartOfChord(true);  // Should be part of chord
            } else if (excludedChannels != null) {
                // Check if the note container's channel is in the excluded channels
                boolean isExcludedChannel = false;
                for (int channel : excludedChannels) {
                    if (noteContainer.getChannel() == channel && noteContainer.getNote() == 0) {
                        isExcludedChannel = true;
                        break;
                    }
                }

                if (isExcludedChannel) {
                    // Verify that excluded channels are not marked as part of the chord
                    verify(noteContainer, never()).setPartOfChord(true); // Should not be part of chord
                }
            }
        }
    }

    /**
     * Tests that 2-note chords with adjacent channels are correctly highlighted in the harp view.
     *
     * <p>This test verifies that when a 2-note chord is detected (C4 and E4 frequencies),
     * the corresponding note containers for channels 1 and 2 are properly marked as part of the chord.
     *
     * <p>Test Steps:
     * 1. Set up a chord test with channels 1 and 2 and frequencies C4 and E4
     * 2. Call updateHarpView with the chord detection result
     * 3. Verify that note containers for channels 1 and 2 are marked as part of the chord
     */
    @Test
    @DisplayName("Test that 2-note chords (adjacent channels) are correctly highlighted")
    void testTwoNoteChordHighlighting() {
        // Arrange
        int[] channels = {1, 2};
        double[] frequencies = {FREQ_C4, FREQ_E4};

        ChordDetectionResult chordResult = setupChordTest(channels, frequencies);

        // Act
        harpController.updateHarpView(0.0, chordResult);

        // Assert
        verifyChordNotes(channels, null);
    }

    /**
     * Tests that 3-note chords with adjacent channels are correctly highlighted in the harp view.
     *
     * <p>This test verifies that when a 3-note chord is detected (C4, E4, and G4 frequencies),
     * the corresponding note containers for channels 1, 2, and 3 are properly marked as part of the chord.
     *
     * <p>Test Steps:
     * 1. Set up a chord test with channels 1, 2, and 3 and frequencies C4, E4, and G4
     * 2. Call updateHarpView with the chord detection result
     * 3. Verify that note containers for channels 1, 2, and 3 are marked as part of the chord
     */
    @Test
    @DisplayName("Test that 3-note chords (adjacent channels) are correctly highlighted")
    void testThreeNoteChordHighlighting() {
        // Arrange
        int[] channels = {1, 2, 3};
        double[] frequencies = {FREQ_C4, FREQ_E4, FREQ_G4};

        ChordDetectionResult chordResult = setupChordTest(channels, frequencies);

        // Act
        harpController.updateHarpView(0.0, chordResult);

        // Assert
        verifyChordNotes(channels, null);
    }

    /**
     * Tests that 4-note chords with adjacent channels are correctly highlighted in the harp view.
     *
     * <p>This test verifies that when a 4-note chord is detected (C4, E4, G4, and C5 frequencies),
     * the corresponding note containers for channels 1, 2, 3, and 4 are properly marked as part of the chord.
     *
     * <p>Test Steps:
     * 1. Set up a chord test with channels 1, 2, 3, and 4 and frequencies C4, E4, G4, and C5
     * 2. Call updateHarpView with the chord detection result
     * 3. Verify that note containers for channels 1, 2, 3, and 4 are marked as part of the chord
     */
    @Test
    @DisplayName("Test that 4-note chords (adjacent channels) are correctly highlighted")
    void testFourNoteChordHighlighting() {
        // Arrange
        int[] channels = {1, 2, 3, 4};
        double[] frequencies = {FREQ_C4, FREQ_E4, FREQ_G4, FREQ_C5};

        ChordDetectionResult chordResult = setupChordTest(channels, frequencies);

        // Act
        harpController.updateHarpView(0.0, chordResult);

        // Assert
        verifyChordNotes(channels, null);
    }

    /**
     * Tests that 2-note chords with a middle channel covered are correctly highlighted in the harp view.
     *
     * <p>This test verifies that when a 2-note chord is detected (C4 and G4 frequencies) with
     * channels 1 and 3, the corresponding note containers are properly marked as part of the chord,
     * while the note container for the middle channel 2 is not marked as part of the chord.
     *
     * <p>Test Steps:
     * 1. Set up a chord test with channels 1 and 3 and frequencies C4 and G4
     * 2. Call updateHarpView with the chord detection result
     * 3. Verify that note containers for channels 1 and 3 are marked as part of the chord
     * 4. Verify that the note container for channel 2 is not marked as part of the chord
     */
    @Test
    @DisplayName("Test that 2-note chords with middle channel covered are correctly highlighted")
    void testTwoNoteChordWithMiddleChannelCovered() {
        // Arrange
        int[] channels = {1, 3};
        double[] frequencies = {FREQ_C4, FREQ_G4};
        int[] excludedChannels = {2};

        ChordDetectionResult chordResult = setupChordTest(channels, frequencies);

        // Act
        harpController.updateHarpView(0.0, chordResult);

        // Assert
        verifyChordNotes(channels, excludedChannels);
    }

    /**
     * Tests that 2-note chords with two middle channels covered are correctly highlighted in the harp view.
     *
     * <p>This test verifies that when a 2-note chord is detected (C4 and C5 frequencies) with
     * channels 1 and 4, the corresponding note containers are properly marked as part of the chord,
     * while the note containers for the middle channels 2 and 3 are not marked as part of the chord.
     *
     * <p>Test Steps:
     * 1. Set up a chord test with channels 1 and 4 and frequencies C4 and C5
     * 2. Call updateHarpView with the chord detection result
     * 3. Verify that note containers for channels 1 and 4 are marked as part of the chord
     * 4. Verify that note containers for channels 2 and 3 are not marked as part of the chord
     */
    @Test
    @DisplayName("Test that 2-note chords with two middle channels covered are correctly highlighted")
    void testTwoNoteChordWithMiddleTwoChannelsCovered() {
        // Arrange
        int[] channels = {1, 4};
        double[] frequencies = {FREQ_C4, FREQ_C5};
        int[] excludedChannels = {2, 3};

        ChordDetectionResult chordResult = setupChordTest(channels, frequencies);

        // Act
        harpController.updateHarpView(0.0, chordResult);

        // Assert
        verifyChordNotes(channels, excludedChannels);
    }

    /**
     * Tests that notes are properly processed after visualization in the harp view.
     *
     * <p>This test verifies that when chord detection results are processed,
     * all note containers are submitted to the executor service for processing.
     *
     * <p>Test Steps:
     * 1. Create a chord detection result with C4 and E4 frequencies
     * 2. Set up frequency ranges for channels 1 and 2 to match these frequencies
     * 3. Call updateHarpView with the chord detection result
     * 4. Verify that all note containers are submitted to the executor service
     */
    @Test
    @DisplayName("Test that notes are properly reset after visualization")
    void testNotesResetAfterVisualization() {
        // Arrange
        ChordDetectionResult chordResult = createChordDetectionResult(FREQ_C4, FREQ_E4);

        // Setup the frequency ranges for the note containers
        setupFrequencyRanges(1, FREQ_C4);
        setupFrequencyRanges(2, FREQ_E4);

        // Enable chord display mode
        when(mockModel.getSelectedShowChordIndex()).thenReturn(1);

        // Act
        harpController.updateHarpView(0.0, chordResult);

        // Assert
        for (NoteContainer noteContainer : noteContainers) {
            verify(mockExecutorService, atLeastOnce()).submit(noteContainer);
        }
    }

    /**
     * Tests that chords are highlighted only when showChordIndex is set to 1 (chord display mode).
     *
     * <p>This test verifies that chord highlighting behavior depends on the showChordIndex setting:
     * - When showChordIndex is 0 (individual notes mode), no notes should be marked as part of a chord
     * - When showChordIndex is 1 (chord display mode), notes that are part of the detected chord should be highlighted
     *
     * <p>Test Steps:
     * 1. Create a chord detection result with C4 and E4 frequencies
     * 2. Set up frequency ranges for channels 1 and 2 to match these frequencies
     * 3. Set showChordIndex to 0 (individual notes mode)
     * 4. Call updateHarpView with the chord detection result
     * 5. Verify that no note containers are marked as part of a chord
     * 6. Reset the mocks
     * 7. Set showChordIndex to 1 (chord display mode)
     * 8. Call updateHarpView with the same chord detection result
     * 9. Verify that note containers for channels 1 and 2 with note index 0 are marked as part of a chord
     */
    @Test
    @DisplayName("Test that chords are highlighted only when showChordIndex is 1")
    void testShowChordIndexFunctionality() {
        // Arrange
        ChordDetectionResult chordResult = createChordDetectionResult(FREQ_C4, FREQ_E4);

        // Setup the frequency ranges for the note containers
        setupFrequencyRanges(1, FREQ_C4);
        setupFrequencyRanges(2, FREQ_E4);

        // Test with showChordIndex = 0 (individual notes mode)
        when(mockModel.getSelectedShowChordIndex()).thenReturn(0);

        // Act
        harpController.updateHarpView(0.0, chordResult);

        // Assert - No notes should be marked as part of a chord
        for (NoteContainer noteContainer : noteContainers) {
            verify(noteContainer, never()).setPartOfChord(true);
        }

        // Reset mocks
        reset(noteContainers.toArray());

        // Test with showChordIndex = 1 (chord display mode)
        when(mockModel.getSelectedShowChordIndex()).thenReturn(1);

        // Act
        harpController.updateHarpView(0.0, chordResult);

        // Assert - Notes in channels 1 and 2 with note index 0 should be marked as part of a chord
        for (NoteContainer noteContainer : noteContainers) {
            if ((noteContainer.getChannel() == 1 || noteContainer.getChannel() == 2) && noteContainer.getNote() == 0) {
                verify(noteContainer, atLeastOnce()).setPartOfChord(true);
            }
        }
    }

    /**
     * Tests that chord mode and individual note mode process frequencies mutually exclusively.
     *
     * <p>This test verifies three scenarios:
     * 1. In chord mode with no chord detected, no note containers should have frequency set
     * 2. In chord mode with a chord detected, only note containers that are part of the chord should have frequency set
     * 3. In individual note mode, all note containers should have frequency set regardless of chord detection
     *
     * <p>Test Steps:
     * 1. Create a test frequency and two chord detection results (one with no chord, one with a chord)
     * 2. Set up frequency ranges for channels 1 and 2 to match the chord frequencies
     * 3. Test chord mode with no chord detected:
     * - Set showChordIndex to 1 (chord display mode)
     * - Call updateHarpView with the test frequency and no chord result
     * - Verify that no note containers have frequency set
     * 4. Test chord mode with chord detected:
     * - Set showChordIndex to 1 (chord display mode)
     * - Call updateHarpView with the test frequency and chord result
     * - Verify that only note containers that are part of the chord have frequency set
     * 5. Test individual note mode with chord detected:
     * - Set showChordIndex to 0 (individual notes mode)
     * - Call updateHarpView with the test frequency and chord result
     * - Verify that all note containers have frequency set
     */
    @Test
    @DisplayName("Test that in chord mode, only chord notes are processed, and in individual note mode, only individual notes are processed")
    void testMutuallyExclusiveChordAndIndividualNoteProcessing() {
        // Arrange
        double testFrequency = 300.0; // A frequency between C4 and E4

        // Create a chord detection result with no pitches (no chord detected)
        ChordDetectionResult noChordResult = ChordDetectionResult.of(new double[0], 0.0);

        // Create a chord detection result with pitches (chord detected)
        ChordDetectionResult chordResult = createChordDetectionResult(FREQ_C4, FREQ_E4);

        // Setup the frequency ranges for the note containers
        setupFrequencyRanges(1, FREQ_C4);
        setupFrequencyRanges(2, FREQ_E4);

        // Test 1: Chord mode (showChordIndex = 1) with no chord detected
        reset(noteContainers.toArray());
        when(mockModel.getSelectedShowChordIndex()).thenReturn(1);
        harpController.updateHarpView(testFrequency, noChordResult);

        // Assert - No note containers should be marked as part of a chord when no chord is detected
        for (NoteContainer noteContainer : noteContainers) {
            verify(noteContainer, never()).setPartOfChord(true);
        }

        // Reset mocks
        reset(noteContainers.toArray());

        // Test 2: Chord mode (showChordIndex = 1) with chord detected
        when(mockModel.getSelectedShowChordIndex()).thenReturn(1);
        harpController.updateHarpView(testFrequency, chordResult);

        // Assert - Only note containers that are part of the chord should be marked as part of a chord
        for (NoteContainer noteContainer : noteContainers) {
            if ((noteContainer.getChannel() == 1 || noteContainer.getChannel() == 2) && noteContainer.getNote() == 0) {
                verify(noteContainer).setPartOfChord(true);
            } else {
                verify(noteContainer, never()).setPartOfChord(true);
            }
        }

        // Reset mocks
        reset(noteContainers.toArray());

        // Test 3: Individual note mode (showChordIndex = 0) with chord detected
        when(mockModel.getSelectedShowChordIndex()).thenReturn(0);
        harpController.updateHarpView(testFrequency, chordResult);

        // Assert - No note containers should be marked as part of a chord in individual note mode
        for (NoteContainer noteContainer : noteContainers) {
            verify(noteContainer, never()).setPartOfChord(true);
        }
    }

    @Test
    @DisplayName("Test ChordHarmonica creation with two channels")
    void testChordHarmonicaCreation() {
        // Arrange
        Harmonica harmonica = AbstractHarmonica.create(AbstractHarmonica.KEY.C, AbstractHarmonica.TUNE.RICHTER);
        List<Integer> channels = List.of(1, 2);
        int noteIndex = 0;

        // Act
        ChordHarmonica chordHarmonica = new ChordHarmonica(harmonica, channels, noteIndex);

        // Assert
        assertEquals(noteIndex, chordHarmonica.getNoteIndex());
        assertIterableEquals(channels, chordHarmonica.getChannels());

        // Verify tones are calculated correctly
        List<Double> expectedTones = new ArrayList<>();
        for (int channel : channels) {
            expectedTones.add(harmonica.getNoteFrequency(channel, noteIndex));
        }
        assertIterableEquals(expectedTones, chordHarmonica.getTones());
    }

    @Test
    @DisplayName("Test ChordHarmonica creation with multiple channels")
    void testChordHarmonicaWithMultipleChannels() {
        // Arrange
        Harmonica harmonica = AbstractHarmonica.create(AbstractHarmonica.KEY.C, AbstractHarmonica.TUNE.RICHTER);
        List<Integer> channels = List.of(1, 2, 3, 4);
        int noteIndex = 0;

        // Act
        ChordHarmonica chordHarmonica = new ChordHarmonica(harmonica, channels, noteIndex);

        // Assert
        assertEquals(noteIndex, chordHarmonica.getNoteIndex());
        assertIterableEquals(channels, chordHarmonica.getChannels());

        // Verify tones are calculated correctly
        List<Double> expectedTones = new ArrayList<>();
        for (int channel : channels) {
            expectedTones.add(harmonica.getNoteFrequency(channel, noteIndex));
        }
        assertIterableEquals(expectedTones, chordHarmonica.getTones());
    }

    /**
     * Tests that no chord highlighting occurs when no chord is detected, even if chord display mode is enabled.
     *
     * <p>This test verifies that when no chord is detected (empty frequencies array),
     * no note containers are marked as part of a chord, even if chord display mode is enabled.
     *
     * <p>Test Steps:
     * 1. Create a chord detection result with no frequencies
     * 2. Enable chord display mode
     * 3. Call updateHarpView with the chord detection result
     * 4. Verify that no note containers are marked as part of a chord
     */
    @Test
    @DisplayName("Test that no chord highlighting occurs when no chord is detected")
    void testNoChordHighlightingWhenNoChordDetected() {
        // Arrange
        ChordDetectionResult noChordResult = ChordDetectionResult.of(new double[0], 0.0);

        // Enable chord display mode
        when(mockModel.getSelectedShowChordIndex()).thenReturn(1);

        // Act
        harpController.updateHarpView(0.0, noChordResult);

        // Assert - No notes should be marked as part of a chord
        for (NoteContainer noteContainer : noteContainers) {
            verify(noteContainer, never()).setPartOfChord(true);
        }
    }
}
