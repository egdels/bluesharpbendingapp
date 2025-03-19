package de.schliweb.bluesharpbendingapp.view.android;

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
import lombok.Setter;

/**
 * Represents a UI element that handles displaying and updating specific note-related information
 * within an Android application. This class is implemented to manage visual updates for a note's
 * display, including modifying its background and text appearance to reflect cent adjustments.
 * <p>
 * This class provides methods to clear visual indications of adjustments or update the note's
 * display based on cent values in a musical context. It ensures that all visual changes occur
 * on the UI thread to properly modify UI components.
 * <p>
 * Implements the {@code HarpViewNoteElement} interface.
 */
public class TrainingViewNoteElementAndroid implements HarpViewNoteElement {

    /**
     * A static mapping that associates {@link TextView} instances with their corresponding
     * {@link TrainingViewNoteElementAndroid} objects. This allows the system to maintain a single
     * instance of {@link TrainingViewNoteElementAndroid} for each {@link TextView}, supporting efficient
     * reuse and ensuring correct association between a visual TextView and its functional data.
     */
    private static final HashMap<TextView, TrainingViewNoteElementAndroid> instances = new HashMap<>();
    /**
     * A TextView used to display the current musical note and its associated pitch offset (cents).
     * This TextView is formatted dynamically to display the note in bold and larger font size,
     * along with the pitch offset in a distinct monospace style and adjusted size.
     * It is intended to represent pitch accuracy feedback during training sessions.
     */
    private final TextView noteTextView;
    /**
     * Holds a reference to the associated FragmentActivity.
     * This variable is initialized in the constructor of the containing class
     * by retrieving the context of a provided TextView and casting it to a FragmentActivity.
     * Used to manage and interact with the activity context where the containing class operates.
     */
    private final FragmentActivity activity;

    /**
     * Represents the name of the note displayed within the associated TextView.
     * <p>
     * This variable stores the textual representation of the note, which may be used
     * for display and formatting purposes. It is expected to be dynamically updated
     * based on the application's context and the current musical note being processed.
     */
    @Setter
    private String noteName;

    /**
     * Constructor for the TrainingViewNoteElementAndroid class.
     * This initializes the noteTextView and the associated activity context.
     *
     * @param textView The TextView instance used to display the note element.
     */
    private TrainingViewNoteElementAndroid(TextView textView) {
        this.noteTextView = textView;
        this.activity = (FragmentActivity) textView.getContext();
    }

    /**
     * Retrieves an instance of the TrainingViewNoteElementAndroid associated with the given TextView.
     * The method ensures that the same instance is reused for a specific TextView.
     *
     * @param textView The TextView instance for which the TrainingViewNoteElementAndroid instance is to be retrieved.
     * @return An instance of TrainingViewNoteElementAndroid associated with the specified TextView.
     */
    public static TrainingViewNoteElementAndroid getInstance(TextView textView) {
        return instances.computeIfAbsent(textView, TrainingViewNoteElementAndroid::new);
    }

    @Override
    public void clear() {
        // Execute on UI thread since we're modifying UI elements
        activity.runOnUiThread(() -> {
            // Get the LayerDrawable and extract the line layer (index 1)
            LayerDrawable layout = (LayerDrawable) noteTextView.getBackground();
            GradientDrawable line = (GradientDrawable) layout.getDrawable(1);

            // Make the line completely transparent
            line.setAlpha(0);
            updateTextViewCent(noteTextView, 0);
        });
    }

    @Override
    public void update(double cents) {
        activity.runOnUiThread(() -> {
            LayerDrawable layout = (LayerDrawable) noteTextView.getBackground();

            GradientDrawable line = (GradientDrawable) layout.getDrawable(1);
            line.setAlpha(255);

            double height = noteTextView.getHeight();

            int lineWidth = Math.max((int) (noteTextView.getHeight() / 10.0), 10);

            line.setStroke(lineWidth, Color.rgb((int) (250.0 * Math.abs(cents / 50.0)), (int) (250.0 * (1.0 - Math.abs(cents / 50.0))), 0));

            // Limit the cents values to the range -44 to 44
            double limitedCents = Math.max(-44.0, Math.min(44.0, cents));
            double position = height - height * (limitedCents / 50.0);


            line.setBounds(line.getBounds().left - lineWidth / 2,    // Move left edge further left
                    line.getBounds().top,                   // Keep top position
                    line.getBounds().right + lineWidth / 2,   // Move right edge further right
                    (int) position                          // Set bottom position based on touch/movement
            );
            updateTextViewCent(noteTextView, cents);
        });
    }

    /**
     * Updates the specified TextView to display a formatted string consisting of a note name and its corresponding cent offset.
     * The note name is displayed in bold, with increased size and black color. The cent offset is displayed
     * in a monospace font, also with increased size and black color.
     *
     * @param textView The TextView instance to be updated with the formatted note name and cent offset.
     * @param cents    The cent offset value to be displayed, formatted with a sign and space padding (e.g., "+10").
     */
    private void updateTextViewCent(TextView textView, double cents) {
        String noteText = noteName;

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
