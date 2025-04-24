package de.schliweb.bluesharpbendingapp.model.microphone;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for the AbstractMicrophone class.
 */
class AbstractMicrophoneTest {

    private TestMicrophone microphone;

    @BeforeEach
    void setUp() {
        microphone = new TestMicrophone();
    }

    @Test
    void testGetSupportedAlgorithms() {
        String[] algorithms = AbstractMicrophone.getSupportedAlgorithms();
        assertNotNull(algorithms);
        assertEquals(3, algorithms.length);
        assertEquals("YIN", algorithms[0]);
        assertEquals("MPM", algorithms[1]);
        assertEquals("ZCR", algorithms[2]);
    }

    @Test
    void testGetSupportedConfidences() {
        String[] confidences = AbstractMicrophone.getSupportedConfidences();
        assertNotNull(confidences);
        assertEquals(19, confidences.length);
        assertEquals("0.95", confidences[0]);
        assertEquals("0.05", confidences[18]);
    }

    @Test
    void testGetAlgorithm() {
        // Default algorithm should be "YIN"
        assertEquals("YIN", microphone.getAlgorithm());
    }

    @Test
    void testSetAlgorithm() {
        // Set to MPM (index 1)
        microphone.setAlgorithm(1);
        assertEquals("MPM", microphone.getAlgorithm());

        // Set to invalid index, should not change
        microphone.setAlgorithm(-1);
        assertEquals("MPM", microphone.getAlgorithm());

        microphone.setAlgorithm(100);
        assertEquals("MPM", microphone.getAlgorithm());
    }

    @Test
    void testGetConfidence() {
        // Default confidence should be 0.95
        assertEquals("0.95", microphone.getConfidence());
    }

    @Test
    void testSetConfidence() {
        // Set to 0.9 (index 1)
        microphone.setConfidence(1);
        assertEquals("0.9", microphone.getConfidence());

        // Set to 0.45 (index 10)
        microphone.setConfidence(10);
        assertEquals("0.45", microphone.getConfidence());

        // Set to invalid index, should not change (and log error)
        microphone.setConfidence(-1);
        assertEquals("0.45", microphone.getConfidence());

        microphone.setConfidence(100);
        assertEquals("0.45", microphone.getConfidence());
    }

    /**
     * Test implementation of AbstractMicrophone for testing purposes.
     */
    private static class TestMicrophone extends AbstractMicrophone {
        @Override
        public void close() {
            // No implementation needed for test
        }

        @Override
        public String getName() {
            return "TestMicrophone";
        }

        @Override
        public void setName(int storedMicrophoneIndex) {
            // No implementation needed for test
        }

        @Override
        public String[] getSupportedMicrophones() {
            return new String[]{"TestMicrophone"};
        }

        @Override
        public void open() {
            // No implementation needed for test
        }

        @Override
        public void setMicrophoneHandler(MicrophoneHandler handler) {
            // No implementation needed for test
        }
    }
}
