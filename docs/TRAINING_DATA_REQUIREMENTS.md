# Training Data and Classification for the Harmonica Chord Detection Model

This document explains what training data with which classification must be provided for the TensorFlow Magenta Harmonica Chord Detection Model.

## Required Training Data

### Audio Recordings

For effective model training, audio recordings with the following properties are needed:

1. **Recording Quality**:
   - Sample rate: 44.1 kHz or 48 kHz
   - Bit depth: 16-bit or 24-bit
   - Format: WAV (uncompressed)
   - Recording environment: Quiet environment with minimal background noise
   - Microphone placement: 15-30 cm away from the harmonica

2. **Record for each harmonica key (C, A, D, G, F, Bb)**:
   - **Single notes**: Play each channel (blow and draw) clearly for 2-3 seconds
   - **Chords**: Standard chord combinations (e.g., 1-2-3 blow, 4-5-6 draw)
   - **Bent notes**: Various bending depths (quarter, half, full)
   - **Overblow and overdraw**: If applicable
   - **Dynamic variations**: Different volumes (soft, medium, loud)

3. **Minimum number of recordings**:
   - At least 50-100 examples for each note type for a basic model
   - 500+ examples for a more robust model

### Directory Structure

The training data should be organized in a structured directory hierarchy:

```
data/raw/
  ├── key_C/
  │   ├── single_notes/
  │   ├── chords/
  │   └── bends/
  ├── key_A/
  │   ├── single_notes/
  │   └── ...
  └── ...
```

### Metadata

Each recording should be provided with metadata containing the following information:
- Harmonica key (C, A, D, G, F, Bb)
- Played notes/chord
- Playing technique (normal, bent, overblow)

### Marking of Notes and Chords

The marking of which note or chord was played in an audio recording is done in several ways:

1. **Filename Convention**: 
   - For single notes: `note_[NoteName]_[PlayingTechnique].wav`
     Example: `note_C4_blow.wav` (Note C4, blown)
   - For chords: `chord_[ChordName]_[Number].wav`
     Example: `chord_Cmaj_1.wav` (C major chord, first recording)

2. **Directory Structure**:
   - The harmonica key is marked by the parent directory (e.g., `key_C/`)
   - Subdirectories can be organized by playing technique or note type (e.g., `single_notes/`, `chords/`, `bends/`)

3. **Metadata JSON File**:
   - Optionally, a JSON file with detailed metadata for each recording can be provided
   - This file is passed to the data preparation script with the parameter `--metadata_file`
   - Format: `{"filename.wav": {"key": "C", "note": "C4", "technique": "blow"}}`

During feature extraction, these markings are extracted from the filenames and directory structure and converted into labels. These labels are then converted during model training into classes that consider both the root note and octave information, where C4=48, C5=60, D4=50, etc.

### Naming Schema for Notes and Chords

Yes, note names and chord names must follow a specific naming schema to be correctly recognized by the system:

1. **Note Names**:
   - Must start with a capital letter (A, B, C, D, E, F, G)
   - Can optionally contain an accidental (# for sharp, b for flat)
   - Should include an octave number (e.g., C4 for middle C)
   - Examples: C4, D#3, Eb5, F2

2. **Chord Names**:
   - Must start with a capital letter (A, B, C, D, E, F, G)
   - Can optionally contain an accidental (# for sharp, b for flat)
   - Can either specify a chord quality (maj, min, 7, dim, etc.) or list individual notes (in the format "C4-E4-G4" or "D4-G4")
   - Examples: Cmaj, Dmin, G7, Ebdim, C4-E4-G4, D4-G4, F#3-A#3-C#4
   - For chords in the format "C4-E4-G4" or "D4-G4", the first note is used as the root note of the chord.

The system processes note names and chord names as follows:
- For note names, the root note (first letter) is extracted and converted to a semitone index (C=0, D=2, E=4, F=5, G=7, A=9, B=11)
- Accidentals are taken into account (# increases the index by 1, b decreases it by 1)
- The octave information is extracted and combined with the semitone index (Octave 4 = +48, Octave 5 = +60, etc.)
- For chords in the format "C4-E4-G4", both the root note and octave are used for classification
- For chords like "Cmaj", octave 4 is assumed by default if not otherwise specified
- Unrecognized names are classified as C4 (48) by default
- Chords in different octaves (e.g., "C4-E4-G4" and "C5-E5-G5") are recognized as different chords

## Classification

The model is trained to classify audio recordings into the following classes:

1. **Primary Classification**: 96 classes corresponding to the 12 semitones across 8 octaves (0-95):
   - C0 = 0, C1 = 12, C2 = 24, C3 = 36, C4 = 48, C5 = 60, C6 = 72, C7 = 84
   - C#0/Db0 = 1, C#1/Db1 = 13, ..., C#7/Db7 = 85
   - D0 = 2, D1 = 14, ..., D7 = 86
   - D#0/Eb0 = 3, D#1/Eb1 = 15, ..., D#7/Eb7 = 87
   - E0 = 4, E1 = 16, ..., E7 = 88
   - F0 = 5, F1 = 17, ..., F7 = 89
   - F#0/Gb0 = 6, F#1/Gb1 = 18, ..., F#7/Gb7 = 90
   - G0 = 7, G1 = 19, ..., G7 = 91
   - G#0/Ab0 = 8, G#1/Ab1 = 20, ..., G#7/Ab7 = 92
   - A0 = 9, A1 = 21, ..., A7 = 93
   - A#0/Bb0 = 10, A#1/Bb1 = 22, ..., A#7/Bb7 = 94
   - B0 = 11, B1 = 23, ..., B7 = 95

   This extended classification allows the model to distinguish between chords in different octaves (e.g., C4-E4-G4 vs. C5-E5-G5).

2. **Advanced Classification** (optional for advanced models):
   - Harmonica key (C, A, D, G, F, Bb)
   - Playing technique (normal, bent, overblow)

## Processing Pipeline

The training data goes through the following processing steps:

1. **Data Preparation** (data_preparation.py):
   - Resampling to 16 kHz
   - Splitting into 1-second segments
   - Normalization of audio volume
   - Data augmentation (pitch shifting, time stretching, adding noise)
   - Splitting into training and validation sets (by default 80% training, 20% validation)

2. **Feature Extraction** (feature_extraction.py):
   - MFCC (Mel-Frequency Cepstral Coefficients) - 13 coefficients
   - Chroma features (12 bins)
   - Spectral contrast (7 bands)
   - Mel spectrogram
   - Optional: Raw audio data and delta features

3. **Model Training** (model_training.py):
   - Supports various model architectures (simple, CNN, RNN, advanced, multi_key)
   - Trains with the extracted features
   - Considers both root note and octave information for classification
   - Uses 96 classes (12 semitones × 8 octaves) instead of the original 12 classes
   - Converts labels to one-hot encoding for training

## Summary

For successful training of the Harmonica Chord Detection Model, high-quality audio recordings of various harmonica keys are needed, covering single notes, chords, and special playing techniques. These recordings are classified into 96 classes corresponding to the 12 semitones across 8 octaves, allowing the model to distinguish between chords in different octaves. The data goes through a processing pipeline that includes data preparation, feature extraction, and model training.
