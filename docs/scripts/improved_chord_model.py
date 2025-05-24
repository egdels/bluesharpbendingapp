#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Improved chord detection model for the TensorFlow Magenta harmonica chord detection.

This script defines an improved model architecture specifically designed for
better chord detection, with a focus on A-major chord recognition.

Usage:
    This module is imported by model_training.py

Author: BlueSharpBendingApp Team
"""

import tensorflow as tf
import numpy as np

# Constants
NUM_CLASSES = 96  # 12 semitones in an octave * 8 octaves (0-7)
INPUT_NODE = "input_audio"
OUTPUT_NODE = "chord_predictions"
NOTE_NAMES = ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B']

# A-major chord notes (MIDI numbers)
A_MAJOR_MIDI_NOTES = [57, 61, 64, 69]  # A3, C#4, E4, A4

# C-major chord notes (MIDI numbers)
C_MAJOR_MIDI_NOTES = [60, 64, 67]  # C4, E4, G4

def create_improved_chord_model(input_shape):
    """
    Create an improved model specifically designed for chord detection.
    This model incorporates harmonic relationships and chord-specific features.

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

        # Initial convolutional layer with larger kernel to capture more context
        x = tf.keras.layers.Conv1D(64, 7, padding='same')(x)
        x = tf.keras.layers.BatchNormalization()(x)
        x = tf.keras.layers.Activation('relu')(x)
        x = tf.keras.layers.MaxPooling1D(2)(x)

        # Parallel convolutional branches with different kernel sizes
        # This helps capture patterns at different scales
        branch1 = tf.keras.layers.Conv1D(64, 3, padding='same')(x)
        branch1 = tf.keras.layers.BatchNormalization()(branch1)
        branch1 = tf.keras.layers.Activation('relu')(branch1)

        branch2 = tf.keras.layers.Conv1D(64, 5, padding='same')(x)
        branch2 = tf.keras.layers.BatchNormalization()(branch2)
        branch2 = tf.keras.layers.Activation('relu')(branch2)

        branch3 = tf.keras.layers.Conv1D(64, 9, padding='same')(x)
        branch3 = tf.keras.layers.BatchNormalization()(branch3)
        branch3 = tf.keras.layers.Activation('relu')(branch3)

        # Combine branches
        x = tf.keras.layers.Concatenate()([branch1, branch2, branch3])
        x = tf.keras.layers.MaxPooling1D(2)(x)

        # Deep residual blocks
        for filters in [128, 256]:
            # First conv block
            residual = x

            x = tf.keras.layers.Conv1D(filters, 3, padding='same')(x)
            x = tf.keras.layers.BatchNormalization()(x)
            x = tf.keras.layers.Activation('relu')(x)

            x = tf.keras.layers.Conv1D(filters, 3, padding='same')(x)
            x = tf.keras.layers.BatchNormalization()(x)

            # Project residual if needed
            if residual.shape[-1] != filters:
                residual = tf.keras.layers.Conv1D(filters, 1, padding='same')(residual)
                residual = tf.keras.layers.BatchNormalization()(residual)

            # Add residual connection
            x = tf.keras.layers.add([x, residual])
            x = tf.keras.layers.Activation('relu')(x)
            x = tf.keras.layers.MaxPooling1D(2)(x)

        # Attention mechanism specifically designed for chord detection
        # This helps the model focus on the harmonic relationships
        attention = tf.keras.layers.Dense(256, activation='relu')(x)
        attention = tf.keras.layers.Dense(x.shape[-1], activation='sigmoid')(attention)
        x = tf.keras.layers.Multiply()([x, attention])

        # Global pooling
        x = tf.keras.layers.GlobalAveragePooling1D()(x)

        # Dense layers with harmonic awareness
        x = tf.keras.layers.Dense(512, activation='relu')(x)
        x = tf.keras.layers.Dropout(0.4)(x)

        # Add a harmonic relationship layer
        # This layer helps the model learn the relationships between notes in chords
        harmonic_layer = create_harmonic_layer(x)

        # Concatenate with the main branch
        x = tf.keras.layers.Concatenate()([x, harmonic_layer])

        # Final dense layers
        x = tf.keras.layers.Dense(256, activation='relu')(x)
        x = tf.keras.layers.Dropout(0.3)(x)

        # Add a chord-specific layer that focuses on common chord patterns
        chord_patterns = create_chord_patterns_layer(x)
        x = tf.keras.layers.Concatenate()([x, chord_patterns])

        # A-major specific attention
        a_major_attention = create_a_major_attention(x)
        x = tf.keras.layers.Concatenate()([x, a_major_attention])

        # C-major specific attention
        c_major_attention = create_c_major_attention(x)
        x = tf.keras.layers.Concatenate()([x, c_major_attention])

        # Output layer with sigmoid activation for multi-label classification
        outputs = tf.keras.layers.Dense(NUM_CLASSES, activation='sigmoid', name=OUTPUT_NODE)(x)

        model = tf.keras.Model(inputs=inputs, outputs=outputs)

        return model
    else:
        # For 2D input (e.g., spectrograms), use 2D convolutions
        inputs = tf.keras.layers.Input(shape=input_shape, name=INPUT_NODE)

        # Initial convolutional layer
        x = tf.keras.layers.Conv2D(64, (7, 7), padding='same')(inputs)
        x = tf.keras.layers.BatchNormalization()(x)
        x = tf.keras.layers.Activation('relu')(x)
        x = tf.keras.layers.MaxPooling2D((2, 2))(x)

        # Parallel convolutional branches with different kernel sizes
        branch1 = tf.keras.layers.Conv2D(64, (3, 3), padding='same')(x)
        branch1 = tf.keras.layers.BatchNormalization()(branch1)
        branch1 = tf.keras.layers.Activation('relu')(branch1)

        branch2 = tf.keras.layers.Conv2D(64, (5, 5), padding='same')(x)
        branch2 = tf.keras.layers.BatchNormalization()(branch2)
        branch2 = tf.keras.layers.Activation('relu')(branch2)

        branch3 = tf.keras.layers.Conv2D(64, (7, 7), padding='same')(x)
        branch3 = tf.keras.layers.BatchNormalization()(branch3)
        branch3 = tf.keras.layers.Activation('relu')(branch3)

        # Combine branches
        x = tf.keras.layers.Concatenate()([branch1, branch2, branch3])
        x = tf.keras.layers.MaxPooling2D((2, 2))(x)

        # Deep residual blocks
        for filters in [128, 256]:
            # First conv block
            residual = x

            x = tf.keras.layers.Conv2D(filters, (3, 3), padding='same')(x)
            x = tf.keras.layers.BatchNormalization()(x)
            x = tf.keras.layers.Activation('relu')(x)

            x = tf.keras.layers.Conv2D(filters, (3, 3), padding='same')(x)
            x = tf.keras.layers.BatchNormalization()(x)

            # Project residual if needed
            if residual.shape[-1] != filters:
                residual = tf.keras.layers.Conv2D(filters, (1, 1), padding='same')(residual)
                residual = tf.keras.layers.BatchNormalization()(residual)

            # Add residual connection
            x = tf.keras.layers.add([x, residual])
            x = tf.keras.layers.Activation('relu')(x)
            x = tf.keras.layers.MaxPooling2D((2, 2))(x)

        # Global pooling
        x = tf.keras.layers.GlobalAveragePooling2D()(x)

        # Dense layers
        x = tf.keras.layers.Dense(512, activation='relu')(x)
        x = tf.keras.layers.Dropout(0.4)(x)

        # Add a harmonic relationship layer
        harmonic_layer = create_harmonic_layer(x)

        # Concatenate with the main branch
        x = tf.keras.layers.Concatenate()([x, harmonic_layer])

        # Final dense layers
        x = tf.keras.layers.Dense(256, activation='relu')(x)
        x = tf.keras.layers.Dropout(0.3)(x)

        # Add a chord-specific layer
        chord_patterns = create_chord_patterns_layer(x)
        x = tf.keras.layers.Concatenate()([x, chord_patterns])

        # A-major specific attention
        a_major_attention = create_a_major_attention(x)
        x = tf.keras.layers.Concatenate()([x, a_major_attention])

        # C-major specific attention
        c_major_attention = create_c_major_attention(x)
        x = tf.keras.layers.Concatenate()([x, c_major_attention])

        # Output layer with sigmoid activation for multi-label classification
        outputs = tf.keras.layers.Dense(NUM_CLASSES, activation='sigmoid', name=OUTPUT_NODE)(x)

        model = tf.keras.Model(inputs=inputs, outputs=outputs)

        return model

def create_harmonic_layer(x):
    """
    Create a layer that helps the model learn harmonic relationships between notes.

    Args:
        x: Input tensor

    Returns:
        Tensor with harmonic relationship features
    """
    # Create a layer that learns harmonic relationships
    harmonic = tf.keras.layers.Dense(128, activation='relu')(x)
    harmonic = tf.keras.layers.Dense(64, activation='relu')(harmonic)

    # This layer will learn to recognize common harmonic intervals
    # (e.g., perfect fifths, major thirds, etc.)
    return harmonic

def create_chord_patterns_layer(x):
    """
    Create a layer that focuses on common chord patterns.

    Args:
        x: Input tensor

    Returns:
        Tensor with chord pattern features
    """
    # Create a layer that learns common chord patterns
    patterns = tf.keras.layers.Dense(64, activation='relu')(x)
    patterns = tf.keras.layers.Dense(32, activation='relu')(patterns)

    # This layer will learn to recognize common chord patterns
    # (e.g., major, minor, dominant 7th, etc.)
    return patterns

def create_a_major_attention(x):
    """
    Create a layer that specifically focuses on A-major chord detection.

    Args:
        x: Input tensor

    Returns:
        Tensor with A-major specific features
    """
    # Create a layer that specifically focuses on A-major chord
    a_major = tf.keras.layers.Dense(32, activation='relu')(x)
    a_major = tf.keras.layers.Dense(16, activation='relu')(a_major)

    # This layer will learn to specifically recognize A-major chord
    return a_major

def create_c_major_attention(x):
    """
    Create a layer that specifically focuses on C-major chord detection.

    Args:
        x: Input tensor

    Returns:
        Tensor with C-major specific features
    """
    # Create a layer that specifically focuses on C-major chord
    c_major = tf.keras.layers.Dense(32, activation='relu')(x)
    c_major = tf.keras.layers.Dense(16, activation='relu')(c_major)

    # This layer will learn to specifically recognize C-major chord
    return c_major

def improved_chord_focused_loss(y_true, y_pred):
    """
    Improved loss function that emphasizes chord notes and specifically
    focuses on A-major and C-major chord notes.

    Args:
        y_true: True labels
        y_pred: Predicted labels

    Returns:
        Loss value
    """
    # Standard binary cross-entropy loss for all notes
    bce = tf.keras.losses.binary_crossentropy(y_true, y_pred)

    # Find active notes in the ground truth (chord notes)
    active_notes_mask = tf.greater(y_true, 0.5)

    # Calculate additional loss for chord notes
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

    # Create a mask for A-major chord notes
    a_major_mask = tf.zeros_like(y_true, dtype=tf.bool)
    for note in A_MAJOR_MIDI_NOTES:
        if note < NUM_CLASSES:
            # Set the corresponding index to True
            a_major_mask = tf.logical_or(a_major_mask, tf.equal(tf.range(NUM_CLASSES, dtype=tf.int32), note))

    # Calculate additional loss for A-major chord notes
    a_major_loss = tf.where(
        tf.logical_and(active_notes_mask, a_major_mask),
        element_wise_bce,
        tf.zeros_like(y_true)
    )

    # Take the mean of the A-major loss across all notes
    a_major_loss = tf.reduce_mean(a_major_loss, axis=1)

    # Create a mask for C-major chord notes
    c_major_mask = tf.zeros_like(y_true, dtype=tf.bool)
    for note in C_MAJOR_MIDI_NOTES:
        if note < NUM_CLASSES:
            # Set the corresponding index to True
            c_major_mask = tf.logical_or(c_major_mask, tf.equal(tf.range(NUM_CLASSES, dtype=tf.int32), note))

    # Calculate additional loss for C-major chord notes
    c_major_loss = tf.where(
        tf.logical_and(active_notes_mask, c_major_mask),
        element_wise_bce,
        tf.zeros_like(y_true)
    )

    # Take the mean of the C-major loss across all notes
    c_major_loss = tf.reduce_mean(c_major_loss, axis=1)

    # Combine losses with higher weights for chord notes, A-major notes, and C-major notes
    # The factors 3.0 and 5.0 give more emphasis to chord notes and specific chord notes
    combined_loss = bce + 3.0 * chord_loss + 5.0 * a_major_loss + 5.0 * c_major_loss

    return combined_loss
