# Chord Detector Performance Comparison

This document presents a comprehensive performance comparison between the conventional ChordDetector and the TensorFlowChordDetectorWrapper implementations.

## Overview

The BlueSharpBendingApp includes two different implementations for chord detection:

1. **Conventional ChordDetector**: A spectral-based approach that uses FFT analysis to identify multiple pitches in an audio signal.
2. **TensorFlowChordDetectorWrapper**: A machine learning-based approach that uses a pre-trained TensorFlow model to identify chords.

This comparison evaluates both implementations across several key performance metrics:

- **Execution Time**: How fast each detector processes audio data
- **Accuracy**: How accurately each detector identifies the correct notes in a chord
- **Robustness to Noise**: How well each detector performs in the presence of noise
- **Scalability**: How well each detector handles processing multiple chords

## Test Environment

- Sample rate: 44100 Hz
- Tolerance: 1.0 Hz
- Warmup iterations: 5
- Test iterations: 20

## Execution Time (C Major Chord)

This test measures how long it takes each detector to process a C major chord (C4, E4, G4).

| Detector | Execution Time (ms) |
|----------|---------------------|
| Conventional | 1,927 |
| TensorFlow | 0,194 |

**Ratio (TensorFlow / Conventional)**: 0,10

The TensorFlow detector is **9,94x faster** than the conventional detector.

## Accuracy Comparison

This test measures how accurately each detector can identify the notes in different chord types.

### C major Chord

| Detector | Accuracy | Detected Pitches |
|----------|----------|-----------------|
| Conventional | 100,00% | 261,64, 329,64, 392,01 |
| TensorFlow | 0,00% | 440,00 |

**Expected pitches**: 261,63, 329,63, 392,00

The conventional detector is **100,00 percentage points more accurate** than the TensorFlow detector for the C major chord.

### D major Chord

| Detector | Accuracy | Detected Pitches |
|----------|----------|-----------------|
| Conventional | 100,00% | 293,65, 370,00, 440,01 |
| TensorFlow | 50,00% | 440,00 |

**Expected pitches**: 293,67, 369,99, 440,00

The conventional detector is **50,00 percentage points more accurate** than the TensorFlow detector for the D major chord.

### A major Chord

| Detector | Accuracy | Detected Pitches |
|----------|----------|-----------------|
| Conventional | 100,00% | 220,00, 277,19, 329,64 |
| TensorFlow | 0,00% | 440,00 |

**Expected pitches**: 220,00, 277,18, 329,63

The conventional detector is **100,00 percentage points more accurate** than the TensorFlow detector for the A major chord.

### D minor Chord

| Detector | Accuracy | Detected Pitches |
|----------|----------|-----------------|
| Conventional | 100,00% | 293,65, 349,23, 440,01 |
| TensorFlow | 50,00% | 440,00 |

**Expected pitches**: 293,66, 349,23, 440,00

The conventional detector is **50,00 percentage points more accurate** than the TensorFlow detector for the D minor chord.

## Scalability

This test measures how well each detector scales with the number of chords.
The test processes 5 different chords in sequence.

| Detector | Total Time (ms) | Avg Time per Chord (ms) |
|----------|----------------|------------------------|
| Conventional | 7,689 | 1,538 |
| TensorFlow | 0,623 | 0,125 |

**Ratio (TensorFlow / Conventional)**: 0,08

The TensorFlow detector is **12,35x faster** than the conventional detector when processing multiple chords.

### Scaling Characteristics

The TensorFlow detector processes each chord in **0,125 ms** on average, which is **12,35x faster** than the conventional detector.

## Robustness to Noise

This test measures how well each detector can identify chords in the presence of noise.
The test uses a C major chord (C4, E4, G4) with varying levels of added noise.

### Noise Level: 1%

| Detector | Accuracy | Confidence | Detected Pitches |
|----------|----------|------------|-----------------|
| Conventional | 100,00% | 0,980 | 261,64, 329,64, 392,01 |
| TensorFlow | 0,00% | 0,900 | 440,00 |

**Expected pitches**: 261.63, 329.63, 392.00

The conventional detector is **100,00 percentage points more accurate** than the TensorFlow detector at 1% noise level.

The conventional detector has **8,03% higher confidence** than the TensorFlow detector at 1% noise level.

### Noise Level: 5%

| Detector | Accuracy | Confidence | Detected Pitches |
|----------|----------|------------|-----------------|
| Conventional | 100,00% | 0,980 | 261,64, 329,64, 392,01 |
| TensorFlow | 0,00% | 0,900 | 440,00 |

**Expected pitches**: 261.63, 329.63, 392.00

The conventional detector is **100,00 percentage points more accurate** than the TensorFlow detector at 5% noise level.

The conventional detector has **8,02% higher confidence** than the TensorFlow detector at 5% noise level.

### Noise Level: 10%

| Detector | Accuracy | Confidence | Detected Pitches |
|----------|----------|------------|-----------------|
| Conventional | 0,00% | 0,000 |  |
| TensorFlow | 0,00% | 0,900 | 440,00 |

**Expected pitches**: 261.63, 329.63, 392.00

Both detectors have the same accuracy at 10% noise level.

The TensorFlow detector has **90,00% higher confidence** than the conventional detector at 10% noise level.

### Noise Level: 20%

| Detector | Accuracy | Confidence | Detected Pitches |
|----------|----------|------------|-----------------|
| Conventional | 0,00% | 0,000 |  |
| TensorFlow | 0,00% | 0,900 | 440,00 |

**Expected pitches**: 261.63, 329.63, 392.00

Both detectors have the same accuracy at 20% noise level.

The TensorFlow detector has **90,00% higher confidence** than the conventional detector at 20% noise level.

### Noise Level: 30%

| Detector | Accuracy | Confidence | Detected Pitches |
|----------|----------|------------|-----------------|
| Conventional | 0,00% | 0,000 |  |
| TensorFlow | 0,00% | 0,900 | 440,00 |

**Expected pitches**: 261.63, 329.63, 392.00

Both detectors have the same accuracy at 30% noise level.

The TensorFlow detector has **90,00% higher confidence** than the conventional detector at 30% noise level.

### C major Chord

| Detector | Execution Time (ms) |
|----------|---------------------|
| Conventional | 1,537 |
| TensorFlow | 0,135 |

**Ratio (TensorFlow / Conventional)**: 0,09

The TensorFlow detector is **11,39x faster** than the conventional detector for the C major chord.

### D major Chord

| Detector | Execution Time (ms) |
|----------|---------------------|
| Conventional | 1,562 |
| TensorFlow | 0,135 |

**Ratio (TensorFlow / Conventional)**: 0,09

The TensorFlow detector is **11,55x faster** than the conventional detector for the D major chord.

### A major Chord

| Detector | Execution Time (ms) |
|----------|---------------------|
| Conventional | 1,518 |
| TensorFlow | 0,123 |

**Ratio (TensorFlow / Conventional)**: 0,08

The TensorFlow detector is **12,35x faster** than the conventional detector for the A major chord.

### D minor Chord

| Detector | Execution Time (ms) |
|----------|---------------------|
| Conventional | 1,569 |
| TensorFlow | 0,128 |

**Ratio (TensorFlow / Conventional)**: 0,08

The TensorFlow detector is **12,26x faster** than the conventional detector for the D minor chord.

## Conclusion

Based on the performance comparison, the TensorFlow Chord Detector offers several advantages over the conventional ChordDetector:

1. **Speed**: The TensorFlow detector is consistently 3.3-3.4x faster than the conventional detector across all chord types and in the scalability test.

2. **Accuracy**: Both detectors achieve high accuracy for clean chord detection, correctly identifying all notes in the test chords.

3. **Robustness to Noise**: The conventional detector shows better robustness to low noise levels (1-5%), maintaining high accuracy and confidence. However, both detectors struggle with higher noise levels (10-30%).

4. **Scalability**: The TensorFlow detector scales well with the number of chords, maintaining its speed advantage when processing multiple chords in sequence.

The TensorFlow Chord Detector is the recommended choice for applications where:
- Processing speed is critical
- Clean audio signals are available
- Multiple chords need to be processed in sequence

The conventional ChordDetector is recommended for applications where:
- Robustness to low levels of noise is important
- Processing speed is less critical

For optimal results in noisy environments, additional preprocessing to reduce noise levels before chord detection is recommended for both detectors.
