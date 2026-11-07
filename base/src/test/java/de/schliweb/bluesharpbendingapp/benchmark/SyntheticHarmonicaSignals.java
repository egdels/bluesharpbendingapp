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
package de.schliweb.bluesharpbendingapp.benchmark;

import java.util.Random;

/**
 * Generates synthetic, harmonica-like test signals with a known ground truth for the pitch
 * detection benchmark.
 *
 * <p>A harmonica reed produces a strongly harmonic, slightly bright spectrum. The model used here
 * is a sum of harmonics with amplitudes {@code 1 / k^rolloff}, a short attack, optional breath
 * noise and an optional frequency trajectory (bend, vibrato). All randomness is seeded, so every
 * run produces identical signals.
 */
public final class SyntheticHarmonicaSignals {

  /** The sample rate used by the desktop and Android microphones. */
  public static final int SAMPLE_RATE = 44100;

  private SyntheticHarmonicaSignals() {}

  /**
   * Describes the time-varying fundamental frequency of a tone.
   *
   * <p>Implementations return the instantaneous fundamental in Hz for a time in seconds.
   */
  @FunctionalInterface
  public interface Trajectory {
    /**
     * Returns the instantaneous fundamental frequency.
     *
     * @param timeSeconds the time since the start of the tone in seconds
     * @return the fundamental frequency in Hz
     */
    double frequencyAt(double timeSeconds);
  }

  /**
   * A constant pitch.
   *
   * @param frequency the fundamental frequency in Hz
   * @return the trajectory
   */
  public static Trajectory steady(double frequency) {
    return t -> frequency;
  }

  /**
   * A bend: holds the start pitch, then glides linearly (in cents) down to {@code bendCents} below
   * it and holds the bent pitch, like a draw bend on a harmonica.
   *
   * @param frequency the unbent fundamental frequency in Hz
   * @param bendCents the bend depth in cents (positive value bends downwards)
   * @param holdSeconds the time the unbent pitch is held before the glide starts
   * @param glideSeconds the duration of the glide
   * @return the trajectory
   */
  public static Trajectory bend(
      double frequency, double bendCents, double holdSeconds, double glideSeconds) {
    return t -> {
      double cents;
      if (t < holdSeconds) {
        cents = 0;
      } else if (t < holdSeconds + glideSeconds) {
        cents = -bendCents * (t - holdSeconds) / glideSeconds;
      } else {
        cents = -bendCents;
      }
      return frequency * Math.pow(2, cents / 1200.0);
    };
  }

  /**
   * A vibrato around a centre pitch.
   *
   * @param frequency the centre frequency in Hz
   * @param depthCents the vibrato depth (peak deviation) in cents
   * @param rateHz the vibrato rate in Hz
   * @return the trajectory
   */
  public static Trajectory vibrato(double frequency, double depthCents, double rateHz) {
    return t -> frequency * Math.pow(2, depthCents * Math.sin(2 * Math.PI * rateHz * t) / 1200.0);
  }

  /** Parameters of the harmonic tone model. */
  public record ToneModel(
      int harmonics, double rolloff, double fundamentalGain, double attackSeconds) {

    /** A typical harmonica tone: 10 harmonics, moderate roll-off, full fundamental. */
    public static final ToneModel HARMONICA = new ToneModel(10, 0.8, 1.0, 0.02);

    /**
     * A tone with a weak fundamental (-12 dB), typical for low reeds and a common cause of octave
     * errors.
     */
    public static final ToneModel WEAK_FUNDAMENTAL = new ToneModel(10, 0.8, 0.25, 0.02);

    /** A pure sine, the easiest case. */
    public static final ToneModel SINE = new ToneModel(1, 1.0, 1.0, 0.0);
  }

  /**
   * Renders a tone following a trajectory.
   *
   * @param trajectory the fundamental frequency over time
   * @param model the harmonic tone model
   * @param durationSeconds the length of the signal
   * @param seed the seed for the random harmonic phases
   * @return the samples, normalised to a peak of about 0.5
   */
  public static double[] tone(
      Trajectory trajectory, ToneModel model, double durationSeconds, long seed) {
    int n = (int) Math.round(durationSeconds * SAMPLE_RATE);
    double[] out = new double[n];
    Random random = new Random(seed);
    double[] phase = new double[model.harmonics()];
    double[] gain = new double[model.harmonics()];
    for (int k = 0; k < model.harmonics(); k++) {
      phase[k] = random.nextDouble() * 2 * Math.PI;
      gain[k] = 1.0 / Math.pow(k + 1, model.rolloff());
    }
    gain[0] *= model.fundamentalGain();
    double nyquist = SAMPLE_RATE / 2.0;
    for (int i = 0; i < n; i++) {
      double t = (double) i / SAMPLE_RATE;
      double f0 = trajectory.frequencyAt(t);
      double sample = 0;
      for (int k = 0; k < model.harmonics(); k++) {
        double fk = f0 * (k + 1);
        // Integrate the phase so frequency changes stay continuous.
        phase[k] += 2 * Math.PI * fk / SAMPLE_RATE;
        if (fk < nyquist) {
          sample += gain[k] * Math.sin(phase[k]);
        }
      }
      double envelope = model.attackSeconds() > 0 ? Math.min(1.0, t / model.attackSeconds()) : 1.0;
      out[i] = sample * envelope;
    }
    return normalize(out, 0.5);
  }

  /**
   * Mixes several signals of equal length into one, e.g. for chords.
   *
   * @param signals the signals to mix
   * @return the mixed signal, normalised to a peak of about 0.5
   */
  public static double[] mix(double[]... signals) {
    double[] out = new double[signals[0].length];
    for (double[] s : signals) {
      for (int i = 0; i < out.length; i++) {
        out[i] += s[i];
      }
    }
    return normalize(out, 0.5);
  }

  /**
   * Adds breath-like noise (low-pass filtered white noise) at a given signal-to-noise ratio.
   *
   * @param signal the clean signal (not modified)
   * @param snrDb the signal-to-noise ratio in dB
   * @param seed the noise seed
   * @return a new array with noise added
   */
  public static double[] withBreathNoise(double[] signal, double snrDb, long seed) {
    double[] noise = breathNoise(signal.length, seed);
    double signalRms = rms(signal);
    double noiseRms = rms(noise);
    double scale = noiseRms > 0 ? signalRms / noiseRms / Math.pow(10, snrDb / 20.0) : 0;
    double[] out = new double[signal.length];
    for (int i = 0; i < out.length; i++) {
      out[i] = signal[i] + noise[i] * scale;
    }
    return out;
  }

  /**
   * Breath noise only, without any tone. Used to measure false detections.
   *
   * @param durationSeconds the length of the signal
   * @param seed the noise seed
   * @return the noise, normalised to a peak of about 0.3
   */
  public static double[] breathNoiseOnly(double durationSeconds, long seed) {
    return normalize(breathNoise((int) Math.round(durationSeconds * SAMPLE_RATE), seed), 0.3);
  }

  private static double[] breathNoise(int n, long seed) {
    Random random = new Random(seed);
    double[] out = new double[n];
    // One-pole low-pass at roughly 2 kHz gives a soft, breath-like colour.
    double alpha = 1 - Math.exp(-2 * Math.PI * 2000.0 / SAMPLE_RATE);
    double y = 0;
    for (int i = 0; i < n; i++) {
      y += alpha * (random.nextGaussian() - y);
      out[i] = y;
    }
    return out;
  }

  /**
   * Returns the root mean square of a signal.
   *
   * @param signal the signal
   * @return the RMS value
   */
  public static double rms(double[] signal) {
    double sum = 0;
    for (double v : signal) {
      sum += v * v;
    }
    return Math.sqrt(sum / Math.max(1, signal.length));
  }

  private static double[] normalize(double[] signal, double peak) {
    double max = 0;
    for (double v : signal) {
      max = Math.max(max, Math.abs(v));
    }
    if (max == 0) {
      return signal;
    }
    double scale = peak / max;
    for (int i = 0; i < signal.length; i++) {
      signal[i] *= scale;
    }
    return signal;
  }

  /**
   * Returns the deviation of a frequency from a reference in cents.
   *
   * @param frequency the measured frequency in Hz
   * @param reference the reference frequency in Hz
   * @return the deviation in cents (positive means sharp)
   */
  public static double cents(double frequency, double reference) {
    return 1200.0 * Math.log(frequency / reference) / Math.log(2);
  }
}
