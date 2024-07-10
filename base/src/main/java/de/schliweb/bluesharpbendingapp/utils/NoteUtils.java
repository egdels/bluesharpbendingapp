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
 * The type Note utils.
 */
public class NoteUtils {

    /**
     * The constant DECIMAL_PRECISION.
     */
    private static final int DECIMAL_PRECISION = 3;

    private NoteUtils() {
    }

    /**
     * Gets cents.
     *
     * @param f1 the f 1
     * @param f2 the f 2
     * @return the cents
     */
    public static double getCents(double f1, double f2) {
        return (1200) * (Math.log((f1 / f2)) / Math.log(2));
    }

    /**
     * Round double.
     *
     * @param value the value
     * @return the double
     */
    public static double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(DECIMAL_PRECISION, RoundingMode.FLOOR);
        return bd.doubleValue();
    }

}
