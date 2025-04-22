# Utility Classes

This package contains utility classes that provide common functionality used throughout the application.

## Logging Utilities

### LoggingContext

The `LoggingContext` class provides structured logging capabilities using SLF4J's MDC (Mapped Diagnostic Context). It allows adding context information to log entries, making it easier to trace and filter logs.

Key features:
- Add, remove, and clear context entries
- Execute code with temporary context
- Convenience methods for common context types (request ID, component, operation, user ID, session ID)

For detailed usage guidelines, see the [Logging Documentation](../../../../../../../../docs/logging.md).

### LoggingUtils

The `LoggingUtils` class provides standardized methods for logging different types of events at appropriate log levels. It works with the existing `LoggingContext` for structured logging.

Key features:
- Methods for logging application lifecycle events (startup, shutdown, initialization)
- Methods for logging user actions and operations
- Methods for logging warnings, errors, and fatal errors
- Methods for logging performance metrics

The class implements appropriate log levels for different types of events:
- TRACE: Very detailed information for tracing specific operations
- DEBUG: Detailed information for debugging
- INFO: Important application events
- WARN: Potentially harmful situations
- ERROR: Error events
- FATAL: Very severe error events

For detailed usage guidelines, see the [Logging Documentation](../../../../../../../../docs/logging.md).

## How to Add New Utility Classes

When adding new utility classes to this package:

1. Follow the single responsibility principle - each utility class should focus on a specific concern
2. Make utility methods static when they don't require instance state
3. Add comprehensive JavaDoc comments
4. Consider creating a corresponding test class in the test package
5. Update this README.md file to document the new utility class

## Best Practices for Utility Classes

1. Keep utility classes focused on a single concern
2. Make utility methods stateless when possible
3. Use meaningful method names that clearly describe what the method does
4. Add proper error handling and input validation
5. Write unit tests for all utility methods
6. Document the purpose and usage of each utility class and method