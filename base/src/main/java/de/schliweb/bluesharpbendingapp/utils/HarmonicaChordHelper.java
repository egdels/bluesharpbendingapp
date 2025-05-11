package de.schliweb.bluesharpbendingapp.utils;
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

import de.schliweb.bluesharpbendingapp.model.harmonica.ChordHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for working with harmonica chord data.
 * Provides methods for processing and retrieving possible chords information
 * for a given harmonica.
 */
public class HarmonicaChordHelper {

    /**
     * Private constructor to prevent instantiation of the utility class.
     * <p>
     * The HarmonicaChordHelper class is designed as a utility class that provides methods
     * for processing and retrieving possible chords for a given harmonica. Since it only contains
     * static methods, instantiation of the class is not allowed.
     */
    private HarmonicaChordHelper() {
    }

    /**
     * Retrieves all possible chords that can be produced with the given harmonica, mapped by their name.
     * Each chord is represented by its name (e.g., "C-E-G") and its corresponding list of frequencies.
     *
     * @param harmonica the harmonica for which possible chords are to be retrieved
     * @return a map where the key is the name of the chord (e.g., "C-E-G") and the value is a list of lists
     * of frequencies representing the chord
     */
    public static Map<String, List<Double>> getPossibleChords(Harmonica harmonica) {
        List<ChordHarmonica> chords = harmonica.getPossibleChords();
        Map<String, List<Double>> chordMap = new HashMap<>();
        for (ChordHarmonica chord : chords) {
            List<Double> frequencies = chord.getTones();
            // Generate the chord name by determining note names for each frequency
            String chordName = frequencies.stream()
                    .map(HarmonicaChordHelper::frequencyToNote) // Convert each frequency to its note name
                    .collect(Collectors.joining("-"));

            // Correctly add individual frequencies to the map
            chordMap.computeIfAbsent(chordName, k -> new ArrayList<>()).addAll(frequencies);
        }
        return chordMap;
    }


    /**
     * Converts a given frequency to its corresponding musical note name.
     *
     * @param frequency the frequency in Hertz to be converted to a musical note name
     * @return the name of the musical note corresponding to the given frequency
     */
    private static String frequencyToNote(Double frequency) {
        return NoteLookup.getNoteName(frequency);
    }
}
