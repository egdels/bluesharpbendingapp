#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Model training script for the TensorFlow Magenta harmonica chord detection model.

This script trains a TensorFlow model for harmonica chord detection using
the features extracted from audio files.

Usage:
    python model_training.py --features_dir=path/to/features --output_dir=path/to/model

Author: BlueSharpBendingApp Team
"""

import os
import argparse
import numpy as np
import tensorflow as tf
import json
import pickle
from tqdm import tqdm
import matplotlib.pyplot as plt
from datetime import datetime

# Constants
MODEL_NAME = "magenta_chord_detection_model"
INPUT_NODE = "input_audio"
OUTPUT_NODE = "chord_predictions"
NUM_CLASSES = 96  # 12 semitones in an octave * 8 octaves (0-7)
OCTAVE_RANGE = 8  # Support octaves 0-7


def parse_arguments():
    """Parse command line arguments."""
    parser = argparse.ArgumentParser(description='Train a TensorFlow model for harmonica chord detection')
    parser.add_argument('--features_dir', type=str, required=True, help='Directory containing extracted features')
    parser.add_argument('--output_dir', type=str, required=True, help='Directory to save the trained model')
    parser.add_argument('--model_type', type=str, default='advanced', 
                        choices=['simple', 'cnn', 'rnn', 'advanced', 'multi_key'],
                        help='Type of model architecture to use (default: advanced)')
    parser.add_argument('--epochs', type=int, default=50, help='Number of training epochs (default: 50)')
    parser.add_argument('--batch_size', type=int, default=32, help='Batch size for training (default: 32)')
    parser.add_argument('--learning_rate', type=float, default=0.001, help='Learning rate (default: 0.001)')
    parser.add_argument('--early_stopping', action='store_true', help='Use early stopping')
    parser.add_argument('--patience', type=int, default=5, help='Patience for early stopping (default: 5)')
    parser.add_argument('--feature_type', type=str, default='all', 
                        choices=['mfcc', 'chroma', 'contrast', 'mel', 'raw', 'all'],
                        help='Type of features to use for training (default: all)')
    parser.add_argument('--augment', action='store_true', help='Use data augmentation during training')
    parser.add_argument('--tensorboard', action='store_true', help='Enable TensorBoard logging')
    return parser.parse_args()


def load_data(features_dir, feature_type='all'):
    """
    Load features and labels from the specified directory.

    Args:
        features_dir: Directory containing the features and labels
        feature_type: Type of features to use for training

    Returns:
        X_train, y_train, X_val, y_val: Training and validation data and labels
    """
    # Check if the features directory has train and validation subdirectories
    train_dir = os.path.join(features_dir, 'train')
    val_dir = os.path.join(features_dir, 'validation')

    if os.path.exists(train_dir) and os.path.exists(val_dir):
        # Load training data
        with open(os.path.join(train_dir, 'features.pkl'), 'rb') as f:
            train_features = pickle.load(f)
        with open(os.path.join(train_dir, 'labels.pkl'), 'rb') as f:
            train_labels = pickle.load(f)

        # Load validation data
        with open(os.path.join(val_dir, 'features.pkl'), 'rb') as f:
            val_features = pickle.load(f)
        with open(os.path.join(val_dir, 'labels.pkl'), 'rb') as f:
            val_labels = pickle.load(f)
    else:
        # Load all data and split into train and validation sets
        with open(os.path.join(features_dir, 'features.pkl'), 'rb') as f:
            all_features = pickle.load(f)
        with open(os.path.join(features_dir, 'labels.pkl'), 'rb') as f:
            all_labels = pickle.load(f)

        # Split data (80% train, 20% validation)
        split_idx = int(len(all_features) * 0.8)
        train_features = all_features[:split_idx]
        train_labels = all_labels[:split_idx]
        val_features = all_features[split_idx:]
        val_labels = all_labels[split_idx:]

    print(f"Loaded {len(train_features)} training samples and {len(val_features)} validation samples.")

    # Process features based on feature_type
    X_train = process_features(train_features, feature_type)
    X_val = process_features(val_features, feature_type)

    # Process labels
    y_train = process_labels(train_labels)
    y_val = process_labels(val_labels)

    return X_train, y_train, X_val, y_val


def process_features(features_list, feature_type):
    """
    Process features based on the specified feature type.

    Args:
        features_list: List of feature dictionaries
        feature_type: Type of features to use

    Returns:
        Processed features as a numpy array
    """
    if feature_type == 'raw':
        # Use raw audio data
        return np.array([f['raw'] for f in features_list if 'raw' in f])

    elif feature_type == 'mfcc':
        # Use MFCC features
        return np.array([f['mfcc'] for f in features_list if 'mfcc' in f])

    elif feature_type == 'chroma':
        # Use chroma features
        return np.array([f['chroma'] for f in features_list if 'chroma' in f])

    elif feature_type == 'contrast':
        # Use spectral contrast features
        return np.array([f['contrast'] for f in features_list if 'contrast' in f])

    elif feature_type == 'mel':
        # Use mel spectrogram features
        return np.array([f['mel'] for f in features_list if 'mel' in f])

    elif feature_type == 'all':
        # Combine all available features
        combined_features = []

        for f in features_list:
            feature_vector = []

            # Add MFCC features if available
            if 'mfcc' in f:
                feature_vector.append(np.mean(f['mfcc'], axis=1))

            # Add chroma features if available
            if 'chroma' in f:
                feature_vector.append(np.mean(f['chroma'], axis=1))

            # Add spectral contrast features if available
            if 'contrast' in f:
                feature_vector.append(np.mean(f['contrast'], axis=1))

            # Combine all features
            if feature_vector:
                combined_features.append(np.concatenate(feature_vector))

        return np.array(combined_features)

    else:
        raise ValueError(f"Unsupported feature type: {feature_type}")


def process_labels(labels_list):
    """
    Process labels into a format suitable for training.

    Args:
        labels_list: List of labels

    Returns:
        Processed labels as a numpy array
    """
    # Convert labels to one-hot encoding
    processed_labels = []

    for label in labels_list:
        if isinstance(label, dict):
            # Handle key_tuning labels
            # For now, we'll just use the key as the label
            if 'key' in label and label['key'] != 'unknown':
                # Map key to semitone index (C=0, C#=1, D=2, etc.)
                key_map = {'C': 0, 'C#': 1, 'Db': 1, 'D': 2, 'D#': 3, 'Eb': 3,
                          'E': 4, 'F': 5, 'F#': 6, 'Gb': 6, 'G': 7, 'G#': 8,
                          'Ab': 8, 'A': 9, 'A#': 10, 'Bb': 10, 'B': 11}

                if label['key'] in key_map:
                    # For key_tuning labels, we still use the original 12-class system
                    # as octave information is not relevant for keys
                    processed_labels.append(key_map[label['key']])
                else:
                    # Default to C (0) if key is not recognized
                    processed_labels.append(0)
            else:
                # Default to C (0) if key is unknown
                processed_labels.append(0)

        elif isinstance(label, str):
            # Check if the label is in the format "C4-E4-G4" (notes separated by hyphens)
            if '-' in label and all(part[0] in 'ABCDEFG' for part in label.split('-')):
                # Handle chord labels in the format "C4-E4-G4"
                # Extract the root note and octave (first note in the list)
                first_note = label.split('-')[0]
                root_note = first_note[0]

                # Extract octave number
                octave = 4  # Default to octave 4 if not specified
                for i in range(1, len(first_note)):
                    if first_note[i].isdigit():
                        octave = int(first_note[i])
                        break

                # Ensure octave is within range
                octave = max(0, min(octave, OCTAVE_RANGE - 1))

                # Map root note to semitone index
                note_map = {'C': 0, 'D': 2, 'E': 4, 'F': 5, 'G': 7, 'A': 9, 'B': 11}

                if root_note in note_map:
                    # Check for sharp or flat in the root note
                    has_sharp_or_flat = False
                    for i in range(1, len(first_note)):
                        if first_note[i] in ['#', 'b']:
                            has_sharp_or_flat = True
                            if first_note[i] == '#':
                                semitone = (note_map[root_note] + 1) % 12
                            else:  # flat
                                semitone = (note_map[root_note] - 1) % 12
                            break

                    if not has_sharp_or_flat:
                        semitone = note_map[root_note]

                    # Calculate class index based on semitone and octave
                    class_index = semitone + (octave * 12)
                    processed_labels.append(class_index)
                else:
                    # Default to C4 (48) if root note is not recognized
                    processed_labels.append(48)
            elif label.startswith(('C', 'D', 'E', 'F', 'G', 'A', 'B')):
                # Handle note labels (e.g., 'C4', 'A3', etc.) or traditional chord labels (e.g., 'Cmaj')
                # Extract the note name (first character)
                note_name = label[0]

                # Extract octave number if present
                octave = 4  # Default to octave 4 if not specified
                for i in range(1, len(label)):
                    if label[i].isdigit():
                        octave = int(label[i])
                        break

                # Ensure octave is within range
                octave = max(0, min(octave, OCTAVE_RANGE - 1))

                # Map note name to semitone index
                note_map = {'C': 0, 'D': 2, 'E': 4, 'F': 5, 'G': 7, 'A': 9, 'B': 11}

                if note_name in note_map:
                    # Check for sharp or flat
                    has_sharp_or_flat = False
                    for i in range(1, len(label)):
                        if label[i] in ['#', 'b']:
                            has_sharp_or_flat = True
                            if label[i] == '#':
                                semitone = (note_map[note_name] + 1) % 12
                            else:  # flat
                                semitone = (note_map[note_name] - 1) % 12
                            break

                    if not has_sharp_or_flat:
                        semitone = note_map[note_name]

                    # Calculate class index based on semitone and octave
                    class_index = semitone + (octave * 12)
                    processed_labels.append(class_index)
                else:
                    # Default to C4 (48) if note is not recognized
                    processed_labels.append(48)
            else:
                # Default to C4 (48) if label is not a note or chord
                processed_labels.append(48)

        else:
            # Default to C4 (48) for any other label type
            processed_labels.append(48)

    # Convert to numpy array
    processed_labels = np.array(processed_labels)

    # Convert to one-hot encoding
    one_hot_labels = tf.keras.utils.to_categorical(processed_labels, num_classes=NUM_CLASSES)

    return one_hot_labels


def create_simple_model(input_shape):
    """
    Create a simple model for chord detection.

    Args:
        input_shape: Shape of the input data

    Returns:
        A compiled TensorFlow model
    """
    model = tf.keras.Sequential([
        tf.keras.layers.Input(shape=input_shape, name=INPUT_NODE),
        tf.keras.layers.Dense(128, activation='relu'),
        tf.keras.layers.Dropout(0.3),
        tf.keras.layers.Dense(64, activation='relu'),
        tf.keras.layers.Dropout(0.3),
        tf.keras.layers.Dense(NUM_CLASSES, activation='softmax', name=OUTPUT_NODE)
    ])

    return model


def create_cnn_model(input_shape):
    """
    Create a CNN model for chord detection.

    Args:
        input_shape: Shape of the input data

    Returns:
        A compiled TensorFlow model
    """
    # Reshape input for CNN if needed
    if len(input_shape) == 1:
        # For 1D input (e.g., raw audio), reshape to 2D
        model = tf.keras.Sequential([
            tf.keras.layers.Input(shape=input_shape, name=INPUT_NODE),
            tf.keras.layers.Reshape((-1, 1)),
            tf.keras.layers.Conv1D(32, 3, activation='relu', padding='same'),
            tf.keras.layers.MaxPooling1D(2),
            tf.keras.layers.Conv1D(64, 3, activation='relu', padding='same'),
            tf.keras.layers.MaxPooling1D(2),
            tf.keras.layers.Conv1D(128, 3, activation='relu', padding='same'),
            tf.keras.layers.MaxPooling1D(2),
            tf.keras.layers.Flatten(),
            tf.keras.layers.Dense(128, activation='relu'),
            tf.keras.layers.Dropout(0.5),
            tf.keras.layers.Dense(NUM_CLASSES, activation='softmax', name=OUTPUT_NODE)
        ])
    else:
        # For 2D input (e.g., spectrograms), use 2D convolutions
        model = tf.keras.Sequential([
            tf.keras.layers.Input(shape=input_shape, name=INPUT_NODE),
            tf.keras.layers.Conv2D(32, (3, 3), activation='relu', padding='same'),
            tf.keras.layers.MaxPooling2D((2, 2)),
            tf.keras.layers.Conv2D(64, (3, 3), activation='relu', padding='same'),
            tf.keras.layers.MaxPooling2D((2, 2)),
            tf.keras.layers.Conv2D(128, (3, 3), activation='relu', padding='same'),
            tf.keras.layers.MaxPooling2D((2, 2)),
            tf.keras.layers.Flatten(),
            tf.keras.layers.Dense(128, activation='relu'),
            tf.keras.layers.Dropout(0.5),
            tf.keras.layers.Dense(NUM_CLASSES, activation='softmax', name=OUTPUT_NODE)
        ])

    return model


def create_rnn_model(input_shape):
    """
    Create an RNN model for chord detection.

    Args:
        input_shape: Shape of the input data

    Returns:
        A compiled TensorFlow model
    """
    # Reshape input for RNN if needed
    if len(input_shape) == 1:
        # For 1D input (e.g., raw audio), reshape to 2D
        model = tf.keras.Sequential([
            tf.keras.layers.Input(shape=input_shape, name=INPUT_NODE),
            tf.keras.layers.Reshape((-1, 1)),
            tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(64, return_sequences=True)),
            tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(64)),
            tf.keras.layers.Dense(128, activation='relu'),
            tf.keras.layers.Dropout(0.5),
            tf.keras.layers.Dense(NUM_CLASSES, activation='softmax', name=OUTPUT_NODE)
        ])
    else:
        # For 2D input (e.g., spectrograms), flatten the frequency dimension
        model = tf.keras.Sequential([
            tf.keras.layers.Input(shape=input_shape, name=INPUT_NODE),
            tf.keras.layers.Reshape((input_shape[1], -1)),
            tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(64, return_sequences=True)),
            tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(64)),
            tf.keras.layers.Dense(128, activation='relu'),
            tf.keras.layers.Dropout(0.5),
            tf.keras.layers.Dense(NUM_CLASSES, activation='softmax', name=OUTPUT_NODE)
        ])

    return model


def create_advanced_model(input_shape):
    """
    Create an advanced model for chord detection combining CNN and RNN.

    Args:
        input_shape: Shape of the input data

    Returns:
        A compiled TensorFlow model
    """
    # Reshape input if needed
    if len(input_shape) == 1:
        # For 1D input (e.g., raw audio), reshape to 2D
        inputs = tf.keras.layers.Input(shape=input_shape, name=INPUT_NODE)
        x = tf.keras.layers.Reshape((-1, 1))(inputs)

        # Convolutional blocks
        for filters in [32, 64, 128, 256]:
            x = tf.keras.layers.Conv1D(filters, 3, padding='same')(x)
            x = tf.keras.layers.BatchNormalization()(x)
            x = tf.keras.layers.Activation('relu')(x)
            x = tf.keras.layers.MaxPooling1D(2)(x)

        # Recurrent layers
        x = tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(128, return_sequences=True))(x)
        x = tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(128))(x)

        # Dense layers
        x = tf.keras.layers.Dense(256, activation='relu')(x)
        x = tf.keras.layers.Dropout(0.5)(x)
        x = tf.keras.layers.Dense(128, activation='relu')(x)
        x = tf.keras.layers.Dropout(0.3)(x)

        # Output layer
        outputs = tf.keras.layers.Dense(NUM_CLASSES, activation='softmax', name=OUTPUT_NODE)(x)

        model = tf.keras.Model(inputs=inputs, outputs=outputs)
    else:
        # For 2D input (e.g., spectrograms), use 2D convolutions
        inputs = tf.keras.layers.Input(shape=input_shape, name=INPUT_NODE)

        # Convolutional blocks
        x = inputs
        for filters in [32, 64, 128, 256]:
            x = tf.keras.layers.Conv2D(filters, (3, 3), padding='same')(x)
            x = tf.keras.layers.BatchNormalization()(x)
            x = tf.keras.layers.Activation('relu')(x)
            x = tf.keras.layers.MaxPooling2D((2, 2))(x)

        # Reshape for recurrent layers
        x = tf.keras.layers.Reshape((-1, x.shape[-1] * x.shape[-2]))(x)

        # Recurrent layers
        x = tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(128, return_sequences=True))(x)
        x = tf.keras.layers.Bidirectional(tf.keras.layers.LSTM(128))(x)

        # Dense layers
        x = tf.keras.layers.Dense(256, activation='relu')(x)
        x = tf.keras.layers.Dropout(0.5)(x)
        x = tf.keras.layers.Dense(128, activation='relu')(x)
        x = tf.keras.layers.Dropout(0.3)(x)

        # Output layer
        outputs = tf.keras.layers.Dense(NUM_CLASSES, activation='softmax', name=OUTPUT_NODE)(x)

        model = tf.keras.Model(inputs=inputs, outputs=outputs)

    return model


def create_multi_key_model(input_shape, num_keys=12):
    """
    Create a model that can handle multiple harmonica keys.

    Args:
        input_shape: Shape of the input data
        num_keys: Number of different harmonica keys

    Returns:
        A compiled TensorFlow model
    """
    # Audio input branch
    audio_input = tf.keras.layers.Input(shape=input_shape, name=INPUT_NODE)

    # Reshape input if needed
    if len(input_shape) == 1:
        x1 = tf.keras.layers.Reshape((-1, 1))(audio_input)
    else:
        x1 = audio_input

    # Convolutional layers for audio
    x1 = tf.keras.layers.Conv1D(64, 3, activation='relu')(x1)
    x1 = tf.keras.layers.MaxPooling1D(2)(x1)
    x1 = tf.keras.layers.Conv1D(128, 3, activation='relu')(x1)
    x1 = tf.keras.layers.MaxPooling1D(2)(x1)
    x1 = tf.keras.layers.Flatten()(x1)

    # Key input branch
    key_input = tf.keras.layers.Input(shape=(1,), name='key_input')
    x2 = tf.keras.layers.Embedding(num_keys, 8)(key_input)
    x2 = tf.keras.layers.Flatten()(x2)

    # Combine branches
    combined = tf.keras.layers.Concatenate()([x1, x2])
    x = tf.keras.layers.Dense(128, activation='relu')(combined)
    x = tf.keras.layers.Dropout(0.5)(x)
    outputs = tf.keras.layers.Dense(NUM_CLASSES, activation='softmax', name=OUTPUT_NODE)(x)

    model = tf.keras.Model(inputs=[audio_input, key_input], outputs=outputs)

    return model


def train_model(model, X_train, y_train, X_val, y_val, args):
    """
    Train the model with the specified parameters.

    Args:
        model: The TensorFlow model to train
        X_train: Training data
        y_train: Training labels
        X_val: Validation data
        y_val: Validation labels
        args: Command-line arguments

    Returns:
        Training history
    """
    # Compile the model
    model.compile(
        optimizer=tf.keras.optimizers.Adam(learning_rate=args.learning_rate),
        loss='categorical_crossentropy',
        metrics=['accuracy']
    )

    # Print model summary
    model.summary()

    # Set up callbacks
    callbacks = []

    # Model checkpoint callback
    checkpoint_path = os.path.join(args.output_dir, 'checkpoints', 'model_checkpoint.h5')
    os.makedirs(os.path.dirname(checkpoint_path), exist_ok=True)
    checkpoint_callback = tf.keras.callbacks.ModelCheckpoint(
        checkpoint_path,
        save_best_only=True,
        monitor='val_accuracy',
        mode='max'
    )
    callbacks.append(checkpoint_callback)

    # Early stopping callback
    if args.early_stopping:
        early_stopping_callback = tf.keras.callbacks.EarlyStopping(
            monitor='val_accuracy',
            patience=args.patience,
            restore_best_weights=True
        )
        callbacks.append(early_stopping_callback)

    # TensorBoard callback
    if args.tensorboard:
        log_dir = os.path.join(args.output_dir, 'logs', datetime.now().strftime("%Y%m%d-%H%M%S"))
        tensorboard_callback = tf.keras.callbacks.TensorBoard(
            log_dir=log_dir,
            histogram_freq=1
        )
        callbacks.append(tensorboard_callback)

    # Train the model
    print(f"Training model for {args.epochs} epochs with batch size {args.batch_size}...")
    history = model.fit(
        X_train, y_train,
        epochs=args.epochs,
        batch_size=args.batch_size,
        validation_data=(X_val, y_val),
        callbacks=callbacks,
        verbose=1
    )

    return history


def plot_training_history(history, output_dir):
    """
    Plot the training history and save the plots.

    Args:
        history: Training history
        output_dir: Directory to save the plots
    """
    # Create plots directory
    plots_dir = os.path.join(output_dir, 'plots')
    os.makedirs(plots_dir, exist_ok=True)

    # Plot accuracy
    plt.figure(figsize=(10, 4))
    plt.subplot(1, 2, 1)
    plt.plot(history.history['accuracy'], label='Training')
    plt.plot(history.history['val_accuracy'], label='Validation')
    plt.title('Model Accuracy')
    plt.xlabel('Epoch')
    plt.ylabel('Accuracy')
    plt.legend()

    # Plot loss
    plt.subplot(1, 2, 2)
    plt.plot(history.history['loss'], label='Training')
    plt.plot(history.history['val_loss'], label='Validation')
    plt.title('Model Loss')
    plt.xlabel('Epoch')
    plt.ylabel('Loss')
    plt.legend()

    plt.tight_layout()
    plt.savefig(os.path.join(plots_dir, 'training_history.png'))
    plt.close()

    print(f"Training history plots saved to {plots_dir}")


def save_model(model, output_dir):
    """
    Save the trained model in various formats.

    Args:
        model: The trained TensorFlow model
        output_dir: Directory to save the model
    """
    # Create models directory
    models_dir = os.path.join(output_dir, 'models')
    os.makedirs(models_dir, exist_ok=True)

    # Save in SavedModel format
    saved_model_path = os.path.join(models_dir, MODEL_NAME)
    model.save(saved_model_path)
    print(f"Model saved to {saved_model_path}")

    # Save in .pb format
    pb_path = os.path.join(models_dir, f"{MODEL_NAME}.pb")

    # Convert to TensorFlow Lite format
    converter = tf.lite.TFLiteConverter.from_saved_model(saved_model_path)
    tflite_model = converter.convert()

    # Save the .pb file
    with open(pb_path, 'wb') as f:
        f.write(tflite_model)

    print(f"Model also saved as .pb file: {pb_path}")

    # Save model architecture as JSON
    model_json = model.to_json()
    with open(os.path.join(models_dir, f"{MODEL_NAME}_architecture.json"), 'w') as f:
        f.write(model_json)

    # Save model weights
    model.save_weights(os.path.join(models_dir, f"{MODEL_NAME}_weights.h5"))

    print(f"Model architecture and weights saved to {models_dir}")


def main():
    """Main function."""
    args = parse_arguments()

    # Check if features directory exists
    if not os.path.exists(args.features_dir):
        print(f"Error: Features directory {args.features_dir} does not exist.")
        return

    # Create output directory if it doesn't exist
    os.makedirs(args.output_dir, exist_ok=True)

    # Load data
    X_train, y_train, X_val, y_val = load_data(args.features_dir, args.feature_type)

    # Create model based on model_type
    input_shape = X_train.shape[1:]
    print(f"Input shape: {input_shape}")

    if args.model_type == 'simple':
        model = create_simple_model(input_shape)
    elif args.model_type == 'cnn':
        model = create_cnn_model(input_shape)
    elif args.model_type == 'rnn':
        model = create_rnn_model(input_shape)
    elif args.model_type == 'advanced':
        model = create_advanced_model(input_shape)
    elif args.model_type == 'multi_key':
        model = create_multi_key_model(input_shape)
    else:
        print(f"Error: Unsupported model type {args.model_type}.")
        return

    # Train the model
    history = train_model(model, X_train, y_train, X_val, y_val, args)

    # Plot training history
    plot_training_history(history, args.output_dir)

    # Save the model
    save_model(model, args.output_dir)

    # Save training parameters
    params = vars(args)
    params['input_shape'] = input_shape
    params['num_train_samples'] = len(X_train)
    params['num_val_samples'] = len(X_val)
    params['final_train_accuracy'] = float(history.history['accuracy'][-1])
    params['final_val_accuracy'] = float(history.history['val_accuracy'][-1])
    params['final_train_loss'] = float(history.history['loss'][-1])
    params['final_val_loss'] = float(history.history['val_loss'][-1])
    params['best_val_accuracy'] = float(max(history.history['val_accuracy']))
    params['best_epoch'] = history.history['val_accuracy'].index(max(history.history['val_accuracy'])) + 1

    with open(os.path.join(args.output_dir, 'training_params.json'), 'w') as f:
        json.dump(params, f, indent=2)

    print("Model training completed successfully!")
    print(f"Best validation accuracy: {params['best_val_accuracy']:.4f} at epoch {params['best_epoch']}")


if __name__ == "__main__":
    main()
