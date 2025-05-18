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

/**
 * A standalone class to analyze and compare the performance of ChordDetector and MPMPitchDetector.
 * This class provides a direct comparison of execution time for both detectors with various inputs.
 */
public class PitchDetectorPerformanceAnalysis {

    private static final int SAMPLE_RATE = 44100;
    private static final int ITERATIONS = 100; // Number of iterations for each test to get reliable averages
    private static final double DURATION = 1.0; // Duration of audio samples in seconds

    /**
     * Main method to run the performance analysis.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Running PitchDetector Performance Analysis...");

        // Reset to default frequency range
        PitchDetector.setMaxFrequency(PitchDetector.getDefaultMaxFrequency());
        PitchDetector.setMinFrequency(PitchDetector.getDefaultMinFrequency());

        // Run single note performance tests
        System.out.println("\n=== SINGLE NOTE PERFORMANCE ===");
        compareSingleNotePerformance(261.63); // C4
        compareSingleNotePerformance(440.0);  // A4
        compareSingleNotePerformance(880.0);  // A5

        // Run chord performance tests
        System.out.println("\n=== CHORD PERFORMANCE ===");
        compareChordPerformance(2); // Two-note chord
        compareChordPerformance(3); // Three-note chord
        compareChordPerformance(4); // Four-note chord

        // Run overall performance test
        System.out.println("\n=== OVERALL PERFORMANCE ===");
        compareOverallPerformance();

        System.out.println("\nPerformance analysis complete.");
    }

    /**
     * Compares the performance of ChordDetector and MPMPitchDetector for a single note.
     *
     * @param frequency the frequency of the note in Hz
     */
    private static void compareSingleNotePerformance(double frequency) {
        // Generate a sine wave
        double[] audioData = generateSineWave(frequency, SAMPLE_RATE, DURATION);

        // Measure ChordDetector performance
        long chordDetectorTime = measureChordDetectorPerformance(audioData);

        // Measure MPMPitchDetector performance
        long mpmDetectorTime = measureMPMDetectorPerformance(audioData);

        // Calculate ratio
        double ratio = (double) chordDetectorTime / mpmDetectorTime;

        // Print results
        System.out.println("Single Note Performance - Frequency: " + frequency + " Hz");
        System.out.println("ChordDetector: " + chordDetectorTime + " ms");
        System.out.println("MPMPitchDetector: " + mpmDetectorTime + " ms");
        System.out.println("Ratio (ChordDetector/MPMPitchDetector): " + ratio);
        System.out.println();
    }

    /**
     * Compares the performance of ChordDetector and MPMPitchDetector for a chord.
     *
     * @param noteCount the number of notes in the chord
     */
    private static void compareChordPerformance(int noteCount) {
        // Define frequencies for a C major chord
        double[] allFrequencies = {261.63, 329.63, 392.0, 523.25}; // C4, E4, G4, C5
        double[] frequencies = new double[noteCount];
        double[] amplitudes = new double[noteCount];

        // Use the first noteCount frequencies
        for (int i = 0; i < noteCount; i++) {
            frequencies[i] = allFrequencies[i];
            amplitudes[i] = 1.0;
        }

        // Generate the chord
        double[] audioData = generateChord(frequencies, amplitudes, SAMPLE_RATE, DURATION);

        // Measure ChordDetector performance
        long chordDetectorTime = measureChordDetectorPerformance(audioData);

        // Measure MPMPitchDetector performance
        long mpmDetectorTime = measureMPMDetectorPerformance(audioData);

        // Calculate ratio
        double ratio = (double) chordDetectorTime / mpmDetectorTime;

        // Print results
        System.out.println("Chord Performance - " + noteCount + " notes");
        System.out.println("ChordDetector: " + chordDetectorTime + " ms");
        System.out.println("MPMPitchDetector: " + mpmDetectorTime + " ms");
        System.out.println("Ratio (ChordDetector/MPMPitchDetector): " + ratio);
        System.out.println();
    }

    /**
     * Compares the overall performance of ChordDetector and MPMPitchDetector
     * across a range of test cases.
     */
    private static void compareOverallPerformance() {
        // Test cases
        double[][] testCases = new double[][]{generateSineWave(261.63, SAMPLE_RATE, DURATION), // C4
                generateSineWave(440.0, SAMPLE_RATE, DURATION),  // A4
                generateSineWave(880.0, SAMPLE_RATE, DURATION),  // A5
                generateChord(new double[]{261.63, 329.63}, new double[]{1.0, 1.0}, SAMPLE_RATE, DURATION), // C4, E4
                generateChord(new double[]{261.63, 329.63, 392.0}, new double[]{1.0, 1.0, 1.0}, SAMPLE_RATE, DURATION), // C4, E4, G4
                generateChord(new double[]{261.63, 329.63, 392.0, 523.25}, new double[]{1.0, 1.0, 1.0, 1.0}, SAMPLE_RATE, DURATION) // C4, E4, G4, C5
        };

        // Measure total performance for each detector
        long totalChordDetectorTime = 0;
        long totalMPMDetectorTime = 0;

        for (double[] audioData : testCases) {
            totalChordDetectorTime += measureChordDetectorPerformance(audioData);
            totalMPMDetectorTime += measureMPMDetectorPerformance(audioData);
        }

        // Calculate ratio
        double ratio = (double) totalChordDetectorTime / totalMPMDetectorTime;

        // Print results
        System.out.println("Overall Performance Comparison");
        System.out.println("ChordDetector: " + totalChordDetectorTime + " ms");
        System.out.println("MPMPitchDetector: " + totalMPMDetectorTime + " ms");
        System.out.println("Ratio (ChordDetector/MPMPitchDetector): " + ratio);
        System.out.println();
    }

    /**
     * Measures the execution time of ChordDetector for the given audio data.
     *
     * @param audioData the audio data to analyze
     * @return the execution time in milliseconds
     */
    private static long measureChordDetectorPerformance(double[] audioData) {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < ITERATIONS; i++) {
            PitchDetector.detectChord(audioData, SAMPLE_RATE);
        }

        long endTime = System.currentTimeMillis();
        return (endTime - startTime);
    }

    /**
     * Measures the execution time of MPMPitchDetector for the given audio data.
     *
     * @param audioData the audio data to analyze
     * @return the execution time in milliseconds
     */
    private static long measureMPMDetectorPerformance(double[] audioData) {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < ITERATIONS; i++) {
            PitchDetector.detectPitchMPM(audioData, SAMPLE_RATE);
        }

        long endTime = System.currentTimeMillis();
        return (endTime - startTime);
    }

    /**
     * Generates a sine wave based on the given frequency, sample rate, and duration.
     *
     * @param frequency  the frequency of the sine wave in Hertz
     * @param sampleRate the number of samples per second
     * @param duration   the duration of the sine wave in seconds
     * @return an array of doubles representing the generated sine wave
     */
    private static double[] generateSineWave(double frequency, int sampleRate, double duration) {
        int samples = (int) (sampleRate * duration);
        double[] sineWave = new double[samples];
        for (int i = 0; i < samples; i++) {
            sineWave[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }
        return sineWave;
    }

    /**
     * Generates a chord (multiple sine waves) based on the given frequencies, amplitudes, sample rate, and duration.
     *
     * @param frequencies an array of frequencies in Hertz
     * @param amplitudes  an array of amplitudes for each frequency
     * @param sampleRate  the number of samples per second
     * @param duration    the duration of the chord in seconds
     * @return an array of doubles representing the generated chord
     */
    private static double[] generateChord(double[] frequencies, double[] amplitudes, int sampleRate, double duration) {
        int samples = (int) (sampleRate * duration);
        double[] chord = new double[samples];

        for (int i = 0; i < samples; i++) {
            double sample = 0.0;
            for (int j = 0; j < frequencies.length; j++) {
                sample += amplitudes[j] * Math.sin(2 * Math.PI * frequencies[j] * i / sampleRate);
            }
            // Normalize to avoid clipping
            chord[i] = sample / frequencies.length;
        }

        return chord;
    }
}