#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Improved training pipeline for the TensorFlow Magenta harmonica chord detection model.

This script integrates the enhanced data augmentation and improved chord model
to create a better training pipeline for chord detection, with a focus on
A-major chord recognition.

Usage:
    python improved_training_pipeline.py

Author: BlueSharpBendingApp Team
"""

import os
import sys
import subprocess
import argparse
import json
import time
import numpy as np
import tensorflow as tf
from tqdm import tqdm

# Add the scripts directory to the path
sys.path.append(os.path.join(os.path.dirname(os.path.abspath(__file__)), "docs/scripts"))

# Import the necessary modules
from docs.scripts.model_training import (
    load_data, plot_training_history, save_model
)
from docs.scripts.improved_chord_model import (
    create_improved_chord_model, improved_chord_focused_loss
)

# Constants
OUTPUT_DIR = "./training_output"
FEATURES_DIR = f"{OUTPUT_DIR}/features"
MODEL_DIR = f"{OUTPUT_DIR}/model"
TRAINING_DATA_DIR = "./training_data/data/raw"
AUGMENTED_DATA_DIR = f"{OUTPUT_DIR}/augmented_data"
MAX_EPOCHS = 500
BATCH_SIZE = 32
LEARNING_RATE = 0.0002
EARLY_STOPPING_PATIENCE = 30

def parse_arguments():
    """Parse command line arguments."""
    parser = argparse.ArgumentParser(description='Improved training pipeline for chord detection')
    parser.add_argument('--output_dir', type=str, default=OUTPUT_DIR, help='Directory to save output')
    parser.add_argument('--features_dir', type=str, default=FEATURES_DIR, help='Directory containing features')
    parser.add_argument('--model_dir', type=str, default=MODEL_DIR, help='Directory to save the model')
    parser.add_argument('--training_data_dir', type=str, default=TRAINING_DATA_DIR, help='Directory containing raw training data')
    parser.add_argument('--augmented_data_dir', type=str, default=AUGMENTED_DATA_DIR, help='Directory to save augmented data')
    parser.add_argument('--epochs', type=int, default=MAX_EPOCHS, help='Maximum number of epochs')
    parser.add_argument('--batch_size', type=int, default=BATCH_SIZE, help='Batch size')
    parser.add_argument('--learning_rate', type=float, default=LEARNING_RATE, help='Learning rate')
    parser.add_argument('--patience', type=int, default=EARLY_STOPPING_PATIENCE, help='Patience for early stopping')
    parser.add_argument('--skip_augmentation', action='store_true', help='Skip data augmentation')
    parser.add_argument('--skip_feature_extraction', action='store_true', help='Skip feature extraction')
    parser.add_argument('--test_after_training', action='store_true', help='Run tests after training')
    return parser.parse_args()

def run_enhanced_data_augmentation(args):
    """Run the enhanced data augmentation script."""
    print("Running enhanced data augmentation...")

    # Create the augmented data directory if it doesn't exist
    os.makedirs(args.augmented_data_dir, exist_ok=True)

    # Run the enhanced data augmentation script
    cmd = [
        "python3", "docs/scripts/enhanced_data_augmentation.py",
        f"--input_dir={args.training_data_dir}",
        f"--output_dir={args.augmented_data_dir}",
        "--num_augmentations=20",
        "--focus_on_a_major",
        "--generate_synthetic",
        "--num_synthetic=200"
    ]

    # Execute the command
    try:
        subprocess.run(cmd, check=True)
        print("Enhanced data augmentation completed successfully.")
    except subprocess.CalledProcessError as e:
        print(f"Error running enhanced data augmentation: {e}")
        sys.exit(1)

def run_feature_extraction(args):
    """Run the feature extraction script on the augmented data."""
    print("Running feature extraction on augmented data...")

    # Create the features directory if it doesn't exist
    os.makedirs(args.features_dir, exist_ok=True)

    # Check if a checkpoint file exists
    checkpoint_file = os.path.join(args.features_dir, 'checkpoint.json')
    if os.path.exists(checkpoint_file):
        print(f"Found checkpoint file at {checkpoint_file}. Resuming from checkpoint...")

    # Run the feature extraction script
    cmd = [
        "python3", "docs/scripts/feature_extraction.py",
        f"--input_dir={args.augmented_data_dir}",
        f"--output_dir={args.features_dir}",
        "--feature_type=all",
        "--include_raw",
        "--include_delta",
        "--label_type=chord"
    ]

    # Execute the command
    try:
        print("Starting feature extraction process. This may take a while...")
        print("You can safely interrupt the process with Ctrl+C at any time.")
        print("The process will save a checkpoint and can be resumed later.")
        subprocess.run(cmd, check=True)
        print("Feature extraction completed successfully.")
    except subprocess.CalledProcessError as e:
        # Check if it was interrupted by the user (exit code 1)
        if e.returncode == 1:
            print("Feature extraction was interrupted. You can resume it later by running the same command.")
            print("The process will continue from where it left off.")
            # Don't exit, allow the pipeline to continue with whatever features were extracted
            if input("Do you want to continue with the pipeline using the features extracted so far? (y/n): ").lower() == 'y':
                return
            else:
                sys.exit(1)
        else:
            print(f"Error running feature extraction: {e}")
            sys.exit(1)

def train_improved_model(args):
    """Train the improved chord model."""
    print("Training improved chord model...")

    # Load data
    print("Loading training and validation data...")
    X_train, y_train, X_val, y_val = load_data(args.features_dir, feature_type='all')

    # Print data shapes
    print(f"Training data shape: {X_train.shape}")
    print(f"Training labels shape: {y_train.shape}")
    print(f"Validation data shape: {X_val.shape}")
    print(f"Validation labels shape: {y_val.shape}")

    # Create improved model
    print("Creating improved chord model...")
    input_shape = X_train.shape[1:]
    model = create_improved_chord_model(input_shape)

    # Print model summary
    model.summary()

    # Compile model with improved loss function
    print("Compiling model with improved loss function...")
    model.compile(
        optimizer=tf.keras.optimizers.Adam(learning_rate=args.learning_rate),
        loss=improved_chord_focused_loss,
        metrics=['accuracy', tf.keras.metrics.AUC(), tf.keras.metrics.Precision(), tf.keras.metrics.Recall()]
    )

    # Set up callbacks
    callbacks = []

    # Model checkpoint callback
    checkpoint_path = os.path.join(args.model_dir, 'checkpoints', 'model_checkpoint.h5')
    os.makedirs(os.path.dirname(checkpoint_path), exist_ok=True)
    checkpoint_callback = tf.keras.callbacks.ModelCheckpoint(
        checkpoint_path,
        save_best_only=True,
        monitor='val_accuracy',
        mode='max'
    )
    callbacks.append(checkpoint_callback)

    # Early stopping callback
    early_stopping_callback = tf.keras.callbacks.EarlyStopping(
        monitor='val_accuracy',
        patience=args.patience,
        restore_best_weights=True
    )
    callbacks.append(early_stopping_callback)

    # TensorBoard callback
    log_dir = os.path.join(args.output_dir, 'logs', time.strftime("%Y%m%d-%H%M%S"))
    tensorboard_callback = tf.keras.callbacks.TensorBoard(
        log_dir=log_dir,
        histogram_freq=1
    )
    callbacks.append(tensorboard_callback)

    # Train model
    print(f"Training model for up to {args.epochs} epochs with batch size {args.batch_size}...")
    history = model.fit(
        X_train, y_train,
        epochs=args.epochs,
        batch_size=args.batch_size,
        validation_data=(X_val, y_val),
        callbacks=callbacks,
        verbose=1
    )

    # Plot training history
    plot_training_history(history, args.output_dir)

    # Save model
    save_model(model, args.model_dir)

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

    with open(os.path.join(args.output_dir, 'improved_training_params.json'), 'w') as f:
        json.dump(params, f, indent=2)

    print("Model training completed successfully!")
    print(f"Best validation accuracy: {params['best_val_accuracy']:.4f} at epoch {params['best_epoch']}")

    return model, history

def run_tests(args):
    """Run tests to evaluate the model."""
    print("Running tests to evaluate the model...")

    # Run the A-major chord test
    cmd = ["python3", "./docs/scripts/test_a_chord.py"]

    # Execute the command
    try:
        subprocess.run(cmd, check=True)
        print("A-major chord test completed successfully.")
    except subprocess.CalledProcessError as e:
        print(f"Error running A-major chord test: {e}")

    # Run the Java test
    cmd = ["./gradlew", "test", "--tests", "de.schliweb.bluesharpbendingapp.utils.OnnxModelTest"]

    # Execute the command
    try:
        subprocess.run(cmd, check=True)
        print("Java test completed successfully.")
    except subprocess.CalledProcessError as e:
        print(f"Error running Java test: {e}")

def main():
    """Main function."""
    args = parse_arguments()

    # Create output directories
    os.makedirs(args.output_dir, exist_ok=True)
    os.makedirs(args.model_dir, exist_ok=True)

    # Step 1: Run enhanced data augmentation
    if not args.skip_augmentation:
        run_enhanced_data_augmentation(args)
    else:
        print("Skipping data augmentation...")

    # Step 2: Run feature extraction
    if not args.skip_feature_extraction:
        run_feature_extraction(args)
    else:
        print("Skipping feature extraction...")

    # Step 3: Train improved model
    model, history = train_improved_model(args)

    # Step 4: Run tests
    if args.test_after_training:
        run_tests(args)

    print("Improved training pipeline completed successfully!")
    return 0

if __name__ == "__main__":
    sys.exit(main())
