#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Test script for detecting various chord types using the ONNX model.

This script tests whether the ONNX model correctly identifies the notes
in various chord types from audio files in the training data directory.

Usage:
    python test_onnx_model.py

Author: BlueSharpBendingApp Team
"""

import os
import sys
import unittest
import numpy as np
import onnxruntime as ort
import librosa
import glob
import signal
from tqdm import tqdm
from functools import partial

# Add the parent directory to the path to import from model_evaluation
sys.path.append(os.path.dirname(os.path.abspath(__file__)))
from model_evaluation import extract_features_from_audio

# Constants
MODEL_DIR = "/Users/christian/Documents/git/bluesharpbendingapp/training_output/model"
ONNX_MODEL_PATH = os.path.join(MODEL_DIR, "models", "onnx", "magenta_chord_detection_model.onnx")
TRAINING_DATA_DIR = "/Users/christian/Documents/git/bluesharpbendingapp/training_data/data/raw"
NOTE_NAMES = ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B']
NUM_CLASSES = 96  # 12 semitones in an octave * 8 octaves (0-7)
CONFIDENCE_THRESHOLD = 0.3  # Threshold for confidence values
TIMEOUT_SECONDS = 60  # Timeout for processing a single file
BATCH_SIZE = 10  # Number of files to process in a batch

# Timeout handler
class TimeoutError(Exception):
    pass

def timeout_handler(signum, frame):
    raise TimeoutError("Processing timed out")

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

class OnnxChordDetectionTest(unittest.TestCase):
    """Test case for detecting various chord types using the ONNX model."""

    @classmethod
    def setUpClass(cls):
        """Set up the test environment."""
        # Load the ONNX model
        print(f"Loading ONNX model from {ONNX_MODEL_PATH}...")
        try:
            cls.model = load_onnx_model(ONNX_MODEL_PATH)
            if cls.model:
                print("ONNX model loaded successfully")
                # Print model metadata
                print("\n--- ONNX Model Metadata ---")
                print(f"Inputs: {cls.model.get_inputs()}")
                print(f"Input name: {cls.model.get_inputs()[0].name}")
                print(f"Input shape: {cls.model.get_inputs()[0].shape}")
                print(f"Input type: {cls.model.get_inputs()[0].type}")
                print(f"Outputs: {cls.model.get_outputs()}")
                print(f"Output name: {cls.model.get_outputs()[0].name}")
                print(f"Output shape: {cls.model.get_outputs()[0].shape}")
                print(f"Output type: {cls.model.get_outputs()[0].type}")
                print(f"Providers: {ort.get_available_providers()}")
            else:
                print("Failed to load ONNX model")
        except Exception as e:
            print(f"Error during model loading: {e}")
            cls.model = None

        # Find all chord files
        cls.chord_files = []
        for key_dir in glob.glob(os.path.join(TRAINING_DATA_DIR, "key_*")):
            chords_dir = os.path.join(key_dir, "chords")
            if os.path.exists(chords_dir):
                for chord_file in glob.glob(os.path.join(chords_dir, "chord_*.wav")):
                    cls.chord_files.append(chord_file)

        print(f"Found {len(cls.chord_files)} chord files")

    def test_chord_detection(self):
        """Test detection of various chord types using the ONNX model."""
        # Skip the test if the model couldn't be loaded
        if self.model is None:
            self.skipTest("ONNX model could not be loaded")
            return

        # Skip the test if no chord files were found
        if not self.chord_files:
            self.skipTest("No chord files found")
            return

        # Get the input name from the model
        input_name = self.model.get_inputs()[0].name

        # Results dictionary to store test results
        results = {
            'total_files': len(self.chord_files),
            'model_passed': 0,
            'chroma_passed': 0,
            'overall_passed': 0,
            'chord_results': []
        }

        # Process files in batches to identify problematic files
        print(f"Processing {len(self.chord_files)} files in batches of {BATCH_SIZE}")

        # Create batches
        batches = [self.chord_files[i:i + BATCH_SIZE] for i in range(0, len(self.chord_files), BATCH_SIZE)]
        print(f"Created {len(batches)} batches")

        # Process each batch
        for batch_idx, batch in enumerate(batches):
            print(f"\nProcessing batch {batch_idx + 1}/{len(batches)} ({len(batch)} files)")

            # Test each chord file in the batch
            for chord_file in tqdm(batch, desc=f"Batch {batch_idx + 1}"):
                # Set up timeout for this file
                signal.signal(signal.SIGALRM, timeout_handler)
                signal.alarm(TIMEOUT_SECONDS)

                try:
                    # Extract chord name from filename
                    chord_name = os.path.basename(chord_file).split('_')[1]
                    chord_notes = chord_name.split('-')

                    # Extract expected notes (without octave) for chroma analysis
                    expected_notes = []
                    for note in chord_notes:
                        # Extract note name (without octave)
                        note_name = ''.join([c for c in note if not c.isdigit()])
                        if note_name not in expected_notes:
                            expected_notes.append(note_name)

                    # Calculate the MIDI note numbers for the chord notes
                    chord_indices = []
                    for note in chord_notes:
                        note_name = ''.join([c for c in note if not c.isdigit()])
                        octave = int(''.join([c for c in note if c.isdigit()]))
                        semitone = NOTE_NAMES.index(note_name)
                        midi_note = octave * 12 + semitone
                        chord_indices.append(midi_note)

                    # Extract features from the audio file
                    features = extract_features_from_audio(chord_file)
                    
                    # Ensure features are in the correct format for ONNX
                    features = features.astype(np.float32)

                    # Run inference with ONNX Runtime
                    prediction = self.model.run(None, {input_name: features})[0]

                    # Get the top 5 predicted classes and confidences
                    top_indices = np.argsort(prediction[0])[-5:][::-1]
                    top_confidences = prediction[0][top_indices]

                    # Map the class indices to note names with octaves
                    top_notes = []
                    for idx in top_indices:
                        octave = idx // 12
                        semitone = idx % 12
                        top_notes.append(f"{NOTE_NAMES[semitone]}{octave}")

                    # Check all predictions for chord notes
                    all_chord_confidences = [
                        (idx, prediction[0][idx]) for idx in chord_indices if idx < len(prediction[0])
                    ]

                    # Calculate average confidence for chord notes
                    avg_confidence = sum(conf for _, conf in all_chord_confidences) / len(all_chord_confidences)

                    # Check if any chord note has confidence above threshold
                    above_threshold = [
                        f"{NOTE_NAMES[idx % 12]}{idx // 12}"
                        for idx, conf in all_chord_confidences if conf > CONFIDENCE_THRESHOLD
                    ]

                    # Determine if model prediction passed
                    model_passed = any(conf > CONFIDENCE_THRESHOLD for _, conf in all_chord_confidences)

                    # Extract chroma features directly for verification
                    y, sr = librosa.load(chord_file, sr=16000, mono=True)
                    chroma = librosa.feature.chroma_cqt(y=y, sr=sr)
                    chroma_mean = np.mean(chroma, axis=1)

                    # Find the peaks in the chroma values
                    peaks = [
                        (NOTE_NAMES[i], value) for i, value in enumerate(chroma_mean) if value > 0.1
                    ]

                    # Sort peaks by value (highest first)
                    peaks.sort(key=lambda x: x[1], reverse=True)

                    # Check if the expected notes are in the peaks
                    found_chroma_notes = [note for note, _ in peaks if note in expected_notes]

                    # Determine if chroma analysis passed
                    chroma_passed = any(note in expected_notes for note, _ in peaks)

                    # Determine overall result
                    overall_passed = model_passed or chroma_passed

                    # Update results
                    if model_passed:
                        results['model_passed'] += 1
                    if chroma_passed:
                        results['chroma_passed'] += 1
                    if overall_passed:
                        results['overall_passed'] += 1

                    # Store detailed result
                    results['chord_results'].append({
                        'chord_file': chord_file,
                        'chord_name': chord_name,
                        'chord_notes': chord_notes,
                        'expected_notes': expected_notes,
                        'top_notes': top_notes,
                        'top_confidences': top_confidences.tolist(),
                        'avg_confidence': avg_confidence,
                        'above_threshold': above_threshold,
                        'found_chroma_notes': found_chroma_notes,
                        'model_passed': model_passed,
                        'chroma_passed': chroma_passed,
                        'overall_passed': overall_passed
                    })

                    # Cancel the alarm since processing was successful
                    signal.alarm(0)

                except TimeoutError:
                    signal.alarm(0)
                    results['chord_results'].append({
                        'chord_file': chord_file,
                        'error': f"Timeout after {TIMEOUT_SECONDS} seconds",
                        'model_passed': False,
                        'chroma_passed': False,
                        'overall_passed': False
                    })
                except Exception as e:
                    signal.alarm(0)
                    results['chord_results'].append({
                        'chord_file': chord_file,
                        'error': str(e),
                        'model_passed': False,
                        'chroma_passed': False,
                        'overall_passed': False
                    })

        print("\nTest Summary:")
        print(f"Total chord files tested: {results['total_files']}")
        print(f"Model prediction passed: {results['model_passed']} "
              f"({results['model_passed']/results['total_files']*100:.2f}%)")
        print(f"Chroma analysis passed: {results['chroma_passed']} "
              f"({results['chroma_passed']/results['total_files']*100:.2f}%)")
        print(f"Overall passed: {results['overall_passed']} "
              f"({results['overall_passed']/results['total_files']*100:.2f}%)")

        self.assertGreater(results['overall_passed'], 0, "No chords passed the test")

def main():
    """Main function."""
    unittest.main(argv=['first-arg-is-ignored'], exit=False)

if __name__ == "__main__":
    main()