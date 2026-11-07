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
package de.schliweb.bluesharpbendingapp.utils.basicpitch;

import de.schliweb.bluesharpbendingapp.utils.ChordDetectionResult;
import de.schliweb.bluesharpbendingapp.utils.PitchDetector;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that exposes the existing FFT-based {@code ChordDetector} (via {@link
 * PitchDetector#detectChord(double[], int)}) behind the common {@link MultiPitchDetector}
 * interface.
 *
 * <p>This is the second engine of the Roadmap Phase-0 A/B baseline comparison: it lets the
 * established {@code ChordDetector} run through the very same harness as the new {@link
 * BasicPitchProvider}, so both can be measured under identical conditions (see {@link
 * BaselineComparison}). The detected pitch frequencies (Hz) are converted into the engine-neutral
 * {@link NoteEvent} representation, using the aggregate confidence of the result for every pitch.
 */
public final class ChordDetectorMultiPitchAdapter implements MultiPitchDetector {

  @Override
  public List<NoteEvent> detect(double[] audioData, double sampleRate) {
    ChordDetectionResult result =
        PitchDetector.detectChord(audioData, (int) Math.round(sampleRate));
    List<NoteEvent> events = new ArrayList<>();
    List<Double> pitches = result.pitches();
    // The ChordDetector analyses the whole buffer, so each pitch spans the buffer. A zero duration
    // would be dropped by the post-processor's minimum-duration filter, and nothing would be
    // detected at all.
    double duration = sampleRate > 0 ? audioData.length / sampleRate : 0.0;
    for (int i = 0; i < pitches.size(); i++) {
      double freq = pitches.get(i);
      if (freq <= 0.0) {
        continue;
      }
      double midi = HarpTone.freqToMidi(freq);
      // The public ChordDetector reports one aggregate confidence for all pitches.
      double confidence = result.confidence();
      events.add(new NoteEvent(0.0, duration, midi, confidence));
    }
    return events;
  }

  @Override
  public String name() {
    return "ChordDetector";
  }
}
