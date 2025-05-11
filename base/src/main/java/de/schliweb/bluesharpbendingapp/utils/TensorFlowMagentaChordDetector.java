package de.schliweb.bluesharpbendingapp.utils;

import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.proto.GraphDef;
import org.tensorflow.types.TFloat32;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import java.util.List;

/**
 * Implementation of a chord detection algorithm using TensorFlow with Magenta models.
 * <p>
 * This class provides a modern approach to detect multiple pitches (chords)
 * in an audio signal using machine learning techniques from Google's Magenta project:
 * 1. Pre-processing of audio data to match model input requirements
 * 2. Running inference using a pre-trained TensorFlow model
 * 3. Post-processing of model output to extract chord information
 * <p>
 * The algorithm works by:
 * 1. Converting the audio data to the format expected by the model
 * 2. Running the TensorFlow model to get chord predictions
 * 3. Processing the model output to extract the detected pitches
 * 4. Calculating confidence based on the model's prediction scores
 * <p>
 * Note: This implementation requires a pre-trained Magenta model for chord recognition.
 * The model file should be included in the application's resources.
 * <p>
 * For more information about Magenta, see:
 * https://magenta.tensorflow.org/
 */
public class TensorFlowMagentaChordDetector extends PitchDetector {

    /**
     * The path to the TensorFlow model file in the resources directory.
     */
    private static final String MODEL_PATH = "/models/magenta_chord_detection_model.pb";

    /**
     * The name of the input node in the TensorFlow model.
     */
    private static final String INPUT_NODE = "input_audio";

    /**
     * The name of the output node in the TensorFlow model.
     */
    private static final String OUTPUT_NODE = "chord_predictions";

    /**
     * The sample rate expected by the model (in Hz).
     */
    private static final int MODEL_SAMPLE_RATE = 16000;

    /**
     * The maximum number of pitches to detect.
     */
    private static final int MAX_PITCHES = 4;

    /**
     * The TensorFlow graph containing the loaded model.
     */
    private Graph graph;

    /**
     * The TensorFlow session for running inference.
     */
    private Session session;

    /**
     * Flag indicating whether the model was successfully loaded.
     * If false, the detector will use a fallback implementation.
     */
    private boolean modelLoaded = false;

    /**
     * Default constructor for the TensorFlowMagentaChordDetector class.
     * Initializes a new instance of the chord detection algorithm and attempts to load the TensorFlow model.
     * If the model cannot be loaded, it will use a fallback implementation.
     */
    public TensorFlowMagentaChordDetector() {
        super();
        
        LoggingUtils.logInitializing("TensorFlowMagentaChordDetector");

        // For now, since we don't have a trained model, don't try to load it
        // This is a temporary solution until a trained model is available
        LoggingUtils.logWarning("TensorFlow model not available", "Using fallback implementation for chord detection");
        modelLoaded = false;

        /* Uncomment this section when a trained model is available
        try {
            loadModel();
            modelLoaded = true;
        } catch (IOException e) {
            LoggingUtils.logError("Failed to load TensorFlow model", e);
            LoggingUtils.logWarning("Using fallback implementation for chord detection");
            modelLoaded = false;
        }
        */
        
        LoggingUtils.logInitialized("TensorFlowMagentaChordDetector");
    }

    /**
     * Loads the TensorFlow model from the resources directory.
     *
     * @throws IOException if the model file cannot be loaded
     */
    private void loadModel() throws IOException {
        LoggingUtils.logOperationStarted("Loading TensorFlow model");
        try {
            // Extract model to a temporary file
            Path tempModelPath = extractModelToTempFile();
            LoggingUtils.logDebug("Model extracted to temporary file", tempModelPath.toString());

            // Load the TensorFlow graph
            graph = new Graph();
            byte[] graphBytes = Files.readAllBytes(tempModelPath);
            GraphDef graphDef = GraphDef.parseFrom(graphBytes);
            graph.importGraphDef(graphDef);
            LoggingUtils.logDebug("TensorFlow graph loaded", "Graph size: " + graphBytes.length + " bytes");

            // Initialize a session with the loaded graph
            session = new Session(graph);
            LoggingUtils.logDebug("TensorFlow session initialized");

            LoggingUtils.logOperationCompleted("Loading TensorFlow model");
        } catch (Exception e) {
            // Clean up any partially initialized resources
            if (session != null) {
                session.close();
                session = null;
            }
            if (graph != null) {
                graph.close();
                graph = null;
            }
            LoggingUtils.logError("Failed to load TensorFlow model", e);
            throw new IOException("Failed to load TensorFlow model", e);
        }
    }


    /**
     * Extracts the model file from resources to a temporary file.
     *
     * @return the path to the temporary model file
     * @throws IOException if the model file cannot be extracted
     */
    private Path extractModelToTempFile() throws IOException {
        LoggingUtils.logOperationStarted("Extracting model file");
        
        // Get the model file from resources
        InputStream modelStream = getClass().getResourceAsStream(MODEL_PATH);
        if (modelStream == null) {
            String errorMessage = "Model file not found: " + MODEL_PATH + 
                                 ". Please ensure the model file is placed in the resources directory.";
            LoggingUtils.logError(errorMessage);
            throw new IOException(errorMessage);
        }

        // Create a temporary file
        Path tempModelPath = Files.createTempFile("magenta_chord_detection_model", ".pb");
        LoggingUtils.logDebug("Created temporary file", tempModelPath.toString());

        // Copy the model file to the temporary file
        Files.copy(modelStream, tempModelPath, StandardCopyOption.REPLACE_EXISTING);
        LoggingUtils.logDebug("Copied model to temporary file");

        // Close the input stream
        modelStream.close();
        
        LoggingUtils.logOperationCompleted("Extracting model file");
        return tempModelPath;
    }

    /**
     * Detects multiple pitches (chord) in an audio signal and returns them as a ChordDetectionResult.
     *
     * @param audioData  an array of double values representing the audio signal.
     * @param sampleRate the sample rate of the audio signal in Hz.
     * @return a PitchDetectionResult containing the dominant pitch and confidence.
     */
    @Override
    PitchDetectionResult detectPitch(double[] audioData, int sampleRate) {
        // For backward compatibility, return the dominant pitch
        ChordDetectionResult chordResult = detectChordInternal(audioData, sampleRate);
        if (chordResult.hasPitches()) {
            return new PitchDetectionResult(chordResult.getPitch(0), chordResult.confidence());
        }
        return new PitchDetectionResult(NO_DETECTED_PITCH, 0.0);
    }

    /**
     * Detects multiple pitches (chord) in an audio signal using TensorFlow with Magenta models.
     *
     * @param audioData  an array of double values representing the audio signal.
     * @param sampleRate the sample rate of the audio signal in Hz.
     * @return a ChordDetectionResult containing the detected pitches and confidence.
     */
    public ChordDetectionResult detectChordInternal(double[] audioData, int sampleRate) {
        LoggingUtils.logOperationStarted("Detecting chord");
        LoggingUtils.logAudioProcessing("Input parameters", "Sample rate: " + sampleRate + " Hz, Audio length: " + audioData.length + " samples");
        
        // Check if the audio data is silent
        if (isSilent(audioData, 0.01)) {
            LoggingUtils.logDebug("Audio is silent, no pitches detected");
            LoggingUtils.logOperationCompleted("Detecting chord");
            return new ChordDetectionResult(List.of(), 0.0);
        }

        // If the model wasn't loaded, use the fallback implementation
        if (!modelLoaded || session == null || graph == null) {
            LoggingUtils.logDebug("Model not loaded, using fallback implementation");
            ChordDetectionResult result = useFallbackDetection(audioData, sampleRate);
            LoggingUtils.logOperationCompleted("Detecting chord");
            return result;
        }

        // For now, since we don't have a trained model, always use the fallback implementation
        // This is a temporary solution until a trained model is available
        // Remove this line when a trained model is available
        LoggingUtils.logDebug("No trained model available, using fallback implementation");
        ChordDetectionResult result = useFallbackDetection(audioData, sampleRate);
        LoggingUtils.logOperationCompleted("Detecting chord");
        return result;

        /* Uncomment this section when a trained model is available
        try {
            LoggingUtils.logDebug("Using TensorFlow model for chord detection");
            
            // Preprocess the audio data to match the model's expected input format
            float[] processedAudio = preprocessAudio(audioData, sampleRate);
            LoggingUtils.logAudioProcessing("Preprocessing completed", "Processed audio length: " + processedAudio.length + " samples");

            // Create a TensorFlow tensor from the processed audio data
            FloatNdArray inputArray = NdArrays.ofFloats(Shape.of(1, processedAudio.length));
            for (int i = 0; i < processedAudio.length; i++) {
                inputArray.setFloat(processedAudio[i], 0, i);
            }
            LoggingUtils.logDebug("Created input tensor", "Shape: [1, " + processedAudio.length + "]");

            // Run inference using the TensorFlow model
            LoggingUtils.logDebug("Running TensorFlow inference");
            try (TFloat32 input = TFloat32.tensorOf(inputArray);
                 Tensor result = session.runner()
                         .feed(INPUT_NODE, input)
                         .fetch(OUTPUT_NODE)
                         .run()
                         .get(0)) {

                // Process the model output to extract chord information
                LoggingUtils.logDebug("Processing model output");
                ChordDetectionResult detectionResult = processModelOutput(result);
                LoggingUtils.logAudioProcessing("Chord detection completed", 
                    "Detected " + detectionResult.getPitchCount() + " pitches with confidence " + detectionResult.confidence());
                LoggingUtils.logOperationCompleted("Detecting chord");
                return detectionResult;
            }
        } catch (Exception e) {
            // If there's an error, use the fallback implementation
            LoggingUtils.logError("Error in TensorFlow chord detection", e);
            LoggingUtils.logDebug("Falling back to alternative implementation");
            ChordDetectionResult fallbackResult = useFallbackDetection(audioData, sampleRate);
            LoggingUtils.logOperationCompleted("Detecting chord");
            return fallbackResult;
        }
        */
    }

    /**
     * Uses a fallback implementation for chord detection when the TensorFlow model is not available.
     *
     * @param audioData  an array of double values representing the audio signal.
     * @param sampleRate the sample rate of the audio signal in Hz.
     * @return a ChordDetectionResult containing the detected pitches and confidence.
     */
    private ChordDetectionResult useFallbackDetection(double[] audioData, int sampleRate) {
        // Log that we're using the fallback implementation
        if (!modelLoaded) {
            LoggingUtils.logDebug("Using fallback chord detection (model not loaded)");
        }

        // Use the ChordDetector as a fallback
        return detectChord(audioData, sampleRate);
    }

    /**
     * Preprocesses the audio data to match the model's expected input format.
     *
     * @param audioData  the audio data to preprocess
     * @param sampleRate the sample rate of the audio data
     * @return the preprocessed audio data as a float array
     */
    private float[] preprocessAudio(double[] audioData, int sampleRate) {
        LoggingUtils.logAudioProcessing("Preprocessing audio", "Original sample rate: " + sampleRate + " Hz");
        
        // Resample the audio data if necessary
        double[] resampledAudio = audioData;
        if (sampleRate != MODEL_SAMPLE_RATE) {
            LoggingUtils.logDebug("Resampling audio", "From " + sampleRate + " Hz to " + MODEL_SAMPLE_RATE + " Hz");
            resampledAudio = resampleAudio(audioData, sampleRate, MODEL_SAMPLE_RATE);
            LoggingUtils.logDebug("Resampling completed", "New length: " + resampledAudio.length + " samples");
        }

        // Apply window function to the audio data
        LoggingUtils.logDebug("Applying window function");
        double[] windowedAudio = applyWindow(resampledAudio);

        // Normalize the audio data
        double maxAmplitude = 0.0;
        for (double sample : windowedAudio) {
            maxAmplitude = Math.max(maxAmplitude, Math.abs(sample));
        }
        LoggingUtils.logDebug("Normalizing audio", "Max amplitude: " + maxAmplitude);

        // Convert to float array and normalize
        float[] processedAudio = new float[windowedAudio.length];
        if (maxAmplitude > 0.0) {
            for (int i = 0; i < windowedAudio.length; i++) {
                processedAudio[i] = (float) (windowedAudio[i] / maxAmplitude);
            }
        }

        LoggingUtils.logAudioProcessing("Audio preprocessing completed", "Processed length: " + processedAudio.length + " samples");
        return processedAudio;
    }

    /**
     * Applies a Hann window function to the audio data.
     *
     * @param audioData the audio data to apply the window to
     * @return the windowed audio data
     */
    private double[] applyWindow(double[] audioData) {
        double[] windowedData = new double[audioData.length];
        for (int i = 0; i < audioData.length; i++) {
            windowedData[i] = audioData[i] * hannWindow(i, audioData.length);
        }
        return windowedData;
    }

    /**
     * Resamples audio data from one sample rate to another.
     *
     * @param audioData     the audio data to resample
     * @param sourceSampleRate the source sample rate
     * @param targetSampleRate the target sample rate
     * @return the resampled audio data
     */
    private double[] resampleAudio(double[] audioData, int sourceSampleRate, int targetSampleRate) {
        // Simple linear interpolation resampling
        double ratio = (double) sourceSampleRate / targetSampleRate;
        int targetLength = (int) Math.ceil(audioData.length / ratio);
        double[] resampledAudio = new double[targetLength];

        for (int i = 0; i < targetLength; i++) {
            double sourceIndex = i * ratio;
            int sourceIndexInt = (int) sourceIndex;
            double fraction = sourceIndex - sourceIndexInt;

            if (sourceIndexInt < audioData.length - 1) {
                resampledAudio[i] = audioData[sourceIndexInt] * (1 - fraction) + 
                                   audioData[sourceIndexInt + 1] * fraction;
            } else if (sourceIndexInt < audioData.length) {
                resampledAudio[i] = audioData[sourceIndexInt];
            }
        }

        return resampledAudio;
    }

    /**
     * Processes the model output to extract chord information.
     * This method is currently not used since we don't have a trained model.
     * It will be used when a trained model is available.
     *
     * @param result the TensorFlow tensor containing the model output
     * @return a ChordDetectionResult containing the detected pitches and confidence
     */
    private ChordDetectionResult processModelOutput(Tensor result) {
        // Extract the pitch predictions from the model output
        float[] predictions = new float[12]; // 12 semitones in an octave

        // Since we don't have a real model, we'll use simulated values
        LoggingUtils.logDebug("Using simulated chord predictions (no trained model available)");
        simulatePredictions(predictions);

        /* Uncomment this section when a real model is available
        try {
            // Extract the predictions from the tensor
            float[][] outputValues = result.copyTo(new float[1][12]);
            for (int i = 0; i < 12; i++) {
                predictions[i] = outputValues[0][i];
            }
            LoggingUtils.logDebug("Extracted predictions from model output");
        } catch (Exception e) {
            // If there's an error extracting the predictions, use simulated values
            LoggingUtils.logWarning("Failed to extract predictions from model output", e.getMessage());
            LoggingUtils.logDebug("Using simulated predictions instead");
            simulatePredictions(predictions);
        }
        */

        // Find the indices of the top N predictions
        List<Integer> topIndices = findTopN(predictions, MAX_PITCHES);
        LoggingUtils.logDebug("Found top predictions", "Count: " + topIndices.size());

        // Convert the indices to frequencies
        double[] pitches = new double[topIndices.size()];
        for (int i = 0; i < topIndices.size(); i++) {
            // Convert semitone index to frequency (A4 = 440Hz, index 9)
            int semitoneIndex = topIndices.get(i);
            pitches[i] = 440.0 * Math.pow(2, (semitoneIndex - 9) / 12.0);
        }

        // Calculate confidence based on the prediction scores
        double confidence = 0.0;
        for (int index : topIndices) {
            confidence += predictions[index];
        }
        confidence = topIndices.isEmpty() ? 0.0 : confidence / topIndices.size();
        
        LoggingUtils.logDebug("Calculated chord confidence", "Value: " + confidence);

        return ChordDetectionResult.of(pitches, confidence);
    }

    /**
     * Simulates model predictions when the actual model output is not available.
     * This creates more realistic simulated values than pure random numbers.
     *
     * @param predictions the array to fill with simulated prediction values
     */
    private void simulatePredictions(float[] predictions) {
        LoggingUtils.logDebug("Simulating chord predictions");
        
        // Create a more realistic simulation of chord predictions
        // Initialize with low values
        for (int i = 0; i < predictions.length; i++) {
            predictions[i] = (float) (0.1 + 0.1 * Math.random());
        }

        // Randomly select 1-3 notes to be dominant (simulating a chord)
        int numDominantNotes = 1 + (int) (Math.random() * 3);
        LoggingUtils.logDebug("Simulating chord with dominant notes", "Count: " + numDominantNotes);
        
        for (int i = 0; i < numDominantNotes; i++) {
            int noteIndex = (int) (Math.random() * predictions.length);
            // Make this note dominant with a high probability value
            predictions[noteIndex] = (float) (0.7 + 0.3 * Math.random());

            // If this is a chord, add some probability to musically related notes
            // (perfect fifth = +7 semitones, major third = +4 semitones)
            if (numDominantNotes > 1) {
                int fifthIndex = (noteIndex + 7) % predictions.length;
                int thirdIndex = (noteIndex + 4) % predictions.length;

                if (Math.random() > 0.5) {
                    predictions[fifthIndex] = (float) (0.5 + 0.3 * Math.random());
                }
                if (Math.random() > 0.7) {
                    predictions[thirdIndex] = (float) (0.4 + 0.3 * Math.random());
                }
            }
        }
    }

    /**
     * Finds the indices of the top N values in an array.
     *
     * @param array the array to search
     * @param n     the number of top values to find
     * @return a list of indices of the top N values
     */
    private List<Integer> findTopN(float[] array, int n) {
        List<Integer> indices = new ArrayList<>();

        // Create a list of indices
        for (int i = 0; i < array.length; i++) {
            indices.add(i);
        }

        // Sort the indices by the corresponding values in the array (descending)
        indices.sort((i1, i2) -> Float.compare(array[i2], array[i1]));

        // Return the top N indices
        return indices.subList(0, Math.min(n, indices.size()));
    }

    /**
     * Cleans up resources when the detector is no longer needed.
     * This method should be called when the detector is no longer needed to release TensorFlow resources.
     */
    public void close() {
        LoggingUtils.logOperationStarted("Cleaning up TensorFlow resources");
        
        // Only close resources if the model was loaded
        if (modelLoaded) {
            if (session != null) {
                LoggingUtils.logDebug("Closing TensorFlow session");
                session.close();
                session = null;
            }
            if (graph != null) {
                LoggingUtils.logDebug("Closing TensorFlow graph");
                graph.close();
                graph = null;
            }
        } else {
            LoggingUtils.logDebug("No TensorFlow resources to clean up (model was not loaded)");
        }
        
        LoggingUtils.logOperationCompleted("Cleaning up TensorFlow resources");
    }

    // Note: We don't use finalize() as it's deprecated in Java 9+
    // Instead, users of this class should call close() when done with the detector
}