package de.schliweb.bluesharpbendingapp.utils;
/*
 * MIT License
 *
 * Copyright (c) 2025 Christian Kierdorf
 */

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression tests for bounds handling in PitchDetector.findFirstMinimum to avoid
 * ArrayIndexOutOfBoundsException when callers pass out-of-range indices.
 */
public class PitchDetectorBoundsTest {

    @Test
    public void testFindFirstMinimumClampsWhenMinIndexAtArrayLength() {
        double[] arr = new double[512];
        // Create a simple valley at index 100
        for (int i = 0; i < arr.length; i++) arr[i] = 1.0;
        arr[99] = 0.8;
        arr[100] = 0.1; // local minimum
        arr[101] = 0.8;
        // Pass minIndex equal to array length to simulate buggy caller input
        int idx = invokeFindFirstMinimum(arr, 0.5, 512, 1024);
        // With out-of-range minIndex, the function clamps to a safe range and should not crash; no minimum in that range
        assertEquals(-1, idx);
    }

    @Test
    public void testFindFirstMinimumReturnsMinusOneWhenNoMinimumInRange() {
        double[] arr = new double[16];
        for (int i = 0; i < arr.length; i++) arr[i] = 1.0;
        int idx = invokeFindFirstMinimum(arr, 0.5, 0, 16);
        assertEquals(-1, idx);
    }

    @Test
    public void testFindFirstMinimumHandlesSmallArrays() {
        assertEquals(-1, invokeFindFirstMinimum(new double[]{}, 0.5, 0, 10));
        assertEquals(-1, invokeFindFirstMinimum(new double[]{1}, 0.5, 0, 1));
        assertEquals(-1, invokeFindFirstMinimum(new double[]{1, 0}, 0.5, 0, 2));
    }

    // Helper to access protected static method in same package
    private static int invokeFindFirstMinimum(double[] array, double threshold, int minIndex, int maxIndex) {
        return PitchDetector.findFirstMinimum(array, threshold, minIndex, maxIndex);
    }
}
