# Logging Guidelines for Bluesharp Bending App

This document provides guidelines for logging in the Bluesharp Bending App project. Proper logging is essential for debugging, monitoring, and understanding application behavior.

## Logging Infrastructure

The project uses SLF4J (Simple Logging Facade for Java) as the logging API, which allows for flexibility in the underlying logging implementation.

Two main utility classes are provided to facilitate logging:

1. **LoggingContext**: Provides structured logging capabilities using SLF4J's MDC (Mapped Diagnostic Context)
2. **LoggingUtils**: Provides standardized methods for logging different types of events at appropriate log levels

## Log Levels

Use the following log levels for different types of events:

### TRACE
Very detailed information, useful only when tracing specific operations.
- Method entry/exit in performance-critical code
- Data processing loops
- Detailed algorithm steps

### DEBUG
Detailed information useful for debugging.
- Parameter values
- Internal state changes
- Detailed execution flow
- Audio processing events
- Performance measurements

### INFO
Important application events that highlight the progress of the application.
- Application startup/shutdown
- Component initialization
- User actions
- Operation completions
- Configuration changes

### WARN
Potentially harmful situations that might indicate problems but don't prevent normal operation.
- Deprecated API usage
- Skipped operations due to missing prerequisites
- Recoverable issues
- Resource limitations

### ERROR
Error events that might still allow the application to continue running.
- Exceptions
- Failed operations
- Unexpected conditions that affect functionality

### FATAL
Very severe error events that will likely lead to application termination.
- Critical failures that prevent core functionality from working
- Unrecoverable system errors

## Using LoggingUtils

The `LoggingUtils` class provides methods for logging different types of events at appropriate levels:

```java
// Application startup
LoggingUtils.logAppStartup("MicrophoneController");

// User action
LoggingUtils.logUserAction("Selected microphone", "index=" + microphoneIndex);

// Operation completion
LoggingUtils.logOperationCompleted("Microphone initialization");

// Warning
LoggingUtils.logWarning("Microphone list initialization skipped", "Microphone settings view is not active");

// Error with exception
try {
    // Some operation that might throw an exception
} catch (Exception e) {
    LoggingUtils.logError("Failed to initialize microphone", e);
}

// Performance logging
long startTime = System.currentTimeMillis();
// ... operation ...
long duration = System.currentTimeMillis() - startTime;
LoggingUtils.logPerformance("Audio buffer processing", duration);
```

## Using LoggingContext

The `LoggingContext` class allows adding context information to log entries:

```java
// Add a single context value
LoggingContext.put("userId", "12345");
log.info("User logged in");
LoggingContext.remove("userId");

// Execute code with context
LoggingContext.withContext("operation", "data-import", () -> {
    log.info("Starting data import");
    // ... import logic ...
    log.info("Data import completed");
});

// Set component context
LoggingContext.setComponent("AudioProcessor");
log.info("Processing audio buffer");
```

## Best Practices

1. **Be Consistent**: Use the provided utility classes rather than direct SLF4J calls when possible
2. **Be Concise**: Keep log messages clear and to the point
3. **Be Informative**: Include relevant context in log messages
4. **Be Careful**: Don't log sensitive information (passwords, personal data)
5. **Be Selective**: Use appropriate log levels to avoid log noise
6. **Be Contextual**: Use LoggingContext to add structured context to logs
7. **Be Performance-Aware**: Avoid expensive string concatenation in log statements by using parameterized logging

## Configuration

Log configuration is managed through the logging implementation's configuration files. The project uses different configurations for development and production environments.

For development, logs are more verbose (DEBUG level), while production uses a more restrictive level (INFO) to reduce overhead.