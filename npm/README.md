# Bluesharp Pitch Detection

[![npm version](https://img.shields.io/npm/v/bluesharp-pitch-detection.svg)](https://www.npmjs.com/package/bluesharp-pitch-detection)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

High-accuracy pitch detection algorithms for musical applications, with a focus on harmonica and other musical instruments.

## Features

- Multiple pitch detection algorithms:
  - **YIN**: Excellent for vocal and monophonic instrument detection
  - **MPM**: Modified Phase Matching for improved accuracy
  - **FFT**: Fast Fourier Transform-based detection
  - **Hybrid**: Combines multiple algorithms for optimal results
- Chord detection capabilities
- Note utilities for frequency-to-note conversion
- Web Audio API integration with AudioWorklet support
- Optimized for real-time applications

## Installation

```bash
npm install bluesharp-pitch-detection
```

## Basic Usage

```javascript
import { YINPitchDetector } from 'bluesharp-pitch-detection';

// Configure the detector (optional)
YINPitchDetector.setMinFrequency(80);  // Set minimum frequency in Hz
YINPitchDetector.setMaxFrequency(1000); // Set maximum frequency in Hz

// Detect pitch from audio data
function processMicrophoneData(audioData, sampleRate) {
  const result = YINPitchDetector.detectPitch(audioData, sampleRate);

  if (result.pitch !== YINPitchDetector.NO_DETECTED_PITCH) {
    console.log(`Detected pitch: ${result.pitch.toFixed(2)} Hz`);
    console.log(`Confidence: ${(result.confidence * 100).toFixed(2)}%`);
  } else {
    console.log('No pitch detected');
  }
}
```

## Module Structure

The package provides several modules that can be imported separately:

```javascript
// Import everything
import * as PitchDetection from 'bluesharp-pitch-detection';

// Import specific modules
import { YINPitchDetector, MPMPitchDetector } from 'bluesharp-pitch-detection/pitch-detectors';
import { NoteUtils, NoteLookup } from 'bluesharp-pitch-detection/utils';
import { ChordDetector, ChordDetectionResult } from 'bluesharp-pitch-detection/chord-detection';
import { PitchProcessor } from 'bluesharp-pitch-detection/audio-worklet';
```

## API Documentation

### Pitch Detectors

#### YINPitchDetector

Implementation of the YIN algorithm for pitch detection.

```javascript
import { YINPitchDetector } from 'bluesharp-pitch-detection/pitch-detectors';

// Configure frequency range
YINPitchDetector.setMinFrequency(80);  // Default: 80 Hz
YINPitchDetector.setMaxFrequency(4835); // Default: 4835 Hz

// Detect pitch
const result = YINPitchDetector.detectPitch(audioData, sampleRate);
// result = { pitch: 440.0, confidence: 0.95 }
```

#### MPMPitchDetector

Implementation of the McLeod Pitch Method (MPM) algorithm.

```javascript
import { MPMPitchDetector } from 'bluesharp-pitch-detection/pitch-detectors';

// Configure frequency range
MPMPitchDetector.setMinFrequency(80);
MPMPitchDetector.setMaxFrequency(4835);

// Detect pitch
const result = MPMPitchDetector.detectPitch(audioData, sampleRate);
```

#### FFTDetector

Fast Fourier Transform-based pitch detection.

```javascript
import { FFTDetector } from 'bluesharp-pitch-detection/pitch-detectors';

// Detect pitch
const result = FFTDetector.detectPitch(audioData, sampleRate);
```

#### HybridPitchDetector

Combines multiple algorithms for optimal results.

```javascript
import { HybridPitchDetector } from 'bluesharp-pitch-detection/pitch-detectors';

// Detect pitch
const result = HybridPitchDetector.detectPitch(audioData, sampleRate);
```

### Utilities

#### NoteUtils

Utilities for working with musical notes and frequencies.

```javascript
import { NoteUtils } from 'bluesharp-pitch-detection/utils';

// Calculate cents between two frequencies
const cents = NoteUtils.getCents(440, 466.16); // ~100 cents (a semitone)

// Add cents to a frequency
const newFreq = NoteUtils.addCentsToFrequency(50, 440); // 50 cents higher than A4

// Round a value to a specific decimal precision
const roundedValue = NoteUtils.round(123.4567); // 123.456
```

#### NoteLookup

Lookup table for note frequencies and names.

```javascript
import { NoteLookup } from 'bluesharp-pitch-detection/utils';

// Get note name from frequency
const noteName = NoteLookup.getNoteName(440); // "A4"

// Get frequency from note name
const frequency = NoteLookup.getFrequency("A4"); // 440
```

### Chord Detection

#### ChordDetector

Detects chords from frequency data.

```javascript
import { ChordDetector } from 'bluesharp-pitch-detection/chord-detection';

// Detect chord from audio data
const result = ChordDetector.detectChord(audioData, sampleRate);

// Get detected pitches
if (result.hasPitches()) {
  console.log(`Detected ${result.getPitchCount()} pitches`);
  console.log(`First pitch: ${result.getPitch(0)} Hz`);
  console.log(`Confidence: ${result.confidence}`);
}
```

#### ChordDetectionResult

Represents the result of chord detection.

```javascript
import { ChordDetectionResult } from 'bluesharp-pitch-detection/chord-detection';

// Create a chord detection result with an array of pitches and a confidence value
const pitches = [261.63, 329.63, 392.00]; // C, E, G (C major chord)
const confidence = 0.95;
const result = new ChordDetectionResult(pitches, confidence);

// Alternative: use the static factory method
const result2 = ChordDetectionResult.of(pitches, confidence);

// Convert from a single pitch detection result
const pitchResult = { pitch: 440, confidence: 0.9 };
const chordResult = ChordDetectionResult.fromPitchDetectionResult(pitchResult);
```

### Audio Worklet

#### PitchProcessor

AudioWorklet processor for real-time pitch detection.

```javascript
import { PitchProcessor } from 'bluesharp-pitch-detection/audio-worklet';

// Register the processor
const audioContext = new AudioContext();
await audioContext.audioWorklet.addModule('node_modules/bluesharp-pitch-detection/audio-worklet.js');

// Create and connect the processor
const pitchNode = new AudioWorkletNode(audioContext, 'pitch-processor');
pitchNode.port.onmessage = (event) => {
  const { pitch, confidence } = event.data;
  console.log(`Detected pitch: ${pitch} Hz with confidence ${confidence}`);
};

// Connect to audio source
microphoneSource.connect(pitchNode);
```

## License

MIT © Christian Kierdorf
