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
import de.schliweb.bluesharpbendingapp.utils.Logger;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The type Main model.
 */
public class MainModel implements Serializable {

    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = new Logger(MainModel.class);

    /**
     * The Harmonica.
     */
    private Harmonica harmonica;

    /**
     * The Microphone.
     */
    private Microphone microphone;

    /**
     * The Stored algorithm index.
     */
    private int storedAlgorithmIndex = 1;

    /**
     * The Stored key index.
     */
    private int storedKeyIndex = 4;

    /**
     * The Stored microphone index.
     */
    private int storedMicrophoneIndex = 0;

    /**
     * The Stored tune index.
     */
    private int storedTuneIndex = 6;

    /**
     * The Stored concert pitch index.
     */
    private int storedConcertPitchIndex = 9;


    /**
     * Instantiates a new Main model.
     */
    public MainModel() {
    }

    /**
     * Create from string main model.
     *
     * @param string the string
     * @return the main model
     */
    public static MainModel createFromString(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(", ");
        MainModel model = new MainModel();
        Method[] methods = model.getClass().getMethods();

        for (String entry : strings) {
            entry = entry.replaceFirst("get", "set");
            String m = entry.split(":")[0];
            String p = entry.split(":")[1];
            for (Method method : methods) {
                if (method.getName().equals(m)) {
                    try {
                        method.invoke(model, Integer.parseInt(p));
                    } catch (IllegalAccessException | IllegalArgumentException |
                             InvocationTargetException e) {
                        LOGGER.error(e.getMessage());
                    }
                }
            }
        }
        return model;
    }

    /**
     * Get algorithms string [ ].
     *
     * @return the string [ ]
     */
    public String[] getAlgorithms() {
        LOGGER.info("Enter");
        String[] algorithms = microphone.getSupportedAlgorithms();
        LOGGER.info("Return " + Arrays.toString(algorithms));
        return algorithms;
    }

    /**
     * Gets harmonica.
     *
     * @return the harmonica
     */
    public Harmonica getHarmonica() {
        LOGGER.info("Enter");
        LOGGER.info("Return " + this.harmonica.getClass().getName());
        return this.harmonica;
    }

    /**
     * Sets harmonica.
     *
     * @param harmonica the harmonica
     */
    public void setHarmonica(Harmonica harmonica) {
        LOGGER.info("Enter with parameter " + harmonica.getClass().getName());
        this.harmonica = harmonica;
        LOGGER.info("Leave");
    }

    /**
     * Get keys string [ ].
     *
     * @return the string [ ]
     */
    public String[] getKeys() {
        LOGGER.info("Enter");
        String[] keys = AbstractHarmonica.getSupporterKeys();
        LOGGER.info("Return " + Arrays.toString(keys));
        return keys;
    }

    /**
     * Gets microphone.
     *
     * @return the microphone
     */
    public Microphone getMicrophone() {
        LOGGER.info("Enter");
        LOGGER.info("Return " + microphone.getName());
        return microphone;
    }

    /**
     * Sets microphone.
     *
     * @param microphone the microphone
     */
    public void setMicrophone(Microphone microphone) {
        LOGGER.info("Enter with parameter " + microphone.getName());
        this.microphone = microphone;
        LOGGER.info("Leave");
    }

    /**
     * Get microphones string [ ].
     *
     * @return the string [ ]
     */
    public String[] getMicrophones() {
        LOGGER.info("Enter");
        String[] microphones = microphone.getSupportedMicrophones();
        LOGGER.info("Return " + Arrays.toString(microphones));
        return microphones;
    }

    /**
     * Get concert pitches string [ ].
     *
     * @return the string [ ]
     */
    public String[] getConcertPitches() {
        LOGGER.info("Enter");
        String[] pitches = NoteLookup.getSupportedConcertPitches();
        LOGGER.info("Return " + Arrays.toString(pitches));
        return pitches;
    }

    /**
     * Gets selected algorithm index.
     *
     * @return the selected algorithm index
     */
    public int getSelectedAlgorithmIndex() {
        LOGGER.info("Enter");
        String[] algorithms = getAlgorithms();
        int index = 0;
        for (int i = 0; i < algorithms.length; i++) {
            if (algorithms[i].equals(microphone.getAlgorithm())) {
                index = i;
                break;
            }
        }
        LOGGER.info("Return " + index);
        return index;
    }

    /**
     * Gets selected key index.
     *
     * @return the selected key index
     */
    public int getSelectedKeyIndex() {
        LOGGER.info("Enter");
        String[] keys = getKeys();
        int index = 0;
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].equals(harmonica.getKeyName())) {
                index = i;
                break;
            }
        }
        LOGGER.info("Return " + index);
        return index;
    }

    /**
     * Gets selected microphone index.
     *
     * @return the selected microphone index
     */
    public int getSelectedMicrophoneIndex() {
        LOGGER.info("Enter");
        String[] microphones = getMicrophones();
        int index = 0;
        for (int i = 0; i < microphones.length; i++) {
            if (microphones[i].equals(microphone.getName())) {
                index = i;
                break;
            }
        }
        LOGGER.info("Return " + index);
        return index;
    }

    /**
     * Gets selected tune index.
     *
     * @return the selected tune index
     */
    public int getSelectedTuneIndex() {
        LOGGER.info("Enter");
        String[] tunes = getTunes();
        int index = 0;
        for (int i = 0; i < tunes.length; i++) {
            if (tunes[i].equals(harmonica.getTuneName())) {
                index = i;
                break;
            }
        }
        LOGGER.info("Return " + index);
        return index;
    }

    /**
     * Gets selected concert pitch index.
     *
     * @return the selected concert pitch index
     */
    public int getSelectedConcertPitchIndex() {
        LOGGER.info("Enter");
        String[] pitches = getConcertPitches();
        int index = 0;
        for (int i = 0; i < pitches.length; i++) {
            if (pitches[i].equals(NoteLookup.getConcertPitchName())) {
                index = i;
                break;
            }
        }
        LOGGER.info("Return " + index);
        return index;
    }

    /**
     * Gets stored algorithm index.
     *
     * @return the stored algorithm index
     */
    public int getStoredAlgorithmIndex() {
        LOGGER.info("Enter");
        LOGGER.info("Return " + storedAlgorithmIndex);
        return storedAlgorithmIndex;
    }

    /**
     * Sets stored algorithm index.
     *
     * @param algorithmIndex the algorithm index
     */
    public void setStoredAlgorithmIndex(int algorithmIndex) {
        LOGGER.info("Enter with parameter " + algorithmIndex);
        this.storedAlgorithmIndex = algorithmIndex;
        LOGGER.info("Leave");
    }

    /**
     * Gets stored key index.
     *
     * @return the stored key index
     */
    public int getStoredKeyIndex() {
        LOGGER.info("Enter");
        LOGGER.info("Return " + storedKeyIndex);
        return storedKeyIndex;
    }

    /**
     * Sets stored key index.
     *
     * @param keyIndex the key index
     */
    public void setStoredKeyIndex(int keyIndex) {
        LOGGER.info("Enter with parameter " + keyIndex);
        this.storedKeyIndex = keyIndex;
        LOGGER.info("Leave");
    }

    /**
     * Gets stored microphone index.
     *
     * @return the stored microphone index
     */
    public int getStoredMicrophoneIndex() {
        LOGGER.info("Enter");
        LOGGER.info("Return " + storedMicrophoneIndex);
        return storedMicrophoneIndex;
    }

    /**
     * Sets stored microphone index.
     *
     * @param microphoneIndex the microphone index
     */
    public void setStoredMicrophoneIndex(int microphoneIndex) {
        LOGGER.info("Enter with parameter " + microphoneIndex);
        this.storedMicrophoneIndex = microphoneIndex;
        LOGGER.info("Leave");
    }

    /**
     * Gets stored tune index.
     *
     * @return the stored tune index
     */
    public int getStoredTuneIndex() {
        LOGGER.info("Enter");
        LOGGER.info("Return " + storedTuneIndex);
        return storedTuneIndex;
    }

    /**
     * Sets stored tune index.
     *
     * @param storedTuneIndex the stored tune index
     */
    public void setStoredTuneIndex(int storedTuneIndex) {
        LOGGER.info("Enter with parameter " + storedTuneIndex);
        this.storedTuneIndex = storedTuneIndex;
        LOGGER.info("Leave");
    }

    /**
     * Gets stored concert pitch index.
     *
     * @return the stored concert pitch index
     */
    public int getStoredConcertPitchIndex() {
        return storedConcertPitchIndex;
    }

    /**
     * Sets stored concert pitch index.
     *
     * @param storedConcertPitchIndex the stored concert pitch index
     */
    public void setStoredConcertPitchIndex(int storedConcertPitchIndex) {
        LOGGER.info("Enter with parameter " + storedConcertPitchIndex);
        this.storedConcertPitchIndex = storedConcertPitchIndex;
        LOGGER.info("Leave");
    }

    /**
     * Get tunes string [ ].
     *
     * @return the string [ ]
     */
    public String[] getTunes() {
        LOGGER.info("Enter");
        String[] tunes = AbstractHarmonica.getSupportedTunes();
        LOGGER.info("Return " + Arrays.toString(tunes));
        return tunes;
    }

    @Override
    public String toString() {
        Method[] methods = this.getClass().getMethods();
        ArrayList<String> stringList = new ArrayList<>();
        for (Method m : methods) {
            if (m.getName().indexOf("getStored") == 0) {
                if (m.getReturnType().getName().equals("int")) {
                    try {
                        stringList.add(m.getName() + ":" + m.invoke(this));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return stringList.toString();
    }
}