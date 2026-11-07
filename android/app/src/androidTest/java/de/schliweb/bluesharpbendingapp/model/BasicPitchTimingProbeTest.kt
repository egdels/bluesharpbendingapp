package de.schliweb.bluesharpbendingapp.model
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

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica
import de.schliweb.bluesharpbendingapp.utils.basicpitch.MultiPitchEngine
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.PI
import kotlin.math.sin

@RunWith(AndroidJUnit4::class)
class BasicPitchTimingProbeTest {
    @Test
    fun measureInference() {
        val harp = AbstractHarmonica.create(AbstractHarmonica.KEY.C, AbstractHarmonica.TUNE.RICHTER)
        val t0 = System.nanoTime()
        MultiPitchEngine.createBasicPitch(harp).use { engine ->
            Log.i("BPPROBE", "load ms=" + (System.nanoTime() - t0) / 1e6)
            val sr = 44100
            val audio = DoubleArray(sr) { i ->
                val t = i.toDouble() / sr
                doubleArrayOf(261.63, 329.63, 392.0).sumOf { f -> (1..6).sumOf { k -> sin(2 * PI * f * k * t) / k } } / 8
            }
            repeat(8) {
                val s = System.nanoTime()
                val r = engine.process(audio, sr.toDouble(), 0.0)
                Log.i("BPPROBE", "run $it total ms=" + (System.nanoTime() - s) / 1e6 + " inference ms=" + r.inferenceMillis() + " labels=" + r.mapping().labels())
            }
        }
    }
}
