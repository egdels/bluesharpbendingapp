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

import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the result of a chord detection operation.
 * This class stores multiple detected pitch frequencies in Hz
 * and a confidence score indicating the reliability of the detection.
 * <p>
 * This is an extension of the PitchDetectionResult concept to handle
 * polyphonic sounds (chords) where multiple pitches are present simultaneously.
 */
public record ChordDetectionResult(List<Double> pitches, double confidence) {

    /**
     * Creates a new ChordDetectionResult with the given pitches and confidence.
     *
     * @param pitches    a list of detected pitch frequencies in Hz
     * @param confidence a value between 0.0 and 1.0 indicating the reliability of the detection
     */
    public ChordDetectionResult {
        // Defensive copy to ensure immutability
        pitches = List.copyOf(pitches);
    }

    /**
     * Creates a new ChordDetectionResult with the given pitches and confidence.
     *
     * @param pitches    an array of detected pitch frequencies in Hz
     * @param confidence a value between 0.0 and 1.0 indicating the reliability of the detection
     * @return a new ChordDetectionResult instance
     */
    public static ChordDetectionResult of(double[] pitches, double confidence) {
        return new ChordDetectionResult(
                Arrays.stream(pitches).boxed().collect(Collectors.toList()),
                confidence
        );
    }

    /**
     * Creates a new {@code ChordDetectionResult} instance from the provided list of pitches and confidence value.
     *
     * @param pitches    a list of detected pitch frequencies in Hz
     * @param confidence a value between 0.0 and 1.0 indicating the reliability of the detection
     * @return a new {@code ChordDetectionResult} instance
     */
    public static ChordDetectionResult of(List<Double> pitches, double confidence) {
        return new ChordDetectionResult(pitches, confidence);
    }

    /**
     * Creates a ChordDetectionResult from a single pitch detection result.
     *
     * @param result the PitchDetectionResult to convert
     * @return a new ChordDetectionResult containing the single pitch
     */
    public static ChordDetectionResult fromPitchDetectionResult(PitchDetector.PitchDetectionResult result) {
        if (result.pitch() == PitchDetector.NO_DETECTED_PITCH) {
            return new ChordDetectionResult(List.of(), 0.0);
        }
        return new ChordDetectionResult(List.of(result.pitch()), result.confidence());
    }

    /**
     * Checks if any pitches were detected.
     *
     * @return true if at least one pitch was detected, false otherwise
     */
    public boolean hasPitches() {
        return !pitches.isEmpty();
    }

    /**
     * Gets the number of detected pitches.
     *
     * @return the number of detected pitches
     */
    public int getPitchCount() {
        return pitches.size();
    }

    /**
     * Gets the pitch at the specified index.
     *
     * @param index the index of the pitch to get
     * @return the pitch at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public double getPitch(int index) {
        return pitches.get(index);
    }

    /**
     * Returns a string representation of this ChordHarmonica instance, including the list of note names
     * derived from the detected pitch frequencies.
     *
     * @return a string representation of the ChordHarmonica instance containing its notes
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ChordHarmonica{notes=[");

        for (int i = 0; i < pitches.size(); i++) {
            double frequency = pitches.get(i);
            String noteName = NoteLookup.getNoteName(frequency);

            sb.append(noteName);
            if (i < pitches.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append("]}");
        return sb.toString();
    }
}
