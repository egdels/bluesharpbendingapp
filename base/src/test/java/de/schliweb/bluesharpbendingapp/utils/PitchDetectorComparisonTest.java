package de.schliweb.bluesharpbendingapp.utils;

import org.junit.jupiter.api.Test;

/**
 * Test class to run the PitchDetectorComparison and capture its output.
 */
public class PitchDetectorComparisonTest {

    /**
     * Runs the PitchDetectorComparison and prints the results.
     */
    @Test
    public void testPitchDetectorComparison() {
        System.out.println("[DEBUG_LOG] Starting PitchDetectorComparison test");
        
        // Run the comparison
        PitchDetectorComparison.main(new String[0]);
        
        System.out.println("[DEBUG_LOG] PitchDetectorComparison test completed");
    }
}