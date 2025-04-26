# JavaScript Pitch Detector Tests

This directory contains tests for the JavaScript implementations of the YIN and MPM pitch detection algorithms.

## Test Files

- **YINPitchDetectorTest.js**: Contains tests for the YINPitchDetector implementation.
- **MPMPitchDetectorTest.js**: Contains tests for the MPMPitchDetector implementation.
- **index.html**: A simple HTML test runner that can be used to run the tests in a browser.

## Running the Tests

There are two ways to run the tests:

### 1. Using the HTML Test Runner

1. Open the `index.html` file in a web browser.
2. Click the "Run All Tests" button.
3. The test results will be displayed on the page.

### 2. Using Jest or Another JavaScript Testing Framework

The test files are written in a way that they can be adapted to work with Jest or another JavaScript testing framework. To run the tests with Jest:

1. Install Jest: `npm install --save-dev jest`
2. Configure Jest to handle ES modules (if necessary).
3. Run the tests: `jest`

## Test Coverage

The tests cover the following scenarios:

### YIN Pitch Detector Tests

- Detection of pitch with a pure sine wave
- Detection of pitch with silence (should return NO_DETECTED_PITCH)
- Detection of pitch with noise (should return NO_DETECTED_PITCH)
- Detection of pitch with white noise
- Detection of pitch with mixed frequencies

### MPM Pitch Detector Tests

- Detection of pitch with a pure sine wave
- Detection of pitch with silence (should return NO_DETECTED_PITCH)
- Detection of pitch with noise (should return NO_DETECTED_PITCH)
- Detection of pitch with low and high frequencies
- Detection of pitch with harmonics
- Detection of pitch with different amplitudes
- Detection of pitch with different frequencies
- Testing frequency range settings
- Testing with mixed frequencies

## Helper Functions

Both test files include helper functions for generating test audio data:

- `generateSineWave`: Generates a sine wave with a specified frequency, sample rate, and duration.
- `generateMixedSineWaveWithAmplitudes`: Generates a sine wave with two combined frequencies and different amplitudes.
- `generateSineWaveWithNoise`: Generates a sine wave with added white noise.
- `generateOverlappingSineWave`: Generates a sine wave signal that combines two overlapping sine waves with different frequencies and amplitudes.

These functions are used to create test data for the various test scenarios.