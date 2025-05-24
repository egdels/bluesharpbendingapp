package de.schliweb.bluesharpbendingapp.utils;

import de.schliweb.bluesharpbendingapp.model.harmonica.AbstractHarmonica;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for {@link ChordAudioFileGenerator}.
 * <p>
 * This class tests the functionality of the ChordAudioFileGenerator utility,
 * which generates WAV audio files for harmonica chords.
 */
class ChordAudioFileGeneratorTest {

    /**
     * Tests the generation of chord audio files for a C Richter harmonica.
     * <p>
     * This test verifies that the ChordAudioFileGenerator can successfully generate
     * WAV audio files for all possible chords on a C Richter harmonica.
     *
     * @param tempDir a temporary directory provided by JUnit for test file output
     * @throws IOException if an error occurs while writing the audio files
     */
    @Test
    void testGenerateChordAudioFiles(@TempDir Path tempDir) throws IOException {
        // Arrange
        String outputDirectory = tempDir.toString();
        String key = "C";
        String tune = "RICHTER";

        // Act
        ChordAudioFileGenerator.generateChordAudioFiles(outputDirectory, key, tune);

        // Assert
        // Look for files in the correct location (data/raw/key_C/chords/)
        File chordsDirectory = new File(outputDirectory + "/data/raw/key_" + key + "/chords");
        assertTrue(chordsDirectory.exists(), "Chords directory was not created");

        File[] files = chordsDirectory.listFiles((dir, name) -> name.endsWith(".wav"));

        // Verify that files were created
        assertTrue(files != null && files.length > 0, "No WAV files were generated");

        // Verify that the files have non-zero size
        for (File file : files) {
            assertTrue(file.length() > 0, "Generated file " + file.getName() + " has zero size");
            // Verify that the file name follows the required format
            assertTrue(file.getName().startsWith("chord_"), "File name does not start with 'chord_': " + file.getName());
        }

        System.out.println("Generated " + files.length + " chord audio files in " + chordsDirectory);
    }

    @Test
    void testGenerateChordAudioFilesWithVariations(@TempDir Path tempDir) throws IOException {
        // Arrange
        String outputDirectory = tempDir.toString();
        String key = "C";
        String tune = "RICHTER";
        int variations = 3; // Test with 3 variations

        // Act
        ChordAudioFileGenerator.generateChordAudioFilesWithVariations(outputDirectory, key, tune, variations);

        // Assert
        // Look for files in the correct location (data/raw/key_C/chords_variations/)
        File chordsDirectory = new File(outputDirectory + "/data/raw/key_" + key + "/chords_variations");
        assertTrue(chordsDirectory.exists(), "Chords variations directory was not created");

        File[] files = chordsDirectory.listFiles((dir, name) -> name.endsWith(".wav"));

        // Verify that files were created
        assertTrue(files != null && files.length > 0, "No WAV files were generated");

        // Count the number of files with each variation type
        int standardCount = 0;
        int lightNoiseCount = 0;
        int mediumNoiseCount = 0;
        int heavyNoiseCount = 0;

        for (File file : files) {
            assertTrue(file.length() > 0, "Generated file " + file.getName() + " has zero size");
            // Verify that the file name follows the required format
            assertTrue(file.getName().startsWith("chord_"), "File name does not start with 'chord_': " + file.getName());

            // Count the variations
            if (file.getName().contains("_light_noise")) {
                lightNoiseCount++;
            } else if (file.getName().contains("_medium_noise")) {
                mediumNoiseCount++;
            } else if (file.getName().contains("_heavy_noise")) {
                heavyNoiseCount++;
            } else if (!file.getName().contains("_light_noise") && 
                       !file.getName().contains("_medium_noise") && 
                       !file.getName().contains("_heavy_noise") &&
                       !file.getName().contains("_soft") &&
                       !file.getName().contains("_loud") &&
                       !file.getName().contains("_increasing") &&
                       !file.getName().contains("_decreasing") &&
                       !file.getName().contains("_varying")) {
                standardCount++;
            }
        }

        // Verify that we have the expected number of each variation
        // The number of standard files should be equal to the number of each variation type
        assertTrue(standardCount > 0, "No standard files were generated");
        assertEquals(standardCount, lightNoiseCount, "Incorrect number of light noise variations");
        assertEquals(standardCount, mediumNoiseCount, "Incorrect number of medium noise variations");
        assertEquals(standardCount, heavyNoiseCount, "Incorrect number of heavy noise variations");

        // The total number of files should be standardCount * (1 + variations)
        assertEquals(standardCount * (1 + variations), files.length, "Incorrect total number of files");

        System.out.println("Generated " + files.length + " chord audio files with variations in " + chordsDirectory);
        System.out.println("Standard files: " + standardCount);
        System.out.println("Light noise variations: " + lightNoiseCount);
        System.out.println("Medium noise variations: " + mediumNoiseCount);
        System.out.println("Heavy noise variations: " + heavyNoiseCount);
    }

    @Test
    void testGenerateAllPossibleKeys(@TempDir Path tempDir) throws IOException {
        // Arrange
        String outputDirectory = tempDir.toString();
        String tune = "RICHTER";

        // Get the number of all possible keys
        String[] allKeys = AbstractHarmonica.getSupporterKeys();
        int expectedKeyCount = allKeys.length;

        // Act
        ChordAudioFileGenerator.generateAllPossibleKeys(outputDirectory, tune);

        // Assert
        // Count the number of key directories created
        File dataRawDir = new File(outputDirectory + "/data/raw");
        assertTrue(dataRawDir.exists(), "data/raw directory was not created");

        File[] keyDirs = dataRawDir.listFiles(File::isDirectory);
        assertTrue(keyDirs != null && keyDirs.length > 0, "No key directories were created");

        // Verify that a directory was created for each possible key
        assertEquals(expectedKeyCount, keyDirs.length, "Incorrect number of key directories");

        // Verify that each key directory contains a chords directory with WAV files
        for (File keyDir : keyDirs) {
            File chordsDir = new File(keyDir, "chords");
            assertTrue(chordsDir.exists(), "Chords directory was not created in " + keyDir.getName());

            File[] wavFiles = chordsDir.listFiles((dir, name) -> name.endsWith(".wav"));
            assertTrue(wavFiles != null && wavFiles.length > 0, "No WAV files were generated in " + chordsDir.getPath());

            // Verify that the WAV files have non-zero size and follow the naming convention
            for (File file : wavFiles) {
                assertTrue(file.length() > 0, "Generated file " + file.getName() + " has zero size");
                assertTrue(file.getName().startsWith("chord_"), "File name does not start with 'chord_': " + file.getName());
            }
        }

        System.out.println("Generated chord audio files for all " + expectedKeyCount + " possible keys in " + outputDirectory);
    }
}
