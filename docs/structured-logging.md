# Structured Logging in BluesHarpBendingApp

This document describes the structured logging approach implemented in the BluesHarpBendingApp.

## Overview

Structured logging is a logging approach that treats log entries as structured data rather than plain text. This makes logs more machine-readable and easier to search, filter, and analyze. In BluesHarpBendingApp, we've implemented structured logging using SLF4J, Logback, and the logstash-logback-encoder library.

## Benefits of Structured Logging

- **Improved Searchability**: Structured logs can be easily searched and filtered by specific fields.
- **Better Analysis**: Log data can be analyzed more effectively when it's structured.
- **Consistent Format**: All logs follow a consistent format, making them easier to process.
- **Contextual Information**: Additional context can be added to logs using MDC (Mapped Diagnostic Context).

## Implementation Details

### Dependencies

The structured logging implementation uses the following dependencies:

- SLF4J API: The logging facade used throughout the application
- Logback: The SLF4J implementation
- Logstash Logback Encoder: For JSON-formatted logs

### Configuration

Two Logback configuration files are provided:

1. `logback-dev.xml`: Used in development environments
   - Outputs JSON-formatted logs to the console
   - Includes a commented-out pattern-based appender for developers who prefer traditional logs

2. `logback-prod.xml`: Used in production environments
   - Outputs JSON-formatted logs to a file
   - Implements log rotation with a max file size of 5MB and keeps 7 days of history
   - Includes a commented-out traditional log appender for backward compatibility

### LoggingContext Utility

A `LoggingContext` utility class is provided to make it easy to add contextual information to logs using MDC (Mapped Diagnostic Context). This class includes:

- Methods to add, remove, and clear context entries
- Convenience methods for common context fields (requestId, component, operation, userId, sessionId)
- Methods to execute code with temporary context

## How to Use

### Basic Logging

Use the SLF4J logger as usual with Lombok's `@Slf4j` annotation:

```java
@Slf4j
public class MyClass {
    public void myMethod() {
        log.info("This is an info message");
        log.debug("This is a debug message");
        log.error("This is an error message", new Exception("Something went wrong"));
    }
}
```

### Adding Context with MDC

Use the `LoggingContext` utility to add contextual information to logs:

```java
@Slf4j
public class MyClass {
    public void processRequest(String userId, String requestData) {
        // Add context that will be included in all subsequent log entries
        LoggingContext.setRequestId(null); // Generates a random UUID
        LoggingContext.setUserId(userId);
        LoggingContext.setOperation("processRequest");
        
        log.info("Processing request");
        
        try {
            // Process the request
            log.info("Request processed successfully");
        } catch (Exception e) {
            log.error("Error processing request", e);
        } finally {
            // Clear the context when done
            LoggingContext.clear();
        }
    }
}
```

### Temporary Context

Use the `withContext` methods to add context for a specific block of code:

```java
@Slf4j
public class MyClass {
    public void processMultipleItems(List<Item> items) {
        for (Item item : items) {
            LoggingContext.withContext("itemId", item.getId(), () -> {
                log.info("Processing item");
                // Process the item
                log.info("Item processed");
            });
        }
    }
}
```

### Multiple Context Entries

Use a Map to add multiple context entries at once:

```java
@Slf4j
public class MyClass {
    public void complexOperation() {
        Map<String, String> context = new HashMap<>();
        context.put("operation", "complexOperation");
        context.put("priority", "high");
        context.put("source", "scheduler");
        
        LoggingContext.withContext(context, () -> {
            log.info("Starting complex operation");
            // Perform the operation
            log.info("Complex operation completed");
        });
    }
}
```

## Log Format

The JSON log format includes the following fields:

- **timestamp**: The time when the log entry was created
- **level**: The log level (INFO, DEBUG, ERROR, etc.)
- **thread**: The thread name
- **logger**: The logger name (typically the class name)
- **message**: The log message
- **exception**: Stack trace (if an exception was logged)
- **application**: The application name (bluesharpbendingapp)
- **environment**: The environment (development or production)
- **MDC fields**: Any fields added using the LoggingContext utility

## Best Practices

1. **Use Appropriate Log Levels**:
   - ERROR: For errors that need immediate attention
   - WARN: For potential issues that don't prevent the application from functioning
   - INFO: For significant events in the application
   - DEBUG: For detailed information useful during development and debugging
   - TRACE: For very detailed information

2. **Include Contextual Information**:
   - Use the LoggingContext utility to add relevant context to logs
   - Include identifiers (request ID, user ID, etc.) to correlate related log entries

3. **Log Structured Data**:
   - Log data in a structured way, using the MDC rather than concatenating values into the message
   - This makes it easier to search and analyze logs

4. **Be Mindful of Sensitive Information**:
   - Don't log sensitive information like passwords, tokens, or personal data
   - If necessary, mask or encrypt sensitive data before logging

5. **Log at Entry and Exit Points**:
   - Log at the beginning and end of significant operations
   - Include success/failure status and relevant metrics (duration, size, etc.)

## Viewing and Analyzing Logs

### Development Logs

In development, logs are output to the console in JSON format. You can use tools like jq to filter and format the logs:

```bash
# Example: Filter logs by level
cat application.json | jq 'select(.level == "ERROR")'

# Example: Filter logs by user ID
cat application.json | jq 'select(.userId == "12345")'
```

### Production Logs

In production, logs are written to a file in JSON format. These logs can be processed by log management systems like ELK (Elasticsearch, Logstash, Kibana), Graylog, or similar tools.