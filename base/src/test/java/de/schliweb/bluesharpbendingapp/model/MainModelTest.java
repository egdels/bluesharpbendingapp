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
class MainModelTest {
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
    void testGetString() {
        MainModel newMainModel = MainModel.createFromString(mainModel.getString());
        assertEquals(1, newMainModel.getStoredMicrophoneIndex());
        assertEquals(2, newMainModel.getStoredAlgorithmIndex());
        assertEquals(3, newMainModel.getStoredKeyIndex());
        assertEquals(100, newMainModel.getStoredConcertPitchIndex());
        assertEquals(42, newMainModel.getStoredTuneIndex());
    }

    /**
     * Test createFromString with an empty string.
     */
    @Test
    void testCreateFromStringEmptyString() {
        MainModel newMainModel = MainModel.createFromString("");
        assertEquals(0, newMainModel.getStoredMicrophoneIndex());
        assertEquals(1, newMainModel.getStoredAlgorithmIndex());
        assertEquals(4, newMainModel.getStoredKeyIndex());
        assertEquals(11, newMainModel.getStoredConcertPitchIndex());
        assertEquals(6, newMainModel.getStoredTuneIndex());
    }

    /**
     * Test createFromString with an invalid string.
     */
    @Test
    void testCreateFromStringInvalidString() {
        MainModel newMainModel = MainModel.createFromString("invalid:data, other:data");
        assertEquals(0, newMainModel.getStoredMicrophoneIndex());
        assertEquals(1, newMainModel.getStoredAlgorithmIndex());
        assertEquals(4, newMainModel.getStoredKeyIndex());
        assertEquals(11, newMainModel.getStoredConcertPitchIndex());
        assertEquals(6, newMainModel.getStoredTuneIndex());
    }

    /**
     * Test createFromString with a valid string.
     */
    @Test
    void testCreateFromStringValidString() {
        String validString = "[getStoredMicrophoneIndex:2, getStoredAlgorithmIndex:4, getStoredKeyIndex:6, "
                + "getStoredConcertPitchIndex:440, getStoredTuneIndex:9]";
        MainModel newMainModel = MainModel.createFromString(validString);
        assertEquals(2, newMainModel.getStoredMicrophoneIndex());
        assertEquals(4, newMainModel.getStoredAlgorithmIndex());
        assertEquals(6, newMainModel.getStoredKeyIndex());
        assertEquals(440, newMainModel.getStoredConcertPitchIndex());
        assertEquals(9, newMainModel.getStoredTuneIndex());
    }

}
