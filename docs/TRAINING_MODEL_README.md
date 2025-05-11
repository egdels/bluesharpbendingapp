# Training the TensorFlow Magenta Model for Harmonica Chord Detection

This README provides instructions on how to train the TensorFlow Magenta model for harmonica chord detection used in the BlueSharpBendingApp.

## Current Implementation Status

**Important Note**: The current implementation of `TensorFlowMagentaChordDetector` is designed to work even without a trained model. When no model is available, it automatically falls back to a traditional chord detection algorithm. This allows development and testing to proceed while the machine learning model is being refined.

## Prerequisites

Before you begin, make sure you have:

- Python 3.7 or higher
- TensorFlow 2.x
- NumPy
- Librosa (for audio processing)
- A collection of harmonica recordings (for training with real data)

You can install the required Python packages with:

```bash
pip install tensorflow numpy librosa
```

## Quick Start

### Simple Model (Dummy Data)

1. Run the provided script to train a simple model with dummy data:

```bash
python docs/train_magenta_model.py
```

2. Copy the generated model file (`models/magenta_chord_detection_model.pb`) to the app's resources directory:

```
base/src/main/resources/models/magenta_chord_detection_model.pb
```

3. Uncomment the model loading code in `TensorFlowMagentaChordDetector.java`:
   - In the constructor, uncomment the section that loads the model
   - In the `detectChordInternal` method, uncomment the section that uses the model

4. Build and run the app. The TensorFlowMagentaChordDetector will now use your trained model.

### Enhanced Training Pipeline (Recommended)

For a more comprehensive training pipeline that supports real audio data, advanced model architectures, and proper evaluation, use the enhanced scripts:

1. Prepare your audio data in a directory structure (see [Recommended Data Collection Approach](#recommended-data-collection-approach))

2. Run the full training pipeline:

```bash
python docs/scripts/train_magenta_model.py --mode=full --input_dir=path/to/raw/data --output_dir=path/to/output
```

3. The trained model will be automatically copied to the app's resources directory.

4. Uncomment the model loading code in `TensorFlowMagentaChordDetector.java` as described above.

5. Build and run the app with your trained model.

For more options and advanced usage, run:

```bash
python docs/scripts/train_magenta_model.py --help
```

## Limitations of the Simple Model

The provided script creates a very basic model trained on random data. This model:

- Does not use actual harmonica recordings
- Has a simple architecture that may not capture the complexity of harmonica sounds
- Will not provide accurate chord detection
- Serves only as a placeholder to demonstrate the functionality

## Training with Real Data

For a more accurate model, you should train with real harmonica recordings. The enhanced training pipeline provides a complete solution for this:

### Using the Enhanced Training Pipeline (Recommended)

The enhanced training pipeline in the `docs/scripts` directory provides a comprehensive solution for training with real data:

1. **Data Preparation**: Preprocess and organize your audio files
   ```bash
   python docs/scripts/train_magenta_model.py --mode=prepare --input_dir=path/to/raw/data --output_dir=path/to/output --normalize --augment_data
   ```

2. **Feature Extraction**: Extract audio features (MFCCs, chroma, spectral contrast)
   ```bash
   python docs/scripts/train_magenta_model.py --mode=extract --input_dir=path/to/processed/data --output_dir=path/to/output --feature_type=all --include_delta
   ```

3. **Model Training**: Train an advanced model architecture
   ```bash
   python docs/scripts/train_magenta_model.py --mode=train --input_dir=path/to/features --output_dir=path/to/output --model_type=advanced --epochs=50 --early_stopping
   ```

4. **Model Evaluation**: Evaluate the trained model
   ```bash
   python docs/scripts/train_magenta_model.py --mode=evaluate --input_dir=path/to/model --test_data=path/to/test/data --visualize
   ```

Or run the entire pipeline in one command:
```bash
python docs/scripts/train_magenta_model.py --mode=full --input_dir=path/to/raw/data --output_dir=path/to/output --model_type=advanced --normalize --augment_data --early_stopping --visualize
```

### Manual Implementation (Advanced Users)

If you prefer to modify the simple script directly, here's how:

1. Collect recordings of harmonica notes and chords in different keys
2. Organize your data in a structured way (e.g., by key and chord type)
3. Modify the `generate_dummy_data()` function to load and preprocess your recordings
4. Consider using more advanced audio features like MFCCs, chroma features, or spectrograms
5. Experiment with different model architectures (e.g., deeper networks, recurrent layers)

Example code for loading audio files:

```python
import librosa

def load_audio_data(file_paths, labels):
    X = []
    y = []

    for file_path, label in zip(file_paths, labels):
        # Load audio file (resampling to MODEL_SAMPLE_RATE)
        audio, _ = librosa.load(file_path, sr=MODEL_SAMPLE_RATE, mono=True)

        # Ensure all samples are the same length
        if len(audio) > MODEL_SAMPLE_RATE:
            audio = audio[:MODEL_SAMPLE_RATE]
        else:
            audio = np.pad(audio, (0, MODEL_SAMPLE_RATE - len(audio)))

        X.append(audio)
        y.append(label)

    return np.array(X), np.array(y)
```

### Recommended Data Collection Approach

For best results, collect the following types of recordings:

1. **Single Notes**: Record each hole (blow and draw) on harmonicas in different keys
2. **Common Chords**: Record standard chord combinations used in blues, folk, and rock
3. **Bent Notes**: Include various bend depths for each hole where bending is possible
4. **Overblows and Overdraws**: Include these advanced techniques if they'll be used
5. **Different Playing Styles**: Record both clean and expressive playing styles
6. **Background Noise**: Include some recordings with typical background noise

Aim for at least 50-100 examples of each type of sound for a basic model, and 500+ for a more robust model.

## Enhanced Training Scripts

The enhanced training pipeline consists of several specialized scripts in the `docs/scripts` directory:

### Script Overview

1. **data_preparation.py**: Handles the collection, organization, and preprocessing of audio data
   - Resamples audio to a consistent sample rate
   - Splits audio into segments of fixed duration
   - Normalizes audio levels
   - Applies data augmentation (pitch shifting, time stretching, adding noise)
   - Organizes files by key
   - Splits data into training and validation sets

2. **feature_extraction.py**: Extracts audio features from preprocessed audio files
   - Supports multiple feature types (MFCCs, chroma, spectral contrast, mel spectrograms)
   - Calculates delta and delta-delta features
   - Handles different label types (notes, chords, key)
   - Saves features and labels in a format suitable for model training

3. **model_training.py**: Trains the TensorFlow model
   - Supports multiple model architectures (simple, CNN, RNN, advanced, multi-key)
   - Provides options for early stopping, learning rate, batch size, etc.
   - Saves the model in multiple formats (SavedModel, .pb, weights, architecture)
   - Generates training history plots and metrics

4. **model_evaluation.py**: Evaluates the trained model
   - Calculates accuracy, precision, recall, F1-score
   - Generates confusion matrix and per-class metrics
   - Supports evaluation on test data or single audio files
   - Provides visualizations of evaluation results

5. **train_magenta_model.py**: Main script that ties everything together
   - Provides a single entry point for the entire pipeline
   - Supports running individual steps or the full pipeline
   - Handles command-line arguments for all steps
   - Copies the trained model to the app's resources directory

### Directory Structure

The enhanced scripts expect and create the following directory structure:

```
input_dir/                  # Raw audio files
output_dir/
  ├── processed_data/       # Preprocessed audio files
  │   ├── train/            # Training data
  │   │   ├── key_C/        # Organized by key
  │   │   └── ...
  │   └── validation/       # Validation data
  ├── features/             # Extracted features
  │   ├── train/
  │   └── validation/
  ├── model/                # Trained model
  │   ├── models/           # Model files
  │   ├── checkpoints/      # Training checkpoints
  │   ├── plots/            # Training history plots
  │   └── ...
  └── evaluation/           # Evaluation results
      ├── plots/            # Evaluation plots
      └── ...
```

### Customizing the Pipeline

You can customize the pipeline by:

1. **Modifying the scripts**: Each script is well-documented and can be modified for specific needs
2. **Using command-line arguments**: Most parameters can be set via command-line arguments
3. **Creating a configuration file**: You can create a JSON configuration file and pass it to the scripts
4. **Extending the pipeline**: You can add new scripts or modify existing ones to add functionality

## Using Magenta's Pre-trained Models

Google's Magenta project provides pre-trained models for music-related tasks. You can explore using these models instead of training your own:

1. Visit the [Magenta GitHub repository](https://github.com/magenta/magenta)
2. Look for models related to chord recognition or pitch detection
3. Download a pre-trained model
4. Convert it to the format expected by the app (if necessary)

## Advanced Model Architecture

For better chord detection, consider a more sophisticated model architecture:

```python
def create_advanced_model():
    # Input layer
    inputs = tf.keras.Input(shape=(MODEL_SAMPLE_RATE,), name=INPUT_NODE)

    # Reshape for convolutional layers
    x = tf.keras.layers.Reshape((-1, 1))(inputs)

    # Convolutional blocks
    for filters in [32, 64, 128, 256]:
        x = tf.keras.layers.Conv1D(filters, 3, padding='same')(x)
        x = tf.keras.layers.BatchNormalization()(x)
        x = tf.keras.layers.Activation('relu')(x)
        x = tf.keras.layers.MaxPooling1D(2)(x)

    # Recurrent layers for temporal patterns
    x = tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(128, return_sequences=True))(x)
    x = tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(128))(x)

    # Dense layers
    x = tf.keras.layers.Dense(256, activation='relu')(x)
    x = tf.keras.layers.Dropout(0.5)(x)
    x = tf.keras.layers.Dense(128, activation='relu')(x)
    x = tf.keras.layers.Dropout(0.3)(x)

    # Output layer
    outputs = tf.keras.layers.Dense(12, activation='softmax', name=OUTPUT_NODE)(x)

    # Create and compile the model
    model = tf.keras.Model(inputs=inputs, outputs=outputs)
    model.compile(
        optimizer='adam',
        loss='sparse_categorical_crossentropy',
        metrics=['accuracy']
    )

    return model
```

## Integrating the Model with the App

Once you have trained a model, you need to integrate it with the app:

1. **Place the model file in the resources directory**:
   ```
   base/src/main/resources/models/magenta_chord_detection_model.pb
   ```

2. **Enable model loading in the code**:
   Open `TensorFlowMagentaChordDetector.java` and uncomment the following sections:
   - In the constructor, uncomment the model loading code (around line 100)
   - In the `detectChordInternal` method, uncomment the TensorFlow inference code (around line 230)

3. **Test the model**:
   Run the tests in `TensorFlowMagentaChordDetectorTest.java` to verify that the model works correctly.

## Troubleshooting

If you encounter issues:

1. **Model not loading in the app**: 
   - Check that the model file is correctly named and placed in the resources directory
   - Verify that the model format is compatible with the TensorFlow version used in the app
   - Check the logs for specific error messages

2. **Conversion errors**: 
   - Ensure you're using compatible TensorFlow versions for training and conversion
   - Try using TensorFlow's SavedModel format instead of .pb if you encounter compatibility issues

3. **Poor detection accuracy**: 
   - Try training with more data, especially for the specific harmonica keys you use
   - Use a more complex model architecture with convolutional and recurrent layers
   - Adjust hyperparameters like learning rate, batch size, and number of epochs
   - Implement data augmentation to increase the effective size of your training dataset

## Further Resources

- [TensorFlow Tutorials](https://www.tensorflow.org/tutorials)
- [Magenta Project](https://magenta.tensorflow.org/)
- [Audio Processing with Librosa](https://librosa.org/doc/latest/tutorial.html)
- [Deep Learning for Audio](https://www.tensorflow.org/tutorials/audio/simple_audio)
- [TensorFlow Model Optimization](https://www.tensorflow.org/model_optimization)

## Need Help?

If you need assistance with training the model or have questions about the implementation, please open an issue on the BlueSharpBendingApp repository.
