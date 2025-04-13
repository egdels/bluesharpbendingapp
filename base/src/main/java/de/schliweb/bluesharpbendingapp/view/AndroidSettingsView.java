package de.schliweb.bluesharpbendingapp.view;

/**
 * The AndroidSettingsView interface provides a contract for managing
 * and updating the settings specific to the Android platform within the application.
 */
public interface AndroidSettingsView {
    /**
     * Sets the selected lock screen based on the specified index.
     *
     * @param selectedLockScreenIndex the index of the lock screen to be selected
     */
    void setSelectedLockScreen(int selectedLockScreenIndex);
}
