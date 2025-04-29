# Styling Guide for BluesHarp Bending App

This document provides guidelines for maintaining consistent styling across all platforms (Android, desktop, and web) in the BluesHarp Bending App.

## Common Style Elements

The app uses a set of common style elements defined in the base module to ensure visual consistency across all platforms. These elements include colors, dimensions, and utility methods for applying styles.

### Colors

Colors are defined in the `AppColors` class (`de.schliweb.bluesharpbendingapp.view.style.AppColors`). This class provides constants for all the colors used in the app, including:

- Base colors (black, white)
- Note colors (blow note, draw note, blow bend note, draw bend note, overblow note, overdraw note)
- Material Design colors (primary, secondary, etc.)
- Additional colors (success, error, warning, info)

Example usage:
```java
// Get the color for a blow bend note
String blowBendColor = AppColors.BLOW_BEND_NOTE; // Returns "#ff9800"
```

### Dimensions

Dimensions are defined in the `AppDimensions` class (`de.schliweb.bluesharpbendingapp.view.style.AppDimensions`). This class provides constants for all the dimensions used in the app, including:

- Note dimensions (corner radius, stroke width, margin, padding)
- Overlay dimensions (corner radius, padding, stroke width)

Example usage:
```java
// Get the corner radius for a note
int cornerRadius = AppDimensions.NOTE_CORNER_RADIUS; // Returns 8
```

### Style Utilities

The `StyleUtils` class (`de.schliweb.bluesharpbendingapp.view.style.StyleUtils`) provides utility methods for applying styles programmatically. This class includes methods for:

- Getting the background color for a note based on its type
- Getting dimensions for notes and overlays

Example usage:
```java
// Get the background color for a blow bend note
String color = StyleUtils.getNoteBackgroundColor(true, true, false); // Returns "#ff9800"

// Get the corner radius for a note
int cornerRadius = StyleUtils.getNoteCornerRadius(); // Returns 8
```

## Platform-Specific Implementation

### Android

In the Android app, colors are defined in `colors.xml` and dimensions are defined in `dimens.xml`. These resources reference the common style elements defined in the base module.

### Desktop

In the desktop app, styles are defined in `main-window.css`. This CSS file includes comments that reference the corresponding constants in the `AppColors` and `AppDimensions` classes.

### Web

In the web app, styles are defined in `style.css`. This CSS file includes comments that reference the corresponding constants in the `AppColors` and `AppDimensions` classes.

## Maintaining Consistency

When making changes to the styling of the app, follow these guidelines:

1. If you need to add a new color or dimension, add it to the appropriate class in the base module (`AppColors` or `AppDimensions`).
2. Update the platform-specific style files to use the new color or dimension.
3. Add utility methods to the `StyleUtils` class if needed.
4. Test the changes on all platforms to ensure visual consistency.

By following these guidelines, we can ensure that the app has a consistent look and feel across all platforms.