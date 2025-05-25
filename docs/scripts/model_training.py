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
MODEL_NAME = "chord_detection_model"
INPUT_NODE = "input_audio"
OUTPUT_NODE = "chord_predictions"
NUM_CLASSES = 96  # 12 semitones in an octave * 8 octaves (0-7)
OCTAVE_RANGE = 8  # Support octaves 0-7
NOTE_NAMES = ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B']


def parse_arguments():
    """Parse command line arguments."""
    parser = argparse.ArgumentParser(description='Train a TensorFlow model for harmonica chord detection')
    parser.add_argument('--features_dir', type=str, required=True, help='Directory containing extracted features')
    parser.add_argument('--output_dir', type=str, required=True, help='Directory to save the trained model')
    parser.add_argument('--model_type', type=str, default='advanced', 
                        choices=['simple', 'cnn', 'rnn', 'advanced', 'multi_key'],
                        help='Type of model architecture to use (default: advanced)')
    parser.add_argument('--epochs', type=int, default=100, help='Number of training epochs (default: 100)')
    parser.add_argument('--batch_size', type=int, default=32, help='Batch size for training (default: 32)')
    parser.add_argument('--learning_rate', type=float, default=0.0005, help='Learning rate (default: 0.0005)')
    parser.add_argument('--early_stopping', action='store_true', help='Use early stopping')
    parser.add_argument('--patience', type=int, default=10, help='Patience for early stopping (default: 10)')
    parser.add_argument('--feature_type', type=str, default='all', 
                        choices=['mfcc', 'chroma', 'contrast', 'mel', 'raw', 'all'],
                        help='Type of features to use for training (default: all)')
    parser.add_argument('--augment', action='store_true', help='Use data augmentation during training')
    parser.add_argument('--tensorboard', action='store_true', help='Enable TensorBoard logging')
    parser.add_argument('--class_weight', action='store_true', help='Use class weighting to emphasize certain chords')
    parser.add_argument('--target_file', type=str, default=None, help='Specific audio file to ensure is in training set')
    return parser.parse_args()


def load_data(features_dir, feature_type='all', target_file=None):
    """
    Load features and labels from the specified directory.

    Args:
        features_dir: Directory containing the features and labels
        feature_type: Type of features to use for training
        target_file: Specific audio file to ensure is in training set

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

    # If a target file is specified, ensure it's in the training set
    if target_file:
        target_file_basename = os.path.basename(target_file)
        print(f"Ensuring target file '{target_file_basename}' is in the training set...")

        # Check if the target file is in the validation set
        target_in_val = False
        target_val_idx = None

        for i, feature in enumerate(val_features):
            if 'file_path' in feature and target_file_basename in feature['file_path']:
                target_in_val = True
                target_val_idx = i
                break

        # If the target file is in the validation set, move it to the training set
        if target_in_val and target_val_idx is not None:
            print(f"Moving target file from validation set to training set...")
            target_feature = val_features.pop(target_val_idx)
            target_label = val_labels.pop(target_val_idx)
            train_features.append(target_feature)
            train_labels.append(target_label)
        else:
            # Check if the target file is already in the training set
            target_in_train = False
            for feature in train_features:
                if 'file_path' in feature and target_file_basename in feature['file_path']:
                    target_in_train = True
                    break

            if target_in_train:
                print(f"Target file is already in the training set.")
            else:
                print(f"Warning: Target file not found in either training or validation set.")

                # Try to extract features from the target file and add to training set
                try:
                    import librosa
                    from model_evaluation import extract_features_from_audio

                    print(f"Extracting features from target file and adding to training set...")
                    features = extract_features_from_audio(target_file, feature_type)

                    # Create a feature dictionary
                    feature_dict = {
                        'file_path': target_file,
                        'mfcc': features[0] if feature_type in ['mfcc', 'all'] else None,
                        'chroma': features[1] if feature_type in ['chroma', 'all'] else None,
                        'contrast': features[2] if feature_type in ['contrast', 'all'] else None
                    }

                    # Extract chord name from the target file name
                    file_name = os.path.basename(target_file)
                    if "chord_" in file_name and "_" in file_name[6:]:
                        # Extract chord part from filename (e.g., "chord_C4-E4-G4_1.wav" -> "C4-E4-G4")
                        chord_part = file_name[6:].split('_')[0]
                        label = chord_part
                    else:
                        # Default label if chord name can't be extracted
                        label = "unknown"

                    # Add to training set
                    train_features.append(feature_dict)
                    train_labels.append(label)

                    print(f"Successfully added target file to training set.")
                except Exception as e:
                    print(f"Error adding target file to training set: {e}")

    # Process features based on feature_type
    X_train = process_features(train_features, feature_type)
    X_val = process_features(val_features, feature_type)

    # Print a sample of the labels for debugging
    print(f"\nSample of training labels (first 10):")
    for i, label in enumerate(train_labels[:10]):
        print(f"  Label {i}: {label}")

    # Count unique labels
    label_counts = {}
    for label in train_labels:
        if label in label_counts:
            label_counts[label] += 1
        else:
            label_counts[label] = 1

    print(f"\nUnique labels in training data ({len(label_counts)}):")
    for label, count in sorted(label_counts.items(), key=lambda x: x[1], reverse=True)[:10]:
        print(f"  {label}: {count} occurrences")

    # Process labels
    y_train = process_labels(train_labels)
    y_val = process_labels(val_labels)

    print(f"Final dataset: {len(X_train)} training samples and {len(X_val)} validation samples.")

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
    For multi-label classification, we use multi-hot encoding where each note in a chord is represented.

    Args:
        labels_list: List of labels

    Returns:
        Processed labels as a numpy array with multi-hot encoding
    """
    # Initialize multi-hot encoded labels (one row per sample, one column per note class)
    multi_hot_labels = np.zeros((len(labels_list), NUM_CLASSES), dtype=np.float32)

    # Define note mapping for all notes including sharps and flats
    note_map = {
        'C': 0, 'C#': 1, 'Db': 1, 
        'D': 2, 'D#': 3, 'Eb': 3, 
        'E': 4, 
        'F': 5, 'F#': 6, 'Gb': 6, 
        'G': 7, 'G#': 8, 'Ab': 8, 
        'A': 9, 'A#': 10, 'Bb': 10, 
        'B': 11
    }

    for i, label in enumerate(labels_list):
        if isinstance(label, dict):
            # Handle key_tuning labels
            # For now, we'll just use the key as the label
            if 'key' in label and label['key'] != 'unknown':
                # Map key to semitone index (C=0, C#=1, D=2, etc.)
                key_map = {'C': 0, 'C#': 1, 'Db': 1, 'D': 2, 'D#': 3, 'Eb': 3,
                          'E': 4, 'F': 5, 'F#': 6, 'Gb': 6, 'G': 7, 'G#': 8,
                          'Ab': 8, 'A': 9, 'A#': 10, 'Bb': 10, 'B': 11}

                if label['key'] in key_map:
                    # For key_tuning labels, we'll set all octaves of the key
                    key_semitone = key_map[label['key']]
                    for octave in range(OCTAVE_RANGE):
                        class_index = key_semitone + (octave * 12)
                        multi_hot_labels[i, class_index] = 1.0
                else:
                    # Default to C if key is not recognized
                    for octave in range(OCTAVE_RANGE):
                        multi_hot_labels[i, octave * 12] = 1.0
            else:
                # Default to C if key is unknown
                for octave in range(OCTAVE_RANGE):
                    multi_hot_labels[i, octave * 12] = 1.0

        elif isinstance(label, str):
            # Check if the label is in the format "C4-E4-G4" (notes separated by hyphens)
            if '-' in label and all(part[0] in 'ABCDEFG' for part in label.split('-')):
                # Handle chord labels in the format "C4-E4-G4"
                # Process each note in the chord
                for note_str in label.split('-'):
                    # Extract the complete note name (including sharp/flat)
                    note_name = ''
                    for j in range(len(note_str)):
                        if note_str[j].isalpha() or note_str[j] in ['#', 'b']:
                            note_name += note_str[j]
                        else:
                            break

                    # Extract octave number
                    octave = 4  # Default to octave 4 if not specified
                    for j in range(len(note_name), len(note_str)):
                        if note_str[j].isdigit():
                            octave = int(note_str[j])
                            break

                    # Ensure octave is within range
                    octave = max(0, min(octave, OCTAVE_RANGE - 1))

                    # Map note name to semitone index
                    if note_name in note_map:
                        semitone = note_map[note_name]

                        # Calculate class index based on semitone and octave
                        class_index = semitone + (octave * 12)
                        multi_hot_labels[i, class_index] = 1.0
                    else:
                        # Default to C4 (48) if note is not recognized
                        print(f"Warning: Note '{note_name}' not recognized in chord '{label}', defaulting to C4")
                        multi_hot_labels[i, 48] = 1.0
            elif label.startswith(('C', 'D', 'E', 'F', 'G', 'A', 'B')):
                # Handle note labels (e.g., 'C4', 'A3', etc.) or traditional chord labels (e.g., 'Cmaj')
                # Extract the complete note name (including sharp/flat)
                note_name = ''
                for j in range(len(label)):
                    if label[j].isalpha() or label[j] in ['#', 'b']:
                        note_name += label[j]
                    else:
                        break

                # Extract octave number if present
                octave = 4  # Default to octave 4 if not specified
                for j in range(len(note_name), len(label)):
                    if label[j].isdigit():
                        octave = int(label[j])
                        break

                # Ensure octave is within range
                octave = max(0, min(octave, OCTAVE_RANGE - 1))

                # Map note name to semitone index
                if note_name in note_map:
                    semitone = note_map[note_name]

                    # Calculate class index based on semitone and octave
                    class_index = semitone + (octave * 12)
                    multi_hot_labels[i, class_index] = 1.0
                else:
                    # Default to C4 (48) if note is not recognized
                    print(f"Warning: Note '{note_name}' not recognized in label '{label}', defaulting to C4")
                    multi_hot_labels[i, 48] = 1.0
            else:
                # Default to C4 (48) if label is not a note or chord
                multi_hot_labels[i, 48] = 1.0
        else:
            # Default to C4 (48) for any other label type
            multi_hot_labels[i, 48] = 1.0

    return multi_hot_labels


def create_simple_model(input_shape):
    """
    Create a simple model for multi-label chord detection.

    Args:
        input_shape: Shape of the input data

    Returns:
        A TensorFlow model for multi-label classification
    """
    # Create a TensorFlow 2.x compatible model with explicit names for all layers
    inputs = tf.keras.layers.Input(shape=input_shape, name=INPUT_NODE)
    x = tf.keras.layers.Dense(128, activation='relu', name='dense_0')(inputs)
    x = tf.keras.layers.Dropout(0.3, name='dropout_0')(x)
    x = tf.keras.layers.Dense(64, activation='relu', name='dense_1')(x)
    x = tf.keras.layers.Dropout(0.3, name='dropout_1')(x)
    # Use sigmoid activation for multi-label classification
    outputs = tf.keras.layers.Dense(NUM_CLASSES, activation='sigmoid', name=OUTPUT_NODE)(x)

    model = tf.keras.Model(inputs=inputs, outputs=outputs, name='chord_detection_model')

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
            tf.keras.layers.Dense(NUM_CLASSES, activation='sigmoid', name=OUTPUT_NODE)
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
            tf.keras.layers.Dense(NUM_CLASSES, activation='sigmoid', name=OUTPUT_NODE)
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
            tf.keras.layers.Dense(NUM_CLASSES, activation='sigmoid', name=OUTPUT_NODE)
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
            tf.keras.layers.Dense(NUM_CLASSES, activation='sigmoid', name=OUTPUT_NODE)
        ])

    return model


def create_advanced_model(input_shape):
    """
    Create an advanced model for chord detection combining CNN and attention mechanisms.
    Optimized for better recognition of all chord types.

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

        # Convolutional blocks with smaller filters for better feature extraction
        # Using residual connections to help with gradient flow
        skip_connections = []
        for i, filters in enumerate([32, 64, 128, 256]):
            # First conv block
            conv1 = tf.keras.layers.Conv1D(filters, 3, padding='same')(x)
            bn1 = tf.keras.layers.BatchNormalization()(conv1)
            act1 = tf.keras.layers.Activation('relu')(bn1)

            # Second conv block
            conv2 = tf.keras.layers.Conv1D(filters, 3, padding='same')(act1)
            bn2 = tf.keras.layers.BatchNormalization()(conv2)
            act2 = tf.keras.layers.Activation('relu')(bn2)

            # Skip connection
            if i > 0:  # For layers after the first one
                # Project input to match the output dimensions
                skip = tf.keras.layers.Conv1D(filters, 1, padding='same')(x)
                skip = tf.keras.layers.BatchNormalization()(skip)
                # Add skip connection
                act2 = tf.keras.layers.add([act2, skip])
                act2 = tf.keras.layers.Activation('relu')(act2)

            # Save for later concatenation
            skip_connections.append(act2)

            # Pooling
            x = tf.keras.layers.MaxPooling1D(2)(act2)

        # Add attention mechanism to focus on important features
        attention = tf.keras.layers.GlobalAveragePooling1D()(x)
        attention = tf.keras.layers.Dense(256, activation='relu')(attention)
        attention = tf.keras.layers.Dense(x.shape[-1], activation='sigmoid')(attention)
        attention = tf.keras.layers.Reshape((1, x.shape[-1]))(attention)

        # Apply attention
        x = tf.keras.layers.Multiply()([x, attention])

        # Global pooling
        x = tf.keras.layers.GlobalAveragePooling1D()(x)

        # Concatenate with the last skip connection after pooling it
        last_skip = tf.keras.layers.GlobalAveragePooling1D()(skip_connections[-1])
        x = tf.keras.layers.Concatenate()([x, last_skip])

        # Dense layers with more units for better representation
        x = tf.keras.layers.Dense(512, activation='relu')(x)
        x = tf.keras.layers.Dropout(0.4)(x)
        x = tf.keras.layers.Dense(256, activation='relu')(x)
        x = tf.keras.layers.Dropout(0.3)(x)

        # Add a chord detector auxiliary output
        # This helps the model focus on chord structure
        # We use 12 units to represent the 12 semitones in an octave
        chord_detector = tf.keras.layers.Dense(12, activation='sigmoid', name='chord_detector')(x)

        # Main output layer with sigmoid activation for multi-label classification
        main_output = tf.keras.layers.Dense(NUM_CLASSES, activation='sigmoid', name=OUTPUT_NODE)(x)

        # Create model with multiple outputs
        model = tf.keras.Model(inputs=inputs, outputs=[main_output, chord_detector])

        # We'll only use the main output for training and inference
        # The chord_detector is an auxiliary output to help the model focus on chord structure
        final_model = tf.keras.Model(inputs=model.input, outputs=model.outputs[0])

        return final_model
    else:
        # For 2D input (e.g., spectrograms), use 2D convolutions
        inputs = tf.keras.layers.Input(shape=input_shape, name=INPUT_NODE)

        # Convolutional blocks with residual connections
        skip_connections = []
        x = inputs
        for i, filters in enumerate([32, 64, 128, 256]):
            # First conv block
            conv1 = tf.keras.layers.Conv2D(filters, (3, 3), padding='same')(x)
            bn1 = tf.keras.layers.BatchNormalization()(conv1)
            act1 = tf.keras.layers.Activation('relu')(bn1)

            # Second conv block
            conv2 = tf.keras.layers.Conv2D(filters, (3, 3), padding='same')(act1)
            bn2 = tf.keras.layers.BatchNormalization()(conv2)
            act2 = tf.keras.layers.Activation('relu')(bn2)

            # Skip connection
            if i > 0:  # For layers after the first one
                # Project input to match the output dimensions
                skip = tf.keras.layers.Conv2D(filters, (1, 1), padding='same')(x)
                skip = tf.keras.layers.BatchNormalization()(skip)
                # Add skip connection
                act2 = tf.keras.layers.add([act2, skip])
                act2 = tf.keras.layers.Activation('relu')(act2)

            # Save for later concatenation
            skip_connections.append(act2)

            # Pooling
            x = tf.keras.layers.MaxPooling2D((2, 2))(act2)

        # Add attention mechanism
        attention = tf.keras.layers.GlobalAveragePooling2D()(x)
        attention = tf.keras.layers.Dense(256, activation='relu')(attention)
        attention = tf.keras.layers.Dense(x.shape[-1], activation='sigmoid')(attention)
        attention = tf.keras.layers.Reshape((1, 1, x.shape[-1]))(attention)

        # Apply attention
        x = tf.keras.layers.Multiply()([x, attention])

        # Global pooling
        x = tf.keras.layers.GlobalAveragePooling2D()(x)

        # Concatenate with the last skip connection after pooling it
        last_skip = tf.keras.layers.GlobalAveragePooling2D()(skip_connections[-1])
        x = tf.keras.layers.Concatenate()([x, last_skip])

        # Dense layers with more units for better representation
        x = tf.keras.layers.Dense(512, activation='relu')(x)
        x = tf.keras.layers.Dropout(0.4)(x)
        x = tf.keras.layers.Dense(256, activation='relu')(x)
        x = tf.keras.layers.Dropout(0.3)(x)

        # Add a chord detector auxiliary output
        # This helps the model focus on chord structure
        chord_detector = tf.keras.layers.Dense(12, activation='sigmoid', name='chord_detector')(x)

        # Main output layer with sigmoid activation for multi-label classification
        main_output = tf.keras.layers.Dense(NUM_CLASSES, activation='sigmoid', name=OUTPUT_NODE)(x)

        # Create model with multiple outputs
        model = tf.keras.Model(inputs=inputs, outputs=[main_output, chord_detector])

        # We'll only use the main output for training and inference
        final_model = tf.keras.Model(inputs=model.input, outputs=model.outputs[0])

        return final_model


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
    # Use sigmoid activation for multi-label classification
    outputs = tf.keras.layers.Dense(NUM_CLASSES, activation='sigmoid', name=OUTPUT_NODE)(x)

    model = tf.keras.Model(inputs=[audio_input, key_input], outputs=outputs)

    return model


def create_class_weights(y_train, target_class=None):
    """
    Create class weights to emphasize certain classes.

    Args:
        y_train: Training labels (one-hot encoded)
        target_class: Specific class to emphasize (not used)
                      If None, all chord notes will be emphasized

    Returns:
        Dictionary of class weights
    """
    # Convert one-hot encoded labels to class indices
    y_indices = np.argmax(y_train, axis=1)

    # Count occurrences of each class
    class_counts = np.bincount(y_indices)

    # Calculate weights inversely proportional to class frequency
    n_samples = len(y_indices)
    n_classes = len(class_counts)
    weights = n_samples / (n_classes * class_counts)

    # Create dictionary of class weights
    class_weights = {i: weights[i] for i in range(len(weights))}

    # Analyze the training data to identify chord notes
    chord_notes = set()
    for i in range(y_train.shape[0]):
        # Find all active notes in this sample
        active_notes = np.where(y_train[i] > 0.5)[0]
        for note in active_notes:
            chord_notes.add(note)

    # Emphasize all chord notes
    for cls in chord_notes:
        if cls < len(weights):
            class_weights[cls] = weights[cls] * 3.0  # Increase weight by 3x for all chord notes

    print(f"Created class weights for {len(chord_notes)} chord notes")
    return class_weights


def train_model(model, X_train, y_train, X_val, y_val, args):
    """
    Train the model with the specified parameters.
    Optimized for better chord recognition across all chord types.

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
    # Define a custom loss function that emphasizes chord notes
    def chord_focused_loss(y_true, y_pred):
        # Standard binary cross-entropy loss for all notes
        bce = tf.keras.losses.binary_crossentropy(y_true, y_pred)

        # Find active notes in the ground truth (chord notes)
        # This creates a boolean mask where True indicates an active note
        active_notes_mask = tf.greater(y_true, 0.5)

        # Calculate additional loss for chord notes
        # We only consider the loss for notes that are active in the ground truth
        # Calculate binary cross-entropy manually to get per-element losses
        epsilon = tf.constant(1e-7, dtype=y_pred.dtype)
        y_pred = tf.clip_by_value(y_pred, epsilon, 1 - epsilon)
        element_wise_bce = -y_true * tf.math.log(y_pred) - (1 - y_true) * tf.math.log(1 - y_pred)

        # Apply the mask to only consider active notes
        chord_loss = tf.where(
            active_notes_mask,
            element_wise_bce,
            tf.zeros_like(y_true)
        )

        # Take the mean of the chord loss across all notes
        chord_loss = tf.reduce_mean(chord_loss, axis=1)

        # Combine losses with higher weight for chord notes
        # The factor 3.0 gives more emphasis to chord notes
        combined_loss = bce + 3.0 * chord_loss

        return combined_loss

    # Compile the model with the custom loss function
    model.compile(
        optimizer=tf.keras.optimizers.Adam(learning_rate=args.learning_rate),
        loss=chord_focused_loss,  # Use custom loss function
        metrics=['accuracy', tf.keras.metrics.AUC(), tf.keras.metrics.Precision(), tf.keras.metrics.Recall()]
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

    # Create class weights if requested
    class_weights = None
    if args.class_weight:
        print("Using class weighting to emphasize chord notes...")
        class_weights = create_class_weights(y_train)
        print(f"Class weights: {class_weights}")

    # Train the model
    print(f"Training model for {args.epochs} epochs with batch size {args.batch_size}...")
    history = model.fit(
        X_train, y_train,
        epochs=args.epochs,
        batch_size=args.batch_size,
        validation_data=(X_val, y_val),
        callbacks=callbacks,
        class_weight=class_weights,
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
    Save the trained model in various formats, with additional validation and fixes.
    Also converts the model to ONNX format.

    Args:
        model: The trained TensorFlow model
        output_dir: Directory to save the model
    """
    import tensorflow as tf

    # Create models directory
    models_dir = os.path.join(output_dir, 'models')
    os.makedirs(models_dir, exist_ok=True)

    # Validate initialization of all layers
    for layer in model.layers:
        try:
            weights = layer.get_weights()
            print(f"Layer {layer.name} initialized with weights shape {[w.shape for w in weights]}")
        except Exception as e:
            print(f"Error initializing layer {layer.name}: {e}")
            raise e

    # Save in SavedModel format
    saved_model_dir = os.path.join(models_dir, "saved_model")
    tf.saved_model.save(
        obj=model,
        export_dir=saved_model_dir,
        options=tf.saved_model.SaveOptions(save_debug_info=True)
    )

    print(f"Model saved in SavedModel format: {saved_model_dir}")

    # Save model weights
    model.save_weights(os.path.join(models_dir, f"{MODEL_NAME}.weights.h5"))
    print(f"Model weights saved at {models_dir}")

    # Convert model to ONNX format
    try:
        import tf2onnx
        import onnx

        # Create directory for ONNX model
        onnx_dir = os.path.join(models_dir, "onnx")
        os.makedirs(onnx_dir, exist_ok=True)

        # Path for the ONNX model
        onnx_model_path = os.path.join(onnx_dir, f"{MODEL_NAME}.onnx")

        # Convert the model to ONNX format
        print("Converting model to ONNX format...")
        spec = (tf.TensorSpec((None, *model.input_shape[1:]), tf.float32, name="input"),)
        output_path = tf2onnx.convert.from_keras(model, input_signature=spec, opset=13, output_path=onnx_model_path)

        # Verify the model
        onnx_model = onnx.load(onnx_model_path)
        onnx.checker.check_model(onnx_model)

        print(f"Model converted and saved in ONNX format: {onnx_model_path}")
    except ImportError:
        print("Warning: tf2onnx or onnx package not found. ONNX conversion skipped.")
        print("To enable ONNX conversion, install the required packages with:")
        print("pip install tf2onnx onnx")
    except Exception as e:
        print(f"Error converting model to ONNX format: {e}")
        print("ONNX conversion failed, but other model formats were saved successfully.")

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
    print("Loading training and validation data...")
    X_train, y_train, X_val, y_val = load_data(args.features_dir, args.feature_type, args.target_file)

    # Analyze the loaded data
    input_shape = X_train.shape[1:]
    print(f"Input shape: {input_shape}")
    print(f"Training samples: {len(X_train)}")
    print(f"Validation samples: {len(X_val)}")

    # Analyze chord distribution in training data
    chord_counts = np.sum(y_train, axis=0)
    active_notes = np.where(chord_counts > 0)[0]
    print(f"Number of active notes in training data: {len(active_notes)}")

    # Print the top 10 most common notes
    top_indices = np.argsort(chord_counts)[-10:][::-1]
    print("Top 10 most common notes in training data:")
    for idx in top_indices:
        note_name = NOTE_NAMES[idx % 12]
        octave = idx // 12
        print(f"  {note_name}{octave} (index {idx}): {chord_counts[idx]} occurrences")

    # Create model based on model_type
    print(f"Creating advanced model optimized for all chord types...")
    model = create_advanced_model(input_shape)

    # Print model architecture summary
    print("Model architecture summary:")
    model.summary()

    # Count model parameters
    trainable_params = np.sum([np.prod(v.shape) for v in model.trainable_weights])
    non_trainable_params = np.sum([np.prod(v.shape) for v in model.non_trainable_weights])
    total_params = trainable_params + non_trainable_params
    print(f"Total parameters: {total_params:,} (Trainable: {trainable_params:,}, Non-trainable: {non_trainable_params:,})")

    # Print information about the target file if specified
    if args.target_file:
        print(f"Ensuring target file '{os.path.basename(args.target_file)}' is included in training")
        print(f"This file will be included in the training set")

    # Print information about class weighting
    if args.class_weight:
        print(f"Using class weighting to emphasize chord notes")
        print(f"All chord notes will receive 3x weight")

    # Print training parameters
    print(f"Training parameters:")
    print(f"  - Epochs: {args.epochs}")
    print(f"  - Batch size: {args.batch_size}")
    print(f"  - Learning rate: {args.learning_rate}")
    print(f"  - Early stopping: {args.early_stopping}")
    if args.early_stopping:
        print(f"  - Patience: {args.patience}")
    print(f"  - Feature type: {args.feature_type}")
    print(f"  - Class weighting: {args.class_weight}")
    print(f"  - TensorBoard logging: {args.tensorboard}")

    # Train the model
    print("Starting model training...")
    history = train_model(model, X_train, y_train, X_val, y_val, args)

    # Plot training history
    plot_training_history(history, args.output_dir)

    # Save the model
    print("Saving model...")
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

    # Add information about active notes
    params['active_notes_count'] = int(len(active_notes))
    params['top_notes'] = [int(idx) for idx in top_indices.tolist()]

    with open(os.path.join(args.output_dir, 'training_params.json'), 'w') as f:
        json.dump(params, f, indent=2)

    print("Model training completed successfully!")
    print(f"Best validation accuracy: {params['best_val_accuracy']:.4f} at epoch {params['best_epoch']}")
    print(f"Model saved to {os.path.join(args.output_dir, 'models')}")
    print(f"Training parameters saved to {os.path.join(args.output_dir, 'training_params.json')}")
    print("You can now use the model for chord detection!")


if __name__ == "__main__":
    main()
