package de.schliweb.bluesharpbendingapp.controller;

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
     */
    private ChordDetectionResult createChordDetectionResult(double... frequencies) {
        return ChordDetectionResult.of(frequencies, 1.0);
    }

    @Test
    @DisplayName("Test that 2-note chords (adjacent channels) are correctly highlighted")
    void testTwoNoteChordHighlighting() {
        // Arrange
        int channel1 = 1;
        int channel2 = 2;

        ChordDetectionResult chordResult = createChordDetectionResult(FREQ_C4, FREQ_E4);

        // Setup the frequency ranges for the note containers
        setupFrequencyRanges(channel1, FREQ_C4);
        setupFrequencyRanges(channel2, FREQ_E4);

        // Enable chord display mode
        when(mockModel.getSelectedShowChordIndex()).thenReturn(1);

        // Act
        harpController.updateHarpView(0.0, chordResult);

        // Assert
        for (NoteContainer noteContainer : noteContainers) {
            if ((noteContainer.getChannel() == channel1 || noteContainer.getChannel() == channel2) && 
                noteContainer.getNote() == 0) {
                verify(noteContainer).setPartOfChord(false); // First reset all flags
                verify(noteContainer).setPartOfChord(true);  // Then set for chord notes
            }
        }
    }

    @Test
    @DisplayName("Test that 3-note chords (adjacent channels) are correctly highlighted")
    void testThreeNoteChordHighlighting() {
        // Arrange
        int channel1 = 1;
        int channel2 = 2;
        int channel3 = 3;

        ChordDetectionResult chordResult = createChordDetectionResult(FREQ_C4, FREQ_E4, FREQ_G4);

        // Setup the frequency ranges for the note containers
        setupFrequencyRanges(channel1, FREQ_C4);
        setupFrequencyRanges(channel2, FREQ_E4);
        setupFrequencyRanges(channel3, FREQ_G4);

        // Enable chord display mode
        when(mockModel.getSelectedShowChordIndex()).thenReturn(1);

        // Act
        harpController.updateHarpView(0.0, chordResult);

        // Assert
        for (NoteContainer noteContainer : noteContainers) {
            if ((noteContainer.getChannel() == channel1 || 
                 noteContainer.getChannel() == channel2 || 
                 noteContainer.getChannel() == channel3) && 
                noteContainer.getNote() == 0) {
                verify(noteContainer).setPartOfChord(false); // First reset all flags
                verify(noteContainer).setPartOfChord(true);  // Then set for chord notes
            }
        }
    }

    @Test
    @DisplayName("Test that 4-note chords (adjacent channels) are correctly highlighted")
    void testFourNoteChordHighlighting() {
        // Arrange
        int channel1 = 1;
        int channel2 = 2;
        int channel3 = 3;
        int channel4 = 4;

        ChordDetectionResult chordResult = createChordDetectionResult(FREQ_C4, FREQ_E4, FREQ_G4, FREQ_C5);

        // Setup the frequency ranges for the note containers
        setupFrequencyRanges(channel1, FREQ_C4);
        setupFrequencyRanges(channel2, FREQ_E4);
        setupFrequencyRanges(channel3, FREQ_G4);
        setupFrequencyRanges(channel4, FREQ_C5);

        // Enable chord display mode
        when(mockModel.getSelectedShowChordIndex()).thenReturn(1);

        // Act
        harpController.updateHarpView(0.0, chordResult);

        // Assert
        for (NoteContainer noteContainer : noteContainers) {
            if ((noteContainer.getChannel() == channel1 || 
                 noteContainer.getChannel() == channel2 || 
                 noteContainer.getChannel() == channel3 || 
                 noteContainer.getChannel() == channel4) && 
                noteContainer.getNote() == 0) {
                verify(noteContainer).setPartOfChord(false); // First reset all flags
                verify(noteContainer).setPartOfChord(true);  // Then set for chord notes
            }
        }
    }

    @Test
    @DisplayName("Test that 2-note chords with middle channel covered are correctly highlighted")
    void testTwoNoteChordWithMiddleChannelCovered() {
        // Arrange
        int channel1 = 1;
        int channel2 = 2;
        int channel3 = 3;

        ChordDetectionResult chordResult = createChordDetectionResult(FREQ_C4, FREQ_G4);

        // Setup the frequency ranges for the note containers
        setupFrequencyRanges(channel1, FREQ_C4);
        setupFrequencyRanges(channel3, FREQ_G4);

        // Enable chord display mode
        when(mockModel.getSelectedShowChordIndex()).thenReturn(1);

        // Act
        harpController.updateHarpView(0.0, chordResult);

        // Assert
        for (NoteContainer noteContainer : noteContainers) {
            if ((noteContainer.getChannel() == channel1 || noteContainer.getChannel() == channel3) && 
                noteContainer.getNote() == 0) {
                verify(noteContainer).setPartOfChord(false); // First reset all flags
                verify(noteContainer).setPartOfChord(true);  // Then set for chord notes
            } else if (noteContainer.getChannel() == channel2 && noteContainer.getNote() == 0) {
                verify(noteContainer).setPartOfChord(false); // First reset all flags
                verify(noteContainer, never()).setPartOfChord(true); // Should not be part of chord
            }
        }
    }

    @Test
    @DisplayName("Test that 2-note chords with two middle channels covered are correctly highlighted")
    void testTwoNoteChordWithMiddleTwoChannelsCovered() {
        // Arrange
        int channel1 = 1;
        int channel2 = 2;
        int channel3 = 3;
        int channel4 = 4;

        ChordDetectionResult chordResult = createChordDetectionResult(FREQ_C4, FREQ_C5);

        // Setup the frequency ranges for the note containers
        setupFrequencyRanges(channel1, FREQ_C4);
        setupFrequencyRanges(channel4, FREQ_C5);

        // Enable chord display mode
        when(mockModel.getSelectedShowChordIndex()).thenReturn(1);

        // Act
        harpController.updateHarpView(0.0, chordResult);

        // Assert
        for (NoteContainer noteContainer : noteContainers) {
            if ((noteContainer.getChannel() == channel1 || noteContainer.getChannel() == channel4) && 
                noteContainer.getNote() == 0) {
                verify(noteContainer).setPartOfChord(false); // First reset all flags
                verify(noteContainer).setPartOfChord(true);  // Then set for chord notes
            } else if ((noteContainer.getChannel() == channel2 || noteContainer.getChannel() == channel3) && 
                       noteContainer.getNote() == 0) {
                verify(noteContainer).setPartOfChord(false); // First reset all flags
                verify(noteContainer, never()).setPartOfChord(true); // Should not be part of chord
            }
        }
    }

    @Test
    @DisplayName("Test that notes are properly reset after visualization")
    void testNotesResetAfterVisualization() {
        // Arrange
        ChordDetectionResult chordResult = createChordDetectionResult(FREQ_C4, FREQ_E4);

        // Setup the frequency ranges for the note containers
        setupFrequencyRanges(1, FREQ_C4);
        setupFrequencyRanges(2, FREQ_E4);

        // Act
        harpController.updateHarpView(0.0, chordResult);

        // Assert
        for (NoteContainer noteContainer : noteContainers) {
            if (noteContainer.getNote() == 0) {
                verify(noteContainer, atLeastOnce()).setPartOfChord(false);
                verify(mockExecutorService, atLeastOnce()).submit(noteContainer);
            }
        }
    }

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
            if ((noteContainer.getChannel() == 1 || noteContainer.getChannel() == 2) && 
                noteContainer.getNote() == 0) {
                verify(noteContainer, atLeastOnce()).setPartOfChord(true);
            }
        }
    }

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
        when(mockModel.getSelectedShowChordIndex()).thenReturn(1);
        harpController.updateHarpView(testFrequency, noChordResult);

        // Assert - No note containers should have frequency set in chord mode when no chord is detected
        for (NoteContainer noteContainer : noteContainers) {
            verify(noteContainer, never()).setFrequencyToHandle(testFrequency);
        }

        // Reset mocks
        reset(noteContainers.toArray());

        // Test 2: Chord mode (showChordIndex = 1) with chord detected
        when(mockModel.getSelectedShowChordIndex()).thenReturn(1);
        harpController.updateHarpView(testFrequency, chordResult);

        // Assert - Only note containers that are part of the chord should have frequency set
        for (NoteContainer noteContainer : noteContainers) {
            if ((noteContainer.getChannel() == 1 || noteContainer.getChannel() == 2) && 
                noteContainer.getNote() == 0) {
                verify(noteContainer).setFrequencyToHandle(testFrequency);
            } else {
                verify(noteContainer, never()).setFrequencyToHandle(testFrequency);
            }
        }

        // Reset mocks
        reset(noteContainers.toArray());

        // Test 3: Individual note mode (showChordIndex = 0) with chord detected
        when(mockModel.getSelectedShowChordIndex()).thenReturn(0);
        harpController.updateHarpView(testFrequency, chordResult);

        // Assert - All note containers should have frequency set in individual note mode, even if a chord is detected
        for (NoteContainer noteContainer : noteContainers) {
            verify(noteContainer).setFrequencyToHandle(testFrequency);
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
}
