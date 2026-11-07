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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/** Closing an engine must release the native ONNX Runtime session of its detector. */
class MultiPitchEngineCloseTest {

  /** A runner that only counts how often it was closed. */
  private static final class CountingRunner implements BasicPitchModelRunner, AutoCloseable {
    final AtomicInteger closed = new AtomicInteger();

    @Override
    public BasicPitchOutput run(double[] audioData, double sampleRate) {
      return new BasicPitchOutput(new float[0][88], new float[0][88], new float[0][264], 86.0);
    }

    @Override
    public void close() {
      closed.incrementAndGet();
    }
  }

  @Test
  void closingEngineClosesBasicPitchRunner() {
    CountingRunner runner = new CountingRunner();
    MultiPitchEngine engine =
        new MultiPitchEngine(
            MultiPitchEngine.Engine.BASIC_PITCH,
            new BasicPitchProvider(runner),
            AbstractHarmonica.create(AbstractHarmonica.KEY.C, AbstractHarmonica.TUNE.RICHTER),
            false);

    engine.close();

    assertEquals(1, runner.closed.get());
  }

  @Test
  void closingChordDetectorEngineIsHarmless() {
    MultiPitchEngine engine = MultiPitchEngine.createDefault();
    assertDoesNotThrow(engine::close);
  }

  @Test
  void realOnnxEngineCanBeClosedTwice() {
    MultiPitchEngine engine =
        MultiPitchEngine.createBasicPitch(
            AbstractHarmonica.create(AbstractHarmonica.KEY.C, AbstractHarmonica.TUNE.RICHTER));
    assumeTrue(
        engine.selectedEngine() == MultiPitchEngine.Engine.BASIC_PITCH,
        "ONNX Runtime or model unavailable; skipping");
    assertDoesNotThrow(engine::close);
    assertDoesNotThrow(engine::close);
  }
}
