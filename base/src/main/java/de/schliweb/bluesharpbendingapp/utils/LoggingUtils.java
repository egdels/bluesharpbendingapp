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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for standardized logging with appropriate log levels.
 * <p>
 * This class provides methods for logging at different levels with clear guidelines
 * on when to use each level. It works with the existing LoggingContext for structured logging.
 * </p>
 * <p>
 * Log Level Guidelines:
 * <ul>
 *   <li><b>TRACE</b>: Very detailed information, useful only when tracing specific operations.
 *       Use for method entry/exit in performance-critical code or data processing loops.</li>
 *   <li><b>DEBUG</b>: Detailed information useful for debugging.
 *       Use for parameter values, internal state changes, and detailed execution flow.</li>
 *   <li><b>INFO</b>: Important application events that highlight the progress of the application.
 *       Use for application startup/shutdown, component initialization, user actions, and operation completions.</li>
 *   <li><b>WARN</b>: Potentially harmful situations that might indicate problems but don't prevent normal operation.
 *       Use for deprecated API usage, skipped operations due to missing prerequisites, or recoverable issues.</li>
 *   <li><b>ERROR</b>: Error events that might still allow the application to continue running.
 *       Use for exceptions, failed operations, or unexpected conditions that affect functionality.</li>
 *   <li><b>FATAL</b>: Very severe error events that will likely lead to application termination.
 *       Use for critical failures that prevent core functionality from working.</li>
 * </ul>
 * </p>
 * <p>
 * Example usage:
 * <pre>
 * // Application startup
 * LoggingUtils.logAppStartup("MicrophoneController");
 *
 * // User action
 * LoggingUtils.logUserAction("Selected microphone", "index=" + microphoneIndex);
 *
 * // Operation completion
 * LoggingUtils.logOperationCompleted("Microphone initialization");
 *
 * // Warning
 * LoggingUtils.logWarning("Microphone list initialization skipped", "Microphone settings view is not active");
 *
 * // Error with exception
 * try {
 *     // Some operation that might throw an exception
 * } catch (Exception e) {
 *     LoggingUtils.logError("Failed to initialize microphone", e);
 * }
 * </pre>
 * </p>
 */
public class LoggingUtils {
    // Default logger instance
    private static Logger log = LoggerFactory.getLogger(LoggingUtils.class);

    /**
     * Sets the logger instance for testing purposes.
     * This method should only be used in test code.
     *
     * @param logger the logger to use
     */
    static void setLogger(Logger logger) {
        log = logger;
    }

    /**
     * Logs application startup events.
     *
     * @param component the component that is starting up
     */
    public static void logAppStartup(String component) {
        log.info("Application starting: {}", component);
    }

    /**
     * Logs application shutdown events.
     *
     * @param component the component that is shutting down
     */
    public static void logAppShutdown(String component) {
        log.info("Application shutting down: {}", component);
    }

    /**
     * Logs component initialization events.
     *
     * @param component the component being initialized
     */
    public static void logInitializing(String component) {
        log.info("Initializing: {}", component);
    }

    /**
     * Logs component initialization completion events.
     *
     * @param component the component that was initialized
     */
    public static void logInitialized(String component) {
        log.info("Initialized successfully: {}", component);
    }

    /**
     * Logs user actions.
     *
     * @param action  the action performed by the user
     * @param details additional details about the action
     */
    public static void logUserAction(String action, String details) {
        log.info("User action - {}: {}", action, details);
    }

    /**
     * Logs operation start events.
     *
     * @param operation the operation being started
     */
    public static void logOperationStarted(String operation) {
        log.info("Started: {}", operation);
    }

    /**
     * Logs operation completion events.
     *
     * @param operation the operation that was completed
     */
    public static void logOperationCompleted(String operation) {
        log.info("Completed successfully: {}", operation);
    }

    /**
     * Logs audio processing events.
     *
     * @param event   the audio processing event
     * @param details additional details about the event
     */
    public static void logAudioProcessing(String event, String details) {
        log.debug("Audio processing - {}: {}", event, details);
    }

    /**
     * Logs detailed debug information.
     *
     * @param message the debug message
     * @param details additional details for debugging
     */
    public static void logDebug(String message, String details) {
        log.debug("{}: {}", message, details);
    }

    /**
     * Logs detailed debug information.
     *
     * @param message the debug message
     */
    public static void logDebug(String message) {
        log.debug(message);
    }

    /**
     * Logs warning events.
     *
     * @param warning the warning message
     * @param reason  the reason for the warning
     */
    public static void logWarning(String warning, String reason) {
        log.warn("{}: {}", warning, reason);
    }

    /**
     * Logs warning events.
     *
     * @param warning the warning message
     */
    public static void logWarning(String warning) {
        log.warn(warning);
    }

    /**
     * Logs error events.
     *
     * @param error  the error message
     * @param reason the reason for the error
     */
    public static void logError(String error, String reason) {
        log.error("{}: {}", error, reason);
    }

    /**
     * Logs error events with an exception.
     *
     * @param error     the error message
     * @param exception the exception that caused the error
     */
    public static void logError(String error, Throwable exception) {
        log.error("{}: {}", error, exception.getMessage(), exception);
    }

    /**
     * Logs error events.
     *
     * @param error the error message
     */
    public static void logError(String error) {
        log.error(error);
    }

    /**
     * Logs fatal error events.
     *
     * @param error  the fatal error message
     * @param reason the reason for the fatal error
     */
    public static void logFatal(String error, String reason) {
        log.error("FATAL - {}: {}", error, reason);
    }

    /**
     * Logs fatal error events with an exception.
     *
     * @param error     the fatal error message
     * @param exception the exception that caused the fatal error
     */
    public static void logFatal(String error, Throwable exception) {
        log.error("FATAL - {}: {}", error, exception.getMessage(), exception);
    }

    /**
     * Logs configuration changes.
     *
     * @param component the component being configured
     * @param setting   the setting being changed
     * @param value     the new value of the setting
     */
    public static void logConfigChange(String component, String setting, String value) {
        log.info("Configuration change - {} - {}: {}", component, setting, value);
    }

    /**
     * Logs performance-related events.
     *
     * @param operation the operation being measured
     * @param duration  the duration of the operation in milliseconds
     */
    public static void logPerformance(String operation, long duration) {
        log.debug("Performance - {}: {} ms", operation, duration);
    }
}
