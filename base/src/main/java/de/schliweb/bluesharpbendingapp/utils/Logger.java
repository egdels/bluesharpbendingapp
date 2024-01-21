package de.schliweb.bluesharpbendingapp.utils;
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

/**
 * The type Logger.
 */
public class Logger {

    /**
     * The constant isDebug.
     */
    private static boolean isDebug = false;

    /**
     * The constant isInfo.
     */
    private static boolean isInfo = false;
    /**
     * The Clazz.
     */
    private final Class<?> clazz;

    /**
     * Instantiates a new Logger.
     *
     * @param clazz the clazz
     */
    public Logger(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * Gets method name.
     *
     * @param depth the depth
     * @return the method name
     */
    public static String getMethodName(final int depth) {
        return Thread.currentThread().getStackTrace()[depth].getMethodName();
    }

    /**
     * Sets debug.
     *
     * @param isDebug the is debug
     */
    public static void setDebug(boolean isDebug) {
        Logger.isDebug = isDebug;
    }

    /**
     * Sets info.
     *
     * @param isInfo the is info
     */
    public static void setInfo(boolean isInfo) {
        Logger.isInfo = isInfo;
    }

    /**
     * Debug.
     *
     * @param string the string
     */
    public void debug(String string) {
        if (Logger.isDebug)
            System.out.println("DEBUG: " + this.clazz.getName() + "." + getMethodName(3) + " " + string);
    }

    /**
     * Error.
     *
     * @param string the string
     */
    public void error(String string) {
        System.out.println("ERROR: " + this.clazz.getName() + "." + getMethodName(3) + " " + string);
    }

    /**
     * Info.
     *
     * @param string the string
     */
    public void info(String string) {
        if (Logger.isInfo)
            System.out.println("INFO: " + this.clazz.getName() + "." + getMethodName(3) + " " + string);
    }
}
