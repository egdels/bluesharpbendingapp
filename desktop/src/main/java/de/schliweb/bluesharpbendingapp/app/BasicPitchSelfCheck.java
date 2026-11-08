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
package de.schliweb.bluesharpbendingapp.app;

import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.utils.basicpitch.MultiPitchEngine;
import java.util.List;

/**
 * Packaging self check for the desktop installers: loads ONNX Runtime and the Basic Pitch model
 * from the packaged jar with the packaged (jlink) runtime and analyses a C major chord. Exits with
 * status 1 if Basic Pitch is not available or detects the wrong notes, so a CI build fails when the
 * native libraries or a required JDK module are missing from an installer.
 *
 * <p>Run by the Gradle task {@code :desktop:verifyPackagedBasicPitch}.
 */
public final class BasicPitchSelfCheck {

  private BasicPitchSelfCheck() {}

  /**
   * Runs the check.
   *
   * @param args not used
   */
  public static void main(String[] args) {
    System.out.println(
        "os.name="
            + System.getProperty("os.name")
            + " os.arch="
            + System.getProperty("os.arch")
            + " java="
            + Runtime.version());
    // The settings are kept in java.util.prefs (PreferencesSettingsStore); the jlink runtime must
    // contain the module, or the application fails at startup.
    if (ModuleLayer.boot().findModule("java.prefs").isEmpty()) {
      System.out.println("FAILED: module java.prefs missing in the runtime");
      System.exit(1);
    }
    try (MultiPitchEngine engine =
        MultiPitchEngine.createBasicPitch(
            AbstractHarmonica.create(AbstractHarmonica.KEY.C, AbstractHarmonica.TUNE.RICHTER))) {
      if (engine.selectedEngine() != MultiPitchEngine.Engine.BASIC_PITCH) {
        System.out.println("FAILED: ONNX Runtime or the Basic Pitch model could not be loaded");
        System.exit(1);
      }
      int sampleRate = 44100;
      double[] audio = new double[sampleRate];
      double[] freqs = {261.63, 329.63, 392.00};
      for (int i = 0; i < audio.length; i++) {
        double t = (double) i / sampleRate;
        for (double f : freqs) {
          for (int k = 1; k <= 6; k++) {
            audio[i] += Math.sin(2 * Math.PI * f * k * t) / k / 8;
          }
        }
      }
      List<String> labels = engine.process(audio, sampleRate, 0).mapping().labels();
      System.out.println("Basic Pitch labels=" + labels);
      if (!labels.containsAll(List.of("1B", "2B", "3B"))) {
        System.out.println("FAILED: expected 1B, 2B and 3B");
        System.exit(1);
      }
      System.out.println("OK: Basic Pitch works in the packaged application");
    }
  }
}
