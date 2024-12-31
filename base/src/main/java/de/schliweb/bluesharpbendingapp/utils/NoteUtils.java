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

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * The NoteUtils class provides utility methods for working with musical notes and frequencies.
 * The methods in this class involve calculations related to cents (a unit for measuring musical intervals),
 * as well as rounding and frequency adjustments.
 * It is designed as a utility class and cannot be instantiated.
 */
public class NoteUtils {

    /**
     * Defines the number of decimal places to which values should be rounded.
     * This constant is used primarily in rounding operations, such as when trimming
     * numerical results to a specific precision for display or further calculations.
     */
    private static final int DECIMAL_PRECISION = 3;

    private NoteUtils() {
    }

    /**
     * Calculates the difference in pitch between two frequencies, measured in cents.
     * Cents are a logarithmic unit of measure used for musical intervals, where 1200 cents
     * represents an octave. This method computes the value using the formula:
     * cents = 1200 * log2(f1 / f2).
     *
     * @param f1 the first frequency (e.g., the higher frequency in the interval)
     * @param f2 the second frequency (e.g., the lower frequency in the interval)
     * @return the interval between the two frequencies, in cents
     */
    public static double getCents(double f1, double f2) {
        return (1200) * (Math.log((f1 / f2)) / Math.log(2));
    }

    /**
     * Rounds the given value to a specific number of decimal places using floor rounding mode.
     *
     * @param value the value to be rounded
     * @return the rounded value as a double
     */
    public static double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(DECIMAL_PRECISION, RoundingMode.FLOOR);
        return bd.doubleValue();
    }

    /**
     * Adjusts the given frequency by a specified number of cents.
     * This method calculates the new frequency by applying the formula
     * based on cents, which is a logarithmic unit used to measure musical intervals.
     *
     * @param cents the number of cents to add or subtract from the base frequency
     * @param frequency the base frequency to be adjusted
     * @return the adjusted frequency after applying the given cents
     */
    public static double addCentsToFrequency(double cents, double frequency) {
        return (Math.pow(2.0, cents / 1200.0) * frequency);
    }

}
