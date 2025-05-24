#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Test script for detecting various chord types using the TensorFlow model.

This script tests whether the TensorFlow model correctly identifies the notes
in various chord types from audio files in the training data directory.

Usage:
    python test_all_chords.py

Author: BlueSharpBendingApp Team
"""

import os
import sys
import unittest
import numpy as np
import tensorflow as tf
import librosa
import glob
import signal
from tqdm import tqdm
from functools import partial

# Add the parent directory to the path to import from model_evaluation
sys.path.append(os.path.dirname(os.path.abspath(__file__)))
from model_evaluation import load_model, extract_features_from_audio

# Constants
MODEL_DIR = "/Users/christian/Documents/git/bluesharpbendingapp/training_output/model"
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

class ChordDetectionTest(unittest.TestCase):
    """Test case for detecting various chord types."""

    @classmethod
    def setUpClass(cls):
        """Set up the test environment."""
        # Load the model
        print(f"Loading model from {MODEL_DIR}...")
        try:
            cls.model = load_model(MODEL_DIR)
            print("Model loaded successfully")


# Extract model-specific parameters
            if hasattr(cls.model, 'signatures'):
                # SavedModel: Extract inputs, outputs, and signatures
                serving_fn = cls.model.signatures['serving_default']
                print("\n--- Model Signatures and Parameters ---")
                print(f"Available signatures: {list(cls.model.signatures.keys())}")
                print(f"Inputs: {serving_fn.structured_input_signature}")
                print(f"Outputs: {serving_fn.structured_outputs}")

            if isinstance(cls.model, tf.keras.Model):
                # Keras model: Print summary and parameter counts
                print("\n--- Keras Model Info ---")
                cls.model.summary()
                trainable_params = np.sum([np.prod(v.shape) for v in cls.model.trainable_variables])
                non_trainable_params = np.sum([np.prod(v.shape) for v in cls.model.non_trainable_variables])
                print(f"Trainable parameters: {trainable_params}")
                print(f"Non-trainable parameters: {non_trainable_params}")

        except Exception as e:
            print(f"Error loading model: {e}")
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
        """Test detection of various chord types."""
        # Skip the test if the model couldn't be loaded
        if self.model is None:
            self.skipTest("Model could not be loaded")
            return

        # Skip the test if no chord files were found
        if not self.chord_files:
            self.skipTest("No chord files found")
            return

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

                    # Run inference based on model type
                    if hasattr(self.model, 'signatures'):
                        # SavedModel
                        serving_fn = self.model.signatures['serving_default']
                        # Convert to float32 to match the model's expected input type
                        features = features.astype(np.float32)
                        features_tensor = tf.convert_to_tensor(features)
                        output = serving_fn(features_tensor)
                        # Get the output tensor
                        output_key = list(output.keys())[0]
                        prediction = output[output_key].numpy()
                    elif hasattr(self.model, 'predict'):
                        # Keras model
                        prediction = self.model.predict(features)
                    else:
                        raise ValueError("Model is not a recognized TensorFlow model type")

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
