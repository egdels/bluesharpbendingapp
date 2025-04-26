# JavaScript Pitch Detector Test Results

## Introduction

This document provides the results of the tests for the JavaScript implementations of the YIN and MPM pitch detection algorithms. These tests verify that the JavaScript implementations function correctly and produce results comparable to their Java counterparts.

## How to Run the Tests

The tests can be run using the HTML test runner provided in the `index.html` file:

1. Open the `index.html` file in a web browser
2. Click the "Run All Tests" button
3. The test results will be displayed on the page, showing which tests passed and which failed

Alternatively, the tests can be adapted to run with a JavaScript testing framework like Jest.

## Test Results

### YIN Pitch Detector Tests

| Test Case | Description | Expected Outcome | Result |
|-----------|-------------|------------------|--------|
| Pure Sine Wave | Tests detection of a 440Hz sine wave | Should detect pitch of 440Hz with high accuracy (within 0.02Hz) | PASS |
| Silence | Tests detection with silence (sample sizes 44100 and 48000) | Should return NO_DETECTED_PITCH | PASS |
| Noise | Tests detection with random noise | Should return NO_DETECTED_PITCH | PASS |
| Frequency with White Noise | Tests detection of a 934.6Hz sine wave with 10% white noise | Should detect pitch of 934.6Hz (within 0.5Hz) with confidence > 0.6 | PASS |
| Mixed Frequencies (Weak Subharmonic) | Tests detection with main frequency 934.6Hz (amplitude 1.0) and subharmonic 460.0Hz (amplitude 0.3) | Should detect main frequency within 5.0Hz with confidence > 0.8 | PASS |
| Mixed Frequencies (Moderate Subharmonic) | Tests detection with main frequency 934.6Hz (amplitude 1.0) and subharmonic 460.0Hz (amplitude 0.5) | Should detect main frequency within 10.0Hz with confidence > 0.3 | PASS |
| Mixed Frequencies (Dominant Main) | Tests detection with main frequency 934.6Hz (amplitude 1.9) and subharmonic 460.0Hz (amplitude 1.0) | Should detect main frequency within 10.0Hz with confidence > 0.1 | PASS |

### MPM Pitch Detector Tests

| Test Case | Description | Expected Outcome | Result |
|-----------|-------------|------------------|--------|
| Pure Sine Wave | Tests detection of a 440Hz sine wave | Should detect pitch of 440Hz (within 1.0Hz) with confidence > 0.9 | PASS |
| Silence | Tests detection with silence | Should return NO_DETECTED_PITCH with confidence 0.0 | PASS |
| Noise | Tests detection with random noise | Should return NO_DETECTED_PITCH with confidence 0.0 | PASS |
| Low Frequency | Tests detection of an 80Hz sine wave | Should detect pitch of 80Hz (within 1.0Hz) with confidence > 0.9 | PASS |
| High Frequency | Tests detection of a 4000Hz sine wave | Should detect pitch of 4000Hz (within 1.0Hz) with confidence > 0.9 | PASS |
| Harmonics | Tests detection with fundamental frequency 440Hz and first harmonic 880Hz at half amplitude | Should detect fundamental frequency of 440Hz (within 1.0Hz) with confidence > 0.9 | PASS |
| Different Amplitudes | Tests detection with amplitudes 0.01, 0.1, and 1.0 | Should detect pitch of 440Hz (within 1.0Hz) with confidence > 0.9 regardless of amplitude | PASS |
| Different Frequencies | Tests detection with frequencies 100Hz, 440Hz, and 2000Hz | Should detect each frequency within specified tolerance with confidence > 0.9 | PASS |
| Frequency Range Settings | Tests setting min/max frequency to 100Hz/3000Hz | Should detect pitch of 440Hz (within 1.0Hz) and correctly set min/max frequency | PASS |
| Frequency Outside Range | Tests detection with frequency 440Hz when range is set to 500Hz-1000Hz | Should return NO_DETECTED_PITCH | PASS |
| Default Frequency Range | Tests that default frequency range is set correctly | Should have correct default min/max frequency values | PASS |
| Mixed Frequencies | Tests detection with various combinations of main and subharmonic frequencies | Should detect main frequency within specified tolerance with confidence above minimum threshold | PASS |

## Test Coverage

The tests cover a wide range of scenarios for both pitch detection algorithms:

### YIN Algorithm Coverage:
- Basic pitch detection with pure sine waves
- Handling of silence and noise
- Robustness to white noise
- Detection of main frequency in the presence of subharmonics
- Different confidence thresholds based on signal characteristics

### MPM Algorithm Coverage:
- Basic pitch detection with pure sine waves
- Handling of silence and noise
- Detection of low and high frequencies
- Handling of harmonics
- Robustness to different signal amplitudes
- Configurable frequency range
- Detection of main frequency in the presence of subharmonics

## Conclusion

All tests for both the YIN and MPM pitch detection algorithms pass successfully. This indicates that the JavaScript implementations are functioning correctly and producing results comparable to their Java counterparts. The implementations are robust to various signal characteristics and edge cases, making them suitable for use in the application.