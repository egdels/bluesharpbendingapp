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

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Runs the pitch detection benchmark and writes a Markdown report.
 *
 * <p>Tagged {@code benchmark} so it does not run with the normal tests; run it with {@code
 * ./gradlew :base:pitchBenchmark}. The report is written to {@code
 * base/build/reports/pitch-benchmark/report.md}.
 */
@Tag("benchmark")
class PitchBenchmarkTest {

  /** Desktop: 0.1 s buffers with 50 % overlap (see MicrophoneDesktop). */
  private static final int DESKTOP_FRAME = 4410;

  /** Android: the minimum buffer of 4096 bytes of PCM float (see MicrophoneAndroid). */
  private static final int ANDROID_FRAME = 1024;

  @Test
  void runBenchmark() throws IOException {
    StringBuilder report = new StringBuilder();
    report.append("# Pitch detection benchmark (synthetic signals)\n\n");
    report.append("Generated ").append(LocalDate.now()).append(" with ");
    report.append(Runtime.version()).append(" on ").append(System.getProperty("os.arch"));
    report.append(". Keys ").append(PitchBenchmark.KEYS).append(", Richter tuning.\n\n");

    report.append(
        PitchBenchmark.monoMarkdown(
            "Monophonic, desktop frames (" + DESKTOP_FRAME + " samples, 50 % hop)",
            PitchBenchmark.runMono(
                PitchBenchmark.defaultMonoDetectors(), DESKTOP_FRAME, DESKTOP_FRAME / 2)));
    report.append(
        PitchBenchmark.monoMarkdown(
            "Monophonic, Android frames (" + ANDROID_FRAME + " samples, no overlap)",
            PitchBenchmark.runMono(
                PitchBenchmark.defaultMonoDetectors(), ANDROID_FRAME, ANDROID_FRAME)));
    report.append(
        PitchBenchmark.polyMarkdown(
            "Polyphonic, desktop frames (" + DESKTOP_FRAME + " samples, 50 % hop)",
            PitchBenchmark.runPoly(
                PitchBenchmark.defaultPolyDetectors(), DESKTOP_FRAME, DESKTOP_FRAME / 2)));

    Path dir =
        Path.of(System.getProperty("pitchBenchmark.reportDir", "build/reports/pitch-benchmark"));
    Files.createDirectories(dir);
    Path file = dir.resolve("report.md");
    Files.writeString(file, report, StandardCharsets.UTF_8);
    System.out.println(report);
    System.out.println("Report written to " + file.toAbsolutePath());
    assertFalse(report.isEmpty());
  }
}
