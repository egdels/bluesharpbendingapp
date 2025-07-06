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

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * The PitchDetector class provides functionality for analyzing audio data and detecting
 * pitch and harmonic content. It supports multiple pitch detection algorithms, including
 * YIN, MPM, FFT, and a Hybrid method. Additionally, it provides utility methods for
 * operations such as RMS calculation, spectral analysis, and noise determination.
 * <p>
 * The class includes static methods for backward compatibility and protected methods
 * for more advanced or specific signal processing tasks. It is suitable for applications
 * in audio analysis, music processing, and speech analysis.
 */
public abstract class PitchDetector {

    /**
     * A constant representing the absence of a detected pitch.
     * When a pitch detection method fails to identify a fundamental frequency within
     * the audio signal, this value is returned to indicate no pitch was detected.
     */
    public static final double NO_DETECTED_PITCH = -1;

    /**
     * The default minimum frequency that can be detected (in Hz).
     */
    private static final double DEFAULT_MIN_FREQUENCY = 80.0;

    /**
     * The default maximum frequency that can be detected (in Hz).
     */
    private static final double DEFAULT_MAX_FREQUENCY = 4835.0;

    /**
     * The minimum frequency that can be detected (in Hz).
     */
    @Getter
    @Setter
    protected static double minFrequency = DEFAULT_MIN_FREQUENCY;

    /**
     * The maximum frequency that can be detected (in Hz).
     */
    @Getter
    @Setter
    protected static double maxFrequency = DEFAULT_MAX_FREQUENCY;

    public static void restoreDefaults () {
    	minFrequency = DEFAULT_MIN_FREQUENCY;
    	maxFrequency = DEFAULT_MAX_FREQUENCY;
    }

    /**
     * Gets the default minimum frequency that can be detected (in Hz).
     *
     * @return the default minimum frequency in Hz
     */
    public static double getDefaultMinFrequency() {
        return DEFAULT_MIN_FREQUENCY;
    }

    /**
     * Gets the default maximum frequency that can be detected (in Hz).
     *
     * @return the default maximum frequency in Hz
     */
    public static double getDefaultMaxFrequency() {
        return DEFAULT_MAX_FREQUENCY;
    }

    /**
     * Static method for detecting pitch using the YIN algorithm.
     * This method is provided for backward compatibility with code that uses static methods.
     * It creates a temporary YINPitchDetector instance and delegates to it.
     *
     * @param audioData  an array of double values representing the audio signal.
     * @param sampleRate the sample rate of the audio signal in Hz.
     * @return a PitchDetectionResult containing the detected pitch in Hz and confidence value.
     */
    public static PitchDetectionResult detectPitchYIN(double[] audioData, int sampleRate) {
        YINPitchDetector detector = new YINPitchDetector();
        return detector.detectPitch(audioData, sampleRate);
    }

    /**
     * Static method for detecting pitch using the MPM algorithm.
     * This method is provided for backward compatibility with code that uses static methods.
     * It creates a temporary MPMPitchDetector instance and delegates to it.
     *
     * @param audioData  an array of double values representing the audio signal.
     * @param sampleRate the sample rate of the audio signal in Hz.
     * @return a PitchDetectionResult containing the detected pitch in Hz and confidence value.
     */
    public static PitchDetectionResult detectPitchMPM(double[] audioData, int sampleRate) {
        MPMPitchDetector detector = new MPMPitchDetector();
        return detector.detectPitch(audioData, sampleRate);
    }

    /**
     * Static method for detecting pitch using the Hybrid algorithm.
     * This method is provided for backward compatibility with code that uses static methods.
     * It creates a temporary HybridPitchDetector instance and delegates to it.
     * <p>
     * The Hybrid algorithm combines the strengths of YIN, MPM, and FFT algorithms
     * to achieve accurate pitch detection across a wide frequency range.
     *
     * @param audioData  an array of double values representing the audio signal.
     * @param sampleRate the sample rate of the audio signal in Hz.
     * @return a PitchDetectionResult containing the detected pitch in Hz and confidence value.
     */
    public static PitchDetectionResult detectPitchHybrid(double[] audioData, int sampleRate) {
        HybridPitchDetector detector = new HybridPitchDetector();
        return detector.detectPitch(audioData, sampleRate);
    }

    /**
     * Static method for detecting multiple pitches (chord) using spectral analysis.
     * This method creates a temporary ChordDetector instance and delegates to it.
     * <p>
     * This is a modern approach that can detect chords (multiple simultaneous pitches)
     * in an audio signal, unlike the YIN and MPM algorithms which are designed for
     * monophonic (single-pitch) detection.
     *
     * @param audioData  an array of double values representing the audio signal.
     * @param sampleRate the sample rate of the audio signal in Hz.
     * @return a ChordDetectionResult containing the detected pitches and confidence value.
     */
    public static ChordDetectionResult detectChord(double[] audioData, int sampleRate) {
        ChordDetector detector = new ChordDetector();
        return detector.detectChordInternal(audioData, sampleRate);
    }

    /**
     * Static method for detecting pitch using the FFT algorithm.
     * This method employs spectral analysis via the Fast Fourier Transform (FFT)
     * to estimate the pitch of a given input audio signal.
     *
     * @param audioData  an array of double values representing the audio signal.
     *                   Each value corresponds to the amplitude of the signal at a specific point in time.
     * @param sampleRate the sample rate of the audio signal in Hz. This is the number of samples
     *                   per second used to digitize the waveform.
     * @return a PitchDetectionResult containing the detected pitch in Hz and a confidence value
     * indicating the reliability of the detection.
     */
    public static PitchDetectionResult detectPitchFFT(double[] audioData, int sampleRate) {
        FFTDetector detector = new FFTDetector();
        return detector.detectPitch(audioData, sampleRate);
    }

    /**
     * Calculates the Root Mean Square (RMS) value of an audio signal.
     * The RMS value is a measure of the audio signal's energy and is
     * commonly used in audio processing to represent the signal's amplitude.
     *
     * @param audioData an array of double values representing the audio signal.
     *                  Each value corresponds to the amplitude of the signal at
     *                  a specific point in time.
     * @return the RMS value of the audio signal as a double.
     */
    public static double calcRMS(double[] audioData) {
        double sum = IntStream.range(0, audioData.length).parallel().mapToDouble(i -> audioData[i] * audioData[i]).sum();
        return Math.sqrt(sum / audioData.length);
    }

    /**
     * Applies parabolic interpolation to refine a peak index.
     * This method is used by pitch detection algorithms to improve the accuracy
     * of peak detection in various functions (NSDF, CMNDF, etc.).
     *
     * @param values    an array of double values representing the function to interpolate
     * @param peakIndex the index of the detected peak in the values array
     * @return the refined peak index as a double, adjusted using parabolic interpolation
     */
    protected static double parabolicInterpolation(double[] values, int peakIndex) {
        if (peakIndex <= 0 || peakIndex >= values.length - 1) {
            return peakIndex;
        }

        double x0 = values[peakIndex - 1];
        double x1 = values[peakIndex];
        double x2 = values[peakIndex + 1];

        // Calculate the adjustment using parabolic interpolation
        double denominator = x0 - 2 * x1 + x2;

        // Avoid division by zero or very small values
        if (Math.abs(denominator) < 1e-10) {
            return peakIndex;
        }

        double adjustment = 0.5 * (x0 - x2) / denominator;

        // Limit the adjustment to a reasonable range to avoid extreme values
        if (Math.abs(adjustment) > 1) {
            adjustment = 0;
        }

        return peakIndex + adjustment;
    }

    /**
     * Applies a Hann window function to the sample at the given index.
     *
     * @param index the index of the sample
     * @param size  the total number of samples
     * @return the window coefficient
     */
    protected static double hannWindow(int index, int size) {
        return 0.5 * (1 - Math.cos(2 * Math.PI * index / (size - 1)));
    }

    /**
     * Determines whether a specific element in an array is a local minimum.
     * A local minimum is defined as an element that is smaller than its
     * immediate neighbors.
     *
     * @param array the array of double values to evaluate
     * @param index the index of the element to check for being a local minimum
     * @return true if the element at the specified index is a local minimum;
     * false otherwise
     */
    protected static boolean isLocalMinimum(double[] array, int index) {
        if (index <= 0 || index >= array.length - 1) {
            return false;
        }
        return array[index] < array[index - 1] && array[index] < array[index + 1];
    }

    /**
     * Finds the first index in the array where the value is below a specified threshold
     * and is a local minimum.
     *
     * @param array     an array of double values to search in
     * @param threshold a double value representing the threshold for identifying valid values
     * @param minIndex  an integer specifying the minimum index to start the search from
     * @param maxIndex  an integer specifying the maximum index up to which the search should be conducted
     * @return the index of the first local minimum that satisfies the threshold condition;
     * returns -1 if no valid index is found within the given range
     */
    protected static int findFirstMinimum(double[] array, double threshold, int minIndex, int maxIndex) {
        for (int i = minIndex; i < maxIndex; i++) {
            if (array[i] < threshold && isLocalMinimum(array, i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Identifies the local peaks in the given array within a specific range.
     * A peak is defined as an element that is greater than its immediate neighbors
     * and exceeds a specified threshold.
     *
     * @param array     an array of double values to search in
     * @param threshold the minimum value for a peak to be considered
     * @param minIndex  the minimum index to start the search from
     * @return a list of integers where each integer represents the index of a detected peak
     */
    protected static List<Integer> findPeaks(double[] array, double threshold, int minIndex) {
        List<Integer> peaks = new ArrayList<>();

        // Ensure we don't go out of bounds
        if (array.length < 2) {
            return peaks;
        }

        // Find all peaks in the array that exceed the threshold
        for (int i = 1; i < array.length - 1; i++) {
            if (array[i] > array[i - 1] && array[i] > array[i + 1] && array[i] > threshold) {
                // Add the actual index value (not the array index)
                peaks.add(i + minIndex);
            }
        }

        return peaks;
    }

    /**
     * Determines if the audio signal is likely to be noise based on statistical properties.
     * <p>
     * This method analyzes the audio data by calculating:
     * 1. The mean and standard deviation to determine the coefficient of variation (CV)
     * 2. The zero-crossing rate (ZCR) to measure how often the signal changes sign
     * <p>
     * White noise typically has a high coefficient of variation (CV > 5.0) and
     * a high zero-crossing rate (ZCR > 0.4). These thresholds were determined
     * empirically to provide good discrimination between musical signals and noise.
     *
     * @param audioData the audio data to analyze
     * @return true if the signal is likely to be noise, false otherwise
     */
    protected static boolean isLikelyNoise(double[] audioData) {
        if (audioData.length == 0) {
            return true;
        }

        // Calculate mean using parallel stream
        double mean = IntStream.range(0, audioData.length).parallel().mapToDouble(i -> audioData[i]).average().orElse(0.0);

        // Calculate standard deviation using parallel stream
        double stdDev = Math.sqrt(IntStream.range(0, audioData.length).parallel().mapToDouble(i -> {
            double diff = audioData[i] - mean;
            return diff * diff;
        }).sum() / audioData.length);

        // Calculate zero-crossing rate using parallel stream
        int zeroCrossings = IntStream.range(1, audioData.length).parallel().map(i -> (audioData[i] >= 0 && audioData[i - 1] < 0) || (audioData[i] < 0 && audioData[i - 1] >= 0) ? 1 : 0).sum();
        double zeroCrossingRate = (double) zeroCrossings / (audioData.length - 1);

        // White noise typically has:
        // 1. High standard deviation relative to mean (high coefficient of variation)
        // 2. High zero-crossing rate
        double cv = Math.abs(stdDev / (mean + 1e-10));

        // Thresholds for noise detection
        return cv > 5.0 && zeroCrossingRate > 0.4;
    }

    /**
     * Prepares the input for a Fast Fourier Transform (FFT) by converting a real-valued
     * audio signal array into an array of complex numbers. The audio data is weighted
     * using a Hann window function. If the length of the audio data is smaller than the
     * specified FFT size, the rest of the array is zero-padded.
     *
     * @param audioData The array of real-valued audio signal data.
     * @param fftSize   The size of the FFT to be performed; determines the size of the
     *                  returned complex array.
     * @return An array of complex numbers, representing the weighted and zero-padded
     * audio data, prepared for FFT.
     */
    protected static Complex[] prepareFFTInput(double[] audioData, int fftSize) {
        Complex[] complexInput = new Complex[fftSize];

        // Parallel processing for large arrays
        if (audioData.length > 10000) {
            IntStream.range(0, fftSize).parallel().forEach(i -> {
                if (i < audioData.length) {
                    complexInput[i] = new Complex(audioData[i] * hannWindow(i, audioData.length), 0);
                } else {
                    complexInput[i] = new Complex(0, 0);
                }
            });
        } else {
            // Serial processing for smaller arrays
            for (int i = 0; i < fftSize; i++) {
                if (i < audioData.length) {
                    complexInput[i] = new Complex(audioData[i] * hannWindow(i, audioData.length), 0);
                } else {
                    complexInput[i] = new Complex(0, 0);
                }
            }
        }

        return complexInput;
    }

    /**
     * Performs a Fast Fourier Transform (FFT) on the given complex input data
     * and converts the result into an interleaved array of real and imaginary values.
     *
     * @param complexInput an array of Complex numbers representing the input signal
     *                     for the FFT operation
     * @param fftSize      the size of the FFT to be performed
     * @return an array of double values of size 2 * fftSize, where each pair of
     * consecutive values represents the real and imaginary parts of the FFT result
     */
    protected static double[] performFFT(Complex[] complexInput, int fftSize) {
        // Perform FFT using Commons Math
        FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] result = transformer.transform(complexInput, TransformType.FORWARD);

        // Convert result to format expected by rest of code
        double[] fftOutput = new double[fftSize * 2];
        for (int i = 0; i < fftSize; i++) {
            fftOutput[i * 2] = result[i].getReal();
            fftOutput[i * 2 + 1] = result[i].getImaginary();
        }
        return fftOutput;
    }

    /**
     * Calculates the magnitude spectrum from the FFT output.
     *
     * @param fftOutput an array containing the FFT output, where even indices are real parts
     *                  and odd indices are imaginary parts
     * @param fftSize   the size of the FFT, must be an even integer
     * @return an array representing the magnitude spectrum, where each element corresponds
     * to the magnitude of a frequency bin
     */
    protected static double[] calculateMagnitudeSpectrum(double[] fftOutput, int fftSize) {
        double[] magnitudeSpectrum = new double[fftSize / 2];
        IntStream.range(0, fftSize / 2).parallel().forEach(i -> {
            double real = fftOutput[i * 2];
            double imag = fftOutput[i * 2 + 1];
            magnitudeSpectrum[i] = Math.sqrt(real * real + imag * imag);
        });
        return magnitudeSpectrum;
    }

    /**
     * Identifies and refines peaks in a given magnitude spectrum. Peaks are
     * detected based on a threshold and are further refined using parabolic
     * interpolation. The results are sorted by peak magnitude in descending order.
     *
     * @param magnitudeSpectrum an array representing the magnitude spectrum of the signal
     * @param sampleRate        the sample rate of the signal in Hz
     * @param fftSize           the size of the FFT (Fast Fourier Transform) used to compute the spectrum
     * @param peakThreshold     the minimum magnitude a peak must have to be considered valid
     * @return a list of detected and refined peaks, sorted by magnitude in descending order
     */
    protected static List<Peak> findPeaks(double[] magnitudeSpectrum, int sampleRate, int fftSize, double peakThreshold) {
        List<Peak> peaks = new ArrayList<>();

        // Skip initial bins (DC and very low frequencies)
        int startBin = Math.max(1, (int) (minFrequency * fftSize / sampleRate));
        int endBin = Math.min(magnitudeSpectrum.length - 1, (int) (maxFrequency * fftSize / sampleRate));

        for (int i = startBin + 1; i < endBin - 1; i++) {
            // Check if this is a local maximum
            if (magnitudeSpectrum[i] > magnitudeSpectrum[i - 1] && magnitudeSpectrum[i] > magnitudeSpectrum[i + 1] && magnitudeSpectrum[i] > peakThreshold) {

                // Refine peak position using parabolic interpolation
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
     * Finds the next power of two greater than or equal to the given number.
     *
     * @param n the number to find the next power of two for
     * @return the next power of two
     */
    protected int nextPowerOfTwo(int n) {
        return (int) Math.pow(2, Math.ceil(Math.log(n) / Math.log(2)));
    }

    /**
     * Detects the pitch of an audio signal using the specific algorithm implemented by the subclass.
     *
     * @param audioData  an array of double values representing the audio signal.
     * @param sampleRate the sample rate of the audio signal in Hz.
     * @return a PitchDetectionResult containing the detected pitch in Hz and confidence value.
     */
    abstract PitchDetectionResult detectPitch(double[] audioData, int sampleRate);

    /**
     * Represents the result of a pitch detection operation.
     * This class stores the detected pitch frequency in Hz
     * and a confidence score indicating the reliability of the detection.
     */
    public record PitchDetectionResult(double pitch, double confidence) {
    }

    /**
     * Represents a peak in a frequency spectrum characterized by its
     * frequency and magnitude. This class is used to encapsulate the
     * attributes of a spectral peak for further analysis or processing.
     */
    protected static class Peak {
        /**
         * Represents the frequency of a spectral peak in Hertz (Hz).
         * This value identifies the specific component of the spectrum
         * characterized by its oscillation rate per second, commonly
         * used in audio signal processing and frequency analysis.
         */
        final double frequency;

        /**
         * Represents the magnitude (amplitude) of a spectral peak in a frequency spectrum.
         * Magnitude indicates the strength or intensity of the frequency component at a specific peak.
         * <p>
         * This value is typically used in audio signal processing and frequency analysis to
         * quantify the contribution of a particular frequency to the overall signal.
         */
        final double magnitude;

        /**
         * Constructs a Peak object with the specified frequency and magnitude.
         * This constructor initializes the frequency and magnitude of the peak,
         * which represent the characteristics of a spectral peak in a frequency spectrum.
         *
         * @param frequency The frequency of the spectral peak in Hertz (Hz).
         *                  This value identifies the specific component in the spectrum.
         * @param magnitude The magnitude (amplitude) of the spectral peak, which represents
         *                  the intensity or strength of the frequency component.
         */
        protected Peak(double frequency, double magnitude) {
            this.frequency = frequency;
            this.magnitude = magnitude;
        }

        /**
         * Returns a string representation of the Peak object. The string includes
         * the frequency and magnitude of the peak in a formatted structure.
         *
         * @return A string representation of the Peak object in the format:
         * "Peak[frequency Hz, mag=magnitude]".
         */
        @Override
        public String toString() {
            return String.format("Peak[%.2f Hz, mag=%.4f]", frequency, magnitude);
        }
    }

}
