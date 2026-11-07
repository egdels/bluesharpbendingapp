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

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Notes that already stopped inside the window are not reported with an active tail. */
class MultiPitchEngineActiveTailTest {

  /** C4 stopped at 0.5 s, E4 still sounding at the end of the 1 s window. */
  private static final MultiPitchDetector DETECTOR =
      new MultiPitchDetector() {
        @Override
        public List<NoteEvent> detect(double[] audioData, double sampleRate) {
          return List.of(new NoteEvent(0.0, 0.5, 60, 0.9), new NoteEvent(0.0, 0.998, 64, 0.9));
        }

        @Override
        public String name() {
          return "fake";
        }
      };

  private static MultiPitchEngine engine() {
    return new MultiPitchEngine(
        MultiPitchEngine.Engine.BASIC_PITCH,
        DETECTOR,
        AbstractHarmonica.create(AbstractHarmonica.KEY.C, AbstractHarmonica.TUNE.RICHTER),
        false);
  }

  @Test
  void withoutActiveTailAllNotesAreKept() {
    assertEquals(2, engine().process(new double[44100], 44100, 0).keptEvents().size());
  }

  @Test
  void withActiveTailOnlySoundingNotesAreKept() {
    MultiPitchEngine.EngineResult result = engine().process(new double[44100], 44100, 0, 0.15);
    assertEquals(1, result.keptEvents().size());
    assertEquals(64, Math.round(result.keptEvents().get(0).midi()));
    assertEquals(List.of("2B"), result.mapping().labels());
  }
}
