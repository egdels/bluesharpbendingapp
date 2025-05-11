package de.schliweb.bluesharpbendingapp.utils;

import lombok.Getter;
import lombok.Setter;

/**
 * Abstract base class for pitch detection algorithms.
 * <p>
 * This class provides common functionality for different pitch detection algorithms
 * such as YIN, MPM, and ZCR. It defines the basic structure and shared methods
 * that all pitch detectors use, allowing for consistent configuration and usage
 * across different implementations.
 * <p>
 * Each concrete implementation should provide its own algorithm-specific logic
 * in the detectPitch method.
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
     * Detects the pitch of an audio signal using the specific algorithm implemented by the subclass.
     *
     * @param audioData  an array of double values representing the audio signal.
     * @param sampleRate the sample rate of the audio signal in Hz.
     * @return a PitchDetectionResult containing the detected pitch in Hz and confidence value.
     */
    abstract PitchDetectionResult detectPitch(double[] audioData, int sampleRate);

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
        double sum = 0;
        for (double sample : audioData) {
            sum += sample * sample;
        }
        return Math.sqrt(sum / audioData.length);
    }

    /**
     * Checks if the audio data is silent (contains only very small values).
     *
     * @param audioData an array of double values representing the audio signal.
     * @param threshold the threshold below which the signal is considered silent.
     * @return true if the audio data is silent, false otherwise.
     */
    protected static boolean isSilent(double[] audioData, double threshold) {
        double rms = calcRMS(audioData);
        return rms < threshold;
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
     * Performs an in-place Fast Fourier Transform (FFT) on the input data.
     * This is a radix-2 decimation-in-time FFT algorithm.
     *
     * @param data the input/output data array (complex numbers as pairs of real, imaginary)
     * @param n    the size of the FFT (number of complex numbers)
     */
    protected static void fft(double[] data, int n) {
        // Bit-reversal permutation
        int shift = 1;
        while (shift < n) {
            shift <<= 1;
        }
        shift >>= 1;

        // Bit reversal
        int i, j;
        for (i = 0; i < n; i++) {
            j = bitReverse(i, shift);
            if (j > i) {
                // Swap real parts
                double temp = data[i * 2];
                data[i * 2] = data[j * 2];
                data[j * 2] = temp;

                // Swap imaginary parts
                temp = data[i * 2 + 1];
                data[i * 2 + 1] = data[j * 2 + 1];
                data[j * 2 + 1] = temp;
            }
        }

        // Cooley-Tukey FFT
        for (int len = 2; len <= n; len <<= 1) {
            double angle = -2 * Math.PI / len;
            double wReal = Math.cos(angle);
            double wImag = Math.sin(angle);

            for (i = 0; i < n; i += len) {
                double uReal = 1.0;
                double uImag = 0.0;

                for (j = 0; j < len / 2; j++) {
                    int p = i + j;
                    int q = i + j + len / 2;

                    double pReal = data[p * 2];
                    double pImag = data[p * 2 + 1];
                    double qReal = data[q * 2];
                    double qImag = data[q * 2 + 1];

                    // Temporary values for the multiplication
                    double tempReal = uReal * qReal - uImag * qImag;
                    double tempImag = uReal * qImag + uImag * qReal;

                    // Update data
                    data[q * 2] = pReal - tempReal;
                    data[q * 2 + 1] = pImag - tempImag;
                    data[p * 2] = pReal + tempReal;
                    data[p * 2 + 1] = pImag + tempImag;

                    // Update u
                    double nextUReal = uReal * wReal - uImag * wImag;
                    double nextUImag = uReal * wImag + uImag * wReal;
                    uReal = nextUReal;
                    uImag = nextUImag;
                }
            }
        }
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
     * Reverses the bits of an integer value up to the given shift.
     *
     * @param value the value to reverse
     * @param shift the bit position to reverse up to
     * @return the bit-reversed value
     */
    protected static int bitReverse(int value, int shift) {
        int result = 0;
        while (shift > 0) {
            result = (result << 1) | (value & 1);
            value >>= 1;
            shift >>= 1;
        }
        return result;
    }

    /**
     * Represents the result of a pitch detection operation.
     * This class stores the detected pitch frequency in Hz
     * and a confidence score indicating the reliability of the detection.
     */
    public record PitchDetectionResult(double pitch, double confidence) {
    }
}
