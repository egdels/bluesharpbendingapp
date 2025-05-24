# Plan for Using JLibrosa for Audio Feature Extraction

## 1. Overview

This document outlines a plan for implementing audio feature extraction using JLibrosa in the Bluesharp Bending App. JLibrosa is a Java port of the Python librosa library, which provides tools for music and audio analysis. The implementation will focus on extracting MFCC (Mel-frequency cepstral coefficients), Chroma, and SpectralContrast features from audio data, which are essential for the chord detection functionality.

## 2. JLibrosa Library

### 2.1 What is JLibrosa?

JLibrosa is a Java library that provides audio feature extraction capabilities similar to the Python librosa library. It is designed to be used in Java applications that require audio analysis, such as music information retrieval, speech recognition, and chord detection.

Key features of JLibrosa include:
- Loading and processing audio files
- Extracting various audio features (MFCC, Chroma, SpectralContrast, etc.)
- Resampling and normalization of audio data
- Compatibility with Java 8 and above

### 2.2 Feature Extraction Capabilities

JLibrosa provides the following feature extraction capabilities that are relevant to our use case:

1. **MFCC (Mel-frequency cepstral coefficients)**:
   - Captures the timbral characteristics of audio
   - Useful for identifying the instrument and playing technique
   - Standard in audio analysis and music information retrieval

2. **Chroma**:
   - Represents the 12 different pitch classes (C, C#, D, etc.)
   - Useful for identifying the harmonic content of audio
   - Essential for chord detection

3. **SpectralContrast**:
   - Captures the difference between peaks and valleys in the spectrum
   - Useful for distinguishing between different types of sounds
   - Helps in identifying the presence of multiple notes in a chord

## 3. Current Implementation Analysis

### 3.1 Current Feature Extraction Approach

The current implementation in `DesktopTensorFlowChordDetector` does not include proper feature extraction. The `preprocessAudio` method simply converts the double array to a float array without extracting any meaningful features:

```java
private float[] preprocessAudio(double[] audioData) {
    // Convert double[] to float[]
    float[] floatData = new float[audioData.length];
    for (int i = 0; i < audioData.length; i++) {
        floatData[i] = (float) audioData[i];
    }
    return floatData;
}
```

This suggests that the TensorFlow model might be expecting raw audio data, or the feature extraction is not implemented yet.

### 3.2 Integration Points

The main integration point for JLibrosa would be the `preprocessAudio` method in the `DesktopTensorFlowChordDetector` class. This method needs to be enhanced to extract MFCC, Chroma, and SpectralContrast features using JLibrosa instead of just converting the audio data to float.

Other integration points include:
- The `runModelInference` method, which might need to be updated to handle the new feature format
- The model itself, which might need to be retrained to work with the extracted features

### 3.3 Audio Processing Pipeline

The current audio processing pipeline in `DesktopTensorFlowChordDetector` consists of the following steps:
1. Check if the audio is silence
2. Resample the audio to 16kHz if needed
3. Preprocess the audio data (currently just converts to float)
4. Run model inference
5. Process the model output to get detected pitches
6. Calculate confidence

## 4. JLibrosa Integration Design

### 4.1 Architecture

The JLibrosa integration will consist of the following components:

1. **AudioFeatureExtractor**: A new class responsible for extracting audio features using JLibrosa.
2. **FeatureType**: An enum defining the types of features that can be extracted (MFCC, Chroma, SpectralContrast, All).
3. **FeatureConfiguration**: A class for configuring feature extraction parameters.
4. **FeatureExtractionResult**: A class for storing the extracted features.

### 4.2 Class Definitions

#### 4.2.1 AudioFeatureExtractor

```java
public class AudioFeatureExtractor {
    private static final int SAMPLE_RATE = 16000;
    private static final int N_MFCC = 13;
    private static final int N_CHROMA = 12;
    private static final int N_CONTRAST = 7;
    private static final int HOP_LENGTH = 512;
    
    private final JLibrosa jLibrosa;
    
    public AudioFeatureExtractor() {
        this.jLibrosa = new JLibrosa();
    }
    
    public float[] extractFeatures(double[] audioData, int sampleRate, FeatureType featureType) {
        // Resample if necessary
        if (sampleRate != SAMPLE_RATE) {
            audioData = resampleAudio(audioData, sampleRate, SAMPLE_RATE);
        }
        
        // Convert to float array
        float[] floatData = new float[audioData.length];
        for (int i = 0; i < audioData.length; i++) {
            floatData[i] = (float) audioData[i];
        }
        
        // Extract features based on feature type
        switch (featureType) {
            case MFCC:
                return extractMFCC(floatData);
            case CHROMA:
                return extractChroma(floatData);
            case SPECTRAL_CONTRAST:
                return extractSpectralContrast(floatData);
            case ALL:
                return extractAllFeatures(floatData);
            default:
                throw new IllegalArgumentException("Unsupported feature type: " + featureType);
        }
    }
    
    private float[] extractMFCC(float[] audioData) {
        float[][] mfcc = jLibrosa.generateMFCCFeatures(audioData, SAMPLE_RATE, N_MFCC, HOP_LENGTH);
        return flattenFeatures(mfcc);
    }
    
    private float[] extractChroma(float[] audioData) {
        float[][] chroma = jLibrosa.generateChromaFeatures(audioData, SAMPLE_RATE, N_CHROMA, HOP_LENGTH);
        return flattenFeatures(chroma);
    }
    
    private float[] extractSpectralContrast(float[] audioData) {
        float[][] contrast = jLibrosa.generateSpectralContrastFeatures(audioData, SAMPLE_RATE, N_CONTRAST, HOP_LENGTH);
        return flattenFeatures(contrast);
    }
    
    private float[] extractAllFeatures(float[] audioData) {
        float[] mfcc = extractMFCC(audioData);
        float[] chroma = extractChroma(audioData);
        float[] contrast = extractSpectralContrast(audioData);
        
        // Combine all features
        float[] allFeatures = new float[mfcc.length + chroma.length + contrast.length];
        System.arraycopy(mfcc, 0, allFeatures, 0, mfcc.length);
        System.arraycopy(chroma, 0, allFeatures, mfcc.length, chroma.length);
        System.arraycopy(contrast, 0, allFeatures, mfcc.length + chroma.length, contrast.length);
        
        return allFeatures;
    }
    
    private float[] flattenFeatures(float[][] features) {
        // Calculate the total size of the flattened array
        int totalSize = 0;
        for (float[] feature : features) {
            totalSize += feature.length;
        }
        
        // Create the flattened array
        float[] flattened = new float[totalSize];
        int index = 0;
        for (float[] feature : features) {
            System.arraycopy(feature, 0, flattened, index, feature.length);
            index += feature.length;
        }
        
        return flattened;
    }
    
    private double[] resampleAudio(double[] audioData, int originalSampleRate, int targetSampleRate) {
        // Use JLibrosa's resampling functionality
        return jLibrosa.resampleAudio(audioData, originalSampleRate, targetSampleRate);
    }
}
```

#### 4.2.2 FeatureType

```java
public enum FeatureType {
    MFCC,
    CHROMA,
    SPECTRAL_CONTRAST,
    ALL
}
```

#### 4.2.3 FeatureConfiguration

```java
public class FeatureConfiguration {
    private final int sampleRate;
    private final int nMfcc;
    private final int nChroma;
    private final int nContrast;
    private final int hopLength;
    
    private FeatureConfiguration(Builder builder) {
        this.sampleRate = builder.sampleRate;
        this.nMfcc = builder.nMfcc;
        this.nChroma = builder.nChroma;
        this.nContrast = builder.nContrast;
        this.hopLength = builder.hopLength;
    }
    
    public int getSampleRate() {
        return sampleRate;
    }
    
    public int getNMfcc() {
        return nMfcc;
    }
    
    public int getNChroma() {
        return nChroma;
    }
    
    public int getNContrast() {
        return nContrast;
    }
    
    public int getHopLength() {
        return hopLength;
    }
    
    public static class Builder {
        private int sampleRate = 16000;
        private int nMfcc = 13;
        private int nChroma = 12;
        private int nContrast = 7;
        private int hopLength = 512;
        
        public Builder sampleRate(int sampleRate) {
            this.sampleRate = sampleRate;
            return this;
        }
        
        public Builder nMfcc(int nMfcc) {
            this.nMfcc = nMfcc;
            return this;
        }
        
        public Builder nChroma(int nChroma) {
            this.nChroma = nChroma;
            return this;
        }
        
        public Builder nContrast(int nContrast) {
            this.nContrast = nContrast;
            return this;
        }
        
        public Builder hopLength(int hopLength) {
            this.hopLength = hopLength;
            return this;
        }
        
        public FeatureConfiguration build() {
            return new FeatureConfiguration(this);
        }
    }
}
```

#### 4.2.4 FeatureExtractionResult

```java
public class FeatureExtractionResult {
    private final float[] mfcc;
    private final float[] chroma;
    private final float[] spectralContrast;
    private final float[] combined;
    
    public FeatureExtractionResult(float[] mfcc, float[] chroma, float[] spectralContrast) {
        this.mfcc = mfcc;
        this.chroma = chroma;
        this.spectralContrast = spectralContrast;
        
        // Combine all features
        this.combined = new float[mfcc.length + chroma.length + spectralContrast.length];
        System.arraycopy(mfcc, 0, combined, 0, mfcc.length);
        System.arraycopy(chroma, 0, combined, mfcc.length, chroma.length);
        System.arraycopy(spectralContrast, 0, combined, mfcc.length + chroma.length, spectralContrast.length);
    }
    
    public float[] getMfcc() {
        return mfcc;
    }
    
    public float[] getChroma() {
        return chroma;
    }
    
    public float[] getSpectralContrast() {
        return spectralContrast;
    }
    
    public float[] getCombined() {
        return combined;
    }
}
```

### 4.3 Integration with DesktopTensorFlowChordDetector

The `DesktopTensorFlowChordDetector` class will be updated to use the `AudioFeatureExtractor` for feature extraction:

```java
public class DesktopTensorFlowChordDetector implements ChordDetectionAlgorithm {
    // ... existing code ...
    
    private final AudioFeatureExtractor featureExtractor;
    
    public DesktopTensorFlowChordDetector() {
        // ... existing initialization code ...
        
        this.featureExtractor = new AudioFeatureExtractor();
    }
    
    @Override
    public ChordDetectionResult detectChord(double[] audioData, int sampleRate) {
        // Check if the audio data is silence
        if (isSilence(audioData)) {
            return new ChordDetectionResult(List.of(), 0.0);
        }
        
        try {
            // Extract features using JLibrosa
            float[] features = featureExtractor.extractFeatures(audioData, sampleRate, FeatureType.ALL);
            
            // Run model inference with the extracted features
            float[] modelOutput = runModelInference(features);
            
            // Process the model output to get the detected pitches
            List<Double> pitches = processModelOutput(modelOutput, sampleRate);
            
            // Calculate confidence based on the model output
            double confidence = calculateConfidence(modelOutput);
            
            return new ChordDetectionResult(pitches, confidence);
        } catch (Exception e) {
            LoggingUtils.logError("Error in TensorFlow chord detection, falling back to conventional ChordDetector", e);
            ChordDetector conventionalDetector = new ChordDetector();
            return conventionalDetector.detectChordInternal(audioData, sampleRate);
        }
    }
    
    // ... existing code ...
}
```

## 5. Implementation Steps

### 5.1 Adding JLibrosa as a Dependency

Add JLibrosa to the project's dependencies in the `desktop/build.gradle` file:

```gradle
dependencies {
    // ... existing dependencies ...
    
    // JLibrosa for audio feature extraction
    implementation 'com.github.Subtitle-Synchronizer:jlibrosa:1.1.0'
}
```

### 5.2 Creating the AudioFeatureExtractor Class

Create the `AudioFeatureExtractor` class in the `de.schliweb.bluesharpbendingapp.utils` package:

```java
package de.schliweb.bluesharpbendingapp.utils;

// ... imports ...

public class AudioFeatureExtractor {
    // ... implementation as defined above ...
}
```

### 5.3 Creating the FeatureType Enum

Create the `FeatureType` enum in the `de.schliweb.bluesharpbendingapp.utils` package:

```java
package de.schliweb.bluesharpbendingapp.utils;

public enum FeatureType {
    // ... implementation as defined above ...
}
```

### 5.4 Creating the FeatureConfiguration Class

Create the `FeatureConfiguration` class in the `de.schliweb.bluesharpbendingapp.utils` package:

```java
package de.schliweb.bluesharpbendingapp.utils;

// ... imports ...

public class FeatureConfiguration {
    // ... implementation as defined above ...
}
```

### 5.5 Creating the FeatureExtractionResult Class

Create the `FeatureExtractionResult` class in the `de.schliweb.bluesharpbendingapp.utils` package:

```java
package de.schliweb.bluesharpbendingapp.utils;

public class FeatureExtractionResult {
    // ... implementation as defined above ...
}
```

### 5.6 Updating the DesktopTensorFlowChordDetector Class

Update the `DesktopTensorFlowChordDetector` class to use the `AudioFeatureExtractor` for feature extraction:

```java
package de.schliweb.bluesharpbendingapp.utils;

// ... imports ...

public class DesktopTensorFlowChordDetector implements ChordDetectionAlgorithm {
    // ... updated implementation as defined above ...
}
```

### 5.7 Testing the Implementation

Create unit tests for the `AudioFeatureExtractor` class to ensure that it correctly extracts features from audio data:

```java
package de.schliweb.bluesharpbendingapp.utils;

// ... imports ...

public class AudioFeatureExtractorTest {
    private AudioFeatureExtractor extractor;
    
    @BeforeEach
    void setUp() {
        extractor = new AudioFeatureExtractor();
    }
    
    @Test
    void testExtractMFCC() {
        // Generate a test audio signal (e.g., a sine wave)
        double[] audioData = generateTestAudio();
        
        // Extract MFCC features
        float[] features = extractor.extractFeatures(audioData, 16000, FeatureType.MFCC);
        
        // Assert that the features are not null and have the expected length
        assertNotNull(features);
        assertTrue(features.length > 0);
    }
    
    @Test
    void testExtractChroma() {
        // Generate a test audio signal (e.g., a sine wave)
        double[] audioData = generateTestAudio();
        
        // Extract Chroma features
        float[] features = extractor.extractFeatures(audioData, 16000, FeatureType.CHROMA);
        
        // Assert that the features are not null and have the expected length
        assertNotNull(features);
        assertTrue(features.length > 0);
    }
    
    @Test
    void testExtractSpectralContrast() {
        // Generate a test audio signal (e.g., a sine wave)
        double[] audioData = generateTestAudio();
        
        // Extract SpectralContrast features
        float[] features = extractor.extractFeatures(audioData, 16000, FeatureType.SPECTRAL_CONTRAST);
        
        // Assert that the features are not null and have the expected length
        assertNotNull(features);
        assertTrue(features.length > 0);
    }
    
    @Test
    void testExtractAllFeatures() {
        // Generate a test audio signal (e.g., a sine wave)
        double[] audioData = generateTestAudio();
        
        // Extract all features
        float[] features = extractor.extractFeatures(audioData, 16000, FeatureType.ALL);
        
        // Assert that the features are not null and have the expected length
        assertNotNull(features);
        assertTrue(features.length > 0);
    }
    
    private double[] generateTestAudio() {
        // Generate a 1-second sine wave at 440 Hz (A4)
        int sampleRate = 16000;
        double[] audioData = new double[sampleRate];
        double frequency = 440.0; // A4
        
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }
        
        return audioData;
    }
}
```

## 6. Performance Considerations

### 6.1 Memory Usage

- **Feature Extraction**: The feature extraction process can be memory-intensive, especially when extracting multiple features. Consider using a memory pool or object reuse to minimize garbage collection.
- **Caching**: Consider caching extracted features for frequently used audio samples to avoid redundant computation.
- **Batch Processing**: Process audio data in small batches rather than loading large audio files into memory.

### 6.2 Processing Speed

- **Parallel Processing**: Use parallel streams for computationally intensive operations like feature extraction.
- **Optimized Libraries**: JLibrosa is optimized for performance, but consider using native libraries for even better performance.
- **Feature Selection**: Extract only the features that are necessary for the specific use case to reduce computation time.

### 6.3 Platform-Specific Considerations

- **Desktop**: Desktop platforms typically have more memory and processing power, so performance is less of a concern.
- **Android**: Android devices have limited resources, so consider using a more lightweight feature extraction approach or pre-computing features when possible.
- **Web**: For the web application, consider extracting features on the server side to reduce client-side computation.

## 7. Example Usage

### 7.1 Basic Usage

```java
// Create an AudioFeatureExtractor
AudioFeatureExtractor extractor = new AudioFeatureExtractor();

// Extract features from audio data
double[] audioData = ...; // Get audio data from somewhere
int sampleRate = 16000;
float[] features = extractor.extractFeatures(audioData, sampleRate, FeatureType.ALL);

// Use the features for chord detection
// ...
```

### 7.2 Integration with DesktopTensorFlowChordDetector

```java
public class DesktopTensorFlowChordDetector implements ChordDetectionAlgorithm {
    private final AudioFeatureExtractor featureExtractor;
    
    public DesktopTensorFlowChordDetector() {
        this.featureExtractor = new AudioFeatureExtractor();
        // ... other initialization ...
    }
    
    @Override
    public ChordDetectionResult detectChord(double[] audioData, int sampleRate) {
        // Extract features
        float[] features = featureExtractor.extractFeatures(audioData, sampleRate, FeatureType.ALL);
        
        // Run model inference
        float[] modelOutput = runModelInference(features);
        
        // Process model output
        List<Double> pitches = processModelOutput(modelOutput, sampleRate);
        double confidence = calculateConfidence(modelOutput);
        
        return new ChordDetectionResult(pitches, confidence);
    }
    
    // ... other methods ...
}
```

### 7.3 Custom Feature Configuration

```java
// Create a custom feature configuration
FeatureConfiguration config = new FeatureConfiguration.Builder()
    .sampleRate(22050)
    .nMfcc(20)
    .nChroma(24)
    .nContrast(10)
    .hopLength(1024)
    .build();

// Create an AudioFeatureExtractor with the custom configuration
AudioFeatureExtractor extractor = new AudioFeatureExtractor(config);

// Extract features
float[] features = extractor.extractFeatures(audioData, sampleRate, FeatureType.ALL);
```

## 8. Conclusion

This plan outlines a comprehensive approach to implementing audio feature extraction using JLibrosa in the Bluesharp Bending App. By extracting MFCC, Chroma, and SpectralContrast features from audio data, the chord detection functionality can be significantly improved.

The implementation consists of several new classes (`AudioFeatureExtractor`, `FeatureType`, `FeatureConfiguration`, `FeatureExtractionResult`) and updates to the existing `DesktopTensorFlowChordDetector` class. The plan also includes performance considerations and example usage to guide the implementation.

By following this plan, the Bluesharp Bending App will be able to leverage the power of JLibrosa for audio feature extraction, leading to more accurate chord detection and a better user experience.