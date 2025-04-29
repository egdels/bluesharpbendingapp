# Test Concept for BlueSharp Bending App

## 1. Introduction

This document outlines the test concept for the BlueSharp Bending App, specifying which test cases should be manually tested on which Android devices with which screen resolutions. The goal is to ensure the app functions correctly and displays properly across a representative range of devices and screen configurations.

## 2. App Features to Test

Based on the analysis of the app's codebase, the following key features need to be tested:

### 2.1 Harp View
- Display of harmonica notes in the table layout
- Correct rendering of different note types (blow, draw, overblow/overdraw)
- Note enlargement functionality when tapping on notes
- Proper scaling of the harp table on different screen sizes
- Correct display in both portrait and landscape orientations

### 2.2 Training View
- Display of training options in the spinner dropdowns
- Start/stop button functionality
- Progress bar display
- Note display during training
- Proper layout in both portrait and landscape orientations

### 2.3 Settings View
- Display and functionality of all spinner dropdowns:
  - Key selection
  - Tune selection
  - Algorithm selection
  - Confidence level selection
  - Concert pitch selection
- Screen lock toggle functionality
- Reset button functionality
- Proper layout in both portrait and landscape orientations

### 2.4 General UI
- Navigation between fragments
- App bar visibility toggle
- Dark mode/light mode appearance
- Overall responsiveness and usability

## 3. Android Device Categories

To ensure comprehensive testing, the app should be tested on the following device categories:

### 3.1 Phone Categories
- Small phones (< 5.5" screen, ~720p resolution)
- Medium phones (5.5"-6.2" screen, ~1080p resolution)
- Large phones (> 6.2" screen, ~1440p resolution)

### 3.2 Tablet Categories
- Small tablets (7"-8" screen, ~1280x800 resolution)
- Medium tablets (9"-10" screen, ~1920x1200 resolution)
- Large tablets (> 10" screen, ~2560x1600 resolution)

### 3.3 Foldable Devices
- Folding phones (both folded and unfolded states)

## 4. Screen Resolutions and Densities

The following screen resolutions and densities should be covered in testing:

### 4.1 Common Phone Resolutions
- 720 x 1280 (hdpi/xhdpi)
- 1080 x 1920 (xxhdpi)
- 1440 x 2560 (xxxhdpi)
- 1080 x 2340 (taller aspect ratio, xxhdpi)
- 1440 x 3040 (taller aspect ratio, xxxhdpi)

### 4.2 Common Tablet Resolutions
- 800 x 1280 (mdpi/hdpi)
- 1200 x 1920 (xhdpi)
- 1600 x 2560 (xxhdpi)

## 5. Test Matrix

The following matrix maps specific test cases to device categories and screen resolutions:

### 5.1 High Priority Test Combinations

| Test Case | Device Category | Screen Resolution | Orientation |
|-----------|-----------------|-------------------|-------------|
| Harp View Layout | Small Phone | 720 x 1280 | Portrait & Landscape |
| Harp View Layout | Medium Phone | 1080 x 1920 | Portrait & Landscape |
| Harp View Layout | Large Tablet | 1600 x 2560 | Landscape |
| Training View Functionality | Medium Phone | 1080 x 1920 | Portrait & Landscape |
| Training View Functionality | Small Tablet | 800 x 1280 | Landscape |
| Settings View All Options | Medium Phone | 1080 x 1920 | Portrait |
| Settings View All Options | Large Phone | 1440 x 2560 | Portrait |
| Settings View All Options | Medium Tablet | 1200 x 1920 | Landscape |
| Navigation Between Views | Medium Phone | 1080 x 1920 | Portrait & Landscape |
| Dark Mode Appearance | Medium Phone | 1080 x 1920 | Portrait |

### 5.2 Medium Priority Test Combinations

| Test Case | Device Category | Screen Resolution | Orientation |
|-----------|-----------------|-------------------|-------------|
| Harp View Layout | Large Phone | 1440 x 2560 | Portrait & Landscape |
| Harp View Layout | Small Tablet | 800 x 1280 | Portrait & Landscape |
| Harp View Layout | Medium Tablet | 1200 x 1920 | Portrait & Landscape |
| Harp View Note Enlargement | All Phone Categories | Various | Portrait & Landscape |
| Training View Functionality | Small Phone | 720 x 1280 | Portrait & Landscape |
| Training View Functionality | Large Phone | 1440 x 2560 | Portrait & Landscape |
| Training View Functionality | Medium Tablet | 1200 x 1920 | Portrait & Landscape |
| Training View Functionality | Large Tablet | 1600 x 2560 | Portrait & Landscape |
| Settings View All Options | Small Phone | 720 x 1280 | Portrait & Landscape |
| Settings View All Options | Small Tablet | 800 x 1280 | Portrait & Landscape |
| Settings View All Options | Large Tablet | 1600 x 2560 | Portrait & Landscape |
| App Bar Visibility Toggle | All Device Categories | Various | Portrait & Landscape |
| Screen Lock Functionality | Medium Phone | 1080 x 1920 | Portrait |

### 5.3 Lower Priority Test Combinations

| Test Case | Device Category | Screen Resolution | Orientation |
|-----------|-----------------|-------------------|-------------|
| Harp View Layout | Foldable | Folded & Unfolded | Portrait & Landscape |
| Training View Functionality | Foldable | Folded & Unfolded | Portrait & Landscape |
| Settings View All Options | Foldable | Folded & Unfolded | Portrait & Landscape |
| Dark Mode Appearance | All Device Categories (except Medium Phone) | Various | Portrait & Landscape |
| Navigation Between Views | All Device Categories (except Medium Phone) | Various | Portrait & Landscape |

## 6. Specific Test Procedures

### 6.1 Harp View Tests
1. Verify all notes are displayed correctly in the table
2. Check that note colors match their types (blow, draw, overblow/overdraw)
3. Tap on notes to verify enlargement functionality
4. Verify enlarged notes display correctly
5. Rotate device to check layout in both orientations
6. Verify channel numbers are displayed correctly

### 6.2 Training View Tests
1. Verify training options appear in dropdown
2. Select different training options and verify they load
3. Test start/stop button functionality
4. Verify progress bar updates correctly
5. Check that note display shows the correct note
6. Rotate device to check layout in both orientations

### 6.3 Settings View Tests
1. Verify all dropdown options are displayed correctly
2. Test selection of different options in each dropdown
3. Verify screen lock toggle works correctly
4. Test reset button functionality
5. Rotate device to check layout in both orientations

### 6.4 General UI Tests
1. Navigate between all fragments using menu options
2. Test app bar visibility toggle by tapping on screen
3. Switch between light and dark modes (if supported by device)
4. Verify overall responsiveness and usability

## 7. Test Environment

### 7.1 Recommended Physical Devices
- Samsung Galaxy S8 or similar (Small Phone)
- Google Pixel 6 or similar (Medium Phone)
- Samsung Galaxy S22 Ultra or similar (Large Phone)
- Samsung Galaxy Tab A7 or similar (Small Tablet)
- Samsung Galaxy Tab S7 or similar (Medium Tablet)
- Samsung Galaxy Tab S8 Ultra or similar (Large Tablet)
- Samsung Galaxy Z Fold or similar (Foldable)

### 7.2 Emulator Configurations
If physical devices are not available, the following emulator configurations should be used:
- Pixel 2 (5.0", 1080x1920, xxhdpi)
- Pixel 4 XL (6.3", 1440x3040, xxxhdpi)
- Pixel C (9.9", 2560x1800, xhdpi)
- Nexus 7 (7.0", 1200x1920, xhdpi)

## 8. Test Documentation

For each test case, document:
1. Device used (make, model, Android version)
2. Screen resolution and orientation
3. Test result (Pass/Fail)
4. Screenshots of any issues
5. Description of any issues encountered
6. Steps to reproduce issues

## 9. Conclusion

This test concept provides a comprehensive approach to manually testing the BlueSharp Bending App across various Android devices and screen resolutions. By following this test plan, the team can ensure the app functions correctly and displays properly for all users, regardless of their device specifications.