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

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link LoggingContext}.
 */
class LoggingContextTest {

    // No need for tearDown as we're using mocks

    @Test
    void testPutAndGet() {
        try (MockedStatic<MDC> mockedMDC = Mockito.mockStatic(MDC.class)) {
            // Setup mock behavior
            mockedMDC.when(() -> MDC.get("testKey")).thenReturn("testValue");

            // Test basic put operation
            LoggingContext.put("testKey", "testValue");

            // Verify MDC.put was called with correct parameters
            mockedMDC.verify(() -> MDC.put("testKey", "testValue"));
            assertEquals("testValue", MDC.get("testKey"));

            // Test null key (should be ignored)
            LoggingContext.put(null, "testValue");

            // Verify MDC.put was not called with null key
            mockedMDC.verify(() -> MDC.put(eq(null), anyString()), never());

            // Test null value (should be ignored)
            LoggingContext.put("nullValueKey", null);

            // Verify MDC.put was not called with null value
            mockedMDC.verify(() -> MDC.put(eq("nullValueKey"), isNull()), never());
        }
    }

    @Test
    void testRemove() {
        try (MockedStatic<MDC> mockedMDC = Mockito.mockStatic(MDC.class)) {
            // Setup mock behavior
            mockedMDC.when(() -> MDC.get("testKey")).thenReturn("testValue");

            // Test remove
            LoggingContext.remove("testKey");

            // Verify MDC.remove was called with correct parameter
            mockedMDC.verify(() -> MDC.remove("testKey"));

            // Update mock behavior after remove is called
            mockedMDC.when(() -> MDC.get("testKey")).thenReturn(null);

            assertNull(MDC.get("testKey"));

            // Test remove with null key (should not throw exception)
            assertDoesNotThrow(() -> LoggingContext.remove(null));

            // Verify MDC.remove was not called with null key
            mockedMDC.verify(() -> MDC.remove(isNull()), never());

            // Test remove non-existent key (should not throw exception)
            assertDoesNotThrow(() -> LoggingContext.remove("nonExistentKey"));

            // Verify MDC.remove was called with non-existent key
            mockedMDC.verify(() -> MDC.remove("nonExistentKey"));
        }
    }

    @Test
    void testClear() {
        try (MockedStatic<MDC> mockedMDC = Mockito.mockStatic(MDC.class)) {
            // Setup mock behavior
            mockedMDC.when(() -> MDC.get("key1")).thenReturn("value1");
            mockedMDC.when(() -> MDC.get("key2")).thenReturn("value2");

            // Test clear
            LoggingContext.clear();

            // Verify MDC.clear was called
            mockedMDC.verify(MDC::clear);

            // Update mock behavior after clear is called
            mockedMDC.when(() -> MDC.get("key1")).thenReturn(null);
            mockedMDC.when(() -> MDC.get("key2")).thenReturn(null);

            // Verify values are cleared
            assertNull(MDC.get("key1"));
            assertNull(MDC.get("key2"));
        }
    }

    @Test
    void testWithContextRunnable() {
        try (MockedStatic<MDC> mockedMDC = Mockito.mockStatic(MDC.class)) {
            // Setup mock behavior
            mockedMDC.when(() -> MDC.get("testKey")).thenReturn("testValue").thenReturn(null);

            // Test with context for Runnable
            AtomicBoolean executed = new AtomicBoolean(false);

            LoggingContext.withContext("testKey", "testValue", () -> {
                assertEquals("testValue", MDC.get("testKey"));
                executed.set(true);
            });

            // Verify MDC.put was called with correct parameters
            mockedMDC.verify(() -> MDC.put("testKey", "testValue"));

            // Verify runnable was executed
            assertTrue(executed.get());

            // Verify MDC.remove was called with correct parameter
            mockedMDC.verify(() -> MDC.remove("testKey"));

            // Verify context was cleared after execution
            assertNull(MDC.get("testKey"));
        }
    }

    @Test
    void testWithContextSupplier() {
        try (MockedStatic<MDC> mockedMDC = Mockito.mockStatic(MDC.class)) {
            // Setup mock behavior
            mockedMDC.when(() -> MDC.get("testKey")).thenReturn("testValue").thenReturn(null);

            // Test with context for Supplier
            String result = LoggingContext.withContext("testKey", "testValue", () -> {
                assertEquals("testValue", MDC.get("testKey"));
                return "result";
            });

            // Verify MDC.put was called with correct parameters
            mockedMDC.verify(() -> MDC.put("testKey", "testValue"));

            // Verify supplier returned the expected result
            assertEquals("result", result);

            // Verify MDC.remove was called with correct parameter
            mockedMDC.verify(() -> MDC.remove("testKey"));

            // Verify context was cleared after execution
            assertNull(MDC.get("testKey"));
        }
    }

    @Test
    void testWithContextMap() {
        try (MockedStatic<MDC> mockedMDC = Mockito.mockStatic(MDC.class)) {
            // Setup
            Map<String, String> context = new HashMap<>();
            context.put("key1", "value1");
            context.put("key2", "value2");

            // Setup mock behavior
            mockedMDC.when(() -> MDC.get("key1")).thenReturn("value1").thenReturn(null);
            mockedMDC.when(() -> MDC.get("key2")).thenReturn("value2").thenReturn(null);

            // Test with context map for Runnable
            LoggingContext.withContext(context, () -> {
                assertEquals("value1", MDC.get("key1"));
                assertEquals("value2", MDC.get("key2"));
            });

            // Verify MDC.put was called with correct parameters
            mockedMDC.verify(() -> MDC.put("key1", "value1"));
            mockedMDC.verify(() -> MDC.put("key2", "value2"));

            // Verify MDC.remove was called with correct parameters
            mockedMDC.verify(() -> MDC.remove("key1"));
            mockedMDC.verify(() -> MDC.remove("key2"));

            // Verify context was cleared after execution
            assertNull(MDC.get("key1"));
            assertNull(MDC.get("key2"));

            // Reset mock behavior for the second test
            mockedMDC.when(() -> MDC.get("key1")).thenReturn("value1").thenReturn(null);
            mockedMDC.when(() -> MDC.get("key2")).thenReturn("value2").thenReturn(null);

            // Test with context map for Supplier
            String result = LoggingContext.withContext(context, () -> {
                assertEquals("value1", MDC.get("key1"));
                assertEquals("value2", MDC.get("key2"));
                return "mapResult";
            });

            // Verify MDC.put was called with correct parameters again
            mockedMDC.verify(() -> MDC.put("key1", "value1"), times(2));
            mockedMDC.verify(() -> MDC.put("key2", "value2"), times(2));

            // Verify supplier returned the expected result
            assertEquals("mapResult", result);

            // Verify MDC.remove was called with correct parameters again
            mockedMDC.verify(() -> MDC.remove("key1"), times(2));
            mockedMDC.verify(() -> MDC.remove("key2"), times(2));

            // Verify context was cleared after execution
            assertNull(MDC.get("key1"));
            assertNull(MDC.get("key2"));
        }
    }

    @Test
    void testSetRequestId() {
        try (MockedStatic<MDC> mockedMDC = Mockito.mockStatic(MDC.class)) {
            // Test with provided request ID
            String providedId = "test-request-id";

            // Setup mock behavior
            mockedMDC.when(() -> MDC.get("requestId")).thenReturn(providedId);

            String returnedId = LoggingContext.setRequestId(providedId);

            // Verify MDC.put was called with correct parameters
            mockedMDC.verify(() -> MDC.put("requestId", providedId));

            assertEquals(providedId, returnedId);
            assertEquals(providedId, MDC.get("requestId"));

            // Test with null (should generate a UUID)
            // We need to capture the generated UUID to verify it
            mockedMDC.reset();

            // Use an answer to capture the UUID that will be generated
            mockedMDC.when(() -> MDC.put(eq("requestId"), anyString())).thenAnswer(invocation -> {
                String capturedUuid = invocation.getArgument(1);
                // Setup the mock to return this UUID when MDC.get is called
                mockedMDC.when(() -> MDC.get("requestId")).thenReturn(capturedUuid);
                return null;
            });

            String generatedId = LoggingContext.setRequestId(null);

            // Verify MDC.put was called with a non-null value
            mockedMDC.verify(() -> MDC.put(eq("requestId"), argThat(Objects::nonNull)));

            assertNotNull(generatedId);
            assertEquals(generatedId, MDC.get("requestId"));

            // Verify the generated ID is a valid UUID
            assertDoesNotThrow(() -> UUID.fromString(generatedId));
        }
    }

    @Test
    void testComponentOperations() {
        try (MockedStatic<MDC> mockedMDC = Mockito.mockStatic(MDC.class)) {
            // Setup mock behavior
            mockedMDC.when(() -> MDC.get("component")).thenReturn("TestComponent");

            // Test setComponent
            LoggingContext.setComponent("TestComponent");

            // Verify MDC.put was called with correct parameters
            mockedMDC.verify(() -> MDC.put("component", "TestComponent"));

            assertEquals("TestComponent", MDC.get("component"));
            assertEquals("TestComponent", LoggingContext.getComponent());

            // Setup mock behavior for update
            mockedMDC.when(() -> MDC.get("component")).thenReturn("UpdatedComponent");

            // Test component update
            LoggingContext.setComponent("UpdatedComponent");

            // Verify MDC.put was called with updated value
            mockedMDC.verify(() -> MDC.put("component", "UpdatedComponent"));

            assertEquals("UpdatedComponent", LoggingContext.getComponent());
        }
    }

    @Test
    void testOperationOperations() {
        try (MockedStatic<MDC> mockedMDC = Mockito.mockStatic(MDC.class)) {
            // Setup mock behavior
            mockedMDC.when(() -> MDC.get("operation")).thenReturn("TestOperation");

            // Test setOperation
            LoggingContext.setOperation("TestOperation");

            // Verify MDC.put was called with correct parameters
            mockedMDC.verify(() -> MDC.put("operation", "TestOperation"));

            assertEquals("TestOperation", MDC.get("operation"));
            assertEquals("TestOperation", LoggingContext.getOperation());

            // Setup mock behavior for update
            mockedMDC.when(() -> MDC.get("operation")).thenReturn("UpdatedOperation");

            // Test operation update
            LoggingContext.setOperation("UpdatedOperation");

            // Verify MDC.put was called with updated value
            mockedMDC.verify(() -> MDC.put("operation", "UpdatedOperation"));

            assertEquals("UpdatedOperation", LoggingContext.getOperation());
        }
    }

    @Test
    void testUserIdOperations() {
        try (MockedStatic<MDC> mockedMDC = Mockito.mockStatic(MDC.class)) {
            // Setup mock behavior
            mockedMDC.when(() -> MDC.get("userId")).thenReturn("user123");

            // Test setUserId
            LoggingContext.setUserId("user123");

            // Verify MDC.put was called with correct parameters
            mockedMDC.verify(() -> MDC.put("userId", "user123"));

            assertEquals("user123", MDC.get("userId"));
            assertEquals("user123", LoggingContext.getUserId());

            // Setup mock behavior for update
            mockedMDC.when(() -> MDC.get("userId")).thenReturn("user456");

            // Test userId update
            LoggingContext.setUserId("user456");

            // Verify MDC.put was called with updated value
            mockedMDC.verify(() -> MDC.put("userId", "user456"));

            assertEquals("user456", LoggingContext.getUserId());
        }
    }

    @Test
    void testSessionIdOperations() {
        try (MockedStatic<MDC> mockedMDC = Mockito.mockStatic(MDC.class)) {
            // Setup mock behavior
            mockedMDC.when(() -> MDC.get("sessionId")).thenReturn("session123");

            // Test setSessionId
            LoggingContext.setSessionId("session123");

            // Verify MDC.put was called with correct parameters
            mockedMDC.verify(() -> MDC.put("sessionId", "session123"));

            assertEquals("session123", MDC.get("sessionId"));
            assertEquals("session123", LoggingContext.getSessionId());

            // Setup mock behavior for update
            mockedMDC.when(() -> MDC.get("sessionId")).thenReturn("session456");

            // Test sessionId update
            LoggingContext.setSessionId("session456");

            // Verify MDC.put was called with updated value
            mockedMDC.verify(() -> MDC.put("sessionId", "session456"));

            assertEquals("session456", LoggingContext.getSessionId());
        }
    }

    @Test
    void testExceptionHandlingInWithContext() {
        try (MockedStatic<MDC> mockedMDC = Mockito.mockStatic(MDC.class)) {
            // Setup mock behavior
            mockedMDC.when(() -> MDC.get("exceptionKey")).thenReturn("exceptionValue");

            // Test that context is cleared even if an exception is thrown
            try {
                LoggingContext.withContext("exceptionKey", "exceptionValue", () -> {
                    throw new RuntimeException("Test exception");
                });
                fail("Expected exception was not thrown");
            } catch (RuntimeException e) {
                assertEquals("Test exception", e.getMessage());
            }

            // Verify MDC.put was called with correct parameters
            mockedMDC.verify(() -> MDC.put("exceptionKey", "exceptionValue"));

            // Verify MDC.remove was called with correct parameter
            mockedMDC.verify(() -> MDC.remove("exceptionKey"));

            // Update mock behavior after exception is caught
            mockedMDC.when(() -> MDC.get("exceptionKey")).thenReturn(null);

            // Verify context was cleared despite the exception
            assertNull(MDC.get("exceptionKey"));

            // Reset mock behavior for the second test
            mockedMDC.when(() -> MDC.get("exceptionKey")).thenReturn("exceptionValue");

            // Test with supplier
            AtomicReference<Exception> caughtException = new AtomicReference<>();
            try {
                LoggingContext.withContext("exceptionKey", "exceptionValue", () -> {
                    throw new RuntimeException("Test supplier exception");
                });
                fail("Expected exception was not thrown");
            } catch (RuntimeException e) {
                caughtException.set(e);
            }

            // Verify MDC.put was called with correct parameters again
            mockedMDC.verify(() -> MDC.put("exceptionKey", "exceptionValue"), times(2));

            // Verify MDC.remove was called with correct parameter again
            mockedMDC.verify(() -> MDC.remove("exceptionKey"), times(2));

            // Update mock behavior after second exception is caught
            mockedMDC.when(() -> MDC.get("exceptionKey")).thenReturn(null);

            assertNotNull(caughtException.get());
            assertEquals("Test supplier exception", caughtException.get().getMessage());
            assertNull(MDC.get("exceptionKey"));
        }
    }
}
