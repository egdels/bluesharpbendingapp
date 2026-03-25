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
 * Utility class for applying consistent styles across all platforms. This class provides methods
 * for getting colors and dimensions based on the note type.
 */
public class StyleUtils {

  /**
   * Gets the background color for a note based on its type.
   *
   * @param isBlowNote true if the note is a blow note, false if it's a draw note
   * @param isBend true if the note is a bend note
   * @param isOverflow true if the note is an overflow note (overblow or overdraw)
   * @return the color string in hex format (e.g., "#ff9800")
   */
  public static String getNoteBackgroundColor(
      boolean isBlowNote, boolean isBend, boolean isOverflow) {
    if (isOverflow) {
      return isBlowNote ? AppColors.OVERBLOW_NOTE : AppColors.OVERDRAW_NOTE;
    } else if (isBend) {
      return isBlowNote ? AppColors.BLOW_BEND_NOTE : AppColors.DRAW_BEND_NOTE;
    } else {
      return isBlowNote ? AppColors.BLOW_NOTE : AppColors.DRAW_NOTE;
    }
  }

  /**
   * Gets the corner radius for a note.
   *
   * @return the corner radius in pixels
   */
  public static int getNoteCornerRadius() {
    return AppDimensions.NOTE_CORNER_RADIUS;
  }

  /**
   * Gets the stroke width for a note.
   *
   * @return the stroke width in pixels
   */
  public static int getNoteStrokeWidth() {
    return AppDimensions.NOTE_STROKE_WIDTH;
  }

  /**
   * Gets the margin for a note.
   *
   * @return the margin in pixels
   */
  public static int getNoteMargin() {
    return AppDimensions.NOTE_MARGIN;
  }

  /**
   * Gets the padding for a note.
   *
   * @return the padding in pixels
   */
  public static int getNotePadding() {
    return AppDimensions.NOTE_PADDING;
  }

  private StyleUtils() {}
}
