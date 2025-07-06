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

/**
 * The HybridPitchDetector class provides a robust, hybrid approach for pitch detection by
 * combining multiple algorithms and techniques to achieve high accuracy across a wide
 * frequency range. It incorporates noise detection, energy analysis, and pitch estimation
 * tailored to different frequency bands.
 * <p>
 * This class leverages the YIN, MPM, and Fourier Transform algorithms to effectively handle
 * pitch detection for low, mid, and high-frequency ranges, respectively. By analyzing the
 * energy distribution and characteristics of the input audio signal, it dynamically applies
 * the most appropriate algorithm for pitch detection.
 * <p>
 * This detector is designed for applications in music analysis, speech processing, and other
 * domains requiring precise pitch estimation.
 */
public class HybridPitchDetector extends PitchDetector {

    /**
     * A threshold value for the energy level of low-frequency components in an audio signal.
     * This constant is used to differentiate between significant and insignificant energy
     * in low-frequency ranges during pitch detection and audio processing. Its value is
     * set to 750 by default, which can be adjusted programmatically if needed.
     * <p>
     * The threshold plays a critical role in determining the presence of low-frequency
     * energy in a signal and helps optimize the pitch detection algorithm used within
     * the HybridPitchDetector class.
     */
    private static final double THRESHOLD_LOW_FREQUENCY_ENERGY = 750;

    @Getter
    @Setter
    private static double thresholdLowFrequencyEnergy = THRESHOLD_LOW_FREQUENCY_ENERGY;

    /**
     * A constant defining the lower bound of the frequency range for pitch detection in Hz.
     * This value represents the minimum frequency that the pitch detection algorithms consider
     * during the processing of an audio signal. It helps in filtering out frequencies below this
     * threshold to optimize detection accuracy and performance.
     */
    private static final int FREQUENCY_RANGE_LOW = 275;

    @Getter
    @Setter
    private static int frequencyRangeLow = FREQUENCY_RANGE_LOW;

    /**
     * Represents the upper limit of the frequency range threshold, expressed in Hertz (Hz),
     * for the hybrid pitch detection algorithm.
     * <p>
     * This constant is used by the HybridPitchDetector class to restrict pitch detection
     * to frequencies less than or equal to this value. Adjusting this value can impact the
     * algorithm's sensitivity to higher frequency ranges.
     * <p>
     * Default value is set to 900 Hz.
     */
    private static final int FREQUENCY_RANGE_HIGH = 900;

    @Getter
    @Setter
    private static int frequencyRangeHigh = FREQUENCY_RANGE_HIGH;

    /**
     * Represents the threshold value for high-frequency energy in the pitch detection process.
     * This constant is used to analyze and differentiate between low-energy and high-energy
     * signals in high-frequency ranges during the pitch detection computations.
     * <p>
     * Value is set in Hertz (Hz).
     * Default value: 400 Hz.
     * <p>
     * Primarily used in hybrid pitch detection algorithms to optimize performance and accuracy
     * by applying varied detection techniques based on the signal's energy and frequency characteristics.
     */
    private static final double THRESHOLD_HIGH_FREQUENCY_ENERGY = 400;

    @Getter
    @Setter
    private static double thresholdHighFrequencyEnergy = THRESHOLD_HIGH_FREQUENCY_ENERGY;

    /**
     * An instance of the YIN pitch detection algorithm.
     * The YIN algorithm is used to estimate the pitch of an audio signal
     * based on the autocorrelation method, making it particularly suitable
     * for monophonic signals and low-frequency detection.
     * <p>
     * This variable is part of a hybrid pitch detection system and is
     * utilized in conjunction with other pitch detection algorithms to
     * enhance performance and accuracy under varying signal conditions.
     * <p>
     * The detection process leverages energy thresholds and frequency ranges
     * to decide whether to use the YIN algorithm or other detection methods,
     * such as MPM or FFT-based techniques.
     */
    private final YINPitchDetector yinDetector = new YINPitchDetector();
    /**
     * Represents an instance of the MPM (McLeod Pitch Method) pitch detection algorithm,
     * used for detecting the fundamental frequency of audio signals.
     * This variable is initialized as a final field and is utilized within the
     * HybridPitchDetector class to enable hybrid pitch detection by combining
     * multiple pitch detection techniques.
     */
    private final MPMPitchDetector mpmDetector = new MPMPitchDetector();

    /**
     * An instance of the FFTDetector class used for pitch detection through frequency domain analysis.
     * The detector applies the Fast Fourier Transform (FFT) algorithm to analyze the spectral components
     * of audio data, aiding in detecting the presence and characteristics of specific frequency components.
     * <p>
     * This field is a key component of the hybrid pitch detection system, complementing other detection
     * algorithms to provide accurate and reliable pitch detection across various audio signals.
     */
    private final FFTDetector fftDetector = new FFTDetector();

    public static void restoreDefaults () {
        PitchDetector.restoreDefaults();
        thresholdLowFrequencyEnergy = THRESHOLD_LOW_FREQUENCY_ENERGY;
        frequencyRangeLow = FREQUENCY_RANGE_LOW;
        frequencyRangeHigh = FREQUENCY_RANGE_HIGH;
        thresholdHighFrequencyEnergy = THRESHOLD_HIGH_FREQUENCY_ENERGY;
    }

    /**
     * Detects the pitch of a given audio signal using a hybrid approach that combines
     * multiple pitch detection algorithms (e.g., YIN, FFT, MPM). The detection process
     * includes noise diagnosis, energy analysis for low and high frequencies, and
     * fallback mechanisms to improve accuracy across a wide range of input signals.
     *
     * @param audioData an array of doubles representing the audio signal data in the time domain
     * @param sampleRate the sample rate of the audio data in Hz
     * @return a PitchDetectionResult object containing the detected pitch in Hz and the confidence score
     */
    @Override
    public PitchDetectionResult detectPitch(double[] audioData, int sampleRate) {
        // Step 1: Noise-Diagnose – falls Signal nur Rauschen enthält
        if (isLikelyNoise(audioData)) {
            return new PitchDetectionResult(NO_DETECTED_PITCH, 0.0);
        }

        // Step 2: Energieanalyse für Frequenzen unter 300 Hz
        double lowFrequencyEnergy = calculateEnergyUsingGoertzel(audioData, sampleRate, frequencyRangeLow);
        // Step 3: Entscheidung basierend auf Energie-Analyse
        if (lowFrequencyEnergy > thresholdLowFrequencyEnergy) {
            // Verwende YIN für Frequenzen unter 300 Hz
            PitchDetectionResult yinResult = yinDetector.detectPitch(audioData, sampleRate);
            if (yinResult.pitch() != NO_DETECTED_PITCH) {
                return yinResult;
            }
        } else {
            // Step 4: Energieanalyse für hohe Frequenzen (z. B. 1000 Hz)
            double highFrequencyEnergy = calculateEnergyUsingGoertzel(audioData, sampleRate, frequencyRangeHigh);

            // Wenn Energie im Bereich hoher Frequenzen hoch ist, nutze FFT
            if (highFrequencyEnergy > thresholdHighFrequencyEnergy) {
                PitchDetectionResult fftResult = fftDetector.detectPitch(audioData, sampleRate);
                if (fftResult.pitch() != NO_DETECTED_PITCH) {
                    return fftResult;
                }
            }

            // Wenn Hochfrequenzenergie NICHT hoch genug ist, nutze MPM
            PitchDetectionResult mpmResult = mpmDetector.detectPitch(audioData, sampleRate);
            if (mpmResult.pitch() != NO_DETECTED_PITCH) {
                return mpmResult;
            }
        }


        // Fallback: Versuche erneut mit YIN
        return yinDetector.detectPitch(audioData, sampleRate);
    }


    /**
     * Calculates the energy of a specific frequency component in the given audio data
     * using the Goertzel algorithm. This method is designed for efficient frequency
     * analysis, particularly when evaluating a single frequency component.
     *
     * @param audioData an array of doubles representing the audio signal data in the time domain
     * @param sampleRate the sample rate of the audio data in Hz
     * @param frequency the target frequency in Hz to calculate the energy for
     * @return the calculated energy of the specified frequency in the audio data
     */
    private double calculateEnergyUsingGoertzel(double[] audioData, int sampleRate, int frequency) {
        double omega = 2.0 * Math.PI * frequency / sampleRate; // Ziel-Frequenz berechnen
        double cosine = Math.cos(omega);
        double coeff = 2.0 * cosine;
        double q0 = 0, q1 = 0, q2 = 0;

        for (double audioDatum : audioData) {
            q0 = coeff * q1 - q2 + audioDatum;
            q2 = q1;
            q1 = q0;
        }

        // Goertzel-Energiemessung:
        return q1 * q1 + q2 * q2 - coeff * q1 * q2;
    }


}
