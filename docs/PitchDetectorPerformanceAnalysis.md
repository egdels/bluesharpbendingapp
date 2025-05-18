# Performance Analysis: ChordDetector vs. MPMPitchDetector

This document provides a performance analysis of the two pitch detection algorithms used in the Bluesharp Bending App: ChordDetector and MPMPitchDetector.

## Algorithm Overview

### ChordDetector

The ChordDetector class implements a spectral-based algorithm for chord detection. It is designed to detect multiple pitches (chords) in an audio signal using spectral analysis. The algorithm works by:

1. Applying a window function to the audio data
2. Computing the FFT to get the frequency spectrum
3. Finding peaks in the spectrum that correspond to pitches
4. Filtering and refining the detected pitches
5. Filtering harmonics to avoid overtones and prioritize fundamental frequencies
6. Prioritizing lower frequencies over higher harmonics

ChordDetector can detect up to 4 pitches simultaneously, making it suitable for chord detection.

### MPMPitchDetector

The MPMPitchDetector class implements the McLeod Pitch Method (MPM) for pitch detection. It is designed to detect a single fundamental frequency (pitch) in an audio signal. The algorithm works by:

1. Calculating the Normalized Square Difference Function (NSDF)
2. Finding peaks in the NSDF
3. Selecting the most significant peak
4. Applying parabolic interpolation to refine the peak position
5. Converting the peak position to a frequency

MPMPitchDetector is optimized for detecting a single pitch, making it suitable for monophonic (single-note) detection.

## Performance Characteristics

Based on the analysis of the code and the implementation details of both algorithms, the following performance characteristics can be observed:

### Computational Complexity

- **ChordDetector**: Higher computational complexity due to FFT computation, multiple peak detection, and additional filtering steps for harmonics and lower frequencies. The time complexity is dominated by the FFT operation, which is O(n log n) where n is the number of audio samples.

- **MPMPitchDetector**: Lower computational complexity for single pitch detection. The NSDF calculation has a time complexity of O(n²) where n is the number of audio samples, but it's optimized to focus only on the frequency range of interest.

### Memory Usage

- **ChordDetector**: Higher memory usage due to FFT computation and storage of the magnitude spectrum. Requires additional arrays for the FFT input and magnitude spectrum.

- **MPMPitchDetector**: Lower memory usage as it only needs to store the NSDF array for the frequency range of interest.

### Accuracy

- **ChordDetector**: Designed for multi-pitch detection, with specific filtering to handle harmonics and prioritize fundamental frequencies. It can detect up to 4 pitches simultaneously with high accuracy.

- **MPMPitchDetector**: Optimized for single-pitch detection with high accuracy for monophonic signals. It may not perform as well with polyphonic signals (multiple simultaneous pitches).

### Use Cases

- **ChordDetector**: Best suited for:
  - Detecting chords (multiple simultaneous pitches)
  - Analyzing complex harmonic content
  - Applications where detecting multiple pitches is important

- **MPMPitchDetector**: Best suited for:
  - Detecting single notes (monophonic signals)
  - Real-time applications where performance is critical
  - Applications where only the fundamental frequency is needed

## Recommendations

Based on the performance characteristics of both algorithms, the following recommendations can be made:

1. **For single-note detection**: Use MPMPitchDetector for better performance and efficiency. It is optimized for detecting a single fundamental frequency and has lower computational complexity.

2. **For chord detection**: Use ChordDetector when multiple pitches need to be detected simultaneously. It is specifically designed to handle polyphonic signals and can detect up to 4 pitches.

3. **For real-time applications**: Consider the trade-off between accuracy and performance. If only single-note detection is needed, MPMPitchDetector will provide better performance. If chord detection is required, ChordDetector is necessary despite its higher computational cost.

4. **For harmonica-specific applications**: Since harmonica playing often involves single notes with strong harmonics, MPMPitchDetector may be sufficient for most use cases. However, for detecting bent notes or chords, ChordDetector may provide more accurate results.

## Conclusion

Both ChordDetector and MPMPitchDetector have their strengths and are optimized for different use cases. The choice between them should be based on the specific requirements of the application:

- Use MPMPitchDetector when performance is critical and only single-note detection is needed.
- Use ChordDetector when multiple pitches need to be detected simultaneously, even at the cost of higher computational complexity.

For the Bluesharp Bending App, a hybrid approach could be beneficial: use MPMPitchDetector for real-time feedback during practice sessions, and use ChordDetector for more detailed analysis when accuracy is more important than real-time performance.