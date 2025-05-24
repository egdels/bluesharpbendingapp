# TensorFlow Model for Harmonica Chord Detection

## Overview

This document provides comprehensive documentation for the TensorFlow model used in the Bluesharp Bending App for harmonica chord detection. The model is designed to analyze audio input from a harmonica and identify the notes and chords being played, which is essential for the app's functionality in helping beginners learn to bend notes by visualizing the notes being played.

## Purpose of the Model

The TensorFlow model serves as the core audio analysis component of the Bluesharp Bending App. It is specifically designed to:

1. **Detect harmonica chords and notes** from audio input
2. **Identify individual notes** within chords
3. **Recognize bent notes** and their pitch
4. **Work with different harmonica keys** (C, A, D, G, F, Bb)

This enables the app to provide real-time feedback to users about the notes they are playing, helping them visualize and learn proper bending techniques.

## Model Creation Process

The model is created through a comprehensive workflow implemented in Python. The complete process involves:

### 1. Data Preparation (`data_preparation.py`)

This script handles the collection, organization, and preprocessing of audio data:

- **Input**: Raw audio recordings of harmonica notes and chords
- **Processing**:
  - Resamples audio to 16kHz
  - Trims silence from beginning and end
  - Splits audio into 1-second segments
  - Normalizes audio levels
  - Applies data augmentation (pitch shifting, time stretching, adding noise)
- **Output**: Processed audio files organized by harmonica key and split into training/validation sets

### 2. Feature Extraction (`feature_extraction.py`)

This script extracts relevant audio features from the processed audio files:

- **Input**: Processed audio files from the data preparation step
- **Features Extracted**:
  - MFCC (Mel-frequency cepstral coefficients) - captures timbral characteristics
  - Chroma features - represents the 12 different pitch classes
  - Spectral contrast - captures the difference between peaks and valleys in the spectrum
  - Mel spectrogram - frequency representation that approximates human auditory perception
- **Output**: Feature vectors and corresponding labels (notes/chords) saved as pickle files

### 3. Model Training (`model_training.py`)

This script builds and trains the TensorFlow model:

- **Input**: Feature vectors and labels from the feature extraction step
- **Model Architecture**: 
  - The default "advanced" model combines CNNs with attention mechanisms and residual connections
  - Designed specifically for multi-label classification (detecting multiple notes in a chord)
  - Uses a custom loss function that emphasizes chord notes
- **Training Process**:
  - Trains with early stopping to prevent overfitting
  - Uses class weighting to handle imbalanced data
  - Monitors training with TensorBoard
  - Saves checkpoints of the best model
- **Output**: Trained model saved in TensorFlow SavedModel format and ONNX format

### 4. Model Testing (`test_all_chords.py` and `test_a_chord.py`)

These scripts evaluate the model's performance:

- **Input**: Trained model and test audio files
- **Testing Process**:
  - Extracts features from test audio files
  - Runs model inference to get predictions
  - Compares predictions with expected notes
  - Also performs direct chroma analysis as a secondary verification
- **Output**: Test results including success rates and detailed diagnostics

## Model Architecture

The default model architecture is an advanced CNN-based model optimized for chord detection:

### Input Layer
- Accepts audio features (combination of MFCC, chroma, and spectral contrast features)

### Convolutional Blocks
- Multiple convolutional blocks with residual connections
- Each block contains:
  - Two convolutional layers with batch normalization and ReLU activation
  - Skip connections to help with gradient flow
  - Max pooling for downsampling

### Attention Mechanism
- Attention layer to focus on important features in the audio
- Helps the model identify the most relevant parts of the input for chord detection

### Dense Layers
- Multiple dense layers with dropout for regularization
- Auxiliary chord detector output to help the model focus on chord structure

### Output Layer
- 96 output nodes (12 semitones × 8 octaves)
- Sigmoid activation for multi-label classification (multiple notes can be active simultaneously)

## Using the Model with Python

To use the trained model for chord detection in Python:

```python
import tensorflow as tf
import numpy as np
import librosa

# Load the model
model_dir = "path/to/saved_model"
model = tf.saved_model.load(model_dir)
serving_fn = model.signatures["serving_default"]

# Function to extract features from audio
def extract_features(audio_file):
    # Load audio file
    y, sr = librosa.load(audio_file, sr=16000, mono=True)

    # Extract features
    mfcc = librosa.feature.mfcc(y=y, sr=sr, n_mfcc=13)
    chroma = librosa.feature.chroma_cqt(y=y, sr=sr)
    contrast = librosa.feature.spectral_contrast(y=y, sr=sr)

    # Combine features
    mfcc_mean = np.mean(mfcc, axis=1)
    chroma_mean = np.mean(chroma, axis=1)
    contrast_mean = np.mean(contrast, axis=1)

    features = np.concatenate([mfcc_mean, chroma_mean, contrast_mean])
    return features.reshape(1, -1).astype(np.float32)

# Function to predict chord from audio
def predict_chord(audio_file):
    # Extract features
    features = extract_features(audio_file)

    # Convert to tensor
    features_tensor = tf.convert_to_tensor(features)

    # Run inference
    output = serving_fn(features_tensor)
    prediction = output[list(output.keys())[0]].numpy()

    # Get the top 5 predicted notes
    top_indices = np.argsort(prediction[0])[-5:][::-1]

    # Map indices to note names
    note_names = ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B']
    top_notes = []
    for idx in top_indices:
        octave = idx // 12
        semitone = idx % 12
        top_notes.append(f"{note_names[semitone]}{octave}")

    return top_notes, prediction[0][top_indices]

# Example usage
audio_file = "path/to/audio/file.wav"
notes, confidences = predict_chord(audio_file)
print(f"Detected notes: {notes}")
print(f"Confidences: {confidences}")
```

## Using the ONNX Model

The model is also available in ONNX (Open Neural Network Exchange) format, which provides better cross-platform compatibility and performance optimization.

### What is ONNX?

ONNX is an open format to represent deep learning models. Benefits include:

- **Cross-platform compatibility**: Run the same model on different frameworks and hardware
- **Performance optimization**: ONNX Runtime provides optimized inference across different platforms
- **Wider ecosystem support**: Many tools and libraries support ONNX format

### Using the ONNX Model with Python

To use the ONNX model for chord detection:

```python
import numpy as np
import onnxruntime as ort
import librosa

# Load the ONNX model
onnx_model_path = "path/to/model.onnx"
session = ort.InferenceSession(onnx_model_path)
input_name = session.get_inputs()[0].name

# Function to extract features from audio (same as TensorFlow example)
def extract_features(audio_file):
    # Load audio file
    y, sr = librosa.load(audio_file, sr=16000, mono=True)

    # Extract features
    mfcc = librosa.feature.mfcc(y=y, sr=sr, n_mfcc=13)
    chroma = librosa.feature.chroma_cqt(y=y, sr=sr)
    contrast = librosa.feature.spectral_contrast(y=y, sr=sr)

    # Combine features
    mfcc_mean = np.mean(mfcc, axis=1)
    chroma_mean = np.mean(chroma, axis=1)
    contrast_mean = np.mean(contrast, axis=1)

    features = np.concatenate([mfcc_mean, chroma_mean, contrast_mean])
    return features.reshape(1, -1).astype(np.float32)

# Function to predict chord from audio using ONNX model
def predict_chord_onnx(audio_file):
    # Extract features
    features = extract_features(audio_file)

    # Run inference with ONNX Runtime
    prediction = session.run(None, {input_name: features})[0]

    # Get the top 5 predicted notes
    top_indices = np.argsort(prediction[0])[-5:][::-1]

    # Map indices to note names
    note_names = ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B']
    top_notes = []
    for idx in top_indices:
        octave = idx // 12
        semitone = idx % 12
        top_notes.append(f"{note_names[semitone]}{octave}")

    return top_notes, prediction[0][top_indices]

# Example usage
audio_file = "path/to/audio/file.wav"
notes, confidences = predict_chord_onnx(audio_file)
print(f"Detected notes: {notes}")
print(f"Confidences: {confidences}")
```

### Installing ONNX Runtime

To use the ONNX model, you need to install the ONNX Runtime:

```bash
pip install onnxruntime
```

For GPU acceleration:

```bash
pip install onnxruntime-gpu
```

## Training Your Own Model

To train your own model with custom data:

1. **Prepare your data**:
   ```bash
   python docs/scripts/data_preparation.py \
     --input_dir="path/to/raw/data" \
     --output_dir="path/to/processed/data" \
     --generate_metadata \
     --normalize \
     --augment \
     --organize \
     --split_ratio=0.8
   ```

2. **Extract features**:
   ```bash
   python docs/scripts/feature_extraction.py \
     --input_dir="path/to/processed/data" \
     --output_dir="path/to/features" \
     --feature_type="all" \
     --include_raw \
     --label_type="chord"
   ```

3. **Train the model**:
   ```bash
   python docs/scripts/model_training.py \
     --features_dir="path/to/features" \
     --output_dir="path/to/model" \
     --model_type=advanced \
     --epochs=300 \
     --batch_size=32 \
     --learning_rate=0.0003 \
     --early_stopping \
     --patience=30 \
     --feature_type=all \
     --class_weight \
     --tensorboard
   ```

   The training process will:
   - Train the model with the specified parameters
   - Save the model in TensorFlow SavedModel format
   - Convert the model to ONNX format (requires tf2onnx and onnx packages)
   - Save model weights in H5 format

4. **Test the model**:
   ```bash
   python docs/scripts/test_all_chords.py
   ```

Alternatively, you can use the provided `train_all_chords.sh` script to run the complete workflow:

```bash
./train_all_chords.sh
```

## Performance Considerations

- The model is optimized for real-time audio analysis on desktop platforms
- Feature extraction is the most computationally intensive part of the process
- The model has been designed to balance accuracy with performance
- For mobile platforms, consider using a quantized version of the model
- The ONNX format provides additional performance benefits:
  - Optimized inference across different platforms
  - Reduced memory footprint
  - Faster startup time
  - Better hardware acceleration support on some platforms

## Conclusion

The TensorFlow model for harmonica chord detection is a critical component of the Bluesharp Bending App. It enables the app to provide real-time feedback to users by accurately identifying the notes and chords they are playing. The model is created through a comprehensive workflow involving data preparation, feature extraction, model training, and testing, all implemented in Python. 

The model is available in both TensorFlow SavedModel format and ONNX format, providing flexibility for deployment across different platforms and frameworks. The ONNX format in particular offers performance benefits and wider compatibility, making it easier to integrate the model into various environments.
