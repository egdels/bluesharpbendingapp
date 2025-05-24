package de.schliweb.bluesharpbendingapp.utils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for optimizing the THRESHOLD_HIGH_FREQUENCY_ENERGY and FREQUENCY_RANGE_HIGH values in HybridPitchDetector.
 * This class evaluates different values to find the optimal combination that provides
 * the best performance (execution time) while maintaining pitch detection accuracy.
 */
class HighFrequencyOptimizationTest {

    private static final int SAMPLE_RATE = 44100;
    private static final double TOLERANCE = 1.0; // 1Hz tolerance for pitch detection
    private static final double ORIGINAL_THRESHOLD = HybridPitchDetector.getThresholdHighFrequencyEnergy();
    private static final int ORIGINAL_FREQUENCY_RANGE = HybridPitchDetector.getFrequencyRangeHigh();

    // Test frequencies covering low, medium, and high ranges
    private static final double[] TEST_FREQUENCIES = {
        100.0, 150.0, 200.0, 250.0, 290.0,  // Low frequencies
        300.0, 310.0, 350.0, 400.0, 500.0, 700.0, 900.0, 990.0,  // Medium frequencies
        1000.0, 1100.0, 1500.0, 2000.0, 3000.0  // High frequencies
    };

    // Threshold values to test
    private static final double[] TEST_THRESHOLDS = {
        100.0, 250.0, 400.0, 500.0, 600.0, 750.0, 1000.0
    };

    // Frequency range values to test
    private static final int[] TEST_FREQUENCY_RANGES = {
        800, 900, 1000, 1100, 1200
    };

    @BeforeAll
    static void setUp() {
        // Store the original values
        System.out.println("[DEBUG_LOG] Original high frequency threshold: " + ORIGINAL_THRESHOLD);
        System.out.println("[DEBUG_LOG] Original high frequency range: " + ORIGINAL_FREQUENCY_RANGE);
    }

    @AfterAll
    static void tearDown() {
        // Restore the original values
        HybridPitchDetector.setThresholdHighFrequencyEnergy(ORIGINAL_THRESHOLD);
        HybridPitchDetector.setFrequencyRangeHigh(ORIGINAL_FREQUENCY_RANGE);
        System.out.println("[DEBUG_LOG] Restored original high frequency threshold: " + HybridPitchDetector.getThresholdHighFrequencyEnergy());
        System.out.println("[DEBUG_LOG] Restored original high frequency range: " + HybridPitchDetector.getFrequencyRangeHigh());
    }

    /**
     * Tests a specific combination of threshold and frequency range values with pure sine waves.
     * This test evaluates the accuracy and execution time of pitch detection for the given combination.
     *
     * @param threshold the threshold value to test
     * @param frequencyRange the frequency range value to test
     * @return a map entry containing the average error, number of successful detections, and average execution time
     */
    private Map.Entry<Double, Map.Entry<Integer, Long>> testCombinationWithPureSineWaves(double threshold, int frequencyRange) {
        HybridPitchDetector.setThresholdHighFrequencyEnergy(threshold);
        HybridPitchDetector.setFrequencyRangeHigh(frequencyRange);

        double totalError = 0.0;
        int successfulDetections = 0;
        long totalExecutionTime = 0;

        for (double frequency : TEST_FREQUENCIES) {
            double[] sineWave = generateSineWave(frequency, SAMPLE_RATE, 1.0);

            // Measure execution time
            long startTime = System.nanoTime();
            PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchHybrid(sineWave, SAMPLE_RATE);
            long endTime = System.nanoTime();
            long executionTime = endTime - startTime;
            totalExecutionTime += executionTime;

            if (result.pitch() != PitchDetector.NO_DETECTED_PITCH) {
                double error = Math.abs(result.pitch() - frequency);
                totalError += error;
                successfulDetections++;
            }
        }

        double averageError = successfulDetections > 0 ? totalError / successfulDetections : Double.MAX_VALUE;
        long averageExecutionTime = TEST_FREQUENCIES.length > 0 ? totalExecutionTime / TEST_FREQUENCIES.length : 0;

        return Map.entry(averageError, Map.entry(successfulDetections, averageExecutionTime));
    }

    /**
     * Tests different combinations of threshold and frequency range values with pure sine waves.
     * This test evaluates the accuracy and execution time of pitch detection for each combination.
     */
    @Test
    @DisplayName("Test different combinations of threshold and frequency range values")
    void testCombinationsOfThresholdAndFrequencyRange() {
        Map<String, Double> combinationToAverageError = new HashMap<>();
        Map<String, Long> combinationToAverageExecutionTime = new HashMap<>();
        Map<String, Integer> combinationToSuccessfulDetections = new HashMap<>();

        for (double threshold : TEST_THRESHOLDS) {
            for (int frequencyRange : TEST_FREQUENCY_RANGES) {
                Map.Entry<Double, Map.Entry<Integer, Long>> result = testCombinationWithPureSineWaves(threshold, frequencyRange);
                double averageError = result.getKey();
                int successfulDetections = result.getValue().getKey();
                long averageExecutionTime = result.getValue().getValue();

                String combination = "T" + threshold + "_F" + frequencyRange;
                combinationToAverageError.put(combination, averageError);
                combinationToAverageExecutionTime.put(combination, averageExecutionTime);
                combinationToSuccessfulDetections.put(combination, successfulDetections);

                System.out.println("[DEBUG_LOG] Combination: " + combination + 
                                   ", Average Error: " + averageError + 
                                   ", Successful Detections: " + successfulDetections + "/" + TEST_FREQUENCIES.length +
                                   ", Average Execution Time: " + averageExecutionTime + " ns");
            }
        }

        // Find the combination with the lowest execution time
        String bestCombination = findBestCombinationByExecutionTime(combinationToAverageExecutionTime, combinationToAverageError);
        System.out.println("[DEBUG_LOG] Best combination by execution time: " + bestCombination + 
                           ", Average Execution Time: " + combinationToAverageExecutionTime.get(bestCombination) + " ns" +
                           ", Average Error: " + combinationToAverageError.get(bestCombination));

        // Assert that we found a combination with acceptable error
        assertTrue(combinationToAverageError.get(bestCombination) < TOLERANCE, 
                   "Best combination should have average error less than the tolerance");
    }

    /**
     * Tests a specific combination of threshold and frequency range values.
     * This test evaluates the accuracy and execution time of pitch detection for the given combination.
     */
    @Test
    @DisplayName("Test specific combination of threshold and frequency range")
    void testSpecificCombination() {
        double threshold = 400.0;
        int frequencyRange = 900;

        HybridPitchDetector.setThresholdHighFrequencyEnergy(threshold);
        HybridPitchDetector.setFrequencyRangeHigh(frequencyRange);

        double totalError = 0.0;
        int successfulDetections = 0;
        long totalExecutionTime = 0;
        double maxCentError = 0.0;
        double totalCentError = 0.0;

        StringBuilder results = new StringBuilder();
        results.append("Threshold: ").append(threshold).append(", Frequency Range: ").append(frequencyRange).append("\n");

        for (double frequency : TEST_FREQUENCIES) {
            double[] sineWave = generateSineWave(frequency, SAMPLE_RATE, 1.0);

            // Measure execution time
            long startTime = System.nanoTime();
            PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchHybrid(sineWave, SAMPLE_RATE);
            long endTime = System.nanoTime();
            long executionTime = endTime - startTime;
            totalExecutionTime += executionTime;

            if (result.pitch() != PitchDetector.NO_DETECTED_PITCH) {
                double error = Math.abs(result.pitch() - frequency);
                double centError = Math.abs(frequencyToCents(result.pitch()) - frequencyToCents(frequency));

                totalError += error;
                totalCentError += centError;
                maxCentError = Math.max(maxCentError, centError);
                successfulDetections++;

                results.append("  Frequency: ").append(frequency)
                       .append(", Detected: ").append(result.pitch())
                       .append(", Error: ").append(error)
                       .append(", Cent Error: ").append(centError)
                       .append(", Execution Time: ").append(executionTime).append(" ns\n");
            } else {
                results.append("  Frequency: ").append(frequency)
                       .append(", No pitch detected")
                       .append(", Execution Time: ").append(executionTime).append(" ns\n");
            }
        }

        double averageError = successfulDetections > 0 ? totalError / successfulDetections : Double.MAX_VALUE;
        double averageCentError = successfulDetections > 0 ? totalCentError / successfulDetections : Double.MAX_VALUE;
        long averageExecutionTime = TEST_FREQUENCIES.length > 0 ? totalExecutionTime / TEST_FREQUENCIES.length : 0;

        results.append("Summary:\n");
        results.append("  Average Error: ").append(averageError).append("\n");
        results.append("  Average Cent Error: ").append(averageCentError).append("\n");
        results.append("  Max Cent Error: ").append(maxCentError).append("\n");
        results.append("  Successful Detections: ").append(successfulDetections).append("/").append(TEST_FREQUENCIES.length).append("\n");
        results.append("  Average Execution Time: ").append(averageExecutionTime).append(" ns\n");

        // Throw an exception with the results
        throw new RuntimeException("TEST RESULTS:\n" + results.toString());
    }

    /**
     * Tests the accuracy of pitch detection in terms of cents.
     * This test verifies that the pitch detection accuracy is within 1 cent for different combinations.
     */
    @Test
    @DisplayName("Test pitch detection accuracy in cents")
    void testPitchDetectionAccuracyInCents() {
        for (double threshold : TEST_THRESHOLDS) {
            for (int frequencyRange : TEST_FREQUENCY_RANGES) {
                HybridPitchDetector.setThresholdHighFrequencyEnergy(threshold);
                HybridPitchDetector.setFrequencyRangeHigh(frequencyRange);

                double maxCentError = 0.0;
                double totalCentError = 0.0;
                int successfulDetections = 0;

                for (double frequency : TEST_FREQUENCIES) {
                    double[] sineWave = generateSineWave(frequency, SAMPLE_RATE, 1.0);
                    PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchHybrid(sineWave, SAMPLE_RATE);

                    if (result.pitch() != PitchDetector.NO_DETECTED_PITCH) {
                        double centError = Math.abs(frequencyToCents(result.pitch()) - frequencyToCents(frequency));
                        totalCentError += centError;
                        maxCentError = Math.max(maxCentError, centError);
                        successfulDetections++;

                        System.out.println("[DEBUG_LOG] Threshold: " + threshold + 
                                           ", Frequency Range: " + frequencyRange + 
                                           ", Frequency: " + frequency + 
                                           ", Detected: " + result.pitch() + 
                                           ", Cent Error: " + centError);
                    }
                }

                double averageCentError = successfulDetections > 0 ? totalCentError / successfulDetections : Double.MAX_VALUE;

                System.out.println("[DEBUG_LOG] Threshold: " + threshold + 
                                   ", Frequency Range: " + frequencyRange + 
                                   ", Average Cent Error: " + averageCentError + 
                                   ", Max Cent Error: " + maxCentError + 
                                   ", Successful Detections: " + successfulDetections + "/" + TEST_FREQUENCIES.length);

                // Assert that the maximum cent error is within the acceptable range
                assertTrue(maxCentError < 1.0, "Maximum cent error should be less than 1 cent for threshold " + threshold + " and frequency range " + frequencyRange);
            }
        }
    }

    /**
     * Finds the combination with the lowest execution time among those with acceptable error.
     *
     * @param combinationToAverageExecutionTime a map of combinations to their average execution times
     * @param combinationToAverageError a map of combinations to their average errors
     * @return the combination with the lowest execution time among those with acceptable error
     */
    private String findBestCombinationByExecutionTime(Map<String, Long> combinationToAverageExecutionTime, Map<String, Double> combinationToAverageError) {
        String bestCombination = "";
        long lowestExecutionTime = Long.MAX_VALUE;

        for (Map.Entry<String, Long> entry : combinationToAverageExecutionTime.entrySet()) {
            String combination = entry.getKey();
            long executionTime = entry.getValue();
            double error = combinationToAverageError.get(combination);

            // Only consider combinations with acceptable error
            if (error < TOLERANCE && executionTime < lowestExecutionTime) {
                lowestExecutionTime = executionTime;
                bestCombination = combination;
            }
        }

        return bestCombination;
    }

    /**
     * Converts a frequency to cents relative to C0 (16.35 Hz).
     *
     * @param frequency the frequency in Hz
     * @return the frequency in cents relative to C0
     */
    private double frequencyToCents(double frequency) {
        return 1200 * Math.log(frequency / 16.35) / Math.log(2);
    }

    /**
     * Generates a sine wave with the specified frequency, sample rate, and duration.
     *
     * @param frequency the frequency of the sine wave in Hz
     * @param sampleRate the sample rate in Hz
     * @param duration the duration of the sine wave in seconds
     * @return an array of doubles representing the sine wave
     */
    private double[] generateSineWave(double frequency, int sampleRate, double duration) {
        int numSamples = (int) (duration * sampleRate);
        double[] sineWave = new double[numSamples];

        for (int i = 0; i < numSamples; i++) {
            double t = (double) i / sampleRate;
            sineWave[i] = Math.sin(2 * Math.PI * frequency * t);
        }

        return sineWave;
    }

    /**
     * Generates a complex signal with the specified fundamental frequency, sample rate, and duration.
     * The complex signal includes harmonics and noise.
     *
     * @param frequency the fundamental frequency of the signal in Hz
     * @param sampleRate the sample rate in Hz
     * @param duration the duration of the signal in seconds
     * @return an array of doubles representing the complex signal
     */
    private double[] generateComplexSignal(double frequency, int sampleRate, double duration) {
        int numSamples = (int) (duration * sampleRate);
        double[] complexSignal = new double[numSamples];

        for (int i = 0; i < numSamples; i++) {
            double t = (double) i / sampleRate;
            // Add fundamental frequency and harmonics
            complexSignal[i] = Math.sin(2 * Math.PI * frequency * t) + 
                              0.5 * Math.sin(2 * Math.PI * frequency * 2 * t) + 
                              0.3 * Math.sin(2 * Math.PI * frequency * 3 * t) + 
                              0.1 * Math.sin(2 * Math.PI * frequency * 4 * t);
            // Add some noise
            complexSignal[i] += 0.1 * (Math.random() * 2 - 1);
        }

        return complexSignal;
    }
}