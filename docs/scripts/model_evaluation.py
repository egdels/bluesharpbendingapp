#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Model evaluation script for the TensorFlow Magenta harmonica chord detection model.

This script evaluates a trained TensorFlow model for harmonica chord detection
and provides various metrics and visualizations.

Usage:
    python model_evaluation.py --model_dir=path/to/model --test_data=path/to/test/data

Author: BlueSharpBendingApp Team
"""

import os
import argparse
import numpy as np
import tensorflow as tf
import json
import pickle
import matplotlib.pyplot as plt
import seaborn as sns
from tqdm import tqdm
import librosa
import soundfile as sf
from sklearn.metrics import confusion_matrix, classification_report, accuracy_score, precision_recall_fscore_support

# Constants
MODEL_NAME = "magenta_chord_detection_model"
INPUT_NODE = "input_audio"
OUTPUT_NODE = "chord_predictions"
OUTPUT_TENSOR = "chord_predictions:0"
NUM_CLASSES = 96  # 12 semitones in an octave * 8 octaves (0-7)
OCTAVE_RANGE = 8  # Support octaves 0-7
SAMPLE_RATE = 16000  # Expected sample rate of the audio files


def parse_arguments():
    """Parse command line arguments."""
    parser = argparse.ArgumentParser(description='Evaluate a trained TensorFlow model for harmonica chord detection')
    parser.add_argument('--model_dir', type=str, required=True, help='Directory containing the trained model')
    parser.add_argument('--test_data', type=str, required=True, help='Directory containing test data or path to a test file')
    parser.add_argument('--output_dir', type=str, default=None, help='Directory to save evaluation results (default: model_dir/evaluation)')
    parser.add_argument('--feature_type', type=str, default='all', 
                        choices=['mfcc', 'chroma', 'contrast', 'mel', 'raw', 'all'],
                        help='Type of features to use for evaluation (default: all)')
    parser.add_argument('--batch_size', type=int, default=32, help='Batch size for evaluation (default: 32)')
    parser.add_argument('--audio_file', action='store_true', help='Test data is a single audio file')
    parser.add_argument('--visualize', action='store_true', help='Generate visualizations')
    return parser.parse_args()


def load_model(model_dir):
    """
    Load a trained TensorFlow model.

    Args:
        model_dir: Directory containing the trained model

    Returns:
        The loaded TensorFlow model
    """
    # Check for different model formats
    saved_model_path = os.path.join(model_dir, 'models', MODEL_NAME)
    saved_model_dir = os.path.join(model_dir, 'models', 'saved_model')
    pb_path = os.path.join(model_dir, 'models', f"{MODEL_NAME}.pb")
    h5_path = os.path.join(model_dir, 'models', f"{MODEL_NAME}.h5")

    # Try to load the model from SavedModel format
    if os.path.exists(saved_model_path):
        print(f"Loading model from {saved_model_path}...")
        model = tf.keras.models.load_model(saved_model_path)
        return model

    # Try to load the model from saved_model directory
    elif os.path.exists(saved_model_dir):
        print(f"Loading model from {saved_model_dir}...")
        model = tf.saved_model.load(saved_model_dir)
        return model

    # Try to load the model from .h5 format
    elif os.path.exists(h5_path):
        print(f"Loading model from {h5_path}...")
        model = tf.keras.models.load_model(h5_path)
        return model

    # Try to load the model from .pb format
    elif os.path.exists(pb_path):
        print(f"Loading model from {pb_path}...")
        # This is more complex and depends on how the model was saved
        # For TensorFlow models:
        try:
            # Try to load as a SavedModel
            model = tf.saved_model.load(pb_path)
            return model
        except Exception as e:
            print(f"Failed to load model as SavedModel: {e}")
            # Try to load as a frozen graph
            try:
                with tf.io.gfile.GFile(pb_path, 'rb') as f:
                    graph_def = tf.compat.v1.GraphDef()
                    graph_def.ParseFromString(f.read())

                with tf.compat.v1.Graph().as_default() as graph:
                    tf.compat.v1.import_graph_def(graph_def, name='')

                return tf.compat.v1.Session(graph=graph)
            except Exception as e2:
                print(f"Failed to load model as frozen graph: {e2}")
                raise ValueError(f"Could not load model from {pb_path}")

    else:
        # Try to load from model architecture and weights
        architecture_path = os.path.join(model_dir, 'models', f"{MODEL_NAME}_architecture.json")
        weights_path = os.path.join(model_dir, 'models', f"{MODEL_NAME}_weights.h5")

        if os.path.exists(architecture_path) and os.path.exists(weights_path):
            print(f"Loading model from architecture and weights...")
            with open(architecture_path, 'r') as f:
                model_json = f.read()
            model = tf.keras.models.model_from_json(model_json)
            model.load_weights(weights_path)
            return model

    raise FileNotFoundError(f"No model found in {model_dir}")


def load_test_data(test_data_path, feature_type='all'):
    """
    Load test data from a directory or file.

    Args:
        test_data_path: Path to the test data directory or file
        feature_type: Type of features to use for evaluation

    Returns:
        X_test, y_test: Test data and labels
    """
    # Check if test_data_path is a directory
    if os.path.isdir(test_data_path):
        # Load features and labels from the directory
        features_file = os.path.join(test_data_path, 'features.pkl')
        labels_file = os.path.join(test_data_path, 'labels.pkl')

        if os.path.exists(features_file) and os.path.exists(labels_file):
            with open(features_file, 'rb') as f:
                features = pickle.load(f)
            with open(labels_file, 'rb') as f:
                labels = pickle.load(f)

            # Process features and labels
            X_test = process_features(features, feature_type)
            y_test = process_labels(labels)

            return X_test, y_test
        else:
            raise FileNotFoundError(f"Features or labels file not found in {test_data_path}")

    # Check if test_data_path is a file
    elif os.path.isfile(test_data_path):
        # Load the file based on its extension
        if test_data_path.endswith('.pkl'):
            # Assume it's a pickle file with features
            with open(test_data_path, 'rb') as f:
                data = pickle.load(f)

            if isinstance(data, tuple) and len(data) == 2:
                # Assume it's a tuple of (features, labels)
                features, labels = data
                X_test = process_features(features, feature_type)
                y_test = process_labels(labels)
                return X_test, y_test
            else:
                # Assume it's just features
                X_test = process_features(data, feature_type)
                return X_test, None

        elif test_data_path.endswith(('.wav', '.mp3', '.flac', '.ogg')):
            # It's an audio file, extract features directly
            features = extract_features_from_audio(test_data_path, feature_type)
            return features, None

        else:
            raise ValueError(f"Unsupported file format: {test_data_path}")

    else:
        raise FileNotFoundError(f"Test data path not found: {test_data_path}")


def process_features(features_list, feature_type):
    """
    Process features based on the specified feature type.

    Args:
        features_list: List of feature dictionaries
        feature_type: Type of features to use

    Returns:
        Processed features as a numpy array
    """
    if feature_type == 'raw':
        # Use raw audio data
        return np.array([f['raw'] for f in features_list if 'raw' in f])

    elif feature_type == 'mfcc':
        # Use MFCC features
        return np.array([f['mfcc'] for f in features_list if 'mfcc' in f])

    elif feature_type == 'chroma':
        # Use chroma features
        return np.array([f['chroma'] for f in features_list if 'chroma' in f])

    elif feature_type == 'contrast':
        # Use spectral contrast features
        return np.array([f['contrast'] for f in features_list if 'contrast' in f])

    elif feature_type == 'mel':
        # Use mel spectrogram features
        return np.array([f['mel'] for f in features_list if 'mel' in f])

    elif feature_type == 'all':
        # Combine all available features
        combined_features = []

        for f in features_list:
            feature_vector = []

            # Add MFCC features if available
            if 'mfcc' in f:
                feature_vector.append(np.mean(f['mfcc'], axis=1))

            # Add chroma features if available
            if 'chroma' in f:
                feature_vector.append(np.mean(f['chroma'], axis=1))

            # Add spectral contrast features if available
            if 'contrast' in f:
                feature_vector.append(np.mean(f['contrast'], axis=1))

            # Combine all features
            if feature_vector:
                combined_features.append(np.concatenate(feature_vector))

        return np.array(combined_features)

    else:
        raise ValueError(f"Unsupported feature type: {feature_type}")


def process_labels(labels_list):
    """
    Process labels into a format suitable for evaluation.

    Args:
        labels_list: List of labels

    Returns:
        Processed labels as a numpy array
    """
    # Convert labels to one-hot encoding
    processed_labels = []

    for label in labels_list:
        if isinstance(label, dict):
            # Handle key_tuning labels
            # For now, we'll just use the key as the label
            if 'key' in label and label['key'] != 'unknown':
                # Map key to semitone index (C=0, C#=1, D=2, etc.)
                key_map = {'C': 0, 'C#': 1, 'Db': 1, 'D': 2, 'D#': 3, 'Eb': 3,
                          'E': 4, 'F': 5, 'F#': 6, 'Gb': 6, 'G': 7, 'G#': 8,
                          'Ab': 8, 'A': 9, 'A#': 10, 'Bb': 10, 'B': 11}

                if label['key'] in key_map:
                    # For key_tuning labels, we still use the original 12-class system
                    # as octave information is not relevant for keys
                    processed_labels.append(key_map[label['key']])
                else:
                    # Default to C (0) if key is not recognized
                    processed_labels.append(0)
            else:
                # Default to C (0) if key is unknown
                processed_labels.append(0)

        elif isinstance(label, str):
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

    # Convert to numpy array
    processed_labels = np.array(processed_labels)

    # Convert to one-hot encoding
    one_hot_labels = tf.keras.utils.to_categorical(processed_labels, num_classes=NUM_CLASSES)

    return one_hot_labels


def extract_features_from_audio(audio_path, feature_type='all'):
    """
    Extract features from an audio file.

    Args:
        audio_path: Path to the audio file
        feature_type: Type of features to extract

    Returns:
        Extracted features as a numpy array
    """
    # Load audio file
    y, sr = librosa.load(audio_path, sr=SAMPLE_RATE, mono=True)

    # Ensure the audio is at least 1 second long and long enough for FFT
    if len(y) < sr:
        y = np.pad(y, (0, sr - len(y)))

    # For chroma_cqt, we need to ensure the audio is long enough for the largest FFT size
    # The largest FFT size used by chroma_cqt seems to be 32768 based on the warnings
    if len(y) < 32768:
        # Only pad if we're going to extract chroma features
        if feature_type in ['chroma', 'all']:
            y_for_chroma = np.pad(y, (0, 32768 - len(y)))
        else:
            y_for_chroma = y
    else:
        y_for_chroma = y

    # Calculate appropriate n_fft value based on audio length
    # n_fft should be a power of 2 and less than the audio length
    n_fft = 512  # Default FFT size
    if len(y) < n_fft:
        # Find the largest power of 2 that's less than the audio length
        n_fft = 2 ** int(np.log2(len(y)))
        print(f"Adjusted n_fft to {n_fft} for audio of length {len(y)}")

    # Extract features based on feature_type
    features = {}

    # Include raw audio
    features['raw'] = y

    # Extract MFCC features
    if feature_type in ['mfcc', 'all']:
        mfcc = librosa.feature.mfcc(y=y, sr=sr, n_mfcc=13, n_fft=n_fft)
        features['mfcc'] = mfcc

    # Extract chroma features
    if feature_type in ['chroma', 'all']:
        # chroma_cqt doesn't accept n_fft, but we can adjust hop_length
        # Use the padded audio signal for chroma_cqt to avoid FFT size warnings
        hop_length = min(512, len(y_for_chroma)) // 4  # Ensure hop_length is appropriate for audio length
        chroma = librosa.feature.chroma_cqt(y=y_for_chroma, sr=sr, hop_length=hop_length)
        features['chroma'] = chroma

    # Extract spectral contrast features
    if feature_type in ['contrast', 'all']:
        contrast = librosa.feature.spectral_contrast(y=y, sr=sr, n_fft=n_fft)
        features['contrast'] = contrast

    # Extract mel spectrogram features
    if feature_type in ['mel', 'all']:
        mel = librosa.feature.melspectrogram(y=y, sr=sr, n_fft=n_fft)
        features['mel'] = librosa.power_to_db(mel)

    # Process features for model input
    processed_features = process_features([features], feature_type)

    return processed_features


def evaluate_model(model, X_test, y_test, batch_size=32):
    """
    Evaluate the model on test data.

    Args:
        model: The trained TensorFlow model
        X_test: Test data
        y_test: Test labels
        batch_size: Batch size for evaluation

    Returns:
        Dictionary containing evaluation metrics
    """
    # Evaluate the model
    print("Evaluating model on test data...")

    # Check if the model is a TensorFlow Session (frozen graph)
    if isinstance(model, tf.compat.v1.Session):
        # Get input and output tensors
        input_tensor = model.graph.get_tensor_by_name('input_audio:0')
        output_tensor = model.graph.get_tensor_by_name(OUTPUT_TENSOR)

        # Evaluate batch by batch
        y_pred = []
        for i in range(0, len(X_test), batch_size):
            batch_data = X_test[i:i+batch_size]
            batch_preds = []

            for x in batch_data:
                # Reshape input data to match the model's expected input shape
                x = np.expand_dims(x, axis=0).astype(np.float32)

                # Run inference
                output_data = model.run(output_tensor, feed_dict={input_tensor: x})
                batch_preds.append(output_data[0])

            y_pred.extend(batch_preds)

        y_pred = np.array(y_pred)
    # Check if the model is a SavedModel
    elif hasattr(model, 'signatures'):
        # Get the serving signature
        serving_fn = model.signatures['serving_default']

        # Evaluate batch by batch
        y_pred = []
        for i in range(0, len(X_test), batch_size):
            batch_data = X_test[i:i+batch_size]
            batch_preds = []

            for x in batch_data:
                # Reshape input data to match the model's expected input shape
                x = np.expand_dims(x, axis=0).astype(np.float32)
                x_tensor = tf.convert_to_tensor(x)

                # Run inference
                output_data = serving_fn(x_tensor)

                # Get the output tensor (assuming it's the first output)
                output_key = list(output_data.keys())[0]
                output_value = output_data[output_key].numpy()

                batch_preds.append(output_value[0])

            y_pred.extend(batch_preds)

        y_pred = np.array(y_pred)
    else:
        # For regular TensorFlow models
        y_pred = model.predict(X_test, batch_size=batch_size)

    # Convert predictions and labels to class indices
    y_pred_classes = np.argmax(y_pred, axis=1)
    y_test_classes = np.argmax(y_test, axis=1)

    # Calculate metrics
    accuracy = accuracy_score(y_test_classes, y_pred_classes)
    precision, recall, f1, _ = precision_recall_fscore_support(y_test_classes, y_pred_classes, average='weighted')

    # Create confusion matrix
    cm = confusion_matrix(y_test_classes, y_pred_classes)

    # Generate classification report
    report = classification_report(y_test_classes, y_pred_classes, output_dict=True)

    # Return metrics
    metrics = {
        'accuracy': accuracy,
        'precision': precision,
        'recall': recall,
        'f1': f1,
        'confusion_matrix': cm,
        'classification_report': report,
        'y_pred': y_pred,
        'y_pred_classes': y_pred_classes,
        'y_test_classes': y_test_classes
    }

    return metrics


def predict_audio_file(model, audio_path, feature_type='all'):
    """
    Predict the chord for a single audio file.

    Args:
        model: The trained TensorFlow model
        audio_path: Path to the audio file
        feature_type: Type of features to extract

    Returns:
        Predicted chord and confidence
    """
    # Extract features from the audio file
    features = extract_features_from_audio(audio_path, feature_type)

    # Make prediction
    if isinstance(model, tf.compat.v1.Session):
        # Get input and output tensors
        input_tensor = model.graph.get_tensor_by_name('input_audio:0')
        output_tensor = model.graph.get_tensor_by_name(OUTPUT_TENSOR)

        # Reshape input data to match the model's expected input shape
        features = np.expand_dims(features, axis=0).astype(np.float32)

        # Run inference
        prediction = model.run(output_tensor, feed_dict={input_tensor: features})
    # Check if the model is a SavedModel
    elif hasattr(model, 'signatures'):
        # Get the serving signature
        serving_fn = model.signatures['serving_default']

        # Reshape input data to match the model's expected input shape
        features = np.expand_dims(features, axis=0).astype(np.float32)
        features_tensor = tf.convert_to_tensor(features)

        # Run inference
        output_data = serving_fn(features_tensor)

        # Get the output tensor (assuming it's the first output)
        output_key = list(output_data.keys())[0]
        prediction = output_data[output_key].numpy()
    else:
        # For regular TensorFlow models
        prediction = model.predict(features)

    # Get the predicted class and confidence
    predicted_class = np.argmax(prediction[0])
    confidence = prediction[0][predicted_class]

    # Map the class index to a note name with octave
    note_names = ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B']
    octave = predicted_class // 12
    semitone = predicted_class % 12
    predicted_note = f"{note_names[semitone]}{octave}"

    return predicted_note, confidence


def visualize_confusion_matrix(cm, output_dir):
    """
    Visualize the confusion matrix.

    Args:
        cm: Confusion matrix
        output_dir: Directory to save the visualization
    """
    # Create plots directory
    plots_dir = os.path.join(output_dir, 'plots')
    os.makedirs(plots_dir, exist_ok=True)

    # Define note names with octaves
    note_names = []
    for octave in range(OCTAVE_RANGE):
        for semitone in ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B']:
            note_names.append(f"{semitone}{octave}")

    # For visualization, we'll use a subset of the confusion matrix
    # focusing on the most relevant octaves (e.g., 3-5)
    relevant_octaves = [3, 4, 5]  # Adjust as needed
    relevant_indices = []
    for octave in relevant_octaves:
        for semitone in range(12):
            relevant_indices.append(octave * 12 + semitone)

    # Extract the relevant subset of the confusion matrix
    if len(relevant_indices) < len(cm):
        cm_subset = cm[relevant_indices, :][:, relevant_indices]
        note_names_subset = [note_names[i] for i in relevant_indices]
    else:
        cm_subset = cm
        note_names_subset = note_names

    # Plot confusion matrix
    plt.figure(figsize=(15, 12))
    sns.heatmap(cm_subset, annot=True, fmt='d', cmap='Blues', 
                xticklabels=note_names_subset, yticklabels=note_names_subset)
    plt.xlabel('Predicted')
    plt.ylabel('True')
    plt.title('Confusion Matrix')
    plt.tight_layout()
    plt.savefig(os.path.join(plots_dir, 'confusion_matrix.png'))
    plt.close()

    print(f"Confusion matrix visualization saved to {plots_dir}")


def visualize_metrics(metrics, output_dir):
    """
    Visualize evaluation metrics.

    Args:
        metrics: Dictionary containing evaluation metrics
        output_dir: Directory to save the visualizations
    """
    # Create plots directory
    plots_dir = os.path.join(output_dir, 'plots')
    os.makedirs(plots_dir, exist_ok=True)

    # Define note names with octaves
    note_names = []
    for octave in range(OCTAVE_RANGE):
        for semitone in ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B']:
            note_names.append(f"{semitone}{octave}")

    # Plot per-class metrics
    report = metrics['classification_report']

    # Extract per-class metrics
    classes = []
    precision = []
    recall = []
    f1 = []

    # Focus on the most relevant octaves (e.g., 3-5)
    relevant_octaves = [3, 4, 5]  # Adjust as needed
    relevant_indices = []
    for octave in relevant_octaves:
        for semitone in range(12):
            relevant_indices.append(octave * 12 + semitone)

    for i in relevant_indices:
        if str(i) in report:
            classes.append(note_names[i])
            precision.append(report[str(i)]['precision'])
            recall.append(report[str(i)]['recall'])
            f1.append(report[str(i)]['f1-score'])

    # Plot per-class metrics
    plt.figure(figsize=(12, 6))
    x = np.arange(len(classes))
    width = 0.25

    plt.bar(x - width, precision, width, label='Precision')
    plt.bar(x, recall, width, label='Recall')
    plt.bar(x + width, f1, width, label='F1-score')

    plt.xlabel('Note')
    plt.ylabel('Score')
    plt.title('Per-class Metrics')
    plt.xticks(x, classes)
    plt.legend()
    plt.tight_layout()
    plt.savefig(os.path.join(plots_dir, 'per_class_metrics.png'))
    plt.close()

    print(f"Per-class metrics visualization saved to {plots_dir}")


def save_evaluation_results(metrics, output_dir):
    """
    Save evaluation results to files.

    Args:
        metrics: Dictionary containing evaluation metrics
        output_dir: Directory to save the results
    """
    # Create output directory if it doesn't exist
    os.makedirs(output_dir, exist_ok=True)

    # Save metrics to JSON file
    metrics_file = os.path.join(output_dir, 'evaluation_metrics.json')

    # Convert numpy arrays to lists for JSON serialization
    metrics_json = {
        'accuracy': float(metrics['accuracy']),
        'precision': float(metrics['precision']),
        'recall': float(metrics['recall']),
        'f1': float(metrics['f1']),
        'confusion_matrix': metrics['confusion_matrix'].tolist(),
        'classification_report': metrics['classification_report']
    }

    with open(metrics_file, 'w') as f:
        json.dump(metrics_json, f, indent=2)

    print(f"Evaluation metrics saved to {metrics_file}")


def main():
    """Main function."""
    args = parse_arguments()

    # Set output directory
    if args.output_dir is None:
        args.output_dir = os.path.join(args.model_dir, 'evaluation')

    # Create output directory if it doesn't exist
    os.makedirs(args.output_dir, exist_ok=True)

    # Load the model
    model = load_model(args.model_dir)

    # Check if we're evaluating a single audio file
    if args.audio_file:
        # Predict for a single audio file
        predicted_note, confidence = predict_audio_file(model, args.test_data, args.feature_type)

        print(f"Predicted note: {predicted_note}")
        print(f"Confidence: {confidence:.4f}")

        # Save prediction to file
        prediction = {
            'audio_file': args.test_data,
            'predicted_note': predicted_note,
            'confidence': float(confidence)
        }

        with open(os.path.join(args.output_dir, 'prediction.json'), 'w') as f:
            json.dump(prediction, f, indent=2)

        print(f"Prediction saved to {os.path.join(args.output_dir, 'prediction.json')}")
    else:
        # Load test data
        X_test, y_test = load_test_data(args.test_data, args.feature_type)

        if y_test is None:
            # If no labels are available, just make predictions
            if isinstance(model, tf.compat.v1.Session):
                # Get input and output tensors
                input_tensor = model.graph.get_tensor_by_name('input_audio:0')
                output_tensor = model.graph.get_tensor_by_name(OUTPUT_TENSOR)

                # Make predictions
                predictions = []
                for x in X_test:
                    # Reshape input data to match the model's expected input shape
                    x = np.expand_dims(x, axis=0).astype(np.float32)

                    # Run inference
                    output_data = model.run(output_tensor, feed_dict={input_tensor: x})
                    predictions.append(output_data[0])

                predictions = np.array(predictions)
            elif hasattr(model, 'signatures'):
                # Get the serving signature
                serving_fn = model.signatures['serving_default']

                # Make predictions
                predictions = []
                for x in X_test:
                    # Reshape input data to match the model's expected input shape
                    x = np.expand_dims(x, axis=0).astype(np.float32)
                    x_tensor = tf.convert_to_tensor(x)

                    # Run inference
                    output_data = serving_fn(x_tensor)

                    # Get the output tensor (assuming it's the first output)
                    output_key = list(output_data.keys())[0]
                    output_value = output_data[output_key].numpy()

                    predictions.append(output_value[0])

                predictions = np.array(predictions)
            else:
                # For regular TensorFlow models
                predictions = model.predict(X_test, batch_size=args.batch_size)

            # Get the predicted classes and confidences
            predicted_classes = np.argmax(predictions, axis=1)
            confidences = np.max(predictions, axis=1)

            # Map the class indices to note names with octaves
            note_names = ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B']
            predicted_notes = []
            for i in predicted_classes:
                octave = i // 12
                semitone = i % 12
                predicted_notes.append(f"{note_names[semitone]}{octave}")

            # Save predictions to file
            predictions_data = {
                'predicted_notes': predicted_notes,
                'predicted_classes': predicted_classes.tolist(),
                'confidences': confidences.tolist()
            }

            with open(os.path.join(args.output_dir, 'predictions.json'), 'w') as f:
                json.dump(predictions_data, f, indent=2)

            print(f"Predictions saved to {os.path.join(args.output_dir, 'predictions.json')}")
        else:
            # Evaluate the model
            metrics = evaluate_model(model, X_test, y_test, args.batch_size)

            # Print evaluation metrics
            print(f"Accuracy: {metrics['accuracy']:.4f}")
            print(f"Precision: {metrics['precision']:.4f}")
            print(f"Recall: {metrics['recall']:.4f}")
            print(f"F1-score: {metrics['f1']:.4f}")

            # Save evaluation results
            save_evaluation_results(metrics, args.output_dir)

            # Generate visualizations if requested
            if args.visualize:
                visualize_confusion_matrix(metrics['confusion_matrix'], args.output_dir)
                visualize_metrics(metrics, args.output_dir)

    print("Evaluation completed successfully!")


if __name__ == "__main__":
    main()
