/*
 * Copyright (c) 2023 Christian Kierdorf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package de.schliweb.bluesharpbendingapp.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Utility class for internationalization (i18n) in the application.
 * Provides methods to manage locales and resource bundles.
 */
public class I18nUtils {

    private static final String RESOURCE_BUNDLE_BASE_NAME = "i18n.messages";

    @Getter
    @Setter
    private static Locale currentLocale = Locale.getDefault();

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private I18nUtils() {
        // Utility class should not be instantiated
    }

    /**
     * Gets the resource bundle for the current locale.
     *
     * @return The resource bundle for the current locale
     */
    public static ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME, currentLocale);
    }

    /**
     * Gets a string from the resource bundle for the current locale.
     *
     * @param key The key of the string to get
     * @return The string for the specified key
     */
    public static String getString(String key) {
        return getResourceBundle().getString(key);
    }

    /**
     * Gets a string from the resource bundle for the current locale and formats it with the specified arguments.
     *
     * @param key  The key of the string to get
     * @param args The arguments to format the string with
     * @return The formatted string for the specified key
     */
    public static String getString(String key, Object... args) {
        return String.format(getString(key), args);
    }

}