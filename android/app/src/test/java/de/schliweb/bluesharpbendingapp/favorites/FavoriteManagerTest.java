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

import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.Collections;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Basic unit tests for FavoriteManager covering toggle, load, rename, remove, reorder.
 */
public class FavoriteManagerTest {

    /** Simple in-memory SharedPreferences implementation for unit tests. */
    static class InMemorySharedPreferences implements SharedPreferences {
        private final java.util.Map<String, Object> data = new java.util.concurrent.ConcurrentHashMap<>();

        @Override public Map<String, ?> getAll() { return new java.util.HashMap<String, Object>(data); }
        @Override public String getString(String key, String defValue) { Object v = data.get(key); return v instanceof String ? (String) v : defValue; }
        @Override public Set<String> getStringSet(String key, Set<String> defValues) {
            Object v = data.get(key);
            if (v instanceof Set<?>) {
                Set<?> raw = (Set<?>) v;
                java.util.Set<String> safe = new java.util.HashSet<>();
                for (Object o : raw) {
                    if (o instanceof String) {
                        safe.add((String) o);
                    } else {
                        return defValues;
                    }
                }
                return safe;
            }
            return defValues;
        }
        @Override public int getInt(String key, int defValue) { Object v = data.get(key); return v instanceof Integer ? (Integer) v : defValue; }
        @Override public long getLong(String key, long defValue) { Object v = data.get(key); return v instanceof Long ? (Long) v : defValue; }
        @Override public float getFloat(String key, float defValue) { Object v = data.get(key); return v instanceof Float ? (Float) v : defValue; }
        @Override public boolean getBoolean(String key, boolean defValue) { Object v = data.get(key); return v instanceof Boolean ? (Boolean) v : defValue; }
        @Override public boolean contains(String key) { return data.containsKey(key); }
        @Override public Editor edit() { return new InMemoryEditor(); }
        @Override public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) { }
        @Override public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) { }

        class InMemoryEditor implements Editor {
            private final java.util.Map<String, Object> pending = new java.util.HashMap<>();
            private final java.util.Set<String> removals = new java.util.HashSet<>();
            @Override public Editor putString(String key, String value) { if (value == null) { removals.add(key); pending.remove(key); } else { pending.put(key, value); } return this; }
            @Override public Editor putStringSet(String key, Set<String> values) { pending.put(key, values); return this; }
            @Override public Editor putInt(String key, int value) { pending.put(key, value); return this; }
            @Override public Editor putLong(String key, long value) { pending.put(key, value); return this; }
            @Override public Editor putFloat(String key, float value) { pending.put(key, value); return this; }
            @Override public Editor putBoolean(String key, boolean value) { pending.put(key, value); return this; }
            @Override public Editor remove(String key) { removals.add(key); return this; }
            @Override public Editor clear() { data.clear(); pending.clear(); removals.clear(); return this; }
            @Override public boolean commit() { apply(); return true; }
            @Override public void apply() { for (String r : removals) { data.remove(r); } data.putAll(pending); pending.clear(); removals.clear(); }
        }
    }

    private FavoriteManager manager;
    private SharedPreferences prefs;

    static class InlineExecutorService extends AbstractExecutorService {
        private volatile boolean shutdown = false;
        @Override public void shutdown() { shutdown = true; }
        @Override public List<Runnable> shutdownNow() { shutdown = true; return Collections.emptyList(); }
        @Override public boolean isShutdown() { return shutdown; }
        @Override public boolean isTerminated() { return shutdown; }
        @Override public boolean awaitTermination(long timeout, TimeUnit unit) { return true; }
        @Override public void execute(Runnable command) { command.run(); }
    }

    @Before
    public void setUp() {
        // In-memory SharedPreferences for deterministic and isolated unit tests
        prefs = new InMemorySharedPreferences();
        ExecutorService executor = new InlineExecutorService();
        AndroidSharedPreferencesFavoritesStorage storage = new AndroidSharedPreferencesFavoritesStorage(prefs);
        manager = new FavoriteManager(storage, executor);
        // Clear
        for (Favorite f : manager.load()) {
            manager.remove(f.id);
        }
    }

    @Test
    public void testToggleCreateAndRemove() {
        assertTrue(manager.load().isEmpty());
        Favorite f = manager.toggle("RICHTER", null, "C", 10, null);
        assertNotNull(f);
        List<Favorite> list = manager.load();
        assertEquals(1, list.size());
        assertTrue(manager.isCurrentFavorite("RICHTER", null, "C", 10));

        Favorite removed = manager.toggle("RICHTER", null, "C", 10, null);
        assertNull(removed);
        assertFalse(manager.isCurrentFavorite("RICHTER", null, "C", 10));
        assertTrue(manager.load().isEmpty());
    }

    @Test
    public void testRenameAndReorder() {
        Favorite a = manager.toggle("RICHTER", null, "C", 10, null);
        Favorite b = manager.toggle("RICHTER", null, "G", 10, null);
        assertNotNull(a);
        assertNotNull(b);
        // rename
        manager.rename(a.id, "Richter • C");
        Favorite aLoaded = manager.load().stream().filter(x -> x.id.equals(a.id)).findFirst().orElse(null);
        assertNotNull(aLoaded);
        assertEquals("Richter • C", aLoaded.label);
        // reorder (b first)
        manager.reorder(List.of(b, a));
        List<Favorite> ordered = manager.load();
        assertEquals(b.id, ordered.get(0).id);
        assertEquals(a.id, ordered.get(1).id);
    }

}
