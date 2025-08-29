package de.schliweb.bluesharpbendingapp.favorites;
/*
 * MIT License
 *
 * Copyright (c) 2025 Christian Kierdorf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Android implementation of FavoritesStorage backed by SharedPreferences.
 * It persists a single JSON array string under a fixed key and preferences file,
 * matching the previous Android FavoriteManager behavior to ensure seamless migration.
 */
public final class AndroidSharedPreferencesFavoritesStorage implements FavoritesStorage {
    /** SharedPreferences file name, kept for backward compatibility. */
    public static final String PREFS_FILE = "de.schliweb.bluesharpbendingapp.prefs";
    /** Key for the favorites JSON payload (schema v1). */
    public static final String KEY_FAVORITES_V1 = "favorites_v1";

    private final SharedPreferences prefs;

    /**
     * Creates a storage instance using application Context with the default prefs file.
     */
    public AndroidSharedPreferencesFavoritesStorage(Context context) {
        this(context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE));
    }

    /**
     * Visible for testing/more control.
     */
    public AndroidSharedPreferencesFavoritesStorage(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    @Override
    public String read() {
        String raw = prefs.getString(KEY_FAVORITES_V1, "");
        return raw == null ? "" : raw;
    }

    @Override
    public void write(String json) {
        prefs.edit().putString(KEY_FAVORITES_V1, json != null ? json : "").apply();
    }
}
