package de.schliweb.bluesharpbendingapp.view.style;

/**
 * Utility class for applying consistent styles across all platforms.
 * This class provides methods for getting colors and dimensions based on the note type.
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
    public static String getNoteBackgroundColor(boolean isBlowNote, boolean isBend, boolean isOverflow) {
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
}