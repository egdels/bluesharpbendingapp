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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.function.UnaryOperator;

/**
 * The directories of the desktop application, following the conventions of each platform:
 *
 * <ul>
 *   <li>macOS: data in {@code ~/Library/Application Support/BluesharpBendingApp}, logs in {@code
 *       ~/Library/Logs/BluesharpBendingApp}
 *   <li>Windows: data in {@code %APPDATA%\BluesharpBendingApp}, logs in {@code
 *       %LOCALAPPDATA%\BluesharpBendingApp\logs}
 *   <li>Linux and others: data in {@code $XDG_DATA_HOME/bluesharpbendingapp} (default {@code
 *       ~/.local/share}), logs in {@code $XDG_STATE_HOME/bluesharpbendingapp/logs} (default {@code
 *       ~/.local/state})
 * </ul>
 *
 * <p>The settings themselves are kept in {@code java.util.prefs}. Up to version 3.9 settings,
 * favorites and logs were all kept in {@code ~/BluesHarpBendingApp.tmp} ({@link #legacyDir()}).
 *
 * @param dataDir directory for application data (favorites)
 * @param logDir directory for the log files
 * @param legacyDir directory used by versions up to 3.9
 */
public record AppDirectories(Path dataDir, Path logDir, Path legacyDir) {

  private static final String APP_NAME = "BluesharpBendingApp";

  private static final String APP_NAME_LINUX = "bluesharpbendingapp";

  /** Name of the directory in the user's home used by versions up to 3.9. */
  static final String LEGACY_DIR_NAME = "BluesHarpBendingApp.tmp";

  /**
   * Returns the directories for the running platform and user.
   *
   * @return the directories
   */
  public static AppDirectories forCurrentUser() {
    return resolve(System.getProperty("os.name"), System.getProperty("user.home"), System::getenv);
  }

  /**
   * Resolves the directories for a platform.
   *
   * @param osName the value of the {@code os.name} system property
   * @param userHome the user's home directory
   * @param env lookup of environment variables (returns {@code null} if unset)
   * @return the directories
   */
  static AppDirectories resolve(String osName, String userHome, UnaryOperator<String> env) {
    String os = osName == null ? "" : osName.toLowerCase(Locale.ROOT);
    Path home = Paths.get(userHome);
    Path legacy = home.resolve(LEGACY_DIR_NAME);
    if (os.contains("mac")) {
      Path library = home.resolve("Library");
      return new AppDirectories(
          library.resolve("Application Support").resolve(APP_NAME),
          library.resolve("Logs").resolve(APP_NAME),
          legacy);
    }
    if (os.contains("win")) {
      Path roaming = envDir(env, "APPDATA", home.resolve("AppData").resolve("Roaming"));
      Path local = envDir(env, "LOCALAPPDATA", home.resolve("AppData").resolve("Local"));
      return new AppDirectories(
          roaming.resolve(APP_NAME), local.resolve(APP_NAME).resolve("logs"), legacy);
    }
    Path data = envDir(env, "XDG_DATA_HOME", home.resolve(".local").resolve("share"));
    Path state = envDir(env, "XDG_STATE_HOME", home.resolve(".local").resolve("state"));
    return new AppDirectories(
        data.resolve(APP_NAME_LINUX), state.resolve(APP_NAME_LINUX).resolve("logs"), legacy);
  }

  /** An absolute directory from an environment variable, or the fallback if unset or relative. */
  private static Path envDir(UnaryOperator<String> env, String name, Path fallback) {
    String value = env.apply(name);
    if (value == null || value.isBlank()) {
      return fallback;
    }
    Path path = Paths.get(value);
    return path.isAbsolute() ? path : fallback;
  }
}
