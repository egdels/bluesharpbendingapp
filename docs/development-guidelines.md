# Development Guidelines for Bluesharp Bending App

This document provides comprehensive guidelines for developers contributing to the Bluesharp Bending App project. Following these guidelines ensures consistency across the codebase and helps maintain high code quality.

## Table of Contents

1. [Code Style and Formatting](#code-style-and-formatting)
2. [Documentation Standards](#documentation-standards)
3. [Project Structure](#project-structure)
4. [Development Workflow](#development-workflow)
5. [Testing Requirements](#testing-requirements)
6. [Logging Practices](#logging-practices)
7. [Dependency Management](#dependency-management)
8. [Performance Considerations](#performance-considerations)

## Code Style and Formatting

### Java Code Style

- **Indentation**: Use 4 spaces for indentation, not tabs.
- **Line Length**: Keep lines under 120 characters.
- **Naming Conventions**:
  - Classes: `PascalCase` (e.g., `MainController`, `HarmonicaModel`)
  - Methods and variables: `camelCase` (e.g., `detectPitch()`, `audioBuffer`)
  - Constants: `UPPER_SNAKE_CASE` (e.g., `DEFAULT_SAMPLE_RATE`, `TEMP_FILE`)
  - Packages: lowercase with dots (e.g., `de.schliweb.bluesharpbendingapp.utils`)
- **Braces**: Use the "Egyptian brackets" style with opening braces on the same line.
  ```java
  if (condition) {
      // code
  } else {
      // code
  }
  ```
- **Imports**:
  - Organize imports alphabetically within groups
  - No wildcard imports (e.g., `import java.util.*`)
  - Group imports in the following order:
    1. Java standard library imports
    2. Third-party library imports
    3. Project-specific imports

### XML/FXML Style

- Use 2 spaces for indentation in XML/FXML files.
- Keep attributes on separate lines if there are more than 3 attributes.
- Use descriptive `fx:id` values that match their purpose.

### CSS Style

- Use 2 spaces for indentation in CSS files.
- Use kebab-case for class and ID names (e.g., `main-container`, `note-display`).
- Group related properties together.
- Add comments to explain complex styling decisions.

## Documentation Standards

### JavaDoc

- All public classes, methods, and fields should have JavaDoc comments.
- JavaDoc should explain the purpose, not just restate the name.
- Include `@param`, `@return`, and `@throws` tags where applicable.
- Use HTML formatting in JavaDoc when needed for clarity.

Example:
```java
/**
 * Detects the pitch from the provided audio data.
 * 
 * @param audioData The audio data as an array of doubles
 * @param sampleRate The sample rate of the audio data in Hz
 * @return A PitchDetectionResult containing the detected pitch and confidence
 * @throws IllegalArgumentException if audioData is null or empty
 */
public static PitchDetector.PitchDetectionResult detectPitch(double[] audioData, int sampleRate) {
    // Implementation
}
```

### Markdown Documentation

- Use Markdown for all documentation files.
- Follow a consistent structure with clear headings and subheadings.
- Include a table of contents for longer documents.
- Use code blocks with language specification for code examples.
- Include examples and explanations for complex concepts.

### Code Comments

- Use comments to explain "why", not "what" (the code should be self-explanatory).
- Add comments for complex algorithms or business logic.
- Keep comments up-to-date when changing code.
- Use TODO comments for planned improvements, but track them in the issue tracker as well.

## Project Structure

### Module Organization

The project is divided into several modules:

- **base**: Core functionality shared across all platforms
- **desktop**: Desktop application (JavaFX)
- **android**: Android application
- **webapp**: Web application (Spring Boot)
- **tuner**: Example application for pitch detection

### Package Structure

Follow this package structure within each module:

- `de.schliweb.bluesharpbendingapp`: Root package
  - `.app`: Application entry points and configuration
  - `.controller`: Controllers (MVC pattern)
  - `.model`: Data models and business logic
  - `.view`: UI components
    - `.desktop`: Desktop-specific views
    - `.android`: Android-specific views
    - `.web`: Web-specific views
  - `.utils`: Utility classes
  - `.di`: Dependency injection configuration

### Resource Organization

- Place FXML files in `/resources/fxml/`
- Place CSS files in `/resources/styles/`
- Place images in `/resources/images/`
- Place configuration files in `/resources/config/`

## Development Workflow

### Git Workflow

1. **Branching Strategy**:
   - `main`: Stable production code
   - `develop`: Integration branch for features
   - `feature/*`: Feature branches
   - `bugfix/*`: Bug fix branches
   - `release/*`: Release preparation branches

2. **Commit Messages**:
   - Use present tense ("Add feature" not "Added feature")
   - Start with a verb
   - Keep the first line under 50 characters
   - Add detailed description after a blank line if needed

3. **Pull Requests**:
   - Create a pull request for each feature or bug fix
   - Include a clear description of the changes
   - Reference related issues
   - Ensure all tests pass before requesting review

### Development Process

1. **Issue Tracking**:
   - Create an issue for each feature, enhancement, or bug
   - Use labels to categorize issues
   - Assign issues to team members

2. **Development Cycle**:
   - Pick an issue from the backlog
   - Create a feature branch
   - Implement the feature with tests
   - Submit a pull request
   - Address review comments
   - Merge to develop branch

3. **Release Process**:
   - Create a release branch from develop
   - Perform final testing
   - Update version numbers
   - Merge to main
   - Tag the release
   - Build and publish artifacts

## Testing Requirements

### Unit Testing

- Use JUnit 5 for unit tests.
- Aim for at least 80% code coverage for core functionality.
- Test both positive and negative cases.
- Use descriptive test method names that explain the scenario and expected outcome.
- Structure tests using the Arrange-Act-Assert pattern.

Example:
```java
@Test
void detectPitch_withValidAudioData_returnsCorrectFrequency() {
    // Arrange
    double[] audioData = generateSineWave(440.0, DEFAULT_SAMPLE_RATE, 0.1);
    
    // Act
    PitchDetector.PitchDetectionResult result = YINPitchDetector.detectPitch(audioData, DEFAULT_SAMPLE_RATE);
    
    // Assert
    assertEquals(440.0, result.pitch(), 1.0);
}
```

### Performance Testing

- Include performance tests for time-critical operations.
- Use JUnit 5 parameterized tests for testing with different inputs.
- Include warmup iterations to get consistent results.
- Log performance metrics for analysis.
- Compare different implementations or configurations.

### UI Testing

- Test UI components for correct rendering and behavior.
- Use TestFX for JavaFX UI testing.
- Test user workflows and interactions.
- Verify UI responsiveness and performance.

## Logging Practices

Follow the detailed guidelines in [logging.md](logging.md) and [structured-logging.md](structured-logging.md). Key points:

- Use the provided `LoggingContext` and `LoggingUtils` classes.
- Set the component context before logging.
- Use appropriate log levels (TRACE, DEBUG, INFO, WARN, ERROR, FATAL).
- Include relevant context in log messages.
- Avoid logging sensitive information.

Example:
```java
LoggingContext.setComponent("PitchDetector");
LoggingUtils.logOperationStarted("Pitch detection");
// Operation code
LoggingUtils.logOperationCompleted("Pitch detection");
```

## Dependency Management

- Use Gradle for dependency management.
- Keep dependencies up-to-date but stable.
- Avoid adding unnecessary dependencies.
- Document the purpose of each dependency in build.gradle files.
- Use version catalogs for consistent dependency versions across modules.

## Performance Considerations

### Audio Processing

- Optimize audio processing algorithms for real-time performance.
- Use appropriate buffer sizes for the target platform.
- Consider the trade-off between accuracy and performance.
- Profile and benchmark audio processing code regularly.

### UI Responsiveness

- Keep UI operations off the main thread.
- Use background threads for long-running operations.
- Update UI elements efficiently to avoid jank.
- Implement proper cancellation for background tasks.

### Memory Management

- Avoid unnecessary object creation in performance-critical code.
- Reuse buffers and objects where possible.
- Be mindful of memory usage on mobile devices.
- Profile memory usage to identify leaks or inefficiencies.