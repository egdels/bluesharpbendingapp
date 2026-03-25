/*
 * Copyright (c) 2023 Christian Kierdorf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package de.schliweb.bluesharpbendingapp.view.style;

/**
 * This class defines the common color constants used across all platforms. These colors are used
 * for styling the UI elements in the application.
 */
public class AppColors {
  // Base colors
  public static final String BLACK = "#000000";
  public static final String WHITE = "#FFFFFF";

  // Note colors
  public static final String BLOW_NOTE = "#5B5A60"; // Darker Grey for better contrast
  public static final String DRAW_NOTE = "#5B5A60"; // Darker Grey for better contrast
  public static final String BLOW_BEND_NOTE = "#FF6A00"; // More intense Orange
  public static final String DRAW_BEND_NOTE = "#0057FF"; // Lighter and stronger Blue
  public static final String OVERBLOW_NOTE = "#E100E1"; // Magenta
  public static final String OVERDRAW_NOTE = "#E100E1"; // Magenta

  // Material Design colors
  public static final String PRIMARY = "#1976D2"; // Blue
  public static final String PRIMARY_VARIANT = "#004BA0"; // Dark blue
  public static final String SECONDARY = "#FF5722"; // Deep orange
  public static final String SECONDARY_VARIANT = "#C41C00"; // Dark orange

  // Additional colors
  public static final String SUCCESS = "#4CAF50"; // Green
  public static final String ERROR = "#F44336"; // Red
  public static final String WARNING = "#FFC107"; // Amber
  public static final String INFO = "#2196F3"; // Light blue

  private AppColors() {}
}
