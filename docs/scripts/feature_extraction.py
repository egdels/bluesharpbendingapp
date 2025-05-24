#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Feature extraction script for the TensorFlow Magenta harmonica chord detection model.

This script extracts audio features from preprocessed audio files for training
the harmonica chord detection model.

Usage:
    python feature_extraction.py --input_dir=path/to/processed/data --output_dir=path/to/features

Author: BlueSharpBendingApp Team
"""

import os
import argparse
import numpy as np
import librosa
import json
from tqdm import tqdm
import pickle
import glob
import sys
import time
import signal

# Constants
SAMPLE_RATE = 16000  # Expected sample rate of the audio files
DURATION = 1.0  # Expected duration of each audio file in seconds
N_MFCC = 13  # Number of MFCC coefficients to extract
N_CHROMA = 12  # Number of chroma bins
N_CONTRAST = 7  # Number of spectral contrast bands
HOP_LENGTH = 512  # Hop length for feature extraction


def parse_arguments():
    """Parse command line arguments."""
    parser = argparse.ArgumentParser(description='Extract features from audio files for model training')
    parser.add_argument('--input_dir', type=str, required=True, help='Directory containing processed audio files')
    parser.add_argument('--output_dir', type=str, required=True, help='Directory to save extracted features')
    parser.add_argument('--feature_type', type=str, default='all', 
                        choices=['mfcc', 'chroma', 'contrast', 'mel', 'all'],
                        help='Type of features to extract (default: all)')
    parser.add_argument('--include_raw', action='store_true', 
                        help='Include raw audio data in the features')
    parser.add_argument('--include_delta', action='store_true', 
                        help='Include delta and delta-delta features')
    parser.add_argument('--label_type', type=str, default='note', 
                        choices=['note', 'chord', 'key_tuning'],
                        help='Type of labels to extract (default: note, key_tuning extracts only key information)')
    return parser.parse_args()


def extract_features(audio_path, feature_type='all', include_raw=False, include_delta=False):
    """
    Extract features from an audio file.

    Args:
        audio_path: Path to the audio file
        feature_type: Type of features to extract ('mfcc', 'chroma', 'contrast', 'mel', 'all')
        include_raw: Whether to include the raw audio data
        include_delta: Whether to include delta and delta-delta features

    Returns:
        Dictionary containing the extracted features
    """
    try:
        # Load audio file
        y, sr = librosa.load(audio_path, sr=SAMPLE_RATE, mono=True)

        # Ensure the audio is the expected length
        if len(y) < SAMPLE_RATE * DURATION:
            y = np.pad(y, (0, int(SAMPLE_RATE * DURATION) - len(y)))
        elif len(y) > SAMPLE_RATE * DURATION:
            y = y[:int(SAMPLE_RATE * DURATION)]

        features = {}

        # Include raw audio if requested
        if include_raw:
            features['raw'] = y

        # Extract MFCC features
        if feature_type in ['mfcc', 'all']:
            mfcc = librosa.feature.mfcc(y=y, sr=sr, n_mfcc=N_MFCC, hop_length=HOP_LENGTH)
            features['mfcc'] = mfcc

            if include_delta:
                mfcc_delta = librosa.feature.delta(mfcc)
                mfcc_delta2 = librosa.feature.delta(mfcc, order=2)
                features['mfcc_delta'] = mfcc_delta
                features['mfcc_delta2'] = mfcc_delta2

        # Extract chroma features
        if feature_type in ['chroma', 'all']:
            chroma = librosa.feature.chroma_cqt(y=y, sr=sr, hop_length=HOP_LENGTH)
            features['chroma'] = chroma

            if include_delta:
                chroma_delta = librosa.feature.delta(chroma)
                features['chroma_delta'] = chroma_delta

        # Extract spectral contrast features
        if feature_type in ['contrast', 'all']:
            contrast = librosa.feature.spectral_contrast(y=y, sr=sr, hop_length=HOP_LENGTH)
            features['contrast'] = contrast

            if include_delta:
                contrast_delta = librosa.feature.delta(contrast)
                features['contrast_delta'] = contrast_delta

        # Extract mel spectrogram features
        if feature_type in ['mel', 'all']:
            mel = librosa.feature.melspectrogram(y=y, sr=sr, hop_length=HOP_LENGTH)
            features['mel'] = librosa.power_to_db(mel)

        return features

    except Exception as e:
        print(f"Error extracting features from {audio_path}: {e}")
        return None


def save_checkpoint(output_dir, processed_files, features_list, labels_list):
    """
    Save a checkpoint of the current processing state.

    Args:
        output_dir: Directory to save the checkpoint
        processed_files: Set of already processed files
        features_list: List of extracted features
        labels_list: List of extracted labels
    """
    # Save checkpoint data
    checkpoint_file = os.path.join(output_dir, 'checkpoint.json')
    with open(checkpoint_file, 'w') as f:
        json.dump({
            'processed_files': list(processed_files),
            'timestamp': time.time()
        }, f, indent=2)

    # Save current features and labels
    features_file = os.path.join(output_dir, 'features.pkl')
    labels_file = os.path.join(output_dir, 'labels.pkl')

    with open(features_file, 'wb') as f:
        pickle.dump(features_list, f)

    with open(labels_file, 'wb') as f:
        pickle.dump(labels_list, f)

    print(f"Checkpoint saved. {len(processed_files)} files processed so far.")


def get_label_from_path(audio_path, label_type):
    """
    Extract label information from the audio file path.

    Args:
        audio_path: Path to the audio file
        label_type: Type of label to extract ('note', 'chord', 'key_tuning')

    Returns:
        Label information
    """
    # Extract filename and directory information
    filename = os.path.basename(audio_path)
    parent_dir = os.path.basename(os.path.dirname(audio_path))
    grandparent_dir = os.path.basename(os.path.dirname(os.path.dirname(audio_path)))

    # Print debug information
    print(f"Extracting label from: {audio_path}")
    print(f"Filename: {filename}, Parent dir: {parent_dir}, Grandparent dir: {grandparent_dir}")

    # Extract label based on the label type
    if label_type == 'note':
        # Assume filename format: note_C4_blow.wav or similar
        parts = filename.split('_')
        if len(parts) >= 2:
            # Try to extract note information from filename
            for part in parts:
                if part.endswith('.wav'):
                    part = part[:-4]  # Remove .wav extension
                if len(part) >= 2 and part[0] in 'ABCDEFG' and (part[1:].isdigit() or part[1] in ['#', 'b']):
                    return part  # Return note name (e.g., 'C4' or 'C#4')

        # If note information not found in filename, try to extract from directory structure
        if 'single_notes' in audio_path:
            # Try to extract note from filename more aggressively
            for part in filename.split('_'):
                if part.endswith('.wav'):
                    part = part[:-4]  # Remove .wav extension
                # Look for any part that starts with a note name
                for note in ['A', 'B', 'C', 'D', 'E', 'F', 'G']:
                    if part.startswith(note):
                        return part  # Return the part as the note name

        # If still not found, return unknown
        print(f"Warning: Could not extract note from {audio_path}")
        return 'unknown'

    elif label_type == 'chord':
        # Handle various chord filename formats:
        # 1. chord_C4-E4-G4.wav (basic format)
        # 2. chord_C4-E4-G4_1.wav (with index)
        # 3. chord_C4-E4-G4_1_0_noise0.05.wav (with additional parameters)
        # 4. chord_C4-E4-G4_1_pitch-1.wav (with negative parameters)

        # Extract the chord part from the filename
        if filename.startswith('chord_'):
            # Remove 'chord_' prefix
            remaining = filename[6:]

            # If there's a hyphen, it's likely a chord with individual notes
            if '-' in remaining:
                # Extract the chord notes part (everything before the first underscore or .wav)
                if '_' in remaining:
                    chord_part = remaining.split('_')[0]
                else:
                    chord_part = remaining.split('.')[0]

                print(f"Extracted chord: {chord_part}")
                return chord_part

            # For traditional chord names without hyphens
            if '_' in remaining:
                chord_part = remaining.split('_')[0]
            else:
                chord_part = remaining.split('.')[0]

            print(f"Extracted chord: {chord_part}")
            return chord_part

        # If the filename doesn't start with 'chord_', try to find a chord pattern
        # Look for patterns like 'B4-D5-F#5' in the filename
        parts = filename.replace('.wav', '').split('_')
        for part in parts:
            if '-' in part and any(note in part for note in ['A', 'B', 'C', 'D', 'E', 'F', 'G']):
                print(f"Extracted chord: {part}")
                return part

        # If still not found, check if the file is in a 'chords' directory
        if 'chords' in audio_path:
            # Try one more approach - look for any part with a note pattern
            for part in parts:
                for note in ['A', 'B', 'C', 'D', 'E', 'F', 'G']:
                    if part.startswith(note) and (len(part) > 1 and (part[1].isdigit() or part[1] in ['#', 'b'])):
                        # This might be a single note in a chord
                        print(f"Extracted single note from chord: {part}")
                        return part

        # If still not found, return unknown
        print(f"Warning: Could not extract chord from {audio_path}")
        return 'unknown'

    elif label_type == 'key_tuning':
        # Extract only key from directory structure
        if grandparent_dir.startswith('key_'):
            key = grandparent_dir[4:]  # Remove 'key_' prefix
            return {'key': key}
        elif parent_dir.startswith('key_'):
            key = parent_dir[4:]  # Remove 'key_' prefix
            return {'key': key}

        # If key information not found, try to extract from filename
        for part in filename.split('_'):
            if part.startswith('key_'):
                key = part[4:]  # Remove 'key_' prefix
                return {'key': key}

        # If still not found, return unknown
        print(f"Warning: Could not extract key from {audio_path}")
        return {'key': 'unknown'}

    # Default case
    print(f"Warning: Unknown label type {label_type} for {audio_path}")
    return 'unknown'


def process_directory(input_dir, output_dir, feature_type, include_raw, include_delta, label_type):
    """
    Process all audio files in the input directory and its subdirectories.
    Includes checkpointing to allow resuming from interruptions.

    Args:
        input_dir: Directory containing processed audio files
        output_dir: Directory to save extracted features
        feature_type: Type of features to extract
        include_raw: Whether to include the raw audio data
        include_delta: Whether to include delta and delta-delta features
        label_type: Type of labels to extract
    """
    # Create output directory if it doesn't exist
    os.makedirs(output_dir, exist_ok=True)

    # Find all audio files
    audio_files = []
    for root, _, files in os.walk(input_dir):
        for file in files:
            if file.endswith('.wav'):
                audio_files.append(os.path.join(root, file))

    print(f"Found {len(audio_files)} audio files to process.")

    # Check for checkpoint file
    checkpoint_file = os.path.join(output_dir, 'checkpoint.json')
    processed_files = set()
    features_list = []
    labels_list = []

    if os.path.exists(checkpoint_file):
        try:
            with open(checkpoint_file, 'r') as f:
                checkpoint_data = json.load(f)
                processed_files = set(checkpoint_data.get('processed_files', []))
                # Load existing features and labels if available
                if os.path.exists(os.path.join(output_dir, 'features.pkl')) and os.path.exists(os.path.join(output_dir, 'labels.pkl')):
                    with open(os.path.join(output_dir, 'features.pkl'), 'rb') as f_feat:
                        features_list = pickle.load(f_feat)
                    with open(os.path.join(output_dir, 'labels.pkl'), 'rb') as f_lab:
                        labels_list = pickle.load(f_lab)
                    print(f"Loaded {len(features_list)} features from checkpoint.")
            print(f"Resuming from checkpoint. {len(processed_files)} files already processed.")
        except Exception as e:
            print(f"Error loading checkpoint: {e}. Starting from scratch.")
            processed_files = set()

    # Filter out already processed files
    files_to_process = [f for f in audio_files if f not in processed_files]
    print(f"Processing {len(files_to_process)} new files.")

    # Process each file
    try:
        for audio_path in tqdm(files_to_process, desc="Extracting features"):
            # Extract features
            features = extract_features(audio_path, feature_type, include_raw, include_delta)

            if features is not None:
                # Extract label
                label = get_label_from_path(audio_path, label_type)

                # Add to lists
                features_list.append(features)
                labels_list.append(label)

                # Update processed files
                processed_files.add(audio_path)

                # Save checkpoint periodically (every 10 files)
                if len(processed_files) % 10 == 0:
                    save_checkpoint(output_dir, processed_files, features_list, labels_list)
    except KeyboardInterrupt:
        print("\nProcess interrupted by user. Saving checkpoint...")
        save_checkpoint(output_dir, processed_files, features_list, labels_list)
        print("Checkpoint saved. You can resume processing later.")
        sys.exit(1)

    print(f"Extracted features from {len(features_list)} files.")

    # Save features and labels
    features_file = os.path.join(output_dir, 'features.pkl')
    labels_file = os.path.join(output_dir, 'labels.pkl')

    with open(features_file, 'wb') as f:
        pickle.dump(features_list, f)

    with open(labels_file, 'wb') as f:
        pickle.dump(labels_list, f)

    print(f"Features saved to {features_file}")
    print(f"Labels saved to {labels_file}")

    # Save metadata
    metadata = {
        'num_samples': len(features_list),
        'feature_type': feature_type,
        'include_raw': include_raw,
        'include_delta': include_delta,
        'label_type': label_type,
        'sample_rate': SAMPLE_RATE,
        'duration': DURATION,
        'n_mfcc': N_MFCC,
        'n_chroma': N_CHROMA,
        'n_contrast': N_CONTRAST,
        'hop_length': HOP_LENGTH
    }

    metadata_file = os.path.join(output_dir, 'features_metadata.json')
    with open(metadata_file, 'w') as f:
        json.dump(metadata, f, indent=2)

    print(f"Metadata saved to {metadata_file}")


def process_dataset(input_dir, output_dir, feature_type, include_raw, include_delta, label_type):
    """
    Process the dataset, handling train and validation sets separately.

    Args:
        input_dir: Directory containing processed audio files
        output_dir: Directory to save extracted features
        feature_type: Type of features to extract
        include_raw: Whether to include the raw audio data
        include_delta: Whether to include delta and delta-delta features
        label_type: Type of labels to extract
    """
    # Create output directory if it doesn't exist
    os.makedirs(output_dir, exist_ok=True)

    # Check if the input directory has train and validation subdirectories
    train_dir = os.path.join(input_dir, 'train')
    val_dir = os.path.join(input_dir, 'validation')

    if os.path.exists(train_dir) and os.path.exists(val_dir):
        # Process train and validation sets separately
        print("Processing training set...")
        train_output_dir = os.path.join(output_dir, 'train')
        process_directory(train_dir, train_output_dir, feature_type, include_raw, include_delta, label_type)

        print("\nProcessing validation set...")
        val_output_dir = os.path.join(output_dir, 'validation')
        process_directory(val_dir, val_output_dir, feature_type, include_raw, include_delta, label_type)
    else:
        # Process the entire directory as a single set
        print("Processing all files as a single set...")
        process_directory(input_dir, output_dir, feature_type, include_raw, include_delta, label_type)


def signal_handler(sig, frame):
    """Handle SIGINT (Ctrl+C) signals."""
    print("\nProcess interrupted by user. Exiting gracefully...")
    sys.exit(0)


def main():
    """Main function."""
    # Register signal handler for SIGINT (Ctrl+C)
    signal.signal(signal.SIGINT, signal_handler)

    args = parse_arguments()

    # Check if input directory exists
    if not os.path.exists(args.input_dir):
        print(f"Error: Input directory {args.input_dir} does not exist.")
        return

    try:
        # Process the dataset
        process_dataset(
            args.input_dir,
            args.output_dir,
            args.feature_type,
            args.include_raw,
            args.include_delta,
            args.label_type
        )

        print("Feature extraction completed successfully!")
    except KeyboardInterrupt:
        # This is a fallback in case the signal handler doesn't catch the interrupt
        print("\nProcess interrupted by user. Exiting gracefully...")
        sys.exit(0)


if __name__ == "__main__":
    main()
