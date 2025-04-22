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
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.widget.TextView;

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
        LayerDrawable layout = (LayerDrawable) noteTextView.getBackground();

        GradientDrawable line = (GradientDrawable) layout.getDrawable(1);
        line.setAlpha(255);

        double height = noteTextView.getHeight();

        int lineWidth = Math.max((int) (noteTextView.getHeight() / 10.0), 10);

        line.setStroke(lineWidth, Color.rgb((int) (250.0 * Math.abs(cents / 50.0)), (int) (250.0 * (1.0 - Math.abs(cents / 50.0))), 0));

        // Limit the cents values to the range -44 to 44
        double limitedCents = Math.clamp(cents, -44, 44);
        double position = height - height * (limitedCents / 50.0);


        line.setBounds(line.getBounds().left - lineWidth / 2,    // Move left edge further left
                line.getBounds().top,                   // Keep top position
                line.getBounds().right + lineWidth / 2,   // Move right edge further right
                (int) position                          // Set bottom position based on touch/movement
        );
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
        LayerDrawable layout = (LayerDrawable) noteTextView.getBackground();

        GradientDrawable line = (GradientDrawable) layout.getDrawable(1);
        line.setAlpha(255);

        double height = noteTextView.getHeight();

        int lineWidth = Math.max((int) (noteTextView.getHeight() / 10.0), 10);

        line.setStroke(lineWidth, Color.rgb((int) (250.0 * Math.abs(cents / 50.0)), (int) (250.0 * (1.0 - Math.abs(cents / 50.0))), 0));

        // Limit the cents values to the range -44 to 44
        double limitedCents = Math.clamp(cents, -44, 44);
        double position = height - height * (limitedCents / 50.0);


        // For normal (non-enlarged) view, keep original horizontal bounds
        // Only update vertical position of the line
        line.setBounds(line.getBounds().left,    // Keep original left position
                line.getBounds().top,     // Keep top position
                line.getBounds().right,   // Keep original right position
                (int) position           // Set bottom position based on touch/movement
        );
    }


    /**
     * Clears the visible line in the background of the given TextView by setting its transparency to fully clear.
     *
     * @param noteTextView The TextView whose background line layer will be modified to become transparent.
     */
    public static void clearTextViewLine(TextView noteTextView) {
        // Get the LayerDrawable and extract the line layer (index 1)
        LayerDrawable layout = (LayerDrawable) noteTextView.getBackground();
        GradientDrawable line = (GradientDrawable) layout.getDrawable(1);

        // Make the line completely transparent
        line.setAlpha(0);
    }

    /**
     * Updates the content and style of a `TextView` to display a note text and a cent value with specific formatting.
     * This includes styling text appearance, setting fonts, and adjusting text sizes.
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

        // Create a SpannableString combining note text and cents with a line break
        SpannableString spannableString = new SpannableString(noteText + "\n" + centsString);

        // Set the color for the entire text to black
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the note text bold (first part only)
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, noteText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Increase size of note text by factor 2.0
        spannableString.setSpan(new RelativeSizeSpan(2.0f), 0, noteText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply monospace font to the cents part (including "Cents:")
        spannableString.setSpan(new TypefaceSpan("monospace"), noteText.length(), spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make cents part 1.5 times larger than normal text
        spannableString.setSpan(new RelativeSizeSpan(1.5f), noteText.length() + 1, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the final formatted text to the TextView
        noteTextView.setText(spannableString);
    }

}
