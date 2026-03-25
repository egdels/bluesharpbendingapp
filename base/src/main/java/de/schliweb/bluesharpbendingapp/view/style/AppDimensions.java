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
 * This class defines the common dimension constants used across all platforms. These dimensions are
 * used for styling the UI elements in the application.
 */
public class AppDimensions {
  // Note dimensions
  public static final int NOTE_CORNER_RADIUS = 8; // Corner radius for notes
  public static final int NOTE_STROKE_WIDTH = 1; // Stroke width for notes
  public static final int NOTE_MARGIN = 4; // Margin for notes
  public static final int NOTE_PADDING = 8; // Padding for notes

  // Overlay dimensions
  public static final int OVERLAY_CORNER_RADIUS = 24; // Corner radius for overlays
  public static final int OVERLAY_PADDING = 24; // Padding for overlays
  public static final int OVERLAY_STROKE_WIDTH = 1; // Stroke width for overlays

  private AppDimensions() {}
}
