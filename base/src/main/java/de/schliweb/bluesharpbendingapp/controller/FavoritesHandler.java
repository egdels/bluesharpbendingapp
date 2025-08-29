package de.schliweb.bluesharpbendingapp.controller;
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

import de.schliweb.bluesharpbendingapp.favorites.Favorite;

import java.util.List;

/**
 * Handler interface for managing Favorites across platforms.
 * <p>
 * Implementations are responsible for coordinating between the UI layer and the
 * underlying favorites manager/persistence. This interface is platform-neutral
 * and belongs to the base module so it can be implemented by both Desktop and Android.
 * <p>
 * Responsibilities:
 * - Load persisted favorites and expose them to the UI
 * - Toggle the current combination as favorite (create/remove)
 * - Rename, remove, and reorder favorites
 * - Query if the current combination is a favorite
 * <p>
 * Note: Applying a favorite (setting tuning/key/holes on the active model) is
 * intentionally left to platform-specific controllers to avoid tight coupling.
 */
public interface FavoritesHandler {
    /**
     * Loads the current list of favorites ordered by their sort order.
     *
     * @return ordered list of favorites; never null
     */
    List<Favorite> loadFavorites();

    /**
     * Toggles the favorite for the given combination. If the combination already exists,
     * it is removed and {@code null} is returned. Otherwise, a new favorite is created and returned.
     *
     * @param tuningId   tuning identifier (e.g., "RICHTER", "CUSTOM:xyz")
     * @param tuningHash optional stable signature/hash for custom tunings; may be null
     * @param key        musical key (e.g., "C", "G")
     * @param holes      optional hole count (e.g., 10, 12); may be null
     * @param label      optional display label; may be null
     * @return the created favorite if newly added; {@code null} if removed
     */
    Favorite toggleFavorite(String tuningId, String tuningHash, String key, Integer holes, String label);

    /**
     * Checks whether the given combination is currently stored as a favorite.
     */
    boolean isCurrentFavorite(String tuningId, String tuningHash, String key, Integer holes);

    /**
     * Renames the favorite with the specified id. No-op if the id is unknown.
     */
    void renameFavorite(String id, String newLabel);

    /**
     * Removes the favorite with the specified id. No-op if the id is unknown.
     */
    void removeFavorite(String id);

    /**
     * Reorders the favorites according to the provided list. Items not present in the list
     * are appended at the end preserving their relative ordering.
     */
    void reorderFavorites(List<Favorite> inNewOrder);
}
