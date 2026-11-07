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

import androidx.test.ext.junit.runners.AndroidJUnit4
import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica
import de.schliweb.bluesharpbendingapp.utils.basicpitch.MultiPitchEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.PI
import kotlin.math.sin

/**
 * Runs the Basic Pitch model with the source-built, reduced ONNX Runtime on a device or emulator.
 * Requires scripts/build_onnxruntime_android.sh to have been run for the device's ABI.
 */
@RunWith(AndroidJUnit4::class)
class BasicPitchOnDeviceTest {

    @Test
    fun basicPitchDetectsCMajorBlowChord() {
        val harp = AbstractHarmonica.create(AbstractHarmonica.KEY.C, AbstractHarmonica.TUNE.RICHTER)
        MultiPitchEngine.createBasicPitch(harp).use { engine ->
            assertEquals(
                "ONNX Runtime or the model could not be loaded",
                MultiPitchEngine.Engine.BASIC_PITCH,
                engine.selectedEngine(),
            )
            val sampleRate = 44100
            val freqs = doubleArrayOf(261.63, 329.63, 392.00)
            val audio = DoubleArray(sampleRate) { i ->
                val t = i.toDouble() / sampleRate
                freqs.sumOf { f -> (1..6).sumOf { k -> sin(2 * PI * f * k * t) / k } } / 8
            }
            val result = engine.process(audio, sampleRate.toDouble(), 0.0)
            val labels = result.mapping().labels()
            assertTrue("expected 1B, 2B, 3B in $labels", labels.containsAll(listOf("1B", "2B", "3B")))
            assertEquals(MultiPitchEngine.Engine.BASIC_PITCH, result.engineUsed())
        }
    }
}
