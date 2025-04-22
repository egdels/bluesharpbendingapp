# Audio Processing Performance Testing

This document describes the approach used for testing the performance of audio processing algorithms in the Bluesharp Bending App.

## Overview

The Bluesharp Bending App relies on real-time audio processing to detect the pitch of notes played on a harmonica. The performance of these algorithms is critical for providing a responsive user experience. This document outlines the performance testing methodology and provides guidance on interpreting the results.

## Algorithms Tested

The app uses two main pitch detection algorithms:

1. **YIN Algorithm** - A time-domain pitch detection algorithm that computes a difference function, normalizes it, and finds the first minimum below a threshold to determine the pitch.
   - An optimized version of the YIN algorithm specifically for harmonica sounds is currently in development and being tested for performance improvements.

2. **McLeod Pitch Method (MPM)** - An algorithm that calculates a normalized square difference function (NSDF), finds peaks, and selects the most significant peak to determine the pitch.

## Testing Methodology

The performance tests are implemented in the `PitchDetectionPerformanceTest` class. The tests measure the execution time of both algorithms with different input sizes and characteristics:

1. **Different buffer sizes** - Tests how the algorithms scale with increasing audio duration (0.1s, 0.5s, 1.0s, 2.0s).
2. **Different signal types** - Tests performance with various audio signals:
   - Pure sine waves
   - Square waves
   - Noise
   - Complex signals with harmonics

### Test Setup

- **Warm-up phase**: Before running the performance tests, a warm-up phase is executed to ensure JVM optimizations are applied.
- **Multiple iterations**: Each test runs multiple iterations to get more stable average execution times.
- **Time measurement**: System.nanoTime() is used for high-precision timing.

## Running the Tests

To run the performance tests:

```bash
./gradlew :base:test --tests "de.schliweb.bluesharpbendingapp.utils.PitchDetectionPerformanceTest"
```

## Interpreting Results

The test results are printed to the console and include:

1. **Execution time**: The average time in milliseconds for each algorithm to process audio of different durations.
2. **Comparative performance**: Direct comparison between YIN and MPM algorithms, including the ratio of their execution times.
3. **Signal type impact**: How different signal types affect the performance of each algorithm.

### Example Output

```
YIN algorithm with 0.1 seconds of audio (buffer size: 4410): 0.45 ms
YIN algorithm with 0.5 seconds of audio (buffer size: 22050): 2.34 ms
YIN algorithm with 1.0 seconds of audio (buffer size: 44100): 4.67 ms
YIN algorithm with 2.0 seconds of audio (buffer size: 88200): 9.32 ms

MPM algorithm with 0.1 seconds of audio (buffer size: 4410): 0.38 ms
MPM algorithm with 0.5 seconds of audio (buffer size: 22050): 1.98 ms
MPM algorithm with 1.0 seconds of audio (buffer size: 44100): 3.95 ms
MPM algorithm with 2.0 seconds of audio (buffer size: 88200): 7.89 ms

Duration: 0.1 seconds (buffer size: 4410)
  YIN: 0.45 ms
  MPM: 0.38 ms
  Ratio (MPM/YIN): 0.84

Duration: 0.5 seconds (buffer size: 22050)
  YIN: 2.34 ms
  MPM: 1.98 ms
  Ratio (MPM/YIN): 0.85

Duration: 1.0 seconds (buffer size: 44100)
  YIN: 4.67 ms
  MPM: 3.95 ms
  Ratio (MPM/YIN): 0.85

Duration: 2.0 seconds (buffer size: 88200)
  YIN: 9.32 ms
  MPM: 7.89 ms
  Ratio (MPM/YIN): 0.85

Signal Type: Pure Sine Wave
  YIN: 4.67 ms
  MPM: 3.95 ms

Signal Type: Square Wave
  YIN: 4.70 ms
  MPM: 3.98 ms

Signal Type: Noise
  YIN: 4.72 ms
  MPM: 4.01 ms

Signal Type: Complex Signal
  YIN: 4.69 ms
  MPM: 3.97 ms
```

## Performance Considerations

When interpreting the results, consider the following:

1. **Execution time vs. buffer size**: Both algorithms should scale linearly with buffer size. If not, there may be inefficiencies in the implementation.

2. **Algorithm comparison**: The MPM algorithm is typically faster than YIN for the same input. If YIN is significantly faster, it might indicate an implementation issue.

3. **Signal type impact**: Different signal types should have minimal impact on execution time. If certain signal types cause significant slowdowns, the algorithms may need optimization for those cases.

4. **Real-time processing**: For real-time applications, the processing time should be significantly less than the buffer duration. For example, processing 0.1 seconds of audio should take much less than 100ms to maintain real-time performance.

## Optimization Strategies

If performance issues are identified, consider the following optimization strategies:

1. **Algorithm selection**: Choose the faster algorithm for your specific use case.
2. **Buffer size adjustment**: Use smaller buffer sizes for lower latency, but be aware that very small buffers may reduce pitch detection accuracy.
3. **Code optimization**: Profile the code to identify bottlenecks and optimize critical sections.
4. **Specialized algorithms**: Implement optimized versions of algorithms for specific use cases, such as the harmonica-optimized YIN algorithm currently in development.
5. **Parallel processing**: Consider using multi-threading for processing larger buffers.

## Conclusion

Regular performance testing helps ensure that the audio processing components of the Bluesharp Bending App remain efficient and responsive. By monitoring the execution time of the pitch detection algorithms, we can identify potential performance issues early and maintain a high-quality user experience.
