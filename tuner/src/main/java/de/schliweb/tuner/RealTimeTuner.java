package de.schliweb.tuner;
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

import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;
import de.schliweb.bluesharpbendingapp.model.microphone.MicrophoneHandler;
import de.schliweb.bluesharpbendingapp.model.microphone.desktop.MicrophoneDesktop;
import de.schliweb.bluesharpbendingapp.utils.NoteUtils;

import java.util.List;

/**
 * The RealTimeTuner class provides functionality to process and display
 * real-time pitch detection results. It implements the MicrophoneHandler
 * interface to handle audio input from a microphone and updates the UI
 * with detected pitch, note name, and pitch deviation in cents.
 */
public class RealTimeTuner implements MicrophoneHandler {

    /**
     * The default algorithm used for pitch detection within the application.
     * This constant is employed during the initialization of the microphone
     * to select the pitch detection method to be applied in real-time audio processing.
     * <p>
     * The available algorithms depend on the specific implementation of the
     * microphone or audio processing library being used. The value assigned to
     * this constant should match one of the supported algorithms of the microphone
     * implementation.
     * <p>
     * In this application, the "YIN" algorithm is selected as the default due to
     * its effectiveness in pitch detection under various audio conditions.
     */
    private static final String DEFAULT_ALGORITHM = "YIN";

    /**
     * A constant that defines the default name setting for the tuner application.
     * <p>
     * This variable is used as a fallback value or placeholder to represent an
     * uninitialized or default state in the RealTimeTuner class. The value is
     * set to `0` and remains static and immutable throughout the lifecycle of
     * the application.
     * <p>
     * Purpose:
     * - Serves as a predefined default reference for initial or reset states.
     * - Ensures consistency and ease of management for uninitialized configurations.
     */
    private static final int DEFAULT_NAME = 0;

    /**
     * A default confidence level used for pitch detection or other tuning-related calculations.
     * This value represents a threshold or base confidence that can be used to determine
     * if the detected pitch or result is reliable.
     * <p>
     * The value is set to `0.7`, indicating a moderately high level of confidence is required
     * by default for processing or accepting pitch-related data.
     * <p>
     * This field is static and final, meaning it is a constant and cannot be changed at runtime.
     * It ensures consistency in behavior and can be used as a standard baseline across
     * the `RealTimeTuner` class.
     */
    private static final double DEFAULT_CONFIDENCE = 0.7;

    /**
     * A reference to the {@code TunerFrame} instance used for managing
     * the graphical user interface of the real-time tuner application.
     * <p>
     * The {@code tunerFrame} serves as the main interface between the user
     * and the tuner, displaying information such as the detected frequency,
     * corresponding musical note, and tuning accuracy. Additionally, it updates
     * UI components in real-time to provide feedback during the tuning process.
     * <p>
     * This variable is intended to be final, ensuring that the same instance
     * of {@code TunerFrame} is used throughout the lifecycle of the
     * {@code RealTimeTuner} class.
     */
    private final TunerFrame tunerFrame;

    /**
     * Represents the microphone device used for real-time pitch detection and volume measurement
     * in the RealTimeTuner class. This field handles the input audio stream, enabling analysis,
     * processing, and interaction with the tuner functionality.
     * <p>
     * The microphone is initialized and managed within the RealTimeTuner class, including
     * its setup and closure during the tuning process. It plays a crucial role as the audio
     * data source for detecting pitch and volume from the user's input.
     */
    private MicrophoneDesktop microphone;

    /**
     * Indicates whether the tuning process is currently active.
     * <p>
     * This variable is a state flag used to track whether the real-time
     * tuning functionality is running. When {@code true}, the system is actively
     * tuning and processing pitch data. When {@code false}, the tuning process is
     * inactive.
     * <p>
     * It is primarily manipulated within methods that start or stop the
     * tuning process, ensuring accurate representation of the tuning state
     * throughout the application's lifecycle.
     */
    private boolean isTuning = false;

    /**
     * Constructs a RealTimeTuner instance and initializes the microphone for real-time audio processing.
     *
     * @param tunerFrame the TunerFrame object used to display tuning information and user interface.
     */
    public RealTimeTuner(TunerFrame tunerFrame) {
        this.tunerFrame = tunerFrame;
        initializeMicrophone();
    }

    /**
     * Initializes the real-time tuning process by enabling the microphone and displaying the tuner interface.
     * <p>
     * This method performs the following operations:
     * 1. Sets the `isTuning` flag to true, indicating that the tuner is active.
     * 2. Opens the microphone for capturing and processing real-time audio data.
     * 3. Opens the tuner frame to display relevant tuning information and the user interface.
     * <p>
     * Usage Notes:
     * - Before invoking this method, ensure that the tuner frame is properly instantiated.
     * - The microphone should be accessible and properly configured for audio capture.
     * <p>
     * Key Interactions:
     * - Calls the `open` method on the `microphone` object to initialize audio input.
     * - Calls the `open` method on the `tunerFrame` object to make the GUI visible to the user.
     * <p>
     * This method is crucial for starting the tuning operation and should be paired with `stopTuning`
     * to gracefully terminate the tuning process and release resources.
     */
    public void startTuning() {
        isTuning = true;
        microphone.open();
        tunerFrame.open();
    }

    /**
     * Stops the real-time tuning process by deactivating the tuner.
     * <p>
     * This method performs the following actions:
     * - Checks if the tuner is currently active by verifying the `isTuning` flag.
     * - Sets the `isTuning` flag to `false` to indicate that the tuning process has been stopped.
     * - Calls the `closeMicrophone` method to release the microphone resource and terminate audio input.
     * <p>
     * Usage Notes:
     * - This method is meant to be called after the tuning process has started using the `startTuning` method.
     * - It is crucial to invoke this method to ensure proper resource management and avoid potential issues
     * with audio device availability.
     * <p>
     * Key Interactions:
     * - Interacts with the `isTuning` field to manage the state of the tuner.
     * - Delegates microphone resource cleanup to the `closeMicrophone` method.
     */
    public void stopTuning() {
        if (isTuning) {
            isTuning = false;
            closeMicrophone();
        }
    }

    /**
     * Handles the incoming pitch and volume data during real-time audio processing.
     * <p>
     * This method validates the given pitch value and processes it to determine
     * relevant musical information if the pitch is valid. The volume parameter is
     * currently not utilized in this implementation but may be incorporated in future
     * extensions of the functionality.
     *
     * @param pitch  The frequency of the audio signal in Hz. It is validated before
     *               being processed.
     * @param volume The amplitude of the audio signal, representing its loudness.
     *               This parameter is not actively used in the current implementation.
     */
    @Override
    public void handle(double pitch, double volume) {
        if (isValidPitch(pitch)) {
            processPitchData(pitch);
        }
    }

    /**
     * Initializes the microphone for real-time audio processing with the specified default configurations.
     * <p>
     * This method performs the following:
     * 1. Creates an instance of the `MicrophoneDesktop` class and assigns it to the `microphone` field.
     * 2. Retrieves the list of supported audio processing algorithms from the microphone and selects the index
     * corresponding to the `DEFAULT_ALGORITHM`. If the algorithm is unsupported, the index will be set to -1.
     * 3. Configures the microphone to use the selected algorithm.
     * 4. Sets the microphone's name to the value of the `DEFAULT_NAME` constant.
     * 5. Retrieves the list of supported confidence levels for audio processing and selects the index
     * corresponding to `DEFAULT_CONFIDENCE`. If the confidence level is unsupported, the index will be set to -1.
     * 6. Configures the microphone to use the selected confidence level.
     * 7. Assigns the current instance as the microphone handler to process incoming audio data.
     * <p>
     * This method is designed to prepare the microphone for subsequent real-time audio processing operations,
     * ensuring that it is configured with appropriate default settings. It is an essential step in the
     * initialization of the audio tuning workflow.
     */
    private void initializeMicrophone() {
        microphone = new MicrophoneDesktop();
        List<String> supportedAlgorithms = List.of(microphone.getSupportedAlgorithms());
        int algorithmIndex = supportedAlgorithms.indexOf(DEFAULT_ALGORITHM); // Gibt den Index oder -1 zurück
        microphone.setAlgorithm(algorithmIndex);
        microphone.setName(DEFAULT_NAME);
        List<String> supportedConfidences = List.of(microphone.getSupportedConfidences());
        int confidenceIndex = supportedConfidences.indexOf(DEFAULT_CONFIDENCE + ""); // Gibt den Index oder -1 zurück
        microphone.setConfidence(confidenceIndex);
        microphone.setMicrophoneHandler(this);
    }

    /**
     * Releases the resources used by the microphone component and ensures proper cleanup.
     * <p>
     * This method is responsible for closing the `microphone` instance if it has been
     * initialized, preventing resource leaks and freeing up any associated system resources.
     * It should be called when the microphone is no longer needed, such as when stopping
     * the real-time tuning process.
     * <p>
     * Key Behavior:
     * - Checks if the `microphone` field is not null.
     * - Invokes the `close` method on the `microphone` object to terminate any running
     * tasks or processes associated with audio input.
     * <p>
     * Usage Notes:
     * - Typically used in the context of terminating the tuning process (e.g., within
     * the `stopTuning` method).
     * - Ensures clean resource management and avoids potential issues with microphone
     * availability for other programs or processes.
     */
    private void closeMicrophone() {
        if (microphone != null) {
            microphone.close();
        }
    }

    /**
     * Validates if the given pitch value is valid.
     * <p>
     * A pitch is considered valid if it is greater than 0. This method serves
     * as a helper to filter out non-positive pitch values that are not usable
     * in the context of pitch processing or real-time tuning operations.
     *
     * @param pitch The pitch value to be validated, represented as a double.
     *              This value is typically a frequency measured in Hertz (Hz).
     * @return true if the pitch is greater than 0, false otherwise.
     */
    private boolean isValidPitch(double pitch) {
        return pitch > 0;
    }

    /**
     * Processes the given pitch value to determine the musical note and tuning offset in cents.
     * <p>
     * This method performs the following operations:
     * 1. Converts the provided pitch frequency into its corresponding musical note name.
     * 2. Retrieves the reference frequency of the computed note.
     * 3. Calculates the tuning offset in cents, representing the difference between the actual
     * pitch frequency and the reference frequency.
     * 4. Updates the user interface with the processed pitch, note name, and tuning details.
     *
     * @param pitch The frequency of the audio signal in Hz. This value is used to calculate
     *              the corresponding note and tuning information.
     */
    private void processPitchData(double pitch) {
        String note = NoteLookup.getNoteName(pitch);
        double referenceFrequency = NoteLookup.getNoteFrequency(note);
        double cents = NoteUtils.getCents(pitch, referenceFrequency);
        tunerFrame.updateUI(pitch, note, cents);
    }
}