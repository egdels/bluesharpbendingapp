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


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The type Main model test.
 */
class MainModelTest {

    @Test
    void testGetSelectedLockScreenIndex_usesSelectedValueIfSet() {
        MainModel model = new MainModel();
        model.setSelectedLockScreenIndex(5);
        model.setStoredLockScreenIndex(2);

        assertEquals(5, model.getSelectedLockScreenIndex());
    }

    @Test
    void testGetSelectedLockScreenIndex_usesStoredValueIfSelectedIsZero() {
        MainModel model = new MainModel();
        model.setSelectedLockScreenIndex(0);
        model.setStoredLockScreenIndex(2);

        assertEquals(2, model.getSelectedLockScreenIndex());
    }

    @Test
    void testGetSelectedLockScreenIndex_returnsZeroWhenBothAreZero() {
        MainModel model = new MainModel();
        model.setSelectedLockScreenIndex(0);
        model.setStoredLockScreenIndex(0);

        assertEquals(0, model.getSelectedLockScreenIndex());
    }

    @Test
    void testGetSelectedLockScreenIndex_handlesNegativeSelectedIndex() {
        MainModel model = new MainModel();
        model.setSelectedLockScreenIndex(-1);
        model.setStoredLockScreenIndex(3);

        assertEquals(-1, model.getSelectedLockScreenIndex());
    }
}
