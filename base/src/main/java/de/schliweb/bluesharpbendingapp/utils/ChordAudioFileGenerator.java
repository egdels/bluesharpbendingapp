package de.schliweb.bluesharpbendingapp.utils;

import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.ChordHarmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.Harmonica;
import de.schliweb.bluesharpbendingapp.model.harmonica.NoteLookup;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Utility class for generating audio files for harmonica chords.
 * <p>
 * This class provides methods to create WAV audio files for all possible chords
 * on a harmonica. These audio files can be used for chord recognition training.
 * The generated files use the same audio format as the application's microphone
 * input (44.1 kHz, 16-bit, mono).
 */
public class ChordAudioFileGenerator {

    /**
     * The sample rate for the generated audio files (44.1 kHz).
     */
    private static final int SAMPLE_RATE = 44100;

    /**
     * The number of bits per sample for the generated audio files (16-bit).
     */
    private static final int BITS_PER_SAMPLE = 16;

    /**
     * The duration of each generated chord audio file in seconds.
     */
    private static final double DURATION = 2.0;

    /**
     * Random number generator for creating variations.
     */
    private static final Random RANDOM = new Random();

    /**
     * Generates WAV audio files for all possible chords on a harmonica.
     *
     * @param outputDirectory the directory where the audio files will be saved
     * @param key             the key of the harmonica (e.g., "C", "A")
     * @param tune            the tuning of the harmonica (e.g., "RICHTER", "COUNTRY")
     * @throws IOException if an error occurs while writing the audio files
     */
    public static void generateChordAudioFiles(String outputDirectory, String key, String tune) throws IOException {
        // Create the required directory structure: data/raw/key_X/chords/
        String chordsDirectory = String.format("%s/data/raw/key_%s/chords", outputDirectory, key);
        File directory = new File(chordsDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Create a harmonica instance with the specified key and tune
        Harmonica harmonica = AbstractHarmonica.create(key, tune);

        // Get all possible chords for the harmonica
        List<ChordHarmonica> chords = harmonica.getPossibleChords();

        // Generate an audio file for each chord
        for (int i = 0; i < chords.size(); i++) {
            ChordHarmonica chord = chords.get(i);
            String chordName = getChordName(chord);
            String fileName = String.format("%s/chord_%s_%d.wav", 
                    chordsDirectory, chordName, i + 1);
            generateChordAudioFile(chord, fileName);
        }
    }

    /**
     * Generates WAV audio files with variations for all possible chords on a harmonica.
     *
     * @param outputDirectory the directory where the audio files will be saved
     * @param key             the key of the harmonica (e.g., "C", "A")
     * @param tune            the tuning of the harmonica (e.g., "RICHTER", "COUNTRY")
     * @param variations      the number of variations to generate for each chord (1-8)
     * @throws IOException if an error occurs while writing the audio files
     */
    public static void generateChordAudioFilesWithVariations(String outputDirectory, String key, String tune, int variations) throws IOException {
        // Create the required directory structure: data/raw/key_X/chords_variations/
        String chordsDirectory = String.format("%s/data/raw/key_%s/chords_variations", outputDirectory, key);
        File directory = new File(chordsDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Create a harmonica instance with the specified key and tune
        Harmonica harmonica = AbstractHarmonica.create(key, tune);

        // Get all possible chords for the harmonica
        List<ChordHarmonica> chords = harmonica.getPossibleChords();

        // Generate audio files with variations for each chord
        for (int i = 0; i < chords.size(); i++) {
            ChordHarmonica chord = chords.get(i);
            String chordName = getChordName(chord);
            String baseFileName = String.format("%s/chord_%s_%d", 
                    chordsDirectory, chordName, i + 1);

            // Generate the standard version
            generateChordAudioFile(chord, baseFileName + ".wav");

            // Generate variations
            for (int v = 0; v < variations && v < 8; v++) {
                generateChordAudioFileWithVariation(chord, baseFileName, v);
            }
        }

        System.out.println("Generated chord audio files with " + variations + " variations for key " + key);
    }

    /**
     * Extracts a chord name from a ChordHarmonica object.
     * The chord name is in the format "C4-E4-G4" or similar, listing all notes in the chord.
     *
     * @param chord the ChordHarmonica object
     * @return the chord name as a string
     */
    private static String getChordName(ChordHarmonica chord) {
        List<Double> frequencies = chord.getTones();
        List<String> noteNames = new ArrayList<>();

        for (Double frequency : frequencies) {
            String noteName = NoteLookup.getNoteName(frequency);
            if (noteName != null) {
                noteNames.add(noteName);
            }
        }

        // Join the note names with hyphens
        return String.join("-", noteNames);
    }

    /**
     * Generates a WAV audio file for a specific chord.
     *
     * @param chord    the chord for which to generate an audio file
     * @param fileName the name of the output file
     * @throws IOException if an error occurs while writing the audio file
     */
    public static void generateChordAudioFile(ChordHarmonica chord, String fileName) throws IOException {
        // Get the frequencies of the chord
        List<Double> frequencies = chord.getTones();

        // Generate audio data for the chord
        double[] audioData = generateChordAudioData(frequencies, SAMPLE_RATE, DURATION);

        // Convert the audio data to a byte array
        byte[] byteData = convertToByteArray(audioData);

        // Create an audio format object
        AudioFormat audioFormat = new AudioFormat(SAMPLE_RATE, BITS_PER_SAMPLE, 1, true, true);

        // Create an audio input stream from the byte array
        AudioInputStream audioInputStream = new AudioInputStream(
                new ByteArrayInputStream(byteData),
                audioFormat,
                byteData.length / audioFormat.getFrameSize());

        // Write the audio data to a WAV file
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File(fileName));
    }

    /**
     * Generates a WAV audio file for a specific chord with variations.
     *
     * @param chord       the chord for which to generate an audio file
     * @param fileName    the base name of the output file (without extension)
     * @param variationId the ID of the variation to apply
     * @throws IOException if an error occurs while writing the audio file
     */
    public static void generateChordAudioFileWithVariation(ChordHarmonica chord, String fileName, int variationId) throws IOException {
        // Get the frequencies of the chord
        List<Double> frequencies = chord.getTones();

        // Generate audio data for the chord
        double[] audioData = generateChordAudioData(frequencies, SAMPLE_RATE, DURATION);

        // Apply variation based on variationId
        double[] variedData;
        String variationName;

        switch (variationId) {
            case 0: // Light noise
                variedData = addNoise(audioData, 0.05);
                variationName = "light_noise";
                break;
            case 1: // Medium noise
                variedData = addNoise(audioData, 0.1);
                variationName = "medium_noise";
                break;
            case 2: // Heavy noise
                variedData = addNoise(audioData, 0.2);
                variationName = "heavy_noise";
                break;
            case 3: // Soft amplitude (quieter)
                variedData = varyAmplitude(audioData, 0.5);
                variationName = "soft";
                break;
            case 4: // Loud amplitude (louder)
                variedData = varyAmplitude(audioData, 0.8);
                variationName = "loud";
                break;
            case 5: // Increasing amplitude
                variedData = createAmplitudeVariation(audioData, 0);
                variationName = "increasing";
                break;
            case 6: // Decreasing amplitude
                variedData = createAmplitudeVariation(audioData, 1);
                variationName = "decreasing";
                break;
            case 7: // Varying amplitude
                variedData = createAmplitudeVariation(audioData, 2);
                variationName = "varying";
                break;
            default:
                variedData = audioData;
                variationName = "standard";
        }

        // Convert the audio data to a byte array
        byte[] byteData = convertToByteArray(variedData);

        // Create an audio format object
        AudioFormat audioFormat = new AudioFormat(SAMPLE_RATE, BITS_PER_SAMPLE, 1, true, true);

        // Create an audio input stream from the byte array
        AudioInputStream audioInputStream = new AudioInputStream(
                new ByteArrayInputStream(byteData),
                audioFormat,
                byteData.length / audioFormat.getFrameSize());

        // Create the full file name with variation
        String fullFileName = fileName + "_" + variationName + ".wav";

        // Write the audio data to a WAV file
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File(fullFileName));
    }

    /**
     * Generates audio data for a chord with the specified frequencies.
     *
     * @param frequencies the frequencies of the chord tones
     * @param sampleRate  the sample rate of the audio data
     * @param duration    the duration of the audio data in seconds
     * @return an array of doubles representing the audio data
     */
    private static double[] generateChordAudioData(List<Double> frequencies, int sampleRate, double duration) {
        // Convert the list of frequencies to an array
        double[] freqArray = new double[frequencies.size()];
        for (int i = 0; i < frequencies.size(); i++) {
            freqArray[i] = frequencies.get(i);
        }

        // Create an array of amplitudes (equal amplitude for each frequency)
        double[] amplitudes = new double[frequencies.size()];
        double amplitude = 1.0 / frequencies.size(); // Normalize amplitude to prevent clipping
        for (int i = 0; i < amplitudes.length; i++) {
            amplitudes[i] = amplitude;
        }

        // Generate the audio data using our own implementation
        return generateMultipleFrequencySignal(freqArray, amplitudes, sampleRate, duration);
    }

    /**
     * Generates a signal with multiple frequency components and specified amplitudes.
     * <p>
     * This method creates a complex waveform by combining multiple sine waves with
     * different frequencies and amplitudes. This is useful for generating chord sounds
     * that have multiple frequency components.
     *
     * @param frequencies an array of frequencies in Hz
     * @param amplitudes  an array of amplitudes for each frequency
     * @param sampleRate  the sample rate in Hz
     * @param duration    the duration in seconds
     * @return an array of double values representing the complex signal
     * @throws IllegalArgumentException if frequencies and amplitudes arrays have different lengths
     */
    private static double[] generateMultipleFrequencySignal(double[] frequencies, double[] amplitudes, 
                                                         int sampleRate, double duration) {
        if (frequencies.length != amplitudes.length) {
            throw new IllegalArgumentException("Frequencies and amplitudes arrays must have the same length");
        }

        int samples = (int) (sampleRate * duration);
        double[] audioData = new double[samples];

        for (int i = 0; i < samples; i++) {
            double t = (double) i / sampleRate;
            double sample = 0.0;

            for (int j = 0; j < frequencies.length; j++) {
                sample += amplitudes[j] * Math.sin(2 * Math.PI * frequencies[j] * t);
            }

            audioData[i] = sample;
        }

        return audioData;
    }

    /**
     * Converts an array of doubles to a byte array suitable for WAV file creation.
     *
     * @param audioData the audio data as an array of doubles in the range [-1.0, 1.0]
     * @return a byte array representing the audio data
     */
    private static byte[] convertToByteArray(double[] audioData) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(audioData.length * 2);
        byteBuffer.order(ByteOrder.BIG_ENDIAN);

        for (double sample : audioData) {
            // Clip the sample to the range [-1.0, 1.0]
            if (sample > 1.0) sample = 1.0;
            if (sample < -1.0) sample = -1.0;

            // Convert to 16-bit sample
            short sampleShort = (short) (sample * 32767.0);
            byteBuffer.putShort(sampleShort);
        }

        return byteBuffer.array();
    }

    /**
     * Adds white noise to audio data.
     *
     * @param audioData  the original audio data
     * @param noiseLevel the level of noise to add (0.0-1.0)
     * @return the audio data with added noise
     */
    private static double[] addNoise(double[] audioData, double noiseLevel) {
        double[] noisyData = new double[audioData.length];

        for (int i = 0; i < audioData.length; i++) {
            // Generate random noise between -1 and 1, scaled by noiseLevel
            double noise = (RANDOM.nextDouble() * 2 - 1) * noiseLevel;
            // Add noise to the original sample
            noisyData[i] = audioData[i] + noise;
            // Clip to ensure the result is within [-1.0, 1.0]
            if (noisyData[i] > 1.0) noisyData[i] = 1.0;
            if (noisyData[i] < -1.0) noisyData[i] = -1.0;
        }

        return noisyData;
    }

    /**
     * Varies the amplitude of audio data.
     *
     * @param audioData      the original audio data
     * @param amplitudeFactor the factor by which to multiply the amplitude (0.0-2.0)
     * @return the audio data with varied amplitude
     */
    private static double[] varyAmplitude(double[] audioData, double amplitudeFactor) {
        double[] variedData = new double[audioData.length];

        for (int i = 0; i < audioData.length; i++) {
            // Multiply the sample by the amplitude factor
            variedData[i] = audioData[i] * amplitudeFactor;
            // Clip to ensure the result is within [-1.0, 1.0]
            if (variedData[i] > 1.0) variedData[i] = 1.0;
            if (variedData[i] < -1.0) variedData[i] = -1.0;
        }

        return variedData;
    }

    /**
     * Creates a variation of audio data with varying amplitude over time.
     *
     * @param audioData the original audio data
     * @param type      the type of amplitude variation (0: increasing, 1: decreasing, 2: random)
     * @return the audio data with varying amplitude
     */
    private static double[] createAmplitudeVariation(double[] audioData, int type) {
        double[] variedData = new double[audioData.length];

        for (int i = 0; i < audioData.length; i++) {
            double amplitudeFactor;

            switch (type) {
                case 0: // Increasing amplitude
                    amplitudeFactor = (double) i / audioData.length;
                    break;
                case 1: // Decreasing amplitude
                    amplitudeFactor = 1.0 - ((double) i / audioData.length);
                    break;
                case 2: // Random amplitude variations
                    amplitudeFactor = 0.5 + 0.5 * Math.sin(i * 0.001);
                    break;
                default:
                    amplitudeFactor = 1.0;
            }

            // Apply the amplitude factor
            variedData[i] = audioData[i] * amplitudeFactor;
        }

        return variedData;
    }

    /**
     * Generates WAV audio files for all required harmonica keys.
     * The required keys are C, A, D, G, F, and Bb.
     *
     * @param outputDirectory the directory where the audio files will be saved
     * @param tune            the tuning of the harmonica (e.g., "RICHTER", "COUNTRY")
     * @throws IOException if an error occurs while writing the audio files
     */
    public static void generateAllRequiredKeys(String outputDirectory, String tune) throws IOException {
        // Required keys according to MODEL_TRAINING_GUIDE.md
        String[] requiredKeys = {"C", "A", "D", "G", "F", "B_FLAT"};

        for (String key : requiredKeys) {
            System.out.println("Generating chord audio files for key " + key + "...");
            generateChordAudioFiles(outputDirectory, key, tune);
        }
    }

    /**
     * Generates WAV audio files for all possible harmonica keys.
     * This includes all keys defined in the AbstractHarmonica.KEY enum.
     *
     * @param outputDirectory the directory where the audio files will be saved
     * @param tune            the tuning of the harmonica (e.g., "RICHTER", "COUNTRY")
     * @throws IOException if an error occurs while writing the audio files
     */
    public static void generateAllPossibleKeys(String outputDirectory, String tune) throws IOException {
        // Get all possible keys from the AbstractHarmonica.KEY enum
        String[] allKeys = AbstractHarmonica.getSupporterKeys();

        for (String key : allKeys) {
            System.out.println("Generating chord audio files for key " + key + "...");
            generateChordAudioFiles(outputDirectory, key, tune);
        }
    }

    /**
     * Generates WAV audio files with variations for all required harmonica keys.
     * The required keys are C, A, D, G, F, and Bb.
     *
     * @param outputDirectory the directory where the audio files will be saved
     * @param tune            the tuning of the harmonica (e.g., "RICHTER", "COUNTRY")
     * @param variations      the number of variations to generate for each chord (1-8)
     * @throws IOException if an error occurs while writing the audio files
     */
    public static void generateAllRequiredKeysWithVariations(String outputDirectory, String tune, int variations) throws IOException {
        // Required keys according to MODEL_TRAINING_GUIDE.md
        String[] requiredKeys = {"C", "A", "D", "G", "F", "B_FLAT"};

        for (String key : requiredKeys) {
            System.out.println("Generating chord audio files with variations for key " + key + "...");
            generateChordAudioFilesWithVariations(outputDirectory, key, tune, variations);
        }
    }

    /**
     * Generates WAV audio files with variations for all possible harmonica keys.
     * This includes all keys defined in the AbstractHarmonica.KEY enum.
     *
     * @param outputDirectory the directory where the audio files will be saved
     * @param tune            the tuning of the harmonica (e.g., "RICHTER", "COUNTRY")
     * @param variations      the number of variations to generate for each chord (1-8)
     * @throws IOException if an error occurs while writing the audio files
     */
    public static void generateAllPossibleKeysWithVariations(String outputDirectory, String tune, int variations) throws IOException {
        // Get all possible keys from the AbstractHarmonica.KEY enum
        String[] allKeys = AbstractHarmonica.getSupporterKeys();

        for (String key : allKeys) {
            System.out.println("Generating chord audio files with variations for key " + key + "...");
            generateChordAudioFilesWithVariations(outputDirectory, key, tune, variations);
        }
    }

    /**
     * Main method for generating chord audio files from the command line.
     *
     * @param args command line arguments: outputDirectory [key] [tune] [variations]
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: ChordAudioFileGenerator <outputDirectory> [key] [tune] [variations]");
            System.out.println("Example 1: ChordAudioFileGenerator ./training_data");
            System.out.println("Example 2: ChordAudioFileGenerator ./training_data C RICHTER");
            System.out.println("Example 3: ChordAudioFileGenerator ./training_data C RICHTER 3");
            System.out.println("Example 4: ChordAudioFileGenerator ./training_data all RICHTER 5");
            System.out.println("Example 5: ChordAudioFileGenerator ./training_data all_possible RICHTER 3");
            System.out.println();
            System.out.println("Parameters:");
            System.out.println("  outputDirectory: Directory where audio files will be saved");
            System.out.println("  key: Harmonica key (C, A, D, G, F, B_FLAT), 'all' for required keys, or 'all_possible' for all possible keys");
            System.out.println("  tune: Harmonica tuning (RICHTER, COUNTRY, etc.)");
            System.out.println("  variations: Number of variations to generate (1-8)");
            System.out.println();
            System.out.println("Variations:");
            System.out.println("  0: Light noise (5% noise level)");
            System.out.println("  1: Medium noise (10% noise level)");
            System.out.println("  2: Heavy noise (20% noise level)");
            System.out.println("  3: Soft amplitude (50% of original)");
            System.out.println("  4: Loud amplitude (80% of original)");
            System.out.println("  5: Increasing amplitude");
            System.out.println("  6: Decreasing amplitude");
            System.out.println("  7: Varying amplitude");
            return;
        }

        String outputDirectory = args[0];

        try {
            if (args.length >= 4) {
                // Check if variations parameter is provided
                int variations = Integer.parseInt(args[3]);
                if (variations < 0 || variations > 8) {
                    System.out.println("Variations must be between 0 and 8. Using default value of 3.");
                    variations = 3;
                }

                // Generate files for a specific key and tune with variations
                String key = args[1];
                String tune = args[2];

                if ("all_possible".equalsIgnoreCase(key)) {
                    // Generate files for all possible keys with variations
                    generateAllPossibleKeysWithVariations(outputDirectory, tune, variations);
                    System.out.println("Chord audio files with " + variations + " variations generated successfully for all possible keys in " + outputDirectory);
                } else if ("all".equalsIgnoreCase(key)) {
                    // Generate files for all required keys with variations
                    generateAllRequiredKeysWithVariations(outputDirectory, tune, variations);
                    System.out.println("Chord audio files with " + variations + " variations generated successfully for all required keys in " + outputDirectory);
                } else {
                    // Generate files for a specific key with variations
                    generateChordAudioFilesWithVariations(outputDirectory, key, tune, variations);
                    System.out.println("Chord audio files with " + variations + " variations generated successfully for key " + key + " in " + outputDirectory);
                }
            } else if (args.length >= 3) {
                // Generate files for a specific key and tune without variations
                String key = args[1];
                String tune = args[2];

                if ("all_possible".equalsIgnoreCase(key)) {
                    // Generate files for all possible keys without variations
                    generateAllPossibleKeys(outputDirectory, tune);
                    System.out.println("Chord audio files generated successfully for all possible keys in " + outputDirectory);
                } else if ("all".equalsIgnoreCase(key)) {
                    // Generate files for all required keys without variations
                    generateAllRequiredKeys(outputDirectory, tune);
                    System.out.println("Chord audio files generated successfully for all required keys in " + outputDirectory);
                } else {
                    generateChordAudioFiles(outputDirectory, key, tune);
                    System.out.println("Chord audio files generated successfully for key " + key + " in " + outputDirectory);
                }
            } else {
                // Generate files for all required keys with RICHTER tuning without variations
                generateAllRequiredKeys(outputDirectory, "RICHTER");
                System.out.println("Chord audio files generated successfully for all required keys in " + outputDirectory);
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing variations parameter: " + e.getMessage());
            System.err.println("Using default behavior without variations.");
            try {
                generateAllRequiredKeys(outputDirectory, "RICHTER");
                System.out.println("Chord audio files generated successfully for all required keys in " + outputDirectory);
            } catch (IOException ioe) {
                System.err.println("Error generating chord audio files: " + ioe.getMessage());
                ioe.printStackTrace();
            }
        } catch (IOException e) {
            System.err.println("Error generating chord audio files: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
