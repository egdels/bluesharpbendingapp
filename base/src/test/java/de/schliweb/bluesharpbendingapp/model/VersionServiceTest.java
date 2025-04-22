package de.schliweb.bluesharpbendingapp.model;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for the VersionService utility class.
 * This class tests the basic functionality of the VersionService by directly manipulating
 * its internal state through reflection.
 */
class VersionServiceTest {

    private static final String TEST_VERSION = "1.2.3";

    /**
     * Sets up the test environment before each test by resetting the versionFromHost field.
     */
    @BeforeEach
    void setUp() throws Exception {
        // Reset the static versionFromHost field using reflection
        Field versionField = VersionService.class.getDeclaredField("versionFromHost");
        versionField.setAccessible(true);
        versionField.set(null, null);
    }

    /**
     * Tests that getVersionFromHost returns the cached version if it's already been set.
     */
    @Test
    void testGetVersionFromHost_CachedVersion() throws Exception {
        // Set the versionFromHost field directly using reflection
        Field versionField = VersionService.class.getDeclaredField("versionFromHost");
        versionField.setAccessible(true);
        versionField.set(null, TEST_VERSION);

        // Act
        String result = VersionService.getVersionFromHost();

        // Assert
        assertEquals(TEST_VERSION, result);
    }

    /**
     * Tests that getVersionFromHost returns a non-null value when the version is fetched from the server.
     * <p>
     * Note: This test actually connects to the real server, which is not ideal for a unit test.
     * In a real-world scenario, we would mock the HTTP connection to ensure the test runs in isolation.
     */
    @Test
    void testGetVersionFromHost_FetchesVersion() {
        // Act
        String result = VersionService.getVersionFromHost();

        // Assert
        // We can't assert the exact value since it might change, but we can verify it's not null
        assertTrue(result != null && !result.isEmpty(), "Version should be fetched from server");
    }
}
