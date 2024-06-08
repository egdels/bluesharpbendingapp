package de.schliweb.bluesharpbendingapp.view;

/**
 * The interface Tune view.
 */
public interface TuneView {

    /**
     * Init.
     */
    void init();

    /**
     * Clear.
     */
    void clear();

    /**
     * Update.
     *
     * @param key   the key
     * @param cents the cents
     */
    void update(String key, double cents);
}
