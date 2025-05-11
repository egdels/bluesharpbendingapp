#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Test script to verify if the model can distinguish between a chord (C4-E4-G4) and a single note (C4).
This is a simplified version that directly tests the logic without importing the actual modules.
"""

def mock_get_label_from_path(audio_path, label_type):
    """
    A simplified version of get_label_from_path that tests the note and chord extraction logic.
    """
    import os
    filename = os.path.basename(audio_path)
    
    if label_type == 'note':
        # Assume filename format: note_C4_blow.wav or similar
        parts = filename.split('_')
        if len(parts) >= 2 and parts[0] == 'note':
            for part in parts:
                if part.endswith('.wav'):
                    part = part[:-4]  # Remove .wav extension
                if len(part) >= 2 and part[0] in 'ABCDEFG':
                    return part  # Return note name (e.g., 'C4')
        return 'unknown'
    
    elif label_type == 'chord':
        # Assume filename format: chord_Cmaj_1.wav or chord_C4-E4-G4_1.wav or similar
        parts = filename.split('_')
        if len(parts) >= 2 and parts[0] == 'chord':
            return parts[1]  # Return chord name (e.g., 'Cmaj' or 'C4-E4-G4')
        return 'unknown'
    
    return 'unknown'

def mock_process_labels(labels_list):
    """
    A simplified version of process_labels that tests the note and chord processing logic.
    """
    OCTAVE_RANGE = 8  # Support octaves 0-7
    processed_labels = []

    for label in labels_list:
        if isinstance(label, str):
            # Check if the label is in the format "C4-E4-G4" (notes separated by hyphens)
            if '-' in label and all(part[0] in 'ABCDEFG' for part in label.split('-')):
                # Handle chord labels in the format "C4-E4-G4"
                # Extract the root note and octave (first note in the list)
                first_note = label.split('-')[0]
                root_note = first_note[0]
                
                # Extract octave number
                octave = 4  # Default to octave 4 if not specified
                for i in range(1, len(first_note)):
                    if first_note[i].isdigit():
                        octave = int(first_note[i])
                        break
                
                # Ensure octave is within range
                octave = max(0, min(octave, OCTAVE_RANGE - 1))

                # Map root note to semitone index
                note_map = {'C': 0, 'D': 2, 'E': 4, 'F': 5, 'G': 7, 'A': 9, 'B': 11}

                if root_note in note_map:
                    # Check for sharp or flat in the root note
                    has_sharp_or_flat = False
                    for i in range(1, len(first_note)):
                        if first_note[i] in ['#', 'b']:
                            has_sharp_or_flat = True
                            if first_note[i] == '#':
                                semitone = (note_map[root_note] + 1) % 12
                            else:  # flat
                                semitone = (note_map[root_note] - 1) % 12
                            break
                    
                    if not has_sharp_or_flat:
                        semitone = note_map[root_note]

                    # Calculate class index based on semitone and octave
                    class_index = semitone + (octave * 12)
                    processed_labels.append(class_index)
                else:
                    # Default to C4 (48) if root note is not recognized
                    processed_labels.append(48)
            elif label.startswith(('C', 'D', 'E', 'F', 'G', 'A', 'B')):
                # Handle note labels (e.g., 'C4', 'A3', etc.) or traditional chord labels (e.g., 'Cmaj')
                # Extract the note name (first character)
                note_name = label[0]

                # Extract octave number if present
                octave = 4  # Default to octave 4 if not specified
                for i in range(1, len(label)):
                    if label[i].isdigit():
                        octave = int(label[i])
                        break

                # Ensure octave is within range
                octave = max(0, min(octave, OCTAVE_RANGE - 1))

                # Map note name to semitone index
                note_map = {'C': 0, 'D': 2, 'E': 4, 'F': 5, 'G': 7, 'A': 9, 'B': 11}

                if note_name in note_map:
                    # Check for sharp or flat
                    has_sharp_or_flat = False
                    for i in range(1, len(label)):
                        if label[i] in ['#', 'b']:
                            has_sharp_or_flat = True
                            if label[i] == '#':
                                semitone = (note_map[note_name] + 1) % 12
                            else:  # flat
                                semitone = (note_map[note_name] - 1) % 12
                            break

                    if not has_sharp_or_flat:
                        semitone = note_map[note_name]

                    # Calculate class index based on semitone and octave
                    class_index = semitone + (octave * 12)
                    processed_labels.append(class_index)
                else:
                    # Default to C4 (48) if note is not recognized
                    processed_labels.append(48)
            else:
                # Default to C4 (48) if label is not a note or chord
                processed_labels.append(48)
        else:
            # Default to C4 (48) for any other label type
            processed_labels.append(48)

    # For testing purposes, just return the processed labels
    return processed_labels

def test_label_extraction():
    """Test that get_label_from_path correctly extracts note and chord labels."""
    # Create mock file paths
    note_file_path = "/path/to/note_C4_blow.wav"
    chord_file_path = "/path/to/chord_C4-E4-G4_1.wav"

    # Extract the labels
    note_label = mock_get_label_from_path(note_file_path, 'note')
    chord_label = mock_get_label_from_path(chord_file_path, 'chord')

    # Verify that the labels are correctly extracted
    print(f"Extracted note label: {note_label}")
    print(f"Extracted chord label: {chord_label}")
    assert note_label == "C4", f"Expected 'C4', got '{note_label}'"
    assert chord_label == "C4-E4-G4", f"Expected 'C4-E4-G4', got '{chord_label}'"

    print("Label extraction test passed!")

def test_chord_vs_note_processing():
    """Test if the model processes a C4-E4-G4 chord differently from a C4 single note."""
    # Create a list of labels including both a single note and a chord
    labels = ["C4", "C4-E4-G4"]

    # Process the labels
    processed_labels = mock_process_labels(labels)

    # Verify that the labels are correctly processed
    print(f"Processed labels: {processed_labels}")
    expected_labels = [48, 48]  # C4=48, C4-E4-G4=48
    assert processed_labels == expected_labels, f"Expected {expected_labels}, got {processed_labels}"

    # Check if the single note and chord with the same root note are processed the same way
    if processed_labels[0] == processed_labels[1]:
        print("C4 and C4-E4-G4 are processed the same way (both mapped to class index 48).")
        print("The model does NOT distinguish between a single note and a chord with the same root note.")
    else:
        print("C4 and C4-E4-G4 are processed differently.")
        print("The model CAN distinguish between a single note and a chord with the same root note.")

def main():
    """Run the tests."""
    print("Testing if the model can distinguish between C4-E4-G4 (chord) and C4 (single note)...")

    # Test label extraction
    test_label_extraction()

    # Test chord vs note processing
    test_chord_vs_note_processing()

    print("\nConclusion:")
    print("While the model can extract different labels for notes and chords during the feature extraction phase,")
    print("it does not distinguish between them in its classification output.")
    print("Both are mapped to the same class index based on the root note and octave.")

if __name__ == "__main__":
    main()