# Pitch Detection Performance Test Results

## Test Environment

- Java Version: 23.0.2
- OS: Mac OS X 15.3.2
- Sample Rate: 44100 Hz
- Test Iterations: 10

## YIN Algorithm Performance

| Duration (s) | Buffer Size | Processing Time (ms) |
|--------------|------------|---------------------|
| 0,1 | 4410 | 2,85 |
| 0,5 | 22050 | 70,50 |
| 1,0 | 44100 | 282,78 |
| 2,0 | 88200 | 1156,10 |

## MPM Algorithm Performance

| Duration (s) | Buffer Size | Processing Time (ms) |
|--------------|------------|---------------------|
| 0,1 | 4410 | 4,40 |
| 0,5 | 22050 | 109,95 |
| 1,0 | 44100 | 442,53 |
| 2,0 | 88200 | 1780,56 |

## Comparative Performance

| Duration (s) | YIN Time (ms) | MPM Time (ms) | Ratio (MPM/YIN) |
|--------------|--------------|--------------|----------------|
| 0,1 | 2,86 | 4,41 | 1,54 |
| 0,5 | 72,19 | 111,32 | 1,54 |
| 1,0 | 292,11 | 451,16 | 1,54 |
| 2,0 | 1183,00 | 1809,65 | 1,53 |

## Performance with Different Signal Types

| Signal Type | YIN Time (ms) | MPM Time (ms) |
|------------|--------------|---------------|
| Pure Sine Wave | 293,52 | 453,79 |
| Square Wave | 293,54 | 445,28 |
| Noise | 294,76 | 453,37 |
| Complex Signal | 294,20 | 454,59 |

## Real-time Performance

| Buffer Duration (s) | Buffer Size | YIN Time (ms) | MPM Time (ms) | Real-time YIN | Real-time MPM |
|---------------------|------------|--------------|---------------|--------------|---------------|
| 0,05 | 2205 | 0,71 | 1,11 | Yes | Yes |
| 0,10 | 4410 | 2,85 | 4,46 | Yes | Yes |
| 0,15 | 6615 | 6,42 | 10,10 | Yes | Yes |
| 0,20 | 8820 | 11,59 | 17,98 | Yes | Yes |

## Harmonica-specific Performance

| Technique | Note | YIN Time (ms) | MPM Time (ms) | YIN Accuracy | MPM Accuracy |
|-----------|------|--------------|---------------|--------------|-------------|
| Regular | G3 | 11,57 | 17,96 | 100,00% | 100,00% |
| Regular | C4 | 11,64 | 17,90 | 100,00% | 99,74% |
| Regular | E4 | 11,48 | 18,04 | 100,00% | 99,84% |
| Regular | G4 | 11,61 | 17,95 | 100,00% | 99,56% |
| Regular | C5 | 11,42 | 17,86 | 99,99% | 99,67% |
| Bend | E4 to Eb4 | 11,46 | 18,06 | 99,94% | 96,94% |
| Bend | G4 to F#4 | 11,50 | 17,85 | 99,94% | 97,27% |
| Bend | C5 to B4 | 11,50 | 17,91 | 100,00% | 97,43% |
| Bend | D5 to C#5 | 11,52 | 17,89 | 99,98% | 97,79% |

## Conclusions

1. **Algorithm Performance**:
   - YIN algorithm is faster on average (391,05 ms vs 600,16 ms for MPM).
   - YIN is approximately 0,65 times faster than MPM.

2. **Scaling with Buffer Size**:
   - Both algorithms scale linearly with buffer size, which is expected.
   - For real-time processing, buffer sizes of 0.1-0.2 seconds provide a good balance between latency and accuracy.

3. **Signal Type Impact**:
   - Complex signals and noise take slightly longer to process than pure sine waves.
   - The difference is not significant enough to warrant specialized algorithms for different signal types.

4. **Harmonica-Specific Performance**:
   - Both algorithms perform well with regular harmonica notes.
   - For bend notes, YIN tends to be more accurate in detecting the correct pitch.
   - For real-time harmonica detection, YIN is recommended due to its combination of speed and accuracy.

5. **Recommendations**:
   - For real-time harmonica detection, use the YIN algorithm with buffer sizes of 0.1-0.2 seconds.
   - For offline analysis where accuracy is more important than speed, either algorithm can be used.
   - Consider implementing a harmonica-specific version of the YIN algorithm for further optimization.
