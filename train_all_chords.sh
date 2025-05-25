#!/bin/bash
# Script to train a model for recognizing all chord types
# This script performs the complete workflow:
# 1. Data preparation - processes raw audio files
# 2. Feature extraction - extracts features from processed audio
# 3. Model training - trains a TensorFlow model on the extracted features
# 4. Model deployment - copies the trained model to the resources directory
# 5. Model testing - tests the model on all chord types

# Set up directories
OUTPUT_DIR="./training_output"
FEATURES_DIR="$OUTPUT_DIR/features"
TRAINING_DATA_DIR="/Users/christian/Documents/git/bluesharpbendingapp/training_data/data/raw"
MODEL_DIR="$OUTPUT_DIR/model"

# Create output directory if it doesn't exist
mkdir -p "$OUTPUT_DIR"
echo "Starting chord recognition model training workflow..."
echo "Output directory: $OUTPUT_DIR"
echo "Training data directory: $TRAINING_DATA_DIR"

# Step 1: Prepare the data
# This step processes the raw audio files, normalizes them, and organizes them by key
echo "Step 1: Preparing data for all chord types..."
python3 docs/scripts/data_preparation.py \
  --input_dir="$TRAINING_DATA_DIR" \
  --output_dir="$OUTPUT_DIR/processed_data" \
  --generate_metadata \
  --normalize \
  --augment \
  --organize \
  --split_ratio=0.8

# Step 2: Extract features
# This step extracts various audio features from the processed data
echo "Step 2: Extracting features from processed data..."
python3 docs/scripts/feature_extraction.py \
  --input_dir="$OUTPUT_DIR/processed_data" \
  --output_dir="$FEATURES_DIR" \
  --feature_type="all" \
  --include_raw \
  --label_type="chord"

# Step 3: Train the model
# This step trains a TensorFlow model on the extracted features
echo "Step 3: Training model for all chord types..."
WRAPT_DISABLE_EXTENSIONS=true python3 docs/scripts/model_training.py \
  --features_dir="$FEATURES_DIR" \
  --output_dir="$MODEL_DIR" \
  --model_type=advanced \
  --epochs=300 \
  --batch_size=32 \
  --learning_rate=0.0003 \
  --early_stopping \
  --patience=30 \
  --feature_type=all \
  --class_weight \
  --tensorboard

echo "Model training completed!"

# Step 4: Copy model to resources directory
# This step copies the trained model to the resources directory for use in the application
echo "Step 4: Copying model to resources directory..."
#mkdir -p "base/desktop/src/main/resources/models/saved_model"
#mkdir -p "base/src/main/resources/models/keras"
mkdir -p "desktop/src/main/resources/models/onnx"
#cp -r "$MODEL_DIR/models/saved_model/"* "base/src/main/resources/models/saved_model/"
#cp -r "$MODEL_DIR/models/"*.keras "base/src/main/resources/models/keras/" 2>/dev/null || true
cp -r "$MODEL_DIR/models/onnx/"*.onnx "desktop/src/main/resources/models/onnx/" 2>/dev/null || true
echo "Model copied to resources directory (SavedModel, Keras, and ONNX formats)"

# Step 5: Test the model
# This step tests the model on all chord types
echo "Step 5: Testing model with all chord types..."
python3 ./docs/scripts/test_all_chords.py

# Step 6: Test the model specifically on A major chord
# This step tests the model on the A major chord specifically
echo "Step 6: Testing model with A major chord..."
python3 ./docs/scripts/test_a_chord.py

echo "Process complete!"
echo "The trained model is now available in the resources directory and ready for use in the application."
echo "You can find the model evaluation results in the test output above."
