# Using the ONNX Model in the Bluesharp Bending App

This document provides instructions for using the ONNX model in the Bluesharp Bending App. It includes information about the test scripts created to verify the ONNX model's functionality and examples of how to use the ONNX model in your own code.

## Overview

The Bluesharp Bending App now supports using models in ONNX (Open Neural Network Exchange) format. ONNX is an open format to represent deep learning models, providing better cross-platform compatibility and performance optimization.

Benefits of using ONNX include:
- **Cross-platform compatibility**: Run the same model on different frameworks and hardware
- **Performance optimization**: ONNX Runtime provides optimized inference across different platforms
- **Reduced memory footprint**: ONNX models can be smaller than their TensorFlow counterparts
- **Faster startup time**: ONNX models can load faster than TensorFlow models
- **Wider ecosystem support**: Many tools and libraries support ONNX format

## Test Scripts

Three test scripts have been created to verify the ONNX model's functionality:

1. **test_onnx_simple.py**: A simple script that demonstrates how to use the ONNX model with a single audio file
2. **test_onnx_model.py**: A comprehensive test script that tests the ONNX model on multiple audio files
3. **compare_tf_onnx_models.py**: A script that compares the predictions of the TensorFlow and ONNX models

### Running the Test Scripts

To run the test scripts, you need to have the following dependencies installed:

```bash
pip install onnxruntime numpy librosa tqdm
```

For GPU acceleration, you can install:

```bash
pip install onnxruntime-gpu
```

#### Simple Test

The simple test script demonstrates how to use the ONNX model with a single audio file:

```bash
python docs/scripts/test_onnx_simple.py [audio_file]
```

If no audio file is provided, the script will use a default test file.

#### Comprehensive Test

The comprehensive test script tests the ONNX model on multiple audio files:

```bash
python docs/scripts/test_onnx_model.py
```

This script will test the ONNX model on all chord files found in the training data directory.

#### Comparison Test

The comparison test script compares the predictions of the TensorFlow and ONNX models:

```bash
python docs/scripts/compare_tf_onnx_models.py [--num_files=N]
```

By default, the script will test on 10 randomly selected files. You can specify a different number of files using the `--num_files` parameter.

## Using the ONNX Model in Your Code

To use the ONNX model in your own code, you need to:

1. Install the ONNX Runtime:
   ```bash
   pip install onnxruntime
   ```

2. Load the ONNX model:
   ```python
   import onnxruntime as ort

   # Load the ONNX model
   onnx_model_path = "path/to/model.onnx"
   session = ort.InferenceSession(onnx_model_path)
   input_name = session.get_inputs()[0].name
   ```

3. Extract features from audio:
   ```python
   import numpy as np
   import librosa

   def extract_features(audio_file):
       # Load audio file
       y, sr = librosa.load(audio_file, sr=16000, mono=True)

       # Extract features
       mfcc = librosa.feature.mfcc(y=y, sr=sr, n_mfcc=13)
       chroma = librosa.feature.chroma_cqt(y=y, sr=sr)
       contrast = librosa.feature.spectral_contrast(y=y, sr=sr)

       # Combine features
       mfcc_mean = np.mean(mfcc, axis=1)
       chroma_mean = np.mean(chroma, axis=1)
       contrast_mean = np.mean(contrast, axis=1)

       features = np.concatenate([mfcc_mean, chroma_mean, contrast_mean])
       return features.reshape(1, -1).astype(np.float32)
   ```

4. Run inference with the ONNX model:
   ```python
   # Extract features
   features = extract_features(audio_file)

   # Run inference with ONNX Runtime
   prediction = session.run(None, {input_name: features})[0]
   ```

5. Process the predictions:
   ```python
   # Get the top 5 predicted notes
   top_indices = np.argsort(prediction[0])[-5:][::-1]
   top_confidences = prediction[0][top_indices]

   # Map indices to note names
   note_names = ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B']
   top_notes = []
   for idx in top_indices:
       octave = idx // 12
       semitone = idx % 12
       top_notes.append(f"{note_names[semitone]}{octave}")

   # Print results
   print(f"Detected notes: {top_notes}")
   print(f"Confidences: {top_confidences}")
   ```

## Integration with the Bluesharp Bending App

The ONNX model can be integrated into the Bluesharp Bending App by:

1. Adding a new model provider that uses the ONNX Runtime
2. Configuring the app to use the ONNX model instead of the TensorFlow model
3. Ensuring that the ONNX Runtime is available on the target platform

For more information about the ONNX format and ONNX Runtime, see:
- [ONNX GitHub Repository](https://github.com/onnx/onnx)
- [ONNX Runtime Documentation](https://onnxruntime.ai/)

## Model Retraining

The ONNX model has been retrained to improve its accuracy and performance. The model has been tested and verified to work correctly with the current implementation.

### Verification

The model has been verified through unit tests and benchmark comparisons:

1. `OnnxChordDetectorSimpleTest`: Tests that the detector correctly identifies notes in a C major chord (C4, E4, G4)
2. `ChordDetectorBenchmark`: Compares the performance and accuracy of the ONNX-based detector with the spectral-based detector
3. `OnnxChordDetectorAllChordsTest`: Tests that the detector correctly identifies chords in all the training data files

All tests confirm that the detector produces accurate results and can recognize at least 80% of the training data.

## Performance Comparison

The ONNX model generally provides better performance than the TensorFlow model, especially on mobile devices and embedded systems. The comparison test script can be used to compare the predictions of the TensorFlow and ONNX models to ensure that they produce similar results.

Typical performance improvements include:
- Faster loading time
- Reduced memory usage
- Faster inference time
- Better hardware acceleration on some platforms

## Troubleshooting

If you encounter issues with the ONNX model, check the following:

1. Ensure that the ONNX Runtime is installed:
   ```bash
   pip install onnxruntime
   ```

2. Check that the ONNX model file exists and is accessible

3. Verify that the input features are in the correct format (float32)

4. Check the ONNX Runtime version compatibility with your platform

5. For GPU acceleration, ensure that the appropriate GPU drivers are installed

If you continue to experience issues, please report them on the project's issue tracker.
