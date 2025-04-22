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

import org.slf4j.MDC;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Utility class for structured logging with SLF4J MDC (Mapped Diagnostic Context).
 * <p>
 * This class provides methods to add context information to log entries,
 * making it easier to trace and filter logs. The context is stored in the
 * MDC, which is included in log entries when using the structured logging
 * configuration.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 * // Add a single context value
 * LoggingContext.put("userId", "12345");
 * log.info("User logged in");
 * LoggingContext.remove("userId");
 *
 * // Execute code with context
 * LoggingContext.withContext("operation", "data-import", () -> {
 *     log.info("Starting data import");
 *     // ... import logic ...
 *     log.info("Data import completed");
 * });
 * </pre>
 * </p>
 */
public class LoggingContext {

    private static final String REQUEST_ID = "requestId";
    private static final String COMPONENT = "component";
    private static final String OPERATION = "operation";
    private static final String USER_ID = "userId";
    private static final String SESSION_ID = "sessionId";

    private LoggingContext() {
    }

    /**
     * Adds a key-value pair to the MDC.
     *
     * @param key   the key to add
     * @param value the value to add
     */
    public static void put(String key, String value) {
        if (key != null && value != null) {
            MDC.put(key, value);
        }
    }

    /**
     * Removes a key from the MDC.
     *
     * @param key the key to remove
     */
    public static void remove(String key) {
        if (key != null) {
            MDC.remove(key);
        }
    }

    /**
     * Clears all entries from the MDC.
     */
    public static void clear() {
        MDC.clear();
    }

    /**
     * Executes a runnable with the given context, and clears the context afterward.
     *
     * @param key      the context key
     * @param value    the context value
     * @param runnable the code to execute with the context
     */
    public static void withContext(String key, String value, Runnable runnable) {
        try {
            put(key, value);
            runnable.run();
        } finally {
            remove(key);
        }
    }

    /**
     * Executes a supplier with the given context, and clears the context afterward.
     *
     * @param key      the context key
     * @param value    the context value
     * @param supplier the code to execute with the context
     * @param <T>      the return type of the supplier
     * @return the result of the supplier
     */
    public static <T> T withContext(String key, String value, Supplier<T> supplier) {
        try {
            put(key, value);
            return supplier.get();
        } finally {
            remove(key);
        }
    }

    /**
     * Executes a runnable with multiple context entries, and clears them afterward.
     *
     * @param context  a map of context keys and values
     * @param runnable the code to execute with the context
     */
    public static void withContext(Map<String, String> context, Runnable runnable) {
        try {
            context.forEach(LoggingContext::put);
            runnable.run();
        } finally {
            context.keySet().forEach(LoggingContext::remove);
        }
    }

    /**
     * Executes a supplier with multiple context entries, and clears them afterward.
     *
     * @param context  a map of context keys and values
     * @param supplier the code to execute with the context
     * @param <T>      the return type of the supplier
     * @return the result of the supplier
     */
    public static <T> T withContext(Map<String, String> context, Supplier<T> supplier) {
        try {
            context.forEach(LoggingContext::put);
            return supplier.get();
        } finally {
            context.keySet().forEach(LoggingContext::remove);
        }
    }

    /**
     * Sets a request ID in the MDC. If no ID is provided, a random UUID is generated.
     *
     * @param requestId the request ID to set, or null to generate a random ID
     * @return the request ID that was set
     */
    public static String setRequestId(String requestId) {
        String id = requestId != null ? requestId : UUID.randomUUID().toString();
        put(REQUEST_ID, id);
        return id;
    }

    /**
     * Sets a component name in the MDC.
     *
     * @param component the component name to set
     */
    public static void setComponent(String component) {
        put(COMPONENT, component);
    }

    /**
     * Sets an operation name in the MDC.
     *
     * @param operation the operation name to set
     */
    public static void setOperation(String operation) {
        put(OPERATION, operation);
    }

    /**
     * Sets a user ID in the MDC.
     *
     * @param userId the user ID to set
     */
    public static void setUserId(String userId) {
        put(USER_ID, userId);
    }

    /**
     * Sets a session ID in the MDC.
     *
     * @param sessionId the session ID to set
     */
    public static void setSessionId(String sessionId) {
        put(SESSION_ID, sessionId);
    }

    /**
     * Gets the current request ID from the MDC.
     *
     * @return the current request ID, or null if not set
     */
    public static String getRequestId() {
        return MDC.get(REQUEST_ID);
    }

    /**
     * Gets the current component name from the MDC.
     *
     * @return the current component name, or null if not set
     */
    public static String getComponent() {
        return MDC.get(COMPONENT);
    }

    /**
     * Gets the current operation name from the MDC.
     *
     * @return the current operation name, or null if not set
     */
    public static String getOperation() {
        return MDC.get(OPERATION);
    }

    /**
     * Gets the current user ID from the MDC.
     *
     * @return the current user ID, or null if not set
     */
    public static String getUserId() {
        return MDC.get(USER_ID);
    }

    /**
     * Gets the current session ID from the MDC.
     *
     * @return the current session ID, or null if not set
     */
    public static String getSessionId() {
        return MDC.get(SESSION_ID);
    }
}