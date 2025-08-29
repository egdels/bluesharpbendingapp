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

import java.util.Objects;

/**
 * Represents a Favorite combination of tuning, key, and optionally holes for quick recall.
 * This model is used by the FavoriteManager to persist and manage favorites across platforms.
 */
public final class Favorite {
    /**
     * Unique identifier (UUID string).
     */
    public String id;
    /**
     * Optional display label.
     */
    public String label;
    /**
     * Tuning identifier (e.g., "RICHTER", "CIRCULAR", "DIMINISHED", or "CUSTOM:xyz").
     */
    public String tuningId;
    /**
     * Stable signature/hash of tuning definition (used for custom tunings).
     */
    public String tuningHash;
    /**
     * Key name (e.g., "C", "G", "A"), or a semitone index string if the app uses that representation.
     */
    public String key;
    /**
     * Optional holes count (e.g., 10, 12).
     */
    public Integer holes;
    /**
     * Creation timestamp (millis).
     */
    public Long createdAt;
    /**
     * Sort order for displaying chips.
     */
    public int sortOrder;

    public Favorite() {
    }

    public Favorite(String id, String label, String tuningId, String tuningHash,
                    String key, Integer holes, Long createdAt, int sortOrder) {
        this.id = id;
        this.label = label;
        this.tuningId = tuningId;
        this.tuningHash = tuningHash;
        this.key = key;
        this.holes = holes;
        this.createdAt = createdAt;
        this.sortOrder = sortOrder;
    }

    /**
     * Determines whether this favorite matches the provided combination for duplicate detection.
     */
    public boolean matches(String tuningId, String tuningHash, String key, Integer holes) {
        if (!Objects.equals(this.tuningId, tuningId)) return false;
        if (!Objects.equals(this.tuningHash, tuningHash)) return false;
        if (!Objects.equals(this.key, key)) return false;
        return Objects.equals(this.holes, holes);
    }
}
