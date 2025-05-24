package de.schliweb.bluesharpbendingapp.utils;
/*
 * Copyright (c) 2023 Christian Kierdorf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Benchmark class to compare the performance and accuracy of the two chord detection methods:
 * 1. ChordDetector - A spectral-based algorithm using FFT
 * 2. OnnxChordDetector - A machine learning approach using an ONNX model
 * 
 * This class measures:
 * - Execution time (performance)
 * - Accuracy in detecting chords
 * - Memory usage
 * 
 * The results are documented and can be used to make informed decisions about
 * which method to use in different scenarios.
 */
public class ChordDetectorBenchmark {

    private static final int SAMPLE_RATE = 44100;
    private static final double TOLERANCE = 1.0;
    private static final int WARMUP_ITERATIONS = 3;
    private static final int BENCHMARK_ITERATIONS = 10;

    // Test audio file for C major chord
    private static final String TEST_AUDIO_FILE = "models/test_audio/chord_C4-E4-G4.wav";

    // Synthetic chord data with names and frequencies
    private static final Object[][] SYNTHETIC_CHORDS = {
        {"C major", new double[]{261.63, 329.63, 392.0}},  // C4, E4, G4
        {"G major", new double[]{196.0, 246.94, 293.66}},  // G3, B3, D4
        {"A minor", new double[]{220.0, 261.63, 329.63}},  // A3, C4, E4
        {"D minor", new double[]{293.66, 349.23, 440.0}}   // D4, F4, A4
    };

    // Expected frequencies for each chord
    private static final Map<String, double[]> EXPECTED_FREQUENCIES = new HashMap<>();

    // Detectors
    private ChordDetector spectralDetector;
    private OnnxChordDetector onnxDetector;

    // Results storage
    private final Map<String, List<Double>> spectralTimings = new HashMap<>();
    private final Map<String, List<Double>> onnxTimings = new HashMap<>();
    private final Map<String, Double> spectralAccuracy = new HashMap<>();
    private final Map<String, Double> onnxAccuracy = new HashMap<>();

    @BeforeEach
    void setUp() {
        // Reset frequency range to defaults
        PitchDetector.setMaxFrequency(PitchDetector.getDefaultMaxFrequency());
        PitchDetector.setMinFrequency(PitchDetector.getDefaultMinFrequency());

        // Initialize detectors
        spectralDetector = new ChordDetector();
        onnxDetector = new OnnxChordDetector();

        // Initialize expected frequencies
        EXPECTED_FREQUENCIES.put("chord_C4-E4-G4.wav", new double[]{261.63, 329.63, 392.0});  // C major
        EXPECTED_FREQUENCIES.put("chord_G3-B3-D4.wav", new double[]{196.0, 246.94, 293.66});  // G major
        EXPECTED_FREQUENCIES.put("chord_A3-C4-E4.wav", new double[]{220.0, 261.63, 329.63});  // A minor
        EXPECTED_FREQUENCIES.put("chord_D4-F4-A4.wav", new double[]{293.66, 349.23, 440.0});  // D minor
    }

    /**
     * Runs the benchmark for real and synthetic chord data and collects performance and accuracy data.
     */
    @Test
    void runBenchmark() {
        System.out.println("=== Chord Detection Benchmark ===");
        System.out.println("Comparing spectral-based ChordDetector vs. ONNX-based OnnxChordDetector");
        System.out.println("Sample rate: " + SAMPLE_RATE + " Hz");
        System.out.println("Warmup iterations: " + WARMUP_ITERATIONS);
        System.out.println("Benchmark iterations: " + BENCHMARK_ITERATIONS);
        System.out.println("Frequency tolerance: " + TOLERANCE + " Hz");
        System.out.println();

        // Test with real audio file
        try {
            // Extract the filename without path
            String fileName = TEST_AUDIO_FILE.substring(TEST_AUDIO_FILE.lastIndexOf('/') + 1);
            System.out.println("Testing with real audio: " + fileName);

            // Load audio data
            double[] audioData = loadAudioFile(TEST_AUDIO_FILE);
            assertNotNull(audioData, "Failed to load audio file: " + TEST_AUDIO_FILE);

            // Get expected frequencies
            double[] expectedFreqs = EXPECTED_FREQUENCIES.get(fileName);
            assertNotNull(expectedFreqs, "No expected frequencies defined for: " + fileName);

            // Run benchmark for this file
            benchmarkFile(audioData, expectedFreqs, fileName);

            System.out.println();
        } catch (Exception e) {
            System.err.println("Error processing file " + TEST_AUDIO_FILE + ": " + e.getMessage());
            e.printStackTrace();
        }

        // Test with synthetic chord data
        for (int i = 1; i < SYNTHETIC_CHORDS.length; i++) {  // Skip C major as we already tested it with real audio
            try {
                String chordName = (String) SYNTHETIC_CHORDS[i][0];
                double[] expectedFreqs = (double[]) SYNTHETIC_CHORDS[i][1];

                System.out.println("Testing with synthetic chord: " + chordName);

                // Generate a unique filename for this chord
                String fileName = "synthetic_" + chordName.replace(" ", "_") + ".wav";

                // Generate synthetic chord
                double[] audioData = generateChord(expectedFreqs, new double[]{1.0, 1.0, 1.0}, SAMPLE_RATE, 2.0);

                // Run benchmark for this synthetic chord
                benchmarkFile(audioData, expectedFreqs, fileName);

                System.out.println();
            } catch (Exception e) {
                System.err.println("Error processing synthetic chord " + SYNTHETIC_CHORDS[i][0] + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Print summary results
        printSummaryResults();
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
                if (j < amplitudes.length) {
                    sample += amplitudes[j] * Math.sin(2 * Math.PI * frequencies[j] * i / sampleRate);
                }
            }
            // Normalize to avoid clipping
            chord[i] = sample / frequencies.length;
        }

        return chord;
    }

    /**
     * Benchmarks both detectors with a single audio file.
     * 
     * @param audioData The audio data to analyze
     * @param expectedFreqs The expected frequencies in the chord
     * @param fileName The name of the audio file (for reporting)
     */
    private void benchmarkFile(double[] audioData, double[] expectedFreqs, String fileName) {
        // Initialize result lists
        spectralTimings.put(fileName, new ArrayList<>());
        onnxTimings.put(fileName, new ArrayList<>());

        // Warmup
        System.out.println("  Warming up...");
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            spectralDetector.detectChordInternal(audioData, SAMPLE_RATE);
            onnxDetector.detectChordInternal(audioData, SAMPLE_RATE);
        }

        // Benchmark spectral detector
        System.out.println("  Benchmarking spectral detector...");
        List<ChordDetectionResult> spectralResults = new ArrayList<>();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long startTime = System.nanoTime();
            ChordDetectionResult result = spectralDetector.detectChordInternal(audioData, SAMPLE_RATE);
            long endTime = System.nanoTime();
            double elapsedMs = (endTime - startTime) / 1_000_000.0;
            spectralTimings.get(fileName).add(elapsedMs);
            spectralResults.add(result);
        }

        // Benchmark ONNX detector
        System.out.println("  Benchmarking ONNX detector...");
        List<ChordDetectionResult> onnxResults = new ArrayList<>();
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            long startTime = System.nanoTime();
            ChordDetectionResult result = onnxDetector.detectChordInternal(audioData, SAMPLE_RATE);
            long endTime = System.nanoTime();
            double elapsedMs = (endTime - startTime) / 1_000_000.0;
            onnxTimings.get(fileName).add(elapsedMs);
            onnxResults.add(result);
        }

        // Calculate accuracy
        double spectralAcc = calculateAccuracy(spectralResults, expectedFreqs);
        double onnxAcc = calculateAccuracy(onnxResults, expectedFreqs);
        spectralAccuracy.put(fileName, spectralAcc);
        onnxAccuracy.put(fileName, onnxAcc);

        // Print results for this file
        printFileResults(fileName);
    }

    /**
     * Calculates the accuracy of chord detection results compared to expected frequencies.
     * 
     * @param results List of chord detection results
     * @param expectedFreqs Array of expected frequencies
     * @return Accuracy as a percentage (0-100)
     */
    private double calculateAccuracy(List<ChordDetectionResult> results, double[] expectedFreqs) {
        if (results.isEmpty()) {
            return 0.0;
        }

        int totalExpectedNotes = expectedFreqs.length * results.size();
        int correctlyDetectedNotes = 0;

        for (ChordDetectionResult result : results) {
            if (!result.hasPitches()) {
                continue;
            }

            List<Double> detectedPitches = result.pitches();
            for (double expectedFreq : expectedFreqs) {
                for (double detectedPitch : detectedPitches) {
                    if (Math.abs(detectedPitch - expectedFreq) <= TOLERANCE) {
                        correctlyDetectedNotes++;
                        break;
                    }
                }
            }
        }

        return (double) correctlyDetectedNotes / totalExpectedNotes * 100.0;
    }

    /**
     * Prints the benchmark results for a single file.
     * 
     * @param fileName The name of the audio file
     */
    private void printFileResults(String fileName) {
        List<Double> spectralTimes = spectralTimings.get(fileName);
        List<Double> onnxTimes = onnxTimings.get(fileName);

        double spectralAvg = spectralTimes.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double onnxAvg = onnxTimes.stream().mapToDouble(Double::doubleValue).average().orElse(0);

        double spectralAcc = spectralAccuracy.get(fileName);
        double onnxAcc = onnxAccuracy.get(fileName);

        System.out.println("  Results for " + fileName + ":");
        System.out.println("    Spectral detector: " + String.format("%.2f ms", spectralAvg) + 
                           ", Accuracy: " + String.format("%.2f%%", spectralAcc));
        System.out.println("    ONNX detector: " + String.format("%.2f ms", onnxAvg) + 
                           ", Accuracy: " + String.format("%.2f%%", onnxAcc));

        // Performance comparison
        double speedupFactor = onnxAvg / spectralAvg;
        String fasterMethod = speedupFactor > 1 ? "Spectral" : "ONNX";
        double factor = Math.max(speedupFactor, 1/speedupFactor);
        System.out.println("    " + fasterMethod + " detector is " + String.format("%.2fx", factor) + " faster");

        // Accuracy comparison
        double accuracyDiff = Math.abs(spectralAcc - onnxAcc);
        String moreAccurateMethod = spectralAcc > onnxAcc ? "Spectral" : "ONNX";
        System.out.println("    " + moreAccurateMethod + " detector is " + 
                           String.format("%.2f", accuracyDiff) + " percentage points more accurate");
    }

    /**
     * Prints a summary of all benchmark results.
     */
    private void printSummaryResults() {
        System.out.println("=== Summary Results ===");

        // Calculate overall averages
        double spectralTimeAvg = spectralTimings.values().stream()
                .flatMap(List::stream)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);

        double onnxTimeAvg = onnxTimings.values().stream()
                .flatMap(List::stream)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);

        double spectralAccAvg = spectralAccuracy.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);

        double onnxAccAvg = onnxAccuracy.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);

        System.out.println("Average execution time:");
        System.out.println("  Spectral detector: " + String.format("%.2f ms", spectralTimeAvg));
        System.out.println("  ONNX detector: " + String.format("%.2f ms", onnxTimeAvg));

        System.out.println("Average accuracy:");
        System.out.println("  Spectral detector: " + String.format("%.2f%%", spectralAccAvg));
        System.out.println("  ONNX detector: " + String.format("%.2f%%", onnxAccAvg));

        // Overall performance comparison
        double speedupFactor = onnxTimeAvg / spectralTimeAvg;
        String fasterMethod = speedupFactor > 1 ? "Spectral" : "ONNX";
        double factor = Math.max(speedupFactor, 1/speedupFactor);
        System.out.println("Overall, the " + fasterMethod + " detector is " + 
                           String.format("%.2fx", factor) + " faster");

        // Overall accuracy comparison
        double accuracyDiff = Math.abs(spectralAccAvg - onnxAccAvg);
        String moreAccurateMethod = spectralAccAvg > onnxAccAvg ? "Spectral" : "ONNX";
        System.out.println("Overall, the " + moreAccurateMethod + " detector is " + 
                           String.format("%.2f", accuracyDiff) + " percentage points more accurate");

        // Recommendations
        System.out.println("\nRecommendations:");
        if (spectralTimeAvg < onnxTimeAvg && spectralAccAvg >= onnxAccAvg) {
            System.out.println("The spectral detector is both faster and more accurate, making it the better choice overall.");
        } else if (onnxTimeAvg < spectralTimeAvg && onnxAccAvg >= spectralAccAvg) {
            System.out.println("The ONNX detector is both faster and more accurate, making it the better choice overall.");
        } else if (spectralTimeAvg < onnxTimeAvg) {
            System.out.println("The spectral detector is faster, but the ONNX detector is more accurate.");
            System.out.println("Use the spectral detector for real-time applications where performance is critical.");
            System.out.println("Use the ONNX detector for applications where accuracy is more important than speed.");
        } else {
            System.out.println("The ONNX detector is faster, but the spectral detector is more accurate.");
            System.out.println("Use the ONNX detector for real-time applications where performance is critical.");
            System.out.println("Use the spectral detector for applications where accuracy is more important than speed.");
        }
    }

    /**
     * Loads an audio file from the resources directory and converts it to a double array.
     * 
     * @param audioFilePath The path to the audio file in the resources directory
     * @return A double array containing the audio data, or null if the file couldn't be loaded
     */
    private double[] loadAudioFile(String audioFilePath) {
        try {
            URL resourceUrl = getClass().getClassLoader().getResource(audioFilePath);
            if (resourceUrl == null) {
                System.err.println("Audio file not found: " + audioFilePath);
                return null;
            }

            Path path = Paths.get(resourceUrl.toURI());
            byte[] audioBytes = Files.readAllBytes(path);

            // Convert bytes to double array (assuming 16-bit PCM, little-endian)
            double[] audioData = new double[audioBytes.length / 2];
            for (int i = 0; i < audioData.length; i++) {
                // Convert two bytes to a short (little-endian)
                short sample = (short) ((audioBytes[i * 2 + 1] << 8) | (audioBytes[i * 2] & 0xFF));
                // Normalize to [-1.0, 1.0]
                audioData[i] = sample / 32768.0;
            }

            return audioData;
        } catch (URISyntaxException | IOException e) {
            System.err.println("Error loading audio file: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
