package de.schliweb.bluesharpbendingapp.utils;

import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Comparison test for the three pitch detection algorithms: ZCR, YIN, and MPM.
 * This test evaluates and compares the performance, accuracy, and confidence of each algorithm
 * across all combinations of harmonica keys and tunes.
 */
public class PitchDetectorComparisonTest {

    private static final int SAMPLE_RATE = 44100;
    private static final double DURATION = 1.0; // 1 second of audio
    private static final int WARMUP_RUNS = 3; // Number of warmup runs before measuring performance
    private static final int TEST_RUNS = 10; // Number of test runs for performance measurement

    // Data structures to store results
    private final Map<String, List<AlgorithmResult>> yinResults = new HashMap<>();
    private final Map<String, List<AlgorithmResult>> mpmResults = new HashMap<>();

    @BeforeEach
    void setUp() {
        // Reset frequency ranges to defaults for all detectors
        YINPitchDetector.setMinFrequency(YINPitchDetector.getDefaultMinFrequency());
        YINPitchDetector.setMaxFrequency(YINPitchDetector.getDefaultMaxFrequency());
        MPMPitchDetector.setMinFrequency(MPMPitchDetector.getDefaultMinFrequency());
        MPMPitchDetector.setMaxFrequency(MPMPitchDetector.getDefaultMaxFrequency());
    }

    @Test
    void compareAllPitchDetectors() {
        System.out.println("Comparing pitch detectors for all harmonica keys and tunes");
        System.out.println("==========================================================");

        // Clear previous results
        yinResults.clear();
        mpmResults.clear();

        // Get all keys and tunes
        String[] keys = AbstractHarmonica.getSupporterKeys();
        String[] tunes = AbstractHarmonica.getSupportedTunes();

        // For each combination of key and tune
        for (String key : keys) {
            for (String tune : tunes) {
                compareDetectorsForHarmonica(key, tune);
            }
        }

        // Print detailed results
        System.out.println("\nDetailed Results:");
        System.out.println("Format: Key, Tune, FreqType, Algorithm, Performance (ms), Accuracy (cents), Confidence");
        System.out.println("--------------------------------------------------------------------------------");
        printDetailedResults();

        // Print summary statistics
        System.out.println("\nSummary Statistics:");
        System.out.println("-------------------");
        printSummaryStatistics();

        // Save results to files
        try {
            saveDetailedResultsToFile("pitch-detector-comparison-results.csv");
            saveSummaryStatisticsToFile("pitch-detector-comparison-summary.csv");
            System.out.println("\nResults saved to files:");
            System.out.println("- pitch-detector-comparison-results.csv");
            System.out.println("- pitch-detector-comparison-summary.csv");
        } catch (IOException e) {
            System.err.println("Error saving results to file: " + e.getMessage());
        }
    }

    /**
     * Saves detailed results to a CSV file.
     *
     * @param filename the name of the file to save to
     * @throws IOException if an I/O error occurs
     */
    private void saveDetailedResultsToFile(String filename) throws IOException {
        File file = new File(filename);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write header
            writer.write("Key,Tune,FreqType,Algorithm,Performance (ms),Accuracy (cents),Confidence");
            writer.newLine();


            // Write YIN results
            for (Map.Entry<String, List<AlgorithmResult>> entry : yinResults.entrySet()) {
                for (AlgorithmResult result : entry.getValue()) {
                    writer.write(String.format("%s,YIN,%.2f,%.2f,%.2f", 
                            entry.getKey(), result.performance, result.accuracy, result.confidence));
                    writer.newLine();
                }
            }

            // Write MPM results
            for (Map.Entry<String, List<AlgorithmResult>> entry : mpmResults.entrySet()) {
                for (AlgorithmResult result : entry.getValue()) {
                    writer.write(String.format("%s,MPM,%.2f,%.2f,%.2f", 
                            entry.getKey(), result.performance, result.accuracy, result.confidence));
                    writer.newLine();
                }
            }
        }
    }

    /**
     * Saves summary statistics to a CSV file.
     *
     * @param filename the name of the file to save to
     * @throws IOException if an I/O error occurs
     */
    private void saveSummaryStatisticsToFile(String filename) throws IOException {
        File file = new File(filename);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write header
            writer.write("Algorithm,Avg Performance (ms),Avg Accuracy (cents),Avg Confidence");
            writer.newLine();


            double yinAvgPerformance = calculateAveragePerformance(yinResults);
            double yinAvgAccuracy = calculateAverageAccuracy(yinResults);
            double yinAvgConfidence = calculateAverageConfidence(yinResults);

            double mpmAvgPerformance = calculateAveragePerformance(mpmResults);
            double mpmAvgAccuracy = calculateAverageAccuracy(mpmResults);
            double mpmAvgConfidence = calculateAverageConfidence(mpmResults);

            // Write statistics
            writer.write(String.format("YIN;%.2f;%.2f;%.2f", yinAvgPerformance, yinAvgAccuracy, yinAvgConfidence));
            writer.newLine();
            writer.write(String.format("MPM;%.2f;%.2f;%.2f", mpmAvgPerformance, mpmAvgAccuracy, mpmAvgConfidence));
            writer.newLine();
        }
    }

    /**
     * Prints detailed results for all test cases.
     */
    private void printDetailedResults() {

        // Print YIN results
        for (Map.Entry<String, List<AlgorithmResult>> entry : yinResults.entrySet()) {
            for (AlgorithmResult result : entry.getValue()) {
                System.out.printf("%s; YIN; %.2f; %.2f; %.2f%n",
                        entry.getKey(), result.performance, result.accuracy, result.confidence);
            }
        }

        // Print MPM results
        for (Map.Entry<String, List<AlgorithmResult>> entry : mpmResults.entrySet()) {
            for (AlgorithmResult result : entry.getValue()) {
                System.out.printf("%s; MPM; %.2f; %.2f; %.2f%n",
                        entry.getKey(), result.performance, result.accuracy, result.confidence);
            }
        }
    }

    /**
     * Calculates and prints summary statistics for each algorithm.
     */
    private void printSummaryStatistics() {

        // Calculate YIN statistics
        double yinAvgPerformance = calculateAveragePerformance(yinResults);
        double yinAvgAccuracy = calculateAverageAccuracy(yinResults);
        double yinAvgConfidence = calculateAverageConfidence(yinResults);

        // Calculate MPM statistics
        double mpmAvgPerformance = calculateAveragePerformance(mpmResults);
        double mpmAvgAccuracy = calculateAverageAccuracy(mpmResults);
        double mpmAvgConfidence = calculateAverageConfidence(mpmResults);

        // Print statistics
        System.out.println("Algorithm, Avg Performance (ms), Avg Accuracy (cents), Avg Confidence");
        System.out.printf("YIN; %.2f; %.2f; %.2f%n", yinAvgPerformance, yinAvgAccuracy, yinAvgConfidence);
        System.out.printf("MPM; %.2f; %.2f; %.2f%n", mpmAvgPerformance, mpmAvgAccuracy, mpmAvgConfidence);
    }

    /**
     * Calculates the average performance for an algorithm.
     */
    private double calculateAveragePerformance(Map<String, List<AlgorithmResult>> results) {
        double sum = 0;
        int count = 0;
        for (List<AlgorithmResult> resultList : results.values()) {
            for (AlgorithmResult result : resultList) {
                sum += result.performance;
                count++;
            }
        }
        return count > 0 ? sum / count : 0;
    }

    /**
     * Calculates the average accuracy for an algorithm.
     */
    private double calculateAverageAccuracy(Map<String, List<AlgorithmResult>> results) {
        double sum = 0;
        int count = 0;
        for (List<AlgorithmResult> resultList : results.values()) {
            for (AlgorithmResult result : resultList) {
                sum += result.accuracy;
                count++;
            }
        }
        return count > 0 ? sum / count : 0;
    }

    /**
     * Calculates the average confidence for an algorithm.
     */
    private double calculateAverageConfidence(Map<String, List<AlgorithmResult>> results) {
        double sum = 0;
        int count = 0;
        for (List<AlgorithmResult> resultList : results.values()) {
            for (AlgorithmResult result : resultList) {
                sum += result.confidence;
                count++;
            }
        }
        return count > 0 ? sum / count : 0;
    }

    /**
     * Compares the three pitch detection algorithms for a specific harmonica key and tune.
     *
     * @param key  the harmonica key
     * @param tune the harmonica tune
     */
    private void compareDetectorsForHarmonica(String key, String tune) {
        // Create harmonica instance
        Harmonica harmonica = AbstractHarmonica.create(key, tune);

        // Get min and max frequencies for this harmonica
        double minFreq = harmonica.getHarmonicaMinFrequency();
        double maxFreq = harmonica.getHarmonicaMaxFrequency();

        // Set frequency ranges for all detectors
        YINPitchDetector.setMinFrequency(minFreq);
        YINPitchDetector.setMaxFrequency(maxFreq);
        MPMPitchDetector.setMinFrequency(minFreq);
        MPMPitchDetector.setMaxFrequency(maxFreq);

        // Test with min frequency
        compareDetectorsForFrequency(key, tune, minFreq, "Min");

        // Test with max frequency
        compareDetectorsForFrequency(key, tune, maxFreq, "Max");

        // Test with middle frequency
        double midFreq = (minFreq + maxFreq) / 2;
        compareDetectorsForFrequency(key, tune, midFreq, "Mid");
    }

    /**
     * Compares the three pitch detection algorithms for a specific frequency.
     *
     * @param key       the harmonica key
     * @param tune      the harmonica tune
     * @param frequency the test frequency
     * @param freqType  the type of frequency (Min, Max, Mid)
     */
    private void compareDetectorsForFrequency(String key, String tune, double frequency, String freqType) {
        // Generate test audio data
        double[] audioData = generateSineWave(frequency, SAMPLE_RATE, DURATION);

        // Test YIN
        AlgorithmResult yinResult = testAlgorithm("YIN", audioData, frequency);

        // Test MPM
        AlgorithmResult mpmResult = testAlgorithm("MPM", audioData, frequency);

        // Store results
        String resultKey = String.format("%s; %s; %s", key, tune, freqType);

        // Store ZCR result

        // Store YIN result
        if (!yinResults.containsKey(resultKey)) {
            yinResults.put(resultKey, new ArrayList<>());
        }
        yinResults.get(resultKey).add(yinResult);

        // Store MPM result
        if (!mpmResults.containsKey(resultKey)) {
            mpmResults.put(resultKey, new ArrayList<>());
        }
        mpmResults.get(resultKey).add(mpmResult);

        // Print progress indicator
        System.out.printf("Testing %s; %s; %s...%n", key, tune, freqType);
    }

    /**
     * Tests a specific pitch detection algorithm and measures its performance, accuracy, and confidence.
     *
     * @param algorithm  the name of the algorithm ("ZCR", "YIN", or "MPM")
     * @param audioData  the audio data to test with
     * @param frequency  the expected frequency
     * @return an AlgorithmResult containing performance, accuracy, and confidence metrics
     */
    private AlgorithmResult testAlgorithm(String algorithm, double[] audioData, double frequency) {
        // Warmup runs to stabilize JVM performance
        for (int i = 0; i < WARMUP_RUNS; i++) {
            runAlgorithm(algorithm, audioData);
        }

        // Measure performance
        long startTime = System.nanoTime();
        PitchDetector.PitchDetectionResult result = null;
        for (int i = 0; i < TEST_RUNS; i++) {
            result = runAlgorithm(algorithm, audioData);
        }
        long endTime = System.nanoTime();
        double performance = (endTime - startTime) / (TEST_RUNS * 1_000_000.0); // Convert to ms

        // Calculate accuracy in cents (0 cents = perfect match)
        double accuracy = Math.abs(NoteUtils.getCents(frequency, result.pitch()));

        // Get confidence
        double confidence = result.confidence();

        return new AlgorithmResult(performance, accuracy, confidence);
    }

    /**
     * Runs a specific pitch detection algorithm on the given audio data.
     *
     * @param algorithm the name of the algorithm ("ZCR", "YIN", or "MPM")
     * @param audioData the audio data to analyze
     * @return the pitch detection result
     */
    private PitchDetector.PitchDetectionResult runAlgorithm(String algorithm, double[] audioData) {
        switch (algorithm) {
            case "YIN":
                return YINPitchDetector.detectPitch(audioData, SAMPLE_RATE);
            case "MPM":
                return MPMPitchDetector.detectPitch(audioData, SAMPLE_RATE);
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }

    /**
     * Generates a sine wave based on the given frequency, sample rate, and duration.
     *
     * @param frequency  the frequency of the sine wave in Hertz
     * @param sampleRate the number of samples per second
     * @param duration   the duration of the sine wave in seconds
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
     * Class to hold the results of algorithm testing.
     */
    private static class AlgorithmResult {
        final double performance; // in milliseconds
        final double accuracy;    // in cents
        final double confidence;  // 0.0 to 1.0

        AlgorithmResult(double performance, double accuracy, double confidence) {
            this.performance = performance;
            this.accuracy = accuracy;
            this.confidence = confidence;
        }
    }
}
