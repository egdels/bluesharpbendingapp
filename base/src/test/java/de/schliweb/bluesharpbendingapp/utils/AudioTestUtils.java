package de.schliweb.bluesharpbendingapp.utils;

/**
 * Utility class for generating audio test signals.
 * <p>
 * This class provides methods for generating various types of audio signals
 * commonly used in testing pitch detection algorithms, such as sine waves,
 * mixed sine waves, sine waves with noise, frequency sweeps, and signals
 * with multiple frequency components.
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

    /**
     * Generates a frequency sweep (chirp) signal that transitions from a start frequency
     * to an end frequency over the specified duration.
     * <p>
     * This method creates a signal whose frequency changes linearly from the start
     * frequency to the end frequency. This is useful for testing how pitch detection
     * algorithms track changing frequencies, especially in the high frequency range.
     *
     * @param startFreq  the starting frequency in Hz
     * @param endFreq    the ending frequency in Hz
     * @param sampleRate the sample rate in Hz
     * @param duration   the duration in seconds
     * @return an array of double values representing the frequency sweep
     */
    public static double[] generateFrequencySweep(double startFreq, double endFreq, int sampleRate, double duration) {
        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];

        for (int i = 0; i < samples; i++) {
            double t = (double) i / sampleRate;
            // Linear frequency sweep
            double instantFreq = startFreq + (endFreq - startFreq) * (t / duration);
            // Phase is the integral of frequency over time
            double phase = 2 * Math.PI * (startFreq * t + 0.5 * (endFreq - startFreq) * t * t / duration);
            audioData[i] = Math.sin(phase);
        }

        return audioData;
    }

    /**
     * Generates a signal with multiple frequency components and specified amplitudes.
     * <p>
     * This method creates a complex waveform by combining multiple sine waves with
     * different frequencies and amplitudes. This is useful for testing how pitch detection
     * algorithms handle signals with multiple frequency components, which is common in
     * real-world musical sounds like harmonicas.
     *
     * @param frequencies an array of frequencies in Hz
     * @param amplitudes  an array of amplitudes for each frequency
     * @param sampleRate  the sample rate in Hz
     * @param duration    the duration in seconds
     * @return an array of double values representing the complex signal
     * @throws IllegalArgumentException if frequencies and amplitudes arrays have different lengths
     */
    public static double[] generateMultipleFrequencySignal(double[] frequencies, double[] amplitudes, 
                                                         int sampleRate, double duration) {
        if (frequencies.length != amplitudes.length) {
            throw new IllegalArgumentException("Frequencies and amplitudes arrays must have the same length");
        }

        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];

        for (int i = 0; i < samples; i++) {
            double t = (double) i / sampleRate;
            double sample = 0.0;

            for (int j = 0; j < frequencies.length; j++) {
                sample += amplitudes[j] * Math.sin(2 * Math.PI * frequencies[j] * t);
            }

            audioData[i] = sample;
        }

        return audioData;
    }

    /**
     * Generates a signal that abruptly transitions from one frequency to another.
     * <p>
     * This method creates a signal that starts at one frequency and then abruptly
     * changes to another frequency at a specified transition point. This is useful
     * for testing how quickly pitch detection algorithms can respond to sudden
     * frequency changes, which is important for real-time applications.
     *
     * @param freq1       the first frequency in Hz
     * @param freq2       the second frequency in Hz
     * @param sampleRate  the sample rate in Hz
     * @param duration    the total duration in seconds
     * @param transitionPoint the point at which to transition from freq1 to freq2, as a fraction of duration (0.0-1.0)
     * @return an array of double values representing the signal with a frequency transition
     */
    public static double[] generateFrequencyTransition(double freq1, double freq2, int sampleRate, 
                                                     double duration, double transitionPoint) {
        int samples = (int) (sampleRate * duration);
        int transitionSample = (int) (samples * transitionPoint);
        double[] audioData = new double[samples];

        // Phase continuity variables
        double phase = 0.0;
        double phaseAtTransition = 0.0;

        for (int i = 0; i < samples; i++) {
            double t = (double) i / sampleRate;

            if (i < transitionSample) {
                // First frequency
                phase = 2 * Math.PI * freq1 * t;
                if (i == transitionSample - 1) {
                    phaseAtTransition = phase;
                }
                audioData[i] = Math.sin(phase);
            } else {
                // Second frequency, with phase continuity
                double tSinceTransition = (double) (i - transitionSample) / sampleRate;
                phase = phaseAtTransition + 2 * Math.PI * freq2 * tSinceTransition;
                audioData[i] = Math.sin(phase);
            }
        }

        return audioData;
    }
}
