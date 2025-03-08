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

import androidx.fragment.app.FragmentActivity;

import java.util.HashMap;

import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;


/**
 * A class representing a note element in a harp view for Android platform.
 * This class is responsible for managing the UI updates of note elements,
 * including color and layout changes, based on input pitch offset values (cents).
 * It also supports dynamically managing enlarged text views for better visualization.
 * <p>
 * Implements {@link HarpViewNoteElement}, providing definitions for methods
 * to update and clear note-related visual properties.
 */
public class HarpViewNoteElementAndroid implements HarpViewNoteElement {


    /**
     * A static map associating TextView objects with their respective
     * HarpViewNoteElementAndroid instances. Ensures that each TextView
     * has a unique corresponding instance of HarpViewNoteElementAndroid.
     * Used to manage and retrieve instances efficiently.
     */
    private static final HashMap<TextView, HarpViewNoteElementAndroid> instances = new HashMap<>();


    /**
     * A `TextView` instance used to display note-related information,
     * including graphical and textual updates representing musical notes
     * and their associated properties like pitch variations (cents).
     * <p>
     * `noteTextView` serves as the core text rendering component within
     * the system. It interacts with various internal methods to dynamically
     * update its appearance and content based on musical note adjustments.
     * <p>
     * Key functionalities tied to this variable include:
     * - Integration with UI elements, such as background and dynamic visuals.
     * - Setting formatted textual content, including note names and pitch offsets.
     * - Visual styling like bold text, font scaling, and line drawing.
     * - Being manipulated on the main UI thread for smooth real-time updates.
     * <p>
     * This variable is used throughout the class to manage and render note
     * representation consistently. It is designed to adapt for both regular
     * and enlarged views, enabling an enhanced experience for various display
     * contexts.
     */
    private final TextView noteTextView;


    /**
     * Represents the activity context associated with the current note element.
     * Used to execute operations on the UI thread or retrieve additional resources
     * related to the activity context.
     * <p>
     * This variable is initialized as the context of the provided TextView when the
     * HarpViewNoteElementAndroid is instantiated. It ensures proper interaction
     * with Android framework UI components.
     */
    private final FragmentActivity activity;

    /**
     * Represents a specialized TextView element that is visually emphasized
     * and utilized for displaying enlarged note and cent information.
     * <p>
     * This field is specifically designed to react to updates in musical note pitch values
     * and provides an enhanced display by applying formatting, such as size augmentation
     * and style adjustments, to the text content.
     * <p>
     * It is updated and modified dynamically within the user interface thread, and is
     * cleared or reset to a default state when necessary, to ensure consistency
     * and proper interaction with the underlying logic of pitching and cent display.
     */
    private TextView enlargedTextView;


    /**
     * Constructs a new HarpViewNoteElementAndroid instance with the specified TextView.
     * Initializes the associated note display and context for further usage.
     *
     * @param textView the TextView that will be linked to this HarpViewNoteElementAndroid instance.
     */
    private HarpViewNoteElementAndroid(TextView textView) {
        this.noteTextView = textView;
        this.activity = (FragmentActivity) textView.getContext();
    }


    /**
     * Retrieves an existing instance of {@code HarpViewNoteElementAndroid} associated with the specified
     * {@code TextView}, or creates a new one if no such instance exists.
     *
     * @param textView the {@code TextView} linked to the desired {@code HarpViewNoteElementAndroid} instance.
     * @return the {@code HarpViewNoteElementAndroid} instance associated with the specified {@code TextView}.
     */
    public static HarpViewNoteElementAndroid getInstance(TextView textView) {
        return instances.computeIfAbsent(textView, HarpViewNoteElementAndroid::new);
    }


    @Override
    public void update(double cents) {
        if (enlargedTextView != null) {
            updateTextView(enlargedTextView, cents);
        }
        updateTextView(noteTextView, cents);
    }


    /**
     * Updates the appearance and position of a graphic line on the given TextView's background,
     * based on the provided cents value. This method modifies the visual representation of
     * pitch deviation and, if the TextView is an enlarged view, updates its displayed cents value.
     *
     * @param textView the TextView whose appearance will be updated
     * @param cents the pitch deviation value in cents; ranges from -44 to 44
     *              with values clamped within this range for graphical rendering
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


    @Override
    public void clear() {
        if (enlargedTextView != null) {
            clearTextView(enlargedTextView);
        }
        clearTextView(noteTextView);
    }


    /**
     * Clears the visual elements associated with the specified TextView. This method modifies
     * the background of the TextView to make specific layers transparent and resets any
     * additional display elements if applicable. The operation is performed on the UI thread
     * to ensure safe manipulation of the user interface.
     *
     * @param textView the TextView whose background and display settings will be cleared
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
     * Sets the specified TextView as the enlarged view for this instance. The method adjusts
     * the background color of the enlarged TextView to match the background of the associated
     * note TextView and clears any existing text in the enlarged TextView.
     *
     * @param textView the TextView to be set as the enlarged view. If null, the operation
     *                 is skipped.
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
     * Updates the text and style of a given TextView to display a formatted
     * representation of note text and pitch deviation in cents.
     * The method applies various styles to enhance the visual distinction
     * between the note text and the cents text.
     *
     * @param textView the TextView to be updated with the formatted note and cents text
     * @param cents the pitch deviation value in cents; positive values represent upward
     *              deviation, while negative values represent downward deviation
     */
    private void updateTextViewCent(TextView textView, double cents) {
        // Get the existing note text from the TextView
        CharSequence noteText = noteTextView.getText();

        // Format cents with leading spaces and sign (+/-), suppress lint warning
        @SuppressLint("DefaultLocale") String centsString = String.format("%+3d", (int) cents);
        centsString = "Cents:" + centsString;

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
        textView.setText(spannableString);
    }
}
