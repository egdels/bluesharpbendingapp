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


/**
 * HybridPitchDetector is a pitch detection class that dynamically selects the most
 * suitable pitch detection algorithm based on the input signal's estimated frequency
 * and desired frequency ranges. It combines the strengths of different algorithms
 * including YIN, Multiple Pitch Measurement (MPM), and Fast Fourier Transform (FFT)
 * to improve detection accuracy and performance across various frequency domains.
 * <p>
 * The class divides the frequency spectrum into three ranges:
 * - Low frequencies (below 300 Hz): Uses the YIN algorithm, known for its accuracy at low frequencies.
 * - Medium frequencies (300 Hz to 1000 Hz): Uses the MPM algorithm, optimized for medium-range accuracy.
 * - High frequencies (above 1000 Hz): Uses the FFT algorithm for efficiency and accuracy.
 * <p>
 * The detection process involves initial noise screening, rough frequency estimation with FFT,
 * and dynamic delegation to the appropriate algorithm based on the estimated pitch and its
 * confidence level.
 */
public class HybridPitchDetector extends PitchDetector {

    /**
     * Defines the threshold value in Hz used to distinguish low frequencies from medium frequencies
     * within the pitch detection algorithm. Frequencies below this value are categorized as low.
     * <p>
     * This threshold is utilized by the HybridPitchDetector to determine which pitch detection
     * algorithm to apply based on the characteristics of the input audio signal. The value is
     * statically set and commonly used across all instances of pitch detection.
     */
    private static final double LOW_FREQUENCY_THRESHOLD = 300.0; // Threshold between low and medium frequencies
    /**
     * Defines the threshold frequency in Hz above which audio frequencies are classified as high-frequency.
     * This value is used by the pitch detection algorithms in the HybridPitchDetector class to dynamically
     * select the appropriate algorithm for high-frequency audio signal processing.
     * <p>
     * High-frequency signals are typically processed using different algorithms compared to low or medium
     * frequency signals, ensuring optimized performance and accuracy.
     */
    private static final double HIGH_FREQUENCY_THRESHOLD = 1000.0; // Threshold between medium and high frequencies

    /**
     * An instance of the YINPitchDetector class used for pitch detection.
     * <p>
     * This object utilizes the YIN algorithm to perform high-accuracy pitch detection
     * by analyzing audio signals. The YIN algorithm is particularly effective for determining
     * the fundamental frequency of an input signal and is characterized by its use of
     * the cumulative mean normalized difference function (CMNDF), thresholding, and lag estimation.
     * <p>
     * The yinDetector variable operates as the YIN-based pitch detection mechanism in the system,
     * playing a crucial role in detecting pitch within the specified frequency ranges or contexts.
     */
    private final YINPitchDetector yinDetector = new YINPitchDetector();
    /**
     * A final instance of the `MPMPitchDetector` class, which is responsible for pitch detection
     * using the McLeod Pitch Method (MPM). This detector analyzes audio signals to determine
     * the fundamental frequency (pitch) and its confidence level.
     * <p>
     * The `mpmDetector` is part of the `HybridPitchDetector` system, which dynamically selects
     * and executes different pitch detection algorithms based on frequency thresholds and confidence
     * values. The `MPMPitchDetector` implementation is specifically utilized for scenarios where
     * the McLeod Pitch Method is deemed appropriate.
     */
    private final MPMPitchDetector mpmDetector = new MPMPitchDetector();
    /**
     * An instance of FFTDetector used for pitch detection using the Fast Fourier Transform algorithm.
     * It is one of the pitch detection methods utilized by the HybridPitchDetector.
     */
    private final FFTDetector fftDetector = new FFTDetector();

    /**
     * Detects the pitch of an audio signal using a hybrid approach that selects the most appropriate
     * pitch detection algorithm based on the characteristics of the input audio data and the estimated frequency range.
     * <p>
     * Depending on the signal characteristics, this method utilizes:
     * - YIN for low frequencies (below 300 Hz) for improved accuracy in that range.
     * - MPM for frequencies between 300 Hz and 1000 Hz for better handling of intermediate ranges.
     * - FFT for high frequencies (above 1000 Hz) or for performance efficiency in certain cases.
     * If no pitch is detected using one algorithm, it will fallback to other algorithms based on the context.
     *
     * @param audioData  Array of double values representing the audio samples to be analyzed.
     * @param sampleRate The sample rate of the audio data in Hz, which provides the temporal resolution of the signal.
     * @return A {@code PitchDetectionResult} containing the detected pitch frequency in Hz and a confidence score,
     * or a pitch value of {@code NO_DETECTED_PITCH} with 0.0 confidence if no pitch could be reliably detected.
     */
    @Override
    public PitchDetectionResult detectPitch(double[] audioData, int sampleRate) {
        // Step 1: Early noise detection
        if (isLikelyNoise(audioData)) {
            return new PitchDetectionResult(NO_DETECTED_PITCH, 0.0);
        }

        // Step 2: First use FFT to roughly determine the frequency
        PitchDetectionResult fftResult = fftDetector.detectPitch(audioData, sampleRate);

        // If FFT found a pitch, use it to decide which algorithm to use next
        if (fftResult.pitch() != NO_DETECTED_PITCH) {
            double estimatedFrequency = fftResult.pitch();

            // If frequency is below 300 Hz, use YIN for better accuracy with low frequencies
            if (estimatedFrequency < LOW_FREQUENCY_THRESHOLD) {
                PitchDetectionResult yinResult = yinDetector.detectPitch(audioData, sampleRate);
                if (yinResult.pitch() != NO_DETECTED_PITCH) {
                    return yinResult;
                }
            }
            // For frequencies between 300 Hz and 1000 Hz, use MPM for better accuracy with medium frequencies
            else if (estimatedFrequency < HIGH_FREQUENCY_THRESHOLD) {
                PitchDetectionResult mpmResult = mpmDetector.detectPitch(audioData, sampleRate);
                if (mpmResult.pitch() != NO_DETECTED_PITCH) {
                    return mpmResult;
                }
            }
            // For frequencies above 1000 Hz, use FFT which is more performant for high frequencies
            else {
                // We already have the FFT result from the initial estimation
                return fftResult;
            }
        }

        // If FFT didn't find a pitch, we need to try other methods based on expected frequency range

        // For expected low frequencies (below 300 Hz), try YIN
        if (getMinFrequency() < LOW_FREQUENCY_THRESHOLD) {
            PitchDetectionResult yinResult = yinDetector.detectPitch(audioData, sampleRate);
            if (yinResult.pitch() != NO_DETECTED_PITCH) {
                return yinResult;
            }
        }

        // For expected medium frequencies (between 300 Hz and 1000 Hz), try MPM
        if (getMinFrequency() >= LOW_FREQUENCY_THRESHOLD && getMinFrequency() < HIGH_FREQUENCY_THRESHOLD) {
            PitchDetectionResult mpmResult = mpmDetector.detectPitch(audioData, sampleRate);
            if (mpmResult.pitch() != NO_DETECTED_PITCH) {
                return mpmResult;
            }
        }

        // For expected high frequencies (above 1000 Hz), try FFT
        if (getMinFrequency() >= HIGH_FREQUENCY_THRESHOLD) {
            PitchDetectionResult fallbackFftResult = fftDetector.detectPitch(audioData, sampleRate);
            if (fallbackFftResult.pitch() != NO_DETECTED_PITCH) {
                return fallbackFftResult;
            }
        }

        // Fallback if no clear result
        return new PitchDetectionResult(NO_DETECTED_PITCH, 0.0);
    }

}
