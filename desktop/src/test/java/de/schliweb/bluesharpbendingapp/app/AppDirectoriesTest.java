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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AppDirectoriesTest {

  @Test
  void macOs() {
    AppDirectories d =
        AppDirectories.resolve("Mac OS X", "/Users/u", Map.<String, String>of()::get);
    assertEquals(
        Paths.get("/Users/u/Library/Application Support/BluesharpBendingApp"), d.dataDir());
    assertEquals(Paths.get("/Users/u/Library/Logs/BluesharpBendingApp"), d.logDir());
    assertEquals(Paths.get("/Users/u/BluesHarpBendingApp.tmp"), d.legacyDir());
  }

  @Test
  void linuxDefaultsAndXdgVariables() {
    AppDirectories d = AppDirectories.resolve("Linux", "/home/u", Map.<String, String>of()::get);
    assertEquals(Paths.get("/home/u/.local/share/bluesharpbendingapp"), d.dataDir());
    assertEquals(Paths.get("/home/u/.local/state/bluesharpbendingapp/logs"), d.logDir());

    AppDirectories xdg =
        AppDirectories.resolve(
            "Linux", "/home/u", Map.of("XDG_DATA_HOME", "/d", "XDG_STATE_HOME", "/s")::get);
    assertEquals(Paths.get("/d/bluesharpbendingapp"), xdg.dataDir());
    assertEquals(Paths.get("/s/bluesharpbendingapp/logs"), xdg.logDir());

    // Relative values are invalid per the XDG specification and are ignored.
    AppDirectories relative =
        AppDirectories.resolve("Linux", "/home/u", Map.of("XDG_DATA_HOME", "rel")::get);
    assertEquals(Paths.get("/home/u/.local/share/bluesharpbendingapp"), relative.dataDir());
  }

  @Test
  void windowsUsesAppDataVariables() {
    String roaming = Paths.get("/r").toAbsolutePath().toString();
    String local = Paths.get("/l").toAbsolutePath().toString();
    AppDirectories d =
        AppDirectories.resolve(
            "Windows 11", "/home/u", Map.of("APPDATA", roaming, "LOCALAPPDATA", local)::get);
    assertEquals(Paths.get(roaming, "BluesharpBendingApp"), d.dataDir());
    assertEquals(Paths.get(local, "BluesharpBendingApp", "logs"), d.logDir());
  }
}
