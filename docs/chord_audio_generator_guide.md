# Chord Audio File Generator

## Overview
The Chord Audio File Generator is a utility for creating WAV audio files for all possible chords on a harmonica. These audio files can be used for chord recognition training and meet the requirements specified in the MODEL_TRAINING_GUIDE.md document.

## Features
- Generates WAV audio files for all possible chords on a harmonica
- Supports different harmonica keys and tunings
- Creates high-quality audio files (44.1 kHz, 16-bit, mono)
- Each chord is played for 2 seconds

## Usage

### Command Line
The ChordAudioFileGenerator can be run from the command line:

```
java -cp <classpath> de.schliweb.bluesharpbendingapp.utils.ChordAudioFileGenerator <outputDirectory> [key] [tune]
```

Examples:
```
# Generate files for all required keys (C, A, D, G, F, Bb) with RICHTER tuning
java -cp bluesharpbendingapp.jar de.schliweb.bluesharpbendingapp.utils.ChordAudioFileGenerator ./training_data

# Generate files for a specific key and tuning
java -cp bluesharpbendingapp.jar de.schliweb.bluesharpbendingapp.utils.ChordAudioFileGenerator ./training_data C RICHTER
```

### Parameters
- `outputDirectory`: The directory where the audio files will be saved
- `key` (optional): The key of the harmonica (e.g., "C", "A", "G")
- `tune` (optional): The tuning of the harmonica (e.g., "RICHTER", "COUNTRY")

If key and tune are not provided, the generator will create files for all required keys (C, A, D, G, F, Bb) with RICHTER tuning.

### Supported Keys
The following harmonica keys are supported:
- A, A_FLAT, B, B_FLAT, C, D, D_FLAT, E, E_FLAT, F, F_HASH, G
- HA, HA_FLAT, HB_FLAT, HG
- LA, LA_FLAT, LB, LB_FLAT, LC, LD, LD_FLAT, LE, LE_FLAT, LF, LF_HASH, LG
- LLE, LLF, LLF_HASH

### Supported Tunings
The following harmonica tunings are supported:
- RICHTER
- COUNTRY
- DIMINISHED
- HARMONICMOLL
- MELODYMAKER
- NATURALMOLL
- PADDYRICHTER
- CIRCULAR
- AUGMENTED

## Output Files
The generated audio files are organized in a directory structure that follows the requirements in MODEL_TRAINING_GUIDE.md:

```
data/raw/
  ├── key_C/
  │   └── chords/
  │       ├── chord_C4-E4-G4_1.wav
  │       ├── chord_D4-G4_2.wav
  │       └── ...
  ├── key_A/
  │   └── chords/
  │       ├── chord_A3-C#4-E4_1.wav
  │       └── ...
  └── ...
```

The audio files are named using the following pattern:
```
chord_[ChordName]_[Number].wav
```

For example:
```
chord_C4-E4-G4_1.wav
chord_D4-G4_2.wav
...
```

The chord name is derived from the notes in the chord, joined with hyphens.

## Programmatic Usage
You can also use the ChordAudioFileGenerator programmatically in your Java code:

```
// Java code example
import de.schliweb.bluesharpbendingapp.utils.ChordAudioFileGenerator;

// Generate chord audio files for a specific key and tuning
ChordAudioFileGenerator.generateChordAudioFiles("./training_data", "C", "RICHTER");

// Generate chord audio files for all required keys (C, A, D, G, F, Bb) with RICHTER tuning
ChordAudioFileGenerator.generateAllRequiredKeys("./training_data", "RICHTER");
```

## Technical Details
- Sample rate: 44.1 kHz
- Bit depth: 16-bit
- Channels: 1 (mono)
- Duration: 2 seconds per chord
- Format: WAV (PCM)

## Use Cases
- Training chord recognition
- Creating reference audio for harmonica players
- Testing chord detection algorithms
