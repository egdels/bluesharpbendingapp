package de.schliweb.bluesharpbendingapp.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A simple test class to run the performance comparison between ChordDetector and MPMPitchDetector.
 * This class runs a single test that compares the performance of both detectors and prints the results.
 */
public class PitchDetectorPerformanceTest {

    private static final int SAMPLE_RATE = 44100;
    private static final int ITERATIONS = 100; // Number of iterations for each test to get reliable averages
    private static final double DURATION = 1.0; // Duration of audio samples in seconds

    /**
     * Runs a performance comparison between ChordDetector and MPMPitchDetector.
     * This test generates various audio inputs and measures the execution time of both detectors.
     */
    @Test
    public void testPerformanceComparison() {
        System.out.println("[DEBUG_LOG] Running PitchDetector Performance Comparison...");

        // Reset to default frequency range
        PitchDetector.setMaxFrequency(PitchDetector.getDefaultMaxFrequency());
        PitchDetector.setMinFrequency(PitchDetector.getDefaultMinFrequency());

        // Run single note performance tests
        System.out.println("[DEBUG_LOG] === SINGLE NOTE PERFORMANCE ===");
        compareSingleNotePerformance(261.63); // C4
        compareSingleNotePerformance(440.0);  // A4
        compareSingleNotePerformance(880.0);  // A5

        // Run chord performance tests
        System.out.println("[DEBUG_LOG] === CHORD PERFORMANCE ===");
        compareChordPerformance(2); // Two-note chord
        compareChordPerformance(3); // Three-note chord
        compareChordPerformance(4); // Four-note chord

        // Run overall performance test
        System.out.println("[DEBUG_LOG] === OVERALL PERFORMANCE ===");
        compareOverallPerformance();

        System.out.println("[DEBUG_LOG] Performance comparison complete.");
    }

    /**
     * Compares the performance of ChordDetector and MPMPitchDetector for a single note.
     *
     * @param frequency the frequency of the note in Hz
     */
    private void compareSingleNotePerformance(double frequency) {
        // Generate a sine wave
        double[] audioData = generateSineWave(frequency, SAMPLE_RATE, DURATION);

        // Measure ChordDetector performance
        long chordDetectorTime = measureChordDetectorPerformance(audioData);

        // Measure MPMPitchDetector performance
        long mpmDetectorTime = measureMPMDetectorPerformance(audioData);

        // Calculate ratio
        double ratio = (double) chordDetectorTime / mpmDetectorTime;

        // Print results
        System.out.println("[DEBUG_LOG] Single Note Performance - Frequency: " + frequency + " Hz");
        System.out.println("[DEBUG_LOG] ChordDetector: " + chordDetectorTime + " ms");
        System.out.println("[DEBUG_LOG] MPMPitchDetector: " + mpmDetectorTime + " ms");
        System.out.println("[DEBUG_LOG] Ratio (ChordDetector/MPMPitchDetector): " + ratio);
        System.out.println();
    }

    /**
     * Compares the performance of ChordDetector and MPMPitchDetector for a chord.
     *
     * @param noteCount the number of notes in the chord
     */
    private void compareChordPerformance(int noteCount) {
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
        System.out.println("[DEBUG_LOG] Chord Performance - " + noteCount + " notes");
        System.out.println("[DEBUG_LOG] ChordDetector: " + chordDetectorTime + " ms");
        System.out.println("[DEBUG_LOG] MPMPitchDetector: " + mpmDetectorTime + " ms");
        System.out.println("[DEBUG_LOG] Ratio (ChordDetector/MPMPitchDetector): " + ratio);
        System.out.println();
    }

    /**
     * Compares the overall performance of ChordDetector and MPMPitchDetector
     * across a range of test cases.
     */
    private void compareOverallPerformance() {
        // Test cases
        double[][] testCases = new double[][] {
            generateSineWave(261.63, SAMPLE_RATE, DURATION), // C4
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
        System.out.println("[DEBUG_LOG] Overall Performance Comparison");
        System.out.println("[DEBUG_LOG] ChordDetector: " + totalChordDetectorTime + " ms");
        System.out.println("[DEBUG_LOG] MPMPitchDetector: " + totalMPMDetectorTime + " ms");
        System.out.println("[DEBUG_LOG] Ratio (ChordDetector/MPMPitchDetector): " + ratio);
        System.out.println();

        // Use assertions to display results in test output
        String message = "Performance Comparison Results:\n" +
                         "ChordDetector: " + totalChordDetectorTime + " ms\n" +
                         "MPMPitchDetector: " + totalMPMDetectorTime + " ms\n" +
                         "Ratio (ChordDetector/MPMPitchDetector): " + ratio;

        // This assertion will always pass but will display the message in the test output
        assertTrue(true, message);
    }

    /**
     * Measures the execution time of ChordDetector for the given audio data.
     *
     * @param audioData the audio data to analyze
     * @return the execution time in milliseconds
     */
    private long measureChordDetectorPerformance(double[] audioData) {
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
    private long measureMPMDetectorPerformance(double[] audioData) {
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
     * @param frequency the frequency of the sine wave in Hertz
     * @param sampleRate the number of samples per second
     * @param duration the duration of the sine wave in seconds
     * @return an array of doubles representing the generated sine wave
     */
    private double[] generateSineWave(double frequency, int sampleRate, double duration) {
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
     * @param amplitudes an array of amplitudes for each frequency
     * @param sampleRate the number of samples per second
     * @param duration the duration of the chord in seconds
     * @return an array of doubles representing the generated chord
     */
    private double[] generateChord(double[] frequencies, double[] amplitudes, int sampleRate, double duration) {
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
