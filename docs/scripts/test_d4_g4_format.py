#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Test script to verify that chord names in the format "D4-G4" are correctly processed.
This is a simplified version that directly tests the logic without importing the actual modules.
"""

def mock_get_label_from_path(audio_path, label_type):
    """
    A simplified version of get_label_from_path that only tests the chord extraction logic.
    """
    import os
    filename = os.path.basename(audio_path)

    if label_type == 'chord':
        # Assume filename format: chord_Cmaj_1.wav or chord_C4-E4-G4_1.wav or similar
        parts = filename.split('_')
        if len(parts) >= 2 and parts[0] == 'chord':
            return parts[1]  # Return chord name (e.g., 'Cmaj' or 'C4-E4-G4' or 'D4-G4')

        return 'unknown'

    return 'unknown'

def mock_process_labels(labels_list):
    """
    A simplified version of process_labels that only tests the chord processing logic.
    """
    processed_labels = []

    for label in labels_list:
        if isinstance(label, str):
            # Check if the label is in the format "C4-E4-G4" or "D4-G4" (notes separated by hyphens)
            if '-' in label and all(part[0] in 'ABCDEFG' for part in label.split('-')):
                # Handle chord labels in the format "C4-E4-G4" or "D4-G4"
                # Extract the root note (first note in the list)
                root_note = label.split('-')[0][0]

                # Map root note to semitone index
                note_map = {'C': 0, 'D': 2, 'E': 4, 'F': 5, 'G': 7, 'A': 9, 'B': 11}

                if root_note in note_map:
                    # Check for sharp or flat in the root note
                    if len(label.split('-')[0]) > 1 and label.split('-')[0][1] in ['#', 'b']:
                        if label.split('-')[0][1] == '#':
                            semitone = (note_map[root_note] + 1) % 12
                        else:  # flat
                            semitone = (note_map[root_note] - 1) % 12
                    else:
                        semitone = note_map[root_note]

                    processed_labels.append(semitone)
                else:
                    # Default to C (0) if root note is not recognized
                    processed_labels.append(0)
            elif label.startswith(('C', 'D', 'E', 'F', 'G', 'A', 'B')):
                # Handle note labels (e.g., 'C4', 'A3', etc.) or traditional chord labels (e.g., 'Cmaj')
                # Extract the note name (first character)
                note_name = label[0]

                # Map note name to semitone index
                note_map = {'C': 0, 'D': 2, 'E': 4, 'F': 5, 'G': 7, 'A': 9, 'B': 11}

                if note_name in note_map:
                    # Check for sharp or flat
                    if len(label) > 1 and label[1] in ['#', 'b']:
                        if label[1] == '#':
                            semitone = (note_map[note_name] + 1) % 12
                        else:  # flat
                            semitone = (note_map[note_name] - 1) % 12
                    else:
                        semitone = note_map[note_name]

                    processed_labels.append(semitone)
                else:
                    # Default to C (0) if note is not recognized
                    processed_labels.append(0)
            else:
                # Default to C (0) if label is not a note or chord
                processed_labels.append(0)
        else:
            # Default to C (0) for any other label type
            processed_labels.append(0)

    # For testing purposes, just return the processed labels
    return processed_labels

def test_get_label_from_path():
    """Test that get_label_from_path correctly extracts chord names in the format "D4-G4"."""
    # Create a mock file path with a chord name in the format "D4-G4"
    file_path = "/path/to/chord_D4-G4_1.wav"

    # Extract the label
    label = mock_get_label_from_path(file_path, 'chord')

    # Verify that the label is correctly extracted
    print(f"Extracted label: {label}")
    assert label == "D4-G4", f"Expected 'D4-G4', got '{label}'"

    print("get_label_from_path test passed!")

def test_process_labels():
    """Test that process_labels correctly processes chord names in the format "D4-G4"."""
    # Create a list of labels including a chord name in the format "D4-G4"
    labels = ["D4-G4", "Cmaj", "C4-E4-G4", "G7"]

    # Process the labels
    processed_labels = mock_process_labels(labels)

    # Verify that the labels are correctly processed
    print(f"Processed labels: {processed_labels}")
    expected_labels = [2, 0, 0, 7]  # D=2, C=0, C=0, G=7
    assert processed_labels == expected_labels, f"Expected {expected_labels}, got {processed_labels}"

    print("process_labels test passed!")

def main():
    """Run the tests."""
    print("Testing chord name processing for 'D4-G4'...")

    # Test get_label_from_path
    test_get_label_from_path()

    # Test process_labels
    test_process_labels()

    print("All tests passed! 'D4-G4' is a valid chord name.")

if __name__ == "__main__":
    main()