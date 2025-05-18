# Comparison of Pitch Detection Algorithms for Richter Harmonica in C

This document compares the performance and accuracy of three pitch detection algorithms (YIN, MPM, and FFT) for a Richter harmonica in C.

## Richter Harmonica in C

A Richter harmonica in C has the following characteristics:

- Key: C
- Frequency range: ~261.63 Hz (C4) to ~1046.5 Hz (C6)
- Blow notes (channel 1-10): C, E, G, C, E, G, C, E, G, C
- Draw notes (channel 1-10): D, G, B, D, F, A, B, D, F, A

## Pitch Detection Algorithms

### YIN Algorithm

The YIN algorithm is based on autocorrelation with cumulative mean normalization.

**Strengths:**
- Good accuracy for monophonic signals
- Robust against noise
- Performs well for harmonica notes in the middle to high range

**Weaknesses:**
- Computationally intensive
- May struggle with very low frequencies
- Can be slower than FFT-based methods

**Best use case:**
- Clean monophonic signals with moderate to high frequencies
- When accuracy is more important than speed
- When noise is present in the signal

### MPM Algorithm (McLeod Pitch Method)

The MPM algorithm is based on the normalized square difference function (NSDF).

**Strengths:**
- Better accuracy than YIN for many cases
- Good for harmonica frequencies
- Handles harmonics well
- More robust against certain types of noise

**Weaknesses:**
- Still computationally intensive
- May have issues with certain harmonics
- Can be slower than FFT-based methods

**Best use case:**
- Monophonic signals with strong fundamental frequencies
- When high accuracy is required
- For instruments with complex harmonic structures

### FFT Algorithm

The FFT algorithm is based on Fast Fourier Transform spectral analysis.

**Strengths:**
- Fast computation
- Good for identifying multiple frequencies
- Works well for higher frequencies
- Efficient implementation available

**Weaknesses:**
- Less accurate for low frequencies
- Sensitive to noise
- May struggle with closely spaced harmonics

**Best use case:**
- Quick analysis
- Polyphonic signals
- Higher frequencies
- Real-time applications where speed is critical

## Comparison for Richter Harmonica in C

For a Richter harmonica in C:

1. **Accuracy:**
   - YIN and MPM typically provide better accuracy for harmonica notes
   - FFT may be less accurate for the lower harmonica notes (below ~300 Hz)
   - All three algorithms perform well for the middle to high range of the harmonica

2. **Performance:**
   - FFT is generally the fastest algorithm
   - YIN and MPM are more computationally intensive
   - For real-time applications, FFT might be preferred due to its speed

3. **Noise Handling:**
   - With noise (common in real-world harmonica playing), YIN often performs best
   - MPM also handles noise well
   - FFT is more sensitive to noise and may produce less accurate results in noisy environments

4. **Specific Notes:**
   - For the lowest notes on the harmonica (C4, D4), YIN and MPM typically provide better accuracy
   - For the highest notes (G5, C6), all three algorithms perform similarly
   - For the middle range, MPM often provides the best balance of accuracy and performance

## Recommendations

Based on the analysis:

1. **For real-time applications** (e.g., tuners, live performance tools):
   - Use FFT for its speed, especially on devices with limited processing power
   - Consider using YIN or MPM if accuracy is more important than speed and sufficient processing power is available

2. **For offline analysis** (e.g., recording analysis, research):
   - Use MPM for the best overall accuracy
   - Use YIN if the signal contains significant noise
   - Use FFT if speed is critical or if analyzing polyphonic content

3. **For harmonica-specific applications:**
   - Consider a hybrid approach that uses different algorithms for different frequency ranges
   - Use MPM or YIN for the lower notes (channels 1-3)
   - Use FFT for the higher notes (channels 7-10)
   - For the middle range (channels 4-6), choose based on the specific requirements (speed vs. accuracy)

## Conclusion

All three algorithms (YIN, MPM, and FFT) can effectively detect the pitch of a Richter harmonica in C, but they have different strengths and weaknesses. The choice of algorithm depends on the specific requirements of the application, including the importance of accuracy, speed, and noise handling.

For most harmonica-specific applications, MPM provides the best overall balance of accuracy and performance, especially for the frequency range of a Richter harmonica in C. However, FFT may be preferred for real-time applications where speed is critical, and YIN may be preferred for noisy environments.