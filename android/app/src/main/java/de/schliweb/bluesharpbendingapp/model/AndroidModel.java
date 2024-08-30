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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.schliweb.bluesharpbendingapp.utils.Logger;

/**
 * The type Android model.
 */
public class AndroidModel extends MainModel {

    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = new Logger(AndroidModel.class);

    /**
     * The Stored lock screen index.
     */
    private int storedLockScreenIndex = 0;

    /**
     * Instantiates a new Android model.
     */
    public AndroidModel() {
        super();
    }

    /**
     * Create from string android model.
     *
     * @param string the string
     * @return the android model
     */
    public static AndroidModel createFromString(String string) {
        String[] strings = string.replace("[", "").replace("]", "").split(", ");

        AndroidModel model = new AndroidModel();
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
                        } catch (IllegalAccessException | IllegalArgumentException |
                                 InvocationTargetException e) {
                            LOGGER.error(e.getMessage());
                        }
                    }
                }
            }
        }
        return model;
    }

    /**
     * Gets stored lock screen index.
     *
     * @return the stored lock screen index
     */
    public int getStoredLockScreenIndex() {
        return storedLockScreenIndex;
    }

    /**
     * Sets stored lock screen index.
     *
     * @param storedlockScreenIndex the storedlock screen index
     */
    public void setStoredLockScreenIndex(int storedlockScreenIndex) {
        this.storedLockScreenIndex = storedlockScreenIndex;
    }
}
