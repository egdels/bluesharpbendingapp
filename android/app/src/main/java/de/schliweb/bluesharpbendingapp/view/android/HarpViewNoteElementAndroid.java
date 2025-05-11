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

import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;

import java.util.HashMap;


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
     * Represents the initial background color assigned to the note element.
     * This value is immutable and used to define the starting color state
     * of a note TextView within the {@code HarpViewNoteElementAndroid} class.
     */
    private final int initialBackgroundColor;

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
        this.initialBackgroundColor = TextViewUtils.saveBackgroundColor(textView);
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
            activity.runOnUiThread(() -> {
                TextViewUtils.updateEnlargedTextViewLine(enlargedTextView, cents);
                TextViewUtils.updateTextViewCent(enlargedTextView, (String) noteTextView.getText(), cents);
            });
        }
        activity.runOnUiThread(() -> TextViewUtils.updateTextViewLine(noteTextView, cents));
    }

    @Override
    public void clear() {
        if (enlargedTextView != null) {
            activity.runOnUiThread(() -> {
                TextViewUtils.clearTextViewLine(enlargedTextView);
                TextViewUtils.updateTextViewCent(enlargedTextView, (String) noteTextView.getText(), 0);
            });
        }
        activity.runOnUiThread(() -> {
            TextViewUtils.restoreBackgroundColor(noteTextView, initialBackgroundColor);
            TextViewUtils.clearTextViewLine(noteTextView);
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
            // Clear any existing text and line in the enlarged TextView
            TextViewUtils.updateTextViewCent(enlargedTextView, (String) noteTextView.getText(), 0);
            TextViewUtils.clearTextViewLine(enlargedTextView);
        }
    }

    /**
     * Highlights the note TextView to visually represent a chord.
     * This method utilizes Android's runOnUiThread to ensure thread safety
     * and calls TextViewUtils.highlightAsChord to modify the background color
     * of the note TextView to indicate a chord highlight state.
     */
    @Override
    public void highlightAsChord() {
        activity.runOnUiThread(() -> TextViewUtils.highlightAsChord(noteTextView));
    }

}
