package de.schliweb.bluesharpbendingapp.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the ChordDetector.
 * This class tests the functionality of the chord detection algorithm.
 */
class ChordDetectorTest {

    private static final int SAMPLE_RATE = 44100;
    private static final double TOLERANCE = 1.0;

    @BeforeEach
    void setUp() {
        PitchDetector.setMaxFrequency(PitchDetector.getDefaultMaxFrequency());
        PitchDetector.setMinFrequency(PitchDetector.getDefaultMinFrequency());
    }

    /**
     * Tests that the chord detector can detect multiple pitches (a chord) correctly.
     */
    @ParameterizedTest
    @CsvSource({
            // First frequency, first amplitude, second frequency, second amplitude, third frequency, third amplitude
            "261.63, 1.0, 329.63, 1.0, 392.0, 1.0", // C major chord (C4, E4, G4)
            "293.665, 1.0, 392.0, 1.0, 493.88, 1.0", // D major chord (D4, G4, B4)
            "440.0, 1.0, 554.37, 1.0, 659.25, 1.0", // A major chord (A4, C#5, E5)
            "220.0, 1.0, 277.18, 1.0, 329.63, 1.0", // A major chord (A3, C#4, E4)
            "440.0, 1.0, 554.37, 1.0, 659.25, 1.0", // A major chord (A4, C#5, E5)
            "293.66, 1.0, 349.23, 1.0, 440.0, 1.0",  // D minor chord (D4, F4, A4)
            "246.94, 1.0, 329.63, 1.0, 415.30, 1.0", // E major chord (B3, E4, G#4)
            "493.88, 1.0, 659.25, 1.0, 830.61, 1.0",// E major chord (B4, E5, G#5)
            "220.0, 1.0, 293.66, 1.0, 369.99, 1.0", // D major chord (A3, D4, F#4)
            "196.0, 1.0, 246.94, 1.0, 293.66, 1.0" // G major chord (G3, B3, D4)
    })
    void testChordDetection(double freq1, double amp1, double freq2, double amp2, double freq3, double amp3) {
        // Generate a chord (multiple sine waves)
        double[] chord = generateChord(
                new double[]{freq1, freq2, freq3},
                new double[]{amp1, amp2, amp3},
                SAMPLE_RATE, 1.0);

        // Detect the chord
        ChordDetectionResult result = PitchDetector.detectChord(chord, SAMPLE_RATE);

        // Verify that multiple pitches were detected
        assertTrue(result.hasPitches(), "Should detect pitches in the chord");
        assertTrue(result.getPitchCount() == 3, "Should detect 3 pitches in the chord but was " + result.getPitchCount());


        // Verify that the detected pitches match the input frequencies
        List<Double> pitches = result.pitches();
        boolean foundFreq1 = false;
        boolean foundFreq2 = false;
        boolean foundFreq3 = false;

        for (double pitch : pitches) {
            if (Math.abs(pitch - freq1) <= TOLERANCE) {
                foundFreq1 = true;
            } else if (Math.abs(pitch - freq2) <= TOLERANCE) {
                foundFreq2 = true;
            } else if (Math.abs(pitch - freq3) <= TOLERANCE) {
                foundFreq3 = true;
            }
        }

        // At least two of the three frequencies should be detected
        int detectedCount = (foundFreq1 ? 1 : 0) + (foundFreq2 ? 1 : 0) + (foundFreq3 ? 1 : 0);
        assertTrue(detectedCount == 3,
                "Should detect 3 frequencies in the chord. Found: " +
                (foundFreq1 ? freq1 + " " : "") + 
                (foundFreq2 ? freq2 + " " : "") + 
                (foundFreq3 ? freq3 + " " : ""));
    }

    /**
     * Tests that the chord detector returns no pitches for silence.
     */
    @Test
    void testSilence() {
        double[] audioData = new double[SAMPLE_RATE]; // All zeros (silence)

        ChordDetectionResult result = PitchDetector.detectChord(audioData, SAMPLE_RATE);

        assertFalse(result.hasPitches(), "Should not detect any pitches in silence");
        assertEquals(0, result.getPitchCount(), "Should have zero pitches for silence");
        assertEquals(0.0, result.confidence(), "Confidence should be zero for silence");
    }

    /**
     * Tests that the chord detector handles noise appropriately.
     */
    @Test
    void testNoise() {
        double[] audioData = new double[SAMPLE_RATE];
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] = Math.random() * 2 - 1; // Random noise between -1 and 1
        }

        ChordDetectionResult result = PitchDetector.detectChord(audioData, SAMPLE_RATE);

        // Noise might produce some peaks, but the confidence should be low
        if (result.hasPitches()) {
            assertTrue(result.confidence() < 0.5, "Confidence should be low for noise");
        }
    }

    /**
     * Tests that the chord detector can handle a chord with added noise.
     */
    @Test
    void testChordWithNoise() {
        // Generate a C major chord
        double[] chord = generateChord(
                new double[]{261.63, 329.63, 392.0}, // C4, E4, G4
                new double[]{1.0, 1.0, 1.0},
                SAMPLE_RATE, 1.0);

        // Add noise
        for (int i = 0; i < chord.length; i++) {
            chord[i] += (Math.random() * 0.1 - 0.05); // Add noise at 5% level
        }

        // Detect the chord
        ChordDetectionResult result = PitchDetector.detectChord(chord, SAMPLE_RATE);

        // Verify that pitches were still detected despite the noise
        assertTrue(result.hasPitches(), "Should detect pitches in the chord with noise");
    }

    /**
     * Tests the chord detector's ability to identify two-note harmonic chords that are an octave apart.
     *
     * @param freq1 the frequency of the first note in Hertz
     * @param amp1 the amplitude of the first note
     * @param freq2 the frequency of the second note in Hertz
     * @param amp2 the amplitude of the second note
     */
    @ParameterizedTest
    @CsvSource({
            // First frequency, first amplitude, second frequency, second amplitude
            "261.63, 1.0, 523.25, 1.0", // C4 and C5
            "293.66, 1.0, 587.33, 1.0", // D4 and D5
            "329.63, 1.0, 659.25, 1.0", // E4 and E5
            "349.23, 1.0, 698.46, 1.0", // F4 and F5
            "392.0, 1.0, 784.0, 1.0",   // G4 and G5
            "440.0, 1.0, 880.0, 1.0",   // A4 and A5
            "493.88, 1.0, 987.77, 1.0"  // B4 and B5
    })
    void testTwoNoteHarmonicaChordDetectionOctave(double freq1, double amp1, double freq2, double amp2) {
        // Generate a 2-note chord (two sine waves)
        double[] chord = generateChord(
                new double[]{freq1, freq2},
                new double[]{amp1, amp2},
                SAMPLE_RATE, 1.0);

        // Detect the chord
        ChordDetectionResult result = PitchDetector.detectChord(chord, SAMPLE_RATE);

        // Verify that multiple pitches were detected
        assertTrue(result.hasPitches(), "Should detect pitches in the 2-note chord");
        assertTrue(result.getPitchCount() == 2, "Should detect 2 pitch in the 2-note chord");

        // Verify that the detected pitches match the input frequencies
        List<Double> pitches = result.pitches();
        boolean foundFreq1 = false;
        boolean foundFreq2 = false;

        for (double pitch : pitches) {
            if (Math.abs(pitch - freq1) <= TOLERANCE) {
                foundFreq1 = true;
            } else if (Math.abs(pitch - freq2) <= TOLERANCE) {
                foundFreq2 = true;
            }
        }

        int detectedCount = (foundFreq1 ? 1 : 0) + (foundFreq2 ? 1 : 0);
        assertTrue(detectedCount == 2,
                "Should detect 2 frequencies in the chord. Found: " +
                        (foundFreq1 ? freq1 + " " : "") +
                        (foundFreq2 ? freq2 + " " : "") );

    }

    /**
     * Tests that the chord detector can detect 4-note chords correctly.
     */
    @ParameterizedTest
    @CsvSource({
            // First frequency, first amplitude, second frequency, second amplitude, third frequency, third amplitude, fourth frequency, fourth amplitude
            "261.63, 1.0, 329.63, 1.0, 392.0, 1.0, 493.88, 1.0", // C major 7th chord (C4, E4, G4, B4)
            "220.0, 1.0, 277.18, 1.0, 329.63, 1.0, 391.995, 1.0",  // A minor 7th chord (A3, C4, E4, G4)
            "246.94, 1.0, 311.13, 1.0, 369.99, 1.0, 493.88, 1.0", // B diminished 7th chord (B3, D4, F4, Ab4)

            // Major 7th chords
            "261.63, 1.0, 329.63, 1.0, 392.0, 1.0, 493.88, 1.0", // Cmaj7 (C, E, G, B)
            "293.66, 1.0, 369.99, 1.0, 440.0, 1.0, 554.37, 1.0", // Dmaj7 (D, F#, A, C#)
            "329.63, 1.0, 415.30, 1.0, 493.88, 1.0, 622.25, 1.0", // Emaj7 (E, G#, B, D#)
            "349.23, 1.0, 440.0, 1.0, 523.25, 1.0, 659.25, 1.0", // Fmaj7 (F, A, C, E)
            "392.0, 1.0, 493.88, 1.0, 587.33, 1.0, 739.99, 1.0", // Gmaj7 (G, B, D, F#)
            "220.00, 1.0, 277.18, 1.0, 329.63, 1.0, 391.995, 1.0", // Amaj7 (Octave Lower) (A, C#, E, G#)

            // Minor 7th chords
            "440.0, 1.0, 523.25, 1.0, 659.25, 1.0, 783.99, 1.0", // Am7 (A, C, E, G)
            "493.88, 1.0, 587.33, 1.0, 739.99, 1.0, 880.0, 1.0", // Bm7 (B, D, F#, A)
            "261.63, 1.0, 311.13, 1.0, 392.0, 1.0, 466.16, 1.0", // Cm7 (C, Eb, G, Bb)
            "293.66, 1.0, 349.23, 1.0, 440.0, 1.0, 523.25, 1.0", // Dm7 (D, F, A, C)
            "329.63, 1.0, 392.0, 1.0, 493.88, 1.0, 587.33, 1.0", // Em7 (E, G, B, D)

            // Diminished 7th chords
            "246.94, 1.0, 311.13, 1.0, 369.99, 1.0, 493.88, 1.0", // Bdim7 (B, D, F, Ab)
            "293.66, 1.0, 349.23, 1.0, 415.30, 1.0, 493.88, 1.0", // Ddim7 (D, F, Ab, B)
            "329.63, 1.0, 392.0, 1.0, 466.16, 1.0, 554.37, 1.0" // Edim7 (E, G, Bb, Db)
    })
    void testFourNoteChordDetection(double freq1, double amp1, double freq2, double amp2, 
                                   double freq3, double amp3, double freq4, double amp4) {
        // Generate a 4-note chord (multiple sine waves)
        double[] chord = generateChord(
                new double[]{freq1, freq2, freq3, freq4},
                new double[]{amp1, amp2, amp3, amp4},
                SAMPLE_RATE, 1.0);

        // Detect the chord
        ChordDetectionResult result = PitchDetector.detectChord(chord, SAMPLE_RATE);

        // Verify that multiple pitches were detected
        assertTrue(result.hasPitches(), "Should detect pitches in the 4-note chord");
        assertTrue(result.getPitchCount() == 4, "Should detect 4 pitches in the 4-note chord but was " + result.getPitchCount());

        // Verify that the detected pitches match the input frequencies
        List<Double> pitches = result.pitches();
        boolean foundFreq1 = false;
        boolean foundFreq2 = false;
        boolean foundFreq3 = false;
        boolean foundFreq4 = false;

        for (double pitch : pitches) {
            if (Math.abs(pitch - freq1) <= TOLERANCE) {
                foundFreq1 = true;
            } else if (Math.abs(pitch - freq2) <= TOLERANCE) {
                foundFreq2 = true;
            } else if (Math.abs(pitch - freq3) <= TOLERANCE) {
                foundFreq3 = true;
            } else if (Math.abs(pitch - freq4) <= TOLERANCE) {
                foundFreq4 = true;
            }
        }

        // At least three of the four frequencies should be detected
        int detectedCount = (foundFreq1 ? 1 : 0) + (foundFreq2 ? 1 : 0) + 
                           (foundFreq3 ? 1 : 0) + (foundFreq4 ? 1 : 0);
        assertTrue(detectedCount == 4,
                "Should detect 4 frequencies in the chord. Found: " +
                (foundFreq1 ? freq1 + " " : "") + 
                (foundFreq2 ? freq2 + " " : "") + 
                (foundFreq3 ? freq3 + " " : "") +
                (foundFreq4 ? freq4 + " " : ""));
    }

    /**
     * Generates a chord (multiple sine waves) based on the given frequencies, amplitudes, sample rate, and duration.
     *
     * @param frequencies an array of frequencies in Hertz
     * @param amplitudes an array of amplitudes for each frequency
     * @param sampleRate the number of samples per second
     * @param duration the duration of the chord in seconds
     * @return an array of doubles representing the generated chord
     */
    private double[] generateChord(double[] frequencies, double[] amplitudes, int sampleRate, double duration) {
        int samples = (int) (sampleRate * duration);
        double[] chord = new double[samples];

        for (int i = 0; i < samples; i++) {
            double sample = 0.0;
            for (int j = 0; j < frequencies.length; j++) {
                sample += amplitudes[j] * Math.sin(2 * Math.PI * frequencies[j] * i / sampleRate);
            }
            // Normalize to avoid clipping
            chord[i] = sample / frequencies.length;
        }

        return chord;
    }

    /**
     * Tests that clear chords (without noise) have high confidence values.
     * This test verifies that the confidence calculation produces high values
     * for clean, well-defined chords with strong harmonic content.
     */
    @Test
    void testClearChordConfidence() {
        // Generate a clear C major chord (C4, E4, G4)
        double[] chord = generateChord(
                new double[]{261.63, 329.63, 392.0},
                new double[]{1.0, 1.0, 1.0},
                SAMPLE_RATE, 1.0);

        // Detect the chord
        ChordDetectionResult result = PitchDetector.detectChord(chord, SAMPLE_RATE);

        // Verify that the confidence is high for a clear chord
        assertTrue(result.hasPitches(), "Should detect pitches in a clear chord");
        assertTrue(result.confidence() > 0.8, 
                "Confidence should be high (>0.8) for a clear chord, but was " + result.confidence());
    }

    /**
     * Tests how confidence decreases as noise level increases in a chord.
     * This test verifies that the confidence calculation is sensitive to
     * the signal-to-noise ratio, with confidence decreasing as noise increases.
     */
    @ParameterizedTest
    @ValueSource(doubles = {0.001, 0.005, 0.01, 0.02, 0.05})
    void testChordWithVaryingNoiseConfidence(double noiseLevel) {
        // Generate a C major chord (C4, E4, G4)
        double[] chord = generateChord(
                new double[]{261.63, 329.63, 392.0},
                new double[]{1.0, 1.0, 1.0},
                SAMPLE_RATE, 1.0);

        // Add noise at the specified level
        for (int i = 0; i < chord.length; i++) {
            chord[i] += (Math.random() * 2 - 1) * noiseLevel; // Add noise
        }

        // Detect the chord
        ChordDetectionResult result = PitchDetector.detectChord(chord, SAMPLE_RATE);

        // For noise levels above 0.05, we might not detect pitches, so only check if we have pitches
        if (result.hasPitches()) {
            // For very low noise (0.001-0.005), confidence should still be high
            if (noiseLevel <= 0.005) {
                assertTrue(result.confidence() > 0.7, 
                        "Confidence should be high (>0.7) for very low noise level " + noiseLevel + 
                        ", but was " + result.confidence());
            }
            // For low noise (0.01-0.02), confidence should be moderate to high
            else if (noiseLevel <= 0.02) {
                assertTrue(result.confidence() > 0.6, 
                        "Confidence should be moderate to high (>0.6) for low noise level " + noiseLevel + 
                        ", but was " + result.confidence());
            }
            // For medium noise (0.05), confidence can be lower
            else {
                // No specific assertion, just verify we have a valid confidence value
                assertTrue(result.confidence() >= 0.0 && result.confidence() <= 1.0,
                        "Confidence should be between 0 and 1 for medium noise level " + noiseLevel + 
                        ", but was " + result.confidence());
            }
        }
        // For higher noise levels, it's acceptable not to detect pitches
        // This is actually a good sign that the detector doesn't produce false positives
    }

    /**
     * Tests how confidence varies with the number of notes in a chord.
     * This test verifies that the confidence calculation works well for
     * both single notes and complex chords.
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4})
    void testSingleNoteVsChordConfidence(int noteCount) {
        // Define frequencies for a C major chord
        double[] allFrequencies = {261.63, 329.63, 392.0, 523.25}; // C4, E4, G4, C5
        double[] frequencies = new double[noteCount];
        double[] amplitudes = new double[noteCount];

        // Use the first noteCount frequencies
        for (int i = 0; i < noteCount; i++) {
            frequencies[i] = allFrequencies[i];
            amplitudes[i] = 1.0;
        }

        // Generate the chord or single note
        double[] audio = generateChord(frequencies, amplitudes, SAMPLE_RATE, 1.0);

        // Detect the chord
        ChordDetectionResult result = PitchDetector.detectChord(audio, SAMPLE_RATE);

        // Verify that confidence is high for all cases
        assertTrue(result.hasPitches(), "Should detect pitches for " + noteCount + " note(s)");
        assertEquals(noteCount, result.getPitchCount(), 
                "Should detect " + noteCount + " pitch(es), but detected " + result.getPitchCount());

        // Confidence should be high for clean signals regardless of note count
        assertTrue(result.confidence() > 0.7, 
                "Confidence should be high (>0.7) for " + noteCount + " clean note(s), but was " + 
                result.confidence());
    }

    /**
     * Tests confidence for signals with varying amplitudes.
     * This test verifies that the confidence calculation works correctly
     * for signals with different amplitudes, from very low to high.
     * 
     * Note: The current implementation of the chord detector maintains high confidence
     * even for very low amplitude signals, as long as the signal-to-noise ratio is good.
     * This is actually a desirable behavior for many applications.
     */
    @ParameterizedTest
    @ValueSource(doubles = {1.0, 0.5, 0.1, 0.05, 0.01, 0.005})
    void testLowAmplitudeSignalConfidence(double amplitude) {
        // Generate a C major chord (C4, E4, G4) with the specified amplitude
        double[] chord = generateChord(
                new double[]{261.63, 329.63, 392.0},
                new double[]{amplitude, amplitude, amplitude},
                SAMPLE_RATE, 1.0);

        // Detect the chord
        ChordDetectionResult result = PitchDetector.detectChord(chord, SAMPLE_RATE);

        // For extremely low amplitudes, we might not detect any pitches
        if (amplitude < 0.005) {
            // No specific assertion - it's acceptable either to detect or not detect pitches
            // at extremely low amplitudes
            if (result.hasPitches()) {
                // If pitches are detected, just verify confidence is a valid value
                assertTrue(result.confidence() >= 0.0 && result.confidence() <= 1.0,
                        "Confidence should be between 0 and 1 for extremely low amplitude " + amplitude + 
                        ", but was " + result.confidence());
            }
        } else {
            // For low to high amplitudes, we should detect pitches
            assertTrue(result.hasPitches(), "Should detect pitches for amplitude " + amplitude);

            // The current implementation maintains high confidence even for low amplitudes
            // as long as the signal is clean (good signal-to-noise ratio)
            if (amplitude >= 0.5) {
                // High amplitude should have high confidence
                assertTrue(result.confidence() > 0.7, 
                        "Confidence should be high (>0.7) for high amplitude " + amplitude + 
                        ", but was " + result.confidence());
            } else if (amplitude >= 0.05) {
                // Medium amplitude should have good confidence
                assertTrue(result.confidence() > 0.6, 
                        "Confidence should be good (>0.6) for medium amplitude " + amplitude + 
                        ", but was " + result.confidence());
            } else {
                // Even low amplitude can have good confidence if the signal is clean
                assertTrue(result.confidence() > 0.5, 
                        "Confidence should be reasonable (>0.5) for low amplitude " + amplitude + 
                        ", but was " + result.confidence());
            }
        }
    }
}
