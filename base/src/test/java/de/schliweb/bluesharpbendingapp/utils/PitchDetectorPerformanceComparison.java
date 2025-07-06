package de.schliweb.bluesharpbendingapp.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Performance comparison between ChordDetector and MPMPitchDetector.
 * This class measures and compares the execution time of both detectors
 * with various inputs, including single notes, chords, and different signal conditions.
 */
public class PitchDetectorPerformanceComparison {

    private static final int SAMPLE_RATE = 44100;
    private static final int ITERATIONS = 100; // Number of iterations for each test to get reliable averages
    private static final double DURATION = 1.0; // Duration of audio samples in seconds

    @BeforeEach
    void setUp() {
        // Reset to default frequency range before each test
        PitchDetector.setMaxFrequency(PitchDetector.getDefaultMaxFrequency());
        PitchDetector.setMinFrequency(PitchDetector.getDefaultMinFrequency());
    }

    /**
     * Compares the performance of ChordDetector and MPMPitchDetector for single notes
     * at different frequencies.
     */
    @ParameterizedTest
    @CsvSource({
            "261.63", // C4
            "440.0",  // A4
            "880.0",  // A5
            "1760.0", // A6
            "110.0"   // A2
    })
    void compareSingleNotePerformance(double frequency) {
        // Generate a sine wave
        double[] audioData = generateSineWave(frequency, SAMPLE_RATE, DURATION);

        // Measure ChordDetector performance
        long chordDetectorTime = measureChordDetectorPerformance(audioData);

        // Measure MPMPitchDetector performance
        long mpmDetectorTime = measureMPMDetectorPerformance(audioData);

        // Print results
        System.out.println("Single Note Performance Comparison - Frequency: " + frequency + " Hz");
        System.out.println("ChordDetector: " + chordDetectorTime + " ms");
        System.out.println("MPMPitchDetector: " + mpmDetectorTime + " ms");
        System.out.println("Ratio (ChordDetector/MPMPitchDetector): " + (double) chordDetectorTime / mpmDetectorTime);
        System.out.println();
    }

    /**
     * Compares the performance of ChordDetector and MPMPitchDetector for chords
     * with different numbers of notes.
     */
    @ParameterizedTest
    @CsvSource({
            "2", // Two-note chord
            "3", // Three-note chord
            "4"  // Four-note chord
    })
    void compareChordPerformance(int noteCount) {
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

        // Print results
        System.out.println("Chord Performance Comparison - " + noteCount + " notes");
        System.out.println("ChordDetector: " + chordDetectorTime + " ms");
        System.out.println("MPMPitchDetector: " + mpmDetectorTime + " ms");
        System.out.println("Ratio (ChordDetector/MPMPitchDetector): " + (double) chordDetectorTime / mpmDetectorTime);
        System.out.println();
    }

    /**
     * Compares the performance of ChordDetector and MPMPitchDetector for signals
     * with different noise levels.
     */
    @ParameterizedTest
    @CsvSource({
            "0.0",  // No noise
            "0.01", // Low noise
            "0.05", // Medium noise
            "0.1"   // High noise
    })
    void compareNoisePerformance(double noiseLevel) {
        // Generate a C4 sine wave
        double[] audioData = generateSineWave(261.63, SAMPLE_RATE, DURATION);

        // Add noise
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] += (Math.random() * 2 - 1) * noiseLevel;
        }

        // Measure ChordDetector performance
        long chordDetectorTime = measureChordDetectorPerformance(audioData);

        // Measure MPMPitchDetector performance
        long mpmDetectorTime = measureMPMDetectorPerformance(audioData);

        // Print results
        System.out.println("Noise Performance Comparison - Noise Level: " + noiseLevel);
        System.out.println("ChordDetector: " + chordDetectorTime + " ms");
        System.out.println("MPMPitchDetector: " + mpmDetectorTime + " ms");
        System.out.println("Ratio (ChordDetector/MPMPitchDetector): " + (double) chordDetectorTime / mpmDetectorTime);
        System.out.println();
    }

    /**
     * Compares the performance of ChordDetector and MPMPitchDetector for signals
     * with different sample sizes.
     */
    @ParameterizedTest
    @CsvSource({
            "0.1",  // 100 ms
            "0.5",  // 500 ms
            "1.0",  // 1 second
            "2.0"   // 2 seconds
    })
    void compareSampleSizePerformance(double duration) {
        // Generate a C4 sine wave with the specified duration
        double[] audioData = generateSineWave(261.63, SAMPLE_RATE, duration);

        // Measure ChordDetector performance
        long chordDetectorTime = measureChordDetectorPerformance(audioData);

        // Measure MPMPitchDetector performance
        long mpmDetectorTime = measureMPMDetectorPerformance(audioData);

        // Print results
        System.out.println("Sample Size Performance Comparison - Duration: " + duration + " seconds");
        System.out.println("ChordDetector: " + chordDetectorTime + " ms");
        System.out.println("MPMPitchDetector: " + mpmDetectorTime + " ms");
        System.out.println("Ratio (ChordDetector/MPMPitchDetector): " + (double) chordDetectorTime / mpmDetectorTime);
        System.out.println();
    }

    /**
     * Compares the performance of ChordDetector and MPMPitchDetector for signals
     * with harmonics.
     */
    @Test
    void compareHarmonicPerformance() {
        // Generate a signal with fundamental frequency and harmonics
        double fundamentalFreq = 261.63; // C4
        double[] audioData = generateSineWave(fundamentalFreq, SAMPLE_RATE, DURATION);
        double[] harmonic1 = generateSineWave(2 * fundamentalFreq, SAMPLE_RATE, DURATION); // First harmonic
        double[] harmonic2 = generateSineWave(3 * fundamentalFreq, SAMPLE_RATE, DURATION); // Second harmonic

        // Add harmonics with decreasing amplitudes
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] += 0.5 * harmonic1[i] + 0.25 * harmonic2[i];
        }

        // Measure ChordDetector performance
        long chordDetectorTime = measureChordDetectorPerformance(audioData);

        // Measure MPMPitchDetector performance
        long mpmDetectorTime = measureMPMDetectorPerformance(audioData);

        // Print results
        System.out.println("Harmonic Performance Comparison");
        System.out.println("ChordDetector: " + chordDetectorTime + " ms");
        System.out.println("MPMPitchDetector: " + mpmDetectorTime + " ms");
        System.out.println("Ratio (ChordDetector/MPMPitchDetector): " + (double) chordDetectorTime / mpmDetectorTime);
        System.out.println();
    }

    /**
     * Compares the overall performance of ChordDetector and MPMPitchDetector
     * across a range of test cases.
     */
    @Test
    void compareOverallPerformance() {
        List<double[]> testCases = new ArrayList<>();

        // Add single notes at different frequencies
        testCases.add(generateSineWave(261.63, SAMPLE_RATE, DURATION)); // C4
        testCases.add(generateSineWave(440.0, SAMPLE_RATE, DURATION));  // A4
        testCases.add(generateSineWave(880.0, SAMPLE_RATE, DURATION));  // A5

        // Add chords with different numbers of notes
        testCases.add(generateChord(new double[]{261.63, 329.63}, new double[]{1.0, 1.0}, SAMPLE_RATE, DURATION)); // C4, E4
        testCases.add(generateChord(new double[]{261.63, 329.63, 392.0}, new double[]{1.0, 1.0, 1.0}, SAMPLE_RATE, DURATION)); // C4, E4, G4
        testCases.add(generateChord(new double[]{261.63, 329.63, 392.0, 523.25}, new double[]{1.0, 1.0, 1.0, 1.0}, SAMPLE_RATE, DURATION)); // C4, E4, G4, C5

        // Add signals with noise
        double[] noiseSignal = generateSineWave(261.63, SAMPLE_RATE, DURATION);
        for (int i = 0; i < noiseSignal.length; i++) {
            noiseSignal[i] += (Math.random() * 2 - 1) * 0.05; // 5% noise
        }
        testCases.add(noiseSignal);

        // Add signal with harmonics
        double[] harmonicSignal = generateSineWave(261.63, SAMPLE_RATE, DURATION);
        double[] harmonic1 = generateSineWave(2 * 261.63, SAMPLE_RATE, DURATION);
        double[] harmonic2 = generateSineWave(3 * 261.63, SAMPLE_RATE, DURATION);
        for (int i = 0; i < harmonicSignal.length; i++) {
            harmonicSignal[i] += 0.5 * harmonic1[i] + 0.25 * harmonic2[i];
        }
        testCases.add(harmonicSignal);

        // Results for each test case
        StringBuilder results = new StringBuilder();
        results.append("[DEBUG_LOG] PERFORMANCE COMPARISON RESULTS:\n");

        // Individual test case results
        int testCaseNum = 1;
        for (double[] audioData : testCases) {
            long chordTime = measureChordDetectorPerformance(audioData);
            long mpmTime = measureMPMDetectorPerformance(audioData);
            double ratio = (double) chordTime / mpmTime;

            String testType;
            if (testCaseNum <= 3) {
                testType = "Single Note";
            } else if (testCaseNum <= 6) {
                testType = "Chord";
            } else if (testCaseNum == 7) {
                testType = "Noise";
            } else {
                testType = "Harmonic";
            }

            results.append(String.format("[DEBUG_LOG] Test Case %d (%s): ChordDetector=%d ms, MPMPitchDetector=%d ms, Ratio=%.2f\n", 
                testCaseNum, testType, chordTime, mpmTime, ratio));

            testCaseNum++;
        }

        // Measure total performance for each detector
        long totalChordDetectorTime = 0;
        long totalMPMDetectorTime = 0;

        for (double[] audioData : testCases) {
            totalChordDetectorTime += measureChordDetectorPerformance(audioData);
            totalMPMDetectorTime += measureMPMDetectorPerformance(audioData);
        }

        double overallRatio = (double) totalChordDetectorTime / totalMPMDetectorTime;

        // Add overall results
        results.append(String.format("[DEBUG_LOG] OVERALL: ChordDetector=%d ms, MPMPitchDetector=%d ms, Ratio=%.2f\n", 
            totalChordDetectorTime, totalMPMDetectorTime, overallRatio));

        // Print results to console (will be captured by test runner)
        System.out.println(results.toString());

        // Use assertions to display results in test output
        String message = "Performance Comparison Results:\n" +
                         "ChordDetector: " + totalChordDetectorTime + " ms\n" +
                         "MPMPitchDetector: " + totalMPMDetectorTime + " ms\n" +
                         "Ratio (ChordDetector/MPMPitchDetector): " + overallRatio;

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

    /**
     * Main method to run the performance comparison directly.
     * This allows seeing the results without relying on the test runner.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Running PitchDetector Performance Comparison...");

        PitchDetectorPerformanceComparison comparison = new PitchDetectorPerformanceComparison();
        comparison.setUp();

        // Run single note performance tests
        System.out.println("\n=== SINGLE NOTE PERFORMANCE ===");
        comparison.compareSingleNotePerformance(261.63); // C4
        comparison.compareSingleNotePerformance(440.0);  // A4
        comparison.compareSingleNotePerformance(880.0);  // A5

        // Run chord performance tests
        System.out.println("\n=== CHORD PERFORMANCE ===");
        comparison.compareChordPerformance(2); // Two-note chord
        comparison.compareChordPerformance(3); // Three-note chord
        comparison.compareChordPerformance(4); // Four-note chord

        // Run noise performance tests
        System.out.println("\n=== NOISE PERFORMANCE ===");
        comparison.compareNoisePerformance(0.0);  // No noise
        comparison.compareNoisePerformance(0.05); // Medium noise

        // Run sample size performance tests
        System.out.println("\n=== SAMPLE SIZE PERFORMANCE ===");
        comparison.compareSampleSizePerformance(0.5); // 500 ms
        comparison.compareSampleSizePerformance(1.0); // 1 second

        // Run harmonic performance test
        System.out.println("\n=== HARMONIC PERFORMANCE ===");
        comparison.compareHarmonicPerformance();

        // Run overall performance test
        System.out.println("\n=== OVERALL PERFORMANCE ===");
        comparison.compareOverallPerformance();

        System.out.println("\nPerformance comparison complete.");
    }
}
