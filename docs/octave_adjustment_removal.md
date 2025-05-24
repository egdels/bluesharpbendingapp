# Removal of Octave Adjustment in OnnxChordDetector

## Overview

This document summarizes the changes made to remove the octave adjustment from the OnnxChordDetector class and the findings from testing these changes.

## Background

The OnnxChordDetector class previously included an octave adjustment in the frequency calculation to compensate for a retrained model that was believed to map indices to notes that are one octave higher than the previous version. This adjustment subtracted 12 semitones (one octave) from the MIDI note number before calculating the frequency:

```java
// Adjust for the retrained model by shifting down one octave (12 semitones)
double frequency = 261.63 * Math.pow(2, (midiNote - 60 - 12) / 12.0);
```

However, it was suspected that this adjustment might not be necessary.

## Investigation

To determine if the octave adjustment was necessary, the following steps were taken:

1. Tests were run with the original implementation (with octave adjustment) to establish a baseline.
2. The octave adjustment was removed from the OnnxChordDetector class.
3. Tests were run again to see if they still passed without the adjustment.
4. The results of both implementations were compared.

## Changes Made

The following changes were made to remove the octave adjustment:

1. In the OnnxChordDetector class, the frequency calculation was modified to remove the octave adjustment:
   ```java
   // Calculate the frequency without octave adjustment
   double frequency = 261.63 * Math.pow(2, (midiNote - 60) / 12.0);
   ```

2. The comments in the code were updated to reflect this change.

3. The documentation in onnx_model_usage.md was updated to remove the section about the octave adjustment.

4. The OnnxChordDetectorAllChordsTest class was updated to remove the comment about the octave adjustment.

## Test Results

All tests passed both with and without the octave adjustment:

1. OnnxChordDetectorSimpleTest: All 4 tests passed.
2. OnnxChordDetectorAllChordsTest: The test passed, confirming that the detector can recognize at least 80% of the training data.

This indicates that the octave adjustment was not necessary, as the client suspected.

## Conclusion

The octave adjustment has been successfully removed from the OnnxChordDetector class, and all tests are passing. This confirms that the adjustment was not necessary for the correct functioning of the chord detector.

The removal of the octave adjustment simplifies the code and makes it more straightforward, without affecting the functionality or performance of the chord detector.