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

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


/**
 * This class represents an Android-specific model that extends the functionality of the MainModel.
 * It provides the ability to store lock screen indices and create model instances from string representations.
 * <p>
 * The class includes utility methods for converting a string format to an AndroidModel instance
 * using reflection to dynamically set properties.
 */
@Setter
@Getter
@Slf4j
public class AndroidModel extends MainModel {


    /**
     * Represents the index of the currently stored lock screen configuration in the {@code AndroidModel}.
     * This variable is initialized with a default value of 0 and can be set or retrieved
     * using the appropriate getter and setter methods.
     */
    private int storedLockScreenIndex = 0;


    /**
     * Default constructor for the AndroidModel class.
     * Initializes a new instance of AndroidModel by invoking the superclass constructor.
     */
    public AndroidModel() {
        super();
    }


    /**
     * Creates an instance of AndroidModel by parsing a formatted string and dynamically
     * setting the corresponding field values using reflection. The input string should
     * contain key-value pairs in a specific format that map to the fields of AndroidModel.
     *
     * @param string the formatted string representation of the model, with key-value pairs
     *               separated by commas. Each key-value pair should be in the format
     *               'getField:value', where 'getField' corresponds to a getter of a field
     *               in the AndroidModel class.
     * @return an instance of AndroidModel with its fields set to the values parsed from the input string.
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
                            log.error(e.getMessage());
                        }
                    }
                }
            }
        }
        return model;
    }

    /**
     * Converts a string representation into an instance of AndroidModel.
     * Delegates the string parsing and instance creation to the createFromString(String) method.
     *
     * @param input the string representation of the AndroidModel, formatted with key-value pairs
     *              that map to the fields of AndroidModel. Each key-value pair should be in the
     *              format 'getField:value'.
     * @return an AndroidModel instance with fields set based on the input string.
     */
    // Non-static equivalent:
    public AndroidModel fromString(String input) {
        return createFromString(input);
    }


}
