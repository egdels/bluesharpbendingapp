# Improved Model Training for Chord Detection

This document describes the improvements made to the model training process for better chord detection, with a focus on A-major chord recognition.

## Overview

The original model had difficulty recognizing A-major chords consistently. To address this issue, we've implemented several improvements to the training process:

1. **Enhanced Data Augmentation**: Created more diverse training data with a focus on A-major chords
2. **Improved Model Architecture**: Designed a specialized model architecture for chord detection
3. **Specialized Loss Function**: Developed a loss function that emphasizes chord notes, especially A-major chord notes
4. **Integrated Training Pipeline**: Combined all improvements into a single training pipeline

## Enhanced Data Augmentation

The enhanced data augmentation script (`docs/scripts/enhanced_data_augmentation.py`) applies advanced augmentation techniques to create more diverse training data:

- **Time stretching**: Changes the speed of the audio without affecting pitch
- **Pitch shifting**: Changes the pitch of the audio without affecting speed
- **Noise addition**: Adds random noise to simulate real-world conditions
- **Equalization**: Applies random EQ curves to simulate different recording conditions
- **Compression**: Applies dynamic range compression to simulate different processing
- **Synthetic chord generation**: Creates synthetic A-major chord samples with harmonics and envelope

The script also has options to focus on A-major chord files by generating more augmentations for them and to generate completely synthetic A-major chord samples.

### Usage

```bash
python docs/scripts/enhanced_data_augmentation.py \
  --input_dir=path/to/audio \
  --output_dir=path/to/augmented \
  --num_augmentations=10 \
  --focus_on_a_major \
  --generate_synthetic \
  --num_synthetic=100
```

## Improved Model Architecture

The improved model architecture (`docs/scripts/improved_chord_model.py`) is specifically designed for better chord detection:

- **Parallel convolutional branches**: Captures patterns at different scales
- **Deep residual blocks**: Improves gradient flow for better training
- **Attention mechanisms**: Focuses on harmonic relationships
- **Specialized layers**:
  - Harmonic relationship layer: Learns relationships between notes in chords
  - Chord patterns layer: Recognizes common chord patterns
  - A-major specific attention: Specifically focuses on A-major chord detection

### Model Structure

The model has two variants:
- For 1D input (raw audio): Uses 1D convolutions
- For 2D input (spectrograms): Uses 2D convolutions

Both variants include:
1. Initial convolutional layers
2. Parallel branches with different kernel sizes
3. Deep residual blocks
4. Attention mechanisms
5. Specialized layers for chord detection
6. Output layer for multi-label classification

## Specialized Loss Function

The improved loss function (`improved_chord_focused_loss`) emphasizes chord notes and specifically focuses on A-major chord notes:

- Standard binary cross-entropy loss for all notes
- Additional loss for chord notes (3x weight)
- Additional loss for A-major chord notes (5x weight)

This helps the model focus on correctly identifying chord notes, especially A-major chord notes.

## Integrated Training Pipeline

The integrated training pipeline (`improved_training_pipeline.py`) combines all these improvements into a single script:

1. Runs the enhanced data augmentation
2. Extracts features from the augmented data
3. Trains the improved chord model with the specialized loss function
4. Tests the model to verify its performance

### Usage

```bash
python improved_training_pipeline.py \
  --output_dir=./training_output \
  --training_data_dir=./training_data/data/raw \
  --epochs=500 \
  --batch_size=32 \
  --learning_rate=0.0002 \
  --test_after_training
```

### Options

- `--output_dir`: Directory to save output (default: ./training_output)
- `--features_dir`: Directory containing features (default: ./training_output/features)
- `--model_dir`: Directory to save the model (default: ./training_output/model)
- `--training_data_dir`: Directory containing raw training data (default: ./training_data/data/raw)
- `--augmented_data_dir`: Directory to save augmented data (default: ./training_output/augmented_data)
- `--epochs`: Maximum number of epochs (default: 500)
- `--batch_size`: Batch size (default: 32)
- `--learning_rate`: Learning rate (default: 0.0002)
- `--patience`: Patience for early stopping (default: 30)
- `--skip_augmentation`: Skip data augmentation
- `--skip_feature_extraction`: Skip feature extraction
- `--test_after_training`: Run tests after training

## Expected Results

The improved training process should result in a model that:

1. Better recognizes A-major chords
2. Has higher overall chord detection accuracy
3. Generalizes better to different playing styles and conditions

## Conclusion

By implementing these improvements, we've created a more robust model training process that should significantly improve chord detection, especially for A-major chords. The integrated training pipeline makes it easy to train the improved model with all the enhancements.