package de.schliweb.bluesharpbendingapp.model;
/*
 * Copyright (c) 2023 Christian Kierdorf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */

import de.schliweb.bluesharpbendingapp.utils.LoggingContext;
import de.schliweb.bluesharpbendingapp.utils.LoggingUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * The MainModel class represents the primary data model for storing configuration and state-related
 * information. It supports both persisted and runtime-selected indices for various functionalities.
 * <p>
 * This class provides getter methods to retrieve the selected indices for different parameters
 * such as algorithm, key, microphone, tune, concert pitch, training, precision, confidence, and
 * lock screen. If a selected index is not set (equals to zero), the corresponding stored index is
 * returned as a fallback, ensuring consistent behavior in case of partial updates.
 * <p>
 * The MainModel is also capable of serializing its state into a string representation through
 * the getString() method. This serialized format is utilized for storage and retrieval operations
 * carried out by other systems or services.
 */
@Getter
@Setter
public class MainModel implements Serializable {

    /**
     * Constructs a new MainModel with default values.
     * This constructor initializes the model with predefined default values
     * for various settings like algorithm, key, microphone, etc.
     */
    public MainModel() {
        LoggingContext.setComponent("MainModel");
        LoggingUtils.logInitializing("MainModel");
        LoggingUtils.logDebug("Creating new MainModel instance with default values");
        LoggingUtils.logInitialized("MainModel");
    }

    /**
     * Represents the index of the currently selected or stored algorithm.
     * This variable is used to track and reference the specific algorithm
     * configuration or implementation within the application's context.
     * <p>
     * The default value is set to 0, indicating the initial or default
     * algorithm selection before any user modifications or settings are applied.
     */
    private int storedAlgorithmIndex = 0;

    /**
     * Represents an integer index used to store or retrieve a specific key
     * in the context of a storage or data management operation. This variable
     * may serve as a reference to identify particular items or configurations
     * within a larger dataset or system.
     */
    private int storedKeyIndex = 4;

    /**
     * Represents the index of the currently selected or stored microphone.
     * This variable is used to track the microphone's position or identifier
     * within a list or array of available microphones.
     * <p>
     * It is initialized to 0 by default, which typically corresponds to the
     * primary or default microphone in most systems unless specifically updated.
     */
    private int storedMicrophoneIndex = 0;

    /**
     * Represents the index for a stored tune configuration.
     * This variable is used to maintain the index of a specific tune
     * that is currently stored or pre-selected within the application.
     * It serves as a default or reference point for tune-related operations.
     */
    private int storedTuneIndex = 6;

    /**
     * Represents the index value of the currently stored concert pitch setting.
     * This variable typically refers to a predefined scale of concert pitch values,
     * where the index corresponds to a specific pitch configuration.
     * <p>
     * It is used as part of the overall application state, potentially being serialized
     * by the ModelStorageService or utilized in other parts of the system requiring
     * pitch-related data.
     */
    private int storedConcertPitchIndex = 11;

    /**
     * Represents the index of the currently stored training session.
     * This variable is used to track and manage the storage or retrieval
     * of specific training sessions within the application state.
     * <p>
     * The initial value is set to 0, indicating that no training session
     * is currently actively stored or indexed.
     */
    private int storedTrainingIndex = 0;

    /**
     * Represents the index value that determines the level of precision
     * stored or used for data handling within the application's logic.
     * This variable is likely utilized for selecting or adjusting
     * the precision level in numerical or operational contexts.
     */
    private int storedPrecisionIndex = 2;

    /**
     * Represents a confidence index used by the application to store
     * or track a numeric value associated with a particular level
     * of confidence or certainty.
     * <p>
     * This variable is primarily utilized as part of the application's
     * internal logic, potentially maintaining state or providing context
     * for file storage, model modification, or related processing tasks.
     * <p>
     * The value is initialized to 0, which could represent a default or
     * uninitialized confidence level, and is likely modified during the
     * application's runtime based on specific processing or input.
     */
    private int storedConfidenceIndex = 0;

    /**
     * The `storedLockScreenIndex` variable represents the index of the currently
     * stored lock screen configuration. It is used to track or reference a
     * specific lock screen setup, likely within a model or configuration
     * management system.
     * <p>
     * This variable is initialized to `0` by default, which may indicate the
     * default or initial lock screen configuration. Its value can be updated
     * to reflect changes in the selected or stored lock screen index.
     */
    private int storedLockScreenIndex = 0;

    /**
     * Represents the index of the currently selected lock screen option.
     * This variable holds an integer value corresponding to the selected
     * lock screen configuration in the application. It is used to track and
     * manage the user's lock screen choice within the application's state.
     */
    private int selectedLockScreenIndex;
    /**
     * Represents the index of the selected algorithm within a collection or list of algorithms.
     * This variable is used to track and identify the currently chosen algorithm
     * by its index for processing or configuration purposes.
     * <p>
     * A negative value or uninitialized state may indicate that no algorithm is selected.
     * The specific range or valid values depend on the implementation of the algorithms collection.
     */
    private int selectedAlgorithmIndex;
    /**
     * Represents the index of the currently selected key.
     * This variable is likely used for managing the state of a selection
     * within a list or array of keys, enabling tracking, updates, and interactions
     * based on the user's selection.
     */
    private int selectedKeyIndex;
    /**
     * Represents the index of the currently selected microphone.
     * This variable is used to track which microphone option
     * is active or in use within the application.
     * <p>
     * The index value is expected to correspond to an entry in
     * a list or array of available microphones, where an integer
     * index is used to identify the selection.
     */
    private int selectedMicrophoneIndex;
    /**
     * Represents the index of the currently selected tune within a collection of tunes.
     * This variable is typically used for tracking or persisting the user's current selection
     * in the context of the application. Its value corresponds to the position in a list or array
     * of tunes, starting from zero.
     */
    private int selectedTuneIndex;
    /**
     * Represents the currently selected index for the concert pitch setting.
     * This variable is used to identify and manage the active configuration
     * of the musical tuning system or pitch reference within the application.
     * <p>
     * The concert pitch setting typically determines the fundamental tuning
     * frequency (e.g., A4 = 440 Hz) that serves as a reference for musical
     * intonation. The value of this index corresponds to a specific option
     * available in the configuration settings.
     */
    private int selectedConcertPitchIndex;
    /**
     * Represents the index of the currently selected training session or item.
     * This variable is used to track the user's selection, enabling actions
     * such as loading, editing, or displaying the corresponding training session.
     * It is assumed to correlate with the data structure or list of available trainings.
     */
    private int selectedTrainingIndex;
    /**
     * Represents the index of the selected confidence level within a specified list or collection.
     * This variable is typically used to indicate and track the currently selected confidence level
     * in a user interface or within a data processing context.
     * <p>
     * It is assumed that this index corresponds to the position in a predefined sequence
     * of confidence levels and is updated to reflect user interaction or programmatic changes.
     * <p>
     * A value of -1 or any other special value may be reserved to indicate that no selection has been made.
     */
    private int selectedConfidenceIndex;
    /**
     * Represents the index of the selected precision level in a collection or array of
     * precision options. This variable is used to track which precision level
     * has been chosen, likely as part of user configuration or application settings.
     * <p>
     * A value of this variable corresponds to the position of a specific precision
     * option, facilitating the retrieval, storage, or adjustment of precision-related
     * configurations. The interpretation of this index depends on the context in
     * which it is applied within the application.
     */
    private int selectedPrecisionIndex;

    /**
     * Retrieves the index of the selected lock screen configuration.
     * If a specific lock screen index is selected, it returns that value.
     * Otherwise, it falls back to the stored lock screen index.
     *
     * @return the index of the selected lock screen if available,
     * or the stored lock screen index as a fallback.
     */
    public int getSelectedLockScreenIndex() {
        return selectedLockScreenIndex != 0 ? selectedLockScreenIndex : storedLockScreenIndex;
    }

    /**
     * Returns the index of the selected algorithm. It prioritizes the value of
     * {@code selectedAlgorithmIndex} if it is not zero; otherwise, it returns
     * the value of {@code storedAlgorithmIndex}.
     *
     * @return The index of the selected algorithm. If {@code selectedAlgorithmIndex} is zero,
     * {@code storedAlgorithmIndex} is returned.
     */
    public int getSelectedAlgorithmIndex() {
        return selectedAlgorithmIndex != 0 ? selectedAlgorithmIndex : storedAlgorithmIndex;
    }

    /**
     * Retrieves the index of the currently selected key.
     * If the selected key index is not set (equals 0), the stored key index is returned.
     *
     * @return the index of the selected key or the stored key index if no key is selected.
     */
    public int getSelectedKeyIndex() {
        return selectedKeyIndex != 0 ? selectedKeyIndex : storedKeyIndex;
    }

    /**
     * Retrieves the selected microphone index. If the selected microphone index is set,
     * it returns that value. Otherwise, it falls back to a stored microphone index.
     *
     * @return The index of the selected microphone if set; otherwise, the stored microphone index.
     */
    public int getSelectedMicrophoneIndex() {
        return selectedMicrophoneIndex != 0 ? selectedMicrophoneIndex : storedMicrophoneIndex;
    }

    /**
     * Retrieves the index of the selected tune. If the `selectedTuneIndex` is not set
     * (i.e., equals zero), it returns the value of `storedTuneIndex`. This method ensures
     * a valid tune index is returned based on the application's current state.
     *
     * @return the index of the selected tune if `selectedTuneIndex` is not zero;
     * otherwise, the index of the stored tune.
     */
    public int getSelectedTuneIndex() {
        return selectedTuneIndex != 0 ? selectedTuneIndex : storedTuneIndex;
    }

    /**
     * Retrieves the index of the selected concert pitch.
     * If the selected concert pitch index is zero, the stored concert pitch index is returned as a fallback.
     *
     * @return the selected concert pitch index if non-zero; otherwise, the stored concert pitch index.
     */
    public int getSelectedConcertPitchIndex() {
        return selectedConcertPitchIndex != 0 ? selectedConcertPitchIndex : storedConcertPitchIndex;
    }

    /**
     * Retrieves the selected index for training if it has been explicitly set
     * (a non-zero value). If the selected training index is zero, the stored
     * training index is returned as a fallback.
     *
     * @return The selected training index if it is non-zero; otherwise,
     * the stored training index.
     */
    public int getSelectedTrainingIndex() {
        return selectedTrainingIndex != 0 ? selectedTrainingIndex : storedTrainingIndex;
    }

    /**
     * Retrieves the selected confidence index if it has been explicitly set
     * (a non-zero value). If not, defaults to the stored confidence index.
     *
     * @return The selected confidence index if it is non-zero; otherwise,
     * the stored confidence index.
     */
    public int getSelectedConfidenceIndex() {
        return selectedConfidenceIndex != 0 ? selectedConfidenceIndex : storedConfidenceIndex;
    }

    /**
     * Retrieves the selected precision index if it has been explicitly set
     * (a non-zero value). If not, defaults to the stored precision index.
     *
     * @return The selected precision index if it is non-zero; otherwise,
     * the stored precision index.
     */
    public int getSelectedPrecisionIndex() {
        return selectedPrecisionIndex != 0 ? selectedPrecisionIndex : storedPrecisionIndex;
    }

    /**
     * Generates a string representation of the object's state by dynamically
     * invoking all getter methods that start with "getStored" and have a return type of int.
     * The resulting string contains key-value pairs, where each key corresponds
     * to a method name and its value is the method's returned result.
     *
     * @return A string representation of the object's stored state, formatted as
     * "methodName:value", with elements separated by commas and enclosed in brackets.
     */
    public String getString() {
        LoggingContext.setComponent("MainModel");
        LoggingContext.setOperation("getString");
        LoggingUtils.logDebug("Generating string representation of model");

        long startTime = System.currentTimeMillis();

        Method[] methods = this.getClass().getMethods();
        ArrayList<String> stringList = new ArrayList<>();
        int methodsProcessed = 0;

        for (Method m : methods) {
            if (m.getName().indexOf("getStored") == 0 && int.class == m.getReturnType()) {
                try {
                    Object value = m.invoke(this);
                    stringList.add(m.getName() + ":" + value);
                    methodsProcessed++;
                    LoggingUtils.logDebug("Added property to string representation", m.getName() + "=" + value);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    LoggingUtils.logError("Error invoking method during string generation", e);
                }
            }
        }

        String result = stringList.toString();
        long duration = System.currentTimeMillis() - startTime;
        LoggingUtils.logPerformance("Model string generation", duration);
        LoggingUtils.logDebug("Generated string representation with " + methodsProcessed + " properties", "length=" + result.length());

        return result;
    }

}
