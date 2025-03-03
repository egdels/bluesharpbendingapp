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
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;
import android.graphics.Typeface;


import java.util.HashMap;

/**
 * The type Harp view note element android.
 */
public class HarpViewNoteElementAndroid implements HarpViewNoteElement {

    /**
     * The constant instances.
     */
    private static final HashMap<TextView, HarpViewNoteElementAndroid> instances = new HashMap<>();

    /**
     * The Text view.
     */
    private final TextView noteTextView;

    /**
     * The Activity.
     */
    private final FragmentActivity activity;
    /**
     * Represents the TextView used for displaying an enlarged note representation.
     * This TextView is visually distinct and shares synchronized properties
     * with the regular note TextView, such as layout and styling, but is used
     * specifically for enhanced visibility.
     * <p>
     * It may be updated dynamically, cleared of content or background, or
     * set to reflect specific note and cent information based on user interactions
     * or system behavior within the HarpViewNoteElementAndroid class.
     */
    private TextView enlargedTextView;

    /**
     * Instantiates a new Harp view note element android.
     *
     * @param textView the text view
     */
    private HarpViewNoteElementAndroid(TextView textView) {
        this.noteTextView = textView;
        this.activity = (FragmentActivity) textView.getContext();
    }

    /**
     * Gets instance.
     *
     * @param textView the text view
     * @return the instance
     */
    public static HarpViewNoteElementAndroid getInstance(TextView textView) {
        return instances.computeIfAbsent(textView, HarpViewNoteElementAndroid::new);
    }

    /**
     * Update.
     *
     * @param cents the cents
     */
    @Override
    public void update(double cents) {
        if (enlargedTextView != null) {
            updateTextView(enlargedTextView, cents);
        }
        updateTextView(noteTextView, cents);
    }

    /**
     * Updates the specified TextView's graphical and visual elements based on the given cents value.
     * This includes modifying the gradient line's alpha and stroke properties, adjusting its bounds,
     * and synchronizing additional visual elements for an enlarged view when applicable.
     *
     * @param textView the TextView whose background elements are to be updated.
     * @param cents    the cents value used to determine the visual adjustments to the line and its position.
     */
    private void updateTextView(TextView textView, double cents) {
        activity.runOnUiThread(() -> {
            LayerDrawable layout = (LayerDrawable) textView.getBackground();

            GradientDrawable line = (GradientDrawable) layout.getDrawable(1);
            line.setAlpha(255);

            double height = textView.getHeight();

            int lineWidth = Math.max((int) (textView.getHeight() / 10.0), 10);

            line.setStroke(lineWidth, Color.rgb((int) (250.0 * Math.abs(cents / 50.0)), (int) (250.0 * (1.0 - Math.abs(cents / 50.0))), 0));

            // Limit the cents values to the range -44 to 44
            double limitedCents = Math.max(-44.0, Math.min(44.0, cents));
            double position = height - height * (limitedCents / 50.0);


            if (textView.equals(enlargedTextView)) {
                // For the enlarged view, extend the line horizontally by half lineWidth on each side
                // This creates a wider line for better visibility in the enlarged view
                line.setBounds(line.getBounds().left - lineWidth / 2,    // Move left edge further left
                        line.getBounds().top,                   // Keep top position
                        line.getBounds().right + lineWidth / 2,   // Move right edge further right
                        (int) position                          // Set bottom position based on touch/movement
                );

                // Update the cents display in enlarged view
                updateTextViewCent(enlargedTextView, cents);
            } else {
                // For normal (non-enlarged) view, keep original horizontal bounds
                // Only update vertical position of the line
                line.setBounds(line.getBounds().left,    // Keep original left position
                        line.getBounds().top,     // Keep top position
                        line.getBounds().right,   // Keep original right position
                        (int) position           // Set bottom position based on touch/movement
                );
            }
        });
    }

    /**
     * Clear.
     */
    @Override
    public void clear() {
        if (enlargedTextView != null) {
            clearTextView(enlargedTextView);
        }
        clearTextView(noteTextView);
    }

    /**
     * Clears the specified TextView by resetting its background layer and, if applicable,
     * resetting its cents display to 0.
     *
     * @param textView the TextView to be cleared, whose background will be reset
     *                 and content updated accordingly
     */
    private void clearTextView(TextView textView) {
        // Execute on UI thread since we're modifying UI elements
        activity.runOnUiThread(() -> {
            // Get the LayerDrawable and extract the line layer (index 1)
            LayerDrawable layout = (LayerDrawable) textView.getBackground();
            GradientDrawable line = (GradientDrawable) layout.getDrawable(1);

            // Make the line completely transparent
            line.setAlpha(0);

            // If this is the enlarged TextView, reset its cents display to 0
            if (textView.equals(enlargedTextView)) {
                updateTextViewCent(enlargedTextView, 0);
            }
        });
    }

    /**
     * Sets the TextView to be displayed as the enlarged note representation.
     * This method also synchronizes certain visual properties of the enlarged TextView
     * with the note TextView, including background color, and clears its text content.
     *
     * @param textView the TextView to be used as the enlarged note representation
     *                 or null to unset the current enlarged TextView.
     */
    public void setEnlargedTextView(TextView textView) {
        // Store the reference to the enlarged TextView
        this.enlargedTextView = textView;

        // Only proceed if a valid TextView was provided
        if (enlargedTextView != null) {
            // Extract the background layers and shapes
            LayerDrawable layout = (LayerDrawable) textView.getBackground();
            GradientDrawable shape = (GradientDrawable) layout.getDrawable(0);

            // Get the color from the note TextView's background
            LayerDrawable noteLayout = (LayerDrawable) noteTextView.getBackground();
            GradientDrawable noteShape = (GradientDrawable) noteLayout.getDrawable(0);

            // Apply the note's background color to the enlarged TextView
            shape.setColor(noteShape.getColor());

            // Clear any existing text in the enlarged TextView
            clearTextView(enlargedTextView);
        }
    }

    /**
     * Updates the provided TextView with formatted note and cent information.
     * The note part is displayed in bold with a larger font size, and the cents part is
     * displayed in monospace font with a relative size adjustment.
     *
     * @param textView the TextView to update with the formatted information
     * @param cents    the cent value to display, formatted with leading spaces and sign (+/-)
     */
    private void updateTextViewCent(TextView textView, double cents) {
        // Get the existing note text from the TextView
        CharSequence noteText = noteTextView.getText();

        // Format cents with leading spaces and sign (+/-), suppress lint warning
        @SuppressLint("DefaultLocale") String centsString = String.format("%+3d", (int) cents);
        centsString = "Cents:" + centsString;

        // Create a SpannableString combining note text and cents with a line break
        SpannableString spannableString = new SpannableString(noteText + "\n" + centsString);

        // Make the note text bold (first part only)
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, noteText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Increase size of note text by factor 2.0
        spannableString.setSpan(new RelativeSizeSpan(2.0f), 0, noteText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply monospace font to the cents part (including "Cents:")
        spannableString.setSpan(new TypefaceSpan("monospace"), noteText.length(), spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make cents part 1.5 times larger than normal text
        spannableString.setSpan(new RelativeSizeSpan(1.5f), noteText.length() + 1, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the final formatted text to the TextView
        textView.setText(spannableString);
    }
}
