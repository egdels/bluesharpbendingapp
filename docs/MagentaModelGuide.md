# Guide: Using TensorFlow Magenta for Harmonica Chord Detection

This guide explains how to create and use a TensorFlow Magenta model for harmonica chord detection with different harmonica keys.

## Table of Contents

1. [Introduction](#introduction)
2. [Prerequisites](#prerequisites)
3. [Understanding Harmonica Keys and Tunings](#understanding-harmonica-keys-and-tunings)
4. [Setting Up the Environment](#setting-up-the-environment)
5. [Collecting Training Data](#collecting-training-data)
6. [Training the Model](#training-the-model)
7. [Using the Model in the App](#using-the-model-in-the-app)
8. [Troubleshooting](#troubleshooting)
9. [Advanced Customization](#advanced-customization)

## Introduction

The BlueSharpBendingApp now includes support for TensorFlow Magenta-based chord detection. This advanced feature uses machine learning to recognize chords played on a harmonica, providing more accurate detection especially for complex chords and in noisy environments.

Unlike traditional pitch detection algorithms that analyze frequency peaks, the Magenta model is trained to recognize the unique timbral and harmonic characteristics of harmonica sounds, making it particularly effective for:

- Detecting multiple notes played simultaneously (chords)
- Recognizing bent notes and overblows
- Working with different harmonica keys
- Filtering out background noise

## Prerequisites

Before you begin, make sure you have:

- Python 3.7 or higher
- TensorFlow 2.x
- Magenta library
- Audio recording equipment
- A collection of harmonicas in different keys (or access to recordings)
- Basic understanding of machine learning concepts

## Understanding Harmonica Keys

Diatonic harmonicas come in different keys (C, A, D, G, etc.). The key determines the base scale of the harmonica. For chord detection, the model needs to understand the frequency relationships specific to each key.

Common harmonica keys include:
- C (the most common)
- A
- D
- G
- F
- Bb

## Setting Up the Environment

1. Install the required Python packages:

```bash
pip install tensorflow magenta librosa numpy soundfile
```

2. Create a project directory:

```bash
mkdir harmonica_chord_detection
cd harmonica_chord_detection
```

3. Set up the directory structure:

```bash
mkdir -p data/raw data/processed models results
```

## Collecting Training Data

For effective chord detection across different keys, you need comprehensive training data:

### Recording Guidelines

1. **Record in a quiet environment** with minimal background noise
2. Use a **high-quality microphone** positioned 6-12 inches from the harmonica
3. **Sample rate**: 44.1kHz or 48kHz
4. **Bit depth**: 16-bit or 24-bit
5. **Format**: WAV (uncompressed)

### What to Record

For each harmonica key:

1. **Single notes**: Play each hole (draw and blow) clearly for 2-3 seconds
2. **Common chords**: Record standard chord combinations (e.g., 1-2-3 blow, 4-5-6 draw)
3. **Bent notes**: Include various bend depths (quarter, half, full)
4. **Overblows and overdraws** (if applicable)
5. **Dynamic variations**: Play at different volumes (soft, medium, loud)

### Organizing the Data

Create a structured folder system:

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

Label each recording with metadata:
- Harmonica key
- Notes/chord being played
- Playing technique (normal, bend, overblow)

## Training the Model

### Data Preprocessing

1. Convert all audio files to a consistent format:

```python
import librosa
import soundfile as sf

def preprocess_audio(file_path, output_path, sr=16000):
    # Load audio file
    audio, _ = librosa.load(file_path, sr=sr, mono=True)

    # Normalize
    audio = librosa.util.normalize(audio)

    # Save processed file
    sf.write(output_path, audio, sr)
```

2. Extract features from the audio:

```python
def extract_features(file_path):
    # Load audio
    audio, sr = librosa.load(file_path, sr=16000, mono=True)

    # Extract features
    chroma = librosa.feature.chroma_cqt(y=audio, sr=sr)
    mfcc = librosa.feature.mfcc(y=audio, sr=sr, n_mfcc=13)
    spectral_contrast = librosa.feature.spectral_contrast(y=audio, sr=sr)

    # Combine features
    features = np.concatenate([
        np.mean(chroma, axis=1),
        np.mean(mfcc, axis=1),
        np.mean(spectral_contrast, axis=1)
    ])

    return features
```

3. Create training datasets with labels:

```python
def create_dataset(data_dir, label_map):
    features_list = []
    labels_list = []

    for label_name, label_id in label_map.items():
        label_dir = os.path.join(data_dir, label_name)
        for file_name in os.listdir(label_dir):
            if file_name.endswith('.wav'):
                file_path = os.path.join(label_dir, file_name)
                features = extract_features(file_path)
                features_list.append(features)
                labels_list.append(label_id)

    return np.array(features_list), np.array(labels_list)
```

### Model Architecture

For harmonica chord detection, a combination of convolutional and recurrent layers works well:

```python
def create_model(input_shape, num_classes):
    model = tf.keras.Sequential([
        tf.keras.layers.Input(shape=input_shape),
        tf.keras.layers.Reshape((-1, 1)),
        tf.keras.layers.Conv1D(64, 3, activation='relu'),
        tf.keras.layers.MaxPooling1D(2),
        tf.keras.layers.Conv1D(128, 3, activation='relu'),
        tf.keras.layers.MaxPooling1D(2),
        tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(64, return_sequences=True)),
        tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(64)),
        tf.keras.layers.Dense(128, activation='relu'),
        tf.keras.layers.Dropout(0.5),
        tf.keras.layers.Dense(num_classes, activation='softmax')
    ])
    return model
```

### Training Process

1. Split data into training and validation sets:

```python
from sklearn.model_selection import train_test_split

X_train, X_val, y_train, y_val = train_test_split(
    features, labels, test_size=0.2, random_state=42
)
```

2. Train the model:

```python
model = create_model(X_train.shape[1:], num_classes)
model.compile(
    optimizer='adam',
    loss='sparse_categorical_crossentropy',
    metrics=['accuracy']
)

history = model.fit(
    X_train, y_train,
    validation_data=(X_val, y_val),
    epochs=50,
    batch_size=32,
    callbacks=[
        tf.keras.callbacks.EarlyStopping(patience=5, restore_best_weights=True),
        tf.keras.callbacks.ModelCheckpoint('models/checkpoint.h5', save_best_only=True)
    ]
)
```

3. Evaluate the model:

```python
test_loss, test_acc = model.evaluate(X_val, y_val)
print(f'Test accuracy: {test_acc:.4f}')
```

4. Convert to TensorFlow SavedModel format:

```python
model.save('models/harmonica_chord_model')
```

5. Convert to TensorFlow Lite (optional, for mobile deployment):

```python
converter = tf.lite.TFLiteConverter.from_saved_model('models/harmonica_chord_model')
tflite_model = converter.convert()

with open('models/harmonica_chord_model.tflite', 'wb') as f:
    f.write(tflite_model)
```

## Using the Model in the App

### Current Implementation Status

**Important Note**: The current implementation of `TensorFlowMagentaChordDetector` is designed to work even without a trained model. When no model is available, it automatically falls back to a traditional chord detection algorithm. This allows the app to function while the machine learning model is being developed.

### Model Integration

1. Place your trained model file in the app's resources directory:
   - `base/src/main/resources/models/magenta_chord_detection_model.pb`

2. Enable the model in the code:
   - Open `TensorFlowMagentaChordDetector.java`
   - Uncomment the model loading code in the constructor
   - Uncomment the TensorFlow inference code in the `detectChordInternal` method

3. The app will use the model when you select "TensorFlow-Magenta" as the algorithm in the settings.

### Customizing for Different Keys

To optimize detection for a specific harmonica key:

1. In the app settings, select the "TensorFlow-Magenta" algorithm
2. The model will automatically adjust based on the detected frequency range
3. For best results, train separate models for each key

## Troubleshooting

### Common Issues

1. **Model not loading**
   - Check that the model file is correctly placed in the resources directory
   - Verify the model format is compatible (TensorFlow SavedModel or .pb format)

2. **Poor detection accuracy**
   - Ensure you've trained with sufficient data for your specific harmonica
   - Try recording in a quieter environment
   - Check microphone placement and quality

3. **High latency**
   - Reduce model complexity for faster inference
   - Consider using TensorFlow Lite for more efficient processing

## Advanced Customization

### Fine-tuning for Specific Playing Styles

If you have a unique playing style or use special techniques, you can fine-tune the model:

1. Record examples of your specific playing style
2. Use transfer learning to adapt the existing model:

```python
# Load the base model
base_model = tf.keras.models.load_model('models/harmonica_chord_model')

# Freeze most layers
for layer in base_model.layers[:-2]:
    layer.trainable = False

# Add new classification layers
x = base_model.layers[-2].output
output = tf.keras.layers.Dense(num_classes, activation='softmax')(x)
new_model = tf.keras.Model(inputs=base_model.input, outputs=output)

# Train with your custom data
new_model.compile(optimizer='adam', loss='sparse_categorical_crossentropy', metrics=['accuracy'])
new_model.fit(X_custom, y_custom, epochs=10)
```

### Creating Multi-Key Models

For players who use multiple harmonicas, you can create a unified model:

1. Combine training data from all keys
2. Add key information as an additional input feature
3. Train a model that can detect chords across different keys

```python
# Example of a multi-key model architecture
def create_multi_key_model(audio_input_shape, num_classes, num_keys):
    # Audio input branch
    audio_input = tf.keras.layers.Input(shape=audio_input_shape)
    x1 = tf.keras.layers.Conv1D(64, 3, activation='relu')(audio_input)
    x1 = tf.keras.layers.MaxPooling1D(2)(x1)
    x1 = tf.keras.layers.Flatten()(x1)

    # Key input branch
    key_input = tf.keras.layers.Input(shape=(1,))
    x2 = tf.keras.layers.Embedding(num_keys, 8)(key_input)
    x2 = tf.keras.layers.Flatten()(x2)

    # Combine branches
    combined = tf.keras.layers.Concatenate()([x1, x2])
    x = tf.keras.layers.Dense(128, activation='relu')(combined)
    output = tf.keras.layers.Dense(num_classes, activation='softmax')(x)

    return tf.keras.Model(inputs=[audio_input, key_input], outputs=output)
```

---

By following this guide, you can create and use a TensorFlow Magenta model for accurate harmonica chord detection across different keys. The machine learning approach provides superior results compared to traditional frequency analysis methods, especially for the complex sounds produced by harmonicas.
