# Comparison of Pitch Detection Algorithms: YIN, MPM, FFT, and Hybrid

This document provides a comprehensive comparison of four pitch detection algorithms used in the BlueSharpBendingApp:

1. **YIN**: Time-domain algorithm based on autocorrelation with cumulative mean normalization
2. **MPM** (McLeod Pitch Method): Time-domain algorithm based on normalized square difference function (NSDF)
3. **FFT** (Fast Fourier Transform): Frequency-domain algorithm based on spectral analysis
4. **Hybrid**: Combined algorithm that uses YIN for low frequencies, MPM for mid-range, and FFT for high frequencies

## Theoretical Analysis

### YIN Algorithm
- **Based on**: Autocorrelation with cumulative mean normalization
- **Strengths**: Good accuracy for monophonic signals, robust against noise
- **Weaknesses**: Computationally intensive, may struggle with very low frequencies
- **Best use case**: Clean monophonic signals with moderate to high frequencies
- **Confidence**: Based on how close the CMNDF value is to the threshold, higher values indicate more reliable pitch detection

### MPM Algorithm (McLeod Pitch Method)
- **Based on**: Normalized square difference function (NSDF)
- **Strengths**: Better accuracy than YIN for many cases, good for harmonica frequencies
- **Weaknesses**: Still computationally intensive, may have issues with certain harmonics
- **Best use case**: Monophonic signals with strong fundamental frequencies
- **Confidence**: Directly related to the NSDF value at the detected peak, higher values indicate stronger periodicity

### FFT Algorithm
- **Based on**: Fast Fourier Transform spectral analysis
- **Strengths**: Fast computation, good for identifying multiple frequencies
- **Weaknesses**: Less accurate for low frequencies, sensitive to noise
- **Best use case**: Quick analysis, polyphonic signals, higher frequencies
- **Confidence**: Based on peak prominence in the magnitude spectrum, higher values indicate clearer spectral peaks

### Hybrid Algorithm
- **Based on**: Combines YIN (for low frequencies), MPM (for mid-range), and FFT (for high frequencies)
- **Strengths**: Leverages the best aspects of each algorithm for different frequency ranges
- **Weaknesses**: More complex implementation, potentially higher computational cost due to running multiple algorithms
- **Best use case**: Applications requiring high accuracy across a wide frequency range
- **Confidence**: Combines confidence values from the individual algorithms, potentially providing more reliable results
- **Note**: Uses parallel processing to mitigate performance impact of running multiple algorithms

## Performance Comparison

### Execution Time
- **FFT**: Fastest algorithm, typically 5-10x faster than YIN or MPM
- **MPM**: Moderate performance, typically faster than YIN but slower than FFT
- **YIN**: Slowest algorithm, especially for lower frequencies
- **Hybrid**: Performance varies depending on the frequency range, but generally faster than YIN and MPM for higher frequencies due to using FFT

### Accuracy
- **YIN**: High accuracy for most frequencies, especially good for harmonica notes
- **MPM**: Generally high accuracy, sometimes better than YIN for certain frequency ranges
- **FFT**: Less accurate for low frequencies, but good for higher frequencies
- **Hybrid**: Best overall accuracy across the entire frequency range, as it uses the most appropriate algorithm for each frequency range

### Confidence
- **YIN**: Generally provides moderate to high confidence values for clear harmonica tones
- **MPM**: Often gives the highest confidence values for strong fundamental frequencies
- **FFT**: Confidence values vary more with frequency, typically higher for mid to high frequencies
- **Hybrid**: Provides consistently high confidence values across the frequency range

## Performance with Noise

- **YIN**: Most robust against noise, maintains good accuracy and confidence even with moderate noise levels
- **MPM**: Good noise resistance, but accuracy degrades faster than YIN with increasing noise
- **FFT**: Most sensitive to noise, accuracy and confidence drop significantly with even low noise levels
- **Hybrid**: Good noise resistance overall, as it leverages the strengths of each algorithm

## Performance Across Frequency Ranges

### Low Frequencies (< 300 Hz)
- **YIN**: Best accuracy and confidence
- **MPM**: Good accuracy, but sometimes less reliable than YIN
- **FFT**: Poorest accuracy and confidence
- **Hybrid**: Uses YIN, so performance is similar to YIN

### Mid-Range Frequencies (300-1000 Hz)
- **YIN**: Good accuracy but slower
- **MPM**: Best balance of accuracy and performance
- **FFT**: Good performance but less accurate than MPM
- **Hybrid**: Uses MPM, so performance is similar to MPM

### High Frequencies (> 1000 Hz)
- **YIN**: Good accuracy but very slow
- **MPM**: Good accuracy but still computationally intensive
- **FFT**: Best balance of accuracy and performance
- **Hybrid**: Uses FFT, so performance is similar to FFT

## Recommendations for BlueSharpBendingApp

1. **For real-time pitch detection**: Use the Hybrid algorithm, as it provides the best balance of accuracy and performance across the entire frequency range of a harmonica.

2. **For offline analysis**: Use YIN for the most accurate results, especially if noise is present.

3. **For very resource-constrained environments**: Use FFT, but be aware of its limitations for low frequencies and in the presence of noise.

4. **For specific frequency ranges**:
   - Low frequencies (< 300 Hz): Use YIN
   - Mid-range frequencies (300-1000 Hz): Use MPM
   - High frequencies (> 1000 Hz): Use FFT

5. **For noisy environments**: Use YIN or the Hybrid algorithm, as they are most robust against noise.

## Conclusion

The Hybrid algorithm provides the best overall performance for the BlueSharpBendingApp, as it combines the strengths of YIN, MPM, and FFT to provide accurate pitch detection across the entire frequency range of a harmonica, while maintaining good performance and noise resistance.