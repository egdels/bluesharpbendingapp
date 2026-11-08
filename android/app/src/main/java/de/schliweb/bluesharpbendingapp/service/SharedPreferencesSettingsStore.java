/*
 * Copyright (c) 2026 Christian Kierdorf
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

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

/** {@link SettingsStore} on Android {@link SharedPreferences} (file {@value #PREFS_NAME}). */
public class SharedPreferencesSettingsStore implements SettingsStore {

  /** Name of the preferences file. */
  public static final String PREFS_NAME = "settings";

  private final SharedPreferences preferences;

  /**
   * Creates the store.
   *
   * @param context any context; the application context is used
   */
  public SharedPreferencesSettingsStore(@NonNull Context context) {
    this.preferences =
        context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
  }

  @Override
  public String get(String key) {
    return preferences.getString(key, null);
  }

  @Override
  public void put(String key, String value) {
    // apply() writes asynchronously; reads see the new value immediately.
    preferences.edit().putString(key, value).apply();
  }
}
