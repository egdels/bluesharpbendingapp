#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Test script to verify that note names in the format "A#3" are correctly processed.
This is a simplified version that directly tests the logic without importing the actual modules.
"""

def mock_get_label_from_path(audio_path, label_type):
    """
    A simplified version of get_label_from_path that tests the note extraction logic.
    """
    import os
    filename = os.path.basename(audio_path)

    if label_type == 'note':
        # Assume filename format: note_C4_blow.wav or similar
        parts = filename.split('_')
        if len(parts) >= 2:
            # Try to extract note information from filename
            for part in parts:
                if part.endswith('.wav'):
                    part = part[:-4]  # Remove .wav extension
                if len(part) >= 2 and part[0] in 'ABCDEFG':
                    # Check if the part starts with a note letter
                    # This is a simplified version that doesn't check if the rest is a digit
                    # to allow for sharps and flats
                    return part  # Return note name (e.g., 'C4', 'A#3')

        # If note information not found in filename, return unknown
        return 'unknown'

    return 'unknown'

def mock_process_labels(labels_list):
    """
    A simplified version of process_labels that tests the note processing logic.
    """
    processed_labels = []

    for label in labels_list:
        if isinstance(label, str) and label.startswith(('C', 'D', 'E', 'F', 'G', 'A', 'B')):
            # Handle note labels (e.g., 'C4', 'A#3', etc.)
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
            # Default to C (0) for any other label type
            processed_labels.append(0)

    # For testing purposes, just return the processed labels
    return processed_labels

def test_get_label_from_path():
    """Test that get_label_from_path correctly extracts note names in the format "A#3"."""
    # Create a mock file path with a note name in the format "A#3"
    file_path = "/path/to/note_A#3_blow.wav"

    # Extract the label
    label = mock_get_label_from_path(file_path, 'note')

    # Verify that the label is correctly extracted
    print(f"Extracted label: {label}")
    assert label == "A#3", f"Expected 'A#3', got '{label}'"

    print("get_label_from_path test passed!")

def test_process_labels():
    """Test that process_labels correctly processes note names in the format "A#3"."""
    # Create a list of labels including a note name in the format "A#3"
    labels = ["A#3", "C4", "Eb5", "G7"]

    # Process the labels
    processed_labels = mock_process_labels(labels)

    # Verify that the labels are correctly processed
    print(f"Processed labels: {processed_labels}")
    expected_labels = [10, 0, 3, 7]  # A#=10, C=0, Eb=3, G=7
    assert processed_labels == expected_labels, f"Expected {expected_labels}, got {processed_labels}"

    print("process_labels test passed!")

def main():
    """Run the tests."""
    print("Testing note name processing for 'A#3'...")

    # Test get_label_from_path
    test_get_label_from_path()

    # Test process_labels
    test_process_labels()

    print("All tests passed! 'A#3' is a valid note name.")

if __name__ == "__main__":
    main()