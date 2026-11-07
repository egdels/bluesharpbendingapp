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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;
import de.schliweb.bluesharpbendingapp.model.microphone.MicrophoneHandler;
import de.schliweb.bluesharpbendingapp.utils.ChordDetectionResult;
import de.schliweb.bluesharpbendingapp.utils.basicpitch.MultiPitchChordTracker;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * End-to-end test of the optional Basic Pitch chord engine on the desktop: frames go through the
 * capture path into the tracker (real ONNX model), and the processing path hands the result to the
 * microphone handler.
 */
class MicrophoneDesktopChordEngineTest {

  private static final int SR = 44100;
  private static final Harmonica C_RICHTER =
      AbstractHarmonica.create(AbstractHarmonica.KEY.C, AbstractHarmonica.TUNE.RICHTER);

  private final MicrophoneDesktop microphone = new MicrophoneDesktop();

  @AfterEach
  void tearDown() {
    microphone.setChordEngine(0, C_RICHTER); // closes the tracker
  }

  private MultiPitchChordTracker tracker() throws Exception {
    Field f = MicrophoneDesktop.class.getDeclaredField("chordTracker");
    f.setAccessible(true);
    return ((AtomicReference<?>) f.get(microphone)).get() instanceof MultiPitchChordTracker t
        ? t
        : null;
  }

  /** 16-bit big-endian PCM frames of a C major blow chord (C4, E4, G4 with harmonics). */
  private static byte[][] chordFrames(double seconds, int frameSamples) {
    int frames = (int) (seconds * SR / frameSamples);
    byte[][] out = new byte[frames][2 * frameSamples];
    double[] freqs = {261.63, 329.63, 392.00};
    for (int f = 0; f < frames; f++) {
      for (int i = 0; i < frameSamples; i++) {
        double t = (double) (f * frameSamples + i) / SR;
        double v = 0;
        for (double fr : freqs) {
          for (int k = 1; k <= 6; k++) {
            v += Math.sin(2 * Math.PI * fr * k * t) / k;
          }
        }
        short s = (short) Math.round(v / 8 * 32767);
        out[f][2 * i] = (byte) (s >> 8);
        out[f][2 * i + 1] = (byte) s;
      }
    }
    return out;
  }

  @Test
  void defaultEngineHasNoTracker() throws Exception {
    microphone.setChordEngine(0, C_RICHTER);
    assertNull(tracker());
  }

  @Test
  void basicPitchChordReachesTheHandler() throws Exception {
    microphone.setChordDetectionEnabled(true);
    microphone.setChordEngine(1, C_RICHTER);
    org.junit.jupiter.api.Assumptions.assumeTrue(
        tracker() != null, "ONNX Runtime or Basic Pitch model unavailable; skipping");

    AtomicReference<ChordDetectionResult> received = new AtomicReference<>();
    microphone.setMicrophoneHandler(
        new MicrophoneHandler() {
          @Override
          public void handle(double frequency, double volume, ChordDetectionResult chord) {
            if (chord.hasPitches()) {
              received.set(chord);
            }
          }
        });

    Method feed =
        MicrophoneDesktop.class.getDeclaredMethod("feedChordTracker", byte[].class, int.class);
    feed.setAccessible(true);
    int frameSamples = 4410;
    byte[][] frames = chordFrames(1.5, frameSamples);
    for (byte[] frame : frames) {
      feed.invoke(microphone, frame, frame.length);
    }

    // The analysis runs asynchronously; poll the processing path until a chord arrives.
    long deadline = System.currentTimeMillis() + 15_000;
    byte[] last = frames[frames.length - 1];
    while (received.get() == null && System.currentTimeMillis() < deadline) {
      microphone.processAudioData(last, last.length);
      Thread.sleep(50);
    }

    ChordDetectionResult chord = received.get();
    assertNotNull(chord, "no Basic Pitch chord result reached the handler");
    List<Double> pitches = chord.pitches();
    for (double expected : new double[] {261.63, 329.63, 392.00}) {
      assertTrue(
          pitches.stream().anyMatch(p -> Math.abs(p - expected) < 1.0),
          expected + " missing in " + pitches);
    }
  }
}
