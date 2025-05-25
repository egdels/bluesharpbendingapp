#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Comparison script for TensorFlow and ONNX models.

This script compares the predictions of the TensorFlow and ONNX models
on the same audio files to verify that they produce similar results.

Usage:
    python compare_tf_onnx_models.py [--num_files=N]

Author: BlueSharpBendingApp Team
"""

import os
import sys
import numpy as np
import tensorflow as tf
import onnxruntime as ort
import librosa
import glob
import argparse
from tqdm import tqdm

# Add the parent directory to the path to import from model_evaluation
sys.path.append(os.path.dirname(os.path.abspath(__file__)))
from model_evaluation import load_model, extract_features_from_audio

# Constants
MODEL_DIR = "/Users/christian/Documents/git/bluesharpbendingapp/training_output/model"
TF_MODEL_DIR = os.path.join(MODEL_DIR, "models", "saved_model")
ONNX_MODEL_PATH = os.path.join(MODEL_DIR, "models", "onnx", "chord_detection_model.onnx")
TRAINING_DATA_DIR = "/Users/christian/Documents/git/bluesharpbendingapp/training_data/data/raw"
NOTE_NAMES = ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B']
NUM_CLASSES = 96  # 12 semitones in an octave * 8 octaves (0-7)
SAMPLE_RATE = 16000  # Expected sample rate of the audio files
DEFAULT_NUM_FILES = 10  # Default number of files to test

def load_onnx_model(model_path):
    """
    Load an ONNX model using ONNX Runtime.
    
    Args:
        model_path: Path to the ONNX model file
        
    Returns:
        ONNX Runtime InferenceSession
    """
    try:
        # Create an ONNX Runtime inference session
        session = ort.InferenceSession(model_path)
        return session
    except Exception as e:
        print(f"Error loading ONNX model: {e}")
        return None

def predict_with_tf_model(model, features):
    """
    Predict using the TensorFlow model.
    
    Args:
        model: TensorFlow model
        features: Input features
        
    Returns:
        Prediction array
    """
    # Check if the model is a SavedModel
    if hasattr(model, 'signatures'):
        # Get the serving signature
        serving_fn = model.signatures['serving_default']
        
        # Convert to tensor
        features_tensor = tf.convert_to_tensor(features.astype(np.float32))
        
        # Run inference
        output = serving_fn(features_tensor)
        
        # Get the output tensor (assuming it's the first output)
        output_key = list(output.keys())[0]
        prediction = output[output_key].numpy()
    else:
        # For regular TensorFlow models
        prediction = model.predict(features)
    
    return prediction

def predict_with_onnx_model(model, features):
    """
    Predict using the ONNX model.
    
    Args:
        model: ONNX Runtime InferenceSession
        features: Input features
        
    Returns:
        Prediction array
    """
    # Get the input name from the model
    input_name = model.get_inputs()[0].name
    
    # Ensure features are in the correct format for ONNX
    features = features.astype(np.float32)
    
    # Run inference with ONNX Runtime
    prediction = model.run(None, {input_name: features})[0]
    
    return prediction

def get_top_notes(prediction, n=5):
    """
    Get the top N predicted notes from a prediction array.
    
    Args:
        prediction: Prediction array
        n: Number of top notes to return
        
    Returns:
        List of top note names and confidences
    """
    # Get the top N predicted classes and confidences
    top_indices = np.argsort(prediction[0])[-n:][::-1]
    top_confidences = prediction[0][top_indices]
    
    # Map the class indices to note names with octaves
    top_notes = []
    for idx in top_indices:
        octave = idx // 12
        semitone = idx % 12
        top_notes.append(f"{NOTE_NAMES[semitone]}{octave}")
    
    return top_notes, top_confidences

def compare_predictions(tf_prediction, onnx_prediction):
    """
    Compare the predictions from TensorFlow and ONNX models.
    
    Args:
        tf_prediction: Prediction from TensorFlow model
        onnx_prediction: Prediction from ONNX model
        
    Returns:
        Dictionary with comparison metrics
    """
    # Calculate mean absolute error
    mae = np.mean(np.abs(tf_prediction - onnx_prediction))
    
    # Calculate mean squared error
    mse = np.mean(np.square(tf_prediction - onnx_prediction))
    
    # Calculate correlation coefficient
    correlation = np.corrcoef(tf_prediction.flatten(), onnx_prediction.flatten())[0, 1]
    
    # Get top 5 notes from each model
    tf_top_notes, tf_confidences = get_top_notes(tf_prediction)
    onnx_top_notes, onnx_confidences = get_top_notes(onnx_prediction)
    
    # Calculate overlap in top 5 notes
    overlap = len(set(tf_top_notes) & set(onnx_top_notes))
    overlap_percentage = overlap / 5 * 100
    
    # Check if the top note is the same
    top_note_match = tf_top_notes[0] == onnx_top_notes[0]
    
    return {
        'mae': mae,
        'mse': mse,
        'correlation': correlation,
        'tf_top_notes': tf_top_notes,
        'tf_confidences': tf_confidences,
        'onnx_top_notes': onnx_top_notes,
        'onnx_confidences': onnx_confidences,
        'overlap': overlap,
        'overlap_percentage': overlap_percentage,
        'top_note_match': top_note_match
    }

def main():
    """Main function."""
    # Parse command line arguments
    parser = argparse.ArgumentParser(description='Compare TensorFlow and ONNX models')
    parser.add_argument('--num_files', type=int, default=DEFAULT_NUM_FILES,
                        help=f'Number of files to test (default: {DEFAULT_NUM_FILES})')
    args = parser.parse_args()
    
    # Check if the TensorFlow model exists
    if not os.path.exists(TF_MODEL_DIR):
        print(f"Error: TensorFlow model not found at {TF_MODEL_DIR}")
        print("Please run the model training script first to generate the TensorFlow model.")
        return 1
    
    # Check if the ONNX model exists
    if not os.path.exists(ONNX_MODEL_PATH):
        print(f"Error: ONNX model not found at {ONNX_MODEL_PATH}")
        print("Please run the model training script first to generate the ONNX model.")
        return 1
    
    # Load the TensorFlow model
    print(f"Loading TensorFlow model from {TF_MODEL_DIR}...")
    tf_model = load_model(MODEL_DIR)
    if tf_model is None:
        print("Failed to load TensorFlow model")
        return 1
    
    print("TensorFlow model loaded successfully")
    
    # Load the ONNX model
    print(f"Loading ONNX model from {ONNX_MODEL_PATH}...")
    onnx_model = load_onnx_model(ONNX_MODEL_PATH)
    if onnx_model is None:
        print("Failed to load ONNX model")
        return 1
    
    print("ONNX model loaded successfully")
    
    # Find all chord files
    chord_files = []
    for key_dir in glob.glob(os.path.join(TRAINING_DATA_DIR, "key_*")):
        chords_dir = os.path.join(key_dir, "chords")
        if os.path.exists(chords_dir):
            for chord_file in glob.glob(os.path.join(chords_dir, "chord_*.wav")):
                chord_files.append(chord_file)
    
    print(f"Found {len(chord_files)} chord files")
    
    # Limit the number of files to test
    if args.num_files < len(chord_files):
        # Randomly select files
        np.random.seed(42)  # For reproducibility
        chord_files = np.random.choice(chord_files, args.num_files, replace=False)
    
    print(f"Testing on {len(chord_files)} files")
    
    # Results dictionary
    results = {
        'total_files': len(chord_files),
        'mae_avg': 0,
        'mse_avg': 0,
        'correlation_avg': 0,
        'overlap_avg': 0,
        'top_note_match_count': 0,
        'file_results': []
    }
    
    # Process each file
    for chord_file in tqdm(chord_files, desc="Processing files"):
        try:
            # Extract chord name from filename
            chord_name = os.path.basename(chord_file).split('_')[1].split('.')[0]
            
            # Extract features from the audio file
            features = extract_features_from_audio(chord_file)
            
            # Predict with TensorFlow model
            tf_prediction = predict_with_tf_model(tf_model, features)
            
            # Predict with ONNX model
            onnx_prediction = predict_with_onnx_model(onnx_model, features)
            
            # Compare predictions
            comparison = compare_predictions(tf_prediction, onnx_prediction)
            
            # Update results
            results['mae_avg'] += comparison['mae']
            results['mse_avg'] += comparison['mse']
            results['correlation_avg'] += comparison['correlation']
            results['overlap_avg'] += comparison['overlap']
            if comparison['top_note_match']:
                results['top_note_match_count'] += 1
            
            # Store detailed result
            results['file_results'].append({
                'chord_file': chord_file,
                'chord_name': chord_name,
                'mae': comparison['mae'],
                'mse': comparison['mse'],
                'correlation': comparison['correlation'],
                'tf_top_notes': comparison['tf_top_notes'],
                'tf_confidences': comparison['tf_confidences'].tolist(),
                'onnx_top_notes': comparison['onnx_top_notes'],
                'onnx_confidences': comparison['onnx_confidences'].tolist(),
                'overlap': comparison['overlap'],
                'overlap_percentage': comparison['overlap_percentage'],
                'top_note_match': comparison['top_note_match']
            })
            
        except Exception as e:
            print(f"Error processing {chord_file}: {e}")
            results['file_results'].append({
                'chord_file': chord_file,
                'error': str(e)
            })
    
    # Calculate averages
    results['mae_avg'] /= len(chord_files)
    results['mse_avg'] /= len(chord_files)
    results['correlation_avg'] /= len(chord_files)
    results['overlap_avg'] /= len(chord_files)
    results['top_note_match_percentage'] = results['top_note_match_count'] / len(chord_files) * 100
    
    # Print summary
    print("\nComparison Summary:")
    print(f"Total files tested: {results['total_files']}")
    print(f"Mean Absolute Error (avg): {results['mae_avg']:.6f}")
    print(f"Mean Squared Error (avg): {results['mse_avg']:.6f}")
    print(f"Correlation (avg): {results['correlation_avg']:.6f}")
    print(f"Overlap in top 5 notes (avg): {results['overlap_avg']:.2f}/5 ({results['overlap_avg']/5*100:.2f}%)")
    print(f"Top note match: {results['top_note_match_count']}/{results['total_files']} ({results['top_note_match_percentage']:.2f}%)")
    
    # Print detailed results for a few files
    print("\nDetailed results for first 3 files:")
    for i, result in enumerate(results['file_results'][:3]):
        if 'error' in result:
            print(f"\nFile {i+1}: {result['chord_file']} - Error: {result['error']}")
            continue
            
        print(f"\nFile {i+1}: {result['chord_file']} - Chord: {result['chord_name']}")
        print(f"  MAE: {result['mae']:.6f}, MSE: {result['mse']:.6f}, Correlation: {result['correlation']:.6f}")
        print(f"  TensorFlow top notes: {result['tf_top_notes']}")
        print(f"  ONNX top notes: {result['onnx_top_notes']}")
        print(f"  Overlap: {result['overlap']}/5 ({result['overlap_percentage']:.2f}%)")
        print(f"  Top note match: {result['top_note_match']}")
    
    return 0

if __name__ == "__main__":
    sys.exit(main())