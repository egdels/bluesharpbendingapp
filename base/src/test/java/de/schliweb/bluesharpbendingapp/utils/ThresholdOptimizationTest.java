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
 * Test class for optimizing the THRESHOLD_LOW_FREQUENCY_ENERGY value in HybridPitchDetector.
 * This class evaluates different threshold values to find the optimal value that provides
 * the best pitch detection accuracy across a wide range of frequencies.
 */
class ThresholdOptimizationTest {

    private static final int SAMPLE_RATE = 44100;
    private static final double TOLERANCE = 1.0; // 1Hz tolerance for pitch detection
    private static final double ORIGINAL_THRESHOLD = HybridPitchDetector.getThresholdLowFrequencyEnergy();

    // Test frequencies covering low, medium, and high ranges
    private static final double[] TEST_FREQUENCIES = {
        100.0, 150.0, 200.0, 250.0, 290.0,  // Low frequencies
        300.0, 310.0, 350.0, 400.0, 500.0, 700.0, 900.0, 990.0,  // Medium frequencies
        1000.0, 1100.0, 1500.0, 2000.0, 3000.0  // High frequencies
    };

    // Threshold values to test
    private static final double[] TEST_THRESHOLDS = {
        100.0, 250.0, 500.0, 750.0, 1000.0, 1250.0, 1500.0, 2000.0, 2500.0, 3000.0
    };

    @BeforeAll
    static void setUp() {
        // Store the original threshold value
        System.out.println("[DEBUG_LOG] Original threshold: " + ORIGINAL_THRESHOLD);
    }

    @BeforeEach
    void setUpEach() {
        // Reset to original threshold value before each test
        HybridPitchDetector.setThresholdLowFrequencyEnergy(ORIGINAL_THRESHOLD);
        System.out.println("[DEBUG_LOG] Reset to original threshold: " + HybridPitchDetector.getThresholdLowFrequencyEnergy());
    }

    @AfterEach
    void tearDownEach() {
        // Restore the original threshold value after each test
        HybridPitchDetector.setThresholdLowFrequencyEnergy(ORIGINAL_THRESHOLD);
        System.out.println("[DEBUG_LOG] Restored original threshold: " + HybridPitchDetector.getThresholdLowFrequencyEnergy());
    }

    @AfterAll
    static void tearDown() {
        // Restore the original threshold value
        HybridPitchDetector.setThresholdLowFrequencyEnergy(ORIGINAL_THRESHOLD);
        System.out.println("[DEBUG_LOG] Restored original threshold: " + HybridPitchDetector.getThresholdLowFrequencyEnergy());
    }

    /**
     * Tests a specific threshold value with pure sine waves at various frequencies.
     * This test evaluates the accuracy of pitch detection for the given threshold value.
     *
     * @param threshold the threshold value to test
     * @return a pair containing the average error and the number of successful detections
     */
    private Map.Entry<Double, Integer> testThresholdWithPureSineWaves(double threshold) {
        HybridPitchDetector.setThresholdLowFrequencyEnergy(threshold);

        double totalError = 0.0;
        int successfulDetections = 0;

        for (double frequency : TEST_FREQUENCIES) {
            double[] sineWave = generateSineWave(frequency, SAMPLE_RATE, 1.0);
            PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchHybrid(sineWave, SAMPLE_RATE);

            if (result.pitch() != PitchDetector.NO_DETECTED_PITCH) {
                double error = Math.abs(result.pitch() - frequency);
                totalError += error;
                successfulDetections++;
            }
        }

        double averageError = successfulDetections > 0 ? totalError / successfulDetections : Double.MAX_VALUE;

        return Map.entry(averageError, successfulDetections);
    }

    @Test
    @DisplayName("Test threshold value 500")
    void testThreshold500() {
        double threshold = 500.0;
        evaluateThreshold(threshold);
    }

    @Test
    @DisplayName("Test threshold value 750")
    void testThreshold750() {
        double threshold = 750.0;
        evaluateThreshold(threshold);
    }

    @Test
    @DisplayName("Test threshold value 1000 (original)")
    void testThreshold1000() {
        double threshold = 1000.0;
        evaluateThreshold(threshold);
    }

    @Test
    @DisplayName("Test threshold value 1250")
    void testThreshold1250() {
        double threshold = 1250.0;
        evaluateThreshold(threshold);
    }

    @Test
    @DisplayName("Test threshold value 1500")
    void testThreshold1500() {
        double threshold = 1500.0;
        evaluateThreshold(threshold);
    }

    /**
     * Evaluates a specific threshold value with pure sine waves and complex signals.
     * This method tests the threshold with different types of signals and asserts the results.
     *
     * @param threshold the threshold value to evaluate
     */
    private void evaluateThreshold(double threshold) {
        // Test with pure sine waves
        Map.Entry<Double, Integer> pureSineResult = testThresholdWithPureSineWaves(threshold);
        double pureSineAverageError = pureSineResult.getKey();
        int pureSineSuccessfulDetections = pureSineResult.getValue();
        double pureSineSuccessRate = (double) pureSineSuccessfulDetections / TEST_FREQUENCIES.length;

        // Test with complex signals
        Map.Entry<Double, Integer> complexResult = testThresholdWithComplexSignals(threshold);
        double complexAverageError = complexResult.getKey();
        int complexSuccessfulDetections = complexResult.getValue();
        double complexSuccessRate = (double) complexSuccessfulDetections / TEST_FREQUENCIES.length;

        // Test with harmonica notes
        Map.Entry<Double, Integer> harmonicaResult = testThresholdWithHarmonicaNotes(threshold);
        double harmonicaSuccessRate = harmonicaResult.getKey();
        int harmonicaSuccessfulDetections = harmonicaResult.getValue();

        // Calculate an overall score (lower is better)
        double overallScore = pureSineAverageError * (1.0 - pureSineSuccessRate) + 
                             complexAverageError * (1.0 - complexSuccessRate) + 
                             (1.0 - harmonicaSuccessRate);

        // Log the results
        StringBuilder results = new StringBuilder();
        results.append("Threshold: ").append(threshold).append("\n");
        results.append("  Pure Sine Waves:\n");
        results.append("    Average Error: ").append(pureSineAverageError).append("\n");
        results.append("    Successful Detections: ").append(pureSineSuccessfulDetections).append("/").append(TEST_FREQUENCIES.length);
        results.append(" (").append(String.format("%.1f", pureSineSuccessRate * 100)).append("%)\n");
        results.append("  Complex Signals:\n");
        results.append("    Average Error: ").append(complexAverageError).append("\n");
        results.append("    Successful Detections: ").append(complexSuccessfulDetections).append("/").append(TEST_FREQUENCIES.length);
        results.append(" (").append(String.format("%.1f", complexSuccessRate * 100)).append("%)\n");
        results.append("  Harmonica Notes:\n");
        results.append("    Success Rate: ").append(String.format("%.1f", harmonicaSuccessRate * 100)).append("%\n");
        results.append("    Successful Detections: ").append(harmonicaSuccessfulDetections).append("\n");
        results.append("  Overall Score: ").append(String.format("%.4f", overallScore)).append(" (lower is better)\n");

        System.out.println("[DEBUG_LOG] " + results.toString());

        // Assert that the results meet the expected criteria
        assertTrue(pureSineSuccessRate >= 0.8, "Pure sine wave detection rate should be at least 80%");
        assertTrue(complexSuccessRate >= 0.7, "Complex signal detection rate should be at least 70%");
        assertTrue(harmonicaSuccessRate >= 0.7, "Harmonica note detection rate should be at least 70%");
        assertTrue(pureSineAverageError < TOLERANCE * 2, "Pure sine wave average error should be less than twice the tolerance");
        assertTrue(complexAverageError < TOLERANCE * 3, "Complex signal average error should be less than three times the tolerance");
    }

    /**
     * Tests a specific threshold value with complex signals (sine waves with harmonics and noise).
     * This test evaluates the robustness of pitch detection for the given threshold value.
     *
     * @param threshold the threshold value to test
     * @return a pair containing the average error and the number of successful detections
     */
    private Map.Entry<Double, Integer> testThresholdWithComplexSignals(double threshold) {
        HybridPitchDetector.setThresholdLowFrequencyEnergy(threshold);

        double totalError = 0.0;
        int successfulDetections = 0;

        for (double frequency : TEST_FREQUENCIES) {
            // Generate a complex signal with harmonics and noise
            double[] complexSignal = generateComplexSignal(frequency, SAMPLE_RATE, 1.0);
            PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchHybrid(complexSignal, SAMPLE_RATE);

            if (result.pitch() != PitchDetector.NO_DETECTED_PITCH) {
                double error = Math.abs(result.pitch() - frequency);
                totalError += error;
                successfulDetections++;
            }
        }

        double averageError = successfulDetections > 0 ? totalError / successfulDetections : Double.MAX_VALUE;

        return Map.entry(averageError, successfulDetections);
    }

    /**
     * Tests a specific threshold value with harmonica notes.
     * This test evaluates how the threshold affects the detector's ability to handle harmonica notes.
     *
     * @param threshold the threshold value to test
     * @return a pair containing the success rate and the number of successful detections
     */
    private Map.Entry<Double, Integer> testThresholdWithHarmonicaNotes(double threshold) {
        HybridPitchDetector.setThresholdLowFrequencyEnergy(threshold);

        // Define harmonica notes and their expected frequencies
        double[][] harmonicaNotes = {
            {261.63, 293.66, 329.63, 349.23, 392.00, 440.00, 493.88}, // C4 to B4
            {523.25, 587.33, 659.25, 698.46, 783.99, 880.00, 987.77}  // C5 to B5
        };

        int successfulDetections = 0;
        int totalTests = 0;

        for (double[] noteSet : harmonicaNotes) {
            for (double note : noteSet) {
                // Generate a harmonica-like signal for this note
                double[] harmonicaSignal = generateHarmonicaSignal(note, SAMPLE_RATE, 1.0);
                PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchHybrid(harmonicaSignal, SAMPLE_RATE);

                totalTests++;
                if (result.pitch() != PitchDetector.NO_DETECTED_PITCH && Math.abs(result.pitch() - note) < TOLERANCE * 2) {
                    successfulDetections++;
                }
            }
        }

        double successRate = totalTests > 0 ? (double) successfulDetections / totalTests : 0.0;

        return Map.entry(successRate, successfulDetections);
    }

    /**
     * Tests different threshold values with complex signals (sine waves with harmonics and noise).
     * This test evaluates the robustness of pitch detection for each threshold value.
     */
    @Test
    @DisplayName("Test different threshold values with complex signals")
    void testThresholdValuesWithComplexSignals() {
        Map<Double, Double> thresholdToAverageError = new HashMap<>();

        for (double threshold : TEST_THRESHOLDS) {
            HybridPitchDetector.setThresholdLowFrequencyEnergy(threshold);
            System.out.println("[DEBUG_LOG] Testing threshold with complex signals: " + threshold);

            double totalError = 0.0;
            int successfulDetections = 0;

            for (double frequency : TEST_FREQUENCIES) {
                // Generate a complex signal with harmonics and noise
                double[] complexSignal = generateComplexSignal(frequency, SAMPLE_RATE, 1.0);
                PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchHybrid(complexSignal, SAMPLE_RATE);

                if (result.pitch() != PitchDetector.NO_DETECTED_PITCH) {
                    double error = Math.abs(result.pitch() - frequency);
                    totalError += error;
                    successfulDetections++;
                    System.out.println("[DEBUG_LOG] Complex - Frequency: " + frequency + ", Detected: " + result.pitch() + ", Error: " + error + ", Confidence: " + result.confidence());
                } else {
                    System.out.println("[DEBUG_LOG] Complex - Frequency: " + frequency + ", No pitch detected");
                }
            }

            if (successfulDetections > 0) {
                double averageError = totalError / successfulDetections;
                thresholdToAverageError.put(threshold, averageError);
                System.out.println("[DEBUG_LOG] Complex - Threshold: " + threshold + ", Average Error: " + averageError + ", Successful Detections: " + successfulDetections + "/" + TEST_FREQUENCIES.length);
            } else {
                System.out.println("[DEBUG_LOG] Complex - Threshold: " + threshold + ", No successful detections");
            }
        }

        // Find the threshold with the lowest average error
        double bestThreshold = findBestThreshold(thresholdToAverageError);
        System.out.println("[DEBUG_LOG] Complex - Best threshold: " + bestThreshold + ", Average Error: " + thresholdToAverageError.get(bestThreshold));

        // Assert that we found a threshold with acceptable error
        assertTrue(thresholdToAverageError.get(bestThreshold) < TOLERANCE * 2, "Best threshold for complex signals should have average error less than twice the tolerance");
    }

    /**
     * Tests different threshold values with signals that have varying energy levels.
     * This test evaluates how the threshold affects the detector's ability to handle signals with different energy levels.
     */
    @Test
    @DisplayName("Test different threshold values with varying energy levels")
    void testThresholdValuesWithVaryingEnergyLevels() {
        Map<Double, Double> thresholdToAverageError = new HashMap<>();
        double[] amplitudes = {0.1, 0.5, 1.0, 2.0, 5.0};

        for (double threshold : TEST_THRESHOLDS) {
            HybridPitchDetector.setThresholdLowFrequencyEnergy(threshold);
            System.out.println("[DEBUG_LOG] Testing threshold with varying energy levels: " + threshold);

            double totalError = 0.0;
            int successfulDetections = 0;

            for (double frequency : TEST_FREQUENCIES) {
                for (double amplitude : amplitudes) {
                    double[] sineWave = generateSineWave(frequency, SAMPLE_RATE, 1.0, amplitude);
                    PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchHybrid(sineWave, SAMPLE_RATE);

                    if (result.pitch() != PitchDetector.NO_DETECTED_PITCH) {
                        double error = Math.abs(result.pitch() - frequency);
                        totalError += error;
                        successfulDetections++;
                        System.out.println("[DEBUG_LOG] Energy - Frequency: " + frequency + ", Amplitude: " + amplitude + ", Detected: " + result.pitch() + ", Error: " + error);
                    } else {
                        System.out.println("[DEBUG_LOG] Energy - Frequency: " + frequency + ", Amplitude: " + amplitude + ", No pitch detected");
                    }
                }
            }

            if (successfulDetections > 0) {
                double averageError = totalError / successfulDetections;
                thresholdToAverageError.put(threshold, averageError);
                System.out.println("[DEBUG_LOG] Energy - Threshold: " + threshold + ", Average Error: " + averageError + ", Successful Detections: " + successfulDetections + "/" + (TEST_FREQUENCIES.length * amplitudes.length));
            } else {
                System.out.println("[DEBUG_LOG] Energy - Threshold: " + threshold + ", No successful detections");
            }
        }

        // Find the threshold with the lowest average error
        double bestThreshold = findBestThreshold(thresholdToAverageError);
        System.out.println("[DEBUG_LOG] Energy - Best threshold: " + bestThreshold + ", Average Error: " + thresholdToAverageError.get(bestThreshold));

        // Assert that we found a threshold with acceptable error
        assertTrue(thresholdToAverageError.get(bestThreshold) < TOLERANCE * 2, "Best threshold for varying energy levels should have average error less than twice the tolerance");
    }

    /**
     * Tests different threshold values with real harmonica notes.
     * This test evaluates how the threshold affects the detector's ability to handle real harmonica notes.
     */
    @Test
    @DisplayName("Test different threshold values with harmonica notes")
    void testThresholdValuesWithHarmonicaNotes() {
        Map<Double, Double> thresholdToSuccessRate = new HashMap<>();

        // Define harmonica notes and their expected frequencies
        double[][] harmonicaNotes = {
            {261.63, 293.66, 329.63, 349.23, 392.00, 440.00, 493.88}, // C4 to B4
            {523.25, 587.33, 659.25, 698.46, 783.99, 880.00, 987.77}  // C5 to B5
        };

        for (double threshold : TEST_THRESHOLDS) {
            HybridPitchDetector.setThresholdLowFrequencyEnergy(threshold);
            System.out.println("[DEBUG_LOG] Testing threshold with harmonica notes: " + threshold);

            int successfulDetections = 0;
            int totalTests = 0;

            for (double[] noteSet : harmonicaNotes) {
                for (double note : noteSet) {
                    // Generate a harmonica-like signal for this note
                    double[] harmonicaSignal = generateHarmonicaSignal(note, SAMPLE_RATE, 1.0);
                    PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchHybrid(harmonicaSignal, SAMPLE_RATE);

                    totalTests++;
                    if (result.pitch() != PitchDetector.NO_DETECTED_PITCH && Math.abs(result.pitch() - note) < TOLERANCE * 2) {
                        successfulDetections++;
                        System.out.println("[DEBUG_LOG] Harmonica - Note: " + note + ", Detected: " + result.pitch() + ", Success");
                    } else {
                        System.out.println("[DEBUG_LOG] Harmonica - Note: " + note + ", Detected: " + (result.pitch() == PitchDetector.NO_DETECTED_PITCH ? "None" : result.pitch()) + ", Failure");
                    }
                }
            }

            double successRate = (double) successfulDetections / totalTests;
            thresholdToSuccessRate.put(threshold, successRate);
            System.out.println("[DEBUG_LOG] Harmonica - Threshold: " + threshold + ", Success Rate: " + successRate + " (" + successfulDetections + "/" + totalTests + ")");
        }

        // Find the threshold with the highest success rate
        double bestThreshold = findBestThresholdBySuccessRate(thresholdToSuccessRate);
        System.out.println("[DEBUG_LOG] Harmonica - Best threshold: " + bestThreshold + ", Success Rate: " + thresholdToSuccessRate.get(bestThreshold));

        // Assert that we found a threshold with acceptable success rate
        assertTrue(thresholdToSuccessRate.get(bestThreshold) > 0.8, "Best threshold for harmonica notes should have success rate greater than 80%");
    }

    /**
     * Finds the threshold with the lowest average error.
     *
     * @param thresholdToAverageError a map of threshold values to their average errors
     * @return the threshold with the lowest average error
     */
    private double findBestThreshold(Map<Double, Double> thresholdToAverageError) {
        double bestThreshold = 0.0;
        double lowestError = Double.MAX_VALUE;

        for (Map.Entry<Double, Double> entry : thresholdToAverageError.entrySet()) {
            if (entry.getValue() < lowestError) {
                lowestError = entry.getValue();
                bestThreshold = entry.getKey();
            }
        }

        return bestThreshold;
    }

    /**
     * Finds the threshold with the highest success rate.
     *
     * @param thresholdToSuccessRate a map of threshold values to their success rates
     * @return the threshold with the highest success rate
     */
    private double findBestThresholdBySuccessRate(Map<Double, Double> thresholdToSuccessRate) {
        double bestThreshold = 0.0;
        double highestSuccessRate = 0.0;

        for (Map.Entry<Double, Double> entry : thresholdToSuccessRate.entrySet()) {
            if (entry.getValue() > highestSuccessRate) {
                highestSuccessRate = entry.getValue();
                bestThreshold = entry.getKey();
            }
        }

        return bestThreshold;
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
        return generateSineWave(frequency, sampleRate, duration, 1.0);
    }

    /**
     * Generates a sine wave with the specified frequency, sample rate, duration, and amplitude.
     *
     * @param frequency the frequency of the sine wave in Hz
     * @param sampleRate the sample rate in Hz
     * @param duration the duration of the sine wave in seconds
     * @param amplitude the amplitude of the sine wave
     * @return an array of doubles representing the sine wave
     */
    private double[] generateSineWave(double frequency, int sampleRate, double duration, double amplitude) {
        int numSamples = (int) (duration * sampleRate);
        double[] sineWave = new double[numSamples];

        for (int i = 0; i < numSamples; i++) {
            double t = (double) i / sampleRate;
            sineWave[i] = amplitude * Math.sin(2 * Math.PI * frequency * t);
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

    /**
     * Generates a harmonica-like signal with the specified fundamental frequency, sample rate, and duration.
     * The harmonica signal includes specific harmonics typical of a harmonica.
     *
     * @param frequency the fundamental frequency of the harmonica note in Hz
     * @param sampleRate the sample rate in Hz
     * @param duration the duration of the signal in seconds
     * @return an array of doubles representing the harmonica signal
     */
    private double[] generateHarmonicaSignal(double frequency, int sampleRate, double duration) {
        int numSamples = (int) (duration * sampleRate);
        double[] harmonicaSignal = new double[numSamples];

        // Harmonica-specific harmonic amplitudes
        double[] harmonicAmplitudes = {1.0, 0.7, 0.5, 0.3, 0.2, 0.1};

        for (int i = 0; i < numSamples; i++) {
            double t = (double) i / sampleRate;
            harmonicaSignal[i] = 0;

            // Add fundamental and harmonics
            for (int h = 0; h < harmonicAmplitudes.length; h++) {
                harmonicaSignal[i] += harmonicAmplitudes[h] * Math.sin(2 * Math.PI * frequency * (h + 1) * t);
            }

            // Add some breath noise
            harmonicaSignal[i] += 0.05 * (Math.random() * 2 - 1);
        }

        return harmonicaSignal;
    }
}
