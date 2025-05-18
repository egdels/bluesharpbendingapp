package de.schliweb.bluesharpbendingapp.utils;

/**
 * A simple utility class to run the PitchDetectorComparison.
 * This class calls the main method of PitchDetectorComparison to generate
 * a comprehensive comparison of the different pitch detection algorithms.
 */
public class RunPitchDetectorComparison {
    
    /**
     * Main method to run the PitchDetectorComparison.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Starting pitch detector comparison...");
        PitchDetectorComparison.main(args);
        System.out.println("Pitch detector comparison completed.");
    }
}