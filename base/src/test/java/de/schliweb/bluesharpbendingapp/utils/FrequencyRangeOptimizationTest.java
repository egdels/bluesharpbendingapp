package de.schliweb.bluesharpbendingapp.utils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
 * Test class for optimizing the FREQUENCY_RANGE_LOW value in HybridPitchDetector.
 * This class evaluates different frequency range values to find the optimal value that provides
 * the best performance (execution time) while maintaining pitch detection accuracy.
 */
class FrequencyRangeOptimizationTest {

    private static final int SAMPLE_RATE = 44100;
    private static final double TOLERANCE = 1.0; // 1Hz tolerance for pitch detection
    private static final double ORIGINAL_THRESHOLD = HybridPitchDetector.getThresholdLowFrequencyEnergy();
    private static final int ORIGINAL_FREQUENCY_RANGE = HybridPitchDetector.getFrequencyRangeLow();

    // Test frequencies covering low, medium, and high ranges
    private static final double[] TEST_FREQUENCIES = {
        100.0, 150.0, 200.0, 250.0, 290.0,  // Low frequencies
        300.0, 310.0, 350.0, 400.0, 500.0, 700.0, 900.0, 990.0,  // Medium frequencies
        1000.0, 1100.0, 1500.0, 2000.0, 3000.0  // High frequencies
    };

    // Frequency range values to test
    private static final int[] TEST_FREQUENCY_RANGES = {
        200, 250, 275, 300, 325, 350, 400
    };

    @BeforeAll
    static void setUp() {
        // Store the original values
        System.out.println("[DEBUG_LOG] Original threshold: " + ORIGINAL_THRESHOLD);
        System.out.println("[DEBUG_LOG] Original frequency range: " + ORIGINAL_FREQUENCY_RANGE);
    }

    @BeforeEach
    void setUpEach() {
        // Reset to original values before each test
        HybridPitchDetector.setThresholdLowFrequencyEnergy(ORIGINAL_THRESHOLD);
        HybridPitchDetector.setFrequencyRangeLow(ORIGINAL_FREQUENCY_RANGE);
        System.out.println("[DEBUG_LOG] Reset to original threshold: " + HybridPitchDetector.getThresholdLowFrequencyEnergy());
        System.out.println("[DEBUG_LOG] Reset to original frequency range: " + HybridPitchDetector.getFrequencyRangeLow());
    }

    @AfterEach
    void tearDownEach() {
        // Restore the original values after each test
        HybridPitchDetector.setThresholdLowFrequencyEnergy(ORIGINAL_THRESHOLD);
        HybridPitchDetector.setFrequencyRangeLow(ORIGINAL_FREQUENCY_RANGE);
        System.out.println("[DEBUG_LOG] Restored original threshold: " + HybridPitchDetector.getThresholdLowFrequencyEnergy());
        System.out.println("[DEBUG_LOG] Restored original frequency range: " + HybridPitchDetector.getFrequencyRangeLow());
    }

    @AfterAll
    static void tearDown() {
        // Restore the original values
        HybridPitchDetector.setThresholdLowFrequencyEnergy(ORIGINAL_THRESHOLD);
        HybridPitchDetector.setFrequencyRangeLow(ORIGINAL_FREQUENCY_RANGE);
        System.out.println("[DEBUG_LOG] Restored original threshold: " + HybridPitchDetector.getThresholdLowFrequencyEnergy());
        System.out.println("[DEBUG_LOG] Restored original frequency range: " + HybridPitchDetector.getFrequencyRangeLow());
    }

    /**
     * Tests a specific frequency range value with pure sine waves at various frequencies.
     * This test evaluates the accuracy and execution time of pitch detection for the given frequency range.
     *
     * @param frequencyRange the frequency range value to test
     * @return a map entry containing the average error, number of successful detections, and average execution time
     */
    private Map.Entry<Double, Map.Entry<Integer, Long>> testFrequencyRangeWithPureSineWaves(int frequencyRange) {
        HybridPitchDetector.setFrequencyRangeLow(frequencyRange);

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
     * Tests different frequency range values with pure sine waves.
     * This test evaluates the accuracy and execution time of pitch detection for each frequency range value.
     */
    @Test
    @DisplayName("Test different frequency range values with pure sine waves")
    void testFrequencyRangeValuesWithPureSineWaves() {
        Map<Integer, Double> rangeToAverageError = new HashMap<>();
        Map<Integer, Long> rangeToAverageExecutionTime = new HashMap<>();
        Map<Integer, Integer> rangeToSuccessfulDetections = new HashMap<>();

        for (int frequencyRange : TEST_FREQUENCY_RANGES) {
            Map.Entry<Double, Map.Entry<Integer, Long>> result = testFrequencyRangeWithPureSineWaves(frequencyRange);
            double averageError = result.getKey();
            int successfulDetections = result.getValue().getKey();
            long averageExecutionTime = result.getValue().getValue();

            rangeToAverageError.put(frequencyRange, averageError);
            rangeToAverageExecutionTime.put(frequencyRange, averageExecutionTime);
            rangeToSuccessfulDetections.put(frequencyRange, successfulDetections);

            System.out.println("[DEBUG_LOG] Frequency Range: " + frequencyRange + 
                               ", Average Error: " + averageError + 
                               ", Successful Detections: " + successfulDetections + "/" + TEST_FREQUENCIES.length +
                               ", Average Execution Time: " + averageExecutionTime + " ns");
        }

        // Find the frequency range with the lowest execution time
        int bestFrequencyRange = findBestFrequencyRangeByExecutionTime(rangeToAverageExecutionTime, rangeToAverageError);
        System.out.println("[DEBUG_LOG] Best frequency range by execution time: " + bestFrequencyRange + 
                           ", Average Execution Time: " + rangeToAverageExecutionTime.get(bestFrequencyRange) + " ns" +
                           ", Average Error: " + rangeToAverageError.get(bestFrequencyRange));

        // Assert that we found a frequency range with acceptable error
        assertTrue(rangeToAverageError.get(bestFrequencyRange) < TOLERANCE, 
                   "Best frequency range should have average error less than the tolerance");
    }

    /**
     * Tests different combinations of threshold and frequency range values.
     * This test evaluates the accuracy and execution time of pitch detection for each combination.
     */
    @Test
    @DisplayName("Test different combinations of threshold and frequency range values")
    void testCombinationsOfThresholdAndFrequencyRange() {
        // Threshold values to test
        double[] testThresholds = {500.0, 750.0, 1000.0, 1250.0};

        Map<String, Double> combinationToAverageError = new HashMap<>();
        Map<String, Long> combinationToAverageExecutionTime = new HashMap<>();
        Map<String, Integer> combinationToSuccessfulDetections = new HashMap<>();

        for (double threshold : testThresholds) {
            HybridPitchDetector.setThresholdLowFrequencyEnergy(threshold);

            for (int frequencyRange : TEST_FREQUENCY_RANGES) {
                HybridPitchDetector.setFrequencyRangeLow(frequencyRange);

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
        double threshold = 750.0;
        int frequencyRange = 325;

        HybridPitchDetector.setThresholdLowFrequencyEnergy(threshold);
        HybridPitchDetector.setFrequencyRangeLow(frequencyRange);

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

        // Log the results for debugging
        System.out.println("[DEBUG_LOG] " + results.toString());

        // Assert that the average error is within the acceptable range
        assertTrue(averageError < TOLERANCE, 
                   "Average error should be less than the tolerance for the specific combination");

        // Assert that the average cent error is within an acceptable range
        assertTrue(averageCentError < 1.0, 
                   "Average cent error should be less than 1 cent for the specific combination");

        // Assert that all frequencies were detected
        assertEquals(TEST_FREQUENCIES.length, successfulDetections, 
                     "All test frequencies should be detected");
    }

    /**
     * Tests the accuracy of pitch detection in terms of cents.
     * This test verifies that the pitch detection accuracy is within 1 cent for different frequency ranges.
     */
    @Test
    @DisplayName("Test pitch detection accuracy in cents")
    void testPitchDetectionAccuracyInCents() {
        for (int frequencyRange : TEST_FREQUENCY_RANGES) {
            HybridPitchDetector.setFrequencyRangeLow(frequencyRange);

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

                    System.out.println("[DEBUG_LOG] Frequency Range: " + frequencyRange + 
                                       ", Frequency: " + frequency + 
                                       ", Detected: " + result.pitch() + 
                                       ", Cent Error: " + centError);
                }
            }

            double averageCentError = successfulDetections > 0 ? totalCentError / successfulDetections : Double.MAX_VALUE;

            System.out.println("[DEBUG_LOG] Frequency Range: " + frequencyRange + 
                               ", Average Cent Error: " + averageCentError + 
                               ", Max Cent Error: " + maxCentError + 
                               ", Successful Detections: " + successfulDetections + "/" + TEST_FREQUENCIES.length);

            // Assert that the maximum cent error is within the acceptable range
            assertTrue(maxCentError < 1.0, "Maximum cent error should be less than 1 cent for frequency range " + frequencyRange);
        }
    }

    /**
     * Finds the frequency range with the lowest execution time among those with acceptable error.
     *
     * @param rangeToAverageExecutionTime a map of frequency range values to their average execution times
     * @param rangeToAverageError a map of frequency range values to their average errors
     * @return the frequency range with the lowest execution time among those with acceptable error
     */
    private int findBestFrequencyRangeByExecutionTime(Map<Integer, Long> rangeToAverageExecutionTime, Map<Integer, Double> rangeToAverageError) {
        int bestFrequencyRange = 0;
        long lowestExecutionTime = Long.MAX_VALUE;

        for (Map.Entry<Integer, Long> entry : rangeToAverageExecutionTime.entrySet()) {
            int frequencyRange = entry.getKey();
            long executionTime = entry.getValue();
            double error = rangeToAverageError.get(frequencyRange);

            // Only consider frequency ranges with acceptable error
            if (error < TOLERANCE && executionTime < lowestExecutionTime) {
                lowestExecutionTime = executionTime;
                bestFrequencyRange = frequencyRange;
            }
        }

        return bestFrequencyRange;
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
