package de.schliweb.bluesharpbendingapp.view;

import de.schliweb.bluesharpbendingapp.controller.MicrophoneSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.controller.NoteSettingsViewHandler;
import de.schliweb.bluesharpbendingapp.controller.TuneViewHandler;

/**
 * The interface Main window.
 */
public interface TuningMainWindow {

    /**
     * Gets microphone settings view.
     *
     * @return the microphone settings view
     */
    MicrophoneSettingsView getMicrophoneSettingsView();

    /**
     * Is microphone settings view active boolean.
     *
     * @return the boolean
     */
    boolean isMicrophoneSettingsViewActive();

    /**
     * Open.
     */
    void open();

    /**
     * Sets microphone settings view handler.
     *
     * @param microphoneSettingsViewHandler the microphone settings view handler
     */
    void setMicrophoneSettingsViewHandler(MicrophoneSettingsViewHandler microphoneSettingsViewHandler);

    /**
     * Is note settings view active boolean.
     *
     * @return the boolean
     */
    boolean isNoteSettingsViewActive();

    /**
     * Gets note settings view.
     *
     * @return the note settings view
     */
    NoteSettingsView getNoteSettingsView();

    /**
     * Sets note settings view handler.
     *
     * @param noteSettingsViewHandler the note settings view handler
     */
    void setNoteSettingsViewHandler(NoteSettingsViewHandler noteSettingsViewHandler);

    /**
     * Is tune view active boolean.
     *
     * @return the boolean
     */
    boolean isTuneViewActive();

    /**
     * Sets tune view handler.
     *
     * @param tuneViewHandler the tune view handler
     */
    void setTuneViewHandler(TuneViewHandler tuneViewHandler);

    /**
     * Gets tune view.
     *
     * @return the tune view
     */
    TuneView getTuneView();

}
