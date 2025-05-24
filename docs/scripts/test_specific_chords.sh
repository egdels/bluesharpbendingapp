#!/bin/bash
# Script to test specific chord types
# This script runs the test scripts for A-major and C-major chords

echo "Testing A-major chord detection..."
python3 docs/scripts/test_a_chord.py

echo -e "\n\n"
echo "Testing C-major chord detection..."
python3 docs/scripts/test_c_chord.py

echo -e "\n\n"
echo "All tests completed."