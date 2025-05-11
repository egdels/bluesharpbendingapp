#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Main training script for the TensorFlow Magenta harmonica chord detection model.

This script provides a single entry point for the entire training pipeline:
1. Data preparation
2. Feature extraction
3. Model training
4. Model evaluation

Usage:
    python train_magenta_model.py --mode=full --input_dir=path/to/raw/data --output_dir=path/to/output

Author: BlueSharpBendingApp Team
"""

import os
import argparse
import subprocess
import sys
import json
from datetime import datetime

# Constants
SCRIPTS_DIR = os.path.dirname(os.path.abspath(__file__))


def parse_arguments():
    """Parse command line arguments."""
    parser = argparse.ArgumentParser(description='Train a TensorFlow Magenta model for harmonica chord detection')
    
    # Main options
    parser.add_argument('--mode', type=str, default='full',
                        choices=['full', 'prepare', 'extract', 'train', 'evaluate', 'predict'],
                        help='Mode of operation (default: full)')
    parser.add_argument('--input_dir', type=str, required=True,
                        help='Directory containing raw audio files (for prepare mode) or processed data (for other modes)')
    parser.add_argument('--output_dir', type=str, required=True,
                        help='Directory to save output files')
    
    # Data preparation options
    parser.add_argument('--metadata_file', type=str, default=None,
                        help='JSON file with metadata for audio files (for prepare mode)')
    parser.add_argument('--split_ratio', type=float, default=0.8,
                        help='Train/validation split ratio (default: 0.8)')
    parser.add_argument('--normalize', action='store_true',
                        help='Normalize audio files (for prepare mode)')
    parser.add_argument('--augment_data', action='store_true',
                        help='Apply data augmentation during preparation (for prepare mode)')
    parser.add_argument('--organize', action='store_true',
                        help='Organize files by key and tuning based on metadata (for prepare mode)')
    
    # Feature extraction options
    parser.add_argument('--feature_type', type=str, default='all',
                        choices=['mfcc', 'chroma', 'contrast', 'mel', 'raw', 'all'],
                        help='Type of features to extract (default: all)')
    parser.add_argument('--include_raw', action='store_true',
                        help='Include raw audio data in the features (for extract mode)')
    parser.add_argument('--include_delta', action='store_true',
                        help='Include delta and delta-delta features (for extract mode)')
    parser.add_argument('--label_type', type=str, default='note',
                        choices=['note', 'chord', 'key_tuning'],
                        help='Type of labels to extract (default: note)')
    
    # Model training options
    parser.add_argument('--model_type', type=str, default='advanced',
                        choices=['simple', 'cnn', 'rnn', 'advanced', 'multi_key'],
                        help='Type of model architecture to use (default: advanced)')
    parser.add_argument('--epochs', type=int, default=50,
                        help='Number of training epochs (default: 50)')
    parser.add_argument('--batch_size', type=int, default=32,
                        help='Batch size for training (default: 32)')
    parser.add_argument('--learning_rate', type=float, default=0.001,
                        help='Learning rate (default: 0.001)')
    parser.add_argument('--early_stopping', action='store_true',
                        help='Use early stopping (for train mode)')
    parser.add_argument('--patience', type=int, default=5,
                        help='Patience for early stopping (default: 5)')
    parser.add_argument('--augment_training', action='store_true',
                        help='Use data augmentation during training (for train mode)')
    parser.add_argument('--tensorboard', action='store_true',
                        help='Enable TensorBoard logging (for train mode)')
    
    # Model evaluation options
    parser.add_argument('--test_data', type=str, default=None,
                        help='Directory containing test data or path to a test file (for evaluate mode)')
    parser.add_argument('--audio_file', action='store_true',
                        help='Test data is a single audio file (for evaluate/predict mode)')
    parser.add_argument('--visualize', action='store_true',
                        help='Generate visualizations (for evaluate mode)')
    
    return parser.parse_args()


def prepare_data(args):
    """Run the data preparation script."""
    print("\n=== Step 1: Data Preparation ===")
    
    # Construct the command
    cmd = [
        sys.executable,
        os.path.join(SCRIPTS_DIR, 'data_preparation.py'),
        f"--input_dir={args.input_dir}",
        f"--output_dir={os.path.join(args.output_dir, 'processed_data')}",
        f"--split_ratio={args.split_ratio}"
    ]
    
    # Add optional arguments
    if args.metadata_file:
        cmd.append(f"--metadata_file={args.metadata_file}")
    if args.normalize:
        cmd.append("--normalize")
    if args.augment_data:
        cmd.append("--augment")
    if args.organize:
        cmd.append("--organize")
    
    # Run the command
    print(f"Running command: {' '.join(cmd)}")
    result = subprocess.run(cmd, check=True)
    
    if result.returncode != 0:
        print("Data preparation failed.")
        sys.exit(1)
    
    return os.path.join(args.output_dir, 'processed_data')


def extract_features(args, processed_data_dir):
    """Run the feature extraction script."""
    print("\n=== Step 2: Feature Extraction ===")
    
    # Construct the command
    cmd = [
        sys.executable,
        os.path.join(SCRIPTS_DIR, 'feature_extraction.py'),
        f"--input_dir={processed_data_dir}",
        f"--output_dir={os.path.join(args.output_dir, 'features')}",
        f"--feature_type={args.feature_type}",
        f"--label_type={args.label_type}"
    ]
    
    # Add optional arguments
    if args.include_raw:
        cmd.append("--include_raw")
    if args.include_delta:
        cmd.append("--include_delta")
    
    # Run the command
    print(f"Running command: {' '.join(cmd)}")
    result = subprocess.run(cmd, check=True)
    
    if result.returncode != 0:
        print("Feature extraction failed.")
        sys.exit(1)
    
    return os.path.join(args.output_dir, 'features')


def train_model(args, features_dir):
    """Run the model training script."""
    print("\n=== Step 3: Model Training ===")
    
    # Construct the command
    cmd = [
        sys.executable,
        os.path.join(SCRIPTS_DIR, 'model_training.py'),
        f"--features_dir={features_dir}",
        f"--output_dir={os.path.join(args.output_dir, 'model')}",
        f"--model_type={args.model_type}",
        f"--epochs={args.epochs}",
        f"--batch_size={args.batch_size}",
        f"--learning_rate={args.learning_rate}",
        f"--feature_type={args.feature_type}"
    ]
    
    # Add optional arguments
    if args.early_stopping:
        cmd.append("--early_stopping")
        cmd.append(f"--patience={args.patience}")
    if args.augment_training:
        cmd.append("--augment")
    if args.tensorboard:
        cmd.append("--tensorboard")
    
    # Run the command
    print(f"Running command: {' '.join(cmd)}")
    result = subprocess.run(cmd, check=True)
    
    if result.returncode != 0:
        print("Model training failed.")
        sys.exit(1)
    
    return os.path.join(args.output_dir, 'model')


def evaluate_model(args, model_dir, test_data=None):
    """Run the model evaluation script."""
    print("\n=== Step 4: Model Evaluation ===")
    
    # If test_data is not provided, use validation data from features directory
    if test_data is None:
        if args.test_data:
            test_data = args.test_data
        else:
            test_data = os.path.join(args.output_dir, 'features', 'validation')
    
    # Construct the command
    cmd = [
        sys.executable,
        os.path.join(SCRIPTS_DIR, 'model_evaluation.py'),
        f"--model_dir={model_dir}",
        f"--test_data={test_data}",
        f"--output_dir={os.path.join(args.output_dir, 'evaluation')}",
        f"--feature_type={args.feature_type}",
        f"--batch_size={args.batch_size}"
    ]
    
    # Add optional arguments
    if args.audio_file:
        cmd.append("--audio_file")
    if args.visualize:
        cmd.append("--visualize")
    
    # Run the command
    print(f"Running command: {' '.join(cmd)}")
    result = subprocess.run(cmd, check=True)
    
    if result.returncode != 0:
        print("Model evaluation failed.")
        sys.exit(1)
    
    return os.path.join(args.output_dir, 'evaluation')


def predict_audio(args, model_dir, audio_file=None):
    """Run the model prediction for a single audio file."""
    print("\n=== Predicting Audio File ===")
    
    # If audio_file is not provided, use the one from args
    if audio_file is None:
        if args.test_data:
            audio_file = args.test_data
        else:
            print("No audio file specified for prediction.")
            sys.exit(1)
    
    # Construct the command
    cmd = [
        sys.executable,
        os.path.join(SCRIPTS_DIR, 'model_evaluation.py'),
        f"--model_dir={model_dir}",
        f"--test_data={audio_file}",
        f"--output_dir={os.path.join(args.output_dir, 'prediction')}",
        f"--feature_type={args.feature_type}",
        "--audio_file"
    ]
    
    # Run the command
    print(f"Running command: {' '.join(cmd)}")
    result = subprocess.run(cmd, check=True)
    
    if result.returncode != 0:
        print("Audio prediction failed.")
        sys.exit(1)
    
    # Read and display the prediction result
    prediction_file = os.path.join(args.output_dir, 'prediction', 'prediction.json')
    if os.path.exists(prediction_file):
        with open(prediction_file, 'r') as f:
            prediction = json.load(f)
        
        print(f"\nPrediction for {os.path.basename(audio_file)}:")
        print(f"  Note: {prediction['predicted_note']}")
        print(f"  Confidence: {prediction['confidence']:.4f}")
    
    return os.path.join(args.output_dir, 'prediction')


def copy_model_to_resources(model_dir):
    """Copy the trained model to the app's resources directory."""
    print("\n=== Copying Model to Resources ===")
    
    # Define the source and destination paths
    source_path = os.path.join(model_dir, 'models', 'magenta_chord_detection_model.pb')
    dest_dir = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(SCRIPTS_DIR))),
                           'base', 'src', 'main', 'resources', 'models')
    dest_path = os.path.join(dest_dir, 'magenta_chord_detection_model.pb')
    
    # Check if the source file exists
    if not os.path.exists(source_path):
        print(f"Model file not found at {source_path}")
        return False
    
    # Create the destination directory if it doesn't exist
    os.makedirs(dest_dir, exist_ok=True)
    
    # Copy the file
    try:
        import shutil
        shutil.copy2(source_path, dest_path)
        print(f"Model copied to {dest_path}")
        return True
    except Exception as e:
        print(f"Error copying model: {e}")
        return False


def save_pipeline_info(args, output_dir):
    """Save information about the pipeline run."""
    pipeline_info = {
        'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
        'command_line_args': vars(args),
        'output_directory': output_dir
    }
    
    info_file = os.path.join(output_dir, 'pipeline_info.json')
    with open(info_file, 'w') as f:
        json.dump(pipeline_info, f, indent=2)
    
    print(f"Pipeline information saved to {info_file}")


def main():
    """Main function."""
    # Parse command line arguments
    args = parse_arguments()
    
    # Create output directory if it doesn't exist
    os.makedirs(args.output_dir, exist_ok=True)
    
    try:
        # Run the appropriate pipeline based on the mode
        if args.mode == 'full':
            # Run the full pipeline
            processed_data_dir = prepare_data(args)
            features_dir = extract_features(args, processed_data_dir)
            model_dir = train_model(args, features_dir)
            evaluation_dir = evaluate_model(args, model_dir)
            
            # Copy the model to the app's resources directory
            copy_model_to_resources(model_dir)
            
        elif args.mode == 'prepare':
            # Run only the data preparation step
            prepare_data(args)
            
        elif args.mode == 'extract':
            # Run only the feature extraction step
            extract_features(args, args.input_dir)
            
        elif args.mode == 'train':
            # Run only the model training step
            train_model(args, args.input_dir)
            
        elif args.mode == 'evaluate':
            # Run only the model evaluation step
            evaluate_model(args, args.input_dir, args.test_data)
            
        elif args.mode == 'predict':
            # Run only the prediction for a single audio file
            predict_audio(args, args.input_dir, args.test_data)
        
        # Save information about the pipeline run
        save_pipeline_info(args, args.output_dir)
        
        print("\n=== Pipeline completed successfully! ===")
        
    except Exception as e:
        print(f"\nError: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main()