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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link LoggingUtils}.
 */
class LoggingUtilsTest {

    private static final String TEST_COMPONENT = "TestComponent";
    private static final String TEST_OPERATION = "TestOperation";
    private static final String TEST_ACTION = "TestAction";
    private static final String TEST_DETAILS = "TestDetails";
    private static final String TEST_WARNING = "TestWarning";
    private static final String TEST_REASON = "TestReason";
    private static final String TEST_ERROR = "TestError";
    private static final String TEST_SETTING = "TestSetting";
    private static final String TEST_VALUE = "TestValue";
    private static final long TEST_DURATION = 100L;
    private static final Exception TEST_EXCEPTION = new RuntimeException("Test exception");

    private Logger mockLogger;

    @BeforeEach
    void setUp() {
        // Create a mock logger
        mockLogger = mock(Logger.class);

        // Use the setLogger method to inject our mock logger
        LoggingUtils.setLogger(mockLogger);
    }

    @Test
    void testLogAppStartup() {
        // Call the method to test
        LoggingUtils.logAppStartup(TEST_COMPONENT);

        // Verify the logger was called with the expected parameters
        verify(mockLogger).info("Application starting: {}", TEST_COMPONENT);
    }

    @Test
    void testLogAppShutdown() {
        LoggingUtils.logAppShutdown(TEST_COMPONENT);

        verify(mockLogger).info("Application shutting down: {}", TEST_COMPONENT);
    }

    @Test
    void testLogInitializing() {
        LoggingUtils.logInitializing(TEST_COMPONENT);

        verify(mockLogger).info("Initializing: {}", TEST_COMPONENT);
    }

    @Test
    void testLogInitialized() {
        LoggingUtils.logInitialized(TEST_COMPONENT);

        verify(mockLogger).info("Initialized successfully: {}", TEST_COMPONENT);
    }

    @Test
    void testLogUserAction() {
        LoggingUtils.logUserAction(TEST_ACTION, TEST_DETAILS);

        verify(mockLogger).info("User action - {}: {}", TEST_ACTION, TEST_DETAILS);
    }

    @Test
    void testLogOperationStarted() {
        LoggingUtils.logOperationStarted(TEST_OPERATION);

        verify(mockLogger).info("Started: {}", TEST_OPERATION);
    }

    @Test
    void testLogOperationCompleted() {
        LoggingUtils.logOperationCompleted(TEST_OPERATION);

        verify(mockLogger).info("Completed successfully: {}", TEST_OPERATION);
    }

    @Test
    void testLogAudioProcessing() {
        LoggingUtils.logAudioProcessing(TEST_ACTION, TEST_DETAILS);

        verify(mockLogger).debug("Audio processing - {}: {}", TEST_ACTION, TEST_DETAILS);
    }

    @Test
    void testLogDebugWithDetails() {
        LoggingUtils.logDebug(TEST_ACTION, TEST_DETAILS);

        verify(mockLogger).debug("{}: {}", TEST_ACTION, TEST_DETAILS);
    }

    @Test
    void testLogDebugWithoutDetails() {
        LoggingUtils.logDebug(TEST_ACTION);

        verify(mockLogger).debug(TEST_ACTION);
    }

    @Test
    void testLogWarningWithReason() {
        LoggingUtils.logWarning(TEST_WARNING, TEST_REASON);

        verify(mockLogger).warn("{}: {}", TEST_WARNING, TEST_REASON);
    }

    @Test
    void testLogWarningWithoutReason() {
        LoggingUtils.logWarning(TEST_WARNING);

        verify(mockLogger).warn(TEST_WARNING);
    }

    @Test
    void testLogErrorWithReason() {
        LoggingUtils.logError(TEST_ERROR, TEST_REASON);

        verify(mockLogger).error("{}: {}", TEST_ERROR, TEST_REASON);
    }

    @Test
    void testLogErrorWithException() {
        LoggingUtils.logError(TEST_ERROR, TEST_EXCEPTION);

        verify(mockLogger).error("{}: {}", TEST_ERROR, TEST_EXCEPTION.getMessage(), TEST_EXCEPTION);
    }

    @Test
    void testLogErrorWithoutDetails() {
        LoggingUtils.logError(TEST_ERROR);

        verify(mockLogger).error(TEST_ERROR);
    }

    @Test
    void testLogFatalWithReason() {
        LoggingUtils.logFatal(TEST_ERROR, TEST_REASON);

        verify(mockLogger).error("FATAL - {}: {}", TEST_ERROR, TEST_REASON);
    }

    @Test
    void testLogFatalWithException() {
        LoggingUtils.logFatal(TEST_ERROR, TEST_EXCEPTION);

        verify(mockLogger).error("FATAL - {}: {}", TEST_ERROR, TEST_EXCEPTION.getMessage(), TEST_EXCEPTION);
    }

    @Test
    void testLogConfigChange() {
        LoggingUtils.logConfigChange(TEST_COMPONENT, TEST_SETTING, TEST_VALUE);

        verify(mockLogger).info("Configuration change - {} - {}: {}", TEST_COMPONENT, TEST_SETTING, TEST_VALUE);
    }

    @Test
    void testLogPerformance() {
        LoggingUtils.logPerformance(TEST_OPERATION, TEST_DURATION);

        verify(mockLogger).debug("Performance - {}: {} ms", TEST_OPERATION, TEST_DURATION);
    }
}
