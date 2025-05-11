#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Test script to verify if the model can distinguish between a chord (C4-E4-G4) and a note in a different octave (C5).
This is a simplified version that directly tests the logic without importing the actual modules.
"""

def mock_process_labels(labels_list):
    """
    A simplified version of the modified process_labels function that considers octave information.
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
                # Handle note labels (e.g., 'C4', 'C5', etc.)
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

def test_chord_vs_note_different_octave():
    """Test if the model can distinguish between a C4-E4-G4 chord and a C5 note."""
    # Create a list of labels including a chord and a note in a different octave
    labels = ["C4-E4-G4", "C5"]

    # Process the labels
    processed_labels = mock_process_labels(labels)

    # Verify that the labels are correctly processed
    print(f"Processed labels: {processed_labels}")
    expected_labels = [48, 60]  # C4-E4-G4=48, C5=60
    assert processed_labels == expected_labels, f"Expected {expected_labels}, got {processed_labels}"

    # Check if the chord and note in different octave are processed differently
    if processed_labels[0] != processed_labels[1]:
        print("C4-E4-G4 and C5 are processed differently.")
        print(f"C4-E4-G4 is mapped to class index {processed_labels[0]}, while C5 is mapped to class index {processed_labels[1]}.")
        print("The model CAN distinguish between a chord and a note in a different octave.")
    else:
        print("C4-E4-G4 and C5 are processed the same way.")
        print("The model CANNOT distinguish between a chord and a note in a different octave.")

def main():
    """Run the tests."""
    print("Testing if the model can distinguish between C4-E4-G4 (chord) and C5 (note in different octave)...")

    # Test chord vs note in different octave
    test_chord_vs_note_different_octave()

    print("\nConclusion:")
    print("The model processes C4-E4-G4 as a C4 note (class index 48) based on its root note and octave.")
    print("The model processes C5 as a C5 note (class index 60).")
    print("Therefore, the model CAN distinguish between C4-E4-G4 and C5 because they are in different octaves.")

if __name__ == "__main__":
    main()