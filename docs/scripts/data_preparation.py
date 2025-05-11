#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Data preparation script for the TensorFlow Magenta harmonica chord detection model.

This script handles the collection, organization, and preprocessing of audio data
for training the harmonica chord detection model.

Usage:
    python data_preparation.py --input_dir=path/to/raw/data --output_dir=path/to/processed/data

Author: BlueSharpBendingApp Team
"""

import os
import argparse
import shutil
import numpy as np
import librosa
import soundfile as sf
from tqdm import tqdm
import json
import random

# Constants
SAMPLE_RATE = 16000  # Target sample rate for all audio files
DURATION = 1.0  # Duration in seconds for each processed audio clip
SILENCE_THRESHOLD = 0.01  # Threshold for detecting silence


def parse_arguments():
    """Parse command line arguments."""
    parser = argparse.ArgumentParser(description='Prepare audio data for harmonica chord detection model training')
    parser.add_argument('--input_dir', type=str, required=True, help='Directory containing raw audio files')
    parser.add_argument('--output_dir', type=str, required=True, help='Directory to save processed audio files')
    parser.add_argument('--metadata_file', type=str, default=None, 
                        help='JSON file with metadata for audio files (optional)')
    parser.add_argument('--generate_metadata', action='store_true',
                        help='Generate metadata from directory structure and filenames')
    parser.add_argument('--metadata_output', type=str, default=None,
                        help='File to save generated metadata (default: metadata.json in output_dir)')
    parser.add_argument('--split_ratio', type=float, default=0.8, 
                        help='Train/validation split ratio (default: 0.8)')
    parser.add_argument('--normalize', action='store_true', help='Normalize audio files')
    parser.add_argument('--augment', action='store_true', help='Apply data augmentation')
    parser.add_argument('--organize', action='store_true', 
                        help='Organize files by key based on metadata')
    return parser.parse_args()


def load_metadata(metadata_file):
    """Load metadata from a JSON file."""
    if not metadata_file or not os.path.exists(metadata_file):
        print("No metadata file provided or file doesn't exist.")
        return {}

    try:
        with open(metadata_file, 'r') as f:
            metadata = json.load(f)
        print(f"Loaded metadata for {len(metadata)} audio files.")
        return metadata
    except Exception as e:
        print(f"Error loading metadata: {e}")
        return {}


def generate_metadata_from_directory(input_dir):
    """
    Generate metadata from directory structure and filenames.

    Expected directory structure:
    data/raw/
      ├── key_C/
      │   ├── single_notes/
      │   ├── chords/
      │   └── bends/
      ├── key_A/
      │   ├── single_notes/
      │   └── ...
      └── ...

    Expected filename convention:
    - For single notes: note_[NoteName]_[PlayingTechnique].wav
      Example: note_C4_blow.wav (Note C4, blown)
    - For chords: chord_[ChordName]_[Number].wav
      Example: chord_Cmaj_1.wav (C major chord, first recording)
    """
    metadata = {}

    for root, _, files in os.walk(input_dir):
        # Extract key from directory path
        key = None
        for part in root.split(os.sep):
            if part.startswith('key_'):
                key = part[4:]  # Remove 'key_' prefix

        # Extract technique from directory path
        technique = None
        if 'bends' in root.split(os.sep):
            technique = 'bent'
        elif 'single_notes' in root.split(os.sep):
            technique = 'normal'

        for file in files:
            if not file.endswith(('.wav', '.mp3', '.flac', '.ogg')):
                continue

            file_metadata = {}

            # Add key if found
            if key:
                file_metadata['key'] = key

            # Extract note/chord and technique from filename
            if file.startswith('note_'):
                # Format: note_[NoteName]_[PlayingTechnique].wav
                parts = os.path.splitext(file)[0].split('_')
                if len(parts) >= 3:
                    file_metadata['note'] = parts[1]
                    file_metadata['technique'] = parts[2] if not technique else technique

            elif file.startswith('chord_'):
                # Format: chord_[ChordName]_[Number].wav
                parts = os.path.splitext(file)[0].split('_')
                if len(parts) >= 3:
                    chord_name = parts[1]
                    file_metadata['chord'] = chord_name
                    file_metadata['technique'] = 'normal' if not technique else technique

            # If we have any metadata, add it to the dictionary
            if file_metadata:
                metadata[file] = file_metadata

    print(f"Generated metadata for {len(metadata)} audio files from directory structure.")
    return metadata


def create_directory_structure(output_dir):
    """Create the directory structure for processed data."""
    os.makedirs(output_dir, exist_ok=True)

    # Create train and validation directories
    train_dir = os.path.join(output_dir, 'train')
    val_dir = os.path.join(output_dir, 'validation')
    os.makedirs(train_dir, exist_ok=True)
    os.makedirs(val_dir, exist_ok=True)

    # Create directories for different harmonica keys only (no tunings)
    keys = ['C', 'A', 'D', 'G', 'F', 'Bb']

    for key in keys:
        os.makedirs(os.path.join(train_dir, f"key_{key}"), exist_ok=True)
        os.makedirs(os.path.join(val_dir, f"key_{key}"), exist_ok=True)

    # Create a directory for unknown key
    os.makedirs(os.path.join(train_dir, 'unknown'), exist_ok=True)
    os.makedirs(os.path.join(val_dir, 'unknown'), exist_ok=True)

    print(f"Created directory structure in {output_dir}")


def is_silent(audio, threshold=SILENCE_THRESHOLD):
    """Check if an audio clip is silent."""
    return np.max(np.abs(audio)) < threshold


def preprocess_audio(file_path, output_path, normalize=True):
    """
    Preprocess an audio file:
    1. Load and resample to target sample rate
    2. Trim silence from beginning and end
    3. Split into segments of specified duration
    4. Normalize if requested
    """
    try:
        # Load audio file
        audio, sr = librosa.load(file_path, sr=SAMPLE_RATE, mono=True)

        # Trim silence from beginning and end
        audio, _ = librosa.effects.trim(audio, top_db=20)

        # If audio is too short, pad it
        if len(audio) < int(DURATION * SAMPLE_RATE):
            audio = np.pad(audio, (0, int(DURATION * SAMPLE_RATE) - len(audio)))

        # Split into segments
        segments = []
        for i in range(0, len(audio) - int(DURATION * SAMPLE_RATE) + 1, int(DURATION * SAMPLE_RATE)):
            segment = audio[i:i + int(DURATION * SAMPLE_RATE)]
            if not is_silent(segment):
                segments.append(segment)

        # If no segments were created (e.g., all silent), use the original audio
        if not segments:
            segments = [audio[:int(DURATION * SAMPLE_RATE)]]

        # Normalize if requested
        if normalize:
            segments = [librosa.util.normalize(segment) for segment in segments]

        # Save segments
        for i, segment in enumerate(segments):
            segment_path = f"{output_path}_{i}.wav"
            sf.write(segment_path, segment, SAMPLE_RATE)

        return len(segments)
    except Exception as e:
        print(f"Error processing {file_path}: {e}")
        return 0


def apply_augmentation(audio_path, output_dir):
    """
    Apply data augmentation techniques to an audio file:
    1. Pitch shifting
    2. Time stretching
    3. Adding noise
    """
    try:
        # Load audio
        audio, sr = librosa.load(audio_path, sr=SAMPLE_RATE, mono=True)

        # Base filename without extension
        base_name = os.path.splitext(os.path.basename(audio_path))[0]

        # 1. Pitch shifting (up and down by 1 semitone)
        for n_steps in [-1, 1]:
            shifted_audio = librosa.effects.pitch_shift(audio, sr=sr, n_steps=n_steps)
            output_path = os.path.join(output_dir, f"{base_name}_pitch{n_steps}.wav")
            sf.write(output_path, shifted_audio, sr)

        # 2. Time stretching (faster and slower by 10%)
        for rate in [0.9, 1.1]:
            stretched_audio = librosa.effects.time_stretch(audio, rate=rate)
            # Ensure the audio is the right length
            if len(stretched_audio) > int(DURATION * sr):
                stretched_audio = stretched_audio[:int(DURATION * sr)]
            else:
                stretched_audio = np.pad(stretched_audio, (0, int(DURATION * sr) - len(stretched_audio)))

            output_path = os.path.join(output_dir, f"{base_name}_stretch{rate}.wav")
            sf.write(output_path, stretched_audio, sr)

        # 3. Adding noise (at 5% and 10% levels)
        for noise_level in [0.05, 0.1]:
            noise = np.random.normal(0, noise_level, size=audio.shape)
            noisy_audio = audio + noise
            # Ensure the audio doesn't clip
            noisy_audio = np.clip(noisy_audio, -1.0, 1.0)
            output_path = os.path.join(output_dir, f"{base_name}_noise{noise_level}.wav")
            sf.write(output_path, noisy_audio, sr)

        return 5  # Number of augmented files created
    except Exception as e:
        print(f"Error augmenting {audio_path}: {e}")
        return 0


def process_files(input_dir, output_dir, metadata, split_ratio, normalize, augment, organize):
    """Process all audio files in the input directory."""
    # Create directory structure
    create_directory_structure(output_dir)

    # Get all audio files
    audio_files = []
    for root, _, files in os.walk(input_dir):
        for file in files:
            if file.endswith(('.wav', '.mp3', '.flac', '.ogg')):
                audio_files.append(os.path.join(root, file))

    print(f"Found {len(audio_files)} audio files to process.")

    # Shuffle files to ensure random train/validation split
    random.shuffle(audio_files)

    # Calculate split index
    split_idx = int(len(audio_files) * split_ratio)

    # Process files
    train_count = 0
    val_count = 0
    augmented_count = 0

    for i, file_path in enumerate(tqdm(audio_files, desc="Processing audio files")):
        # Determine if this file goes to train or validation set
        is_train = i < split_idx
        set_dir = 'train' if is_train else 'validation'

        # Get file metadata
        file_name = os.path.basename(file_path)
        file_info = metadata.get(file_name, {})

        # Determine output directory based on metadata (only key, no tuning)
        if organize and 'key' in file_info:
            key = file_info['key']
            output_subdir = os.path.join(output_dir, set_dir, f"key_{key}")
        else:
            output_subdir = os.path.join(output_dir, set_dir, 'unknown')

        os.makedirs(output_subdir, exist_ok=True)

        # Preprocess the audio file
        output_base = os.path.join(output_subdir, os.path.splitext(file_name)[0])
        segments = preprocess_audio(file_path, output_base, normalize)

        # Update counts
        if is_train:
            train_count += segments
        else:
            val_count += segments

        # Apply augmentation if requested (only to training set)
        if augment and is_train:
            for i in range(segments):
                segment_path = f"{output_base}_{i}.wav"
                augmented = apply_augmentation(segment_path, output_subdir)
                augmented_count += augmented

    # Create a metadata file for the processed data
    processed_metadata = {
        'total_files': len(audio_files),
        'train_segments': train_count,
        'validation_segments': val_count,
        'augmented_segments': augmented_count,
        'total_segments': train_count + val_count + augmented_count,
        'sample_rate': SAMPLE_RATE,
        'duration': DURATION,
        'normalize': normalize,
        'augment': augment,
        'organize': organize,
        'split_ratio': split_ratio
    }

    with open(os.path.join(output_dir, 'processed_metadata.json'), 'w') as f:
        json.dump(processed_metadata, f, indent=2)

    print(f"Processed {len(audio_files)} files into {train_count + val_count} segments.")
    print(f"Training segments: {train_count}")
    print(f"Validation segments: {val_count}")
    if augment:
        print(f"Augmented segments: {augmented_count}")
    print(f"Total segments: {train_count + val_count + augmented_count}")
    print(f"Metadata saved to {os.path.join(output_dir, 'processed_metadata.json')}")


def main():
    """Main function."""
    args = parse_arguments()

    # Check if input directory exists
    if not os.path.exists(args.input_dir):
        print(f"Error: Input directory {args.input_dir} does not exist.")
        return

    # Generate metadata from directory structure if requested
    metadata = {}
    if args.generate_metadata:
        metadata = generate_metadata_from_directory(args.input_dir)

        # Determine where to save the generated metadata
        metadata_output = args.metadata_output
        if not metadata_output:
            metadata_output = os.path.join(args.output_dir, 'metadata.json')

        # Create output directory if it doesn't exist
        os.makedirs(os.path.dirname(metadata_output), exist_ok=True)

        # Save the generated metadata
        try:
            with open(metadata_output, 'w') as f:
                json.dump(metadata, f, indent=2)
            print(f"Generated metadata saved to {metadata_output}")

            # If no metadata file was specified, use the generated one
            if not args.metadata_file:
                args.metadata_file = metadata_output
        except Exception as e:
            print(f"Error saving generated metadata: {e}")

    # Load metadata if provided (or if we just generated it)
    if args.metadata_file:
        loaded_metadata = load_metadata(args.metadata_file)
        # If we generated metadata and also loaded some, merge them
        if args.generate_metadata:
            metadata.update(loaded_metadata)  # Loaded metadata takes precedence
        else:
            metadata = loaded_metadata

    # Process files
    process_files(
        args.input_dir, 
        args.output_dir, 
        metadata, 
        args.split_ratio, 
        args.normalize, 
        args.augment, 
        args.organize
    )

    print("Data preparation completed successfully!")


if __name__ == "__main__":
    main()
