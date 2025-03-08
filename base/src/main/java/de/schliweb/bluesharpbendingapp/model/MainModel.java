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
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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
@Setter
@Slf4j
public class MainModel {

    @Getter
    private Harmonica harmonica;


    @Getter
    private Training training;


    @Getter
    private Microphone microphone;

    @Getter
    private int storedAlgorithmIndex = 0;

    @Getter
    private int storedKeyIndex = 4;

    @Getter
    private int storedMicrophoneIndex = 0;

    @Getter
    private int storedTuneIndex = 6;

    @Getter
    private int storedConcertPitchIndex = 11;

    @Getter
    private int storedTrainingIndex = 0;

    private int storedPrecisionIndex;

    @Getter
    private int storedConfidenceIndex = 0;

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
                            log.error(e.getMessage());
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
     * Retrieves the list of supported keys for the harmonica.
     *
     * @return an array of strings representing the supported keys
     */
    public String[] getKeys() {
        return AbstractHarmonica.getSupporterKeys();
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
                    log.error(e.getMessage());
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
     * Retrieves the index of the selected confidence level from the list of available confidence levels.
     * The selected confidence level is determined based on the microphone's current confidence setting.
     *
     * @return the index of the selected confidence level in the array of confidence levels
     */
    public int getSelectedConfidenceIndex() {
        String[] confidences = getConfidences();
        int index = 0;
        for (int i = 0; i < confidences.length; i++) {
            if (confidences[i].equals(microphone.getConfidence())) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Retrieves the supported confidence levels from the microphone device.
     *
     * @return an array of strings representing the supported confidence levels.
     */
    public String[] getConfidences() {
        return microphone.getSupportedConfidences();
    }

}