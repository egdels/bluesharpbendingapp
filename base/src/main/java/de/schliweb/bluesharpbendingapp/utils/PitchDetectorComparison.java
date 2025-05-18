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

import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;

/**
 * A class to compare the performance, accuracy, and confidence of YIN, MPM, FFT, and Hybrid pitch detection algorithms
 * for a Richter harmonica in C.
 * <p>
 * This class generates test signals based on the frequencies of a C Richter harmonica,
 * and evaluates each algorithm's ability to detect these frequencies accurately.
 * It also measures the execution time and confidence values of each algorithm.
 * <p>
 * The comparison includes:
 * - YIN: Time-domain algorithm based on autocorrelation with cumulative mean normalization
 * - MPM: Time-domain algorithm based on normalized square difference function (NSDF)
 * - FFT: Frequency-domain algorithm based on Fast Fourier Transform spectral analysis
 * - Hybrid: Combined algorithm that uses YIN for low frequencies, MPM for mid-range, and FFT for high frequencies
 */
public class PitchDetectorComparison {

    private static final int SAMPLE_RATE = 44100;
    private static final int ITERATIONS = 100; // Number of iterations for each test to get reliable averages
    private static final double DURATION = 1.0; // Duration of audio samples in seconds

    /**
     * Main method to run the performance and accuracy comparison.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Running Pitch Detector Comparison for Richter Harmonica in C...");

        // Create a C Richter harmonica
        Harmonica harmonica = AbstractHarmonica.create(AbstractHarmonica.KEY.C, AbstractHarmonica.TUNE.RICHTER);

        // Reset to default frequency range
        PitchDetector.setMaxFrequency(PitchDetector.getDefaultMaxFrequency());
        PitchDetector.setMinFrequency(PitchDetector.getDefaultMinFrequency());

        // Run single note performance and accuracy tests for blow notes
        System.out.println("\n=== BLOW NOTES PERFORMANCE AND ACCURACY ===");
        for (int channel = 1; channel <= 10; channel++) {
            double frequency = harmonica.getNoteFrequency(channel, 0);
            comparePerformanceAndAccuracy(frequency, "Blow Channel " + channel);
        }

        // Run single note performance and accuracy tests for draw notes
        System.out.println("\n=== DRAW NOTES PERFORMANCE AND ACCURACY ===");
        for (int channel = 1; channel <= 10; channel++) {
            double frequency = harmonica.getNoteFrequency(channel, 1);
            comparePerformanceAndAccuracy(frequency, "Draw Channel " + channel);
        }

        // Run tests with noise
        System.out.println("\n=== PERFORMANCE AND ACCURACY WITH NOISE ===");
        for (int channel = 1; channel <= 10; channel++) {
            double frequency = harmonica.getNoteFrequency(channel, 0);
            comparePerformanceAndAccuracyWithNoise(frequency, "Blow Channel " + channel + " with Noise", 0.1);
        }

        // Run tests across different frequency ranges
        System.out.println("\n=== PERFORMANCE ACROSS FREQUENCY RANGES ===");
        compareAcrossFrequencyRanges();

        // Run tests with complex signals
        System.out.println("\n=== PERFORMANCE WITH COMPLEX SIGNALS ===");
        compareWithComplexSignals();

        // Run overall performance test
        System.out.println("\n=== OVERALL PERFORMANCE ===");
        compareOverallPerformance(harmonica);

        // Print theoretical analysis
        System.out.println("\n=== THEORETICAL ANALYSIS ===");
        printTheoreticalAnalysis();

        System.out.println("\nComparison complete.");
    }

    /**
     * Prints a theoretical analysis of the four pitch detection algorithms.
     */
    private static void printTheoreticalAnalysis() {
        System.out.println("YIN Algorithm:");
        System.out.println("- Based on autocorrelation with cumulative mean normalization");
        System.out.println("- Strengths: Good accuracy for monophonic signals, robust against noise");
        System.out.println("- Weaknesses: Computationally intensive, may struggle with very low frequencies");
        System.out.println("- Best use case: Clean monophonic signals with moderate to high frequencies");
        System.out.println("- Confidence: Based on how close the CMNDF value is to the threshold, higher values indicate more reliable pitch detection");
        System.out.println();

        System.out.println("MPM Algorithm (McLeod Pitch Method):");
        System.out.println("- Based on normalized square difference function (NSDF)");
        System.out.println("- Strengths: Better accuracy than YIN for many cases, good for harmonica frequencies");
        System.out.println("- Weaknesses: Still computationally intensive, may have issues with certain harmonics");
        System.out.println("- Best use case: Monophonic signals with strong fundamental frequencies");
        System.out.println("- Confidence: Directly related to the NSDF value at the detected peak, higher values indicate stronger periodicity");
        System.out.println();

        System.out.println("FFT Algorithm:");
        System.out.println("- Based on Fast Fourier Transform spectral analysis");
        System.out.println("- Strengths: Fast computation, good for identifying multiple frequencies");
        System.out.println("- Weaknesses: Less accurate for low frequencies, sensitive to noise");
        System.out.println("- Best use case: Quick analysis, polyphonic signals, higher frequencies");
        System.out.println("- Confidence: Based on peak prominence in the magnitude spectrum, higher values indicate clearer spectral peaks");
        System.out.println();

        System.out.println("Hybrid Algorithm:");
        System.out.println("- Combines YIN (for low frequencies), MPM (for mid-range), and FFT (for high frequencies)");
        System.out.println("- Strengths: Leverages the best aspects of each algorithm for different frequency ranges");
        System.out.println("- Weaknesses: More complex implementation, potentially higher computational cost due to running multiple algorithms");
        System.out.println("- Best use case: Applications requiring high accuracy across a wide frequency range");
        System.out.println("- Confidence: Combines confidence values from the individual algorithms, potentially providing more reliable results");
        System.out.println("- Note: Uses parallel processing to mitigate performance impact of running multiple algorithms");
        System.out.println();

        System.out.println("For Richter Harmonica in C:");
        System.out.println("- Frequency range: ~261.63 Hz (C4) to ~1046.5 Hz (C6)");
        System.out.println("- YIN and MPM typically provide better accuracy for harmonica notes");
        System.out.println("- FFT is faster but may be less accurate for the lower harmonica notes");
        System.out.println("- Hybrid should provide good accuracy across the entire harmonica range");
        System.out.println("- With noise (common in real-world harmonica playing), YIN often performs best");
        System.out.println("- For real-time applications, FFT might be preferred due to its speed, unless accuracy is critical");
        System.out.println();

        System.out.println("Confidence Comparison:");
        System.out.println("- YIN: Generally provides moderate to high confidence values for clear harmonica tones");
        System.out.println("- MPM: Often gives the highest confidence values for strong fundamental frequencies");
        System.out.println("- FFT: Confidence values vary more with frequency, typically higher for mid to high frequencies");
        System.out.println("- Hybrid: Should provide consistently high confidence values across the frequency range");
        System.out.println("- With noise: All algorithms show reduced confidence, but YIN and MPM typically maintain higher values");
        System.out.println("- For harmonica applications: Consider using confidence thresholds to filter out unreliable detections");
    }

    /**
     * Compares the performance, accuracy, and confidence of YIN, MPM, FFT, and Hybrid for a single frequency.
     *
     * @param frequency   the frequency to test in Hz
     * @param description a description of the test
     */
    private static void comparePerformanceAndAccuracy(double frequency, String description) {
        // Generate a sine wave
        double[] audioData = generateSineWave(frequency, SAMPLE_RATE, DURATION);

        // Measure YIN performance, accuracy, and confidence
        long yinTime = measureYINPerformance(audioData);
        double yinAccuracy = measureYINAccuracy(audioData, frequency);
        double yinConfidence = measureYINConfidence(audioData);

        // Measure MPM performance, accuracy, and confidence
        long mpmTime = measureMPMPerformance(audioData);
        double mpmAccuracy = measureMPMAccuracy(audioData, frequency);
        double mpmConfidence = measureMPMConfidence(audioData);

        // Measure FFT performance, accuracy, and confidence
        long fftTime = measureFFTPerformance(audioData);
        double fftAccuracy = measureFFTAccuracy(audioData, frequency);
        double fftConfidence = measureFFTConfidence(audioData);

        // Measure Hybrid performance, accuracy, and confidence
        long hybridTime = measureHybridPerformance(audioData);
        double hybridAccuracy = measureHybridAccuracy(audioData, frequency);
        double hybridConfidence = measureHybridConfidence(audioData);

        // Print results
        System.out.println(description + " - Frequency: " + frequency + " Hz");
        System.out.println("YIN:    " + yinTime + " ms, Accuracy: " + yinAccuracy + " cents, Confidence: " + yinConfidence);
        System.out.println("MPM:    " + mpmTime + " ms, Accuracy: " + mpmAccuracy + " cents, Confidence: " + mpmConfidence);
        System.out.println("FFT:    " + fftTime + " ms, Accuracy: " + fftAccuracy + " cents, Confidence: " + fftConfidence);
        System.out.println("Hybrid: " + hybridTime + " ms, Accuracy: " + hybridAccuracy + " cents, Confidence: " + hybridConfidence);
        System.out.println();
    }

    /**
     * Compares the performance, accuracy, and confidence of YIN, MPM, FFT, and Hybrid for a single frequency with added noise.
     *
     * @param frequency   the frequency to test in Hz
     * @param description a description of the test
     * @param noiseLevel  the level of noise to add (0.0 to 1.0)
     */
    private static void comparePerformanceAndAccuracyWithNoise(double frequency, String description, double noiseLevel) {
        // Generate a sine wave with noise
        double[] audioData = generateSineWaveWithNoise(frequency, SAMPLE_RATE, DURATION, noiseLevel);

        // Measure YIN performance, accuracy, and confidence
        long yinTime = measureYINPerformance(audioData);
        double yinAccuracy = measureYINAccuracy(audioData, frequency);
        double yinConfidence = measureYINConfidence(audioData);

        // Measure MPM performance, accuracy, and confidence
        long mpmTime = measureMPMPerformance(audioData);
        double mpmAccuracy = measureMPMAccuracy(audioData, frequency);
        double mpmConfidence = measureMPMConfidence(audioData);

        // Measure FFT performance, accuracy, and confidence
        long fftTime = measureFFTPerformance(audioData);
        double fftAccuracy = measureFFTAccuracy(audioData, frequency);
        double fftConfidence = measureFFTConfidence(audioData);

        // Measure Hybrid performance, accuracy, and confidence
        long hybridTime = measureHybridPerformance(audioData);
        double hybridAccuracy = measureHybridAccuracy(audioData, frequency);
        double hybridConfidence = measureHybridConfidence(audioData);

        // Print results
        System.out.println(description + " - Frequency: " + frequency + " Hz");
        System.out.println("YIN:    " + yinTime + " ms, Accuracy: " + yinAccuracy + " cents, Confidence: " + yinConfidence);
        System.out.println("MPM:    " + mpmTime + " ms, Accuracy: " + mpmAccuracy + " cents, Confidence: " + mpmConfidence);
        System.out.println("FFT:    " + fftTime + " ms, Accuracy: " + fftAccuracy + " cents, Confidence: " + fftConfidence);
        System.out.println("Hybrid: " + hybridTime + " ms, Accuracy: " + hybridAccuracy + " cents, Confidence: " + hybridConfidence);
        System.out.println();
    }

    /**
     * Compares the overall performance, accuracy, and confidence of YIN, MPM, FFT, and Hybrid across all harmonica notes.
     *
     * @param harmonica the harmonica to use for generating test frequencies
     */
    private static void compareOverallPerformance(Harmonica harmonica) {
        // Create test cases for all blow and draw notes
        double[][] testCases = new double[20][];
        double[] frequencies = new double[20];
        int index = 0;

        // Add blow notes
        for (int channel = 1; channel <= 10; channel++) {
            double frequency = harmonica.getNoteFrequency(channel, 0);
            frequencies[index] = frequency;
            testCases[index++] = generateSineWave(frequency, SAMPLE_RATE, DURATION);
        }

        // Add draw notes
        for (int channel = 1; channel <= 10; channel++) {
            double frequency = harmonica.getNoteFrequency(channel, 1);
            frequencies[index] = frequency;
            testCases[index++] = generateSineWave(frequency, SAMPLE_RATE, DURATION);
        }

        // Measure total performance for each detector
        long totalYINTime = 0;
        long totalMPMTime = 0;
        long totalFFTTime = 0;
        long totalHybridTime = 0;

        // Measure total accuracy for each detector
        double totalYINAccuracy = 0;
        double totalMPMAccuracy = 0;
        double totalFFTAccuracy = 0;
        double totalHybridAccuracy = 0;

        // Measure total confidence for each detector
        double totalYINConfidence = 0;
        double totalMPMConfidence = 0;
        double totalFFTConfidence = 0;
        double totalHybridConfidence = 0;

        for (int i = 0; i < testCases.length; i++) {
            double[] audioData = testCases[i];
            double frequency = frequencies[i];

            // Measure performance
            totalYINTime += measureYINPerformance(audioData);
            totalMPMTime += measureMPMPerformance(audioData);
            totalFFTTime += measureFFTPerformance(audioData);
            totalHybridTime += measureHybridPerformance(audioData);

            // Measure accuracy
            totalYINAccuracy += measureYINAccuracy(audioData, frequency);
            totalMPMAccuracy += measureMPMAccuracy(audioData, frequency);
            totalFFTAccuracy += measureFFTAccuracy(audioData, frequency);
            totalHybridAccuracy += measureHybridAccuracy(audioData, frequency);

            // Measure confidence
            totalYINConfidence += measureYINConfidence(audioData);
            totalMPMConfidence += measureMPMConfidence(audioData);
            totalFFTConfidence += measureFFTConfidence(audioData);
            totalHybridConfidence += measureHybridConfidence(audioData);
        }

        // Calculate averages
        double avgYINAccuracy = totalYINAccuracy / testCases.length;
        double avgMPMAccuracy = totalMPMAccuracy / testCases.length;
        double avgFFTAccuracy = totalFFTAccuracy / testCases.length;
        double avgHybridAccuracy = totalHybridAccuracy / testCases.length;

        double avgYINConfidence = totalYINConfidence / testCases.length;
        double avgMPMConfidence = totalMPMConfidence / testCases.length;
        double avgFFTConfidence = totalFFTConfidence / testCases.length;
        double avgHybridConfidence = totalHybridConfidence / testCases.length;

        // Print results
        System.out.println("Overall Performance Comparison");
        System.out.println("YIN:    " + totalYINTime + " ms, Avg Accuracy: " + avgYINAccuracy + " cents, Avg Confidence: " + avgYINConfidence);
        System.out.println("MPM:    " + totalMPMTime + " ms, Avg Accuracy: " + avgMPMAccuracy + " cents, Avg Confidence: " + avgMPMConfidence);
        System.out.println("FFT:    " + totalFFTTime + " ms, Avg Accuracy: " + avgFFTAccuracy + " cents, Avg Confidence: " + avgFFTConfidence);
        System.out.println("Hybrid: " + totalHybridTime + " ms, Avg Accuracy: " + avgHybridAccuracy + " cents, Avg Confidence: " + avgHybridConfidence);

        System.out.println("\nPerformance Ratios:");
        System.out.println("Ratio (YIN/MPM):     " + String.format("%.2f", (double) totalYINTime / totalMPMTime));
        System.out.println("Ratio (YIN/FFT):     " + String.format("%.2f", (double) totalYINTime / totalFFTTime));
        System.out.println("Ratio (YIN/Hybrid):  " + String.format("%.2f", (double) totalYINTime / totalHybridTime));
        System.out.println("Ratio (MPM/FFT):     " + String.format("%.2f", (double) totalMPMTime / totalFFTTime));
        System.out.println("Ratio (MPM/Hybrid):  " + String.format("%.2f", (double) totalMPMTime / totalHybridTime));
        System.out.println("Ratio (FFT/Hybrid):  " + String.format("%.2f", (double) totalFFTTime / totalHybridTime));
        System.out.println();
    }

    /**
     * Measures the execution time of YIN for the given audio data.
     *
     * @param audioData the audio data to analyze
     * @return the execution time in milliseconds
     */
    private static long measureYINPerformance(double[] audioData) {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < ITERATIONS; i++) {
            PitchDetector.detectPitchYIN(audioData, SAMPLE_RATE);
        }

        long endTime = System.currentTimeMillis();
        return (endTime - startTime);
    }

    /**
     * Measures the execution time of MPM for the given audio data.
     *
     * @param audioData the audio data to analyze
     * @return the execution time in milliseconds
     */
    private static long measureMPMPerformance(double[] audioData) {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < ITERATIONS; i++) {
            PitchDetector.detectPitchMPM(audioData, SAMPLE_RATE);
        }

        long endTime = System.currentTimeMillis();
        return (endTime - startTime);
    }

    /**
     * Measures the execution time of FFT for the given audio data.
     *
     * @param audioData the audio data to analyze
     * @return the execution time in milliseconds
     */
    private static long measureFFTPerformance(double[] audioData) {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < ITERATIONS; i++) {
            PitchDetector.detectPitchFFT(audioData, SAMPLE_RATE);
        }

        long endTime = System.currentTimeMillis();
        return (endTime - startTime);
    }

    /**
     * Measures the accuracy of YIN for the given audio data.
     *
     * @param audioData         the audio data to analyze
     * @param expectedFrequency the expected frequency in Hz
     * @return the accuracy in cents (difference between detected and expected frequency)
     */
    private static double measureYINAccuracy(double[] audioData, double expectedFrequency) {
        PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchYIN(audioData, SAMPLE_RATE);
        if (result.pitch() == PitchDetector.NO_DETECTED_PITCH) {
            return Double.MAX_VALUE; // Could not detect pitch
        }
        return Math.abs(NoteUtils.getCents(expectedFrequency, result.pitch()));
    }

    /**
     * Measures the confidence of YIN for the given audio data.
     *
     * @param audioData the audio data to analyze
     * @return the confidence value (0.0 to 1.0)
     */
    private static double measureYINConfidence(double[] audioData) {
        PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchYIN(audioData, SAMPLE_RATE);
        if (result.pitch() == PitchDetector.NO_DETECTED_PITCH) {
            return 0.0; // Could not detect pitch
        }
        return result.confidence();
    }

    /**
     * Measures the accuracy of MPM for the given audio data.
     *
     * @param audioData         the audio data to analyze
     * @param expectedFrequency the expected frequency in Hz
     * @return the accuracy in cents (difference between detected and expected frequency)
     */
    private static double measureMPMAccuracy(double[] audioData, double expectedFrequency) {
        PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, SAMPLE_RATE);
        if (result.pitch() == PitchDetector.NO_DETECTED_PITCH) {
            return Double.MAX_VALUE; // Could not detect pitch
        }
        return Math.abs(NoteUtils.getCents(expectedFrequency, result.pitch()));
    }

    /**
     * Measures the confidence of MPM for the given audio data.
     *
     * @param audioData the audio data to analyze
     * @return the confidence value (0.0 to 1.0)
     */
    private static double measureMPMConfidence(double[] audioData) {
        PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchMPM(audioData, SAMPLE_RATE);
        if (result.pitch() == PitchDetector.NO_DETECTED_PITCH) {
            return 0.0; // Could not detect pitch
        }
        return result.confidence();
    }

    /**
     * Measures the accuracy of FFT for the given audio data.
     *
     * @param audioData         the audio data to analyze
     * @param expectedFrequency the expected frequency in Hz
     * @return the accuracy in cents (difference between detected and expected frequency)
     */
    private static double measureFFTAccuracy(double[] audioData, double expectedFrequency) {
        PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchFFT(audioData, SAMPLE_RATE);
        if (result.pitch() == PitchDetector.NO_DETECTED_PITCH) {
            return Double.MAX_VALUE; // Could not detect pitch
        }
        return Math.abs(NoteUtils.getCents(expectedFrequency, result.pitch()));
    }

    /**
     * Measures the confidence of FFT for the given audio data.
     *
     * @param audioData the audio data to analyze
     * @return the confidence value (0.0 to 1.0)
     */
    private static double measureFFTConfidence(double[] audioData) {
        PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchFFT(audioData, SAMPLE_RATE);
        if (result.pitch() == PitchDetector.NO_DETECTED_PITCH) {
            return 0.0; // Could not detect pitch
        }
        return result.confidence();
    }

    /**
     * Measures the execution time of Hybrid for the given audio data.
     *
     * @param audioData the audio data to analyze
     * @return the execution time in milliseconds
     */
    private static long measureHybridPerformance(double[] audioData) {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < ITERATIONS; i++) {
            PitchDetector.detectPitchHybrid(audioData, SAMPLE_RATE);
        }

        long endTime = System.currentTimeMillis();
        return (endTime - startTime);
    }

    /**
     * Measures the accuracy of Hybrid for the given audio data.
     *
     * @param audioData         the audio data to analyze
     * @param expectedFrequency the expected frequency in Hz
     * @return the accuracy in cents (difference between detected and expected frequency)
     */
    private static double measureHybridAccuracy(double[] audioData, double expectedFrequency) {
        PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchHybrid(audioData, SAMPLE_RATE);
        if (result.pitch() == PitchDetector.NO_DETECTED_PITCH) {
            return Double.MAX_VALUE; // Could not detect pitch
        }
        return Math.abs(NoteUtils.getCents(expectedFrequency, result.pitch()));
    }

    /**
     * Measures the confidence of Hybrid for the given audio data.
     *
     * @param audioData the audio data to analyze
     * @return the confidence value (0.0 to 1.0)
     */
    private static double measureHybridConfidence(double[] audioData) {
        PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchHybrid(audioData, SAMPLE_RATE);
        if (result.pitch() == PitchDetector.NO_DETECTED_PITCH) {
            return 0.0; // Could not detect pitch
        }
        return result.confidence();
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
     * Generates a sine wave with added white noise.
     *
     * @param frequency  the frequency of the sine wave in Hertz
     * @param sampleRate the number of samples per second
     * @param duration   the duration of the sine wave in seconds
     * @param noiseLevel the level of noise to add (0.0 to 1.0)
     * @return an array of doubles representing the generated sine wave with noise
     */
    private static double[] generateSineWaveWithNoise(double frequency, int sampleRate, double duration, double noiseLevel) {
        int samples = (int) (sampleRate * duration);
        double[] sineWave = new double[samples];
        for (int i = 0; i < samples; i++) {
            double signal = Math.sin(2 * Math.PI * frequency * i / sampleRate);
            double noise = (Math.random() * 2 - 1) * noiseLevel;
            sineWave[i] = signal + noise;
        }
        return sineWave;
    }

    /**
     * Generates a sine wave with harmonics.
     *
     * @param fundamentalFreq    the fundamental frequency in Hertz
     * @param sampleRate         the number of samples per second
     * @param duration           the duration of the sine wave in seconds
     * @param harmonicAmplitudes array of amplitudes for each harmonic (index 0 is fundamental)
     * @return an array of doubles representing the generated complex wave
     */
    private static double[] generateSineWaveWithHarmonics(double fundamentalFreq, int sampleRate, double duration, double[] harmonicAmplitudes) {
        int samples = (int) (sampleRate * duration);
        double[] complexWave = new double[samples];

        for (int i = 0; i < samples; i++) {
            double t = (double) i / sampleRate;
            double sample = 0;

            for (int h = 0; h < harmonicAmplitudes.length; h++) {
                double freq = fundamentalFreq * (h + 1);
                sample += harmonicAmplitudes[h] * Math.sin(2 * Math.PI * freq * t);
            }

            complexWave[i] = sample;
        }

        return complexWave;
    }

    /**
     * Compares the performance, accuracy, and confidence of all detectors across different frequency ranges.
     * This method tests low, medium, and high frequencies to evaluate how each detector performs
     * across the frequency spectrum.
     */
    private static void compareAcrossFrequencyRanges() {
        // Define frequency ranges
        double[] lowFreqs = {80, 100, 150, 200, 250};
        double[] midFreqs = {300, 400, 500, 600, 700};
        double[] highFreqs = {800, 1000, 1500, 2000, 3000};

        // Test low frequencies
        System.out.println("Low Frequencies (80-250 Hz):");
        for (double freq : lowFreqs) {
            comparePerformanceAndAccuracy(freq, "Frequency " + freq + " Hz");
        }

        // Calculate and print averages for low frequencies
        printFrequencyRangeAverages(lowFreqs, "Low Frequency Range (80-250 Hz)");

        // Test mid frequencies
        System.out.println("\nMid Frequencies (300-700 Hz):");
        for (double freq : midFreqs) {
            comparePerformanceAndAccuracy(freq, "Frequency " + freq + " Hz");
        }

        // Calculate and print averages for mid frequencies
        printFrequencyRangeAverages(midFreqs, "Mid Frequency Range (300-700 Hz)");

        // Test high frequencies
        System.out.println("\nHigh Frequencies (800-3000 Hz):");
        for (double freq : highFreqs) {
            comparePerformanceAndAccuracy(freq, "Frequency " + freq + " Hz");
        }

        // Calculate and print averages for high frequencies
        printFrequencyRangeAverages(highFreqs, "High Frequency Range (800-3000 Hz)");
    }

    /**
     * Calculates and prints average performance, accuracy, and confidence for a frequency range.
     *
     * @param frequencies array of frequencies in the range
     * @param description description of the frequency range
     */
    private static void printFrequencyRangeAverages(double[] frequencies, String description) {
        // Arrays to store performance, accuracy, and confidence for each detector
        long[] yinTimes = new long[frequencies.length];
        long[] mpmTimes = new long[frequencies.length];
        long[] fftTimes = new long[frequencies.length];
        long[] hybridTimes = new long[frequencies.length];

        double[] yinAccuracies = new double[frequencies.length];
        double[] mpmAccuracies = new double[frequencies.length];
        double[] fftAccuracies = new double[frequencies.length];
        double[] hybridAccuracies = new double[frequencies.length];

        double[] yinConfidences = new double[frequencies.length];
        double[] mpmConfidences = new double[frequencies.length];
        double[] fftConfidences = new double[frequencies.length];
        double[] hybridConfidences = new double[frequencies.length];

        // Measure performance, accuracy, and confidence for each frequency
        for (int i = 0; i < frequencies.length; i++) {
            double freq = frequencies[i];
            double[] audioData = generateSineWave(freq, SAMPLE_RATE, DURATION);

            // Measure performance
            yinTimes[i] = measureYINPerformance(audioData);
            mpmTimes[i] = measureMPMPerformance(audioData);
            fftTimes[i] = measureFFTPerformance(audioData);
            hybridTimes[i] = measureHybridPerformance(audioData);

            // Measure accuracy
            yinAccuracies[i] = measureYINAccuracy(audioData, freq);
            mpmAccuracies[i] = measureMPMAccuracy(audioData, freq);
            fftAccuracies[i] = measureFFTAccuracy(audioData, freq);
            hybridAccuracies[i] = measureHybridAccuracy(audioData, freq);

            // Measure confidence
            yinConfidences[i] = measureYINConfidence(audioData);
            mpmConfidences[i] = measureMPMConfidence(audioData);
            fftConfidences[i] = measureFFTConfidence(audioData);
            hybridConfidences[i] = measureHybridConfidence(audioData);
        }

        // Calculate averages
        long avgYinTime = calculateAverage(yinTimes);
        long avgMpmTime = calculateAverage(mpmTimes);
        long avgFftTime = calculateAverage(fftTimes);
        long avgHybridTime = calculateAverage(hybridTimes);

        double avgYinAccuracy = calculateAverage(yinAccuracies);
        double avgMpmAccuracy = calculateAverage(mpmAccuracies);
        double avgFftAccuracy = calculateAverage(fftAccuracies);
        double avgHybridAccuracy = calculateAverage(hybridAccuracies);

        double avgYinConfidence = calculateAverage(yinConfidences);
        double avgMpmConfidence = calculateAverage(mpmConfidences);
        double avgFftConfidence = calculateAverage(fftConfidences);
        double avgHybridConfidence = calculateAverage(hybridConfidences);

        // Print averages
        System.out.println("\nAverage Results for " + description + ":");
        System.out.println("YIN:    " + avgYinTime + " ms, Accuracy: " + avgYinAccuracy + " cents, Confidence: " + avgYinConfidence);
        System.out.println("MPM:    " + avgMpmTime + " ms, Accuracy: " + avgMpmAccuracy + " cents, Confidence: " + avgMpmConfidence);
        System.out.println("FFT:    " + avgFftTime + " ms, Accuracy: " + avgFftAccuracy + " cents, Confidence: " + avgFftConfidence);
        System.out.println("Hybrid: " + avgHybridTime + " ms, Accuracy: " + avgHybridAccuracy + " cents, Confidence: " + avgHybridConfidence);
        System.out.println();
    }

    /**
     * Calculates the average of an array of long values.
     *
     * @param values array of long values
     * @return the average as a long
     */
    private static long calculateAverage(long[] values) {
        long sum = 0;
        for (long value : values) {
            sum += value;
        }
        return sum / values.length;
    }

    /**
     * Calculates the average of an array of double values.
     *
     * @param values array of double values
     * @return the average as a double
     */
    private static double calculateAverage(double[] values) {
        double sum = 0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.length;
    }

    /**
     * Compares the performance, accuracy, and confidence of all detectors with complex signals.
     * This method tests signals with harmonics, varying amplitudes, and other complex characteristics
     * to evaluate how each detector performs with more realistic audio signals.
     */
    private static void compareWithComplexSignals() {
        // Test with harmonics
        System.out.println("Signals with Harmonics:");
        compareWithHarmonics();

        // Test with varying noise levels
        System.out.println("\nSignals with Varying Noise Levels:");
        compareWithVaryingNoiseLevels();
    }

    /**
     * Compares the performance, accuracy, and confidence of all detectors with signals containing harmonics.
     */
    private static void compareWithHarmonics() {
        double fundamentalFreq = 440.0; // A4

        // Test with different harmonic profiles

        // Strong fundamental, weak harmonics (typical of flute)
        double[] fluteProfile = {1.0, 0.3, 0.1, 0.05, 0.02};
        double[] audioData = generateSineWaveWithHarmonics(fundamentalFreq, SAMPLE_RATE, DURATION, fluteProfile);
        System.out.println("Strong fundamental, weak harmonics (flute-like):");
        compareAllDetectors(audioData, fundamentalFreq, "Flute-like");

        // Moderate fundamental, strong even harmonics (clarinet-like)
        double[] clarinetProfile = {1.0, 0.1, 0.7, 0.05, 0.3};
        audioData = generateSineWaveWithHarmonics(fundamentalFreq, SAMPLE_RATE, DURATION, clarinetProfile);
        System.out.println("\nModerate fundamental, strong even harmonics (clarinet-like):");
        compareAllDetectors(audioData, fundamentalFreq, "Clarinet-like");

        // Moderate fundamental, strong odd harmonics (saxophone-like)
        double[] saxProfile = {1.0, 0.6, 0.2, 0.5, 0.1};
        audioData = generateSineWaveWithHarmonics(fundamentalFreq, SAMPLE_RATE, DURATION, saxProfile);
        System.out.println("\nModerate fundamental, strong odd harmonics (saxophone-like):");
        compareAllDetectors(audioData, fundamentalFreq, "Saxophone-like");

        // Weak fundamental, strong harmonics (harmonica-like)
        double[] harmonicaProfile = {0.5, 0.8, 0.7, 0.6, 0.4};
        audioData = generateSineWaveWithHarmonics(fundamentalFreq, SAMPLE_RATE, DURATION, harmonicaProfile);
        System.out.println("\nWeak fundamental, strong harmonics (harmonica-like):");
        compareAllDetectors(audioData, fundamentalFreq, "Harmonica-like");
    }

    /**
     * Compares the performance, accuracy, and confidence of all detectors with signals containing varying noise levels.
     */
    private static void compareWithVaryingNoiseLevels() {
        double frequency = 440.0; // A4

        // Test with different noise levels
        double[] noiseLevels = {0.01, 0.05, 0.1, 0.2, 0.3};

        for (double noiseLevel : noiseLevels) {
            double[] audioData = generateSineWaveWithNoise(frequency, SAMPLE_RATE, DURATION, noiseLevel);
            System.out.println("Noise Level: " + noiseLevel + " (10% = moderate noise, 30% = heavy noise)");
            compareAllDetectors(audioData, frequency, "Noise Level " + noiseLevel);
            System.out.println();
        }
    }

    /**
     * Compares all detectors on the same audio data and prints the results.
     *
     * @param audioData         the audio data to analyze
     * @param expectedFrequency the expected frequency in Hz
     * @param description       a description of the test
     */
    private static void compareAllDetectors(double[] audioData, double expectedFrequency, String description) {
        // Measure YIN performance, accuracy, and confidence
        long yinTime = measureYINPerformance(audioData);
        double yinAccuracy = measureYINAccuracy(audioData, expectedFrequency);
        double yinConfidence = measureYINConfidence(audioData);

        // Measure MPM performance, accuracy, and confidence
        long mpmTime = measureMPMPerformance(audioData);
        double mpmAccuracy = measureMPMAccuracy(audioData, expectedFrequency);
        double mpmConfidence = measureMPMConfidence(audioData);

        // Measure FFT performance, accuracy, and confidence
        long fftTime = measureFFTPerformance(audioData);
        double fftAccuracy = measureFFTAccuracy(audioData, expectedFrequency);
        double fftConfidence = measureFFTConfidence(audioData);

        // Measure Hybrid performance, accuracy, and confidence
        long hybridTime = measureHybridPerformance(audioData);
        double hybridAccuracy = measureHybridAccuracy(audioData, expectedFrequency);
        double hybridConfidence = measureHybridConfidence(audioData);

        // Print results
        System.out.println("YIN:    " + yinTime + " ms, Accuracy: " + yinAccuracy + " cents, Confidence: " + yinConfidence);
        System.out.println("MPM:    " + mpmTime + " ms, Accuracy: " + mpmAccuracy + " cents, Confidence: " + mpmConfidence);
        System.out.println("FFT:    " + fftTime + " ms, Accuracy: " + fftAccuracy + " cents, Confidence: " + fftConfidence);
        System.out.println("Hybrid: " + hybridTime + " ms, Accuracy: " + hybridAccuracy + " cents, Confidence: " + hybridConfidence);
    }
}
