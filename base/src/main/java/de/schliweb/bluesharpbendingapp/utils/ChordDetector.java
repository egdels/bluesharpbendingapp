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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
     *
     * This constant defines the threshold ratio within which frequencies are considered
     * close enough to be treated as harmonics of a fundamental frequency.
     *
     * A lower value increases the precision of harmonic filtering, potentially excluding
     * frequencies that are slightly off harmonic intervals. A higher value allows for more
     * leniency, which can help account for slight inharmonicities or measurement errors.
     *
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
        // For backward compatibility, return the dominant pitch
        ChordDetectionResult chordResult = detectChordInternal(audioData, sampleRate);
        if (chordResult.hasPitches()) {
            return new PitchDetectionResult(chordResult.getPitch(0), chordResult.confidence());
        }
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

        // Prepare for FFT (needs power of 2 size)
        int fftSize = nextPowerOfTwo(audioData.length);
        double[] fftInput = new double[fftSize * 2]; // Complex numbers (real, imag)

        // Apply window function and prepare FFT input
        for (int i = 0; i < audioData.length; i++) {
            fftInput[i * 2] = audioData[i] * hannWindow(i, audioData.length);
            fftInput[i * 2 + 1] = 0; // Imaginary part is zero
        }

        // Perform FFT
        fft(fftInput, fftSize);

        // Calculate magnitude spectrum
        double[] magnitudeSpectrum = new double[fftSize / 2];
        for (int i = 0; i < fftSize / 2; i++) {
            double real = fftInput[i * 2];
            double imag = fftInput[i * 2 + 1];
            magnitudeSpectrum[i] = Math.sqrt(real * real + imag * imag);
        }

        // Calculate spectral flatness to distinguish between tonal sounds and noise
        double spectralFlatness = calculateSpectralFlatness(magnitudeSpectrum, sampleRate);

        // If the spectral flatness is high, it's likely noise
        if (spectralFlatness > SPECTRAL_FLATNESS_THRESHOLD) {
            return new ChordDetectionResult(List.of(), 0.0);
        }

        // Normalize the spectrum
        double maxMagnitude = Arrays.stream(magnitudeSpectrum).max().orElse(1.0);
        for (int i = 0; i < magnitudeSpectrum.length; i++) {
            magnitudeSpectrum[i] /= maxMagnitude;
        }

        // Find peaks in the spectrum
        List<Peak> peaks = findPeaks(magnitudeSpectrum, sampleRate, fftSize);

        // Filter peaks based on frequency range and threshold
        peaks = filterPeaks(peaks);

        // [NEU] Filter harmonics to avoid overtones and prioritize fundamental frequencies
        peaks = filterHarmonics(peaks);

// [NEU] Prioritize lower frequencies over higher harmonics
        peaks = prioritizeLowerFrequencies(peaks);


        // Merge peaks that are too close
        peaks = mergePeaks(peaks);

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
        double confidence = peaks.isEmpty() ? 0.0 :
                            peaks.stream().mapToDouble(p -> p.magnitude).sum() / peaks.size();

        return ChordDetectionResult.of(pitches, confidence);
    }

    /**
     * Finds peaks in the magnitude spectrum that correspond to pitches.
     *
     * @param magnitudeSpectrum the magnitude spectrum from FFT
     * @param sampleRate        the sample rate of the audio signal in Hz
     * @param fftSize           the size of the FFT
     * @return a list of peaks found in the spectrum
     */
    private List<Peak> findPeaks(double[] magnitudeSpectrum, int sampleRate, int fftSize) {
        List<Peak> peaks = new ArrayList<>();

        // Skip the first few bins (DC and very low frequencies)
        int startBin = Math.max(1, (int) (minFrequency * fftSize / sampleRate));
        int endBin = Math.min(magnitudeSpectrum.length - 1, (int) (maxFrequency * fftSize / sampleRate));

        for (int i = startBin + 1; i < endBin - 1; i++) {
            // Check if this is a local maximum
            if (magnitudeSpectrum[i] > magnitudeSpectrum[i - 1] &&
                magnitudeSpectrum[i] > magnitudeSpectrum[i + 1] &&
                magnitudeSpectrum[i] > PEAK_THRESHOLD) {

                // Refine the peak position using parabolic interpolation
                double refinedBin = parabolicInterpolation(magnitudeSpectrum, i);
                double frequency = refinedBin * sampleRate / fftSize;

                // Add the peak to the list
                peaks.add(new Peak(frequency, magnitudeSpectrum[i]));
            }
        }

        // Sort peaks by magnitude (descending)
        peaks.sort((p1, p2) -> Double.compare(p2.magnitude, p1.magnitude));

        return peaks;
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
                double mergedFrequency = (currentPeak.frequency * currentPeak.magnitude +
                                         nextPeak.frequency * nextPeak.magnitude) / totalMagnitude;
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
     * Finds the next power of two greater than or equal to the given number.
     *
     * @param n the number to find the next power of two for
     * @return the next power of two
     */
    private int nextPowerOfTwo(int n) {
        int power = 1;
        while (power < n) {
            power *= 2;
        }
        return power;
    }

    /**
     * Calculates the spectral flatness of a magnitude spectrum.
     * Spectral flatness is the ratio of the geometric mean to the arithmetic mean
     * of the spectrum, and is a good measure of how "noisy" a sound is.
     * Values close to 0 indicate tonal sounds, while values close to 1 indicate noise.
     *
     * @param magnitudeSpectrum the magnitude spectrum to calculate flatness for
     * @param sampleRate the sample rate of the audio signal in Hz
     * @return the spectral flatness value between 0 and 1
     */
    private double calculateSpectralFlatness(double[] magnitudeSpectrum, int sampleRate) {
        // Skip the first few bins (DC and very low frequencies)
        int startBin = Math.max(1, (int) (minFrequency * magnitudeSpectrum.length / (sampleRate / 2)));
        int endBin = Math.min(magnitudeSpectrum.length - 1, (int) (maxFrequency * magnitudeSpectrum.length / (sampleRate / 2)));

        double sum = 0.0;
        double logSum = 0.0;
        int count = 0;

        // Calculate arithmetic mean and geometric mean
        for (int i = startBin; i <= endBin; i++) {
            // Add a small value to avoid log(0)
            double value = magnitudeSpectrum[i] + 1e-10;
            sum += value;
            logSum += Math.log(value);
            count++;
        }

        if (count == 0 || sum == 0) {
            return 1.0; // Maximum flatness (noise)
        }

        double arithmeticMean = sum / count;
        double geometricMean = Math.exp(logSum / count);

        // Calculate spectral flatness
        return geometricMean / arithmeticMean;
    }

    /**
     * Represents a peak in the frequency spectrum.
     */
    private static class Peak {
        final double frequency;
        final double magnitude;

        Peak(double frequency, double magnitude) {
            this.frequency = frequency;
            this.magnitude = magnitude;
        }
    }

    /**
     * Filters out harmonic frequencies by comparing the ratios of detected frequencies.
     *
     * This method checks if a frequency is a harmonic (integer multiple) of another frequency
     * and filters those harmonics out, prioritizing the lower fundamental frequencies.
     *
     * @param peaks the list of detected peaks to filter
     * @return the filtered list of peaks (without harmonics)
     */
    /**
     * Filters out harmonic frequencies by checking each peak against deeper fundamental frequencies.
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
                    if (ratio > 5.0) { // Unrealistische Harmonie
                        continue; // Akzeptiere es als separaten Ton
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
                if (peak.frequency > lowerPeak.frequency &&
                        peak.magnitude < lowerPeak.magnitude * 0.6) { // Adjusted amplitude ratio (factor 0.6)
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
