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

    # Extract label based on the label type
    if label_type == 'note':
        # Assume filename format: note_C4_blow.wav or similar
        parts = filename.split('_')
        if len(parts) >= 2:
            # Try to extract note information from filename
            for part in parts:
                if part.endswith('.wav'):
                    part = part[:-4]  # Remove .wav extension
                if len(part) >= 2 and part[0] in 'ABCDEFG' and part[1:].isdigit():
                    return part  # Return note name (e.g., 'C4')

        # If note information not found in filename, return unknown
        return 'unknown'

    elif label_type == 'chord':
        # Assume filename format: chord_Cmaj_1.wav or chord_C4-E4-G4_1.wav or similar
        parts = filename.split('_')
        if len(parts) >= 2 and parts[0] == 'chord':
            return parts[1]  # Return chord name (e.g., 'Cmaj' or 'C4-E4-G4')

        # If chord information not found, return unknown
        return 'unknown'

    elif label_type == 'key_tuning':
        # Extract only key from directory structure
        if grandparent_dir.startswith('key_'):
            key = grandparent_dir[4:]  # Remove 'key_' prefix
            return {'key': key}

        # If key information not found, return unknown
        return {'key': 'unknown'}

    # Default case
    return 'unknown'


def process_directory(input_dir, output_dir, feature_type, include_raw, include_delta, label_type):
    """
    Process all audio files in the input directory and its subdirectories.

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

    # Process each file
    features_list = []
    labels_list = []

    for audio_path in tqdm(audio_files, desc="Extracting features"):
        # Extract features
        features = extract_features(audio_path, feature_type, include_raw, include_delta)

        if features is not None:
            # Extract label
            label = get_label_from_path(audio_path, label_type)

            # Add to lists
            features_list.append(features)
            labels_list.append(label)

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


def main():
    """Main function."""
    args = parse_arguments()

    # Check if input directory exists
    if not os.path.exists(args.input_dir):
        print(f"Error: Input directory {args.input_dir} does not exist.")
        return

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


if __name__ == "__main__":
    main()
