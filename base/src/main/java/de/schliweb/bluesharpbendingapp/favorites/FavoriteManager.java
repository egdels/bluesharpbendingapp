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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * Platform-neutral FavoriteManager using a pluggable FavoritesStorage.
 * <p>
 * Responsibilities:
 * - Load and store favorites as JSON (schema key: favorites_v1)
 * - Toggle favorites (create/remove) based on a unique combination
 * - Rename, remove, and reorder favorites
 * - Defensive parsing and graceful handling of missing/extra fields
 */
public class FavoriteManager {

    private static final int MAX_FAVORITES_SOFT_LIMIT = 24;

    private final FavoritesStorage storage;
    private final ExecutorService executor;

    /**
     * In-memory cache; index by id for quick access.
     */
    private final Map<String, Favorite> cacheById = new LinkedHashMap<>();


    /**
     * Creates a manager backed by the provided storage implementation.
     */
    public FavoriteManager(FavoritesStorage storage, ExecutorService executor) {
        this.storage = storage;
        this.executor = executor;
        // Preload from storage
        List<Favorite> loaded = readFromStorage();
        for (Favorite f : loaded) {
            cacheById.put(f.id, f);
        }
        sortCacheBySortOrder();
    }

    /**
     * Loads the favorites list ordered by sortOrder.
     */
    public synchronized List<Favorite> load() {
        return new ArrayList<>(cacheAsOrderedList());
    }

    /**
     * Toggles a favorite for the given combination. If it exists, remove and return null.
     * If it does not exist, create it, persist, and return the created Favorite.
     */
    public synchronized Favorite toggle(String tuningId, String tuningHash, String key, Integer holes, String label) {
        Favorite existing = findByCombination(tuningId, tuningHash, key, holes);
        if (existing != null) {
            cacheById.remove(existing.id);
            persistAsync();
            return null; // removed
        }
        if (cacheById.size() >= MAX_FAVORITES_SOFT_LIMIT) {
            // Soft limit: still allow creation but caller might show a hint elsewhere.
        }
        long now = System.currentTimeMillis();
        int nextOrder = cacheById.size();
        Favorite created = new Favorite(UUID.randomUUID().toString(), label,
                tuningId, tuningHash, key, holes, now, nextOrder);
        cacheById.put(created.id, created);
        persistAsync();
        return created;
    }

    /**
     * Removes a favorite by id.
     */
    public synchronized void remove(String id) {
        if (cacheById.remove(id) != null) {
            normalizeSortOrder();
            persistAsync();
        }
    }

    /**
     * Renames a favorite by id.
     */
    public synchronized void rename(String id, String newLabel) {
        Favorite f = cacheById.get(id);
        if (f != null) {
            f.label = newLabel;
            persistAsync();
        }
    }

    /**
     * Reorders favorites according to the provided list (by id), and persists new sortOrder.
     */
    public synchronized void reorder(List<Favorite> inNewOrder) {
        Map<String, Favorite> newOrder = new LinkedHashMap<>();
        int i = 0;
        for (Favorite item : inNewOrder) {
            Favorite existing = cacheById.get(item.id);
            if (existing != null) {
                existing.sortOrder = i++;
                newOrder.put(existing.id, existing);
            }
        }
        // Append missing items preserving order
        for (Favorite f : cacheAsOrderedList()) {
            if (!newOrder.containsKey(f.id)) {
                f.sortOrder = i++;
                newOrder.put(f.id, f);
            }
        }
        cacheById.clear();
        cacheById.putAll(newOrder);
        persistAsync();
    }

    /**
     * Checks if the given combination is currently a favorite.
     */
    public synchronized boolean isCurrentFavorite(String tuningId, String tuningHash, String key, Integer holes) {
        return findByCombination(tuningId, tuningHash, key, holes) != null;
    }

    // ------------------- Internal helpers -------------------

    private void persistAsync() {
        final String json = toJsonString(cacheAsOrderedList());
        executor.execute(() -> storage.write(json));
    }

    private void sortCacheBySortOrder() {
        List<Favorite> ordered = cacheAsOrderedList();
        cacheById.clear();
        for (Favorite f : ordered) {
            cacheById.put(f.id, f);
        }
    }

    private void normalizeSortOrder() {
        int i = 0;
        for (Favorite f : cacheAsOrderedList()) {
            f.sortOrder = i++;
        }
    }

    private List<Favorite> cacheAsOrderedList() {
        List<Favorite> list = new ArrayList<>(cacheById.values());
        Collections.sort(list, Comparator.comparingInt(a -> a.sortOrder));
        return list;
    }

    private Favorite findByCombination(String tuningId, String tuningHash, String key, Integer holes) {
        for (Favorite f : cacheById.values()) {
            if (f.matches(tuningId, tuningHash, key, holes)) return f;
        }
        return null;
    }

    private List<Favorite> readFromStorage() {
        List<Favorite> result = new ArrayList<>();
        String raw = storage.read();
        if (raw == null || raw.isEmpty()) return result;
        try {
            JSONArray arr = new JSONArray(raw);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.optJSONObject(i);
                if (obj == null) continue;
                Favorite f = new Favorite();
                f.id = optString(obj, "id", UUID.randomUUID().toString());
                f.label = optString(obj, "label", null);
                f.tuningId = optString(obj, "tuningId", null);
                f.tuningHash = optString(obj, "tuningHash", null);
                f.key = optString(obj, "key", null);
                if (obj.has("holes") && !obj.isNull("holes")) {
                    f.holes = obj.optInt("holes");
                } else {
                    f.holes = null;
                }
                long created = obj.optLong("createdAt", System.currentTimeMillis());
                f.createdAt = created;
                f.sortOrder = obj.optInt("sortOrder", i);
                if (f.tuningId != null && f.key != null) {
                    result.add(f);
                }
            }
        } catch (JSONException ignored) {
            // Corrupt data -> treat as empty per defensive policy.
        }
        return result;
    }

    private static String toJsonString(List<Favorite> list) {
        JSONArray arr = new JSONArray();
        for (Favorite f : list) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("id", f.id);
                if (f.label != null) obj.put("label", f.label);
                else obj.put("label", JSONObject.NULL);
                obj.put("tuningId", f.tuningId);
                if (f.tuningHash != null) obj.put("tuningHash", f.tuningHash);
                else obj.put("tuningHash", JSONObject.NULL);
                obj.put("key", f.key);
                if (f.holes != null) obj.put("holes", f.holes);
                else obj.put("holes", JSONObject.NULL);
                obj.put("createdAt", f.createdAt != null ? f.createdAt : System.currentTimeMillis());
                obj.put("sortOrder", f.sortOrder);
            } catch (JSONException ignored) {
            }
            arr.put(obj);
        }
        return arr.toString();
    }

    private static String optString(JSONObject obj, String key, String def) {
        if (!obj.has(key) || obj.isNull(key)) return def;
        String v = obj.optString(key, def);
        if (v == null) return def;
        v = v.trim();
        if ("null".equalsIgnoreCase(v)) return def;
        return v.isEmpty() ? def : v;
    }
}
