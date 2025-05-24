#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Enhanced data augmentation script for the TensorFlow Magenta harmonica chord detection model.

This script applies advanced augmentation techniques to audio files to create
more diverse training data, with a focus on improving A-major chord detection.

Usage:
    python enhanced_data_augmentation.py --input_dir=path/to/audio --output_dir=path/to/augmented

Author: BlueSharpBendingApp Team
"""

import os
import argparse
import numpy as np
import librosa
import soundfile as sf
from tqdm import tqdm
import random
import glob

# Constants
SAMPLE_RATE = 16000
DURATION = 2.0  # seconds
A_MAJOR_FREQUENCIES = [220.0, 277.18, 329.63, 440.0]  # A3, C#4, E4, A4
A_MAJOR_NOTES = ['A3', 'C#4', 'E4', 'A4']

def parse_arguments():
    """Parse command line arguments."""
    parser = argparse.ArgumentParser(description='Apply enhanced data augmentation for chord detection')
    parser.add_argument('--input_dir', type=str, required=True, help='Directory containing audio files')
    parser.add_argument('--output_dir', type=str, required=True, help='Directory to save augmented files')
    parser.add_argument('--num_augmentations', type=int, default=10, help='Number of augmentations per file')
    parser.add_argument('--focus_on_a_major', action='store_true', help='Focus augmentation on A-major chord')
    parser.add_argument('--generate_synthetic', action='store_true', help='Generate synthetic A-major chord samples')
    parser.add_argument('--num_synthetic', type=int, default=100, help='Number of synthetic samples to generate')
    return parser.parse_args()

def apply_time_stretch(audio, rate=None):
    """Apply time stretching to audio."""
    if rate is None:
        # Random time stretch factor between 0.8 and 1.2
        rate = random.uniform(0.8, 1.2)
    return librosa.effects.time_stretch(audio, rate=rate)

def apply_pitch_shift(audio, sr, n_steps=None):
    """Apply pitch shifting to audio."""
    if n_steps is None:
        # Random pitch shift between -2 and 2 semitones
        n_steps = random.uniform(-2, 2)
    return librosa.effects.pitch_shift(audio, sr=sr, n_steps=n_steps)

def apply_noise(audio, noise_level=None):
    """Add random noise to audio."""
    if noise_level is None:
        # Random noise level between 0.001 and 0.01
        noise_level = random.uniform(0.001, 0.01)
    noise = np.random.randn(len(audio)) * noise_level
    return audio + noise

def apply_eq(audio, sr):
    """Apply random equalization to audio."""
    # Create a random EQ curve
    n_bands = 6
    gains = np.random.uniform(-6, 6, n_bands)  # Random gains between -6 and 6 dB
    
    # Convert to linear scale
    gains = 10 ** (gains / 20)
    
    # Apply EQ using FFT
    D = librosa.stft(audio)
    n_fft = 2 * (D.shape[0] - 1)
    
    # Create frequency bands
    freq_bands = np.logspace(np.log10(20), np.log10(sr/2), n_bands+1)
    freq_bins = np.floor(freq_bands / sr * n_fft).astype(int)
    freq_bins = np.minimum(freq_bins, n_fft//2)
    
    # Apply gains to each frequency band
    for i in range(n_bands):
        D[freq_bins[i]:freq_bins[i+1], :] *= gains[i]
    
    # Inverse STFT
    return librosa.istft(D)

def apply_compression(audio, threshold=None, ratio=None):
    """Apply dynamic range compression to audio."""
    if threshold is None:
        # Random threshold between -30 and -10 dB
        threshold = random.uniform(-30, -10)
    if ratio is None:
        # Random ratio between 2 and 5
        ratio = random.uniform(2, 5)
    
    # Convert to dB
    audio_db = librosa.amplitude_to_db(np.abs(audio))
    
    # Apply compression
    mask = audio_db > threshold
    audio_db[mask] = threshold + (audio_db[mask] - threshold) / ratio
    
    # Convert back to amplitude
    return librosa.db_to_amplitude(audio_db) * np.sign(audio)

def generate_synthetic_a_major_chord(duration=DURATION, sr=SAMPLE_RATE, harmonics=3, noise_level=0.005):
    """Generate a synthetic A-major chord with harmonics."""
    t = np.linspace(0, duration, int(sr * duration), endpoint=False)
    chord = np.zeros_like(t)
    
    # Add each note of the A-major chord with harmonics
    for freq in A_MAJOR_FREQUENCIES:
        # Fundamental frequency
        note = np.sin(2 * np.pi * freq * t)
        
        # Add harmonics
        for h in range(2, harmonics + 2):
            harmonic_amp = 1.0 / h  # Amplitude decreases with harmonic number
            note += harmonic_amp * np.sin(2 * np.pi * freq * h * t)
        
        # Add the note to the chord
        chord += note
    
    # Normalize
    chord = chord / np.max(np.abs(chord))
    
    # Apply envelope (ADSR)
    attack = int(0.05 * sr)
    decay = int(0.1 * sr)
    sustain_level = 0.7
    release = int(0.2 * sr)
    
    envelope = np.ones_like(chord)
    # Attack
    envelope[:attack] = np.linspace(0, 1, attack)
    # Decay
    envelope[attack:attack+decay] = np.linspace(1, sustain_level, decay)
    # Sustain (already set to sustain_level)
    # Release
    release_start = len(envelope) - release
    envelope[release_start:] = np.linspace(sustain_level, 0, release)
    
    # Apply envelope
    chord = chord * envelope
    
    # Add some noise
    chord = chord + np.random.randn(len(chord)) * noise_level
    
    return chord

def augment_audio_file(file_path, output_dir, num_augmentations=5, focus_on_a_major=False):
    """Apply multiple augmentations to an audio file."""
    try:
        # Load audio file
        y, sr = librosa.load(file_path, sr=SAMPLE_RATE, mono=True)
        
        # Ensure the audio is the expected length
        if len(y) < SAMPLE_RATE * DURATION:
            y = np.pad(y, (0, int(SAMPLE_RATE * DURATION) - len(y)))
        elif len(y) > SAMPLE_RATE * DURATION:
            y = y[:int(SAMPLE_RATE * DURATION)]
        
        # Create output directory if it doesn't exist
        os.makedirs(output_dir, exist_ok=True)
        
        # Get filename without extension
        filename = os.path.splitext(os.path.basename(file_path))[0]
        
        # Check if this is an A-major chord file
        is_a_major = any(note in filename for note in A_MAJOR_NOTES) or 'A-major' in filename or 'Amaj' in filename
        
        # If focusing on A-major and this is not an A-major file, reduce augmentations
        if focus_on_a_major and not is_a_major:
            num_augmentations = max(1, num_augmentations // 3)
        
        # If focusing on A-major and this is an A-major file, increase augmentations
        if focus_on_a_major and is_a_major:
            num_augmentations = num_augmentations * 2
        
        # Apply augmentations
        for i in range(num_augmentations):
            # Start with the original audio
            augmented = y.copy()
            
            # Apply random augmentations
            # Time stretch (50% chance)
            if random.random() < 0.5:
                augmented = apply_time_stretch(augmented)
            
            # Pitch shift (50% chance)
            if random.random() < 0.5:
                augmented = apply_pitch_shift(augmented, sr)
            
            # Add noise (30% chance)
            if random.random() < 0.3:
                augmented = apply_noise(augmented)
            
            # Apply EQ (40% chance)
            if random.random() < 0.4:
                augmented = apply_eq(augmented, sr)
            
            # Apply compression (30% chance)
            if random.random() < 0.3:
                augmented = apply_compression(augmented)
            
            # Save augmented audio
            output_path = os.path.join(output_dir, f"{filename}_aug_{i+1}.wav")
            sf.write(output_path, augmented, sr)
            
        return num_augmentations
    
    except Exception as e:
        print(f"Error augmenting {file_path}: {e}")
        return 0

def main():
    """Main function."""
    args = parse_arguments()
    
    # Create output directory if it doesn't exist
    os.makedirs(args.output_dir, exist_ok=True)
    
    # Find all audio files in the input directory
    audio_files = []
    for ext in ['*.wav', '*.mp3', '*.flac']:
        audio_files.extend(glob.glob(os.path.join(args.input_dir, '**', ext), recursive=True))
    
    print(f"Found {len(audio_files)} audio files")
    
    # Apply augmentations to each file
    total_augmentations = 0
    for file_path in tqdm(audio_files, desc="Augmenting audio files"):
        num_augmentations = augment_audio_file(
            file_path, 
            args.output_dir, 
            args.num_augmentations,
            args.focus_on_a_major
        )
        total_augmentations += num_augmentations
    
    print(f"Created {total_augmentations} augmented audio files")
    
    # Generate synthetic A-major chord samples if requested
    if args.generate_synthetic:
        print(f"Generating {args.num_synthetic} synthetic A-major chord samples...")
        for i in tqdm(range(args.num_synthetic), desc="Generating synthetic samples"):
            # Generate synthetic chord
            chord = generate_synthetic_a_major_chord()
            
            # Save synthetic chord
            output_path = os.path.join(args.output_dir, f"synthetic_A_major_chord_{i+1}.wav")
            sf.write(output_path, chord, SAMPLE_RATE)
        
        print(f"Created {args.num_synthetic} synthetic A-major chord samples")
    
    print("Data augmentation completed successfully!")

if __name__ == "__main__":
    main()