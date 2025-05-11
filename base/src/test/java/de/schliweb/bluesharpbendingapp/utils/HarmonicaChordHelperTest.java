package de.schliweb.bluesharpbendingapp.utils;

import de.schliweb.bluesharpbendingapp.model.harmonica.ChordHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HarmonicaChordHelperTest {

    /**
     * Tests for the {@link HarmonicaChordHelper#getPossibleChords(Harmonica)} method.
     * This method analyzes the list of chords from the given harmonica, converts them into
     * a map where the keys are chord names (based on frequencies converted to notes), and
     * the values are lists of frequencies associated with those chord names.
     */
    @Test
    void testGetPossibleChords_withSingleChord() {
        Harmonica harmonicaMock = mock(Harmonica.class);
        ChordHarmonica chordMock = mock(ChordHarmonica.class);

        List<Double> frequencies = Arrays.asList(440.0, 659.25, 987.77);
        when(chordMock.getTones()).thenReturn(frequencies);

        List<ChordHarmonica> chordList = List.of(chordMock);
        when(harmonicaMock.getPossibleChords()).thenReturn(chordList);

        // Mock NoteLookup static class
        try (MockedStatic<NoteLookup> noteLookupMock = Mockito.mockStatic(NoteLookup.class)) {
            noteLookupMock.when(() -> NoteLookup.getNoteName(440.0)).thenReturn("A");
            noteLookupMock.when(() -> NoteLookup.getNoteName(659.25)).thenReturn("E");
            noteLookupMock.when(() -> NoteLookup.getNoteName(987.77)).thenReturn("B");

            Map<String, List<Double>> expected = new HashMap<>();
            expected.put("A-E-B", frequencies);

            Map<String, List<Double>> result = HarmonicaChordHelper.getPossibleChords(harmonicaMock);

            assertEquals(expected, result);
        }
    }

    @Test
    void testGetPossibleChords_withMultipleChordsSameName() {
        Harmonica harmonicaMock = mock(Harmonica.class);
        ChordHarmonica chordMock1 = mock(ChordHarmonica.class);
        ChordHarmonica chordMock2 = mock(ChordHarmonica.class);

        List<Double> frequencies1 = Arrays.asList(440.0, 659.25, 987.77);
        List<Double> frequencies2 = Arrays.asList(661.0, 987.0, 440.0);
        when(chordMock1.getTones()).thenReturn(frequencies1);
        when(chordMock2.getTones()).thenReturn(frequencies2);

        List<ChordHarmonica> chordList = Arrays.asList(chordMock1, chordMock2);
        when(harmonicaMock.getPossibleChords()).thenReturn(chordList);

        // Mock NoteLookup static class
        try (MockedStatic<NoteLookup> noteLookupMock = Mockito.mockStatic(NoteLookup.class)) {
            noteLookupMock.when(() -> NoteLookup.getNoteName(440.0)).thenReturn("A");
            noteLookupMock.when(() -> NoteLookup.getNoteName(659.25)).thenReturn("E");
            noteLookupMock.when(() -> NoteLookup.getNoteName(987.77)).thenReturn("B");
            noteLookupMock.when(() -> NoteLookup.getNoteName(661.0)).thenReturn("E");
            noteLookupMock.when(() -> NoteLookup.getNoteName(987.0)).thenReturn("B");

            Map<String, List<Double>> expected = new HashMap<>();
            expected.put("A-E-B", frequencies1);
            expected.put("E-B-A", frequencies2);

            Map<String, List<Double>> result = HarmonicaChordHelper.getPossibleChords(harmonicaMock);

            assertEquals(expected, result);
        }
    }

    @Test
    void testGetPossibleChords_withNoChords() {
        Harmonica harmonicaMock = mock(Harmonica.class);

        when(harmonicaMock.getPossibleChords()).thenReturn(new ArrayList<>());

        Map<String, List<Double>> expected = new HashMap<>();
        Map<String, List<Double>> result = HarmonicaChordHelper.getPossibleChords(harmonicaMock);

        assertEquals(expected, result);
    }
}
