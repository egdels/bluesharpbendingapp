import tensorflow as tf
import numpy as np
import os
import sys
import subprocess
import warnings
from pathlib import Path

# Check if the new scripts are available
SCRIPTS_DIR = Path(__file__).parent / "scripts"
NEW_SCRIPT_PATH = SCRIPTS_DIR / "train_magenta_model.py"
USE_NEW_SCRIPTS = NEW_SCRIPT_PATH.exists()

# Define the model parameters
MODEL_NAME = "magenta_chord_detection_model"
INPUT_NODE = "input_audio"
OUTPUT_NODE = "chord_predictions"
MODEL_SAMPLE_RATE = 16000
MAX_PITCHES = 4

def create_simple_model():
    """
    Creates a simple TensorFlow model for chord detection.
    This is a placeholder model that can be used for testing.
    For a real application, you would need to train a more complex model.
    """
    # Define the input shape (1 second of audio at 16kHz)
    input_shape = (None, MODEL_SAMPLE_RATE)

    # Create the model
    inputs = tf.keras.Input(shape=input_shape[1:], name=INPUT_NODE)

    # Add some convolutional layers
    x = tf.keras.layers.Reshape((-1, 1))(inputs)
    x = tf.keras.layers.Conv1D(32, 3, activation='relu')(x)
    x = tf.keras.layers.MaxPooling1D(2)(x)
    x = tf.keras.layers.Conv1D(64, 3, activation='relu')(x)
    x = tf.keras.layers.MaxPooling1D(2)(x)
    x = tf.keras.layers.Flatten()(x)

    # Add some dense layers
    x = tf.keras.layers.Dense(128, activation='relu')(x)
    x = tf.keras.layers.Dropout(0.5)(x)

    # Output layer (12 semitones in an octave)
    outputs = tf.keras.layers.Dense(12, activation='softmax', name=OUTPUT_NODE)(x)

    # Create the model
    model = tf.keras.Model(inputs=inputs, outputs=outputs)

    # Compile the model
    model.compile(
        optimizer='adam',
        loss='sparse_categorical_crossentropy',
        metrics=['accuracy']
    )

    return model

def generate_dummy_data():
    """
    Generates dummy data for training the model.
    In a real application, you would use actual audio recordings.
    """
    # Generate 100 examples of 1 second of audio at 16kHz
    X = np.random.randn(100, MODEL_SAMPLE_RATE)

    # Generate random labels (0-11 for the 12 semitones)
    y = np.random.randint(0, 12, size=(100,))

    return X, y

def train_model(model, X, y, epochs=10):
    """
    Trains the model on the provided data.
    """
    model.fit(X, y, epochs=epochs, validation_split=0.2)
    return model

def save_model(model, output_dir):
    """
    Saves the model in the TensorFlow SavedModel format.
    """
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    # Save the model with .keras extension
    keras_path = os.path.join(output_dir, MODEL_NAME + ".keras")
    model.save(keras_path)

    # Convert to .pb format using the correct converter for Keras models
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    tflite_model = converter.convert()

    # Save the .pb file
    pb_path = os.path.join(output_dir, f"{MODEL_NAME}.pb")
    with open(pb_path, 'wb') as f:
        f.write(tflite_model)

    print(f"Model saved to {keras_path}")
    print(f"Model also saved as .pb file: {pb_path}")

def main():
    """
    Main function to create, train, and save the model.
    """
    # Check if command line arguments were provided
    args = sys.argv[1:]
    output_dir = "models"

    # If the new scripts are available, use them
    if USE_NEW_SCRIPTS:
        warnings.warn(
            "\nNOTICE: Enhanced training scripts are available!\n"
            "For more advanced features, please use the new scripts in the 'scripts' directory:\n"
            f"python {NEW_SCRIPT_PATH} --mode=full --input_dir=path/to/raw/data --output_dir=path/to/output\n"
            "Run with --help for more options.\n"
            "Falling back to simple implementation for backward compatibility."
        )

        # If arguments were provided, try to use the new script
        if args:
            try:
                print("Using enhanced training scripts...")
                cmd = [sys.executable, str(NEW_SCRIPT_PATH)] + args
                subprocess.run(cmd, check=True)
                return
            except Exception as e:
                print(f"Error running enhanced scripts: {e}")
                print("Falling back to simple implementation.")

    # Use the original implementation
    print("Creating model...")
    model = create_simple_model()

    print("Generating dummy data...")
    X, y = generate_dummy_data()

    print("Training model...")
    model = train_model(model, X, y)

    print("Saving model...")
    save_model(model, output_dir)

    print("Done!")
    print("\nNOTE: This is a simple implementation that uses random data.")
    print("For a more comprehensive implementation with real audio data, use the scripts in the 'scripts' directory.")
    print(f"python {NEW_SCRIPT_PATH} --mode=full --input_dir=path/to/raw/data --output_dir=path/to/output")

if __name__ == "__main__":
    main()
