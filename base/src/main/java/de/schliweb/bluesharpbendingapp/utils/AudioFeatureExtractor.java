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

import java.util.Arrays;

/**
 * Utility class for extracting audio features.
 * <p>
 * This class provides methods for extracting various audio features such as MFCC-like,
 * Chroma-like, and Spectral Contrast-like features from audio data. These features are used for
 * chord detection and other audio analysis tasks.
 * <p>
 * Note: This is a simplified implementation that approximates the features
 * without requiring external libraries like JLibrosa.
 */
public class AudioFeatureExtractor {

    /**
     * Number of MFCC-like coefficients to extract.
     */
    private static final int N_MFCC = 13;

    /**
     * Number of chroma bins to use.
     */
    private static final int N_CHROMA = 12;

    /**
     * Number of bands for spectral contrast.
     */
    private static final int N_CONTRAST = 7;

    /**
     * FFT size for feature extraction.
     */
    private static final int N_FFT = 2048;

    /**
     * Default constructor for the AudioFeatureExtractor class.
     * Initializes a new instance of the audio feature extractor.
     */
    public AudioFeatureExtractor() {
        LoggingContext.setComponent("AudioFeatureExtractor");
        LoggingUtils.logDebug("Initializing AudioFeatureExtractor");
    }

    /**
     * Extracts features from audio data.
     *
     * @param audioData  the audio data
     * @param sampleRate the sample rate of the audio data
     * @return the extracted features as a float array
     */
    public float[] extractFeatures(double[] audioData, int sampleRate) {
        LoggingUtils.logDebug("Extracting features from audio data", 
                             "Sample rate: " + sampleRate + " Hz, data length: " + audioData.length);
        LoggingUtils.logOperationStarted("Feature extraction");

        // Check for silence (all zeros or very low energy)
        boolean isSilence = true;
        for (double sample : audioData) {
            if (Math.abs(sample) > 1e-6) {
                isSilence = false;
                break;
            }
        }

        if (isSilence) {
            LoggingUtils.logDebug("Detected silence, returning zero features");
            LoggingUtils.logOperationCompleted("Feature extraction (silence)");
            // Return all zeros for silence
            return new float[32]; // 13 MFCC + 12 Chroma + 7 Contrast
        }

        try {
            // Create feature array (32 features)
            float[] features = new float[32];

            // 1. Berechne die verbesserten MFCC-Features (13 Features)
            float[] mfccFeatures = computeMFCC(audioData, sampleRate);
            System.arraycopy(mfccFeatures, 0, features, 0, mfccFeatures.length);

            // Approximate chroma features (12 features, one for each semitone)
            // Perform a simple FFT to get frequency information
            double[] magnitudeSpectrum = calculateMagnitudeSpectrum(audioData);

            // Calculate approximate chroma features
            double[] chroma = new double[12];
            for (int i = 0; i < magnitudeSpectrum.length; i++) {
                double freq = i * sampleRate / (double) N_FFT;
                if (freq > 0) {
                    // Convert frequency to chroma bin
                    double noteNum = 12 * (Math.log(freq / 440.0) / Math.log(2)) + 69;
                    int chromaBin = (int) Math.round(noteNum) % 12;
                    if (chromaBin >= 0 && chromaBin < 12) {
                        chroma[chromaBin] += magnitudeSpectrum[i];
                    }
                }
            }

            // Normalize chroma features
            double maxChroma = Arrays.stream(chroma).max().orElse(1.0);
            if (maxChroma > 0) {
                for (int i = 0; i < chroma.length; i++) {
                    chroma[i] /= maxChroma;
                }
            }

            // Add chroma features to the feature vector
            for (int i = 0; i < 12; i++) {
                features[i + 13] = (float) chroma[i];
            }

            // Approximate spectral contrast features (7 features)
            // Divide the spectrum into 7 bands and calculate contrast
            int binPerBand = magnitudeSpectrum.length / N_CONTRAST;

            for (int band = 0; band < N_CONTRAST; band++) {
                int startBin = band * binPerBand;
                int endBin = (band + 1) * binPerBand;

                // Find peak and valley in this band
                double peak = Double.MIN_VALUE;
                double valley = Double.MAX_VALUE;

                for (int i = startBin; i < Math.min(endBin, magnitudeSpectrum.length); i++) {
                    peak = Math.max(peak, magnitudeSpectrum[i]);
                    valley = Math.min(valley, magnitudeSpectrum[i]);
                }

                // Calculate contrast
                double contrast = peak - valley;
                features[13 + 12 + band] = (float) contrast;
            }

            LoggingUtils.logDebug("Feature extraction completed", 
                                 "Features length: " + features.length);
            LoggingUtils.logOperationCompleted("Feature extraction");
            return features;

        } catch (Exception e) {
            LoggingUtils.logError("Error extracting features", e.getMessage());
            LoggingUtils.logOperationCompleted("Feature extraction (failed)");
            // Return default features in case of error
            return createDefaultFeatures();
        }
    }

    /**
     * Calculates the magnitude spectrum of audio data using a simple FFT.
     *
     * @param audioData the audio data
     * @return the magnitude spectrum
     */
    private double[] calculateMagnitudeSpectrum(double[] audioData) {
        LoggingUtils.logDebug("Calculating magnitude spectrum");

        // Prepare for FFT (needs power of 2 size)
        int fftSize = N_FFT;
        double[] fftInput = new double[fftSize * 2]; // Complex numbers (real, imag)

        // Apply window function and prepare FFT input
        for (int i = 0; i < Math.min(audioData.length, fftSize); i++) {
            // Apply Hann window
            double window = 0.5 * (1 - Math.cos(2 * Math.PI * i / (Math.min(audioData.length, fftSize) - 1)));
            fftInput[i * 2] = audioData[i] * window;
        }

        // Perform FFT (simplified)
        fft(fftInput, fftSize);

        // Calculate magnitude spectrum
        double[] magnitudeSpectrum = new double[fftSize / 2];
        for (int i = 0; i < fftSize / 2; i++) {
            double real = fftInput[i * 2];
            double imag = fftInput[i * 2 + 1];
            magnitudeSpectrum[i] = Math.sqrt(real * real + imag * imag);
        }

        // Normalize magnitude spectrum
        double maxMagnitude = Arrays.stream(magnitudeSpectrum).max().orElse(1.0);
        if (maxMagnitude > 0) {
            for (int i = 0; i < magnitudeSpectrum.length; i++) {
                magnitudeSpectrum[i] /= maxMagnitude;
            }
        }

        return magnitudeSpectrum;
    }

    /**
     * Berechnet die Mel Frequency Cepstral Coefficients (MFCC).
     *
     * @param audioData  das Audio-Signal in Double-Array-Form.
     * @param sampleRate die Sample-Rate des Signals (z. B. 16000 Hz).
     * @return ein Float-Array mit den berechneten MFCC-Werten.
     */
    private float[] computeMFCC(double[] audioData, int sampleRate) {
        // Framegröße für FFT
        final int frameSize = N_FFT; // 2048 (definiert in der Klasse)
        final int numCoefficients = N_MFCC; // Anzahl der MFCC-Werte (z. B. 13)

        // 1. Frame-Signal aufteilen mit Hann-Fenster
        audioData = applyHannWindow(audioData, frameSize);

        // 2. FFT und Power-Spektrum berechnen
        double[] magnitudeSpectrum = calculateMagnitudeSpectrum(audioData);
        double[] powerSpectrum = new double[magnitudeSpectrum.length];
        for (int i = 0; i < magnitudeSpectrum.length; i++) {
            powerSpectrum[i] = magnitudeSpectrum[i] * magnitudeSpectrum[i]; // Quadratische Energie
        }

        // 3. Mel-Filterbank anwenden
        int numMelFilters = 26; // Typische Anzahl von Mel-Bins
        double[][] melFilterBank = createMelFilterBank(sampleRate, frameSize, numMelFilters);
        double[] melSpectrum = applyMelFilterBank(melFilterBank, powerSpectrum);

        // 4. Logarithmische Skalierung
        double[] logMelSpectrum = new double[melSpectrum.length];
        for (int i = 0; i < melSpectrum.length; i++) {
            logMelSpectrum[i] = Math.log10(melSpectrum[i] + 1e-6); // +1e-6 zur Vermeidung von log(0)
        }

        // 5. Diskrete Cosinus-Transformation (DCT) anwenden
        double[] mfcc = calculateDCT(logMelSpectrum, numCoefficients);

        // 6. MFCC-Werte normalisieren und zurückgeben
        float[] mfccFloat = new float[mfcc.length];
        for (int i = 0; i < mfcc.length; i++) {
            mfccFloat[i] = (float) mfcc[i]; // Konvertieren in Float
        }
        return mfccFloat;
    }

    private double[] applyHannWindow(double[] audioData, int frameSize) {
        double[] windowedData = new double[frameSize];
        for (int i = 0; i < Math.min(audioData.length, frameSize); i++) {
            double hann = 0.5 * (1 - Math.cos(2 * Math.PI * i / (frameSize - 1)));
            windowedData[i] = audioData[i] * hann;
        }
        return windowedData;
    }

    private double[][] createMelFilterBank(int sampleRate, int fftSize, int numMelFilters) {
        double[][] melFilters = new double[numMelFilters][fftSize / 2]; // Filterbänke

        double melLow = 0;
        double melHigh = 2595 * Math.log10(1 + (sampleRate / 2) / 700.0); // Obergrenze (Nyquist)

        // Mel-Frequenzen berechnen
        double[] melPoints = new double[numMelFilters + 2];
        for (int i = 0; i < melPoints.length; i++) {
            melPoints[i] = melLow + (i * (melHigh - melLow) / (numMelFilters + 1));
            melPoints[i] = 700 * (Math.pow(10, melPoints[i] / 2595) - 1); // Zurück zur Hertz-Skala
        }

        // FFT-Bins mit den Mel-Frequenzen verbinden
        int[] binPoints = new int[melPoints.length];
        for (int i = 0; i < melPoints.length; i++) {
            binPoints[i] = (int) Math.floor((fftSize + 1) * melPoints[i] / sampleRate);
        }

        // Filterbank füllen
        for (int i = 1; i < melPoints.length - 1; i++) {
            for (int j = binPoints[i - 1]; j < binPoints[i]; j++) {
                melFilters[i - 1][j] = (j - binPoints[i - 1]) / (double) (binPoints[i] - binPoints[i - 1]);
            }
            for (int j = binPoints[i]; j < binPoints[i + 1]; j++) {
                melFilters[i - 1][j] = (binPoints[i + 1] - j) / (double) (binPoints[i + 1] - binPoints[i]);
            }
        }
        return melFilters;
    }

    private double[] applyMelFilterBank(double[][] melFilterBank, double[] powerSpectrum) {
        double[] melSpectrum = new double[melFilterBank.length];
        for (int i = 0; i < melFilterBank.length; i++) {
            for (int j = 0; j < melFilterBank[i].length; j++) {
                melSpectrum[i] += melFilterBank[i][j] * powerSpectrum[j];
            }
        }
        return melSpectrum;
    }

    private double[] calculateDCT(double[] input, int numCoefficients) {
        double[] dct = new double[numCoefficients];
        for (int i = 0; i < numCoefficients; i++) {
            for (int j = 0; j < input.length; j++) {
                dct[i] += input[j] * Math.cos(Math.PI * i * (j + 0.5) / input.length);
            }
        }
        return dct;
    }




    /**
     * Performs an in-place Fast Fourier Transform (FFT) on the given data.
     * This is a simplified implementation of the Cooley-Tukey FFT algorithm.
     *
     * @param data the data to transform (real and imaginary parts interleaved)
     * @param n    the size of the FFT
     */
    private void fft(double[] data, int n) {
        // Bit-reverse permutation
        int shift = 1 + Integer.numberOfLeadingZeros(n);
        for (int i = 0; i < n; i++) {
            int j = Integer.reverse(i) >>> shift;
            if (j > i) {
                double temp = data[i * 2];
                data[i * 2] = data[j * 2];
                data[j * 2] = temp;
                temp = data[i * 2 + 1];
                data[i * 2 + 1] = data[j * 2 + 1];
                data[j * 2 + 1] = temp;
            }
        }

        // Cooley-Tukey FFT
        for (int len = 2; len <= n; len <<= 1) {
            double angle = -2 * Math.PI / len;
            double wr = Math.cos(angle);
            double wi = Math.sin(angle);

            for (int i = 0; i < n; i += len) {
                double tr = 1.0;
                double ti = 0.0;

                for (int j = 0; j < len / 2; j++) {
                    int p = i + j;
                    int q = i + j + len / 2;

                    double pRe = data[p * 2];
                    double pIm = data[p * 2 + 1];
                    double qRe = data[q * 2] * tr - data[q * 2 + 1] * ti;
                    double qIm = data[q * 2] * ti + data[q * 2 + 1] * tr;

                    data[p * 2] = pRe + qRe;
                    data[p * 2 + 1] = pIm + qIm;
                    data[q * 2] = pRe - qRe;
                    data[q * 2 + 1] = pIm - qIm;

                    double trNew = tr * wr - ti * wi;
                    ti = tr * wi + ti * wr;
                    tr = trNew;
                }
            }
        }
    }

    /**
     * Creates default features in case of error.
     *
     * @return the default features
     */
    private float[] createDefaultFeatures() {
        LoggingUtils.logDebug("Creating default features");

        // Create default features (all zeros)
        float[] defaultFeatures = new float[N_MFCC + N_CHROMA + N_CONTRAST];
        Arrays.fill(defaultFeatures, 0.0f);

        return defaultFeatures;
    }
}
