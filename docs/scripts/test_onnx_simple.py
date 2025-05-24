#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Simple test script for using the ONNX model to detect chords.

This script demonstrates how to use the ONNX model to detect chords in audio files.
It loads the ONNX model and runs inference on a single audio file.

Usage:
    python test_onnx_simple.py [audio_file]

If no audio file is provided, the script will use a default test file.

Author: BlueSharpBendingApp Team
"""

import os
import sys
import numpy as np
import onnxruntime as ort
import librosa
import argparse

# Constants
MODEL_DIR = "/Users/christian/Documents/git/bluesharpbendingapp/training_output/model"
ONNX_MODEL_PATH = os.path.join(MODEL_DIR, "models", "onnx", "magenta_chord_detection_model.onnx")
DEFAULT_TEST_FILE = "/Users/christian/Documents/git/bluesharpbendingapp/training_data/data/raw/key_C/chords/chord_C4-E4-G4.wav"
NOTE_NAMES = ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B']
SAMPLE_RATE = 16000  # Expected sample rate of the audio files

def extract_features(audio_file):
    """
    Extract features from an audio file.
    
    Args:
        audio_file: Path to the audio file
        
    Returns:
        Extracted features as a numpy array
    """
    # Load audio file
    y, sr = librosa.load(audio_file, sr=SAMPLE_RATE, mono=True)
    
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

def predict_chord_onnx(model, audio_file):
    """
    Predict the chord from an audio file using the ONNX model.
    
    Args:
        model: ONNX Runtime InferenceSession
        audio_file: Path to the audio file
        
    Returns:
        Predicted notes and confidences
    """
    # Extract features
    features = extract_features(audio_file)
    
    # Get the input name from the model
    input_name = model.get_inputs()[0].name
    
    # Run inference with ONNX Runtime
    prediction = model.run(None, {input_name: features})[0]
    
    # Get the top 5 predicted notes
    top_indices = np.argsort(prediction[0])[-5:][::-1]
    top_confidences = prediction[0][top_indices]
    
    # Map indices to note names
    top_notes = []
    for idx in top_indices:
        octave = idx // 12
        semitone = idx % 12
        top_notes.append(f"{NOTE_NAMES[semitone]}{octave}")
    
    return top_notes, top_confidences

def main():
    """Main function."""
    # Parse command line arguments
    parser = argparse.ArgumentParser(description='Test ONNX model for chord detection')
    parser.add_argument('audio_file', nargs='?', default=DEFAULT_TEST_FILE,
                        help='Path to audio file to test (default: a C major chord)')
    args = parser.parse_args()
    
    # Check if the ONNX model exists
    if not os.path.exists(ONNX_MODEL_PATH):
        print(f"Error: ONNX model not found at {ONNX_MODEL_PATH}")
        print("Please run the model training script first to generate the ONNX model.")
        return 1
    
    # Check if the audio file exists
    if not os.path.exists(args.audio_file):
        print(f"Error: Audio file not found at {args.audio_file}")
        return 1
    
    # Load the ONNX model
    print(f"Loading ONNX model from {ONNX_MODEL_PATH}...")
    model = load_onnx_model(ONNX_MODEL_PATH)
    if model is None:
        print("Failed to load ONNX model")
        return 1
    
    print("ONNX model loaded successfully")
    
    # Print model metadata
    print("\n--- ONNX Model Metadata ---")
    print(f"Inputs: {model.get_inputs()}")
    print(f"Input name: {model.get_inputs()[0].name}")
    print(f"Input shape: {model.get_inputs()[0].shape}")
    print(f"Input type: {model.get_inputs()[0].type}")
    print(f"Outputs: {model.get_outputs()}")
    print(f"Output name: {model.get_outputs()[0].name}")
    print(f"Output shape: {model.get_outputs()[0].shape}")
    print(f"Output type: {model.get_outputs()[0].type}")
    print(f"Providers: {ort.get_available_providers()}")
    
    # Predict chord
    print(f"\nPredicting chord for {args.audio_file}...")
    top_notes, top_confidences = predict_chord_onnx(model, args.audio_file)
    
    # Print results
    print("\n--- Prediction Results ---")
    print("Top 5 predicted notes:")
    for i, (note, confidence) in enumerate(zip(top_notes, top_confidences)):
        print(f"{i+1}. {note} (confidence: {confidence:.4f})")
    
    # Extract expected chord from filename (if available)
    filename = os.path.basename(args.audio_file)
    if filename.startswith("chord_") and "_" in filename:
        chord_name = filename.split("_")[1].split(".")[0]
        print(f"\nExpected chord: {chord_name}")
    
    return 0

if __name__ == "__main__":
    sys.exit(main())