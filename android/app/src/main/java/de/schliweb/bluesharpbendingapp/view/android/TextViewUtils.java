package de.schliweb.bluesharpbendingapp.view.android;
/*
 * Copyright (c) 2023 Christian Kierdorf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.widget.TextView;
import de.schliweb.bluesharpbendingapp.R;

/**
 * Utility class for manipulating and updating properties of TextView objects,
 * specifically for managing appearance and behavior in tuning-related applications.
 */
public class TextViewUtils {

    private TextViewUtils() {
    }

    /**
     * Updates the appearance and position of a line in the given `TextView` background layer
     * based on the passed cent value, dynamically modifying its size, color, and vertical position.
     *
     * @param noteTextView The `TextView` whose background layer includes the line to be updated.
     * @param cents        A double representing the cent value, which determines the position,
     *                     color intensity, and other visual attributes of the line.
     */
    public static void updateEnlargedTextViewLine(TextView noteTextView, double cents) {
        // Check if the background is a LayerDrawable before attempting to cast
        if (!(noteTextView.getBackground() instanceof LayerDrawable layout)) {
            return; // Exit if not a LayerDrawable
        }

        // Check if the LayerDrawable has enough layers
        if (layout.getNumberOfLayers() <= 1) {
            return; // Exit if not enough layers
        }

        GradientDrawable line = (GradientDrawable) layout.getDrawable(1);
        line.setAlpha(255); // Make line fully visible

        double height = noteTextView.getHeight();
        double width = noteTextView.getWidth();

        // Calculate line height (thickness)
        int lineHeight = Math.max((int) (noteTextView.getHeight() / 10.0), 4);

        // Set stroke color based on cents value
        int lineColor = Color.rgb((int) (250.0 * Math.abs(cents / 50.0)), (int) (250.0 * (1.0 - Math.abs(cents / 50.0))), 0);

        double limitedCents = Math.clamp(cents, -50, 50);

        // Calculate vertical position: middle of the view for 0 cents, moving up or down based on cents
        // For positive cents, move up from the middle; for negative cents, move down from the middle
        double middleY = height / 2.0;
        double offsetY = (limitedCents / 50.0) * (height / 2.0); // Scale to half the height
        double lineY = middleY - offsetY; // Subtract offset to move up for positive cents

        int lineWidth = (int) (width);
        // Calculate left position to center the line
        int leftPosition = (int) ((width - lineWidth) / 2.0);

        // Set bounds for a horizontal line at the calculated position
        line.setBounds(leftPosition,                       // Left position (centered)
                (int) (lineY - lineHeight / 2.0),   // Top position (centered around lineY)
                leftPosition + lineWidth,           // Right position (left + width)
                (int) (lineY + lineHeight / 2.0)    // Bottom position (centered around lineY)
        );

        // Set the stroke to make the line visible
        line.setStroke(0, Color.TRANSPARENT); // Clear any existing stroke
        line.setColor(lineColor); // Set the fill color instead
    }

    /**
     * Updates the position and appearance of a line within the background of the specified `TextView`
     * based on the provided cent value. The line's position, color, and size are dynamically updated
     * to reflect the cent-based input.
     *
     * @param noteTextView The `TextView` whose background contains the line to be updated.
     * @param cents        A double indicating the cent value that influences the line's position, color
     *                     intensity, and attributes. Values are constrained within the range -44 to 44.
     */
    public static void updateTextViewLine(TextView noteTextView, double cents) {
        // Get the background drawable
        android.graphics.drawable.Drawable background = noteTextView.getBackground();

        // Check if the background is a LayerDrawable before attempting to cast
        if (!(background instanceof LayerDrawable layout)) {
            return; // Exit if not a LayerDrawable
        }

        // Check if the LayerDrawable has enough layers
        if (layout.getNumberOfLayers() <= 1) {
            return; // Exit if not enough layers
        }

        GradientDrawable line = (GradientDrawable) layout.getDrawable(1);
        line.setAlpha(255); // Make line fully visible

        double height = noteTextView.getHeight();
        double width = noteTextView.getWidth();

        // Calculate line height (thickness)
        int lineHeight = Math.max((int) (noteTextView.getHeight() / 10.0), 3);

        // Set stroke color based on cents value
        int lineColor = Color.rgb((int) (250.0 * Math.abs(cents / 50.0)), (int) (250.0 * (1.0 - Math.abs(cents / 50.0))), 0);

        double limitedCents = Math.clamp(cents, -50, 50);

        // Calculate vertical position: middle of the view for 0 cents, moving up or down based on cents
        // For positive cents, move up from the middle; for negative cents, move down from the middle
        double middleY = height / 2.0;
        double offsetY = (limitedCents / 50.0) * (height / 2.0); // Scale to half the height
        double lineY = middleY - offsetY; // Subtract offset to move up for positive cents

        // Get the corner radius from resources
        float cornerRadius = noteTextView.getContext().getResources().getDimension(R.dimen.note_corner_radius);

        int lineWidth = (int) (width - cornerRadius / 2);

        // Calculate left position to center the line
        int leftPosition = (int) ((width - lineWidth) / 2.0);

        // Set bounds for a horizontal line at the calculated position
        line.setBounds(leftPosition,                       // Left position (centered)
                (int) (lineY - lineHeight / 2.0),   // Top position (centered around lineY)
                leftPosition + lineWidth,           // Right position (left + width)
                (int) (lineY + lineHeight / 2.0)    // Bottom position (centered around lineY)
        );

        // Set the stroke to make the line visible
        line.setStroke(0, Color.TRANSPARENT); // Clear any existing stroke
        line.setColor(lineColor); // Set the fill color instead
    }


    /**
     * Clears the visible line in the background of the given TextView by setting its transparency to fully clear.
     *
     * @param noteTextView The TextView whose background line layer will be modified to become transparent.
     */
    public static void clearTextViewLine(TextView noteTextView) {
        // Get the background drawable
        android.graphics.drawable.Drawable background = noteTextView.getBackground();

        // Check the type of background drawable before attempting to cast
        if (background instanceof LayerDrawable layout) {
            // Get the LayerDrawable and extract the line layer (index 1)
            if (layout.getNumberOfLayers() > 1) {
                GradientDrawable line = (GradientDrawable) layout.getDrawable(1);
                // Make the line completely transparent
                line.setAlpha(0);

                // Reset both stroke and fill color to transparent to ensure it's not visible
                line.setStroke(0, Color.TRANSPARENT);
                line.setColor(Color.TRANSPARENT);
            }
        }
        // If the background is not a LayerDrawable or doesn't have enough layers,
        // we don't need to do anything as there's no line to clear
    }

    /**
     * Updates the content and style of a `TextView` to display a note text and a cent value with specific formatting.
     * This includes styling text appearance, setting fonts, and adjusting text sizes.
     * Uses modern text styling approaches with SpannableStringBuilder.
     *
     * @param noteTextView The `TextView` that will display the formatted note text and cent value.
     * @param noteText     The note text to be displayed, which will appear in bold and enlarged.
     * @param cents        A double value representing the cent information, formatted and styled to appear
     *                     as part of the displayed text.
     */
    public static void updateTextViewCent(TextView noteTextView, String noteText, double cents) {
        // Format cents with leading spaces and sign (+/-), suppress lint warning
        @SuppressLint("DefaultLocale") String centsString = String.format("%+3d", (int) cents);
        centsString = "ct:" + centsString;

        // Use SpannableStringBuilder for more efficient span operations
        Spannable spannableString = new SpannableString(noteText + "\n" + centsString);

        // Apply Material Design text appearance
        // Set the color for the entire text to use the theme's text color
        noteTextView.setTextColor(noteTextView.getContext().getResources().getColor(android.R.color.black, noteTextView.getContext().getTheme()));

        // Make the note text bold (first part only)
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, noteText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Increase size of note text by factor 1.5
        spannableString.setSpan(new RelativeSizeSpan(1.5f), 0, noteText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply monospace font to the cents part
        spannableString.setSpan(new TypefaceSpan("monospace"), noteText.length(), spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make cents part 1.2 times larger than normal text
        spannableString.setSpan(new RelativeSizeSpan(1.2f), noteText.length() + 1, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the final formatted text to the TextView
        noteTextView.setText(spannableString);

        // Apply modern elevation for a subtle shadow effect
        noteTextView.setElevation(4f);
    }

}
