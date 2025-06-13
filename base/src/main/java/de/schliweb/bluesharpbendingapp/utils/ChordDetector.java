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

import org.apache.commons.math3.complex.Complex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Implementation of a spectral-based algorithm for chord detection.
 * <p>
 * This class provides a modern approach to detect multiple pitches (chords)
 * in an audio signal using spectral analysis. It extends the PitchDetector
 * class and overrides the detectPitch method to return multiple pitches.
 * <p>
 * The algorithm works by:
 * 1. Applying a window function to the audio data
 * 2. Computing the FFT to get the frequency spectrum
 * 3. Finding peaks in the spectrum that correspond to pitches
 * 4. Filtering and refining the detected pitches
 */
public class ChordDetector extends PitchDetector {

    /**
     * The minimum amplitude threshold for peak detection in the spectrum.
     * Peaks with amplitudes below this threshold will not be considered.
     */
    private static final double PEAK_THRESHOLD = 0.05;

    /**
     * The minimum distance between peaks in Hz.
     * Peaks that are closer than this distance will be merged.
     */
    private static final double MIN_PEAK_DISTANCE_HZ = 25.0;

    /**
     * The maximum number of pitches to detect.
     */
    private static final int MAX_PITCHES = 4;

    /**
     * The threshold for spectral flatness to distinguish between tonal sounds and noise.
     * Values above this threshold indicate noise.
     */
    private static final double SPECTRAL_FLATNESS_THRESHOLD = 0.4;

    /**
     * Represents the tolerance level used to identify and filter out harmonic frequencies
     * in the pitch detection process within audio signals.
     * <p>
     * This constant defines the threshold ratio within which frequencies are considered
     * close enough to be treated as harmonics of a fundamental frequency.
     * <p>
     * A lower value increases the precision of harmonic filtering, potentially excluding
     * frequencies that are slightly off harmonic intervals. A higher value allows for more
     * leniency, which can help account for slight inharmonicities or measurement errors.
     * <p>
     * Typical use cases include:
     * - Distinguishing between fundamental frequencies and their harmonics.
     * - Improving the accuracy of chord and pitch detection by suppressing redundant harmonic peaks.
     */
    private static final double HARMONIC_TOLERANCE = 0.05;

    /**
     * Default constructor for the ChordDetector class.
     * Initializes a new instance of the chord detection algorithm.
     */
    protected ChordDetector() {
        super();
        LoggingContext.setComponent("ChordDetector");
        LoggingUtils.logDebug("Initializing ChordDetector");
    }

    /**
     * Detects multiple pitches (chord) in an audio signal and returns them as a ChordDetectionResult.
     * This method is not used directly but is called by the detectChord method.
     *
     * @param audioData  an array of double values representing the audio signal.
     * @param sampleRate the sample rate of the audio signal in Hz.
     * @return a PitchDetectionResult containing the dominant pitch and confidence.
     */
    @Override
    PitchDetectionResult detectPitch(double[] audioData, int sampleRate) {
        LoggingUtils.logDebug("Detecting pitch using ChordDetector");

        // For backward compatibility, return the dominant pitch
        ChordDetectionResult chordResult = detectChordInternal(audioData, sampleRate);

        if (chordResult.hasPitches()) {
            double pitch = chordResult.getPitch(0);
            double confidence = chordResult.confidence();
            LoggingUtils.logDebug("Detected dominant pitch", String.format("%.2f Hz with confidence %.2f", pitch, confidence));
            return new PitchDetectionResult(pitch, confidence);
        }

        LoggingUtils.logDebug("No pitch detected");
        return new PitchDetectionResult(NO_DETECTED_PITCH, 0.0);
    }

    /**
     * Detects multiple pitches (chord) in an audio signal.
     *
     * @param audioData  an array of double values representing the audio signal.
     * @param sampleRate the sample rate of the audio signal in Hz.
     * @return a ChordDetectionResult containing the detected pitches and confidence.
     */
    public ChordDetectionResult detectChordInternal(double[] audioData, int sampleRate) {
        LoggingUtils.logDebug("Detecting chord using spectral analysis", "Sample rate: " + sampleRate + " Hz, data length: " + audioData.length);
        LoggingUtils.logOperationStarted("Chord detection");

        // Prepare for FFT (needs power of 2 size)
        int fftSize = Math.max(1024, nextPowerOfTwo(audioData.length));
        LoggingUtils.logDebug("FFT size", String.valueOf(fftSize));

        Complex[] complexInput = prepareFFTInput(audioData, fftSize);
        double[] fftOutput = performFFT(complexInput, fftSize);

        double[] magnitudeSpectrum = calculateMagnitudeSpectrum(fftOutput, fftSize);

        // Calculate spectral flatness to distinguish between tonal sounds and noise
        double spectralFlatness = calculateSpectralFlatness(magnitudeSpectrum, sampleRate);
        LoggingUtils.logDebug("Spectral flatness", String.format("%.4f (threshold: %.4f)", spectralFlatness, SPECTRAL_FLATNESS_THRESHOLD));

        // If the spectral flatness is high, it's likely noise
        if (spectralFlatness > SPECTRAL_FLATNESS_THRESHOLD) {
            LoggingUtils.logDebug("Audio signal classified as noise", "Spectral flatness above threshold, returning empty result");
            LoggingUtils.logOperationCompleted("Chord detection (noise detected)");
            return new ChordDetectionResult(List.of(), 0.0);
        }

        // Normalize the spectrum
        double maxMagnitude = Arrays.stream(magnitudeSpectrum).max().orElse(1.0);
        for (int i = 0; i < magnitudeSpectrum.length; i++) {
            magnitudeSpectrum[i] /= maxMagnitude;
        }

        // Find peaks in the spectrum
        List<Peak> peaks = PitchDetector.findPeaks(magnitudeSpectrum, sampleRate, fftSize, PEAK_THRESHOLD);
        LoggingUtils.logDebug("Initial peaks found", String.valueOf(peaks.size()));

        // Filter peaks based on frequency range and threshold
        peaks = filterPeaks(peaks);
        LoggingUtils.logDebug("Peaks after frequency filtering", String.valueOf(peaks.size()));

        // Filter harmonics to avoid overtones and prioritize fundamental frequencies
        peaks = filterHarmonics(peaks);
        LoggingUtils.logDebug("Peaks after harmonic filtering", String.valueOf(peaks.size()));

        // Prioritize lower frequencies over higher harmonics
        peaks = prioritizeLowerFrequencies(peaks);
        LoggingUtils.logDebug("Peaks after prioritization", String.valueOf(peaks.size()));

        // Merge peaks that are too close
        peaks = mergePeaks(peaks);
        LoggingUtils.logDebug("Peaks after merging", String.valueOf(peaks.size()));

        // Limit the number of peaks
        if (peaks.size() > MAX_PITCHES) {
            peaks = peaks.subList(0, MAX_PITCHES);
        }

        // Extract pitches from peaks
        double[] pitches = new double[peaks.size()];
        for (int i = 0; i < peaks.size(); i++) {
            pitches[i] = peaks.get(i).frequency;
        }

        // Calculate confidence based on the strength of the peaks
        double confidence = peaks.isEmpty() ? 0.0 : peaks.stream().mapToDouble(p -> p.magnitude).sum() / peaks.size();

        // Create the result
        ChordDetectionResult result = ChordDetectionResult.of(pitches, confidence);

        // Log the result
        if (result.hasPitches()) {
            StringBuilder pitchesStr = new StringBuilder();
            for (int i = 0; i < result.getPitchCount(); i++) {
                if (i > 0) pitchesStr.append(", ");
                pitchesStr.append(String.format("%.2f Hz", result.getPitch(i)));
            }
            LoggingUtils.logDebug("Detected chord", String.format("%d pitches [%s] with confidence %.2f", result.getPitchCount(), pitchesStr, confidence));
        } else {
            LoggingUtils.logDebug("No chord detected", "Confidence: " + confidence);
        }

        LoggingUtils.logOperationCompleted("Chord detection");
        return result;
    }

    /**
     * Filters peaks based on frequency range and threshold.
     *
     * @param peaks the list of peaks to filter
     * @return the filtered list of peaks
     */
    private List<Peak> filterPeaks(List<Peak> peaks) {
        List<Peak> filteredPeaks = new ArrayList<>();

        for (Peak peak : peaks) {
            // Check if the peak is within the frequency range
            if (peak.frequency >= minFrequency && peak.frequency <= maxFrequency) {
                filteredPeaks.add(peak);
            }
        }

        return filteredPeaks;
    }

    /**
     * Merges peaks that are too close to each other.
     *
     * @param peaks the list of peaks to merge
     * @return the merged list of peaks
     */
    private List<Peak> mergePeaks(List<Peak> peaks) {
        if (peaks.isEmpty()) {
            return peaks;
        }

        List<Peak> mergedPeaks = new ArrayList<>();
        Peak currentPeak = peaks.get(0);

        for (int i = 1; i < peaks.size(); i++) {
            Peak nextPeak = peaks.get(i);

            // Check if the peaks are too close
            if (Math.abs(nextPeak.frequency - currentPeak.frequency) < MIN_PEAK_DISTANCE_HZ) {
                // Merge the peaks (weighted average based on magnitude)
                double totalMagnitude = currentPeak.magnitude + nextPeak.magnitude;
                double mergedFrequency = (currentPeak.frequency * currentPeak.magnitude + nextPeak.frequency * nextPeak.magnitude) / totalMagnitude;
                currentPeak = new Peak(mergedFrequency, totalMagnitude);
            } else {
                // Add the current peak and move to the next one
                mergedPeaks.add(currentPeak);
                currentPeak = nextPeak;
            }
        }

        // Add the last peak
        mergedPeaks.add(currentPeak);

        return mergedPeaks;
    }

    /**
     * Calculates the spectral flatness of a magnitude spectrum.
     * Spectral flatness is the ratio of the geometric mean to the arithmetic mean
     * of the spectrum, and is a good measure of how "noisy" a sound is.
     * Values close to 0 indicate tonal sounds, while values close to 1 indicate noise.
     *
     * @param magnitudeSpectrum the magnitude spectrum to calculate flatness for
     * @param sampleRate        the sample rate of the audio signal in Hz
     * @return the spectral flatness value between 0 and 1
     */
    private double calculateSpectralFlatness(double[] magnitudeSpectrum, int sampleRate) {
        // Skip the first few bins (DC and very low frequencies)
        int startBin = Math.max(1, (int) (minFrequency * magnitudeSpectrum.length / (sampleRate / 2)));
        int endBin = Math.min(magnitudeSpectrum.length - 1, (int) (maxFrequency * magnitudeSpectrum.length / (sampleRate / 2)));

        // Use parallel streams to calculate sums
        int count = endBin - startBin + 1;

        // Calculate arithmetic mean and geometric mean in parallel
        double[] sums = IntStream.rangeClosed(startBin, endBin).parallel().mapToDouble(i -> {
            // Add a small value to avoid log(0)
            double value = magnitudeSpectrum[i] + 1e-10;
            return value;
        }).collect(() -> new double[2],  // Initialize an array to hold sum and logSum
                (acc, value) -> {
                    acc[0] += value;          // sum
                    acc[1] += Math.log(value); // logSum
                }, (acc1, acc2) -> {
                    acc1[0] += acc2[0];  // Combine sums
                    acc1[1] += acc2[1];  // Combine logSums
                });

        double sum = sums[0];
        double logSum = sums[1];

        if (count == 0 || sum == 0) {
            return 1.0; // Maximum flatness (noise)
        }

        double arithmeticMean = sum / count;
        double geometricMean = Math.exp(logSum / count);

        // Calculate spectral flatness
        return geometricMean / arithmeticMean;
    }

    /**
     * Filters out harmonic frequencies by comparing the ratios of detected frequencies.
     * <p>
     * This method checks if a frequency is a harmonic (integer multiple) of another frequency
     * and filters those harmonics out, prioritizing the lower fundamental frequencies.
     * Harmonics are identified by their frequency ratios (integer multiples).
     *
     * @param peaks the list of detected peaks to filter
     * @return the filtered list of peaks (without harmonics)
     */
    private List<Peak> filterHarmonics(List<Peak> peaks) {
        List<Peak> filteredPeaks = new ArrayList<>();

        for (int i = 0; i < peaks.size(); i++) {
            boolean isHarmonic = false;

            for (int j = 0; j < i; j++) {
                double ratio = peaks.get(i).frequency / peaks.get(j).frequency;

                // If the peak frequency is within a harmonic series, filter it out
                // Special case for octaves (ratio ~2.0): don't filter them out
                if (Math.abs(ratio - 2.0) < 0.1) {
                    // Allow octaves to pass through
                    continue;
                }
                if (Math.abs(ratio - Math.round(ratio)) < HARMONIC_TOLERANCE) {  // Adjusted harmonic threshold
                    if (ratio > 5.0) { // Unrealistic harmonic
                        continue; // Accept it as a separate tone
                    }
                    if (peaks.get(i).magnitude < peaks.get(j).magnitude * 0.3) { // Adjusted amplitude ratio to suppress harmonics
                        isHarmonic = true;
                        break;
                    }
                }
            }

            if (!isHarmonic) {
                filteredPeaks.add(peaks.get(i));
            }
        }

        return filteredPeaks;
    }

    /**
     * Prioritizes lower frequencies by comparing relative amplitudes of peaks.
     * This ensures that higher harmonics are deprioritized if similar strength exists at lower frequencies.
     *
     * @param peaks the list of detected peaks to prioritize
     * @return the ordered and prioritized list of peaks
     */
    private List<Peak> prioritizeLowerFrequencies(List<Peak> peaks) {
        peaks.sort((p1, p2) -> Double.compare(p1.frequency, p2.frequency)); // Sort by frequency (ascending)

        List<Peak> prioritizedPeaks = new ArrayList<>();
        for (Peak peak : peaks) {
            boolean isOverridden = false;

            for (Peak lowerPeak : prioritizedPeaks) {
                if (peak.frequency > lowerPeak.frequency && peak.magnitude < lowerPeak.magnitude * 0.6) { // Adjusted amplitude ratio (factor 0.6)
                    isOverridden = true;
                    break;
                }
            }

            if (!isOverridden) {
                prioritizedPeaks.add(peak);
            }
        }

        return prioritizedPeaks;
    }
}
