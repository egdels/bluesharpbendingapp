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

import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;
import de.schliweb.bluesharpbendingapp.model.microphone.Microphone;
import de.schliweb.bluesharpbendingapp.model.training.AbstractTraining;
import de.schliweb.bluesharpbendingapp.model.training.Training;
import de.schliweb.bluesharpbendingapp.utils.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * MainModel serves as a representation of the main data model for an application
 * that works with harmonicas, microphones, and related configurations.
 * It provides getters and setters for dynamic attribute manipulation as well as
 * methods to retrieve indices and configurations for selected or stored values.
 * <p>
 * This class also supports utility methods for creating an instance from a string
 * representation and accessing supported attributes like algorithms, keys, tunes,
 * and concert pitches.
 */
public class MainModel {

    /**
     * The LOGGER is a static, final instance of the Logger class, specific to the MainModel class.
     * It is used for logging debug, error, and info messages to aid in monitoring and troubleshooting the execution flow of the application.
     */
    private static final Logger LOGGER = new Logger(MainModel.class);

    /**
     * Represents the currently selected or managed harmonica in the model.
     * It can be used to configure, store, or retrieve data related to a harmonica.
     */
    private Harmonica harmonica;

    /**
     * Represents the training being used in the MainModel class.
     * This variable holds the current training instance and provides access
     * to training-related functionalities such as managing notes, controlling
     * the state, and tracking progress.
     * <p>
     * The Training interface is a blueprint that offers methods for handling
     * different aspects of training, like retrieving notes, moving through
     * the training sequence, starting or stopping the process, and checking
     * the training status.
     * <p>
     * This variable may be set or retrieved to interact with the active training
     * instance, enabling the use of training-specific operations in the main model.
     */
    private Training training;

    /**
     * Represents a private field of type Microphone used in the MainModel class.
     * Stores the currently selected or active microphone instance for handling audio input.
     */
    private Microphone microphone;

    /**
     * Represents the stored index of the selected algorithm in the system.
     * This value is used to persist and retrieve the currently selected algorithm
     * state across different executions or sessions.
     */
    private int storedAlgorithmIndex = 0;

    /**
     * Represents the stored index for the selected key in the main model.
     * This index corresponds to the stored key within the application state,
     * allowing retrieval or modification of the current key setting.
     */
    private int storedKeyIndex = 4;

    /**
     * Represents the index of the microphone that is stored in the application settings.
     * This value is used to remember and retrieve the user's previously selected microphone.
     */
    private int storedMicrophoneIndex = 0;

    /**
     * Stores the index of the selected tune for the application.
     * This index is used to persist the user's tune selection between sessions
     * or to share information between components of the application.
     * Defaults to 6 if not explicitly set.
     */
    private int storedTuneIndex = 6;

    /**
     * Holds the index value representing the stored concert pitch setting.
     * This value is used to persist and retrieve the selected concert pitch
     * from a predefined list of concert pitches.
     */
    private int storedConcertPitchIndex = 11;

    /**
     * Represents the index of the stored training within the model.
     * This variable is initialized to a default value of 0, indicating
     * that no specific training selection is stored by default.
     * It can be set or retrieved using the respective getter and setter methods.
     */
    private int storedTrainingIndex = 0;

    /**
     * Represents the index of the stored precision value within the application's internal configuration.
     * This value is used to persist and retrieve the currently selected precision setting.
     */
    private int storedPrecisionIndex;

    /**
     * Creates a new instance of {@code MainModel} by parsing a provided string.
     * <p>
     * The input string is expected to follow a specific format where key-value pairs
     * are represented as "getMethodName:value", separated by commas, and enclosed
     * in square brackets. The method converts each "get" method name in the input
     * to its corresponding "set" method and invokes the setter methods on a new
     * {@code MainModel} instance, passing the parsed integer values as arguments.
     * <p>
     * If any error occurs during method invocation, it is logged using the {@code LOGGER}.
     *
     * @param string the input string to parse and create the {@code MainModel} object.
     * @return a newly created {@code MainModel} instance with attributes set based on the input string.
     */
    public static MainModel createFromString(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        MainModel model = new MainModel();
        Method[] methods = model.getClass().getMethods();

        for (String entry : strings) {
            entry = entry.replaceFirst("get", "set");
            if (entry.contains(":")) {
                String m = entry.split(":")[0];
                String p = entry.split(":")[1];
                for (Method method : methods) {
                    if (method.getName().equals(m)) {
                        try {
                            method.invoke(model, Integer.parseInt(p));
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            LOGGER.error(e.getMessage());
                        }
                    }
                }
            }
        }
        return model;
    }

    /**
     * Retrieves the list of algorithms supported by the microphone.
     *
     * @return an array of strings representing the supported algorithms
     */
    public String[] getAlgorithms() {
        return microphone.getSupportedAlgorithms();
    }

    /**
     * Retrieves the current Harmonica instance associated with this MainModel.
     *
     * @return the current Harmonica instance
     */
    public Harmonica getHarmonica() {
        return this.harmonica;
    }

    /**
     * Sets the current Harmonica instance associated with this MainModel.
     *
     * @param harmonica the Harmonica instance to associate with this MainModel
     */
    public void setHarmonica(Harmonica harmonica) {
        this.harmonica = harmonica;
    }

    /**
     * Retrieves the list of supported keys for the harmonica.
     *
     * @return an array of strings representing the supported keys
     */
    public String[] getKeys() {
        return AbstractHarmonica.getSupporterKeys();
    }

    /**
     * Retrieves the current Microphone instance associated with this MainModel.
     *
     * @return the current Microphone instance
     */
    public Microphone getMicrophone() {
        return microphone;
    }

    /**
     * Sets the current Microphone instance for this MainModel.
     *
     * @param microphone the Microphone instance to be set
     */
    public void setMicrophone(Microphone microphone) {
        this.microphone = microphone;
    }

    /**
     * Retrieves a list of microphones supported by the system or application.
     *
     * @return an array of strings, where each string represents the name of a supported microphone
     */
    public String[] getMicrophones() {
        return microphone.getSupportedMicrophones();
    }

    /**
     * Retrieves the list of supported concert pitches.
     *
     * @return an array of strings representing the supported concert pitches
     */
    public String[] getConcertPitches() {
        return NoteLookup.getSupportedConcertPitches();
    }

    /**
     * Determines the index of the currently selected algorithm in the list of supported algorithms.
     * <p>
     * This method retrieves the list of algorithms supported by the microphone and
     * compares them with the currently active algorithm. The index of the matching algorithm
     * within the list is then returned. If no match is found, an index of 0 is returned by default.
     *
     * @return the index of the selected algorithm in the list of supported algorithms
     */
    public int getSelectedAlgorithmIndex() {
        String[] algorithms = getAlgorithms();
        int index = 0;
        for (int i = 0; i < algorithms.length; i++) {
            if (algorithms[i].equals(microphone.getAlgorithm())) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Determines the index of the currently selected key in the list of supported keys.
     * <p>
     * This method retrieves the list of supported keys from the harmonica and compares
     * them with the currently selected key name. The index of the matching key
     * within the list is then returned. If no match is found, an index of 0 is returned by default.
     *
     * @return the index of the selected key in the list of supported keys
     */
    public int getSelectedKeyIndex() {
        String[] keys = getKeys();
        int index = 0;
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].equals(harmonica.getKeyName())) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Determines the index of the currently selected microphone in the list of supported microphones.
     * <p>
     * This method retrieves the list of microphones supported by the system and compares
     * them with the currently active microphone's name. The index of the matching microphone
     * within the list is then returned. If no match is found, an index of 0 is returned by default.
     *
     * @return the index of the selected microphone in the list of supported microphones
     */
    public int getSelectedMicrophoneIndex() {
        String[] microphones = getMicrophones();
        int index = 0;
        for (int i = 0; i < microphones.length; i++) {
            if (microphones[i].equals(microphone.getName())) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Determines the index of the currently selected tune in the list of supported tunes.
     * <p>
     * This method retrieves the list of tunes from the harmonica and compares each tune
     * with the currently selected tune name. The index of the matching tune within the
     * list is then returned. If no match is found, an index of 0 is returned by default.
     *
     * @return the index of the selected tune in the list of supported tunes
     */
    public int getSelectedTuneIndex() {
        String[] tunes = getTunes();
        int index = 0;
        for (int i = 0; i < tunes.length; i++) {
            if (tunes[i].equals(harmonica.getTuneName())) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Determines the index of the currently selected concert pitch in the list of supported concert pitches.
     * <p>
     * This method retrieves the list of supported concert pitches and compares each pitch
     * with the name of the currently selected concert pitch obtained from the NoteLookup utility.
     * The index of the matching concert pitch within the list is then returned.
     * If no match is found, an index of 0 is returned by default.
     *
     * @return the index of the selected concert pitch in the list of supported concert pitches
     */
    public int getSelectedConcertPitchIndex() {
        String[] pitches = getConcertPitches();
        int index = 0;
        for (int i = 0; i < pitches.length; i++) {
            if (pitches[i].equals(NoteLookup.getConcertPitchName())) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Retrieves the stored algorithm index.
     *
     * @return the stored algorithm index as an integer
     */
    public int getStoredAlgorithmIndex() {
        return storedAlgorithmIndex;
    }

    /**
     * Sets the stored algorithm index for this MainModel.
     *
     * @param algorithmIndex the index of the algorithm to be stored
     */
    public void setStoredAlgorithmIndex(int algorithmIndex) {
        this.storedAlgorithmIndex = algorithmIndex;
    }

    /**
     * Retrieves the stored key index.
     *
     * @return the stored key index as an integer
     */
    public int getStoredKeyIndex() {
        return storedKeyIndex;
    }

    /**
     * Sets the stored key index for this MainModel.
     *
     * @param keyIndex the index of the key to be stored
     */
    public void setStoredKeyIndex(int keyIndex) {
        this.storedKeyIndex = keyIndex;
    }

    /**
     * Retrieves the stored microphone index for this MainModel.
     *
     * @return the stored microphone index as an integer
     */
    public int getStoredMicrophoneIndex() {
        return storedMicrophoneIndex;
    }

    /**
     * Sets the index of the microphone to be stored in the model.
     *
     * @param microphoneIndex the index of the microphone to be stored
     */
    public void setStoredMicrophoneIndex(int microphoneIndex) {
        this.storedMicrophoneIndex = microphoneIndex;
    }

    /**
     * Retrieves the stored tune index for this MainModel.
     *
     * @return the stored tune index as an integer
     */
    public int getStoredTuneIndex() {
        return storedTuneIndex;
    }

    /**
     * Sets the stored tune index for this MainModel.
     *
     * @param storedTuneIndex the index of the tune to be stored
     */
    public void setStoredTuneIndex(int storedTuneIndex) {
        this.storedTuneIndex = storedTuneIndex;
    }

    /**
     * Retrieves the stored concert pitch index for this MainModel.
     *
     * @return the stored concert pitch index as an integer
     */
    public int getStoredConcertPitchIndex() {
        return storedConcertPitchIndex;
    }

    /**
     * Sets the stored concert pitch index for this MainModel.
     *
     * @param storedConcertPitchIndex the index of the concert pitch to be stored
     */
    public void setStoredConcertPitchIndex(int storedConcertPitchIndex) {
        this.storedConcertPitchIndex = storedConcertPitchIndex;
    }

    /**
     * Retrieves the list of supported tunes for the harmonica.
     *
     * @return an array of strings representing the supported tunes
     */
    public String[] getTunes() {
        return AbstractHarmonica.getSupportedTunes();
    }

    /**
     * Retrieves a string representation of this object's stored integer attributes.
     * <p>
     * The method iterates through all public methods of the object's class, checking
     * for methods with names starting with "getStored" and returning an integer type.
     * It invokes these methods via reflection to gather their names and return values,
     * appending them to a list. If an exception occurs during method invocation, it is logged.
     *
     * @return a string representation of the list of "getStored" methods and their
     * corresponding integer return values, formatted as methodName:value pairs.
     */
    public String getString() {
        Method[] methods = this.getClass().getMethods();
        ArrayList<String> stringList = new ArrayList<>();
        for (Method m : methods) {
            if (m.getName().indexOf("getStored") == 0 && int.class == m.getReturnType()) {
                try {
                    stringList.add(m.getName() + ":" + m.invoke(this));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
        return stringList.toString();
    }

    /**
     * Retrieves the list of supported training sessions.
     *
     * @return an array of strings representing the supported training sessions
     */
    public String[] getTrainings() {
        return AbstractTraining.getSupportedTrainings();
    }

    /**
     * Retrieves the index of the currently selected training from the list of available trainings.
     *
     * @return the zero-based index of the selected training in the array of trainings
     */
    public int getSelectedTrainingIndex() {
        String[] trainings = getTrainings();
        int index = 0;
        for (int i = 0; i < trainings.length; i++) {
            if (trainings[i].equals(training.getTrainingName())) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Retrieves the stored training index for this MainModel.
     *
     * @return the stored training index as an integer
     */
    public int getStoredTrainingIndex() {
        return storedTrainingIndex;
    }

    /**
     * Sets the stored training index to the specified value.
     *
     * @param trainingIndex the new value for the stored training index
     */
    public void setStoredTrainingIndex(int trainingIndex) {
        this.storedTrainingIndex = trainingIndex;
    }

    /**
     * Retrieves the training instance associated with this object.
     *
     * @return the Training instance
     */
    public Training getTraining() {
        return training;
    }

    /**
     * Sets the training object for this instance.
     *
     * @param training the Training object to be assigned
     */
    public void setTraining(Training training) {
        this.training = training;
    }

    /**
     * Retrieves the supported precisions for the training model.
     *
     * @return an array of strings representing the supported precisions.
     */
    public String[] getPrecisions() {
        return AbstractTraining.getSupportedPrecisions();
    }

    /**
     * Retrieves the index of the currently selected precision.
     *
     * @return the index corresponding to the selected precision setting
     */
    public int getSelectedPrecisionIndex() {
        return this.storedPrecisionIndex;
    }

    /**
     * Sets the stored precision index to the specified value.
     *
     * @param storedPrecisionIndex the precision index to be stored
     */
    public void setStoredPrecisionIndex(int storedPrecisionIndex) {
        this.storedPrecisionIndex = storedPrecisionIndex;
    }

}