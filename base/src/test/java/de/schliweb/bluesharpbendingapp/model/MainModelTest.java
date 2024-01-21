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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The type Main model test.
 */
public class MainModelTest {
    /**
     * The constant mainModel.
     */
    private static MainModel mainModel;

    /**
     * Sets up before class.
     */
    @BeforeAll
    static void setUpBeforeClass() {
        mainModel = new MainModel();
        mainModel.setStoredMicrophoneIndex(1);
        mainModel.setStoredAlgorithmIndex(2);
        mainModel.setStoredKeyIndex(3);
        mainModel.setStoredConcertPitchIndex(100);
        mainModel.setStoredTuneIndex(42);
    }

    /**
     * Test to string.
     */
    @Test
    public void testToString() {
        MainModel newMainModel = MainModel.createFromString(mainModel.toString());
        assertEquals(1, newMainModel.getStoredMicrophoneIndex());
        assertEquals(2, newMainModel.getStoredAlgorithmIndex());
        assertEquals(3, newMainModel.getStoredKeyIndex());
        assertEquals(100, newMainModel.getStoredConcertPitchIndex());
        assertEquals(42, newMainModel.getStoredTuneIndex());
    }

}
