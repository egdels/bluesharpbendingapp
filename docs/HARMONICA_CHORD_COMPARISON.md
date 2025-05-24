# Harmonica Chord Comparison

This document provides information about the enhanced chord generation capabilities in the BlueSharpBendingApp, which are particularly useful for comparing harmonicas with different keys and tunings.

## Overview

The BlueSharpBendingApp now includes additional chord types specifically designed for comparing harmonicas with different keys and tunings. These chords highlight the differences between tuning systems and showcase the special techniques possible on a harmonica.

## New Chord Types

### Tuning Comparison Chords

These chords are designed to highlight the differences between various tuning systems:

1. **Channel 4 Chords**: Channel 4 differs between Richter and PaddyRichter/MelodyMaker tunings. The blow note is a whole tone higher in PaddyRichter and MelodyMaker (9 half-tones vs 7 half-tones in Richter).

2. **Channel 6 Chords**: Channel 6 differs between Richter and Country/MelodyMaker tunings. The draw note is a half tone higher in Country and MelodyMaker (18 half-tones vs 17 half-tones in Richter).

3. **Combined Chords**: Chords that include both channels 4 and 6, which are particularly useful for comparing tuning systems that differ in both channels (e.g., Richter vs MelodyMaker).

### Special Technique Chords

These chords showcase the special techniques possible on a harmonica:

1. **Draw Bend Chords**: Chords that include draw bends (noteIndex = 2), which are available on channels 1-6.

2. **Blow Bend Chords**: Chords that include blow bends (noteIndex = -1), which are available on channels 8-10.

3. **Overblow Chords**: Chords that include overblows (noteIndex = 2 with inverse cents handling), which produce notes not normally available.

4. **Overdraw Chords**: Chords that include overdraws (noteIndex = -1 without inverse cents handling), which also produce notes not normally available.

## Using Chords for Comparison

### Comparing Different Tunings

To compare different tunings, you can use the `HarmonicaChordHelper` class to get all possible chords for different harmonica tunings:

```java
// Create harmonicas with different tunings
Harmonica richterHarmonica = AbstractHarmonica.create(KEY.C, TUNE.RICHTER);
Harmonica paddyRichterHarmonica = AbstractHarmonica.create(KEY.C, TUNE.PADDYRICHTER);
Harmonica countryHarmonica = AbstractHarmonica.create(KEY.C, TUNE.COUNTRY);
Harmonica melodyMakerHarmonica = AbstractHarmonica.create(KEY.C, TUNE.MELODYMAKER);

// Get chords for each tuning
Map<String, List<Double>> richterChords = HarmonicaChordHelper.getPossibleChords(richterHarmonica);
Map<String, List<Double>> paddyRichterChords = HarmonicaChordHelper.getPossibleChords(paddyRichterHarmonica);
Map<String, List<Double>> countryChords = HarmonicaChordHelper.getPossibleChords(countryHarmonica);
Map<String, List<Double>> melodyMakerChords = HarmonicaChordHelper.getPossibleChords(melodyMakerHarmonica);

// Compare chords that include channel 4
// The blow note on channel 4 is different between Richter and PaddyRichter/MelodyMaker
```

### Comparing Different Keys

To compare different keys, you can use the same approach with different key values:

```java
// Create harmonicas with different keys
Harmonica cHarmonica = AbstractHarmonica.create(KEY.C, TUNE.RICHTER);
Harmonica aHarmonica = AbstractHarmonica.create(KEY.A, TUNE.RICHTER);
Harmonica gHarmonica = AbstractHarmonica.create(KEY.G, TUNE.RICHTER);

// Get chords for each key
Map<String, List<Double>> cChords = HarmonicaChordHelper.getPossibleChords(cHarmonica);
Map<String, List<Double>> aChords = HarmonicaChordHelper.getPossibleChords(aHarmonica);
Map<String, List<Double>> gChords = HarmonicaChordHelper.getPossibleChords(gHarmonica);

// Compare chords across different keys
```

### Generating Audio Files for Comparison

You can use the `ChordAudioFileGenerator` class to generate audio files for all possible chords, including the new chord types:

```java
// Generate audio files for all required keys with the Richter tuning
ChordAudioFileGenerator.generateAllRequiredKeys("./output_directory", "RICHTER");

// Generate audio files for a specific key and tuning
ChordAudioFileGenerator.generateChordAudioFiles("./output_directory", "C", "PADDYRICHTER");

// Generate audio files with variations for more robust comparison
ChordAudioFileGenerator.generateChordAudioFilesWithVariations("./output_directory", "C", "RICHTER", 3);
```

## Benefits of Additional Chords

The additional chord types provide several benefits for comparing harmonicas:

1. **Highlight Tuning Differences**: The tuning comparison chords specifically target the channels that differ between tuning systems, making it easier to hear and analyze these differences.

2. **Showcase Special Techniques**: The special technique chords demonstrate the unique capabilities of each harmonica, including bends, overblows, and overdraws, which are essential for advanced playing.

3. **Comprehensive Comparison**: By including a wide range of chord types, the comparison is more comprehensive and provides a better understanding of the differences between harmonicas.

4. **Training Data for Machine Learning**: The additional chords provide more diverse training data for machine learning models that recognize harmonica chords, improving their accuracy and robustness.

## Conclusion

The enhanced chord generation capabilities in the BlueSharpBendingApp provide powerful tools for comparing harmonicas with different keys and tunings. By generating and analyzing these chords, users can gain a deeper understanding of the differences between harmonicas and make more informed decisions about which harmonica to use for specific musical contexts.