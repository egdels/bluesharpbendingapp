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
package de.schliweb.bluesharpbendingapp.model.microphone.desktop;

/**
 * Provides audio signal preprocessing filters for the desktop microphone implementation.
 *
 * <p>This class implements several signal processing techniques to improve pitch detection accuracy
 * on desktop platforms, where raw audio input lacks the automatic preprocessing (AGC, noise
 * suppression) that Android provides via its AudioSource.MIC API.
 *
 * <p>The following filters are available:
 *
 * <ul>
 *   <li><b>High-pass filter</b>: Removes low-frequency noise (e.g., mains hum at 50/60 Hz, fan
 *       noise) that can interfere with pitch detection.
 *   <li><b>Noise gate</b>: Suppresses audio frames below a configurable RMS threshold, preventing
 *       background noise from triggering false pitch detections.
 *   <li><b>Noise reduction</b>: Uses spectral subtraction based on a noise profile captured during
 *       initial silence to reduce stationary background noise.
 * </ul>
 */
public class AudioSignalFilter {

  /**
   * Coefficient for the single-pole high-pass filter. A value of 0.98 provides a cutoff frequency
   * of approximately 140 Hz at a 44100 Hz sample rate, effectively removing mains hum (50/60 Hz)
   * and low-frequency rumble while preserving harmonica frequencies.
   */
  private static final double HP_ALPHA = 0.98;

  /**
   * Minimum RMS (Root Mean Square) amplitude threshold for the noise gate. Audio frames with an RMS
   * value below this threshold are considered background noise and will be suppressed. The value of
   * 0.01 provides a good balance between filtering noise and preserving quiet but intentional
   * playing.
   */
  static final double MIN_RMS_THRESHOLD = 0.001;

  /**
   * Number of initial audio frames used to build the noise profile for spectral subtraction. A
   * value of 10 frames provides sufficient data to estimate the stationary noise floor without
   * requiring a long initialization period.
   */
  private static final int NOISE_PROFILE_FRAMES = 10;

  /**
   * Over-subtraction factor for spectral noise reduction. Values greater than 1.0 provide more
   * aggressive noise removal at the cost of potential signal distortion. A value of 1.5 provides
   * moderate noise reduction suitable for real-time audio processing.
   */
  private static final double NOISE_SUBTRACTION_FACTOR = 1.5;

  /**
   * Previous raw sample value, used by the high-pass filter to compute the difference between
   * consecutive samples.
   */
  private double previousSample = 0;

  /**
   * Previous filtered output value, used by the high-pass filter to maintain filter state across
   * consecutive samples.
   */
  private double previousFiltered = 0;

  /**
   * Accumulated noise profile representing the average amplitude at each sample position. This
   * profile is built from the first {@link #NOISE_PROFILE_FRAMES} audio frames and used for
   * spectral subtraction noise reduction.
   */
  private double[] noiseProfile;

  /**
   * Counter tracking the number of audio frames processed during the noise profile calibration
   * phase. Once this reaches {@link #NOISE_PROFILE_FRAMES}, the noise profile is finalized and
   * noise reduction becomes active.
   */
  private int noiseProfileFrameCount = 0;

  /**
   * Flag indicating whether the noise profile has been fully calibrated and noise reduction is
   * ready to be applied.
   */
  private boolean noiseProfileReady = false;

  /**
   * Applies a single-pole high-pass filter to the audio data in-place.
   *
   * <p>This filter removes low-frequency components below approximately 140 Hz (at 44100 Hz sample
   * rate), which helps eliminate mains hum (50/60 Hz), fan noise, and other low-frequency
   * interference that can degrade pitch detection accuracy.
   *
   * <p>The filter uses the difference equation: {@code y[n] = alpha * (y[n-1] + x[n] - x[n-1])}
   *
   * @param audioData the audio sample array to filter; modified in-place
   */
  public void applyHighPassFilter(double[] audioData) {
    for (int i = 0; i < audioData.length; i++) {
      double filtered = HP_ALPHA * (previousFiltered + audioData[i] - previousSample);
      previousSample = audioData[i];
      previousFiltered = filtered;
      audioData[i] = filtered;
    }
  }

  /**
   * Determines whether the audio frame should be suppressed by the noise gate.
   *
   * <p>The noise gate compares the RMS (Root Mean Square) amplitude of the audio frame against the
   * configured threshold {@link #MIN_RMS_THRESHOLD}. Frames below this threshold are considered
   * background noise and should not be processed for pitch detection.
   *
   * @param rms the RMS amplitude of the audio frame
   * @return {@code true} if the frame is below the noise gate threshold and should be suppressed;
   *     {@code false} if the frame contains a valid signal
   */
  public boolean isBelowNoiseGate(double rms) {
    return rms < MIN_RMS_THRESHOLD;
  }

  /**
   * Updates the noise profile with the given audio frame during the calibration phase, or applies
   * spectral subtraction noise reduction if the profile is ready.
   *
   * <p>During the first {@link #NOISE_PROFILE_FRAMES} frames, this method accumulates sample
   * amplitudes to build an average noise floor profile. After calibration is complete, subsequent
   * calls subtract the noise profile from the audio data to reduce stationary background noise.
   *
   * <p>The noise reduction uses over-subtraction with a factor of {@link #NOISE_SUBTRACTION_FACTOR}
   * and applies spectral flooring to prevent negative amplitudes.
   *
   * @param audioData the audio sample array to process; modified in-place during the noise
   *     reduction phase
   */
  public void applyNoiseReduction(double[] audioData) {
    if (!noiseProfileReady) {
      calibrateNoiseProfile(audioData);
    } else {
      subtractNoise(audioData);
    }
  }

  /**
   * Accumulates audio frame data into the noise profile during the calibration phase.
   *
   * <p>Each frame's absolute sample values are added to the noise profile array. Once {@link
   * #NOISE_PROFILE_FRAMES} frames have been accumulated, the profile is averaged and the {@link
   * #noiseProfileReady} flag is set to {@code true}.
   *
   * @param audioData the audio sample array from which to build the noise profile
   */
  private void calibrateNoiseProfile(double[] audioData) {
    if (noiseProfile == null) {
      noiseProfile = new double[audioData.length];
    }
    int len = Math.min(audioData.length, noiseProfile.length);
    for (int i = 0; i < len; i++) {
      noiseProfile[i] += Math.abs(audioData[i]);
    }
    noiseProfileFrameCount++;
    if (noiseProfileFrameCount >= NOISE_PROFILE_FRAMES) {
      for (int i = 0; i < noiseProfile.length; i++) {
        noiseProfile[i] /= noiseProfileFrameCount;
      }
      noiseProfileReady = true;
    }
  }

  /**
   * Applies spectral subtraction to the audio data using the calibrated noise profile.
   *
   * <p>For each sample, the noise floor estimate (multiplied by the over-subtraction factor) is
   * subtracted from the absolute signal value. The result preserves the original sign of the
   * sample. Spectral flooring ensures that the output amplitude never goes below zero.
   *
   * @param audioData the audio sample array to process; modified in-place
   */
  private void subtractNoise(double[] audioData) {
    int len = Math.min(audioData.length, noiseProfile.length);
    for (int i = 0; i < len; i++) {
      double magnitude = Math.abs(audioData[i]);
      double reduced = magnitude - NOISE_SUBTRACTION_FACTOR * noiseProfile[i];
      audioData[i] = Math.signum(audioData[i]) * Math.max(reduced, 0);
    }
  }

  /**
   * Resets the filter state, clearing the high-pass filter history and noise profile.
   *
   * <p>This method should be called when the microphone is reopened or when the audio source
   * changes, to ensure that stale filter state does not affect the new audio stream.
   */
  public void reset() {
    previousSample = 0;
    previousFiltered = 0;
    noiseProfile = null;
    noiseProfileFrameCount = 0;
    noiseProfileReady = false;
  }

  /**
   * Returns whether the noise profile calibration is complete.
   *
   * @return {@code true} if the noise profile has been fully calibrated
   */
  public boolean isNoiseProfileReady() {
    return noiseProfileReady;
  }
}
