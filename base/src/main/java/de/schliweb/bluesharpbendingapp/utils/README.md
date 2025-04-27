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

## Dependency Injection Utilities

### DependencyInjectionInitializer

The `DependencyInjectionInitializer` class provides utilities for initializing dependency injection across different platforms. It reduces code duplication by providing common methods for dependency injection setup.

Key features:
- Common constants for dependency injection configuration
- Methods for creating modules with the necessary dependencies
- Logging utilities for dependency injection initialization
- Platform-independent implementation that works across desktop, android, and other platforms

Usage example:

```java
// Log application startup
DependencyInjectionInitializer.logAppStartup("ComponentName", "Application Name");

// Create modules with the provided dependencies
BaseModule baseModule = DependencyInjectionInitializer.createBaseModule(tempDirectory);
ViewModule viewModule = DependencyInjectionInitializer.createViewModule(mainWindow);
MicrophoneModule microphoneModule = DependencyInjectionInitializer.createMicrophoneModule(microphone);

// Log that dependency injection has been initialized
DependencyInjectionInitializer.logDependencyInjectionInitialized("ComponentName");
```

## Audio Processing Utilities

### PitchDetector

The `PitchDetector` abstract class provides a common interface and shared functionality for different pitch detection algorithms. It defines the basic structure and methods that all pitch detectors use, allowing for consistent configuration and usage across different implementations.

Key features:
- Abstract base class for different pitch detection algorithms (YIN, MPM)
- Configurable frequency range (minimum and maximum frequencies)
- Common utility methods for audio processing (RMS calculation, silence detection)
- Parabolic interpolation for improved peak detection accuracy

Usage example:

```java
// Configure the frequency range
PitchDetector.setMinFrequency(80.0);
PitchDetector.setMaxFrequency(1000.0);

// Detect pitch in audio data
PitchDetector.PitchDetectionResult result = PitchDetector.detectPitchYIN(audioData, sampleRate);
double pitch = result.pitch();
double confidence = result.confidence();
```

### YINPitchDetector

The `YINPitchDetector` class implements the YIN algorithm for pitch detection. It provides a clean, efficient implementation for detecting the fundamental frequency (pitch) of an audio signal.

Key features:
- Implementation of the YIN algorithm for pitch detection
- Frequency range is set via `PitchDetector`
- Dynamic threshold adaptation based on signal energy
- Confidence measure for detected pitches

### MPMPitchDetector

The `MPMPitchDetector` class implements the McLeod Pitch Method (MPM) for pitch detection. It analyzes audio signals to detect the fundamental frequency (pitch) and calculate its confidence.

Key features:
- Implementation of the MPM algorithm for pitch detection
- Normalized Square Difference Function (NSDF) for peak detection
- Frequency range is set via `PitchDetector`
- Confidence measure for detected pitches

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
