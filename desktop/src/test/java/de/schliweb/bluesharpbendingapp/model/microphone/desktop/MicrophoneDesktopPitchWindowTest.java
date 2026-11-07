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

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.schliweb.bluesharpbendingapp.model.microphone.MicrophoneHandler;
import de.schliweb.bluesharpbendingapp.utils.ChordDetectionResult;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

/** Single-note detection on the desktop must analyse the newest part of each frame. */
class MicrophoneDesktopPitchWindowTest {

  private static final int SR = 44100;

  @Test
  void pitchComesFromTheMostRecentSamples() {
    MicrophoneDesktop microphone = new MicrophoneDesktop();
    microphone.setAlgorithm(1); // MPM
    AtomicReference<Double> pitch = new AtomicReference<>();
    microphone.setMicrophoneHandler(
        new MicrophoneHandler() {
          @Override
          public void handle(double frequency, double volume, ChordDetectionResult chord) {
            pitch.set(frequency);
          }
        });

    // One 4410-sample frame: 300 Hz, then 400 Hz in the last PITCH_WINDOW_SAMPLES samples.
    int samples = MicrophoneDesktop.BUFFER_SIZE / 2;
    int switchAt = samples - MicrophoneDesktop.PITCH_WINDOW_SAMPLES;
    byte[] frame = new byte[samples * 2];
    double phase = 0;
    for (int i = 0; i < samples; i++) {
      phase += 2 * Math.PI * (i < switchAt ? 300.0 : 400.0) / SR;
      short s = (short) Math.round(0.5 * Math.sin(phase) * 32767);
      frame[2 * i] = (byte) (s >> 8);
      frame[2 * i + 1] = (byte) s;
    }

    microphone.processAudioData(frame, frame.length);

    assertEquals(400.0, pitch.get(), 2.0);
  }
}
