package de.schliweb.bluesharpbendingapp.utils;

/**
 * Utility class for generating audio test signals.
 * <p>
 * This class provides methods for generating various types of audio signals
 * commonly used in testing pitch detection algorithms, such as sine waves,
 * mixed sine waves, and sine waves with noise.
 * <p>
 * These utilities help ensure consistent test signal generation across
 * different test classes, reducing code duplication and improving maintainability.
 */
public class AudioTestUtils {

    /**
     * Generates a sine wave with the specified frequency, sample rate, and duration.
     * <p>
     * This method creates a pure sine wave with amplitude 1.0 at the given frequency.
     * The resulting array contains samples that represent the sine wave over the
     * specified duration at the given sample rate.
     *
     * @param frequency  the frequency of the sine wave in Hz
     * @param sampleRate the sample rate in Hz
     * @param duration   the duration of the sine wave in seconds
     * @return an array of double values representing the sine wave
     */
    public static double[] generateSineWave(double frequency, int sampleRate, double duration) {
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }
        return audioData;
    }

    /**
     * Generates a mixed sine wave with two frequencies and specified amplitudes.
     * <p>
     * This method creates a complex waveform by combining two sine waves with
     * different frequencies and amplitudes. This is useful for testing how
     * pitch detection algorithms handle signals with multiple frequency components.
     *
     * @param primaryFreq    the primary frequency in Hz
     * @param primaryAmp     the amplitude of the primary frequency
     * @param secondaryFreq  the secondary frequency in Hz
     * @param secondaryAmp   the amplitude of the secondary frequency
     * @param sampleRate     the sample rate in Hz
     * @param duration       the duration in seconds
     * @return an array of double values representing the mixed sine wave
     */
    public static double[] generateMixedSineWaveWithAmplitudes(double primaryFreq, double primaryAmp,
                                                        double secondaryFreq, double secondaryAmp,
                                                        int sampleRate, int duration) {
        int samples = sampleRate * duration;
        double[] audioData = new double[samples];

        for (int i = 0; i < samples; i++) {
            double t = (double) i / sampleRate;
            audioData[i] = primaryAmp * Math.sin(2 * Math.PI * primaryFreq * t) +
                          secondaryAmp * Math.sin(2 * Math.PI * secondaryFreq * t);
        }

        return audioData;
    }

    /**
     * Generates a sine wave with added white noise.
     * <p>
     * This method creates a sine wave and adds random noise to it. The noise level
     * parameter controls the amplitude of the noise relative to the sine wave.
     * This is useful for testing how pitch detection algorithms perform in the
     * presence of noise.
     *
     * @param frequency   the frequency of the sine wave in Hz
     * @param sampleRate  the sample rate in Hz
     * @param durationMs  the duration in milliseconds
     * @param noiseLevel  the level of noise as a fraction of the signal amplitude
     * @return an array of double values representing the noisy sine wave
     */
    public static double[] generateSineWaveWithNoise(double frequency, int sampleRate, int durationMs, double noiseLevel) {
        int samples = (int) (sampleRate * durationMs / 1000.0);
        double[] audioData = new double[samples];

        for (int i = 0; i < samples; i++) {
            double t = (double) i / sampleRate;
            double signal = Math.sin(2 * Math.PI * frequency * t);
            double noise = (Math.random() * 2 - 1) * noiseLevel;
            audioData[i] = signal + noise;
        }

        return audioData;
    }

    /**
     * Generates white noise with the specified sample rate and duration.
     * <p>
     * This method creates random noise with values between -1 and 1.
     * This is useful for testing how pitch detection algorithms handle
     * non-periodic signals.
     *
     * @param sampleRate the sample rate in Hz
     * @param duration   the duration in seconds
     * @return an array of double values representing white noise
     */
    public static double[] generateWhiteNoise(int sampleRate, double duration) {
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = Math.random() * 2 - 1; // Random values between -1 and 1
        }
        return audioData;
    }

    /**
     * Generates a linearly increasing signal with the specified sample rate and duration.
     * <p>
     * This method creates a signal that increases linearly from 0 to 1 over the
     * specified duration. This is useful for testing how pitch detection algorithms
     * handle non-periodic signals.
     *
     * @param sampleRate the sample rate in Hz
     * @param duration   the duration in seconds
     * @return an array of double values representing a linearly increasing signal
     */
    public static double[] generateLinearlyIncreasingSignal(int sampleRate, double duration) {
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            audioData[i] = i / (double) samples;
        }
        return audioData;
    }

    /**
     * Generates a zero-crossing wave (square-like waveform) with the specified frequency,
     * amplitude, sample rate, and duration.
     * <p>
     * This method creates a waveform that alternates between positive and negative
     * amplitude values based on the sign of a sine wave at the given frequency.
     * This is useful for testing how pitch detection algorithms handle signals
     * with sharp transitions.
     *
     * @param frequency  the frequency of the underlying sine wave in Hz
     * @param amplitude  the amplitude of the square wave
     * @param sampleRate the sample rate in Hz
     * @param duration   the duration in seconds
     * @return an array of double values representing the zero-crossing wave
     */
    public static double[] generateZeroCrossingWave(double frequency, double amplitude, int sampleRate, double duration) {
        double[] sineWave = generateSineWave(frequency, sampleRate, duration);
        double[] zeroCrossingWave = new double[sineWave.length];
        for (int i = 0; i < sineWave.length; i++) {
            zeroCrossingWave[i] = (sineWave[i] >= 0) ? amplitude : -amplitude;
        }
        return zeroCrossingWave;
    }

    /**
     * Generates a sine wave with varying amplitude.
     * <p>
     * This method creates a sine wave whose amplitude increases linearly from 0 to 1
     * over the specified duration. This is useful for testing how pitch detection
     * algorithms handle signals with varying amplitude.
     *
     * @param frequency  the frequency of the sine wave in Hz
     * @param sampleRate the sample rate in Hz
     * @param duration   the duration in seconds
     * @return an array of double values representing the sine wave with varying amplitude
     */
    public static double[] generateSineWaveWithVaryingAmplitude(double frequency, int sampleRate, double duration) {
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];
        for (int i = 0; i < samples; i++) {
            double amplitude = (double) i / samples; // Scale amplitude from 0 to 1
            audioData[i] = amplitude * Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }
        return audioData;
    }
}