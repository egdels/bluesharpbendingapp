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
package de.schliweb.bluesharpbendingapp.service;

import de.schliweb.bluesharpbendingapp.utils.LoggingUtils;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * {@link SettingsStore} on {@code java.util.prefs}: on macOS a property list in {@code
 * ~/Library/Preferences}, on Windows the registry, on Linux {@code ~/.java/.userPrefs}.
 */
public class PreferencesSettingsStore implements SettingsStore {

  /** Preferences node of the application settings. */
  public static final String NODE = "/de/schliweb/bluesharpbendingapp/settings";

  private final Preferences preferences;

  /** Creates a store on the settings node of the current user. */
  public PreferencesSettingsStore() {
    this(Preferences.userRoot().node(NODE));
  }

  /**
   * Creates a store on the given node.
   *
   * @param preferences the node
   */
  public PreferencesSettingsStore(Preferences preferences) {
    this.preferences = preferences;
  }

  @Override
  public String get(String key) {
    return preferences.get(key, null);
  }

  @Override
  public void put(String key, String value) {
    preferences.put(key, value);
  }

  @Override
  public void flush() {
    try {
      preferences.flush();
    } catch (BackingStoreException e) {
      LoggingUtils.logError("Failed to write the settings", e);
    }
  }
}
